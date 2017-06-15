package com.aionemu.gameserver.cqfd.events.task.impl;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventStat;
import com.aionemu.gameserver.cqfd.events.task.CQFDEventTask;
import com.aionemu.gameserver.cqfd.events.task.CQFDEventTaskType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class CQFDEventTaskRegister extends CQFDEventTask{

	private static final Logger _log = LoggerFactory.getLogger(CQFDEventTaskRegister.class);
	private final static ArrayList<Player> registered = new ArrayList<Player>();
	private boolean busy = false;
	private final int _registrationTime;
	private ScheduledFuture<?> _currentScheduledTask = null;
	
	
	public CQFDEventTaskRegister(){
		super(null);
		_registrationTime = -1;
	}
	
	public CQFDEventTaskRegister(CQFDEvent event, int registrationTime){
		super(event);
		_registrationTime = registrationTime;
		setEventTask(CQFDEventTaskType.REGISTRATION);
	}
	
	public void cancel(){
		if(_currentScheduledTask != null)
			_currentScheduledTask.cancel(true);
	}
	
	@Override
	public void run(){
		_log.info("init CQFDEventTaskRegister");
		if(busy){
			_log.info("[CQFDEventTaskRegister] erreur on multiple registration");
			return;
		}
		busy = true;
		registered.clear();
		
		getEvent().setEventStat(CQFDEventStat.REGISTRATION);
		//displayAnnounce();// registration open
		//question();
		
		
		_currentScheduledTask = ThreadPoolManager.getInstance().schedule(new CQFDEventTaskRegisterEnd(getEvent()), _registrationTime * 1000L);
	}
	
	
	public class CQFDEventTaskRegisterEnd implements Runnable{
		private final CQFDEvent _event;
		
		public CQFDEventTaskRegisterEnd (CQFDEvent event){
			_event = event;
		}
		
		@Override
		public void run()
		{
			if(_event.getEventStat() != CQFDEventStat.REGISTRATION)
				return;
			getEvent().setEventStat(CQFDEventStat.REGISTRATION_END);
			_log.info("CQFDEventTaskRegisterEnd");
			_log.info("return registered size : "+ registered.size());
			getEvent().onEventTaskEnd(registered);
		}
	}
	/**
	 * L2
	 */
	/*
	public static void question()
	{
		_log.info("question");
		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
			if(player != null && !player.isDead() && player.getReflection().isDefault() && !player.isInOlympiadMode() && !player.isInObserverMode() && player.getVar("jailed") == null)
				player.scriptRequest(new CustomMessage("scripts.cqfd.events.invade.AskPlayer", player).toString(), "cqfd.events.task.impl.CQFDEventTaskRegister:addPlayer", new Object[0]);
	}
	 */
	
    public static void addPlayer(Player player)
    {
    	_log.info("addPlayer");

        if(player == null || !checkPlayer(player, true) || !checkDualBox(player))
                    return;

        /*
		if(!CQFDConfig.EVENT_InvadeAllowMultiReg) 
		{
			if("IP".equalsIgnoreCase(CQFDConfig.EVENT_InvadeCheckWindowMethod))
				boxes.put(player.getStoredId(), player.getIP());
			if("HWid".equalsIgnoreCase(CQFDConfig.EVENT_InvadeCheckWindowMethod))
                boxes.put(player.getStoredId(), player.getNetConnection().getHWID());
		}
		*/
        _log.info("Player added");
        registered.add(player);
        //show(new CustomMessage("scripts.cqfd.events.invade.Registered", player), player);
    }
    private static boolean checkDualBox(Player player) {
    	/*
        if(!CQFDConfig.EVENT_InvadeAllowMultiReg)
        {
                if("IP".equalsIgnoreCase(CQFDConfig.EVENT_InvadeCheckWindowMethod)) {
                        if(boxes.containsValue(player.getIP())) {
                                show(new CustomMessage("scripts.cqfd.events.invade.CancelledBox", player), player);
                                return false;
                        }
                }
               
                else if("HWid".equalsIgnoreCase(CQFDConfig.EVENT_InvadeCheckWindowMethod)) {
                        if(boxes.containsValue(player.getNetConnection().getHWID())) {
                                show(new CustomMessage("scripts.cqfd.events.invade.CancelledBox", player), player);
                                return false;
                        }
                }
        }
        */
        return true;
    }
    
    public static boolean checkPlayer(Player player, boolean first)
    {
    	/*
            if(first && !_isRegistrationActive)
            {
                    show(new CustomMessage("scripts.events.Late", player), player);
                    return false;
            }
           
            if(first && (players_list.contains(player))) {
                    show(new CustomMessage("scripts.cqfd.events.invade.Cancelled", player), player);
                    if(players_list.contains(player.getStoredId()))
                            players_list.remove(player.getStoredId());
                    if(boxes.containsKey(player.getStoredId()))
                            boxes.remove(player.getStoredId());
                    return false;
            }

            if(first && player.isDead())
                    return false;

            if(first && (players_list.contains(player) || players_list.contains(player.getStoredId())))
            {
                    show(new CustomMessage("scripts.cqfd.events.invade.Cancelled", player), player);
                    return false;
            }

            if(player.isMounted())
            {
                    show(new CustomMessage("scripts.cqfd.events.invade.Cancelled", player), player);
                    return false;
            }

            if(player.isInDuel())
            {
                    show(new CustomMessage("scripts.cqfd.events.invade.CancelledDuel", player), player);
                    return false;
            }

            if(player.getTeam() != TeamType.NONE)
            {
                    show(new CustomMessage("scripts.cqfd.events.invade.CancelledOtherEvent", player), player);
                    return false;
            }

            if(player.isInOlympiadMode() || first && Olympiad.isRegistered(player))
            {
                    show(new CustomMessage("scripts.cqfd.events.invade.CancelledOlympiad", player), player);
                    return false;
            }

            if(player.isInParty() && player.getParty().isInDimensionalRift())
            {
                    show(new CustomMessage("scripts.cqfd.events.invade.CancelledOtherEvent", player), player);
                    return false;
            }

            if(player.isTeleporting())
            {
                    show(new CustomMessage("scripts.cqfd.events.invade.CancelledTeleport", player), player);
                    return false;
            }
    	 	*/
            return true;
    }
    //////////////////////////////////////////////////////////////////////////
    //
    //
    //
    //////////////////////////////////////////////////////////////////////////
    
}
