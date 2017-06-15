package com.aionemu.gameserver.eventengine;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Equipment;
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

public class EventTeam{
	private final ArrayList<EventPlayer> _players = new ArrayList<EventPlayer>();
	public final int teamId;
	public EventPlayer teamSpawn = null;
	public static Logger log = LoggerFactory.getLogger(EventTeam.class);
	
	public EventTeam(Player admin, int teamId){
		this.teamId = teamId;
		setSpawn(admin);
	}
	
	public ArrayList<EventPlayer> getEventPlayers(){
		return _players;
	}
	
	public void setSpawn(Player admin){
		teamSpawn = new EventPlayer(admin);
	}
	
	public EventPlayer getSpawn(){
		return teamSpawn;
	}
	
	public void start(Boolean balance){
		if(balance)
			for(EventPlayer pl : _players)
				if(!balance || deequip(pl))
					teleportPlayer(pl);
		createLeague();
	}
	
	public void stop(Boolean balance){
		if(balance)
			for(EventPlayer ev : _players){
				teleportBackPlayer(ev);
				if(balance)
					equip(ev);
			}
	}
	
	
	
	public void addPlayer(Player player){
		addPlayer(player, new EventPlayer(player));
	}
	
	public void addPlayer(Player player, EventPlayer ep){
		if(ep == null)
			addPlayer(player);
		else
			_players.add(ep);
	}
	
	
	public void removePlayer(EventPlayer ep){
		
		ep.player.setEventTeamId(-1);
		_players.remove(ep);
	}
	
	public EventPlayer removePlayer(Player pl){
		EventPlayer ep = get(pl);
		removePlayer(ep);
		return ep;
	}
	
	public boolean contain(Player player){
		for(EventPlayer ep : _players)
			if(ep.player == player)
				return true;
		return false;
	}
	
	public EventPlayer get(Player player){
		for(EventPlayer ep : _players)
			if(ep.player == player)
				return ep;
		return null;
	}
	/*************
	 * 
	 *	BALANCE
	 *
	 ************/
	
	/**
	 * use deequip
	 */
	@Deprecated
	public void deequipAll(){
		for(EventPlayer ep : _players)
			deequip(ep);
	}
	
	public boolean deequip(EventPlayer ep){
		final Equipment equipment = ep.player.getEquipment();
		final List<Item> equipedItems = equipment.getEquippedItems();
		if(equipedItems.size() > ep.player.getInventory().getFreeSlots())
		{
			PacketSendUtility.sendMessage(ep.player, "[EventEngine] Votre inventaire est plein veuillez le videz puis recontacter l'animateur");
			PacketSendUtility.sendMessage(teamSpawn.player, "[EventEngine] "+ep.player.getName() + " a son inventaire plein");
			_players.remove(ep.player);
			return false;
		}
		
		for(Item item : equipedItems){
			ep.seauvItem(item.getObjectId(), item.getEquipmentSlot());//store item
			equipment.unEquipItem(item.getObjectId(), item.getEquipmentSlot());//deequipe item
			
		}
		return true;
	}
	
	public boolean equip(EventPlayer ev){
		if(ev == null){
			log.info("equip EventPlayer null");
			return false;
		}
		ev.equip();
		return true;
	}
	
	/*************
	 * 
	 *	TELEPORT
	 *
	 ************/
		
	public void teleportAllPlayer(){
		teleportAllPlayer(true);
	}
	
	public void teleportAllPlayer(boolean createLeague){
		if(createLeague)
			createLeague();
		
		for(EventPlayer ep : _players)
			teleportPlayer(ep);
	}
	
	public void teleportPlayer(EventPlayer ep){
			teleportPlayer(ep , teamSpawn);
	}
	
	public void teleportPlayer(Player player){
		for(EventPlayer ep : _players )
			if(ep.player == player)
				teleportPlayer(ep , teamSpawn);
	}
	
