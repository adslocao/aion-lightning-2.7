package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapType;



public class CmdMoveTo extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 4) {
			showHelp(admin);
			return;
		}

		int worldId;
		float x, y, z;

		try {
			worldId = ParseInteger(params[0]);
			x = ParseFloat(params[1]);
			y = ParseFloat(params[2]);
			z = ParseFloat(params[3]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "All the parameters should be numbers");
			return;
		}

		if (WorldMapType.getWorld(worldId) == null) {
			PacketSendUtility.sendMessage(admin, "Illegal WorldId %d " + worldId);
		}
		else {
			TeleportService.teleportTo(admin, worldId, x, y, z, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to " + x + " " + y + " " + z + " [" + worldId + "]");
		}
	}
}
