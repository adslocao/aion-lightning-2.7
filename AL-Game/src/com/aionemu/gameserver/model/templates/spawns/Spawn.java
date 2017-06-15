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

import com.aionemu.gameserver.model.templates.event.EventTemplate;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * @author xTz
 * @modified Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spawn")
public class Spawn {

	@XmlAttribute(name = "custom")
	private Boolean isCustom = false;

	@XmlAttribute(name = "handler")
	private SpawnHandlerType handler;

	@XmlAttribute(name = "pool")
	private Integer pool = 0;

	@XmlAttribute(name = "respawn_time")
	private Integer respawnTime = 0;

	@XmlAttribute(name = "spawn_time")
	private SpawnTime spawnTime = SpawnTime.ALL;

	@XmlAttribute(name = "npc_id", required = true)
	private int npcId;

	@XmlElement(name = "spot")
	private List<SpawnSpotTemplate> spawnTemplates;

	@XmlTransient
	private EventTemplate eventTemplate;

	public Spawn() {
	}

	public Spawn(int npcId, int respawnTime, SpawnHandlerType handler) {
		this.npcId = npcId;
		this.respawnTime = respawnTime;
		this.handler = handler;
	}

	void beforeMarshal(Marshaller marshaller) {
		if (spawnTime == SpawnTime.ALL)
			spawnTime = null;
		if (pool == 0)
			pool = null;
		if (isCustom == false)
			isCustom = null;
	}

	void afterMarshal(Marshaller marshaller) {
		if (isCustom == null)
			isCustom = false;
		if (spawnTime == null)
			spawnTime = SpawnTime.ALL;
		if (pool == null)
			pool = 0;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getPool() {
		return pool;
	}

	public SpawnTime getSpawnTime() {
		return spawnTime;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public SpawnHandlerType getSpawnHandlerType() {
		return handler;
	}

	public List<SpawnSpotTemplate> getSpawnSpotTemplates() {
		if (spawnTemplates == null)
			spawnTemplates = new ArrayList<SpawnSpotTemplate>();
		return spawnTemplates;
	}

	public void addSpawnSpot(SpawnSpotTemplate template) {
		getSpawnSpotTemplates().add(template);
	}

	public boolean isCustom() {
		return isCustom == null ? false : isCustom;
	}

	public void setCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}

	public boolean isEventSpawn() {
		return eventTemplate != null;
	}

	public EventTemplate getEventTemplate() {
		return eventTemplate;
	}

	public void setEventTemplate(EventTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
	}
}
