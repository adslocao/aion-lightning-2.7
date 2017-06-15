/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Actions.SelectDialogResult;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.ItemUseObserver;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.ActionItemNpc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 * @modified vlog
 */
@AIName("useitem")
public class ActionItemNpcAI2 extends NpcAI2 {

	private List<Player> registeredPlayers = new ArrayList<Player>();

	@Override
	protected void handleDialogStart(Player player) {
		// if (!QuestEngine.getInstance().onCanAct(new QuestEnv(getOwner(), player, 0, 0),
		// getObjectTemplate().getTemplateId(), QuestActionType.ACTION_ITEM_USE))
		// return;
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		handleUseItemStart(player);
	}

	protected void handleUseItemStart(final Player player) {

		final ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
				PacketSendUtility.broadcastPacket(player,
						new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
				PacketSendUtility.sendPacket(player,
						new SM_USE_OBJECT(player.getObjectId(), getObjectId(), getTalkDelay(), 0));
			}
		};

		player.getObserveController().attach(observer);
		final ActionItemNpc actionItem = player.getActionItemNpc();
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(),
				actionItem.getTalkDelay(), actionItem.getStartCondition()));
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()),
				true);
		player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player,
						new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(),
						actionItem.getTalkDelay(), actionItem.getEndCondition()));
				player.getObserveController().removeObserver(observer);
				handleUseItemFinish(player);
			}
		}, actionItem.getTalkDelay()));
	}

	protected void handleUseItemFinish(Player player) {
		if (getOwner().isInInstance())
			AI2Actions.handleUseItemFinish(this, player);

		SelectDialogResult dialogResult = AI2Actions.selectDialog(this, player, 0, -1);
		if (!dialogResult.isSuccess())
			return;

		QuestEnv questEnv = dialogResult.getEnv();
		if (QuestService.getQuestDrop(getNpcId()).isEmpty())
			return;

		if (registeredPlayers.isEmpty()) {
			AI2Actions.scheduleRespawn(this);
			if (player.isInGroup2()) {
				registeredPlayers = QuestService.getEachDropMembers(player.getPlayerGroup2(), getNpcId(),
						questEnv.getQuestId());
				if (registeredPlayers.isEmpty())
					registeredPlayers.add(player);
			} else
				registeredPlayers.add(player);
			AI2Actions.registerDrop(this, player, registeredPlayers);
			AI2Actions.dieSilently(this, player);
			DropService.getInstance().requestDropList(player, getObjectId());
		} else if (registeredPlayers.contains(player)) {
			AI2Actions.dieSilently(this, player);
			DropService.getInstance().requestDropList(player, getObjectId());
		}
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		registeredPlayers.clear();
	}

	public int getTalkDelay() {
		return getObjectTemplate().getTalkDelay() * 1000;
	}

}
