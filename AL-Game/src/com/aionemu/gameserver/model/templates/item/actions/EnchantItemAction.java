/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *     Aion-unique is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Aion-unique is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.item.actions;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.EnchantService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author Nemiroff, Wakizashi, vlog
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantItemAction")
public class EnchantItemAction extends AbstractItemAction {

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (targetItem == null) { // no item selected.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		if (parentItem == null) {
			return false;
		}
		int msID = parentItem.getItemTemplate().getTemplateId() / 1000000;
		int tID = targetItem.getItemTemplate().getTemplateId() / 1000000;
		if ((msID != 167 && msID != 166) || tID >= 120) {
			return false;
		}
		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		act(player, parentItem, targetItem, null, 1);
	}

	// necessary overloading to not change AbstractItemAction
	public void act(final Player player, final Item parentItem, final Item targetItem, final Item supplementItem,
			final int targetWeapon) {
		// Current enchant level
		final int currentEnchant = targetItem.getEnchantLevel();
		final boolean isSuccess = isSuccess(player, parentItem, targetItem, supplementItem, targetWeapon, currentEnchant);
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacketAndReceive(player,
				new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), CustomConfig.ENCHANT_INCANT_TIME, 0, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				int itemId = parentItem.getItemTemplate().getTemplateId();
				// Enchantment stone
				if (itemId > 166000000 && itemId < 167000000)
					EnchantService.enchantItemAct(player, parentItem, targetItem, supplementItem, currentEnchant, isSuccess);
				// Manastone
				else
					EnchantService.socketManastoneAct(player, parentItem, targetItem, supplementItem, targetWeapon, isSuccess);
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
						parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2, 0));
				if (CustomConfig.ENABLE_ENCHANT_ANNOUNCE) {
					if ((itemId > 166000000 && itemId < 167000000) && currentEnchant == 14 && isSuccess) {
						Iterator<Player> iter = World.getInstance().getPlayersIterator();
						while (iter.hasNext()) {
							Player player2 = iter.next();
							if (player2.getRace() == player.getRace()) {
								PacketSendUtility.sendPacket(player2, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEEDED_15(
										player.getName(), targetItem.getItemTemplate().getNameId()));
							}
						}
					}
				}
			}

		}, CustomConfig.ENCHANT_INCANT_TIME));
	}

	/**
	 * Check, if the item enchant will be successful
	 *
	 * @param player
	 * @param parentItem the enchantment-/manastone to insert
	 * @param targetItem the current item to enchant
	 * @param supplementItem the item to increase the enchant chance (if exists)
	 * @param targetWeapon the fused weapon (if exists)
	 * @param currentEnchant current enchant level
	 * @return true if successful
	 */
	private boolean isSuccess(final Player player, final Item parentItem, final Item targetItem,
			final Item supplementItem, final int targetWeapon, final int currentEnchant) {
		if (parentItem.getItemTemplate() != null) {
			// Id of the stone
			int itemId = parentItem.getItemTemplate().getTemplateId();
			// Enchantment stone
			if (itemId > 166000000 && itemId < 167000000) {
				return EnchantService.enchantItem(player, parentItem, targetItem, supplementItem);
			}
			// Manastone
			return EnchantService.socketManastone(player, parentItem, targetItem, supplementItem, targetWeapon);
		}
		return false;
	}

}
