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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * @author ATracer
 * @author kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealOverTimeEffect")
public abstract class HealOverTimeEffect extends AbstractOverTimeEffect {

	public void calculate(Effect effect, HealType healType) {
		if (!super.calculate(effect, null, null))
			return;
		
		Creature effector = effect.getEffector();

		int valueWithDelta = value + delta * effect.getSkillLevel();
		int maxCurValue = getMaxStatValue(effect);
		int possibleHealValue = 0;
		if (percent)
			possibleHealValue = maxCurValue * valueWithDelta / 100;
		else
			possibleHealValue = valueWithDelta;
		
		int finalHeal = possibleHealValue;
		
		if (!percent && healType == HealType.HP && effect.getItemTemplate() == null) {
			int baseHeal = possibleHealValue;
			int boostHealAdd = effector.getGameStats().getStat(StatEnum.HEAL_BOOST, 0).getCurrent();
			// Apply percent Heal Boost bonus (ex. Passive skills)
			int boostHeal = (effector.getGameStats().getStat(StatEnum.HEAL_BOOST, baseHeal).getCurrent() - boostHealAdd);
			// Apply Add Heal Boost bonus (ex. Skills like Benevolence)
			if (boostHealAdd > 0)
				boostHeal += Math.round(boostHeal * boostHealAdd / 1000);
			finalHeal = effect.getEffected().getGameStats().getStat(StatEnum.HEAL_SKILL_BOOST, boostHeal).getCurrent();
		}
		effect.setReservedInt(position, finalHeal);
		effect.addSucessEffect(this);
	}

	public void onPeriodicAction(Effect effect, HealType healType) {
		Creature effected = effect.getEffected();

		int currentValue = getCurrentStatValue(effect);
		int maxCurValue = getMaxStatValue(effect);
		int possibleHealValue = effect.getReservedInt(position);
		
		int healValue = maxCurValue - currentValue < possibleHealValue ? (maxCurValue - currentValue) : possibleHealValue;
		
		if (healValue <= 0)
			return;
		
		switch (healType) {
			case HP:
				effected.getLifeStats().increaseHp(TYPE.HP, healValue, effect.getSkillId(), LOG.HEAL);
				break;
			case MP:
				effected.getLifeStats().increaseMp(TYPE.MP, healValue, effect.getSkillId(), LOG.MPHEAL);
				break;
			case FP:
				((Player)effected).getLifeStats().increaseFp(TYPE.FP, healValue, effect.getSkillId(), LOG.FPHEAL);
				break;
			case DP:
				((Player)effected).getCommonData().addDp(healValue);
				break;
		}

	}
	
	protected abstract int getCurrentStatValue(Effect effect);
	protected abstract int getMaxStatValue(Effect effect);
}
