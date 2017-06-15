/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.tallocsHollow;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

import ai.AggressiveNpcAI2;

/**
 *
 * @author xTz
 */
@AIName("kinquid")
public class KinquidAI2 extends AggressiveNpcAI2 {

	private boolean isHome = true;

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (isHome) {
			isHome = false;
			getPosition().getWorldMapInstance().getDoors().get(48).setOpen(false);
			check();
		}
	}

	@Override
	protected void handleBackHome() {
		isHome = true;
		getPosition().getWorldMapInstance().getDoors().get(48).setOpen(true);
		super.handleBackHome();
		despawnDestroyer();
	}

	private void doSchedule() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				check();
			}

		}, 25000);
	}

	private void despawnDestroyer() {
		Npc cleaveArmor = getPosition().getWorldMapInstance().getNpc(282008);
		if (cleaveArmor != null) {
			cleaveArmor.getController().onDelete();
		}
		Npc accessoryDestruction = getPosition().getWorldMapInstance().getNpc(282009);
		if (accessoryDestruction != null) {
			accessoryDestruction.getController().onDelete();
		}
	}

	private void check() {
		despawnDestroyer();
		if (getPosition().isSpawned() && !isAlreadyDead() && !isHome) {
			int spawnLocNr = Rnd.get(1, 3);
			int spawnNr = Rnd.get(1, 2);
			switch (spawnNr) {
				case 1:
					spawnNr = 282008;
					break;
				case 2:
					spawnNr = 282009;
					break;
			}

			switch (spawnLocNr) {
				case 1:
					spawn(spawnNr, 266.70685f, 680.6733f, 1167.2369f, (byte) 0);
					break;
				case 2:
					spawn(spawnNr, 292.02466f, 719.7132f, 1169.3982f, (byte) 0);
					break;
				case 3:
					spawn(spawnNr, 263.4334f, 716.73004f, 1170.3693f, (byte) 0);
					break;
			}
		}
		doSchedule();
	}

}
