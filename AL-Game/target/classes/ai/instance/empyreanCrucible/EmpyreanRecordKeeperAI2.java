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
package ai.instance.empyreanCrucible;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.StageType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("empyreanrecordkeeper")
public class EmpyreanRecordKeeperAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId) {
		
		InstanceHandler instanceHandler = getPosition().getWorldMapInstance().getInstanceHandler();
		if (dialogId == 10000) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			switch (getNpcId()) {
				case 799567:
					instanceHandler.onChangeStage(StageType.START_STAGE_1_ELEVATOR);
					break;
				case 799568:
					instanceHandler.onChangeStage(StageType.START_STAGE_2_ELEVATOR);
					break;
				case 799569:
					instanceHandler.onChangeStage(StageType.START_STAGE_3_ELEVATOR);
					break;
				case 205331:
					instanceHandler.onChangeStage(StageType.START_STAGE_4_ELEVATOR);
					break;
				case 205338: // teleport to stage 5
					instanceHandler.onChangeStage(StageType.START_STAGE_5);
					break;
				case 205332:
					instanceHandler.onChangeStage(StageType.START_STAGE_5_ROUND_1);
					break;
				case 205339: // teleport to stage 6
					instanceHandler.onChangeStage(StageType.START_STAGE_6);
					break;
				case 205333:
					instanceHandler.onChangeStage(StageType.START_STAGE_6_ROUND_1);
					break;
				case 205340: // teleport to stage 7
					instanceHandler.onChangeStage(StageType.START_STAGE_7);
					break;
				case 205334:
					instanceHandler.onChangeStage(StageType.START_STAGE_7_ROUND_1);
					break;
			}
			AI2Actions.deleteOwner(this);
		}
		return true;
	}
}
