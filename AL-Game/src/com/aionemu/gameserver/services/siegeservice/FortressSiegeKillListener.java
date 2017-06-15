package com.aionemu.gameserver.services.siegeservice;

import java.util.HashMap;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.FortressLocation;

public class FortressSiegeKillListener {

		private final static HashMap<FortressSiege, FortressSiegeKillListener> _instances = new HashMap<FortressSiege ,FortressSiegeKillListener>();
				
		private final FortressSiege siege;

		public FortressSiegeKillListener(FortressSiege siege) {
			this.siege = siege;
		}

		public void onKill(Player player) {
			FortressLocation fortress = siege.getSiegeLocation();

			if (fortress.isInsideLocation(player)) {
				siege.addKill(player);
			}
		}
		
		
		public static void onKillEvent(Player player) {
			for(FortressSiegeKillListener fortressLisener : _instances.values()){
				if(fortressLisener != null)//multi threading error? (remove instances on onKillEvent)
					fortressLisener.onKill(player);
			}
		}
		
		
		public static void addKillLisener(FortressSiege fortress){
			_instances.put(fortress, new FortressSiegeKillListener(fortress));
		}
		
		public static void removeKillLisener(FortressSiege fortress){
			_instances.remove(fortress);
		}
	}

