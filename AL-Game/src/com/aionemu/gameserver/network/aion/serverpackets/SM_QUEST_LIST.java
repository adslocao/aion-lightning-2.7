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

import javolution.util.FastList;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.QuestsData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.model.QuestState;

public class SM_QUEST_LIST extends AionServerPacket {

	private FastList<QuestState> questState;

	public SM_QUEST_LIST(FastList<QuestState> questState) {
		this.questState = questState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {

		writeH(0x01); // unk
		writeH(-questState.size() & 0xFFFF);

		QuestsData QUEST_DATA = DataManager.QUEST_DATA;
		for (QuestState qs : questState) {
			writeH(qs.getQuestId());
			writeH(QUEST_DATA.getQuestById(qs.getQuestId()).getCategory().getId());
			writeC(qs.getStatus().value());
			writeD(qs.getQuestVars().getQuestVars());
			writeC(qs.getCompleteCount());
		}
		FastList.recycle(questState);
		questState = null;
	}
}
