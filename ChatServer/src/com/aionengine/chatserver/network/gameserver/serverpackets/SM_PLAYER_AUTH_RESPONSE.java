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
package com.aionengine.chatserver.network.gameserver.serverpackets;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionengine.chatserver.model.ChatClient;
import com.aionengine.chatserver.network.gameserver.AbstractGameServerPacket;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;

/**
 * @author ATracer
 */
public class SM_PLAYER_AUTH_RESPONSE extends AbstractGameServerPacket
{
	private int		playerId;
	private byte[]	token;

	public SM_PLAYER_AUTH_RESPONSE(ChatClient chatClient)
	{
		super(0x01);
		this.playerId = chatClient.getClientId();
		token = chatClient.getToken();
	}

	@Override
	protected void writeImpl(GameChannelHandler gameChannelHandler, ChannelBuffer buf)
	{
		writeC(buf, getOpCode());
		writeD(buf, playerId);
		writeC(buf, token.length);
		writeB(buf, token);
	}

}
