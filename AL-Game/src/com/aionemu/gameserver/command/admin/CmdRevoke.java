package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


public class CmdRevoke extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length != 3) {
			showHelp(admin);
			return;
		}

		int type = 0;
		if (params[2].equalsIgnoreCase("acceslevel"))
			type = 1;
		else if (params[2].equalsIgnoreCase("membership"))
			type = 2;
		else {
			showHelp(admin);
			return;
		}

		Player player = World.getInstance().findPlayer(Util.convertName(params[1]));
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
        LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), 0, type);
	}
}