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
package com.aionemu.gameserver.services;

import java.sql.Timestamp;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;

/**
 * @author xTz
 */
public class SystemMailService {

	private static final Logger log = LoggerFactory.getLogger(SystemMailService.class);

	public static final SystemMailService getInstance() {
		return SingletonHolder.instance;
	}

	private SystemMailService() {
		log.info("SystemMailService: Initialized.");
	}

	/**
	 * @param sender
	 * @param recipientName
	 * @param title
	 * @param message
	 * @param attachedItemObjId
	 * @param attachedItemCount
	 * @param attachedKinahCount
	 * @param express
	 */
	public void sendMail(String sender, String recipientName, String title, String message, int attachedItemObjId,
		int attachedItemCount, int attachedKinahCount, boolean express) {
		if (attachedItemObjId != 0) {
			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(attachedItemObjId);
			if (itemTemplate == null) {
				log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName
					+ "] RETURN ITEM ID:" + itemTemplate + " ITEM COUNT " + attachedItemCount + " KINAH COUNT "
					+ attachedKinahCount + " ITEM TEMPLATE IS MISSING ");
				return;
			}
		}

		if (attachedItemCount == 0)
			return;

		if (recipientName.length() > 16) {
			log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] ITEM RETURN"
				+ attachedItemObjId + " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount
				+ " RECIPIENT NAME LENGTH > 16 ");
			return;
		}

		if (sender.length() > 16) {
			log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] ITEM RETURN"
				+ attachedItemObjId + " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount
				+ " SENDER NAME LENGTH > 16 ");
			return;
		}

		if (title.length() > 20)
			title = title.substring(0, 20);

		if (message.length() > 1000)
			message = message.substring(0, 1000);

		PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(recipientName);
		Player onlineRecipient;

		if (recipientCommonData == null) {
			log.info("[SYSMAILSERVICE] > [RecipientName: " + recipientName + "] NO SUCH CHARACTER NAME.");
			return;
		}

		if (recipientCommonData.isOnline()) {
			onlineRecipient = World.getInstance().findPlayer(recipientCommonData.getPlayerObjId());
			if (!onlineRecipient.getMailbox().haveFreeSlots()) {
				log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + onlineRecipient.getName()
					+ "] ITEM RETURN" + attachedItemObjId + " ITEM COUNT " + attachedItemCount + " KINAH COUNT "
					+ attachedKinahCount + " MAILBOX FULL ");
				return;
			}
		}
		else {
			if (recipientCommonData.getMailboxLetters() >= 100) {
				log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] ITEM RETURN "
					+ attachedItemObjId + " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount
					+ " MAILBOX FULL ");
				return;
			}
			onlineRecipient = null;
		}

		Item attachedItem = null;
		int finalAttachedKinahCount = 0;
		int itemId = attachedItemObjId;
		int count = attachedItemCount;

		if (itemId != 0) {
			Item senderItem = ItemFactory.newItem(itemId, count);
			if (senderItem != null) {
				senderItem.setEquipped(false);
				senderItem.setEquipmentSlot(0);
				senderItem.setItemLocation(StorageType.MAILBOX.getId());
				attachedItem = senderItem;
			}
		}

		if (attachedKinahCount > 0)
			finalAttachedKinahCount = attachedKinahCount;

		String finalSender = sender;
		Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		Letter newLetter = new Letter(IDFactory.getInstance().nextId(), recipientCommonData.getPlayerObjId(), attachedItem,
			finalAttachedKinahCount, title, message, finalSender, time, true, express);

		if (!DAOManager.getDAO(MailDAO.class).storeLetter(time, newLetter))
			return;

		if (attachedItem != null)
			if (!DAOManager.getDAO(InventoryDAO.class).store(attachedItem, recipientCommonData.getPlayerObjId()))
				return;

		/**
		 * Send mail update packets
		 */
		if (onlineRecipient != null) {
			Mailbox recipientMailbox = onlineRecipient.getMailbox();
			recipientMailbox.putLetterToMailbox(newLetter);

			PacketSendUtility.sendPacket(onlineRecipient, new SM_MAIL_SERVICE(onlineRecipient, onlineRecipient.getMailbox()
				.getLetters()));
			PacketSendUtility.sendPacket(onlineRecipient, new SM_MAIL_SERVICE(onlineRecipient.getMailbox()));
			boolean haveExpress = onlineRecipient.getMailbox().haveUnreadExpress();
			if (express && haveExpress)
				PacketSendUtility.sendPacket(onlineRecipient, new SM_SYSTEM_MESSAGE(1300899));
		}

		/**
		 * Update loaded common data and db if player is offline
		 */
		if (!recipientCommonData.isOnline()) {
			recipientCommonData.setMailboxLetters(recipientCommonData.getMailboxLetters() + 1);
			DAOManager.getDAO(MailDAO.class).updateOfflineMailCounter(recipientCommonData);
		}
		if (LoggingConfig.LOG_SYSMAIL)
			log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName
			+ "] RETURN ITEM ID:" + itemId + " ITEM COUNT " + attachedItemCount + " KINAH COUNT "
			+ attachedKinahCount + " MESSAGE SUCCESSFULLY SENDED ");
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final SystemMailService instance = new SystemMailService();
	}
}
