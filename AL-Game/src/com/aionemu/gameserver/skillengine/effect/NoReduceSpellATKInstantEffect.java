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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoReduceSpellATKInstantEffect")
public class NoReduceSpellATKInstantEffect extends DamageEffect {

	@XmlAttribute
	protected boolean percent;

	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, null, null))
			return;
		
		int valueWithDelta = value + delta * effect.getSkillLevel();
		if (percent)
			valueWithDelta = ((effect.getEffected().getLifeStats().getMaxHp() * value) / 100);
		AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, 0, getElement(), true, true);
	}
}
