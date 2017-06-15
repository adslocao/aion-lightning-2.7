package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.world.World;
import java.util.Collection;

public class FortressSiegeStartRunnable implements Runnable {

	private final int fortressSiegeLocationId;
	
	public final static String SIEGE_BOSS_AI_NAME = "siege_protector";

	public FortressSiegeStartRunnable(int fortressSiegeLocationId) {
		this.fortressSiegeLocationId = fortressSiegeLocationId;
	}

	@Override
	public void run() {
		SiegeService.getInstance().startSiege(getFortressSiegeLocationId());
		/*if (CustomConfig.AUTO_ASSAULT == true)
			CustomBalaurAssault.startCheck(getProtector());*/
	}

	/*private SiegeNpc getProtector() {
		Collection<SiegeNpc> npcs = World.getInstance().getLocalSiegeNpcs(getFortressSiegeLocationId());
		for (SiegeNpc npc : npcs) {
			if (SIEGE_BOSS_AI_NAME.equals(npc.getObjectTemplate().getAi()))
				return npc;
		}
		throw new SiegeException("Not Found Protector for assault: " + getFortressSiegeLocationId());
	}*/

	public int getFortressSiegeLocationId() {
		return fortressSiegeLocationId;
	}
}
