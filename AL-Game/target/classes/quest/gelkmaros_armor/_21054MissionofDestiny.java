/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.gelkmaros_armor;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;


/**
 * @author keqi
 *
 */
public class _21054MissionofDestiny extends QuestHandler {

	private final static int questId = 21054;

	public _21054MissionofDestiny() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799318).addOnQuestStart(questId);
		qe.registerQuestNpc(799318).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		
		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()){
			if (targetId == 799318) {
				switch (dialog) {
					case START_DIALOG:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012: {
						return sendQuestDialog(env, 1012);
					}
					case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					}
					case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					}
					case REFUSE_QUEST: {
						return sendQuestDialog(env, 1004);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 799318) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					}
					case SELECT_ACTION_2034: {
						return sendQuestDialog(env, 2034);
					}
					case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, var, var, true, 5, 2716);
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799318) {
				if(env.getDialogId() == 26)
				   return sendQuestDialog(env, 5);
				else if(env.getDialogId() == 1009)
				   return sendQuestDialog(env, 5);
				else
				   return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}

