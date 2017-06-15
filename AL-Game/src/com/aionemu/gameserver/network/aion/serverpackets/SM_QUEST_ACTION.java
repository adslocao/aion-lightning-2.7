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


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author VladimirZ
 */
public class SM_QUEST_ACTION extends AionServerPacket {

	protected int questId;
	private int status;
	private int step;
	protected int action;
	private int timer;

	SM_QUEST_ACTION(){
		
	}
	/**
	 * Accept Quest(1)
	 * 
	 * @param questId
	 * @param status
	 * @param step
	 */
	public SM_QUEST_ACTION(int questId, int status, int step) {
		this.action = 1;
		this.questId = questId;
		this.status = status;
		this.step = step;
	}

	/**
	 * Quest Steps/Finish (2)
	 * 
	 * @param questId
	 * @param status
	 * @param step
	 */
	public SM_QUEST_ACTION(int questId, QuestStatus status, int step) {
		this.action = 2;
		this.questId = questId;
		this.status = status.value();
		this.step = step;
	}

	/**
	 * Delete Quest(3)
	 * 
	 * @param questId
	 */
	public SM_QUEST_ACTION(int questId) {
		this.action = 3;
		this.questId = questId;
		this.status = 0;
		this.step = 0;
	}

	/**
	 * Display Timer(4)
	 * 
	 * @param questId
	 * @param timer
	 */
	public SM_QUEST_ACTION(int questId, int timer) {
		this.action = 4;
		this.questId = questId;
		this.timer = timer;
		this.step = 0;
	}

	/**
	 * Display Timer(4)
	 * 
	 * @param questId
	 * @param timer
	 */
	public SM_QUEST_ACTION(int questId, boolean fake) {
		this.action = 6;
		this.questId = questId;
		this.timer = 0;
		this.step = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aionemu.commons.network.mmocore.SendablePacket#writeImpl(com.aionemu.commons.network.mmocore.MMOConnection)
	 */
	@Override
	protected void writeImpl(AionConnection con) {

		writeC(action);
		writeD(questId);
		switch (action) {
			case 1:
				writeC(status);// quest status goes by ENUM value
				writeC(0x0);
				writeD(step);// current quest step
				writeH(0);
				break;
			case 2:
				writeC(status);// quest status goes by ENUM value
				writeC(0x0);
				writeD(step);// current quest step
				writeH(0);
				break;
			case 3:
				writeC(status);// quest status goes by ENUM value
				writeC(0x0);
				writeD(step);// current quest step
				writeH(0);
				break;
			case 4:
				writeD(timer);// sets client timer ie 84030000 is 900 seconds/15 mins
				writeC(0x01);
				writeH(0x0);
				writeC(0x01);
			case 6:
				writeH(0x01);// ???
				writeD(0x0);// current quest step
				writeH(0x0);
		}
	}
}
