package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 *
 * @author ginho1
 *
 */
public class CM_PLAYER_LISTENER extends AionClientPacket {
	/*
	 * This CM is send every five minutes by client.
	 * Permettrait de vérifier toutes les 5 minutes les
	 * titres et emmotes temporaires des joueurs et les désactiver
	 * si nécessaire.
	 */
	public CM_PLAYER_LISTENER(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		//Player player = getConnection().getActivePlayer();
	}
}
