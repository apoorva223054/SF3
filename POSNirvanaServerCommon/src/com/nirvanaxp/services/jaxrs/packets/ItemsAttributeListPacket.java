/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
@XmlRootElement(name = "ItemsAttributeListPacket")
public class ItemsAttributeListPacket extends PostPacket
{

	List<ItemsAttribute> itemsAttribute;

	/**
	 * @return the ItemAttribute
	 */
	public List<ItemsAttribute> getItemsAttribute()
	{
		return itemsAttribute;
	}

	/**
	 * @param ItemAttribute
	 *            the ItemAttribute to set
	 */
	public void setItemsAttribute(List<ItemsAttribute> itemsAttribute)
	{
		this.itemsAttribute = itemsAttribute;
	}

	@Override
	public String toString()
	{
		return "ItemsAttributeListPacket [itemsAttribute=" + itemsAttribute + "]";
	}

}
