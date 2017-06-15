/*
 * Copyright (C) 2012 Steve
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.shedule;

import com.aionemu.commons.utils.xml.JAXBUtil;
import java.io.File;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.apache.commons.io.FileUtils;
import com.aionemu.gameserver.model.Race;

/**
 *
 * @author Steve
 */
@XmlRootElement(name = "tvt_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class TvtSchedule {

    @XmlElement(name = "tvt_level", required = true)
    private List<TvtLevel> tvtLevelList;

    public List<TvtLevel> getTvtLevelList() {
        return tvtLevelList;
    }
    
    public TvtLevel getTvtLevel(int tvtId) {
        return tvtLevelList.get(tvtId);
    }

    public void setTvtLevelList(List<TvtLevel> tvtLevelList) {
        this.tvtLevelList = tvtLevelList;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "tvt_level")
    public static class TvtLevel {

        @XmlAttribute(required = true)
        private int id;
        @XmlAttribute(required = true)
        private int level;
        @XmlAttribute(name = "start_time", required = true)
        private int startTime;
        @XmlAttribute(required = true)
        private int duration;
        @XmlAttribute(required = true)
        private int mapId;
        @XmlAttribute(required = true)
        private boolean boss;
        @XmlElement(name = "tvt_registrator", required = true)
        private List<TvtRegistrator> tvtRegistrator;
        @XmlElement(name = "tvt_time", required = true)
        private List<String> tvtTime;
        @XmlElement(name = "tvt_helpers", required = true)
        private List<TvtHelpers> tvtHelpers;
        @XmlElement(name = "tvt_clock", required = true)
        private List<TvtClock> tvtClock;
        @XmlElement(name = "tvt_boss", required = true)
        private List<TvtBoss> tvtBoss;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int time) {
            this.startTime = time;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getMapId() {
            return mapId;
        }

        public void setMapId(int mapId) {
            this.mapId = mapId;
        }
        
        public void setBoss(boolean boss){
            this.boss = boss;
        }
        
        public boolean getBoss(){
            return boss;
        }

        public List<String> getTvtTimes() {
            return tvtTime;
        }

        public void setTvtTimes(List<String> tvtTime) {
            this.tvtTime = tvtTime;
        }

        public List<TvtRegistrator> getTvtRegistrator() {
            return tvtRegistrator;
        }

        public void setTvtRegistrator(List<TvtRegistrator> reg) {
            this.tvtRegistrator = reg;
        }
        
        public List<TvtHelpers> getTvtHelpers() {
            return tvtHelpers;
        }
        
        public List<TvtClock> getTvtClock() {
            return tvtClock;
        }
        
        public List<TvtBoss> getTvtBoss() {
            return tvtBoss;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "tvt_registrator")
    public static class TvtRegistrator {

        @XmlAttribute(required = true)
        private int mapId;
        @XmlAttribute(required = true)
        private int npcId;
        @XmlAttribute(required = true)
        private float x;
        @XmlAttribute(required = true)
        private float y;
        @XmlAttribute(required = true)
        private float z;
        @XmlAttribute(required = true)
        private byte h;

        public int getMapId() {
            return mapId;
        }

        public int getNpcId() {
            return npcId;
        }

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
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "tvt_helpers")
    public static class TvtHelpers {
        @XmlAttribute(required = true)
        private int helperId;
        @XmlAttribute(required = true)
        private Race race;
        @XmlElement(name = "tvt_helper_loc", required = true)
        private List<String> loc;
        
        public int getHelperId(){
            return helperId;
        }
        
        public Race getRace(){
            return race;
        }
        
        public List<String> getLoc() {
            return loc;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "tvt_clock")
    public static class TvtClock {
        @XmlAttribute(required = true)
        private int clockId;
        @XmlAttribute(required = true)
        private Race race;
        @XmlElement(name = "tvt_clock_loc", required = true)
        private List<String> loc;
        
        public int getClockId(){
            return clockId;
        }
        
        public Race getRace(){
            return race;
        }
        
        public List<String> getLoc() {
            return loc;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "tvt_boss")
    public static class TvtBoss {
        @XmlAttribute(required = true)
        private int bossId;
        @XmlAttribute(name = "need_score", required = true)
        private int needScore;
        @XmlAttribute(required = true)
        private int reward;
        @XmlAttribute(required = true)
        private int vote;
        @XmlAttribute(required = true)
        private int ap;
        @XmlAttribute(required = true)
        private boolean boost;
        @XmlAttribute(name = "remove_cd", required = true)
        private boolean removeCd;
        @XmlElement(name = "tvt_boss_loc", required = true)
        private List<String> loc;
        
        public int getBossId(){
            return bossId;
        }
        
        public int getNeedScore(){
            return needScore;
        }
        
        public int getReward(){
            return reward;
        }
        
        public int getVote(){
            return vote;
        }
        
        public int getAp(){
            return ap;
        }
        
        public boolean isBoost(){
            return boost;
        }
        
        public boolean isCd(){
            return removeCd;
        }
        
        public List<String> getLoc() {
            return loc;
        }
    }

    public static TvtSchedule load() {
        TvtSchedule ss;
        try {
            String xml = FileUtils.readFileToString(new File("./config/shedule/tvt_schedule.xml"));
            ss = JAXBUtil.deserialize(xml, TvtSchedule.class);
        } catch (Exception e) {
            throw new RuntimeException("[TVT] Failed to initialize tvt", e);
        }
        return ss;
    }
}
