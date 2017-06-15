package ai.instance.abyssal_splinter;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

import ai.AggressiveNpcAI2;

/**
 * @author Luzien
 */
@AIName("pazuzu")
public class PazuzuAI2 extends AggressiveNpcAI2 {

	private boolean isStart = false;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!isStart) {
			isStart = true;
			AI2Actions.useSkill(this, 19145);
			if (getPosition().getWorldMapInstance().getNpc(281909) == null) {
				spawnWorms();
			}
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		isStart = false;
	}

	private void spawnWorms() {
		Npc pazuzu = getPosition().getWorldMapInstance().getNpc(216951);
		Npc worms = getPosition().getWorldMapInstance().getNpc(281909);
		if (pazuzu != null && !pazuzu.getLifeStats().isAlreadyDead()) {
			if (worms == null) {
				spawn(281909, 651.351990f, 326.425995f, 465.523987f, (byte) 8);
				spawn(281909, 666.604980f, 314.497009f, 465.394012f, (byte) 27);
				spawn(281909, 685.588989f, 342.955994f, 465.908997f, (byte) 68);
				spawn(281909, 651.322021f, 346.554993f, 465.563995f, (byte) 111);
				spawn(281909, 666.7373f, 314.2235f, 465.38953f, (byte) 30);
				AI2Actions.useSkill(this, 19145);
			}
			scheduleRespawn();
		}
	}

	private void scheduleRespawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawnWorms();
			}

		}, 70000); // Powerwiki --> Worms are spawned every 70s
	}

}
