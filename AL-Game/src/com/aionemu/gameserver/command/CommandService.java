package com.aionemu.gameserver.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.command.admin.*;
import com.aionemu.gameserver.command.admin.CmdTvt2;
import com.aionemu.gameserver.command.player.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;

public class CommandService {
	private static final Logger log = LoggerFactory.getLogger("CommandService.class");
	private static final CommandService instance = new CommandService();
	
	private Map<String, BaseCommand> commands;
	private Map<String, BaseCommand> commandAlias;
	
	public CommandService () {
		commands = new HashMap<String, BaseCommand>();
		commandAlias = new HashMap<String, BaseCommand>();
		
		/* ### ADMIN ### */
		commands.put("add", new CmdAdd());
		commands.put("addcube", new CmdAddCube());
		commands.put("adddrop", new CmdAddDrop());
		commands.put("addemotion", new CmdAddEmotion());
		commands.put("addexp", new CmdAddExp());
		commands.put("addset", new CmdAddSet());
		commands.put("addskill", new CmdAddSkill());
		commands.put("addtitle", new CmdAddTitle());
		commands.put("admin", new CmdAdmin());
		commands.put("ai2", new CmdAi2Command());
		commands.put("announce", new CmdAnnounce());
		commands.put("announcement", new CmdAnnouncement());
		commands.put("appearance", new CmdAppearance());
		commands.put("assault", new CmdAssault());	
		commands.put("ban", new CmdBan());
		commands.put("bk", new CmdBk());
		commands.put("changerace", new CmdChangeRace());
		commands.put("channel", new CmdChannel());
		commands.put("clear", new CmdClear());
		commands.put("configure", new CmdConfigure());
		commands.put("cooldown", new CmdCooldown());
		commands.put("cometome", new CmdCometome());
		commands.put("delete", new CmdDelete());
		commands.put("delskill", new CmdDelSkill());
		commands.put("dispel", new CmdDispel());
		commands.put("dropinfo", new CmdDropInfo());
		commands.put("dye", new CmdDye());
		commands.put("enemy", new CmdEnemy());
		commands.put("equip", new CmdEquip());
		commands.put("energybuff", new CmdEnergyBuff());
		commands.put("fsc", new CmdFsc());
		commands.put("gag", new CmdGag());
		commands.put("gm", new CmdGm());
		commands.put("goto", new CmdGoTo());
		commands.put("gps", new CmdGps());
		commands.put("grouptome", new CmdGroupToMe());
		commands.put("heal", new CmdHeal());
		commands.put("html", new CmdHtml());
		commands.put("info", new CmdInfo());
		commands.put("invis", new CmdInvis());
		commands.put("invul", new CmdInvul());
		commands.put("kick", new CmdKick());
		commands.put("kill", new CmdKill());
		commands.put("kinah", new CmdKinah());
		commands.put("marry", new CmdMarry());
		commands.put("missyou", new CmdMissyou());
		commands.put("morph", new CmdMorph());
		commands.put("motion", new CmdMotion());
		commands.put("moveplayertoplayer", new CmdMovePlayerToPlayer());
		commands.put("moveto", new CmdMoveTo());
		commands.put("movetome", new CmdMoveToMe());
		commands.put("movetomeall", new CmdMoveToMeAll());
		commands.put("movetonpc", new CmdMoveToNpc());
		commands.put("movetoobject", new CmdMoveToObject());
		commands.put("movetoplayer", new CmdMoveToPlayer());
		commands.put("movie", new CmdMovie());
		commands.put("neutral", new CmdNeutral());
		commands.put("notice", new CmdNotice());
		commands.put("npcskill", new CmdNpcSkill());
		commands.put("legion", new CmdLegionCommand());
		commands.put("online", new CmdOnline());
		commands.put("passkeyreset", new CmdPasskeyReset());
		commands.put("petitions", new CmdPetitions());
		commands.put("playerinfo", new CmdPlayerInfo());
		commands.put("pet", new CmdPet());
		commands.put("powerup", new CmdPowerUp());
		commands.put("promote", new CmdPromote());
		commands.put("quest", new CmdQuest());
		commands.put("ranking", new CmdRanking());
		commands.put("raw", new CmdRaw());
		commands.put("reload", new CmdReload());
		commands.put("reloadspawn", new CmdReloadSpawn());
		commands.put("remove", new CmdRemove());
		commands.put("removecd", new CmdRemoveCd());
		commands.put("rename", new CmdRename());
		commands.put("res", new CmdRes());
		commands.put("revenge", new CmdRevenge());
		commands.put("revoke", new CmdRevoke());
		commands.put("ring", new CmdRing());
		commands.put("rprison", new CmdRPrison());
		commands.put("say", new CmdSay());
		commands.put("see", new CmdSee());
		commands.put("send", new CmdSend());
		commands.put("set", new CmdSet());
		commands.put("setlife", new CmdSetLife());
		commands.put("setrace", new CmdSetRace());
		commands.put("siege", new CmdSiege2());
		commands.put("spawnassemblednpc", new CmdSpawnAssembledNpc());
		commands.put("spawn", new CmdSpawnNpc());
		commands.put("spawnupdate", new CmdSpawnUpdate());
		commands.put("speed", new CmdSpeed());
		commands.put("sprison", new CmdSPrison());
		commands.put("stat", new CmdStat());
		commands.put("state", new CmdState());
		commands.put("status", new CmdStatus());
		commands.put("stigma", new CmdStigma());
		commands.put("sysmail", new CmdSysMail());
		commands.put("system", new CmdSystem());
		commands.put("tele", new CmdTele());
		commands.put("teleportation", new CmdTeleportation());
		commands.put("time", new CmdTime());
		commands.put("tp", new CmdTp());
		commands.put("tvt2", new CmdTvt2());
		commands.put("unban", new CmdUnBan());
		commands.put("unbanchar", new CmdUnBanChar());
		commands.put("unbanip", new CmdUnBanIp());
		commands.put("unbanmac", new CmdUnBanMac());
		commands.put("ungag", new CmdUnGag());
		commands.put("unstuck", new CmdUnstuck());
		commands.put("useskill", new CmdUseSkill());
		commands.put("warp", new CmdWarp());
		commands.put("wc", new CmdWc());
		commands.put("weather", new CmdWeather());
		commands.put("zone", new CmdZone());
		commands.put("ms", new CmdMultipleSpawn());
		/* ### PLAYER ### */
		commands.put("answer", new CmdAnswer());
		commands.put("buff", new CmdBuff());
		commands.put("commands", new CmdCommands());
		commands.put("delitem", new CmdDelItem());
		commands.put("enchant", new CmdEnchant());
		commands.put("f", new CmdF());
		commands.put("giveme", new CmdGiveMe());
		commands.put("gettoll", new CmdGetShopPoint());
		commands.put("givemissingskills", new CmdGiveMissingSkills());
		commands.put("gmlist", new CmdGmList());
		commands.put("help", new CmdHelp());
		commands.put("id", new CmdId());
		commands.put("infos", new CmdInfos());
		commands.put("locale", new CmdLocale());
		commands.put("mcheck", new CmdMcheck());
		commands.put("noexp", new CmdNoexp());
		commands.put("preview", new CmdPreview());
		commands.put("questauto", new CmdQuestauto());
		commands.put("questrestart", new CmdQuestrestart());
		commands.put("remodel", new CmdRemodel());
		commands.put("reskin", new CmdReskin());
		commands.put("shop", new CmdShop()); // Warning! Requires additional table. See sql/updates/addWebShop.sql
		commands.put("toll", new CmdToll());
//		commands.put("Tvt2", new CmdTvt2());
		commands.put("find", new CmdFind());
		
		//EventEngine
		
		commands.put("event", new CmdEvent());
		commands.put("join", new CmdJoin());
		//commands.put("male", new CmdMale());
		commands.put("rank", new CmdCustomRank());
		loadSecurity();
		loadAlias();
	}
	
