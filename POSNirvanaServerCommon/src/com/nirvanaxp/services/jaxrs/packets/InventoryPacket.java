/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.InventoryItemDefault;
import com.nirvanaxp.types.entities.inventory.PhysicalInventory;

@XmlRootElement(name = "InventoryPacket")
public class InventoryPacket extends PostPacket
{

	private Inventory inventory;

	private InventoryItemDefault inventoryItemDefault;

	private PhysicalInventory physicalInventory;
	
	private int  isAdmin;
	/**
	 * @return the inventory
	 */
	public Inventory getInventory()
	{
		return inventory;
	}

	/**
	 * @param inventory
	 *            the inventory to set
	 */
	public void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
	}

	public InventoryItemDefault getInventoryItemDefault()
	{
		return inventoryItemDefault;
	}

	public void setInventoryItemDefault(InventoryItemDefault inventoryItemDefault)
	{
		this.inventoryItemDefault = inventoryItemDefault;
	}

	
	public PhysicalInventory getPhysicalInventory() {
		return physicalInventory;
	}

	public void setPhysicalInventory(PhysicalInventory physicalInventory) {
		this.physicalInventory = physicalInventory;
	}
	

	public int getIsAdmin()
	{
		return isAdmin;
	}

	public void setIsAdmin(int isAdmin)
	{
		this.isAdmin = isAdmin;
	}

	@Override
	public String toString()
	{
		return "InventoryPacket [inventory=" + inventory + ", inventoryItemDefault=" + inventoryItemDefault + ", physicalInventory=" + physicalInventory + ", isAdmin=" + isAdmin + "]";
	}

	 

	
}
