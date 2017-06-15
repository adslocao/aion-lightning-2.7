package com.aionemu.gameserver.model.items.storage;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.Item;

/**
 * @author KID
 */
public class ItemStorage {

	public static final int FIRST_AVAILABLE_SLOT = 65535;

	private FastMap<Integer, Item> items;
	private int limit;

	public ItemStorage(int limit) {
		this.limit = limit;
		this.items = FastMap.newInstance();
	}

	public FastList<Item> getItems() {
		FastList<Item> temp = FastList.newInstance();
		temp.addAll(items.values());
		return temp;
	}

	public int getLimit() {
		return this.limit;
	}

	public boolean setLimit(int limit) {
		if (this.items.size() > limit) {
			return false;
		}

		this.limit = limit;
		return true;
	}

	public Item getFirstItemById(int itemId) {
		for (Item item : items.values()) {
			if (item.getItemTemplate().getTemplateId() == itemId) {
				return item;
			}
		}
		return null;
	}

	public FastList<Item> getItemsById(int itemId) {
		FastList<Item> temp = FastList.newInstance();
		for (Item item : items.values()) {
			if (item.getItemTemplate().getTemplateId() == itemId) {
				temp.add(item);
			}
		}
		return temp;
	}
	
	public int countItemsById(int itemId) {
		int count = 0;
		for (Item item : items.values()) {
			if (item.getItemTemplate().getTemplateId() == itemId) {
				count++;
			}
		}
		return count;
	}

	public Item getItemByObjId(int itemObjId) {
		return this.items.get(itemObjId);
	}

	public int getSlotIdByItemId(int itemId) {
		for (Item item : this.items.values()) {
			if (item.getItemTemplate().getTemplateId() == itemId) {
				return item.getEquipmentSlot();
			}
		}
		return -1;
	}

	public Item getItemBySlotId(short slotId) {
		for (Item item : this.items.values()) {
			if (item.getEquipmentSlot() == slotId) {
				return item;
			}
		}
		return null;
	}

	public int getSlotIdByObjId(int objId) {
		Item item = this.getItemByObjId(objId);
		if (item != null)
			return item.getEquipmentSlot();
		else
			return -1;
	}

	public int getNextAvailableSlot() {
		return FIRST_AVAILABLE_SLOT;
	}

	public boolean putItem(Item item) {
		if (this.items.containsKey(item.getObjectId()))
			return false;

		this.items.put(item.getObjectId(), item);
		return true;
	}

	public Item removeItem(int objId) {
		return this.items.remove(objId);
	}

	public boolean isFull() {
		return this.items.size() >= limit;
	}

	public int getFreeSlots() {
		return limit - this.items.size();
	}

	public int size() {
		return this.items.size();
	}

}
