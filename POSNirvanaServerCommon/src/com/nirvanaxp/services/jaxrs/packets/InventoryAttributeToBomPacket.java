/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;

@XmlRootElement(name = "InventoryAttributeToBomPacket")
public class InventoryAttributeToBomPacket extends PostPacket
{
	private List<InventoryAttributeBOM> inventoryAttributeBOMList;
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

	public List<InventoryAttributeBOM> getInventoryAttributeBOMList()
	{
		return inventoryAttributeBOMList;
	}

	public void setInventoryAttributeBOMList(List<InventoryAttributeBOM> inventoryAttributeBOMList)
	{
		this.inventoryAttributeBOMList = inventoryAttributeBOMList;
	}

	@Override
	public String toString()
	{
		final int maxLen = 10;
		return "InventoryAttributeToBomPacket [inventoryAttributeBOMList="
				+ (inventoryAttributeBOMList != null ? inventoryAttributeBOMList.subList(0, Math.min(inventoryAttributeBOMList.size(), maxLen)) : null) + "]";
	}

}
