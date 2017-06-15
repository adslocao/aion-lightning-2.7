/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.questEngine.handlers.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import javolution.util.FastMap;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.MonsterHunt;

/**
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonsterHuntData", propOrder = { "monster" })
@XmlSeeAlso({
    KillSpawnedData.class,
    MentorMonsterHuntData.class
})
public class MonsterHuntData extends XMLQuest {

	@XmlElement(name = "monster", required = true)
	protected List<Monster> monster;
	@XmlAttribute(name = "start_npc_id")
	protected int startNpcId;
	@XmlAttribute(name = "start_npc_id2")
	protected int startNpcId2;
	@XmlAttribute(name = "end_npc_id")
	protected int endNpcId;
	@XmlAttribute(name = "end_npc_id2")
	protected int endNpcId2;

	@Override
	public void register(QuestEngine questEngine) {
		FastMap<Integer, Monster> monsterNpcs = new FastMap<Integer, Monster>();
		for (Monster m : monster)
			monsterNpcs.put(m.getNpcId(), m);
		MonsterHunt template = new MonsterHunt(id, startNpcId, startNpcId2, endNpcId, endNpcId2, monsterNpcs);
		questEngine.addQuestHandler(template);
	}

}
