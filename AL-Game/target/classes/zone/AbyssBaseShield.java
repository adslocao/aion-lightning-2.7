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
package zone;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;


/**
 * @author MrPoke
 *
 */
@ZoneNameAnnotation("ASMODIANS_BASE_400010000 ELYOS_BASE_400010000")
public class AbyssBaseShield implements ZoneHandler {

	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		Creature actingCreature = creature.getActingCreature();
		if (actingCreature instanceof Player && !((Player)actingCreature).isGM()){
			switch(zone.getZoneTemplate().getName()){
				case ASMODIANS_BASE_400010000:
					if (((Player)actingCreature).getRace() == Race.ELYOS)
						creature.getController().die();
					break;
				case ELYOS_BASE_400010000:
					if (((Player)actingCreature).getRace() == Race.ASMODIANS)
						creature.getController().die();
					break;
			}
		}
	}

	@Override
	public void onLeaveZone(Creature player, ZoneInstance zone) {
	}

}
