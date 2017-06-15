/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;

/**
 * @author Mr. Poke
 */
public abstract class PlayerEmotionListDAO implements DAO {

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.commons.database.dao.DAO#getClassName()
	 */
	@Override
	public String getClassName() {
		return PlayerEmotionListDAO.class.getName();
	}

	/**
	 * @param player
	 */
	public abstract void loadEmotions(Player player);

	/**
	 * @param player
	 */
	public abstract void insertEmotion(Player player, Emotion emotion);
	
	
	public abstract void deleteEmotion(int playerId, int emotionId);
}
