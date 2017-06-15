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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.TransformEffect;

/**
 * @author Sweetkr, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformEffect")
public abstract class TransformEffect extends EffectTemplate {

	@XmlAttribute
	protected int model;

	@XmlAttribute
	protected TransformType type = TransformType.NONE;

	@XmlAttribute
	protected int panelid;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	public void endEffect(Effect effect, AbnormalState state) {
		final Creature effected = effect.getEffected();

		int newModel = 0;
		int newpanel = 0;
		if (effected instanceof Player) {
			
			for (Effect tmp : effected.getEffectController().getAbnormalEffects())	{
				for (EffectTemplate template : tmp.getEffectTemplates()) {
					if (template instanceof TransformEffect) {
						if (((TransformEffect)template).getTransformId() == model)
							continue;
						newModel = ((TransformEffect)template).getTransformId();
						newpanel = ((TransformEffect)template).getPanelId();
						break;
					}
				}
			}
			effected.setTransformedModelId(newModel);
		}
		else if (effected instanceof Summon) {
			effected.setTransformedModelId(0);
		}
		else if (effected instanceof Npc) {
			effected.setTransformedModelId(effected.getObjectTemplate().getTemplateId());
		}
		
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, panelid, false));
		if(newModel == 0){
			if (state != null)
				effected.getEffectController().unsetAbnormal(state.getId());

			if (effected instanceof Player)
				((Player) effected).setTransformed(false);
		}
		else
			PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, newpanel, true));

	}


	/*
	public void endEffect(Effect effect) {
		final Creature effected = effect.getEffected();

		if (state != null)
			effected.getEffectController().unsetAbnormal(state.getId());
		
		if (effected instanceof Player) {
			int newModel = 0;
			TransformType transformType = TransformType.PC;
			for (Effect tmp : effected.getEffectController().getAbnormalEffects())	{
				for (EffectTemplate template : tmp.getEffectTemplates()) {
					if (template instanceof TransformEffect) {
						if (((TransformEffect)template).getTransformId() == model)
							continue;
						newModel = ((TransformEffect)template).getTransformId();
						transformType = ((TransformEffect)template).getTransformType();
						break;
					}
				}
			}
			effected.getTransformModel().setModelId(newModel);
			effected.getTransformModel().setTransformType(transformType);
		}
		else if (effected instanceof Summon) {
			effected.getTransformModel().setModelId(0);
		}
		else if (effected instanceof Npc) {
			effected.getTransformModel().setModelId(effected.getObjectTemplate().getTemplateId());
		}
		effected.getTransformModel().setPanelId(0);
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, 0, false));

		if (effected instanceof Player)
			((Player) effected).setTransformed(false);
	}
	*/
	
	public void startEffect(Effect effect, AbnormalState effectId) {
		final Creature effected = effect.getEffected();
		
		if (effectId != null) {
			effect.setAbnormal(effectId.getId());
			effected.getEffectController().setAbnormal(effectId.getId());
		}
		
		effected.setTransformedModelId(model);
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_TRANSFORM(effected, panelid, true));

		if (effected instanceof Player) {
			((Player) effected).setTransformed(true);
		}
	}

	public TransformType getTransformType() {
		return type;
	}
	
	public int getTransformId()	{
		return model;
	}

	public int getPanelId()	{
		return panelid;
	}
}
