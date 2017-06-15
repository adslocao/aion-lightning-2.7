package com.aionemu.gameserver.utils.audit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.PunishmentConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.audit.AutoBan;
import com.google.common.base.Preconditions;


public class AuditLogger {

	private static final Logger log = LoggerFactory.getLogger(AuditLogger.class);
	
	public static void auditCmd (Player admin, VisibleObject obj, String cmd) {
		int playerId = 0;
		String target = "";
		if (obj != null) {
			if (obj instanceof Player) {
				playerId = ((Player)obj).getCommonData().getPlayerObjId();
				target = "[Target (Player): " +  ((Player)obj).getCommonData().getName() + "]";
			}
			else if (obj instanceof Npc)
				target = "[Target (Npc):" + ((Npc)obj).getName() + "(" + ((Npc)obj).getNpcId() + ")]";
			else if (obj instanceof Creature)
				target = "[Target (Creature): " + ((Creature)obj).getName() + "]";
		}
		
		String text = "[By: " + admin.getCommonData().getName() + "(" + admin.getAcountName() + ")]" + target + "[Command: " + cmd + "]";

		Connection con = null;
		try {
	    	con = DatabaseFactory.getConnection();
	        PreparedStatement stmt = con.prepareStatement("INSERT INTO audit (id_account, id_admin, id_player, type, text) VALUES (?, ?, ?, ?, ?);");
	        stmt.setInt(1, admin.getPlayerAccount().getId());
	        stmt.setInt(2, admin.getCommonData().getPlayerObjId());
	        stmt.setInt(3, playerId);
	        stmt.setString(4, "cmd");
	        stmt.setString(5, text);
	        
	        stmt.execute();
	        	
	        stmt.close();
	    }
	    catch (SQLException e) { 
	    	log.error("Audit Log Fail", e);
	    }
	    finally {
	    	DatabaseFactory.close(con);
	    }
	}

	public static final void info(Player player, String message) {
		Preconditions.checkNotNull(player, "Player should not be null or use different info method");
		if (LoggingConfig.LOG_AUDIT) {
			info(player.getName(), player.getObjectId(), message);
		}
		if (PunishmentConfig.PUNISHMENT_ENABLE) {
			AutoBan.punishment(player, message);
		}
	}

	public static final void info(String playerName, int objectId, String message) {
			message += " Player name: " + playerName + " objectId: " + objectId;
			log.info(message);

			if (CustomConfig.GM_AUDIT_MESSAGE_BROADCAST)
				GMService.getInstance().broadcastMessage(message, 0);
	}
}
