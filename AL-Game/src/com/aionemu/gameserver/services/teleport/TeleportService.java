/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.teleport;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.portal.EntryPoint;
import com.aionemu.gameserver.model.templates.portal.ExitPoint;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.teleport.TelelocationTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleportType;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.world.WorldPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer , orz, Simple modified by Wakizashi
 */
public class TeleportService {

	private static final Logger log = LoggerFactory.getLogger(TeleportService.class);
	private static final int TELEPORT_DEFAULT_DELAY = 2200;
	private static final int BEAM_DEFAULT_DELAY = 3000;

	/**
	 * Schedules teleport animation
	 *
	 * @param activePlayer
	 * @param mapid
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void scheduleTeleportTask(final Player activePlayer, final int mapid, final float x, final float y,
			final float z) {
		teleportTo(activePlayer, mapid, x, y, z, TELEPORT_DEFAULT_DELAY);
	}
	public static void scheduleBeamTask(final Player activePlayer, final int mapid, final float x, final float y,
			final float z) {
		teleportTo(activePlayer, mapid, x, y, z, BEAM_DEFAULT_DELAY);
	}

	/**
	 * Performs flight teleportation
	 *
	 * @param template
	 * @param locId
	 * @param player
	 */
	public static void teleport(TeleporterTemplate template, int locId, Player player) {

		if (template.getTeleLocIdData() == null) {
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM())
				PacketSendUtility.sendMessage(player,
						"Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);

			return;
		}

		TeleportLocation location = template.getTeleLocIdData().getTeleportLocation(locId);
		if (location == null) {
			log.info(String.format("Missing locId for this teleporter at teleporter_templates.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM())
				PacketSendUtility.sendMessage(player,
						"Missing locId for this teleporter at teleporter_templates.xml with locId: " + locId);

			return;
		}

		TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(locId);
		if (locationTemplate == null) {
			log.info(String.format("Missing info at teleport_location.xml with locId: %d", locId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			if (player.isGM())
				PacketSendUtility.sendMessage(player, "Missing info at teleport_location.xml with locId: " + locId);

			return;
		}

		if (location.getRequiredQuest() > 0) {
			QuestState qs = player.getQuestStateList().getQuestState(location.getRequiredQuest());
			if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
				return;
			}
		}

		// TODO: remove teleportation route if it's enemy fortress (1221, 1231, 1241)
		int id = getFortressId(locId);
		if (id > 0 && !SiegeService.getInstance().getFortress(id).isCanTeleport(player)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
			PacketSendUtility.sendMessage(player, "Teleporter is dead"); // TODO retail chk
			return;
		}

		if (!checkKinahForTransportation(location, player))
			return;

		if(location.getType() == TeleportType.BEAM)
		{
			PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 2), 50);
			PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(locationTemplate.getMapId(), locationTemplate.getX(),
					locationTemplate.getY(), locationTemplate.getZ(), (byte) locationTemplate.getHeading(), 1));
			scheduleBeamTask(player, locationTemplate.getMapId(), locationTemplate.getX(), locationTemplate.getY(),
					locationTemplate.getZ());
			return;
		}

		if (location.getType() == TeleportType.FLIGHT) {
			if (GSConfig.ENABLE_FLYPATH_VALIDATOR) {
				FlyPathEntry flypath = DataManager.FLY_PATH.getPathTemplate(location.getLocId());
				if (flypath == null) {
					AuditLogger.info(player, "Try to use null flyPath #" + location.getLocId() + " || " + player.getFlightTeleportId());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					return;
				}

				double dist = MathUtil.getDistance(player, flypath.getStartX(), flypath.getStartY(), flypath.getStartZ());
				if (dist > 7) {
					AuditLogger.info(player, "Try to use flyPath #" + location.getLocId() + " but hes too far "
							+ dist);
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					return;
				}

				if (player.getWorldId() != flypath.getStartWorldId()) {
					AuditLogger.info(player, "Try to use flyPath #" + location.getLocId()
							+ " from not native start world " + player.getWorldId() + ". expected " + flypath.getStartWorldId());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_NO_ROUTE);
					return;
				}

				player.setCurrentFlypath(flypath);
			}

			player.setState(CreatureState.FLIGHT_TELEPORT);
			player.unsetState(CreatureState.ACTIVE);
			player.setFlightTeleportId(location.getTeleportId());
			PacketSendUtility.broadcastPacket(player,
					new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, location.getTeleportId(), 0), true);
		}

		else {
			PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 11), 50);
			PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(locationTemplate.getMapId(), locationTemplate.getX(),
					locationTemplate.getY(), locationTemplate.getZ(), (byte) locationTemplate.getHeading(), 4));
			scheduleTeleportTask(player, locationTemplate.getMapId(), locationTemplate.getX(), locationTemplate.getY(),
					locationTemplate.getZ());
		}
	}
	/**
	 * This Method a Teleport player with the Circle = So Same as the Gatekeeper and all can see this circle=)
	 * Example: TelepotService.goInCircle(player, 1000000, 1, 1, 1, (byte)0);
	 * @param player
	 * @param MapId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param h 
	 */
	public static void goInCircle(Player player, int MapId, int instanceId, float x, float y, float z, byte h)
	{
		player.setTelEffect(11);
		PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 11), 50);
		PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(MapId, x, y, z, (byte) h, 3));
		teleportTo(player, MapId, instanceId, x, y, z, 2000, false);
	}
	/**
	 * Check kinah in inventory for teleportation
	 *
	 * @param location
	 * @param player
	 * @return
	 */
	private static boolean checkKinahForTransportation(TeleportLocation location, Player player) {
		Storage inventory = player.getInventory();

		// TODO: Price vary depending on the influence ratio
		int basePrice = (int) (location.getPrice());
		// TODO check for location.getPricePvp()

		long transportationPrice = PricesService.getPriceForService(basePrice, player.getRace());

		if (!inventory.tryDecreaseKinah(transportationPrice)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(transportationPrice));
			return false;
		}
		return true;
	}

	/**
	 * @param player
	 * @param targetObjectId
	 */
	public static void showMap(Player player, int targetObjectId, int npcId) {
		if (player.isInFlyingState()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_AIRPORT_WHEN_FLYING);
			return;
		}

		Npc object = (Npc) World.getInstance().findVisibleObject(targetObjectId);
		if (player.isEnemy(object)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_MOVE_TO_AIRPORT_WRONG_NPC);// TODO retail
			// message
			return;
		}

		PacketSendUtility.sendPacket(player, new SM_TELEPORT_MAP(player, targetObjectId, getTeleporterTemplate(npcId)));
	}

	/**
	 * Move Player concerning object with specific conditions
	 *
	 * @param object
	 * @param player
	 * @param direction
	 * @param distance
	 * @return true or false
	 */
	public static boolean moveToTargetWithDistance(VisibleObject object, Player player, int direction, int distance) {
		double radian = Math.toRadians(object.getHeading() * 3);
		float x0 = object.getX();
		float y0 = object.getY();
		float x1 = (float) (Math.cos(Math.PI * direction + radian) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction + radian) * distance);
		return teleportTo(player, object.getWorldId(), x0 + x1, y0 + y1, object.getZ(), 0);
	}

	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, int delay) {
		return teleportTo(player, worldId, x, y, z, player.getHeading(), delay);
	}

	/**
	 * Teleport Creature to the location using current heading and instanceId
	 *
	 * @param worldId
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @return true or false
	 */
	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, byte h, int delay) {
		int instanceId = 1;
		if (player.getWorldId() == worldId) {
			instanceId = player.getInstanceId();
		}
		return teleportTo(player, worldId, instanceId, x, y, z, h, delay, false);
	}

	public static boolean teleportTo(Player player, int worldId, float x, float y, float z, int delay, boolean beam)
	{
		int instanceId = 1;
		if(player.getWorldId() == worldId)
		{
			instanceId = player.getInstanceId();
		}
		return teleportTo(player, worldId, instanceId, x, y, z, delay, beam);
	}


	/**
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @return true or false
	 */
	public static boolean teleportTo(Player player, int worldId, int instanceId, float x, float y, float z, int delay, boolean beam) {
		return teleportTo(player, worldId, instanceId, x, y, z, player.getHeading(), delay, beam);
	}

	/**
	 * @param player
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param delay
	 * @return
	 */
	public static boolean teleportTo(final Player player, final int worldId, final int instanceId, final float x, final float y, final float z, final byte heading, final int delay,final boolean beam) {
		if (player.getLifeStats().isAlreadyDead() || !player.isSpawned())
			return false;
		else if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		}

		if (player.getWorldId() != worldId) {
			player.getController().onLeaveWorld();
		}

		if (delay == 0) {
			changePosition(player, worldId, instanceId, x, y, z, heading);
			return true;
		}

		player.setTelEffect(11);
		player.setIsTeleporting(true);
		player.getController().cancelUseItem();
		//PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), 0, 0, delay, 0, 0)); Not needed casting line
		if(beam)
		{
			PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 2), 50);
			PacketSendUtility.sendPacket(player, new SM_TELEPORT_LOC(worldId, x, y, z, heading, 1));
		}    
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (player.getLifeStats().isAlreadyDead() || !player.isSpawned())
					return;

				//PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(0, 0, 0, 0, 1, 0)); Not needed casting line
				changePosition(player, worldId, instanceId, x, y, z, heading);
			}

		}, delay);

		return true;
	}


	/**
	 * @param worldId
	 * @param instanceId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @param type
	 */

	private static void changePosition(Player player, int worldId, int instanceId, float x, float y, float z, byte heading) {
		player.getFlyController().endFly();

		World.getInstance().despawn(player);

		int currentWorldId = player.getWorldId();
		World.getInstance().setPosition(player, worldId, instanceId, x, y, z, heading);

		Pet pet = player.getPet();
		if (pet != null)
			World.getInstance().setPosition(pet, worldId, instanceId, x, y, z, heading);

		/**
		 * instant teleport when map is the same
		 */
		if (currentWorldId == worldId) {
			PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
			PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
			PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			World.getInstance().spawn(player);
			player.getEffectController().updatePlayerEffectIcons();
			player.getController().updateZone();

			if (pet != null)
				World.getInstance().spawn(pet);
		}
		/**
		 * teleport with full map reloading
		 */
		else {
			player.getController().startProtectionActiveTask();
			PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
			PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
		}
		player.getController().startProtectionActiveTask();
		player.setIsTeleporting(false);
	}

	/**
	 * @return the teleporterData
	 */
	public static TeleporterTemplate getTeleporterTemplate(int npcId) {
		return DataManager.TELEPORTER_DATA.getTeleporterTemplate(npcId);
	}

	public static ExitPoint getExitPointByRace(PortalTemplate template, Race race) {
		for (ExitPoint exit : template.getExitPoints()) {
			if (exit.getRace() == race || exit.getRace() == Race.PC_ALL) {
				return exit;
			}
		}
		return null;
	}

	public static EntryPoint getEntryPointByRace(PortalTemplate template, Race race) {
		for (EntryPoint entry : template.getEntryPoints()) {
			if (entry.getRace() == race || entry.getRace() == Race.PC_ALL) {
				return entry;
			}
		}
		return null;
	}

	public static int getInstanceMap(PortalTemplate template) {
		if (template.isInstance()) {
			if (template.getExitPoints().size() > 1) {
				log.warn("An instance portal " + template.getNpcId() + " has more than one exit.");
			}
			return template.getExitPoints().get(0).getMapId();
		}
		log.warn("Portal " + template.getNpcId() + " is not an instance. Check getInstanceMap call");
		return 0;
	}

	/**
	 * @param channel
	 */
	public static void changeChannel(Player player, int channel) {
		World.getInstance().despawn(player);
		World.getInstance().setPosition(player, player.getWorldId(), channel + 1, player.getX(), player.getY(),
				player.getZ(), player.getHeading());
		player.getController().startProtectionActiveTask();
		PacketSendUtility.sendPacket(player, new SM_CHANNEL_INFO(player.getPosition()));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_SPAWN(player));
	}

	/**
	 * This method will move a player to their bind location with 0 delay
	 *
	 * @param player
	 * @param useTeleport
	 */
	public static void moveToBindLocation(Player player, boolean useTeleport) {
		moveToBindLocation(player, useTeleport, 0);
	}

	/**
	 * This method will move a player to their bind location
	 *
	 * @param player
	 * @param useTeleport
	 * @param delay
	 */
	public static void moveToBindLocation(Player player, boolean useTeleport, int delay) {
		float x, y, z;
		int worldId;
		byte h = 0;

		if (player.getBindPoint() != null) {
			BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
			h = bplist.getHeading();
		}
		else {
			LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}

		InstanceService.onLeaveInstance(player);

		if (useTeleport) {
			PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 2), 50);
			teleportTo(player, worldId, x, y, z, h, delay);
		}
		else {
			World.getInstance().setPosition(player, worldId, 1, x, y, z, h);
		}
	}

	/**
	 * This method will send the set bind point packet
	 *
	 * @param player
	 */
	public static void sendSetBindPoint(Player player) {
		int worldId;
		float x, y, z;
		if (player.getBindPoint() != null) {
			BindPointPosition bplist = player.getBindPoint();
			worldId = bplist.getMapId();
			x = bplist.getX();
			y = bplist.getY();
			z = bplist.getZ();
		}
		else {
			LocationData locationData = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
			worldId = locationData.getMapId();
			x = locationData.getX();
			y = locationData.getY();
			z = locationData.getZ();
		}
		PacketSendUtility.sendPacket(player, new SM_SET_BIND_POINT(worldId, x, y, z, player));
	}

	/**
	 * @param portalName
	 */
	public static void teleportToPortalExit(Player player, String portalName, int worldId, int delay) {
		PortalTemplate template = DataManager.PORTAL_DATA.getTemplateByNameAndWorld(worldId, portalName);
		if (template == null) {
			log.warn("No portal template found for : " + portalName + " " + worldId);
			return;
		}

		Race playerRace = player.getRace();
		ExitPoint exitPoint = getExitPointByRace(template, playerRace);
		if (exitPoint == null) {
			log.warn("No portal exit for Race " + playerRace + " on " + portalName + " " + worldId);
		}
		else {
			teleportTo(player, worldId, exitPoint.getX(), exitPoint.getY(), exitPoint.getZ(), delay, true);
		}
	}

	public static void teleportToNpc(Player player, int npcId) {
		int delay = 0;
		int worldId = player.getWorldId();
		SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(worldId, npcId);

		if (searchResult == null) {
			log.warn("No npc spawn found for : " + npcId);
			return;
		}

		SpawnSpotTemplate spot = searchResult.getSpot();
		WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(searchResult.getWorldId());
		WorldMapInstance newInstance = null;

		if (worldTemplate.isInstance()) {
			newInstance = InstanceService.getNextAvailableInstance(searchResult.getWorldId());
		}

		if (newInstance != null) {
			InstanceService.registerPlayerWithInstance(newInstance, player);
			teleportTo(player, searchResult.getWorldId(), newInstance.getInstanceId(), spot.getX(), spot.getY(), spot.getZ(), delay, true);
		}
		else {
			teleportTo(player, searchResult.getWorldId(), spot.getX(), spot.getY(), spot.getZ(), delay);
		}
	}

	/**
	 * @param player
	 * @param b
	 */
	public static void moveToKiskLocation(Player player, WorldPosition kisk) {
		int mapId = kisk.getMapId();
		float x = kisk.getX();
		float y = kisk.getY();
		float z = kisk.getZ();
		byte heading = kisk.getHeading();

		int instanceId = 1;
		if (player.getWorldId() == mapId)
			instanceId = player.getInstanceId();

		teleportTo(player, mapId, instanceId, x, y, z, heading, 0, true);
	}

	public static void teleportToPrison(Player player) {
		if (player.getRace() == Race.ELYOS)
			goInCircle(player, WorldMapType.DE_PRISON.getId(),1, 275, 239, 49, (byte)0);
		//teleportTo(player, WorldMapType.DE_PRISON.getId(), 275, 239, 49, 3000, true);
		else if (player.getRace() == Race.ASMODIANS)
			goInCircle(player, WorldMapType.DF_PRISON.getId(),1, 275, 239, 49, (byte)0);
	}

	private static int getFortressId(int locId) {
		switch (locId) {
		case 49:
		case 61:
			return 1011; // Divine Fortress
		case 36:
		case 54:
			return 1131; // Siel's Western Fortress
		case 37:
		case 55:
			return 1132; // Siel's Eastern Fortress
		case 39:
		case 56:
			return 1141; // Sulfur Archipelago
		case 44:
		case 62:
			return 1211; // Roah Fortress
		case 45:
		case 57:
		case 72:
		case 75:
			return 1221; // Krotan Refuge
		case 46:
		case 58:
		case 73:
		case 76:
			return 1231; // Kysis Fortress
		case 47:
		case 59:
		case 74:
		case 77:
			return 1241; // Miren Fortress
		case 48:
		case 60:
			return 1251; // Asteria Fortress
		case 90:
			return 2011; // Temple of Scales
		case 91:
			return 2021; // Altar of Avarice
		case 93:
			return 3011; // Vorgaltem Citadel
		case 94:
			return 3021; // Crimson Temple
		}
		return 0;
	}

}