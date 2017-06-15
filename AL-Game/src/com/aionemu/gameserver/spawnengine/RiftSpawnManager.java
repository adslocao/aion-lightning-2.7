/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.spawnengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.RiftController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.NpcKnownList;

/**
 * @author ATracer, ginho1
 */
public class RiftSpawnManager {

	private static final Logger log = LoggerFactory.getLogger(RiftSpawnManager.class);

	private static final ConcurrentLinkedQueue<Npc> rifts = new ConcurrentLinkedQueue<Npc>();

	private static final int RIFT_RESPAWN_DELAY	= 3600;	// 1 hour
	private static final int RIFT_LIFETIME		= 3500;	// 1 hour

	private static final Map<String, SpawnTemplate> spawnGroups = new HashMap<String, SpawnTemplate>();

	public static void addRiftSpawnTemplate(SpawnGroup2 spawn) {
		if (spawn.hasPool()) {
			SpawnTemplate template = spawn.getSpawnTemplates().get(0);
			spawnGroups.put(template.getAnchor(), template);
		}
		else {
			for (SpawnTemplate template : spawn.getSpawnTemplates()) {
				spawnGroups.put(template.getAnchor(), template);
			}
		}
	}

	public static void spawnAll() {
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				log.info("Rift Manager");
				ArrayList<Integer> rifts = new ArrayList<Integer>();
				int nbRift, rndRift;

				for (int i = 0; i < 4; i++){
					// Generate number of rift for each town
					nbRift = getNbRift();

					log.info("Spawning " + nbRift + " rifts for the map : " + getMapName(i));

					for (int j = 0; j < nbRift; j++){
						rndRift = Rnd.get(i*7, (i+1)*7-1);

						// try to avoid duplicate
						while (rifts.contains(rndRift))
							rndRift = Rnd.get(i*7, (i+1)*7-1);

						// Save rift spawned
						rifts.add(rndRift);

						// Spawnrift
						spawnRift(RiftEnum.values()[rndRift]);
					}
					rifts.clear();
				}
			}
		}, 0, RIFT_RESPAWN_DELAY * 1000);
	}

	/**
	 *
	 * @return
	 */
	private static int getNbRift(){
		double rnd = Rnd.get(0, 99);

		/*
		 * 0 : 29%
		 * 1 : 45%
		 * 2 : 15%
		 * 3 : 5%
		 * 4 : 3%
		 * 5 : 2%
		 * 6 : 1%
		 */
		if (rnd == 0)
			return 6;
		else if (rnd <= 2)
			return 5;
		else if (rnd <= 5)
			return 4;
		else if (rnd <= 10)
			return 3;
		else if (rnd <= 25)
			return 2;
		else if (rnd <= 70)
			return 1;
		else
			return 0;
	}

	/**
	 *
	 * @param mapId
	 * @return
	 */
	private static String getMapName(int mapId){
		switch (mapId){
			case 0:
				return "ELTNEN";
			case 1:
				return "HEIRON";
			case 2:
				return "MORHEIM";
			case 3:
				return "BELUSLAN";
			default:
				return "UNKNOWN";
		}
	}

	/**
	 * @param rift
	 */
	private static void spawnRift(RiftEnum rift) {

		SpawnTemplate masterTemplate = spawnGroups.get(rift.getMaster());
		SpawnTemplate slaveTemplate = spawnGroups.get(rift.getSlave());

		if (masterTemplate == null || slaveTemplate == null)
			return;

		int instanceCount = World.getInstance().getWorldMap(masterTemplate.getWorldId()).getInstanceCount();

		if (slaveTemplate.hasPool()) {
			slaveTemplate = slaveTemplate.changeTemplate();
		}
		log.info("Spawning rift : " + rift.name());
		for (int i = 1; i <= instanceCount; i++) {
			Npc slave = spawnInstance(i, slaveTemplate, new RiftController(null, rift));
			spawnInstance(i, masterTemplate, new RiftController(slave, rift));
		}
	}

	private static Npc spawnInstance(int instanceIndex, SpawnTemplate spawnTemplate, RiftController riftController) {
		NpcTemplate masterObjectTemplate = DataManager.NPC_DATA.getNpcTemplate(700137);
		Npc npc = new Npc(IDFactory.getInstance().nextId(), riftController, spawnTemplate, masterObjectTemplate);

		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));

		World world = World.getInstance();
		world.storeObject(npc);
		world.setPosition(npc, spawnTemplate.getWorldId(), instanceIndex, spawnTemplate.getX(),
			spawnTemplate.getY(), spawnTemplate.getZ(), spawnTemplate.getHeading());
		world.spawn(npc);
		rifts.add(npc);

		scheduleDelete(npc);
		riftController.sendAnnounce();

		return npc;
	}

	/**
	 * @param npc
	 */
	private static void scheduleDelete(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				npc.getSpawn().setUse(false);
				npc.getController().onDelete();
				rifts.remove(npc);
			}
		}, RIFT_LIFETIME * 1000);
	}

	public enum RiftEnum {
		ELTNEN_AM("ELTNEN_AM", "MORHEIM_AS", 12, 20, 28, Race.ASMODIANS),
		ELTNEN_BM("ELTNEN_BM", "MORHEIM_BS", 20, 20, 32, Race.ASMODIANS),
		ELTNEN_CM("ELTNEN_CM", "MORHEIM_CS", 35, 20, 36, Race.ASMODIANS),
		ELTNEN_DM("ELTNEN_DM", "MORHEIM_DS", 35, 20, 37, Race.ASMODIANS),
		ELTNEN_EM("ELTNEN_EM", "MORHEIM_ES", 45, 20, 40, Race.ASMODIANS),
		ELTNEN_FM("ELTNEN_FM", "MORHEIM_FS", 50, 20, 40, Race.ASMODIANS),
		ELTNEN_GM("ELTNEN_GM", "MORHEIM_GS", 50, 20, 45, Race.ASMODIANS),

		HEIRON_AM("HEIRON_AM", "BELUSLAN_AS", 24, 20, 35, Race.ASMODIANS),
		HEIRON_BM("HEIRON_BM", "BELUSLAN_BS", 36, 20, 42, Race.ASMODIANS),
		HEIRON_CM("HEIRON_CM", "BELUSLAN_CS", 48, 20, 46, Race.ASMODIANS),
		HEIRON_DM("HEIRON_DM", "BELUSLAN_DS", 48, 20, 40, Race.ASMODIANS),
		HEIRON_EM("HEIRON_EM", "BELUSLAN_ES", 60, 20, 50, Race.ASMODIANS),
		HEIRON_FM("HEIRON_FM", "BELUSLAN_FS", 72, 20, CustomConfig.HEIRON_FM, Race.ASMODIANS),
		HEIRON_GM("HEIRON_GM", "BELUSLAN_GS", 72, 20, CustomConfig.HEIRON_GM, Race.ASMODIANS),

		MORHEIM_AM("MORHEIM_AM", "ELTNEN_AS", 12, 20, 28, Race.ELYOS),
		MORHEIM_BM("MORHEIM_BM", "ELTNEN_BS", 20, 20, 32, Race.ELYOS),
		MORHEIM_CM("MORHEIM_CM", "ELTNEN_CS", 35, 20, 36, Race.ELYOS),
		MORHEIM_DM("MORHEIM_DM", "ELTNEN_DS", 35, 20, 37, Race.ELYOS),
		MORHEIM_EM("MORHEIM_EM", "ELTNEN_ES", 45, 20, 40, Race.ELYOS),
		MORHEIM_FM("MORHEIM_FM", "ELTNEN_FS", 50, 20, 40, Race.ELYOS),
		MORHEIM_GM("MORHEIM_GM", "ELTNEN_GS", 50, 20, 45, Race.ELYOS),

		BELUSLAN_AM("BELUSLAN_AM", "HEIRON_AS", 24, 20, 35, Race.ELYOS),
		BELUSLAN_BM("BELUSLAN_BM", "HEIRON_BS", 36, 20, 42, Race.ELYOS),
		BELUSLAN_CM("BELUSLAN_CM", "HEIRON_CS", 48, 20, 46, Race.ELYOS),
		BELUSLAN_DM("BELUSLAN_DM", "HEIRON_DS", 48, 20, 40, Race.ELYOS),
		BELUSLAN_EM("BELUSLAN_EM", "HEIRON_ES", 60, 20, 50, Race.ELYOS),
		BELUSLAN_FM("BELUSLAN_FM", "HEIRON_FS", 72, 20, CustomConfig.BELUSLAN_FM, Race.ELYOS),
		BELUSLAN_GM("BELUSLAN_GM", "HEIRON_GS", 72, 20, CustomConfig.BELUSLAN_GM, Race.ELYOS);

		private String master;
		private String slave;
		private int entries;
		private int minLevel;
		private int maxLevel;
		private Race destination;

		private RiftEnum(String master, String slave, int entries, int minLevel, int maxLevel, Race destination) {
			this.master = master;
			this.slave = slave;
			this.entries = entries;
			this.minLevel = minLevel;
			this.maxLevel = maxLevel;
			this.destination = destination;
		}

		/**
		 * @return the master
		 */
		public String getMaster() {
			return master;
		}

		/**
		 * @return the slave
		 */
		public String getSlave() {
			return slave;
		}

		/**
		 * @return the entries
		 */
		public int getEntries() {
			return entries;
		}

		/**
		 * @return the minLevel
		 */
		public int getMinLevel() {
			return minLevel;
		}

		/**
		 * @return the maxLevel
		 */
		public int getMaxLevel() {
			return maxLevel;
		}

		/**
		 * @return the destination
		 */
		public Race getDestination() {
			return destination;
		}
	}

	/**
	 * @param activePlayer
	 */
	public static void sendRiftStatus(Player activePlayer) {
		for (Npc rift : rifts) {
			if (rift.getWorldId() == activePlayer.getWorldId()) {
				((RiftController) rift.getController()).sendMessage(activePlayer);
			}
		}
	}
}
