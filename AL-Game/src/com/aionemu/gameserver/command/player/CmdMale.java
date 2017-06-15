package com.aionemu.gameserver.command.player;

import java.util.ArrayList;
import java.util.HashMap;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SERIAL_KILLER;
import com.aionemu.gameserver.services.serialkillers.SerialKiller;
import com.aionemu.gameserver.services.serialkillers.SerialKillerDebuff;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class CmdMale extends BaseCommand {

	private ArrayList<Player> _killers = new ArrayList<Player>();
	private SerialKillerDebuff debuff = new SerialKillerDebuff();;
	
	@Override
	public void execute(Player player, String... params) {
		if(player.getSKInfo() == null)
			player.setSKInfo(new SerialKiller(player));
		
		if (params.length == 0) {// remove
			if(player.getSKInfo().getRank() != 0)
			{
			player.getSKInfo().setRank(0);
			_killers.remove(player);
			debuff.endEffect(player);
			}
		} else {// add lv
			int rank = player.getSKInfo().getRank();
			if (rank < 2){
				player.getSKInfo().setRank(++rank);
				if(!_killers.contains(player))
					_killers.add(player);
				debuff.applyEffect(player, rank);
			}
		}
		sendToAll();
	}
	
	private void sendToAll() {// NEED OPTI
		HashMap<Integer, ArrayList<Player>> maps = new HashMap<Integer, ArrayList<Player>>();

		for (Player pl : _killers) {// get New pl maps
			if (pl == null)
				continue;
			ArrayList<Player> map = maps.get(pl.getWorldId());
			if (map == null) {
				map = new ArrayList<Player>();
				maps.put(pl.getWorldId(), map);
			}
			map.add(pl);
		}

		for (int mapId : maps.keySet()) {//send
			final ArrayList<Player> killers = maps.get(mapId);
			for (Player ppl : World.getInstance().getWorldMap(mapId).getWorld().getAllPlayers())
				PacketSendUtility.sendPacket(ppl, new SM_SERIAL_KILLER(killers));
		}
	}

}