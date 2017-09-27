package com.aionemu.gameserver.eventengine;

import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.custom.WebShopConf;
import com.aionemu.gameserver.dao.WebShopDAO;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author Ferosia
 */

public class EventWebShop {
	public static Logger log = LoggerFactory.getLogger(EventWebShop.class);

	private final static int INIT_TIME = WebShopConf.WEBSHOP_INIT_TIME * 1000;
	private final static int FREQUENCE = WebShopConf.WEBSHOP_FREQUENCE * 1000;
	private final static boolean DISABLE_ON_SIEGE = WebShopConf.WEBSHOP_DISABLE_ON_SIEGE;
	
	private static ScheduledFuture<?> currentScheduledTask = null;
	
	public static final void startEventWebShopTask(){

		log.info("startEventWebShop");
		
		if(currentScheduledTask != null)
			currentScheduledTask.cancel(true);
		
		currentScheduledTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// Don't execute task if there is no connected player
				if(World.getInstance().countAllPlayers() == 0)
					return;
				
				if(DISABLE_ON_SIEGE && SiegeService.getInstance().isAtLeastOneSiegeInProgress())
					return;
				
				try {
					DAOManager.getDAO(WebShopDAO.class).checkAllPendingShopItem();
				}
				catch (Exception ex){
					log.warn("Exception on EventWebShop");
				}
			}
		}, INIT_TIME, FREQUENCE);
	}
}