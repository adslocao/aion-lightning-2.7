/**
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.model.autogroup.AutoGroupsType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import org.apache.commons.lang.StringUtils;

/**
 * @author SheppeR, Guapo, nrg
 */
public class SM_AUTO_GROUP extends AionServerPacket {

	private int windowId;
	private byte instanceMaskId;
	private int mapId;
	private int messageId;
	private int titleId;
	private int waitTime;
	private boolean close;
	String name = StringUtils.EMPTY;

	public SM_AUTO_GROUP(byte instanceMaskId) {
		this.instanceMaskId = instanceMaskId;
	}

	public SM_AUTO_GROUP(byte instanceMaskId, int windowId) {
		this.instanceMaskId = instanceMaskId;
		this.windowId = windowId;
	}

	public SM_AUTO_GROUP(byte instanceMaskId, int windowId, boolean close) {
		this.instanceMaskId = instanceMaskId;
		this.windowId = windowId;
		this.close = close;
	}

	public SM_AUTO_GROUP(byte instanceMaskId, int windowId, int waitTime, String name) {
		this.instanceMaskId = instanceMaskId;
		this.windowId = windowId;
		this.waitTime = waitTime;
		this.name = name;
	}

	@Override
	protected void writeImpl(AionConnection con) {

		AutoGroupsType agt = AutoGroupsType.getAutoGroupByInstanceMaskId(instanceMaskId);
		messageId = agt.getNameId();
		titleId = agt.getTittleId();
		mapId = agt.getInstanceMapId();

		writeD(instanceMaskId);
		writeC(windowId);
		writeD(mapId);
		switch (windowId) {
			case 0: // request entry
				writeD(messageId);
				writeD(titleId);
				writeD(0);
				break;
			case 1: // waiting window
				writeD(0);
				writeD(0);
				writeD(waitTime);
				break;
			case 2: // cancel looking
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 3: // pass window
				writeD(0);
				writeD(0);
				writeD(waitTime);
				break;
			case 4: // enter window
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 5: // after you click enter
				writeD(0);
				writeD(0);
				writeD(0);
				break;
			case 6: // entry icon
				writeD(messageId);
				writeD(titleId);
				writeD(close ? 0 : 1);
				break;
			case 7: // failed window
				writeD(messageId);
				writeD(titleId);
				writeD(0);
				break;
			case 8: // on login
				writeD(0);
				writeD(0);
				writeD(waitTime);
				break;
		}
		writeC(0);
		writeS(name);
	}
}
