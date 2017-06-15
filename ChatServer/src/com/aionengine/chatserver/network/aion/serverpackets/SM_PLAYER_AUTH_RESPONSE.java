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
package com.aionengine.chatserver.network.aion.serverpackets;

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionengine.chatserver.network.aion.AbstractServerPacket;
import com.aionengine.chatserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
public class SM_PLAYER_AUTH_RESPONSE extends AbstractServerPacket
{
	public SM_PLAYER_AUTH_RESPONSE()
	{
		super(0x02);
	}

	@Override
	protected void writeImpl(ClientChannelHandler clientChannelHandler, ChannelBuffer buf)
	{
		writeC(buf, getOpCode());
		writeC(buf, 0x40); // ?
		writeH(buf, 0x01); // ?
		writeD(buf, 0x0BDD0000); // TODO this is actually dynamic
	}

}
