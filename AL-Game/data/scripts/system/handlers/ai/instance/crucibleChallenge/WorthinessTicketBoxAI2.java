/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.crucibleChallenge;

import java.util.Set;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.drop.DropRegistrationService;

import ai.ChestAI2;

/**
 *
 * @author xTz
 */
@AIName("worthinessticketbox")
public class WorthinessTicketBoxAI2 extends ChestAI2 {

	@Override
	protected void handleUseItemFinish(Player player) {
		super.handleUseItemFinish(player);
		spawn(205674, 345.52954f, 1662.6697f, 95.25f, (byte) 0);
	}

	@Override
	public void handleDropRegistered() {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(getObjectId());
		dropItems.clear();
		dropItems.add(DropRegistrationService.getInstance().regDropItem(1, getPosition().getWorldMapInstance().getSoloPlayerObj(), getNpcId(), 186000134, 1));
	}
}
