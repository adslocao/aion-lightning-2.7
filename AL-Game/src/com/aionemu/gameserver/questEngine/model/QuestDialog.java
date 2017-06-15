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
package com.aionemu.gameserver.questEngine.model;

/**
 * Quest dialog mappings
 * 
 * @author vlog
 */
public enum QuestDialog {

	NULL(0), // default mapping
	// Quest selection
	USE_OBJECT(-1),
	START_DIALOG(26),
	FINISH_DIALOG(1008),
	ACCEPT_QUEST(1002),
	REFUSE_QUEST(1003),
	ASK_ACCEPTION(1007),
	CHECK_COLLECTED_ITEMS(34),
	// Dialog selection
	SELECT_ACTION_1011(1011),
	SELECT_ACTION_1012(1012),
	SELECT_ACTION_1013(1013),
	SELECT_ACTION_1352(1352),
	SELECT_ACTION_1353(1353),
	SELECT_ACTION_1354(1354),
	SELECT_ACTION_1438(1438),
	SELECT_ACTION_1609(1609),
	SELECT_ACTION_1693(1693),
	SELECT_ACTION_1694(1694),
	SELECT_ACTION_1695(1695),
	SELECT_ACTION_2034(2034),
	SELECT_ACTION_2035(2035),
	SELECT_ACTION_2036(2036),
	SELECT_ACTION_2376(2376),
	SELECT_ACTION_2377(2377),
	SELECT_ACTION_2546(2546),
	SELECT_ACTION_2717(2717),
	SELECT_ACTION_2718(2718),
	SELECT_ACTION_2720(2720),
	SELECT_ACTION_3058(3058),
	SELECT_ACTION_3143(3143),
	SELECT_ACTION_3399(3399),
	SELECT_ACTION_3400(3400),
	SELECT_ACTION_4081(4081),
	SELECT_ACTION_4763(4763),
	SELECT_REWARD(1009),
	SELECT_NO_REWARD(18),
	// Step changing
	STEP_TO_1(10000),
	STEP_TO_2(10001),
	STEP_TO_3(10002),
	STEP_TO_4(10003),
	STEP_TO_5(10004),
	STEP_TO_6(10005),
	STEP_TO_7(10006),
	STEP_TO_8(10007),
	STEP_TO_9(10008),
	STEP_TO_10(10009),
	STEP_TO_11(10010),
	STEP_TO_12(10011),
	STEP_TO_13(10012),
	STEP_TO_14(10013),
	STEP_TO_20(10019),
	STEP_TO_21(10020),
	STEP_TO_30(10029),
	STEP_TO_31(10030),
	STEP_TO_40(10039),
	STEP_TO_41(10040),
	SET_REWARD(10255),
	// non-quest
	BUY_FOR_AP(50),
	EXCHANGE_COIN(54);

	private int id;

	private QuestDialog(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}
}
