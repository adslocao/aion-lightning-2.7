package com.aionemu.gameserver.command.admin;

import org.apache.commons.lang.StringUtils;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class CmdWc extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 2) {
			showHelp(admin);
			return;
		}
		
		Race adminRace = admin.getRace();
		
		String message = "";
		if (params[1].equalsIgnoreCase("ely")) {
			message = "[World-Elyos]" + admin.getName() + ": ";
			adminRace = Race.ELYOS;
		}
		else if (params[1].equalsIgnoreCase("asm")) {
			message = "[World-Asmodian]" + admin.getName() + ": ";
			adminRace = Race.ASMODIANS;
		}
		else if (params[1].equalsIgnoreCase("all")) {
			message = "[World-All]" + admin.getName() + ": ";
			adminRace = Race.PC_ALL;
		}
		else
			showHelp(admin);
		
		message.concat(StringUtils.join(params, " ", 2, params.length));
		
		for (Player a : World.getInstance().getAllPlayers()) {
			if (a.getAccessLevel() >= 3)
				PacketSendUtility.sendMessage(a, message);
			else if (a.getRace() == adminRace || adminRace == Race.PC_ALL)
				PacketSendUtility.sendMessage(a, message);
		}
	}
}
