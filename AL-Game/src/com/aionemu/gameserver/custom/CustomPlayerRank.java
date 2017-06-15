package com.aionemu.gameserver.custom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CustomPlayerRank {
	public static final int NB_PTS_BY_RANK = 100;
	private static final int MAX_RANK = 10;
	private static final HashMap<Integer, BossInfo> BOSS_PTS = new HashMap<Integer, BossInfo>();
	public static final int[][] COIN_ID  = {
		//  FER      BRONZE      ARGENT      OR      PLATINE      Mithril
		{186000001, 186000002, 186000003, 186000004, 186000005, 186000018},// Ely
		{186000006, 186000007, 186000008, 186000009, 186000010, 186000019} // Asmo
	};

	private final Player _player;
	
	static{
		//BOSS_PTS.put(216238, new BossInfo(10, 0));//test
		
		//Rank 1 Taloc // 100 points
		BOSS_PTS.put(215488, new BossInfo(100, 1)); //Celestius
		
		//Rank 2 Udas sup + inf fusionnees // 100 points
		BOSS_PTS.put(215794, new BossInfo(10, 2)); //Tete d enclume
		BOSS_PTS.put(215792, new BossInfo(10, 2)); //Octron
		BOSS_PTS.put(215784, new BossInfo(10, 2)); //Chura Deux-lames
		BOSS_PTS.put(215797, new BossInfo(10, 2)); //Bergrisar
		BOSS_PTS.put(215795, new BossInfo(20, 2)); //Maitre forgeron Debilkarim
		BOSS_PTS.put(215782, new BossInfo(10, 2)); //Vallakhan
		BOSS_PTS.put(215793, new BossInfo(10, 2)); //Anurakti le Devoue
		BOSS_PTS.put(215783, new BossInfo(20, 2)); //Nexus
		
		//Rank 3 : Esoterasse* // 100 points
		BOSS_PTS.put(217185, new BossInfo(30, 3)); //Dalia Terre-Noire
		BOSS_PTS.put(217195, new BossInfo(30, 3)); //Capitaine Murugan
		BOSS_PTS.put(217205, new BossInfo(10, 3)); //Prototype de Kexkra
		BOSS_PTS.put(217206, new BossInfo(30, 3)); //Gouvernant Surama

		//Rank 4 : Besh // 100 points
		//Chemin principal (= 70 points)
		BOSS_PTS.put(216182, new BossInfo(8, 4)); //Isbariya
		BOSS_PTS.put(216183, new BossInfo(14, 4)); //Aile-Ouragan
		BOSS_PTS.put(216169, new BossInfo(8, 4)); //Dorakiki l Audacieux
		BOSS_PTS.put(216165, new BossInfo(8, 4)); //Virhana
		BOSS_PTS.put(216167, new BossInfo(8, 4)); //Taros Mort-Fleau
		BOSS_PTS.put(216157, new BossInfo(8, 4)); //Capitaine Lakhara
		BOSS_PTS.put(216158, new BossInfo(8, 4)); //Ahbana la Mauvaise
		BOSS_PTS.put(216164, new BossInfo(8, 4)); //Macunbello
		//Chemin additionel (= 30 points)
		BOSS_PTS.put(216162, new BossInfo(5, 4)); //Vehala le maudit
		BOSS_PTS.put(216163, new BossInfo(5, 4)); //Porte peste
		BOSS_PTS.put(216166, new BossInfo(5, 4)); //Crepuscule
		BOSS_PTS.put(216159, new BossInfo(5, 4)); //Le convoqueur d ames
		BOSS_PTS.put(216160, new BossInfo(5, 4)); //Manadar
		BOSS_PTS.put(216525, new BossInfo(5, 4)); //Thurzon le non-mort

		//Abysses confinees // 100 points
		BOSS_PTS.put(216951, new BossInfo(25, 5)); //Pazuzu
		BOSS_PTS.put(216950, new BossInfo(25, 5)); //Kaluva le Quatrieme fragment
		BOSS_PTS.put(282010, new BossInfo(20, 5)); //Diurnespectre
		BOSS_PTS.put(216960, new BossInfo(30, 5)); //Yamenes furieux

		//Rank 5 : Antre de Padmarashka (boss = Ragnarok**)  // 100 points
		BOSS_PTS.put(216576, new BossInfo(100, 6)); //Ragnarok
	}
	

	public CustomPlayerRank(Player player){
		_player = player;
	}
	
	private static final Set<Integer> BOSS_LIST = BOSS_PTS.keySet();
	
	private static final Integer[] ELITE_LIST = {
		215488//Celestius
		};
	
	private int _currentRank = 1;
	private int _currentPts = 0;
	
	//        INIT
	///////////////////////////////////
	public void setRank(int rank){
		_currentRank = rank;
	}
	public void setPts(int pts){
		_currentPts = pts;
	}
	///////////////////////////////////
	
	public void addPts(int count){
		_currentPts += count;
		checkLvUp();
	}
	
	public void addCoin(int count){
		addCoin(count, _currentRank);
	}
	
	public void addCoin(int count, int rank){
		final int[] coins = COIN_ID[_player.getRace().getRaceId()];
		if(rank < 1 || rank > coins.length )
			rank = coins.length;
		
		ItemService.addItem(_player, coins[rank-1], count);
	}
	
	public boolean consummeCoin(int count, int rank){
		final int[] coins = COIN_ID[_player.getRace().getRaceId()];
		if(rank < 1 || rank > coins.length ){
			PacketSendUtility.sendMessage(_player, "Invalid ItemRank");
			return false;
		}
			
		if(_player.getInventory().isFull()){
			PacketSendUtility.sendMessage(_player, "Your inventory is full");
			return false;
		}
		if(_player.getInventory().getItemCountByItemId(coins[rank-1]) < count)
			return false;
		else{
			_player.getInventory().decreaseByItemId(coins[rank-1], count);
			return true;
		}
		
	}
	
	public int getRank(){
		return _currentRank;
	}
	
	public int getPoints(){
		return _currentPts;
	}
	
	public void checkLvUp(){
		if(_currentRank >= MAX_RANK ||_currentPts < NB_PTS_BY_RANK)
			return;
		_currentRank++;
		_currentPts = 0;
		PacketSendUtility.broadcastPacket(_player, new SM_LEVEL_UPDATE(_player.getObjectId(), 0, _player.getLevel()), true);
		PacketSendUtility.sendMessage(_player, "Rank up !");
		//PacketSendUtility.broadcastPacket(_player, new SM_PLAYER_INFO(_player, true), true);
	}
	
	
	
	public static synchronized void onMonsterKill(Creature creature){
		if(creature instanceof Npc){
			Npc npc = (Npc)creature;
			if(containElite(npc.getNpcId()) || npc.isBoss() && BOSS_LIST.contains(npc.getNpcId())){//si est un boss et qui donne des pts
				BossInfo binfo = BOSS_PTS.get(npc.getNpcId());
				Collection<AggroInfo> hate = npc.getAggroList().getList();
				for(AggroInfo agI : hate){
					if(agI.getAttacker() instanceof Player){
						CustomPlayerRank cpl = ((Player) agI.getAttacker()).getCustomPlayerRank();
						if(cpl.getRank() <= binfo.rank){
							cpl.addCoin(binfo.pts);
							cpl.addPts(binfo.pts);
						}
						else//mentora
							cpl.addCoin(binfo.pts, binfo.rank);
					}
				}
				
			}
		}
	}
	
	
	public static String getCoinName(int rank, Player player){
		final int[] coins = COIN_ID[player.getRace().getRaceId()];
		if(rank < 1 || rank > coins.length )
			return "NA";
		
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(coins[rank-1]);
		if(itemTemplate == null)
			return "NA";
		else
			return itemTemplate.getName();
	}
	
	
	public static boolean containElite(int mobId){
		for(int i : ELITE_LIST)
			if(mobId == i)
				return true;
		return false;
	}
}
