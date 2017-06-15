/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * aion-emu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * aion-emu. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import javolution.util.FastList;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.DarkPoetaReward;
import com.aionemu.gameserver.model.instance.instancereward.DredgionReward;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.PvPArenaReward;
import com.aionemu.gameserver.model.instance.playerreward.CruciblePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.DredgionPlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Dns, ginho1, nrg, xTz
 */
@SuppressWarnings("rawtypes")
public class SM_INSTANCE_SCORE extends AionServerPacket {

	private int mapId;
	private int instanceTime;
	private InstanceScoreType instanceScoreType;
	private InstanceReward instanceReward;

	public SM_INSTANCE_SCORE(int instanceTime, InstanceReward instanceReward) {
		this.mapId = instanceReward.getMapId();
		this.instanceTime = instanceTime;
		this.instanceReward = instanceReward;
		instanceScoreType = instanceReward.getInstanceScoreType();
	}

	public SM_INSTANCE_SCORE(InstanceReward instanceReward, InstanceScoreType instanceScoreType) {
		this.mapId = instanceReward.getMapId();
		this.instanceReward = instanceReward;
		this.instanceScoreType = instanceScoreType;
	}

	public SM_INSTANCE_SCORE(InstanceReward instanceReward) {
		this.mapId = instanceReward.getMapId();
		this.instanceReward = instanceReward;
		this.instanceScoreType = instanceReward.getInstanceScoreType();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void writeImpl(AionConnection con) {
		int playerCount = 0;
		writeD(mapId);
		writeD(instanceTime);
		writeD(instanceScoreType.getId());
		switch (mapId) {
			case 300110000:
			case 300210000:
				fillTableWithGroup(Race.ELYOS);
				fillTableWithGroup(Race.ASMODIANS);
				DredgionReward dredgionReward = (DredgionReward) instanceReward;
				int elyosScore = dredgionReward.getPointsByRace(Race.ELYOS).intValue();
				int asmosScore = dredgionReward.getPointsByRace(Race.ASMODIANS).intValue();
				writeD(instanceScoreType.isEndProgress() ? (asmosScore > elyosScore ? 1 : 0) : 255);
				writeD(elyosScore);
				writeD(asmosScore);
				for (DredgionReward.DredgionRooms dredgionRoom : dredgionReward.getDredgionRooms()) {
					writeC(dredgionRoom.getState());
				}
				break;
			case 300320000:
			case 300300000:
				for (CruciblePlayerReward playerReward : (FastList<CruciblePlayerReward>) instanceReward.getPlayersInside()) {
					writeD(playerReward.getOwner()); // obj
					writeD(playerReward.getPoints()); // points
					writeD(playerReward.getPlayer().getPlayerClass().getClassId()); // unk
					writeD(playerReward.getInsignia());
					playerCount++;
				}
				if (playerCount < 6) {
					writeB(new byte[16 * (6 - playerCount)]); // spaces
				}
				break;
			case 300040000:
				DarkPoetaReward dpr = (DarkPoetaReward) instanceReward;
				writeD(dpr.getPoints());
				writeD(dpr.getNpcKills());
				writeD(dpr.getGatherCollections()); // gathers
				writeD(dpr.getRank()); // 7 for none, 8 for F, 5 for D, 4 C, 3 B, 2 A, 1 S
				break;
			case 300350000:
			case 300360000:
			case 300420000:
			case 300430000:
				PvPArenaReward arenaReward = (PvPArenaReward) instanceReward;
				int rank, points;
				boolean isRewarded = false;//arenaReward.isRewarded();
				for (InstancePlayerReward reward : arenaReward.getPlayersInside()) {
					PvPArenaPlayerReward playerReward = (PvPArenaPlayerReward) reward;
					points = playerReward.getPoints();
					rank = arenaReward.getRank(points);
					Player player = playerReward.getPlayer();
					writeD(player.getObjectId()); // obj
					writeD(playerReward.getPvPKills()); // kills
					writeD(isRewarded ? points + playerReward.getTimeBonus() : points); // points
					writeD(player.getAbyssRank().getRank().getId()); // abyss rank
					writeC(0); // unk
					writeC(player.getPlayerClass().getClassId()); // class id
					writeC(1); // unk
					writeC(rank); // top position
					writeD(0); // 0F 00 00 00 
					writeD(arenaReward.getRankBonus(rank)); // rank bonus
					writeD(isRewarded ? playerReward.getTimeBonus() : 0); // time bonus
					writeD(0); // unk
					writeD(0); // unk
					writeS(player.getName(), 52); // playerName
					playerCount++;
				}
				if (playerCount < 12) {
					writeB(new byte[92 * (12 - playerCount)]); // spaces
				}
				PvPArenaPlayerReward rewardedPlayer = arenaReward.getPlayerReward(con.getActivePlayer());
				if (isRewarded && arenaReward.canRewarded()) {
					writeD(rewardedPlayer.getScoreAP()); // abyss points
					writeD(186000130); // 186000130
					writeD(rewardedPlayer.getScoreCrucible()); // Crucible Insignia
					writeD(186000137); // 186000137
					writeD(rewardedPlayer.getScoreCourage()); // Courage Insignia
				}
				else {
					writeB(new byte[20]);
				}
				writeD(0); // unk
				writeD(0); // unk
				writeD(0); // unk
				writeD(0); // unk
				writeD(0); // unk
				writeD(0); // unk
				writeD(arenaReward.getRound()); // round
				writeD(arenaReward.getCapPoints()); // cap points
				writeD(3); // unk
				writeD(0); // unk
				break;
		}
	}

	private void fillTableWithGroup(Race race) {
		int count = 0;
		DredgionReward dredgionReward = (DredgionReward) instanceReward;
		for (InstancePlayerReward playerReward : dredgionReward.getPlayersInsideByRace(race)) {
			DredgionPlayerReward dpr = (DredgionPlayerReward) playerReward;
			Player member = dpr.getPlayer();
			writeD(member.getObjectId()); // playerObjectId
			writeD(member.getAbyssRank().getRank().getId()); // playerRank
			writeD(dpr.getPvPKills()); // pvpKills
			writeD(dpr.getMonsterKills()); // monsterKills
			writeD(dpr.getZoneCaptured()); // captured
			writeD(dpr.getPoints()); // playerScore

			if (instanceScoreType.isEndProgress()) {
				boolean winner = race.equals(dredgionReward.getWinningRace());
				writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints()) + (int) (dpr.getPoints() * 1.6f)); // apBonus1
				writeD((winner ? dredgionReward.getWinnerPoints() : dredgionReward.getLooserPoints())); // apBonus2
			}
			else {
				writeB(new byte[8]);
			}

			writeC(member.getPlayerClass().getClassId()); // playerClass
			writeC(0); // unk
			writeS(member.getName(), 54); // playerName
			count++;
		}
		if (count < 6) {
			writeB(new byte[88 * (6 - count)]); // spaces
		}
	}

}