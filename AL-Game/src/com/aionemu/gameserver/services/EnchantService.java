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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatEnchantFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 * @modified Wakizashi, Source, vlog
 */
public class EnchantService {

	private static final Logger log = LoggerFactory.getLogger(EnchantService.class);

	/**
	 * @param player
	 * @param targetItem
	 * @param parentItem
	 */
	public static boolean breakItem(Player player, Item targetItem, Item parentItem) {
		Storage inventory = player.getInventory();

		if (inventory.getItemByObjId(targetItem.getObjectId()) == null)
			return false;
		if (inventory.getItemByObjId(parentItem.getObjectId()) == null)
			return false;

		ItemTemplate itemTemplate = targetItem.getItemTemplate();
		int quality = itemTemplate.getItemQuality().getQualityId();
		
		if (!itemTemplate.isArmor() && !itemTemplate.isWeapon()) {
			AuditLogger.info(player, "Player try break dont compatible item type.");
			return false;
		}

		if (!itemTemplate.isArmor() && !itemTemplate.isWeapon()) {
			AuditLogger.info(player, "Break item hack, armor/weapon iD changed.");
			return false;
		}

		// Quality modifier
		/*if (itemTemplate.isSoulBound() && !itemTemplate.isArmor())
			quality += 1;
		else if (!itemTemplate.isSoulBound() && itemTemplate.isArmor())
			quality -= 1;*/
		
		// 0 JUNK
		// 1 COMMON
		// 2 RARE
		// 3 LEGEND
		// 4 UNIQUE
		// 5 EPIC
		// 6 MYTHIC
		
		// Junk = common
		Math.max(1, quality);
		// in 2.7 Mythic or higher = epic
		Math.min(5, quality);


		int numberMin = 1;
		int numberMax = 1 + quality;
		int levelMin = -10;//-10 + 10 * (quality - 1);
		int levelMax = 14;//14  + 10 * (quality - 1);
		
		switch (quality) {
			case 0: // JUNK
			case 1: // COMMON
				levelMin = -5;
				levelMax = 0;
				break;
			case 2: // RARE
				levelMin = 0;
				levelMax = 10;
				break;
			case 3: // LEGEND
				levelMin = 5;
				levelMax = 24;
				break;
			case 4: // UNIQUE
				levelMin = 15;
				levelMax = 49;
				break;
			case 5: // EPIC
				levelMin = 30;
				levelMax = 69;
				break;
			case 6: // MYTHIC
				levelMin = 30;
				levelMax = 69;
			case 7:
				levelMin = 30;
				levelMax = 69;
				break;
		}

		int level = getRandomNumerAverage(levelMin, levelMax);
		int number = getRandomNumerAverage(numberMin, numberMax);
		int enchantItemLevel = targetItem.getItemTemplate().getLevel() + level + (itemTemplate.isWeapon() ? 5 : 0);
		enchantItemLevel = Math.min(enchantItemLevel, 189);
		enchantItemLevel = Math.max(0, enchantItemLevel);
		
		//int enchantItemLevel = targetItem.getItemTemplate().getLevel() + level;
		int enchantItemId = 166000000 + enchantItemLevel;

		if (inventory.delete(targetItem) != null) {
			if (inventory.decreaseByObjectId(parentItem.getObjectId(), 1))
				ItemService.addItem(player, enchantItemId, number);
		}
		else
			AuditLogger.info(player, "Possible break item hack, do not remove item.");
		return true;
	}
	
	private static int getRandomNumerAverage(int min, int max){
		int cmpt = 1;
		List<Integer> pioche = new ArrayList<Integer>();
		float half = (min + max) / 2;
		
		for(int i=min; i<=half; i++){
			for(int j=0; j<cmpt; j++){
				pioche.add(min);
				if(min != max){
					pioche.add(max);
				}
			}
			min++;
			max--;
			cmpt++;
		}
				
		return pioche.get(Rnd.get(0, pioche.size()-1));
	}

