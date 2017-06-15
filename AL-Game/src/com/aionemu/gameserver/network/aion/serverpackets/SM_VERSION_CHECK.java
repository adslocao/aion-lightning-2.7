/**
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


import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.network.IPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.network.NetworkController;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.ChatService;
import com.aionemu.gameserver.services.EventService;

/**
 * @author -Nemesiss- CC fix
 * @modified by Novo, cura
 */
public class SM_VERSION_CHECK extends AionServerPacket {

	/**
	 * Number of characters can be created
	 */
	private int characterLimitCount;

	/**
	 * Related to the character creation mode
	 */
	private final int characterFactionsMode;
	private final int characterCreateMode;

	/**
	 * @param chatService
	 */
	public SM_VERSION_CHECK() {
		if (MembershipConfig.CHARACTER_ADDITIONAL_ENABLE != 10 && MembershipConfig.CHARACTER_ADDITIONAL_COUNT > GSConfig.CHARACTER_LIMIT_COUNT) {
			characterLimitCount = MembershipConfig.CHARACTER_ADDITIONAL_COUNT;
		}
		else {
			characterLimitCount = GSConfig.CHARACTER_LIMIT_COUNT;
		}
		
		characterLimitCount *= NetworkController.getInstance().getServerCount();

		if (GSConfig.CHARACTER_FACTIONS_MODE < 0 || GSConfig.CHARACTER_FACTIONS_MODE > 2)
			characterFactionsMode = 0;
		else
			characterFactionsMode = GSConfig.CHARACTER_FACTIONS_MODE;

		if (GSConfig.CHARACTER_CREATE_MODE < 0 || GSConfig.CHARACTER_CREATE_MODE > 3)
			characterCreateMode = 0;
		else
			characterCreateMode = GSConfig.CHARACTER_CREATE_MODE * 0x04;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(0x00);
		writeC(NetworkConfig.GAMESERVER_ID);
		writeD(0x01B1A3);// unk
		writeD(0x01B1A3);// unk
		writeD(0x00000000);// spacing
		writeD(0x01B1A3);// unk
		writeD(0x4E9E4A);// Some kind of time in mili
		writeC(0x00);// unk
		writeC(GSConfig.SERVER_COUNTRY_CODE);// country code;
		writeC(0x00);// unk

		int serverMode = (characterLimitCount * 0x10) | characterFactionsMode;

		if (GSConfig.FACTIONS_RATIO_LIMITED) {
			if (GameServer.getCountFor(Race.ELYOS) + GameServer.getCountFor(Race.ASMODIANS) > GSConfig.FACTIONS_CHARACTER_CREATE_MAX_LIMIT_COUNT)
				writeC(serverMode | 0x0C);
			else if (GameServer.getRatiosFor(Race.ELYOS) > GSConfig.FACTIONS_RATIO_VALUE)
				writeC(serverMode | 0x04);
			else if (GameServer.getRatiosFor(Race.ASMODIANS) > GSConfig.FACTIONS_RATIO_VALUE)
				writeC(serverMode | 0x08);
			else
				writeC(serverMode);
		}
		else {
			writeC(serverMode | characterCreateMode);
		}

		writeD((int) (System.currentTimeMillis() / 1000));
		writeH(0x015E);
		writeH(0x0A01);
		writeH(0x0A01);
		writeH(0x0370A);
		writeC(0x02);
		writeC(0x00);
		writeC(GSConfig.CHARACTER_REENTRY_TIME);
		writeC(GSConfig.XMAS_ENABLE || EventService.getInstance().isChristmasTime() ? 0x01 : 0x00);
		writeD(0x00); //2.5

		writeH(1);// its loop size
		//for... chat servers?
		{
			writeC(0x00);//spacer
			// if the correct ip is not sent it will not work
			writeB(IPConfig.getDefaultAddress());
			writeH(ChatService.getPort());
		}
	}
}
