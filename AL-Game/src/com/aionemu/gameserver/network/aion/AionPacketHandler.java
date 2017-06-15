/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.utils.Util;

/**
 * @author -Nemesiss-
 * @author Luno
 */
public class AionPacketHandler {

	/**
	 * logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(AionPacketHandler.class);

	private Map<Integer, AionClientPacket> packetsPrototypes = new HashMap<Integer, AionClientPacket>();

	/**
	 * Reads one packet from given ByteBuffer
	 * 
	 * @param data
	 * @param client
	 * @return AionClientPacket object from binary data
	 */
	public AionClientPacket handle(ByteBuffer data, AionConnection client) {
		State state = client.getState();
		int id = data.getShort() & 0xffff;
		/* Second opcodec. */
		data.position(data.position() + 3);

		return getPacket(state, id, data, client);
	}

	public void addPacketPrototype(AionClientPacket packetPrototype) {
		packetsPrototypes.put(packetPrototype.getOpcode(), packetPrototype);
	}

	private AionClientPacket getPacket(State state, int id, ByteBuffer buf, AionConnection con) {
		AionClientPacket prototype = packetsPrototypes.get(id);

		if (prototype == null) {
			unknownPacket(state, id, buf);
			return null;
		}
		log.debug("PACKET : " + getHexString(toBytes(id)));
		AionClientPacket res = prototype.clonePacket();
		res.setBuffer(buf);
		res.setConnection(con);

		return res;
	}
	
	private static String getHexString(byte[] bytes) {
		String result = new String();
		for (byte b : bytes) {
			if (b <= 0x0F && b >= 0x00)
				result += '0';
			result += String.format("%x", b);
		}
		return result;
	}
	
	private byte[] toBytes(int i){
		byte[] result = new byte[4];

		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i /*>> 0*/);

		return result;
	}


	/**
	 * Logs unknown packet.
	 * 
	 * @param state
	 * @param id
	 * @param data
	 */
	private void unknownPacket(State state, int id, ByteBuffer data) {
		if (NetworkConfig.DISPLAY_UNKNOWNPACKETS) {
			log.warn(String.format("Unknown packet recived from Aion client: 0x%04X, state=%s %n%s", id, state.toString(),
				Util.toHex(data)));
		}
	}
}
