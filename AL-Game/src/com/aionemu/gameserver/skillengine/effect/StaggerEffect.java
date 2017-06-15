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

import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaggerEffect")
public class StaggerEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
		final Creature effected = effect.getEffected();
		if(!effected.getEffectController().applyPhysicalAbnormalEffect())
			return;

		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), 
			new SM_FORCED_MOVE(effect.getEffector(), effect.getEffected().getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
		World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(),
			effected.getHeading());
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.STAGGER_RESISTANCE, SpellStatus.STAGGER);
	}

	@Override
	public void startEffect(Effect effect) {
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();

		// Check for packets if this must be fixed someway, but for now it works good so
		effect.setSkillMoveType(SkillMoveType.PULL);
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.STAGGER.getId());
		effect.setAbnormal(AbnormalState.STAGGER.getId());

		effected.getController().cancelCurrentSkill();
		// Move effected 3 meters backward as on retail
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		float x1 = (float) (Math.cos(radian) * 3);
		float y1 = (float) (Math.sin(radian) * 3);

		float z = effected.getZ();
		Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, effected.getX() + x1,
			effected.getY() + y1, effected.getZ(), false);
		x1 = closestCollision.x;
		y1 = closestCollision.y;
		z = closestCollision.z;
		effect.setTragetLoc(x1, y1, z);
	}

	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STAGGER.getId());
	}

}
