package com.aionemu.gameserver.utils.audit;

import java.util.Collection;
import java.util.Map;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.player.GmConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import javolution.util.FastMap;

public class GMService {
	public static final GMService getInstance() {
		return SingletonHolder.instance;
	}
	
	private Map<Integer, Player> gms = new FastMap<Integer, Player>();
	private GMService() { }
	
	public Collection<Player> getGMs(){
		return gms.values();
	}
	
	public void onPlayerLogin(Player player){
		if (player.isGM()){
			gms.put(player.getObjectId(), player);
			
			int requiredLevel = 0;
			
			if (player.getCommonData().getGmConfig(GmConfig.GM_ONGMLOGIN_OFF));
				requiredLevel = player.getAccessLevel();
			
			broadcastMessage(player.getFullName()+ " logged in.", requiredLevel);
			
			if (player.getAccessLevel() <= AdminConfig.ANNOUNCE_FORCE_LEVEL)
				broadcastMessagePlayer(player.getFullName()+ " logged in.");
		}
	}
	
	public void onPlayerLogedOut(Player player){
		gms.remove(player.getObjectId());
	}
	
	public void broadcastMessage(String message, int requiredLevel){
		SM_MESSAGE packet = new SM_MESSAGE(0, null, message, ChatType.YELLOW);
		for (Player player : gms.values()) {
			if (player.getAccessLevel() >= requiredLevel)
				PacketSendUtility.sendPacket(player, packet);
		}
	}
	
	public void broadcastMessagePlayer(String message) {
		SM_MESSAGE packet = new SM_MESSAGE(0, null, message, ChatType.YELLOW);
		for (Player player : World.getInstance().getAllPlayers()) {
			if (!player.isGM())
				PacketSendUtility.sendPacket(player, packet);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final GMService instance = new GMService();
	}
}
