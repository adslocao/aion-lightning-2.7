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
package com.aionemu.gameserver.questEngine.task;

import java.util.concurrent.Future;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class QuestTasks {

	/**
	 * Schedule new following checker task
	 * 
	 * @param player
	 * @param npc
	 * @param target
	 * @return
	 */
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc target) {
		final Npc npc = (Npc) env.getVisibleObject();
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new FollowingNpcCheckTask(env, new TargetDestinationChecker(npc, target)), 1000, 1000);
	}

	/**
	 * Schedule new following checker task
	 * 
	 * @param player
	 * @param npc
	 * @param npcTargetId
	 * @return
	 */
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, int npcTargetId) {
		final Npc npc = (Npc) env.getVisibleObject();
		SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(npc.getWorldId(), npcTargetId);
		if (searchResult == null) {
			throw new IllegalArgumentException("Supplied npc doesn't exist: " + npcTargetId);
		}
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, searchResult.getSpot().getX(), searchResult
				.getSpot().getY(), searchResult.getSpot().getZ())), 1000, 1000);
	}

	/**
	 * Schedule new following checker task
	 * 
	 * @param env
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, float x, float y, float z) {
		final Npc npc = (Npc) env.getVisibleObject();
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(
			new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, x, y, z)), 1000, 1000);
	}
}
