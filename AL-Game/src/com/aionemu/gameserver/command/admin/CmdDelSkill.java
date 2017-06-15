package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * @author xTz
 */
public class CmdDelSkill extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 2 || params.length > 3) {
			showHelp(admin);
			return;
		}

		Player player;
		PlayerSkillList playerSkillList = null;
		String recipient = null;
		recipient = Util.convertName(params[1]);
		int skillId = 0;
		if (params.length == 3) {
			player = World.getInstance().findPlayer(recipient);
			if (player == null) {
				PacketSendUtility.sendMessage(admin, "The specified player is not online.");
				return;
			}

			if ("all".startsWith(params[2]))
				playerSkillList = player.getSkillList();
			else {
				try {
					skillId = ParseInteger(params[2]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "Param 1 must be an integer or <all>.");
					return;
				}

				if (!check(admin, player, skillId))
					return;
			}
			apply(admin, player, skillId, playerSkillList);

		}
		if (params.length == 2) {
			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "You should select a target first!");
				return;
			}

			if (target instanceof Player) {
				player = (Player) target;

				if ("all".startsWith(params[1]))
					playerSkillList = player.getSkillList();
				else {
					try {
						skillId = ParseInteger(params[1]);
					}
					catch (NumberFormatException e) {
						PacketSendUtility.sendMessage(admin, "Param 0 must be an integer or <all>.");
						return;
					}

					if (!check(admin, player, skillId))
						return;
				}
				if (target instanceof Player)
					apply(admin, player, skillId, playerSkillList);
			}
			else
				PacketSendUtility.sendMessage(admin, "This command can only be used on a player !");
		}
	}

	private static boolean check(Player admin, Player player, int skillId) {
		if (skillId != 0 && !player.getSkillList().isSkillPresent(skillId)) {
			PacketSendUtility.sendMessage(admin, "Player dont have this skill.");
			return false;
		}
		if (player.getSkillList().getSkillEntry(skillId).isStigma()) {
			PacketSendUtility.sendMessage(admin, "You can't remove stigma skill.");
			return false;
		}
		return true;
	}

	public void apply(Player admin, Player player, int skillId, PlayerSkillList playerSkillList) {
		if (skillId != 0) {
			SkillLearnService.removeSkill(player, skillId);
			PacketSendUtility.sendMessage(admin, "You have successfully deleted the specified skill.");
		}
		else {
			for (PlayerSkillEntry skillEntry : playerSkillList.getAllSkills()) {
				if (!skillEntry.isStigma()) {
					SkillLearnService.removeSkill(player, skillEntry.getSkillId());
				}
			}

			PacketSendUtility.sendMessage(admin, "You have success delete All skills.");
		}
	}
}
