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

package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.GmConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * Packet that reads Whisper chat messages.<br>
 * 
 * @author SoulKeeper
 */
public class CM_CHAT_MESSAGE_WHISPER extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger("CHAT_LOG");

	/**
	 * To whom this message is sent
	 */
	private String name;

	/**
	 * Message text
	 */
	private String message;

	/**
	 * Constructs new client packet instance.
	 * 
	 * @param opcode
	 */
	public CM_CHAT_MESSAGE_WHISPER(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);

	}

	/**
	 * Read message
	 */
	@Override
	protected void readImpl() {
		name = readS();
		message = readS();
	}

	/**
	 * Print debug info
	 */
	@Override
	protected void runImpl() {
		String formatname = Util.convertName(name);

		Player sender = getConnection().getActivePlayer();
		Player receiver = World.getInstance().findPlayer(formatname);

		if (LoggingConfig.LOG_CHAT)
			log.info(String.format("[MESSAGE] [%s] Whisper To: %s, Message: %s", sender.getName(), formatname, message));

		if (receiver == null)
			sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
		else if (receiver.getCommonData().getGmConfig(GmConfig.GM_WHISPER_OFF) && receiver.getAccessLevel() > sender.getAccessLevel()) {
			PacketSendUtility.sendMessage(sender, "You can't talk with this gm.");
			return;
		}
		else if (sender.getLevel() < CustomConfig.LEVEL_TO_WHISPER)
			sendPacket(SM_SYSTEM_MESSAGE.STR_CANT_WHISPER_LEVEL(String.valueOf(CustomConfig.LEVEL_TO_WHISPER)));
		else if (receiver.getBlockList().contains(sender.getObjectId()))
			sendPacket(SM_SYSTEM_MESSAGE.STR_YOU_EXCLUDED(receiver.getName()));
		else if ((!CustomConfig.SPEAKING_BETWEEN_FACTIONS)
			&& (sender.getRace().getRaceId() != receiver.getRace().getRaceId())
			&& (sender.getAccessLevel() == 0) && (receiver.getAccessLevel() == 0))
			sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(formatname));
		else {
			if (RestrictionsManager.canChat(sender))
				PacketSendUtility.sendPacket(receiver, new SM_MESSAGE(sender, message, ChatType.WHISPER));
		}
	}
}
