/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.model.autogroup;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutoGroup")
public class AutoGroup {

    @XmlAttribute(required = true)
    protected byte id;
    @XmlAttribute(required = true)
    protected int instanceId;
    @XmlAttribute(name = "name_id")
    protected Integer nameId;
    @XmlAttribute(name = "title_id")
    protected Integer titleId;
    @XmlAttribute(name = "min_lvl")
    protected Integer minLvl;
    @XmlAttribute(name = "max_lvl")
    protected Integer maxLvl;
    @XmlAttribute(name = "register_fast")
    protected Boolean registerFast;
    @XmlAttribute(name = "register_group")
    protected Boolean registerGroup;
    @XmlAttribute(name = "npc_ids")
    protected List<Integer> npcIds;

    public byte getId() {
        return id;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getNameId() {
        return nameId;
    }

    public int getTitleId() {
        return titleId;
    }

    public int getMinLvl() {
        return minLvl;
    }

    public int getMaxLvl() {
        return maxLvl;
    }

    public Boolean hasRegisterFast() {
        return registerFast;
    }

    public Boolean hasRegisterGroup() {
        return registerGroup;
    }

    public List<Integer> getNpcIds() {
        if (npcIds == null) {
            npcIds = Collections.emptyList();
        }
        return this.npcIds;
    }
}
