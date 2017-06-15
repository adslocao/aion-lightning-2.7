package com.aionemu.gameserver.services.tvt;

import javolution.util.FastList;
import com.aionemu.gameserver.configs.shedule.TvtSchedule;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;



public class TvtRegistrator {

    private FastList<Npc> regNpc = new FastList<Npc>();
    private int tvtId;
    private int startTime;
    private int duration;
    long remainingTime;
    private boolean isStarted;
    private TvtPlayerHolder holders;
    private int level;
    private int mapId;

    public TvtRegistrator(int tvtId, int startTime, int duration, int level, int mapId) {
        this.tvtId = tvtId;
        this.startTime = startTime;
        this.duration = duration;
        setRemainingTime(System.currentTimeMillis() + startTime * 1000);
        holders = new TvtPlayerHolder(this);
        this.level = level;
        this.mapId = mapId;
        isStarted = true;
    }

    public int getRemainingTime() {
        int result;
        result = (int) (remainingTime - System.currentTimeMillis()) / 1000;
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    public final void setRemainingTime(long paramLong) {
        this.remainingTime = paramLong;
    }

    public void clearRegNpc() {
        regNpc.clear();
    }

    public FastList<Npc> getRegNpc() {
        return regNpc;
    }

    public void spawnRegNpc() {
        TvtSchedule tvtSchedule = TvtService.getInstance().getTvtSchedule();
        for (TvtSchedule.TvtLevel l : tvtSchedule.getTvtLevelList()) {
            if (l.getId() != getTvtId()) {
                continue;
            }
            for (TvtSchedule.TvtRegistrator tr : l.getTvtRegistrator()) {
                SpawnTemplate reg = SpawnEngine.addNewSpawn(tr.getMapId(), tr.getNpcId(), tr.getX(), tr.getY(), tr.getZ(), tr.getH(), 0);
                regNpc.add((Npc) SpawnEngine.spawnObject(reg, 1));
            }
        }
    }

    public void despawnRegNpc() {
        for (VisibleObject obj : regNpc) {
            obj.getController().delete();
        }
        clearRegNpc();
    }

    public void setTvtId(int id) {
        this.tvtId = id;
    }

    public int getTvtId() {
        return tvtId;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public final int getStartTime() {
        return startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public boolean getIsStarted() {
        return isStarted;
    }

    public void setIsStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public TvtPlayerHolder getHolders() {
        return holders;
    }

    public int getLevel() {
        return level;
    }

    public int getMapId() {
        return mapId;
    }
}