package com.aionemu.gameserver.command.admin;

import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.OutpostLocation;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.siegeservice.Siege;
import com.aionemu.gameserver.services.siegeservice.SiegeRaceCounter;
import com.aionemu.gameserver.utils.PacketSendUtility;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;



@SuppressWarnings("rawtypes")
public class CmdSiege2 extends BaseCommand {

	private static final String COMMAND_START = "start";
	private static final String COMMAND_STOP = "stop";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_LIST_LOCATIONS = "locations";
	private static final String COMMAND_LIST_SIEGES = "sieges";
	private static final String COMMAND_CAPTURE = "capture";


	public void execute(Player admin, String... params) {

		if (params.length == 0) {
			showHelp(admin);
			return;
		}

		if (COMMAND_STOP.equalsIgnoreCase(params[0]) || COMMAND_START.equalsIgnoreCase(params[0])) {
			handleStartStopSiege(admin, params);
		}
		else if (COMMAND_LIST.equalsIgnoreCase(params[0])) {
			handleList(admin, params);
		}
		else if (COMMAND_LIST_SIEGES.equals(params[0])) {
			listLocations(admin);
		}
		else if (COMMAND_CAPTURE.equals(params[0])) {
			capture(admin, params);
		}
	}

	protected void handleStartStopSiege(Player admin, String... params) {
		if (params.length != 2 || !NumberUtils.isDigits(params[1])) {
			showHelp(admin);
			return;
		}

		int siegeLocId = NumberUtils.toInt(params[1]);
		if (!isValidSiegeLocationId(admin, siegeLocId)) {
			showHelp(admin);
			return;
		}

		if (COMMAND_START.equalsIgnoreCase(params[0])) {
			if (SiegeService.getInstance().isSiegeInProgress(siegeLocId)) {
				PacketSendUtility.sendMessage(admin, "Siege Location " + siegeLocId + " is already under siege");
			}
			else {
				PacketSendUtility.sendMessage(admin, "Siege Location " + siegeLocId + " - starting siege!");
				SiegeService.getInstance().startSiege(siegeLocId);
			}
		}
		else if (COMMAND_STOP.equalsIgnoreCase(params[0])) {
			if (!SiegeService.getInstance().isSiegeInProgress(siegeLocId)) {
				PacketSendUtility.sendMessage(admin, "Siege Location " + siegeLocId + " is not under siege");
			}
			else {
				PacketSendUtility.sendMessage(admin, "Siege Location " + siegeLocId + " - stopping siege!");
				SiegeService.getInstance().stopSiege(siegeLocId);
			}
		}
	}

	protected boolean isValidSiegeLocationId(Player player, int fortressId) {

		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(fortressId)) {
			PacketSendUtility.sendMessage(player, "Id " + fortressId + " is invalid");
			return false;
		}

