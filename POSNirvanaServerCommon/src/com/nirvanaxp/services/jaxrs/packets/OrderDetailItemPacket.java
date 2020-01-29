package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderHeader;

@XmlRootElement(name = "OrderDetailItemPacket")
public class OrderDetailItemPacket extends PostPacket{
	
	private OrderHeader orderHeader;
	private String comment;
    private String orderDetailItemId;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getOrderDetailItemId() {
		 if(orderDetailItemId != null && (orderDetailItemId.length()==0 || orderDetailItemId.equals("0"))){return null;}else{	return orderDetailItemId;}
	}
	public void setOrderDetailItemId(String orderDetailItemId) {
		this.orderDetailItemId = orderDetailItemId;
	}
	public OrderHeader getOrderHeader() {
		return orderHeader;
	}
	public void setOrderHeader(OrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}
	@Override
	public String toString() {
		return "OrderDetailItemPacket [orderHeader=" + orderHeader
				+ ", comment=" + comment + ", orderDetailItemId="
				+ orderDetailItemId + "]";
	}
    
    
	

}
