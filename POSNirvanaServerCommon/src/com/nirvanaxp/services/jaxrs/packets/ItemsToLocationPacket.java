package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsToLocation;

@XmlRootElement(name = "ItemsToLocationPacket")
public class ItemsToLocationPacket extends PostPacket{
	
	List<ItemsToLocation> itemsToLocationList;

	public List<ItemsToLocation> getItemsToLocationList() {
		return itemsToLocationList;
	}

	public void setItemsToLocationList(List<ItemsToLocation> itemsToLocationList) {
		this.itemsToLocationList = itemsToLocationList;
	}
	

}
