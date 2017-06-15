package com.aionemu.gameserver.command.player;

import org.apache.commons.lang.ArrayUtils;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/*"syntax .questauto <questid>*/
/**
 * @author ATracer
 */
public class CmdQuestauto extends BaseCommand {

	/**
	 * put quests for automation here (new int[]{1245,1345,7895})
	 */
	private final int[] questIds = new int[] {};

	


	public void execute(Player player, String... params) {
		if (params.length != 1) {
			showHelp(player);
			return;
		}
		int questId = 0;
		try {
			questId = Integer.parseInt(params[0]);
		}
		catch (Exception ex) {
			PacketSendUtility.sendMessage(player, "wrong quest id");
			return;
		}
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START) {
			PacketSendUtility.sendMessage(player, "quest is not started");
			return;
		}

		if (!ArrayUtils.contains(questIds, questId)) {
			PacketSendUtility.sendMessage(player, "this quest is not supported");
			return;
		}

		qs.setStatus(QuestStatus.REWARD);
		PacketSendUtility
			.sendPacket(player, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
	}

}