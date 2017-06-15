/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aionemu.gameserver.model.items.storage;

/**
 * @author kosyachok, IlBuono
 */
public enum StorageType {
	CUBE(0, 27, 9),
	REGULAR_WAREHOUSE(1, 96, 8),
	ACCOUNT_WAREHOUSE(2, 16, 8),
	LEGION_WAREHOUSE(3, 56, 8),
	PET_BAG_6(32, 6, 6),
	PET_BAG_12(33, 12, 6),
	PET_BAG_18(34, 18, 6),
	PET_BAG_24(35, 24, 6),
	BROKER(126),
	MAILBOX(127);

	private int id;
	private int limit;
	private int length;

	private StorageType(int id) {
		this.id = id;
	}

	private StorageType(int id, int limit, int length) {
		this.id = id;
		this.limit = limit;
		this.length = length;
	}

	public int getId() {
		return id;
	}

	public int getLimit() {
		return limit;
	}

	public int getLength() {
		return length;
	}

	public static StorageType getStorageTypeById(int id) {
		for (StorageType st : values()) {
			if (st.id == id)
				return st;
		}
		return null;
	}
}
