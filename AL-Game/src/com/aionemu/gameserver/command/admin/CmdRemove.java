package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


public class CmdRemove extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 3) {
			showHelp(admin);
			return;
		}

		int itemId = 0;
		long itemCount = 1;
		Player target = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Player isn't online.");
			return;
		}

		try {
			itemId = ParseInteger(params[1]);
			if (params.length == 3) {
				itemCount = ParseLong(params[2]);
			}
		}
		catch (NumberFormatException e) {
			if(params[1].contains("all"))
				clearInventory(target);
			else
				PacketSendUtility.sendMessage(admin, "Parameters need to be an integer.");
			return;
		}

		Storage bag = target.getInventory();

		long itemsInBag = bag.getItemCountByItemId(itemId);
		if (itemsInBag == 0) {
			PacketSendUtility.sendMessage(admin, "Items with that id are not found in the player's bag.");
			return;
		}

		Item item = bag.getFirstItemByItemId(itemId);
		bag.decreaseByObjectId(item.getObjectId(), itemCount);

		PacketSendUtility.sendMessage(admin, "Item(s) removed succesfully");
		PacketSendUtility.sendMessage(target, "Admin removed an item from your bag");
	}
	
	
	
	private final static void clearInventory(Player target){
		for(Item item : target.getInventory().getItems())
			target.getInventory().delete(item);
	}
}