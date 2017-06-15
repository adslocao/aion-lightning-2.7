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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.Collection;

/**
 * @author Source & xTz
 */
public class SM_SERIAL_KILLER extends AionServerPacket {

    private int type;
    private int debuffLvl;
    private Collection<Player> players;

    public SM_SERIAL_KILLER(boolean showMsg, int debuffLvl) {
        this.type = showMsg ? 1 : 0;
        this.debuffLvl = debuffLvl;
    }

    public SM_SERIAL_KILLER(Collection<Player> players) {
        this.type = 4;
        this.players = players;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        switch (type) {
            case 0:
            case 1:
                writeD(type);
                writeD(0x01);
                writeD(0x01);
                writeH(0x01);
                writeD(debuffLvl);
                break;
            case 4:
                writeD(type);
                writeD(0x01); // unk
                writeD(0x01); // unk
                writeH(players.size());
                for (Player player : players) {
                    writeD(player.getSKInfo().getRank());
                    writeD(player.getObjectId());
                    writeD(0x01); // unk
                    writeD(player.getAbyssRank().getRank().getId());
                    writeH(player.getLevel());
                    writeF(player.getX());
                    writeF(player.getY());
                    writeS(player.getName(), 118);
                    writeD(0x00); // unk
                    writeD(0x00); // unk
                    writeD(0x00); // unk
                    writeD(0x00); // unk
                }
                break;
        }
    }

}