package com.aionemu.gameserver.command.player;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.TranslationService;

/**
 * @author Ferosia
 */

public class CmdLocale extends BaseCommand {
	
	public void execute(final Player player, String... params) {
		Logger log = LoggerFactory.getLogger(CmdLocale.class);
		// 0 or one parameter needed
		if (params.length > 1) {
			showHelp(player);
			return;
		}
		
		if(params.length == 0) {
			String message = TranslationService.LOCALE_SHOW_VALUE.toString(player, player.getCommonData().getLocale());
			sendCommandMessage(player, message);
			return;
		}
		
		final int accountId = player.getPlayerAccount().getId();
		final String playerLocale = params[0].toString();
		
		if(playerLocale.contains("fr") || playerLocale.contains("en")) {
			Connection con = null;
			try {
				con  = DatabaseFactory.getConnection();
				PreparedStatement stmt = con
					.prepareStatement("UPDATE account_locale SET locale=? WHERE account_id=?");
				
				stmt.setString(1, playerLocale);
				stmt.setInt(2, accountId);
				stmt.execute();
				stmt.close();
				
				player.getCommonData().setLocale(playerLocale);
				
				String message = TranslationService.LOCALE_UPDATED.toString(player);
				sendCommandMessage(player, message);
				return;
			}
			catch (Exception e) {
				String message = TranslationService.GENERAL_ERROR_DB.toString(player);
				sendCommandMessage(player, message);
				log.error("Error updating player account locale : " + player.getObjectId() + " " + player.getName(), e);
				return;
			}
			finally {
				DatabaseFactory.close(con);
			}
		}
		else {
			String message = TranslationService.LOCALE_ERR_UNKNOWN_TYPE.toString(player);
			sendCommandMessage(player, message);
			return;
		}
	}
}