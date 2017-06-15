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
package com.aionemu.gameserver.world.zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.geometry.Area;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneTemplate;
import com.aionemu.gameserver.world.zone.handler.AdvencedZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;

/**
 * @author ATracer
 */
public class ZoneInstance implements Comparable<ZoneInstance>{

	private ZoneInfo template;
	private int mapId;
	private Map<Integer, Creature> creatures = new FastMap<Integer, Creature>();
	protected List<ZoneHandler> handlers = new ArrayList<ZoneHandler>(); 
	
	public ZoneInstance(int mapId, ZoneInfo template) {
		this.template = template;
		this.mapId = mapId;
	}

	/**
	 * @return the template
	 */
	public Area getAreaTemplate() {
		return template.getArea();
	}

	/**
	 * @return the template
	 */
	public ZoneTemplate getZoneTemplate() {
		return template.getZoneTemplate();
	}
	
	/**
	 * @return the breath
	 */
	public boolean isBreath() {
		return template.getZoneTemplate().isBreath();
	}

	public boolean revalidate(Creature creature){
		return (mapId == creature.getWorldId() && template.getArea().isInside3D(creature.getX(), creature.getY(), creature.getZ()));
	}
	
	public synchronized boolean onEnter(Creature creature){
		if (creatures.containsKey(creature.getObjectId()))
			return false;
		creatures.put(creature.getObjectId(), creature);
		if (isBreath())
			creature.setInsideZoneType(ZoneType.WATER);
		creature.getController().onEnterZone(this);
		for (int i = 0 ; i < handlers.size(); i++)
			handlers.get(i).onEnterZone(creature, this);
		return true;
	}
	
	public synchronized boolean onLeave(Creature creature){
		if (!creatures.containsKey(creature.getObjectId()))
			return false;
		creatures.remove(creature.getObjectId());
		if (isBreath())
			creature.unsetInsideZoneType(ZoneType.WATER);
		creature.getController().onLeaveZone(this);
		for (int i = 0 ; i < handlers.size(); i++)
			handlers.get(i).onLeaveZone(creature, this);
		return true;
	}
	
	public boolean onDie(Creature attacker, Creature target){
		if (!creatures.containsKey(target.getObjectId()))
			return false;
		for (int i = 0 ; i < handlers.size(); i++){
			ZoneHandler handler = handlers.get(i);
			if (handler instanceof AdvencedZoneHandler){
				if (((AdvencedZoneHandler) handler).onDie(attacker, target, this))
					return true;
			}
		}
		return false;
	}

	public boolean isInsideCreature(Creature creature){
		return creatures.containsKey(creature.getObjectId());
	}
	
	public boolean isInsideCordinate(float x, float y, float z){
		return template.getArea().isInside3D(x, y, z);
	}

	@Override
	public int compareTo(ZoneInstance o) {
		int result = getZoneTemplate().getPriority()-o.getZoneTemplate().getPriority();
		if (result == 0){
			return template.getZoneTemplate().getName().ordinal()-o.template.getZoneTemplate().getName().ordinal();
		}
		return result;
	}
	
	public void addHandler(ZoneHandler handler){
		this.handlers.add(handler);
	}

	
	/**
	 * @return the creatures
	 */
	public Map<Integer, Creature> getCreatures() {
		return creatures;
	}
}
