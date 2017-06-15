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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.spawnengine.VisibleObjectSpawner;

/**
 * @author antness thx to Guapo for sniffing
 */
public class CM_READ_EXPRESS_MAIL extends AionClientPacket {
	
	private int action;

	public CM_READ_EXPRESS_MAIL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		this.action = readC();
	}

	@Override
	protected void runImpl() {
		
		final Player player = getConnection().getActivePlayer();
		boolean haveUnreadExpress = player.getMailbox().haveUnreadExpress();
		switch (this.action) {
			case 0:
				// window is closed
				if (player.getPostman() != null && !haveUnreadExpress) {
					player.getPostman().getController().onDelete();
					player.setPostman(null);
				}
				break;
			case 1:
				// spawn postman
				if (player.getPostman() != null) {
					player.getPostman().getController().onDelete();
					player.setPostman(null);
				}
				if (haveUnreadExpress) {
					VisibleObjectSpawner.spawnPostman(player);
				}
				break;
		}
		// PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player.getMailbox()));
	}
}
