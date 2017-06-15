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
package ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.services.siegeservice.FortressSiege;

/**
 * @author ATracer, Source
 * @deprecated This should be removed after removing {@link com.aionemu.gameserver.services.SiegeService2#SIEGE_BOSS_AI_NAME}
 */
@Deprecated
@AIName(FortressSiege.SIEGE_BOSS_AI_NAME)
public class SiegeProtectorNpcAI2 extends SiegeNpcAI2 {

	@Override
	protected void handleBackHome() 
	{
		super.handleBackHome();
		getOwner().getLifeStats().cancelRestoreTask();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if(getOwner().getLifeStats().getCurrentHp() > getOwner().getLifeStats().getMaxHp()){
			getOwner().getLifeStats().setCurrentHp(getOwner().getLifeStats().getMaxHp());
		}
	}
}
