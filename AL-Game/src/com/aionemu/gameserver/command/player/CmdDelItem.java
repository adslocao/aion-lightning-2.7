package com.aionemu.gameserver.command.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 
 * @author Ferosia
 *
 */

public class CmdDelItem extends BaseCommand {
	
	public static Logger log = LoggerFactory.getLogger(CmdDelItem.class);
	
	public void execute(Player player, String... params) {
		if (params.length != 1) {
			showHelp(player);
			return;
		}
		String [] itemLink = params[0].split(";");
		String [] itemIdArray = itemLink[0].split(":");
		int itemId = ParseInteger(itemIdArray[1]);
		
		if (player == null) {
			PacketSendUtility.sendMessage(player, "Player isn't online.");
			return;
		}

		Storage bag = player.getInventory();

		long itemsInBag = bag.getItemCountByItemId(itemId);
		if (itemsInBag == 0) {
			PacketSendUtility.sendMessage(player, "[DelItem] L'objet " + params[0] + " n'a pas \u00E9t\u00E9 trouv\u00E9 dans votre inventaire.");
			return;
		}

		Item item = bag.getFirstItemByItemId(itemId);
		int itemCount = (int) item.getItemCount();
		bag.decreaseByObjectId(item.getObjectId(), itemCount);

		PacketSendUtility.sendMessage(player, "[DelItem] L'objet " + params[0] + " a bien \u00E9t\u00E9 supprim\u00E9 de votre inventaire.");
	}	
}