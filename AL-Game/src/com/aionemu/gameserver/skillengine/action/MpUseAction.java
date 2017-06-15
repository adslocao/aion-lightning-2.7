/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 * @see Ferosia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MpUseAction")
public class MpUseAction extends Action {

	@XmlAttribute(required = true)
	protected int value;

	@XmlAttribute
	protected int delta;

	@XmlAttribute
	protected boolean ratio;

	@Override
	public void act(Skill skill) {
		Creature effector = skill.getEffector();
		if(((Player) effector).isInvul() && ((Player) effector).isGM()) {
			// No MP reducing only if effector is GM and is Invulnerable
			return;
		}
		int valueWithDelta = value + delta * skill.getSkillLevel();
		if (ratio)
			valueWithDelta = (int) ((skill.getEffector().getLifeStats().getMaxMp() * valueWithDelta) / 100);
		int changeMpPercent = skill.getBoostSkillCost();
		if (changeMpPercent != 0) {
			// changeMpPercent is negative
			valueWithDelta = valueWithDelta - ((valueWithDelta / ((100 / changeMpPercent))));
		}

		effector.getLifeStats().reduceMp(valueWithDelta);
	}

}
