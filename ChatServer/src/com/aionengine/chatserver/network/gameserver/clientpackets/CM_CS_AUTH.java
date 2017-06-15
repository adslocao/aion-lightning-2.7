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

import org.jboss.netty.buffer.ChannelBuffer;

import com.aionengine.chatserver.network.gameserver.AbstractGameClientPacket;
import com.aionengine.chatserver.network.gameserver.GsAuthResponse;
import com.aionengine.chatserver.network.gameserver.serverpackets.SM_GS_AUTH_RESPONSE;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler;
import com.aionengine.chatserver.network.netty.handler.GameChannelHandler.State;
import com.aionengine.chatserver.service.GameServerService;

/**
 * 
 * @author ATracer
 * 
 */
public class CM_CS_AUTH extends AbstractGameClientPacket
{
	/**
	 * Password for authentication
	 */
	private String				password;

	/**
	 * Id of GameServer
	 */
	private byte				gameServerId;

	/**
	 * Default address for server
	 */
	private byte[]				defaultAddress;

	public CM_CS_AUTH(ChannelBuffer buf, GameChannelHandler gameChannelHandler)
	{
		super(buf, gameChannelHandler, 0x00);
	}

	@Override
	protected void readImpl()
	{
		gameServerId = (byte) readC();

		defaultAddress = readB(readC());
		password = readS();
	}

	@Override
	protected void runImpl()
	{
		GsAuthResponse resp = GameServerService.registerGameServer(gameChannelHandler, gameServerId, defaultAddress,
			password);

		switch (resp)
		{
			case AUTHED:
				gameChannelHandler.setState(State.AUTHED);
				gameChannelHandler.sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				break;
			case NOT_AUTHED:
				gameChannelHandler.sendPacket(new SM_GS_AUTH_RESPONSE(resp));
				break;
			default:
				gameChannelHandler.close(new SM_GS_AUTH_RESPONSE(resp));
		}
	}
}
