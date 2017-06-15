package ai.instance.beshmundirTemple;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("dorakikithebold")
public class DorakikiTheBold extends AggressiveNpcAI2 {
	private Npc fixit = null;
	private Npc chopper = null;
	private Npc sorcererHakiki = null;
	
	private boolean figthStart = false;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);      

		if(figthStart){
			return;
		}
		figthStart = true;
		PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_MESSAGE(getOwner().getObjectId(), getOwner().getName(),
			"Niark, I need help !!", ChatType.NORMAL));

		fixit = (Npc) spawn(281647, 1178.0f, 1220.0f, 283.3f, (byte) 0);
		chopper = (Npc) spawn(281649, 1180.0f, 1207.0f, 283.6f, (byte) 0);
		sorcererHakiki = (Npc) spawn(281648, 1166.0f, 1206.0f, 283.8f, (byte) 0);
		attackPlayer(fixit, getTarget());
		attackPlayer(chopper, getTarget());
		attackPlayer(sorcererHakiki, getTarget());
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
	}

	@Override
	public void handleDied() {
		super.handleDied();
		cancelTask();
	}
	
	private void cancelTask(){
		figthStart = false;
		fixit.getController().onDelete();
		chopper.getController().onDelete();
		sorcererHakiki.getController().onDelete();
	}
}
