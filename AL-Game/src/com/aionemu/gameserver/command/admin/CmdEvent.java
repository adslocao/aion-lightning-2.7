package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.cqfd.events.impl.CQFDEventPvPAera;
import com.aionemu.gameserver.cqfd.events.impl.CQFDEventTvT;
import com.aionemu.gameserver.eventengine.Event;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

public class CmdEvent extends BaseCommand {

	@Override
	public void execute(Player player, String... params) {
		if (params.length == 0) {
			HTMLService.showHTML(player, getHtmlSummary("============= Team ============   <br>"
												+ "addTeam \"teamId\"              <br>"
												+ "rmTeam \"teamId\"               <br>"
												+ "clearTeam                         <br>"
												+ "disableSoulSick                   <br>"
												+ "enableSoulSick                    <br>"
												+ "============= Player ============ <br>"
												+ "addPlayer \"playerName\" !\"teamId\"  <br>"
												+ "rmPlayer \"playerName\"             <br>"
												+ "setTeam \"playerName\" \"teamId\"     <br>"
												+ "res   !\"t\"     !\"teamId\" <br>"
												+ "respawn !\"teamId\" <br>"
												+ "============= Teleport ========== <br>"
												+ "teleportPlayer \"PlayerName\"       <br>"
												+ "teleportTeam !\"teamId\"            <br>"
												+ "start                       <br>"
												+ "kick \"playerName\"   <br>"
												+ "teleportBackTeam !\"teamId\"        <br>"
												+ "stop                   <br>"
												+ "============== Reward =========== <br>"
												+ "addReward \"itemId\" \"itemCount\" !\"rewardId\" <br>;"
												+ "clearReward !\"rewardId\"           <br>"
												+ "rewardPlayer \"playerName\" !\"rewardId\" <br>"
												+ "rewardTeam   \"teamId\"     !\"rewardId\" <br>"
												+ "rewardAll !\"rewardId\""
												+ "stat"));
			return;
		}
		try{
			
		String cmd = params[0];
		if(cmd.equalsIgnoreCase("stat")){
			PacketSendUtility.sendMessage(player, Event.instance.printEventStat());
			HTMLService.showHTML(player, getHtmlSummary(Event.instance.printHtmlEventStat()));
		}
		else if(cmd.equalsIgnoreCase("addTeam")){
			//addTeam <teamId>
			if(checkParam(params , "addTeam <teamId>" , 2, player))
				return;
			Event.instance.addTeam(player, Integer.parseInt(params[1]));
		}
		else if(cmd.equalsIgnoreCase("rmTeam")){
			//rmTeam <teamId>
			if(checkParam(params , "rmTeam <teamId>" , 2, player))
				return;
			Event.instance.removeTeam(Integer.parseInt(params[1]));
		}
		else if(cmd.equalsIgnoreCase("clearTeam")){
			//rmTeam <teamId>
			Event.instance.clearTeam();
		}
		else if(cmd.equalsIgnoreCase("addPlayer")){
			//addPlayer <playerName> !<teamId>
			if(params.length < 2){
				PacketSendUtility.sendMessage(player, "syntaxe : addPlayer <playerName> !<teamId>");
				return;
			}
			//checkPlayer
			Player target = getPlayer(params[1], player);
			if(target == null)
				return;
			
			else if(params.length == 2)
				Event.instance.addPlayer(target, player);
			else
				Event.instance.addPlayer(target, Integer.parseInt(params[2]), player);
		}
		else if(cmd.equalsIgnoreCase("rmPlayer")){
			//rmPlayer <playerName>
			if(checkParam(params , "rmPlayer <playerName>" , 2, player))
				return;
			
			//checkPlayer
			Player target = getPlayer(params[1], player);
			if(target == null)
				return;
			
			Event.instance.removePlayer(target);
		}
		else if(cmd.equalsIgnoreCase("setTeam")){
			//setTeam <playerName> <teamId> 
			if(checkParam(params , "setTeam <playerName> <teamId> " , 3, player))
				return;
			
			//checkPlayer
			Player target = getPlayer(params[1], player);
			if(target == null)
				return;
			
			Event.instance.setTeam(target, Integer.parseInt(params[2]));
		}
		
		else if(cmd.equalsIgnoreCase("disableSoulSick")){
			Event.instance.disableSoulSick(true);
		}
		else if(cmd.equalsIgnoreCase("enableSoulSick")){
			Event.instance.disableSoulSick(false);
		}
		
		else if(cmd.equalsIgnoreCase("teleportPlayer")){
			//teleportPlayer <PlayerName>
			if(checkParam(params , "teleportPlayer <PlayerName>" , 2, player))
				return;
			
			//checkPlayer
			Player target = getPlayer(params[1], player);
			if(target == null)
				return;
			
			Event.instance.teleportPlayer(target);
		}
		else if(cmd.equalsIgnoreCase("teleportTeam")){
			//teleportTeam !<teamId>
			if(params.length == 1)
				Event.instance.teleportTeam();
			else
				Event.instance.teleportTeam(Integer.parseInt(params[1]));
		}
		else if(cmd.equalsIgnoreCase("start")){
			//teleportAll
			Event.instance.start();
		}
		else if(cmd.equalsIgnoreCase("kick")){
			//teleportBackPlayer <playerName>
			if(checkParam(params , "kick <playerName>" , 2, player))
				return;
			
			//checkPlayer
			Player target = getPlayer(params[1], player);
			if(target == null)
				return;
			
			Event.instance.teleportBackPlayer(target);
		}
		else if(cmd.equalsIgnoreCase("teleportBackTeam")){
			//teleportBackTeam !<teamId>
			if(params.length == 1)
				Event.instance.teleportBackTeam();
			else
				Event.instance.teleportBackTeam(Integer.parseInt(params[1]));
		}
		else if(cmd.equalsIgnoreCase("stop")){
			//teleportBackAll
			Event.instance.stop();
		}
		else if(cmd.equalsIgnoreCase("addReward")){
			//addReward <itemId> <itemCount> !<rewardId>
			if(params.length < 3){
				PacketSendUtility.sendMessage(player, "syntaxe : addReward <itemId> <itemCount> !<rewardId>");
				return;
			}
			final int itemId = Integer.parseInt(params[1]);
			final int itemCount = Integer.parseInt(params[2]);
			
			if(params.length == 3)
				Event.instance.addRewardID(itemId, itemCount);
			else
				Event.instance.addRewardID(itemId, itemCount, Integer.parseInt(params[3]));
		}
		
		else if(cmd.equalsIgnoreCase("clearReward")){
			//clearReward !<rewardId>
			if(params.length == 1)
				Event.instance.clearReward();
			else
				Event.instance.clearReward(Integer.parseInt(params[1]));
		}
		else if(cmd.equalsIgnoreCase("rewardPlayer")){
			//rewardPlayer <playerName> !<rewardId>
			if(params.length < 2){
				PacketSendUtility.sendMessage(player, "syntaxe : rewardPlayer <playerName> !<rewardId>");
				return;
			}
			
			//checkPlayer
			Player target = getPlayer(params[1], player);
			if(target == null)
				return;
			
			if(params.length == 2)
				Event.instance.rewardPlayer(target);
			else
				Event.instance.rewardPlayer(target, Integer.parseInt(params[2]));
		}
		else if(cmd.equalsIgnoreCase("rewardTeam")){
			//rewardTeam   <teamId>     !<rewardId>
			if(params.length < 2){
				PacketSendUtility.sendMessage(player, "syntaxe : rewardTeam   <teamId>     !<rewardId>");
				return;
			}
			final int teamId = Integer.parseInt(params[1]);
			if(params.length == 2)
				Event.instance.rewardTeam(teamId);
			else
				Event.instance.rewardTeam(teamId, Integer.parseInt(params[2]));
		}
		else if(cmd.equalsIgnoreCase("respawn")){
			// respawn !<teamId>
			if(params.length == 1)
				Event.instance.respawn();
			else
				Event.instance.respawn(Integer.parseInt(params[1]));
		}
		else if(cmd.equalsIgnoreCase("res")){
			
			//res   !<t>     !<teamId>
	
			if(params.length == 1)
				Event.instance.resurrectAll(false);
			else if(params.length == 2){
				if(params[1].equalsIgnoreCase("t")){
					Event.instance.resurrectAll(true);
				}
				else
					Event.instance.resurrect(Integer.parseInt(params[1]));
			}
			else if(params.length == 2){
				boolean teleportAtSpawn = false;
				if(params[1].equalsIgnoreCase("t"))
					teleportAtSpawn = true;
				
				Event.instance.resurrect(Integer.parseInt(params[2]), teleportAtSpawn);
			}
				
		}
		else if(cmd.equalsIgnoreCase("rewardAll")){
			//rewardAll !<rewardId>
			if(params.length == 1)
				Event.instance.rewardAll();
			else
				Event.instance.rewardAll(Integer.parseInt(params[1]));
		}
		else if(cmd.equalsIgnoreCase("auto")){
			if(params.length == 1)
				PacketSendUtility.sendMessage(player,"//event auto <tvt|openpvp>");
			if(params[1].equalsIgnoreCase("tvt"))
				CQFDEventTvT.instance.start();
			else if(params[1].equalsIgnoreCase("openpvp"))
				CQFDEventPvPAera.openPvPAera();
		}
		else
			PacketSendUtility.sendMessage(player,"Unknow param : "+ cmd);
		}
		
		catch (Exception ex){
			PacketSendUtility.sendMessage(player,"params format error \n "+ex.getMessage() +" caused "+ ex.getCause());
			ex.printStackTrace();
			return;
		}
		PacketSendUtility.sendMessage(player,"[EventEngine] command success");

		
	}

	private static boolean checkParam(String[] params, String syntaxe, int nbParamNeeded, Player player){
		if(params.length == nbParamNeeded)
			return false;
		
		PacketSendUtility.sendMessage(player, "syntaxe : "+syntaxe);
		return true;
	}
	
	private static Player getPlayer(String playerName, Player admin){
		Player target = World.getInstance().findPlayer(Util.convertName(playerName));
		if(target == null)
			PacketSendUtility.sendMessage(admin, "player : "+ playerName + " not found");
		return target;
	}
	
	
	public String getHtmlSummary(String content) {
		String title = "Event Engine";
		
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
		
		html += content;
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
}
