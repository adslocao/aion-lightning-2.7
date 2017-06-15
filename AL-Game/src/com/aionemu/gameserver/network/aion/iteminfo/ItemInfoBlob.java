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
package com.aionemu.gameserver.network.aion.iteminfo;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.PacketWriteHelper;

/**
 * Entry item info packet data (contains blob entries with detailed info).
 * 
 * @author -Nemesiss-
 *
 */
public class ItemInfoBlob extends PacketWriteHelper{
	protected final Player player;
	protected final Item item;

	private ItemBlobEntry itemBlobEnt;

	public ItemInfoBlob(Player player, Item item) {
		this.player = player;
		this.item = item;
	}

	@Override
	public void writeMe(ByteBuffer buf) {
		ByteBuffer buf2 = buf.duplicate().order(buf.order());
		writeH(buf, 0);//size - not known
		int curPos = buf.position();
		itemBlobEnt.writeMe(buf);
		writeH(buf2, buf.position()-curPos);//size - known now
	}

	public void addBlobEntry(ItemBlobType type)
	{
		ItemBlobEntry ent = type.newBlobEntry();
		ent.setParent(this);

		if(itemBlobEnt == null)
			itemBlobEnt = ent;
		else
			itemBlobEnt.addBlobEntry(ent);
	}

	public static ItemBlobEntry newBlobEntry(ItemBlobType type, Player player, Item item)
	{
		ItemBlobEntry ent = type.newBlobEntry();
		ent.setParent(new ItemInfoBlob(player, item));
		return ent;
	}

	public static ItemInfoBlob getFullBlob(Player player, Item item)
	{
		ItemInfoBlob blob = new ItemInfoBlob(player, item);

		ItemTemplate itemTemplate = item.getItemTemplate();

		if(item.getConditioningInfo() != null)
			blob.addBlobEntry(ItemBlobType.CONDITIONING_INFO);

		if(item.hasFusionedItem())
			blob.addBlobEntry(ItemBlobType.COMPOSITE_ITEM);

		if(item.getEquipmentType() != EquipType.NONE)
		{
			//EQUIPPED SLOT
			blob.addBlobEntry(ItemBlobType.EQUIPPED_SLOT);

			//SLOT INFO
			if(itemTemplate.getArmorType() != null) {
				switch(itemTemplate.getArmorType()) {
					case CLOTHES:
						blob.addBlobEntry(ItemBlobType.SLOTS_CLOTHES);
						break;
					case SHIELD:
						blob.addBlobEntry(ItemBlobType.SLOTS_SHIELD);
						break;
					default:
						blob.addBlobEntry(ItemBlobType.SLOTS_ARMOR);
						break;
				}
			} else if(itemTemplate.isWeapon())
				blob.addBlobEntry(ItemBlobType.SLOTS_WEAPON);
			else if(item.getEquipmentType() == EquipType.STIGMA)
				blob.addBlobEntry(ItemBlobType.STIGMA_INFO);
			else if(item.getEquipmentType() == EquipType.ARMOR)
				blob.addBlobEntry(ItemBlobType.SLOTS_ACCESSORY);

			//MANA STONES
			blob.addBlobEntry(ItemBlobType.MANA_SOCKETS);

			//STATS MOD
			//xx blob.addBlobEntry(ItemBlobType.STATS_MOD);
		}

		//GENERAL INFO
		blob.addBlobEntry(ItemBlobType.GENERAL_INFO);

		return blob;
	}

	public enum ItemBlobType {
		GENERAL_INFO(0x00){ @Override	ItemBlobEntry newBlobEntry() { return new GeneralInfoBlobEntry(); }},							//30 + S	OK
		SLOTS_WEAPON(0x01){ @Override	ItemBlobEntry newBlobEntry() { return new WeaponInfoBlobEntry(); }},							//9				OK
		SLOTS_ARMOR(0x02){ @Override	ItemBlobEntry newBlobEntry() { return new ArmorInfoBlobEntry(); }},								//13			OK
		SLOTS_SHIELD(0x03){ @Override	ItemBlobEntry newBlobEntry() { return new ShieldInfoBlobEntry(); }},							//13			OK [Not handled before]
		SLOTS_ACCESSORY(0x04){ @Override	ItemBlobEntry newBlobEntry() { return new AccessoryInfoBlobEntry(); }},				//9				OK [Not handled before]
		//missing(0x05),//9 [dd]				 [Not handled before]
		EQUIPPED_SLOT(0x06){ @Override	ItemBlobEntry newBlobEntry() { return new EquippedSlotBlobEntry(); }},					//5 			OK
		STIGMA_INFO(0x07){ @Override	ItemBlobEntry newBlobEntry() { return new StigmaInfoBlobEntry(); }},							//259			OK
		//missing(0x08),//5 [d] Stigma Shard?					 [Not handled before]
		//missing(0x09),//15?						 [Not handled before]
		STAT_MOD(0x0A){ @Override	ItemBlobEntry newBlobEntry() { throw new RuntimeException("not impl yet!"); }},				//8 [hdc]	?? [Not handled before] retail send it xx times (smth dynamically changed)
		MANA_SOCKETS(0x0B) { @Override	ItemBlobEntry newBlobEntry() { return new ManaStoneInfoBlobEntry(); }},					//45			OK
		// 0x0C - not used?
		SLOTS_CLOTHES(0x0D){ @Override	ItemBlobEntry newBlobEntry() { return new ClothesInfoBlobEntry(); }},						//9				OK [Not handled before]
		COMPOSITE_ITEM(0x0E){ @Override	ItemBlobEntry newBlobEntry() { return new CompositeItemBlobEntry(); }},					//30			OK
		CONDITIONING_INFO(0x0F){ @Override	ItemBlobEntry newBlobEntry() { return new ConditioningInfoBlobEntry(); }};	//5				OK

		private int entryId;

		private ItemBlobType(int entryId)	{
			this.entryId = entryId;
		}

		public int getEntryId() {
			return entryId;
		}

		abstract ItemBlobEntry newBlobEntry();
	}
}
