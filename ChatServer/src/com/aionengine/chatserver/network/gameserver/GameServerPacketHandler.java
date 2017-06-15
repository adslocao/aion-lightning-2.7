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
package com.aionengine.chatserver.network.gameserver;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionengine.chatserver.common.netty.AbstractPacketHandler;
import com.aionengine.chatserver.network.gameserver.clientpackets.CM_CS_AUTH;
import com.aionengine.chatserver.network.gameserver.clientpackets.CM_PLAYER_AUTH;
import com.aionengine.chatserver.network.gameserver.clientpackets.CM_PLAYER_LOGOUT;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler.State;

/**
 * @author ATracer
 */
public class GameServerPacketHandler extends AbstractPacketHandler
{
	/**
	 * Reads one packet from ChannelBuffer
	 * 
	 * @param buf
	 * @param channelHandler
	 * @return AbstractGameClientPacket
	 */
	public static AbstractGameClientPacket handle(ChannelBuffer buf, GameChannelHandler channelHandler)
	{
		byte opCode = buf.readByte();
		State state = channelHandler.getState();
		AbstractGameClientPacket gamePacket = null;

		switch (state)
		{
			case CONNECTED:
			{
				switch (opCode)
				{
					case 0x00:
						gamePacket = new CM_CS_AUTH(buf, channelHandler);
						break;
					default:
						unknownPacket(opCode, state.toString());
				}
				break;
			}
			case AUTHED:
			{
				switch (opCode)
				{
					case 0x01:
						gamePacket = new CM_PLAYER_AUTH(buf, channelHandler);
						break;
					case 0x02:
						gamePacket = new CM_PLAYER_LOGOUT(buf, channelHandler);
						break;
					default:
						unknownPacket(opCode, state.toString());
				}
				break;
			}
		}

		return gamePacket;
	}

}
