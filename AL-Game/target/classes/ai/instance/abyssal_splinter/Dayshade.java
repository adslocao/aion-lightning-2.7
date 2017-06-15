package ai.instance.abyssal_splinter;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;

import ai.AggressiveNpcAI2;

/**
 * @author Luzien
 */
@AIName("dayshade")
public class Dayshade extends AggressiveNpcAI2 {

	private boolean isStart;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!isStart) {
			isStart = true;
			// TODO remove
			AI2Actions.useSkill(this, 19227);
			spawn(216948, 415.330994f, 664.830994f, 437.470001f, (byte) 10); // rukril
			spawn(216949, 447.037994f, 735.560974f, 437.490997f, (byte) 94); // ebonsoul
			AI2Actions.deleteOwner(this);
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		isStart = false;
	}
}
