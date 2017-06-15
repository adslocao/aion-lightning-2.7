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
package com.aionemu.gameserver.controllers.effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABNORMAL_EFFECT;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.EffectType;
import com.aionemu.gameserver.skillengine.effect.OpenAerialEffect;
import com.aionemu.gameserver.skillengine.effect.ParalyzeEffect;
import com.aionemu.gameserver.skillengine.effect.PulledEffect;
import com.aionemu.gameserver.skillengine.effect.SpinEffect;
import com.aionemu.gameserver.skillengine.effect.StaggerEffect;
import com.aionemu.gameserver.skillengine.effect.StumbleEffect;
import com.aionemu.gameserver.skillengine.effect.StunEffect;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author ATracer modified by Wakizashi, Sippolo
 */
public class EffectController {
	public static Logger log = LoggerFactory.getLogger(EffectController.class);
	private Creature owner;

	protected Map<String, Effect> passiveEffectMap = new FastMap<String, Effect>().shared();
	protected Map<String, Effect> noshowEffects = new FastMap<String, Effect>().shared();
	protected Map<String, Effect> abnormalEffectMap = new FastMap<String, Effect>().shared();

	private final Lock lock = new ReentrantLock();

	protected int abnormals;

	public EffectController(Creature owner) {
		this.owner = owner;
	}

	/**
	 * @return the owner
	 */
	public Creature getOwner() {
		return owner;
	}

	/**
	 * @param effect
	 */
	public void addEffect(Effect effect) {
		Map<String, Effect> mapToUpdate = getMapForEffect(effect);

		lock.lock();
		try {
			
			boolean useEffectId = true;
			Effect existingEffect = mapToUpdate.get(effect.getStack());
			if (existingEffect != null) {
				// check stack level
				if (existingEffect.getSkillStackLvl() > effect.getSkillStackLvl()){
					return;
				}
				
				// check skill level (when stack level same)
				if (existingEffect.getSkillStackLvl() == effect.getSkillStackLvl()
					&& existingEffect.getSkillLevel() > effect.getSkillLevel()){
					return;
				}
				existingEffect.endEffect();
				useEffectId = false;
			}
			
			Effect conflictedEffect = findConflictedEffect(mapToUpdate, effect);
			if (conflictedEffect != null) {
				conflictedEffect.endEffect();
				useEffectId = false;
			}
			
			if (useEffectId) {
				/**
				 * idea here is that effects with same effectId shouldnt stack
				 * effect with higher basiclvl takes priority
				 */ 
				Iterator<Effect> iter2 = mapToUpdate.values().iterator();
				while (iter2.hasNext())	{
					Effect ef = iter2.next();
					if (ef.getTargetSlot() == effect.getTargetSlot())	{
						for (EffectTemplate et : ef.getEffectTemplates())	{
							if (et.getEffectid() == 0)
								continue;
							for (EffectTemplate et2 : effect.getEffectTemplates())	{
								if (et2.getEffectid() == 0)
									continue;
								if (et.getEffectid() == et2.getEffectid()) {
									if (et.getBasicLvl() > et2.getBasicLvl())
										return;
									else
										ef.endEffect();
								}
							}
						}
					}
				}
			}
			
			//TODO Gestion MANTRA
			if (effect.isToggle() && mapToUpdate.size() >= 3) {
				Iterator<Effect> iter = mapToUpdate.values().iterator();
				Effect nextEffect = iter.next();
				nextEffect.endEffect();
				iter.remove();
			}

			mapToUpdate.put(effect.getStack(), effect);
		}
		finally {
			lock.unlock();
		}

		// ? move into lock area
		effect.startEffect(false);

		if (!effect.isPassive()) {
			broadCastEffects();
		}
	}

