package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;

public class CmdTp extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			showHelp(admin);
			return;
		}
		
		float z = ParseFloat(params[0]);
		
		TeleportService.teleportTo(admin, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(),
				admin.getZ() + z, admin.getHeading(), 0, true);
	}
}