	/**
	 * @param player
	 * @param parentItem
	 *          the enchantment stone
	 * @param targetItem
	 *          the item to enchant
	 * @param supplementItem
	 *          the item, giving additional chance
	 * @return true, if successful
	 */
	public static boolean enchantItem(Player player, Item parentItem, Item targetItem, Item supplementItem) {
		int enchantStoneLevel = parentItem.getItemTemplate().getLevel();
		int targetItemLevel = targetItem.getItemTemplate().getLevel();
		int enchantitemLevel = targetItem.getEnchantLevel() + 1;

		// Modifier, depending on the quality of the item
		// Decreases the chance of enchant
		int qualityCap = 0;

		ItemQuality quality = targetItem.getItemTemplate().getItemQuality();

		switch (quality) {
			case JUNK:
			case COMMON:
				qualityCap = 5;
				break;
			case RARE:
				qualityCap = 10;
				break;
			case LEGEND:
				qualityCap = 15;
				break;
			case UNIQUE:
				qualityCap = 20;
				break;
			case EPIC:
				qualityCap = 25;
				break;
			case MYTHIC:
				qualityCap = 30;
				break;
		}

		// Start value of success
		float success = EnchantsConfig.ENCHANT_STONE;

		// Extra success chance
		// The greater the enchant stone level, the greater the
		// level difference modifier
		int levelDiff = enchantStoneLevel - targetItemLevel;
		success += levelDiff > 0 ? levelDiff * 3f / qualityCap : 0;

		// Level difference
		// Can be negative, if the item quality too hight
		// And the level difference too small
		success += levelDiff - qualityCap;

		// Enchant next level difficulty
		// The greater item enchant level,
		// the lower start success chance
		success -= targetItem.getEnchantLevel() * qualityCap / (enchantitemLevel > 10 ? 4f : 5f);

		// Supplement is used
		if (supplementItem != null) {
			// Amount of supplement items
			int supplementUseCount = 1;
			// Additional success rate for the supplement
			int addsuccessRate = 10;
			int supplementId = supplementItem.getItemTemplate().getTemplateId();
			int enchantstoneLevel = parentItem.getItemTemplate().getLevel();

			switch (supplementId) {
				// lesser supplements
				case 166100000: // Heroic or Less (blue)
				case 166100003: // Fabled (yellow)
				case 166100006: // Eternal (orange)
					addsuccessRate = EnchantsConfig.LSUP; // Default 10
					break;

				// supplements
				case 166100001: // Heroic or Less (blue)
				case 166100004: // Fabled (yellow)
				case 166100007: // Eternal (orange)
					addsuccessRate = EnchantsConfig.RSUP; // Default 20
					break;

				// greater supplements
				case 166100002: // Heroic or Less (blue)
				case 166100005: // Fabled (yellow)
				case 166100008: // Eternal (orange)
					addsuccessRate = EnchantsConfig.GSUP; // Default 30
					break;

				default:
					AuditLogger.info(player, "Possible client hack. Supplement Id incorrect!!! Id: " + supplementId);
					return false;
			}

			// -- Required supplements depending on the level of enchant stone

			// Enchant stone level 31 - 40
			if (enchantstoneLevel > 30 && enchantstoneLevel < 41)
				supplementUseCount = 5;

			// Enchant stone level 41 - 50
			if (enchantstoneLevel > 40 && enchantstoneLevel < 51)
				supplementUseCount = 10;

			// Enchant stone level 51 - 60
			if (enchantstoneLevel > 50 && enchantstoneLevel < 61)
				supplementUseCount = 25;

			// Enchant stone level 61 - 70
			if (enchantstoneLevel > 60 && enchantstoneLevel < 71)
				supplementUseCount = 55;

			// Enchant stone level 71 - 80
			if (enchantstoneLevel > 70 && enchantstoneLevel < 81)
				supplementUseCount = 85;

			// Enchant stone level 81 - 90
			if (enchantstoneLevel > 80 && enchantstoneLevel < 91)
				supplementUseCount = 115;

			// Enchant stone level 91 - 100
			if (enchantstoneLevel > 90 && enchantstoneLevel < 101)
				supplementUseCount = 145;

			// Enchant stone level 101 - 110
			if (enchantstoneLevel > 100 && enchantstoneLevel < 111)
				supplementUseCount = 175;

			// Enchant stone level 111 - 120
			if (enchantstoneLevel > 110 && enchantstoneLevel < 121)
				supplementUseCount = 205;

			// Enchant stone level 121 - 130
			if (enchantstoneLevel > 120 && enchantstoneLevel < 131)
				supplementUseCount = 235;

			// Enchant stone level 131 - 140
			if (enchantstoneLevel > 130 && enchantstoneLevel < 141)
				supplementUseCount = 265;

			// Enchant stone level 141 - 150
			if (enchantstoneLevel > 140 && enchantstoneLevel < 151)
				supplementUseCount = 295;

			// Enchant stone level 151 - 160
			if (enchantstoneLevel > 150 && enchantstoneLevel < 161)
				supplementUseCount = 325;

			// Enchant stone level 161 - 170
			if (enchantstoneLevel > 160 && enchantstoneLevel < 171)
				supplementUseCount = 355;

			// Enchant stone level 171 - 180
			if (enchantstoneLevel > 170 && enchantstoneLevel < 181)
				supplementUseCount = 385;

			// Enchant stone level 181 and higher
			if (enchantstoneLevel > 180)
				supplementUseCount = 415;

			// Beginning from the level 11 of the enchantment of the item,
			// There will be 2 times more supplements required
			if (enchantitemLevel > 10)
				supplementUseCount = supplementUseCount * 2;

			// Check the required amount of the supplements
			
			if (player.getInventory().getItemCountByItemId(supplementId) < supplementUseCount)
				return false;

			// Add success rate of the supplement to the overall chance
			success += addsuccessRate;
			
			// Put supplements to wait for update
			player.subtractSupplements(supplementUseCount, supplementId);
		}

		// The overall success chance can't be more, than 95
		if (success >= 95)
			success = 95;

		boolean result = false;
		float random = Rnd.get(1, 1000) / 10f;

		// If the random number < overall success rate,
		// The item will be successfully enchanted
		if (random < success)
			result = true;

		// For test purpose. To use by administrator
		if (player.getAccessLevel() > 1)
			PacketSendUtility.sendMessage(player, (result ? "Success" : "Fail") + " Rnd:" + random + " Luck:" + success);
		else
			PacketSendUtility.sendMessage(player, "Taux de reussite :" + success);
		return result;
	}

