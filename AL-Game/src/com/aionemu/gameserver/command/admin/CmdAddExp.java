package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;


//syntax //addexp <exp>
public class CmdAddExp extends BaseCommand {

	public void execute(Player admin, String... params) {
		if (params.length != 1) {
			showHelp(admin);
			return;
		}

		Player target = AutoTarget(admin, false);
		
		target.getCommonData().setExp(target.getCommonData().getExp() + ParseLong(params[0]));
		if (target == admin)
			PacketSendUtility.sendMessage(admin, "Vous vous etes ajoute " + params[0] + " points d'experience.");
		else {
			PacketSendUtility.sendMessage(admin, "Vous avez ajoute " + params[0] + " points d'experience a " + target.getName());
			PacketSendUtility.sendMessage(target, "vous recevez " + params[0] + " points d'experience.");
		}
	}
	
}
