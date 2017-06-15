package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


public class CmdPromote extends BaseCommand {
	
	

	public void execute(Player admin, String... params) {
		if (params.length != 4) {
			showHelp(admin);
			return;
		}

		int mask = 0;
		mask = ParseInteger(params[3]);

		int type = 0;
		if (params[2].equalsIgnoreCase("accesslevel")) {
			type = 1;
			if (mask > 13 || mask < 0) {
				PacketSendUtility.sendMessage(admin, "accesslevel can be 0 - 12");
				return;
			}
		}
		else if (params[2].equalsIgnoreCase("membership")) {
			type = 2;
			if (mask > 10 || mask < 0) {
				PacketSendUtility.sendMessage(admin, "membership can be 0 - 10");
				return;
			}
		}
		else {
			showHelp(admin);
			return;
		}

		Player player = World.getInstance().findPlayer(Util.convertName(params[1]));
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}
		LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), mask, type);
	}
}
