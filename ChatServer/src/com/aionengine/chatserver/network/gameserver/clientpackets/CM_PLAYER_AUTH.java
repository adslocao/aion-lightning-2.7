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
package com.aionengine.chatserver.network.gameserver.clientpackets;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionengine.chatserver.model.ChatClient;
import com.aionengine.chatserver.network.gameserver.AbstractGameClientPacket;
import com.aionengine.chatserver.network.gameserver.serverpackets.SM_PLAYER_AUTH_RESPONSE;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;
import com.aionengine.chatserver.service.ChatService;

/**
 * @author ATracer
 */
public class CM_PLAYER_AUTH extends AbstractGameClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_PLAYER_AUTH.class);

	private int					playerId;
	
	private String				playerLogin;

	public CM_PLAYER_AUTH(ChannelBuffer buf, GameChannelHandler gameChannelHandler)
	{
		super(buf, gameChannelHandler, 0x01);
	}

	@Override
	protected void readImpl()
	{
		playerId = readD();
		playerLogin = readS();
	}

	@Override
	protected void runImpl()
	{
		ChatClient chatClient = null;
		try
		{
			chatClient = ChatService.getInstance().registerPlayer(playerId, playerLogin);
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("Error registering player on ChatServer: " + e.getMessage());
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("Error registering player on ChatServer: " + e.getMessage());
		}

		if (chatClient != null)
		{
			gameChannelHandler.sendPacket(new SM_PLAYER_AUTH_RESPONSE(chatClient));
		}
		else
		{
			log.info("Player was not authed " + playerId);
		}
	}
}
