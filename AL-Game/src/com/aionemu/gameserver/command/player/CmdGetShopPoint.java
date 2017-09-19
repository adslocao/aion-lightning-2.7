package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.services.TranslationService;

/**
 * @author Ferosia
 */

public class CmdGetShopPoint extends BaseCommand {
	
	private static int cost = CustomConfig.TOLL_EXCHANGE_PRICE;
	
	public void execute(Player player, String... params) {
		
		// 2 parameters needed
		if (params.length != 2) {
			showHelp(player);
			return;
		}
		
		int kinahCount = ParseInteger(params[0]);
		if(kinahCount == 0) {
			String message = TranslationService.TOLL_ERROR_KINAH.toString(player);
			sendCommandMessage(player, message);
			return;
		}
		
		if (player.getInventory().getKinah() < kinahCount) {
			String message = TranslationService.TOLL_ERROR_NOTENOUGHKINAH.toString(player);
			sendCommandMessage(player, message);
			return;
		}
		
		int tollCount = ParseInteger(params[1]);
		if(tollCount == 0) {
			String message = TranslationService.TOLL_ERROR_TOLL.toString(player);
			sendCommandMessage(player, message);
			return;
		}
		
		if(kinahCount != tollCount * cost) {
			String message = TranslationService.TOLL_ERROR_COST.toString(player);
			sendCommandMessage(player, message);
			showHelp(player);
			return;
		}
		
		player.getInventory().decreaseKinah(kinahCount);
		InGameShopEn.getInstance().addToll(player, tollCount);
		
		String message = TranslationService.TOLL_SUCCESS.toString(player, String.valueOf(kinahCount), String.valueOf(tollCount));
		sendCommandMessage(player, message);
	}
}