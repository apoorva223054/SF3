/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.InventoryItemBom;

@XmlRootElement(name = "InventoryToBomPacket")
public class InventoryToBomPacket extends PostPacket
{

	@Override
	public String toString()
	{
		return "InventoryToBomPacket [inventoryItemToBOMs=" + inventoryItemToBOMs + "]";
	}

	private List<InventoryItemBom> inventoryItemToBOMs;
	private int isBaseLocationUpdate;
	
	private String locationsListId;

	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}


	public int getIsBaseLocationUpdate()
	{
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate)
	{
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	public List<InventoryItemBom> getInventoryItemToBOMs()
	{
		return inventoryItemToBOMs;
	}

	public void setInventoryItemToBOMs(List<InventoryItemBom> inventoryItemToBOMs)
	{
		this.inventoryItemToBOMs = inventoryItemToBOMs;
	}

}
