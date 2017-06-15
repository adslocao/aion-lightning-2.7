package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.abyss.AbyssRankUpdateService;

public class CmdRanking extends BaseCommand {
	
		
	public void execute(Player admin, String... params) {
		AbyssRankUpdateService.getInstance().performUpdate();
	}
	
}

