/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.world.World;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Siege<SL extends SiegeLocation> {

	private static final Logger log = LoggerFactory.getLogger(Siege.class);
	private final SiegeBossDeathListener siegeBossDeathListener = new SiegeBossDeathListener(this);
	private final SiegeBossDoAddDamageListener siegeBossDoAddDamageListener = new SiegeBossDoAddDamageListener(this);
	private final AtomicBoolean finished = new AtomicBoolean();
	private final SiegeCounter siegeCounter = new SiegeCounter();
	private final SL siegeLocation;
	private boolean bossKilled;
	private SiegeNpc boss;
	private Date startTime;
	private boolean started;

	public Siege(SL siegeLocation) {
		this.siegeLocation = siegeLocation;
	}

	public final void startSiege() {

		boolean doubleStart = false;

		// keeping synchronization as minimal as possible
		synchronized (this) {
			if (started) {
				doubleStart = true;
			}
			else {
				startTime = new Date();
				started = true;
			}
		}

		if (doubleStart) {
			log.error("Attempt to start siege of SiegeLocation#" + siegeLocation.getLocationId() + " for 2 times");
			return;
		}

		onSiegeStart();
		//Check for Balaur Assault
		if (SiegeConfig.BALAUR_AUTO_ASSAULT) {
			BalaurAssaultService.getInstance().onSiegeStart(this);
		}
	}

	public final void stopSiege() {
		if (finished.compareAndSet(false, true)) {
			onSiegeFinish();
			//Check for Balaur Assault
			if (SiegeConfig.BALAUR_AUTO_ASSAULT) {
				BalaurAssaultService.getInstance().onSiegeFinish(this);
			}
		}
		else {
			log.error("Attempt to stop siege of SiegeLocation#" + siegeLocation.getLocationId() + " for 2 times");
		}
	}

	public SL getSiegeLocation() {
		return siegeLocation;
	}

	public int getSiegeLocationId() {
		return siegeLocation.getLocationId();
	}

	public boolean isBossKilled() {
		return bossKilled;
	}

	public void setBossKilled(boolean bossKilled) {
		this.bossKilled = bossKilled;
	}

	public SiegeNpc getBoss() {
		return boss;
	}

	public void setBoss(SiegeNpc boss) {
		this.boss = boss;
	}

	public SiegeBossDoAddDamageListener getSiegeBossDoAddDamageListener() {
		return siegeBossDoAddDamageListener;
	}

	public SiegeBossDeathListener getSiegeBossDeathListener() {
		return siegeBossDeathListener;
	}

	public SiegeCounter getSiegeCounter() {
		return siegeCounter;
	}

	protected abstract void onSiegeStart();

	protected abstract void onSiegeFinish();

	public void addBossDamage(Creature attacker, int damage) {
		// We don't have to add damage anymore if siege is finished
		if (isFinished()) {
			return;
		}

		// Just to be sure that attacker exists.
		// if don't - dunno what to do
		if (attacker == null) {
			return;
		}

		// Actually we don't care if damage was done from summon.
		// We should threat all the damage like it was done from the owner
		attacker = attacker.getMaster();
		getSiegeCounter().addDamage(attacker, damage);
	}

	/**
	 * Returns siege duration in seconds or -1 if it's endless
	 *
	 * @return siege duration in seconnd or -1 if siege should never end using
	 * timer
	 */
	public abstract int getDurationInSeconds();

	public abstract boolean isEndless();

	public boolean isStarted() {
		return started;
	}

	public boolean isFinished() {
		return finished.get();
	}

	public Date getStartTime() {
		return startTime;
	}

	protected void registerSiegeBossListeners() {
		// Add hate listener - we should know when someone attacked general
		EnhancedObject eo = (EnhancedObject) getBoss().getAggroList();
		eo.addCallback(getSiegeBossDoAddDamageListener());

		// Add die listener - we should stop the siege when general dies
		AbstractAI ai = (AbstractAI) getBoss().getAi2();
		eo = (EnhancedObject) ai;
		eo.addCallback(getSiegeBossDeathListener());
	}

	protected void unregisterSiegeBossListeners() {
		// Add hate listener - we should know when someone attacked general
		EnhancedObject eo = (EnhancedObject) getBoss().getAggroList();
		eo.removeCallback(getSiegeBossDoAddDamageListener());

		// Add die listener - we should stop the siege when general dies
		AbstractAI ai = (AbstractAI) getBoss().getAi2();
		eo = (EnhancedObject) ai;
		eo.removeCallback(getSiegeBossDeathListener());
	}

	/**
	 * TODO: This should be done in some other, more "gentle" way...
	 *
	 * @ deprecated This should be removed
	 */
	// @Deprecated
	protected void initSiegeBoss(Collection<String> bossAiNames) {

		SiegeNpc boss = null;

		Collection<SiegeNpc> npcs = World.getInstance().getLocalSiegeNpcs(getSiegeLocationId());
		for (SiegeNpc npc : npcs) {
			if (bossAiNames.contains(npc.getObjectTemplate().getAi())) {

				if (boss != null) {
					log.warn("[TEST] BOSS 1 :" + boss.getNpcId() + " | BOSS 2 :" + npc.getNpcId());
					throw new SiegeException("Found 2 siege bosses for outpost " + getSiegeLocationId());
				}

				boss = npc;
			}
		}

		if (boss == null) {
			throw new SiegeException("Siege Boss not found for siege " + getSiegeLocationId());
		}

		setBoss(boss);
		registerSiegeBossListeners();
	}

	protected void broadcastUpdate() {
		SiegeService.getInstance().broadcastUpdate();
	}

	protected void broadcastUpdate(SiegeLocation location) {
		SiegeService.getInstance().broadcastUpdate(location);
	}

}