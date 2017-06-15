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
package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.SummonedObject;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author Tomate
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HpCondition")
public class HpCondition extends Condition {

	@XmlAttribute(required = true)
	protected int value;

	@XmlAttribute
	protected int delta;
	
	@XmlAttribute
	protected boolean ratio;

	@Override
	public boolean validate(Skill skill) {
		//exception for Servants, Totems to let them cast last skill and die
		if (skill.getEffector() instanceof SummonedObject)
			return true;
		
		int valueWithDelta = value + delta * skill.getSkillLevel();
		if (ratio)
			valueWithDelta = (int) ((skill.getEffector().getLifeStats().getMaxHp() * valueWithDelta) / 100);
		return skill.getEffector().getLifeStats().getCurrentHp() > valueWithDelta;
	}
}
