package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/*"syntax .enchant < | cancel> */

/**
 * @author Ferosia
 */

public class CmdEnchant extends BaseCommand {

	public void execute(Player player, String... params) {
		int enchant = -1;
		
		if (params.length == 0) {
			enchant = 15;
		}
		else if (params.length == 1) {
			if ("cancel".startsWith(params[0])) {
				enchant = 0;
			}
		}
		
		if (enchant < 0 || params.length > 1) {
			showHelp(player);
			return;
		}
		
		enchant(player, enchant);
	}
	
	
	private void enchant(Player player, int enchant) {
		for (Item targetItem : player.getEquipment().getEquippedItemsWithoutStigma()) {
			if (isUpgradeble(targetItem)) {
				if (targetItem.getEnchantLevel() == enchant)
					continue;
				if (enchant > 15)
					enchant = 15;
				if (enchant < 0)
					enchant = 0;

				targetItem.setEnchantLevel(enchant);
				if (targetItem.isEquipped()) {
					player.getGameStats().updateStatsVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			}
		}
		PacketSendUtility.sendMessage(player, "All equipped items were enchanted to level " + enchant);

	}
	
	/**
	 * Verify if the item is enchantable
	 * 
	 * @param item
	 */
	public static boolean isUpgradeble(Item item) {
		if (item.getItemTemplate().isNoEnchant())
			return false;
		if (item.getItemTemplate().isWeapon())
			return true;
		if (item.getItemTemplate().isArmor()) {
			int at = item.getItemTemplate().getItemSlot();
			if (at == 1 || /* Main Hand */
			at == 2 || /* Sub Hand */
			at == 8 || /* Jacket */
			at == 16 || /* Gloves */
			at == 32 || /* Boots */
			at == 2048 || /* Shoulder */
			at == 4096 || /* Pants */
			at == 131072 || /* Main Off Hand */
			at == 262144) /* Sub Off Hand */
				return true;
		}
		return false;

	}
	
}