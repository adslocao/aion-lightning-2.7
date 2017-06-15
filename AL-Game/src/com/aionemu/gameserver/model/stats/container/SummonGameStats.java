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
package com.aionemu.gameserver.model.stats.container;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class SummonGameStats extends CreatureGameStats<Summon> {

	private int cachedSpeed;
	private final SummonStatsTemplate statsTemplate;

	/**
	 * @param owner
	 * @param statsTemplate
	 */
	public SummonGameStats(Summon owner, SummonStatsTemplate statsTemplate) {
		super(owner);
		this.statsTemplate = statsTemplate;
	}

	@Override
	protected void onStatsChange() {
		updateStatsAndSpeedVisually();
	}

	public void updateStatsAndSpeedVisually() {
		updateStatsVisually();
		checkSpeedStats();
	}

	public void updateStatsVisually() {
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_STATS);
	}

	private void checkSpeedStats() {
		int current = getMovementSpeed().getCurrent();
		if (current != cachedSpeed) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
		cachedSpeed = current;
	}

	@Override
	public Stat2 getStat(StatEnum statEnum, int base) {
		Stat2 stat = super.getStat(statEnum, base);
		if (owner.getMaster() == null)
			return stat;
		switch (statEnum) {
			case MAXHP:
			case PHYSICAL_DEFENSE:
			case EVASION:
			case BOOST_MAGICAL_SKILL:
			case MAGICAL_ACCURACY:
			case MAGICAL_RESIST:
				stat.setBonusRate(0.2f);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_ACCURACY:
				stat.setBonusRate(0.2f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_ACCURACY, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_CRITICAL:
				stat.setBonusRate(0.2f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_CRITICAL, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);
			case PHYSICAL_ATTACK:
				stat.setBonusRate(0.2f);
				owner.getMaster().getGameStats().getItemStatBoost(StatEnum.MAIN_HAND_POWER, stat);
				return owner.getMaster().getGameStats().getItemStatBoost(statEnum, stat);

		}
		return stat;
	}

	@Override
	public Stat2 getMaxHp() {
		return getStat(StatEnum.MAXHP, statsTemplate.getMaxHp());
	}

	@Override
	public Stat2 getMaxMp() {
		return getStat(StatEnum.MAXHP, statsTemplate.getMaxMp());
	}

	@Override
	public Stat2 getAttackSpeed() {
		return getStat(StatEnum.ATTACK_SPEED, owner.getObjectTemplate().getAttackDelay());
	}

	@Override
	public Stat2 getMovementSpeed() {
		return getStat(StatEnum.SPEED, Math.round(statsTemplate.getRunSpeed() * 1000));
	}

	@Override
	public Stat2 getAttackRange() {
		return getStat(StatEnum.ATTACK_RANGE, Math.round(owner.getObjectTemplate().getAttackRange() * 1000));
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_DEFENSE, statsTemplate.getPdefense());
	}

	@Override
	public Stat2 getMResist() {
		return getStat(StatEnum.MAGICAL_RESIST, statsTemplate.getMresist());
	}

	@Override
	public Stat2 getPower() {
		return getStat(StatEnum.POWER, 100);
	}

	@Override
	public Stat2 getHealth() {
		return getStat(StatEnum.HEALTH, 100);
	}

	@Override
	public Stat2 getAccuracy() {
		return getStat(StatEnum.PHYSICAL_ACCURACY, 100);
	}

	@Override
	public Stat2 getAgility() {
		return getStat(StatEnum.AGILITY, 100);
	}

	@Override
	public Stat2 getKnowledge() {
		return getStat(StatEnum.KNOWLEDGE, 100);
	}

	@Override
	public Stat2 getWill() {
		return getStat(StatEnum.WILL, 100);
	}

	@Override
	public Stat2 getEvasion() {
		return getStat(StatEnum.EVASION, statsTemplate.getEvasion());
	}

	@Override
	public Stat2 getParry() {
		return getStat(StatEnum.PARRY, statsTemplate.getParry());
	}

	@Override
	public Stat2 getBlock() {
		return getStat(StatEnum.BLOCK, statsTemplate.getBlock());
	}

	@Override
	public Stat2 getMainHandPAttack() {
		return getStat(StatEnum.PHYSICAL_ATTACK, statsTemplate.getMainHandAttack());
	}

	@Override
	public Stat2 getMainHandPCritical() {
		return getStat(StatEnum.PHYSICAL_CRITICAL, statsTemplate.getMainHandCritRate());
	}

	@Override
	public Stat2 getMainHandPAccuracy() {
		return getStat(StatEnum.PHYSICAL_ACCURACY, statsTemplate.getMainHandAccuracy());
	}

	@Override
	public Stat2 getMAttack() {
		return getStat(StatEnum.MAGICAL_ATTACK, 100);
	}

	@Override
	public Stat2 getMBoost() {
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, statsTemplate.getMainHandAccuracy());
	}

	@Override
	public Stat2 getMAccuracy() {
		return getStat(StatEnum.MAGICAL_ACCURACY, statsTemplate.getMagicAccuracy());
	}

	@Override
	public Stat2 getMCritical() {
		return getStat(StatEnum.MAGICAL_CRITICAL, 50);
	}

	@Override
	public Stat2 getHpRegenRate() {
		return getStat(StatEnum.REGEN_HP, owner.getLevel() + 3);
	}

	@Override
	public Stat2 getMpRegenRate() {
		throw new IllegalStateException("No mp regen for Summon");
	}

	@Override
	public void updateStatInfo() {
		Player master = owner.getMaster();
		if (master != null) {
			PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(owner));
		}
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2, 0, 0));
	}
}
