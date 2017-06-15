package ai.instance.beshmundirTemple;

import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("stormwing")
public class StormwingAI extends AggressiveNpcAI2 {
	private boolean startFigth = false;
	private Future<?> skillTaskTooth;
	private Future<?> skillTaskSpawn;
	private int nbTwister1 = 0;
	private int nbTwister2 = 0;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!startFigth) {
			startFigth = true;
			scheduleSkillTooth();
			spawnTwister();
		}
		checkPercentage(getLifeStats().getHpPercentage());
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
	}

	private void spawnTwister(){
		if(nbTwister1 < 3){
			rndSpawnInRange(281795);
			nbTwister1 += 1;
		}
		if(nbTwister2 < 2 && getLifeStats().getHpPercentage() <= 50){
			rndSpawnInRange(281797);
			nbTwister2 += 1;
		}
		scheduleSpawnTwister();
	}

	private void scheduleSpawnTwister(){
		skillTaskSpawn = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawnTwister();
			}
		}, 40000);
	}

	private void scheduleSkillTooth() {
		skillTaskTooth = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				doSkillTooth();
			}
		}, 2000);
	}

	private void doSkillTooth(){
		if(!MathUtil.isIn3dRange(getOwner(), getTarget(), 8) && MathUtil.isIn3dRange(getOwner(), getTarget(), 10)){
			if(Rnd.get(4) == 0){
				SkillEngine.getInstance().getSkill(getOwner(), 18615, 45, getTarget()).useSkill();
			}
		}
		scheduleSkillTooth();
	}

	private void checkPercentage(int hpPercentage) {

	}

	private Npc rndSpawnInRange(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * Rnd.get(28));
		float y1 = (float) (Math.sin(Math.PI * direction) * Rnd.get(28));
		return (Npc) spawn(npcId, 548 + x1, 1360 + y1, 225, (byte) 0);
	}

	private void cancelTask(){
		if (skillTaskTooth != null && !skillTaskTooth.isCancelled()) {
			skillTaskTooth.cancel(true);
		}
		if (skillTaskSpawn != null && !skillTaskSpawn.isCancelled()) {
			skillTaskSpawn.cancel(true);
		}
		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281797);
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
		npcs = getPosition().getWorldMapInstance().getNpcs(281795);
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
		getOwner().getEffectController().removeEffect(18617);
		nbTwister1 = 0;
		nbTwister2 = 0;
		startFigth = false;
	}
}
