package com.aionemu.gameserver.command.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.GmConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdGm extends BaseCommand {
	
	public CmdGm() {
		subCmds.put("list", new SubCmdGmList());
		subCmds.put("loginannounce", new SubCmdGmOnLoginAnnounce());
		subCmds.put("whisper", new SubCmdGmWhisper());
	}

	public void execute(Player admin, String... params) {
		showHelp(admin);
	}
	
	public class SubCmdGmList extends BaseCommand {
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return ;
			}
			
			if (params[0].equalsIgnoreCase("off")) {
				admin.getCommonData().setGmConfig(GmConfig.GM_GMLIST_OFF, true);
				saveGmConfig(admin);
				PacketSendUtility.sendMessage(admin, "Affichage sur Gm List non-actif");
			}
			else if (params[0].equalsIgnoreCase("on")) {
				admin.getCommonData().setGmConfig(GmConfig.GM_GMLIST_OFF, false);
				saveGmConfig(admin);
				PacketSendUtility.sendMessage(admin, "Affichage sur Gm List actif");
			}
			else
				showHelp(admin);
		}
		
		private void saveGmConfig(Player player) {
			Connection con = null;
			try {
				con = DatabaseFactory.getConnection();
				PreparedStatement stmt = con.prepareStatement("UPDATE players SET gmconfig=? WHERE id=?");

				PlayerCommonData pcd = player.getCommonData();
				stmt.setInt(1, pcd.getGmConfig());
				stmt.setInt(2, player.getObjectId());
				stmt.execute();
				stmt.close();
			}
			catch (Exception e) { }
			finally {
				DatabaseFactory.close(con);
			}
		}
	}
	
	public class SubCmdGmOnLoginAnnounce extends BaseCommand {
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return ;
			}
			
			if (params[0].equalsIgnoreCase("off")) {
				admin.getCommonData().setGmConfig(GmConfig.GM_ONGMLOGIN_OFF, true);
				PacketSendUtility.sendMessage(admin, "Affichage de connexion staff non actif");
			}
			else if (params[0].equalsIgnoreCase("on")) {
				admin.getCommonData().setGmConfig(GmConfig.GM_ONGMLOGIN_OFF, false);
				PacketSendUtility.sendMessage(admin, "Affichage de connexion staff actif");
			}
			else
				showHelp(admin);
		}
	}
	
	public class SubCmdGmWhisper extends BaseCommand {
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return ;
			}
			
			if (params[0].equalsIgnoreCase("off")) {
				admin.getCommonData().setGmConfig(GmConfig.GM_WHISPER_OFF, true);
				PacketSendUtility.sendMessage(admin, "Whisper Off");
			}
			else if (params[0].equalsIgnoreCase("on")) {
				admin.getCommonData().setGmConfig(GmConfig.GM_WHISPER_OFF, false);
				PacketSendUtility.sendMessage(admin, "Whisper On");
			}
			else
				showHelp(admin);
		}
	}

}
