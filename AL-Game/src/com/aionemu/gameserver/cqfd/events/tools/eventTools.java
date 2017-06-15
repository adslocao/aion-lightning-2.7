package com.aionemu.gameserver.cqfd.events.tools;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventPlayer;
import com.aionemu.gameserver.cqfd.events.CQFDEventTeam;
import com.aionemu.gameserver.cqfd.events.CQFDReward;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.team2.league.LeagueService;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.SystemMailService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class eventTools {

	public static Logger log = LoggerFactory.getLogger(eventTools.class);
	
	public static void announce(Collection<CQFDEventTeam> teams, String message){
		for(CQFDEventTeam et : teams)
			for(CQFDEventPlayer ep : et.getEventPlayers())
				if(ep._player != null)
					//ep._player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "", message));
					PacketSendUtility.sendBrightYellowMessageOnCenter(ep._player, message);
	}
	
	public static void announceStart(CQFDEvent event){
		for (Player player : World.getInstance().getAllPlayers()) 
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[Event] " +event.getEventName()+" started");
	}
	
	public static void announceEnd(CQFDEvent event){
		for (Player player : World.getInstance().getAllPlayers()) 
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[Event] " +event.getEventName()+" ended");
	}
	
	public static void announceRegisterEnd(CQFDEvent event){
		for (Player player : World.getInstance().getAllPlayers()) 
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, "[Event] " +event.getEventName()+" register ended");
	}
	
	/*
	public static void teleportPlayer(CQFDEventTeam team , CQFDEventPlayer ep, CQFDEventPlayer loc){
		if(ep._player == null){
			team._players.remove(ep);
			return;
		}
		if(ep._player.getLifeStats().isAlreadyDead())
			resu(team, ep._player);
			
		
		//ep._player.setEventTeamId(_team._teamId);
		teleportPlayer(team, ep, loc);
	}
	*/
	
	
	
	public static boolean deequip(CQFDEventTeam team , CQFDEventPlayer ep){
		//final ItemInstance[] equipments = ep._player.getInventory().getPaperdollItems();
		//for(ItemInstance item : equipments)
		//	ep.unequip(item);//store item
		return true;
	}
	
	public static void teleportBackPlayer(CQFDEventTeam team , CQFDEventPlayer ep){
		if(ep == null || ep._player == null)
			return;
		
		if(ep._player.getLifeStats().isAlreadyDead())
			resu(team, ep._player);
		
		teleportPlayer(team, ep , ep);
		expelFromGroup(team, ep._player);
		team.removePlayer(ep);
	}
	
	public static void teleportBackAllPlayer(CQFDEventTeam team){
		for(CQFDEventPlayer ep : team._players)
		{
			if(ep == null || ep._player == null)
				continue;
			if(ep._player.getLifeStats().isAlreadyDead())
				resu(team, ep._player);
			
			teleportPlayer(team, ep , ep);
			expelFromGroup(team, ep._player);
		}
		team._players.clear();
	}
	
	public static void resu(CQFDEventTeam team, CQFDEventPlayer ep){
		resu(team, ep._player);
	}
	
	public static void resu(CQFDEventTeam team, Creature creature)
	{
		if(creature == null )
			return;
		
		if(!creature.getLifeStats().isAlreadyDead())
			return;

		if(creature instanceof Player)
		{
			PlayerReviveService.revive((Player)creature, 100, 100, false);

		}
		else{
			//ReviveService
		}
		
		creature.getLifeStats().restoreHp();
		creature.getLifeStats().restoreMp();
	}
	
	/**
	 * L2
	 * @param team
	 */
	/*
	public static void createLeague(CQFDEventTeam team){
		try{
			
			//if(_players.size() <= 1 && _players.size() > 192) //LIMITE?
			//	return;
		
		
			int playerInGroup = 0;
			Player GroupLeader = null;
			Party Group = null;
			
			Player ChannelCoLeader = null;
			CommandChannel ChannelCo = null;
		
			for(CQFDEventPlayer ep : team._players){
				if(ep._player == null)
					return;
			
				playerInGroup++;
			
				expelFromGroup(team, ep._player);
			
				if(playerInGroup == 9)
					playerInGroup = 1;
			
			//store leader
				if(playerInGroup == 1){
				//if league not create and one alliance already create
					if(ChannelCoLeader == null && GroupLeader != null)
						ChannelCoLeader = GroupLeader;//save league leader
					GroupLeader = ep._player;
					continue;
				}
			
				else if(playerInGroup == 2){//create party
					GroupLeader.setParty(Group = new Party(GroupLeader, 0));
					//if ChannelCoLeader selected and ChannelCo not create
					if(ChannelCo != null)
						Group.setCommandChannel(ChannelCo);
					else if(ChannelCoLeader != null)
						Group.setCommandChannel(ChannelCo = new CommandChannel(GroupLeader));	
				}
				else
					Group.addPartyMember(ep._player);
			}
		}
		catch(Exception ex){
			if(team._team != null && team._team._player != null )
				team._team._player.sendMessage("Error on league creation");
			ex.printStackTrace();
		}
	}
	
	public static final void expelFromGroup(CQFDEventTeam team,  Player pl){
		try{
			
			if(pl.getParty() != null)
				if(pl.getParty().getMemberCount() == 2)
					pl.getParty().dissolveParty();
				else
					pl.getParty().removePartyMember(pl, true);

		}
		catch(Exception ex){
			if(team._team != null && team._team._player != null && pl != null)
				pl.sendMessage("Error on group expel of : "+ pl.getName());
			ex.printStackTrace();
		}
	}*/
	
	/**
	 * AION
	 */
	public static void createLeague(CQFDEventTeam team){
		try{
			if(team._players.size() <= 1 && team._players.size() > 192)
				return;
		
		
			int playerInAlliance = 0;
			Player allianceLeader = null;
			PlayerAlliance alliance = null;
		
			Player leagueLeader = null;
			League league = null;
		
			for(CQFDEventPlayer ep : team._players){
				if(ep._player == null)
					return;
			
				playerInAlliance++;
			
				expelFromGroup(team, ep._player);
			
				if(playerInAlliance == 25)
					playerInAlliance = 1;
			
			//store leader
				if(playerInAlliance == 1){
				//if league not create and one alliance already create
					if(leagueLeader == null && allianceLeader != null)
					leagueLeader = allianceLeader;//save league leader
					allianceLeader = ep._player;
					continue;
				}
			
				else if(playerInAlliance == 2){
					alliance = PlayerAllianceService.createAlliance(allianceLeader, ep._player);
					PlayerAllianceService.addPlayer(alliance, ep._player);
					
					//if leagueLeader selected and league not create
					if(league != null)
						LeagueService.addAllianceToLeague(league, alliance);
					else if(leagueLeader != null)
						league = LeagueService.createLeague(leagueLeader, allianceLeader);	
				}
				else
					PlayerAllianceService.addPlayer(alliance, ep._player);
			}
		}
		catch(Exception ex){
			if(team._team != null && team._team._player != null )
				PacketSendUtility.sendMessage(team._team._player, "Error on league creation");
			ex.printStackTrace();
		}
	}
	
	private final static void expelFromGroup(CQFDEventTeam team, Player pl){
		try{
			//expel player from old group
			PlayerGroup oldGroup = pl.getPlayerGroup2();
			if(oldGroup != null)
				PlayerGroupService.removePlayer(pl);
			//expel player from old alliance
			PlayerAlliance oldAlliance = pl.getPlayerAlliance2();
			if(oldAlliance != null)
				PlayerAllianceService.removePlayer(pl);
		}
		catch(Exception ex){
			if(team._team != null && team._team._player != null && pl != null)
				PacketSendUtility.sendMessage(team._team._player, "Error on group expel of : "+ pl.getName());
			ex.printStackTrace();
		}
	}

	

	/** AION
	 * 
	 * */
	public final static void sendMail(Player pl, int itemId, int itemCount){
		
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (itemId != 0) {
			if (itemTemplate == null) {
				log.warn("[EventEngine] invalid itemId");
				return;
			}
			int maxStackCount = (int) itemTemplate.getMaxStackCount();
			if (itemCount > maxStackCount && maxStackCount != 0){
				
				int nbMail = itemCount/maxStackCount;
				int res = itemCount%maxStackCount;
				
				while(nbMail > 0){
					SystemMailService.getInstance().sendMail("EventEngine", pl.getName(), "System Mail", " ", itemId, maxStackCount, 0,
							false);
					nbMail--;
				}
				if(res != 0){
					SystemMailService.getInstance().sendMail("EventEngine", pl.getName(), "System Mail", " ", itemId, res, 0,
							false);
				}
			}
			else{
				SystemMailService.getInstance().sendMail("EventEngine", pl.getName(), "System Mail", " ", itemId, itemCount, 0,
						false);
			}
		}
		log.warn("[EventEngine] sendReward give "+ itemTemplate.getName() + " x"+itemCount+" to "+pl.getName());
	}
	
	
	
	/*
	private static void teleportTo(Player player, CQFDEventPlayer loc)
	{
		player.sendMessage("EventEngine is teleporting you.");
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		player.teleToLocation(new Location(loc._x, loc._y, loc._z, loc._head), loc._instanceId);
	}
	*/
	
	public static void teleportPlayer(CQFDEventTeam team, CQFDEventPlayer ep, CQFDEventPlayer loc){
		if(ep._player == null){
			team._players.remove(ep);
			return;
		}
		if(ep._player.getLifeStats().isAlreadyDead())
			PlayerReviveService.revive(ep._player, 100, 100, false);
		
		ep._player.setEventTeamId(team._team._teamId);
		TeleportService.teleportTo(ep._player, loc._world, loc._instanceId, loc._x, loc._y,
				loc._z, loc._head, 0, true);
	}
	///////////////////////////////////////////////////////////////
	//
	//					HTML
	//
	/////////////////////////////////////////////////////////////
	
	public String printEventStat(CQFDEvent event){
		String result = "";
		
		result += "============ STAT ============\n";
		result += "- stat : "+ event._eventStat.name()+" \n";
		result += "============ TEAM ============\n";
		result += "- nb : "+ event._teams.size()+" \n";
		for(Integer teamId :event._teams.keySet()){
			CQFDEventPlayer loc = event._teams.get(teamId).getSpawn();
			
			result += "-- TeamId  : "+" " + printLoc(loc, "Team"+teamId) +"\n";
			for(CQFDEventPlayer ep : event._teams.get(teamId).getEventPlayers()){
				if(ep._player == null)
					result += "---- : player offline \n";
				else
					result += "---- : "+ ep._player.getName()+" \n";
			}
		}
		result += "=========== REWARD ===========\n";
		result += " coming soon\n";
		
		return result;
	}
	
	public String printHtmlEventStat(CQFDEvent event){
		String result = "";
		
		result += "============ STAT ============<br>";
		result += "- stat : "+ event._eventStat.name()+" <br>";
		result += "============ TEAM ============<br>";
		result += "- nb : "+ event._teams.size()+" <br>";
		for(Integer teamId :event._teams.keySet()){
			result += "-- TeamId  : "+ teamId+" <br>";
			for(CQFDEventPlayer ep : event._teams.get(teamId).getEventPlayers()){
				if(ep._player == null)
					result += "---- : player offline <br>";
				else
					result += "---- : "+ ep._player.getName()+" <br>";
			}
		}
		result += "=========== REWARD ===========<br>";
		for(Integer rewardId :event._rewards.keySet()){
			result += "-- rewardId  : "+ rewardId+" <br>";
			for(CQFDReward reward : event._rewards.get(rewardId)){
				//ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(reward.getItemId()); // L2
				ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(reward.getItemId()); //AION
				if(itemTemplate == null)
					result += "---- : invalid reward <br>";
				else
					result += "---- : "+ itemTemplate.getName() +" x"+reward.getItemCount()+" <br>";
			}
		}
		return result;
	}
	
	
	/**
	 * @param loc
	 * @param name
	 * @return
	 */
	
	public String printLoc(CQFDEventPlayer loc, String name){
		return "[pos:"+name+ ";"+loc._world+" "+loc._x+" "+loc._y+" "+loc._z+" 0]";
	}
	
}
