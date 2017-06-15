/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Avol, ATracer, kosyachok
 * @modified cura
 */
public class Equipment {

	private static final Logger log = LoggerFactory.getLogger(Equipment.class);

	private SortedMap<Integer, Item> equipment = new TreeMap<Integer, Item>();
	private Player owner;

	private Set<Integer> markedFreeSlots = new HashSet<Integer>();
	private PersistentState persistentState = PersistentState.UPDATED;

	private static final int[] ARMOR_SLOTS = new int[] { ItemSlot.BOOTS.getSlotIdMask(), ItemSlot.GLOVES.getSlotIdMask(),
		ItemSlot.PANTS.getSlotIdMask(), ItemSlot.SHOULDER.getSlotIdMask(), ItemSlot.TORSO.getSlotIdMask() };

	public Equipment(Player player) {
		this.owner = player;
	}

	/**
	 * @param itemUniqueId
	 * @param slot
	 * @return item or null in case of failure
	 */
	public Item equipItem(int itemUniqueId, int slot) {
		Item item = owner.getInventory().getItemByObjId(itemUniqueId);

		if (item == null)
			return null;

		ItemTemplate itemTemplate = item.getItemTemplate();

		// don't allow to wear items of higher level
		if (itemTemplate.getLevel() > owner.getCommonData().getLevel()) {
			PacketSendUtility.sendPacket(
				owner,
				SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(item.getNameID(),
					itemTemplate.getLevel()));
			return null;
		}
		
		if (owner.getAccessLevel() == 0) {
			if ((itemTemplate.getRace() == Race.ASMODIANS || itemTemplate.getRace() == Race.ELYOS) && itemTemplate.getRace() != owner.getRace()) {
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_RACE);
				return null;
			}
	
			int requiredLevel = item.getItemTemplate().getRequiredLevel(owner.getCommonData().getPlayerClass());
			if (requiredLevel == -1 || requiredLevel > owner.getLevel())
				return null;
		}
		
		int itemSlotToEquip = 0;

		synchronized (equipment) {
			markedFreeSlots.clear();

			// validate item against current equipment and mark free slots
			switch (item.getEquipmentType()) {
				case ARMOR:
					if (!validateEquippedArmor(item, true))
						return null;
					break;
				case WEAPON:
					if (!validateEquippedWeapon(item, true))
						return null;
					break;
			}
			
			// check whether there is already item in specified slot
			int itemSlotMask = 0;
			switch (item.getEquipmentType()) {
				case STIGMA:
					itemSlotMask = slot;
					break;
				default:
					itemSlotMask = itemTemplate.getItemSlot();
					break;
			}

			if (!StigmaService.notifyEquipAction(owner, item, slot))
				return null;
			// find correct slot
			ItemSlot[] possibleSlots = ItemSlot.getSlotsFor(itemSlotMask);
			for (int i = 0; i < possibleSlots.length; i++) {
				ItemSlot possibleSlot = possibleSlots[i];
				int slotId = possibleSlot.getSlotIdMask();
				if (equipment.get(slotId) == null || markedFreeSlots.contains(slotId)) {
					itemSlotToEquip = slotId;
					break;
				}
			}

			// equip first occupied slot if there is no free
			if (itemSlotToEquip == 0)
				itemSlotToEquip = possibleSlots[0].getSlotIdMask();
		}

		if (itemSlotToEquip == 0)
			return null;
		
		if (itemTemplate.isSoulBound() && !item.isSoulBound()) {
			soulBindItem(owner, item, itemSlotToEquip);
			return null;
		}
		
