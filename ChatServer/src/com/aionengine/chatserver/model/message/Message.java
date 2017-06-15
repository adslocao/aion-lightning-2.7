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
package com.aionengine.chatserver.model.message;

import com.aionengine.chatserver.model.ChatClient;
import com.aionengine.chatserver.model.channel.Channel;

/**
 * @author ATracer
 */
public class Message
{
	private Channel channel;
	
	private byte[] text;
	
	private ChatClient sender;
	
	/**
	 * 
	 * @param channel
	 * @param text
	 */
	public Message(Channel channel, byte[] text, ChatClient sender)
	{
		this.channel = channel;
		this.text = text;
		this.sender = sender;
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel()
	{
		return channel;
	}

	/**
	 * @return the text
	 */
	public byte[] getText()
	{
		return text;
	}	
	
	public int size()
	{
		return text.length;
	}

	/**
	 * @return the sender
	 */
	public ChatClient getSender()
	{
		return sender;
	}
}
