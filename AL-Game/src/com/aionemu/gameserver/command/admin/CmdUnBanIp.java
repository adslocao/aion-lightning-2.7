package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;

/**
 * @author Watson
 */
public class CmdUnBanIp extends BaseCommand {
	
	
	public void execute(Player player, String... params) {
		if (params == null || params.length < 2) {
			showHelp(player);
			return;
		}

		LoginServer.getInstance().sendBanPacket((byte) 2, 0, params[1], -1, player.getObjectId());
	}

}
