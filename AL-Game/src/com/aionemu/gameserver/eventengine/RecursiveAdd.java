package com.aionemu.gameserver.eventengine;


import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.custom.RecursiveAddConf;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.TranslationService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author Krunchy
 * @modified Ferosia
 */

public class RecursiveAdd{
	public static Logger _log = LoggerFactory.getLogger(RecursiveAdd.class);
	
	private final static int REWARD_ID = RecursiveAddConf.itemId;
	private final static int REWARD_COUNT = RecursiveAddConf.itemCount;
	
	private final static int FREQUENCE = RecursiveAddConf.frequence*60*1000;
	private final static int INIT_TIME = 5*60*1000;
	
	private static ScheduledFuture<?> _currentScheduledTask = null;
	
	public static final void startRecursiveAddTask(){
		_log.info("startRecursiveAddTask");
		if(_currentScheduledTask != null)
			_currentScheduledTask.cancel(true);
		
		if(REWARD_ID == -1 || REWARD_COUNT == -1 || FREQUENCE <= 0)
			return;
		
		_currentScheduledTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try{
					for(Player pl : World.getInstance().getAllPlayers())
						if(pl.isOnline())
							rewardPlayer(pl);
				}
				catch (Exception ex){
					_log.warn("Exception on RecursiveAdd");
				}
			}
		}, INIT_TIME, FREQUENCE);
	}
	
	
	public static final void rewardPlayer(Player player){
		if(player != null && !player.isInPrison()){
			// Send message to player only if inventory is not full
			if(ItemService.addItem(player, REWARD_ID, REWARD_COUNT) > 0) {
				String MESSAGE = TranslationService.RECURSIVEADD_MESSAGE.toString(player);
				PacketSendUtility.sendBrightYellowMessageOnCenter(player, MESSAGE);
			}
		}
	}
}
