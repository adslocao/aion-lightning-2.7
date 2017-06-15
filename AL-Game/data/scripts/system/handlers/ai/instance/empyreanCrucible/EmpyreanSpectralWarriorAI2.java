/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.empyreanCrucible;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.instance.StageType;

import ai.AggressiveNpcAI2;

/**
 *
 * @author xTz
 */
public class EmpyreanSpectralWarriorAI2 extends AggressiveNpcAI2 {
	private boolean isDone = false;
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (!isDone && hpPercentage <= 50) {
			// to do increase power.
			isDone = true;
			getPosition().getWorldMapInstance().getInstanceHandler().onChangeStage(StageType.START_STAGE_6_ROUND_5);
		}
	}
}
