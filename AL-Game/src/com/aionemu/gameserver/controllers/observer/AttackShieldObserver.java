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
package com.aionemu.gameserver.controllers.observer;

import java.util.List;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HitType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.commons.utils.Rnd;

/**
 * @author ATracer modified by Sippolo, kecimis
 */
public class AttackShieldObserver extends AttackCalcObserver {

	private int hit;
	private int totalHit;
	private boolean percent;
	private Effect effect;
	private HitType hitType;
	private int shieldType;
	private int probability = 100;
	private int minradius = 0;

	/**
	 * @param percent
	 * @param value
	 * @param status
	 */
	public AttackShieldObserver(int hit, int totalHit, boolean percent, Effect effect, HitType type, int shieldType, int probability) {
		this(hit, totalHit, percent, effect, type, shieldType, probability, 0);
	}
	
	public AttackShieldObserver(int hit, int totalHit, boolean percent, Effect effect, HitType type, int shieldType, int probability, int minradius) {
		this.hit = hit;
		this.totalHit = totalHit;//totalHit is radius
		this.effect = effect;
		this.percent = percent;
		this.hitType = type;
		this.shieldType = shieldType;
		this.probability = probability;
		this.minradius = minradius;// implemented only for reflected shield
	}

	@Override
	public void checkShield(List<AttackResult> attackList, Creature attacker) {
		for (AttackResult attackResult : attackList) {
			
			if (AttackStatus.getBaseStatus(attackResult.getAttackStatus()) == AttackStatus.DODGE
				|| AttackStatus.getBaseStatus(attackResult.getAttackStatus()) == AttackStatus.RESIST)
				continue;
			// Handle Hit Types for Shields
			if (this.hitType != HitType.EVERYHIT) {
				if ((attackResult.getDamageType() != null) && (attackResult.getDamageType() != this.hitType))
					continue;
			}
			
			if(Rnd.get(0, 100) > probability)
				continue;
			
			//shield type 2, normal shield
			if (shieldType == 2) {
				int damage = attackResult.getDamage();

				int absorbedDamage = 0;
				if (percent)
					absorbedDamage = damage * hit / 100;
				else
					absorbedDamage = damage >= hit ? hit : damage;

				absorbedDamage = absorbedDamage >= totalHit ? totalHit : absorbedDamage;
				totalHit -= absorbedDamage;

				if (absorbedDamage > 0)
					attackResult.setShieldType(shieldType);
				attackResult.setDamage(damage - absorbedDamage);

				//dont launch subeffect if damage is fully absorbed
				if (absorbedDamage >= damage)
					attackResult.setLaunchSubEffect(false);
				
				if (totalHit <= 0) {
					effect.endEffect();
					return;
				}
			}
			//shield type 1, reflected damage
			else if (shieldType == 1)	{
				//totalHit is radius
				if (minradius != 0) {
					if(MathUtil.isIn3dRange(attacker, effect.getEffected(), minradius))
						continue;
				}
				if(MathUtil.isIn3dRange(attacker, effect.getEffected(), totalHit)) {
					attackResult.setShieldType(shieldType);
					attackResult.setReflectedDamage(hit);
					attackResult.setReflectedSkillId(effect.getSkillId());
					attacker.getController().onAttack(effect.getEffected(), hit, false);
					
					if (effect.getEffected() instanceof Player)
						PacketSendUtility.sendPacket((Player)effect.getEffected(), SM_SYSTEM_MESSAGE.STR_SKILL_PROC_EFFECT_OCCURRED(effect.getSkillTemplate().getNameId()));
				}
				break;
			}
			//shield type 8, protect effect (ex. skillId: 417 Bodyguard I)
			else if (shieldType == 8) {
				//totalHit is radius
				if (effect.getEffector() == null || effect.getEffector().getLifeStats().isAlreadyDead()) {
					effect.endEffect();
					break;
				}
				
				if(MathUtil.isIn3dRange(effect.getEffector(), effect.getEffected(), totalHit)) {
					int damageProtected = 0;

					if (percent)
						damageProtected = ((int)(attackResult.getDamage() * hit * 0.01));
					else
						damageProtected = hit;
					
					int finalDamage = attackResult.getDamage() - damageProtected;
					
					attackResult.setDamage((finalDamage <= 0 ? 0 : finalDamage));
					attackResult.setShieldType(shieldType);
					attackResult.setProtectedSkillId(effect.getSkillId());
					attackResult.setProtectedDamage(damageProtected);
					attackResult.setProtectorId(effect.getEffectorId());
					effect.getEffector().getController().onAttack(attacker, effect.getSkillId(), TYPE.PROTECTDMG, damageProtected, false, LOG.REGULAR);
				}
			}
		}
	}
}
