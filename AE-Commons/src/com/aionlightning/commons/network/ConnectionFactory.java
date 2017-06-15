/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionlightning.commons.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * This interface defines a factory for connection implementations.<br>
 * It is used by the class {@link com.aionlightning.commons.network.Acceptor Acceptor} to create actual connection
 * implementations.<br>
 *
 * @author -Nemesiss-
 * @see com.aionlightning.commons.network.Acceptor
 */
public interface ConnectionFactory {

	/**
	 * Create a new {@link com.aionlightning.commons.network.AConnection AConnection} instance.<br>
	 *
	 * @param socket     that new {@link com.aionlightning.commons.network.AConnection AConnection} instance will represent.<br>
	 * @param dispatcher to wich new connection will be registered.<br>
	 * @return a new instance of {@link com.aionlightning.commons.network.AConnection AConnection}<br>
	 * @throws IOException
	 * @see com.aionlightning.commons.network.AConnection
	 * @see com.aionlightning.commons.network.Dispatcher
	 */
	public AConnection create(SocketChannel socket, Dispatcher dispatcher) throws IOException;
}
