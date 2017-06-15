package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;

/**
 * @author SoulKeeper
 */
public class FortressSiegeAbyssPointsAddedListener extends AbyssPointsService.AddAPGlobalCallback {

	private final FortressSiege siege;

	public FortressSiegeAbyssPointsAddedListener(FortressSiege siege) {
		this.siege = siege;
	}

	@Override
	public void onAbyssPointsAdded(Player player, int abyssPoints) {
		FortressLocation fortress = siege.getSiegeLocation();

		// Make sure that only AP earned near this fortress will be added
		// Abyss points can be added only while in the siege zones
		if (fortress.isInsideLocation(player)) {
			siege.addAbyssPoints(player, abyssPoints);
		}
	}
}
