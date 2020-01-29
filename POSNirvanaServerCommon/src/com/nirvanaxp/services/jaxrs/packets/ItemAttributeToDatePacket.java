/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.category.ItemAttributeToDate;

@XmlRootElement(name = "ItemAttributeToDatePacket")
public class ItemAttributeToDatePacket extends PostPacket
{
	public ItemAttributeToDate itemAttributeToDate ;

	public ItemAttributeToDate getItemAttributeToDate() {
		return itemAttributeToDate;
	}

	public void setItemAttributeToDate(ItemAttributeToDate itemAttributeToDate) {
		this.itemAttributeToDate = itemAttributeToDate;
	}

	@Override
	public String toString() {
		return "ItemToAttributePacket [itemAttributeToDate=" + itemAttributeToDate + "]";
	}

 
	
}
