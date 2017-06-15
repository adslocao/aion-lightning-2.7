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
package com.aionemu.gameserver.questEngine.handlers.models;

import gnu.trove.list.array.TIntArrayList;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.KillInWorld;


/**
 * @author vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KillInWorldData")
public class KillInWorldData extends XMLQuest {
	
	@XmlAttribute(name = "end_npc_id")
	protected int endNpc;
	@XmlAttribute(name = "end_npc_id2")
	protected int endNpc2;
	@XmlAttribute(name = "start_npc_id")
	protected int startNpc;
	@XmlAttribute(name = "start_npc_id2")
	protected int startNpc2;
	@XmlAttribute(name = "amount")
	protected int amount;
	@XmlElement(name = "world", required = true)
	protected List<WorldData> worlds;

	@Override
	public void register(QuestEngine questEngine) {
		TIntArrayList worldIds = new TIntArrayList();
		for (WorldData world : worlds) {
			worldIds.add(world.getWorldId());
		}
		if (worldIds.size() == 1 && worldIds.get(0) == 0) {
			Iterator<WorldMapTemplate> itr = DataManager.WORLD_MAPS_DATA.iterator();
			worldIds.clear();
			while (itr.hasNext()) {
				WorldMapTemplate template = itr.next();
				worldIds.add(template.getMapId());
			}
		}
		KillInWorld template = new KillInWorld(id, endNpc, endNpc2, startNpc, startNpc2, worldIds, amount);
		questEngine.addQuestHandler(template);
	}
}
