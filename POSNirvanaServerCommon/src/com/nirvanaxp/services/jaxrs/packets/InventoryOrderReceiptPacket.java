/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.InventoryOrderReceipt;
import com.nirvanaxp.types.entities.inventory.InventoryOrderReceiptForItem;

@XmlRootElement(name = "InventoryOrderReceiptPacket")
public class InventoryOrderReceiptPacket extends PostPacket
{

	private InventoryOrderReceipt inventoryOrderReceipt;
	private List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemsList;

	public List<InventoryOrderReceiptForItem> getInventoryOrderReceiptForItemsList() {
		return inventoryOrderReceiptForItemsList;
	}

	public void setInventoryOrderReceiptForItemsList(
			List<InventoryOrderReceiptForItem> inventoryOrderReceiptForItemsList) {
		this.inventoryOrderReceiptForItemsList = inventoryOrderReceiptForItemsList;
	}

	/**
	 * @return the inventoryOrderReceipt
	 */
	public InventoryOrderReceipt getInventoryOrderReceipt()
	{
		return inventoryOrderReceipt;
	}

	/**
	 * @param inventoryOrderReceipt
	 *            the inventoryOrderReceipt to set
	 */
	public void setInventoryOrderReceipt(InventoryOrderReceipt inventoryOrderReceipt)
	{
		this.inventoryOrderReceipt = inventoryOrderReceipt;
	}

	@Override
	public String toString()
	{
		return "InventoryOrderReceiptPacket [inventoryOrderReceipt=" + inventoryOrderReceipt + "]";
	}

}
