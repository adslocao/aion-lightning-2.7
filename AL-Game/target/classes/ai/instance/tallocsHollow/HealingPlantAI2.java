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
package ai.instance.tallocsHollow;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;

import ai.ActionItemNpcAI2;

/**
 * @author xTz
 */
@AIName("healingplant")
public class HealingPlantAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(2, 0, getTalkDelay());
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
			case 700940:
				player.getLifeStats().increaseHp(TYPE.HP, 20000);
				break;
			case 700941:
				player.getLifeStats().increaseHp(TYPE.HP, 30000);
				break;
		}
		AI2Actions.deleteOwner(this);
	}

}
