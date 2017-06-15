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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.templates.item.RequireSkill;
import com.aionemu.gameserver.model.templates.item.Stigma;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUBE_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 * @modified cura
 */
public class StigmaService {

	private static final Logger log = LoggerFactory.getLogger(StigmaService.class);

	public static boolean extendAdvancedStigmaSlots(Player player, int Nb) {
		int newAdvancedSlotSize = player.getCommonData().getAdvencedStigmaSlotSize() + Nb;
		if (newAdvancedSlotSize <= 6) { // maximum
			player.getCommonData().setAdvencedStigmaSlotSize(newAdvancedSlotSize);
			PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.stigmaSlots(player.getCommonData()
				.getAdvencedStigmaSlotSize()));
			return true;
		}
		return false;
	}

	/**
	 * @param player
	 * @param resultItem
	 * @param slot
	 * @return
	 */
	public static boolean notifyEquipAction(Player player, Item resultItem, int slot) {
		if (resultItem.getItemTemplate().isStigma()) {
			if (slot >= ItemSlot.STIGMA1.getSlotIdMask() && slot <= ItemSlot.STIGMA6.getSlotIdMask()) {
				// check the number of stigma wearing
				int psc = getPossibleStigmaCount(player);
				int rsc = player.getEquipment().getEquippedItemsRegularStigma().size();
				if (psc <= rsc) {
					AuditLogger.info(player, "Possible client hack, normal stigma count big : player has " + rsc + " and should have " + psc);
					return false;
				}
			}
			else if (slot >= ItemSlot.ADV_STIGMA1.getSlotIdMask() && slot <= ItemSlot.ADV_STIGMA5.getSlotIdMask()) {
				// check the number of advanced stigma wearing
				int pasc = getPossibleAdvencedStigmaCount(player);
				int rasc = player.getEquipment().getEquippedItemsAdvencedStigma().size();
				if (pasc <= rasc) {
					AuditLogger.info(player, "Possible client hack, advanced stigma count big : player has " + rasc + " and should have " + pasc);
					return false;
				}
			}

			if (resultItem.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass()) == false) {
				AuditLogger.info(player,"Possible client hack, stigma " + resultItem.getItemId() + " not valid for class " + player.getCommonData().getPlayerClass());
				return false;
			}

			Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();

			if (stigmaInfo == null) {
				log.warn("Stigma info missing for item: " + resultItem.getItemTemplate().getTemplateId());
				return false;
			}

			int skillId = stigmaInfo.getSkillid();
			int shardCount = stigmaInfo.getShard();
			if (player.getInventory().getItemCountByItemId(141000001) < shardCount) {
				AuditLogger.info(player,"Possible client hack, stigma shard count low.");
				return false;
			}
			int needSkill = stigmaInfo.getRequireSkill().size();
			//log.warn("needSkill : " + needSkill);
			for (RequireSkill rs : stigmaInfo.getRequireSkill()) {
				for (int id : rs.getSkillId()) {
					if (player.getSkillList().isSkillPresent(id))
						needSkill--;
					break;
				}
			}
			//log.warn("needSkill : " + needSkill);
			if (needSkill != 0) {
				AuditLogger.info(player,"Possible client hack, advanced stigma skill.");
			}

			if (!player.getInventory().decreaseByItemId(141000001, shardCount))
				return false;
			player.getSkillList().addStigmaSkill(player, skillId, stigmaInfo.getSkilllvl(), true);
		}
		return true;
	}

	/**
	 * @param player
	 * @param resultItem
	 * @return
	 */
	public static boolean notifyUnequipAction(Player player, Item resultItem) {
		if (resultItem.getItemTemplate().isStigma()) {
			Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
			int skillId = stigmaInfo.getSkillid();
			for (Item item : player.getEquipment().getEquippedItemsAllStigma()) {
				Stigma si = item.getItemTemplate().getStigma();
				if (resultItem == item || si == null)
					continue;
				for (RequireSkill rs : si.getRequireSkill()) {
					if (rs.getSkillId().contains(skillId)) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300410, new DescriptionId(resultItem
							.getItemTemplate().getNameId()), new DescriptionId(item.getItemTemplate().getNameId())));
						return false;
					}
				}
			}
			SkillLearnService.removeSkill(player, skillId);
			int nameId = DataManager.SKILL_DATA.getSkillTemplate(skillId).getNameId();
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300403, new DescriptionId(nameId)));
		}
		return true;
	}

	/**
	 * @param player
	 */
	public static void onPlayerLogin(Player player) {
		List<Item> equippedItems = player.getEquipment().getEquippedItemsAllStigma();
		for (Item item : equippedItems) {
			if (item.getItemTemplate().isStigma()) {
				Stigma stigmaInfo = item.getItemTemplate().getStigma();

				if (stigmaInfo == null) {
					log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
					return;
				}
				int skillId = stigmaInfo.getSkillid();
				player.getSkillList().addStigmaSkill(player, skillId, stigmaInfo.getSkilllvl(), false);
			}
		}

		for (Item item : equippedItems) {
			if (item.getItemTemplate().isStigma()) {
				if (!isPossibleEquippedStigma(player, item)) {
					AuditLogger.info(player,"Possible client hack stigma count big :O");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}

				Stigma stigmaInfo = item.getItemTemplate().getStigma();

				if (stigmaInfo == null) {
					log.warn("Stigma info missing for item: " + item.getItemTemplate().getTemplateId());
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}

				int needSkill = stigmaInfo.getRequireSkill().size();
				for (RequireSkill rs : stigmaInfo.getRequireSkill()) {
					for (int id : rs.getSkillId()) {
						if (player.getSkillList().isSkillPresent(id)) {
							needSkill--;
							break;
						}
					}
				}
				if (needSkill != 0) {
					AuditLogger.info(player,"Possible client hack advenced stigma skill.");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}
				if (item.getItemTemplate().isClassSpecific(player.getCommonData().getPlayerClass()) == false) {
					AuditLogger.info(player,"Possible client hack not valid for class.");
					player.getEquipment().unEquipItem(item.getObjectId(), 0);
					continue;
				}
			}
		}
	}

	/**
	 * Get the number of available Stigma
	 * 
	 * @param player
	 * @return
	 */
	private static int getPossibleStigmaCount(Player player) {
		if (player == null || player.getLevel() < 20)
			return 0;

		if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
			return 6;
		}

		/*
		 * Stigma Quest Elyos: 1929, Asmodians: 2900
		 */
		boolean isCompleteQuest = false;

		if (player.getRace() == Race.ELYOS) {
			isCompleteQuest = player.isCompleteQuest(1929)
				|| (player.getQuestStateList().getQuestState(1929).getStatus() == QuestStatus.START && player
					.getQuestStateList().getQuestState(1929).getQuestVars().getQuestVars() == 98);
		}
		else {
			isCompleteQuest = player.isCompleteQuest(2900)
				|| (player.getQuestStateList().getQuestState(2900).getStatus() == QuestStatus.START && player
					.getQuestStateList().getQuestState(2900).getQuestVars().getQuestVars() == 99);
		}

		int playerLevel = player.getLevel();

		if (isCompleteQuest) {
			if (playerLevel < 30)
				return 2;
			else if (playerLevel < 40)
				return 3;
			else if (playerLevel < 50)
				return 4;
			else if (playerLevel < 55)
				return 5;
			else
				return 6;
		}
		return 0;
	}

	/**
	 * Get the number of available Advenced Stigma
	 * 
	 * @param player
	 * @return
	 */
	private static int getPossibleAdvencedStigmaCount(Player player) {
		if (player == null || player.getLevel() < 45)
			return 0;

		if (player.havePermission(MembershipConfig.STIGMA_SLOT_QUEST)) {
			return 5;
		}
		
		int playerLevel = player.getLevel();

		if (playerLevel < 50)
			return 2;
		else if (playerLevel < 51)
			return 3;
		else if (playerLevel < 55)
			return 4;
		else 
			return 5;

		/*
		 * Advenced Stigma Quest 1st - Elyos: 3930, Asmodians: 4934 2nd - Elyos: 3931, Asmodians: 4935 3rd- Elyos: 3932,
		 * Asmodians: 4936 4th - Elyos: 11049, Asmodians: 21049 5th - Elyos: 30217, Asmodians: 30317
		 */
		/*
		if (player.getRace() == Race.ELYOS) {
			// Check whether Stigma Quests
			if (!player.isCompleteQuest(1929))
				return 0;

			if (player.isCompleteQuest(30217) || player.isCompleteQuest(11276))
				return 5;
			else if (player.isCompleteQuest(11049))
				return 4;
			else if (player.isCompleteQuest(3932))
				return 3;
			else if (player.isCompleteQuest(3931))
				return 2;
			else if (player.isCompleteQuest(3930))
				return 1;
		}
		else {
			// Check whether Stigma Quests
			if (!player.isCompleteQuest(2900))
				return 0;

			if (player.isCompleteQuest(30317) || player.isCompleteQuest(21278))
				return 5;
			else if (player.isCompleteQuest(21049))
				return 4;
			else if (player.isCompleteQuest(4936))
				return 3;
			else if (player.isCompleteQuest(4935))
				return 2;
			else if (player.isCompleteQuest(4934))
				return 1;
		}
		return 0;
		*/
	}

	/**
	 * Stigma is a worn check available slots
	 * 
	 * @param player
	 * @param item
	 * @return
	 */
	private static boolean isPossibleEquippedStigma(Player player, Item item) {
		if (player == null || (item == null || !item.getItemTemplate().isStigma()))
			return false;

		int itemSlotToEquip = item.getEquipmentSlot();

		// Stigma
		if (itemSlotToEquip >= ItemSlot.STIGMA1.getSlotIdMask() && itemSlotToEquip <= ItemSlot.STIGMA6.getSlotIdMask()) {
			int stigmaCount = getPossibleStigmaCount(player);

			if (stigmaCount > 0) {
				if (stigmaCount == 1) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 2) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 3) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 4) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA4.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 5) {
					if (itemSlotToEquip == ItemSlot.STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA3.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA4.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.STIGMA5.getSlotIdMask())
						return true;
				}
				else if (stigmaCount == 6)
					return true;
			}
		}
		// Advenced Stigma
		else if (itemSlotToEquip >= ItemSlot.ADV_STIGMA1.getSlotIdMask()
			&& itemSlotToEquip <= ItemSlot.ADV_STIGMA5.getSlotIdMask()) {
			int advStigmaCount = getPossibleAdvencedStigmaCount(player);

			if (advStigmaCount > 0) {
				if (advStigmaCount == 1) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 2) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 3) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA3.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 4) {
					if (itemSlotToEquip == ItemSlot.ADV_STIGMA1.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA2.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA3.getSlotIdMask()
						|| itemSlotToEquip == ItemSlot.ADV_STIGMA4.getSlotIdMask())
						return true;
				}
				else if (advStigmaCount == 5)
					return true;
			}
		}
		return false;
	}
}
