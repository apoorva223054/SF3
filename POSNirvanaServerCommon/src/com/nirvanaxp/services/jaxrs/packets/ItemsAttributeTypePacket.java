/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
@XmlRootElement(name = "ItemsAttributeTypePacket")
public class ItemsAttributeTypePacket extends PostPacket
{

	private ItemsAttributeType ItemsAttributeType;
	private int isBaseLocationUpdate;
	private String locationsListId;

	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	public ItemsAttributeType getItemsAttributeType()
	{
		return ItemsAttributeType;
	}

	public void setItemsAttributeType(ItemsAttributeType itemsAttributeType)
	{
		ItemsAttributeType = itemsAttributeType;
	}

	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	@Override
	public String toString() {
		return "ItemsAttributeTypePacket [ItemsAttributeType="
				+ ItemsAttributeType + ", isBaseLocationUpdate="
				+ isBaseLocationUpdate + "]";
	}

	

}
