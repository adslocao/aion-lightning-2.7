package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author KID
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShieldCondition")
public class ShieldCondition extends Condition {

	@Override
	public boolean validate(Skill env) {
		if (env.getEffector() instanceof Player) {
			Player player = (Player) env.getEffector();
			return player.getEquipment().isShieldEquipped();
		}
		
		return false;
	}
}
