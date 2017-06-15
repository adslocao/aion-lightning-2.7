package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * //Syntax : //useskill <skillId> <skillLevel> [true:justEffect]
 */
public class CmdUseSkill extends BaseCommand {
		
	public void execute(Player admin, String... params) {
		if (params.length < 2 || params.length > 3) {
			showHelp(admin);
			return;
		}
		
		Creature target = getTarget(admin, false);
		if (target == null) {
			
		}
		
		int skillId = ParseInteger(params[0]);
		int skillLevel = ParseInteger(params[1]);

		if (target.getTarget() == null || !(target.getTarget() instanceof Creature)) {
			PacketSendUtility.sendMessage(admin, "Target must select some creature!");
			return;
		}

		SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (skillTemplate == null) {
			PacketSendUtility.sendMessage(admin, "No skill template id:" + skillId);
			return;
		}
			
		if (params.length == 3 && params[2].equalsIgnoreCase("true")) {
			SkillEngine.getInstance().applyEffectDirectly(skillId, target, (Creature)target.getTarget(), 2000);
			PacketSendUtility.sendMessage(admin, "applyingskillid:" + skillId);
		}
		else
			target.getController().useSkill(skillId, skillLevel);
	}
}
