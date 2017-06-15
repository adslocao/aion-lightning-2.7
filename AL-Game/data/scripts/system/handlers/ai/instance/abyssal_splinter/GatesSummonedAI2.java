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
package ai.instance.abyssal_splinter;

import java.util.concurrent.Future;

import ai.GeneralNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;

/**
 *
 * @author Ritsu
 */
@AIName("gatessummoned")
public class GatesSummonedAI2 extends GeneralNpcAI2 {

	private Future<?> eventTask;
	private boolean canThink = true;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		startMove();
	}

	@Override
	public boolean canThink() {
		return canThink;
	}

	@Override
	protected void handleDied() {
		cancelEventTask();
		super.handleDied();
		AI2Actions.deleteOwner(GatesSummonedAI2.this);
	}

	@Override
	protected void handleDespawned() {
		cancelEventTask();
		super.handleDespawned();
	}

	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		startEventTask();
	}

	private void startMove()
	{
		final AbstractAI ia = this;
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if(isAlreadyDead()){
					return;
				}
				canThink = false;
				EmoteManager.emoteStopAttacking(getOwner());
				setStateIfNot(AIState.FOLLOWING);
				getOwner().setState(1);
				Npc boss = (getPosition().getWorldMapInstance().getNpc(216960) == null ? getPosition().getWorldMapInstance().getNpc(216952) : getPosition().getWorldMapInstance().getNpc(216960));
				AI2Actions.targetCreature(ia, boss);
				getMoveController().moveToTargetObject();
			}
		}, 2000);
		
	}

	private void cancelEventTask() {
		if (eventTask != null && !eventTask.isDone()) {
			eventTask.cancel(true);
		}
	}

	private void startEventTask() {
		eventTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Npc boss1 = getPosition().getWorldMapInstance().getNpc(216960);
				Npc boss2 = getPosition().getWorldMapInstance().getNpc(216952);
				Npc boss = ( boss1 == null ? boss2 : boss1);
				if ((boss1 == null && boss2 == null) || isAlreadyDead() || getOwner() == null)
					cancelEventTask();
				else{
					if(Rnd.get(1) == 0)
						SkillEngine.getInstance().getSkill(getOwner(), 19257, 55, boss).useSkill();
					else
						SkillEngine.getInstance().getSkill(getOwner(), 19281, 55, boss).useSkill();
				}
				AI2Actions.deleteOwner(GatesSummonedAI2.this);
			}

		}, 5000, 30000);

	}
}