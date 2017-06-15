package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.itemset.ItemPart;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


/*syntax //addset <player> <itemset ID>
 syntax //addset <itemset ID>") */


/**
 * @author Antivirus
 */
public class CmdAddSet extends BaseCommand {

		
	public void execute(Player admin, String... params) {
		if (params.length == 0 || params.length > 2) {
			showHelp(admin);
			return;
		}

		int itemSetId = ParseInteger(params[0]);
		Player receiver = null;
		
		if (itemSetId == 0) {
			if (params.length != 2) {
				showHelp(admin);
				return;
			}
			
			receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (receiver == null) {
				PacketSendUtility.sendMessage(admin, "joueur introuvable.");
				return;
			}
			
			itemSetId = ParseInteger(params[1]);
		}
		else
			receiver = AutoTarget(admin, false);

		ItemSetTemplate itemSet = DataManager.ITEM_SET_DATA.getItemSetTemplate(itemSetId);
		if (itemSet == null) {
			PacketSendUtility.sendMessage(admin, "ItemSet does not exist with id " + itemSetId);
			return;
		}

		if (receiver.getInventory().getFreeSlots() < itemSet.getItempart().size()) {
			PacketSendUtility.sendMessage(admin, "Inventory needs at least " + itemSet.getItempart().size() + " free slots.");
			return;
		}

		for (ItemPart setPart : itemSet.getItempart()) {
			long count = ItemService.addItem(receiver, setPart.getItemid(), 1);

			if (count != 0) {
				PacketSendUtility.sendMessage(admin, "Item " + setPart.getItemid() + " couldn't be added");
				return;
			}
		}
		PacketSendUtility.sendMessage(admin, "ensemble item ajoute");
		if (admin != receiver)
			PacketSendUtility.sendMessage(receiver, "admin gives you an item set");
	}
}
