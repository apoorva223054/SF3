/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsSchedule;
@XmlRootElement(name = "ItemsSchedulePacket")
public class ItemsSchedulePacket extends PostPacket
{

	private ItemsSchedule itemsSchedule;

	public ItemsSchedule getItemsSchedule() {
		return itemsSchedule;
	}

	public void setItemsSchedule(ItemsSchedule itemsSchedule) {
		this.itemsSchedule = itemsSchedule;
	}

	

}
