package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author KID
 */
public class CmdStatus extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params[1].equalsIgnoreCase("alliance")) {
			PacketSendUtility.sendMessage(admin, PlayerAllianceService.getServiceStatus());
		}
		else if (params[1].equalsIgnoreCase("group")) {
			PacketSendUtility.sendMessage(admin, PlayerGroupService.getServiceStatus());
		}
		else{
			showHelp(admin);
		}
	}
}
