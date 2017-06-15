package ai.instance.abyssal_splinter;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Npc;

@AIName("kaluvaspawn")
public class KaluvaSpawnAI2 extends NpcAI2 {

	@Override
	protected void handleDied() {
		super.handleDied();
		checkKaluva();
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				AI2Actions.targetSelf(KaluvaSpawnAI2.this);
				AI2Actions.useSkill(KaluvaSpawnAI2.this, 19223); // apply hatching buff(counter);
			}

		}, 1000);
		scheduleHatch();
	}

	private void checkKaluva() {
		Npc kaluva = getPosition().getWorldMapInstance().getNpc(216950);
		if (kaluva != null && !kaluva.getLifeStats().isAlreadyDead()) {
			kaluva.getEffectController().removeEffect(19152);
		}
		AI2Actions.deleteOwner(this);
	}

	private void scheduleHatch() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					hatchAdds();
					checkKaluva();
				}
			}

		}, 21000); // schedule hatch when debuff ends(20s)
	}

	private void hatchAdds() { // 4 different spawn-formations; See Powerwiki for more information
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(281911, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				spawn(281911, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				break;
			case 2:
				for (int i = 0; i < 12; i++) {
					spawn(281912, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				}
				break;
			case 3:
				spawn(282057, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				break;
			case 4:
				spawn(281911, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				spawn(281912, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				spawn(281912, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				spawn(281912, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
				break;
		}
	}

}
