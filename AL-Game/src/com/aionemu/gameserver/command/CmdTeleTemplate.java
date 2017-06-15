package com.aionemu.gameserver.command;

public class CmdTeleTemplate {
	//private String name;
	private int worldId;
	private float x;
	private float y;
	private float z;
	
	public CmdTeleTemplate(int worldId, float x, float y, float z) {
		this.worldId = worldId;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getWorldId() { return worldId; }
	public float getX() { return x; }
	public float getY() { return y; }
	public float getZ() { return z; }
	
}
