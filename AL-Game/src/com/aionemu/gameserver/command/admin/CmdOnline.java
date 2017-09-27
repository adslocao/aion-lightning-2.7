package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;


/*Syntax: //online */


public class CmdOnline extends BaseCommand {
	
	public void execute(Player admin, String... params) {

		int playerCount = World.getInstance().countAllPlayers();

		if (playerCount == 1) {
			PacketSendUtility.sendMessage(admin, "There is " + (playerCount) + " player online !");
		}
		else {
			PacketSendUtility.sendMessage(admin, "There are " + (playerCount) + " players online !");
		}
	}

	public void onFail(Player admin, String message) {
		showHelp(admin);
		return;
	}
}