	public void teleportPlayer(EventPlayer ep, EventPlayer loc){
		if(ep.player == null){
			_players.remove(ep);
			return;
		}
		if(ep.player.getLifeStats().isAlreadyDead())
			PlayerReviveService.revive(ep.player, 100, 100, false);
		
		ep.player.setEventTeamId(teamId);
		TeleportService.teleportTo(ep.player, loc.world, loc.instanceId, loc.x, loc.y,
				loc.z, loc.head, 0, true);
	}
	
	public void teleportBackAll(){
		for(EventPlayer ep :	_players)
			teleportBackPlayer(ep);
	}
	
	public void teleportBackPlayer(Player player){
		for(EventPlayer ep : _players )
			if(ep.player == player)
				teleportBackPlayer(ep);
	}
	
	public void teleportBackPlayer(EventPlayer ep){
		
		if(ep == null || ep.player == null)
			return;
		
		if(ep.player.getLifeStats().isAlreadyDead())
			PlayerReviveService.revive(ep.player, 100, 100, false);
		
		TeleportService.teleportTo(ep.player, ep.world, ep.instanceId, ep.x, ep.y,
				ep.z, ep.head, 0, true);
		expelFromGroup(ep.player);
		removePlayer(ep);
	}
	
	/*************
	 * 
	 *	REWARD
	 *
	 ************/
	
	public void rewardAllPlayer(){
		rewardAllPlayer(-1);
	}
	
	public void rewardPlayer(Player pl){
		rewardPlayer(pl, -1);
	}
	
	public void rewardAllPlayer(int rewardId){
			for(EventPlayer ep : _players)
				rewardPlayer(ep.player, rewardId);
	}
	
	public void rewardPlayer(Player pl , int rewardId){
		EventRewards rewards = Event.instance._rewards.get(rewardId);
		if(pl != null)
			if(rewards != null)
					for(EventReward	reward: rewards)
						sendMail(pl, reward.itemId, reward.itemCount);
	}
	
	
	public void resurrectAll(boolean teleportResurrectedAtSpawn){
		for (EventPlayer ep :_players){
			if(ep.player.getLifeStats().isAlreadyDead()){
				if(!teleportResurrectedAtSpawn)
					PlayerReviveService.revive(ep.player, 100, 100, false);
				else
					teleportPlayer(ep.player);
			}
		}
	}
	
	/*************
	 * 
	 *	GROUP
	 *
	 ************/
	
	
	public void createLeague(){
		try{
			if(_players.size() <= 1 && _players.size() > 192)
				return;
		
		
			int playerInAlliance = 0;
			Player allianceLeader = null;
			PlayerAlliance alliance = null;
		
			Player leagueLeader = null;
			League league = null;
		
			for(EventPlayer ep : _players){
				if(ep.player == null)
					return;
			
				playerInAlliance++;
			
				expelFromGroup(ep.player);
			
				if(playerInAlliance == 25)
					playerInAlliance = 1;
			
			//store leader
				if(playerInAlliance == 1){
				//if league not create and one alliance already create
					if(leagueLeader == null && allianceLeader != null)
					leagueLeader = allianceLeader;//save league leader
					allianceLeader = ep.player;
					continue;
				}
			
				else if(playerInAlliance == 2){
					alliance = PlayerAllianceService.createAlliance(allianceLeader, ep.player);
					PlayerAllianceService.addPlayer(alliance, ep.player);
					
					//if leagueLeader selected and league not create
					if(league != null)
						LeagueService.addAllianceToLeague(league, alliance);
					else if(leagueLeader != null)
						league = LeagueService.createLeague(leagueLeader, allianceLeader);	
				}
				else
					PlayerAllianceService.addPlayer(alliance, ep.player);
			}
		}
		catch(Exception ex){
			if(teamSpawn != null && teamSpawn.player != null )
				PacketSendUtility.sendMessage(teamSpawn.player, "Error on league creation");
			ex.printStackTrace();
		}
	}
	
	private final void expelFromGroup(Player pl){
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
			if(teamSpawn != null && teamSpawn.player != null && pl != null)
				PacketSendUtility.sendMessage(teamSpawn.player, "Error on group expel of : "+ pl.getName());
			ex.printStackTrace();
		}
	}
	
	
	private final static void sendMail(Player pl, int itemId, int itemCount){
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
}
