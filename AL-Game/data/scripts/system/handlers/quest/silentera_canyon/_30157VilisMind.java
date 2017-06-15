package quest.silentera_canyon;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 *
 */
public class _30157VilisMind extends QuestHandler 
{

	private final static int	questId	= 30157;

	public _30157VilisMind()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(204304).addOnQuestStart(questId); //Vili
		qe.registerQuestNpc(204304).addOnTalkEvent(questId); //Vili
		qe.registerQuestNpc(799234).addOnTalkEvent(questId); //Nep
		qe.registerQuestNpc(700570).addOnTalkEvent(questId); //Statue Sinigalla
		qe.registerQuestNpc(799339).addOnTalkEvent(questId); //Sinigalla
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if(targetId == 204304) //Vili
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else if (dialog == QuestDialog.ACCEPT_QUEST)
				{
					if (!giveQuestItem(env, 182209254, 1))
						return true;
					return sendQuestStartDialog(env);
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if(targetId == 799234) //Nep
		{
			if(qs != null && qs.getStatus() == QuestStatus.REWARD) //Reward
				if(dialog == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 2375);
				else if(dialog == QuestDialog.SELECT_REWARD)
					return sendQuestEndDialog(env);
		}
		else if(targetId == 700570) //Statue Sinigalla
		{
			if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
			{
				if (env.getDialog() == QuestDialog.USE_OBJECT) 
				{
					QuestService.addNewSpawn(600010000, 1, 799339, (float) 545.3877, (float) 1232.0298, (float) 304.3357, (byte) 76);
					return useQuestObject(env, 0, 0, false, 0, 0, 0, 182209254, 1);
				}
			}
		}

		else if(targetId == 799339) //Sinigalla
		{

			if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
			{
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1352);
				else if(dialog == QuestDialog.STEP_TO_1)
				{
					defaultCloseDialog(env, 0, 0, true, false);
					final Npc npc = (Npc)env.getVisibleObject();
					ThreadPoolManager.getInstance().schedule(new Runnable(){
						@Override
						public void run()
						{
							npc.getController().onDelete();	
						}
					}, 40000);	
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}

		}

		return false;
	}
}
