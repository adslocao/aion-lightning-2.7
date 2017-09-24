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
package instance.abyss;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;

// import java.util.List;

/**
 * @author zhkchi
 * @reworked vlog, Luzien
 * @see http://gameguide.na.aiononline.com/aion/Abyssal+Splinter+Walkthrough
 */
@InstanceID(300220000)
public class AbyssalSplinterInstance extends GeneralInstanceHandler {
	private int destroyedFragments = 0;
	private boolean bossSpawned = false;
	private boolean enosKilled = false;

	@Override
	public void onDie(Npc npc) {
		final int npcId = npc.getNpcId();
		switch (npcId) {
		case 216951: // Pazuzu the Life Current
			spawnPazuzuReward();
			break;
		case 216950: // Kaluva the Fourth Fragment
			spawnKaluvaReward();
			break;
		case 216948: //rukril 
		case 216949: //ebonsoul
			if (getNpc(npcId == 216949 ? 216948 : 216949) == null) {
				spawnDayshadeReward();
			} else {
				sendMsg(npcId == 216948 ? 1400634 : 1400635); //Defeat Rukril/Ebonsoul in 1 min!

				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {

						if (getNpc(npcId == 216949 ? 216948 : 216949) == null
								|| getNpc(npcId == 216949 ? 216948 : 216949).getLifeStats().isAlreadyDead()) {
							return;
						}
						switch (npcId) {
						case 216948:
							spawn(216948, 447.1937f, 683.72217f, 433.1805f, (byte) 108); // rukril
							break;
						case 216949:
							spawn(216949, 455.5502f, 702.09485f, 433.13727f, (byte) 108); // ebonsoul
							break;
						}

					}

				}, 60000);
			}
			break;
		case 216960: // Yamennes Painflare
		case 216952: // Yamennes Blindsight
			spawnYamennesGenesisTreasureBoxes();
			spawnYamennesAbyssalTreasureBox(npc.getNpcId());
			spawn(730317, 328.476f, 762.585f, 197.479f, (byte) 90); //Exit
			break;
		case 700955: // HugeAetherFragment
			destroyedFragments++;
			onFragmentKill();
			npc.getController().onDelete();
			break;

