package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;


/*syntax .questrestart <quest id>*/
/**
 * @author ginho1
 *
 */
public class CmdQuestrestart extends BaseCommand {

	


	public void execute(Player player, String... params) {

		if (params.length != 1) {
			showHelp(player);
			return;
		}
		int id;
		try {
			id = Integer.valueOf(params[0]);
		}
		catch (NumberFormatException e)	{
			PacketSendUtility.sendMessage(player, "syntax .questrestart <quest id>");
			return;
		}

		QuestState qs = player.getQuestStateList().getQuestState(id);

		if (qs == null || id == 1006 || id == 2008) {
			PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] can't be restarted.");
			return;
		}

		if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) {
			if(qs.getQuestVarById(0) != 0) {
				qs.setStatus(QuestStatus.START);
				qs.setQuestVar(0);
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(id, qs.getStatus(), qs.getQuestVars().getQuestVars()));
				PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] restarted.");
			} else
				PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] can't be restarted.");
		} else	{
			PacketSendUtility.sendMessage(player, "Quest [quest: "+id+"] can't be restarted.");
		}
	}

}
