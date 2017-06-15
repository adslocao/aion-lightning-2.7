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
package instance;

import java.util.Map;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;




/**
 * @author xTz
 */
@InstanceID(300160000)
public class LowerUdasTempleInstance extends GeneralInstanceHandler {

	private Map<Integer,StaticDoor> doors; 
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		int rnd = Rnd.get(1, 100);
		if (rnd > 80) { //spawn named drop chests, 20% both, 30% epic, 50% fabled chest
			spawn(216150, 455.984f, 1192.506f, 190.221f, (byte) 116);
			spawn(216645, 435.664f, 1182.577f, 190.221f, (byte) 116);
		}
		else if (rnd > 50) {
			spawn(216150, 455.984f, 1192.506f, 190.221f, (byte) 116);
		}
		else {
			spawn(216645, 435.664f, 1182.577f, 190.221f, (byte) 116);
		}
	}
	@Override
	public void onDie(Npc npc) {
		switch(npc.getObjectTemplate().getTemplateId()) {
			case 215795: //Debilkarim
				openDoor(111);
			break;
		}
	}

	private void openDoor(int doorId){
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}
