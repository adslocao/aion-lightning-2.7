package com.aionemu.gameserver.command.admin;

import java.lang.reflect.Field;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.administration.DeveloperConfig;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.DredgionConfig;
import com.aionemu.gameserver.configs.main.DropConfig;
import com.aionemu.gameserver.configs.main.EnchantsConfig;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.main.FallDamageConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.HTMLConfig;
import com.aionemu.gameserver.configs.main.InGameShopConfig;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.PeriodicSaveConfig;
import com.aionemu.gameserver.configs.main.PricesConfig;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.configs.main.ShutdownConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.main.ThreadConfig;
import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.configs.network.IPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.collect.ImmutableMap;

/*syntax //configure <set|show> <configname> <property> [<newvalue>*/


public class CmdConfigure extends BaseCommand {

	private static final ImmutableMap<String, Class<?>> commands = new ImmutableMap.Builder<String, Class<?>>()
		.put("player", AdminConfig.class)
		.put("ai", AIConfig.class)
		.put("cache", CacheConfig.class)
		.put("custom", CustomConfig.class)
		.put("dredgion", DredgionConfig.class)
		.put("drop", DropConfig.class)
		.put("enchants", EnchantsConfig.class)
		.put("events", EventsConfig.class)
		.put("falldamage", FallDamageConfig.class)
		.put("gameserver", GSConfig.class)
		.put("geodata", GeoDataConfig.class)
		.put("group", GroupConfig.class)
		.put("html", HTMLConfig.class)
		.put("ingameshop", InGameShopConfig.class)
		.put("legions", LegionConfig.class)
		.put("membership", MembershipConfig.class)
		.put("periodicsave", PeriodicSaveConfig.class)
		.put("prices", PricesConfig.class)
		.put("ranking", RankingConfig.class)
		.put("rates", RateConfig.class)
		.put("shutdown", ShutdownConfig.class)
		.put("siege", SiegeConfig.class)
		.put("thread", ThreadConfig.class)
		.put("world", WorldConfig.class)
		.put("ipconfig", IPConfig.class)
		.put("network", NetworkConfig.class)
		.put("developer", DeveloperConfig.class)
		.build();

	
	
	public void execute(Player player, String... params) {
		String command = "";
		if (params.length == 3) {
			// show
			command = params[0];
			if (!"show".equalsIgnoreCase(command)) {
				PacketSendUtility.sendMessage(player, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
				return;
			}
		}
		else if (params.length == 4) {
			// set
			command = params[0];
			if (!"set".equalsIgnoreCase(command)) {
				PacketSendUtility.sendMessage(player, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
				return;
			}
		}
		else {
			PacketSendUtility.sendMessage(player, "syntax //configure <set|show> <configname> <property> [<newvalue>]");
			return;
		}

		Class<?> classToMofify = commands.get(params[1].toLowerCase());

		if (command.equalsIgnoreCase("show")) {
			String fieldName = params[2];
			Field someField;
			try {
				someField = classToMofify.getDeclaredField(fieldName.toUpperCase());
				PacketSendUtility.sendMessage(player, "Current value is " + someField.get(null));
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(player, "Error! Wrong property or value.");
				return;
			}
		}
		else if (command.equalsIgnoreCase("set")) {
			String fieldName = params[2];
			String newValue = params[3];
			if (classToMofify != null) {
				Field someField;
				try {
					someField = classToMofify.getDeclaredField(fieldName.toUpperCase());
					Class<?> classType = someField.getType();
					if (classType == String.class) {
						someField.set(null, newValue);
					}
					else if (classType == int.class || classType == Integer.class) {
						someField.set(null, Integer.parseInt(newValue));
					}
					else if (classType == Boolean.class || classType == boolean.class) {
						someField.set(null, Boolean.valueOf(newValue));
					}
					else if (classType == byte.class || classType == Byte.class) {
						someField.set(null, Byte.valueOf(newValue));
					}
					else if (classType == float.class || classType == Float.class) {
						someField.set(null, Float.valueOf(newValue));
					}

				}
				catch (Exception e) {
					PacketSendUtility.sendMessage(player, "Error! Wrong property or value.");
					return;
				}
			}
			PacketSendUtility.sendMessage(player, "Property changed and applyed");
		}
	}

}
	