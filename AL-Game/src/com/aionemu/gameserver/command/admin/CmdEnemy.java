package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Pan
 */
public class CmdEnemy extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length != 2) {
			showHelp(admin);
			return;
		}

		String output = "You now appear as enemy to " + params[1] + ".";

		int neutralType = admin.getAdminNeutral();

		if (params[1].equals("all")) {
			admin.setAdminEnmity(3);
			admin.setAdminNeutral(0);
		}
		else if (params[1].equals("players")) {
			admin.setAdminEnmity(2);
			if (neutralType > 1)
				admin.setAdminNeutral(0);
		}
		else if (params[1].equals("npcs")) {
			admin.setAdminEnmity(1);
			if (neutralType == 1 || neutralType == 3)
				admin.setAdminNeutral(0);
		}
		else if (params[1].equals("cancel")) {
			admin.setAdminEnmity(0);
			output = "You appear regular to both Players and Npcs.";
		}
		else if (params[1].equals("help")) {
			showHelp(admin);
			return;
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

