package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapType;


/**
 * Admin command allowing to change weathers of the world.
 * 
 * @author Kwazar
 */
public class CmdWeather extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length == 0 || params.length > 2) {
			showHelp(admin);
			return;
		}

		String regionName = null;
		int weatherType = -1;

		regionName = new String(params[0]);

		if (params.length == 2) {
			try {
				weatherType = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "weather type parameter need to be an integer [0-8].");
				return;
			}
		}

		if (regionName.equals("reset")) {
			WeatherService.getInstance().resetWeather();
			return;
		}

		// Retrieving regionId by name
		WorldMapType region = null;
		for (WorldMapType worldMapType : WorldMapType.values()) {
			if (worldMapType.name().toLowerCase().equals(regionName.toLowerCase())) {
				region = worldMapType;
			}
		}

		if (region != null) {
			if (weatherType > -1 && weatherType < 9) {
				WeatherService.getInstance().changeRegionWeather(region.getId(), new Integer(weatherType));
			}
			else {
				PacketSendUtility.sendMessage(admin, "Weather type must be between 0 and 8");
				return;
			}
		}
		else {
			PacketSendUtility.sendMessage(admin, "Region " + regionName + " not found");
			return;
		}
	}

}