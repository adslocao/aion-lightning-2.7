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
package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xTz
 * @modified Rolandas
 */
public class SpawnGroup2 {

	private int worldId;
	private int npcId;
	private int pool;
	private SpawnTime spawnTime = SpawnTime.ALL;
	private int respawnTime;
	private SpawnHandlerType handlerType;
	private List<SpawnTemplate> spots = new ArrayList<SpawnTemplate>();

	public SpawnGroup2(int worldId, Spawn spawn) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SpawnTemplate spawnTemplate = new SpawnTemplate(this, template);
			if (spawn.isEventSpawn())
				spawnTemplate.setEventTemplate(spawn.getEventTemplate());
			spots.add(spawnTemplate);
		}
	}

	public SpawnGroup2(int worldId, Spawn spawn, int siegeId, SiegeRace race, SiegeModType mod) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SiegeSpawnTemplate spawnTemplate = new SiegeSpawnTemplate(this, template);
			spawnTemplate.setSiegeId(siegeId);
			spawnTemplate.setSiegeRace(race);
			spawnTemplate.setSiegeModType(mod);
			spots.add(spawnTemplate);
		}
	}

	private void initializing(Spawn spawn) {
		spawnTime = spawn.getSpawnTime();
		respawnTime = spawn.getRespawnTime();
		pool = spawn.getPool();
		npcId = spawn.getNpcId();
		handlerType = spawn.getSpawnHandlerType();
	}

	public SpawnGroup2(int worldId, int npcId) {
		this.worldId = worldId;
		this.npcId = npcId;
	}

	public List<SpawnTemplate> getSpawnTemplates() {
		return spots;
	}

	public void addSpawnTemplate(SpawnTemplate spawnTemplate) {
		spots.add(spawnTemplate);
	}

	public int getWorldId() {
		return worldId;
	}

	public int getNpcId() {
		return npcId;
	}

	public SpawnTime getSpawnTime() {
		return spawnTime;
	}

	public int getPool() {
		return pool;
	}

	public boolean hasPool() {
		return pool > 0;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public boolean isPartialDaySpawn() {
		return !getSpawnTime().equals(SpawnTime.ALL);
	}

	public SpawnHandlerType getHandlerType() {
		return handlerType;
	}

	public synchronized SpawnTemplate getRndTemplate() {
		List<SpawnTemplate> templates = new ArrayList<SpawnTemplate>();
		for (SpawnTemplate template : spots) {
			if (!template.isUsed()) {
				templates.add(template);
			}
		}
		SpawnTemplate spawnTemplate = templates.get(Rnd.get(0, templates.size() - 1));
		spawnTemplate.setUse(true);
		return spawnTemplate;
	}
}
