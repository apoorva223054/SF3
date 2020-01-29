package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems;
@XmlRootElement(name = "RequestOrderDetailItemsPacket")
public class RequestOrderDetailItemsPacket extends PostPacket
{

	private RequestOrderDetailItems requestOrderDetailItems;

	public RequestOrderDetailItems getRequestOrderDetailItems() {
		return requestOrderDetailItems;
	}

	public void setRequestOrderDetailItems(RequestOrderDetailItems requestOrderDetailItems) {
		this.requestOrderDetailItems = requestOrderDetailItems;
	}

	@Override
	public String toString() {
		return "RequestOrderDetailItemsPacket [requestOrderDetailItems=" + requestOrderDetailItems + "]";
	}
	

	
	
	
}
