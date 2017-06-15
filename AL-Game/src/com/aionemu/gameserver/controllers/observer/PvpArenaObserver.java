package com.aionemu.gameserver.controllers.observer;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.PvPArenaReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillSubType;
import com.aionemu.gameserver.utils.MathUtil;

public class PvpArenaObserver extends ActionObserver {
	private Player player;
	private boolean isStarted;
	private PvPArenaReward instanceReward;

	public PvpArenaObserver(Player player, PvPArenaReward instanceReward, boolean isStarted) {
		super(ObserverType.ALL);
		this.player = player;
		this.isStarted = isStarted;
		this.instanceReward = instanceReward;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}
	
	private boolean isOnDiscipline(){
		return (player.getWorldId() == 300430000 || player.getWorldId() == 300360000);
	}
	
	private boolean isOnChaos(){
		return (player.getWorldId() == 300350000 || player.getWorldId() == 300420000);
	}
	
	@Override
	public void moved() {
		checkUnderBridge();
	}
	
	@Override
	public void skilluse(Skill skill) {
		checkSkillBeforeStart(skill);
		checkUnderBridge();
	}

	public void attacked(Creature creature) {
		checkUnderBridge();
	};

	public void attack(Creature creature) {
		checkUnderBridge();
		looseAtkPoint();
	};

	public void equip(Item item, Player owner) {
		checkUnderBridge();
	};
	
	public void unequip(Item item, Player owner) {
		checkUnderBridge();
	};

	public void dotattacked(Creature creature, Effect dotEffect) {
		checkUnderBridge();
	};
	
	public void itemused(Item item) {
	};

	private void looseAtkPoint() {
		if((!isOnDiscipline() && !isOnChaos()) || isStarted){
			return;
		}
		instanceReward.regPlayerReward(player);
		((PvPArenaPlayerReward) instanceReward.getPlayerReward(player)).addPoints(-200);
		instanceReward.sendPacket();
	}
	
	private void checkSkillBeforeStart(Skill skill){
		if((!isOnDiscipline() && !isOnChaos()) || isStarted){
			return;
		}

		if(skill.getSkillTemplate().getSubType() == SkillSubType.ATTACK || skill.getSkillTemplate().getSubType() == SkillSubType.SUMMONTRAP || skill.getSkillTemplate().getSubType() == SkillSubType.DEBUFF){
			skill.cancelCast();
		}
	}
	
	private void checkUnderBridge(){
		boolean passedThrough = false;

		if(!isOnDiscipline()){
			return;
		}
		
		passedThrough = (MathUtil.isInRange(player.getX(), player.getY(), 290, 1249, 200) && player.getZ() < 234);

		if (!passedThrough) {
			return;
		}

		if (!(player.getLifeStats().isAlreadyDead())){
			player.getController().die();
		}
		
		player.getFlyController().endFly();
		player.getObserveController().removeObserver(this);
	}
}
