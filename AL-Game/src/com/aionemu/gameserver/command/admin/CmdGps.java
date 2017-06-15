package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdGps extends BaseCommand {
	

	public void execute(Player admin, String... params) {
        PacketSendUtility.sendMessage(admin, "== GPS Coordinates ==");
        PacketSendUtility.sendMessage(admin, "X = " + admin.getX());
        PacketSendUtility.sendMessage(admin, "Y = " + admin.getY());
        PacketSendUtility.sendMessage(admin, "Z = " + admin.getZ());
        PacketSendUtility.sendMessage(admin, "H = " + admin.getHeading());
        PacketSendUtility.sendMessage(admin, "World = " + admin.getWorldId());
    }
}

