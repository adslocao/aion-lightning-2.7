/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.effect;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonTrapEffect")
public class SummonTrapEffect extends SummonEffect {

	@XmlAttribute(name = "skill_id", required = true)
	protected int skillId;

	@Override
	public void applyEffect(Effect effect) {
		/*
		 * Creature effector = effect.getEffector(); float x = effector.getX();
		 * float y = effector.getY(); float z = effector.getZ(); byte heading =
		 * effector.getHeading(); int worldId = effector.getWorldId(); int
		 * instanceId = effector.getInstanceId();
		 * 
		 * SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId,
		 * npcId, x, y, z, heading); final Trap trap =
		 * VisibleObjectSpawner.spawnTrap(spawn, instanceId, effector, skillId);
		 * 
		 * Future<?> task = ThreadPoolManager.getInstance().schedule(new
		 * Runnable() {
		 * 
		 * @Override public void run() { trap.getController().onDelete(); } },
		 * time * 1000); trap.getController().addTask(TaskId.DESPAWN, task);
		 */

		final Creature effector = effect.getEffector();
		// should only be set if player has no target to avoid errors
		if (effect.getEffector().getTarget() == null)
			effect.getEffector().setTarget(effect.getEffector());
		float x = effect.getX();
		float y = effect.getY();
		float z = effect.getZ();
		if (x == 0 && y == 0) {
			Creature effected = effect.getEffected();
			x = effected.getX();
			y = effected.getY();
			z = effected.getZ();

			// Correction spawn trap
			double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
			x += (float) Math.cos(radian) * 2;
			y += (float) Math.sin(radian) * 2;
		}
		final byte heading = effector.getHeading();
		final int worldId = effector.getWorldId();
		final int instanceId = effector.getInstanceId();

		final SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				final Trap trap = VisibleObjectSpawner.spawnTrap(spawn, instanceId, effector, skillId);

				Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						trap.getController().onDelete();
					}
				}, time * 1000);
				trap.getController().addTask(TaskId.DESPAWN, task);
			}
		}, 330);
	}
}
