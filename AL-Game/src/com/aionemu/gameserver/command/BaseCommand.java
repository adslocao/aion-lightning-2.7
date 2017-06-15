package com.aionemu.gameserver.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public abstract class BaseCommand {
	private String help;
	public abstract void execute (Player player, String... params);
	protected Map<String, BaseCommand> subCmds;
	private int security;
	
	public BaseCommand () {
		subCmds = new HashMap<String, BaseCommand>();
		security = -1;
	}
	
	public void setSecurity (int security) {
		this.security = security;
	}
	
	public void setHelp (String help) {
		this.help = help;
	}
	
	public boolean haveAccess (Player player) {
		if (security < 0)
			return false;
		return player.getAccessLevel() >= security;
		
	}
	
	public BaseCommand getSubCommand (String name) {
		return subCmds.get(name);
	}
	
	public int getSecurity () {
		return security;
	}
	
	protected void showHelp (Player player) {
		PacketSendUtility.sendMessage(player, help);
	}
	
	protected void sendCommandMessage(Player player, String message) {
		PacketSendUtility.sendBrightYellowMessage(player, message);
	}
	
	public String getHelp () {
		return help;
	}
	
	protected int ParseInteger (String data) {
    	try {
    		return Integer.parseInt(data);
    	}
    	catch (NumberFormatException e) {
    		return 0;
    	}
    }
	
	protected long ParseLong (String data) {
		try {
			return Long.parseLong(data);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	protected float ParseFloat (String data) {
		try {
			return Float.parseFloat(data);
		}
		catch (NumberFormatException e) {
			return 0f;
		}
	}
	
	protected byte ParseByte (String data) {
		try {
			return Byte.parseByte(data);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	protected int GetItemIDFromLinkOrID (String data) {
		if (data.startsWith("[item:")) { 
			Pattern id = Pattern.compile("\\[item:(\\d{9})");
			Matcher result = id.matcher(data);
			
			if (result.find())
				return ParseInteger(result.group(1));
			else
				return 0;
		}
		else
			return ParseInteger(data);
	}
	
	protected int GetItemIDFromLink (String data0, String data1) {
		if (data0.equals("[item:")) {
			data0 += data1;
			Pattern id = Pattern.compile("(\\d{9})");
			Matcher result = id.matcher(data0);
			
			if (result.find())
				return ParseInteger(result.group(1));
			else
				return 0;
		}
		else
			return 0;
	}
	
	protected Player AutoTarget (Player admin, boolean notMySelf) {
		VisibleObject target = admin.getTarget();
        if (target == null && !notMySelf)
        	return admin;

        if (target instanceof Player)
        	return (Player)target;
        else if(!notMySelf)
        	return admin;
        else
        	return null;
	}
	
	protected Player AutoTarget (Player admin) {
		return AutoTarget(admin, false);
	}
	
	protected Creature getTarget (Player admin, boolean notMySelf) {
		VisibleObject target = admin.getTarget();
        if (target == null && !notMySelf)
        	return admin;

        if (target instanceof Creature)
        	return (Creature)target;
        else
        	return null;
	}
	
	protected Npc NpcTarget (Player admin) {
		VisibleObject creature = admin.getTarget();
		if (admin.getTarget() instanceof Npc)
			return (Npc) creature;
		return null;
	}
	
	protected String getEndString (String [] params, int index, int removeEndIndex) {
		String result = params[index];
		for (int i = index + 1; i < params.length - removeEndIndex; i++)
			result += " " + params[i];
		return result;
	}
	
	protected String getEndString (String [] params, int index) {
		return getEndString(params, index, 0);
	}
}
