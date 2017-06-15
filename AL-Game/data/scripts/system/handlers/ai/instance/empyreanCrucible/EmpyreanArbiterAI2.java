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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("empyreanarbiter")
public class EmpyreanArbiterAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		if (player.getInventory().getFirstItemByItemId(186000124) != null) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
		}
		else {
			// to do
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		}
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId) {
		int instanceId = getPosition().getInstanceId();

		if (dialogId == 10000 && player.getInventory().decreaseByItemId(186000124, 1)) {
			switch (getNpcId()) {
				case 799573:
					TeleportService.teleportTo(player, 300300000, instanceId, 358.2547f, 349.26443f, 96.09108f, (byte) 59, 3000, true);
					break;
				case 205426:
					TeleportService.teleportTo(player, 300300000, instanceId, 1260.15f, 812.34f, 358.6056f, (byte) 90, 3000, true);
					break;
				case 205427:
					TeleportService.teleportTo(player, 300300000, instanceId, 1616.0248f, 154.43837f, 126f, (byte) 10, 3000, true);
					break;
				case 205428:
					TeleportService.teleportTo(player, 300300000, instanceId, 1793.9233f, 796.92f, 469.36542f, (byte) 60, 3000, true);
					break;
			}
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		}
		return true;
	}
}
