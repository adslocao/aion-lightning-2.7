package ai.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import com.aionemu.gameserver.services.TranslationService;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ferosia
 *
 */
@AIName("recharger")
public class RechargerAI2 extends NpcAI2 {
		
	@Override
    protected void handleDialogStart(final Player player) {
		
		// HP + MP
		player.getLifeStats().increaseHp(TYPE.HP, player.getLifeStats().getMaxHp() + 1);
		player.getLifeStats().increaseMp(TYPE.MP, player.getLifeStats().getMaxMp() + 1);
		String message = TranslationService.RECHARGER_LIFE.toString(player);
		sendCommandMessage(player, message);
		
		// Removing dots + abnormal effects
		player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.SPEC2);
		message = TranslationService.RECHARGER_DOTS.toString(player);
		sendCommandMessage(player, message);
		
		/*
		// DP
		player.getCommonData().setDp(player.getGameStats().getMaxDp().getCurrent());
		String message = TranslationService.RECHARGER_DP.toString(player);
		sendCommandMessage(player, message);
		*/
		
		// Fly Points
		player.getLifeStats().setCurrentFp(player.getLifeStats().getMaxFp());
		message = TranslationService.RECHARGER_FLY.toString(player);
		sendCommandMessage(player, message);
		
		// Remove skills Cooldown
		List<Integer> delayIds = new ArrayList<Integer>();
		if (player.getSkillCoolDowns() != null) {
			for (Entry<Integer, Long> en : player.getSkillCoolDowns().entrySet())
				delayIds.add(en.getKey());

			for (Integer delayId : delayIds)
				player.setSkillCoolDown(delayId, 0);

			delayIds.clear();
			PacketSendUtility.sendPacket(player, new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));
			message = TranslationService.RECHARGER_SKILL.toString(player);
			sendCommandMessage(player, message);
		}
		
		message = TranslationService.RECHARGER_FIGHT.toString(player, player.getName());
		PacketSendUtility.broadcastPacket(player, new SM_MESSAGE(getObjectId(), getName(),
				message, ChatType.NORMAL), true);
	}
	
	protected void sendCommandMessage(Player player, String message) {
		PacketSendUtility.sendBrightYellowMessage(player, message);
	}
	
}