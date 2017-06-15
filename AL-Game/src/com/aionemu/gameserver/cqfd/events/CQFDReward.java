package com.aionemu.gameserver.cqfd.events;

public class CQFDReward {
	private final int _itemId;
	private int _count;
	
	private int isWinnerReward = 0;
	public CQFDReward(int itemId, int count){
		_itemId = itemId;
		_count = count;
	}
	
	public int getItemId(){
		return _itemId;
	}
	public int getItemCount(){
		return _count;
	}
	public void setItemCount(int count){
		_count = count;
	}
	
	public boolean isWinnerReward(){
		return isWinnerReward == 1;
	}
	public boolean isNeutralReward(){
		return isWinnerReward == -1;
	}
}
