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

import com.aionengine.chatserver.common.netty.BaseServerPacket;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;

/**
 * @author ATracer
 */
public abstract class AbstractGameServerPacket extends BaseServerPacket
{
	/**
	 * 
	 * @param opCode
	 */
	public AbstractGameServerPacket(int opCode)
	{
		super(opCode);
	}
	
	/**
	 * 
	 * @param gameChannelHandler
	 * @param buf
	 */
	public void write(GameChannelHandler gameChannelHandler, ChannelBuffer buf)
	{
		writeH(buf, 0);
		writeImpl(gameChannelHandler, buf);
	}
	
	/**
	 * 
	 * @param gameChannelHandler
	 * @param buf
	 */
	protected abstract void writeImpl(GameChannelHandler gameChannelHandler, ChannelBuffer buf);
	
}
