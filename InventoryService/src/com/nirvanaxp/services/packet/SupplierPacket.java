package com.nirvanaxp.services.packet;

import java.util.List;

import com.nirvanaxp.types.entities.locations.Location;

public class SupplierPacket {
	Location suppliers;
	List<RequestOrderDetailItemsDisplayPacket> RequestOrderDetailItems;
	
	public Location getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(Location suppliers) {
		this.suppliers = suppliers;
	}
	public List<RequestOrderDetailItemsDisplayPacket> getRequestOrderDetailItems() {
		return RequestOrderDetailItems;
	}
	public void setRequestOrderDetailItems(List<RequestOrderDetailItemsDisplayPacket> requestOrderDetailItems) {
		RequestOrderDetailItems = requestOrderDetailItems;
	}
	@Override
	public String toString() {
		return "SupplierPacket [suppliers=" + suppliers + ", RequestOrderDetailItems=" + RequestOrderDetailItems + "]";
	}

}