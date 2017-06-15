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
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Sweetkr
 * @author Cura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HideEffect")
public class HideEffect extends BufEffect {

	//TODO! value should be enum already (@XmlEnum) - having int here is just stupid 
	//TODO calc probability
	@XmlAttribute(name = "bufcount")
	protected int buffCount;

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);

		Creature effected = effect.getEffected();
		effected.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());

		CreatureVisualState visualState;

		switch (value) {
			case 1:
				visualState = CreatureVisualState.HIDE1;
				break;
			case 2:
				visualState = CreatureVisualState.HIDE2;
				break;
			case 3:
				visualState = CreatureVisualState.HIDE3;
				break;
			case 5:
				visualState = CreatureVisualState.HIDE5;
				break;
			case 10:
				visualState = CreatureVisualState.HIDE10;
				break;
			case 13:
				visualState = CreatureVisualState.HIDE13;
				break;
			case 20:
				visualState = CreatureVisualState.HIDE20;
				break;
			default:
				visualState = CreatureVisualState.VISIBLE;
				break;
		}
		effected.unsetVisualState(visualState);
		ActionObserver observer = effect.getActionObserver(position);
		effect.getEffected().getObserveController().removeObserver(observer);
		
		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));
	}

	@Override
	public void startEffect(final Effect effect) {
		super.startEffect(effect);

		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		effect.setAbnormal(AbnormalState.HIDE.getId());

		CreatureVisualState visualState;

		AttackUtil.deselectTargettingMe(effected);

		switch (value) {
			case 1:
				visualState = CreatureVisualState.HIDE1;
				break;
			case 2:
				visualState = CreatureVisualState.HIDE2;
				break;
			case 3:
				visualState = CreatureVisualState.HIDE3;
				break;
			case 5:
				visualState = CreatureVisualState.HIDE5;
				break;
			case 10:
				visualState = CreatureVisualState.HIDE10;
				break;
			case 13:
				visualState = CreatureVisualState.HIDE13;
				break;
			case 20:
				visualState = CreatureVisualState.HIDE20;
				break;
			default:
				visualState = CreatureVisualState.VISIBLE;
				break;
		}
		effected.setVisualState(visualState);

		PacketSendUtility.broadcastPacketAndReceive(effected, new SM_PLAYER_STATE(effected));

		// Remove Hide when use skill
		ActionObserver observer = new ActionObserver(ObserverType.SKILLUSE) {

			int bufNumber = 1;

			@Override
			public void skilluse(Skill skill) {
				// [2.5] Allow self buffs = (buffCount - 1)
				if (skill.isSelfBuff() && bufNumber++ < buffCount)
					return;
				
				effect.endEffect();
			}
		};
		effected.getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);

		// Set attacked and dotattacked observers
		effect.setCancelOnDmg(true);

		// Remove Hide when attacking
		effected.getObserveController().attach(new ActionObserver(ObserverType.ATTACK) {

			@Override
			public void attack(Creature creature) {
				effected.getEffectController().removeEffect(effect.getSkillId());
			}
		});
		/**
		 * for player adding:
		 * Remove Hide when using any item action
		 * Remove hide when requesting dialog to any npc
		 */
		if (effected instanceof Player) {
			effected.getObserveController().attach(new ActionObserver(ObserverType.ITEMUSE) {

				@Override
				public void itemused(Item item) {
					effected.getEffectController().removeEffect(effect.getSkillId());
				}
			});
			effected.getObserveController().attach(new ActionObserver(ObserverType.NPCDIALOGREQUEST) {

				@Override
				public void npcdialogrequested(Npc npc) {
					effected.getEffectController().removeEffect(effect.getSkillId());
				}
			});
		}
	}
}
