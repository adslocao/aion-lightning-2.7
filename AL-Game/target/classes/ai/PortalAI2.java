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
package ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.services.teleport.PortalService;

/**
 * @author ATracer
 */
@AIName("portal")
public class PortalAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getPortalTemplate(getNpcId());
		AI2Actions.selectDialog(this, player, 0, -1);
		if (portalTemplate != null) {
			PortalService.port(portalTemplate, player, getObjectId(), getObjectTemplate().getTalkDelay());
		}
	}
}
