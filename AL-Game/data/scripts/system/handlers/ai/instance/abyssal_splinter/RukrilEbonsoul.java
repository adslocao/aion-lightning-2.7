package ai.instance.abyssal_splinter;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;


@AIName("rukrilebonsoul")
public class RukrilEbonsoul extends AggressiveNpcAI2 {
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> task;
	private static final int SHIELD_TIME = 50; // 50 sec
	
	private boolean isLight = false;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			isLight = getOwner().getNpcId() == 216948;
			schedulTask();
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		AI2Actions.deleteOwner(this);
	}

	@Override
	protected void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}
	
	private void schedulTask(){
		task = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				startTask();
			}
		}, SHIELD_TIME * 1000);
	}
	
	private void startTask() {
		if(getPosition().getWorldMapInstance().getNpc(isLight ? 281907 : 281908) == null){
			spawn(isLight ? 281907 : 281908, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
		}
		SkillEngine.getInstance().getSkill(getOwner(), isLight ? 19266 : 19159, 55, getOwner()).useSkill();
		
		schedulTask();
	}


	private void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}

		getOwner().getEffectController().removeAllEffects();
		
		for (Npc npc : getPosition().getWorldMapInstance().getNpcs(281907)) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
		for (Npc npc : getPosition().getWorldMapInstance().getNpcs(281908)) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
}
