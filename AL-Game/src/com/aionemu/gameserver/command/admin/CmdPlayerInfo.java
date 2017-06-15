package com.aionemu.gameserver.command.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.utils.ChatUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;
import com.google.common.base.Predicate;

/*syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars>*/


public class CmdPlayerInfo extends BaseCommand {

	


	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars> ");
			return;
		}

		Player target = World.getInstance().findPlayer(Util.convertName(params[0]));

		if (target == null) {
			PacketSendUtility.sendMessage(admin, Util.convertName(params[0]) + " n'est pas connect\u00E9.");
			return;
		}

		PacketSendUtility.sendMessage(
			admin,
			"\n[Infos sur " + target.getName() + "]\n- Common : niveau " + target.getLevel() + "("
				+ target.getCommonData().getExpShown() + " xp), " + target.getRace() + ", "
				+ target.getPlayerClass() + "\n- IP: " + target.getClientConnection().getIP() + "\n- MAC: "
				+ target.getClientConnection().getMacAddress() + "\n" + "- Nom de compte : "
				+ target.getClientConnection().getAccount().getName() + "\n");

		if (params.length < 2)
			return;

		if (params[1].equals("item")) {
			StringBuilder strbld = new StringBuilder("- Items dans l'inventaire :\n");

			List<Item> items = target.getInventory().getItemsWithKinah();
			Iterator<Item> it = items.iterator();

			if (items.isEmpty())
				strbld.append("Aucun\n");
			else
				while (it.hasNext()) {

					Item act = it.next();
					strbld.append("    " + act.getItemCount() + " x " + ChatUtil.item(act.getItemTemplate().getTemplateId())
						+ "\n");
				}
			items.clear();
			items = target.getEquipment().getEquippedItems();
			it = items.iterator();
			strbld.append("- Items \u00E9quipp\u00E9s :\n");
			if (items.isEmpty())
				strbld.append("Aucun\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + " x " + ChatUtil.item(act.getItemTemplate().getTemplateId())
						+ "\n");
				}

			items.clear();
			items = target.getWarehouse().getItemsWithKinah();
			it = items.iterator();
			strbld.append("- Items en entrep\u00F4t :\n");
			if (items.isEmpty())
				strbld.append("Aucun\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + " x " + "[item:" + act.getItemTemplate().getTemplateId()
						+ "]" + "\n");
				}
			
			IStorage pet6 = target.getStorage(StorageType.PET_BAG_6.getId());
			IStorage pet12 = target.getStorage(StorageType.PET_BAG_12.getId());
			IStorage pet18 = target.getStorage(StorageType.PET_BAG_18.getId());
			IStorage pet24 = target.getStorage(StorageType.PET_BAG_24.getId());
			
			items.clear();
			if (pet6 != null) {
				items = pet6.getItems();
			}
			it = items.iterator();
			strbld.append("- Items en familier 6 slots :\n");
			if (items.isEmpty())
				strbld.append("Aucun\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + " x " + "[item:" + act.getItemTemplate().getTemplateId()
						+ "]" + "\n");
				}
			
			items.clear();
			if (pet12 != null) {
				items = pet12.getItems();
			}
			it = items.iterator();
			strbld.append("- Items en familier 12 slots :\n");
			if (items.isEmpty())
				strbld.append("Aucun\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + " x " + "[item:" + act.getItemTemplate().getTemplateId()
						+ "]" + "\n");
				}
			
			items.clear();
			if (pet18 != null) {
				items = pet18.getItems();
			}
			it = items.iterator();
			strbld.append("- Items en familier 18 slots :\n");
			if (items.isEmpty())
				strbld.append("Aucun\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + " x " + "[item:" + act.getItemTemplate().getTemplateId()
						+ "]" + "\n");
				}
			
			items.clear();
			if (pet24 != null) {
				items = pet24.getItems();
			}
			it = items.iterator();
			strbld.append("- Items en familier 24 slots :\n");
			if (items.isEmpty())
				strbld.append("Aucun\n");
			else
				while (it.hasNext()) {
					Item act = it.next();
					strbld.append("    " + act.getItemCount() + " x " + "[item:" + act.getItemTemplate().getTemplateId()
						+ "]" + "\n");
				}
			
			showAllLines(admin, strbld.toString());
		}
		else if (params[1].equals("group")) {
			final StringBuilder strbld = new StringBuilder("-group info:\n   Leader: ");

				PlayerGroup group = target.getPlayerGroup2();
				if (group == null)
					PacketSendUtility.sendMessage(admin, "- Group info: Pas dans un groupe");
				else {
					strbld.append(group.getLeader().getName() + "\n  Membres:\n");
					group.applyOnMembers(new Predicate<Player>() {

						@Override
						public boolean apply(Player player) {
							strbld.append("    " + player.getName() + "\n");
							return true;
						}

					});
					PacketSendUtility.sendMessage(admin, strbld.toString());
				}
			
		}
		else if (params[1].equals("skill")) {
			StringBuilder strbld = new StringBuilder("- List des skills:\n");

			PlayerSkillEntry sle[] = target.getSkillList().getAllSkills();

			for (int i = 0; i < sle.length; i++)
				strbld.append("#" + sle[i].getSkillId() + " : " + sle[i].getSkillName() + " au level " + sle[i].getSkillLevel() + "\n");
			showAllLines(admin, strbld.toString());
		}
		else if (params[1].equals("loc")) {
			String chatLink = ChatUtil.position(target.getName(), target.getPosition());
			PacketSendUtility.sendMessage(
				admin,
				"- " + chatLink + "'s location:\n  mapid: " + target.getWorldId() + "\n  X: " + target.getX() + " Y: "
					+ target.getY() + "Z: " + target.getZ() + "heading: " + target.getHeading());
		}
		else if (params[1].equals("legion")) {
			StringBuilder strbld = new StringBuilder();

			Legion legion = target.getLegion();
			if (legion == null)
				PacketSendUtility.sendMessage(admin, "-legion info: no legion");
			else {
				ArrayList<LegionMemberEx> legionmemblist = LegionService.getInstance().loadLegionMemberExList(legion);
				Iterator<LegionMemberEx> it = legionmemblist.iterator();

				strbld.append("-legion info:\n  name: " + legion.getLegionName() + ", level: " + legion.getLegionLevel()
					+ "\n  members(online):\n");
				while (it.hasNext()) {
					LegionMemberEx act = it.next();
					strbld.append("    " + act.getName() + "(" + ((act.isOnline() == true) ? "online" : "offline") + ")"
						+ act.getRank().toString() + "\n");
				}
			}
			showAllLines(admin, strbld.toString());
		}
		else if(params[1].equals("ap"))	{
			PacketSendUtility.sendMessage(admin, "AP info about " + target.getName());
			PacketSendUtility.sendMessage(admin, "Total AP = " + target.getAbyssRank().getAp());
			PacketSendUtility.sendMessage(admin, "Total Kills = " + target.getAbyssRank().getAllKill());
			PacketSendUtility.sendMessage(admin, "Today Kills = " + target.getAbyssRank().getDailyKill());
			PacketSendUtility.sendMessage(admin, "Today AP = " + target.getAbyssRank().getDailyAP());
		}
		else if(params[1].equals("chars")) {
			PacketSendUtility.sendMessage(admin, "Autres personnages de " + target.getName() + " (" + target.getClientConnection().getAccount().size() + ") :");

			Iterator<PlayerAccountData> data = target.getClientConnection().getAccount().iterator();
			while(data.hasNext()) {
				PlayerAccountData d = data.next();
				if(d != null && d.getPlayerCommonData() != null) {
					PacketSendUtility.sendMessage(admin, d.getPlayerCommonData().getName());
				}
			}
		}
		else {
			PacketSendUtility.sendMessage(admin, "bad switch!");
			PacketSendUtility.sendMessage(admin, "syntax //playerinfo <playername> <loc | item | group | skill | legion | ap | chars> ");
		}
	}

	private void showAllLines(Player admin, String str) {
		int index = 0;
		String[] strarray = str.split("\n");

		while (index < strarray.length - 20) {
			StringBuilder strbld = new StringBuilder();
			for (int i = 0; i < 20; i++, index++) {
				strbld.append(strarray[index]);
				if (i < 20 - 1)
					strbld.append("\n");
			}
			PacketSendUtility.sendMessage(admin, strbld.toString());
		}
		int odd = strarray.length - index;
		StringBuilder strbld = new StringBuilder();
		for (int i = 0; i < odd; i++, index++)
			strbld.append(strarray[index] + "\n");
		PacketSendUtility.sendMessage(admin, strbld.toString());
	}

}