/**
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
package com.aionlightning.loginserver.network.gameserver.clientpackets;

import com.aionlightning.loginserver.controller.AccountTimeController;
import com.aionlightning.loginserver.model.Account;
import com.aionlightning.loginserver.network.gameserver.GsClientPacket;

/**
 * In this packet GameServer is informing LoginServer that some account is no longer on GameServer [ie was disconencted]
 * 
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_DISCONNECTED extends GsClientPacket {

	/**
	 * AccountId of account that was disconnected form GameServer.
	 */
	private int accountId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		accountId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Account account = this.getConnection().getGameServerInfo().removeAccountFromGameServer(accountId);

		/**
		 * account can be null if a player logged out from gs {@link CM_ACCOUNT_RECONNECT_KEY 
		 */
		if (account != null) {
			AccountTimeController.updateOnLogout(account);
		}
	}
}
