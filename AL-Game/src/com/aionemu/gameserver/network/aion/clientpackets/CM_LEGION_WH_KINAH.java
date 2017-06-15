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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionHistoryType;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.LegionService;

/**
 * @author ATracer
 */
public class CM_LEGION_WH_KINAH extends AionClientPacket {
	private static final Logger log = LoggerFactory.getLogger(CM_LEGION_WH_KINAH.class);

	public CM_LEGION_WH_KINAH(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	private long amount;
	private int operation;

	@Override
	protected void readImpl() {
		this.amount = readQ();
		this.operation = readC();
		log.info("[readImpl]ADD/RMV KINAH WH");
	}

	@Override
	protected void runImpl() {
		log.info("[runImpl]ADD/RMV KINAH WH");
		Player activePlayer = getConnection().getActivePlayer();
		
		Legion legion = activePlayer.getLegion();
		if (legion != null) {
			switch (operation) {
				case 0:
					if (activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).tryDecreaseKinah(amount)) {
						LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryType.KINAH_WITHDRAW, Long.toString(amount));
						activePlayer.getInventory().increaseKinah(amount);
					}
					else
						log.warn("NO KINAH (WH) FOUND TO LEGION WH KINAH");
					break;
				case 1:
					if (activePlayer.getInventory().tryDecreaseKinah(amount)) {
						activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).increaseKinah(amount);
						LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryType.KINAH_DEPOSIT, Long.toString(amount));
					}
					else
						log.warn("NO KINAH (Player) FOUND TO LEGION WH KINAH");
					break;
				default:
					log.warn("NO operation FOUND TO LEGION WH KINAH");
					break;
			}
		}
		else
			log.warn("NO LEGION FOUND TO LEGION WH KINAH");
	}

}
