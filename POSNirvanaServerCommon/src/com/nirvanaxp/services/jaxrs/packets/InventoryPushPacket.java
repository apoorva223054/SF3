/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "InventoryPushPacket")
public class InventoryPushPacket extends PostPacket
{

	@Override
	public String toString()
	{
		return "InventoryPushPacket [inventoryPostPacket=" + inventoryPostPacket + "]";
	}

	private InventoryPostPacket inventoryPostPacket;

	public InventoryPostPacket getInventoryPostPacket()
	{
		return inventoryPostPacket;
	}

	public void setInventoryPostPacket(InventoryPostPacket inventoryPostPacket)
	{
		this.inventoryPostPacket = inventoryPostPacket;
	}

}
