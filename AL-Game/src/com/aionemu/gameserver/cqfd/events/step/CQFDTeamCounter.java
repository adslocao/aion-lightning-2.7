package com.aionemu.gameserver.cqfd.events.step;

import com.aionemu.gameserver.cqfd.Lisener.CQFDListener;
import com.aionemu.gameserver.cqfd.Lisener.CQFDListenerEntry;
import com.aionemu.gameserver.cqfd.Lisener.CQFDListenerManager;
import com.aionemu.gameserver.cqfd.Lisener.CQFDListenerType;
import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventTeam;
import com.aionemu.gameserver.cqfd.events.task.CQFDEventTask;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public class CQFDTeamCounter implements CQFDListener{
	private final CQFDListenerType _lisenerType;
	private final CQFDORDER _order;
	private final CQFDCOUNTERTYPE _type;
	private final CQFDEventTeam _team;
	private final CQFDEvent _event;
	private Class<? extends CQFDEventTask> _actionC = null;
	private int _eatch = 0;
	private int _actionIndex = 0;
	
	private long _count;
	private int _endCount;
	private final int _stepId;
	
	private boolean _isActive = false;
	
	//private static final Logger _log = LoggerFactory.getLogger(CQFDTeamCounter.class);

	public CQFDTeamCounter(CQFDEvent event, CQFDEventTeam team, CQFDListenerType lisenerType, CQFDORDER order, int init, int stepId, CQFDCOUNTERTYPE type){
		_event = event;
		_team = team;
		_lisenerType = lisenerType;
		_endCount = init;
		_order = order;
		_stepId = stepId;
		_type = type;
	}
	
	public CQFDTeamCounter(CQFDEvent event, CQFDEventTeam team, CQFDListenerType lisenerType, Class<? extends CQFDEventTask> c, int eatch, CQFDORDER order, int init, int stepId, CQFDCOUNTERTYPE type){
		_event = event;
		_team = team;
		_lisenerType = lisenerType;
		_actionC = c;
		_eatch = eatch;
		_endCount = init;
		_order = order;
		_stepId = stepId;
		_type = type;
	}
	
	public void stop(){
		if(_isActive){
			CQFDListenerManager.unregisterFor(_lisenerType, this);
			setActive(false);
			_actionIndex = 0;
			if(_event != null)
				_event.stepResult(this);
		}
	}
	
	public void start(){
		if(!_isActive){
			CQFDListenerManager.registerFor(_lisenerType, this, _endCount);
			setActive(true);
		}
	}
	
	private void setActive(boolean isActive){
		_isActive = isActive;
	}
	
	public void changeStep(int stepId){
		if(!_isActive && _stepId == stepId)
			start();
		else if(_isActive && _stepId != stepId)
			stop();
	}
	
	public CQFDEventTeam getTeam(){
		return _team;
	}
	
	public int getStepId(){
		return _stepId;
	}
	
	public long getCount(){
		return _count;
	}
	
	public void setCount(long count){
		_count = count;
	}

	public void updateCount(Object[] params){
		switch(_order){
			case INC : 
				_count++;
				if(_eatch != 0)
					if(++_actionIndex >= _eatch){
						_actionIndex = 0;
						try {
							_actionC.getDeclaredConstructor(CQFDEventTeam.class, Object[].class).newInstance(_event, params).run();
						} catch (Exception e) {
							
						}
					}
				if(_type != CQFDCOUNTERTYPE.INFINITE && _count > _endCount)
					stop();
				break;
			case DEC : 	
				_count--;
				if(_type != CQFDCOUNTERTYPE.INFINITE && _count <= 0)
					stop();
			break;
		}
	}
	
	
	/////////////////////////////////////////
	//
	//
	//
	/////////////////////////////////////////
	
	@Override
	public void onLisenerEvent(CQFDListenerEntry e) {
		if(!_isActive)
			return;
		
		switch(_lisenerType){
			case PLAYER_KILL_PLAYER: 
				if(_team.contain((Player)e.getKiller())){
					Object[] params = {(Player)e.getKiller(), (Player)e.getKilled()};
					updateCount(params);
				}
				break;
			case PLAYER_KILL_MONSTER:
				if(_team.contain((Player) e.getKiller())){
					Object[] params = {(Player)e.getKiller(), (Player)e.getKilled()};
					updateCount(params);
				}
				break;
			default: break;
		}
		
	}
}
