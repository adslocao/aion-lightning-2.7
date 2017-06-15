package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/*Syntax: //announce <n | e | en | a | an> <message>
n = name
e = elyos
a = asmodian*/

public class CmdAnnounce extends BaseCommand {

	public void execute(Player admin, String... params) {
		if (params.length < 2) {
			showHelp(admin);
			return;
		}
		
		String message = "";
		Race raceToSend = Race.PC_ALL;
		
		if (params[0].equalsIgnoreCase("e")) {
			raceToSend = Race.ELYOS;
			message = "Elyos : ";
		}
		else if (params[0].equalsIgnoreCase("en")) {
			raceToSend = Race.ELYOS;
			message = "[" + admin.getName() + "]>Elyos : ";
		}
		else if (params[0].equalsIgnoreCase("a")) {
			raceToSend = Race.ASMODIANS;
			message = "Asmodians : ";
		}
		else if (params[0].equalsIgnoreCase("an")) {
			raceToSend = Race.ASMODIANS;
			message = "[" + admin.getName() + "]>Asmodians : ";
		}
		else if (params[0].equalsIgnoreCase("n"))
			message = "[" + admin.getName() + "] : ";
		else
			message = params[0] + " ";
		
		for (int i = 1; i < params.length; i++)
			message += params[i] + " ";
		
		for (Player player : World.getInstance().getAllPlayers()) {
			if (raceToSend == Race.PC_ALL)
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, message);
			else if (raceToSend == Race.ELYOS && player.getRace() == Race.ELYOS)
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, message);
			else if (raceToSend == Race.ASMODIANS && player.getRace() == Race.ASMODIANS)
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, message);
			else if (player.getAccessLevel() >= 3)
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, message);
		}
	}
	
}
