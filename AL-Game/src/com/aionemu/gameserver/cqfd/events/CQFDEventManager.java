package com.aionemu.gameserver.cqfd.events;

import java.util.ArrayList;

public class CQFDEventManager {
	private static CQFDEventManager _instance = null;

	private ArrayList<CQFDEvent> _events = new ArrayList<CQFDEvent>();
	
	
	public void addEvent(CQFDEvent event){
		_events.add(event);
	}
	
	public void removeEvent(CQFDEvent event){
		_events.remove(event);
	}
	public void init(){
		if(_instance == null)
			_instance = this;
	}
}
