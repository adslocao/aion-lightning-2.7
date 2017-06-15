package ai.instance.beshmundirTemple;

import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;


/**
 * @author Kairyu
 */
@AIName("isbariyaServants")
public class IsbariyaServantsAI2 extends AggressiveNpcAI2 {

	private Future<?> skillTaskDispel;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		int lifetime = (getNpcId() == 281659 ? 20000 : 6000);
		toDespawn(lifetime);
		if(getNpcId() == 281659){
			dispelIsba();
		}
	}

	private void dispelIsba() {
		SkillEngine.getInstance().getSkill(getOwner(), 18980, 55, getPosition().getWorldMapInstance().getNpc(216263)).useSkill();
		scheulDispelIsba();
	}

	private void scheulDispelIsba() {
		skillTaskDispel = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				dispelIsba();
			}
		}, 1000);
	}

	private void toDespawn(int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				AI2Actions.deleteOwner(IsbariyaServantsAI2.this);
			}
		}, delay);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		if (skillTaskDispel != null && !skillTaskDispel.isCancelled()) {
			skillTaskDispel.cancel(true);
		}
	}
}
