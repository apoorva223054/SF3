/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.Item;
@XmlRootElement(name = "ItemListPacket")
public class ItemListPacket extends PostPacket
{

	@Override
	public String toString()
	{
		return "ItemListPacket [item=" + item + "]";
	}

	List<Item> item;

	public List<Item> getItem()
	{
		return item;
	}

	public void setItem(List<Item> item)
	{
		this.item = item;
	}

}