		return true;
	}

	protected void handleList(Player admin, String[] params) {
		if (params.length != 2) {
			showHelp(admin);
			return;
		}

		if (COMMAND_LIST_LOCATIONS.equalsIgnoreCase(params[1])) {
			listLocations(admin);
		}
		else if (COMMAND_LIST_SIEGES.equalsIgnoreCase(params[1])) {
			listSieges(admin);
		}
		else {
			showHelp(admin);
		}
	}

	protected void listLocations(Player player) {
		for (FortressLocation f : SiegeService.getInstance().getFortresses().values()) {
			PacketSendUtility.sendMessage(player, "Fortress: " + f.getLocationId() + " belongs to " + f.getRace());
		}
		for (OutpostLocation o : SiegeService.getInstance().getOutposts().values()) {
			PacketSendUtility.sendMessage(player, "Outpost: " + o.getLocationId() + " belongs to " + o.getRace());
		}
		for (ArtifactLocation a : SiegeService.getInstance().getStandaloneArtifacts().values()) {
			PacketSendUtility.sendMessage(player, "Artifact: " + a.getLocationId() + " belongs to " + a.getRace());
		}
	}

	protected void listSieges(Player player) {
		for (Integer i : SiegeService.getInstance().getSiegeLocations().keySet()) {
			Siege s = SiegeService.getInstance().getSiege(i);
			if (s != null) {
				int secondsLeft = SiegeService.getInstance().getRemainingSiegeTimeInSeconds(i);
				String minSec = secondsLeft / 60 + "m ";
				minSec += secondsLeft % 60 + "s";
				PacketSendUtility.sendMessage(player, "Location: " + i + ": " + minSec + " left.");
			}
		}
	}

	protected void capture(Player player, String[] params) {
		if (params.length < 3 || !NumberUtils.isNumber(params[1])) {
			showHelp(player);
			return;
		}

		int siegeLocationId = NumberUtils.toInt(params[1]);
		if (!SiegeService.getInstance().getSiegeLocations().keySet().contains(siegeLocationId)) {
			PacketSendUtility.sendMessage(player, "Invalid Siege Location Id: " + siegeLocationId);
			return;
		}

		Siege s = SiegeService.getInstance().getSiege(siegeLocationId);
		if (s == null) {
			PacketSendUtility.sendMessage(player, "Siege Location " + siegeLocationId + " is not under siege.");
			return;
		}

		// check if params2 is siege race
		SiegeRace sr = null;
		try {
			sr = SiegeRace.valueOf(params[2].toUpperCase());
		}
		catch (IllegalArgumentException e) {
			//ignore
		}

		// try to find legion by name
		Legion legion = null;
		if (sr == null) {

			try {
				int legionId = Integer.valueOf(params[2]);
				legion = LegionService.getInstance().getLegion(legionId);
			} catch (NumberFormatException e) {
				String legionName = "";
				for(int i = 2; i < params.length; i++)
					legionName += " " + params[i];
				legion = LegionService.getInstance().getLegion(legionName.trim());
			}

			if (legion != null) {
				List<Player> onlinePlayers = legion.getOnlineLegionMembers();
				if (!GenericValidator.isBlankOrNull(onlinePlayers)) {
					sr = SiegeRace.getByRace(onlinePlayers.get(0).getRace());
				}
				else {
					sr = null; // TODO: how it's possible to get legion race?!
					PacketSendUtility.sendMessage(player, "Temp Hack: At least one legion member must be online. Sorry :(");
					return;
				}
			}
		}

		// check if can capture
		if (legion == null && sr == null) {
			PacketSendUtility.sendMessage(player, params[2] + " is not valid siege race or legion name");
			return;
		}

		// get counter for valid race
		// add big amount of damage
		SiegeRaceCounter src = s.getSiegeCounter().getRaceCounter(sr);
		src.addTotalDamage(Integer.MAX_VALUE);
		if (legion != null) {
			src.addLegionDamage(legion, Integer.MAX_VALUE);
		}

		// kill the boss
		if (!s.isFinished()) {
			SiegeNpc boss = s.getBoss();
			boss.getController().onAttack(player, boss.getLifeStats().getMaxHp() + 1, true);
		}
	}

	protected void showHelp2(Player player) {
		PacketSendUtility.sendMessage(player, "AdminCommand //siege Help");
		PacketSendUtility.sendMessage(player, "//siege start|stop <siegeLocationId>");
		PacketSendUtility.sendMessage(player, "//siege list locations|sieges");
		PacketSendUtility.sendMessage(player, "//siege capture <fortressOrArtifactId> <siegeRaceName|legionName|legionId>");

		java.util.Set<Integer> fortressIds = SiegeService.getInstance().getFortresses().keySet();
		java.util.Set<Integer> artifactIds = SiegeService.getInstance().getStandaloneArtifacts().keySet();
		java.util.Set<Integer> outpostIds = SiegeService.getInstance().getOutposts().keySet();
		PacketSendUtility.sendMessage(player, "Fortress: " + StringUtils.join(fortressIds, ", "));
		PacketSendUtility.sendMessage(player, "Artifacts: " + StringUtils.join(artifactIds, ", "));
		PacketSendUtility.sendMessage(player, "Outposts: " + StringUtils.join(outpostIds, ", "));
	}

}