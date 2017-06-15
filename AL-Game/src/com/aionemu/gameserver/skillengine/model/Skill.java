/*
 Aion-Core
 */
package com.aionemu.gameserver.skillengine.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.observer.StartMovingListener;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_RESULT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.MotionLoggingService;
import com.aionemu.gameserver.services.abyss.AbyssService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.action.Action;
import com.aionemu.gameserver.skillengine.action.Actions;
import com.aionemu.gameserver.skillengine.condition.Conditions;
import com.aionemu.gameserver.skillengine.condition.TargetAttribute;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.properties.FirstTargetAttribute;
import com.aionemu.gameserver.skillengine.properties.Properties;
import com.aionemu.gameserver.skillengine.properties.TargetRangeAttribute;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer Modified by Wakzashi
 */
public class Skill {

	private SkillMethod skillMethod = SkillMethod.CAST;

	private List<Creature> effectedList;

	private Creature firstTarget;

	private Creature effector;

	private int skillLevel;

	private int skillStackLvl;

	private StartMovingListener conditionChangeListener;

	private SkillTemplate skillTemplate;

	private boolean firstTargetRangeCheck = true;

	private ItemTemplate itemTemplate;
	private int itemObjectId = 0;

	private int targetType;

	private boolean chainSuccess = true;

	private boolean isCancelled = false;
	private boolean blockedPenaltySkill = false;

	private float x;
	private float y;
	private float z;
	private byte h;

	private int boostSkillCost;

	private FirstTargetAttribute firstTargetAttribute;
	private TargetRangeAttribute targetRangeAttribute;
	private TargetAttribute targetCondition;

	/**
	 * Duration that depends on BOOST_CASTING_TIME
	 */
	private int duration;
	private int hitTime;// from CM_CASTSPELL
	private int serverTime;//time when effect is applied

	private String chainCategory = null;

	public enum SkillMethod {
		CAST,
		ITEM,
		PASSIVE,
		PROVOKED;
	}

	private Logger log = LoggerFactory.getLogger(Skill.class);

	/**
	 * Each skill is a separate object upon invocation Skill level will be populated from player SkillList
	 * 
	 * @param skillTemplate
	 * @param effector
	 * @param world
	 */
	public Skill(SkillTemplate skillTemplate, Player effector, Creature firstTarget) {
		this(skillTemplate, effector, effector.getSkillList().getSkillLevel(skillTemplate.getSkillId()), firstTarget, null);
	}

	/**
	 * @param skillTemplate
	 * @param effector
	 * @param skillLvl
	 * @param firstTarget
	 */
	public Skill(SkillTemplate skillTemplate, Creature effector, int skillLvl, Creature firstTarget,
		ItemTemplate itemTemplate) {
		this.effectedList = new ArrayList<Creature>();
		this.conditionChangeListener = new StartMovingListener();
		this.firstTarget = firstTarget;
		this.skillLevel = skillLvl;
		this.skillStackLvl = skillTemplate.getLvl();
		this.skillTemplate = skillTemplate;
		this.effector = effector;
		this.duration = skillTemplate.getDuration();
		this.itemTemplate = itemTemplate;

		if (itemTemplate != null)
			skillMethod = SkillMethod.ITEM;
		else if (skillTemplate.isPassive())
			skillMethod = SkillMethod.PASSIVE;
		else if (skillTemplate.isProvoked())
			skillMethod = SkillMethod.PROVOKED;
	}

