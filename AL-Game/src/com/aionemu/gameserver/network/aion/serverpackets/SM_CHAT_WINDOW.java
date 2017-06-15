package com.aionemu.gameserver.network.aion.serverpackets;


import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author prix
 */
public class SM_CHAT_WINDOW extends AionServerPacket {

	private Player target;

	public SM_CHAT_WINDOW(Player target) {
		this.target = target;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (target == null)
			return;

		PlayerGroup group = target.getPlayerGroup2();

		if (group == null || group.size() < 2) {
			writeC(4); // no group
			writeS(target.getName());
			writeD(0); // no group yet
			writeC(target.getPlayerClass().getClassId());
			writeC(target.getLevel());
			writeC(0); // unk
		}
		else {
			writeC(2); // group
			writeS(target.getName());
			writeD(group.getTeamId());
			writeS(group.getLeader().getName());

			Collection<Player> members = group.getMembers();
			for (Player groupMember : members)
				writeC(groupMember.getLevel());

			for (int i = group.size(); i < 6; i++)
				writeC(0);

			for (Player groupMember : members)
				writeC(groupMember.getPlayerClass().getClassId());

			for (int i = group.size(); i < 6; i++)
				writeC(0);
		}
	}
}