		case 281909:
			npc.getController().onDelete();
			if (instance.getNpcs(281909).size() != 0) {
				break;
			}
			Npc pazuzu = getNpc(216951);
			if (pazuzu != null && !pazuzu.getLifeStats().isAlreadyDead()) {
				pazuzu.getEffectController().removeEffect(19145);
				pazuzu.getEffectController().removeEffect(19291);
			}
			break;
		case 216945:// Enos hero
			spawnApBox();
			enosKilled = true;
			instance.getDoors().get(19).setOpen(true);
			break;
		case 281911:
		case 281912:
		case 282057:
		case 282107:
		case 282014:
		case 282015:
		case 282131:
		case 281903:
		case 281904:
			npc.getController().onDelete();
			break;
		}
	}

	@Override
	public void onInstanceDestroy() {
		destroyedFragments = 0;
	}

	@Override
	public void handleUseItemFinish(Player player, int npcId) {
		Npc npc = getNpc(npcId);
		if (npc == null || npc.getNpcId() != 700856 || !enosKilled) { // Safty mecanisme
			return;
		}
		if (isSpawned(216960) || isSpawned(216952) || bossSpawned) { // bosses spawned
			return;
		}

		QuestEnv env = new QuestEnv(npc, player, 0, 0);
		QuestEngine.getInstance().onDialog(env);
		if (!isSpawned(700955) && destroyedFragments == 3) { // No Huge Aether Fragments spawned (all destroyed)
			sendMsg(1400732);
			spawn(216960, 329.70886f, 733.8744f, 197.60938f, (byte) 0);
		} else {
			sendMsg(1400731);
			spawn(216952, 329.70886f, 733.8744f, 197.60938f, (byte) 0);
		}
		npc.getController().onDelete();
		bossSpawned = true;
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
				player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

	private void spawnApBox() {
		spawn(700859, 306.507f, 715.272f, 367.919f, (byte) 0);
		spawn(700859, 368.292f, 771.303f, 346.94f, (byte) 0);
		spawn(700859, 350.455f, 788.739f, 320.913f, (byte) 0);
		spawn(700859, 344.288f, 778.731f, 397.681f, (byte) 0);
		spawn(700859, 389.828f, 763.123f, 273.575f, (byte) 0);
		spawn(700859, 360.127f, 684.13f, 390.358f, (byte) 0);
		spawn(700859, 347.353f, 735.86f, 419.013f, (byte) 0);
		spawn(700859, 290.216f, 729.601f, 254.906f, (byte) 0);
		spawn(700859, 311.645f, 768.068f, 343.081f, (byte) 0);
		spawn(700859, 346.968f, 742.378f, 365.216f, (byte) 0);
		spawn(700859, 280.992f, 767.557f, 288.909f, (byte) 0);
		spawn(700859, 270.471f, 743.99f, 318.996f, (byte) 0);
	} 

    private void spawnYamennesGenesisTreasureBoxes() {
        spawn(700934, 326.978f, 729.8414f, 198.46796f, (byte) 16);
        spawn(700934, 326.5296f, 735.13324f, 198.46796f, (byte) 66);
        spawn(700934, 329.8462f, 738.41095f, 198.46796f, (byte) 3);
    }

    private void spawnYamennesAbyssalTreasureBox(int npcId) {
        spawn(npcId == 216952 ? 700937 : 700938, 330.891f, 733.2943f, 198.55286f, (byte) 113);
    }

	private boolean isSpawned(int npcId) {
		return !instance.getNpcs(npcId).isEmpty();
	}
	
	/* START PAZZUZU */
	private void spawnPazuzuReward(){
		spawnPazuzuHugeAetherFragment();
		spawnPazuzuGenesisTreasureBoxes();
		spawnPazuzuAbyssalTreasureBox();
	}

	private void spawnPazuzuHugeAetherFragment() {
		spawn(700955, 669.576f, 335.135f, 465.895f, (byte) 0);
	}

    private void spawnPazuzuGenesisTreasureBoxes() {
        spawn(700934, 651.53204f, 357.085f, 465.8837f, (byte) 66);
        spawn(700934, 647.00446f, 357.2484f, 465.14117f, (byte) 0);
        spawn(700934, 653.8384f, 360.39508f, 465.8837f, (byte) 100);
    }

    private void spawnPazuzuAbyssalTreasureBox() {
        spawn(700860, 649.24286f, 361.33755f, 467.89145f, (byte) 33);
    }

    /*private void spawnPazuzusTreasureBox() {
        if (Rnd.get(0, 100) >= 80) { // 20% chance, not retail
            spawn(700861, 649.243f, 362.338f, 466.0451f, (byte) 0);
        }
    }*/
    /* END PAZZUZU */

    /* START KALUVA */
    private void spawnKaluvaReward(){
    	spawnKaluvaHugeAetherFragment();
    	spawnKaluvaGenesisTreasureBoxes();
    	spawnKaluvaAbyssalTreasureBox();
	}
    
	private void spawnKaluvaHugeAetherFragment() {
		spawn(700955, 633.7498f, 557.8822f, 422.99347f, (byte) 6);
	}
	
	private void spawnKaluvaGenesisTreasureBoxes() {
        spawn(700934, 601.2931f, 584.66705f, 422.2829f, (byte) 6);
        spawn(700934, 597.2156f, 583.95416f, 422.2829f, (byte) 66);
        spawn(700934, 602.9586f, 589.2678f, 422.2829f, (byte) 100);
    }

    private void spawnKaluvaAbyssalTreasureBox() {
        spawn(700935, 598.82776f, 588.25946f, 424.29065f, (byte) 113);
    }
    /* END KALUVA */

    /* START Dayshade */
    private void spawnDayshadeReward(){
    	spawnDayshadeAetherFragment();
    	spawnDayshadeGenesisTreasureBoxes();
    	spawnDayshadeAbyssalTreasureChest();
	}
    
	private void spawnDayshadeAetherFragment() {
		spawn(700955, 452.89706f, 692.36084f, 433.96838f, (byte) 6);
	} 

    private void spawnDayshadeGenesisTreasureBoxes() {
        spawn(700934, 408.10938f, 650.9015f, 439.28332f, (byte) 66);
        spawn(700934, 402.40375f, 655.55237f, 439.26288f, (byte) 33);
        spawn(700934, 406.74445f, 655.5914f, 439.2548f, (byte) 100);
    }

    private void spawnDayshadeAbyssalTreasureChest() {
        spawn(700936, 404.891f, 650.2943f, 439.2548f, (byte) 130);
    }
    /* END Dayshade */
    
    /*
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
	
	
	private void removeSummoned() {
		Npc gate1 = getNpc(282014);
		Npc gate2 = getNpc(282015);
		Npc gate3 = getNpc(282131);
		if ((gate1 == null || gate1.getLifeStats().isAlreadyDead())
				&& (gate2 == null || gate2.getLifeStats().isAlreadyDead())
				&& (gate3 == null || gate3.getLifeStats().isAlreadyDead())) {
			deleteNpcs(instance.getNpcs(281903));// Summoned Orkanimum
			deleteNpcs(instance.getNpcs(281904));// Summoned Lapilima
		}
	}
	*/

	private void onFragmentKill() {
		switch (destroyedFragments) {
		case 1:
			// The destruction of the Huge Aether Fragment has destabilized the artifact!
			sendMsg(1400689);
			break;
		case 2:
			// The destruction of the Huge Aether Fragment has put the artifact protector on alert!
			sendMsg(1400690);
			break;
		case 3:
			// The destruction of the Huge Aether Fragment has caused abnormality on the artifact. The artifact protector is
			// furious!
			sendMsg(1400691);
			break;
		}
	}
}