	/**
	 * Check if the skill can be used
	 * 
	 * @return True if the skill can be used
	 */
	public boolean canUseSkill() {
		Properties properties = skillTemplate.getProperties();
		if (properties != null && !properties.validate(this)) {
			return false;
		}

		if (!preCastCheck()){
			return false;
		}

		// check for counter skill
		if (effector instanceof Player) {
			if (this.skillTemplate.getCounterSkill() != null) {
				long time = ((Player) effector).getLastCounterSkill(skillTemplate.getCounterSkill());
				if ((time + 5000) < System.currentTimeMillis()) {
					log.debug("chain skill failed, too late");
					return false;
				}
			}
		}

		effector.setCasting(this);
		Iterator<Creature> effectedIter = effectedList.iterator();
		while (effectedIter.hasNext()) {
			Creature effected = effectedIter.next();
			if (effected == null)
				effected = effector;

			if (effector instanceof Player) {
				if (!RestrictionsManager.canAffectBySkill((Player) effector, effected))
					effectedIter.remove();
			}
			else {
				if (effector.getEffectController().isAbnormalState(AbnormalState.CANT_ATTACK_STATE))
					effectedIter.remove();
			}
		}
		effector.setCasting(null);

		// TODO: Enable non-targeted, non-point AOE skills to trigger.
		if (targetType == 0 && effectedList.size() == 0 && firstTargetAttribute != FirstTargetAttribute.ME
			&& targetRangeAttribute != TargetRangeAttribute.AREA) {
			log.debug("targettype failed");
			return false;
		}
		return true;
	}

	/**
	 * Skill entry point
	 * 
	 * @return true if usage is successfull
	 */
	public boolean useSkill() {
		if (!canUseSkill())
			return false;

		calculateSkillDuration();

		// must be after calculateskillduration
		if (!checkAnimationTime()) {
			log.debug("check animation time failed");
			return false;
		}

		boostSkillCost = 0;

		// notify skill use observers
		if (skillMethod == SkillMethod.CAST)
			effector.getObserveController().notifySkilluseObservers(this);

		// start casting
		effector.setCasting(this);

		// log skill time if effector instance of player
		// TODO config
		if (effector instanceof Player)
			MotionLoggingService.getInstance().logTime((Player) effector, this.getSkillTemplate(), this.getHitTime(),
				MathUtil.getDistance(effector, firstTarget));

		int cooldown = effector.getSkillCooldown(skillTemplate);
		if (cooldown != 0) {
			effector.setSkillCoolDown(skillTemplate.getCooldownId(),
				cooldown * 100 + this.duration + System.currentTimeMillis());
			effector.setSkillCoolDownBase(skillTemplate.getCooldownId(), System.currentTimeMillis());
		}

		// send packets to start casting
		if (skillMethod == SkillMethod.CAST || skillMethod == SkillMethod.ITEM)
			startCast();

		effector.getObserveController().attach(conditionChangeListener);

		if (this.duration > 0) {
			schedule(this.duration);
		}
		else {
			endCast();
		}
		return true;
	}

