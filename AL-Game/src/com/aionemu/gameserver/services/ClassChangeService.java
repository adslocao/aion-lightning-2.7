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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, sweetkr
 */
public class ClassChangeService {

	//TODO dialog enum
	/**
	 * @param player
	 */
	public static void showClassChangeDialog(Player player) {
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			PlayerClass playerClass = player.getPlayerClass();
			Race playerRace = player.getRace();
			if (player.getLevel() >= 9 && playerClass.isStartingClass()) {
				if (playerRace == Race.ELYOS) {
					switch (playerClass) {
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2375, 1006));
							break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 2716, 1006));
							break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 1006));
							break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 1006));
							break;
					}
				}
				else if (playerRace == Race.ASMODIANS) {
					switch (playerClass) {
						case WARRIOR:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3057, 2008));
							break;
						case SCOUT:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3398, 2008));
							break;
						case MAGE:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 3739, 2008));
							break;
						case PRIEST:
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 4080, 2008));
							break;
					}
				}
			}
		}
	}

	/**
	 * @param player
	 * @param dialogId
	 */
	public static void changeClassToSelection(final Player player, final int dialogId) {
		Race playerRace = player.getRace();
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			if (playerRace == Race.ELYOS) {
				switch (dialogId) {
					case 2376:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
						break;
					case 2461:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
						break;
					case 2717:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
						break;
					case 2802:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
						break;
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
						break;
				}
				completeQuest(player, 1006);
				completeQuest(player, 1007);

				// Stigma Quests Elyos
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
					completeQuest(player, 1929);
				}
			}
			else if (playerRace == Race.ASMODIANS) {
				switch (dialogId) {
					case 3058:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("1")));
						break;
					case 3143:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("2")));
						break;
					case 3399:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("4")));
						break;
					case 3484:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("5")));
						break;
					case 3740:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("7")));
						break;
					case 3825:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("8")));
						break;
					case 4081:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("10")));
						break;
					case 4166:
						setClass(player, PlayerClass.getPlayerClassById(Byte.parseByte("11")));
						break;
				}
				completeQuest(player, 2008);
				completeQuest(player, 2009);

				// Stigma Quests Asmodians
				if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
					completeQuest(player, 2900);
				}
			}
			player.getController().upgradePlayer();
		}
	}
	
	private static void completeQuest(Player player, int questId) {
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 0, 0, 0, null, 0, null));
		}
		else {
			qs.setStatus(QuestStatus.COMPLETE);
		}
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId, QuestStatus.COMPLETE, 0));
	}

	public static void setClass(Player player, PlayerClass playerClass) {
		if (validateSwitch(player, playerClass)) {
			player.getCommonData().setPlayerClass(playerClass);
			player.getController().upgradePlayer();
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0, 0));
		}
	}

	private static boolean validateSwitch(Player player, PlayerClass playerClass) {
		int level = player.getLevel();
		PlayerClass oldClass = player.getPlayerClass();
		if (level < 9) {
			PacketSendUtility.sendMessage(player, "You can only switch class at level 9");
			return false;
		}
		if (!oldClass.isStartingClass()) {
			PacketSendUtility.sendMessage(player, "You already switched class");
			return false;
		}
		switch (oldClass) {
			case WARRIOR:
				if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR)
					break;
			case SCOUT:
				if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER)
					break;
			case MAGE:
				if (playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER)
					break;
			case PRIEST:
				if (playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER)
					break;
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return false;
		}
		return true;
	}
}
