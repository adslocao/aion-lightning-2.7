package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/*syntax //addtitle title_id [playerName]*/

/**
 * @author xavier
 */
public class CmdAddTitle extends BaseCommand {



	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 2) {
			showHelp(admin);
			return;
		}
		
		int titleId = ParseInteger(params[0]);
		if (titleId > 200 || titleId < 1) {
			PacketSendUtility.sendMessage(admin, "title id " + titleId + " is invalid (must be between 1 and 200)");
			return;
		}
		
		Player target = null;
		if (params.length == 2) {
			target = World.getInstance().findPlayer(Util.convertName(params[1]));
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "player " + params[1] + " was not found");
				return;
			}
		}
		else
			target = AutoTarget(admin, false);
		
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Aucune cible valide");
			return;
		}
		
		if (!target.getTitleList().addTitle(titleId, false, 0)) {
			PacketSendUtility.sendMessage(admin, "you can't add title #" + titleId + " to "
				+ (target.equals(admin) ? "yourself" : target.getName()));
		}
		else {
			if (target.equals(admin))
				PacketSendUtility.sendMessage(admin, "you added to yourself title #" + titleId);
			else {
				PacketSendUtility.sendMessage(admin, "you added to " + target.getName() + " title #" + titleId);
				PacketSendUtility.sendMessage(target, admin.getName() + " gave you title #" + titleId);
			}
		}
	}
}
