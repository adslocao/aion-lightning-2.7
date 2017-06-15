package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;


public class CmdMoveToMe extends BaseCommand {
	
	

	public void execute(Player admin, String... params) {
		if ( params.length < 1) {
			showHelp(admin);
			return;
		}

		Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (playerToMove == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}

		if (playerToMove == admin) {
			PacketSendUtility.sendMessage(admin, "Cannot use this command on yourself.");
			return;
		}

		TeleportService.teleportTo(playerToMove, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(),
				admin.getZ(), admin.getHeading(), 0, true);
		PacketSendUtility.sendMessage(admin, "Teleported player " + playerToMove.getName() + " to your location.");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by " + admin.getName() + ".");
	}

}