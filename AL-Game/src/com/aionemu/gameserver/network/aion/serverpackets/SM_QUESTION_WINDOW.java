/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Opens a yes/no question window on the client. Question based on the code given, defined in client_strings.xml
 * 
 * @author Ben, avol, Lyahim
 */
public class SM_QUESTION_WINDOW extends AionServerPacket {

	public static final int STR_BUDDYLIST_ADD_BUDDY_REQUETS = 1300911;
	public static final int STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE = 0x15f91;
	public static final int STR_EXCHANGE_HE_REJECTED_EXCHANGE = 0x13D782; // TODO: make it a simple box, not a
	// question.
	public static final int STR_DUEL_DO_YOU_CONFIRM_DUEL = 0xc36e;
	public static final int STR_DUEL_DO_YOU_ACCEPT_DUEL = 0xc36c;
	public static final int STR_SOUL_HEALING = 160011;
	public static final int STR_BIND_TO_LOCATION = 160012;
	public static final int STR_REQUEST_GROUP_INVITE = 60000;
	public static final int STR_REQUEST_ALLIANCE_INVITE = 70004;
	public static final int STR_REQUEST_LEAGUE_INVITE = 902249;
	public static final int STR_WAREHOUSE_EXPAND_WARNING = 900686;
	public static final int STR_USE_RIFT = 160019;
	public static final int STR_LEGION_INVITE = 80001;
	public static final int STR_LEGION_DISBAND = 80008;
	public static final int STR_LEGION_DISBAND_CANCEL = 80009;
	public static final int STR_LEGION_CHANGE_MASTER = 80011;
	public static final int STR_CRAFT_ADDSKILL_CONFIRM = 900852;
	public static final int STR_BIND_TO_KISK = 160018;
	public static final int STR_SOUL_BOUND_ITEM_DO_YOU_WANT_SOUL_BOUND = 95006;
	public static final int STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE = 160014;
	public static final int STR_SUMMON_PARTY_DO_YOU_ACCEPT_REQUEST = 901721;
	public static final int STR_QUEST_GIVEUP_WHEN_DELETE_QUEST_ITEM = 150001;
	public static final int STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM = 902050;

	/**
	 * %0 is an untradable item. Are you sure you want to acquire it?
	 */
	public static final int STR_CONFIRM_LOOT = 900495;

	private int code;
	private int senderId;
	private Object[] params;
	private ArtifactLocation artifact;

	/**
	 * Creates a new <tt>SM_QUESTION_WINDOW<tt> packet
	 * 
	 * @param code
	 *          code The string code to display, found in client_strings.xml
	 * @param senderId
	 *          sender Object id
	 * @param params
	 *          params The parameters for the string, if any
	 */
	public SM_QUESTION_WINDOW(int code, int senderId, Object... params) {
		this.code = code;
		this.senderId = senderId;
		this.params = params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(code);

		if (code == STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM) {
			writeH(0x33);
			writeH(0x30);
			writeH(0x30);
			writeH(0x31);
			writeH(0x37);
			writeH(0x30);
			writeH(0x30);
			writeH(0x30);
			writeH(0x30);
			writeH(0x00);
		}

		for (Object param : params) {
			if (param instanceof DescriptionId) {
				writeH(0x24);
				writeD(((DescriptionId) param).getValue());
				writeH(0x00); // unk
			}
			else if (param instanceof ArtifactLocation)
				this.artifact = (ArtifactLocation) param;
			else
				writeS(String.valueOf(param));
		}

		// Guardian Stone Activation Window
		if (code == 160027) {
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeH(0x00);
			writeC(1);
			writeD(senderId);
			writeD(0x05);
		}
		// ArtifactLocation Activation Window
		else if (code == 160028) {
			writeD(0x00);
			writeD(0x00);
			writeH(0x00);
			writeC(0x00);
			writeD(0x00);
			if (artifact == null)
				writeD(0x00);
			else
				writeD(artifact.getCoolDown());// ArtifactLocation reuse
		}
		else if (code == STR_BUDDYLIST_ADD_BUDDY_REQUETS) {
			writeB(new byte[17]);
		}
		else if (code == STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM) {
			writeD(0x00);
			writeH(0x00);
			writeC(0x01);
			writeD(senderId);
			writeD(0x05);
		}
		else {
			writeD(0x00);// unk
			writeH(0x00);// unk
			writeC(0x01);// unk
			writeD(senderId);
			writeD(0x06); // group 6, unk
		}
	}
}
