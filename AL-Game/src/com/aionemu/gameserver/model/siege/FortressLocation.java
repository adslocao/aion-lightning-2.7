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
package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLegionReward;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import com.aionemu.gameserver.model.templates.siegelocation.SiegeReward;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import java.util.Date;
import java.util.List;

/**
 * @author Source
 */
public class FortressLocation extends SiegeLocation {

	protected List<SiegeReward> siegeRewards;
	protected List<SiegeLegionReward> siegeLegionRewards;
	protected boolean isUnderShield;
	protected boolean isCanTeleport;

	public FortressLocation() {
	}

	public FortressLocation(SiegeLocationTemplate template) {
		super(template);
		this.siegeRewards = template.getSiegeRewards() != null ? template.getSiegeRewards() : null;
		this.siegeLegionRewards = template.getSiegeLegionRewards() != null ? template.getSiegeLegionRewards() : null;
	}

	public List<SiegeReward> getReward() {
		return this.siegeRewards;
	}

	public List<SiegeLegionReward> getLegionReward() {
		return this.siegeLegionRewards;
	}

	@Override
	public int getInfluenceValue() {
		return SiegeConfig.SIEGE_POINTS_FORTRESS;
	}

	/**
	 * @return isEnemy
	 */
	public boolean isEnemy(Player player) {
		if (!player.isGM() && player.getRace().getRaceId() != getRace().getRaceId()) {
			TeleportService.moveToBindLocation(player, true);
			return true;
		}
		return false;
	}

	/**
	 * @return isUnderShield
	 */
	@Override
	public boolean isUnderShield() {
		return this.isUnderShield;
	}

	/**
	 * @param value new undershield value
	 */
	@Override
	public void setUnderShield(boolean value) {
		this.isUnderShield = value;
	}

	/**
	 * @return isCanTeleport
	 */
	@Override
	public boolean isCanTeleport(Player player) {
		return isCanTeleport && player.getRace().getRaceId() == getRace().getRaceId();
	}

	/**
	 * @param status Teleportation status
	 */
	@Override
	public void setCanTeleport(boolean status) {
		this.isCanTeleport = status;
	}

	/**
	 * @return DescriptionId object with fortress name
	 */
	public DescriptionId getNameAsDescriptionId() {
		return new DescriptionId(template.getNameId());
	}

	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		super.onLeaveZone(creature, zone);
		if (this.isVulnerable())
			creature.unsetInsideZoneType(ZoneType.SIEGE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		super.onEnterZone(creature, zone);
		if (!this.isVulnerable()) {
			return;
		}
		
		// On login move to bind
		creature.setInsideZoneType(ZoneType.SIEGE);
		if (!(creature instanceof Player)) {
			return;
		}
		
		Player player = (Player) creature;
		if (player.getOnlineTime() >= 5){
			return;
		}
		
		PlayerAccountData playerAccData = player.getClientConnection().getAccount().getPlayerAccountData(player.getObjectId());
        long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
        
        Date lastCo = new Date(lastOnline);
        Date now = new Date(System.currentTimeMillis());
        
        if(lastCo.getHours() == now.getHours()){
               return;
        }

		
		isEnemy(player); // move to bind point enemys (on login) from fortress
	}


	@Override
	public void clearLocation() {
		for (Creature creature : getCreatures().values()) {
			if (creature instanceof Kisk) {
				Kisk kisk = (Kisk) creature;
				kisk.getController().die();
			}
		}

		for (Player player : getPlayers().values()){
			isEnemy(player);
		}
	}

}