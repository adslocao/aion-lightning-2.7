package com.aionemu.gameserver.cqfd.events;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.cqfd.events.tools.eventTools;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public class CQFDEventTeam {
	/*
	private ArrayList<Player> _registrationList = new ArrayList<Player>();
	private HashMap<Player, Location> _members = new HashMap<Player, Location>();
	private Location _teamSpawn;
	private final CQFDEvent _event;
	
	public CQFDEventTeam(CQFDEvent event, Location loc){
		_event = event;
		_teamSpawn = loc;
	}
	
	public void addMember(Player player){
		if(!_registrationList.contains(player))
			_registrationList.add(player);
	}
	
	public CQFDEvent getEvent(){
		return _event;
	}
	
	public boolean contain(Player player){
		return _members.containsKey(player);
	}
	public void removeMember(Player player){
		System.out.println("[removeMember]"+ player.getName());
		Location loc = _members.get(player);
		if(loc == null){
			System.out.println("[removeMember] loc null");
			return;
		}
		if(_event.getEventStat() != CQFDEventStat.IDLE){
			System.out.println("[removeMember] teleport");
			player.teleToLocation(loc, ReflectionManager.DEFAULT);
		}
		System.out.println("[removeMember] remove from team");
		_members.remove(player);
	}
	
	////////////////////////////////////////////
	////////////////////////////////////////////
	

	public void start(){
		for(Player player: _registrationList)
			if(player != null){
				_members.put(player , player.getLoc());
				player.teleToLocation(_teamSpawn, _event.getReflectionId());
			}
	}
	
	public void stop(){
		for(Player player : _members.keySet())
			if(player != null){
				System.out.println("[Event] remove : "+ player.getName());
				removeMember(player);
			}
	}
	
	////////////////////////////////////////////
	////////////////////////////////////////////

	public void giveReward(int[][] rewards){
		for(int i = 0 ; i < rewards.length; i++)
			for(Player player : _members.keySet())
				if(player != null){
					System.out.println("[Event] giveReward at "+ player.getName()+ " count "+ rewards[i][1]);
					player.getInventory().addItem(rewards[i][0], rewards[i][1]);
				}
		
	}
	
	
	

public class EventTeam{
*/
	public final ArrayList<CQFDEventPlayer> _players = new ArrayList<CQFDEventPlayer>();
	public CQFDEventPlayer _team = null;
	public static Logger _log = LoggerFactory.getLogger(CQFDEventTeam.class);
	
	public CQFDEventTeam(CQFDEventPlayer eventTeam){
		_team = eventTeam;
	}
	
	public ArrayList<CQFDEventPlayer> getEventPlayers(){
		return _players;
	}
	
	public CQFDEventPlayer getSpawn(){
		return _team;
	}
	
	public CQFDEventPlayer setSpawn(Player admin){
		return _team.setLoc(admin);
	}
	public void start(Boolean balance){
		for(CQFDEventPlayer pl : _players)
			if(!balance || deequip(pl))
				teleportPlayer(pl);
		eventTools.createLeague(this);
	}
	
	public void stop(Boolean balance){
		eventTools.teleportBackAllPlayer(this);
		/*
		for(CQFDEventPlayer ev : _players){
			eventTools.teleportBackPlayer(this, ev);
			if(balance)
					equip(ev);
		}*/
	}
	
	
	public void addPlayer(Player player){
		addPlayer(player, new CQFDEventPlayer(player, _team._event, _team._teamId));
	}
	
	public void addPlayer(Player player, CQFDEventPlayer ep){
		if(ep == null)
			addPlayer(player);
		else
			_players.add(ep);
	}
	
	
	public void removePlayer(CQFDEventPlayer ep){
		ep._player.setEventTeamId(-1);
		_players.remove(ep);
	}
	
	public CQFDEventPlayer removePlayer(Player pl){
		CQFDEventPlayer ep = get(pl);
		removePlayer(ep);
		return ep;
	}
	
	public boolean contain(Player player){
		for(CQFDEventPlayer ep : _players)
			if(ep._player == player)
				return true;
		return false;
	}
	
	public CQFDEventPlayer get(Player player){
		for(CQFDEventPlayer ep : _players)
			if(ep._player == player)
				return ep;
		return null;
	}
	
	
	@Deprecated
	public void deequipAll(){
		for(CQFDEventPlayer ep : _players)
			deequip(ep);
	}
	
	public boolean deequip(CQFDEventPlayer ep){
		return eventTools.deequip(this, ep);
	}
	
	public boolean equip(CQFDEventPlayer ev){
		if(ev == null){
			_log.info("equip EventPlayer null");
			return false;
		}
		ev.equip();
		return true;
	}
	
		
	public void teleportAllPlayer(){
		teleportAllPlayer(true);
	}
	
	public void teleportAllPlayer(boolean createLeague){
		if(createLeague)
			eventTools.createLeague(this);
		
		for(CQFDEventPlayer ep : _players)
			teleportPlayer(ep);
	}
	
	public void teleportPlayer(CQFDEventPlayer ep){
		ep._player.setEventTeamId(_team._teamId);
		eventTools.teleportPlayer(this, ep , _team);
	}
	
	public void teleportPlayer(Player player){
		for(CQFDEventPlayer ep : _players )
			if(ep._player == player)
				teleportPlayer(ep);
	}
	
	public void teleportBackAll(){
		for(CQFDEventPlayer ep :	_players)
			eventTools.teleportBackPlayer(this, ep);
	}
	
	public void teleportBackPlayer(Player player){
		for(CQFDEventPlayer ep : _players )
			if(ep._player == player)
				eventTools.teleportBackPlayer(this, ep);
	}

	
	public void rewardAllPlayer(){
		rewardAllPlayer(-1);
	}
	
	public void rewardPlayer(Player pl){
		rewardPlayer(pl, -1);
	}
	
	public void rewardAllPlayer(int rewardId){
			for(CQFDEventPlayer ep : _players)
				rewardPlayer(ep._player, rewardId);
	}
	
	public void rewardPlayer(Player pl , int rewardId){
		EventRewards rewards = _team._event.getRewards(rewardId);
		if(pl != null)
			if(rewards != null)
					for(CQFDReward	reward: rewards)
						eventTools.sendMail(pl, reward.getItemId(), reward.getItemCount());
	}
	public void resurrectAll(boolean teleportResurrectedAtSpawn){
		for (CQFDEventPlayer ep :_players){
			if(ep._player.getLifeStats().isAlreadyDead()){
				if(!teleportResurrectedAtSpawn)
					eventTools.resu(this, ep._player);
				else
					teleportPlayer(ep._player);
			}
		}
	}
}
