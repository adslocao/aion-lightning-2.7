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
package com.aionemu.gameserver.spawnengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTime;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 */
public class DayTimeSpawnEngine {

	private static final Logger log = LoggerFactory.getLogger(DayTimeSpawnEngine.class);

	private static final List<SpawnGroup2> dayTimeSpawns = new ArrayList<SpawnGroup2>();

	private static final Set<Integer> spawnedObjectIds = new HashSet<Integer>();

	/**
	 * Spawn all objects using current day time
	 */
	public static void spawnAll() {
		onDayTimeChange(GameTimeManager.getGameTime().getDayTime(), false);
	}

	/**
	 * @param dayTime
	 */
	public static void onDayTimeChange(DayTime dayTime) {
		onDayTimeChange(dayTime, true);
	}

	private static void onDayTimeChange(final DayTime dayTime, boolean switchThread) {
		log.info("DayTimeSpawnEngine: new day time is " + dayTime);
		
		Runnable dayChangeSpawnTask = new Runnable() {
			@Override
			public void run() {
				int despawnedCounter = 0;
				Iterator<Integer> iterator = spawnedObjectIds.iterator();
				while (iterator.hasNext()) {
					Integer objectId = iterator.next();
					VisibleObject object = World.getInstance().findVisibleObject(objectId);
					if (object == null || !object.isSpawned()) {
						continue;
					}
					SpawnTemplate template = object.getSpawn();
					SpawnTime spawnTime = template.getSpawnTime();
					if (!spawnTime.isAllowedDuring(dayTime)) {
						iterator.remove();
						if (object instanceof Npc) {
							Npc npc = (Npc) object;
							if (!npc.getLifeStats().isAlreadyDead() && template.hasPool()) {
								template.setUse(false);
							}
						}
						object.getController().onDelete();
						despawnedCounter++;
					}
				}

				int spawnedCounter = 0;
				for (SpawnGroup2 spawn : dayTimeSpawns) {
					SpawnTime spawnTime = spawn.getSpawnTime();

					Collection<Integer> instances = World.getInstance().getWorldMap(spawn.getWorldId())
						.getAvailableInstanceIds();

					if (spawnTime.isAllowedDuring(dayTime) && (spawnTime.isNeedUpdate(dayTime) || spawnedObjectIds.isEmpty())) {
						for (Integer instanceId : instances) {
							if (spawn.hasPool()) {
								for (int pool = 0; pool < spawn.getPool(); pool++) {
									SpawnTemplate template = spawn.getRndTemplate();
									VisibleObject spawnObject = SpawnEngine.spawnObject(template, instanceId);
									spawnedObjectIds.add(spawnObject.getObjectId());
									spawnedCounter++;
								}
							}
							else {
								for(SpawnTemplate template : spawn.getSpawnTemplates()) {
									VisibleObject spawnObject = SpawnEngine.spawnObject(template, instanceId);
								spawnedObjectIds.add(spawnObject.getObjectId());
								spawnedCounter++;
								}
							}
						}
					}
				}
				log.info("DayTimeSpawnEngine: spawned " + spawnedCounter);
				log.info("DayTimeSpawnEngine: despawned " + despawnedCounter);
			}
		};

		if(switchThread)
			ThreadPoolManager.getInstance().execute(dayChangeSpawnTask);
		else
			dayChangeSpawnTask.run();
	}

	public static void addSpawnedObject(VisibleObject object) {
		if (object != null && object.isSpawned()) {
			spawnedObjectIds.add(object.getObjectId());
		}
	}

	/**
	 * @param spawnTemplate
	 */
	public static void addSpawnGroup(SpawnGroup2 spawn) {
		dayTimeSpawns.add(spawn);
	}
}
