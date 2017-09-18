package com.aionemu.gameserver.services.drop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.custom.CustomDrop;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.team2.common.service.PlayerTeamDistributionService;
import com.aionemu.gameserver.model.team2.common.service.PlayerTeamDistributionService.PlayerGroupRewardStats;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import com.aionemu.gameserver.services.TranslationService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ferosia
 * @author Lelouch
 */

public class DropShopPoint {

	private static final Logger log = LoggerFactory.getLogger(DropShopPoint.class);
	private int luck = 0;
	private int min = 0;
	private int max = 0;
	private int amount = 0;
	private Npc npc = null;
	
	public DropShopPoint(Npc npc){
		this.npc = npc;
	}
	
	public void callNpcShopPointRewardForPlayer() {
		AionObject winner = npc.getAggroList().getMostDamage();
		Player player = null;
		PlayerGroup pg = null;
		Collection<Player> listPlayer = new ArrayList<Player>();
		
		// If killed NPC is not in custom shop point npcs list
		if(!makeCalculations(npc)) {
			return;
		}
		
		if (winner instanceof PlayerGroup) {
			pg = (PlayerGroup) winner;
			player = pg.getLeaderObject();
			listPlayer = pg.getMembers();
		}
		else if (winner instanceof Player) {
			player = ((Player) winner);
			listPlayer.add(player);
		}

		if(player == null) {
			//TODO LOG ? Player Message ?
			return;
		}

		boolean stopHL = false;
		for (Player member : listPlayer) {
			if(member.getLevel() - npc.getLevel() >= 10) {
				stopHL = true;
				break;
			}
		}
		if(stopHL) {
			for (Player member : listPlayer) {
				// Level of at least one of your teammates is too high for winning Shop Point.
				String message = TranslationService.DSP_ERROR_LEVEL_GROUP.toString(member);
				if(pg == null){
					// Your level is too high for winning Shop Point
					message = TranslationService.DSP_ERROR_LEVEL_PLAYER.toString(player);
				}
				sendCommandMessage(member, message);
			}
			return;
		}

		/*if (player.getLevel() - npc.getLevel() >= 10) {
			// Your level is too high for winning Shop Point
			sendCommandMessage(player, message);
			return;
		}*/
				
		if(!willGetShopPoint()) {
			for (Player member : listPlayer) {
				// Too bad ! You didn't have chance to win Shop Point on this NPC
				String message = TranslationService.DSP_NO_LUCK_NO_WIN.toString(member);
				sendCommandMessage(member, message);
			}
			return;
		}
		
		amount = calculateQty();
		if(amount <= 0) {
			return;
		}
		
		if(pg == null) {
			addShopPoint(player, amount);
			log.info("Player " + player.getName() + " has win " + amount + 
					" shop point from NPC " + npc.getName() + " (ID " + npc.getNpcId() + ")");
			/*
			// An error has occured when adding your Shop Point. Please contact administrator
			String message = CommandsString.DSP_ERROR_ADD_ERROR.toString(player);
			sendCommandMessage(player, message);
			*/
		}
		else {
			callNpcShopPointRewardForGroup(pg, npc);
		}
	}
	
	private int calculateQty() {
		return (int)( Math.random()*( max - min + 1 ) ) + min;
	}

	private boolean willGetShopPoint() {
		int success = Rnd.get(100);
		return luck > success;
	}
	
	private void addShopPoint(Player player, int amount) {
		int tolls = player.getClientConnection().getAccount().getToll();
		player.getClientConnection().getAccount().setToll(tolls + amount);
        PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(tolls + amount));
        InGameShopEn.getInstance().addToll(player, amount);
        
        // Congratulations ! By killing this NPC, you win %s Shop Points
        String message = TranslationService.DSP_YOU_WIN.toString(player, String.valueOf(amount));
		sendCommandMessage(player, message);
	}
	
	private boolean makeCalculations(Npc npc) {
		if (npc == null) {
			return false;
		}
		
		int npcTemplateId = npc.getNpcId();
		
		String[] npcsShopPoint = CustomDrop.getNpcShopPoint();
		if(!Arrays.asList(npcsShopPoint).contains(String.valueOf(npcTemplateId))) {
			luck = 0;
			max = 0;
			min = 0;
			return false;
		}
		
		if(CustomDrop.SHOPPOINTS_FIXED_REWARD) {
			luck = CustomDrop.SHOPPOINTS_FIXED_REWARD_LUCK;
			max = CustomDrop.SHOPPOINTS_FIXED_REWARD_MAX;
			min = CustomDrop.SHOPPOINTS_FIXED_REWARD_MIN;
			return true;
		}
		
		NpcTemplate npcTemplate = npc.getObjectTemplate();
		NpcRating npcRating = npcTemplate.getRating();
		
		switch (npcRating) {
			case LEGENDARY:
				luck = CustomDrop.SHOPPOINTS_LEGENDARY_LUCK;
				max = CustomDrop.SHOPPOINTS_LEGENDARY_MAX;
				min = CustomDrop.SHOPPOINTS_LEGENDARY_MIN;
				break;
			case HERO:
				luck = CustomDrop.SHOPPOINTS_HERO_LUCK;
				max = CustomDrop.SHOPPOINTS_HERO_MAX;
				min = CustomDrop.SHOPPOINTS_HERO_MIN;
				break;
			case ELITE:
				luck = CustomDrop.SHOPPOINTS_ELITE_LUCK;
				max = CustomDrop.SHOPPOINTS_ELITE_MAX;
				min = CustomDrop.SHOPPOINTS_ELITE_MIN;
				break;
			case NORMAL:
			case JUNK:
			default:
				luck = CustomDrop.SHOPPOINTS_NORMAL_LUCK;
				max = CustomDrop.SHOPPOINTS_NORMAL_MAX;
				min = CustomDrop.SHOPPOINTS_NORMAL_MIN;
				break;
		}
		
		return true;
	}
	
	private void callNpcShopPointRewardForGroup(PlayerGroup playerGroup, Npc npc) {
		if (playerGroup == null || npc == null)
			return;
		// Find Group Members and Determine Highest Level
		PlayerGroupRewardStats filteredStats = new PlayerGroupRewardStats(npc);
		playerGroup.applyOnMembers(filteredStats);
		// All are dead or not nearby.
		if (filteredStats.players.size() == 0 || !PlayerTeamDistributionService.hasOneLivingPlayer(playerGroup))
			return;
		
		for (Player member : filteredStats.players) {
			// Players 10 levels below highest member wont get Shop Point.
			if (filteredStats.highestLevel - member.getLevel() >= 10) {
				continue;
			}
			
			addShopPoint(member, amount);
		}
	}
	
	private void sendCommandMessage(Player player, String message) {
		PacketSendUtility.sendBrightYellowMessage(player, message);
	}
}