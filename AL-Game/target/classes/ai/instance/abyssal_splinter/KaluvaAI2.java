package ai.instance.abyssal_splinter;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.ai.Percentage;

import ai.SummonerAI2;

/**
 * @author Luzien
 */
@AIName("kaluva")
public class KaluvaAI2 extends SummonerAI2 {

	@Override
	protected void handleIndividualSpawnedSummons(Percentage percent) {
		spawn();
		AI2Actions.targetCreature(this, getPosition().getWorldMapInstance().getNpc(281902));
		getMoveController().moveToTargetObject(); // Move to spawn, in order to "hatch" it.
	}

	private void spawn() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(281902, 663.322021f, 556.731995f, 424.295013f, (byte) 64);
				break;
			case 2:
				spawn(281902, 644.0224f, 523.9641f, 423.09103f, (byte) 32);
				break;
			case 3:
				spawn(281902, 611.008f, 539.73395f, 423.25034f, (byte) 119);
				break;
			case 4:
				spawn(281902, 628.4426f, 585.4443f, 424.31854f, (byte) 93);
				break;
		}
	}

}
