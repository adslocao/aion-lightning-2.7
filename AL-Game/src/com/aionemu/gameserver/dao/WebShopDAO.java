package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;

/**
 * @author Ferosia
 */
public abstract class WebShopDAO implements DAO {
	
	public abstract void checkAllPendingShopItem();
	
	public abstract void checkPlayerPendingShopItem(int playerId);
	
	public abstract boolean deletePendingShopItemById(int shopId, int playerId);
	
	@Override
	public String getClassName() {
		return WebShopDAO.class.getName();
	}
}