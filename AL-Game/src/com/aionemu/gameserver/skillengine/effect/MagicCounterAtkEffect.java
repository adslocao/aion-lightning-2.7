/*
 Aion-core by vegar
 */
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.CreatureLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillSubType;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ViAl
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MagicCounterAtkEffect")
public class MagicCounterAtkEffect extends EffectTemplate {

	@XmlAttribute
	protected int maxdmg;

	//TODO bosses are resistent to this?
	
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void startEffect(final Effect effect) {
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		final CreatureLifeStats<? extends Creature> cls = effect.getEffected().getLifeStats();
		ActionObserver observer = null;

		observer = new ActionObserver(ObserverType.SKILLUSE) {

			@Override
			public void skilluse(final Skill skill) {
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						if(skill.isCancelled()){
							return;
						}

						SkillSubType subType = skill.getSkillTemplate().getSubType();
						// Proc on magical skill
						if (skill.getSkillTemplate().getType() != SkillType.MAGICAL){
							return;
						}
						if(subType == SkillSubType.CHANT ||
								subType == SkillSubType.SUMMONHOMING){
							return;
						}
						
						int applyDmg = maxdmg;
						
						if (cls.getMaxHp() / 100 * value <= maxdmg){
							applyDmg = cls.getMaxHp() / 100 * value;
							effected.getController().onAttack(effector, effect.getSkillId(), TYPE.DAMAGE,
									applyDmg, true, LOG.REGULAR);
						}else{
							effected.getController().onAttack(effector, applyDmg, true);
						}
					}
				}, skill.getDuration());

			}
		};

		effect.setActionObserver(observer, position);
		effected.getObserveController().addObserver(observer);
	}

	@Override
	public void endEffect(Effect effect) {
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
