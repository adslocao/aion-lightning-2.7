package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/*syntax //moveplayertoplayer <characterNameToMove> <characterNameDestination>") */


public class CmdMovePlayerToPlayer extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 3) {
			showHelp(admin);
			return;
		}
		Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (playerToMove == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}

		Player playerDestination = World.getInstance().findPlayer(Util.convertName(params[1]));
		if (playerDestination == null) {
			PacketSendUtility.sendMessage(admin, "The destination player is not online.");
			return;
		}

		if (playerToMove.getObjectId() == playerDestination.getObjectId()) {
			PacketSendUtility.sendMessage(admin, "Cannot move the specified player to their own position.");
			return;
		}

		TeleportService.teleportTo(playerToMove, playerDestination.getWorldId(), playerDestination.getInstanceId(),
			playerDestination.getX(), playerDestination.getY(), playerDestination.getZ(), playerDestination.getHeading(), 3000, true);

		PacketSendUtility.sendMessage(admin, "Teleported player " + playerToMove.getName() + " to the location of player "
			+ playerDestination.getName() + ".");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by an administrator.");
	}
}
