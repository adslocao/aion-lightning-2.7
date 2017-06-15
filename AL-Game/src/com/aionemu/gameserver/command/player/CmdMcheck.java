package com.aionemu.gameserver.command.player;

import java.util.Collection;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Checks all LOCKED missions for start conditions immediately And starts them, if conditions are fulfilled
 * 
 * @author vlog
 */
public class CmdMcheck extends BaseCommand {


	public void execute(Player player, String... params) {
		Collection<QuestState> qsl = player.getQuestStateList().getAllQuestState();
		for (QuestState qs : qsl)
			if (qs.getStatus() == QuestStatus.LOCKED) {
				int questId = qs.getQuestId();
				QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, questId, 0));
			}
		PacketSendUtility.sendMessage(player, "Missions checked successfully");
	
	}
	
}