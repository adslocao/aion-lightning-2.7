/**
 * This file is part of aion-lightning <aion-lightning.org>.
 * 
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionlightning.loginserver.network.aion;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import com.aionlightning.commons.network.AConnection;
import com.aionlightning.commons.network.ConnectionFactory;
import com.aionlightning.commons.network.Dispatcher;
import com.aionlightning.loginserver.configs.Config;
import com.aionlightning.loginserver.utils.FloodProtector;

/**
 * ConnectionFactory implementation that will be creating AionConnections
 * 
 * @author -Nemesiss-
 */
public class AionConnectionFactoryImpl implements ConnectionFactory {

	/**
	 * Create a new {@link com.aionlightning.commons.network.AConnection AConnection} instance.<br>
	 * 
	 * @param socket
	 *          that new {@link com.aionlightning.commons.network.AConnection AConnection} instance will represent.<br>
	 * @param dispatcher
	 *          to witch new connection will be registered.<br>
	 * @return a new instance of {@link com.aionlightning.commons.network.AConnection AConnection}<br>
	 * @throws IOException
	 * @see com.aionlightning.commons.network.AConnection
	 * @see com.aionlightning.commons.network.Dispatcher
	 */
	@Override
	public AConnection create(SocketChannel socket, Dispatcher dispatcher) throws IOException {
		if(Config.ENABLE_FLOOD_PROTECTION)
			if (FloodProtector.getInstance().tooFast(socket.socket().getInetAddress().getHostAddress()))
				return null;
		
		return new LoginConnection(socket, dispatcher);
	}
}
