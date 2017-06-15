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
import com.aionemu.gameserver.model.stats.container.StatEnum;

/**
 * @author antness
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
			if(player.isLowXP() && player.getLevel() <= 55) 
				return (long) (reward * 3 * statRate);
			
			else {
				if(player.getLevel() <= 25)
					return (long) (reward * 12 * player.getRates().getXpRate() * statRate);
				else if(player.getLevel() <= 50)
					return (long) (reward * 10 * player.getRates().getXpRate() * statRate);
				else if(player.getLevel() <= 55)
					return (long) (reward * 8 * player.getRates().getXpRate() * statRate);				
				else 
					return (long) (reward * 8 * player.getRates().getXpRate() * statRate);
			}
		}
	},
	GROUP_HUNTING {

		@Override
		public long calcReward(Player player, long reward) {
			float statRate = player.getGameStats().getStat(StatEnum.BOOST_GROUP_HUNTING_XP_RATE, 100).getCurrent() / 100f;
			if(player.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
				statRate *= CustomConfig.FACTION_BONUS_HUNT;
			}
			if(player.isLowXP() && player.getLevel() <= 55) {
			return (long) (reward * 10 * statRate);
			
			}
			else {
				if(player.getLevel() <= 25)
					return (long) (reward * 15 * player.getRates().getGroupXpRate() * statRate);
				else if(player.getLevel() <= 50)
					return (long) (reward * 13 * player.getRates().getGroupXpRate() * statRate);
				else if(player.getLevel() <= 55)
					return (long) (reward * 11 * player.getRates().getGroupXpRate() * statRate);				
				else 
					return (long) (reward * 11 * player.getRates().getGroupXpRate() * statRate);
			}
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
			if(player.isLowXP() && player.getLevel() <= 55) {
				return (long) (reward * 3 * statRate);
			}
			else {			
			
				if(player.getLevel() <= 25)
					return (long) (reward * 12 * player.getRates().getQuestXpRate() * statRate);
				else if(player.getLevel() <= 50)
					return (long) (reward * 10 * player.getRates().getQuestXpRate() * statRate);
				else if(player.getLevel() <= 55)
					return (long) (reward * 8 * player.getRates().getQuestXpRate() * statRate);				
				else 
					return (long) (reward * 8 * player.getRates().getQuestXpRate() * statRate);
			}
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
