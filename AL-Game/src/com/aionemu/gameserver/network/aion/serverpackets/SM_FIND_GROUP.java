/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.FindGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author cura, MrPoke
 */
public class SM_FIND_GROUP extends AionServerPacket {

	private int action;
	private int lastUpdate;
	private Collection<FindGroup> findGroups;
	private int groupSize;
	private int unk;

	public SM_FIND_GROUP(int action, int lastUpdate, Collection<FindGroup> findGroups) {
		this.lastUpdate = lastUpdate;
		this.action = action;
		this.findGroups = findGroups;
		this.groupSize = findGroups.size();
	}

	public SM_FIND_GROUP(int action, int lastUpdate, int unk) {
		this.action = action;
		this.lastUpdate = lastUpdate;
		this.unk = unk;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		switch (action) {
			case 0x00:
			case 0x02:
				writeC(action); // type 0:Recruit Group Members
				writeH(groupSize); // groupSize
				writeH(groupSize); // groupSize
				writeD(lastUpdate); // objId?
				for (FindGroup findGroup : findGroups) {
					writeD(findGroup.getObjectId()); // player object id
					writeD(findGroup.getUnk()); // unk (0 or 65557)
					writeC(findGroup.getGroupType()); // 0:group, 1:alliance
					writeS(findGroup.getMessage()); // text
					writeS(findGroup.getName()); // writer name
					writeC(findGroup.getSize()); // members count
					writeC(findGroup.getMinLevel()); // members																																																					// level
					writeC(findGroup.getMaxLevel()); // members																																																						// level
					writeD(findGroup.getLastUpdate()); // objId?
				}
				break;
			case 0x01:
			case 0x03:
				writeC(0x01); // type 1:Recruit delete
				writeD(lastUpdate); // player object id
				writeD(unk); // unk (0 or 65557)
				break;
			case 0x04:
			case 0x06:
				writeC(action); // type 4:Apply for Group
				writeH(groupSize); // groupSize
				writeH(groupSize); // groupSize
				writeD(lastUpdate); // objId?
				for (FindGroup findGroup : findGroups) {
					writeD(findGroup.getObjectId()); // player object id
					writeC(findGroup.getGroupType()); // 0:group, 1:alliance
					writeS(findGroup.getMessage()); // text
					writeS(findGroup.getName()); // writer name
					writeC(findGroup.getClassId()); // player class id
					writeC(findGroup.getMinLevel()); // player level
					writeD(findGroup.getLastUpdate()); // objId?
				}
				break;
			case 0x05:
				writeC(0x05); // type 5:Apply delete
				writeD(lastUpdate); // player object id
				break;
		}
	}
}
