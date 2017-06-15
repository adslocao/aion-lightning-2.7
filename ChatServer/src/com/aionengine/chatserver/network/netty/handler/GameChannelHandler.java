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
package com.aionengine.chatserver.network.netty.handler;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import com.aionengine.chatserver.network.gameserver.AbstractGameClientPacket;
import com.aionengine.chatserver.network.gameserver.AbstractGameServerPacket;
import com.aionengine.chatserver.network.gameserver.GameServerPacketHandler;

/**
 * @author ATracer
 */
public class GameChannelHandler extends AbstractChannelHandler
{
	/**
	 * Default logger
	 */
	private static final Logger				log	= Logger.getLogger(GameChannelHandler.class);

	/**
	 * Current state of channel
	 */
	private State							state;

	/**
	 * 
	 * @param gameServerPacketHandler
	 */

	public GameChannelHandler()
	{
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		super.channelConnected(ctx, e);
		state = State.CONNECTED;
		channel = ctx.getChannel();
		inetAddress = ((InetSocketAddress) e.getChannel().getRemoteAddress()).getAddress();
		log.info("Channel connected Ip:" + inetAddress.getHostAddress());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		super.messageReceived(ctx, e);
		/**
		 * Packet is frame decoded and decrypted at this stage Here packet will be read and submitted to execution
		 */
		AbstractGameClientPacket gameServerPacket = GameServerPacketHandler
			.handle((ChannelBuffer) e.getMessage(), this);
		log.debug("Received packet: " + gameServerPacket);
		if (gameServerPacket != null && gameServerPacket.read())
		{
			gameServerPacket.run();
		}
	}

	/**
	 * 
	 * @param packet
	 */
	public void sendPacket(AbstractGameServerPacket packet)
	{
		ChannelBuffer cb = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, 2 * 8192);
		packet.write(this, cb);
		channel.write(cb);
		log.debug("Sent packet: " + packet);
	}

	public static enum State
	{
		/**
		 * Means that GameServer just connected, but is not authenticated yet
		 */
		CONNECTED,
		/**
		 * GameServer is authenticated
		 */
		AUTHED
	}

	/**
	 * @return the state
	 */
	public State getState()
	{
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(State state)
	{
		this.state = state;
	}
}
