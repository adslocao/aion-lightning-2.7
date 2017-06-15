package ai.instance.beshmundirTemple;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;

/**
 * @author joelc
 *
 */
@AIName("abhanathewicked")
public class AbhanaTheWicked extends AggressiveNpcAI2 {
	private boolean figthStart = false;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (figthStart) {
			return;
		}
		figthStart = true;
		startFight(40);
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
	}

	@Override
	public void handleDied() {
		super.handleDied();
		cancelTask();
	}

	private void startFight(int delay) {
		SkillEngine.getInstance().getSkill(getOwner(), 18892, 50, getTarget()).useSkill();
		schedulSkill(delay);
	}

	private void schedulSkill(final int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (figthStart) {
					startFight(delay);
				}
			}

		}, (delay + Rnd.get(10) - 5) * 1000);
	}

	private void cancelTask() {
		figthStart = false;
	}
}
