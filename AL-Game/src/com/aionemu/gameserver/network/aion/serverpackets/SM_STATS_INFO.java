/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;

/**
 * In this packet Server is sending User Info?
 * 
 * @author -Nemesiss-
 * @author Luno
 */
public class SM_STATS_INFO extends AionServerPacket {

	/**
	 * Player that stats info will be send
	 */
	private Player player;
	private PlayerGameStats pgs;
	private PlayerLifeStats pls;
	private PlayerCommonData pcd;

	/**
	 * Constructs new <tt>SM_UI</tt> packet
	 * 
	 * @param player
	 */
	public SM_STATS_INFO(Player player) {
		this.player = player;
		this.pcd = player.getCommonData();
		this.pgs = player.getGameStats();
		this.pls = player.getLifeStats();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(player.getObjectId());
		writeD(GameTimeManager.getGameTime().getTime()); // Minutes since 1/1/00 00:00:00

		Stat2 power = pgs.getPower();
		writeH(power.getCurrent());// [current power]
		Stat2 health = pgs.getHealth();
		writeH(health.getCurrent());// [current health]
		Stat2 accuracy = pgs.getAccuracy();
		writeH(accuracy.getCurrent());// [current accuracy]
		Stat2 agility = pgs.getAgility();
		writeH(agility.getCurrent());// [current agility]
		Stat2 knowledge = pgs.getKnowledge();
		writeH(knowledge.getCurrent());// [current knowledge]
		Stat2 will = pgs.getWill();
		writeH(will.getCurrent());// [current will]

		Stat2 wr = pgs.getStat(StatEnum.WATER_RESISTANCE, 0);
		Stat2 windr = pgs.getStat(StatEnum.WIND_RESISTANCE, 0);
		Stat2 er = pgs.getStat(StatEnum.EARTH_RESISTANCE, 0);
		Stat2 fr = pgs.getStat(StatEnum.FIRE_RESISTANCE, 0);
		Stat2 erl = pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_LIGHT, 0);
		Stat2 erd = pgs.getStat(StatEnum.ELEMENTAL_RESISTANCE_DARK, 0);

		writeH(wr.getCurrent());// [current water]
		writeH(windr.getCurrent());// [current wind]
		writeH(er.getCurrent());// [current earth]
		writeH(fr.getCurrent());// [current fire]
		writeH(erl.getCurrent());// [current light resistance]
		writeH(erd.getCurrent());// [current dark resistance]

		writeH(player.getLevel());// [level]

		// something like very dynamic
		writeH(0); // [unk]
		writeH(0);// [unk]
		writeH(0);// [unk]

		writeQ(pcd.getExpNeed());// [xp till next lv]
		writeQ(pcd.getExpRecoverable()); // [recoverable exp]
		writeQ(pcd.getExpShown()); // [current xp]

		writeD(0); // [unk]
		Stat2 maxHp = pgs.getMaxHp();
		writeD(maxHp.getCurrent()); // [max hp]
		writeD(pls.getCurrentHp());// [current hp]

		Stat2 maxMp = pgs.getMaxMp();
		writeD(maxMp.getCurrent());// [max mana]
		writeD(pls.getCurrentMp());// [current mana]

		Stat2 maxDp = pgs.getMaxDp();
		writeH(maxDp.getCurrent());// [max dp]
		writeH(pcd.getDp());// [current dp]

		Stat2 flyTime = pgs.getFlyTime();
		writeD(flyTime.getCurrent());// [max fly time]
		writeD(pls.getCurrentFp());// [current fly time]

		writeC(player.getFlyState());// [fly state]
		writeC(0);// [unk]

		Stat2 mainHandPAtk = pgs.getMainHandPAttack();
		Stat2 offHandPAtk = pgs.getOffHandPAttack();
		writeH(mainHandPAtk.getCurrent()); // [current main hand attack]
		writeH(offHandPAtk.getCurrent()); // [off hand attack]

		Stat2 pdef = pgs.getPDef();
		Stat2 mAtk = pgs.getMAttack();
		Stat2 mresist = pgs.getMResist();

		writeH(pdef.getCurrent());// [current pdef]
		writeH(mAtk.getCurrent());// [current magic attack ?]
		writeH(mresist.getCurrent()); // [current mres]

		Stat2 arange = pgs.getAttackRange();
		Stat2 aspeed = pgs.getAttackSpeed();
		writeF(arange.getCurrent() / 1000f);// attack range
		writeH(aspeed.getCurrent());// attack speed

		Stat2 evasion = pgs.getEvasion();
		Stat2 parry = pgs.getParry();
		Stat2 block = pgs.getBlock();
		writeH(evasion.getCurrent());// [current evasion]
		writeH(parry.getCurrent());// [current parry]
		writeH(block.getCurrent());// [current block]

