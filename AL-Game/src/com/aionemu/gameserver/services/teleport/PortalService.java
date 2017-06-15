/*
 * This file is part of aion-lightning <aion-lightning.com>.
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
package com.aionemu.gameserver.services.teleport;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.portal.ExitPoint;
import com.aionemu.gameserver.model.templates.portal.PortalItem;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer, xTz
 */
public class PortalService {

	private static Logger log = LoggerFactory.getLogger(PortalService.class);

	/**
	 * Add portation task to player with talkDelay delay to location specified by portalTemplate
	 */
	public static void port(final PortalTemplate portalTemplate, final Player player, int npcObjectId, int talkDelay) {
		if (!CustomConfig.ENABLE_INSTANCES)
			return;

		talkDelay *= 1000;
		if (talkDelay > 0) {
			PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), npcObjectId, talkDelay, 1));
                      
                           
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, npcObjectId),
				true);
		}
		player.getController().addTask(TaskId.PORTAL, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				analyzePortation(player, portalTemplate);
			}
		}, talkDelay));
	}

	private static void analyzePortation(final Player player, PortalTemplate portalTemplate) {

		boolean instanceTitleReq = false;
		boolean instanceLevelReq = false;
		boolean instanceRaceReq = false;
		boolean instanceQuestReq = false;
		boolean instanceGroupReq = false;
		int instanceCooldownRate = 0;

		ExitPoint exit = TeleportService.getExitPointByRace(portalTemplate, player.getRace());
		if (exit == null) {
			return;
		}
		int mapId = exit.getMapId();

		if (player.getAccessLevel() < AdminConfig.INSTANCE_REQ) {
			instanceTitleReq = !player.havePermission(MembershipConfig.INSTANCES_TITLE_REQ);
			instanceLevelReq = !player.havePermission(MembershipConfig.INSTANCES_LEVEL_REQ);
			instanceRaceReq = !player.havePermission(MembershipConfig.INSTANCES_RACE_REQ);
			instanceQuestReq = !player.havePermission(MembershipConfig.INSTANCES_QUEST_REQ);
			instanceGroupReq = !player.havePermission(MembershipConfig.INSTANCES_GROUP_REQ);
			instanceCooldownRate = InstanceService.getInstanceRate(player, mapId);
		}

		if (portalTemplate.getInstanceSiegeId() != 0) {
			int LocationId = portalTemplate.getInstanceSiegeId();
			FortressLocation loc = SiegeService.getInstance().getFortress(LocationId);
			if (loc != null) {
				if (loc.getRace().getRaceId() != player.getRace().getRaceId() && instanceRaceReq) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MOVE_PORTAL_ERROR_INVALID_RACE);
					Item usingItem = player.getUsingItem();
					player.setUsingItem(null);
					PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usingItem == null ? 0
						: usingItem.getObjectId(), usingItem == null ? 0 : usingItem.getItemTemplate().getTemplateId(), 0, 3, 0));
					player.getController().cancelPortalUseItem();
					return;
				}
			}
		}

		if (portalTemplate.getIdTitle() != 0 && player.getCommonData().getTitleId() != portalTemplate.getIdTitle()
			&& instanceTitleReq)
			return;

		if (!portalTemplate.existsExitForRace(player.getRace()) && instanceRaceReq) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MOVE_PORTAL_ERROR_INVALID_RACE);
			return;
		}

		if (!(portalTemplate.isInstance()
			&& DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(mapId)!= null
			&& DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(mapId).getCanEnterMentor()
			&& player.isMentor())) {
			if (((portalTemplate.getMaxLevel() != 0 && player.getLevel() > portalTemplate.getMaxLevel()) || player.getLevel() < portalTemplate
				.getMinLevel()) && instanceLevelReq) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
				return;
			}
		}

		PlayerGroup group = player.getPlayerGroup2();
		PlayerAlliance allianceGroup = player.getPlayerAlliance2();
		switch (portalTemplate.getPlayerSize()) {
			case 12:
				if (allianceGroup == null && instanceGroupReq) {
					// to do sniff
					PacketSendUtility.sendMessage(player, "You must be in Alliance.");
					return;
				}
				break;
			case 6:
				if (group == null && instanceGroupReq) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ENTER_ONLY_PARTY_DON);
					return;
				}
				break;
		}

		if (instanceQuestReq && portalTemplate.needQuest()) {
			int[][] quests = portalTemplate.getQuests();
			boolean present = false;
			for (int i = 0; i < quests.length; i++) {
				// TEMP: please remove when Quest 1044 get fixed
				if (quests[i][0] == 1044) {
					present = true;
					break;
				}

				final QuestState qs = player.getQuestStateList().getQuestState(quests[i][0]);
				if (qs != null) {
					if ((quests[i][1] == 0 && qs.getStatus() == QuestStatus.COMPLETE)
						|| (quests[i][1] != 0 && (qs.getStatus() == QuestStatus.COMPLETE || qs.getQuestVarById(0) >= quests[i][1]))) {
						present = true;
						break;
					}
				}
			}

			if (!present) {
				PacketSendUtility.sendMessage(player, "You must complete the entrance quest.");
				return;
			}
		}
		
		if(CustomConfig.INSTANCE_KEYCHECK) {
			if (portalTemplate.getPortalItem() != null && !portalTemplate.getPortalItem().isEmpty()) {
				for (PortalItem pi : portalTemplate.getPortalItem()) {
					if (!player.getInventory().decreaseByItemId(pi.getItemid(), pi.getQuantity())) {
						// TODO: find correct message
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_OPEN_DOOR_NEED_KEY_ITEM);
						return;
					}
				}
			}
		}

		boolean reenter = false;
		int useDelay = 0;
		int instanceCooldown = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(mapId);
		if (instanceCooldownRate > 0) {
			useDelay = instanceCooldown / instanceCooldownRate;
		}
		WorldMapInstance instance = null;
		if (player.getPortalCooldownList().isPortalUseDisabled(mapId) && useDelay > 0) {

			switch (portalTemplate.getPlayerSize()) {
				case 6: // group
					if (player.getPlayerGroup2() != null) {
						instance = InstanceService.getRegisteredInstance(mapId, player.getPlayerGroup2().getTeamId());
					}
					break;
				case 12: // alliance
					if (player.isInAlliance2()) {
						instance = InstanceService.getRegisteredInstance(mapId, player.getPlayerAlliance2().getObjectId());
					}
					break;
				default: // solo
					instance = InstanceService.getRegisteredInstance(mapId, player.getObjectId());
					break;
			}

			if (instance == null) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME);
				return;
			}
			else {
				if (!instance.isRegistered(player.getObjectId())) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME);
					return;
				}
				else {
					reenter = true;
					log.debug(player.getName() + "has been in intance and also have cd, can reenter.");
				}
			}
		}
		else {
			log.debug(player.getName() + "doesn't have cd of this instance, can enter and will be registed to this intance");
		}
		
		switch (portalTemplate.getPlayerSize()) {
			case 6:
				if (group != null || !instanceGroupReq) {
					// If there is a group (whatever group requirement exists or not)...
					if (group != null) {
						instance = InstanceService.getRegisteredInstance(mapId, group.getTeamId());
					}
					// But if there is no group (and solo is enabled, of course)
					else {
						instance = InstanceService.getRegisteredInstance(mapId, player.getObjectId());
					}

					// No instance (for group), group on and default requirement off
					if (instance == null && group != null && !instanceGroupReq) {
						// For each player from group
						for (Player member : group.getMembers()) {
							// Get his instance
							instance = InstanceService.getRegisteredInstance(mapId, member.getObjectId());

							// If some player is soloing and I found no one else yet, I get his instance
							if (instance != null) {
								break;
							}
						}

						// No solo instance found
						if (instance == null)
							instance = registerGroup(group, mapId);
					}

					// No instance and default requirement on = Group on
					else if (instance == null && instanceGroupReq) {
						instance = registerGroup(group, mapId);
					}
					// No instance, default requirement off, no group = Register new instance with player ID
					else if (instance == null && !instanceGroupReq && group == null) {
						instance = InstanceService.getNextAvailableInstance(mapId);
					}

					transfer(player, portalTemplate, instance, reenter);
				}
				break;
			case 12:
				if (allianceGroup != null || !instanceGroupReq) {
					if (allianceGroup != null) {
						instance = InstanceService.getRegisteredInstance(mapId, allianceGroup.getObjectId());
					}
					else {
						instance = InstanceService.getRegisteredInstance(mapId, player.getObjectId());
					}

					if (instance == null && allianceGroup != null && !instanceGroupReq) {
						for (Player member : allianceGroup.getMembers()) {
							instance = InstanceService.getRegisteredInstance(mapId, member.getObjectId());
							if (instance != null) {
								break;
							}
						}
						if (instance == null) {
							instance = registerAlliance(allianceGroup, mapId);
						}
					}
					else if (instance == null && instanceGroupReq) {
						instance = registerAlliance(allianceGroup, mapId);
					}
					else if (instance == null && !instanceGroupReq && allianceGroup == null) {
						instance = InstanceService.getNextAvailableInstance(mapId);
					}
					if (instance.getPlayersInside().size() < portalTemplate.getPlayerSize()) {
						transfer(player, portalTemplate, instance, reenter);
					}
				}
				break;
			default:
				// If there is a group (whatever group requirement exists or not)...
				if (group != null && !instanceGroupReq) {
					instance = InstanceService.getRegisteredInstance(mapId, group.getTeamId());
				}
				// But if there is no group, go to solo
				else {
					instance = InstanceService.getRegisteredInstance(mapId, player.getObjectId());
				}

				// No group instance, group on and default requirement off
				if (instance == null && group != null && !instanceGroupReq) {
					// For each player from group
					for (Player member : group.getMembers()) {
						// Get his instance
						instance = InstanceService.getRegisteredInstance(mapId, member.getObjectId());

						// If some player is soloing and I found no one else yet, I get his instance
						if (instance != null) {
							break;
						}
					}

					// No solo instance found
					if (instance == null && portalTemplate.isInstance())
						instance = registerGroup(group, mapId);
				}

				// if already registered - just teleport
				if (instance != null) {
					reenter = true;
					transfer(player, portalTemplate, instance, reenter);
					return;
				}
				port(player, portalTemplate, reenter);
				break;
		}
	}

	private static void port(Player requester, PortalTemplate portalTemplate, boolean reenter) {
		WorldMapInstance instance = null;
		int worldId = TeleportService.getExitPointByRace(portalTemplate, requester.getRace()).getMapId();
		if (portalTemplate.isInstance()) {
			instance = InstanceService.getNextAvailableInstance(worldId);
			InstanceService.registerPlayerWithInstance(instance, requester);
			transfer(requester, portalTemplate, instance, reenter);
		}
		else {
			/*WorldMap worldMap = World.getInstance().getWorldMap(worldId);
			if (worldMap == null) {
				log.warn("There is no registered map with id " + worldId);
				return;
			}
			instance = worldMap.getWorldMapInstance();*/
			easyTransfer(requester, portalTemplate, reenter);
		}
	}

	private static WorldMapInstance registerGroup(PlayerGroup group, int mapId) {
		WorldMapInstance instance = InstanceService.getNextAvailableInstance(mapId);
		InstanceService.registerGroupWithInstance(instance, group);
		return instance;
	}

	private static WorldMapInstance registerAlliance(PlayerAlliance group, int mapId) {
		WorldMapInstance instance = InstanceService.getNextAvailableInstance(mapId);
		InstanceService.registerAllianceWithInstance(instance, group);
		return instance;
	}

	private static void transfer(Player player, PortalTemplate portalTemplate, WorldMapInstance instance, boolean reenter) {
		ExitPoint exitPoint = TeleportService.getExitPointByRace(portalTemplate, player.getRace());
		player.setInstanceStartPos(exitPoint.getX(), exitPoint.getY(), exitPoint.getZ());
		InstanceService.registerPlayerWithInstance(instance, player);
		TeleportService.teleportTo(player, exitPoint.getMapId(), instance.getInstanceId(), exitPoint.getX(),
			exitPoint.getY(), exitPoint.getZ(), 3000, true);
		int instanceCooldownRate = InstanceService.getInstanceRate(player, exitPoint.getMapId());
		int useDelay = 0;
		int instanceCoolTime = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(instance.getMapId());
		if (instanceCooldownRate > 0) {
			useDelay = instanceCoolTime * 60 * 1000 / instanceCooldownRate;
		}
		if (useDelay > 0 && !reenter) {
			player.getPortalCooldownList().addPortalCooldown(exitPoint.getMapId(), useDelay);
		}
	}

	/**
	 * this method used to teleport players from instance
	 * @param player
	 * @param portalTemplate
	 * @param reenter
	 */
	private static void easyTransfer(Player player, PortalTemplate portalTemplate, boolean reenter) {
		ExitPoint exitPoint = TeleportService.getExitPointByRace(portalTemplate, player.getRace()); 
		TeleportService.teleportTo(player, exitPoint.getMapId(), exitPoint.getX(), exitPoint.getY(), exitPoint.getZ(), 3000, true);
	}
}
