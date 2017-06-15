package quest.silentera_canyon;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 *
 */
public class _30054PetrificationandPride extends QuestHandler 
{

	private final static int	questId	= 30054;

	public _30054PetrificationandPride()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(798929).addOnQuestStart(questId); //Gellius
		qe.registerQuestNpc(798929).addOnTalkEvent(questId); //Gellius
		qe.registerQuestNpc(203901).addOnTalkEvent(questId); //Telemachus
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 798929) //Gellius
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}

			else if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1)
			{
				if(env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2375);
				else if(env.getDialog() == QuestDialog.SELECT_REWARD)
				{
					qs.setQuestVar(1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				}
				else
					return sendQuestEndDialog(env);
			}

			else if(qs != null && qs.getStatus() == QuestStatus.REWARD) //Reward
				return sendQuestEndDialog(env);
		}

		else if(targetId == 203901) //Telemachus
		{
			if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
			{
				if(env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1352);
				else if(env.getDialog() == QuestDialog.STEP_TO_1)
					return defaultCloseDialog(env, 0, 1);
				else
					return sendQuestStartDialog(env);
			}

		}

		return false;
	}
}