	protected void calculateSkillDuration() {
		// Skills that are not affected by boost casting time
		if (isCastTimeFixed()) {
			duration = skillTemplate.getDuration();
			return;
		}
		duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME, skillTemplate.getDuration());
		switch (skillTemplate.getSubType()) {
			case SUMMON:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_SUMMON, duration);
				break;
			case SUMMONHOMING:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_SUMMONHOMING, duration);
				break;
			case SUMMONTRAP:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_TRAP, duration);
				break;
			case HEAL:
				duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_HEAL, duration);
				break;
			case ATTACK:
				if (skillTemplate.getType() == com.aionemu.gameserver.skillengine.model.SkillType.MAGICAL) {
					duration = effector.getGameStats().getPositiveReverseStat(StatEnum.BOOST_CASTING_TIME_ATTACK, duration);
				}
				break;
		}

		// 70% of base skill duration cap
		// No cast speed cap for skill Summoning Alacrity I(skillId: 1778)
		if (!effector.getEffectController().hasAbnormalEffect(1778)) {
			int baseDurationCap = Math.round(skillTemplate.getDuration() * 0.3f);
			if (duration < baseDurationCap) {
				duration = baseDurationCap;
			}
		}

		if (duration < 0) {
			duration = 0;
		}
	}

	private boolean checkAnimationTime() {
		if (!(effector instanceof Player) || skillMethod != SkillMethod.CAST)// TODO item skills?
			return true;
		Player player = (Player) effector;
		
		//if player is without weapon, dont check animation time
		if (player.getEquipment().getMainHandWeaponType() == null)
			return true;
		
		/**
		 * exceptions for certain skills
		 * -herb and mana treatment
		 * -traps
		 */
		//dont check herb and mana treatment
		switch (this.getSkillId()) {
			case 1803:// herb treatment
			case 1804:
			case 1805:
			case 1825:
			case 1827:
			case 1823:// mana treatment
			case 1824:
			case 1826:
			case 1828:
				return true;
		}
		if (this.getSkillTemplate().getSubType() == SkillSubType.SUMMONTRAP)
			return true;
		
		Motion motion = this.getSkillTemplate().getMotion();
		
		if (motion == null || motion.getName() == null) {
			log.debug("missing motion for skillId: "+this.getSkillId());
			return true;
		}

		if (motion.getInstantSkill() && hitTime != 0) {
			log.debug("Instant and hitTime not 0! modified client_skills? player objectid: "+player.getObjectId());
			return false;
		} else if (!motion.getInstantSkill() && hitTime == 0) {
			log.debug("modified client_skills! player objectid: "+player.getObjectId());
			return false;
		}
		
		MotionTime motionTime = DataManager.MOTION_DATA.getMotionTime(motion.getName());

		if (motionTime == null) {
			log.warn("missing motiontime for motionName: "+motion.getName());
			return true;
		}
		
		WeaponTypeWrapper weapons = new WeaponTypeWrapper(player.getEquipment().getMainHandWeaponType(), player.getEquipment().getOffHandWeaponType());
		float serverTime = motionTime.getTimeForWeapon(weapons);
		int clientTime = hitTime;
		
		if (serverTime == 0) {
			log.warn("missing weapon time for motionName: "+motion.getName()+" weapons: "+weapons.toString());
			return true;
		}
		
		//adjust client time with ammotime
		long ammoTime = 0;
		double distance = MathUtil.getDistance(effector, firstTarget);
		if (getSkillTemplate().getAmmoSpeed() != 0)
			ammoTime = Math.round(distance / getSkillTemplate().getAmmoSpeed() * 1000);//checked with client
		clientTime -= ammoTime;
		
		//adjust servertime with motion play speed
		if (motion.getSpeed() != 100) {
			serverTime /= 100f;
			serverTime *= (float)motion.getSpeed();
		}

		Stat2 attackSpeed = player.getGameStats().getAttackSpeed();
		
		//adjust serverTime with attackSpeed
		if (attackSpeed.getBase() != attackSpeed.getCurrent())
			serverTime *= ((float)attackSpeed.getCurrent() / (float)attackSpeed.getBase()); 
		
		//10% tolerance
		serverTime *= 0.8f;
	
		int finalTime = Math.round(serverTime);
		if (motion.getInstantSkill() && hitTime == 0) {
			this.serverTime = Math.round(ammoTime);
		} 
		else {
			if (clientTime < finalTime) {
				// temp log to console for debugging
				log.warn("Possible bad motion, name: " + motion.getName() + " client time:" + clientTime + " < finalTime(serverTime): " + finalTime);
				
				log.debug("modified client_skills! (clientTime<finalTime) player objectid: " + player.getObjectId());
				return false;
			}
			
			this.serverTime = hitTime;
		}
		player.setNextSkillUse(System.currentTimeMillis() + duration + finalTime);
		return true;
	}

	/**
	 * Penalty success skill
	 */
	private void startPenaltySkill() {
		if (skillTemplate.getPenaltySkillId() == 0)
			return;

		Skill skill = SkillEngine.getInstance().getSkill(effector, skillTemplate.getPenaltySkillId(), 1, effector);
		skill.useSkill();
	}

	/**
	 * Start casting of skill
	 */
	private void startCast() {
		int targetObjId = firstTarget != null ? firstTarget.getObjectId() : 0;

		if (skillMethod == SkillMethod.CAST) {
			switch (targetType) {
				case 0: // PlayerObjectId as Target
					PacketSendUtility.broadcastPacketAndReceive(effector,
						new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, targetObjId,
							this.duration));
					if (effector instanceof Npc && firstTarget instanceof Player) {
						NpcAI2 ai = (NpcAI2) effector.getAi2();
						if (ai.poll(AIQuestion.CAN_SHOUT))
							ShoutEventHandler.onCast(ai, firstTarget);
					}
					break;

				case 3: // Target not in sight?
					PacketSendUtility.broadcastPacketAndReceive(effector,
						new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, 0,
							this.duration));
					break;

				case 1: // XYZ as Target
					PacketSendUtility.broadcastPacketAndReceive(effector,
						new SM_CASTSPELL(effector.getObjectId(), skillTemplate.getSkillId(), skillLevel, targetType, x, y, z,
							this.duration));
					break;
			}
		}
		else if (skillMethod == SkillMethod.ITEM && duration > 0) {
			PacketSendUtility.broadcastPacketAndReceive(effector, new SM_ITEM_USAGE_ANIMATION(effector.getObjectId(),
				firstTarget.getObjectId(), (this.itemObjectId == 0 ? 0 : this.itemObjectId), itemTemplate.getTemplateId(),
				this.duration, 0, 0));
		}
	}

	/**
	 * Set this skill as canceled
	 */
	public void cancelCast() {
		isCancelled = true;
	}
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * Apply effects and perform actions specified in skill template
	 */
	private void endCast() {
		if (!effector.isCasting() || isCancelled)
			return;

		// if target out of range
		if (skillTemplate == null)
			return;

		// Check if target is out of skill range
		Properties properties = skillTemplate.getProperties();
		if (properties != null && !properties.endCastValidate(this)) {
			effector.getController().cancelCurrentSkill();
			return;
		}

		if (!preUsageCheck()) {
			return;
		}

		effector.setCasting(null);

		if (this.getSkillTemplate().isAvatar() && effector instanceof Player) {
			AbyssService.rankerSkillAnnounce((Player) effector, this.getSkillTemplate().getNameId());
		}

		/**
		 * try removing item, if its not possible return to prevent exploits
		 */
		if (effector instanceof Player && skillMethod == SkillMethod.ITEM) {
			Item item = ((Player) effector).getInventory().getItemByObjId(this.itemObjectId);
			if (item == null)
				return;
			if (item.getActivationCount() > 1) {
				item.setActivationCount(item.getActivationCount() - 1);
			}
			else {
				if (!((Player) effector).getInventory().decreaseByObjectId(item.getObjectId(), 1, ItemUpdateType.DEC_USE))
					return;
			}
		}

		int spellStatus = 0;
		int dashStatus = 0;
		int resistCount = 0;
		boolean blockedChain = false;

		final List<Effect> effects = new ArrayList<Effect>();
		if (skillTemplate.getEffects() != null) {
			boolean blockAOESpread = false;
			for (Creature effected : effectedList) {
				Effect effect = new Effect(this, effected, 0, itemTemplate);
				// Force RESIST status if AOE spell spread must be blocked
				if (blockAOESpread)
					effect.setAttackStatus(AttackStatus.RESIST);
				effect.initialize();
				final int worldId = effector.getWorldId();
				final int instanceId = effector.getInstanceId();
				effect.setWorldPosition(worldId, instanceId, x, y, z);

				checkSkillSetException(effected);
				effects.add(effect);
				spellStatus = effect.getSpellStatus().getId();
				dashStatus = effect.getDashStatus().getId();

				// Block AOE propagation if firstTarget resists the spell
				if ((!blockAOESpread) && (effect.getAttackStatus() == AttackStatus.RESIST) && (isTargetAOE())){
					blockAOESpread = true;
				}
				
				if (effect.getAttackStatus() == AttackStatus.RESIST || effect.getAttackStatus() == AttackStatus.DODGE) {
					resistCount++;
				}
			}
			if (!effectedList.isEmpty() && resistCount == effectedList.size()) {
				blockedChain = true;
				blockedPenaltySkill = true;
			}
			
			// exception for point point skills(example Ice Sheet)
			if (effectedList.isEmpty() && this.isPointPointSkill()) {
				Effect effect = new Effect(this, null, 0, itemTemplate);
				effect.initialize();
				final int worldId = effector.getWorldId();
				final int instanceId = effector.getInstanceId();
				effect.setWorldPosition(worldId, instanceId, x, y, z);
				effects.add(effect);
				spellStatus = effect.getSpellStatus().getId();
			}
		}

		if (effector instanceof Player && skillMethod == SkillMethod.CAST) {
			Player playerEffector = (Player) effector;
			if (playerEffector.getController().isUnderStance()) {
				playerEffector.getController().stopStance();
			}
			if (skillTemplate.isStance()) {
				playerEffector.getController().startStance(skillTemplate.getSkillId());
			}
		}

		// Check Chain Skill Trigger Rate
		if (CustomConfig.SKILL_CHAIN_TRIGGERRATE) {
			int chainProb = skillTemplate.getChainSkillProb();
			if (chainProb != 0 && !blockedChain) {
				this.chainSuccess = Rnd.get(100) < chainProb;
			}else{
				this.chainSuccess = false;
			}
		}
		else {
			this.chainSuccess = true;
		}

		/**
		 * set variables for chaincondition check
		 */
		if (effector instanceof Player && this.chainSuccess && this.chainCategory != null) {
			((Player) effector).setChainCategory(this.chainCategory);
			((Player) effector).setLastChainSkillTime();
		}

		/**
		 * Perform necessary actions (use mp,dp items etc)
		 */
		Actions skillActions = skillTemplate.getActions();
		if (skillActions != null) {
			for (Action action : skillActions.getActions()) {
				action.act(this);
			}
		}

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				applyEffect(effects);
			}
		}, serverTime);

		// send packets to end casting
		if (skillMethod == SkillMethod.CAST || skillMethod == SkillMethod.ITEM)
			sendCastspellEnd(spellStatus, dashStatus, effects);
	}

	public void applyEffect(List<Effect> effects) {
		/**
		 * Apply effects to effected objects
		 */
		for (Effect effect : effects) {
			effect.applyEffect();
		}

		/**
		 * Use penalty skill
		 */
		if (!blockedPenaltySkill){
			startPenaltySkill();
		}
	}

	/**
	 * @param spellStatus
	 * @param effects
	 */
	private void sendCastspellEnd(int spellStatus, int dashStatus, List<Effect> effects) {
		if (skillMethod == SkillMethod.CAST) {
			switch (targetType) {
				case 0: // PlayerObjectId as Target
					PacketSendUtility.broadcastPacketAndReceive(effector, new SM_CASTSPELL_RESULT(this, effects, serverTime,
						chainSuccess, spellStatus, dashStatus));
					break;

				case 3: // Target not in sight?
					PacketSendUtility.broadcastPacketAndReceive(effector, new SM_CASTSPELL_RESULT(this, effects, serverTime,
						chainSuccess, spellStatus, dashStatus));
					break;

				case 1: // XYZ as Target
					PacketSendUtility.broadcastPacketAndReceive(effector, new SM_CASTSPELL_RESULT(this, effects, serverTime,
						chainSuccess, spellStatus, dashStatus, 1));
					break;
			}
		}
		else if (skillMethod == SkillMethod.ITEM) {
			PacketSendUtility.broadcastPacketAndReceive(effector, new SM_ITEM_USAGE_ANIMATION(effector.getObjectId(),
				firstTarget.getObjectId(), (this.itemObjectId == 0 ? 0 : this.itemObjectId), itemTemplate.getTemplateId(), 0,
				1, 0));
			if (effector instanceof Player)
				PacketSendUtility.sendPacket((Player) effector,
					SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(getItemTemplate().getNameId())));
		}
	}

	/**
	 * Schedule actions/effects of skill (channeled skills)
	 */
	private void schedule(int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				endCast();
			}
		}, delay);
	}

	/**
	 * Check all conditions before starting cast
	 */
	private boolean preCastCheck() {
		Conditions skillConditions = skillTemplate.getStartconditions();
		return skillConditions != null ? skillConditions.validate(this) : true;
	}

	/**
	 * Check all conditions before using skill
	 */
	private boolean preUsageCheck() {
		Conditions skillConditions = skillTemplate.getUseconditions();
		return skillConditions != null ? skillConditions.validate(this) : true;
	}

	private void checkSkillSetException(Creature effected) {
		int setNumber = skillTemplate.getSkillSetException();
		int maxOccur = skillTemplate.getSkillSetMaxOccur();
		if (setNumber != 0) {
			if (maxOccur != 0)
				effected.getEffectController().removeEffectBySetNumber(setNumber, maxOccur, skillTemplate.getSkillId());
			else
				effected.getEffectController().removeEffectBySetNumber(setNumber, 1);
		}
		else {
			effected.getEffectController().removeEffectWithSetNumberReserved();
		}
	}

	/**
	 * @param value
	 *          is the changeMpConsumptionValue to set
	 */
	public void setBoostSkillCost(int value) {
		boostSkillCost = value;
	}

	/**
	 * @return the changeMpConsumptionValue
	 */
	public int getBoostSkillCost() {
		return boostSkillCost;
	}

	/**
	 * @return the effectedList
	 */
	public List<Creature> getEffectedList() {
		return effectedList;
	}

	/**
	 * @return the effector
	 */
	public Creature getEffector() {
		return effector;
	}

	/**
	 * @return the skillLevel
	 */
	public int getSkillLevel() {
		return skillLevel;
	}

	/**
	 * @return the skillId
	 */
	public int getSkillId() {
		return skillTemplate.getSkillId();
	}

	/**
	 * @return the skillStackLvl
	 */
	public int getSkillStackLvl() {
		return skillStackLvl;
	}

	/**
	 * @return the conditionChangeListener
	 */
	public StartMovingListener getConditionChangeListener() {
		return conditionChangeListener;
	}

	/**
	 * @return the skillTemplate
	 */
	public SkillTemplate getSkillTemplate() {
		return skillTemplate;
	}

	/**
	 * @return the firstTarget
	 */
	public Creature getFirstTarget() {
		return firstTarget;
	}

	/**
	 * @param firstTarget
	 *          the firstTarget to set
	 */
	public void setFirstTarget(Creature firstTarget) {
		this.firstTarget = firstTarget;
	}

	/**
	 * @return true or false
	 */
	public boolean isPassive() {
		return skillTemplate.getActivationAttribute() == ActivationAttribute.PASSIVE;
	}

	/**
	 * @return the firstTargetRangeCheck
	 */
	public boolean isFirstTargetRangeCheck() {
		return firstTargetRangeCheck;
	}

	/**
	 * @param FirstTargetAttribute
	 *          the firstTargetAttribute to set
	 */
	public void setFirstTargetAttribute(FirstTargetAttribute firstTargetAttribute) {
		this.firstTargetAttribute = firstTargetAttribute;
	}

	/**
	 * @return true if the present skill is a non-targeted, non-point AOE skill
	 */
	public boolean checkNonTargetAOE() {
		return (firstTargetAttribute == FirstTargetAttribute.ME && targetRangeAttribute == TargetRangeAttribute.AREA);
	}

	/**
	 * @return true if the present skill is a targeted AOE skill
	 */
	public boolean isTargetAOE() {
		return (firstTargetAttribute == FirstTargetAttribute.TARGET && targetRangeAttribute == TargetRangeAttribute.AREA);
	}

	/**
	 * @return true if the present skill is a self buff includes items (such as scroll buffs)
	 */
	public boolean isSelfBuff() {
		return (firstTargetAttribute == FirstTargetAttribute.ME && targetRangeAttribute == TargetRangeAttribute.ONLYONE);
	}

	/**
	 * @return true if the present skill has self as first target
	 */
	public boolean isFirstTargetSelf() {
		return (firstTargetAttribute == FirstTargetAttribute.ME);
	}

	/**
	 * @return true if the present skill has PC target condition
	 */
	public boolean isPCCondition() {
		return (targetCondition == TargetAttribute.PC);
	}

	/**
	 * @return true if the present skill has NPC target condition
	 */
	public boolean isNPCCondition() {
		return (targetCondition == TargetAttribute.NPC);
	}

	/**
	 * @return true if the present skill is a Point skill
	 */
	public boolean isPointSkill() {
		return (this.firstTargetAttribute == FirstTargetAttribute.POINT);
	}

	/**
	 * @param TargetCondition
	 *          the targetCondition to set
	 */
	public void setTargetCondition(TargetAttribute cond) {
		this.targetCondition = cond;
	}

	/**
	 * @param firstTargetRangeCheck
	 *          the firstTargetRangeCheck to set
	 */
	public void setFirstTargetRangeCheck(boolean firstTargetRangeCheck) {
		this.firstTargetRangeCheck = firstTargetRangeCheck;
	}

	/**
	 * @param itemTemplate
	 *          the itemTemplate to set
	 */
	public void setItemTemplate(ItemTemplate itemTemplate) {
		this.itemTemplate = itemTemplate;
	}

	public ItemTemplate getItemTemplate() {
		return this.itemTemplate;
	}

	public void setItemObjectId(int id) {
		this.itemObjectId = id;
	}

	public int getItemObjectId() {
		return this.itemObjectId;
	}

	/**
	 * @param targetRangeAttribute
	 *          the targetRangeAttribute to set
	 */
	public void setTargetRangeAttribute(TargetRangeAttribute targetRangeAttribute) {
		this.targetRangeAttribute = targetRangeAttribute;
	}

	/**
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTargetType(int targetType, float x, float y, float z) {
		this.targetType = targetType;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Calculated position after skill
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param h
	 */
	public void setTargetPosition(float x, float y, float z, byte h) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = h;
	}

	public void setDuration(int t) {
		this.duration = t;
	}

	public int getDuration() {
		return this.duration;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public final byte getH() {
		return h;
	}

	/**
	 * @return Returns the time.
	 */
	public int getHitTime() {
		return hitTime;
	}

	/**
	 * @param time
	 *          The time to set.
	 */
	public void setHitTime(int time) {
		this.hitTime = time;
	}

	/**
	 * @return true if skill must not be affected by boost casting time this comes from old 1.5.0.5 patch notes and still
	 *         applies on 2.5 (confirmed) TODO: maybe another implementation? At the moment this doesnt seem to be handled
	 *         on client infos, so it's hard coded
	 */
	private boolean isCastTimeFixed() {
		if (skillMethod != SkillMethod.CAST) // only casted skills are affected
			return true;

		switch (this.getSkillId()) {
			case 1636:// Fear I
			case 2318:
			case 2319:
			case 1685:// Fear Shriek I
			case 2006:// Hand of Torpor
			case 2011:// Spirit Hypnosis
			case 1495:// Sleep I
			case 2316:
			case 2317:
			case 1443:// Sleeping Storm
			case 1454:// Curse of Roots
			case 1430:// Curse of Old Roots
			case 1497:// Tranquilizing Cloud I
			case 11885:// abyss transformation elyos
			case 11886:
			case 11887:
			case 11888:
			case 11889:
			case 11890:// abyss transformation asmo
			case 11891:
			case 11892:
			case 11893:
			case 11894:
			case 1801:// return skill
			case 1803:// herb treatment
			case 1804:
			case 1805:
			case 1825:
			case 1827:
			case 1823:// mana treatment
			case 1824:
			case 1826:
			case 1828:
				return true;
		}

		return false;
	}

	public boolean isGroundSkill() {
		return skillTemplate.isGroundSkill();
	}

	public void setChainCategory(String chainCategory) {
		this.chainCategory = chainCategory;
	}

	public SkillMethod getSkillMethod() {
		return this.skillMethod;
	}
	
	public boolean isPointPointSkill() {
		if (this.getSkillTemplate().getProperties().getFirstTarget() == FirstTargetAttribute.POINT
			&& this.getSkillTemplate().getProperties().getTargetType() == TargetRangeAttribute.POINT)
			return true;

		return false;
	}
}
