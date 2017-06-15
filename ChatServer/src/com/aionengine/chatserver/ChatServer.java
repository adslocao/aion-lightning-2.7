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
package com.aionengine.chatserver;

import org.apache.log4j.Logger;

import com.aionengine.chatserver.AionEngineCS;
import com.aionengine.chatserver.configs.Config;
import com.aionengine.chatserver.network.netty.NettyServer;
import com.aionengine.commons.services.LoggingService;
import com.aionengine.commons.utils.AEInfos;


/**
 * @author ATracer
 */
public class ChatServer
{
	private static final Logger log = Logger.getLogger(ChatServer.class);
	
    public static void main(String[] args)
    {
    	long start = System.currentTimeMillis();
		
    	
        LoggingService.init();
		log.info("Logging Initialized.");
		AionEngineCS.infoCS();
        AEInfos.printSection("Configurations");
		Config.load();
        
		AEInfos.printSection("NettyServer");
        new NettyServer();	

        AEInfos.printSection("System");
        AEInfos.printAllInfos();
        
        AEInfos.printSection("ChatServerLog");
        log.info("Total Boot Time: " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }
}
