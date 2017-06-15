package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


//sprison <player> <delay>(minutes) This command is sending player to prison.


public class CmdSPrison extends BaseCommand {
	

	public void execute(Player admin, String... params) {
		if (params.length < 3) {
			showHelp(admin);
			return;
		}
		
		Player playerToPrison = World.getInstance().findPlayer(Util.convertName(params[0]));
		int delay = ParseInteger(params[1]);
		if (delay == 0) {
			PacketSendUtility.sendMessage(admin, "Delay incorrect");
			return;
		}
		
		String reason = params[2];
		for(int i = 3; i < params.length; i++)
			reason += " "+params[i];

		if (playerToPrison != null && !playerToPrison.isGM()) {
			PunishmentService.setIsInPrison(playerToPrison, true, delay, reason);
			PacketSendUtility.sendMessage(admin, "Player " + playerToPrison.getName() + " sent to prison for " + delay
				+ " because " + reason + ".");
		}
	}
}
