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
package com.aionemu.gameserver.services.toypet;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import java.util.Collection;

/**
 * @author M@xx, IlBuono, xTz
 */
public class PetService {

	public static PetService getInstance() {
		return SingletonHolder.instance;
	}

	private PetService() {
	}

	public void renamePet(Player player, String name) {
		Pet pet = player.getPet();
		if (pet != null) {
			pet.getCommonData().setName(name);
			DAOManager.getDAO(PlayerPetsDAO.class).updatePetName(pet.getCommonData());
			PacketSendUtility.broadcastPacket(player, new SM_PET(10, pet), true);
		}
	}

	public void onPlayerLogin(Player player) {
		Collection<PetCommonData> playerPets = player.getPetList().getPets();
		if (playerPets != null && playerPets.size() > 0)
			PacketSendUtility.sendPacket(player, new SM_PET(0, playerPets));
	}

	public void removeObject(int objectId, int count, int action, Player player) {
		Item item = player.getInventory().getItemByObjId(objectId);
		if (item == null || player.getPet() == null || count > item.getItemCount())
			return;

		Pet pet = player.getPet();
		pet.getCommonData().setCancelFood(false);
		PacketSendUtility.sendPacket(player, new SM_PET(1, action, item.getObjectId(), count, pet));
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FEEDING, 0, player.getObjectId()));

		schedule(pet, player, item, count, action);
	}

	private void schedule(final Pet pet, final Player player, final Item item, final int count, final int action) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!pet.getCommonData().getCancelFood())
					checkFeeding(pet, player, item, count, action);
			}
		}, 3000);
	}

	private void checkFeeding(Pet pet, Player player, Item item, int count, int action) {
		int exp = (int) (player.getRates().getPetFeedingRate() * 16779264);

		PetCommonData commonData = pet.getCommonData();
		if (!commonData.getCancelFood()) {
			if (isFood(item.getItemId())) {
				commonData.setNrFood(commonData.getNrFood() + 1);
				player.getInventory().decreaseItemCount(item, 1);
				commonData.setHungryLevel(commonData.getHungryLevel() + exp);
				PacketSendUtility.sendPacket(player, new SM_PET(2, action, item.getObjectId(), count - commonData.getNrFood(),
					pet));
			}
			else {
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				return;
			}

			if (commonData.getHungryLevel() > 1560000000) {
				commonData.setReFoodTime(600000);
				commonData.setCurentTime(System.currentTimeMillis());
				DAOManager.getDAO(PlayerPetsDAO.class).setTime(player, pet.getPetId(), System.currentTimeMillis());
				commonData.setHungryLevel(0);
				commonData.setIsFeedingTime(false);
				int i = doReward(player.getLevel(), pet.getPetId());

				PacketSendUtility.sendPacket(player, new SM_PET(6, action, i, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				PacketSendUtility.sendPacket(player, new SM_PET(7, action, 0, 0, pet)); // 2151591961
				ItemService.addItem(player, i, 1);
			}
			else if (pet.getCommonData().getNrFood() < count)
				schedule(pet, player, item, count, action);
			else {
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
			}
		}
	}

	//TODO: Move rewards to xml like other projects maybe?
	private int doReward(int level, int petId) {
		switch (petId) {
			case 900043: // Button-Eye Mookie (Jewels)
				if (level < 20)
					return 188050758;
				else if (level >= 20 && level < 30)
					return 188050759;
				else if (level >= 30 && level < 40)
					return 188050760;
				else if (level >= 40 && level < 50)
					return 188050761;
				else if (level > 50)
					return 188050762;
			case 900044: // Mellow Ksellid (Ore)
				if (level < 20)
					return 188050753;
				else if (level >= 20 && level < 30)
					return 188050754;
				else if (level >= 30 && level < 40)
					return 188050755;
				else if (level >= 40 && level < 50)
					return 188050756;
				else if (level > 50)
					return 188050757;
			case 900045: // Rotund Ayas (Dyes)
				if (level < 20)
					return 188050988;
				else if (level >= 20 && level < 30)
					return 188050989;
				else if (level >= 30 && level < 40)
					return 188050990;
				else if (level >= 40 && level < 50)
					return 188050991;
				else if (level > 50)
					return 188050992;
			case 900046: // Hungry Porgus (Mystery Item) 
				if (level < 20)
					return 188050993;
				else if (level >= 0 && level < 30)
					return 188050994;
				else if (level >= 30 && level < 40)
					return 188050995;
				else if (level >= 40 && level < 50)
					return 188050996;
				else if (level > 50)
					return 188050997;
			case 900047: // Pudgy Porgus (Enchant & Manastones)
				if (level < 20)
					return 188050763;
				else if (level >= 20 && level < 30)
					return 188050764;
				else if (level >= 30 && level < 40)
					return 188050765;
				else if (level >= 40 && level < 50)
					return 188050766;
				else if (level > 50)
					return 188050767;
			case 900048: // Mean Poroco (Enchant & Manastones) 
				if (level < 20)
					return 188051003;
				else if (level >= 20 && level < 30)
					return 188051004;
				else if (level >= 30 && level < 40)
					return 188051005;
				else if (level >= 40 && level < 50)
					return 188051006;
				else if (level > 50)
					return 188051007;
			case 900049: // HeadStrong Poroco (Enchant & Manastones)
				if (level < 20)
					return 188051008;
				else if (level >= 20 && level < 30)
					return 188051009;
				else if (level >= 30 && level < 40)
					return 188051010;
				else if (level >= 40 && level < 50)
					return 188051011;
				else if (level > 50)
					return 188051012;
			case 900050: // Azure Drakie (Balaur Material Bundle)
				if (level < 20)
					return 188050953;
				else if (level >= 20 && level < 30)
					return 188050954;
				else if (level >= 30 && level < 40)
					return 188050955;
				else if (level >= 40 && level < 50)
					return 188050956;
				else if (level > 50)
					return 188050957;
			case 900053: // Tin Narky (Aether)
				if (level < 20)
					return 188050967;
				else if (level >= 20 && level < 30)
					return 188050968;
				else if (level >= 30 && level < 40)
					return 188050969;
				else if (level >= 40 && level < 50)
					return 188050970;
				else if (level > 50)
					return 188050971;
			case 900054: // Pink Griffo (Aether)
				if (level < 20)
					return 188050972;
				else if (level >= 20 && level < 30)
					return 188050973;
				else if (level >= 30 && level < 40)
					return 188050974;
				else if (level >= 40 && level < 50)
					return 188050975;
				else if (level > 50)
					return 188050976;
			case 900057: // Aqua Griffo (Aether)
				if (level < 20)
					return 188051023;
				else if (level >= 20 && level < 30)
					return 188051024;
				else if (level >= 30 && level < 40)
					return 188051025;
				else if (level >= 40 && level < 50)
					return 188051026;
				else if (level > 50)
					return 188051027;
			case 900058: // Sunset Drakie (Balaur Material)
				if (level < 20)
					return 188051018;
				else if (level >= 20 && level < 30)
					return 188051019;
				else if (level >= 30 && level < 40)
					return 188051020;
				else if (level >= 40 && level < 50)
					return 188051021;
				else if (level > 50)
					return 188051022;
			case 900086: // Spurred Crestlich (Enchant & Manastones) 
				if (level < 20)
					return 188051063;
				else if (level >= 20 && level < 30)
					return 188051064;
				else if (level >= 30 && level < 40)
					return 188051065;
				else if (level >= 40 && level < 50)
					return 188051066;
				else if (level > 50)
					return 188051067;
			case 900088: // Pointytail Acarun (Balaur Material)
				if (level < 20)
					return 188051068;
				else if (level >= 20 && level < 30)
					return 188051069;
				else if (level >= 30 && level < 40)
					return 188051070;
				else if (level >= 40 && level < 50)
					return 188051071;
				else if (level > 50)
					return 188051072;
			case 900090: // Golden Radama (Aether)
				if (level < 20)
					return 188051073;
				else if (level >= 20 && level < 30)
					return 188051074;
				else if (level >= 30 && level < 40)
					return 188051075;
				else if (level >= 40 && level < 50)
					return 188051076;
				else if (level > 50)
					return 188051077;
			case 900093: // Hungry Porgus (Enchant & Manastones) 
				if (level < 20)
					return 188051080;
				else if (level >= 20 && level < 30)
					return 188051081;
				else if (level >= 30 && level < 40)
					return 188051082;
				else if (level >= 40 && level < 50)
					return 188051083;
				else if (level > 50)
					return 188051084;
			case 900094: // Pudgy Porgus (Mystery Item) 
				if (level < 20)
					return 188051085;
				else if (level >= 20 && level < 30)
					return 188051086;
				else if (level >= 30 && level < 40)
					return 188051087;
				else if (level >= 40 && level < 50)
					return 188051088;
				else if (level > 50)
					return 188051089;
			case 900097: // Azure Drakie (Balaur Material Bundle) [Elyos]
			case 900098: // Azure Drakie (Balaur Material Bundle) [Asmodian]
				if (level < 20)
					return 188050953;
				else if (level >= 20 && level < 30)
					return 188050954;
				else if (level >= 30 && level < 40)
					return 188050955;
				else if (level >= 40 && level < 50)
					return 188050956;
				else if (level > 50)
					return 188050957;
			case 900099: // HeadStrong Poroco (Enchant & Manastones) [Elyos]
			case 900100: // HeadStrong Poroco (Enchant & Manastones) [Asmodian]
				if (level < 20)
					return 188051008;
				else if (level >= 20 && level < 30)
					return 188051009;
				else if (level >= 30 && level < 40)
					return 188051010;
				else if (level >= 40 && level < 50)
					return 188051011;
				else if (level > 50)
					return 188051012;
			case 900103: // Ten-Thousand-Year-Old Golden Saam (Aether)
				if (level < 20)
					return 188051355;
				else if (level >= 20 && level < 30)
					return 188051356;
				else if (level >= 30 && level < 40)
					return 188051357;
				else if (level >= 40 && level < 50)
					return 188051358;
				else if (level > 50)
					return 188051359;
			case 900117: //Red Cap Kobok (Mystery Items)
					return 188051360;
			case 900125: //Runaway Poppy (Mystery Items)
				if (level < 20)
					return 188051384;
				else if (level >= 20 && level < 30)
					return 188051385;
				else if (level >= 30)
					return 188051386;
		}
		return 0;
	}

	// temporary
	public boolean isFood(int itemId) {
		switch (itemId) {
			case 182006413:
			case 182006414:
			case 182006415:
			case 182006416:
			case 182006417:
			case 182006418:
			case 182004677:
			case 182004557:
			case 182005067:
			case 182003588:
			case 182005068:
			case 182004559:
			case 182004679:
			case 182005069:
			case 182004680:
			case 182004560:
			case 182005070:
			case 182004200:
			case 182004681:
			case 182004561:
			case 182004682:
			case 182005366:
			case 182004562:
			case 182005631:
			case 182006042:
			case 182006114:
			case 182005727:
			case 182005805:
			case 182005703:
			case 182003764:
			case 182003754:
			case 182003765:
			case 182003755:
			case 182003766:
			case 182003756:
			case 182004757:
			case 182003545:
			case 182003797:
			case 182005351:
			case 182003546:
			case 182003798:
			case 182005352:
			case 182003768:
			case 182003758:
			case 182003799:
			case 182005353:
			case 182003769:
			case 182004759:
			case 182004760:
			case 182003770:
			case 182003760:
			case 182003800:
			case 182005354:
			case 182004761:
			case 182003761:
			case 182004071:
			case 182003801:
			case 182005355:
			case 182003802:
			case 182005356:
			case 182004762:
			case 182003772:
			case 182004072:
			case 182005551:
			case 182005545:
			case 182005543:
			case 182005605:
			case 182004254:
			case 182005054:
			case 182004765:
			case 182005055:
			case 182004766:
			case 182005056:
			case 182004506:
			case 182005046:
			case 182005047:
			case 182004257:
			case 182004767:
			case 182004527:
			case 182004528:
			case 182004768:
			case 182004748:
			case 182003660:
			case 182004508:
			case 182005058:
			case 182003661:
			case 182004509:
			case 182004529:
			case 182004749:
			case 182005049:
			case 182005059:
			case 182004259:
			case 182004769:
			case 182004530:
			case 182004770:
			case 182004750:
			case 182005050:
			case 182004771:
			case 182005051:
			case 182004531:
			case 182005325:
			case 182004532:
			case 182004772:
			case 182005326:
			case 182004752:
			case 182004262:
			case 182005052:
			case 182005062:
			case 182005697:
			case 182005741:
			case 182006174:
			case 182005643:
			case 182005855:
			case 182005693:
			case 182005803:
			case 182005745:
			case 182005801:
			case 182004493:
			case 182004393:
			case 182003903:
			case 182003904:
			case 182004394:
			case 182004225:
			case 182004086:
			case 182004226:
			case 182004396:
			case 182004426:
			case 182004496:
			case 182004087:
			case 182004467:
			case 182003907:
			case 182004227:
			case 182004137:
			case 182004017:
			case 182003857:
			case 182003486:
			case 182004297:
			case 182005087:
			case 182004427:
			case 182004497:
			case 182003837:
			case 182003485:
			case 182004296:
			case 182004428:
			case 182004498:
			case 182003858:
			case 182003838:
			case 182003888:
			case 182004088:
			case 182005088:
			case 182004018:
			case 182004128:
			case 182004019:
			case 182004129:
			case 182003859:
			case 182005089:
			case 182004939:
			case 182004139:
			case 182004919:
			case 182004949:
			case 182004140:
			case 182003488:
			case 182004300:
			case 182202103:
			case 182005090:
			case 182004920:
			case 182004940:
			case 182004950:
			case 182003910:
			case 182004230:
			case 182004090:
			case 182004500:
			case 182004410:
			case 182004470:
			case 182004141:
			case 182004091:
			case 182003861:
			case 182004941:
			case 182004951:
			case 182003489:
			case 182004301:
			case 182004471:
			case 182005091:
			case 182004921:
			case 182004131:
			case 182004431:
			case 182004401:
			case 182004411:
			case 182003841:
			case 182004231:
			case 182004402:
			case 182003862:
			case 182003842:
			case 182003490:
			case 182004302:
			case 182004412:
			case 182004132:
			case 182004432:
			case 182004092:
			case 182004472:
			case 182003892:
			case 182004232:
			case 182004022:
			case 182004922:
			case 182005276:
			case 182005595:
			case 182005671:
			case 182005559:
			case 182005569:
			case 182005691:
			case 182005619:
			case 182005673:
			case 182005775:
			case 182005609:
			case 182004976:
			case 182003605:
			case 152012502:
			case 182003617:
			case 182003653:
			case 182003648:
			case 182003654:
			case 182003618:
			case 182004448:
			case 182004978:
			case 182003612:
			case 182003606:
			case 182003558:
			case 182003600:
			case 182003607:
			case 182003559:
			case 182003601:
			case 182003619:
			case 182004449:
			case 182004980:
			case 182005171:
			case 182005151:
			case 182004981:
			case 182005111:
			case 182004982:
			case 182005401:
			case 182006078:
			case 182005787:
			case 182005903:
			case 182006234:
			case 182006270:
			case 182003808:
			case 182003809:
			case 182003810:
			case 182003811:
			case 182003812:
				return true;
		}
		return false;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PetService instance = new PetService();
	}

	/**
	 * @param player
	 * @param dopingItemId
	 */
	public void feedDoping(Player player, int dopingItemId) {
		// TODO Auto-generated method stub

	}
}
