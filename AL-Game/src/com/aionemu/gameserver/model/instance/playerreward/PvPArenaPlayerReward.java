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
package com.aionemu.gameserver.model.instance.playerreward;

import com.aionemu.gameserver.model.gameobjects.player.Player;
//import com.aionemu.gameserver.model.instance.InstanceBuff;

/**
 *
 * @author xTz
 */
public class PvPArenaPlayerReward extends InstancePlayerReward {

	private int position;
	private int timeBonus;
	private float timeBonusModifier;
	private int basicAP;
	private int rankingAP;
	private int scoreAP;
	private int basicCrucible;
	private int rankingCrucible;
	private int scoreCrucible;
	private int basicCourage;
	private int rankingCourage;
	private int scoreCourage;
	private int platinumMedal;
	private int goldMedal;
	private int silverMedal;
	private int gloriousInsignia;
	private int lifeSerum;
	private long logoutTime;
	private boolean isRewarded = false;
	//private InstanceBuff boostMorale;

	public PvPArenaPlayerReward(Player player, int timeBonus/*, byte buffId*/) {
		super(player);
		super.addPoints(13000);
		this.timeBonus = timeBonus;
		timeBonusModifier = ((float) this.timeBonus / (float) 660000);
		//boostMorale = new InstanceBuff(buffId);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getTimeBonus() {
		return timeBonus > 0 ? timeBonus : 0;
	}

	public void updateLogOutTime() {
		logoutTime = System.currentTimeMillis();
	}

	public void updateBonusTime() {
		int offlineTime = (int) (System.currentTimeMillis() - logoutTime);
		timeBonus -= offlineTime * timeBonusModifier;
	}

	public boolean isRewarded() {
		return isRewarded;
	}

	public void setRewarded() {
		isRewarded = true;
	}

	public int getBasicAP() {
		return basicAP;
	}

	public int getRankingAP() {
		return rankingAP;
	}

	public int getScoreAP() {
		return scoreAP;
	}

	public void setBasicAP(int ap) {
		this.basicAP = ap;
	}

	public void setRankingAP(int ap) {
		this.rankingAP = ap;
	}

	public void setScoreAP(int ap) {
		this.scoreAP = ap;
	}

	public float getParticipation() {
		return /*(float) getTimeBonus() / timeBonus*/0;
	}

	public int getBasicCrucible() {
		return basicCrucible;
	}

	public int getRankingCrucible() {
		return rankingCrucible;
	}

	public int getScoreCrucible() {
		return scoreCrucible;
	}

	public void setBasicCrucible(int basicCrucible) {
		this.basicCrucible = basicCrucible;
	}

	public void setRankingCrucible(int rankingCrucible) {
		this.rankingCrucible = rankingCrucible;
	}

	public void setScoreCrucible(int scoreCrucible) {
		this.scoreCrucible = scoreCrucible;
	}

	public void setBasicCourage(int basicCourage) {
		this.basicCourage = basicCourage;
	}

	public void setRankingCourage(int rankingCourage) {
		this.rankingCourage = rankingCourage;
	}

	public void setScoreCourage(int scoreCourage) {
		this.scoreCourage = scoreCourage;
	}

	public int getBasicCourage() {
		return basicCourage;
	}

	public int getRankingCourage() {
		return rankingCourage;
	}

	public int getScoreCourage() {
		return scoreCourage;
	}

	public int getPlatinumMedal() {
		return platinumMedal;
	}

	public void setplatinumMedal(int platinumMedal) {
		this.platinumMedal = platinumMedal;
	}

	public int getGoldmMedal() {
		return goldMedal;
	}

	public void setGoldMedal(int goldMedal) {
		this.goldMedal = goldMedal;
	}

	public int getSilverMedal() {
		return silverMedal;
	}

	public void setSilverMedal(int silverMedal) {
		this.silverMedal = silverMedal;
	}

	public int getGloriousInsignia() {
		return gloriousInsignia;
	}

	public void setGloriousInsignia(int gloriousInsignia) {
		this.gloriousInsignia = gloriousInsignia;
	}

	public int getLifeSerum() {
		return lifeSerum;
	}

	public void setLifeSerum(int lifeSerum) {
		this.lifeSerum = lifeSerum;
	}

	public int getScorePoints() {
		return getPoints();//timeBonus + getPoints();
	}

	/*public boolean hasBoostMorale() {
		return boostMorale.hasInstanceBuff();
	}

	public void applyBoostMoraleEffect(Player player) {
		boostMorale.applyEffect(player, 25000);
	}

	public void endBoostMoraleEffect(Player player) {
		boostMorale.endEffect(player);
	}*/

	/*public int getRemaningTime() {
		int time = boostMorale.getRemaningTime();
		if (time >= 0 && time < 25) {
			return 25 - time;
		}
		return 0;
	}*/
}