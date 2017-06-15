package com.aionemu.gameserver.command.admin;

import java.util.Iterator;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.event.AIEventLog;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/*syntax //ai2 <set|event|event2|info|log|print|createlog|eventlog|movelog>*/


/**
 * @author ATracer
 */
public class CmdAi2Command extends BaseCommand {


	public void execute(Player admin, String... params) {
		/**
		 * Non target commands
		 */

		if (params[0].equalsIgnoreCase("createlog")) {
			boolean oldValue = AIConfig.ONCREATE_DEBUG;
			AIConfig.ONCREATE_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(admin, "New createlog value: " + !oldValue);
			return;
		}

		if (params[0].equalsIgnoreCase("eventlog")) {
			boolean oldValue = AIConfig.EVENT_DEBUG;
			AIConfig.EVENT_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(admin, "New eventlog value: " + !oldValue);
			return;
		}

		if (params[0].equalsIgnoreCase("movelog")) {
			boolean oldValue = AIConfig.MOVE_DEBUG;
			AIConfig.MOVE_DEBUG = !oldValue;
			PacketSendUtility.sendMessage(admin, "New movelog value: " + !oldValue);
			return;
		}

		if (params[0].equalsIgnoreCase("say")) {
			LoggerFactory.getLogger(CmdAi2Command.class).info("[AI2] marker: " + params[1]);
		}

		/**
		 * Target commands
		 */
		VisibleObject target = admin.getTarget();

		if (target == null || !(target instanceof Npc)) {
			PacketSendUtility.sendMessage(admin, "Select target first (Npc only)");
			return;
		}
		Npc npc = (Npc) target;

		if (params[0].equalsIgnoreCase("info")) {
			PacketSendUtility.sendMessage(admin, "Ai name: " + npc.getAi2().getName());
			PacketSendUtility.sendMessage(admin, "Ai state: " + npc.getAi2().getState());
			PacketSendUtility.sendMessage(admin, "Ai substate: " + npc.getAi2().getSubState());
			return;
		}

		if (params[0].equalsIgnoreCase("log")) {
			boolean oldValue = npc.getAi2().isLogging();
			((AbstractAI) npc.getAi2()).setLogging(!oldValue);
			PacketSendUtility.sendMessage(admin, "New log value: " + !oldValue);
			return;
		}

		if (params[0].equalsIgnoreCase("print")) {
			AIEventLog eventLog = ((AbstractAI) npc.getAi2()).getEventLog();
			Iterator<AIEventType> iterator = eventLog.iterator();
			while (iterator.hasNext()) {
				PacketSendUtility.sendMessage(admin, "EVENT: " + iterator.next().name());
			}
			return;
		}

		if (params[0].equalsIgnoreCase("set")) {
			String aiName = params[1];
			AI2Engine.getInstance().setupAI(aiName, npc);
		}
		else if (params[0].equalsIgnoreCase("event")) {
			AIEventType eventType = AIEventType.valueOf(params[1].toUpperCase());
			if (eventType != null) {
				npc.getAi2().onGeneralEvent(eventType);
			}
		}
		else if (params[0].equalsIgnoreCase("event2")) {
			AIEventType eventType = AIEventType.valueOf(params[1].toUpperCase());
			Creature creature = (Creature) World.getInstance().findVisibleObject(Integer.valueOf(params[2]));
			if (eventType != null) {
				npc.getAi2().onCreatureEvent(eventType, creature);
			}
		}
	}

}
