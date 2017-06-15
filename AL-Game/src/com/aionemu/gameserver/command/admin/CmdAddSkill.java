package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/*syntax //addskill <skillId> <skillLevel>*/


/**
 * @author Phantom
 */
public class CmdAddSkill extends BaseCommand {

	public void execute(Player admin, String... params) {
		if (params.length != 2) {
			showHelp(admin);
			return;
		}

		VisibleObject target = admin.getTarget();

		int skillId = 0;
		int skillLevel = 0;

		try {
			skillId = Integer.parseInt(params[0]);
			skillLevel = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "Parameters need to be an integer.");
			return;
		}

		if (target instanceof Player) {
			Player targetpl = (Player) target;
			targetpl.getSkillList().addSkill(targetpl, skillId, skillLevel);
			PacketSendUtility.sendMessage(admin, "You have success add skill");
			PacketSendUtility.sendMessage(targetpl, "You have acquire a new skill");
		}
	}
	
}


