/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
@XmlRootElement(name = "ItemsCharListPacket")
public class ItemsCharListPacket extends PostPacket
{

	List<ItemsChar> itemsChar;

	/**
	 * @return the ItemChar
	 */
	public List<ItemsChar> getItemsChar()
	{
		return itemsChar;
	}

	/**
	 * @param ItemChar
	 *            the ItemChar to set
	 */
	public void setItemsChar(List<ItemsChar> itemsChar)
	{
		this.itemsChar = itemsChar;
	}

	@Override
	public String toString()
	{
		return "ItemsCharListPacket [itemsChar=" + itemsChar + "]";
	}

}
