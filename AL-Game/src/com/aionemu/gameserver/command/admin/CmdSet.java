package com.aionemu.gameserver.command.admin;

import java.util.Arrays;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdSet extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 2) {
			showHelp(admin);
			return;
		}
		
		Player target = AutoTarget(admin, false);

		if (params[0].equalsIgnoreCase("class")) {
			setClass(target, target.getPlayerClass(), ParseByte(params[1]));
		}
		else if (params[0].equalsIgnoreCase("exp")) {
			long exp = ParseLong(params[1]);

			target.getCommonData().setExp(exp);
			PacketSendUtility.sendMessage(admin, "Set exp of target to " + params[1]);
		}
		else if (params[0].equalsIgnoreCase("ap")) {
			int ap = ParseInteger(params[1]);

			AbyssPointsService.setAp(target, ap);
			if (target == admin) 
				PacketSendUtility.sendMessage(admin, "Set your Abyss Points to " + ap + ".");
			else {
				PacketSendUtility.sendMessage(admin, "Set " + target.getName() + " Abyss Points to " + ap + ".");
				PacketSendUtility.sendMessage(target, "Admin set your Abyss Points to " + ap + ".");
			}
		}
		else if (params[0].equalsIgnoreCase("title")) {
			int titleId = ParseInteger(params[1]);

			Player player = target;
			if (titleId <= 160)
				setTitle(player, titleId);
			PacketSendUtility.sendMessage(admin, "Set " + player.getCommonData().getName() + " title to " + titleId);

		}
		else if (params[0].equalsIgnoreCase("level")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "Syntax : //set level <level>");
				return;
			}

			Player myTarget = AutoTarget(admin, false) ;
			
			int level = ParseInteger(params[1]);
			
			if (level > 55)
				level = 55;
			
			if (level < 1)
				level = 1;
			
			myTarget.getCommonData().setLevel(level);
		}
	}

	private void setTitle(Player player, int value) {
		PacketSendUtility.sendPacket(player, new SM_TITLE_INFO(value));
		PacketSendUtility.broadcastPacket(player, (new SM_TITLE_INFO(player, value)));
		player.getCommonData().setTitleId(value);
	}

	private void setClass(Player player, PlayerClass oldClass, byte value) {
		PlayerClass playerClass = PlayerClass.getPlayerClassById(value);
		int level = player.getLevel();
		if (level < 9) {
			PacketSendUtility.sendMessage(player, "You can only switch class after reach level 9");
			return;
		}
		if (Arrays.asList(1, 2, 4, 5, 7, 8, 10, 11).contains(oldClass.ordinal())) {
			PacketSendUtility.sendMessage(player, "You already switched class");
			return;
		}
		int newClassId = playerClass.ordinal();
		switch (oldClass.ordinal()) {
			case 0:
				if (newClassId == 1 || newClassId == 2)
					break;
			case 3:
				if (newClassId == 4 || newClassId == 5)
					break;
			case 6:
				if (newClassId == 7 || newClassId == 8)
					break;
			case 9:
				if (newClassId == 10 || newClassId == 11)
					break;
		
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return;
		}
		player.getCommonData().setPlayerClass(playerClass);
		player.getController().upgradePlayer();
		PacketSendUtility.sendMessage(player, "You have successfuly switched class");
	}
}
