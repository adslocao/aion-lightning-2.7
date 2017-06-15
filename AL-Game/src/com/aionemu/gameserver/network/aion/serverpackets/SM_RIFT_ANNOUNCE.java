/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.controllers.RiftController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class SM_RIFT_ANNOUNCE extends AionServerPacket {

	private int actionId;
	private Race race;
	private RiftController rift;
	private int objectId;
	/**
	 * Rift announce packet
	 * 
	 * @param player
	 */
	public SM_RIFT_ANNOUNCE(Race race) {
		
		this.race = race;
		this.actionId = 0;
	}

	/**
	 * Rift announce packet
	 * 
	 * @param player
	 */
	public SM_RIFT_ANNOUNCE(RiftController rift, boolean isMaster) {
		
		this.rift = rift;
		if (isMaster)
			this.actionId = 2;
		else
			this.actionId = 3;
	}
	
	/**
	 * Rift despawn
	 * 
	 * @param objectId
	 */
	public SM_RIFT_ANNOUNCE(int objectId) {
		
		this.objectId = objectId;
		this.actionId = 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		switch (actionId){
			case 0: //announce
				writeH(0x09);
				writeC(actionId);
				switch (race){
					case ASMODIANS:
						writeD(1);
						writeD(0);
						break;
					case ELYOS:
						writeD(1);
						writeD(0);
						break;
				}
				break;
			case 2:
				writeH(0x21);
				writeC(actionId);
				writeD(rift.getOwner().getObjectId());
				writeD(rift.getMaxEntries()-rift.getUsedEntries());
				writeD(rift.getRemainTime());
				writeD(rift.getMinLevel());
				writeD(rift.getMaxLevel());
				writeF(rift.getOwner().getX());
				writeF(rift.getOwner().getY());
				writeF(rift.getOwner().getZ());
				break;
			case 3:
				writeH(0x0D);
				writeC(actionId);
				writeD(rift.getOwner().getObjectId());
				writeD(rift.getUsedEntries());
				writeD(rift.getRemainTime());
			case 4:
				writeH(0x05);
				writeC(actionId);
				writeD(objectId);
		}
	}
}
