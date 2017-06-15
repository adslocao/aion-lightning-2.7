package com.aionemu.gameserver.services.item;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KID
 */
public class ItemService {

	private static final Logger log = LoggerFactory.getLogger("ITEM_LOG");

	private static final ItemAddPredicate DEFAULT_ADD_PREDICATE = new ItemAddPredicate();

	public static void loadItemStones(Collection<Item> itemList) {
		if (itemList != null && itemList.size() > 0) {
			DAOManager.getDAO(ItemStoneListDAO.class).load(itemList);
		}
	}

	public static long addItem(Player player, int itemId, long count) {
		return addItem(player, itemId, count, DEFAULT_ADD_PREDICATE);
	}

	public static long addItem(Player player, int itemId, long count, ItemAddPredicate predicate) {
		return addItem(player, itemId, count, null, predicate);
	}

	/**
	 * Add new item based on all sourceItem values
	 */
	public static long addItem(Player player, Item sourceItem) {
		return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, DEFAULT_ADD_PREDICATE);
	}

	public static long addItem(Player player, Item sourceItem, ItemAddPredicate predicate) {
		return addItem(player, sourceItem.getItemId(), sourceItem.getItemCount(), sourceItem, predicate);
	}

	public static long addItem(Player player, int itemId, long count, Item sourceItem) {
		return addItem(player, itemId, count, sourceItem, DEFAULT_ADD_PREDICATE);
	}

	/**
	 * Add new item based on sourceItem values
	 */
	public static long addItem(Player player, int itemId, long count, Item sourceItem, ItemAddPredicate predicate) {
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (count <= 0 || itemTemplate == null) {
			return 0;
		}
		Preconditions.checkNotNull(itemTemplate, "No item with id " + itemId);
		Preconditions.checkNotNull(predicate, "Predicate is not supplied");

		if (LoggingConfig.LOG_ITEM) {
			log.info("[ITEM] ID/Count" + (LoggingConfig.ENABLE_ADVANCED_LOGGING ? "/Item Name - " + itemTemplate.getTemplateId() + "/" + count + "/" + itemTemplate.getName() : " - " + itemTemplate.getTemplateId() + "/" + count) + 
			" to player " + player.getName());
		}

		Storage inventory = player.getInventory();
		if (itemTemplate.isKinah()) {
			inventory.increaseKinah(count);
			return 0;
		}

		if (itemTemplate.isStackable()) {
			count = addStackableItem(player, itemTemplate, count, predicate);
		}
		else {
			count = addNonStackableItem(player, itemTemplate, count, sourceItem, predicate);
		}

		if (inventory.isFull() && count > 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_INVEN_ERROR);
		}
		return count;
	}

	/**
	 * Add non-stackable item to inventory
	 */
	private static long addNonStackableItem(Player player, ItemTemplate itemTemplate, long count, Item sourceItem,
		Predicate<Item> predicate) {
		Storage inventory = player.getInventory();
		while (!inventory.isFull() && count > 0) {
			Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId());

			if (newItem.getExpireTime() != 0) {
				ExpireTimerTask.getInstance().addTask(newItem, player);
			}
			if (sourceItem != null) {
				copyItemInfo(sourceItem, newItem);
			}
			predicate.apply(newItem);
			inventory.add(newItem);
			count--;
		}
		return count;
	}

	/**
	 * Copy some item values like item stones and enchange level
	 */
	private static void copyItemInfo(Item sourceItem, Item newItem) {
		newItem.setOptionalSocket(sourceItem.getOptionalSocket());
		if (sourceItem.hasManaStones()) {
			for (ManaStone manaStone : sourceItem.getItemStones()) {
				ItemSocketService.addManaStone(newItem, manaStone.getItemId());
			}
		}
		if (sourceItem.getGodStone() != null) {
			newItem.addGodStone(sourceItem.getGodStone().getItemId());
		}
		if (sourceItem.getEnchantLevel() > 0) {
			newItem.setEnchantLevel(sourceItem.getEnchantLevel());
		}
		if (sourceItem.isSoulBound()) {
			newItem.setSoulBound(true);
		}
	}

	/**
	 * Add stackable item to inventory
	 */
	private static long addStackableItem(Player player, ItemTemplate itemTemplate, long count, ItemAddPredicate predicate) {
		Storage inventory = player.getInventory();
		Collection<Item> items = inventory.getItemsByItemId(itemTemplate.getTemplateId());
		for (Item item : items) {
			if (count == 0) {
				break;
			}
			count = inventory.increaseItemCount(item, count, predicate.getUpdateType(item));
		}

		//dirty & hacky check for arrows and shards...
		if(itemTemplate.getArmorType() == ArmorType.SHARD || itemTemplate.getArmorType() == ArmorType.ARROW) {
			Equipment equipement = player.getEquipment();
			items = equipement.getEquippedItemsByItemId(itemTemplate.getTemplateId());
			for (Item item : items) {
				if (count == 0) {
					break;
				}
				count = equipement.increaseEquippedItemCount(item, count);
			}
		}

		while (!inventory.isFull() && count > 0) {
			Item newItem = ItemFactory.newItem(itemTemplate.getTemplateId(), count);
			count -= newItem.getItemCount();
			inventory.add(newItem);
		}
		return count;
	}

	public static boolean addQuestItems(Player player, List<QuestItems> questItems) {
		int needSlot = 0;
		for (QuestItems qi : questItems) {
			if (qi.getItemId() != ItemId.KINAH.value() && qi.getCount() != 0) {
				long stackCount = DataManager.ITEM_DATA.getItemTemplate(qi.getItemId()).getMaxStackCount();
				long count = qi.getCount() / stackCount;
				if (qi.getCount() % stackCount != 0)
					count++;
				needSlot += count;
			}
		}
		if (needSlot > player.getInventory().getFreeSlots()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
			return false;
		}
		for (QuestItems qi : questItems) {
			addItem(player, qi.getItemId(), qi.getCount());
		}
		return true;
	}

	public static void releaseItemId(Item item) {
		IDFactory.getInstance().releaseId(item.getObjectId());
	}

	public static void releaseItemIds(Collection<Item> items) {
		Collection<Integer> idIterator = Collections2.transform(items, AionObject.OBJECT_TO_ID_TRANSFORMER);
		IDFactory.getInstance().releaseIds(idIterator);
	}

	public static class ItemAddPredicate implements Predicate<Item> {

		public ItemUpdateType getUpdateType(Item item) {
			return ItemUpdateType.DEFAULT;
		}

		@Override
		public boolean apply(Item input) {
			return true;
		}
	}

	public static boolean checkRandomTemplate(int randomItemId) {
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(randomItemId);
		return template != null;
	}

}
