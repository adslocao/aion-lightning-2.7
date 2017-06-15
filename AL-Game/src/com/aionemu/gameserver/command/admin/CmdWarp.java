package com.aionemu.gameserver.command.admin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author Source
 */
public class CmdWarp extends BaseCommand {
	
	
	public void execute(Player player, String... params) {
		String locS, first, last;
		float xF, yF, zF;
		locS = "";
		int mapL = 0;
		int layerI = -1;
		
		if(params.length < 5) {
			if (!GeoDataConfig.GEO_ENABLE) {
				PacketSendUtility.sendMessage(player, "You must turn on geo in config to use this command!");
				return;
			}
			showHelp(player);
			return;
		}

		first = params[0];
		xF = ParseFloat(params[1]);
		yF = ParseFloat(params[2]);
		zF = ParseFloat(params[3]);
		last = params[3];

		Pattern f = Pattern.compile("\\[pos:([^;]+);\\s*+(\\d{9})");
		Pattern l = Pattern.compile("(\\d)\\]");
		Matcher fm = f.matcher(first);
		Matcher lm = l.matcher(last);

		if (fm.find()) {
			locS = fm.group(1);
			mapL = ParseInteger(fm.group(2));
		}
		if (lm.find())
			layerI = ParseInteger(lm.group(1));

		zF = GeoService.getInstance().getZ(mapL, xF, yF);
		PacketSendUtility.sendMessage(player, "MapId (" + mapL + ")\n" + "x:" + xF + " y:" + yF + " z:" + zF + " l("
			+ layerI + ")");

		if (mapL == 400010000)
			PacketSendUtility.sendMessage(player, "Sorry you can't warp at abyss");
		else {
			TeleportService.teleportTo(player, mapL, xF, yF, zF, 0);
			PacketSendUtility.sendMessage(player, "You have successfully warp -> " + locS);
		}
	}
}

