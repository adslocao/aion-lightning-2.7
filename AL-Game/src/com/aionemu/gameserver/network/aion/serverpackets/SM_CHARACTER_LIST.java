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


import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.PlayerInfo;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.player.PlayerService;

/**
 * In this packet Server is sending Character List to client.
 * 
 * @author Nemesiss, AEJTester
 */
public class SM_CHARACTER_LIST extends PlayerInfo {

	/**
	 * PlayOk2 - we dont care...
	 */
	private final int playOk2;

	/**
	 * Constructs new <tt>SM_CHARACTER_LIST </tt> packet
	 */
	public SM_CHARACTER_LIST(int playOk2) {
		this.playOk2 = playOk2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(playOk2);
		
		Account account = con.getAccount();
		writeC(account.size()); // characters count

		for (PlayerAccountData playerData : account.getSortedAccountsList()) {
			PlayerCommonData pcd = playerData.getPlayerCommonData();
			CharacterBanInfo cbi = playerData.getCharBanInfo();
			Player player = PlayerService.getPlayer(pcd.getPlayerObjId(), account);

			writePlayerInfo(playerData);

			writeD(player.getPlayerSettings().getDisplay());//display helmet 0 show, 5 dont show
			writeD(0);
			writeD(0); 
			writeD(DAOManager.getDAO(MailDAO.class).haveUnread(pcd.getPlayerObjId()) ? 1 : 0); // mail
			writeD(0); //unk
			writeD(0); //unk
			writeQ(BrokerService.getInstance().getCollectedMoney(pcd)); // collected money from broker
			writeD(0);

			if(cbi != null && cbi.getEnd() > System.currentTimeMillis()/1000){
				//client wants int so let's hope we do not reach long limit with timestamp while this server is used :P
				writeD((int) cbi.getStart()); //startPunishDate
				writeD((int) cbi.getEnd()); //endPunishDate
				writeS(cbi.getReason());
			}
			else {
				writeD(0);
				writeD(0);
				writeH(0);
			}
		}
	}
}
