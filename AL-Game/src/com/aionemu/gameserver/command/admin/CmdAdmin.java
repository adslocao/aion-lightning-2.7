package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
//import com.aionemu.gameserver.command.CommandService;
import com.aionemu.gameserver.model.gameobjects.player.Player;
//import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdAdmin extends BaseCommand {

	public void execute(Player player, String... params) {
		//HTMLService.showHTML(player, CommandService.getInstance().getCommandList(player));
		//HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("commands.xhtml"));
		PacketSendUtility.sendMessage(player, "This command is currently disabled.");
	}

}