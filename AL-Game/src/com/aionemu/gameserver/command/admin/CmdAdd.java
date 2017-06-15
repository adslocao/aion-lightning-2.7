package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


public class CmdAdd extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 3) {
			showHelp(admin);
			return;
		}
		
		int itemId = 0;
		long itemCount = 1;
		Player receiver = null;
		
		itemId = ParseInteger(params[0]);
		if (itemId == 0) { //params[0] = player || @link
			
			itemId = GetItemIDFromLinkOrID(params[0]);
			
			if (itemId == 0) { //params[0] = player 
				receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
				if (receiver == null) {
					PacketSendUtility.sendMessage(admin, "Aucun joueur trouve sous le nom " + params[0]);
					return;
				}
				
				if (params.length < 2) {
					showHelp(admin);
					return ;
				}
				
				itemId = ParseInteger(params[1]);
				
				if (params.length == 3)
					itemCount = ParseInteger(params[2]);
			}
			else {
				receiver = AutoTarget(admin);
				if (params.length == 2)
					itemCount = ParseInteger(params[1]);
			}
		}
		else {
			receiver = AutoTarget(admin);
			if (receiver == null) {
				PacketSendUtility.sendMessage(admin, "Aucun joueur selectionne.");
				return;
			}
			if (params.length == 2)
				itemCount = ParseInteger(params[1]);
		}
		
		if (itemCount <= 0) {
			PacketSendUtility.sendMessage(admin, "itemCount incorrecte (<= 0)");
			return ;
		}

		if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
			PacketSendUtility.sendMessage(admin, "ItemId incorrecte : " + itemId);
			return;
		}

		long count = ItemService.addItem(receiver, itemId, itemCount);

		if (count == 0) {
			if (itemCount == 1) {
				if (receiver != admin) {
					PacketSendUtility.sendMessage(admin, "You successfully gave " + "[item:" + itemId + "]" + " to " + receiver.getName() + ".");
					PacketSendUtility.sendMessage(receiver, "You received an item " + "[item:" + itemId + "]"
							+ " from the admin " + admin.getName() + ".");
				}
				else
					PacketSendUtility.sendMessage(admin, "You successfully received " + "[item:" + itemId + "].");
			}
			else {
				if (receiver != admin) {
					PacketSendUtility.sendMessage(admin, "You successfully gave " + itemCount + " x " + " [item:" + itemId + "]" + " to " + receiver.getName() + ".");
					PacketSendUtility.sendMessage(receiver, "You received " + itemCount + " x " + " [item:" + itemId + "]"
							+ " from the admin " + admin.getName() + ".");
				}
				else
					PacketSendUtility.sendMessage(admin, "You successfully received " + itemCount + " x " + " [item:" + itemId + "].");
			}
		}
		else
			PacketSendUtility.sendMessage(admin, "Impossible d'ajouter l'item");
	}
}
