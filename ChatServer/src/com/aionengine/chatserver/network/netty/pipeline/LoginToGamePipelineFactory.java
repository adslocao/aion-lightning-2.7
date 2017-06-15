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
package com.aionengine.chatserver.network.netty.pipeline;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

import com.aionengine.chatserver.network.netty.coder.GameServerPacketDecoder;
import com.aionengine.chatserver.network.netty.coder.GameServerPacketEncoder;
import com.aionengine.chatserver.network.netty.coder.PacketFrameDecoder;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;

/**
 * @author ATracer
 */
public class LoginToGamePipelineFactory implements ChannelPipelineFactory
{
	private static final int						THREADS_MAX			= 10;
	private static final int						MEMORY_PER_CHANNEL	= 1048576;
	private static final int						TOTAL_MEMORY		= 134217728;
	private static final int						TIMEOUT				= 100;

	private OrderedMemoryAwareThreadPoolExecutor	pipelineExecutor;

	public LoginToGamePipelineFactory()
	{
		pipelineExecutor = new OrderedMemoryAwareThreadPoolExecutor(THREADS_MAX, MEMORY_PER_CHANNEL, TOTAL_MEMORY,
			TIMEOUT, TimeUnit.MILLISECONDS, Executors.defaultThreadFactory());
	}

	/**
	 * Decoding process will include the following handlers: - framedecoder - packetdecoder - handler
	 * 
	 * Encoding process: - packetencoder
	 * 
	 * Please note the sequence of handlers
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("framedecoder", new PacketFrameDecoder());
		pipeline.addLast("packetdecoder", new GameServerPacketDecoder());
		pipeline.addLast("packetencoder", new GameServerPacketEncoder());

		pipeline.addLast("executor", new ExecutionHandler(pipelineExecutor));
		pipeline.addLast("handler", new GameChannelHandler());

		return pipeline;
	}
}
