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

import com.aionengine.chatserver.common.netty.BaseClientPacket;
import com.aionengine.chatserver.network.netty.handler.ClientChannelHandler;

/**
 * @author ATracer
 */
public abstract class AbstractClientPacket extends BaseClientPacket
{
	protected ClientChannelHandler	clientChannelHandler;

	/**
	 * 
	 * @param channelBuffer
	 * @param clientChannelHandler
	 * @param opCode
	 */
	public AbstractClientPacket(ChannelBuffer channelBuffer, ClientChannelHandler clientChannelHandler, int opCode)
	{
		super(channelBuffer, opCode);
		this.clientChannelHandler = clientChannelHandler;
	}
}
