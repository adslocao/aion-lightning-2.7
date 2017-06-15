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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author ATracer
 */
public class CM_GODSTONE_SOCKET extends AionClientPacket {

	private int npcObjectId;
	private int weaponId;
	private int stoneId;

	public CM_GODSTONE_SOCKET(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		this.npcObjectId = readD();
		this.weaponId = readD();
		this.stoneId = readD();
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		VisibleObject obj = activePlayer.getKnownList().getObject(npcObjectId);
		if(obj != null && obj instanceof Npc && MathUtil.isInRange(activePlayer, obj, 7)) {
			ItemSocketService.socketGodstone(activePlayer, weaponId, stoneId);
		}
	}
}
