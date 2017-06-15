package com.aionemu.gameserver.services.siegeservice;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerInfoContainer{
	private static final Logger log = LoggerFactory.getLogger(PlayerInfoContainer.class);
	public static boolean error = false;
	/*
	 * 	IPs
	 * 		playerName
	 * 			PlayerInfo	
	 */
	public HashMap<String, HashMap<String, PlayerInfo>> _playerInfosByIp = new HashMap<String, HashMap<String , PlayerInfo>>();
	//public static topList

	
	public class PlayerInfo{
		public final String _IP;
		public final String _playerName;	
		public final String _accountName;
		public int _ap = 0;
		public int _kills = 0;
		
		public PlayerInfo(String IP, String playerName, String accountName){
			_IP = IP;
			_playerName = playerName;
			_accountName = accountName;
		}
		
		public void addAP(int ap){
			_ap += ap;
			checkIfInTop(this, _ap);
		}
		
		public void addKill(){
			_kills++;
		}
	}
	
	public static void checkIfInTop(PlayerInfo pi, int ap){
		//checkIn topList
	}
	
	public void updatePlayerInfo(String IP, String playerName, String accountName, int ap){
		if(error)
			return;
		
		try{
		//checkIfAlready Reg and add ap
		for(HashMap<String, PlayerInfo> pis :_playerInfosByIp.values()){//for all ip reg
				PlayerInfo pi = pis.get(playerName);
				if(pi != null){
					pi.addAP(ap);
					return;
				}
		}
		//playerNot reg
		
		PlayerInfo	pi = new PlayerInfo(IP, playerName, accountName);
		pi.addAP(ap);
		
		//checkIf IP already reg
		HashMap<String, PlayerInfo> sameIP = _playerInfosByIp.get(IP);
		
		synchronized (this) {
		if(sameIP == null){
				sameIP = new HashMap<String, PlayerInfo>();
				_playerInfosByIp.put(IP, sameIP);
			}
		}
		sameIP.put(playerName, pi);
		}
		catch(Exception e){
			log.error("[PlayerInfoContainer] PlayerInfoCaintainer has been locked: caused by error");
			e.printStackTrace();
			error = true;
			return;
		}
	}
}