	/**
	 * @param mapToUpdate
	 * @param effect
	 * @return
	 */
	private final Effect findConflictedEffect(Map<String, Effect> mapToUpdate, Effect newEffect) {
		int conflictId = newEffect.getSkillTemplate().getConflictId();
		if(conflictId == 0){
			return null;
		}
		for(Effect effect : mapToUpdate.values()){
			if(effect.getSkillTemplate().getConflictId() == conflictId){
				return effect;
			}
		}
		return null;
	}

	/**
	 * @param effect
	 * @return
	 */
	private Map<String, Effect> getMapForEffect(Effect effect) {
		if (effect.isPassive())
			return passiveEffectMap;

		if (effect.isToggle())
			return noshowEffects;

		return abnormalEffectMap;
	}

	/**
	 * @param stack
	 * @return abnormalEffectMap
	 */
	public Effect getAnormalEffect(String stack) {
		return abnormalEffectMap.get(stack);
	}

	/**
	 * @param skillId
	 * @return
	 */
	public boolean hasAbnormalEffect(int skillId) {
		Iterator<Effect> localIterator = this.abnormalEffectMap.values().iterator();
		while (localIterator.hasNext()) {
			Effect localEffect = localIterator.next();
			if (localEffect.getSkillId() == skillId)
				return true;
		}
		return false;
	}

	
	public void broadCastEffects() {
		owner.addPacketBroadcastMask(BroadcastMode.BROAD_CAST_EFFECTS);
	}

