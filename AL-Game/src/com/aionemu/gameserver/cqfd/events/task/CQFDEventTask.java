package com.aionemu.gameserver.cqfd.events.task;

import com.aionemu.gameserver.cqfd.events.CQFDEvent;

public abstract class CQFDEventTask implements Runnable{
	
	private CQFDEventTaskType _eventTaskType = CQFDEventTaskType.NONE;
	private final CQFDEvent _event;
	public Object[] _params = null;
	private long _initTime = 0;
	private long _delay = 0;
	
	public CQFDEventTask(CQFDEvent event){
		_event = event;
	}
	
	
	public CQFDEventTask(CQFDEvent event, Object[] params) {
		_event = event;
		_params = params;
	}
	
	public CQFDEventTask(CQFDEvent event, long initTime){
		_event = event;
		_initTime = initTime;
	}
	
	
	public CQFDEvent getEvent(){
		return _event;
	}
	
	/**
	 * repeat delay
	 * @return
	 */
	public long getInitDelay(){
		return _initTime*1000;
	}
	
	/**
	 * repeat delay
	 * @return
	 */
	public long getDelay(){
		return _delay*1000;
	}
	

	public Object[] getParams(){
		return _params;
	}
	
	public boolean isFixedRate(){
		return _delay > 0;
	}
	
	public CQFDEventTask setDelay(long seconds){
		_delay = seconds;
		return this;
	}
	
	public CQFDEventTaskType getEventTask(){
		return _eventTaskType;
	}
	
	public void setEventTask(CQFDEventTaskType eventTaskType){
		_eventTaskType = eventTaskType;
	}
	
	public void cancel(){
		
	}
}
