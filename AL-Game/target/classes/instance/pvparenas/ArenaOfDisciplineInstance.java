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
package instance.pvparenas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

/**
 *
 * @author xTz
 */
@InstanceID(300360000)
public class ArenaOfDisciplineInstance extends DisciplineTrainingGroundsInstance {

	@Override
	protected boolean isPvpArena() {
		return true;
	}

	@Override
	protected void reward() {
		// int totalPoints = instanceReward.getTotalPoints();
		// int size = instanceReward.getInstanceRewards().size();
		// 100 * (rate * size) * (playerScore / playersScore)
		// float totalAP = (3.292f * size) * 100; // to do config
		// float totalCrucible = (1.964f * size) * 100; // to do config
		// float totalCourage = (0.174f * size) * 100; // to do config
		for (InstancePlayerReward playerReward : instanceReward.getInstanceRewards()) {
			PvPArenaPlayerReward reward = (PvPArenaPlayerReward) playerReward;
			if (!reward.isRewarded()) {
				// float playerRate = 1;
				// Player player = instance.getPlayer(playerReward.getOwner());

				int score = reward.getScorePoints();

				// AP
				// float scoreRate = ((float) score / (float) totalPoints);
				int rank = instanceReward.getRank(score);
				/*
				 * float percent = reward.getParticipation(); int basicAP = 200;
				 * // to do other formula int rankingAP = 431; if (size > 1) {
				 * rankingAP = rank == 0 ? 1108 : 431; } int scoreAP = (int)
				 * (totalAP * scoreRate); basicAP *= percent; rankingAP *=
				 * percent; rankingAP *= playerRate; reward.setBasicAP(basicAP);
				 * reward.setRankingAP(rankingAP); reward.setScoreAP(scoreAP);
				 */
				reward.setBasicAP(0);
				reward.setRankingAP(0);
				reward.setScoreAP(rank == 0 ? 2000 : 1000);

				// insigne orda
				/*
				 * int basicCrI = 195; basicCrI *= percent; // to do other
				 * formula int rankingCrI = 256; if (size > 1) { rankingCrI =
				 * rank == 0 ? 660 : 256; } rankingCrI *= percent; rankingCrI *=
				 * playerRate; int scoreCrI = (int) (totalCrucible * scoreRate);
				 * reward.setBasicCrucible(basicCrI);
				 * reward.setRankingCrucible(rankingCrI);
				 * reward.setScoreCrucible(scoreCrI);
				 */
				reward.setBasicCrucible(0);
				reward.setRankingCrucible(0);
				reward.setScoreCrucible(rank == 0 ? 550 : 350);

				// insigne courage
				/*
				 * int basicCoI = 0; basicCoI *= percent; // to do other formula
				 * int rankingCoI = 23; if (size > 1) { rankingCoI = rank == 0 ?
				 * 59 : 23; } rankingCoI *= percent; rankingCoI *= playerRate;
				 * int scoreCoI = (int) (totalCourage * scoreRate);
				 * reward.setBasicCourage(basicCoI);
				 * reward.setRankingCourage(rankingCoI);
				 * reward.setScoreCourage(scoreCoI);
				 */
				reward.setBasicCourage(0);
				reward.setRankingCourage(0);
				reward.setScoreCourage(rank == 0 ? 50 : 30);
			}
		}
		super.reward();
	}

	@Override
	protected boolean canStart() {
		boolean res = super.canStart();
		if (!res) {
			return false;
		}

		int itemId = getIdEnteringItem();

		// verif ip et mac
		Map<String, String> allIp = new HashMap<String, String>();
		for (Player p : instance.getPlayersInside()) {
			String ip1 = p.getClientConnection().getMacAddress();
			allIp.put(ip1, "");
		}
		if (instance.getPlayersInside().size() != allIp.size() && NetworkConfig.GAMESERVER_ID != 100) {
			sendPacket(new SM_SYSTEM_MESSAGE(1401303));
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			// reward();
			sendPacket();
			return false;
		}

		for (Player p : instance.getPlayersInside()) {
			List<Item> items = p.getInventory().getItemsByItemId(itemId);
			items = sort(items, on(Item.class).getExpireTime());
			for (Item item : items) {
				ticketRemoved.put(p.getObjectId(), 1);
				p.getInventory().decreaseItemCount(item, 1);
				break;
			}
		}
		return true;
	}

	@Override
	protected int getIdEnteringItem() {
		return 186000136;
	}

}
