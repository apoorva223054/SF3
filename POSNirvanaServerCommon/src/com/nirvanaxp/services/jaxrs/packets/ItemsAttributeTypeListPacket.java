/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
@XmlRootElement(name = "ItemsAttributeTypeListPacket")
public class ItemsAttributeTypeListPacket extends PostPacket
{

	List<ItemsAttributeType> ItemsAttributeType;

	/**
	 * @return the orderStatus
	 */
	public List<ItemsAttributeType> getItemsAttributeType()
	{
		return ItemsAttributeType;
	}

	/**
	 * @param orderStatus
	 *            the orderStatus to set
	 */
	public void setItemsAttributeType(List<ItemsAttributeType> ItemsAttributeType)
	{
		this.ItemsAttributeType = ItemsAttributeType;
	}

	@Override
	public String toString()
	{
		return "ItemsAttributeTypeListPacket [ItemsAttributeType=" + ItemsAttributeType + "]";
	}

}
