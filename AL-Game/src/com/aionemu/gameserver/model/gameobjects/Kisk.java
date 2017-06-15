/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.KiskStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KISK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author Sarynth
 */
public class Kisk extends SummonedObject {

	private final Legion ownerLegion;
	private final Race ownerRace;

	private KiskStatsTemplate kiskStatsTemplate;

	private int remainingResurrections;
	private long kiskSpawnTime;

	private final List<Player> kiskMembers = new ArrayList<Player>();
	private int currentMemberCount = 0;

	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 */
	public Kisk(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate npcTemplate, Player owner) {
		super(objId, controller, spawnTemplate, npcTemplate, npcTemplate.getLevel());

		this.kiskStatsTemplate = npcTemplate.getKiskStatsTemplate();
		if (this.kiskStatsTemplate == null)
			this.kiskStatsTemplate = new KiskStatsTemplate();

		remainingResurrections = this.kiskStatsTemplate.getMaxResurrects();
		kiskSpawnTime = System.currentTimeMillis() / 1000;
		this.ownerLegion = owner.getLegion();
		this.ownerRace = owner.getRace();
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return creature.isEnemyFrom(this);
	}

	@Override
	public boolean isEnemyFrom(Npc npc) {
		return npc.isAttackableNpc() || npc.isAggressiveTo(this);
	}

	/**
	 * Required so that the enemy race can attack the Kisk!
	 */
	@Override
	public boolean isEnemyFrom(Player player) {
		return player.getRace() != this.ownerRace;
	}

	/**
	 * @return NpcObjectType.NORMAL
	 */
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.NORMAL;
	}

	/**
	 * 1 ~ race 2 ~ legion 3 ~ solo 4 ~ group 5 ~ alliance
	 * 
	 * @return useMask
	 */
	public int getUseMask() {
		return this.kiskStatsTemplate.getUseMask();
	}

	public List<Player> getCurrentMemberList() {
		return this.kiskMembers;
	}

	/**
	 * @return
	 */
	public int getCurrentMemberCount() {
		return this.currentMemberCount;
	}

	/**
	 * @return
	 */
	public int getMaxMembers() {
		return this.kiskStatsTemplate.getMaxMembers();
	}

	/**
	 * @return
	 */
	public int getRemainingResurrects() {
		return this.remainingResurrections;
	}

	/**
	 * @return
	 */
	public int getMaxRessurects() {
		return this.kiskStatsTemplate.getMaxResurrects();
	}

	/**
	 * @return
	 */
	public int getRemainingLifetime() {
		long timeElapsed = (System.currentTimeMillis() / 1000) - kiskSpawnTime;
		int timeRemaining = (int) (7200 - timeElapsed); // Fixed 2 hours 2 * 60 * 60
		return (timeRemaining > 0 ? timeRemaining : 0);
	}

	/**
	 * @param player
	 * @return
	 */
	public boolean canBind(Player player) {
		String playerName = player.getName();

		if (!playerName.equals(getCreatorName())) {
			// Check if they fit the usemask
			switch (this.getUseMask()) {
				case 1: // Race
					if (this.ownerRace == player.getRace())
						return false;
					break;

				case 2: // Legion
					if (ownerLegion == null)
						return false;
					if (!ownerLegion.isMember(player.getObjectId()))
						return false;
					break;

				case 3: // Solo
					return false; // Already Checked Name

				case 4: // Group (PlayerGroup or PlayerAllianceGroup)
					boolean isMember = false;
					if(player.isInTeam()){
						isMember = player.getCurrentGroup().hasMember(getCreatorId());
					}
					if (isMember == false)
						return false;
					break;

				case 5: // Alliance
						if (!player.isInAlliance2() || !player.getPlayerAlliance2().hasMember(getCreatorId()))
							return false;
					break;

				default:
					return false;
			}
		}

		if (this.getCurrentMemberCount() >= getMaxMembers())
			return false;

		return true;
	}

	/**
	 * @param player
	 */
	public void addPlayer(Player player) {
		kiskMembers.add(player);
		player.setKisk(this);
		this.currentMemberCount++;
		this.broadcastKiskUpdate();
	}

	/**
	 * @param player
	 */
	public void reAddPlayer(Player player) {
		kiskMembers.add(player);
		player.setKisk(this);
		PacketSendUtility.sendPacket(player, new SM_KISK_UPDATE(this));
	}

	/**
	 * @param player
	 */
	public void removePlayer(Player player) {
		kiskMembers.remove(player);
		player.setKisk(null);
		this.currentMemberCount--;
		this.broadcastKiskUpdate();
	}

	/**
	 * Sends SM_KISK_UPDATE to each member
	 */
	private void broadcastKiskUpdate() {
		// Logic to prevent enemy race from knowing kisk information.
		for (Player member : this.kiskMembers) {
			if (!this.getKnownList().knowns(member))
				PacketSendUtility.sendPacket(member, new SM_KISK_UPDATE(this));
		}

		final Kisk kisk = this;
		getKnownList().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player object) {
				if (object.getRace() == ownerRace)
					PacketSendUtility.sendPacket(object, new SM_KISK_UPDATE(kisk));
			}
		});
	}

	/**
	 * @param message
	 */
	public void broadcastPacket(SM_SYSTEM_MESSAGE message) {
		for (Player member : kiskMembers) {
			if (member != null)
				PacketSendUtility.sendPacket(member, message);
		}
	}

	/**
	 * @param player
	 */
	public void resurrectionUsed(Player player) {
		remainingResurrections--;
		broadcastKiskUpdate();
		if (remainingResurrections <= 0)
			player.getKisk().getController().onDelete();
	}

	/**
	 * @return ownerRace
	 */
	public Race getOwnerRace() {
		return this.ownerRace;
	}

	public boolean isActive() {
		return !this.getLifeStats().isAlreadyDead() && this.getRemainingResurrects() > 0;
	}

}
