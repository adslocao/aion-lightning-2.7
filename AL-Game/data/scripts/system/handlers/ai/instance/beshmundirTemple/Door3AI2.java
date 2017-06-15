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
package ai.instance.beshmundirTemple;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

import ai.ActionItemNpcAI2;

/**
 *
 * @author Gigi
 * @modified Aion Phenix
 */
@AIName("door3")
public class Door3AI2 extends ActionItemNpcAI2 {
	
	private VisibleObject spawned = null;

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		if (player.getInventory().getItemCountByItemId(185000091) > 0) {
			if  (spawned == null || !spawned.isSpawned())
				spawned = spawn(216168, 1511.69f, 1048.16f, 273.441f, (byte) 40); // Spawn Flarestorm
			super.handleUseItemStart(player);
		}
		else {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
		}
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		AI2Actions.deleteOwner(this);
	}
}
