package com.aionemu.gameserver.eventengine;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

public class Event {
	
	static{
		instance = new Event();
	}
	
	public static Logger log = LoggerFactory.getLogger(Event.class);
	public static Event instance;
	
	public int eventStat = 0;
	public boolean disableSoulSick = false;


	public HashMap<Integer, EventTeam> _teams = new HashMap<Integer, EventTeam>();
	
	public HashMap<Integer, EventRewards> _rewards = new HashMap<Integer, EventRewards>();
	
	/***********
	 * 
	 *	TEAM 
	 *
	 **********/
	
	/**
	 * Create Team if not exist and set admin pos for TeamSpawn
	 * else if exist update TeamSpawn with admin pos 
	 * @param admin
	 * @param id
	 * @return
	 */
	public EventTeam addTeam(Player admin, int id){
		// if already contain edit spawn
		if(_teams.containsKey(id))
			_teams.get(id).setSpawn(admin);
		else{
			EventTeam team = new EventTeam(admin, id);
			_teams.put(id, team);
			return team;
		}
		return _teams.get(id);
	}
	
	/**
	 * teleportBack all player in this team
	 * and destroy team (teamspawn)
	 * @param id
	 */
	public void removeTeam(int id){
		EventTeam team = _teams.get(id);
		if(team != null)
			team.teleportBackAll();
		_teams.remove(id);
	}
	
	/**
	 * teleportBack all player in all team
	 * and destroy all teams (teamspawn)
	 */
	public void clearTeam(){
		for(EventTeam team :_teams.values())
			team.teleportBackAll();
		_teams.clear();
	}
	
	public void addPlayer(Player player, Player admin){
		addPlayer(player, 0, admin);
	}
	
	/**
	 * add Player to teamId 
	 * if already in other team set his team
	 * @param player
	 * @param teamId
	 * @param admin
	 */
	public void addPlayer(Player player, int teamId,Player admin){
		EventTeam team = _teams.get(teamId);
		if(eventStat != 0){
			Event.log.info("Event addPlayer can only be use for registration - Use : setTeam <Player> <Team>");
			return;
		}
		
		if(team == null){
			Event.log.info("Event addPlayer team not found : "+ teamId);
			team = addTeam(admin, teamId);
		}
		
		EventPlayer ep = null;
		for(EventTeam teamm: _teams.values()){
			if(teamm.contain(player)){
				ep = teamm.get(player);
				teamm.removePlayer(ep);
				break;
			}
		}
		
		team.addPlayer(player, ep);	
		Event.log.info("Event addPlayer team "+ teamId +" addNew player , team size "+ team.getEventPlayers().size());
	}
	
	/**
	 * remove a Player from event and teleport him back
	 * @param player
	 */
	public void removePlayer(Player player){
		if(eventStat != 0){
			Event.log.info("Event removePlayer can only be use for registration - Use : kick <Player>");
			return;
		}
		
		for(EventTeam team: _teams.values())
			team.removePlayer(player);
	}
	
	/**
	 * switch player to another team and update group
	 * 
	 * @param player
	 * @param id
	 */
	public void setTeam(Player player, int id){
		EventPlayer ep = null;
		for(EventTeam team: _teams.values())
			if(team.contain(player)){
				ep = team.removePlayer(player);
				break;
			}
		
		EventTeam team;
		
		if(_teams.containsKey(id)){
			team = _teams.get(id);
			team.addPlayer(player, ep);
		}
		else
		{
			team = new EventTeam(player, id);
			_teams.put(id, team);
			team.addPlayer(player, ep);
		}
		
		//if event start teleport this player on team spawn
		if(eventStat != 0){
			team.teleportPlayer(player);
			team.createLeague();
		}
	}
	
	/**************
	 * 
	 *	TELEPORT
	 *
	 **************/
	
	/**
	 * teleport Player in his team and update group
	 * @param player
	 */
	public void teleportPlayer(Player player){
		//seach on all team 
		Event.log.info("Event teleportPlayer : "+ _teams.size());
		for(EventTeam team : _teams.values())
			if(team.contain(player)){
				team.teleportPlayer(player);
				team.createLeague();
				break;
			}
	}
	
	public void teleportTeam(){
		teleportTeam(0);
	}
	
	public void teleportTeam(int teamId){
		Event.log.info("Event teleportTeam teams "+teamId+ " : "+ _teams.size());
		if(_teams.containsKey(teamId))
			_teams.get(teamId).teleportAllPlayer();
	}
	
	
	// start
	public void start(){
		Event.log.info("Event teleportAll teams nb : "+ _teams.size());
		//event started
		eventStat = 1;
		
		
		for(EventTeam team : _teams.values())
			team.start(true);
	}
	
	/*
	 * teleport back
	 */
	
	/**
	 * @param player
	 */
	public void teleportBackPlayer(Player player){
		//seach on all team 
		for(EventTeam team : _teams.values())
			if(team.contain(player)){
				team.teleportBackPlayer(player);
				team.createLeague();
				break;
			}
	}
	
