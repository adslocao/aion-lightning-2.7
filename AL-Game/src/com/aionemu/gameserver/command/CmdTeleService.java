package com.aionemu.gameserver.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.world.WorldPosition;


public class CmdTeleService {
	private static final Logger log = LoggerFactory.getLogger(CmdTeleService.class);
	private static final CmdTeleService instance = new CmdTeleService();
	private Map<String, CmdTeleTemplate> teleTemplate;
	
	public CmdTeleService () {
		teleTemplate = new HashMap<String, CmdTeleTemplate>();
		loadTeleTemplate();
	}
	
	public void loadTeleTemplate () {
		teleTemplate.clear();
		Connection con = null;
		try {
	    	con = DatabaseFactory.getConnection();
	        PreparedStatement stmt = con.prepareStatement("SELECT * FROM teleport");

	        ResultSet resultSet = stmt.executeQuery();
	        while (resultSet.next())
	        	teleTemplate.put(resultSet.getString("name"), new CmdTeleTemplate(resultSet.getInt("worldid"), resultSet.getFloat("x"), resultSet.getFloat("y"), resultSet.getFloat("z")));
	        
	        resultSet.close();
	        stmt.close();
	    }
	    catch (SQLException e) { 
	    	log.error("Load 'teleport' Fail", e);
	    }
	    finally {
	    	DatabaseFactory.close(con);
	    }
	}
	
	public boolean addTeleTemplate (String name, WorldPosition pos) {
		Connection con = null;
		boolean result = false;
		try {
	    	con = DatabaseFactory.getConnection();
	        PreparedStatement stmt = con.prepareStatement("INSERT INTO teleport (name, worldid, x, y, z) VALUES (?, ?, ?, ?, ?);");
	        stmt.setString(1, name);
	        stmt.setInt(2, pos.getMapId());
	        stmt.setFloat(3, pos.getX());
	        stmt.setFloat(4, pos.getY());
	        stmt.setFloat(5, pos.getZ());
	        
	        result = stmt.execute();
	        	
	        stmt.close();
	        return result;
	    }
	    catch (SQLException e) { 
	    	log.error("create 'teleport' Fail", e);
	    	return result;
	    }
	    finally {
	    	DatabaseFactory.close(con);
	    }
	}
	
	public CmdTeleTemplate getTeleport (String name) {
		return teleTemplate.get(name);
	}
	
	public static CmdTeleService getInstance () {
		return instance;
	}
}
