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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;


/**
 * @author Luzien
 *
 */
@AIName("omegaclone")
public class CloneOfBarrierAI2 extends AggressiveNpcAI2 {

	@Override
	protected void handleDied() {
		for (VisibleObject object : getKnownList().getKnownObjects().values()) {
			if (object instanceof Npc && isInRange(object, 5)) {
				Npc npc = (Npc) object;
				if (npc.getNpcId() == 216516 && !npc.getLifeStats().isAlreadyDead()) {
					npc.getEffectController().removeEffect(18671);
					break;
				}
			}
		}
		super.handleDied();
	}

}