	public void teleportBackTeam(){
		teleportBackTeam(0);
	}
	
	public void teleportBackTeam(int teamId){
		if(_teams.containsKey(teamId))
			_teams.get(teamId).teleportBackAll();
	}
	
	public void stop(){
		//event stop
		eventStat = 0;
		
		for(EventTeam team : _teams.values()){
			team.stop(true);
		}
	}
	
	/***********
	 * 
	 *	REWARD
	 *
	 **********/
	
	
	public void addRewardID(int itemId, int itemCount){
		addRewardID(itemId, itemCount, -1);
	}
	
	public void addRewardID(int itemId, int itemCount, int rewardId){
		EventRewards rewards = _rewards.get(rewardId);
		if(rewards == null){
			rewards = new EventRewards();
			_rewards.put(rewardId, rewards);
		}
		rewards.add(new EventReward(itemId, itemCount));
	}
	
	public void clearReward(){
		_rewards.clear();
	}
	
	public void clearReward(int rewardId){
		_rewards.remove(rewardId);
	}
	
	
	public void rewardPlayer(Player player){
		rewardPlayer(player, -1);
	}
	
	public void rewardPlayer(Player player , int rewardId){
		for(EventTeam team: _teams.values())
			if(team.contain(player))
				team.rewardPlayer(player, rewardId);
	}
	
	public void rewardTeam(int teamId){
		rewardTeam(teamId, -1);
	}
	
	public void rewardTeam(int teamId, int rewardId){
		if(_teams.containsKey(teamId))
			_teams.get(teamId).rewardAllPlayer(rewardId);
	}
	public void rewardAll(){
		rewardAll(-1);
	}
	
	public void rewardAll(int rewardId){
		for(EventTeam team : _teams.values())
			team.rewardAllPlayer(rewardId);
	}
	
	
	public void respawn(){
		for(EventTeam team : _teams.values())
			team.teleportAllPlayer(false);
	}
	
	public void respawn(int teamId){
		EventTeam team = _teams.get(teamId);
		if(team == null)
			return;
		else
			team.teleportAllPlayer(false);
	}
	
	public void resurrectAll(boolean teleportAtSpawn){
		for(EventTeam team : _teams.values())
			team.resurrectAll(teleportAtSpawn);
	}
	
	public void resurrect(int teamId){
		resurrect(teamId, false);
	}
	
	public void resurrect(int teamId, boolean teleportAtSpawn){
		EventTeam team = _teams.get(teamId);
		if(team == null)
			return;
		else
			team.resurrectAll(teleportAtSpawn);
			
	}
	/***********
	 * 
	 *	OTHER
	 *
	 **********/
	
	public boolean disableSoulSick() {
		return disableSoulSick;
	}

	public void disableSoulSick(boolean disableSoulSick) {
		this.disableSoulSick = disableSoulSick;
	}
	
	public String printEventStat(){
		String result = "";
		
		result += "============ STAT ============\n";
		result += "- stat : "+ eventStat+" \n";
		result += "============ TEAM ============\n";
		result += "- nb : "+ _teams.size()+" \n";
		for(Integer teamId :_teams.keySet()){
			EventPlayer loc = _teams.get(teamId).getSpawn();
			
			result += "-- TeamId  : "+" " + printLoc(loc, "Team"+teamId) +"\n";
			for(EventPlayer ep : _teams.get(teamId).getEventPlayers()){
				if(ep.player == null)
					result += "---- : player offline \n";
				else
					result += "---- : "+ ep.player.getName()+" \n";
			}
		}
		result += "=========== REWARD ===========\n";
		result += " coming soon\n";
		
		return result;
	}
	
	public String printHtmlEventStat(){
		String result = "";
		
		result += "============ STAT ============<br>";
		result += "- stat : "+ eventStat+" <br>";
		result += "============ TEAM ============<br>";
		result += "- nb : "+ _teams.size()+" <br>";
		for(Integer teamId :_teams.keySet()){
			result += "-- TeamId  : "+ teamId+" <br>";
			for(EventPlayer ep : _teams.get(teamId).getEventPlayers()){
				if(ep.player == null)
					result += "---- : player offline <br>";
				else
					result += "---- : "+ ep.player.getName()+" <br>";
			}
		}
		result += "=========== REWARD ===========<br>";
		for(Integer rewardId :_rewards.keySet()){
			result += "-- rewardId  : "+ rewardId+" <br>";
			for(EventReward reward : _rewards.get(rewardId)){
				ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(reward.itemId);
				if(itemTemplate == null)
					result += "---- : invalid reward <br>";
				else
					result += "---- : "+ itemTemplate.getName() +" x"+reward.itemCount+" <br>";
			}
		}
		return result;
	}
	
	
	public String printLoc(EventPlayer loc, String name){
		return "[pos:"+name+ ";"+loc.world+" "+loc.x+" "+loc.y+" "+loc.z+" 0]";
	}
}
