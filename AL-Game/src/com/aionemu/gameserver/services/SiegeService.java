/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.shedule.SiegeSchedule;
import com.aionemu.gameserver.dao.SiegeDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.*;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.siegeservice.*;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.*;
import javax.annotation.Nullable;
import javolution.util.FastMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SoulKeeper
 */
public class SiegeService {

	/**
	 * Just a logger
	 */
	private static final Logger log = LoggerFactory.getLogger(SiegeService.class);
	/**
	 * Balaur protector spawn schedule.
	 */
	@Deprecated
	private static final String BALAUR_PROTECTOR_SPAWN_SCHEDULE = "0 0 21 ? * *";
	/**
	 * We should broadcast fortress status every hour Actually only influence
	 * packet must be sent, but that doesn't matter
	 */
	private static final String SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE = "0 0 * ? * *";
	/**
	 * Singleton that is loaded on the class initialization. Guys, we really do
	 * not SingletonHolder classes
	 */
	private static final SiegeService instance = new SiegeService();
	/**
	 * Map that holds fortressId to Siege. We can easily know what fortresses is
	 * under siege ATM :)
	 */
	private final Map<Integer, Siege<?>> activeSieges = new FastMap<Integer, Siege<?>>().shared();
	/**
	 * Object that holds siege schedule.<br> And maybe other useful information
	 * (in future).
	 */
	private SiegeSchedule siegeSchedule;

	/**
	 * Returns the single instance of siege service
	 *
	 * @return siege service instance
	 */
	public static SiegeService getInstance() {
		return instance;
	}

	private Map<Integer, ArtifactLocation> artifacts;
	private Map<Integer, FortressLocation> fortresses;
	private Map<Integer, OutpostLocation> outposts;
	private Map<Integer, SiegeLocation> locations;

	/**
	 * Initializer. Should be called once.
	 */
	public void initSiegeLocations() {
		if (SiegeConfig.SIEGE_ENABLED) {
			log.info("Initializing sieges...");

			if (siegeSchedule != null) {
				log.error("SiegeService should not be initialized two times!");
				return;
			}

			// initialize current siege locations
			artifacts = DataManager.SIEGE_LOCATION_DATA.getArtifacts();
			fortresses = DataManager.SIEGE_LOCATION_DATA.getFortress();
			outposts = DataManager.SIEGE_LOCATION_DATA.getOutpost();
			locations = DataManager.SIEGE_LOCATION_DATA.getSiegeLocations();
			DAOManager.getDAO(SiegeDAO.class).loadSiegeLocations(locations);
		}
		else {
			artifacts = Collections.emptyMap();
			fortresses = Collections.emptyMap();
			outposts = Collections.emptyMap();
			locations = Collections.emptyMap();
			log.info("Sieges are disabled in config.");
		}
	}

