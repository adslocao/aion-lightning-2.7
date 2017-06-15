package com.aionemu.gameserver.cqfd.events.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.aionemu.gameserver.cqfd.Lisener.CQFDListenerType;
import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventPlayer;
import com.aionemu.gameserver.cqfd.events.CQFDEventStat;
import com.aionemu.gameserver.cqfd.events.CQFDEventTeam;
import com.aionemu.gameserver.cqfd.events.step.CQFDCOUNTERTYPE;
import com.aionemu.gameserver.cqfd.events.step.CQFDORDER;
import com.aionemu.gameserver.cqfd.events.step.CQFDTeamCounter;
import com.aionemu.gameserver.cqfd.events.task.impl.CQFDEventAnnounce;
import com.aionemu.gameserver.cqfd.events.task.impl.CQFDEventReward;

public class CQFDEventPvPAera extends CQFDEvent{
	///////////////////////////////////////////////////////////////////////////////
	//
	//	DATA
	//
	///////////////////////////////////////////////////////////////////////////////

	private final static ArrayList<CQFDEventPlayer> SPAWNS = new ArrayList<CQFDEventPlayer>(
			Arrays.asList(
					//new CQFDEventPlayer(instance, worldId, instanceId, x,y,z,h),
					new CQFDEventPlayer(0, 0, 0,0,0,(byte)0)
					));
	
	protected static int[][][] REWARDS = {
		{// REWARD_LOSE
			{57 , 1},
			{57 , 1}
		},
		{// REWARD_WIN
			{57 , 2},
			{57 , 2}
		}
	};
	public static CQFDEventPvPAera instance = new CQFDEventPvPAera();
	///////////////////////////////////////////////////////////////////////////////
	//
	//	CODE
	//
	///////////////////////////////////////////////////////////////////////////////

	
	private CQFDEventPvPAera(){
		super(SPAWNS,REWARDS,10,-1);
	}
	
	public static void openPvPAera(){
		if(instance.getEventStat() == CQFDEventStat.IDLE);
			instance.start();
		
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//
	//	ABSTRACT
	//
	///////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public void init() {
		//announce
		registerStep(new CQFDEventAnnounce(getTeams().values(), this, "PvP Aera Open", 1), 0);
		//start kill counter
		//registerStep(getTeams().values(), CQFDListenerType.PLAYER_KILL_PLAYER , CQFDORDER.INC, CQFDCOUNTERTYPE.PERSISTENT, -1, 0);		
		registerStep(getTeams().values(), CQFDListenerType.PLAYER_KILL_PLAYER , CQFDEventReward.class, 1, CQFDORDER.INC, CQFDCOUNTERTYPE.PERSISTENT, -1, 0);		

	}

	@Override
	public void stepResult(CQFDTeamCounter counter) {
		final int stepId = getStepId();
		incStepId();
		switch(stepId){
			//	register
			case 0: 
			// kill step
			case 1: 
				System.out.println("[] someone win with"+ counter.getCount() + " kill");
				break;
		}
	}

	@Override
	public void counterOnTimeOut(final HashMap<CQFDCOUNTERTYPE, ArrayList<CQFDTeamCounter>> finalCounters) {
		final ArrayList<CQFDTeamCounter> infiniteCounters = finalCounters.get(CQFDCOUNTERTYPE.INFINITE);
		//reward player here
		long bestScore = 0;
		final ArrayList<CQFDEventTeam> winners = new ArrayList<CQFDEventTeam>(2);
		final ArrayList<CQFDEventTeam> losers = new ArrayList<CQFDEventTeam>();
		
		//give reward to all team
		for(CQFDTeamCounter ts : infiniteCounters){
			if(ts.getCount() == bestScore)
				winners.add(ts.getTeam());
			else if(ts.getCount() > bestScore){
				bestScore = ts.getCount();
				losers.addAll(winners);
				winners.clear();
				winners.add(ts.getTeam());
			}
			
		}
		//winner
		for(CQFDEventTeam team : winners)
			team.rewardAllPlayer(0);
		for(CQFDEventTeam team : losers)
			team.rewardAllPlayer(1);
		
		System.out.println("[Event] counterOnTimeOut bestScore -> "+ bestScore);
	}
	
	/////////////////////////////////////////////////////////////
	//
	//	SUBFOUNC
	//
	/////////////////////////////////////////////////////////////

}

