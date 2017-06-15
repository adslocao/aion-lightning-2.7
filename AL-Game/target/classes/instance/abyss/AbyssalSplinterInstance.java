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

import java.util.Set;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author zhkchi
 * @reworked vlog
 * @see http://gameguide.na.aiononline.com/aion/Abyssal+Splinter+Walkthrough
 */
@InstanceID(300220000)
public class AbyssalSplinterInstance extends GeneralInstanceHandler {

	private int destroyedFragments;
	private int killedPazuzuWorms = 0;

	@Override
	public void onDie(Npc npc) {
		final int npcId = npc.getNpcId();
		switch (npcId) {
			case 216951: // Pazuzu the Life Current
				spawnPazuzuHugeAetherFragment();
				spawnPazuzuGenesisTreasureBoxes();
				spawnPazuzuAbyssalTreasureBox();
				spawnPazuzusTreasureBox();
				break;
			case 216950: // Kaluva the Fourth Fragment
				spawnKaluvaHugeAetherFragment();
				spawnKaluvaGenesisTreasureBoxes();
				spawnKaluvaAbyssalTreasureBox();
				break;
			case 282010: // Dayshade
				// Handled in DayshadeTwinsAI2
				break;
			case 216960: // Yamennes Painflare
			case 216952: // Yamennes Blindsight
				spawnYamennesGenesisTreasureBoxes();
				break;
			case 700955: // HugeAetherFragment
				destroyedFragments++;
				onFragmentKill();
				break;
			case 216948:
			case 216949:
				if (getNpc(npcId == 216949 ? 216948 : 216949) == null) {
					spawn(700955, 452.89706f, 692.36084f, 433.96838f, (byte) 6);
					spawn(700934, 408.10938f, 650.9015f, 439.28332f, (byte) 66);
					spawn(700934, 402.40375f, 655.55237f, 439.26288f, (byte) 33);
					spawn(700934, 406.74445f, 655.5914f, 439.2548f, (byte) 100);
					spawn(700938, 330.891f, 733.2943f, 198.55286f, (byte) 113);
				}
				else {
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {

							if (getNpc(npcId == 216949 ? 216948 : 216949) != null) {
								switch (npcId) {
									case 216948:
										spawn(216948, 415.330994f, 664.830994f, 437.470001f, (byte) 10); // rukril
										break;
									case 216949:
										spawn(216949, 447.037994f, 735.560974f, 437.490997f, (byte) 94); // ebonsoul
										break;
								}
							}
						}

					}, 60000); // Both bosses have to be killed within 60s, otherwise the first one respawns
				}
				npc.getController().onDelete();
				break;
			case 281909:
				if (++killedPazuzuWorms == 5) {
					killedPazuzuWorms = 0;
					Npc pazuzu = getNpc(216951);
					if (pazuzu != null && !pazuzu.getLifeStats().isAlreadyDead()) {
						pazuzu.getEffectController().removeEffect(19145);
						pazuzu.getEffectController().removeEffect(19291);
					}
				}
				npc.getController().onDelete();
				break;
		}
	}

	protected Npc getNpc(int npcId) {
        return instance.getNpc(npcId);
    }

	@Override
	public void onDropRegistered(Npc npc) {
		if (npc.getNpcId() == 700935) { // KaluvaAbyssalTreasureBox
			Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
			// Add Abyssal Fragment for all players
			dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npc.getNpcId(), 185000104, 1));
		}
	}

	@Override
	public void onInstanceDestroy() {
		destroyedFragments = 0;
	}

	private void spawnPazuzuHugeAetherFragment() {
		spawn(700955, 669.576f, 335.135f, 465.895f, (byte) 0);
	}

	private void spawnPazuzuGenesisTreasureBoxes() {
		spawn(700934, 651.53204f, 357.085f, 466.8837f, (byte) 66);
		spawn(700934, 647.00446f, 357.2484f, 466.14117f, (byte) 0);
		spawn(700934, 653.8384f, 360.39508f, 466.8837f, (byte) 100);
	}

	private void spawnPazuzuAbyssalTreasureBox() {
		spawn(700860, 649.24286f, 361.33755f, 467.89145f, (byte) 33);
	}

	private void spawnPazuzusTreasureBox() {
		if (Rnd.get(0, 100) >= 80) { // 20% chance, not retail
			spawn(700861, 649.243f, 362.338f, 466.0451f, (byte) 0);
		}
	}

	private void spawnKaluvaHugeAetherFragment() {
		spawn(700955, 633.7498f, 557.8822f, 424.99347f, (byte) 6);
	}

	private void spawnKaluvaGenesisTreasureBoxes() {
		spawn(700934, 601.2931f, 584.66705f, 424.2829f, (byte) 6);
		spawn(700934, 597.2156f, 583.95416f, 424.2829f, (byte) 66);
		spawn(700934, 602.9586f, 589.2678f, 424.2829f, (byte) 100);
	}

	private void spawnKaluvaAbyssalTreasureBox() {
		spawn(700935, 598.82776f, 588.25946f, 424.29065f, (byte) 113);
	}

	private void spawnYamennesGenesisTreasureBoxes() {
		spawn(700934, 326.978f, 729.8414f, 198.46796f, (byte) 16);
		spawn(700934, 326.5296f, 735.13324f, 198.46796f, (byte) 66);
		spawn(700934, 329.8462f, 738.41095f, 198.46796f, (byte) 3);
	}

	private void onFragmentKill() {
		switch (destroyedFragments) {
			case 1:
				// The destruction of the Huge Aether Fragment has destabilized the artifact!
				sendMessage(SM_SYSTEM_MESSAGE.STR_MSG_IDAbRe_Core_Artifact_Die_01);
				break;
			case 2:
				// The destruction of the Huge Aether Fragment has put the artifact protector on alert!
				sendMessage(SM_SYSTEM_MESSAGE.STR_MSG_IDAbRe_Core_Artifact_Die_02);
				break;
			case 3:
				// The destruction of the Huge Aether Fragment has caused abnormality on the artifact. The artifact protector is
				// furious!
				sendMessage(SM_SYSTEM_MESSAGE.STR_MSG_IDAbRe_Core_Artifact_Die_03);
				break;
		}
	}

	private void sendMessage(SM_SYSTEM_MESSAGE message) {
		for (Player p : instance.getPlayersInside()) {
			PacketSendUtility.sendPacket(p, message);
		}
	}
}
