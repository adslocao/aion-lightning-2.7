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

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.spawnengine.WalkerGroup;

/**
 * @author ATracer
 */
public class ReturningEventHandler {

	/**
	 * @param npcAI
	 */
	public static void onNotAtHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onNotAtHome");
		}
		if (npcAI.setStateIfNot(AIState.RETURNING)) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "returning and restoring");
			}
			EmoteManager.emoteStartReturning(npcAI.getOwner());
			Npc npc = (Npc) npcAI.getOwner();
			SpawnTemplate spawn =npc.getSpawn();
			Point2D dest = null;
			Point2D origin = new Point2D(spawn.getX(), spawn.getY());
			if (npc.hasWalkRoutes()) {
				WalkManager.startWalking(npcAI);
			}
			if (npc.getWalkerGroup() != null) {
				npc.getWalkerGroup().setStep(npc, 1);
				dest = WalkerGroup.getLinePoint(new Point2D(npc.getX(), npc.getY()), origin, npc.getWalkerGroupShift());
			}
			else {
				dest = origin;
			}
			npcAI.getOwner().getMoveController().moveToPoint(dest.getX(), dest.getY(), spawn.getZ());
		}
	}

	/**
	 * @param npcAI
	 */
	public static void onBackHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onBackHome");
		}
		if (npcAI.setStateIfNot(AIState.IDLE)) {
			EmoteManager.emoteStartIdling(npcAI.getOwner());
			ThinkEventHandler.thinkIdle(npcAI);
		}
	}

}
