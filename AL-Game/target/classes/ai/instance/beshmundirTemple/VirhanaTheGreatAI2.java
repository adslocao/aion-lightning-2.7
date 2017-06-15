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

import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;

/**
 * @author Luzien
 *
 */
@AIName("virhana")
public class VirhanaTheGreatAI2 extends AggressiveNpcAI2 {

	private boolean isStart = false;
	private int count = 0;

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!isStart) {
			isStart = true;
			scheduleRage();
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		isStart = false;
		count = 0;
	}

	private void scheduleRage() {
		if (isAlreadyDead() || !isStart) {
			return;
		}
		SkillEngine.getInstance().getSkill(getOwner(), 19121, 55, getOwner()).useSkill();

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				startRage();
			}

		}, 70000);
	}

	private void startRage() {
		if (isAlreadyDead() || !isStart) {
			return;
		}
		if (count < 12) {
			List<Player> players = getClosePlayer(50);
			Player player = (!players.isEmpty() ? players.get(Rnd.get(players.size())) : null);
			if (player != null) {
				SkillEngine.getInstance().getSkill(getOwner(), 18897, 55, player).useSkill();
			}
			count++;

			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					startRage();
				}

			}, 10000);
		} else { // restart after a douzen casts
			count = 0;
			scheduleRage();
		}
	}
}
