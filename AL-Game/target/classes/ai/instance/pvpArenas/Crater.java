/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.pvpArenas;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("pvp_crater")
public class Crater extends AggressiveNpcAI2 {
	private boolean isActive = false;
	private static final int timer = 60000;
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.POSITIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return AIAnswers.NEGATIVE;
		}
	}
	
	@Override
	public AttackIntention chooseAttackIntention() {
		return AttackIntention.SKILL_ATTACK;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		isActive = true;
		//scheduleEruption();
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		isActive = false;
	}
	
	//TODO What is this unused private method ?!
	@SuppressWarnings("unused")
	private void scheduleEruption() {
		if (!isActive) {
			return;
		}
		int timerRnd = Rnd.get(0, 10) - 5;

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				// 20056 lava eruption ou 20055 ou 20057
				SkillEngine.getInstance().getSkill(getOwner(), 20057, 50, getOwner()).useSkill();
				scheduleEruption();
			}

		}, timer + timerRnd * 1000);
	}

}