package com.aionemu.gameserver.command.admin;

import java.util.List;

import javolution.util.FastList;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionRank;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;

import com.aionemu.gameserver.world.World;


public class CmdLegionCommand extends BaseCommand {
	private LegionService service;


	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			showHelp(admin);
			return;
		}
		if (params[0].equalsIgnoreCase("disband")) {
			if (params.length < 2) {
				showHelp(admin);
				return;
			}

			String legionName = getEndString(params, 1);
			Legion legion = service.getLegion(legionName.toLowerCase());
			if (legion == null) {
				PacketSendUtility.sendMessage(admin, "legion "+legionName+" introuvable.");
				return;
			}

			service.disbandLegion(legion);
			PacketSendUtility.sendMessage(admin, "legion "+legion.getLegionName()+" was disbanded.");
		}
		else if (params[0].equalsIgnoreCase("setlevel")) {
			if (params.length < 3) {
				showHelp(admin);
				return;
			}

			String legionName = getEndString(params, 1, 1);
			Legion legion = service.getLegion(legionName.toLowerCase());
			if (legion == null) {
				PacketSendUtility.sendMessage(admin, "legion "+legionName+" introuvable.");
				return;
			}

			byte level = ParseByte(params[params.length -1]);
			if (level < 1 || level > 5) {
				PacketSendUtility.sendMessage(admin, "1-5 legion level is allowed.");
				return;
			}
			else if (level == legion.getLegionLevel()) {
				PacketSendUtility.sendMessage(admin, "legion "+legion.getLegionName()+" is already with that level.");
				return;
			}

			int old = legion.getLegionLevel();
			service.changeLevel(legion, level, true);
			PacketSendUtility.sendMessage(admin, "legion "+legion.getLegionName()+" has raised from "+old+" to "+level+" level.");
		}
		else if (params[0].equalsIgnoreCase("setpoints")) {
			if (params.length < 3) {
				showHelp(admin);
				return;
			}

			String legionName = getEndString(params, 1, 1);
			Legion legion = service.getLegion(legionName.toLowerCase());
			if (legion == null) {
				PacketSendUtility.sendMessage(admin, "legion "+legionName+" introuvable.");
				return;
			}

			int points = ParseInteger(params[params.length -1]);
			if (points < 1 || points > Integer.MAX_VALUE) {
				PacketSendUtility.sendMessage(admin, "1-2.1bil points allowed.");
				return;
			}

			int old = legion.getContributionPoints();
			service.setContributionPoints(legion, points, true);
			PacketSendUtility.sendMessage(admin, "legion "+legion.getLegionName()+" has raised from "+old+" to "+points+" contributiong points.");
		}
		else if (params[0].equalsIgnoreCase("setname")) {
			if (params.length < 2) {
				showHelp(admin);
				return;
			}

			Player player = AutoTarget(admin);
			Legion legion = player.getLegion();
			if (legion == null) {
				PacketSendUtility.sendMessage(admin, "Ce joueur n'a pas de legion");
				return;
			}

			String NewlegionName = getEndString(params, 1);
			if (!service.isValidName(NewlegionName)) {
				PacketSendUtility.sendMessage(admin, NewlegionName+" is incorrect for legion name!");
				return;
			}

			String old = legion.getLegionName();
			service.setLegionName(legion, NewlegionName, true);
			PacketSendUtility.sendMessage(admin, "legion "+old+" has changed name from "+old+" to "+NewlegionName+".");
		}
		else if (params[0].equalsIgnoreCase("info")) {
			if (params.length < 2) {
				showHelp(admin);
				return;
			}

			String legionName = getEndString(params, 1);
			Legion legion = service.getLegion(legionName.toLowerCase());
			if (legion == null) {
				PacketSendUtility.sendMessage(admin, "legion "+legionName+" introuvable.");
				return;
			}

			FastList<String> message = FastList.newInstance(), online = FastList.newInstance(), offline = FastList.newInstance();
			message.add("name: "+legion.getLegionName());
			message.add("contrib points: "+legion.getContributionPoints());
			message.add("level: "+legion.getLegionLevel());
			message.add("id: "+legion.getLegionId());
			List<Integer> members = legion.getLegionMembers();
			message.add("members: "+members.size());

			PlayerDAO dao = null;
			for(int memberId : members) {
				Player pl = World.getInstance().findPlayer(memberId);
				if(pl != null)
					online.add(pl.getName()+" (lv"+pl.getLevel()+") classId "+pl.getPlayerClass().getClassId());
				else {
					if(dao == null)
						dao = DAOManager.getDAO(PlayerDAO.class);

					PlayerCommonData pcd = dao.loadPlayerCommonData(memberId);
					offline.add(pcd.getName()+" (lv"+pcd.getLevel()+") classId "+pcd.getPlayerClass().getClassId());
				}
			}

			message.add("--ONLINE-------- "+online.size());
			message.addAll(online);
			FastList.recycle(online);
			message.add("--OFFLINE-------- "+offline.size());
			message.addAll(offline);
			FastList.recycle(offline);

			for(String msg : message)
				PacketSendUtility.sendMessage(admin, msg);

			FastList.recycle(message);
		}
		else if (params[0].equalsIgnoreCase("kick")) {
			Player player = null;

			if (params.length == 2) {
				player = World.getInstance().findPlayer(Util.convertName(params[1]));
				if (player == null) {
					PacketSendUtility.sendMessage(admin, "joueur "+params[1]+" Introuvable.");
					return;
				}
			}
			else
				player = AutoTarget(admin);

			if (player.getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL) {
				PacketSendUtility.sendMessage(admin, "player "+player.getName()+" is a brigade general. Disband legion!");
				return;
			}

			if (service.removePlayerFromLegionAsItself(player))
				PacketSendUtility.sendMessage(admin, "player "+player.getName()+" was kicked from legion.");
			else
				PacketSendUtility.sendMessage(admin, "You have failed to kick player "+player.getName()+" from legion.");
		}
		else if (params[0].equalsIgnoreCase("invite")) {
			if (params.length < 3) {
				showHelp(admin);
				return ;
			}

			String legionName = getEndString(params, 1, 1);
			Legion legion = service.getLegion(legionName.toLowerCase());
			if (legion == null)
				return;

			Player target = World.getInstance().findPlayer(Util.convertName(params[params.length - 1]));
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "player "+params[params.length - 1]+" not exists.");
				return;
			}

			if (target.isLegionMember()) {
				PacketSendUtility.sendMessage(admin, "player "+target.getName()+" is a already member of "+target.getLegion().getLegionName()+"!");
				return;
			}

			if (service.directAddPlayer(legion, target)) 
				PacketSendUtility.sendMessage(admin, "player "+target.getName()+" was added to "+legion.getLegionName());
			else
				PacketSendUtility.sendMessage(admin, "probably legion "+legion.getLegionName()+" is full");
		}
		else if (params[0].equalsIgnoreCase("bg")) {
			if (params.length < 3) {
				showHelp(admin);
				return ;
			}

			String legionName = getEndString(params, 1, 1);
			Legion legion = service.getLegion(legionName.toLowerCase());
			if (legion == null)
				return;

			Player target = World.getInstance().findPlayer(Util.convertName(params[params.length - 1]));
			if (target == null) {
				PacketSendUtility.sendMessage(admin, "player "+params[params.length - 1]+" not exists.");
				return;
			}

			if (!legion.isMember(target.getObjectId())) {
				PacketSendUtility.sendMessage(admin, "player "+target.getName()+" is not a member of "+legion.getLegionName()+", invite them!");
				return;
			}

			List<Integer> members = legion.getLegionMembers();
			Player bgplayer = null;
			for(int memberId : members) {
				Player pl = World.getInstance().findPlayer(memberId);
				if(pl != null) {
					if(pl.getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL) {
						bgplayer = pl;
						break;
					}
				}
			}

			if (bgplayer == null) {
				PacketSendUtility.sendMessage(admin, "You can't assign a new general while old is offline.");
				return;
			}

			bgplayer.getLegionMember().setRank(LegionRank.LEGIONARY);
			PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(bgplayer, 0, ""));
			PacketSendUtility.sendMessage(admin, "You have sucessfully demoted " + bgplayer.getName() + " to Legionary rank.");
			target.getLegionMember().setRank(LegionRank.BRIGADE_GENERAL);
			PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
			PacketSendUtility.sendMessage(admin, "You have sucessfully promoted " + target.getName() + " to BG rank.");
		}
		else if (params[0].equalsIgnoreCase("help"))
			showHelp(admin);
		else if (params[0].equalsIgnoreCase("setrank")) {
			if (params.length != 3) {
				showHelp(admin);
				return;
			}

			Player target = World.getInstance().findPlayer(Util.convertName(params[1]));
			if(target == null) {
				PacketSendUtility.sendMessage(admin, "player "+params[1]+" not exists.");
				return;
			}

			if (!target.isLegionMember()) {
				PacketSendUtility.sendMessage(admin, "player "+target.getName()+" is not a member of legion.");
				return;
			}

			if (params[2].equalsIgnoreCase("centurion")) {
				target.getLegionMember().setRank(LegionRank.CENTURION);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(admin, "you have promoted player " + target.getName() + " as centurion.");
			}
			else if(params[2].equalsIgnoreCase("deputy")) {
				target.getLegionMember().setRank(LegionRank.DEPUTY);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(admin, "you have promoted player " + target.getName() + " as deputy.");
			}
			else if(params[2].equalsIgnoreCase("legionary")) {
				target.getLegionMember().setRank(LegionRank.LEGIONARY);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(admin, "you have promoted player " + target.getName() + " as legionary.");
			}
			else if(params[2].equalsIgnoreCase("volunteer")) {
				target.getLegionMember().setRank(LegionRank.VOLUNTEER);
				PacketSendUtility.broadcastPacketToLegion(target.getLegion(), new SM_LEGION_UPDATE_MEMBER(target, 0, ""));
				PacketSendUtility.sendMessage(admin, "you have promoted player " + target.getName() + " as volunteer.");
			}
			else
				PacketSendUtility.sendMessage(admin, "rank " + params[2] + " is not supported.");
		}
	}
}
