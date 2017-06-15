package com.aionemu.gameserver.cqfd.Lisener;

public class CQFDTimeEvent implements Runnable{
	private final CQFDListener lisener;
	public CQFDTimeEvent(CQFDListener classs){
		lisener = classs;
	}

	@Override
	public void run(){
		lisener.onLisenerEvent(null);
	}
}