	public void initSieges() {
		if (!SiegeConfig.SIEGE_ENABLED) {
			return;
		}

		// despawn all NPCs spawned by spawn engine.
		// Siege spawns should be controlled by siege service
		for (Integer i : getSiegeLocations().keySet()) {
			deSpawnNpcs(i);
			deSpawnProtectors(i);
		}

		// spawn fortress common npcs
		for (FortressLocation f : getFortresses().values()) {
			spawnNpcs(f.getLocationId(), f.getRace());
		}

		// spawn outpost protectors...
		for (OutpostLocation o : getOutposts().values()) {
			if (SiegeRace.BALAUR != o.getRace() && o.getLocationRace() != o.getRace()) {
				spawnNpcs(o.getLocationId(), o.getRace());
			}
		}

		// spawn artifacts
		for (ArtifactLocation a : getStandaloneArtifacts().values()) {
			spawnNpcs(a.getLocationId(), a.getRace());
		}

		// initialize siege schedule
		siegeSchedule = SiegeSchedule.load();

		// Schedule fortresses sieges protector spawn
		for (final SiegeSchedule.Fortress f : siegeSchedule.getFortressList()) {
			for (String siegeTime : f.getSiegeTimes()) {
				CronService.getInstance().schedule(new FortressSiegeStartRunnable(f.getId()), siegeTime);
				log.info("Scheduled siege of fortressID " + f.getId() + " based on cron expression: " + siegeTime);
			}
		}

		// Outpost siege start... Why it's called balaur?
		CronService.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				// spawn outpost protectors...
				for (OutpostLocation o : getOutposts().values()) {
					if (o.isSiegeAllowed()) {
						startSiege(o.getLocationId());
					}
				}
			}

		}, BALAUR_PROTECTOR_SPAWN_SCHEDULE);

		// Start siege of artifacts
		for (ArtifactLocation artifact : artifacts.values()) {
			if (artifact.isStandAlone()) {
				log.info("Starting siege of artifact #" + artifact.getLocationId());
				startSiege(artifact.getLocationId());
			}
			else {
				log.info("Artifact #" + artifact.getLocationId() + " siege was not started, it belongs to fortress");
			}
		}

		// We should set valid next state for fortress on startup
		// no need to broadcast state here, no players @ server ATM
		updateFortressNextState();

		// Schedule siege status broadcast (every hour)
		CronService.getInstance().schedule(new Runnable() {

			public void run() {
				updateFortressNextState();
				SiegeService.getInstance().broadcastUpdate();
			}

		}, SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE);
		log.info("Broadcasting Siege Location status based on expression: " + SIEGE_LOCATION_STATUS_BROADCAST_SCHEDULE);
	}

	public void startSiege(final int siegeLocationId) {
		log.debug("Starting siege of siege location: " + siegeLocationId);

		// Siege should not be started two times. Never.
		Siege<?> siege;
		synchronized (this) {
			if (activeSieges.containsKey(siegeLocationId)) {
				log.error("Attempt to start siege twice for siege location: " + siegeLocationId);
				return;
			}
			siege = newSiege(siegeLocationId);
			activeSieges.put(siegeLocationId, siege);
		}

		siege.startSiege();

		// certain sieges are endless
		// should end only manually on siege boss death
		if (siege.isEndless()) {
			return;
		}

		// schedule siege end
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				stopSiege(siegeLocationId);
			}

		}, siege.getDurationInSeconds() * 1000);
	}

	public void stopSiege(int siegeLocationId) {

		log.debug("Stopping siege of siege location: " + siegeLocationId);

		// Just a check here...
		// If fortresses was captured in 99% the siege timer will return here
		// without concurrent race
		if (!isSiegeInProgress(siegeLocationId)) {
			log.debug("Siege of siege location " + siegeLocationId + " is not in progress, it was captured earlier?");
			return;
		}

		// We need synchronization here for that 1% of cases :)
		// It may happen that fortresses siege is stopping in the same time by 2 different threads
		// 1 is for killing the boss
		// 2 is for the schedule
		// it might happen that siege will be stopping by other thread, but in such case siege object will be null
		Siege<?> siege;
		synchronized (this) {
			siege = activeSieges.remove(siegeLocationId);
		}
		if (siege == null || siege.isFinished()) {
			return;
		}

		siege.stopSiege();
	}

	/**
	 * Updates next state for fortresses
	 */
	protected void updateFortressNextState() {

		// get current hour and add 1 hour
		Calendar currentHourPlus1 = Calendar.getInstance();
		currentHourPlus1.set(Calendar.MINUTE, 0);
		currentHourPlus1.set(Calendar.SECOND, 0);
		currentHourPlus1.set(Calendar.MILLISECOND, 0);
		currentHourPlus1.add(Calendar.HOUR, 1);

		// filter fortress siege start runnables
		Map<Runnable, JobDetail> fortressSiegeStartRunables = CronService.getInstance().getRunnables();
		fortressSiegeStartRunables = Maps.filterKeys(fortressSiegeStartRunables, new Predicate<Runnable>() {

			@Override
			public boolean apply(@Nullable Runnable runnable) {
				return runnable instanceof FortressSiegeStartRunnable;
			}

		});

		// Create map FortressId-To-AllTriggers
		Map<Integer, List<Trigger>> fortressIdToSiegeStartTriggers = Maps.newHashMap();
		for (Map.Entry<Runnable, JobDetail> entry : fortressSiegeStartRunables.entrySet()) {
			FortressSiegeStartRunnable fssr = (FortressSiegeStartRunnable) entry.getKey();

			List<Trigger> storage = fortressIdToSiegeStartTriggers.get(fssr.getFortressSiegeLocationId());
			if (storage == null) {
				storage = Lists.newArrayList();
				fortressIdToSiegeStartTriggers.put(fssr.getFortressSiegeLocationId(), storage);
			}
			storage.addAll(CronService.getInstance().getJobTriggers(entry.getValue()));
		}

		// update each fortress next state
		for (Map.Entry<Integer, List<Trigger>> entry : fortressIdToSiegeStartTriggers.entrySet()) {

			List<Date> nextFireDates = Lists.newArrayListWithCapacity(entry.getValue().size());
			for (Trigger trigger : entry.getValue()) {
				nextFireDates.add(trigger.getNextFireTime());
			}
			Collections.sort(nextFireDates);

			// clear non-required times
			Date nextSiegeDate = nextFireDates.get(0);
			Calendar siegeStartHour = Calendar.getInstance();
			siegeStartHour.setTime(nextSiegeDate);
			siegeStartHour.set(Calendar.MINUTE, 0);
			siegeStartHour.set(Calendar.SECOND, 0);
			siegeStartHour.set(Calendar.MILLISECOND, 0);

			// update fortress state that will be valid in 1 h
			FortressLocation fortress = getFortressById(entry.getKey());
			if (currentHourPlus1.getTimeInMillis() == siegeStartHour.getTimeInMillis()) {
				fortress.setNextState(SiegeLocation.STATE_VULNERABLE);
			}
			else {
				fortress.setNextState(SiegeLocation.STATE_INVULNERABLE);
			}
		}
	}

	/**
	 * TODO: WTF is it?
	 *
	 * @return seconds before hour end
	 */
	public int getSecondsBeforeHourEnd() {
		Calendar c = Calendar.getInstance();
		int minutesAsSeconds = c.get(Calendar.MINUTE) * 60;
		int seconds = c.get(Calendar.SECOND);
		return 3600 - (minutesAsSeconds + seconds);
	}

	/**
	 * TODO: Check if it's valid
	 * <p/>
	 * If siege duration is endless - will return -1
	 *
	 * @param siegeLocationId Scheduled siege end time
	 * @return remaining seconds in current hour
	 */
	public int getRemainingSiegeTimeInSeconds(int siegeLocationId) {

		Siege<?> siege = getSiege(siegeLocationId);
		if (siege == null || siege.isFinished()) {
			return 0;
		}

		if (!siege.isStarted()) {
			return siege.getDurationInSeconds();
		}

		// endless siege
		if (siege.getDurationInSeconds() == -1) {
			return -1;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(siege.getStartTime());
		calendar.add(Calendar.SECOND, siege.getDurationInSeconds());

		int result = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000);
		return result > 0 ? result : 0;
	}

	public Siege<?> getSiege(SiegeLocation loc) {
		return activeSieges.get(loc.getLocationId());
	}

	public Siege<?> getSiege(Integer siegeLocationId) {
		return activeSieges.get(siegeLocationId);
	}

	public boolean isSiegeInProgress(int fortressId) {
		return activeSieges.containsKey(fortressId);
	}
	
	private List<Integer> getForteresseId(){
		List<Integer> res = new ArrayList<Integer>();
		
		res.add(1131);
		res.add(1132);
		res.add(1141);
		res.add(1211);
		res.add(1251);
		res.add(1241);
		res.add(1231);
		res.add(1221);
		res.add(1011);
		res.add(2011);
		res.add(2021);
		res.add(3011);
		res.add(3021);
		
		return res;
	}

	public boolean isAtLeastOneSiegeInProgress() {
		for (int id : getForteresseId()) {
			if(isSiegeInProgress(id)){
				return true;
			}
		}
		return false;
	}

	public FortressLocation getFortressById(int fortressId) {
		return fortresses.get(fortressId);
	}

	public Map<Integer, OutpostLocation> getOutposts() {
		return outposts;
	}

	public OutpostLocation getOutpost(int id) {
		return outposts.get(id);
	}

	public Map<Integer, FortressLocation> getFortresses() {
		return fortresses;
	}

	public FortressLocation getFortress(int fortressId) {
		return fortresses.get(fortressId);
	}

	public Map<Integer, ArtifactLocation> getArtifacts() {
		return artifacts;
	}

	public ArtifactLocation getArtifact(int id) {
		return getArtifacts().get(id);
	}

	public Map<Integer, ArtifactLocation> getStandaloneArtifacts() {
		return Maps.filterValues(artifacts, new Predicate<ArtifactLocation>() {

			@Override
			public boolean apply(@Nullable ArtifactLocation input) {
				return input != null && input.isStandAlone();
			}

		});
	}

	public Map<Integer, ArtifactLocation> getFortressArtifacts() {
		return Maps.filterValues(artifacts, new Predicate<ArtifactLocation>() {

			@Override
			public boolean apply(@Nullable ArtifactLocation input) {
				return input != null && input.getOwningFortress() != null;
			}

		});
	}

	public Map<Integer, SiegeLocation> getSiegeLocations() {
		return locations;
	}

	public SiegeLocation getSiegeLocation(int locationId) {
		return locations.get(locationId);
	}

	protected Siege<?> newSiege(int siegeLocationId) {
		if (fortresses.containsKey(siegeLocationId)) {
			return new FortressSiege(fortresses.get(siegeLocationId));
		}
		else if (outposts.containsKey(siegeLocationId)) {
			return new OutpostSiege(outposts.get(siegeLocationId));
		}
		else if (artifacts.containsKey(siegeLocationId)) {
			return new ArtifactSiege(artifacts.get(siegeLocationId));
		}
		else {
			throw new SiegeException("Unknown siege handler for siege location: " + siegeLocationId);
		}
	}

	public void cleanLegionId(int legionId) {
		for (SiegeLocation loc : this.getSiegeLocations().values()) {
			if (loc.getLegionId() == legionId) {
				loc.setLegionId(0);
				break;
			}
		}
	}

	public void spawnNpcs(int siegeLocationId, SiegeRace race) {
		List<SpawnGroup2> siegeSpawns = DataManager.SPAWNS_DATA2.getSiegeSpawnsByLocId(siegeLocationId);
		for (SpawnGroup2 group : siegeSpawns) {
			for (SpawnTemplate template : group.getSpawnTemplates()) {
				SiegeSpawnTemplate siegetemplate = (SiegeSpawnTemplate) template;
				if (siegetemplate.getSiegeRace() != race || !siegetemplate.isPeace()) {
					continue;
				}

				SpawnEngine.spawnObject(siegetemplate, 1);
			}
		}
	}

	public void spawnProtectors(int siegeLocationId, SiegeRace race) {
		List<SpawnGroup2> siegeSpawns = DataManager.SPAWNS_DATA2.getSiegeSpawnsByLocId(siegeLocationId);
		for (SpawnGroup2 group : siegeSpawns) {
			for (SpawnTemplate template : group.getSpawnTemplates()) {
				SiegeSpawnTemplate siegetemplate = (SiegeSpawnTemplate) template;
				if (siegetemplate.getSiegeRace() != race || !siegetemplate.isSiege()) {
					continue;
				}

				SpawnEngine.spawnObject(siegetemplate, 1);
			}
		}
	}

	public void deSpawnNpcs(int siegeLocationId) {
		Collection<SiegeNpc> siegeNpcs = World.getInstance().getLocalSiegeNpcs(siegeLocationId);
		for (SiegeNpc npc : siegeNpcs) {
			if (npc.getSpawn().isPeace()) {
				npc.getController().delete();
			}
		}
	}

	public void deSpawnProtectors(int siegeLocationId) {
		Collection<SiegeNpc> siegeNpcs = World.getInstance().getLocalSiegeNpcs(siegeLocationId);
		for (SiegeNpc npc : siegeNpcs) {
			if (npc.getSpawn().isSiege()) {
				npc.getController().delete();
			}
		}
	}

	public boolean isSiegeNpcInActiveSiege(Npc npc) {
		if (npc instanceof SiegeNpc) {
			FortressLocation fort = getFortress(((SiegeNpc) npc).getSiegeId());
			if (fort != null) {
				if (fort.isVulnerable())
					return true;
				else if (fort.getNextState() == 1) {
					return npc.getSpawn().getRespawnTime() >= getSecondsBeforeHourEnd();
				}
			}
		}
		return false;
	}

	public void broadcastUpdate(SiegeLocation loc) {
		SM_SIEGE_LOCATION_INFO pkt = new SM_SIEGE_LOCATION_INFO(loc);
		broadcast(pkt);
	}

	public void broadcastUpdate() {
		SM_SIEGE_LOCATION_INFO pkt = new SM_SIEGE_LOCATION_INFO();
		broadcast(pkt);
	}

	private void broadcast(final SM_SIEGE_LOCATION_INFO pkt) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, pkt);
				PacketSendUtility.sendPacket(player, new SM_INFLUENCE_RATIO());
			}

		});
	}

	public void onPlayerLogin(final Player player) {
		PacketSendUtility.sendPacket(player, new SM_ABYSS_ARTIFACT_INFO(getSiegeLocations().values()));
		PacketSendUtility.sendPacket(player, new SM_ABYSS_ARTIFACT_INFO2(getSiegeLocations().values()));
		PacketSendUtility.sendPacket(player, new SM_ABYSS_ARTIFACT_INFO3(getSiegeLocations().values()));
		//PacketSendUtility.sendPacket(player, new SM_FORTRESS_STATUS()); // TODO when send on retail?
		PacketSendUtility.sendPacket(player, new SM_SHIELD_EFFECT());

		for (FortressLocation loc : getFortresses().values()) {
			// remove teleportation to dead teleporters
			if (!loc.isCanTeleport(player))
				PacketSendUtility.sendPacket(player, new SM_FORTRESS_INFO(loc.getLocationId(), loc.isCanTeleport(player)));
		}
	}

}