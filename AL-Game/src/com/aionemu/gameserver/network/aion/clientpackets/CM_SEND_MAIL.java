/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.MailService;

/**
 * @author Aion Gates, xTz
 */
public class CM_SEND_MAIL extends AionClientPacket {

	private String recipientName;
	private String title;
	private String message;
	private int itemObjId;
	private int itemCount;
	private int kinahCount;
	private int express;

	public CM_SEND_MAIL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		recipientName = readS();
		title = readS();
		message = readS();
		itemObjId = readD();
		itemCount = readD();
		readD();
		kinahCount = readD();
		readD();
		express = readC();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (!player.isTrading() && kinahCount < 1000000000 && kinahCount > -1 && itemCount > -2) {
			MailService.getInstance().sendMail(player, recipientName, title, message, itemObjId, itemCount, kinahCount,
				express == 1 ? true : false);
		}
	}
}
