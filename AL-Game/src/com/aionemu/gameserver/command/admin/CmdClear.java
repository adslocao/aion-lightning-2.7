package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.FindGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author KID
 */
public class CmdClear extends BaseCommand {
	

	public void execute(Player admin, String... params) {
		if (params.length == 1) {
    		showHelp(admin);
    		return;
    	}
		
		if(params[1].equalsIgnoreCase("groups")) {
			PacketSendUtility.sendMessage(admin, "Not implemented, if need this - pm to AT");
		}
		else if(params[1].equalsIgnoreCase("allys")) {
			PacketSendUtility.sendMessage(admin, "Not implemented, if need this - pm to AT");
		}
		else if(params[1].equalsIgnoreCase("findgroup")){
			FindGroupService.getInstance().clean();
		}
		else{
			showHelp(admin);
		}
	}
}

