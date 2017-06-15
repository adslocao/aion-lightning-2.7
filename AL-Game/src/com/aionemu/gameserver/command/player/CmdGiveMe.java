package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.TranslationService;

public class CmdGiveMe extends BaseCommand {
		
	public void execute(Player player, String... params) {
		
		if (params.length < 1 || params.length > 2 ) {
			showHelp(player);
			return;
		}
		int itemId = 0;
		long itemCount = 1;
		
		itemId = ParseInteger(params[0]);
		if (itemId == 0) {
			itemId = GetItemIDFromLinkOrID(params[0]);
			if (itemId == 0) {
				// This ID is not a valid item ID. Please try again.
				String message = TranslationService.GIVE_ME_ERROR_ID.toString(player);
				sendCommandMessage(player, message);
				return;
			}
		}
		
		if (DataManager.ITEM_DATA.getItemTemplate(itemId) == null) {
			// This ID is not a valid item ID. Please try again.
			String message = TranslationService.GIVE_ME_ERROR_ID.toString(player);
			sendCommandMessage(player, message);
			return;
		}
		
		if(params.length == 2) {
			itemCount = ParseInteger(params[1]);
		}
		
		if (itemCount <= 0) {
			// Required quantity is not valid. Please try again.
			String message = TranslationService.GIVE_ME_ERROR_QTY.toString(player);
			sendCommandMessage(player, message);
			return;
		}
		
		ItemCategory category = DataManager.ITEM_DATA.getItemTemplate(itemId).getCategory();
		if (category == ItemCategory.FLUX || category == ItemCategory.GATHERABLE || category == ItemCategory.RAWHIDE ||
				category == ItemCategory.BALIC_MATERIAL || category == ItemCategory.BALIC_EMOTION) {
			
			long count = ItemService.addItem(player, itemId, itemCount);

			if (count == 0) {
				// You received %s [item:%s]
				String message = TranslationService.GIVE_ME_SUCCESS.toString(
						player, String.valueOf(itemCount), String.valueOf(itemId));
				sendCommandMessage(player, message);
			}
			else {
				// An error has occured. Please contact an administrator.
				String message = TranslationService.COMMAND_ERROR_GENERAL.toString(player);
				sendCommandMessage(player, message);
			}
				
		}
		else {
			// This item cannot be given with this command. Check if you can buy it or craft it
			String message = TranslationService.GIVE_ME_NOT_ALLOWED.toString(player);
			sendCommandMessage(player, message);
		}
	}
}