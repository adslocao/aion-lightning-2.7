package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerVarsDAO;

/**
 * @author KID
 */
public class MySQL5PlayerVarsDAO extends PlayerVarsDAO {
	public static Logger log = LoggerFactory.getLogger(MySQL5PlayerVarsDAO.class);
	
	@Override
	public Map<String, Object> load(final int playerId) {
		final Map<String, Object> map = FastMap.newInstance();
		DB.select("SELECT param,value FROM player_vars WHERE player_id=?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				while (rset.next()) {
					String key = rset.getString("param");
					String value = rset.getString("value");
					map.put(key, value);
				}
			}

			@Override
			public void setParams(PreparedStatement st) throws SQLException {
				st.setInt(1, playerId);
			}
		});

		return map;
	}

	@Override
	public String loadTimed(final int playerId, final String key, final long TimeafterExpir) {
		final String[] result = {null};
		DB.select("SELECT value,time FROM player_vars WHERE player_id=? AND param=?", new ParamReadStH() {

			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				while (rset.next()) {
					String value = rset.getString("value");
					String time = rset.getString("time");
					
					//il serait plus interessant de directement seauv le time en Timestamp
					// et non en Date dans la bdd pour des raisons d'optimisation
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
					Date date = null;
				
					try {date = sdf.parse(time);}
					catch (ParseException e) {e.printStackTrace();}
					
					Calendar expireDate = Calendar.getInstance();
					expireDate.setTimeInMillis(date.getTime() + TimeafterExpir);
					
					Calendar currentTime= Calendar.getInstance();
					currentTime.setTimeInMillis(System.currentTimeMillis());
					
					if(expireDate.after(currentTime))
						result[0] = value;
				}
			}

			@Override
			public void setParams(PreparedStatement st) throws SQLException {
				st.setInt(1, playerId);
				st.setString(2, key);
			}
		});

		return result[0];
	}
	
	@Override
	public boolean set(final int playerId, final String key, final Object value) {
		//remove if already exist
		remove(playerId, key);
		boolean result = DB.insertUpdate(
			"INSERT INTO player_vars (`player_id`, `param`, `value`, `time`) VALUES (?,?,?,NOW())", new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
					stmt.setInt(1, playerId);
					stmt.setString(2, key);
					stmt.setString(3, value.toString());
					stmt.execute();
				}
			});

		return result;
	}

	@Override
	public boolean remove(final int playerId, final String key) {
		boolean result = DB.insertUpdate("DELETE FROM player_vars WHERE player_id=? AND param=?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, playerId);
				stmt.setString(2, key);
				stmt.execute();
			}
		});

		return result;
	}

	@Override
	public boolean update(final int playerId, final String key, final Object value) {
		boolean result = DB.insertUpdate("UPDATE player_vars SET value =? WHERE player_id=? AND param=?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, value.toString());
				stmt.setInt(2, playerId);
				stmt.setString(3, key);
				stmt.execute();
			}
		});

		return result;
	}
	
	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
