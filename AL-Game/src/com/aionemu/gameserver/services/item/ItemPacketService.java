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
package com.aionemu.gameserver.services.item;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.Collections;

/**
 * TODO: <br>
 * 0x01 0000 0001 increase count by merge<br>
 * 0x06 0000 0110 decrease count after split, equip<br>
 * 0x16 0001 0110 decrease count by use item<br>
 * 0x19 0001 1001 increase count by looting<br>
 * 0x1A 0001 1010 increase kinah by loot<br>
 * 0x1D 0001 1101 decrease kinah<br>
 * 0x32 0011 0010 increase kinah by quest<br>
 * 
 * @author ATracer
 */
public class ItemPacketService {

	public static enum ItemUpdateType {
		EQUIP_UNEQUIP(-1, false),//internal usage only
		INC_MERGE(0x01, true),
		INC_MERGE_KINAH(0x05, true),
		DEC_SPLIT(0x06, true),
		DEC_USE(0x16, true),
		INC_LOOT(0x19, true),
		INC_GATHER(0x19, true),
		INC_KINAH_LOOT(0x1A, true),
		DEC_KINAH(0x1D, true),
		INC_KINAH_QUEST(0x32, true),
		DEFAULT(0x16, true);

		private final int mask;
		private final boolean	sendable;

		private ItemUpdateType(int mask, boolean sendable) {
			this.mask = mask;
			this.sendable = sendable;
		}

		public int getMask() {
			return mask;
		}

		public boolean isSendable() {
			return sendable;
		}
	}

	public static enum ItemAddType {
		WITH_SLOT(0x07),
		PUT(0x13),
		BUY(0x1C),
		DEFAULT(0x19), // ?
		QUEST(0x35),
		QUESTIONNAIRE(0x40);

		private final int mask;

		private ItemAddType(int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}
	}

	public static enum ItemDeleteType {
		UNKNOWN(0),
		SPLIT(0x04),
		MOVE(0x14),
		DISCARD(0x15),
		USE(0x17);

		private final int mask;

		private ItemDeleteType(int mask) {
			this.mask = mask;
		}

		public int getMask() {
			return mask;
		}

		public static ItemDeleteType fromUpdateType(ItemUpdateType updateType) {
			switch (updateType) {
				case DEC_SPLIT:
					return SPLIT;
				default:
					return UNKNOWN;
			}
		}
	}

	public static void updateItemAfterInfoChange(Player player, Item item) {
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
	}

	public static void updateItemAfterEquip(Player player, Item item) {
		PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, ItemUpdateType.EQUIP_UNEQUIP));
	}

	public static void sendItemPacket(Player player, StorageType storageType, Item item, ItemUpdateType updateType) {
		if (item.getItemCount() <= 0 && !item.getItemTemplate().isKinah()) {
			sendItemDeletePacket(player, storageType, item, ItemDeleteType.fromUpdateType(updateType));
		}
		else {
			sendItemUpdatePacket(player, storageType, item, updateType);
		}
	}

	/**
	 * Item will be deleted from UI slot
	 */
	public static void sendItemDeletePacket(Player player, StorageType storageType, Item item,
		ItemDeleteType deleteType) {
		switch (storageType) {
			case CUBE:
				PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(item.getObjectId(), deleteType));
				break;
			default:
				PacketSendUtility.sendPacket(player, new SM_DELETE_WAREHOUSE_ITEM(storageType.getId(), item.getObjectId(),
					deleteType));
		}
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(storageType, player));
	}

	/**
	 * Item will be updated in UI slot (stacked items)
	 */
	public static void sendItemUpdatePacket(Player player, StorageType storageType, Item item,
		ItemUpdateType updateType) {
		switch (storageType) {
			case CUBE:
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item, updateType));
				break;
			case LEGION_WAREHOUSE:
				if (item.getItemTemplate().isKinah()) {
					PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion()));
					break;
				}
			default:
				PacketSendUtility.sendPacket(player,
					new SM_WAREHOUSE_UPDATE_ITEM(player, item, storageType.getId(), updateType));
		}
	}

	/**
	 * New item will be displayed in storage
	 */
	public static void sendStorageUpdatePacket(Player player, StorageType storageType, Item item) {
		switch (storageType) {
			case CUBE:
				PacketSendUtility.sendPacket(player, new SM_INVENTORY_ADD_ITEM(Collections.singletonList(item), player));
				break;
			case LEGION_WAREHOUSE:
				if (item.getItemTemplate().isKinah()) {
					PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x04, player.getLegion()));
					break;
				}
			default:
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_ADD_ITEM(item, storageType.getId(), player));
		}
		PacketSendUtility.sendPacket(player, SM_CUBE_UPDATE.cubeSize(storageType, player));
	}

}
