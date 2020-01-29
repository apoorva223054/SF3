package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.InventoryOrderReceiptForItem;

@XmlRootElement(name = "InventoryOrderReceiptForItemPacket")
public class InventoryOrderReceiptForItemPacket extends PostPacket
{

	private InventoryOrderReceiptForItem inventoryOrderReceiptForItem;

	public InventoryOrderReceiptForItem getInventoryOrderReceiptForItem() {
		return inventoryOrderReceiptForItem;
	}

	public void setInventoryOrderReceiptForItem(
			InventoryOrderReceiptForItem inventoryOrderReceiptForItem) {
		this.inventoryOrderReceiptForItem = inventoryOrderReceiptForItem;
	}

	@Override
	public String toString() {
		return "InventoryOrderReceiptForItemPacket [inventoryOrderReceiptForItem="
				+ inventoryOrderReceiptForItem + "]";
	}

	

	

}
