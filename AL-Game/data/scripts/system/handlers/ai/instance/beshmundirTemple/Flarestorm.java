package ai.instance.beshmundirTemple;

import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("flarestorm")
public class Flarestorm extends AggressiveNpcAI2  {

	private boolean figthStart = false;
	private boolean wave1 = false;
	private boolean wave2 = false;
	private boolean wave3 = false;
	private Future<?> skillTask = null;
	private Future<?> skillSuaire = null;
	private Future<?> skillOrbe = null;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		if(!figthStart){
			figthStart = true;
			scheduleSkillTree(40);
		}
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

	private void checkPercentage(int hpPercentage) {
		if(hpPercentage <= 75 && !wave1){
			wave1 = true;
			spawnAdd(1);
		}
		if(hpPercentage <= 50 && !wave2){
			wave2 = true;
			spawnAdd(2);
		}
		if(hpPercentage <= 25 && !wave3){
			wave3 = true;
			spawnAdd(3);
		}
	}

	private void spawnAdd(int i) {
		if(i >= 1){
			spawn(281646, 1532.06f, 1047.05f, 273.665f, (byte) 0);
			spawn(281646, 1529.06f, 1050.05f, 273.665f, (byte) 0);
			spawn(281646, 1504.06f, 1032.05f, 273.665f, (byte) 0);
			spawn(281646, 1502.06f, 1038.05f, 272.665f, (byte) 0);
		}
		if(i >= 2){
			spawn(281646, 1526.06f, 1048.05f, 273.665f, (byte) 0);
			spawn(281646, 1498.06f, 139.05f, 272.665f, (byte) 0);
		}
		if(i >= 3){
			spawn(281646, 1534.06f, 1051.05f, 272.665f, (byte) 0);
			spawn(281646, 1496.06f, 1044.05f, 271.765f, (byte) 0);
		}
	}

	private void doSkillTree(final int delay){
		SkillEngine.getInstance().getSkill(getOwner(), 18909, 50, getTarget()).useSkill();
		skillSuaire = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 18910, 50, getTarget()).useSkill();

				skillOrbe = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(getOwner(), 18911, 50, getTarget()).useSkill();
						scheduleSkillTree(delay);
					}

				}, Rnd.get(10, 24) * 1000);
			}

		}, 2000);

	}

	private void scheduleSkillTree(final int delay){
		skillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				doSkillTree(delay);
			}

		}, (delay + Rnd.get(20) - 10) * 1000);
	}

	private void cancelTask() {
		figthStart = false;
		wave1 = false;
		wave2 = false;
		wave3 = false;
		if (skillTask != null && !skillTask.isDone()) {
			skillTask.cancel(true);
		}
		if (skillSuaire != null && !skillSuaire.isDone()) {
			skillSuaire.cancel(true);
		}
		if (skillOrbe != null && !skillOrbe.isDone()) {
			skillOrbe.cancel(true);
		}

		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281698);
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
}
