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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aionlightning.commons.utils.Rnd;
import com.aionlightning.loginserver.controller.AccountController;
import com.aionlightning.loginserver.model.Account;
import com.aionlightning.loginserver.model.ReconnectingAccount;
import com.aionlightning.loginserver.network.gameserver.GsClientPacket;
import com.aionlightning.loginserver.network.gameserver.serverpackets.SM_ACCOUNT_RECONNECT_KEY;

/**
 * This packet is sended by GameServer when player is requesting fast reconnect to login server. LoginServer in response
 * will send reconectKey.
 * 
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_RECONNECT_KEY extends GsClientPacket {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_ACCOUNT_RECONNECT_KEY.class);
	/**
	 * accoundId of account that will be reconnecting.
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
		int reconectKey = Rnd.nextInt();
		Account acc = this.getConnection().getGameServerInfo().removeAccountFromGameServer(accountId);
		if (acc == null)
			log.info("This shouldnt happend! [Error]");
		else
			AccountController.addReconnectingAccount(new ReconnectingAccount(acc, reconectKey));
		sendPacket(new SM_ACCOUNT_RECONNECT_KEY(accountId, reconectKey));
	}
}
