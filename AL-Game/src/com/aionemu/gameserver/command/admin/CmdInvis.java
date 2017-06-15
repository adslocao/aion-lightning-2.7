package com.aionemu.gameserver.command.admin;


	import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.utils.PacketSendUtility;


	/**
	 * @author Divinity
	 */
	public class CmdInvis extends BaseCommand {

		public void execute(Player admin, String... params) {
			if (admin.getVisualState() < 3) {
				admin.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
				admin.setVisualState(CreatureVisualState.HIDE3);
				PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
				PacketSendUtility.sendMessage(admin, "You are invisible.");
			}
			else {
				admin.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
				admin.unsetVisualState(CreatureVisualState.HIDE3);
				PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
				PacketSendUtility.sendMessage(admin, "You are visible.");
			}
		}

	}
