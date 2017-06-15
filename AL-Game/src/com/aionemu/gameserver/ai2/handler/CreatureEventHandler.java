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
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class CreatureEventHandler {

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onCreatureMoved(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
	}

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onCreatureSee(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
	}

	/**
	 * @param ai
	 * @param creature
	 */
	protected static void checkAggro(NpcAI2 ai, Creature creature) {
		Npc owner = ai.getOwner();
		if (creature.getLifeStats().isAlreadyDead()) {
			return;
		}
		if (!owner.canSee(creature) || !GeoService.getInstance().canSee(owner, creature))
			return;
		
		if (!owner.getActiveRegion().isMapRegionActive()) {
			return;
		}
		if (!ai.isInState(AIState.FIGHT) && MathUtil.isIn3dRange(owner, creature, owner.getAggroRange())) {
			if (ai.poll(AIQuestion.CAN_SHOUT)) {
				ShoutEventHandler.onSee(ai, creature);
			}
			if (owner.isAggressiveTo(creature)) {
				if (GeoService.getInstance().canSee(owner, creature)) {
				if (ai.canThink())
					ai.onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
				}
			}
		}
	}

}
