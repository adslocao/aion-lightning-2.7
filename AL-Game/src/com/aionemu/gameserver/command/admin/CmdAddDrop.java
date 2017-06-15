package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/*syntax //adddrop <mobid> <itemid> <min> <max> <chance>*/

/**
 * @author ATracer
 */
public class CmdAddDrop extends BaseCommand {

	
	public void execute(Player admin, String... params) {
		PacketSendUtility.sendMessage(admin, "Now this is not implemented.");
		/*
		if (params.length != 5) {
			showHelp(admin);
			return;
		}

		try {
			final int mobId = Integer.parseInt(params[0]);
			final int itemId = Integer.parseInt(params[1]);
			final int min = Integer.parseInt(params[2]);
			final int max = Integer.parseInt(params[3]);
			final float chance = Float.parseFloat(params[4]);

			DropList dropList = DropRegistration.getInstance().getDropList();

			DropTemplate dropTemplate = new DropTemplate(mobId, itemId, min, max, chance, false);
			dropList.addDropTemplate(mobId, dropTemplate);

			DB.insertUpdate("INSERT INTO droplist (" + "`mob_id`, `item_id`, `min`, `max`, `chance`)" + " VALUES "
				+ "(?, ?, ?, ?, ?)", new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
					ps.setInt(1, mobId);
					ps.setInt(2, itemId);
					ps.setInt(3, min);
					ps.setInt(4, max);
					ps.setFloat(5, chance);
					ps.execute();
				}
			});
		}
		catch (Exception ex) {
			PacketSendUtility.sendMessage(player, "Only numbers are allowed");
			return;
		}
		*/
	}

}
