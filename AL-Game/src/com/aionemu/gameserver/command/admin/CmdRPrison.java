package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


public class CmdRPrison extends BaseCommand {
	
	

	public void execute(Player admin, String... params) {
		if (params.length != 1) {
			showHelp(admin);
			return;
		}
		
		Player playerFromPrison = World.getInstance().findPlayer(Util.convertName(params[0]));
		
		if (playerFromPrison != null) {
			PunishmentService.setIsInPrison(playerFromPrison, false, 0, "");
			PacketSendUtility.sendMessage(admin, "Player " + playerFromPrison.getName() + " removed from prison.");
		}
	}
		
}		
