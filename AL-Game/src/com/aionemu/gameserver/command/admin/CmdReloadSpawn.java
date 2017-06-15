package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author Luno
 */
public class CmdReloadSpawn extends BaseCommand {


	public void execute(Player player, String... params) {
		int worldId = 0;
		if (params.length == 1 && "this".equals(params[0])) {
			worldId = player.getWorldId();
		}

		final int worldIdFinal = worldId;
		// despawn all
		World.getInstance().doOnAllObjects(new Visitor<VisibleObject>() {

			@Override
			public void visit(VisibleObject object) {
				if (worldIdFinal != 0 && object.getWorldId() != worldIdFinal) {
					return;
				}
				if (object instanceof Npc || object instanceof Gatherable || object instanceof StaticObject) {
					object.getController().delete();
				}
			}
		});

		if (worldId == 0) {
			SpawnEngine.spawnAll();
		}
		else {
			SpawnEngine.spawnWorldMap(worldId);
		}
	}

}
