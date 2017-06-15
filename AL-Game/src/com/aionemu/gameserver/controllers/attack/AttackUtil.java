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
package com.aionemu.gameserver.controllers.attack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.SkillElement;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.skillengine.change.Func;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HitType;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * @author ATracer
 */
public class AttackUtil {

	/**
	 * Calculate physical attack status and damage
	 */
	public static List<AttackResult> calculatePhysicalAttackResult(Creature attacker, Creature attacked) {
		AttackStatus attackerStatus = calculateAttackerPhysicalStatus(attacker);
		int damage = StatFunctions.calculateBasePhysicalDamage(attacker, attacked, true);
		List<AttackResult> attackList = new ArrayList<AttackResult>();
		AttackStatus mainHandStatus = calculateMainHandResult(attacker, attacked, attackerStatus, damage, attackList);
		if (attacker instanceof Player && ((Player) attacker).getEquipment().getOffHandWeaponType() != null) {
			calculateOffHandResult(attacker, attacked, mainHandStatus, attackList);
		}
		attacked.getObserveController().checkShieldStatus(attackList, attacker);
		return attackList;
	}

	/**
	 * Calculate physical attack status and damage of the MAIN hand
	 */
	private static final AttackStatus calculateMainHandResult(Creature attacker, Creature attacked,
		AttackStatus attackerStatus, int damage, List<AttackResult> attackList) {
		AttackStatus mainHandStatus = attackerStatus;
		if (mainHandStatus == null)
			mainHandStatus = calculatePhysicalStatus(attacker, attacked, true);

		int mainHandHits = 1;
		if (attacker instanceof Player) {
			Item mainHandWeapon = ((Player) attacker).getEquipment().getMainHandWeapon();
			if (mainHandWeapon != null)
				mainHandHits = Rnd.get(1, mainHandWeapon.getItemTemplate().getWeaponStats().getHitCount());
		}
		else {
			mainHandHits = Rnd.get(1, 3);
		}
		splitPhysicalDamage(attacker, attacked, mainHandHits, damage, mainHandStatus, attackList);
		return mainHandStatus;
	}

	/**
	 * Calculate physical attack status and damage of the OFF hand
	 */
	private static final void calculateOffHandResult(Creature attacker, Creature attacked, AttackStatus mainHandStatus,
		List<AttackResult> attackList) {
		AttackStatus offHandStatus = AttackStatus.getOffHandStats(mainHandStatus);
		Item offHandWeapon = ((Player) attacker).getEquipment().getOffHandWeapon();
		int offHandDamage = StatFunctions.calculateBasePhysicalDamage(attacker, attacked, false);
		int offHandHits = Rnd.get(1, offHandWeapon.getItemTemplate().getWeaponStats().getHitCount());
		splitPhysicalDamage(attacker, attacked, offHandHits, offHandDamage, offHandStatus, attackList);
	}

