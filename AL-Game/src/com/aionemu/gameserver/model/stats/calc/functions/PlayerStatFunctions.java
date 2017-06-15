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
package com.aionemu.gameserver.model.stats.calc.functions;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class PlayerStatFunctions {

	private static final List<IStatFunction> FUNCTIONS = new ArrayList<IStatFunction>();

	static {
		FUNCTIONS.add(new PhysicalAttackFunction());
		FUNCTIONS.add(new MagicalAttackFunction());
		FUNCTIONS.add(new AttackSpeedFunction());
		FUNCTIONS.add(new BoostCastingTimeFunction());
		FUNCTIONS.add(new PvPAttackRatioFunction());
		FUNCTIONS.add(new PDefFunction());
		FUNCTIONS.add(new MaxHpFunction());
		FUNCTIONS.add(new MaxMpFunction());

		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.BLOCK, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.PARRY, 0.25f));
		FUNCTIONS.add(new AgilityModifierFunction(StatEnum.EVASION, 0.3f));
	}

	public static final List<IStatFunction> getFunctions() {
		return FUNCTIONS;
	}

	public static final void addPredefinedStatFunctions(Player player) {
		player.getGameStats().addEffectOnly(null, FUNCTIONS);
	}
}

class PhysicalAttackFunction extends StatFunction {

	PhysicalAttackFunction() {
		stat = StatEnum.PHYSICAL_ATTACK;
	}

	@Override
	public void apply(Stat2 stat) {
		float power = stat.getOwner().getGameStats().getPower().getCurrent();
		stat.setBase(Math.round(stat.getBase() * power / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}

class AgilityModifierFunction extends StatFunction {

	private float modifier;

	AgilityModifierFunction(StatEnum stat, float modifier) {
		this.stat = stat;
		this.modifier = modifier;
	}

	@Override
	public void apply(Stat2 stat) {
		float agility = stat.getOwner().getGameStats().getAgility().getCurrent();
		stat.setBase(Math.round(stat.getBase() + stat.getBase() * (agility - 100) * modifier / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}

class MaxHpFunction extends StatFunction {

	MaxHpFunction() {
		stat = StatEnum.MAXHP;
	}

	@Override
	public void apply(Stat2 stat) {
		float health = stat.getOwner().getGameStats().getHealth().getCurrent();
		stat.setBase(Math.round(stat.getBase() * health / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}

class MaxMpFunction extends StatFunction {

	MaxMpFunction() {
		stat = StatEnum.MAXMP;
	}

	@Override
	public void apply(Stat2 stat) {
		float will = stat.getOwner().getGameStats().getWill().getCurrent();
		stat.setBase(Math.round(stat.getBase() * will / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}

class MagicalAttackFunction extends StatFunction {

	MagicalAttackFunction() {
		stat = StatEnum.MAGICAL_ATTACK;
	}

	@Override
	public void apply(Stat2 stat) {
		float knowledge = stat.getOwner().getGameStats().getKnowledge().getCurrent();
		stat.setBase(Math.round(stat.getBase() * knowledge / 100f));
	}

	@Override
	public int getPriority() {
		return 30;
	}
}

class PDefFunction extends StatFunction {

	PDefFunction() {
		stat = StatEnum.PHYSICAL_DEFENSE;
	}

	@Override
	public void apply(Stat2 stat) {
		if (stat.getOwner().isInFlyingState())
			stat.setBonus(stat.getBonus() - (stat.getBase() / 2));
	}

	@Override
	public int getPriority() {
		return 60;
	}
}

class AttackSpeedFunction extends DuplicateStatFunction {

	AttackSpeedFunction() {
		stat = StatEnum.ATTACK_SPEED;
	}

}

class BoostCastingTimeFunction extends DuplicateStatFunction {

	BoostCastingTimeFunction() {
		stat = StatEnum.BOOST_CASTING_TIME;
	}
}

class PvPAttackRatioFunction extends DuplicateStatFunction {

	PvPAttackRatioFunction() {
		stat = StatEnum.PVP_ATTACK_RATIO;
	}
}

class DuplicateStatFunction extends StatFunction {

	@Override
	public void apply(Stat2 stat) {
		Item mainWeapon = ((Player) stat.getOwner()).getEquipment().getMainHandWeapon();
		Item offWeapon = ((Player) stat.getOwner()).getEquipment().getOffHandWeapon();
		if (mainWeapon != null) {
			StatFunction func1 = null;
			StatFunction func2 = null;
			List<StatFunction> functions1 = mainWeapon.getItemTemplate().getModifiers();
			if (functions1 != null) {
				for (StatFunction func : functions1) {
					if (func.getName() == getName()) {
						func1 = func;
					}
				}
			}

			if (mainWeapon.hasFusionedItem()) {
				ItemTemplate template = mainWeapon.getFusionedItemTemplate();
				if (template != null && template.getModifiers() != null) {
					for (StatFunction func : template.getModifiers()) {
						if (func.getName() == getName()) {
							func2 = func;
						}
					}
				}
			}
			else if (offWeapon != null) {
				List<StatFunction> functions2 = offWeapon.getItemTemplate().getModifiers();
				if (functions2 != null) {
					for (StatFunction func : functions2) {
						if (func.getName() == getName()) {
							func2 = func;
						}
					}
				}
			}
			
			if (func2 == null && func1 == null)
				return;
			else if (func2 != null && func1 == null) {
				func2.apply(stat);
			}
			else if (func2 == null && func1 != null) {
				func1.apply(stat);
			}
			else {
				// pvp attack ratio should be stacked on dual wield
				if (offWeapon != null && getName() == StatEnum.PVP_ATTACK_RATIO) {
					func1.apply(stat);
					func2.apply(stat);
				}
				else {
					if (Math.abs(func1.getValue()) >= Math.abs(func2.getValue()))
						func1.apply(stat);
					else
						func2.apply(stat);
				}
			}
		}
	}

	@Override
	public int getPriority() {
		return 60;
	}

}
