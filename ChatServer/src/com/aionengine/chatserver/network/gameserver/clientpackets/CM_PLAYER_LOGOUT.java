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

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionengine.chatserver.network.gameserver.AbstractGameClientPacket;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;
import com.aionengine.chatserver.service.ChatService;

/**
 * @author ATracer
 */
public class CM_PLAYER_LOGOUT extends AbstractGameClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_PLAYER_LOGOUT.class);

	private int					playerId;

	public CM_PLAYER_LOGOUT(ChannelBuffer buf, GameChannelHandler gameChannelHandler)
	{
		super(buf, gameChannelHandler, 0x02);
	}

	@Override
	protected void readImpl()
	{
		playerId = readD();
	}

	@Override
	protected void runImpl()
	{
		ChatService.getInstance().playerLogout(playerId);
		log.info("Player logout " + playerId);
	}
}
