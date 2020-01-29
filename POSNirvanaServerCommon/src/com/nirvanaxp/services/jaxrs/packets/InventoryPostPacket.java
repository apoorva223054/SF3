/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.inventory.Inventory;

@XmlRootElement(name = "InventoryPostPacket")
public class InventoryPostPacket extends PostPacket
{

	@Override
	public String toString()
	{
		return "InventoryPostPacket [inventoryList=" + inventoryList + ", itemList=" + itemList + "]";
	}

	public List<Inventory> inventoryList;

	public List<Item> itemList;

	public List<Inventory> getInventoryList()
	{
		return inventoryList;
	}

	public void setInventoryList(List<Inventory> inventoryList)
	{
		this.inventoryList = inventoryList;
	}

	public List<Item> getItemList()
	{
		return itemList;
	}

	public void setItemList(List<Item> itemList)
	{
		this.itemList = itemList;
	}
	

}
