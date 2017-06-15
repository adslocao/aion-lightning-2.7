/*
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
package com.aionemu.gameserver.model.ingameshop;

/**
 * @author xTz
 */
public class IGItem {

	private int objectId;
	private int itemId;
	private int itemCount;
	private int itemPrice;
	private int category;
	private int list;
	private int salesRanking;
	private String description;

	public IGItem(int objectId, int itemId, int itemCount, int itemPrice, int category, int list, int salesRanking,
		String description) {
		this.objectId = objectId;
		this.itemId = itemId;
		this.itemCount = itemCount;
		this.itemPrice = itemPrice;
		this.category = category;
		this.list = list;
		this.salesRanking = salesRanking;
		this.description = description;
	}

	public int getObjectId() {
		return objectId;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getItemPrice() {
		return itemPrice;
	}

	public int getCategory() {
		return category;
	}

	public int getList() {
		return list;
	}

	public int getSalesRanking() {
		return salesRanking;
	}

	public String getDescription() {
		return description;
	}

	public void increaseSales() {
		salesRanking++;
	}
}
