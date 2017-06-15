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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.TradeService;


/**
 * @author MrPoke
 *
 */
public class CM_BUY_TRADE_IN_TRADE extends AionClientPacket{

	private int sellerObjId;
	private int itemId;
	private int count;

	/**
	 * @param opcode
	 */
	public CM_BUY_TRADE_IN_TRADE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		sellerObjId = readD();
		itemId = readD();
		count = readD();
		//Have more data,  need ?:)
	}

	@Override
	protected void runImpl() {
		Player player = this.getConnection().getActivePlayer();
		if (count < 1)
			return;
		TradeService.performBuyFromTradeInTrade(player, sellerObjId, itemId, count);
	}

}
