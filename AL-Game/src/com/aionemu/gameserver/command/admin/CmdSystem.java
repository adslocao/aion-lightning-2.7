package com.aionemu.gameserver.command.admin;

import java.util.List;

import com.aionemu.commons.utils.AEInfos;
import com.aionemu.gameserver.ShutdownHook;
import com.aionemu.gameserver.ShutdownHook.ShutdownMode;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.AEVersions;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;


public class CmdSystem extends BaseCommand {
	public void execute (Player admin, String... params) {
		if (params.length < 1) {
			showHelp(admin);
			return;
		}

		if (params[0].equalsIgnoreCase("info")) {
			PacketSendUtility.sendMessage(admin, "System Informations at: " + AEInfos.getRealTime().toString());// Time
			
			for (String line : AEVersions.getFullVersionInfo()) // Version Infos
				PacketSendUtility.sendMessage(admin, line);
			
			for (String line : AEInfos.getOSInfo()) // OS Infos
				PacketSendUtility.sendMessage(admin, line);
			
			for (String line : AEInfos.getCPUInfo()) // CPU Infos
				PacketSendUtility.sendMessage(admin, line);
			
			for (String line : AEInfos.getJREInfo()) // JRE Infos
				PacketSendUtility.sendMessage(admin, line);
			
			for (String line : AEInfos.getJVMInfo()) // JVM Infos
				PacketSendUtility.sendMessage(admin, line);
		}
		else if (params[0].equalsIgnoreCase("memory")) {
			for (String line : AEInfos.getMemoryInfo()) // Memory Infos
				PacketSendUtility.sendMessage(admin, line);
		}
		else if (params[0].equalsIgnoreCase("gc")) {
			long time = System.currentTimeMillis();
			PacketSendUtility.sendMessage(admin, "RAM Used (Before): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			System.gc();
			PacketSendUtility.sendMessage(admin, "RAM Used (After): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			System.runFinalization();
			PacketSendUtility.sendMessage(admin, "RAM Used (Final): "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576));
			PacketSendUtility.sendMessage(admin,
				"Garbage Collection and Finalization finished in: " + (System.currentTimeMillis() - time) + " milliseconds...");
		}
		else if (params[0].equalsIgnoreCase("shutdown")) {
			try {
				int val = Integer.parseInt(params[1]);
				int announceInterval = Integer.parseInt(params[2]);
				ShutdownHook.getInstance().doShutdown(val, announceInterval, ShutdownMode.SHUTDOWN);
				PacketSendUtility.sendMessage(admin, "Server will shutdown in " + val + " seconds.");
			}
			catch (ArrayIndexOutOfBoundsException e) {
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
		}
		else if (params[0].equals("restart")) {
			try {
				int val = Integer.parseInt(params[1]);
				int announceInterval = Integer.parseInt(params[2]);
				ShutdownHook.getInstance().doShutdown(val, announceInterval, ShutdownMode.RESTART);
				PacketSendUtility.sendMessage(admin, "Server will restart in " + val + " seconds.");
			}
			catch (ArrayIndexOutOfBoundsException e) {
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "Numbers only!");
			}
		}
		else if (params[0].equals("threadpool")) {
			List<String> stats = ThreadPoolManager.getInstance().getStats();
			for (String stat : stats)
				PacketSendUtility.sendMessage(admin, stat.replaceAll("\t", ""));
		}
	}
}
