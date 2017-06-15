package com.aionemu.gameserver.command.admin;

import java.util.List;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.FinishedQuestCond;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;


/*syntax //quest <start|set|show|delete> */


public class CmdQuest extends BaseCommand {

	
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			showHelp(admin);
			return;
		}
		Player target = AutoTarget(admin, false);

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Incorrect target!");
			return;
		}

		if (params[0].equalsIgnoreCase("start")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest start <questId>");
				return;
			}
			int id = ParseInteger(params[1]);

			QuestEnv env = new QuestEnv(null, target, id, 0);

			if (QuestService.startQuest(env))
				PacketSendUtility.sendMessage(admin, "Quest started.");
			else {
				QuestTemplate template = DataManager.QUEST_DATA.getQuestById(id);
				List<XMLStartCondition> preconditions = template.getXMLStartConditions();
				if (preconditions != null && preconditions.size() > 0) {
					for (XMLStartCondition condition : preconditions) {
						List<FinishedQuestCond> finisheds = condition.getFinishedPreconditions();
						if (finisheds != null && finisheds.size() > 0) {
							for (FinishedQuestCond fcondition : finisheds) {
								QuestState qs1 = admin.getQuestStateList().getQuestState(fcondition.getQuestId());
								if (qs1 == null || qs1.getStatus() != QuestStatus.COMPLETE) {
									PacketSendUtility.sendMessage(admin, "You have to finish " + fcondition.getQuestId() + " first!");
								}
							}
						}
					}
				}
				PacketSendUtility.sendMessage(admin, "Quest not started. Some preconditions failed");
			}
		}
		else if (params[0].equalsIgnoreCase("set")) {
			if (params.length < 4) {
				PacketSendUtility.sendMessage(admin, "syntax //quest set <questId> <START|NONE|COMPLETE|REWARD> <var> [varNum]");
				return;
			}
			int questId = ParseInteger(params[1]);
			QuestStatus questStatus;
			if (params[2].equalsIgnoreCase("START"))
				questStatus = QuestStatus.START;
			else if (params[2].equalsIgnoreCase("NONE"))
				questStatus = QuestStatus.NONE;
			else if (params[2].equalsIgnoreCase("COMPLETE"))
				questStatus = QuestStatus.COMPLETE;
			else if (params[2].equalsIgnoreCase("REWARD"))
				questStatus = QuestStatus.REWARD;
			else {
				PacketSendUtility.sendMessage(admin, "<status is one of START, NONE, REWARD, COMPLETE>");
				return;
			}
			int var = ParseInteger(params[3]);
			int varNum = 0;
			if (params.length == 5)
				varNum = ParseInteger(params[4]);
			
			QuestState qs = target.getQuestStateList().getQuestState(questId);
			if (qs == null) {
				PacketSendUtility.sendMessage(admin, "<QuestState wasn't initialized for this quest>");
				return;
			}
			qs.setStatus(questStatus);
			
			if (varNum != 0)
				qs.setQuestVarById(varNum, var);
			else
				qs.setQuestVar(var);
			
			PacketSendUtility.sendPacket(target, new SM_QUEST_ACTION(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
			if (questStatus == QuestStatus.COMPLETE) {
				qs.setCompleteCount(qs.getCompleteCount() + 1);
				target.getController().updateNearbyQuests();
			}
		}
		else if (params[0].equalsIgnoreCase("delete")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest delete <quest id>");
				return;
			}
			int id = ParseInteger(params[1]);

			QuestStateList list = admin.getQuestStateList();
			if (list == null || list.getQuestState(id) == null)
				PacketSendUtility.sendMessage(admin, "Quest not deleted.");
			else {
				QuestState qs = list.getQuestState(id);
				qs.setQuestVar(0);
				qs.setCompleteCount(0);
				qs.setStatus(null);
				if (qs.getPersistentState() != PersistentState.NEW)
					qs.setPersistentState(PersistentState.DELETED);
				PacketSendUtility.sendMessage(admin, "Quest deleted. Please logout.");
			}
		}
		else if (params[0].equalsIgnoreCase("show")) {
			if (params.length != 2) {
				PacketSendUtility.sendMessage(admin, "syntax //quest show <quest id>");
				return;
			}
			QuestState qs = target.getQuestStateList().getQuestState(ParseInteger(params[1]));
			if (qs == null)
				PacketSendUtility.sendMessage(admin, "Quest state: NULL");
			else {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 5; i++)
					sb.append(Integer.toString(qs.getQuestVarById(i)) + " ");
				PacketSendUtility.sendMessage(admin, "Quest state: " + qs.getStatus().toString() + "; vars: " + sb.toString()
					+ qs.getQuestVarById(5));
				sb.setLength(0);
				sb = null;
			}
		}
		else
			showHelp(admin);
	}
}
