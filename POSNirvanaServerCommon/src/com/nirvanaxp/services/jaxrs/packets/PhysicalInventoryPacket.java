/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.PhysicalInventory;

@XmlRootElement(name = "PhysicalInventoryPacket")
public class PhysicalInventoryPacket extends PostPacket
{

	private PhysicalInventory physicalInventory;

	
	public PhysicalInventory getPhysicalInventory() {
		return physicalInventory;
	}


	public void setPhysicalInventory(PhysicalInventory physicalInventory) {
		this.physicalInventory = physicalInventory;
	}


	@Override
	public String toString()
	{
		return "PhysicalInventoryPacket [physicalInventory=" + physicalInventory + "]";
	}

}
