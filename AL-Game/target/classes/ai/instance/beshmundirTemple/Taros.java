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
package ai.instance.beshmundirTemple;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("taroslifebane")
public class Taros extends AggressiveNpcAI2 {

	private boolean figthStart = false;
	private boolean buffed = false;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		if (!figthStart) {
			figthStart = true;
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		getOwner().getEffectController().removeAllEffects();
		cancelTask();
	}

	@Override
	public void handleDied() {
		super.handleDied();
		cancelTask();
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 75 && !buffed) {
			SkillEngine.getInstance().getSkill(getOwner(), 18741, 50, getOwner()).useSkill();
			buffed = true;
		}
	}

	private void cancelTask() {
		figthStart = false;
		buffed = false;
	}
}
