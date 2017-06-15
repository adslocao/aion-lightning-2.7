package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.SkillLearnService;



public class CmdGiveMissingSkills extends BaseCommand {
	
	public void execute(Player player, String... params) {
		SkillLearnService.addMissingSkills(player);
	}

}
