/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.services.instance;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.portal.EntryPoint;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AutoGroupService2;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import javolution.util.FastList;

/**
 * @author ATracer
 */
public class InstanceService {

	private static final Logger log = LoggerFactory.getLogger(InstanceService.class);
	private static final FastList<Integer> instanceAggro = new FastList<Integer>();
	private static final FastList<Integer> instanceCoolDownFilter = new FastList<Integer>();
	private static final int SOLO_INSTANCES_DESTROY_DELAY = 10 * 60 * 1000; // 10 minutes

	public static void load() {
		for (String s : CustomConfig.INSTANCES_MOB_AGGRO.split(",")) {
			instanceAggro.add(Integer.parseInt(s));
		}
		for (String s : CustomConfig.INSTANCES_COOL_DOWN_FILTER.split(",")) {
			instanceCoolDownFilter.add(Integer.parseInt(s));
		}
	}

	/**
	 * @param worldId
	 * @return
	 */
	public synchronized static WorldMapInstance getNextAvailableInstance(int worldId) {
		WorldMap map = World.getInstance().getWorldMap(worldId);

		if (!map.isInstanceType())
			throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);

		int nextInstanceId = map.getNextInstanceId();

		log.info("Creating new instance: " + worldId + " " + nextInstanceId);

		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);

		if (map.isInstanceType()) {
			startInstanceChecker(worldMapInstance);
		}

		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId());
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		return worldMapInstance;
	}

	/**
	 * Instance will be destroyed All players moved to bind location All objects - deleted
	 */
	public static void destroyInstance(WorldMapInstance instance) {
		if (instance.getEmptyInstanceTask() != null) {
			instance.getEmptyInstanceTask().cancel(false);
		}

		int worldId = instance.getMapId();
		WorldMap map = World.getInstance().getWorldMap(worldId);
		if (!map.isInstanceType())
			return;
		int instanceId = instance.getInstanceId();


		map.removeWorldMapInstance(instanceId);

		log.info("Destroying instance:" + worldId + " " + instanceId);

		Iterator<VisibleObject> it = instance.objectIterator();
		while (it.hasNext()) {
			VisibleObject obj = it.next();
			if (obj instanceof Player) {
				Player player = (Player) obj;
				PortalTemplate portal = DataManager.PORTAL_DATA.getInstancePortalTemplate(worldId, player.getRace());
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.LEAVE_INSTANCE_NOT_PARTY));
				if (portal == null) {
					TeleportService.moveToBindLocation(player, false);
				}
				else {
					moveToEntryPoint((Player) obj, portal, true);
				}
			}
			else {
				obj.getController().delete();
			}
		}
		instance.getInstanceHandler().onInstanceDestroy();
	}

	/**
	 * @param instance
	 * @param player
	 */
	public static void registerPlayerWithInstance(WorldMapInstance instance, Player player) {
		Integer obj = player.getObjectId();
		instance.register(obj);
		instance.setSoloPlayerObj(player);
	}

	/**
	 * @param instance
	 * @param group
	 */
	public static void registerGroupWithInstance(WorldMapInstance instance, PlayerGroup group) {
		instance.registerGroup(group);
	}
	
	/**
	 * @param instance
	 * @param group
	 */
	public static void registerAllianceWithInstance(WorldMapInstance instance, PlayerAlliance group) {
		instance.registerGroup(group);
	}

	/**
	 * @param worldId
	 * @param objectId
	 * @return instance or null
	 */
	public static WorldMapInstance getRegisteredInstance(int worldId, int objectId) {
		Iterator<WorldMapInstance> iterator = World.getInstance().getWorldMap(worldId).iterator();
		while (iterator.hasNext()) {
			WorldMapInstance instance = iterator.next();
			if (instance.isRegistered(objectId))
				return instance;
		}
		return null;
	}

	/**
	 * @param player
	 */
	public static void onPlayerLogin(Player player) {
		int worldId = player.getWorldId();
		WorldMapTemplate worldTemplate = DataManager.WORLD_MAPS_DATA.getTemplate(worldId);
		if (worldTemplate.isInstance()) {
			PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getInstancePortalTemplate(worldId, player.getRace());

			int lookupId = player.getObjectId();
			if (portalTemplate != null) {
				switch (portalTemplate.getPlayerSize()) {
					case 12:
						if (player.isInAlliance2()) {
							lookupId = player.getPlayerAlliance2().getTeamId();
						}
						break;
					case 6:
						if (player.isInGroup2()) {
							lookupId = player.getPlayerGroup2().getTeamId();
						}
						break;
				}
			}

			WorldMapInstance registeredInstance = getRegisteredInstance(worldId, lookupId);
			if (registeredInstance != null) {
				World.getInstance().setPosition(player, worldId, registeredInstance.getInstanceId(), player.getX(),
						player.getY(), player.getZ(), player.getHeading());
				player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogin(player);
				return;
			}

			if (portalTemplate == null) {
				log.error("No portal template found for " + worldId);
				return;
			}

			moveToEntryPoint(player, portalTemplate, false);
		}
	}

	/**
	 * @param player
	 * @param portalTemplates
	 */
	public static void moveToEntryPoint(Player player, PortalTemplate portalTemplate, boolean useTeleport) {
		
		if (!portalTemplate.existsEntryForRace(player.getRace())) {
			log.warn("Entry point not found for " + player.getRace() + " " + player.getWorldId());
			return;
		}
		
		EntryPoint entryPoint = TeleportService.getEntryPointByRace(portalTemplate, player.getRace());

		if (useTeleport) {
			TeleportService.teleportTo(player, entryPoint.getMapId(), 1, entryPoint.getX(), entryPoint.getY(),
				entryPoint.getZ(), 3000, true);
		}
		else {
			if (isInstanceExist(entryPoint.getMapId(), 1))
				World.getInstance().setPosition(player, entryPoint.getMapId(), 1, entryPoint.getX(), entryPoint.getY(),
					entryPoint.getZ(), player.getHeading());
			else
				TeleportService.moveToBindLocation(player, true);
		}
	}

	/**
	 * @param worldId
	 * @param instanceId
	 * @return
	 */
	public static boolean isInstanceExist(int worldId, int instanceId) {
		return World.getInstance().getWorldMap(worldId).getWorldMapInstanceById(instanceId) != null;
	}

	/**
	 * @param worldMapInstance
	 */

	public static void startInstanceChecker(WorldMapInstance worldMapInstance) {
		int delay = 150000; // 2.5 minutes
		int period = 60000; // 1 minute
		worldMapInstance.setEmptyInstanceTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new EmptyInstanceCheckerTask(worldMapInstance), delay, period));
	}

	private static class EmptyInstanceCheckerTask implements Runnable {

		private WorldMapInstance worldMapInstance;
		private long soloInstanceDestroyTime;

		private EmptyInstanceCheckerTask(WorldMapInstance worldMapInstance) {
			this.worldMapInstance = worldMapInstance;
			this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}
		
		private boolean canDestroySoloInstance() {
			return System.currentTimeMillis() > this.soloInstanceDestroyTime;
		}
		
		private void updateSoloInstanceDestroyTime() {
			this.soloInstanceDestroyTime = System.currentTimeMillis() + SOLO_INSTANCES_DESTROY_DELAY;
		}

		@Override
		public void run() {
			int instanceId = worldMapInstance.getInstanceId();
			int worldId = worldMapInstance.getMapId();
			WorldMap map = World.getInstance().getWorldMap(worldId);
			PlayerGroup registeredGroup = worldMapInstance.getRegisteredGroup();
			if (registeredGroup == null) {
				if (worldMapInstance.playersCount() > 0) {
					updateSoloInstanceDestroyTime();
					return;
				}
				if (worldMapInstance.playersCount() == 0) {
					if (canDestroySoloInstance()) {
						map.removeWorldMapInstance(instanceId);
						destroyInstance(worldMapInstance);
						return;
					}
					else {
						return;
					}
				}
				Iterator<Player> playerIterator = worldMapInstance.playerIterator();
				int mapId = worldMapInstance.getMapId();
				while (playerIterator.hasNext()) {
					Player player = playerIterator.next();
					if (player.isOnline() && player.getWorldId() == mapId) {
						return;
					}
				}
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			}
			else if (registeredGroup.size() == 0) {
				map.removeWorldMapInstance(instanceId);
				destroyInstance(worldMapInstance);
			}
		}
	}

	public static void onLogOut(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onPlayerLogOut(player);
	}

	public static void onEnterInstance(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterInstance(player);
	}

	public static void onLeaveInstance(Player player) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveInstance(player);
		AutoGroupService2.getInstance().onLeaveInstance(player);
	}

	public static void onEnterZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onEnterZone(player, zone);
	}

	public static void onLeaveZone(Player player, ZoneInstance zone) {
		player.getPosition().getWorldMapInstance().getInstanceHandler().onLeaveZone(player, zone);
	}

	public static boolean isAggro(int mapId) {
		return instanceAggro.contains(mapId);
	}

	public static int getInstanceRate(Player player, int mapId) {
		int instanceCooldownRate = player.havePermission(MembershipConfig.INSTANCES_COOLDOWN) && !instanceCoolDownFilter.contains(mapId) ? CustomConfig.INSTANCES_RATE
				: 1;
		if (instanceCoolDownFilter.contains(mapId)) {
			instanceCooldownRate = 1;
		}
		return instanceCooldownRate;
	}
}
