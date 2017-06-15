/**
 * This file is part of aion-lightning <aion-lightning.org>.
 * 
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionlightning.loginserver.network.gameserver.serverpackets;

import com.aionlightning.loginserver.model.Account;
import com.aionlightning.loginserver.network.gameserver.GsConnection;
import com.aionlightning.loginserver.network.gameserver.GsServerPacket;
import com.aionlightning.loginserver.service.ptransfer.PlayerTransferRequest;
import com.aionlightning.loginserver.service.ptransfer.PlayerTransferResultStatus;
import com.aionlightning.loginserver.service.ptransfer.PlayerTransferTask;

/**
 * @author KID
 */
public class SM_PTRANSFER_RESPONSE extends GsServerPacket {
	private PlayerTransferResultStatus result;
	private Account account;
	private PlayerTransferRequest request;
	private int taskId;
	private String reason;
	private PlayerTransferTask task;

	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, int taskId) {
		this.result = result;
		this.taskId = taskId;
	}

	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, PlayerTransferRequest request) {
		this.result = result;
		this.request = request;
		this.account = request.targetAccount;
		this.taskId = request.taskId;
	}
	
	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, int taskId, String reason) {
		this.result = result;
		this.taskId = taskId;
		this.reason = reason;
	}

	public SM_PTRANSFER_RESPONSE(PlayerTransferResultStatus result, PlayerTransferTask task) {
		this.result = result;
		this.task = task;
	}

	@Override
	protected void writeImpl(GsConnection con) {
		writeC(12);
		writeD(result.getId());
		switch(result) {
			case SEND_INFO:
				writeD(request.targetAccountId);
				writeD(taskId);
				writeS(request.name);
				writeS(account.getName());
				writeD(request.db.length);
				writeB(request.db);
				break;
			case OK:
				writeD(taskId);
				break;
			case ERROR:
				writeD(taskId);
				writeS(reason);
				break;
			case PERFORM_ACTION:
				writeC(task.sourceServerId);
				writeC(task.targetServerId);
				writeD(task.sourceAccountId);
				writeD(task.targetAccountId);
				writeD(task.playerId);
				writeD(task.id);
				break;
		}
	}
}
