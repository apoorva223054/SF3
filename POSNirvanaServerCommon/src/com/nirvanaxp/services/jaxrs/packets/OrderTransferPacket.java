package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderHeader;

@XmlRootElement(name = "OrderTransferPacket")
public class OrderTransferPacket extends PostPacket{
	
	private String orderId;
	private String destinationLocationId;
	private  OrderHeader orderHeader;
	
	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getDestinationLocationId() {
		return destinationLocationId;
	}
	public void setDestinationLocationId(String destinationLocationId) {
		this.destinationLocationId = destinationLocationId;
	}


	public OrderHeader getOrderHeader() {
		return orderHeader;
	}
	public void setOrderHeader(OrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}
	@Override
	public String toString() {
		return "OrderTransferPacket [orderId=" + orderId
				+ ", destinationLocationId=" + destinationLocationId
				+ ", orderHeader=" + orderHeader + "]";
	}
	
}
