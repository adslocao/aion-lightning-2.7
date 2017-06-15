/*
 * This file is part of aion-engine <aion-engine.com>
 *
 * aion-engine is private software: you can redistribute it and or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Private Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with aion-engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.command.admin;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.services.MotionLoggingService;
import com.aionemu.gameserver.utils.PacketSendUtility;


public class CmdMotion extends BaseCommand implements StatOwner {
	


	public void execute(Player admin, String... params) {
		if (params.length == 1) {
			showHelp(admin);
			return;
		}
		if (params[1].equalsIgnoreCase("help")) {
			showHelp(admin);
		}	
		else if (params[1].equalsIgnoreCase("start")) {
			MotionLoggingService.getInstance().start();
			PacketSendUtility.sendMessage(admin, "MotionLogginService was started!\nData loaded from DB.");
		}	
		else if (params[1].equalsIgnoreCase("analyze")) {
			MotionLoggingService.getInstance().createAnalyzeFiles();
			PacketSendUtility.sendMessage(admin, "Created testing files!");
		}	
		else if (params[1].equalsIgnoreCase("createxml")) {
			MotionLoggingService.getInstance().createFinalFile();
			PacketSendUtility.sendMessage(admin, "Created new_motion_times.xml in data/static_data/skills!");
		}
		else if (params[1].equalsIgnoreCase("savetosql")) {
			MotionLoggingService.getInstance().saveToSql();
			PacketSendUtility.sendMessage(admin, "MotionLog data saved to sql!");
		}
		else if (params[1].equalsIgnoreCase("advanced")) {
			MotionLoggingService.getInstance().setAdvancedLog((!MotionLoggingService.getInstance().getAdvancedLog()));
			PacketSendUtility.sendMessage(admin, "AdvancedLog set to: "+MotionLoggingService.getInstance().getAdvancedLog());
		} 
		else if (params[1].equalsIgnoreCase("as")) {
			int parameter = 10000;
			if (params.length == 3) {
				try {
					parameter = ParseInteger(params[2]);
				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "Parameter should number");
					return;
				}
			}
			this.addAttackSpeed(admin, -parameter);
			PacketSendUtility.sendMessage(admin, "Attack Speed updated");
		}
		else
			showHelp(admin);
	}
	
	private void addAttackSpeed(Player player, int i) {
		if (i == 0) {
			player.getGameStats().endEffect(this);
		}	else {
			List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
			modifiers.add(new StatAddFunction(StatEnum.ATTACK_SPEED, i, true));
			player.getGameStats().endEffect(this);
			player.getGameStats().addEffect(this, modifiers);
		}
	}
}