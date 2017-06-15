package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Sarynth, (edited by Pan)
 */
public class CmdNeutral extends BaseCommand {
	


	public void execute(Player admin, String... params) {
		if (params.length != 1) {
			showHelp(admin);
			return;
		}

		String output = "You now appear neutral to " + params[0] + ".";

		int enemyType = admin.getAdminEnmity();

		if (params[0].equalsIgnoreCase("all")) {
			admin.setAdminNeutral(3);
			admin.setAdminEnmity(0);
		}
		else if (params[0].equalsIgnoreCase("players")) {
			admin.setAdminNeutral(2);
			if (enemyType > 1)
				admin.setAdminEnmity(0);
		}
		else if (params[0].equalsIgnoreCase("npcs")) {
			admin.setAdminNeutral(1);
			if (enemyType == 1 || enemyType == 3)
				admin.setAdminEnmity(0);
		}
		else if (params[0].equalsIgnoreCase("cancel")) {
			admin.setAdminNeutral(0);
			output = "You appear regular to both Players and Npcs.";
		}
		else {
			showHelp(admin);
			return;
		}

		PacketSendUtility.sendMessage(admin, output);

		admin.clearKnownlist();
		PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
		PacketSendUtility.sendPacket(admin, new SM_MOTION(admin.getObjectId(), admin.getMotions().getActiveMotions()));
		admin.updateKnownlist();
	}
}