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
package instance;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Undertrey
 */
@InstanceID(300200000)
public class HaramelInstance extends GeneralInstanceHandler {

	@Override
	public void onDie(Npc npc) {
		Player player = npc.getAggroList().getMostPlayerDamage();
		if (player == null) {
			return;
		}
		switch (npc.getNpcId()) {
			case 216922:
				npc.getController().onDelete();
				spawn(700852, 224.598f, 331.143f, 141.892f, (byte) 90);
				PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 457));
				switch (player.getPlayerClass()) {
					case GLADIATOR:
					case TEMPLAR:
						spawn(700829, 224.137f, 268.608f, 144.898f, (byte) 90); // chest warrior
						break;
					case ASSASSIN:
					case RANGER:
						spawn(700830, 224.137f, 268.608f, 144.898f, (byte) 90); // chest scout
						break;
					case SORCERER:
					case SPIRIT_MASTER:
						spawn(700831, 224.137f, 268.608f, 144.898f, (byte) 90); // chest mage
						break;
					case CLERIC:
					case CHANTER:
						spawn(700832, 224.137f, 268.608f, 144.898f, (byte) 90); // chest cleric
						break;
				}
		}
	}

}
