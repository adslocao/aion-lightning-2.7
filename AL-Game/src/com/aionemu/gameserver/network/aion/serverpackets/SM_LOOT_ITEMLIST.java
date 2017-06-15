/*
 * This file is part of aion-unique <aion-unique.com>.
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

import java.util.Set;

import javolution.util.FastList;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author alexa026, Avol, Corrected by Metos modified by ATracer, KID
 */
public class SM_LOOT_ITEMLIST extends AionServerPacket {

	private int targetObjectId;
	private FastList<DropItem> dropItems;

	public SM_LOOT_ITEMLIST(int targetObjectId, Set<DropItem> setItems, Player player) {
		this.targetObjectId = targetObjectId;
		this.dropItems = new FastList<DropItem>();
		if (setItems == null) {
			LoggerFactory.getLogger(SM_LOOT_ITEMLIST.class).warn("null Set<DropItem>, skip");
			return;
		}

		for (DropItem item : setItems) {
			if (item.getPlayerObjId() == 0 || player.getObjectId() == item.getPlayerObjId())
				dropItems.add(item);
		}
	}

	/**
	 * {@inheritDoc} dc
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjectId);
		writeC(dropItems.size());

		for (DropItem dropItem : dropItems) {
			writeC(dropItem.getIndex()); // index in droplist
			writeD(dropItem.getDropTemplate().getItemId());
			writeD((int) dropItem.getCount());
			writeH(0);
		}
		
		FastList.recycle(dropItems);
	}
}
