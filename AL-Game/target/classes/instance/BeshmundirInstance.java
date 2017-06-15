/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.custom.CustomFun;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Gigi, nrg, oslo0322, xTz
 * TODO: Hard-/normal mode
 * TODO: AI for each boss
 * see http://raouooble.com/Beshmundir_Temple_Guide.html
 * see http://gameguide.na.aiononline.com/aion/Beshmundir+Temple+Walkthrough%3A+Hard+Mode
 *
 */
@InstanceID(300170000)
public class BeshmundirInstance extends GeneralInstanceHandler {

	private int macunbello = 0;
	private int kills;
	Npc npcMacunbello = null;
	private Map<Integer,StaticDoor> doors;
	private List<Integer> doorIsba;
	private int isbaDifficulty = 0;

	private boolean lakaraKilled = false;
	private boolean viranhaKilled = false;

	private VisibleObject spawnedFlarestorm = null;

	@Override
	public void onDie(Npc npc) {
		super.onDie(npc);				
		switch(npc.getNpcId()) {
			case 216177:
			case 216175: 
			case 216181: 
			case 216179:
				int rnd = Rnd.get(doorIsba.size());
				int doorId = doorIsba.get(rnd);
				StaticDoor door = doors.get(doorId);
				doorIsba.remove(rnd);
				if(door.isOpen()){
					rnd = Rnd.get(doorIsba.size());
					doorId = doorIsba.get(rnd);
					doorIsba.remove(rnd);
					openDoor(doorId);
				}else{
					openDoor(doorId);
				}
				isbaDifficulty++;
				
				if(CustomFun.ISBARYA_CUSTOM){
					sendMsg("Isbariy is less powerfull");
				}
				break;
			case 216583: // Batelier macumbelo
				spawn(799518, 936.0029f, 441.51712f, 220.5029f, (byte) 28);
				break;
			case 216584: // Batelier macumbelo
				spawn(799519, 791.0439f, 439.79608f, 220.3506f, (byte) 28);
				break; 
			case 216585: // Batelier macumbelo
				spawn(799520, 820.70624f, 278.828f, 220.19385f, (byte) 55);
				break;
			case 216586: // Spawn macumbelo
				if(!lakaraKilled){
					return;
				}
				if (macunbello < 12) {
					npcMacunbello = (Npc)spawn(216734, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // strongest macunbello
					SkillEngine.getInstance().applyEffectDirectly(19048, npcMacunbello, npcMacunbello, 0);
				}
				else if (macunbello < 14) {
					npcMacunbello = (Npc)spawn(216737, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th strongest macunbello
					SkillEngine.getInstance().applyEffectDirectly(19047, npcMacunbello, npcMacunbello, 0);
				}
				else if (macunbello < 21) {
					npcMacunbello = (Npc)spawn(216736, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th weakest macunbello
					SkillEngine.getInstance().applyEffectDirectly(19046, npcMacunbello, npcMacunbello, 0);
				}
				else {
					spawn(216735, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // weakest macunbello
				}
				macunbello = 0;
				sendPacket(new SM_QUEST_ACTION(0, 0));
				openDoor(467);
				break;
			case 799342:
				sendPacket(new SM_PLAY_MOVIE(0, 447));
				break;
			// lakara
			case 216238:
				lakaraKilled = true;
				openDoor(470);
				spawn(216246, 1215.32f, 659.387f, 250.238f, (byte) 89); // spawn viranha
				spawn(216159, 1357.0598f, 388.6637f, 249.26372f, (byte) 90); // spawn saoul kepper
				break;
			// Viranha
			case 216246:
				viranhaKilled = true;
				spawn(216250, 1173.88f, 1212.45f, 283.5f, (byte) 89); // spawn dorakiki
				spawn(216248, 1380.6659f, 1302.1467f, 302.375f, (byte) 106); // spawn taros
				openDoor(473);
				break;
			// Taros
			case 216248:
				spawn(216263, 1603.43f, 1593.01f, 307.034f, (byte) 77); // spawn isbarya
				break;
			case 216739:
			case 216740:
				kills ++;
				if (kills < 10) {
					sendMsg(1400465);
				}
				else if (kills == 10 && lakaraKilled) {
					sendMsg(1400470);
					spawn(216158, 1356.5719f, 147.76418f, 246.27373f, (byte) 91);
				}
				break;
			case 216158:
				openDoor(471);
				break;
			case 216263:
				// this is a safety Mechanism
				// AO
				spawn(216264, 558.306f, 1369.02f, 224.795f, (byte) 70);
				// gate
				sendMsg(1400480);
				spawn(730275, 1611.1266f, 1604.6935f, 311.00503f, (byte) 17);
				break;
			case 216250:  // Dorakiki the Bold
				sendMsg(1400471);
				spawn(216527, 1161.859985f, 1213.859985f, 284.057007f, (byte) 110); // Lupukin: cat trader
				break;
			case 216206: 
			case 216207:
			case 216208:
			case 216209:
			case 216210:
			case 216211:
			case 216212: 
			case 216213:
				macunbello ++;
				switch (macunbello) {
					case 12:
						sendMsg(1400466);
						break;
					case 14:
						sendMsg(1400467);
						break;
					case 21:
						sendMsg(1400468);
						break;
				}
				break;
			case 216264: //Ajout Ferosia : Spawn d'une porte de sortie de l'instance à la mort d'AO
				spawn(730286, 569.0353f, 1379.805f, 224.52919f, (byte) 73);
				break;
		}
	}

	private void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}

		});
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
		switch (movieId) {
			case 443:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_BigOrb_Spawn);
				break;
			case 448:
				TeleportService.teleportTo(player, 300170000, instanceId, 958.45233f, 430.4892f, 219.80301f, 0, false);
				break;
			case 449:
				TeleportService.teleportTo(player, 300170000, instanceId, 822.0199f, 465.1819f, 220.29918f, 0, false);
				break;
			case 450:
				TeleportService.teleportTo(player, 300170000, instanceId, 777.1054f, 300.39005f, 219.89926f, 0, false);
				break;
			case 451:
				TeleportService.teleportTo(player, 300170000, instanceId, 942.3092f, 270.91855f, 219.86185f, 0, false);
				break;
		}
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(535).setOpen(true);
		doorIsba = new ArrayList<Integer>();
		doorIsba.add(531);
		doorIsba.add(532);
		doorIsba.add(534);
		doorIsba.add(536);
	}

	@Override
	public void onOpenDoor(int door) {
		// Safty mecanisme BT 
		if(door == 730290 && viranhaKilled && spawnedFlarestorm == null){
			spawnedFlarestorm = spawn(216168, 1511.69f, 1048.16f, 273.441f, (byte) 40);
		}
	}

	private void openDoor(int doorId){
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}

	@Override
	public void onInstanceDestroy() {
		doors.clear();
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.COURTYARD_OF_ETERNITY_300170000 && CustomFun.ISBARYA_CUSTOM) {
			PacketSendUtility.sendYellowMessageOnCenter(player, "The power of isbariya depends on his loyal gardiens !");
		}
	}
	
	@Override
	public int getDif(int npcId){
		if(npcId == 216263){
			return CustomFun.ISBARYA_CUSTOM ? isbaDifficulty : 2;
		}
		return 0;
	}
}