		return equip(itemSlotToEquip, item);
	}

	/**
	 * @param itemSlotToEquip
	 * @param item
	 */
	private Item equip(int itemSlotToEquip, Item item) {
		synchronized (equipment) {
			// remove item first from inventory to have at least one slot free
			owner.getInventory().remove(item);

			// do unequip of necessary items
			Item equippedItem = equipment.get(itemSlotToEquip);
			if (equippedItem != null)
				unEquip(itemSlotToEquip);

			switch (item.getEquipmentType()) {
				case ARMOR:
					validateEquippedArmor(item, false);
					break;
				case WEAPON:
					validateEquippedWeapon(item, false);
					break;
			}

			if (equipment.get(itemSlotToEquip) != null) {
				log.error("CHECKPOINT : putting item to already equiped slot. Info slot: " + itemSlotToEquip + " new item: "
					+ item.getItemTemplate().getTemplateId() + " old item: "
					+ equipment.get(itemSlotToEquip).getItemTemplate().getTemplateId());
				return null;
			}

			// equip target item
			equipment.put(itemSlotToEquip, item);
			item.setEquipped(true);
			item.setEquipmentSlot(itemSlotToEquip);
			ItemPacketService.updateItemAfterEquip(owner, item);

			// update stats
			notifyItemEquipped(item);
			owner.getLifeStats().updateCurrentStats();
			setPersistentState(PersistentState.UPDATE_REQUIRED);
			QuestEngine.getInstance().onEquipItem(new QuestEnv(null, owner, 0, 0), item.getItemId());
			return item;
		}
	}

	private void notifyItemEquipped(Item item) {
		ItemEquipmentListener.onItemEquipment(item, owner);
		owner.getObserveController().notifyItemEquip(item, owner);
		tryUpdateSummonStats();
	}

	private void notifyItemUnequip(Item item) {
		ItemEquipmentListener.onItemUnequipment(item, owner);
		owner.getObserveController().notifyItemUnEquip(item, owner);
		tryUpdateSummonStats();
	}

	private void tryUpdateSummonStats() {
		Summon summon = owner.getSummon();
		if (summon != null) {
			summon.getGameStats().updateStatsAndSpeedVisually();
		}
	}

	/**
	 * Called when CM_EQUIP_ITEM packet arrives with action 1
	 * 
	 * @param itemUniqueId
	 * @param slot
	 * @return item or null in case of failure
	 */
	public Item unEquipItem(int itemUniqueId, int slot) {
		// if inventory is full unequip action is disabled
		if (owner.getInventory().isFull())
			return null;

		synchronized (equipment) {
			Item itemToUnequip = null;

			for (Item item : equipment.values()) {
				if (item.getObjectId() == itemUniqueId) {
					itemToUnequip = item;
				}
			}

			if (itemToUnequip == null || !itemToUnequip.isEquipped())
				return null;

			// if unequip bow - unequip arrows also
			if (itemToUnequip.getItemTemplate().getWeaponType() == WeaponType.BOW) {
				Item possibleArrows = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
				if (possibleArrows != null && possibleArrows.getItemTemplate().getArmorType() == ArmorType.ARROW) {
					// TODO more wise check here is needed
					if (owner.getInventory().getFreeSlots() < 1)
						return null;
					unEquip(ItemSlot.SUB_HAND.getSlotIdMask());
				}
			}

			//Looks very odd - but its retail like
			if (itemToUnequip.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask()) {
				Item ohWeapon = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
				if (ohWeapon != null && ohWeapon.getItemTemplate().isWeapon()) {
					if (owner.getInventory().getFreeSlots() < 2) {
						return null;
					}
					unEquip(ItemSlot.SUB_HAND.getSlotIdMask());
				}
			}

			// if unequip power shard
			if (itemToUnequip.getItemTemplate().isArmor()
				&& itemToUnequip.getItemTemplate().getArmorType() == ArmorType.SHARD) {
				owner.unsetState(CreatureState.POWERSHARD);
				PacketSendUtility.sendPacket(owner, new SM_EMOTION(owner, EmotionType.POWERSHARD_OFF, 0, 0));
			}

			if (!StigmaService.notifyUnequipAction(owner, itemToUnequip))
				return null;

			unEquip(itemToUnequip.getEquipmentSlot());
			return itemToUnequip;
		}
	}

	private void unEquip(int slot) {
		Item item = equipment.remove(slot);
		if (item == null) { // NPE check, there is no item in the given slot.
			return;
		}

		item.setEquipped(false);
		item.setEquipmentSlot(0);
		notifyItemUnequip(item);
		owner.getLifeStats().updateCurrentStats();
		owner.getGameStats().updateStatsAndSpeedVisually();
		owner.getInventory().put(item);
	}

	/**
	 * Used during equip process and analyzes equipped slots
	 * 
	 * @param item
	 * @param itemInMainHand
	 * @param itemInSubHand
	 * @return
	 */
	private boolean validateEquippedWeapon(Item item, boolean validateOnly) {
		// check present skill
		int[] requiredSkills = item.getItemTemplate().getWeaponType().getRequiredSkills();

		if (!checkAvaialbeEquipSkills(requiredSkills))
			return false;

		Item itemInMainHand = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		Item itemInSubHand = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());

		int requiredSlots = 0;
		switch (item.getItemTemplate().getWeaponType().getRequiredSlots()) {
			case 2:
				switch (item.getItemTemplate().getWeaponType()) {
				// if bow and arrows are equipped + new item is bow - dont uneqiup arrows
					case BOW:
						if (itemInSubHand != null && itemInSubHand.getItemTemplate().getArmorType() != ArmorType.ARROW) {
							if (validateOnly) {
								requiredSlots++;
								markedFreeSlots.add(ItemSlot.SUB_HAND.getSlotIdMask());
							}
							else
								unEquip(ItemSlot.SUB_HAND.getSlotIdMask());
						}
						break;
					// if new item is not bow - unequip arrows
					default:
						if (itemInSubHand != null) {
							if (validateOnly) {
								requiredSlots++;
								markedFreeSlots.add(ItemSlot.SUB_HAND.getSlotIdMask());
							}
							else
								unEquip(ItemSlot.SUB_HAND.getSlotIdMask());
						}
				}// no break
			case 1:
				// check dual skill
				if (itemInMainHand != null
					&& (!owner.getSkillList().isSkillPresent(19) && !owner.getSkillList().isSkillPresent(360)
						&& !owner.getSkillList().isSkillPresent(127) && !owner.getSkillList().isSkillPresent(128) && !owner
						.getSkillList().isSkillPresent(924))) {
					if (validateOnly) {
						requiredSlots++;
						markedFreeSlots.add(ItemSlot.MAIN_HAND.getSlotIdMask());
					}
					else
						unEquip(ItemSlot.MAIN_HAND.getSlotIdMask());
				}
				// check 2h weapon in main hand
				else if (itemInMainHand != null && itemInMainHand.getItemTemplate().getWeaponType().getRequiredSlots() == 2) {
					if (validateOnly) {
						requiredSlots++;
						markedFreeSlots.add(ItemSlot.MAIN_HAND.getSlotIdMask());
					}
					else
						unEquip(ItemSlot.MAIN_HAND.getSlotIdMask());
				}

				// unequip arrows if bow+arrows were equipeed
				Item possibleArrows = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
				if (possibleArrows != null && possibleArrows.getItemTemplate().getArmorType() == ArmorType.ARROW) {
					if (validateOnly) {
						requiredSlots++;
						markedFreeSlots.add(ItemSlot.SUB_HAND.getSlotIdMask());
					}
					else
						unEquip(ItemSlot.SUB_HAND.getSlotIdMask());
				}
				break;
		}

		// check agains = required slots - 1(equipping item)
		return owner.getInventory().getFreeSlots() >= requiredSlots - 1;
	}

	/**
	 * @param requiredSkills
	 * @return
	 */
	private boolean checkAvaialbeEquipSkills(int[] requiredSkills) {
		boolean isSkillPresent = false;

		// if no skills required - validate as true
		if (requiredSkills.length == 0)
			return true;

		for (int skill : requiredSkills) {
			if (owner.getSkillList().isSkillPresent(skill)) {
				isSkillPresent = true;
				break;
			}
		}
		return isSkillPresent;
	}

	/**
	 * Used during equip process and analyzes equipped slots
	 * 
	 * @param item
	 * @param itemInMainHand
	 * @return
	 */
	private boolean validateEquippedArmor(Item item, boolean validateOnly) {
		// allow wearing of jewelry etc stuff
		ArmorType armorType = item.getItemTemplate().getArmorType();
		if (armorType == null)
			return true;

		// check present skill
		int[] requiredSkills = armorType.getRequiredSkills();
		if (!checkAvaialbeEquipSkills(requiredSkills))
			return false;

		Item itemInMainHand = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		switch (item.getItemTemplate().getArmorType()) {
			case ARROW:
				if (itemInMainHand == null || itemInMainHand.getItemTemplate().getWeaponType() != WeaponType.BOW) {
					if (validateOnly)
						return false;
				}
				break;
			case SHIELD:
				if (itemInMainHand != null && itemInMainHand.getItemTemplate().getWeaponType().getRequiredSlots() == 2) {
					if (validateOnly) {
						if (owner.getInventory().isFull())
							return false;
						markedFreeSlots.add(ItemSlot.MAIN_HAND.getSlotIdMask());
					}
					else {
						// remove 2H weapon
						unEquip(ItemSlot.MAIN_HAND.getSlotIdMask());
					}
				}
				break;
		}
		return true;
	}

	/**
	 * Will look item in equipment item set
	 * 
	 * @param value
	 * @return Item
	 */
	public Item getEquippedItemByObjId(int value) {
		synchronized (equipment) {
			for (Item item : equipment.values()) {
				if (item.getObjectId() == value)
					return item;
			}
		}

		return null;
	}

	/**
	 * @param value
	 * @return List<Item>
	 */
	public List<Item> getEquippedItemsByItemId(int value) {
		List<Item> equippedItemsById = new ArrayList<Item>();
		synchronized (equipment) {
			for (Item item : equipment.values()) {
				if (item.getItemTemplate().getTemplateId() == value)
					equippedItemsById.add(item);
			}
		}

		return equippedItemsById;
	}

	/**
	 * @return List<Item>
	 */
	public List<Item> getEquippedItems() {
		List<Item> equippedItems = new ArrayList<Item>();
		equippedItems.addAll(equipment.values());

		return equippedItems;
	}

	public List<Integer> getEquippedItemIds() {
		List<Integer> equippedIds = new ArrayList<Integer>();
		for (Item i : equipment.values()) {
			equippedIds.add(i.getItemId());
		}
		return equippedIds;
	}

	/**
	 * @return List<Item>
	 */
	public FastList<Item> getEquippedItemsWithoutStigma() {
		FastList<Item> equippedItems = FastList.newInstance();
		for (Item item : equipment.values()) {
			if (item.getEquipmentSlot() < ItemSlot.STIGMA1.getSlotIdMask())
				equippedItems.add(item);
		}

		return equippedItems;
	}

	/**
	 * @return List<Item>
	 */
	public List<Item> getEquippedItemsAllStigma() {
		List<Item> equippedItems = new ArrayList<Item>();
		for (Item item : equipment.values()) {
			if ((item.getEquipmentSlot() >= ItemSlot.STIGMA1.getSlotIdMask())
				&& (item.getEquipmentSlot() <= ItemSlot.ADV_STIGMA5.getSlotIdMask()))
				equippedItems.add(item);
		}

		return equippedItems;
	}

	/**
	 * @return List<Item>
	 */
	public List<Item> getEquippedItemsRegularStigma() {
		List<Item> equippedItems = new ArrayList<Item>();
		for (Item item : equipment.values()) {
			if ((item.getEquipmentSlot() | ItemSlot.REGULAR_STIGMAS.getSlotIdMask()) == ItemSlot.REGULAR_STIGMAS
				.getSlotIdMask())
				equippedItems.add(item);
		}

		return equippedItems;
	}

	/**
	 * @return List<Item>
	 */
	public List<Item> getEquippedItemsAdvencedStigma() {
		List<Item> equippedItems = new ArrayList<Item>();
		for (Item item : equipment.values()) {
			if ((item.getEquipmentSlot() | ItemSlot.ADVANCED_STIGMAS.getSlotIdMask()) == ItemSlot.ADVANCED_STIGMAS
				.getSlotIdMask())
				equippedItems.add(item);
		}

		return equippedItems;
	}

	/**
	 * @return Number of parts equipped belonging to requested itemset
	 */
	public int itemSetPartsEquipped(int itemSetTemplateId) {
		int number = 0;

		for (Item item : equipment.values()) {
			if (item.getEquipmentSlot() == ItemSlot.MAIN_OFF_HAND.getSlotIdMask()
				|| item.getEquipmentSlot() == ItemSlot.SUB_OFF_HAND.getSlotIdMask()) {
				continue;
			}
			ItemSetTemplate setTemplate = item.getItemTemplate().getItemSet();
			if (setTemplate != null && setTemplate.getId() == itemSetTemplateId) {
				++number;
			}
		}

		return number;
	}

	/**
	 * Should be called only when loading from DB for items isEquipped=1
	 * 
	 * @param item
	 */
	public void onLoadHandler(Item item) {
		if (equipment.containsKey(item.getEquipmentSlot())) {
			log.warn("Duplicate equipped item in slot : " + item.getEquipmentSlot() + " " + owner.getObjectId());
			return;
		}
		equipment.put(item.getEquipmentSlot(), item);
	}

	/**
	 * Should be called only when equipment object totaly constructed on player loading Applies every equipped item stats
	 * modificators
	 */
	public void onLoadApplyEquipmentStats() {
		for (Item item : equipment.values()) {
			if (item.getEquipmentSlot() != ItemSlot.MAIN_OFF_HAND.getSlotIdMask()
				&& item.getEquipmentSlot() != ItemSlot.SUB_OFF_HAND.getSlotIdMask()) {
				ItemEquipmentListener.onItemEquipment(item, owner);
				owner.getLifeStats().synchronizeWithMaxStats();
			}
		}
	}

	/**
	 * @return true or false
	 */
	public boolean isShieldEquipped() {
		Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		return subHandItem != null && subHandItem.getItemTemplate().getArmorType() == ArmorType.SHIELD;
	}

	public Item getEquippedShield() {
		Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		return (subHandItem != null && subHandItem.getItemTemplate().getArmorType() == ArmorType.SHIELD) ? subHandItem
			: null;
	}

	/**
	 * @return true if player is equipping the requested ArmorType
	 */
	public boolean isArmorTypeEquipped(ArmorType type) {
		for (Item item : equipment.values()) {
			if ((item != null) && (item.getItemTemplate().getArmorType() == type) && (item.isEquipped())
				&& (item.getEquipmentSlot() != ItemSlot.SUB_OFF_HAND.getSlotIdMask())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return <tt>WeaponType</tt> of current weapon in main hand or null
	 */
	public WeaponType getMainHandWeaponType() {
		Item mainHandItem = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		if (mainHandItem == null)
			return null;

		return mainHandItem.getItemTemplate().getWeaponType();
	}

	/**
	 * check two-handed weapon equipped
	 * 
	 * @return
	 */
	public boolean hasTwoHandWeapon() {
		WeaponType mainHandWeaponType = getMainHandWeaponType();
		if (mainHandWeaponType == null)
			return false;

		switch (mainHandWeaponType) {
			case BOOK_2H:
			case POLEARM_2H:
			case STAFF_2H:
			case SWORD_2H:
				return true;
		}
		return false;
	}

	/**
	 * @return <tt>WeaponType</tt> of current weapon in off hand or null
	 */
	public WeaponType getOffHandWeaponType() {
		Item offHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		if (offHandItem != null && offHandItem.getItemTemplate().isWeapon())
			return offHandItem.getItemTemplate().getWeaponType();

		return null;
	}

	public boolean isArrowEquipped() {
		Item arrow = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		if (arrow != null && arrow.getItemTemplate().getArmorType() == ArmorType.ARROW)
			return true;

		return false;
	}

	public boolean isPowerShardEquipped() {
		Item leftPowershard = equipment.get(ItemSlot.POWER_SHARD_LEFT.getSlotIdMask());
		if (leftPowershard != null)
			return true;

		Item rightPowershard = equipment.get(ItemSlot.POWER_SHARD_RIGHT.getSlotIdMask());
		if (rightPowershard != null)
			return true;

		return false;
	}

	public Item getMainHandPowerShard() {
		Item mainHandPowerShard = equipment.get(ItemSlot.POWER_SHARD_RIGHT.getSlotIdMask());
		if (mainHandPowerShard != null)
			return mainHandPowerShard;

		return null;
	}

	public Item getOffHandPowerShard() {
		Item offHandPowerShard = equipment.get(ItemSlot.POWER_SHARD_LEFT.getSlotIdMask());
		if (offHandPowerShard != null)
			return offHandPowerShard;

		return null;
	}

	/**
	 * @param powerShardItem
	 * @param count
	 */
	public void usePowerShard(Item powerShardItem, int count) {
		decreaseEquippedItemCount(powerShardItem.getObjectId(), count);

		if (powerShardItem.getItemCount() <= 0) {// Search for next same power shards stack
			List<Item> powerShardStacks = owner.getInventory().getItemsByItemId(
				powerShardItem.getItemTemplate().getTemplateId());
			if (powerShardStacks.size() != 0) {
				equipItem(powerShardStacks.get(0).getObjectId(), powerShardItem.getEquipmentSlot());
			}
			else {
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_MSG_WEAPON_BOOST_MODE_BURN_OUT);
				owner.unsetState(CreatureState.POWERSHARD);
			}
		}
	}

	public void useArrow() {
		Item arrow = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());

		if (arrow == null || !arrow.getItemTemplate().isArmor()
			&& arrow.getItemTemplate().getArmorType() != ArmorType.ARROW)
			return;

		decreaseEquippedItemCount(arrow.getObjectId(), 1);
	}

	/**
	 * increase item count and return left count
	 */
	public long increaseEquippedItemCount(Item item, long count) {
		// Only Arrows and Shards can be increased
		if (item.getItemTemplate().getArmorType() != ArmorType.SHARD
			&& item.getItemTemplate().getArmorType() != ArmorType.ARROW)
			return count;

		long leftCount = item.increaseItemCount(count);
		ItemPacketService.updateItemAfterInfoChange(owner, item);// TODO validate mask here
		setPersistentState(PersistentState.UPDATE_REQUIRED);
		return leftCount;
	}

	private void decreaseEquippedItemCount(int itemObjId, int count) {
		Item equippedItem = getEquippedItemByObjId(itemObjId);

		// Only Arrows and Shards can be decreased
		if (equippedItem.getItemTemplate().getArmorType() != ArmorType.SHARD
			&& equippedItem.getItemTemplate().getArmorType() != ArmorType.ARROW)
			return;

		if (equippedItem.getItemCount() >= count)
			equippedItem.decreaseItemCount(count);
		else
			equippedItem.decreaseItemCount(equippedItem.getItemCount());

		if (equippedItem.getItemCount() == 0) {
			equipment.remove(equippedItem.getEquipmentSlot());
			PacketSendUtility.sendPacket(owner, new SM_DELETE_ITEM(equippedItem.getObjectId()));
			DAOManager.getDAO(InventoryDAO.class).store(equippedItem, owner);
		}

		ItemPacketService.updateItemAfterInfoChange(owner, equippedItem);// TODO validate mask here
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * Switch OFF and MAIN hands
	 */
	public void switchHands() {
		Item mainHandItem = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		Item subHandItem = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		Item mainOffHandItem = equipment.get(ItemSlot.MAIN_OFF_HAND.getSlotIdMask());
		Item subOffHandItem = equipment.get(ItemSlot.SUB_OFF_HAND.getSlotIdMask());

		List<Item> equippedWeapon = new ArrayList<Item>();

		if (mainHandItem != null)
			equippedWeapon.add(mainHandItem);
		if (subHandItem != null)
			equippedWeapon.add(subHandItem);
		if (mainOffHandItem != null)
			equippedWeapon.add(mainOffHandItem);
		if (subOffHandItem != null)
			equippedWeapon.add(subOffHandItem);

		for (Item item : equippedWeapon) {
			equipment.remove(item.getEquipmentSlot());
			item.setEquipped(false);
			PacketSendUtility.sendPacket(owner, new SM_INVENTORY_UPDATE_ITEM(owner, item, ItemUpdateType.EQUIP_UNEQUIP));
			if (owner.getGameStats() != null) {
				if (item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask()
					|| item.getEquipmentSlot() == ItemSlot.SUB_HAND.getSlotIdMask()) {
					notifyItemUnequip(item);
				}
			}

		}

		for (Item item : equippedWeapon) {
			if (item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.MAIN_OFF_HAND.getSlotIdMask());

			else if (item.getEquipmentSlot() == ItemSlot.SUB_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.SUB_OFF_HAND.getSlotIdMask());

			else if (item.getEquipmentSlot() == ItemSlot.MAIN_OFF_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.MAIN_HAND.getSlotIdMask());

			else if (item.getEquipmentSlot() == ItemSlot.SUB_OFF_HAND.getSlotIdMask())
				item.setEquipmentSlot(ItemSlot.SUB_HAND.getSlotIdMask());
		}

		for (Item item : equippedWeapon) {
			equipment.put(item.getEquipmentSlot(), item);
			item.setEquipped(true);
			ItemPacketService.updateItemAfterEquip(owner, item);
		}

		if (owner.getGameStats() != null) {
			for (Item item : equippedWeapon) {
				if (item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask()
					|| item.getEquipmentSlot() == ItemSlot.SUB_HAND.getSlotIdMask()) {
					notifyItemEquipped(item);
				}
			}
		}

		owner.getLifeStats().updateCurrentStats();
		owner.getGameStats().updateStatsAndSpeedVisually();
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}

	/**
	 * @param weaponType
	 */
	public boolean isWeaponEquipped(WeaponType weaponType) {
		if (equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask()) != null
			&& equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask()).getItemTemplate().getWeaponType() == weaponType) {
			return true;
		}
		if (equipment.get(ItemSlot.SUB_HAND.getSlotIdMask()) != null
			&& equipment.get(ItemSlot.SUB_HAND.getSlotIdMask()).getItemTemplate().getWeaponType() == weaponType) {
			return true;
		}
		return false;
	}

	/**
	 * @return true if player wear dual weapon
	 */
	public boolean isDualWeaponEquipped() {
		Item mainWeapon = equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
		Item subWeapon = equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
		if ((mainWeapon == null) || (subWeapon == null))
			return false;

		if ((mainWeapon.getItemTemplate() == null) || (subWeapon.getItemTemplate() == null))
			return false;

		if ((mainWeapon.getItemTemplate().getWeaponType() == null) || (subWeapon.getItemTemplate().getWeaponType() == null))
			return false;

		if ((mainWeapon.getItemTemplate().getWeaponType().getRequiredSlots() == 1)
			&& (subWeapon.getItemTemplate().getWeaponType().getRequiredSlots() == 1)) {
			return true;
		}

		return false;
	}

	/**
	 * @param armorType
	 */
	public boolean isArmorEquipped(ArmorType armorType) {
		for (int slot : ARMOR_SLOTS) {
			if (equipment.get(slot) != null && equipment.get(slot).getItemTemplate().getArmorType() != armorType)
				return false;
		}
		return true;
	}

	public boolean isSlotEquipped(int slot) {
		return !(equipment.get(slot) == null);
	}

	public Item getMainHandWeapon() {
		return equipment.get(ItemSlot.MAIN_HAND.getSlotIdMask());
	}

	public Item getOffHandWeapon() {
		return equipment.get(ItemSlot.SUB_HAND.getSlotIdMask());
	}

	/**
	 * @return the persistentState
	 */
	public PersistentState getPersistentState() {
		return persistentState;
	}

	/**
	 * @param persistentState
	 *          the persistentState to set
	 */
	public void setPersistentState(PersistentState persistentState) {
		this.persistentState = persistentState;
	}

	/**
	 * @param player
	 */
	public void setOwner(Player player) {
		this.owner = player;
	}

	/**
	 * @param player
	 * @param item
	 * @return
	 */
	private boolean soulBindItem(final Player player, final Item item, final int slot) {
		RequestResponseHandler responseHandler = new RequestResponseHandler(player) {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				player.getController().cancelUseItem();
				
				if (player.getInventory().getItemByObjId(item.getObjectId()) == null)
					return;
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(),
					item.getItemId(), 5000, 4), true);

				if (player.isInState(CreatureState.CHAIR) || player.isInState(CreatureState.FLYING)
					|| player.isInState(CreatureState.RESTING) || player.isInState(CreatureState.WEAPON_EQUIPPED)
					|| player.isInState(CreatureState.GLIDING)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_INVALID_STANCE(player.getState()));
					return;
				}
				player.getController().cancelTask(TaskId.ITEM_USE);
				final ActionObserver moveObserver = new ActionObserver(ObserverType.MOVE) {
					@Override
					public void moved() {
						player.getController().cancelTask(TaskId.ITEM_USE);
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_ITEM_CANCELED(item.getNameID()));
						PacketSendUtility.broadcastPacket(player,
							new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 8), true);
					}
				};
				player.getObserveController().attach(moveObserver);

				// item usage animation
				player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						player.getObserveController().removeObserver(moveObserver);

						PacketSendUtility.broadcastPacket(player,
							new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemId(), 0, 6), true);
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_ITEM_SUCCEED(item.getNameID()));

						item.setSoulBound(true);
						ItemPacketService.updateItemAfterInfoChange(owner, item);

						equip(slot, item);
						PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(),
							getEquippedItemsWithoutStigma()), true);
					}
				}, 5000));
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SOUL_BOUND_ITEM_CANCELED(item.getNameID()));
			}
		};

		boolean requested = player.getResponseRequester().putRequest(
			SM_QUESTION_WINDOW.STR_SOUL_BOUND_ITEM_DO_YOU_WANT_SOUL_BOUND, responseHandler);
		if (requested) {
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(
				SM_QUESTION_WINDOW.STR_SOUL_BOUND_ITEM_DO_YOU_WANT_SOUL_BOUND, 0, new DescriptionId(item.getNameID())));
		}

		return false;
	}

	public List<Item> getEquippedForApparence() {
		FastList<Item> equippedItems = FastList.newInstance();
        Item twoHanded = null;
        for (Item item : equipment.values()) {
            long slot = item.getEquipmentSlot();
            if (!ItemSlot.isStigma(slot)) {
                if (slot <= ItemSlot.WINGS.getSlotIdMask()) {
                    if (item.getItemTemplate().isTwoHandWeapon()) {
                        if (twoHanded != null) {
                            continue;
                        }
                        twoHanded = item;
                    }
                    equippedItems.add(item);
                }
            }
        }
        return equippedItems;
	}
}
