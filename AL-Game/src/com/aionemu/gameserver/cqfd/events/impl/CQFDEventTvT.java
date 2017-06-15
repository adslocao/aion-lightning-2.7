package com.aionemu.gameserver.cqfd.events.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.aionemu.gameserver.cqfd.Lisener.CQFDListenerType;
import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventPlayer;
import com.aionemu.gameserver.cqfd.events.CQFDEventTeam;
import com.aionemu.gameserver.cqfd.events.step.CQFDCOUNTERTYPE;
import com.aionemu.gameserver.cqfd.events.step.CQFDORDER;
import com.aionemu.gameserver.cqfd.events.step.CQFDTeamCounter;
import com.aionemu.gameserver.cqfd.events.task.impl.CQFDEventAnnounce;

public class CQFDEventTvT extends CQFDEvent{
	
	
	///////////////////////////////////////////////////////////////////////////////
	//
	//	DATA
	//
	///////////////////////////////////////////////////////////////////////////////

	private final static ArrayList<CQFDEventPlayer> SPAWNS = new ArrayList<CQFDEventPlayer>(
			Arrays.asList(
					//new CQFDEventPlayer(instance, worldId, instanceId, x,y,z,h),
					new CQFDEventPlayer(210050000, 0, 1385,277,590,(byte)110),
					new CQFDEventPlayer(210050000, 0, 1313,335,590,(byte)0)
					));
	
	public static int[][][] REWARDS = {
		{// REWARD_LOSE
			{57 , 1},
			{57 , 1}
		},
		{// REWARD_WIN
			{57 , 2},
			{57 , 2}
		}
	};
	public static CQFDEventTvT instance = new CQFDEventTvT();
	///////////////////////////////////////////////////////////////////////////////
	//
	//	CODE
	//
	///////////////////////////////////////////////////////////////////////////////

	
	public CQFDEventTvT(){
		super(SPAWNS,REWARDS,10,60);
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//
	//	ABSTRACT
	//
	///////////////////////////////////////////////////////////////////////////////

	
	@Override
	public void init() {
		//Step 1
		//announce
		registerStep(new CQFDEventAnnounce(getTeams().values(), this, "Event Started", 1), 1);
		//start kill counter
		registerStep(getTeams().values(), CQFDListenerType.PLAYER_KILL_PLAYER , CQFDORDER.INC, CQFDCOUNTERTYPE.INFINITE, 1, 1);		
		
		//Step 1
		registerStep(new CQFDEventAnnounce(getTeams().values(), this, "1 kills more", 1), 2);
		//start kill counter
		//registerStep(getTeams().values(), CQFDListenerType.PLAYER_KILL_PLAYER , CQFDORDER.INC, CQFDCOUNTERTYPE.TEMPORARY, 10, 2);		

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
