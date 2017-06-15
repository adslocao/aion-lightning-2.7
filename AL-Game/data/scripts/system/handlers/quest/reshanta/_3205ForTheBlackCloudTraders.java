package quest.reshanta;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Ferosia
 */

public class _3205ForTheBlackCloudTraders extends QuestHandler {
	
	private final static int questId = 3205;
	
	public _3205ForTheBlackCloudTraders() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(279010).addOnQuestStart(questId);
		qe.registerQuestNpc(279010).addOnTalkEvent(questId);
		qe.registerQuestNpc(203735).addOnTalkEvent(questId);
		qe.registerQuestNpc(215049).addOnKillEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		
		if(targetId == 279010)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			else if(env.getDialog() == QuestDialog.START_DIALOG && qs.getStatus() == QuestStatus.START)
				return sendQuestDialog(env, 1352);
			else if(env.getDialog() == QuestDialog.SET_REWARD && qs.getStatus() == QuestStatus.START)
				return defaultCloseDialog(env, 15, 15, true, false);
		}
		if(targetId == 203735)
		{
			if(qs != null && qs.getStatus() == QuestStatus.REWARD)
			{
				if(env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if(env.getDialog() == QuestDialog.SELECT_REWARD)
					return sendQuestDialog(env, 5);
				else 
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = env.getTargetId();

		switch(targetId)
			{				
				case 215049:
					if(qs.getStatus() == QuestStatus.START)
					{
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
						updateQuestStatus(env);	
					}
			}
		return false;
	}
}