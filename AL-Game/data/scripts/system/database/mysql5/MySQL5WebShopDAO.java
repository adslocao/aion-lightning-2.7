package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.WebShopDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.SystemMailService;
import com.aionemu.gameserver.services.TranslationService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ferosia
 */
public class MySQL5WebShopDAO extends WebShopDAO {
	
	private static final Logger log = LoggerFactory.getLogger(MySQL5PetitionDAO.class);

	public static final String SELECT_ALL_QUERY = "SELECT `id`, `player_id`, `item`, `nb` FROM `myshop` ORDER BY `player_id`";
	public static final String SELECT_BY_PLAYER_QUERY = "SELECT `id`, `item`, `nb` FROM `myshop` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `myshop` WHERE `id`=?";
	
	@Override
	public void checkAllPendingShopItem() {
		try {
			// log.warn("A new instance of SELECT_ALL_QUERY has been called");
			DB.select(SELECT_ALL_QUERY, new ParamReadStH() {

				@Override
				public void handleRead(ResultSet rset) throws SQLException {
					// Loop on each result
					while (rset.next()) {
						
						int playerId = rset.getInt("player_id");
						Player player = World.getInstance().findPlayer(playerId);
						
						if(player == null)
							continue;
						
						if(player.getMailbox() == null) // If a shop is pending and player is currently login
							continue;
						
						if(!player.getMailbox().haveFreeSlots()) {
							// "Your mailbox is full, unable to receive WebShop items."
							String message = TranslationService.SHOP_MAILBOX_FULL.toString(player);
							sendCommandMessage(player, message);
							continue;
						}
						
						int transactionId = rset.getInt("id");
						int item = rset.getInt("item");
						int nb = rset.getInt("nb");
						
						sendMailsWithItem(player, transactionId, item, nb);
					}
				}

				@Override
				public void setParams(PreparedStatement stmt) throws SQLException {
					// No param to set
				}
				
			});
		}
		catch (Exception ex) {
			log.error("[Shop][Error] Could not catch transaction list for all players from DB: " + ex.getMessage(), ex);
			return;
		}
	}

	@Override
	public void checkPlayerPendingShopItem(int playerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean deletePendingShopItemById(int shopId, int playerId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, shopId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("[Shop][Error] Could not delete transaction " + shopId + " for player " + playerId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
	
	private void sendMailsWithItem(Player player, int transactionId, int item, int nb) {
		int playerId = player.getObjectId();
		String playerName = player.getName();
		// Catch item template for its name and maxStackCount
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(item);
		int maxStackCount = (int) itemTemplate.getMaxStackCount();
		
		// First of all, check if we can remove the row from database
		if (deletePendingShopItemById(transactionId, playerId)) {
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
	
	protected void sendCommandMessage(Player player, String message) {
		PacketSendUtility.sendBrightYellowMessage(player, message);
	}
}