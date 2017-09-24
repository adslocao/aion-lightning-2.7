/*
 * This file is part of aion-lightning <aion-lightning.org>.
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

package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * @author antness
 * @author Seita
 * @author Ferosia
 */
public enum RewardType {
	// TODO : Add Boolean isEventOn.
	// TODO : Add float rewardSolo + float rewardGroup + float rewardQuest
	// TODO : If isEventOn set rewardsEvent else set rewardsNonEvent
	HUNTING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_HUNTING_XP_RATE, 100).getCurrent() / 100f;
			
			if(player.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
				statRate *= CustomConfig.FACTION_BONUS_HUNT;
			}
			
			if(!RateConfig.IS_PROGRESSIVE_XP) {
				return (long) (reward * player.getRates().getXpRate() * statRate);
			}
			
			if(player.getLevel() <= RateConfig.PROG_XP_1_LEVEL)
				return (long) (reward * RateConfig.PROG_XP_1_BONUS * player.getRates().getXpRate() * statRate);
			else if(player.getLevel() <= RateConfig.PROG_XP_2_LEVEL)
				return (long) (reward * RateConfig.PROG_XP_2_BONUS * player.getRates().getXpRate() * statRate);
			else if(player.getLevel() <= RateConfig.PROG_XP_3_LEVEL)
				return (long) (reward * RateConfig.PROG_XP_3_BONUS * player.getRates().getXpRate() * statRate);
			else
				return (long) (reward * RateConfig.PROG_XP_4_BONUS * player.getRates().getXpRate() * statRate);
			
		}
	},
	GROUP_HUNTING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GROUP_HUNTING_XP_RATE, 100).getCurrent() / 100f;
			
			if(player.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
				statRate *= CustomConfig.FACTION_BONUS_HUNT;
			}
			
			if(!RateConfig.IS_PROGRESSIVE_XP) {
				return (long) (reward * player.getRates().getGroupXpRate() * statRate);
			}
			
			if(player.getLevel() <= RateConfig.PROG_XP_1_LEVEL)
				return (long) (reward * RateConfig.PROG_GROUPXP_1_BONUS * player.getRates().getGroupXpRate() * statRate);
			else if(player.getLevel() <= RateConfig.PROG_XP_2_LEVEL)
				return (long) (reward * RateConfig.PROG_GROUPXP_2_BONUS * player.getRates().getGroupXpRate() * statRate);
			else if(player.getLevel() <= RateConfig.PROG_XP_3_LEVEL)
				return (long) (reward * RateConfig.PROG_GROUPXP_3_BONUS * player.getRates().getGroupXpRate() * statRate);
			else
				return (long) (reward * RateConfig.PROG_GROUPXP_4_BONUS * player.getRates().getGroupXpRate() * statRate);
			
		}
	},
	PVP_KILL {

		@Override
		public long calcReward(Player player, long reward) {
			return (reward);
		}
	},
	QUEST {

        @Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_QUEST_XP_RATE, 100).getCurrent() / 100f;
			
			if(player.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
				statRate *= CustomConfig.FACTION_BONUS_QUEST;
			}
			
			if(!RateConfig.IS_PROGRESSIVE_XP) {
				return (long) (reward * player.getRates().getQuestXpRate() * statRate);
			}
			
			if(player.getLevel() <= RateConfig.PROG_XP_1_LEVEL)
				return (long) (reward * RateConfig.PROG_QUESTXP_1_BONUS * player.getRates().getQuestXpRate() * statRate);
			else if(player.getLevel() <= RateConfig.PROG_XP_2_LEVEL)
				return (long) (reward * RateConfig.PROG_QUESTXP_2_BONUS * player.getRates().getQuestXpRate() * statRate);
			else if(player.getLevel() <= RateConfig.PROG_XP_3_LEVEL)
				return (long) (reward * RateConfig.PROG_QUESTXP_3_BONUS * player.getRates().getQuestXpRate() * statRate);
			else
				return (long) (reward * RateConfig.PROG_QUESTXP_4_BONUS * player.getRates().getQuestXpRate() * statRate);
			
		}
	},
	CRAFTING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_CRAFTING_XP_RATE, 100).getCurrent() / 100f;
			if(player.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
				statRate *= CustomConfig.FACTION_BONUS_CRAFT;
			}
			return (long) (reward * player.getRates().getCraftingXPRate() * statRate);
		}
	},
	GATHERING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GATHERING_XP_RATE, 100).getCurrent() / 100f;
			if(player.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
				statRate *= CustomConfig.FACTION_BONUS_GATHER;
			}
			return (long) (reward * player.getRates().getGatheringXPRate() * statRate);
		}
	};

	public abstract long calcReward(Player player, long reward);
}
