/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.InGameShopDAO;
import com.aionemu.gameserver.model.ingameshop.IGItem;

/**
 * @author xTz
 */
public class MySQL5inGameShopDAO extends InGameShopDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5inGameShopDAO.class);
	public static final String SELECT_QUERY = "SELECT `object_id`, `item_id`, `item_count`, `item_price`, `category`, `list`, `sales_ranking`, `description` FROM `ingameshop`";
	public static final String DELETE_QUERY = "DELETE FROM `ingameshop` WHERE `item_id`=? AND `category`=? AND `list`=?";
	public static final String UPDATE_SALES_QUERY = "UPDATE `ingameshop` SET `sales_ranking`=? WHERE `object_id`=?";
	
	@Override
	public FastMap<Integer, IGItem> loadInGameShopItems() {
		FastMap<Integer, IGItem> items = FastMap.newInstance();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int category = rset.getInt("category");
				if(category < 3)
					continue;
				
				int objectId = rset.getInt("object_id");
				int itemId = rset.getInt("item_id");
				int itemCount = rset.getInt("item_count");
				int itemPrice = rset.getInt("item_price");
				int list = rset.getInt("list");
				int salesRanking = rset.getInt("sales_ranking");
				String description = rset.getString("description");
				items.put(objectId, new IGItem(objectId, itemId, itemCount, itemPrice, category, list, salesRanking, description));
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not restore inGameShop data for all from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return items;
	}

	@Override
	public boolean deleteIngameShopItem(int itemId, int category, int list) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, itemId);
			stmt.setInt(2, category);
			stmt.setInt(3, list);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error delete ingameshopItem: " + itemId, e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public void saveIngameShopItem(int objectId, int itemId, int itemCount, int itemPrice, int category, int list,
		int salesRanking, String description) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con
				.prepareStatement("INSERT INTO ingameshop(object_id, item_id, item_count, item_price, category, list, sales_ranking, description)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

			stmt.setInt(1, objectId);
			stmt.setInt(2, itemId);
			stmt.setInt(3, itemCount);
			stmt.setInt(4, itemPrice);
			stmt.setInt(5, category);
			stmt.setInt(6, list);
			stmt.setInt(7, salesRanking);
			stmt.setString(8, description);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error saving Item: " + objectId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean increaseSales(int object, int current) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_SALES_QUERY);
			stmt.setInt(1, current);
			stmt.setInt(2, object);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Error increaseSales Item: " + object, e);
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
}
