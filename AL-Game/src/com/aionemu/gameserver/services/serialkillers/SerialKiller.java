/*
 * This file is part of aion-lightning <aion-lightning.org>.
 * 
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.serialkillers;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Source
 */
public class SerialKiller {

    private Player owner;
    private int killerRank;
    public int victims;

    public SerialKiller(Player owner) {
        this.owner = owner;
    }

    public void refreshOwner(Player player) {
        owner = player;
    }

    public Player getOwner() {
        return owner;
    }

    public void setRank(int rank) {
    	if(rank < 0 || rank > 2)
    		return;
        killerRank = rank;
    }

    public int getRank() {
        return killerRank;
    }

}