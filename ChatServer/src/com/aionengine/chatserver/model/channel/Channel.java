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
package com.aionengine.chatserver.model.channel;

import com.aionengine.chatserver.model.ChannelType;
import com.aionengine.chatserver.utils.IdFactory;

/**
 * @author ATracer
 */
public abstract class Channel
{
	protected ChannelType channelType;
	
	protected int channelId;
	/**
	 * 
	 * @param channelType
	 */
	public Channel(ChannelType channelType)
	{
		this.channelType = channelType;
		this.channelId = IdFactory.getInstance().nextId();
	}

	/**
	 * @return the channelId
	 */
	public int getChannelId()
	{
		return channelId;
	}
	
	/**
	 * @return the channelType
	 */
	public ChannelType getChannelType()
	{
		return channelType;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + channelId;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		if (channelId != other.channelId)
			return false;
		return true;
	}
}
