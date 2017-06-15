package ai.instance.abyssal_splinter;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritsu, Luzien
 */
@AIName("rukril")
public class RukrilAI2 extends AggressiveNpcAI2 {
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> skillTask;
	private Future<?> task;

	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 95 && isHome.compareAndSet(true, false)) {
			startSkillTask();
			scheduleTask();
		}
	}

	private void startSkillTask()	{
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run()	{
				if (isAlreadyDead()){
					cancelTask();
				} else {
					SkillEngine.getInstance().getSkill(getOwner(), 19266, 55, getOwner()).useSkill();
				}
			}
		}, 5000, 70000);
	}

	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
	}

	private void scheduleTask() {
		task = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run(){
				doTask();
			}
		}, 1000);
	}

	private void doTask(){
		Npc twin = getPosition().getWorldMapInstance().getNpc(216949);
		if (twin != null && !twin.getLifeStats().isAlreadyDead() && MathUtil.isIn3dRange(getOwner(), twin, 5)) {
			if(getEffectController().hasAbnormalEffect(19266) || getEffectController().hasAbnormalEffect(19159)){
				getEffectController().removeEffect(19266);
				getEffectController().removeEffect(19159);
			} else {
				getLifeStats().setCurrentHp(Math.min(getLifeStats().getCurrentHp() + 2000, getLifeStats().getMaxHp()));
			}
		}
		scheduleTask();
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		getEffectController().removeEffect(19266);
		getEffectController().removeEffect(19159);
	}
}
