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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.ProvokeTarget;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author ATracer modified by kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProvokerEffect")
public class ProvokerEffect extends ShieldEffect {

	@XmlAttribute(name = "provoke_target")
	protected ProvokeTarget provokeTarget;
	@XmlAttribute(name = "skill_id")
	protected int skillId;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void startEffect(Effect effect) {
		ActionObserver observer = null;
		final Creature effector = effect.getEffector();
		final int prob2 = this.hitTypeProb;
		final int radius = this.radius;
		switch (this.hitType) {
			case NMLATK://ATTACK
				observer = new ActionObserver(ObserverType.ATTACK) {

					@Override
					public void attack(Creature creature) {
						if (Rnd.get(0, 100) <= prob2) {
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target, creature);
						}
					}

				};
				break;
			case EVERYHIT://ATTACKED
				observer = new ActionObserver(ObserverType.ATTACKED) {

					@Override
					public void attacked(Creature creature) {
						if (radius > 0) {
							if (!MathUtil.isIn3dRange(effector, creature, radius))
								return;
						}
						if (Rnd.get(0, 100) <= prob2) {
							Creature target = getProvokeTarget(provokeTarget, effector, creature);
							createProvokedEffect(effector, target, creature);
						}
					}
				};
				break;
				//TODO MAHIT and PHHIT
		}

		if (observer == null)
			return;

		effect.setActionObserver(observer, position);
		effect.getEffected().getObserveController().addObserver(observer);
	}

	/**
	 * @param effector
	 * @param target
	 */
	private void createProvokedEffect(final Creature effector, Creature target, final Creature attacker) {
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		/**
		I dont see a reason for such code
		boolean isTargetRelationEnemy = (template.getProperties() == null) ? false : (template.getProperties().getTargetRelation() == TargetRelationAttribute.ENEMY);
		Effect e = null;
		if ((isTargetRelationEnemy) && (provokeTarget == ProvokeTarget.ME))
			e = new Effect(attacker, target, template, template.getLvl(), template.getEffectsDuration());
		else
			e = new Effect(effector, target, template, template.getLvl(), template.getEffectsDuration());
		*/
		Effect e = new Effect(effector, target, template, template.getLvl(), template.getEffectsDuration());
		e.initialize();
		e.applyEffect();
	}

	/**
	 * @param provokeTarget
	 * @param effector
	 * @param target
	 * @return
	 */
	private Creature getProvokeTarget(ProvokeTarget provokeTarget, Creature effector, Creature target) {
		switch (provokeTarget) {
			case ME:
				return effector;
			case OPPONENT:
				return target;
		}
		throw new IllegalArgumentException("Provoker target is invalid " + provokeTarget);
	}

	@Override
	public void endEffect(Effect effect) {
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
