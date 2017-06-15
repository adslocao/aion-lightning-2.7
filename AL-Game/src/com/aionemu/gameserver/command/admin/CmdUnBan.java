package com.aionemu.gameserver.command.admin;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;

/**
 * //Syntax : //unban <char|account|ip|mac|full> <player>
 */
public class CmdUnBan extends BaseCommand {
	
	public CmdUnBan() {
		subCmds.put("char", new SubCmdUnBanChar());
		subCmds.put("account", new SubCmdUnBanAccount());
		subCmds.put("ip", new SubCmdUnBanIP());
		subCmds.put("full", new SubCmdUnBanFull());
		subCmds.put("mac", new SubCmdUnBanMac());
	}

	public void execute(Player admin, String... params) {
		showHelp(admin);
	}
	
	public class SubCmdUnBanChar extends BaseCommand {
		
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return;
			}

			// Banned player must be offline
			String name = Util.convertName(params[0]);
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
	
	public class SubCmdUnBanAccount extends BaseCommand {
		//TODO pouvoir deban par le nom de perso
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return;
			}

			// Banned player must be offline, so get his account ID from database
			String name = Util.convertName(params[0]);
			int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
			if (accountId == 0) {
				PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
				showHelp(admin);
				return;
			}
			
			LoginServer.getInstance().sendBanPacket((byte)1, accountId, "", -1, admin.getObjectId());
		}
	}
	
	public class SubCmdUnBanIP extends BaseCommand {
		//TODO pouvoir deban par l'ip
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return;
			}

			// Banned player must be offline, so get his account ID from database
			String name = Util.convertName(params[0]);
			int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
			if (accountId == 0) {
				PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
				showHelp(admin);
				return;
			}
			
			LoginServer.getInstance().sendBanPacket((byte)2, accountId, "", -1, admin.getObjectId());
		}
	}
	
	public class SubCmdUnBanFull extends BaseCommand {
		
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return;
			}

			// Banned player must be offline, so get his account ID from database
			String name = Util.convertName(params[0]);
			int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);
			if (accountId == 0) {
				PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
				showHelp(admin);
				return;
			}
			
			LoginServer.getInstance().sendBanPacket((byte)3, accountId, "", -1, admin.getObjectId());
		}
	}
	
	public class SubCmdUnBanMac extends BaseCommand {
		
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return;
			}

			String address = params[0];
			if (BannedMacManager.getInstance().unbanAddress(address, "uban;mac="+address+", "+admin.getObjectId()+"; admin="+admin.getName()))
				PacketSendUtility.sendMessage(admin, "mac " + address + " has unbanned");
			else
				PacketSendUtility.sendMessage(admin, "mac " + address + " is not banned");
		}
	}
}

