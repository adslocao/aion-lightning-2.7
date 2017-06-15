package com.aionemu.gameserver.cqfd.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.cqfd.Lisener.CQFDListenerType;
import com.aionemu.gameserver.cqfd.events.step.CQFDCOUNTERTYPE;
import com.aionemu.gameserver.cqfd.events.step.CQFDORDER;
import com.aionemu.gameserver.cqfd.events.step.CQFDTeamCounter;
import com.aionemu.gameserver.cqfd.events.task.CQFDEventTask;
import com.aionemu.gameserver.cqfd.events.task.impl.CQFDEventTaskRegister;
import com.aionemu.gameserver.cqfd.events.task.impl.CQFDEventTimeOutTask;
import com.aionemu.gameserver.cqfd.events.tools.eventTools;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class CQFDEvent {
	private static final Logger _log = LoggerFactory.getLogger(CQFDEvent.class);
	
	public static final ArrayList<CQFDEvent> _events = new ArrayList<CQFDEvent>();
	public static final CQFDEvent _instance = new CQFDEvent();
	
	public final int _teamId;
	
	public CQFDEventStat _eventStat = CQFDEventStat.IDLE;
	//private Reflection _reflection = ReflectionManager.EVENT;
	
	public HashMap<Integer, EventRewards> _rewards = new HashMap<Integer, EventRewards>();
	public HashMap<Integer, CQFDEventTeam> _teams = new HashMap<Integer, CQFDEventTeam>();

	public ArrayList<CQFDEventPlayer> _spawns = new ArrayList<CQFDEventPlayer>();

	public int _registerDelay = 0;
	
	private HashMap<CQFDCOUNTERTYPE, ArrayList<CQFDTeamCounter>> _counters = new HashMap<CQFDCOUNTERTYPE, ArrayList<CQFDTeamCounter>>();

	private int _stepId = 0;
	private int _maxEventTime = -1;
	public boolean disableSoulSick = false;
	
	
	private ScheduledFuture<?> _currentScheduledTask = null;
	private ScheduledFuture<?> _timeOutTask = null;
	

	private HashMap<Integer, ArrayList<CQFDEventTask>> _taskList = new HashMap<Integer, ArrayList<CQFDEventTask>>();
	private ArrayList<ScheduledFuture<?>> _currentTasks = new ArrayList<ScheduledFuture<?>>();
	
	//manual event
	public CQFDEvent(){
		_teamId = _events.size();
		_events.add(this);
	}
	
	//scripted event
	public CQFDEvent(ArrayList<CQFDEventPlayer> spawns , int[][][] rewards, int registerDelay, int eventTime){
		_maxEventTime = eventTime;
		_teamId = _events.size();
		_events.add(this);
		_spawns = spawns;
		_registerDelay = registerDelay;
		init();
		initVars();
		loadRewards(rewards);
	}
	
	
	public void initVars(){
		for(CQFDCOUNTERTYPE type : CQFDCOUNTERTYPE.values())
			_counters.put(type, new ArrayList<CQFDTeamCounter>());
	}
	
	public void setEventStat(CQFDEventStat stat){
		_eventStat = stat;
	}
	
	public void setMaxEventTime(int maxtime){
		_maxEventTime = maxtime;
	}
	
	public CQFDEventStat getEventStat(){
		return _eventStat;
	}
	
	/*
	public int getReflectionId(){
		return _reflection.getId();
	}
	*/
	public HashMap<CQFDCOUNTERTYPE, ArrayList<CQFDTeamCounter>> getCounts(){
		return _counters;
	}
	
	public int getStepId(){
		return _stepId;
	}
	
	public void incStepId(){
		_log.info("-- incStepId : "+ (_stepId+1)+ ""+this.getEventName());
		setStepId(_stepId+1);
	}
	
	public void setStepId(int stepId){
		if(stepId == 0)
			return;
		
		_stepId = stepId;
		sendStepUpdate();	
	}
	public HashMap<Integer, CQFDEventTeam> getTeams(){
		return _teams;
	}
	
	
	public void loadRewards(int[][][] rewards){
		if(rewards == null)
			_log.info("reward null 0_L on "+ this.getEventName());
		else
			for(int i = 0 ; i < rewards.length ;  i++)
				addRewardID(rewards[i] , i);
	}
	
	/////////////////////////////////////////////////////
	//
	// 	Task
	//
	/////////////////////////////////////////////////////

	public void registerStep(Collection<CQFDEventTeam> teams, CQFDListenerType lisener, CQFDORDER order, CQFDCOUNTERTYPE type, int value, int stepId){
		_log.info("startRegistrationTask");
		for(CQFDEventTeam team: teams)
				_counters.get(type).add(new CQFDTeamCounter(this, team, lisener, order, value, stepId, type));
	}
	public void registerStep(Collection<CQFDEventTeam> teams, CQFDListenerType lisener, Class<? extends CQFDEventTask> task, int eatch, CQFDORDER order, CQFDCOUNTERTYPE type, int value, int stepId){
		for(CQFDEventTeam team: teams)
				_counters.get(type).add(new CQFDTeamCounter(this, team, lisener, task, eatch,order, value, stepId, type));
	}
	
	public void registerStep(CQFDEventTask task, int stepId){
		_log.info("startRegistrationTask");
		ArrayList<CQFDEventTask> tasks = _taskList.get(stepId);
		if(tasks == null){
			tasks = new ArrayList<CQFDEventTask>();
			_taskList.put(stepId, tasks);
		}
		tasks.add(task);
	}
	
	
	/**
	 * 
	 * @param registrationTime in second
	 */
	public void startRegistrationTask(int registrationTime){
		_log.info("startRegistrationTask");
		if(_currentScheduledTask != null)
			_currentScheduledTask.cancel(true);
		

		_log.info("init CQFDEventTaskRegister");
		_currentScheduledTask = ThreadPoolManager.getInstance().schedule(new CQFDEventTaskRegister(this, registrationTime), 1000);
	}
	
	
	public void onEventTaskEnd(ArrayList<Player> registers){
		_log.info("onEventTaskEnd ");
		CQFDEventStat stat = getEventStat();
		switch(stat){
			case REGISTRATION_END : 
				eventTools.announceRegisterEnd(this);
				storePlayerInTeam(registers); 
				teleportPlayersIn(); 
				startEventTimeOut();
				incStepId(); 
				break;
			case TIMEOUT : 
				endEventStat();/*giveReward(); */ break;
			default : break;
		}
	}
	
	public void startEventTimeOut(){
		_log.info("startEventTimeOut");
		if(_maxEventTime != -1)
			_timeOutTask = ThreadPoolManager.getInstance().schedule(new CQFDEventTimeOutTask(this), _maxEventTime * 1000L);
	}
	
	public void stopEventTimeOut(){
		if(_timeOutTask != null)
			_timeOutTask.cancel(true);
	}
	
	public void stopCurrentTask(){
		if(_currentScheduledTask != null)
			_currentScheduledTask.cancel(true);
	}
	
	public void endEventStat(){
		
		stopEventTimeOut();
		stopCurrentTask();
		endEventAllCounters();
		counterOnTimeOut(_counters);
		eventTools.announceEnd(this);
		teleportPlayersOut();
		
		setEventStat(CQFDEventStat.IDLE);
		_counters.clear();
		_rewards.clear();
		_teams.clear();
	}
	
	public void endEventAllCounters(){
		for(ArrayList<CQFDTeamCounter> counts : _counters.values())
			for(CQFDTeamCounter tc : counts)
				tc.stop();
	}
	
	public void startEventAllCounters(){
		for(ArrayList<CQFDTeamCounter> counts : _counters.values())
			for(CQFDTeamCounter tc : counts)
				tc.start();
	}
	
	public void sendStepUpdate(){
		sendStepUpdateToCounters();
		sendStepUpdateToTasks();
	}
	
	
	public void sendStepUpdateToCounters(){
		for(ArrayList<CQFDTeamCounter> t : _counters.values())
			for(CQFDTeamCounter c : t)
				c.changeStep(_stepId);
	}
	
	public void sendStepUpdateToTasks(){
		for(ScheduledFuture<?> s :_currentTasks)
				s.cancel(true);
		_currentTasks.clear();
		ArrayList<CQFDEventTask> tasks = _taskList.get(_stepId);
		if(tasks == null)
			return;
		
		for(CQFDEventTask c : tasks){
			if(c.isFixedRate())
				_currentTasks.add(ThreadPoolManager.getInstance().scheduleAtFixedRate(c, c.getInitDelay() , c.getDelay()));
			else
				_currentTasks.add(ThreadPoolManager.getInstance().schedule(c, c.getInitDelay()));
		}
	}
	
	/////////////////////////////////////////////////////
	//
	//
	//
	/////////////////////////////////////////////////////
	
	public Collection<CQFDEventTeam> launchRegisterStep(ArrayList<CQFDEventPlayer> locs, int registrationTime){
		_log.info("launchRegisterStep");
		setEventStat(CQFDEventStat.START);
		
		//createTeam
		_teams.clear();
		int teamId  = 0;
		
		for(CQFDEventPlayer loc : locs){
			
			_teams.put(teamId , new CQFDEventTeam(loc.setTeamId(teamId).setEvent(this)));
			teamId++;
		}
		_log.info("teams ceated size : "+ _teams.size());
		
		startRegistrationTask(registrationTime);
		return _teams.values();
	}
	
	
	private void storePlayerInTeam(ArrayList<Player> registers){
		_log.info("storePlayerInTeam ");
		int nbTeam = _teams.size();
		int index = 0;
		for(Player player : registers)
			_teams.get(index++%nbTeam).addPlayer(player);
	}
	
	
	private void teleportPlayersIn(){
		if(_eventStat == CQFDEventStat.REGISTRATION_END)
			for(CQFDEventTeam team : _teams.values())
				team.start(false);
	}
	
	public void teleportPlayersOut(){
		if(_eventStat == CQFDEventStat.TIMEOUT || _eventStat == CQFDEventStat.STOP)
			for(CQFDEventTeam team : _teams.values())
				team.stop(false);
	}

	
	public EventRewards getRewards(int rewardId){
		return _rewards.get(rewardId);
	}
	
	/////////////////////////////////////////////////////
	//
	//		COMMAND
	//
	/////////////////////////////////////////////////////

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
	public CQFDEventTeam addTeam(Player admin, int id){
		// if already contain edit spawn
		if(_teams.containsKey(id))
			_teams.get(id).setSpawn(admin);
		else{
			CQFDEventTeam team = new CQFDEventTeam(new CQFDEventPlayer(admin, this, id));
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
		CQFDEventTeam team = _teams.get(id);
		if(team != null)
			team.teleportBackAll();
		_teams.remove(id);
	}
	
	/**
	 * teleportBack all player in all team
	 * and destroy all teams (teamspawn)
	 */
	public void clearTeam(){
		for(CQFDEventTeam team :_teams.values())
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
		CQFDEventTeam team = _teams.get(teamId);
		if(_eventStat != CQFDEventStat.IDLE){
			_log.info("Event addPlayer can only be use for registration - Use : setTeam <Player> <Team>");
			return;
		}
		
		if(team == null){
			_log.info("Event addPlayer team not found : "+ teamId);
			team = addTeam(admin, teamId);
		}
		
		CQFDEventPlayer ep = null;
		for(CQFDEventTeam teamm: _teams.values()){
			if(teamm.contain(player)){
				ep = teamm.get(player);
				teamm.removePlayer(ep);
				break;
			}
		}
		
		team.addPlayer(player, ep);	
		_log.info("Event addPlayer team "+ teamId +" addNew player , team size "+ team.getEventPlayers().size());
	}
	
	/**
	 * remove a Player from event and teleport him back
	 * @param player
	 */
	public void removePlayer(Player player){
		if(_eventStat != CQFDEventStat.IDLE){
			_log.info("Event removePlayer can only be use for registration - Use : kick <Player>");
			return;
		}
		
		for(CQFDEventTeam team: _teams.values())
			team.removePlayer(player);
	}
	
	/**
	 * switch player to another team and update group
	 * 
	 * @param player
	 * @param id
	 */
	public void setTeam(Player player, int id){
		CQFDEventPlayer ep = null;
		for(CQFDEventTeam team: _teams.values())
			if(team.contain(player)){
				ep = team.removePlayer(player);
				break;
			}
		
		CQFDEventTeam team;
		
		if(_teams.containsKey(id)){
			team = _teams.get(id);
			team.addPlayer(player, ep);
		}
		else
		{
			team = new CQFDEventTeam(new CQFDEventPlayer(player, this, id));
			_teams.put(id, team);
			team.addPlayer(player, ep);
		}
		
		//if event start teleport this player on team spawn
		if(_eventStat != CQFDEventStat.IDLE){
			team.teleportPlayer(player);
			eventTools.createLeague(team);
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
		_log.info("Event teleportPlayer : "+ _teams.size());
		for(CQFDEventTeam team : _teams.values())
			if(team.contain(player)){
				team.teleportPlayer(player);
				eventTools.createLeague(team);
				break;
			}
	}
	
	public void teleportTeam(){
		teleportTeam(0);
	}
	
	public void teleportTeam(int teamId){
		_log.info("Event teleportTeam teams "+teamId+ " : "+ _teams.size());
		if(_teams.containsKey(teamId))
			_teams.get(teamId).teleportAllPlayer();
	}
	
	/*
	 * teleport back
	 */
	
	/**
	 * @param player
	 */
	public void teleportBackPlayer(Player player){
		//seach on all team 
		for(CQFDEventTeam team : _teams.values())
			if(team.contain(player)){
				team.teleportBackPlayer(player);
				eventTools.createLeague(team);
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
		rewards.add(new CQFDReward(itemId, itemCount));
	}
	
	public void addRewardID(int[][] rewards, int rewardId){
		for(int[] reward : rewards)
			addRewardID(reward[0], reward[1], rewardId);
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
		for(CQFDEventTeam team: _teams.values())
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
		for(CQFDEventTeam team : _teams.values())
			team.rewardAllPlayer(rewardId);
	}
	
	
	public void respawn(){
		for(CQFDEventTeam team : _teams.values())
			team.teleportAllPlayer(false);
	}
	
	public void respawn(int teamId){
		CQFDEventTeam team = _teams.get(teamId);
		if(team == null)
			return;
		else
			team.teleportAllPlayer(false);
	}
	
	public void resurrectAll(boolean teleportAtSpawn){
		for(CQFDEventTeam team : _teams.values())
			team.resurrectAll(teleportAtSpawn);
	}
	
	public void resurrect(int teamId){
		resurrect(teamId, false);
	}
	
	public void resurrect(int teamId, boolean teleportAtSpawn){
		CQFDEventTeam team = _teams.get(teamId);
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
	
	////////////////////////////////////////////////////////////////////
	//	Announce
	////////////////////////////////////////////////////////////////////
	public String getEventName(){
		return this.getClass().getSimpleName();
	}


	////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////
	
	

	public void start(){
		_stepId = 0;
		launchRegisterStep(_spawns, _registerDelay);
		startEventAllCounters();
		sendStepUpdate();
		eventTools.announceStart(this);
	}
	
	
	public void stop(){
		_eventStat = CQFDEventStat.IDLE;
		endEventStat();
	}
	
	public void init(){};
	

	
	//public void stop(){};
	public void restart(){};
	
	public void counterOnTimeOut(HashMap<CQFDCOUNTERTYPE, ArrayList<CQFDTeamCounter>> finalCounters){};
	
	public void stepResult(CQFDTeamCounter counter){};
}
