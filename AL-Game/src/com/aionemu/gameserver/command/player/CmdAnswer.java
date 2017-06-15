package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.Wedding;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.WeddingService;
import com.aionemu.gameserver.utils.PacketSendUtility;

//syntax .answer yes/no.

public class CmdAnswer extends BaseCommand {
	public void execute(Player player, String... params) {
		Wedding wedding = WeddingService.getInstance().getWedding(player);

		if (params.length != 1) {
			showHelp(player);
			return;
		}
		
		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't use this command on prison.");
			return;
		}

		if (wedding == null)
			PacketSendUtility.sendMessage(player, "Wedding not started.");
		
		if (params[0].equalsIgnoreCase("yes")) {
			PacketSendUtility.sendMessage(player, "You accept.");
			WeddingService.getInstance().acceptWedding(player);
		}

		if (params[0].equalsIgnoreCase("no")) {
			PacketSendUtility.sendMessage(player, "You decide.");
			WeddingService.getInstance().cancelWedding(player);
		}

	}
}