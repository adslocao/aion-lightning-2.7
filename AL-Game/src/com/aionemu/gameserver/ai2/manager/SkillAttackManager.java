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
package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public class SkillAttackManager {

	/**
	 * @param npcAI
	 * @param delay
	 */
	public static void performAttack(NpcAI2 npcAI, int delay) {
		if (npcAI.setSubStateIfNot(AISubState.CAST)) {
			if (delay > 0) {
				ThreadPoolManager.getInstance().schedule(new SkillAction(npcAI), delay);
			}
			else {
				skillAction(npcAI);
			}
		}
	}

	/**
	 * @param npcAI
	 */
	protected static void skillAction(NpcAI2 npcAI) {
		Creature target = (Creature) npcAI.getOwner().getTarget();
		if (target != null && !target.getLifeStats().isAlreadyDead()) {
			final int skillId = npcAI.getSkillId();
			final int skillLevel = npcAI.getSkillLevel();

			SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			int duration = template.getDuration();
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Using skill " + skillId + " level: " + skillLevel + " duration: " + duration);
			}
			switch (template.getSubType()) {
				case BUFF:
					switch (template.getProperties().getFirstTarget()) {
						case ME:
							if (npcAI.getOwner().getEffectController().isAbnormalPresentBySkillId(skillId)) {
								afterUseSkill(npcAI);
								return;
							}
							break;
						default:
							if (target.getEffectController().isAbnormalPresentBySkillId(skillId)) {
								afterUseSkill(npcAI);
								return;
							}
					}
					break;
			}
			boolean success = npcAI.getOwner().getController().useSkill(skillId, skillLevel);
			if (!success || duration == 0) {
				afterUseSkill(npcAI);
			}
			else {
				ThreadPoolManager.getInstance().schedule(new AfterSkillAction(npcAI), duration);
			}
		}
		else {
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}

	}

	/**
	 * @param npcAI
	 */
	protected static void afterUseSkill(NpcAI2 npcAI) {
		npcAI.setSubStateIfNot(AISubState.NONE);
		npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
	}

	/**
	 * @param npcAI
	 * @return
	 */
	public static NpcSkillEntry chooseNextSkill(NpcAI2 npcAI) {
		if (npcAI.isInSubState(AISubState.CAST) || npcAI.getOwner().isCasting()) {
			return null;
		}

		Npc owner = npcAI.getOwner();
		NpcSkillList skillList = owner.getSkillList();
		if (skillList == null || skillList.size() == 0) {
			return null;
		}

		npcAI.getOwner().getGameStats().renewLastSkillTime();

		if (npcAI.getOwner().getGameStats().canUseNextSkill()) {
			NpcSkillEntry npcSkill = skillList.getRandomSkill();
			int currentHpPercent = owner.getLifeStats().getHpPercentage();
			if (npcSkill.hpReady(currentHpPercent) && npcSkill.chanceReady()) {
				// Check for Bind/Silence/Fear debuffs on npc
				SkillTemplate template = npcSkill.getSkillTemplate();
				if ((template.getType() == SkillType.MAGICAL && owner.getEffectController().isAbnormalSet(AbnormalState.SILENCE))
						|| (template.getType() == SkillType.PHYSICAL && owner.getEffectController().isAbnormalSet(AbnormalState.BIND))
						|| (owner.getEffectController().isUnderFear()))
					return null;
				
				return npcSkill;
			}
		}
		return null;
	}

	private final static class SkillAction implements Runnable {

		private NpcAI2 npcAI;

		SkillAction(NpcAI2 npcAI) {
			this.npcAI = npcAI;
		}

		@Override
		public void run() {
			skillAction(npcAI);
			npcAI = null;
		}
	}

	private final static class AfterSkillAction implements Runnable {

		private NpcAI2 npcAI;

		AfterSkillAction(NpcAI2 npcAI) {
			this.npcAI = npcAI;
		}

		@Override
		public void run() {
			afterUseSkill(npcAI);
			npcAI = null;
		}
	}

}
