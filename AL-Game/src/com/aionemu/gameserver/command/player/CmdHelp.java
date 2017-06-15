package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.command.CommandService;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdHelp extends BaseCommand {
	public void execute(Player player, String... params) {
		if (params.length == 0) {
			showHelp(player);
			return;
		}
		
		PacketSendUtility.sendMessage(player, CommandService.getInstance().getHelpFromCommand(player, params));
	}
}