	/**
	 * Broadcasts current effects to all visible objects
	 */
	public void broadCastEffectsImp() {
		List<Effect> effects = getAbnormalEffects();
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ABNORMAL_EFFECT(getOwner(), abnormals, effects));
	}

	/**
	 * Used when player see new player
	 * 
	 * @param player
	 */
	public void sendEffectIconsTo(Player player) {
		List<Effect> effects = getAbnormalEffects();
		PacketSendUtility.sendPacket(player, new SM_ABNORMAL_EFFECT(getOwner(), abnormals, effects));
	}

	/**
	 * @param effect
	 */
	public void clearEffect(Effect effect) {
		Map<String, Effect> mapForEffect = getMapForEffect(effect);
		mapForEffect.remove(effect.getStack());
		broadCastEffects();
	}

	/**
	 * Removes the effect by skillid.
	 * 
	 * @param skillid
	 */
	public void removeEffect(int skillid) {
		for (Effect effect : abnormalEffectMap.values()) {
			if (effect.getSkillId() == skillid) {
				effect.endEffect();
				abnormalEffectMap.remove(effect.getStack());
			}
		}

		for (Effect effect : passiveEffectMap.values()) {
			if (effect.getSkillId() == skillid) {
				effect.endEffect();
				passiveEffectMap.remove(effect.getStack());
			}
		}

		for (Effect effect : noshowEffects.values()) {
			if (effect.getSkillId() == skillid) {
				effect.endEffect();
				noshowEffects.remove(effect.getStack());
			}
		}
	}

	/**
	 * Removes the effect by SkillSetException Number.
	 * 
	 * @param SkillSetException
	 *          Number
	 * @param maxOccur
	 *          Number
	 */
	public void removeEffectBySetNumber(final int setNumber, int maxOccur) {
		removeEffectBySetNumber(setNumber, maxOccur, 0);
	}

	/**
	 * Removes the effect by SkillSetException Number.
	 * 
	 * @param SkillSetException
	 *          Number
	 * @param maxOccur
	 *          Number
	 * @param skillId
	 *          of the effect that is being applied
	 */
	public void removeEffectBySetNumber(final int setNumber, int maxOccur, int skillId) {
		int i = 0;
		// Count the occurences of effects of the setNumber.
		for (Effect effect : abnormalEffectMap.values()) {
			if ((effect.getSkillSetException() == setNumber) && (effect.getSkillId() != skillId))
				i++;
		}
		// if there are too much occurences of effects of the setNumber then remove the oldest effect.
		if (maxOccur <= i) {
			for (Effect effect : abnormalEffectMap.values()) {
				if (effect.getSkillSetException() == setNumber) {
					effect.endEffect();
					abnormalEffectMap.remove(effect.getStack());
					break;
				}
			}
		}

		i = 0;
		for (Effect effect : passiveEffectMap.values()) {
			if (effect.getSkillSetException() == setNumber)
				i++;
		}
		if (maxOccur <= i) {
			for (Effect effect : passiveEffectMap.values()) {
				if (effect.getSkillSetException() == setNumber) {
					effect.endEffect();
					passiveEffectMap.remove(effect.getStack());
					break;
				}
			}
		}

		i = 0;
		for (Effect effect : noshowEffects.values()) {
			if (effect.getSkillSetException() == setNumber)
				i++;
		}
		if (maxOccur <= i) {
			for (Effect effect : noshowEffects.values()) {
				if (effect.getSkillSetException() == setNumber) {
					effect.endEffect();
					noshowEffects.remove(effect.getStack());
					break;
				}
			}
		}
	}

	/**
	 * Removes the effect with SkillSetException Reserved Number (aka 1).
	 */
	public void removeEffectWithSetNumberReserved() {
		removeEffectBySetNumber(1, 1);
	}

	/**
	 * @param effectId
	 */
	public void removeEffectByEffectId(int effectId) {
		for (Effect effect : abnormalEffectMap.values()) {
			if (effect.containsEffectId(effectId)) {
				effect.endEffect();
				abnormalEffectMap.remove(effect.getStack());
			}
		}
	}

	/**
	 * Method used to calculate number of effects of given dispelcategory, targetslot and dispelLevel
	 * used only in DispelBuffCounterAtk, therefore rest of cases are skipped
	 * @param dispelCat
	 * @param targetSlot
	 * @param dispelLevel
	 * @return
	 */
	public int calculateNumberOfEffects(DispelCategoryType dispelCat, SkillTargetSlot targetSlot, int dispelLevel) {
		int number = 0;
		
		for (Effect effect : abnormalEffectMap.values()) {
			//effects with duration 86400000 cant be dispelled
			//TODO recheck
			if (effect.getDuration() >= 86400000)
				continue;
			
			//check for targetslot, effects with target slot higher or equal to 2 cant be removed (ex. skillId: 11885)
			if (effect.getTargetSlot() != targetSlot.ordinal() || effect.getTargetSlotLevel() >= 2)
				continue;

			switch (dispelCat) {
				case BUFF://DispelBuffCounterAtkEffect
					if (effect.getDispelCategory() == DispelCategoryType.BUFF
						&& effect.getReqDispelLevel() <= dispelLevel)
						number++;
					break;
			}
		}
					
		return number;
	}
	
	public void removeEffectByDispelCat(DispelCategoryType dispelCat, SkillTargetSlot targetSlot, int count, int dispelLevel, int power, boolean itemTriggered) {
		for (Effect effect : abnormalEffectMap.values()) {
			if (count == 0)
				break;
			//effects with duration 86400000 cant be dispelled
			//TODO recheck
			if (effect.getDuration() >= 86400000)
				continue;

			// If dispel is triggered by an item (ex. Healing Potion)
			// and debuff is unpottable, do not dispel
			if ((effect.getSkillTemplate().isUndispellableByPotions()) && itemTriggered)
					continue;
			
			//check for targetslot, effects with target slot level higher or equal to 2 cant be removed (ex. skillId: 11885)
			if (effect.getTargetSlot() != targetSlot.ordinal() || effect.getTargetSlotLevel() >= 2)
				continue;

			boolean remove = false;
			switch (dispelCat) {
				case ALL://DispelDebuffEffect
					if ((effect.getDispelCategory() == DispelCategoryType.ALL
						|| effect.getDispelCategory() == DispelCategoryType.DEBUFF_MENTAL
						|| effect.getDispelCategory() == DispelCategoryType.DEBUFF_PHYSICAL)
						&& effect.getReqDispelLevel() <= dispelLevel)
						remove = true;
					break;
				case DEBUFF_MENTAL://DispelDebuffMentalEffect
					if ((effect.getDispelCategory() == DispelCategoryType.ALL
						|| effect.getDispelCategory() == DispelCategoryType.DEBUFF_MENTAL)
						&& effect.getReqDispelLevel() <= dispelLevel)
						remove = true;
					break;
				case DEBUFF_PHYSICAL://DispelDebuffPhysicalEffect
					if ((effect.getDispelCategory() == DispelCategoryType.ALL
						|| effect.getDispelCategory() == DispelCategoryType.DEBUFF_PHYSICAL)
						&& effect.getReqDispelLevel() <= dispelLevel)
						remove = true;
					break;
				case BUFF://DispelBuffEffect or DispelBuffCounterAtkEffect
					if (effect.getDispelCategory() == DispelCategoryType.BUFF
						&& effect.getReqDispelLevel() <= dispelLevel)
						remove = true;
					break;
				case STUN:
					if (effect.getDispelCategory() == DispelCategoryType.STUN)
						remove = true;
					break;
				case NPC_BUFF://DispelNpcBuff
					if (effect.getDispelCategory() == DispelCategoryType.NPC_BUFF)
						remove = true;
					break;
				case NPC_DEBUFF_PHYSICAL://DispelNpcDebuff
					if (effect.getDispelCategory() == DispelCategoryType.NPC_DEBUFF_PHYSICAL)
						remove = true;
					break;
			}

			if (remove) {
				if (removePower(effect, power)) {
					effect.endEffect();
					abnormalEffectMap.remove(effect.getStack());
				}
				count--;
			}
		}
	}
	
	public void removeEffectByEffectType(EffectType effectType) {
		for (Effect effect : abnormalEffectMap.values()) {
			for (EffectTemplate et : effect.getSuccessEffect()) {
				if (effectType == et.getEffectType())
					effect.endEffect();
			}
		}
	}
	
	private boolean removePower(Effect effect, int power) {
		int effectPower = effect.removePower(power);
		
		if (effectPower <= 0)
			return true;
		else
			return false;
	}

	/**
	 * Removes the effect by skillid.
	 * 
	 * @param skillid
	 */
	public void removePassiveEffect(int skillid) {
		for (Effect effect : passiveEffectMap.values()) {
			if (effect.getSkillId() == skillid) {
				effect.endEffect();
				passiveEffectMap.remove(effect.getStack());
			}
		}
	}

	/**
	 * @param skillid
	 */
	public void removeNoshowEffect(int skillid) {
		for (Effect effect : noshowEffects.values()) {
			if (effect.getSkillId() == skillid) {
				effect.endEffect();
				noshowEffects.remove(effect.getStack());
			}
		}
	}

	/**
	 * @see TargetSlot
	 * @param targetSlot
	 */
	public void removeAbnormalEffectsByTargetSlot(SkillTargetSlot targetSlot) {
		for (Effect effect : abnormalEffectMap.values()) {
			if (effect.getTargetSlot() == targetSlot.ordinal()) {
				effect.endEffect();
				abnormalEffectMap.remove(effect.getStack());
			}
		}
	}

	/**
	 * Removes all effects from controllers and ends them appropriately Passive effect will not be removed
	 */
	public void removeAllEffects() {
		this.removeAllEffects(false);
	}

	public void removeAllEffects(boolean logout) {
		if (!logout) {
			Iterator<Map.Entry<String, Effect>> it = abnormalEffectMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Effect> entry = it.next();
				//TODO recheck - kecimis
				if (!entry.getValue().getSkillTemplate().isNoRemoveAtDie() && !entry.getValue().isXpBoost()) {
					entry.getValue().endEffect();
					it.remove();
				}
			}

			for (Effect effect : noshowEffects.values()) {
				effect.endEffect();
			}
			noshowEffects.clear();
		}	else {
			//remove all effects on logout
			for (Effect effect : abnormalEffectMap.values()) {
				effect.endEffect();
			}
			abnormalEffectMap.clear();
			for (Effect effect : noshowEffects.values()) {
				effect.endEffect();
			}
			noshowEffects.clear();
			for (Effect effect : passiveEffectMap.values()) {
				effect.endEffect();
			}
			passiveEffectMap.clear();
		}
	}

	/**
	 * Return true if skillId is present among creature's abnormals
	 */
	public boolean isAbnormalPresentBySkillId(int skillId) {
		for (Effect effect : abnormalEffectMap.values()) {
			if (effect.getSkillId() == skillId)
				return true;
		}
		return false;
	}

	public boolean isNoshowPresentBySkillId(int skillId) {
		for (Effect effect : noshowEffects.values()) {
			if (effect.getSkillId() == skillId)
				return true;
		}
		return false;
	}

	public boolean isPassivePresentBySkillId(int skillId) {
		for (Effect effect : passiveEffectMap.values()) {
			if (effect.getSkillId() == skillId)
				return true;
		}
		return false;
	}
	
	public boolean isPresentBySkillId(int skillId){
		if(isPassivePresentBySkillId(skillId)){
			return true;
		}
		if(isNoshowPresentBySkillId(skillId)){
			return true;
		}
		if(isAbnormalPresentBySkillId(skillId)){
			return true;
		}
		return false;
	}

	/**
	 * return true if creature is under Fear effect
	 */
	public boolean isUnderFear() {
		return isAbnormalSet(AbnormalState.FEAR);
	}

	public void updatePlayerEffectIcons() {
	}

	public void updatePlayerEffectIconsImpl() {
	}

	/**
	 * @return copy of anbornals list
	 */
	public List<Effect> getAbnormalEffects() {
		List<Effect> effects = new ArrayList<Effect>();
		Iterator<Effect> iterator = iterator();
		while (iterator.hasNext()) {
			Effect effect = iterator.next();
			if (effect != null)
				effects.add(effect);
		}
		return effects;
	}

	/**
	 * @return list of effects to display as top icons
	 */
	public Collection<Effect> getAbnormalEffectsToShow() {
		return Collections2.filter(abnormalEffectMap.values(), new Predicate<Effect>() {

			@Override
			public boolean apply(Effect effect) {
				return effect.getSkillTemplate().getTargetSlot() != SkillTargetSlot.NOSHOW;
			}
		});
	}
	
	public Collection<Effect> getAbnormalEffectsWithoutPassive() {
		return Collections2.filter(abnormalEffectMap.values(), new Predicate<Effect>() {

			@Override
			public boolean apply(Effect effect) {
				return !effect.isPassive();
			}
		});
	}
	
	public Collection<Effect> getChantEffects() {
		return Collections2.filter(abnormalEffectMap.values(), new Predicate<Effect>() {

			@Override
			public boolean apply(Effect effect) {
				return effect.isChant();
			}
		});
	}

	/**
	 * ABNORMAL EFFECTS
	 */

	public void setAbnormal(int mask) {
		if ((owner instanceof Player) && ((mask & AbnormalState.CANT_MOVE_STATE.getId()) > 0)
			&& (!((Player) owner).isInvulnerableWing())) {
			Player player = (Player) owner;
			player.getFlyController().onStopGliding(true);
		}
		abnormals |= mask;
	}

	public void unsetAbnormal(int mask) {
		int count = 0;
		for (Effect effect : abnormalEffectMap.values()) {
			if ((effect.getAbnormals() & mask) == mask)
				count++;
		}
		if (count <= 1)
			abnormals &= ~mask;
	}

	/**
	 * Used for checking unique abnormal states
	 * 
	 * @param id
	 * @return
	 */
	public boolean isAbnormalSet(AbnormalState id) {
		return (abnormals & id.getId()) == id.getId();
	}

	/**
	 * Used for compound abnormal state checks
	 * 
	 * @param id
	 * @return
	 */
	public boolean isAbnormalState(AbnormalState id) {
		int state = abnormals & id.getId();
		return state > 0 && state <= id.getId();
	}

	public int getAbnormals() {
		return abnormals;
	}

	/**
	 * @return
	 */
	public Iterator<Effect> iterator() {
		return abnormalEffectMap.values().iterator();
	}

	public boolean checkAvatar() {
		for (Effect eff : getAbnormalEffects()) {
			if (eff.isAvatar())
				return true;
		}
		return false;
	}
	
	public boolean isEmpty(){
		return abnormalEffectMap.isEmpty();
	}
	
	/*******************************************
	 * 
	 * 
	 * 
	 *******************************************/
	

	/**
	 * @param skillId
	 * @return
	 */
	public boolean hasPhycisalAbnormalEffect() {
		Iterator<Effect> localIterator = this.abnormalEffectMap.values().iterator();
		while (localIterator.hasNext()) {
			Effect localEffect = localIterator.next();
			if(localEffect == null)
				continue;
			AbnormalState abs = AbnormalState.getAbnormalStateById(localEffect.getAbnormals());
			if(abs != null && abs.isPhysical())
				return true;
		}
		return false;
	}
	
	public void stopMagicalEffect(){
		Iterator<Effect> localIterator = this.abnormalEffectMap.values().iterator();
		while (localIterator.hasNext()) {
			Effect localEffect = localIterator.next();
			if(localEffect == null)
				continue;
			AbnormalState abs = AbnormalState.getAbnormalStateById(localEffect.getAbnormals());
			if(abs != null && abs.isMagical())
				localEffect.endEffect();
		}
	}
	public boolean applyPhysicalAbnormalEffect(){
		if(hasPhycisalAbnormalEffect())
			return false;
		stopMagicalEffect();	
		return true;
	}
	
	public boolean isUnderPhysicalChocEffect(){
		return (isAbnormalSet(AbnormalState.STUMBLE) || isAbnormalSet(AbnormalState.CANNOT_MOVE) || isAbnormalSet(AbnormalState.SPIN) || isAbnormalSet(AbnormalState.OPENAERIAL) || isAbnormalSet(AbnormalState.STAGGER));
	}

	public boolean isUnderSameChocEffect(EffectTemplate effectTemplate){
		if(isAbnormalSet(AbnormalState.STUMBLE) && effectTemplate instanceof StumbleEffect){
			return true;
		}
		if(isAbnormalSet(AbnormalState.SPIN) && effectTemplate instanceof SpinEffect){
			return true;
		}
		if(isAbnormalSet(AbnormalState.OPENAERIAL) && effectTemplate instanceof OpenAerialEffect){
			return true;
		}
		if(isAbnormalSet(AbnormalState.STAGGER) && effectTemplate instanceof StaggerEffect){
			return true;
		}
		if(isAbnormalSet(AbnormalState.PARALYZE) && effectTemplate instanceof ParalyzeEffect){
			return true;
		}
		if(isAbnormalSet(AbnormalState.STUN) && effectTemplate instanceof StunEffect){
			return true;
		}
		if(isAbnormalSet(AbnormalState.CANNOT_MOVE) && effectTemplate instanceof PulledEffect){
			return true;
		}
		return false;
	}
	
	public void removeAllMagicalChocEffect(){
		Iterator<Effect> localIterator = this.abnormalEffectMap.values().iterator();
		while (localIterator.hasNext()) {
			Effect localEffect = localIterator.next();
			if(localEffect == null)
				continue;
			AbnormalState abs = AbnormalState.getAbnormalStateById(localEffect.getAbnormals());
			if(abs == AbnormalState.STUN || abs == AbnormalState.PARALYZE)
				localEffect.endEffect();
		}
	}
}

