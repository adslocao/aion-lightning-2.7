package com.aionemu.gameserver.command.admin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.CreatureLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_EXP;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;


public class CmdHeal extends BaseCommand {


	public void execute(Player player, String... params) {
		VisibleObject target = player.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(player, "No target selected");
			return;
		}
		if (!(target instanceof Creature)) {
			PacketSendUtility.sendMessage(player, "Target has to be Creature!");
			return;
		}

		Creature creature = (Creature) target;

		if (params.length == 0) {
			creature.getLifeStats().increaseHp(TYPE.HP, creature.getLifeStats().getMaxHp() + 1);
			creature.getLifeStats().increaseMp(TYPE.MP, creature.getLifeStats().getMaxMp() + 1);
			creature.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
			PacketSendUtility.sendMessage(player, creature.getName() + " has been refreshed !");
		}
		else if (params[0].equals("dp") && creature instanceof Player) {
			Player targetPlayer = (Player) creature;
			targetPlayer.getCommonData().setDp(targetPlayer.getGameStats().getMaxDp().getCurrent());
			PacketSendUtility.sendMessage(player, targetPlayer.getName() + " is now full of DP !");
		}
		else if (params[0].equals("fp") && creature instanceof Player) {
			Player targetPlayer = (Player) creature;
			targetPlayer.getLifeStats().setCurrentFp(targetPlayer.getLifeStats().getMaxFp());
			PacketSendUtility.sendMessage(player, targetPlayer.getName() + " FP has been fully refreshed !");
		}
		else if (params[0].equals("repose") && creature instanceof Player) {
			Player targetPlayer = (Player) creature;
			PlayerCommonData pcd = targetPlayer.getCommonData();
			pcd.setCurrentReposteEnergy(pcd.getMaxReposteEnergy());
			PacketSendUtility.sendMessage(player, targetPlayer.getName() + " Reposte Energy has been fully refreshed !");
			PacketSendUtility.sendPacket(targetPlayer,
					new SM_STATUPDATE_EXP(pcd.getExpShown(), pcd.getExpRecoverable(), pcd.getExpNeed(), pcd
							.getCurrentReposteEnergy(), pcd.getMaxReposteEnergy()));
		}
		else if (params.length ==3) {
			int hp;
			try {
				String percent = params[2];
				CreatureLifeStats<?> cls = creature.getLifeStats();
				Pattern heal = Pattern.compile("([^%]+)%");
				Matcher result = heal.matcher(percent);
				int value;

				if (result.find()) {
					hp = Integer.parseInt(result.group(1));

					if (hp < 100)
						value = (int) (hp / 100f * cls.getMaxHp());
					else
						value = cls.getMaxHp();
				}
				else
					value = ParseInteger(params[2]);
				cls.increaseHp(TYPE.HP, value);
				PacketSendUtility.sendMessage(player, creature.getName() + " has been healed for " + value +" health points!");
			}
			catch (Exception ex) {
				showHelp(player);
			}
		}
	}
}