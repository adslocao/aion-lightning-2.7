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
public class _30154FleshtoStone extends QuestHandler 
{

	private final static int	questId	= 30154;

	public _30154FleshtoStone()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(799234).addOnQuestStart(questId); 
		qe.registerQuestNpc(799234).addOnTalkEvent(questId); 
		qe.registerQuestNpc(204433).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if(targetId == 799234)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}

			else if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1)
			{
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2375);
				else if (dialog == QuestDialog.SELECT_REWARD)
					return defaultCloseDialog(env, 1, 1, true, true);
				else
					return sendQuestEndDialog(env);
			}

			else if(qs != null && qs.getStatus() == QuestStatus.REWARD) //Reward
				return sendQuestEndDialog(env);
		}

		else if(targetId == 204433)
		{

			if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
			{
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1352);
				else if (dialog == QuestDialog.STEP_TO_1)
					return defaultCloseDialog(env, 0, 1);
				else
					return sendQuestStartDialog(env);
			}

		}

		return false;
	}
}
