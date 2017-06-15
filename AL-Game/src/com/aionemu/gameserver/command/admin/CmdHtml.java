package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.HTMLService;

/**
 * @author lord_rex
 */
public class CmdHtml extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 2) {
			showHelp(admin);
			return;
		}

		if (params[1].equals("show")) {
			if (params.length >= 3){
				HTMLService.showHTML(admin, HTMLCache.getInstance().getHTML(params[2] + ".xhtml"));
			}
			else{
				showHelp(admin);
			}
		}
	}
}

