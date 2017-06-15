package com.aionemu.gameserver.command.admin;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.SystemMailService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/*No parameters detected.\n"
			+ "Please use //sysmail <Recipient> <Regular||Express> <Item> <Count> <Kinah>\n"
			+ "Regular mail type is 0, Express mail type is 1.\n"
			+ "If parameters (Item, Count) = 0 than the item will not be send\n"
			+ "If parameters (Kinah) = 0 not send Kinah\n" + "Recipient = Player name, @all, @elyos or @asmodians") */
public class CmdSysMail extends BaseCommand {

	

	enum RecipientType {

		ELYOS,
		ASMO,
		ALL,
		PLAYER;

		public boolean isAllowed(Race race) {
			switch (this) {
				case ELYOS:
					return race == Race.ELYOS;
				case ASMO:
					return race == Race.ASMODIANS;
				case ALL:
					return race == Race.ELYOS || race == Race.ASMODIANS;
			}
			return false;
		}
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length != 5) {
			showHelp(admin);
			return;
		}

		RecipientType recipientType = null;
		String recipient = null;
		if (params[0].startsWith("@")) {
			if ("@all".startsWith(params[0]))
				recipientType = RecipientType.ALL;
			else if ("@elyos".startsWith(params[0]))
				recipientType = RecipientType.ELYOS;
			else if ("@asmodians".startsWith(params[0]))
				recipientType = RecipientType.ASMO;
			else {
				PacketSendUtility.sendMessage(admin, "Recipient must be Player name, @all, @elyos or @asmodians.");
				return;
			}
		}
		else {
			recipientType = RecipientType.PLAYER;
			recipient = Util.convertName(params[0]);
		}

		int item = 0, count = 0, kinah = 0;
		boolean express;

		try {
			item = Integer.parseInt(params[2]);
			count = Integer.parseInt(params[3]);
			kinah = Integer.parseInt(params[4]);
			express = Integer.parseInt(params[1]) >= 1;
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "<Regular||Express> <Item|Count|Kinah> value must be an integer.");
			return;
		}

		if (!check(admin, item, count, kinah, recipient, recipientType, express))
			return;

		if (item <= 0)
			item = 0;

		if (count <= 0)
			count = -1;

		if (recipientType == RecipientType.PLAYER)
			SystemMailService.getInstance().sendMail("Admin", recipient, "System Mail", " ", item, count, kinah, express);
		else {
			for (Player player : World.getInstance().getAllPlayers()) {
				if (recipientType.isAllowed(player.getRace()))
					SystemMailService.getInstance().sendMail("Admin", player.getName(), "System Mail", " ", item, count, kinah,
						express);
			}
		}

		if (item != 0) {
			PacketSendUtility.sendMessage(admin, "You send to " + recipientType
				+ (recipientType == RecipientType.PLAYER ? " " + recipient : "") + "\n" + "[item:" + item + "] Count:" + count
				+ " Kinah:" + kinah + "\n" + "Letter send successfully.");
		}
		else if (kinah > 0) {
			PacketSendUtility.sendMessage(admin, "You send to " + recipientType
				+ (recipientType == RecipientType.PLAYER ? " " + recipient : "") + "\n" + " Kinah:" + kinah + "\n"
				+ "Letter send successfully.");
		}
	}

	private static boolean check(Player admin, int item, int count, int kinah, String recipient,
		RecipientType recipientType, boolean express) {
		if (recipientType == null) {
			PacketSendUtility.sendMessage(admin, "Please insert Recipient Type.\n"
				+ "Recipient = player, @all, @elyos or @asmodians");
			return false;
		}
		else if (recipientType == RecipientType.PLAYER) {
			if (express == false) {
				if (!DAOManager.getDAO(PlayerDAO.class).isNameUsed(recipient)) {
					PacketSendUtility.sendMessage(admin, "Could not find a Recipient by that name.");
					return false;
				}
			}
			else {
				Player p = null;
				p = World.getInstance().findPlayer(recipient);
				if (p == null) {
					PacketSendUtility.sendMessage(admin, "This Recipient is offline.");
					return false;
				}
			}

			PlayerCommonData recipientCommonData = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(recipient);
			if (recipientCommonData.getMailboxLetters() >= 100) {
				PacketSendUtility.sendMessage(admin, recipient + "Players mail box is full");
				return false;
			}
		}

		if (item == 0 && count != 0) {
			PacketSendUtility.sendMessage(admin, "Please insert Item Id..");
			return false;
		}

		if (count == 0 && item != 0) {
			PacketSendUtility.sendMessage(admin, "Please insert Item Count.");
			return false;
		}

		if (count <= 0 && item <= 0 && kinah <= 0) {
			PacketSendUtility.sendMessage(admin, "Parameters <Item> <Count> <Kinah> are icorrect.");
			return false;
		}

		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(item);
		if (item != 0) {
			if (itemTemplate == null) {
				PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + item);
				return false;
			}
			long maxStackCount = itemTemplate.getMaxStackCount();
			if (count > maxStackCount && maxStackCount != 0) {
				PacketSendUtility.sendMessage(admin, "Please insert correct Item Count.");
				return false;
			}
		}

		if (kinah < 0) {
			PacketSendUtility.sendMessage(admin, "Kinah value must be >= 0.");
			return false;
		}
		return true;
	}
	
}
