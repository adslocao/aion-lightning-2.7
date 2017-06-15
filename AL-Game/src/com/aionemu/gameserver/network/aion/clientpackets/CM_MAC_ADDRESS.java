/*
 * This file is part of aion-emu <aion-emu.com>.
 * 
 * aion-emu is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * aion-emu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * aion-emu. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;


import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;


/**
 * In this packet client is sending Mac Address - haha.
 * 
 * @author -Nemesiss-, KID
 */
public class CM_MAC_ADDRESS extends AionClientPacket {
	/**
	 * Mac Addres send by client in the same format as: ipconfig /all [ie:
	 * xx-xx-xx-xx-xx-xx]
	 */
	private String	macAddress;

	/**
	 * Constructs new instance of <tt>CM_MAC_ADDRESS </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_MAC_ADDRESS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		readC();
		short counter = (short)readH();
		for(short i = 0; i < counter; i++)
			readD();
		macAddress = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		if(BannedMacManager.getInstance().isBanned(macAddress)) {
			//TODO some information packets
			this.getConnection().closeNow();
			LoggerFactory.getLogger(CM_MAC_ADDRESS.class).info("[MAC_AUDIT] "+macAddress+" ("+this.getConnection().getIP()+") was kicked due to mac ban");
		}
		else
			this.getConnection().setMacAddress(macAddress);
	}
}
