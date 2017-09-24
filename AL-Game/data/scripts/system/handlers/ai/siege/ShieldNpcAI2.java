/*
 * This file is part of aion-lightning <aion-lightning.org>.
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
package ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SHIELD_EFFECT;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * @author Source
 */
@AIName("siege_shieldnpc")
public class ShieldNpcAI2 extends SiegeNpcAI2 {

	@Override
	protected void handleDespawned() {
		sendShieldPacket(false);
		super.handleDespawned();
	}
	
	@Override
	protected void handleDied() {
		sendShieldPacket(false);
		super.handleDied();
	}

	@Override
	protected void handleSpawned() {
		sendShieldPacket(true);
		super.handleSpawned();
	}

	private void sendShieldPacket(boolean shieldStatus) {
		int id = getSpawnTemplate().getSiegeId();
		SiegeService.getInstance().getFortress(id).setUnderShield(shieldStatus);

		final SM_SHIELD_EFFECT packet = new SM_SHIELD_EFFECT(id);
		for (Player player : World.getInstance().getAllPlayers()) {
			PacketSendUtility.sendPacket(player, packet);
		}
	}

}