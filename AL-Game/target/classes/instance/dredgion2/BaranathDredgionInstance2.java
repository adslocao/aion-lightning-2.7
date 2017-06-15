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
package instance.dredgion2;

import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 *
 * @author xTz
 */
@InstanceID(300110000)
public class BaranathDredgionInstance2 extends DredgionInstance2 {

	@Override
	public void onEnterInstance(Player player) {
		super.onEnterInstance(player);
	}

	@Override
	public void onDie(Npc npc) {
		Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
		if (mostPlayerDamage == null) {
			return;
		}
		Race race = mostPlayerDamage.getRace();
		switch (npc.getNpcId()) {
			case 214823:
				updateScore(mostPlayerDamage, npc, 1000, false);
				stopInstance(race);
				if (race == Race.ELYOS) {
					sendMsgByRace(1400230, Race.ELYOS, 0);
				}
				else {
					sendMsgByRace(1400231, Race.ASMODIANS, 0);
				}
				return;
			case 700508:
				switch (race) {
					case ELYOS:
						spawn(700502, 520.88f, 493.40f, 395.34f, (byte) 28, 16);
						sendMsgByRace(1400226, Race.ELYOS, 0);
						break;
					case ASMODIANS:
						spawn(700502, 448.39f, 493.64f, 395.04f, (byte) 108, 12);
						sendMsgByRace(1400227, Race.ASMODIANS, 0);
						break;
				}
				return;
			case 700506:
				switch (race) {
					case ELYOS:
						spawn(730214, 567.59f, 175.20f, 432.28f, (byte) 33);
						sendMsgByRace(1400228, Race.ELYOS, 0);
						break;
					case ASMODIANS:
						spawn(730214, 402.33f, 175.12f, 432.28f, (byte) 41);
						sendMsgByRace(1400229, Race.ASMODIANS, 0);
						break;
				}
				return;
		}
		super.onDie(npc);
	}

	@Override
	protected void openFirstDoors() {
		openDoor(17);
		openDoor(18);
	}

}