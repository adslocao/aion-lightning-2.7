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

import com.aionengine.chatserver.configs.Config;
import com.aionengine.chatserver.network.gameserver.AbstractGameServerPacket;
import com.aionengine.chatserver.network.gameserver.GsAuthResponse;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;

/**
 * @author ATracer
 */
public class SM_GS_AUTH_RESPONSE extends AbstractGameServerPacket
{
	private GsAuthResponse	response;

	public SM_GS_AUTH_RESPONSE(GsAuthResponse resp)
	{
		super(0x00);
		this.response = resp;
	}

	@Override
	protected void writeImpl(GameChannelHandler gameChannelHandler, ChannelBuffer buf)
	{
		writeC(buf, getOpCode());
		writeC(buf, response.getResponseId());
		writeB(buf, Config.CHAT_ADDRESS.getAddress().getAddress());
		writeH(buf, Config.CHAT_ADDRESS.getPort());
	}

}
