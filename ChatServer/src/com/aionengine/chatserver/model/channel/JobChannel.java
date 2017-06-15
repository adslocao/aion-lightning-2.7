/*
 * This file is part of Aion Galaxy EMU <aiongemu.com>.
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
import com.aionengine.chatserver.model.PlayerClass;
import com.aionengine.chatserver.model.Race;

/**
 * @author ATracer
 */
public class JobChannel extends RaceChannel
{
	private PlayerClass	playerClass;

	/**
	 * 
	 * @param playerClass
	 * @param race
	 */
	public JobChannel(PlayerClass playerClass, Race race)
	{
		super(ChannelType.JOB, race);
		this.playerClass = playerClass;
	}

	/**
	 * @return the playerClass
	 */
	public PlayerClass getPlayerClass()
	{
		return playerClass;
	}
}