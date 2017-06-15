package com.aionemu.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.aionemu.gameserver.model.templates.academy.PvpZoneTemplate;



@XmlRootElement(name = "pvp_zone_data")
@XmlAccessorType(XmlAccessType.FIELD)
public class PvpZoneData {
    
    @XmlElement(name = "pvp_world")
    private List<PvpZoneTemplate.PvpWorld> pvpWorld;
    private TIntObjectHashMap<PvpZoneTemplate.PvpWorld> pvpWorldList = new TIntObjectHashMap<PvpZoneTemplate.PvpWorld>();

    void afterUnmarshal(Unmarshaller u, Object parent) {
        pvpWorldList.clear();
        for (PvpZoneTemplate.PvpWorld list : pvpWorld) {
            pvpWorldList.put(list.getMapId(), list);
        }
    }

    public int size() {
        return pvpWorldList.size();
    }

    public PvpZoneTemplate.PvpWorld getMapId(int value) {
        return pvpWorldList.get(value);
    }
    
}
