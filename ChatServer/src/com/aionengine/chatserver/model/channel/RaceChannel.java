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
import com.aionengine.chatserver.model.Race;

/**
 * @author ATracer
 */
public abstract class RaceChannel extends Channel
{
	protected Race race;
	
	public RaceChannel(ChannelType channelType, Race race)
	{
		super(channelType);
		this.race = race;
	}

	/**
	 * @return the race
	 */
	public Race getRace()
	{
		return race;
	}
}
