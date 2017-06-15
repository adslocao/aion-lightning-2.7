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
package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

/**
 * This blob contains stigma info.
 * 
 * @author -Nemesiss-
 *
 */
public class StigmaInfoBlobEntry extends ItemBlobEntry{

	StigmaInfoBlobEntry() {
		super(ItemBlobType.STIGMA_INFO);
	}

	@Override
	public
	void writeThisBlob(ByteBuffer buf) {
		Item item = parent.item;

		writeH(buf, item.getItemTemplate().getStigma().getSkillid()); // skill id
		writeD(buf, 0);
		writeH(buf, 0);
		writeD(buf, 0x3c); // 0x3c
		skip(buf, 160);
		writeC(buf, 1);// 1
		skip(buf, 85);
	}
}
