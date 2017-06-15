package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerRankDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public class MySQL5CustomRankDAO extends PlayerRankDAO{
	private static final Logger log = LoggerFactory.getLogger(MySQL5CustomRankDAO.class);

	//public static final String UPDATE_PLAYER_RANK = "UPDATE customRank SET customRank.rank = ?, customRank.pts = ? where customRank.playerObjId = ?"; 
	public static final String UPDATE_PLAYER_RANK = "INSERT INTO customRank (rank, pts, playerObjId) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE rank=?, pts=?";
	public static final String SELECT_PLAYER_RANK = "SELECT customRank.rank,customRank.pts FROM customRank WHERE customRank.playerObjId = ?"; 
	public static final String INSERT_PLAYER_RANK = "INSERT INTO customRank (rank, pts, playerObjId) VALUES (?,?,?)"; 
	
	
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public void loadCustomRank(final Player player) {
		DB.select(SELECT_PLAYER_RANK, new ParamReadStH() {

			@Override
			public void handleRead(ResultSet rs) throws SQLException {
				while (rs.next()) {
					player.getCustomPlayerRank().setRank(rs.getInt("rank"));
					player.getCustomPlayerRank().setPts(rs.getInt("pts"));
				}
			}

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setInt(1, player.getObjectId());
			}
		});
	}

	@Override
	public void storeCustomRank(Player player) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_PLAYER_RANK);

			stmt.setInt(1, player.getCustomPlayerRank().getRank());
			stmt.setInt(2, player.getCustomPlayerRank().getPoints());
			
			stmt.setInt(3, player.getObjectId());
			
			stmt.setInt(4, player.getCustomPlayerRank().getRank());
			stmt.setInt(5, player.getCustomPlayerRank().getPoints());
			
			stmt.execute();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("storeCustomRank", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}
	
	@Override
	public void insertCustomRank(Player player) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_PLAYER_RANK);

			stmt.setInt(1, 1);
			stmt.setInt(2, 0);
			stmt.setInt(3, player.getObjectId());

			stmt.execute();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("insert", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}
}
