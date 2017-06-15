/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.beshmundirTemple;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;


/**
 * @author Luzien
 */
@AIName("templeSoul")
public class SacrificialSoulAI2 extends AggressiveNpcAI2 {
	private Npc boss;
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		//AI2Actions.useSkill(this, 18901);
		this.setStateIfNot(AIState.FOLLOWING);
		boss = getPosition().getWorldMapInstance().getNpc(216263);
		final SacrificialSoulAI2 soul = this;
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {

				if (boss != null && !NpcActions.isAlreadyDead(boss) && !NpcActions.isAlreadyDead(soul.getOwner())) {
					AI2Actions.targetCreature(soul, boss);
					getMoveController().moveToTargetObject();
				}
			}
		}, 2000);
	}
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (creature.getEffectController().hasAbnormalEffect(18959)) {

			int souls = getPosition().getWorldMapInstance().getNpcs(getNpcId()).size();
			if(souls <= 1){
				creature.getEffectController().removeEffect(18959);
			}
			
			getMoveController().abortMove();
			getOwner().getController().die();
			AI2Actions.deleteOwner(this);
		}
	}
	
	@Override
	protected void handleMoveArrived() {
		if (boss != null && !NpcActions.isAlreadyDead(boss)) {
			SkillEngine.getInstance().getSkill(getOwner(), 18960, 55, boss).useSkill();
			AI2Actions.deleteOwner(this);
		}
	}
	
	@Override
	public boolean canThink() {
		return false;
	}
	
	@Override
	public int modifyDamage(int damage)	{
		return 2;
	}
}
