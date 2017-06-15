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
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_IMMOBILIZE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

/**
 * @author Sarynth modified by Wakizashi, Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PulledEffect")
public class PulledEffect extends EffectTemplate {

	@Override
	public void applyEffect(final Effect effect) {
		effect.addToEffectedController();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		
		ThreadPoolManager.getInstance().schedule(new Runnable() {
	      @Override
	      public void run() {
	    		//effected.getMoveController().abortMove();
	      	World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
	    		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_FORCED_MOVE(effect.getEffector(), effected));
	      }
	    }, 400);
	}

	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected() == null)
			return;
		if (!super.calculate(effect, StatEnum.PULLED_RESISTANCE, null))
			return;

		effect.setSkillMoveType(SkillMoveType.PULL);
		final Creature effector = effect.getEffector();

		// Target must be pulled just one meter away from effector, not IN place of effector
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		final float x1 = (float) Math.cos(radian);
		final float y1 = (float) Math.sin(radian);
		effect.setTragetLoc(effector.getX() + x1, effector.getY() + y1, effector.getZ() + 0.25F);
	}

	@Override
	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(AbnormalState.CANNOT_MOVE.getId());
		effect.setAbnormal(AbnormalState.CANNOT_MOVE.getId());
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new SM_TARGET_IMMOBILIZE(effect.getEffected()));
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.CANNOT_MOVE.getId());
	}
}
