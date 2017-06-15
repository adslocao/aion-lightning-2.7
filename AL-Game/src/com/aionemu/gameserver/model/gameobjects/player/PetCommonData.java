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
package com.aionemu.gameserver.model.gameobjects.player;

import java.sql.Timestamp;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * @author ATracer
 */
public class PetCommonData extends VisibleObjectTemplate {

	private int decoration;
	private String name;
	private final int petId;
	private Timestamp birthday;
	private int hungryLevel;

	private int nrFood = 0;
	private boolean cancelFood = false;
	private boolean feedingTime = true;
	private long curentTime;

	private final int petObjectId;
	private final int masterObjectId;

	private long startMoodTime;
	private int shuggleCounter;

	private int lastSentPoints;
	private long moodCdStarted;
	private long giftCdStarted;
	private Timestamp despawnTime;

	public PetCommonData(int petId, int masterObjectId) {
		this.petObjectId = IDFactory.getInstance().nextId();
		this.petId = petId;
		this.masterObjectId = masterObjectId;
	}

	public final int getDecoration() {
		return decoration;
	}

	public final void setDecoration(int decoration) {
		this.decoration = decoration;
	}

	@Override
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final int getPetId() {
		return petId;
	}

	public int getBirthday() {
		if (birthday == null)
			return 0;

		return (int) (birthday.getTime() / 1000);
	}

	public Timestamp getBirthdayTimestamp() {
		return birthday;
	}

	public void setBirthday(Timestamp birthday) {
		this.birthday = birthday;
	}

	public int getHungryLevel() {
		return hungryLevel;
	}

	public void setHungryLevel(int hungryLevel) {
		this.hungryLevel = hungryLevel;
	}

	public long getCurentTime() {
		return curentTime;
	}

	public void setCurentTime(long curentTime) {
		this.curentTime = curentTime;

	}

	public void setIsFeedingTime(boolean food) {
		this.feedingTime = food;
	}

	public boolean isFeedingTime() {
		return feedingTime;
	}

	public boolean getCancelFood() {
		return cancelFood;
	}

	public void setCancelFood(boolean cancelFood) {
		this.cancelFood = cancelFood;
	}

	public int getNrFood() {
		return nrFood;
	}

	public void setNrFood(int nrFood) {
		this.nrFood = nrFood;
	}

	/**
	 * @param feedingTime
	 */
	public void setFeedingTime(boolean feedingTime) {
		this.feedingTime = feedingTime;
	}

	public void setReFoodTime(final long reFoodTime) {
		setFeedingTime(false);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				feedingTime = true;
				curentTime = 0;
			}
		}, reFoodTime);
	}

	public long getTime() {
		long time = System.currentTimeMillis() - curentTime;
		if (time < 0 || time > 600000) {
			curentTime = 0;
			time = 0;
		}

		return 600000 - time == 600000 ? 0 : 600000 - time;
	}

	public int getObjectId() {
		return petObjectId;
	}

	public int getMasterObjectId() {
		return masterObjectId;
	}

	@Override
	public int getTemplateId() {
		return petId;
	}

	@Override
	public int getNameId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public final long getMoodStartTime() {
		return startMoodTime;
	}

	public final int getShuggleCounter() {
		return shuggleCounter;
	}

	public final void setShuggleCounter(int shuggleCounter) {
		this.shuggleCounter = shuggleCounter;
	}

	public final int getMoodPoints(boolean forPacket) {
		if (startMoodTime == 0)
			startMoodTime = System.currentTimeMillis();
		int points = Math.round((System.currentTimeMillis() - startMoodTime) / 1000f) + shuggleCounter * 1000;
		if (forPacket && points > 9000)
			return 9000;
		return points;
	}

	public final int getLastSentPoints() {
		return lastSentPoints;
	}

	public final void setLastSentPoints(int points) {
		lastSentPoints = points;
	}

	public final boolean increaseShuggleCounter() {
		if (getMoodRemainingTime() > 0)
			return false;
		this.moodCdStarted = System.currentTimeMillis();
		this.shuggleCounter++;
		return true;
	}

	public final void clearMoodStatistics() {
		this.startMoodTime = 0;
		this.shuggleCounter = 0;
	}

	public final void setStartMoodTime(long startMoodTime) {
		this.startMoodTime = startMoodTime;
	}

	/**
	 * @return moodCdStarted
	 */
	public long getMoodCdStarted() {
		return moodCdStarted;
	}

	/**
	 * @param moodCdStarted
	 *          the moodCdStarted to set
	 */
	public void setMoodCdStarted(long moodCdStarted) {
		this.moodCdStarted = moodCdStarted;
	}

	public int getMoodRemainingTime() {
		long stop = moodCdStarted + 600000;
		long remains = stop - System.currentTimeMillis();
		if (remains <= 0) {
			setMoodCdStarted(0);
			return 0;
		}
		return (int) (remains / 1000);
	}

	/**
	 * @return the giftCdStarted
	 */
	public long getGiftCdStarted() {
		return giftCdStarted;
	}

	/**
	 * @param giftCdStarted
	 *          the giftCdStarted to set
	 */
	public void setGiftCdStarted(long giftCdStarted) {
		this.giftCdStarted = giftCdStarted;
	}

	public int getGiftRemainingTime() {
		long stop = giftCdStarted + 3600 * 1000;
		long remains = stop - System.currentTimeMillis();
		if (remains <= 0) {
			setGiftCdStarted(0);
			return 0;
		}
		return (int) (remains / 1000);
	}

	/**
	 * @return the despawnTime
	 */
	public Timestamp getDespawnTime() {
		return despawnTime;
	}

	/**
	 * @param despawnTime
	 *          the despawnTime to set
	 */
	public void setDespawnTime(Timestamp despawnTime) {
		this.despawnTime = despawnTime;
	}

	/**
	 * Saves mood data to DB
	 */
	public void savePetMoodData() {
		DAOManager.getDAO(PlayerPetsDAO.class).savePetMoodData(this);
	}

}
