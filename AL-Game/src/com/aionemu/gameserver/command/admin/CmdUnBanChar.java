package com.aionemu.gameserver.command.admin;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;

/**
 * @author nrg
 */
public class CmdUnBanChar extends BaseCommand {
	
	

	public void execute(Player admin, String... params) {
		if (params == null || params.length < 2) {
			showHelp(admin);
			return;
		}

		// Banned player must be offline
		String name = Util.convertName(params[1]);
		int playerId = DAOManager.getDAO(PlayerDAO.class).getPlayerIdByName(name);
		if (playerId == 0) {
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			showHelp(admin);
			return;
		}

		PacketSendUtility.sendMessage(admin, "Character " + name + " is not longer banned!");
		
		PunishmentService.unbanChar(playerId);
	}
}

