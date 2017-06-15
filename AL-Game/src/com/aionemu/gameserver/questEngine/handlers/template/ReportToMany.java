package com.aionemu.gameserver.questEngine.handlers.template;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.NpcInfos;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Hilgert
 * @modified vlog
 */
public class ReportToMany extends QuestHandler {

	private final int questId;
	private final int startItem;
	private final int startNpc;
	private final int startNpc2;
	private final int endNpc;
	private final int endNpc2;
	private final int startDialog;
	private final int endDialog;
	private final int maxVar;
	private final FastMap<Integer, NpcInfos> NpcInfo;

	/**
	 * @param questId
	 * @param startItem
	 * @param endNpc
	 * @param startDialog
	 * @param endDialog
	 * @param maxVar
	 */
	public ReportToMany(int questId, int startItem, int startNpc, int startNpc2, int endNpc, int endNpc2, FastMap<Integer, NpcInfos> NpcInfo,
		int startDialog, int endDialog, int maxVar) {
		super(questId);
		this.startItem = startItem;
		this.startNpc = startNpc;
		if (startNpc2 != 0) {
			this.startNpc2 = startNpc2;
		}
		else {
			this.startNpc2 = this.startNpc;
		}
		this.endNpc = endNpc;
		if (endNpc2 != 0) {
			this.endNpc2 = endNpc2;
		}
		else {
			this.endNpc2 = this.endNpc;
		}
		this.questId = questId;
		this.NpcInfo = NpcInfo;
		this.startDialog = startDialog;
		this.endDialog = endDialog;
		this.maxVar = maxVar;
	}

	@Override
	public void register() {
		if (startItem != 0)
			qe.registerQuestItem(startItem, questId);
		else {
			if (startNpc != 0) {
				qe.registerQuestNpc(startNpc).addOnQuestStart(questId);
				qe.registerQuestNpc(startNpc).addOnTalkEvent(questId);
			}
			if (startNpc2 != 0) {
				qe.registerQuestNpc(startNpc2).addOnQuestStart(questId);
				qe.registerQuestNpc(startNpc2).addOnTalkEvent(questId);
			}
		}
		for (int npcId : NpcInfo.keySet()) {
			qe.registerQuestNpc(npcId).addOnTalkEvent(questId);
		}
		qe.registerQuestNpc(endNpc).addOnTalkEvent(questId);
		if (endNpc2 != 0) {
			qe.registerQuestNpc(endNpc2).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startItem != 0) {
				if (dialog == QuestDialog.ACCEPT_QUEST) {
					QuestService.startQuest(env);
					return closeDialogWindow(env);
				}
			}
			if (startNpc == 0 || targetId == startNpc || targetId == startNpc2) {
				if (dialog == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, startDialog);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			NpcInfos targetNpcInfo = NpcInfo.get(targetId);
			if (var <= maxVar) {
				if (targetNpcInfo != null && var == targetNpcInfo.getVar()) {
					int closeDialog;
					if (targetNpcInfo.getCloseDialog() == 0) {
						closeDialog = 10000 + targetNpcInfo.getVar();
					}
					else {
						closeDialog = targetNpcInfo.getCloseDialog();
					}

					if (dialog == QuestDialog.START_DIALOG) {
						return sendQuestDialog(env, targetNpcInfo.getQuestDialog());
					}
					else if (env.getDialogId() == closeDialog) {
						if (var == maxVar) {
							qs.setStatus(QuestStatus.REWARD);
							if (closeDialog == 1009) {
								return sendQuestDialog(env, 5);
							}
						}
						else {
							qs.setQuestVarById(0, var + 1);
						}
						updateQuestStatus(env);
						return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (var > maxVar) {
				if (targetId == endNpc || targetId == endNpc2) {
					if (dialog == QuestDialog.START_DIALOG) {
						return sendQuestDialog(env, endDialog);
					}
					else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
						if (startItem != 0) {
							if (!removeQuestItem(env, startItem, 1)) {
								return false;
							}
						}
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD && (targetId == endNpc || targetId == endNpc2)) {
			return sendQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		if (startItem != 0) {
			Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
			}
		}
		return HandlerResult.UNKNOWN;
	}
}
