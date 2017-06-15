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
package com.aionengine.chatserver.service;

import java.util.Map;

import javolution.util.FastMap;

import com.aionengine.chatserver.model.ChatClient;
import com.aionengine.chatserver.model.message.Message;
import com.aionengine.chatserver.network.aion.serverpackets.SM_CHANNEL_MESSAGE;
import com.aionengine.chatserver.network.netty.handler.ClientChannelHandler;

/**
 * 
 * @author ATracer
 *
 */
public class BroadcastService
{
	private Map<Integer, ChatClient> clients;
	
	public static final BroadcastService getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private BroadcastService()
	{
		clients = new FastMap<Integer, ChatClient>();
	}

	/**
	 * 
	 * @param client
	 */
	public void addClient(ChatClient client)
	{
		clients.put(client.getClientId(), client);
	}
	
	/**
	 * 
	 * @param client
	 */
	public void removeClient(ChatClient client)
	{
		clients.remove(client.getClientId());
	}
	
	/**
	 * 
	 * @param message
	 */
	public void broadcastMessage(Message message)
	{
		for(ChatClient client : clients.values())
		{
			if(client.isInChannel(message.getChannel()))
				sendMessage(client, message);
		}
	}
	
	/**
	 * 
	 * @param chatClient
	 * @param message
	 */
	public void sendMessage(ChatClient chatClient, Message message)
	{
		ClientChannelHandler cch = chatClient.getChannelHandler();
		cch.sendPacket(new SM_CHANNEL_MESSAGE(message));
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final BroadcastService instance = new BroadcastService();
	}
}
