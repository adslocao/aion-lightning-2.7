package com.aionemu.gameserver.cqfd.Lisener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class CQFDListenerManager {
	private final static HashMap<CQFDListenerType, ArrayList<CQFDListener>> _classes;
	private final static HashMap<CQFDListener, ScheduledFuture<?>> _timers = new HashMap<CQFDListener, ScheduledFuture<?>>();
	static{
		_classes = new HashMap<CQFDListenerType, ArrayList<CQFDListener>>();
		_classes.put(CQFDListenerType.PLAYER_KILL_PLAYER, new ArrayList<CQFDListener>());
		_classes.put(CQFDListenerType.PLAYER_KILL_MONSTER, new ArrayList<CQFDListener>());
		_classes.put(CQFDListenerType.MONSTER_KILL_PLAYER, new ArrayList<CQFDListener>());
		_classes.put(CQFDListenerType.MONSTER_KILL_MONSTER, new ArrayList<CQFDListener>());
		_classes.put(CQFDListenerType.PLAYERDEATH, new ArrayList<CQFDListener>());
		_classes.put(CQFDListenerType.MONSTERDEATH, new ArrayList<CQFDListener>());
	}
	
	
	private static final Logger _log = LoggerFactory.getLogger(CQFDListenerManager.class);


	
	public static final void onEvent(CQFDListenerType type, Creature killer , Creature killed, Object[] o){
		//_log.info("onEvent : "+ type);
		
		ArrayList<CQFDListener> Aclasses = _classes.get(type);
		//CQFDListener[] classes = Aclasses.toArray(new CQFDListener[Aclasses.size()]);
		
		for(CQFDListener classs : Aclasses){
			if(classs != null){
				classs.onLisenerEvent(new CQFDListenerEntry(killer, killed, o));
				_log.info("onEvent : sendto");
			}
		}
	}
	
	public static final void registerFor(CQFDListenerType type, CQFDListener classs, int value){
		_log.info("lisener add for : "+ type);
		if(CQFDListenerType.TIME == type)
			_timers.put(classs, ThreadPoolManager.getInstance().schedule(new CQFDTimeEvent(classs), value*1000L));
		else	
			_classes.get(type).add(classs);
	}
	
	public static final void unregisterFor(CQFDListenerType type, CQFDListener classs){
		_log.info("lisener remove for : "+ type);
		if(CQFDListenerType.TIME == type){
			ScheduledFuture<?> timer = _timers.get(classs);
			if(timer != null)
				timer.cancel(false);
			_timers.remove(classs);
		}
		_classes.get(type).remove(classs);
	}
}
