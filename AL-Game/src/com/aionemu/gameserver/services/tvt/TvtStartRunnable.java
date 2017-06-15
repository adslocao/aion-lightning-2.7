package com.aionemu.gameserver.services.tvt;



public class TvtStartRunnable implements Runnable {

    private final int tvtId;
    private int startTime;
    private int duration;
    private int level;
    private int mapId;

    public TvtStartRunnable(int tvtId, int startTime, int duration, int level, int mapId) {
        this.tvtId = tvtId;
        this.startTime = startTime;
        this.duration = duration;
        this.level = level;
        this.mapId = mapId;
    }

    @Override
    public void run() {
        TvtService.getInstance().startTvt(getTvtId(), getStartTime(), getDuration(), getLevel(), getMapId());
    }

    public int getTvtId() {
        return tvtId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }
    
    public int getLevel(){
        return level;
    }
    
    public int getMapId(){
        return mapId;
    }
}
