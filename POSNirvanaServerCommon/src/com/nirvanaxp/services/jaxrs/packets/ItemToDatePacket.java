/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.category.ItemToDate;

@XmlRootElement(name = "ItemToDatePacket")
public class ItemToDatePacket extends PostPacket
{
	public ItemToDate itemToDate ;

	public ItemToDate getItemToDate() {
		return itemToDate;
	}

	public void setItemToDate(ItemToDate itemToDate) {
		this.itemToDate = itemToDate;
	}

	@Override
	public String toString() {
		return "ItemToDatePacket [itemToDate=" + itemToDate + "]";
	} 
	
	
	
}
