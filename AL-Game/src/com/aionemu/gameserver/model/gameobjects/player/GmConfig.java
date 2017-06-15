package com.aionemu.gameserver.model.gameobjects.player;

public enum GmConfig {
	GM_GMLIST_OFF(1),
	GM_ONGMLOGIN_OFF(2),
	GM_WHISPER_OFF(4);
	
	private int value = 0;
	
	GmConfig(int value) { this.value = value; }
	public int getValue() { return value; }
	
}
