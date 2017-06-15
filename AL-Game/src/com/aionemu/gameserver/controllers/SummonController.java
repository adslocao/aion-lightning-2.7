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
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.Summon.SummonMode;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_OWNER_REMOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_PANEL_REMOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 * @author RotO (Attack-speed hack protection)
 * modified by Sippolo
 */
public class SummonController extends CreatureController<Summon> {

	private long lastAttackMilis = 0;
	private boolean isAttacked = false;
	private int releaseAfterSkill = -1;

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (getOwner().getMaster() == null)
			return;

		if (object.getObjectId() == getOwner().getMaster().getObjectId()) {
			release(UnsummonType.DISTANCE);
		}
	}

	@Override
	public Summon getOwner() {
		return (Summon) super.getOwner();
	}

	/**
	 * Release summon
	 */
	public void release(final UnsummonType unsummonType) {
		final Summon owner = getOwner();

		if (owner.getMode() == SummonMode.RELEASE)
			return;
		owner.setMode(SummonMode.RELEASE);

		final Player master = owner.getMaster();
		final int summonObjId = owner.getObjectId();
		final VisibleObject target = master.getTarget();

		switch (unsummonType) {
			case COMMAND:
				PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMON_FOLLOWER(getOwner().getNameId()));
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
				break;
			case DISTANCE:
				PacketSendUtility.sendPacket(getOwner().getMaster(), SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMON_BY_TOO_DISTANCE);
				PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
				break;
			case LOGOUT:
			case UNSPECIFIED:
				break;
		}

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {

				owner.getController().delete();
				owner.setMaster(null);
				master.setSummon(null);

				switch (unsummonType) {
					case COMMAND:
					case DISTANCE:
					case UNSPECIFIED:
						PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_UNSUMMONED(getOwner().getNameId()));
						PacketSendUtility.sendPacket(master, new SM_SUMMON_OWNER_REMOVE(summonObjId));

						// TODO temp till found on retail
						PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL_REMOVE());
						if (target instanceof Creature) {
							final Creature lastAttacker = (Creature) target;
							if (!master.getLifeStats().isAlreadyDead() && !lastAttacker.getLifeStats().isAlreadyDead() && isAttacked) {
								ThreadPoolManager.getInstance().schedule(new Runnable() {

									@Override
									public void run() {
										lastAttacker.getAggroList().addHate(master, 1);
									}
								}, 1000);
							}
						}
						break;
					case LOGOUT:
						break;
				}
			}
		}, 5000);
	}

	/**
	 * Change to rest mode
	 */
	public void restMode() {
		getOwner().setMode(SummonMode.REST);
		Player master = getOwner().getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_REST_MODE(getOwner().getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
		getOwner().getLifeStats().triggerRestoreTask();
	}

	public void setUnkMode() {
		getOwner().setMode(SummonMode.UNK);
		Player master = getOwner().getMaster();
		//PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_REST_MODE(getOwner().getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
	}

	/**
	 * Change to guard mode
	 */
	public void guardMode() {
		getOwner().setMode(SummonMode.GUARD);
		Player master = getOwner().getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_GUARD_MODE(getOwner().getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
		getOwner().getLifeStats().triggerRestoreTask();
	}

	/**
	 * Change to attackMode
	 */
	public void attackMode() {
		getOwner().setMode(SummonMode.ATTACK);
		Player master = getOwner().getMaster();
		PacketSendUtility.sendPacket(master, SM_SYSTEM_MESSAGE.STR_SKILL_SUMMON_ATTACK_MODE(getOwner().getNameId()));
		PacketSendUtility.sendPacket(master, new SM_SUMMON_UPDATE(getOwner()));
		getOwner().getLifeStats().cancelRestoreTask();
	}

	@Override
	public void attackTarget(Creature target, int time) {

		Player master = getOwner().getMaster();

		if (!RestrictionsManager.canAttack(master, target))
			return;

		int attackSpeed = getOwner().getGameStats().getAttackSpeed().getCurrent();
		long milis = System.currentTimeMillis();
		if (milis - lastAttackMilis < attackSpeed) {
			/**
			 * Hack!
			 */
			return;
		}
		lastAttackMilis = milis;
		
		super.attackTarget(target, time);
	}

	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (getOwner().getLifeStats().isAlreadyDead())
			return;

		// temp
		if (getOwner().getMode() == SummonMode.RELEASE)
			return;

		super.onAttack(creature, skillId, type, damage, notifyAttack, log);
		getOwner().getLifeStats().reduceHp(damage, creature);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), TYPE.REGULAR, 0, damage, log));
		PacketSendUtility.sendPacket(getOwner().getMaster(), new SM_SUMMON_UPDATE(getOwner()));
	}

	@Override
	public void onDie(final Creature lastAttacker) {
		super.onDie(lastAttacker);
		release(UnsummonType.UNSPECIFIED);
		Summon owner = getOwner();
		final Player master = getOwner().getMaster();
		PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.DIE, 0, lastAttacker == null ? 0
			: lastAttacker.getObjectId()));

		if (lastAttacker != null && !master.getLifeStats().isAlreadyDead()
			&& !lastAttacker.getLifeStats().isAlreadyDead()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					lastAttacker.getAggroList().addHate(master, 1);
				}
			}, 1000);
		}
	}

	public void useSkill(int skillId, Creature target) {
		Creature creature = getOwner();
		boolean petHasSkill = DataManager.PET_SKILL_DATA.petHasSkill(getOwner().getObjectTemplate().getTemplateId(),
			skillId);
		if (!petHasSkill) {
			// hackers!)
			return;
		}
		Skill skill = SkillEngine.getInstance().getSkill(creature, skillId, 1, target);
		if (skill != null) {
			// If skill succeeds, handle automatic release if expected
			if ( (skill.useSkill()) && (skillId == releaseAfterSkill) )
			{
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						release(UnsummonType.UNSPECIFIED);
					}
				}, 1000);
			}
			setReleaseAfterSkill(-1);
		}
	}

	public static enum UnsummonType {
		LOGOUT,
		DISTANCE,
		COMMAND,
		UNSPECIFIED
	}
	
	/**
	 * Handle automatic release if Ultra Skill demands it
	 * @param is the skill commanded by summoner, after which pet is automatically dismissed
	 */
	public void setReleaseAfterSkill(int skillId)
	{
		this.releaseAfterSkill = skillId;
	}

	@Override
	public void onStartMove() {
		super.onStartMove();
		getOwner().getMoveController().setInMove(true);
		getOwner().getObserveController().notifyMoveObservers();
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
	}
	
	@Override
	public void onStopMove() {
		super.onStopMove();
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
	}

	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}
}
