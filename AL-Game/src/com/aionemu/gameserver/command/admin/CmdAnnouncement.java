package com.aionemu.gameserver.command.admin;

import java.util.Set;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.Announcement;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.AnnouncementService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdAnnouncement extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length == 1) {
			showHelp(admin);
			return ;
		}
	
		if (params[1].equalsIgnoreCase("list")) {
			Set<Announcement> announces = AnnouncementService.getInstance().getAnnouncements();
			PacketSendUtility.sendMessage(admin, "ID  |  FACTION  |  CHAT TYPE  |  DELAY  |  MESSAGE");
			PacketSendUtility.sendMessage(admin, "-------------------------------------------------------------------");

			for (Announcement announce : announces)
				PacketSendUtility.sendMessage(
						admin,	announce.getId() + "  |  " + announce.getFaction() + "  |  " + announce.getType() + "  |  "
					+ announce.getDelay() + "  |  " + announce.getAnnounce());
		}
		else if (params[1].equalsIgnoreCase("add")) {
			if (params.length < 6) {
				showHelp(admin);
				return;
			}

			int delay;

			try {
				delay = ParseInteger(params[4]);
			}
			catch (NumberFormatException e) {
				// 15 minutes, default
				delay = 900;
			}

			String message = "";

			// Add with space
			for (int i = 5; i < params.length - 1; i++)
				message += params[i] + " ";

			// Add the last without the end space
			message += params[params.length - 1];

			// Create the announce
			Announcement announce = new Announcement(message, params[2], params[3], delay);

			// Add the announce in the database
			AnnouncementService.getInstance().addAnnouncement(announce);

			// Reload all announcements
			AnnouncementService.getInstance().reload();

			PacketSendUtility.sendMessage(admin, "The announcement has been created with successful !");
		}
		else if (params[1].equalsIgnoreCase("delete")) {
			if ((params.length < 3)) {
				showHelp(admin);
				return;
			}

			int id;

			try {
				id = ParseInteger(params[2]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "The announcement's ID is wrong !");
				showHelp(admin);
				return;
			}

			// Delete the announcement from the database
			AnnouncementService.getInstance().delAnnouncement(id);

			// Reload all announcements
			AnnouncementService.getInstance().reload();

			PacketSendUtility.sendMessage(admin, "The announcement has been deleted with successful !");
		}
		else
			showHelp(admin);
	}
}