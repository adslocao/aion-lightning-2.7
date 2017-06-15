package com.aionemu.gameserver.cqfd.events.task.impl;

import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventStat;
import com.aionemu.gameserver.cqfd.events.task.CQFDEventTask;

public class CQFDEventTimeOutTask extends CQFDEventTask{
	
	public CQFDEventTimeOutTask(CQFDEvent event){
		super(event);
	}
	
	@Override
	public void run(){
		if(getEvent().getEventStat() != CQFDEventStat.STOP && getEvent().getEventStat() != CQFDEventStat.IDLE){
			getEvent().setEventStat(CQFDEventStat.TIMEOUT);
			getEvent().onEventTaskEnd(null);
		}
	}
}