		Stat2 mainHandPCrit = pgs.getMainHandPCritical();
		Stat2 offHandPCrit = pgs.getOffHandPCritical();
		Stat2 mainHandPAcc = pgs.getMainHandPAccuracy();
		Stat2 offHandPAcc = pgs.getOffHandPAccuracy();
		writeH(mainHandPCrit.getCurrent());// [current main hand crit rate]
		writeH(offHandPCrit.getCurrent());// [current off hand crit rate]
		writeH(mainHandPAcc.getCurrent());// [current main_hand_accuracy]
		writeH(offHandPAcc.getCurrent());// [current off_hand_accuracy]

		writeH(0);// [unk]

		Stat2 mAcc = pgs.getMAccuracy();
		Stat2 mCrit = pgs.getMCritical();
		writeH(mAcc.getCurrent());// [current magic accuracy]
		writeH(mCrit.getCurrent());// [current crit spell]
		writeH(0); // [old current magic boost location]

		Stat2 boostCastTime = pgs.getReverseStat(StatEnum.BOOST_CASTING_TIME, 1000);
		writeF(boostCastTime.getCurrent() / 1000f);// [current casting speed]

		Stat2 concentration = pgs.getStat(StatEnum.CONCENTRATION, 0);
		Stat2 mBoost = pgs.getMBoost();
		int totalBoostMagicalSkill = mBoost.getCurrent();
		if (totalBoostMagicalSkill > 2700)
			totalBoostMagicalSkill = 2700;
		Stat2 healBoost = pgs.getStat(StatEnum.HEAL_BOOST, 0);
		writeH(concentration.getCurrent());// [current concetration]
		// Was mBoost.getCurrent() before optimizing
		writeH(totalBoostMagicalSkill); // [current magic boost] 1.9 version
		writeH(healBoost.getCurrent()); // [current heal_boost]

		Stat2 pCritResist = pgs.getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, 0);
		Stat2 mCritResist = pgs.getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0);
		Stat2 pCritDamReduce = pgs.getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0);
		Stat2 mCritDamReduce = pgs.getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0);
		writeH(pCritResist.getCurrent()); // [current strike resist]
		writeH(mCritResist.getCurrent());// [current spell resist]
		writeH(pCritDamReduce.getCurrent());// [current strike fortitude]
		writeH(mCritDamReduce.getCurrent());// [current spell fortitude]
		writeH(20511);// [unk] 1.9 version

		writeD(player.getInventory().getLimit());// [unk]

		writeD(player.getInventory().size());// [unk]
		writeD(0);// [unk]
		writeD(0);// [unk]
		writeD(pcd.getPlayerClass().getClassId());// [Player Class id]

		writeQ(0);// [unk] 1.9 version
		writeQ(pcd.getCurrentReposteEnergy());
		writeQ(pcd.getMaxReposteEnergy());

		writeC(pcd.getCurrentSalvationPercent());

		//unk
		writeB(new byte[7]);

		writeH(power.getBase());// [base power]
		writeH(health.getBase());// [base health]
		writeH(accuracy.getBase());// [base accuracy]
		writeH(agility.getBase());// [base agility]
		writeH(knowledge.getBase());// [base knowledge]
		writeH(will.getBase());// [base will]
		writeH(wr.getBase());// [base water res]
		writeH(windr.getBase());// [base water res]
		writeH(er.getBase());// [base earth resist]
		writeH(fr.getBase());// [base water res]

		writeD(0);// [unk]

		writeD(maxHp.getBase());// [base hp]
		writeD(maxMp.getBase());// [base mana]
		writeD(maxDp.getBase());// [base dp]
		writeD(flyTime.getBase());// [fly time]

		writeH(mainHandPAtk.getBase());// [base main hand attack]
		writeH(offHandPAtk.getBase());// [base off hand attack]
		writeH(mAtk.getBase()); // [base magic attack ?]
		writeH(pdef.getBase()); // [base pdef]
		writeH(mresist.getBase()); // [base magic res]

		writeH(0); // [unk]

		writeF(arange.getBase() / 1000f);// [base attack range]

		writeH(evasion.getBase()); // [base evasion]
		writeH(parry.getBase()); // [base parry]
		writeH(block.getBase()); // [base block]

		writeH(mainHandPCrit.getBase()); // [base main hand crit rate]
		writeH(offHandPCrit.getBase()); // [base off hand crit rate]

		writeH(mCrit.getBase()); // [base magical crit rate]
		writeH(0); // [unk] VERSION 1.9

		writeH(mainHandPAcc.getBase()); // [base main hand accuracy]
		writeH(offHandPAcc.getBase()); // [base off hand accuracy]

		writeH(0); // [base Casting speed] VERSION 1.9

		writeH(mAcc.getBase());// [base magic accuracy]

		writeH(concentration.getBase()); // [base concentration]
		writeH(mBoost.getBase());// [base magic boost]

		writeH(healBoost.getBase()); // [base healboost]
		writeH(pCritResist.getBase()); // [base strike resist]
		writeH(mCritResist.getBase()); // [base spell resist]
		writeH(pCritDamReduce.getBase()); // [base strike fortitude]
		writeH(mCritDamReduce.getBase()); // [base spell fortitude]
	}
}
