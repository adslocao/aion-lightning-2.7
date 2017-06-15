package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public abstract class PlayerRankDAO implements DAO{

	@Override
	public final String getClassName() {
		return PlayerRankDAO.class.getName();
	}

	public abstract void loadCustomRank(Player player);
	public abstract void storeCustomRank(Player player);
	public abstract void insertCustomRank(Player player);
}
