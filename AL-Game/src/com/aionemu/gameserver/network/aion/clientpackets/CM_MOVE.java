/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.controllers.movement.MovementMask;
import com.aionemu.gameserver.controllers.movement.PlayerMoveController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
//import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.taskmanager.tasks.TeamMoveUpdater;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;

/**
 * Packet about player movement.
 * 
 * @author -Nemesiss-
 */
public class CM_MOVE extends AionClientPacket {
	
	// private static final Logger log = LoggerFactory.getLogger(CM_MOVE.class);

	private byte type;
	private byte heading;
	private float x = 0f, y = 0f, z = 0f, x2 = 0f, y2 = 0f, z2 = 0f, vehicleX = 0f, vehicleY = 0f, vehicleZ = 0f,
		vectorX = 0f, vectorY = 0f, vectorZ = 0f;
	private byte glideFlag;
	private int unk1, unk2;

	public CM_MOVE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();

		if (player == null || !player.isSpawned()) {
			return;
		}

		x = readF();
		y = readF();
		z = readF();

		heading = (byte) readC();
		type = (byte) readC();

		if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				vectorX = readF();
				vectorY = readF();
				vectorZ = readF();
				x2 = vectorX + x;
				y2 = vectorY + y;
				z2 = vectorZ + z;
			}
			else {
				x2 = readF();
				y2 = readF();
				z2 = readF();
			}
			
			if(CustomConfig.PHX_SPEEDHACK && !player.isGM() && !player.isInWindstream())
				this.verifyHack(player);
		}
		if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
			glideFlag = (byte) readC();
		}
		if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
			unk1 = readD();
			unk2 = readD();
			vehicleX = readF();
			vehicleY = readF();
			vehicleZ = readF();
		}
	}

	private void verifyHack(Player player) {
		float summ = 0;
		if(vectorX >= 0) summ += vectorX;
		else summ += vectorX *= -1;
		
		if(vectorY >= 0) summ += vectorY;
		else summ += vectorY *= -1;
		
		if(vectorZ >= 0) summ += vectorZ;
		else summ += vectorZ *= -1;
		
		if(summ > CustomConfig.PHX_SPEEDHACK_POWER) {
			AuditLogger.info(player, "PHX Speed Hacker. Used vector power "+summ);
			switch(CustomConfig.PHX_SPEEDHACK_PUNISH) {
				case 1:
					TeleportService.teleportTo(player, player.getWorldId(), x, y, z, heading, 0);
					break;
				case 2:
					player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
					break;
			}
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		// packet was not read correctly
		if (player.isTeleporting() || player.getLifeStats().isAlreadyDead())
			return;

		if(player.getEffectController().isUnderFear())
			return;
		
		PlayerMoveController m = player.getMoveController();
		m.movementMask = type;
		
		/*
		if (player.isInFlyingState() && !player.isInsideZoneType(ZoneType.FLY) && player.getAccessLevel() < AdminConfig.GM_FLIGHT_FREE) {
			AuditLogger.info(player, "Player FLY HACK");
			log.warn("Player FLY HACK : " + player.getName());
			 Desactivation de l'interruption de vol et du message envoye au client.
			player.getFlyController().endFly();
			PacketSendUtility.sendMessage(player, "Votre compte a ete marque comme etant lance avec une tentative de Hack FreeFly. Veuillez redemarrer votre client et supprimer ce parametre");
			
		}
		*/
		
		// Admin Teleportation
		if (player.getAdminTeleportation() && ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE)
			&& ((type & MovementMask.MOUSE) == MovementMask.MOUSE)) {
			m.setNewDirection(x2, y2, z2);
			World.getInstance().updatePosition(player, x2, y2, z2, heading);
			PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		}
		float speed = player.getGameStats().getMovementSpeedFloat();
		if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
			m.glideFlag = glideFlag;
			player.getFlyController().switchToGliding();
		}
		else
			player.getFlyController().onStopGliding(false);

		if (type == 0) {
			player.getController().onStopMove();
			player.getFlyController().onStopGliding(false);
		}
		else if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				m.vectorX = vectorX;
				m.vectorY = vectorY;
				m.vectorZ = vectorZ;
			}
			player.getMoveController().setNewDirection(x2, y2, z2, heading);
			player.getController().onStartMove();
		}
		else {
			player.getController().onMove();
			if ((type & MovementMask.MOUSE) == 0) {
				player.getMoveController().setNewDirection(x + m.vectorX * speed * 1.5f, y + m.vectorY * speed * 1.5f,
					z + m.vectorZ * speed * 1.5f, heading);
			}
		}

		if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
			m.unk1 = unk1;
			m.unk2 = unk2;
			m.vehicleX = vehicleX;
			m.vehicleY = vehicleY;
			m.vehicleZ = vehicleZ;
		}
		double timeDiff = 0;
		if ((type & MovementMask.STARTMOVE) != MovementMask.STARTMOVE && m.isInMove())
			timeDiff = speed * (System.currentTimeMillis() - m.getLastMoveUpdate()) / 1000;
		else if (type == 0)
			timeDiff = speed / 6;
		if ((type & MovementMask.GLIDE) == MovementMask.GLIDE)
			timeDiff += speed / 2;

		if (GSConfig.SPEEDHACK_VALIDATOR) {
			if (!player.isInWindstream() && m.isInMove() && ((type & MovementMask.FALL) != MovementMask.FALL)
				&& ((type & MovementMask.VEHICLE) != MovementMask.VEHICLE)) {
				double dist = MathUtil.getDistance(x, y, m.getTargetX2(), m.getTargetY2());
				double dist2 = MathUtil.getDistance(player.getX(), player.getY(), m.getTargetX2(), m.getTargetY2());
				/*
				 * TODO if ((dist2 - dist - timeDiff) > 10) { PacketSendUtility.broadcastPacketAndReceive(player, new
				 * SM_MOVE(player)); x = player.getX(); y = player.getY(); z = player.getZ(); }
				 */
				if ((dist2 - dist - timeDiff) > speed * 0.5f && player.speedHackValue < 10)
					player.speedHackValue++;
				else if (player.speedHackValue > 0)
					player.speedHackValue--;

				if (player.speedHackValue >= 10) {
					if (GSConfig.SPEEDHACK_KICK)
						getConnection().closeNow();
					AuditLogger.info(player, "Possible speedhack dist:" + (dist2 - dist - timeDiff));
				}
			}
		}
		World.getInstance().updatePosition(player, x, y, z, heading);
		m.updateLastMove();

		if (player.isInGroup2() || player.isInAlliance2())
			TeamMoveUpdater.getInstance().startTask(player);

		if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE || type == 0)
			PacketSendUtility.broadcastPacket(player, new SM_MOVE(player));

		if ((type & MovementMask.FALL) == MovementMask.FALL)
			m.updateFalling(z);
		else
			m.stopFalling(z);

		if (type != 0 && player.isProtectionActive())
			player.getController().stopProtectionActiveTask();
	}

	@Override
	public String toString() {
		return "CM_MOVE [type=" + type + ", heading=" + heading + ", x=" + x + ", y=" + y + ", z=" + z + ", x2=" + x2
			+ ", y2=" + y2 + ", z2=" + z2 + ", vehicleX=" + vehicleX + ", vehicleY=" + vehicleY + ", vehicleZ=" + vehicleZ
			+ ", vectorX=" + vectorX + ", vectorY=" + vectorY + ", vectorZ=" + vectorZ + ", glideFlag=" + glideFlag
			+ ", unk1=" + unk1 + ", unk2=" + unk2 + "]";
	}
}
