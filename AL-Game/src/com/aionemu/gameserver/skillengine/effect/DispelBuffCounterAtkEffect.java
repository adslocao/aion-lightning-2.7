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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.effect.DamageEffect;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DispelBuffCounterAtkEffect")
public class DispelBuffCounterAtkEffect extends DamageEffect {

	@XmlAttribute
	protected int dpower;
	@XmlAttribute
	protected int power;
	@XmlAttribute
	protected int hitvalue;
	@XmlAttribute
	protected int hitdelta;
	@XmlAttribute(name = "dispel_level")
	protected int dispelLevel;

	@Override
	public void calculate(Effect effect) {
		if (!super.calculate(effect, null, null))
			return;
		
		Creature effected = effect.getEffected();
		int count = value + delta * effect.getSkillLevel();
		int finalPower = power + dpower * effect.getSkillLevel();
		
		int i = effected.getEffectController().calculateNumberOfEffects(DispelCategoryType.BUFF, SkillTargetSlot.BUFF, dispelLevel);
		i = (i < count ? i : count);
		
		int newValue = 0;
		if (i == 1)
			newValue = hitvalue;
		else if (i > 1)
			newValue = hitvalue + ((hitvalue / 2) * (i - 1));

		int valueWithDelta = newValue + hitdelta * effect.getSkillLevel();

		int bonus = getActionModifiers(effect);

		AttackUtil.calculateMagicalSkillResult(effect, valueWithDelta, bonus, getElement());

		// First cancel buffs so to avoid shield effects to calculate damage as 0
		//TOOD move to apply effect, this is not good
		effect.getEffected().getEffectController().removeEffectByDispelCat(DispelCategoryType.BUFF, SkillTargetSlot.BUFF
				, i, dispelLevel, finalPower, false);
	}
}
