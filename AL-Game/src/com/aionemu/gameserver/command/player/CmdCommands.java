package com.aionemu.gameserver.command.player;

//import java.util.Map.Entry;

import com.aionemu.gameserver.command.BaseCommand;
//import com.aionemu.gameserver.command.CommandService;
import com.aionemu.gameserver.model.gameobjects.player.Player;
//import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdCommands extends BaseCommand {
	public void execute(Player player, String... params) {
		// HTMLService.showHTML(player, CommandService.getInstance().getCommandList(player));
		//PacketSendUtility.sendMessage(player, CommandService.getInstance().getCommandListMsg(player));
		PacketSendUtility.sendMessage(player, "This command is currently disabled.");
	}
}