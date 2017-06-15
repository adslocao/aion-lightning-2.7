/*
 * This file is part of Aion X EMU <aionxemu.com>.
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionengine.chatserver.network.aion;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionengine.chatserver.common.netty.AbstractPacketHandler;
import com.aionengine.chatserver.network.aion.clientpackets.CM_CHANNEL_MESSAGE;
import com.aionengine.chatserver.network.aion.clientpackets.CM_CHANNEL_REQUEST;
import com.aionengine.chatserver.network.aion.clientpackets.CM_PLAYER_AUTH;
import com.aionengine.chatserver.network.netty.handler.ClientChannelHandler;
import com.aionengine.chatserver.network.netty.handler.ClientChannelHandler.State;

/**
 * @author ATracer
 */
public class ClientPacketHandler extends AbstractPacketHandler
{
	/**
	 * Reads one packet from ChannelBuffer
	 * 
	 * @param buf
	 * @param channelHandler
	 * @return AbstractClientPacket
	 */
	public static AbstractClientPacket handle(ChannelBuffer buf, ClientChannelHandler channelHandler)
	{
		byte opCode = buf.readByte();
		State state = channelHandler.getState();
		AbstractClientPacket clientPacket = null;

		switch (state)
		{
			case CONNECTED:
				switch (opCode)
				{
					case 0x05:
						clientPacket = new CM_PLAYER_AUTH(buf, channelHandler);
						break;
					default:
						//unknownPacket(opCode, state.toString());
				}
				break;
			case AUTHED:
				switch (opCode)
				{
					case 0x10:
						clientPacket = new CM_CHANNEL_REQUEST(buf, channelHandler);
						break;
					case 0x18:
						clientPacket = new CM_CHANNEL_MESSAGE(buf, channelHandler);
					default:
						//unknownPacket(opCode, state.toString());
				}
				break;
		}

		return clientPacket;
	}
}
