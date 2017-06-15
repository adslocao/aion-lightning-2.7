package ai.instance.beshmundirTemple;

import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("capitainelakharabesh")
public class CaptainLakhara extends AggressiveNpcAI2 {
	private boolean figthStart = false;
	private boolean spellTree1 = false;
	private boolean spellTree2 = false;
	private boolean finalStrik = false;

	private Future<?> skillTask1;
	private Future<?> skillTask2;
	private Future<?> skillTaskFinal;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		startFight();
		checkPercentage(getLifeStats().getHpPercentage());
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

	private void cancelTask() {
		figthStart = false;
		spellTree1 = false;
		spellTree2 = false;
		finalStrik = false;
		if (skillTask1 != null && !skillTask1.isDone()) {
			skillTask1.cancel(true);
		}
		if (skillTask2 != null && !skillTask2.isDone()) {
			skillTask2.cancel(true);
		}
		if (skillTaskFinal != null && !skillTaskFinal.isDone()) {
			skillTaskFinal.cancel(true);
		}
	}

	private void startFight() {
		if (figthStart) {
			return;
		}
		figthStart = true;
		SkillEngine.getInstance().getSkill(getOwner(), 18994, 50, getTarget()).useSkill();
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 70 && !spellTree1) {
			spellTree1 = true;
			doSkillTree1();
		}
		if (hpPercentage <= 40 && !spellTree2) {
			spellTree2 = true;
			doSkillTree2();
		}
		if (hpPercentage <= 20 && !finalStrik) {
			finalStrik = true;
			diFinalStrik();
		}
	}

	private void doSkillTree1() {
		PacketSendUtility.broadcastPacketAndReceive(getOwner(),
				new SM_MESSAGE(getOwner().getObjectId(), getOwner().getName(), "Face my wrath", ChatType.NORMAL));
		skillTask1 = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 18202, 45, getTarget()).useSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(getOwner(), 19090, 45, getTarget()).useSkill();
					}

				}, 5000);
			}

		}, 5000);
	}

	private void doSkillTree2() {
		PacketSendUtility.broadcastPacketAndReceive(getOwner(),
				new SM_MESSAGE(getOwner().getObjectId(), getOwner().getName(), "Face my wrath", ChatType.NORMAL));
		skillTask2 = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 18202, 45, getTarget()).useSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						SkillEngine.getInstance().getSkill(getOwner(), 19090, 45, getTarget()).useSkill();
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								SkillEngine.getInstance().getSkill(getOwner(), 18994, 45, getTarget()).useSkill();
							}

						}, 1000);
					}

				}, 5000);
			}

		}, 5000);
	}

	private void diFinalStrik() {

		PacketSendUtility.broadcastPacketAndReceive(getOwner(),
				new SM_MESSAGE(getOwner().getObjectId(), getOwner().getName(), "You will be doomed", ChatType.NORMAL));

		skillTaskFinal = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 18891, 50, getTarget()).useSkill();
			}

		}, 30000);
	}

}
