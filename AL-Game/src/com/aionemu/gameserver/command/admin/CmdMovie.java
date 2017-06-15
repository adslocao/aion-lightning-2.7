package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdMovie extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			showHelp(admin);
		}
		else {
			PacketSendUtility.sendPacket(admin, new SM_PLAY_MOVIE(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
		}
	}
}