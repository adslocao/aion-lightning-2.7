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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.ItemStoneListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.GodstoneInfo;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public class ItemSocketService {

	private static final Logger log = LoggerFactory.getLogger(ItemSocketService.class);

	public static ManaStone addManaStone(Item item, int itemId) {
		if (item == null)
			return null;

		Set<ManaStone> manaStones = item.getItemStones();
		// temp fix for manastone spam till templates are updated
		if (manaStones.size() > 6)
			return null;

		int nextSlot = 0;
		boolean slotFound = false;
		for (ManaStone ms : manaStones) {
			if (nextSlot != ms.getSlot()) {
				slotFound = true;
				break;
			}
			nextSlot++;
		}

		if (!slotFound)
			nextSlot = manaStones.size();

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		manaStones.add(stone);

		return stone;
	}
	
	public static ManaStone addManaStone(Item item, int itemId, int slotId) {
		if (item == null)
			return null;

		Set<ManaStone> manaStones = item.getItemStones();
		// temp fix for manastone spam till templates are updated
		if (manaStones.size() > 6)
			return null;

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
		manaStones.add(stone);
		return stone;
	}

	public static void copyFusionStones(Item source, Item target) {
		if (source.hasManaStones()) {
			for (ManaStone manaStone : source.getItemStones()) {
				target.getFusionStones().add(
					new ManaStone(target.getObjectId(), manaStone.getItemId(), manaStone.getSlot(), PersistentState.NEW));
			}
		}
	}

	public static ManaStone addFusionStone(Item item, int itemId) {
		if (item == null)
			return null;

		Set<ManaStone> fusionStones = item.getFusionStones();
		if (fusionStones.size() > item.getSockets(true))
			return null;

		int nextSlot = 0;
		boolean slotFound = false;
		for (ManaStone ms : fusionStones) {
			if (nextSlot != ms.getSlot()) {
				slotFound = true;
				break;
			}
			nextSlot++;
		}

		if (!slotFound)
			nextSlot = fusionStones.size();

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, nextSlot, PersistentState.NEW);
		fusionStones.add(stone);
		return stone;
	}
	
	public static ManaStone addFusionStone(Item item, int itemId, int slotId) {
		if (item == null)
			return null;

		Set<ManaStone> fusionStones = item.getFusionStones();
		if (fusionStones.size() > item.getSockets(true))
			return null;

		ManaStone stone = new ManaStone(item.getObjectId(), itemId, slotId, PersistentState.NEW);
		fusionStones.add(stone);
		return stone;
	}

	public static void removeManastone(Player player, int itemObjId, int slotNum) {
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasManaStones()) {
			log.warn("Item stone list is empty");
			return;
		}

		Set<ManaStone> itemStones = item.getItemStones();

		if (itemStones.size() <= slotNum)
			return;

		int counter = 0;
		for (ManaStone ms : itemStones) {
			if (counter == slotNum) {
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(Collections.singleton(ms));
				itemStones.remove(ms);
				break;
			}
			counter++;
		}
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeFusionstone(Player player, int itemObjId, int slotNum) {
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjId);
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasFusionStones()) {
			log.warn("Item stone list is empty");
			return;
		}

		Set<ManaStone> itemStones = item.getFusionStones();

		if (itemStones.size() <= slotNum)
			return;

		int counter = 0;
		for (ManaStone ms : itemStones) {
			if (counter == slotNum) {
				ms.setPersistentState(PersistentState.DELETED);
				DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStone(Collections.singleton(ms));
				itemStones.remove(ms);
				break;
			}
			counter++;
		}
		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeAllManastone(Player player, Item item) {
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasManaStones()) {
			return;
		}

		Set<ManaStone> itemStones = item.getItemStones();
		for (ManaStone ms : itemStones) {
			ms.setPersistentState(PersistentState.DELETED);
		}
		DAOManager.getDAO(ItemStoneListDAO.class).storeManaStones(itemStones);
		itemStones.clear();

		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void removeAllFusionStone(Player player, Item item) {
		if (item == null) {
			log.warn("Item not found during manastone remove");
			return;
		}

		if (!item.hasFusionStones()) {
			return;
		}

		Set<ManaStone> fusionStones = item.getFusionStones();
		for (ManaStone ms : fusionStones) {
			ms.setPersistentState(PersistentState.DELETED);
		}
		DAOManager.getDAO(ItemStoneListDAO.class).storeFusionStone(fusionStones);
		fusionStones.clear();

		ItemPacketService.updateItemAfterInfoChange(player, item);
	}

	public static void socketGodstone(Player player, int weaponId, int stoneId) {
		long socketPrice = PricesService.getPriceForService(100000, player.getRace());
		if (player.getInventory().getKinah() < socketPrice)
			return;

		Item weaponItem = player.getInventory().getItemByObjId(weaponId);
		if (weaponItem == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_CANNOT_GIVE_PROC_TO_EQUIPPED_ITEM);
			return;
		}
		int weaponItemId = weaponItem.getItemTemplate().getTemplateId();
		int wID = Math.round(weaponItemId / 1000000);
		if (wID != 100 && wID != 101) {
			return;
		}

		Item godstone = player.getInventory().getItemByObjId(stoneId);

		int godStoneItemId = godstone.getItemTemplate().getTemplateId();
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(godStoneItemId);
		GodstoneInfo godstoneInfo = itemTemplate.getGodstoneInfo();

		if (godstoneInfo == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_NO_PROC_GIVE_ITEM);
			log.warn("Godstone info missing for itemid " + godStoneItemId);
			return;
		}
		
		int godsstoneItemIdmask = Math.round(godStoneItemId / 1000000);
		if (godsstoneItemIdmask != 168){
			return;
		}

		if (!player.getInventory().decreaseByObjectId(stoneId, 1))
			return;

		weaponItem.addGodStone(godStoneItemId);
		PacketSendUtility.sendPacket(player,
			SM_SYSTEM_MESSAGE.STR_GIVE_ITEM_PROC_ENCHANTED_TARGET_ITEM(new DescriptionId(weaponItem.getNameID())));

		player.getInventory().decreaseKinah(socketPrice);
		ItemPacketService.updateItemAfterInfoChange(player, weaponItem);
	}
}
