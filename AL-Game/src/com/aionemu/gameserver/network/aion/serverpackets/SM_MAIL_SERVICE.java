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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.mail.MailMessage;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.MailServicePacket;

/**
 * @author kosyachok
 */
public class SM_MAIL_SERVICE extends MailServicePacket {

	private int serviceId;
	private Collection<Letter> letters;

	private int totalCount;
	private int unreadCount;
	private int unreadExpressCount;

	private int mailMessage;

	private Letter letter;
	private long time;

	private int letterId;
	private int attachmentType;

	public SM_MAIL_SERVICE(Mailbox mailbox) {
		super(null);
		this.serviceId = 0;
		totalCount = mailbox.size();
		unreadCount = mailbox.getUnreadCount();
		unreadExpressCount = mailbox.getUnreadExpressCount();
	}

	/**
	 * Send mailMessage(ex. Send OK, Mailbox full etc.)
	 * 
	 * @param mailMessage
	 */
	public SM_MAIL_SERVICE(MailMessage mailMessage) {
		super(null);
		this.serviceId = 1;
		this.mailMessage = mailMessage.getId();
	}

	/**
	 * Send mailbox info
	 * 
	 * @param player
	 * @param letters
	 */
	public SM_MAIL_SERVICE(Player player, Collection<Letter> letters) {
		super(player);
		this.serviceId = 2;
		this.letters = letters;
	}

	/**
	 * used when reading letter
	 * 
	 * @param player
	 * @param letter
	 * @param time
	 */
	public SM_MAIL_SERVICE(Player player, Letter letter, long time) {
		super(player);
		this.serviceId = 3;
		this.letter = letter;
		this.time = time;
	}

	/**
	 * used when getting attached items
	 * 
	 * @param letterId
	 * @param attachmentType
	 */
	public SM_MAIL_SERVICE(int letterId, int attachmentType) {
		super(null);
		this.serviceId = 5;
		this.letterId = letterId;
		this.attachmentType = attachmentType;
	}

	/**
	 * used when deleting letter
	 * 
	 * @param letterId
	 */
	public SM_MAIL_SERVICE(int letterId) {
		super(null);
		this.serviceId = 6;
		this.letterId = letterId;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(serviceId);
		switch (serviceId) {
			case 0:
				writeMailboxState(totalCount, unreadCount, unreadExpressCount);
				break;
			case 1:
				writeMailMessage(mailMessage);
				break;
			case 2:
				writeLettersList(letters, player);
				break;
			case 3:
				writeLetterRead(letter, time);
				break;
			case 5:
				writeLetterState(letterId, attachmentType);
				break;
			case 6:
				writeLetterDelete(totalCount, unreadCount, unreadExpressCount, letterId);
				break;
		}
	}
}
