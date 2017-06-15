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
package com.aionlightning.loginserver.network.gameserver.serverpackets;

import java.util.Map;

import com.aionlightning.loginserver.controller.BannedMacManager;
import com.aionlightning.loginserver.model.base.BannedMacEntry;
import com.aionlightning.loginserver.network.gameserver.GsConnection;
import com.aionlightning.loginserver.network.gameserver.GsServerPacket;

/**
 * 
 * @author KID
 *
 */
public class SM_MACBAN_LIST extends GsServerPacket {

	private Map<String, BannedMacEntry> bannedList;
	public SM_MACBAN_LIST()
	{
		this.bannedList = BannedMacManager.getInstance().getMap();
	}
	
	@Override
	protected void writeImpl(GsConnection con) {
		writeC(9);
		writeD(bannedList.size());
		
		for(BannedMacEntry entry : bannedList.values())
		{
			writeS(entry.getMac());
			writeQ(entry.getTime().getTime());
			writeS(entry.getDetails());
		}
	}
}
