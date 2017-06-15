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

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillType;

/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OneTimeBoostSkillCriticalEffect")
public class OneTimeBoostSkillCriticalEffect extends BufEffect {

	@XmlAttribute
	private int count;
	@XmlAttribute
	private int value;//TODO handle this
	@XmlAttribute
	private boolean percent;//TODO handle this

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		effect.getEffector().setOneTimeBoostSkillCritical(true);

		final int stopCount = count;

		// Count Physical Skills
		ActionObserver observer = new ActionObserver(ObserverType.SKILLUSE) {

			private int count = 0;

			@Override
			public void skilluse(Skill skill) {
				if (count == stopCount)
					effect.endEffect();

				if ((count < stopCount) && (skill.getSkillTemplate().getType() == SkillType.PHYSICAL))
					count++;
			}
		};

		// TODO: verify if the effect counts normal hits too

		effect.getEffected().getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		effect.getEffector().setOneTimeBoostSkillCritical(false);
		ActionObserver observer = effect.getActionObserver(position);
		effect.getEffected().getObserveController().removeObserver(observer);
	}
}
