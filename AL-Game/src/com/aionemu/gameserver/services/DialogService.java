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
package com.aionemu.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.autogroup.AutoGroupsType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionWarehouse;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.model.templates.teleport.TeleportLocation;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLASTIC_SURGERY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_REPURCHASE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SELL_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADELIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRADE_IN_LIST;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.instance.DredgionService2;
import com.aionemu.gameserver.services.item.ItemChargeService;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author VladimirZ
 */
public class DialogService {

	private static final Logger log = LoggerFactory.getLogger(DialogService.class);
	
	public static void onCloseDialog(Npc npc, Player player) {
		switch (npc.getObjectTemplate().getTitleId()) {
			case 350409:
			case 315073:
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(npc.getObjectId(), 0));
				Legion legion = player.getLegion();
				if (legion != null) {
					LegionWarehouse lwh = player.getLegion().getLegionWarehouse();
					if (lwh.getWhUser() == player.getObjectId()) {
						lwh.setWhUser(0);
					}
				}
				break;
		}
	}

	public static void onDialogSelect(int dialogId, final Player player, Npc npc, int questId, int extendedRewardIndex) {

		QuestEnv env = new QuestEnv(npc, player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env))
			return;

		if (player.getAccessLevel() >= 2 && CustomConfig.ENABLE_SHOW_DIALOGID) {
			PacketSendUtility.sendMessage(player, "dialogId: " + dialogId);
			PacketSendUtility.sendMessage(player, "questId: " + questId);
		}

		int targetObjectId = npc.getObjectId();
		int titleId = npc.getObjectTemplate().getTitleId();

		switch (dialogId) {
			case 2: {
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "Buy list is missing!!");
					break;
				}
				int tradeModifier = tradeListTemplate.getSellPriceRate();
				PacketSendUtility.sendPacket(player, new SM_TRADELIST(player, npc, tradeListTemplate,
					PricesService.getVendorBuyModifier() * tradeModifier / 100));
				break;
			}
			case 3: {
				PacketSendUtility.sendPacket(player,
					new SM_SELL_ITEM(targetObjectId, PricesService.getVendorSellModifier(player.getRace())));
				break;
			}
			case 4: { // stigma
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1));
				break;
			}
			case 5: { // create legion
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 2));
				break;
			}
			case 6: { // disband legion
				LegionService.getInstance().requestDisbandLegion(npc, player);
				break;
			}
			case 7: { // recreate legion
				LegionService.getInstance().recreateLegion(npc, player);
				break;
			}
			case 21: { // warehouse (2.5)
				if (!RestrictionsManager.canUseWarehouse(player))
					return;
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 26));
				WarehouseService.sendWarehouseInfo(player, true);
				break;
			}
			case 26: {
				if (questId != 0) {
					QuestState qs = player.getQuestStateList().getQuestState(questId);
					if (qs != null) {
						if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
							if (AdminConfig.QUEST_DIALOG_LOG) {
								log.info("Error in the quest " + questId + ". No response from " + npc.getNpcId() + " on the step "
									+ qs.getQuestVarById(0));
							}
							if (!"useitem".equals(npc.getAi2().getName())) {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 10));
							}
							else {
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
							}
						}
					}
					else {
						if (AdminConfig.QUEST_DIALOG_LOG) {
							log.info("Quest " + questId + " is not implemented.");
						}
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 10));
					}
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
				}
				break;
			}
			case 28: { // Consign trade?? npc karinerk, koorunerk (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 13));
				break;
			}
			case 30: { // soul healing (2.5)
				final long expLost = player.getCommonData().getExpRecoverable();
				if (expLost == 0) {
					player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
					player.getCommonData().setDeathCount(0);
				}
				final double factor = (expLost < 1000000 ? 0.25 - (0.00000015 * expLost) : 0.1);
				final int price = (int) (expLost * factor);

				RequestResponseHandler responseHandler = new RequestResponseHandler(npc) {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						if (player.getInventory().getKinah() >= price) {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GET_EXP2(expLost));
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SUCCESS_RECOVER_EXPERIENCE);
							player.getCommonData().resetRecoverableExp();
							player.getInventory().decreaseKinah(price);
							player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
							player.getCommonData().setDeathCount(0);
						}
						else {
							PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
						}
					}

					@Override
					public void denyRequest(Creature requester, Player responder) {
						// no message
					}
				};
				if (player.getCommonData().getExpRecoverable() > 0) {
					boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_SOUL_HEALING,
						responseHandler);
					if (result) {
						PacketSendUtility.sendPacket(player,
							new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_SOUL_HEALING, 0, String.valueOf(price)));
					}
				}
				else {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DONOT_HAVE_RECOVER_EXPERIENCE);
				}
				break;
			}
			case 31: { // (2.5)
				switch (npc.getNpcId()) {
					case 204089: // pvp arena in pandaemonium.
						TeleportService.teleportTo(player, 120010000, 1, 984f, 1543f, 222.1f, 3000, true);
						break;
					case 203764: // pvp arena in sanctum.
						TeleportService.teleportTo(player, 110010000, 1, 1462.5f, 1326.1f, 564.1f, 3000, true);
						break;
					case 203981:
						TeleportService.teleportTo(player, 210020000, 1, 439.3f, 422.2f, 274.3f, 3000, true);
						break;
				}
				break;
			}
			case 32: { // (2.5)
				switch (npc.getNpcId()) {
					case 204087:
						TeleportService.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f, 3000, true);
						break;
					case 203875:
						TeleportService.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f, 3000, true);
						break;
					case 203982:
						TeleportService.teleportTo(player, 210020000, 1, 446.2f, 431.1f, 274.5f, 3000, true);
						break;
				}
				break;
			}
			case 36: { // Godstone socketing (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 21));
				break;
			}
			case 37: { // remove mana stone (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 20));
				break;
			}
			case 38: { // modify appearance (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 19));
				break;
			}
			case 39: { // flight and teleport (2.5)
				if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
					int level = player.getLevel();
					if (level < 9) {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
					}
					else {
						TeleportService.showMap(player, targetObjectId, npc.getNpcId());
					}
				}
				else {
					switch (npc.getNpcId()) {
						case 203194: {
							if (player.getRace() == Race.ELYOS) {
								QuestState qs = player.getQuestStateList().getQuestState(1006);
								if (qs == null || qs.getStatus() != QuestStatus.COMPLETE)
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
								else
									TeleportService.showMap(player, targetObjectId, npc.getNpcId());
							}
							break;
						}
						case 203679: {
							if (player.getRace() == Race.ASMODIANS) {
								QuestState qs = player.getQuestStateList().getQuestState(2008);
								if (qs == null || qs.getStatus() != QuestStatus.COMPLETE)
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
								else
									TeleportService.showMap(player, targetObjectId, npc.getNpcId());
							}
							break;
						}
						default: {
							TeleportService.showMap(player, targetObjectId, npc.getNpcId());
						}
					}
				}
				break;
			}
			case 40: // improve extraction (2.5)
			case 41: { // learn tailoring armor smithing etc. (2.5)
				CraftSkillUpdateService.getInstance().learnSkill(player, npc);
				break;
			}
			case 42: { // expand cube (2.5)
				CubeExpandService.expandCube(player, npc);
				break;
			}
			case 43: { // (2.5)
				WarehouseService.expandWarehouse(player, npc);
				break;
			}
			case 48: { // legion warehouse (2.5)
				//PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 26));
				switch (titleId) {
					case 350409:
					case 315073:
						//LegionService.getInstance().openLegionWarehouse(player);
						LegionService.getInstance().openLegionWarehouse(player, npc);
						break;
				}
				break;
			}
			case 51: { // WTF??? Quest dialog packet (2.5)
				break;
			}
			case 53: { // (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 28));
				break;
			}
			case 54: { // coin reward (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 3, 0));
				// PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "This feature is not available yet",
				// ChatType.GOLDEN_YELLOW));
				break;
			}
			case 56:
			case 57: { // (2.5)
				byte changesex = 0; // 0 plastic surgery, 1 gender switch
				byte check_ticket = 2; // 2 no ticket, 1 have ticket
				if (dialogId == 57) {
					// Gender Switch
					changesex = 1;
					if (player.getInventory().getItemCountByItemId(169660000) > 0
						|| player.getInventory().getItemCountByItemId(169660001) > 0) {
						check_ticket = 1;
					}
				}
				else {
					// Plastic Surgery
					if (player.getInventory().getItemCountByItemId(169650000) > 0
						|| player.getInventory().getItemCountByItemId(169650001) > 0) {
						check_ticket = 1;
					}
				}
				PacketSendUtility.sendPacket(player, new SM_PLASTIC_SURGERY(player, check_ticket, changesex));
				player.setEditMode(true);
				break;
			}
			case 58: // dredgion
				if (DredgionService2.getInstance().isDredgionAvialable()) {
					AutoGroupsType agt = AutoGroupsType.getAutoGroup(npc.getNpcId());
					if (agt != null) {
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(agt.getInstanceMaskId()));
					}
					else {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
					}
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1011));
				}
				break;
			case 60: { // (2.5)
				break;
			}
			case 61: { // armsfusion (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 29));
				break;
			}
			case 62: { // armsbreaking (2.5)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 30));
				break;
			}
			case 63: { // join npcFaction (2.5)
				player.getNpcFactions().enterGuild(npc);
				break;
			}
			case 64: { // leave npcFaction (2.5)
				player.getNpcFactions().leaveNpcFaction(npc);
				break;
			}
			case 65: { // repurchase (2.5)
				PacketSendUtility.sendPacket(player, new SM_REPURCHASE(player, npc.getObjectId()));
				break;
			}
			case 66: { // adopt pet (2.5)
				PacketSendUtility.sendPacket(player, new SM_PET(6));
				break;
			}
			case 67: { // surrender pet (2.5)
				PacketSendUtility.sendPacket(player, new SM_PET(7));
				break;
			}
			case 68: { // housing build
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 32));
				break;
			}
			case 69: { // housing destruct
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 33));
				break;
			}
			case 70: { // condition an individual item
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 35));
				break;
			}
			case 71: { // condition all equiped items
				ItemChargeService.startChargingEquippedItems(player);
				break;
			}
			case 72: {// auto groups
				AutoGroupService2.getInstance().sendRequestEntry(player, npc.getNpcId());
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 0));
				break;
			}
			case 73: {
				TradeListTemplate tradeListTemplate = DataManager.TRADE_LIST_DATA.getTradeInListTemplate(npc.getNpcId());
				if (tradeListTemplate == null) {
					PacketSendUtility.sendMessage(player, "Buy list is missing!!");
					break;
				}
				PacketSendUtility.sendPacket(player, new SM_TRADE_IN_LIST(npc, tradeListTemplate, 100));
				break;
			}
			case 10000: {
				if (questId == 0) {
                    // generic npc reply (most are teleporters)
					PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getPortalTemplate(npc.getNpcId());
					TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplate(npc.getNpcId());
					if (template != null) {
						TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
                        //TelelocationTemplate frbeam = DataManager.TELELOCATION_DATA.getTelelocationTemplate(loc.getLocId());
						if (loc != null) {
							TribeClass tribe = npc.getTribe();
							Race race = player.getRace();
							if (tribe.equals(TribeClass.FIELD_OBJECT_LIGHT) && race.equals(Race.ASMODIANS) ||
									(tribe.equals(TribeClass.FIELD_OBJECT_DARK) && race.equals(Race.ELYOS))) {
								return;
							}
                            TeleportService.teleport(template, loc.getLocId(), player);
						}
					}
					else if (portalTemplate != null) {
						PortalService.port(portalTemplate, player, npc.getObjectId(),
							npc.getAi2().getName().equals("actionportal") ? 0 : npc.getObjectTemplate().getTalkDelay());
					}
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				}
				break;
			}
			case 10001: {
				switch (npc.getNpcId()) {
					case 730268: {
						TeleportService.teleportTo(player, 120080000, 542, 202, 93.479576f, 3000, true);
						break;
					}
					case 730265: {
						TeleportService.teleportTo(player, 110070000, 446, 257, 126.97166f, 3000, true);
						break;
					}
					default: {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
					}
				}
				break;
			}
            case 10002:
            {
                switch (npc.getNpcId())
                {
                    
                    default:{
                           PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
                    }
                }
                
            }
			default: {
				if (questId > 0) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
				}
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId));
				}
				break;
			}
		}
	}
}
