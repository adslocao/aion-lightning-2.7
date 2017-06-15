package ai.instance.beshmundirTemple;

import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("fixit")
public class Fixit extends AggressiveNpcAI2 {
	private Npc dorakiki = null;
	private Future<?> skillHeal;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();

		dorakiki = getPosition().getWorldMapInstance().getNpc(216250);
		scheduleHeal(5);
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		cancel();
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancel();
	}

	private void scheduleHeal(final int delay) {
		skillHeal = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				heal(delay);
			}

		}, delay * 1000);
	}

	private void heal(int delay) {
		SkillEngine.getInstance().getSkill(getOwner(), 18971, 55, dorakiki).useSkill();
		scheduleHeal(delay);
	}

	private void cancel(){
		if (skillHeal != null && !skillHeal.isCancelled()) {
			skillHeal.cancel(true);
		}
	}
}
