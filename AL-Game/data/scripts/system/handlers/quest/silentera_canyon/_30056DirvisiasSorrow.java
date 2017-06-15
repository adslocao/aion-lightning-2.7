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
public class _30056DirvisiasSorrow extends QuestHandler 
{

	private final static int	questId	= 30056;

	public _30056DirvisiasSorrow()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(798929).addOnQuestStart(questId); //Gellius
		qe.registerQuestNpc(798929).addOnTalkEvent(questId); //Gellius
		qe.registerQuestNpc(203901).addOnTalkEvent(questId); //Telemachus
		qe.registerQuestNpc(700569).addOnTalkEvent(questId); //Statue Dirvisia
		qe.registerQuestNpc(799034).addOnTalkEvent(questId); //Dirvisia
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		if(targetId == 798929) //Gellius
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else if (dialog == QuestDialog.ACCEPT_QUEST)
				{
					if (!giveQuestItem(env, 182209223, 1))
						return true;
					return sendQuestStartDialog(env);
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		else if(targetId == 203901) //Telemachus
		{
			if(qs != null && qs.getStatus() == QuestStatus.REWARD) //Reward
				if(dialog == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 2375);
				else if(dialog == QuestDialog.SELECT_REWARD)
				{
					player.getInventory().decreaseByItemId(182209224, 1);	
					return sendQuestDialog(env, 5);
				}
				else		
					return sendQuestEndDialog(env);
		}
		else if(targetId == 700569) //Statue de Dirvisia
		{
			if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
			{
				if (env.getDialog() == QuestDialog.USE_OBJECT) 
				{
					QuestService.addNewSpawn(600010000, 1, 799034, 555.8842f, 307.8092f, 310.24997f, (byte) 0);
					return useQuestObject(env, 0, 0, false, 0, 0, 0, 182209223, 1);
				}
			}		
		}

		else if(targetId == 799034) //Dirvisia
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
					}, 400);	
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}

		}

		return false;
	}
}
