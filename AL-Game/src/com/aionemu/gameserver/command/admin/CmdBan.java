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
import com.aionemu.gameserver.world.World;

/*
 * //ban <char|account|ip|mac|full> <player> <time in minutes> <reason>
 * 
 */
public class CmdBan extends BaseCommand {
	
	public CmdBan () {
		subCmds.put("char", new SubCmdBanChar());
		subCmds.put("ip", new SubCmdBanIP());
		subCmds.put("full", new SubCmdBanFull());
		subCmds.put("mac", new SubCmdBanMac());
	}
	
	
	public void execute(Player admin, String... params) {
		showHelp(admin);
	}
	
	public class SubCmdBanChar extends BaseCommand {
		public void execute(Player admin, String... params) {
			int playerId = 0;
			String playerName = Util.convertName(params[0]);

			Player player = World.getInstance().findPlayer(playerName);
			if (player != null)
				playerId = player.getObjectId();

			if (playerId == 0)
				playerId = DAOManager.getDAO(PlayerDAO.class).getPlayerIdByName(playerName);

			if (playerId == 0) {
				PacketSendUtility.sendMessage(admin, "Player " + playerName + " was not found!");
				showHelp(admin);
				return;
			}

			int dayCount = -1;
			dayCount = ParseInteger(params[1]);
			
			if(dayCount < 0) {
				PacketSendUtility.sendMessage(admin, "Second parameter has to be a positive daycount or 0 for infinity");
				showHelp(admin);
				return;
			}

			String reason = params[2];
			for(int i = 3; i < params.length; i++)
				reason += " "+params[i];

			PacketSendUtility.sendMessage(admin, "Char " + playerName + " is now banned for the next "+dayCount+" days!");
			
			PunishmentService.banChar(playerId, dayCount, reason);
		}
		
	}
	
	public class SubCmdBanAccount extends BaseCommand {
		//TODO Ajout d'une raison de ban
		//TODO faire un ban account avec le nom du compte
		//TODO faire un ban account avec le target de l'admin
		public void execute(Player admin, String... params) {
			if (params.length < 2) {
				showHelp(admin);
				return;
			}

			String name = Util.convertName(params[0]);
			int accountId = 0;
			String accountIp = "";

			Player player = World.getInstance().findPlayer(name);
			if (player != null) {
				accountId = player.getClientConnection().getAccount().getId();
				accountIp = player.getClientConnection().getIP();
			}

			if (accountId == 0)
				accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);

			if (accountId == 0) {
				PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
				return;
			}

			int time = ParseInteger(params[1]); // Default: infinity

			LoginServer.getInstance().sendBanPacket((byte)1, accountId, accountIp, time, admin.getObjectId());
			
		}
		
	}

	public class SubCmdBanIP extends BaseCommand {
		//TODO Ajout d'une raison de ban
		//TODO faire un ban ip avec l'ip
		//TODO faire un ban ip avec le nom de compte
		//TODO faire un ban ip avec le target de l'admin
		public void execute(Player admin, String... params) {
			if (params.length < 2) {
				showHelp(admin);
				return;
			}

			String name = Util.convertName(params[0]);
			int accountId = 0;
			String accountIp = "";

			Player player = World.getInstance().findPlayer(name);
			if (player != null) {
				accountId = player.getClientConnection().getAccount().getId();
				accountIp = player.getClientConnection().getIP();
			}

			if (accountId == 0)
				accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);

			if (accountId == 0) {
				PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
				return;
			}

			int time = ParseInteger(params[1]); // Default: infinity

			LoginServer.getInstance().sendBanPacket((byte)2, accountId, accountIp, time, admin.getObjectId());
			
		}
		
	}
	
	public class SubCmdBanFull extends BaseCommand {
		//TODO Ajout d'une raison de ban
		//TODO faire un ban full avec le nom de compte
		//TODO faire un ban full avec le target de l'admin
		public void execute(Player admin, String... params) {
			if (params.length < 2) {
				showHelp(admin);
				return;
			}

			String name = Util.convertName(params[0]);
			int accountId = 0;
			String accountIp = "";

			Player player = World.getInstance().findPlayer(name);
			if (player != null) {
				accountId = player.getClientConnection().getAccount().getId();
				accountIp = player.getClientConnection().getIP();
			}

			if (accountId == 0)
				accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(name);

			if (accountId == 0) {
				PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
				return;
			}

			int time = ParseInteger(params[1]); // Default: infinity

			LoginServer.getInstance().sendBanPacket((byte)3, accountId, accountIp, time, admin.getObjectId());
		}
	}
	
	public class SubCmdBanMac extends BaseCommand {
		//TODO Ajout d'une raison de ban
		//TODO faire un ban mac avec l'adresse
		//TODO faire un ban mac avec le nom de compte
		//TODO faire un ban mac avec une ip
		public void execute(Player admin, String... params) {
			if (params.length < 2) {
				showHelp(admin);
				return;
			}

			Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
			if (player == null) {
				PacketSendUtility.sendMessage(admin, "Player " + params[0] + " was not found!");
				return;
				
			}

			int time = ParseInteger(params[1]); // Default: infinity
			
			BannedMacManager.getInstance().banAddress(player.getClientConnection().getMacAddress(), System.currentTimeMillis() + time * 60 * 1000,
					"author=" + admin.getName() + ", " + admin.getObjectId() + "; target=direct_type");
		}
	}
	
}