	/**
	 * Generate attack results based on weapon hit count
	 */
	private static final List<AttackResult> splitPhysicalDamage(final Creature attacker, final Creature attacked,
		int hitCount, int damage, AttackStatus status, List<AttackResult> attackList) {
		WeaponType weaponType;

		switch (AttackStatus.getBaseStatus(status)) {
			case BLOCK:
				int reduce = damage-attacked.getGameStats().getPositiveReverseStat(StatEnum.DAMAGE_REDUCE, damage);
				if (attacked instanceof Player){
					Item shield = ((Player)attacked).getEquipment().getEquippedShield();
					if (shield != null){
						int reduceMax = shield.getItemTemplate().getWeaponStats().getReduceMax();
						if (reduceMax > 0 && reduceMax < reduce)
							reduce = reduceMax;
					}
				}
				damage -= reduce;
				break;
			case DODGE:
				damage = 0;
				break;
			case PARRY:
				damage *= 0.6;
				break;
			default:
				break;
		}

		if (status.isCritical()) {
			if (attacker instanceof Player) {
				weaponType = ((Player) attacker).getEquipment().getMainHandWeaponType();
				damage = (int) calculateWeaponCritical(damage, weaponType);
				// Proc Stumble/Stagger on Crit calculation
				applyEffectOnCritical((Player) attacker, attacked);
			}
			else
				damage *= 2;
			damage = calculateDamageByReduction(attacked, damage, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
		}

		if (damage < 1)
			damage = 0;
		if (attacked instanceof Npc)
			damage = attacked.getAi2().modifyDamage(damage);

		int firstHit = (int) (damage * (1f - (0.1f * (hitCount - 1))));
		int otherHits = Math.round(damage * 0.1f);
		for (int i = 0; i < hitCount; i++) {
			int dmg = (i == 0 ? firstHit : otherHits);
			attackList.add(new AttackResult(dmg, status, HitType.PHHIT));
		}
		return attackList;
	}

	/**
	 * [Critical] Spear : x1.5 Sword : x2.5 Dagger : x2.3 Mace : x2.0 Greatsword : x1.5 Orb : x2.0 Spellbook : x2.0 Bow :
	 * x1.4 Staff : x1.5
	 * 
	 * @param damages
	 * @param weaponType
	 * @return
	 */
	private static float calculateWeaponCritical(float damages, WeaponType weaponType) {
		// critical without weapon
		if (weaponType == null) {
			return Math.round(damages * 1.5f);
		}

		switch (weaponType) {
			case DAGGER_1H:
				damages = Math.round(damages * 2.3f);
				break;
			case SWORD_1H:
				damages = Math.round(damages * 2.2f);
				break;
			case MACE_1H:
				damages *= 2;
				break;
			case SWORD_2H:
			case POLEARM_2H:
				damages = Math.round(damages * 1.8f);
				break;
			case STAFF_2H:
			case BOW:
				damages = Math.round(damages * 1.7f);
				break;
			default:
				damages = Math.round(damages * 1.5f);
				break;
		}
		return damages;
	}

	/**
	 * @param effect
	 * @param skillDamage
	 */
	public static void calculatePhysicalSkillResult(Effect effect, int skillDamage, int bonus, Func func,
		int randomDamage, int accMod) {
		calculatePhysicalSkillResult(effect, skillDamage, bonus, func, randomDamage, accMod, 100, 0, false, false, false);
	}
		
	/**
	 * @param effect
	 * @param skillDamage
	 * @param bonus (damage from modifiers)
	 * @param func (add/percent)
	 * @param randomDamage
	 * @param accMod
	 */
	public static void calculatePhysicalSkillResult(Effect effect, int skillDamage, int bonus, Func func,
			int randomDamage, int accMod, int criticalProb, int critAddDmg, boolean cannotMiss, boolean shared, boolean ignoreShield) {
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();

		int damage = StatFunctions.calculatePhysicalAttackDamage(effector, effected, func, skillDamage, bonus, true, effect.getSkillTemplate().getPvpDamage());

		float damageMultiplier = effector.getObserveController().getBasePhysicalDamageMultiplier(true);
		damage = Math.round(damage * damageMultiplier);

		// implementation of random damage for skills like Stunning Shot, etc
		if (randomDamage > 0) {
			int randomChance = Rnd.get(100);

			switch (randomDamage) {
				case 1:
					if (randomChance <= 40)
						damage /= 2;
					else if (randomChance <= 70)
						damage *= 1.5;
					break;
				case 2:
					if (randomChance <= 25)
						damage *= 3;
					break;
				case 6:
					if (randomChance <= 30)
						damage *= 2;
					break;
				// TODO rest of the cases
				default:
					/*
					 * chance to do from 50% to 200% damage This must NOT be calculated after critical status check, or it will be
					 * over powered and not retail
					 */
					damage *= (Rnd.get(25, 100) * 0.02f);
					break;
			}
		}

		AttackStatus status = calculateAttackerPhysicalStatus(effector);
		if (status == null) {
			status = calculatePhysicalStatus(effector, effected, true, accMod, true, criticalProb, cannotMiss);
		}
		
		//TODO revisit
		if (!status.isCritical() && effector.isOneTimeBoostSkillCritical()) {
			status = AttackStatus.getCriticalStatusFor(status);
		}
	
		
		switch (AttackStatus.getBaseStatus(status)) {
			case BLOCK:
				int reduce = damage-effected.getGameStats().getPositiveReverseStat(StatEnum.DAMAGE_REDUCE, damage);
				if (effected instanceof Player){
					Item shield = ((Player)effected).getEquipment().getEquippedShield();
					if (shield != null){
						int reduceMax = shield.getItemTemplate().getWeaponStats().getReduceMax();
						if (reduceMax > 0 && reduceMax < reduce)
							reduce = reduceMax;
					}
				}
				damage -= reduce;
				break;
			case PARRY:
				damage *= 0.6;
				break;
			default:
				break;
		}

		if (status.isCritical()) {
			if (effector instanceof Player) {
				WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
				damage = (int) calculateWeaponCritical(damage, weaponType);
				// Set effect as critical damage effect
				effect.setCriticalRatio(calculateWeaponCritical(1, weaponType));
				// Proc Stumble/Stagger on Crit calculation if grab no KD
				if(effect.getSkillId() != 523 && effect.getSkillId() != 525 && effect.getSkillId() != 2052){
					applyEffectOnCritical((Player) effector, effected);
				}
			}
			else {
				damage *= 2;
				effect.setCriticalRatio(2f);
			}
			damage = calculateDamageByReduction(effected, damage, StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE);
			// Proc Stumble/Stagger on Crit calculation
		}
		
		if (effected instanceof Npc)
			damage = effected.getAi2().modifyDamage(damage);

		if (damage < 0)
			damage = 0;
		
		calculateEffectResult(effect, effected, damage, status, HitType.PHHIT);
	}

	/**
	 * If attacker is blinded - return DODGE for physical attacks
	 * 
	 * @param effector
	 * @return
	 */
	private static AttackStatus calculateAttackerPhysicalStatus(Creature effector) {
		if (effector.getObserveController().checkAttackerStatus(AttackStatus.DODGE))
			return AttackStatus.DODGE;
		return null;
	}

	/**
	 * @param effect
	 * @param effected
	 * @param damage
	 * @param status
	 * @param hitType
	 */
	private static void calculateEffectResult(Effect effect, Creature effected, int damage, AttackStatus status,
		HitType hitType) {
		AttackResult attackResult = new AttackResult(damage, status, hitType);
		effected.getObserveController().checkShieldStatus(Collections.singletonList(attackResult), effect.getEffector());
		effect.setReserved1(attackResult.getDamage());
		effect.setAttackStatus(attackResult.getAttackStatus());
		effect.setLaunchSubEffect(attackResult.isLaunchSubEffect());
		effect.setReflectedDamage(attackResult.getReflectedDamage());
		effect.setReflectedSkillId(attackResult.getReflectedSkillId());
		effect.setProtectedDamage(attackResult.getProtectedDamage());
		effect.setProtectedSkillId(attackResult.getProtectedSkillId());
		effect.setProtectorId(attackResult.getProtectorId());
		effect.setShieldDefense(attackResult.getShieldType());
	}

	public static List<AttackResult> calculateMagicalAttackResult(Creature attacker, Creature attacked, SkillElement elem) {
		int damage = StatFunctions.calculateMagicalAttackDamage(attacker, attacked, elem);
		
		AttackStatus status = calculateMagicalStatus(attacker, attacked, false);
		List<AttackResult> attackList = new ArrayList<AttackResult>();
		switch (status) {
			case RESIST:
				damage = 0;
				break;
			case CRITICAL:
				if (attacker instanceof Player)
					damage = (int) calculateWeaponCritical(damage, ((Player) attacker).getEquipment().getMainHandWeaponType());
				else
					damage *= 2;
				damage = calculateDamageByReduction(attacked, damage, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				break;
		}
		attackList.add(new AttackResult(damage, status));
		attacked.getObserveController().checkShieldStatus(attackList, attacker);
		return attackList;

	}

	
	public static int calculateMagicalOverTimeSkillResult(Effect effect, int skillDamage, SkillElement element, int position) {
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();
		
		//TODO is damage multiplier used on dot?
		float damageMultiplier = effector.getObserveController().getBaseMagicalDamageMultiplier();
		
		int	damage = Math.round(StatFunctions.calculateMagicalSkillDamage(effect.getEffector(), effect.getEffected(), skillDamage,
			0, element, false, false, effect.getSkillTemplate().getPvpDamage())	* damageMultiplier);
		
		AttackStatus status = effect.getAttackStatus();
		// calculate attack status only if it has not been forced already
		if (status == AttackStatus.NORMALHIT && position == 1)
			status = calculateMagicalStatus(effector, effected, true);
		switch (status) {
			case CRITICAL:
				if (effector instanceof Player) {
					WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
					damage = (int) calculateWeaponCritical(damage, weaponType);
					// Set effect as critical damage effect
					effect.setCriticalRatio(calculateWeaponCritical(1, weaponType));
				}
				else {
					damage *= 2;
					effect.setCriticalRatio(2f);
				}
				damage = calculateDamageByReduction(effected, damage, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				break;
			default:
				break;
		}
		
		if (damage <= 0)
			damage = 1;
		
		return damage;
	}
	/**
	 * @param effect
	 * @param skillDamage
	 * @param element
	 * @param isNoReduceSpell
	 */
	public static void calculateMagicalSkillResult(Effect effect, int skillDamage, int bonus, SkillElement element) {
		calculateMagicalSkillResult(effect, skillDamage, bonus, element,
			true, false);
	}
	
	public static void calculateMagicalSkillResult(Effect effect, int skillDamage, int bonus, SkillElement element,
		boolean useKnowledge, boolean noReduce) {
		Creature effector = effect.getEffector();
		Creature effected = effect.getEffected();

		float damageMultiplier = effector.getObserveController().getBaseMagicalDamageMultiplier();
		
		int	damage = Math.round(StatFunctions.calculateMagicalSkillDamage(effect.getEffector(), effect.getEffected(), skillDamage,
			bonus, element, useKnowledge, noReduce, effect.getSkillTemplate().getPvpDamage())	* damageMultiplier);


		AttackStatus status = calculateMagicalStatus(effector, effected, true);
		switch (status) {
			case CRITICAL:
				if (effector instanceof Player) {
					WeaponType weaponType = ((Player) effector).getEquipment().getMainHandWeaponType();
					damage = (int) calculateWeaponCritical(damage, weaponType);
					// Set effect as critical damage effect
					effect.setCriticalRatio(calculateWeaponCritical(1, weaponType));
				}
				else {
					damage *= 2;
					effect.setCriticalRatio(2f);
				}
				damage = calculateDamageByReduction(effected, damage, StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE);
				break;
			default:
				break;
		}

		calculateEffectResult(effect, effected, damage, status, HitType.MAHIT);
	}

	/**
	 * Manage attack status rate
	 * 
	 * @source 
	 *         http://www.aionsource.com/forum/mechanic-analysis/42597-character-stats-xp-dp-origin-gerbator-team-july-2009
	 *         -a.html
	 * @return AttackStatus
	 */
	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked, boolean isMainHand) {
		return calculatePhysicalStatus(attacker, attacked, isMainHand, 0, false);
	}

	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked, boolean isMainHand,
		int accMod, boolean isSkill) {
		return calculatePhysicalStatus(attacker, attacked, isMainHand, accMod, isSkill, 100, false);
	}
	
	public static AttackStatus calculatePhysicalStatus(Creature attacker, Creature attacked, boolean isMainHand,
				int accMod, boolean isSkill, int criticalProb, boolean cannotMiss) {
		AttackStatus status = AttackStatus.NORMALHIT;
		if (!isMainHand)
			status = AttackStatus.OFFHAND_NORMALHIT;
		
		
		if (attacked instanceof Player && ((Player) attacked).getEquipment().isShieldEquipped()
			&& StatFunctions.calculatePhysicalBlockRate(attacker, attacked))//TODO accMod
			status = AttackStatus.BLOCK;
		// Parry can only be done with weapon, also weapon can have humanoid mobs,
		// but for now there isnt implementation of monster category
		else if (attacked instanceof Player && ((Player) attacked).getEquipment().getMainHandWeaponType() != null
			&& StatFunctions.calculatePhysicalParryRate(attacker, attacked))//TODO accMod
			status = AttackStatus.PARRY;
		else if (!isSkill) {
			if (StatFunctions.calculatePhysicalDodgeRate(attacker, attacked, accMod))
				status = AttackStatus.DODGE;
		}

		if (StatFunctions.calculatePhysicalCriticalRate(attacker, attacked, isMainHand, criticalProb, isSkill)) {
			switch (status) {
			case BLOCK:
				if (isMainHand)
					status = AttackStatus.CRITICAL_BLOCK;
				else
					status = AttackStatus.OFFHAND_CRITICAL_BLOCK;
				break;
			case PARRY:
				if (isMainHand)
					status = AttackStatus.CRITICAL_PARRY;
				else
					status = AttackStatus.OFFHAND_CRITICAL_PARRY;
				break;
			case DODGE:
				if (isMainHand)
					status = AttackStatus.CRITICAL_DODGE;
				else
					status = AttackStatus.OFFHAND_CRITICAL_DODGE;
				break;
			default:
				if (isMainHand)
					status = AttackStatus.CRITICAL;
				else
					status = AttackStatus.OFFHAND_CRITICAL;
				break;
			}
		}

		return status;
	}

	/**
	 * Every + 100 delta of (MR - MA) = + 10% to resist<br>
	 * if the difference is 1000 = 100% resist
	 */
	public static AttackStatus calculateMagicalStatus(Creature attacker, Creature attacked, boolean isSkill) {
		if (!isSkill) {
			if (Rnd.get(0, 1000) < StatFunctions.calculateMagicalResistRate(attacker, attacked, 0))
				return AttackStatus.RESIST;
		}

		if (StatFunctions.calculateMagicalCriticalRate(attacker, attacked)) {
			return AttackStatus.CRITICAL;
		}

		return AttackStatus.NORMALHIT;
	}

	/**
	 * Send a packet to everyone who is targeting creature.
	 * 
	 * @param creature
	 */
	public static void deselectTargettingMe(Creature creature) {
		for (VisibleObject obj : creature.getKnownList().getKnownPlayers().values()) {
			Player enemy = (Player) obj;
			if (enemy.getTarget() == creature) {
				// Cancel current cast if creature is the target of the casting skill
				if (enemy.getCastingSkill() != null) {
					if (enemy.getCastingSkill().getFirstTarget().getObjectId() == creature.getObjectId())
						enemy.getController().cancelCurrentSkill();
				}
				enemy.setTarget(null);
				PacketSendUtility.sendPacket(enemy, new SM_TARGET_SELECTED(enemy));
			}
		}
	}

	/**
	 * Critical damage get reduced if the target has certain reduction stat
	 */
	public static int calculateDamageByReduction(Creature attacked, int damage, StatEnum stat) {
		// TODO getPositiveReverseStat instead?
		if (attacked instanceof Player) {
			Player player = (Player) attacked;
			switch (stat) {
				case PHYSICAL_CRITICAL_DAMAGE_REDUCE:
				case MAGICAL_CRITICAL_DAMAGE_REDUCE:
					int fortitude = player.getGameStats().getStat(stat, 0).getCurrent();
					damage -= Math.round((fortitude / 1000f) * damage);
			}
		}
		return damage;
	}

	public static void applyEffectOnCritical(Player attacker, Creature attacked) {
		int skillId = 0;
		WeaponType mainHandWeaponType = attacker.getEquipment().getMainHandWeaponType();
		if(mainHandWeaponType != null){
			switch (mainHandWeaponType) {
				case POLEARM_2H:
				case STAFF_2H:
				case SWORD_2H:
					skillId = 8218;
					break;
				case BOW:
					skillId = 8217;
			}
		}
		
		if (skillId == 0)
			return;
		// On retail this effect apply on each crit with 10% of base chance
		// plus bonus effect penetration calculated above
		if (Rnd.get(100) > 10)
			return;

		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null)
			return;
		Effect e = new Effect(attacker, attacked, template, template.getLvl(), template.getEffectsDuration());
		e.initialize();
		e.applyEffect();
	}
}
