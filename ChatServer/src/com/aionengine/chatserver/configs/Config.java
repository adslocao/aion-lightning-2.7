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
package com.aionengine.chatserver.configs;

import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.aionengine.commons.configuration.ConfigurableProcessor;
import com.aionengine.commons.configuration.Property;
import com.aionengine.commons.utils.PropertiesUtils;

/**
 * @author ATracer
 */
public class Config
{
	/**
	 * Logger for this class.
	 */
	protected static final Logger	log	= Logger.getLogger(Config.class);

	/**
	 * Chat Server address
	 */
	@Property(key = "chatserver.network.client.address", defaultValue = "localhost:10241")
	public static InetSocketAddress	CHAT_ADDRESS;

	/**
	 * Game Server address
	 */
	@Property(key = "chatserver.network.gameserver.address", defaultValue = "localhost:9021")
	public static InetSocketAddress			GAME_ADDRESS;
	
	/**
	 * Game Server bind ip
	 */
	@Property(key = "chatserver.network.gameserver.password", defaultValue = "*")
	public static String			GAME_SERVER_PASSWORD;

	/**
	 * Load configs from files.
	 */
	public static void load()
	{
		try
		{
			Properties[] props = PropertiesUtils.loadAllFromDirectory("./config");
			ConfigurableProcessor.process(Config.class, props);
		}
		catch (Exception e)
		{
			log.fatal("Can't load chatserver configuration", e);
			throw new Error("Can't load chatserver configuration", e);
		}
	}
}
