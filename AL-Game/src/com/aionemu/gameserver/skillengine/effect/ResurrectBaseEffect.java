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
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.skillengine.model.Effect;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectBaseEffect")
public class ResurrectBaseEffect extends BufEffect {

	@Override
	public void startEffect(final Effect effect) {
		Creature effected = effect.getEffected();

		if (effected instanceof Player) {
			ActionObserver observer = new ActionObserver(ObserverType.DEATH) {

				@Override
				public void died(Creature creature) {
					if (creature instanceof Player) {
						Player effected = (Player) effect.getEffected();
						if (effected.isInInstance())
							PlayerReviveService.instanceRevive(effected);
						/*
						else if (effected.getKisk() != null)
							PlayerReviveService.kiskRevive(effected);
						*/
						else
							PlayerReviveService.bindRevive(effected);
					}
				}
			};
			effect.getEffected().getObserveController().attach(observer);
			effect.setActionObserver(observer, position);
		}
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);

		if (!effect.getEffected().getLifeStats().isAlreadyDead() && effect.getActionObserver(position) != null) {
			effect.getEffected().getObserveController().removeObserver(effect.getActionObserver(position));
		}
	}
}
