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

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.aionengine.chatserver.common.netty.BaseServerPacket;

/**
 * @author ATracer
 */
public abstract class AbstractChannelHandler extends SimpleChannelUpstreamHandler
{
	private static final Logger	log	= Logger.getLogger(AbstractChannelHandler.class);

	/**
	 * IP address of channel client
	 */
	protected InetAddress		inetAddress;
	/**
	 * Associated channel
	 */
	protected Channel			channel;

	/**
	 * Invoked when a Channel was disconnected from its remote peer
	 */
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		log.info("Channel disconnected IP: " + inetAddress.getHostAddress());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
	{
		// noinspection ThrowableResultOfMethodCallIgnored
		// log.error("NETTY: Exception caught: ", e.getCause());
		e.getChannel().close();
	}

	/**
	 * Closes the channel but ensures that packet is send before close
	 * 
	 * @param packet
	 */
	public void close(BaseServerPacket packet)
	{
		channel.write(packet).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Closes the channel
	 */
	public void close()
	{
		channel.close();
	}

	/**
	 * @return the IP address string
	 */
	public String getIP()
	{
		return inetAddress.getHostAddress();
	}
}
