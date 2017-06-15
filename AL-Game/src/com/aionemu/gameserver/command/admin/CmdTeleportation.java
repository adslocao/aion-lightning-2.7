package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdTeleportation extends BaseCommand {
	
	

	public void execute(Player player, String... params) {
		if (player.getAdminTeleportation()) {
			PacketSendUtility.sendMessage(player, "Teleportation disabled.");
			player.setAdminTeleportation(false);
		}
		else {
			PacketSendUtility.sendMessage(player, "Teleportation enabled.");
			player.setAdminTeleportation(true);
		}
	}
}
