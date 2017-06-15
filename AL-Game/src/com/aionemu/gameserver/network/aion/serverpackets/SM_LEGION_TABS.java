/*
 * This file is part of aion-unique <www.aion-unique.com>.
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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;

import com.aionemu.gameserver.model.team.legion.LegionHistory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Simple, KID
 */
public class SM_LEGION_TABS extends AionServerPacket {

	private int page;
	private Collection<LegionHistory> legionHistory;
	private int tabId;

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory, int tabId) {
		this.legionHistory = legionHistory;
		this.page = 0;
		this.tabId = tabId;
	}

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory, int page, int tabId) {
		this.legionHistory = legionHistory;
		this.page = page;
		this.tabId = tabId;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		/**
		 * If history size is less than page*8 return
		 */
		if (legionHistory.size() < (page * 8))
			return;

		// TODO: Formula's could use a refactor
		int hisSize = legionHistory.size() - (page * 8);
		if(legionHistory.size() > (page + 1) * 8)
			hisSize = 8;
		
		writeD(legionHistory.size());//writeD(0x79); // Unk
		writeD(page); // current page
		writeD(hisSize);

		int i = 0;
		for (LegionHistory history : legionHistory) {
			if (i >= (page * 8) && i <= (8 + (page * 8))) {
				writeD((int) (history.getTime().getTime() / 1000));
				writeH(history.getLegionHistoryType().getHistoryId());
				writeB(this.render(history.getName()).array());
				writeH(0); // unk
				writeS(history.getDescription(), 64);//writeB(new byte[64]);
				writeD(0); // unk
			}
			i++;
			if (i >= (8 + (page * 8)))
				break;
		}
		writeC(tabId);
		writeC(0);
	}
	
	private ByteBuffer render(String name) {
		ByteBuffer bb1 = ByteBuffer.allocate(64);
		for(char ch : name.toCharArray()) {
			bb1.putChar(ch);
		}
		bb1.rewind();
		return Charset.forName("UTF-16LE").encode(bb1.asCharBuffer());
	}
}
