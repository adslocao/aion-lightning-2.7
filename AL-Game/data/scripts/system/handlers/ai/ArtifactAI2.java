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
package ai;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Request;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.controllers.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.model.templates.siegelocation.ArtifactActivation;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_ARTIFACT_INFO3;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer, Source
 */
@AIName("artifact")
public class ArtifactAI2 extends NpcAI2 {

	private transient boolean IDLE = true;

	private Map<Integer, ItemUseObserver> observers = new HashMap<Integer, ItemUseObserver>();

	@Override
	protected SiegeSpawnTemplate getSpawnTemplate() {
		return (SiegeSpawnTemplate) super.getSpawnTemplate();
	}

	@Override
	protected void handleDialogStart(final Player player) {
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
		AI2Actions.addRequest(this, player, 160028, new AI2Request() {

			@Override
			public void acceptRequest(Creature requester, Player responder) {

				AI2Actions.addRequest(ArtifactAI2.this, player, 160016, new AI2Request() {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						onActivate(responder);
					}

				}, new DescriptionId(2 * 716570 + 1), SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId())
					.getTemplate().getActivation().getCount());

			}

		}, loc);
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}

	public void onActivate(final Player player) {
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());

		// Get Skill id, item, count and target defined for each artifact.
		ArtifactActivation activation = loc.getTemplate().getActivation();
		int skillId = activation.getSkillId();
		final int itemId = activation.getItemId();
		final int count = activation.getCount();
		final SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);

		if (skillTemplate == null) {
			LoggerFactory.getLogger(ArtifactAI2.class).error("No skill template for artifact effect id : " + skillId);
			return;
		}

		if (loc.getCoolDown() > 0 || !IDLE) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300702)); // artifact not aveaileble now
			return;
		}

		if (loc.getLegionId() != 0)
			if (!player.isLegionMember() || player.getLegion().getLegionId() != loc.getLegionId()
				|| !player.getLegionMember().hasRights(LegionPermissionsMask.ARTIFACT)) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300703)); // you dont have rights to use
				return;
			}
		if (player.getInventory().getItemCountByItemId(itemId) < count) {
			return;
		}
		LoggerFactory.getLogger(ArtifactAI2.class).debug("Artifact {} actived by {}.", getSpawnTemplate().getSiegeId(),
			player.getName());
		if (!IDLE)
			return;
		IDLE = false;
		// Brodcast start activation.
		final SM_SYSTEM_MESSAGE startMessage = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CASTING(loc.getRace().getDescriptionId(),
			player.getName(), new DescriptionId(skillTemplate.getNameId()));
		final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId(), 1);
		player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, startMessage);
				PacketSendUtility.sendPacket(player, artifactInfo);
			}
		});

		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 1));
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()),
			true);

		ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()),
					true);
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
				final SM_SYSTEM_MESSAGE message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CANCELED(loc.getRace().getDescriptionId(),
					new DescriptionId(skillTemplate.getNameId()));
				final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId(), 0);
				getOwner().getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, message);
						PacketSendUtility.sendPacket(player, artifactInfo);
					}
				});
			}
		};
		observers.put(player.getObjectId(), observer);
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				ItemUseObserver observer = observers.remove(player.getObjectId());
				if (observer != null) {
					player.getObserveController().removeObserver(observer);
				}
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()),
					true);
				if (!player.getInventory().decreaseByItemId(itemId, count))
					return;
				final SM_SYSTEM_MESSAGE message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CORE_CASTING(loc.getRace().getDescriptionId(),
					new DescriptionId(skillTemplate.getNameId()));
				final SM_ABYSS_ARTIFACT_INFO3 artifactInfo = new SM_ABYSS_ARTIFACT_INFO3(loc.getLocationId(), 2);
				player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, message);
						PacketSendUtility.sendPacket(player, artifactInfo);
					}
				});
				loc.setLastActivation(System.currentTimeMillis());
				IDLE = true;
				final ScheduledFuture<?> s;
				if (loc.getTemplate().getRepeatCount() == 1) {
					s = ThreadPoolManager.getInstance().schedule(new ArtifactUseSkill(loc, player, skillTemplate),
						13000);
				}
				else {
					s = ThreadPoolManager.getInstance().scheduleAtFixedRate(
						new ArtifactUseSkill(loc, player, skillTemplate), loc.getTemplate().getRepeatInterval() * 1000,
						13000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							s.cancel(true);
						}
					}, 13000 + (loc.getTemplate().getRepeatInterval() * loc.getTemplate().getRepeatCount() * 1000));
				}

			}
		}, 10000));
	}

	class ArtifactUseSkill implements Runnable {

		private ArtifactLocation artifact;
		private Player player;
		private SkillTemplate skill;
		private int runCount = 1;
		private SM_ABYSS_ARTIFACT_INFO3 artifactFire;
		private SM_ABYSS_ARTIFACT_INFO3 artifactIdle;
		private SM_SYSTEM_MESSAGE message;

		/**
		 * @param artifact
		 * @param targetRace
		 */
		private ArtifactUseSkill(ArtifactLocation artifact, Player activator, SkillTemplate skill) {
			this.artifact = artifact;
			this.player = activator;
			this.skill = skill;
			this.artifactFire = new SM_ABYSS_ARTIFACT_INFO3(artifact.getLocationId(), 3);
			this.artifactIdle = new SM_ABYSS_ARTIFACT_INFO3(artifact.getLocationId(), 0);
			this.message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_FIRE(artifact.getRace().getDescriptionId(), player.getName(),
				new DescriptionId(skill.getNameId()));
		}

		@Override
		public void run() {
			if (artifact.getTemplate().getRepeatCount() < runCount)
				return;

			final boolean start = (runCount == 1);
			final boolean end = (runCount == artifact.getTemplate().getRepeatCount());

			runCount++;
			player.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					if (start) {
						PacketSendUtility.sendPacket(player, message);
					}
					PacketSendUtility.sendPacket(player, artifactFire);
					if (end) {
						PacketSendUtility.sendPacket(player, artifactIdle);
					}
				}
			});
			boolean pc = skill.getStartconditions().onlyPC();
			System.out.println("In artifact zone count: "+artifact.getCreatures().size());
			for (Creature creature : artifact.getCreatures().values()) {
				if (creature.getActingCreature() instanceof Player || (creature instanceof SiegeNpc && !pc)) {
					switch (skill.getProperties().getTargetRelation()) {
						case FRIEND:
							if (player.isEnemy(creature))
								continue;
							break;
						case ENEMY:
							if (!player.isEnemy(creature))
								continue;
					}
					AI2Actions.applyEffect(ArtifactAI2.this, skill, creature);
				}
			}
		}
	}
}
