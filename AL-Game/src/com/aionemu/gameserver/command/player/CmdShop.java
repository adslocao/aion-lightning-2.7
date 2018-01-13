package com.aionemu.gameserver.command.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.TranslationService;
import com.aionemu.gameserver.services.SystemMailService;

/**
 * @author Ferosia
 * Warning! Requires additional table. See sql/updates/addWebShop.sql
 */

public class CmdShop extends BaseCommand {
	
	public static Logger log = LoggerFactory.getLogger(CmdShop.class);
	
	public static final String DELETE_QUERY = "DELETE FROM `myshop` WHERE `id`=?";
	public static final String SELECT_QUERY = "SELECT `id`, `item`, `nb` FROM `myshop` WHERE `player_id`=?";
	
	public void execute(final Player player, String... params) {
		
		// There is no parameter needed
		if (params.length >= 1) {
			showHelp(player);
			return;
		}
		
		final int playerId = player.getObjectId();
		final String playerName = player.getName();
		
		try {
			DB.select(SELECT_QUERY, new ParamReadStH() {
				
				// Set unique parameter to SQL query				
				public void setParams(PreparedStatement stmt) throws SQLException {
					stmt.setInt(1, playerId);
				}
				
				// count will be used to inform player on how many objects he bought in the shop
				int count = 0;
		
				public void handleRead(ResultSet rset) throws SQLException {
					// Loop on each result
					while (rset.next()) {						
						if (!player.getMailbox().haveFreeSlots()) {
							// "Your mailbox is full, unable to receive WebShop items."
							String message = TranslationService.SHOP_MAILBOX_FULL.toString(player);
							sendCommandMessage(player, message);
							return;
						}
						
						count += 1;
						
						int transactionId = rset.getInt("id");
						int item = rset.getInt("item");
						int nb = rset.getInt("nb");
						
						// Catch item template for its name and maxStackCount
						ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(item);
						int maxStackCount = (int) itemTemplate.getMaxStackCount();
						
						// First of all, check if we can remove the row from database
						if (removeShopTransaction(transactionId, playerId)) {
							// Variables creation
							
							String yourCommand = TranslationService.SHOP_MAIL_TITLE.toString(player); // Here is your command
							String thankYou = TranslationService.SHOP_THANKYOU_ORDER.toString(player); // Thank you for your order
							
							// These checks are used to see if there is more than maxStackCount to send
							// If yes, multiple mails will be send to player
							if (nb > maxStackCount && maxStackCount != 0 && item != 182400001) {
								String multipleMail = TranslationService.SHOP_MULTIPLE_MAIL.toString(player);
								int qtyLeft = nb;
								int qtySend = 0;
								for(int i = 1; i <= nb ; i += maxStackCount) {
									if (qtyLeft > maxStackCount) {
										qtySend = maxStackCount;
										qtyLeft -= qtySend;
									}
									else {
										qtySend = qtyLeft;
									}
									// TODO: if(!player.getMailbox().haveFreeSlots()) => create new pending shop transaction
									SystemMailService.getInstance().sendMail(
											"WebShop", playerName, itemTemplate.getName(), yourCommand + 
											// One mail was not enough, please check your mails
											multipleMail + thankYou, item, qtySend, 0, true
									);
								}
							} 
							else if (item == 182400001) {
								SystemMailService.getInstance().sendMail("WebShop", playerName, itemTemplate.getName(), yourCommand + thankYou, 0, -1, nb, true);
							}
							else
								SystemMailService.getInstance().sendMail("WebShop", playerName, itemTemplate.getName(), yourCommand + thankYou, item, nb, 0, true);
						}
						else {
							// A database error has occurred. Please contact an Administrator
							String message = TranslationService.GENERAL_ERROR_DB.toString(player);
							sendCommandMessage(player, message);
							return;
						}
						
					}
					if(count == 0) {
						// No WebShop order found for <playerName>
						String message = TranslationService.SHOP_NO_PENDING_ORDER.toString(player, playerName);
						sendCommandMessage(player, message);
					}
					else if (count == 1) {
						// Your WebShop order for <playerName> has been correctly sent.
						String message = TranslationService.SHOP_ORDER_RECEIVED.toString(player, playerName);
						sendCommandMessage(player, message);
					}
					else {
						// The X WebShop orders for <playerName> have been correctly sent.
						String message = TranslationService.SHOP_ORDERS_RECEIVED.toString(player, String.valueOf(count), playerName);
						sendCommandMessage(player, message);
					}
				}
			});
		}
		// Log error and inform player if there is a database error
		catch (Exception ex) {
			log.error("[Shop][Error] Could not catch transaction list for player " + playerId + " from DB: " + ex.getMessage(), ex);
			String message = TranslationService.GENERAL_ERROR_DB.toString(player);
			sendCommandMessage(player, message);
			return;
		}
	}
	
	// Function to remove transaction from database. Must be done first for security reasons.
	public boolean removeShopTransaction(int transactionId, int playerId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, transactionId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("[Shop][Error] Could not delete transaction " + transactionId + " for player " + playerId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}
	
}