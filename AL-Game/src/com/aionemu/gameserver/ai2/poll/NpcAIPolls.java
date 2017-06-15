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
package com.aionemu.gameserver.ai2.poll;

import com.aionemu.gameserver.ai2.NpcAI2;

/**
 * @author ATracer
 */
public class NpcAIPolls {

	/**
	 * @param npcAI
	 */
	public static AIAnswer shouldDecay(NpcAI2 npcAI) {
		return AIAnswers.POSITIVE;
	}

	/**
	 * @param npcAI
	 * @return
	 */
	public static AIAnswer shouldRespawn(NpcAI2 npcAI) {
		if (npcAI.getOwner().isInInstance()) {
			return AIAnswers.NEGATIVE;
		}
		return AIAnswers.POSITIVE;
	}

}
