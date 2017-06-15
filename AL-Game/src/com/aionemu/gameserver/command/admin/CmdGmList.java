package com.aionemu.gameserver.command.admin;


import java.util.Collection;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.GmConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.GMService;


public class CmdGmList extends BaseCommand {
	public void execute(Player admin, String... params) {

		String sGMNames = "";
		Collection<Player> gms = GMService.getInstance().getGMs();
		int GMCount = 0;

		for (Player player : gms) {
			if (player.getCommonData().getGmConfig(GmConfig.GM_GMLIST_OFF))
				continue;
			
			GMCount++;

			sGMNames += player.getFullName() + "\n";
		}

		if (GMCount == 0)
			PacketSendUtility.sendMessage(admin, "There is no GM online !");
		else if (GMCount == 1)
			PacketSendUtility.sendMessage(admin, "There is 1 GM online !");
		else
			PacketSendUtility.sendMessage(admin, "There are " + GMCount + " GMs online !");
		if (GMCount != 0)
			PacketSendUtility.sendMessage(admin, "List : \n" + sGMNames);
	}
}