	public static void enchantItemAct(Player player, Item parentItem, Item targetItem, Item supplementItem,
			int currentEnchant, boolean result) {
		ItemQuality targetQuality = targetItem.getItemTemplate().getItemQuality();
		
		int addLevel = 1;
		int rnd = Rnd.get(100); //crit modifier
		if (rnd < 2)
			addLevel = 3;
		else if (rnd < 7)
			addLevel = 2;
		
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
			AuditLogger.info(player, "Possible enchant hack, do not remove enchant stone.");
			return;
		}
		//Decrease required supplements
		player.updateSupplements(result);
		
		// Items that are Fabled or Eternal can get up to +15.
		if (result) {
			switch (targetQuality) {
				case COMMON:
				case RARE:
				case LEGEND:
					if (currentEnchant >= EnchantsConfig.ENCHANT_MAX_LEVEL_TYPE1) {
						AuditLogger.info(player, "Possible enchant hack, send fake packet for enchant up more posible.");
						return;
					}
					else
						currentEnchant += addLevel;
					currentEnchant = Math.min(currentEnchant, EnchantsConfig.ENCHANT_MAX_LEVEL_TYPE1);
					break;
				case UNIQUE:
				case EPIC:
				case MYTHIC:
					if (currentEnchant >= EnchantsConfig.ENCHANT_MAX_LEVEL_TYPE2) {
						AuditLogger.info(player, "Possible enchant hack, send fake packet for enchant up more posible.");
						return;
					}
					else
						currentEnchant += addLevel;
					currentEnchant = Math.min(currentEnchant, EnchantsConfig.ENCHANT_MAX_LEVEL_TYPE2);
					break;
				case JUNK:
					return;
			}
		}
		else {
			// Retail: http://powerwiki.na.aiononline.com/aion/Patch+Notes:+1.9.0.1
			// When socketing fails at +11~+15, the value falls back to +10.
			if (currentEnchant > 10)
				currentEnchant = 10;
			else if (currentEnchant > 0)
				currentEnchant -= 1;
		}

