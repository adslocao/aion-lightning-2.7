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
package ai.instance.steelRake;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("steelrakeportal")
public class SteelRakePortalAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId) {
		int objectId = getObjectId();
		if (dialogId == 1012) {
			int quest = 0;
			switch (player.getRace()) {
				case ASMODIANS:
					quest = 4200;
					break;
				case ELYOS:
					quest = 3200;
					break;
			}

			boolean instanceQuestReq = false;
			boolean instanceGroupReq = false;
			if (player.getAccessLevel() < AdminConfig.INSTANCE_REQ) {
				instanceQuestReq = !player.havePermission(MembershipConfig.INSTANCES_QUEST_REQ);
				instanceGroupReq = !player.havePermission(MembershipConfig.INSTANCES_GROUP_REQ);
			}

			if (instanceQuestReq) {
				QuestState qstel = player.getQuestStateList().getQuestState(quest);
				if (qstel == null || qstel.getStatus() != QuestStatus.COMPLETE) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(objectId, 1097));
					return true;
				}
			}
			if (instanceGroupReq) {
				if (player.getPlayerGroup2() == null) {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(objectId, 1182));
					return true;
				}
			}
			PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getPortalTemplate(getNpcId());
			if (portalTemplate != null) {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
				PortalService.port(portalTemplate, player, getObjectId(), getObjectTemplate().getTalkDelay());
			}
		}
		return true;
	}

}