	public void loadSecurity () {
		Connection con = null;
		try {
	    	con = DatabaseFactory.getConnection();
	        PreparedStatement stmt = con.prepareStatement("SELECT * FROM command ORDER BY name");

	        ResultSet resultSet = stmt.executeQuery();
	        while (resultSet.next()) {
	        	String[] params = resultSet.getString("name").split(" ");
	    		BaseCommand load = commands.get(params[0]);
	    		for (int i = 1; i < params.length; i++) {
	    			if (load != null) {
	    				BaseCommand subLoad = load.getSubCommand(params[i].toLowerCase());
	    				if (subLoad != null)
	    					load = subLoad;
	    				else
	    					break;
	    			}
	    			else
	    				break;
	    		}
	        	if (load != null) {
	        		load.setSecurity(resultSet.getInt("security"));
	        		load.setHelp(resultSet.getString("help"));
	        	}
	        	else
	        		log.error("Command " + resultSet.getString("name") + " Introuvable.");
	        }
	        
	        resultSet.close();
	        stmt.close();
	    }
	    catch (SQLException e) { 
	    	log.error("Load 'command' Fail", e);
	    }
	    finally {
	    	DatabaseFactory.close(con);
	    }
	}
	
	public void loadAlias() {
		Connection con = null;
		try {
	    	con = DatabaseFactory.getConnection();
	        PreparedStatement stmt = con.prepareStatement("SELECT * FROM command_alias ORDER BY alias");

	        ResultSet resultSet = stmt.executeQuery();
	        while (resultSet.next()) {
	        	String[] params = resultSet.getString("name").split(" ");
	    		BaseCommand load = commands.get(params[0]);
	    		for (int i = 1; i < params.length; i++) {
	    			if (load != null) {
	    				BaseCommand subLoad = load.getSubCommand(params[i].toLowerCase());
	    				if (subLoad != null)
	    					load = subLoad;
	    				else
	    					break;
	    			}
	    			else
	    				break;
	    		}
	        	if (load != null)
	        		commandAlias.put(resultSet.getString("alias"), load);
	        	else
	        		log.error("Command (alias) " + resultSet.getString("name") + " Introuvable.");
	        }
	        
	        resultSet.close();
	        stmt.close();
	    }
	    catch (SQLException e) { 
	    	log.error("Load 'command' Fail", e);
	    }
	    finally {
	    	DatabaseFactory.close(con);
	    }
	}
	