		targetItem.setEnchantLevel(currentEnchant);
		if (targetItem.isEquipped())
			player.getGameStats().updateStatsVisually();

		ItemPacketService.updateItemAfterInfoChange(player, targetItem);

		if (targetItem.isEquipped())
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		else
			player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);

		if (result)
			PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_SUCCEED(new DescriptionId(targetItem.getNameID())));
		else
			PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameID())));
	}

	/**
	 * @param player
	 * @param parentItem
	 *          the manastone
	 * @param targetItem
	 *          the item to socket
	 * @param supplementItem
	 * @param targetWeapon
	 *          fusioned weapon
	 */
	public static boolean socketManastone(Player player, Item parentItem, Item targetItem, Item supplementItem,
			int targetWeapon) {

		int targetItemLevel = 1;

		// Fusioned weapon. Primary weapon level.
		if (targetWeapon == 1)
			targetItemLevel = targetItem.getItemTemplate().getLevel();
		// Fusioned weapon. Secondary weapon level.
		else
			targetItemLevel = targetItem.getFusionedItemTemplate().getLevel();

		int stoneLevel = parentItem.getItemTemplate().getLevel();
		int slotLevel = (int) (10 * Math.ceil((targetItemLevel + 10) / 10d));
		boolean result = false;

		// Start value of success
		float success = EnchantsConfig.MANA_STONE;

		// The current amount of socketed stones
		int stoneCount;

		// Manastone level shouldn't be greater as 20 + item level
		// Example: item level: 1 - 10. Manastone level should be <= 20
		if (stoneLevel > slotLevel)
			return false;

		// Fusioned weapon. Primary weapon slots.
		if (targetWeapon == 1)
			// Count the inserted stones in the primary weapon
			stoneCount = targetItem.getItemStones().size();
		// Fusioned weapon. Secondary weapon slots.
		else
			// Count the inserted stones in the secondary weapon
			stoneCount = targetItem.getFusionStones().size();

		// Fusioned weapon. Primary weapon slots.
		if (targetWeapon == 1) {
			// Find all free slots in the primary weapon
			if (stoneCount >= targetItem.getSockets(false)) {
				AuditLogger.info(player, "Manastone socket overload");
				return false;
			}
		}
		// Fusioned weapon. Secondary weapon slots.
		else if (!targetItem.hasFusionedItem() || stoneCount >= targetItem.getSockets(true)) {
			// Find all free slots in the secondary weapon
			AuditLogger.info(player, "Manastone socket overload");
			return false;
		}

		// Stone quality modifier
		success += parentItem.getItemTemplate().getItemQuality() == ItemQuality.COMMON ? 25f : 15f;

		// Next socket difficulty modifier
		float socketDiff = stoneCount * 1.25f + 1.75f;

		// Level difference
		success += (slotLevel - stoneLevel) / socketDiff;

		// The supplement item is used
		if (supplementItem != null) {
			int supplementUseCount = 1;
			int addsuccessRate = 10;
			int supplementId = supplementItem.getItemTemplate().getTemplateId();
			int manastoneId = parentItem.getItemTemplate().getTemplateId();
			boolean isSupplements = true;

			int manastoneCount;
			// Not fusioned
			if (targetWeapon == 1)
				manastoneCount = targetItem.getItemStones().size() + 1;
			// Fusioned
			else
				manastoneCount = targetItem.getFusionStones().size() + 1;

			switch (supplementId) {
				// lesser supplements
				case 166100000:
				case 166100003:
				case 166100006:
					addsuccessRate = EnchantsConfig.LSUP; // Default 10
					break;

				// supplements
				case 166100001:
				case 166100004:
				case 166100007:
					addsuccessRate = EnchantsConfig.RSUP; // Default 20
					break;

				// greater supplements
				case 166100002:
				case 166100005:
				case 166100008:
					addsuccessRate = EnchantsConfig.GSUP; // Default 30
					break;

				// felicitous socketing
				case 166150003:
				case 166150004:
					addsuccessRate = EnchantsConfig.FESO; // Default 100
					supplementUseCount = 1;
					isSupplements = false;
					break;

				default:
					AuditLogger.info(player, "Possible client hack. Supplement Id incorrect!!! Id: " + supplementId);
					return false;
			}

			if(isSupplements)
			{
				// basic formula by manastone level

				// 31 - 40
				if (stoneLevel > 30)
					supplementUseCount = supplementUseCount + 1; // 2

				// 41 - 50
				if (stoneLevel > 40)
					supplementUseCount = supplementUseCount + 1; // 3

				// 51 and higher
				if (stoneLevel > 50)
					supplementUseCount = supplementUseCount + 1; // 4

				// manastone attacks and crit strike use more supplements
				switch (manastoneId) {
					// Manastone level: 10
					case 167000226: // HP + 20
					case 167000227: // MP + 20
					case 167000228: // Accuracy +12
					case 167000229: // Evasion +4
					case 167000231: // Magic Boost +12
					case 167000232: // Parry +12
					case 167000233: // Block +12
					case 167000258:
					case 167000259:
					case 167000260:
					case 167000261:
					case 167000263:
					case 167000264:
					case 167000265:
					case 167000290:
					case 167000291:
					case 167000292:
					case 167000293:
					case 167000295:
					case 167000296:
					case 167000297:
					case 167000525:
					case 167000526:
					case 167000527:
					case 167000528:
					case 167000529:
					case 167000530:
						supplementUseCount = 1; // 1
						break;
					// Manastone level 40
					case 167000322: // HP +50
					case 167000323:
					case 167000324:
					case 167000325:
					case 167000327:
					case 167000328:
					case 167000329:
					case 167000531:
					case 167000532:
						supplementUseCount = 2; // 2
						break;
					// Manastone level 50
					case 167000354: // HP +60
					case 167000355:
					case 167000356:
					case 167000357:
					case 167000359:
					case 167000360:
					case 167000361:
					case 167000533:
					case 167000534:
						supplementUseCount = 3; // 3
						break;
					// Manastone level 60
					case 167000543: // HP +70
					case 167000544:
					case 167000545:
					case 167000546:
					case 167000547:
					case 167000548:
					case 167000549:
						supplementUseCount = 4; // 4
						break;
					// Crit and Attack manastone level 10
					case 167000230: // Attack +1
					case 167000235: // Crit Strike +4
					case 167000267:
					case 167000294:
					case 167000299:
					case 167000418:
					case 167000419:
					case 167000420:
					case 167000421:
					case 167000423:
					case 167000424:
					case 167000425:
					case 167000450:
					case 167000451:
					case 167000452:
					case 167000453:
					case 167000455:
					case 167000456:
					case 167000457:
					case 167000465:
					case 167000535:
					case 167000536:
					case 167000537:
					case 167000538:
					case 167001002:
						supplementUseCount = 5; // 5
						break;
					// Crit and Attack manastone or green manastone level 10
					case 167000331: // Crit Strike +10
					case 167000482: // HP +75
					case 167000483:
					case 167000484:
					case 167000485:
					case 167000487:
					case 167000488:
					case 167000489:
					case 167000497:
					case 167000539:
					case 167000540:
						supplementUseCount = 10; // 10
						break;
					// Crit and Attack manastone level 50
					case 167000358: // Attack +3
					case 167000363: // Crit Strike +12
					case 167000514:
					case 167000515:
					case 167000516:
					case 167000517:
					case 167000519:
					case 167000520:
					case 167000521:
					case 167000523:
					case 167000541:
					case 167000542:
						supplementUseCount = 15; // 15
						break;
					// Crit and Attack manastone level 60
					case 167000550: // Crit Strike +14
					case 167000551:
					case 167000552:
					case 167000553:
					case 167000554:
					case 167000555:
					case 167000556:
					case 167000557:
					case 167000559:
					case 167000560:
					case 167000561:
						supplementUseCount = 20; // 20
						break;
					// Green crit and Attack manastone level 20
					case 167000427: // Crit Strike +9
					case 167000454:
					case 167000459:
					case 167001001:
						supplementUseCount = 25; // 25
						break;
					// Green manastone level 40
					case 167000491: // Crit Strike +13
						supplementUseCount = 50; // 50
						break;
					// Green manastone level 50
					case 167000518: // Attack +5
					case 167000522: // Crit Strike +15
						supplementUseCount = 75; // 75
						break;
					case 167000558:// Crit Strike +17
						supplementUseCount = 100; // 100
						break;
				}

				// supplementUseCount * manastoneCount
				if (stoneCount > 0)
					supplementUseCount = supplementUseCount * manastoneCount;
			}

			if (player.getInventory().getItemCountByItemId(supplementId) < supplementUseCount)
				return false;

			// Add successRate
			// Manastones have base/2 additional success bonus
			success += addsuccessRate / 2;
			
			// Put supplements to wait for update
			player.subtractSupplements(supplementUseCount, supplementId);
		}

		float random = Rnd.get(1, 1000) / 10f;

		if (random < success)
			result = true;

		// For test purpose. To use by administrator
		if (player.getAccessLevel() > 2)
			PacketSendUtility.sendMessage(player, (result ? "Success" : "Fail") + " Rnd:" + random + " Luck:" + success);

		return result;
	}

	public static void socketManastoneAct(Player player, Item parentItem, Item targetItem, Item supplementItem,
			int targetWeapon, boolean result) {
		//Decrease required supplements
		player.updateSupplements(result);
		
		if (player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1) && result) {
			PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_OPTION_SUCCEED(new DescriptionId(targetItem.getNameID())));

			if (targetWeapon == 1) {
				ManaStone manaStone = ItemSocketService.addManaStone(targetItem, parentItem.getItemTemplate().getTemplateId());
				if (targetItem.isEquipped()) {
					ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
					player.getGameStats().updateStatsAndSpeedVisually();
				}
			}
			else {
				ManaStone manaStone = ItemSocketService.addFusionStone(targetItem, parentItem.getItemTemplate().getTemplateId());
				if (targetItem.isEquipped()) {
					ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
					player.getGameStats().updateStatsAndSpeedVisually();
				}
			}
		}
		else {
			PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_OPTION_FAILED(new DescriptionId(targetItem.getNameID())));
			if (targetWeapon == 1) {
				Set<ManaStone> manaStones = targetItem.getItemStones();
				if (targetItem.isEquipped()) {
					ItemEquipmentListener.removeStoneStats(manaStones, player.getGameStats());
					player.getGameStats().updateStatsAndSpeedVisually();
				}
				ItemSocketService.removeAllManastone(player, targetItem);
			}
			else {
				Set<ManaStone> manaStones = targetItem.getFusionStones();

				if (targetItem.isEquipped()) {
					ItemEquipmentListener.removeStoneStats(manaStones, player.getGameStats());
					player.getGameStats().updateStatsAndSpeedVisually();
				}

				ItemSocketService.removeAllFusionStone(player, targetItem);
			}
		}

		ItemPacketService.updateItemAfterInfoChange(player, targetItem);
	}

	/**
	 * @param player
	 * @param item
	 */
	public static void onItemEquip(Player player, Item item) {
		List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
		try {
			if (item.getItemTemplate().isWeapon()) {
				switch (item.getItemTemplate().getWeaponType()) {
					case BOOK_2H:
					case ORB_2H:
						modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
						modifiers.add(new StatEnchantFunction(item, StatEnum.MAGICAL_ATTACK));
						break;
					case MACE_1H:
					case STAFF_2H:
						modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
					case DAGGER_1H:
					case BOW:
					case POLEARM_2H:
					case SWORD_1H:
					case SWORD_2H:
						if (item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask())
							modifiers.add(new StatEnchantFunction(item, StatEnum.MAIN_HAND_POWER));
						else
							modifiers.add(new StatEnchantFunction(item, StatEnum.OFF_HAND_POWER));
				}
			}
			else if (item.getItemTemplate().isArmor()) {
				if (item.getItemTemplate().getArmorType() == ArmorType.SHIELD) {
					modifiers.add(new StatEnchantFunction(item, StatEnum.DAMAGE_REDUCE));
					modifiers.add(new StatEnchantFunction(item, StatEnum.BLOCK));
				}
				else {
					modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_DEFENSE));
					modifiers.add(new StatEnchantFunction(item, StatEnum.MAXHP));
					modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_CRITICAL_RESIST));
				}
			}
			if (!modifiers.isEmpty())
				player.getGameStats().addEffect(item, modifiers);
		}
		catch (Exception ex) {
			log.error("Error on item equip.", ex);
		}
	}

}