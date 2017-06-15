package com.aionemu.gameserver.model.templates.academy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pvp_zone_data")
public class PvpZoneTemplate {

    @XmlElement(name = "pvp_stage_world")
    protected List<PvpZoneTemplate.PvpWorld> pvpWorld;

    public List<PvpZoneTemplate.PvpWorld> getPvpWorld() {
        if (pvpWorld == null) {
            pvpWorld = new ArrayList<PvpZoneTemplate.PvpWorld>();
        }
        return pvpWorld;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "pvp_stage_world")
    public static class PvpWorld {

        @XmlElement(name = "pvp_stage")
        protected List<PvpZoneTemplate.PvpStage> pvpStage;
        @XmlAttribute(name = "mapId")
        private int mapId;

        public List<PvpZoneTemplate.PvpStage> getStage() {
            if (pvpStage == null) {
                pvpStage = new ArrayList<PvpZoneTemplate.PvpStage>();
            }
            return pvpStage;
        }

        public int getMapId() {
            return mapId;
        }
        
        public int getSize() {
            return pvpStage.size();
        }
        
        public PvpZoneTemplate.PvpStage getPositionForStage(int value){
            return pvpStage.get(value);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "pvp_stage")
    public static class PvpStage {

        @XmlElement(name = "pvp_position")
        protected List<PvpZoneTemplate.PvpPosition> pvpPosition;
        @XmlAttribute(name = "id")
        private int id;
        @XmlAttribute(name = "time")
        private int time;

        public List<PvpZoneTemplate.PvpPosition> getPosition() {
            if (pvpPosition == null) {
                pvpPosition = new ArrayList<PvpZoneTemplate.PvpPosition>();
            }
            return pvpPosition;
        }

        public int getId() {
            return id;
        }

        public int getTime() {
            return time;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "pvp_position")
    public static class PvpPosition {

        @XmlAttribute(name = "x")
        private float x;
        @XmlAttribute(name = "y")
        private float y;
        @XmlAttribute(name = "z")
        private float z;
        @XmlAttribute(name = "h")
        private byte h;

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        public byte getH() {
            return h;
        }
    }
}
