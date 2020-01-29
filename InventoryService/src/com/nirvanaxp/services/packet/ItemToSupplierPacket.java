package com.nirvanaxp.services.packet;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;

@XmlRootElement(name = "ItemToSupplierPacket")
public class ItemToSupplierPacket extends PostPacket
{

	private ItemToSupplier itemToSupplier;

	
	@Override
	public String toString() {
		return "ItemToSupplierPacket [itemToSupplier=" + itemToSupplier + "]";
	}

	public ItemToSupplier getItemToSupplier() {
		return itemToSupplier;
	}

	public void setItemToSupplier(ItemToSupplier itemToSupplier) {
		this.itemToSupplier = itemToSupplier;
	}



}
