package com.aionemu.gameserver.command.admin;

import java.util.Iterator;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/*Syntax: //notice <message> */


public class CmdNotice extends BaseCommand {




	public void execute(Player player, String... params) {

		String message = "";

		try {
			for (int i = 0; i < params.length; i++) {
				message += " " + params[i];
			}
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(player, "Parameters should be text or number !");
			return;
		}
		Iterator<Player> iter = World.getInstance().getPlayersIterator();

		while (iter.hasNext()) {
			PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), "Information: " + message);
		}
	}


	public void onFail(Player admin, String message) {
		showHelp(admin);
		return;
	}
}
