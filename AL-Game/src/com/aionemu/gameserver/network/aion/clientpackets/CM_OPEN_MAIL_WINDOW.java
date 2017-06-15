package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MAIL_SERVICE;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_OPEN_MAIL_WINDOW extends AionClientPacket {
		
	private int type;

	public CM_OPEN_MAIL_WINDOW(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		this.type = readC();
	}

	@Override
	protected void runImpl() {
		Player player = this.getConnection().getActivePlayer();
		if(type == 1) {
			PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player, player.getMailbox().getExpressLetters()));
			for (Letter letter : player.getMailbox().getExpressLetters())
				letter.setExpress(false);
		}
		else
			PacketSendUtility.sendPacket(player, new SM_MAIL_SERVICE(player, player.getMailbox().getLetters()));
	}

}
