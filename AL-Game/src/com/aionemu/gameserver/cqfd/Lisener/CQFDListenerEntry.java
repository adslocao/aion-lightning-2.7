package com.aionemu.gameserver.cqfd.Lisener;

import com.aionemu.gameserver.model.gameobjects.Creature;

public class CQFDListenerEntry {
	private Creature _killer;
	private Creature _killed;
	private Object[] _agrs;
	
	public CQFDListenerEntry(Creature killer, Creature killed, Object[] agrs){
		_killer = killer;
		_killed = killed;
		_agrs = agrs;
	}
	
	
	public Creature getKiller(){
		return _killer;
	}
	
	public Creature getKilled(){
		return _killed;
	}
	
	public Object[] getAgrs(){
		return _agrs;
	}
}
