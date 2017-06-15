package com.aionemu.gameserver.command.admin;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;


public class CmdPasskeyReset extends BaseCommand {
	
	

	public void execute(Player player, String... params) {
		if (params.length <= 1) {
			showHelp(player);
			return;
		}

		String name = Util.convertName(params[1]);
		int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
		if (accountId == 0) {
			PacketSendUtility.sendMessage(player, "player " + name + " can't find!");
			showHelp(player);
			return;
		}

		try {
			Integer.parseInt(params[2]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(player, "Parameters should be number!");
			return;
		}

		String newPasskey = params[3];
		if (!(newPasskey.length() > 5 && newPasskey.length() < 9)) {
			PacketSendUtility.sendMessage(player, "Passkey is 6~8 digits!");
			return;
		}

		DAOManager.getDAO(PlayerPasskeyDAO.class).updateForcePlayerPasskey(accountId, newPasskey);
		LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", -1, player.getObjectId());
	}
}
