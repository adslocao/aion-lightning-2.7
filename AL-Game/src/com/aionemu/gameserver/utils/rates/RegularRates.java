/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.utils.rates;

import com.aionemu.gameserver.configs.main.RateConfig;

/**
 * @author ATracer
 */
public class RegularRates extends Rates {

	@Override
	public float getGroupXpRate() {
		return RateConfig.GROUPXP_RATE;
	}

	@Override
	public float getDropRate() {
		return RateConfig.DROP_RATE;
	}

	@Override
	public float getApNpcRate() {
		return RateConfig.AP_NPC_RATE;
	}

	@Override
	public float getApPlayerGainRate() {
		return RateConfig.AP_PLAYER_GAIN_RATE;
	}

	@Override
	public float getXpPlayerGainRate() {
		return RateConfig.XP_PLAYER_GAIN_RATE;
	}
	
	@Override
	public float getApPlayerLossRate() {
		return RateConfig.AP_PLAYER_LOSS_RATE;
	}

	@Override
	public float getQuestKinahRate() {
		return RateConfig.QUEST_KINAH_RATE;
	}

	@Override
	public float getQuestXpRate() {
		return RateConfig.QUEST_XP_RATE;
	}

	@Override
	public float getQuestApRate() {
		return RateConfig.QUEST_AP_RATE;
	}

	@Override
	public float getXpRate() {
		return RateConfig.XP_RATE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.rates.Rates#getCraftingXPRate()
	 */
	@Override
	public float getCraftingXPRate() {
		return RateConfig.CRAFTING_XP_RATE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.utils.rates.Rates#getGatheringXPRate()
	 */
	@Override
	public float getGatheringXPRate() {
		return RateConfig.GATHERING_XP_RATE;
	}

	@Override
	public float getPetFeedingRate() {
		return RateConfig.PET_FEEDING_RATE;
	}

	@Override
	public float getDpNpcRate() {
		return RateConfig.DP_NPC_RATE;
	}

	@Override
	public float getDpPlayerRate() {
		return RateConfig.DP_PLAYER_RATE;
	}
}
