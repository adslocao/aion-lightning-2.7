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
package com.aionemu.gameserver.command.admin;

import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunctionProxy;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * @author MrPoke
 */
public class CmdStat extends BaseCommand {

	private static final Logger log = LoggerFactory.getLogger(CmdStat.class);
	/**
	 * @param alias
	 */
	
	public void execute(Player admin, String... params) {
		if (params.length >= 1) {
			VisibleObject target = admin.getTarget();
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "No target selected");
				return;
			}
			if (target instanceof Creature) {
				Creature creature = (Creature) target;

				TreeSet<IStatFunction> stats = creature.getGameStats().getStatsByStatEnum(StatEnum.valueOf(params[0]));
				
				if (params.length == 1) {
					for (IStatFunction stat : stats) {
						PacketSendUtility.sendMessage(admin, stat.toString());
					}
				}
				else if ("details".equals(params[1])) {
					for (IStatFunction stat : stats) {
						String details = collectDetails(stat);
						PacketSendUtility.sendMessage(admin, details);
						log.info(details);
					}
				}
			}
		}
	}

	private String collectDetails(IStatFunction stat) {
		StringBuffer sb = new StringBuffer();
		sb.append(stat.toString() + "\n");
		if(stat instanceof StatFunctionProxy){
			StatFunctionProxy proxy = (StatFunctionProxy) stat;
			sb.append(" -- " + proxy.getProxiedFunction().toString());
		}
		StatOwner owner = stat.getOwner();
		if(owner instanceof Effect){
			Effect effect = (Effect) owner;
			sb.append("\n -- skillId: " + effect.getSkillId());
			sb.append("\n -- skillName: " + effect.getSkillName());
		}
		return sb.toString();
	}

}