	public void process (Player player, String command, boolean admin) {
		if (player.isInPrison()) {
			PacketSendUtility.sendMessage(player, "Vous etes en prison.");
			return ;
		}
		String[] params = command.split(" ");
		BaseCommand myCommand = commands.get(params[0]);
		if (myCommand == null)
			myCommand = commandAlias.get(params[0]);
		
		int needToRemove = 0;
		
		for (int i = 1; i < params.length; i++) {
			if (myCommand != null) {
				BaseCommand subCmd = myCommand.getSubCommand(params[i].toLowerCase());
				if (subCmd != null) {
					myCommand = subCmd;
					needToRemove = i;
				}
				else
					break;
			}
			else
				break;
		}
		
		if (myCommand != null) {
			if (player.getAccessLevel() == 0 && admin) {
				PacketSendUtility.sendMessage(player, "commande introuvable.");
				return ;
			}
			if (myCommand.haveAccess(player)) {
				if (LoggingConfig.LOG_GMAUDIT)
					AuditLogger.auditCmd(player, player.getTarget(), command);
				
				myCommand.execute(player, RemoveFirstsParam(params, needToRemove));
			}
			else
				PacketSendUtility.sendMessage(player, "Pas de permission");
		}
		else
			PacketSendUtility.sendMessage(player, "commande introuvable.");
	}
	
	public String getCommandList(Player player) {
		String title = "";
		String keyCmd = ".";
		if (player.getAccessLevel() == 0) {
			title = "Commands Joueur";
			keyCmd = ".";
		}
		else {
			title = "Commands Admin";
			keyCmd = "//";
		}
		
		String html = "<poll>" +
		"<poll_introduction>" +
		"	<![CDATA[<font color='4CB1E5'>" + title + "</font>]]>" +
		"</poll_introduction>" +
		"<poll_title>" +
		"	<font color='ffc519'></font>" +
		"</poll_title>" +
		"<questions>" +
		"	<question>" +
		"		<title>" +
		"			<![CDATA[";
		
		for (Entry<String, BaseCommand> cmd : commands.entrySet()) {
			String commandName = cmd.getKey();
			BaseCommand command = cmd.getValue();
			
			if (command.getSecurity() <= player.getAccessLevel())
				html += keyCmd + commandName + ":" + command.getHelp() + "<br>";
		}
		
		html += "		]]>" +
		"			</title>" +
		"			<select>" +
		"				<input type='radio'>Close the window</input>" +
		"			</select>" +
		"		</question>" +
		"	</questions>" +
		"</poll>";
		
		return html;
	}
	
	public String getCommandListMsg(Player player) {
		String title = "";
		String keyCmd = ".";
		if (player.getAccessLevel() == 0) {
			title = "--Commandes Joueur--";
			keyCmd = ".";
		}
		else {
			title = "--Commandes Admin---";
			keyCmd = "//";
		}
		
		String msg = "--------------------\n";
		msg += title + "\n";
		msg += "--------------------\n";
		
		for (Entry<String, BaseCommand> cmd : commands.entrySet()) {
			String commandName = cmd.getKey();
			BaseCommand command = cmd.getValue();
			
			if (command.getSecurity() <= player.getAccessLevel())
				msg += keyCmd + commandName + " : " + command.getHelp() + "\n";
		}
		msg += "--------------------\n";
		return msg;
	}
	
	public String getHelpFromCommand(Player player, String [] params) {
		BaseCommand myCommand = commands.get(params[0]);
		
		for (int i = 1; i < params.length; i++) {
			if (myCommand != null) {
				BaseCommand subCmd = myCommand.getSubCommand(params[i].toLowerCase());
				if (subCmd != null)
					myCommand = subCmd;
				else
					break;
			}
			else
				break;
		}
		
		if (myCommand != null) {
			if (myCommand.haveAccess(player))
				return myCommand.getHelp();
			else
				return "Commande introuvable.";
		}
		else
			return "Commande introuvable.";
	}
	
	public static CommandService getInstance () {
		return instance;
	}
	
	private static String [] RemoveFirstsParam (String [] params, int needToRemove) {
		int newMax = params.length - 1 - needToRemove;
		String [] newParams = new String[newMax];
		
		for (int i = 0; i < newMax; i++)
			newParams[i] = params[i+1+needToRemove];
		
		return newParams;
	}
}
