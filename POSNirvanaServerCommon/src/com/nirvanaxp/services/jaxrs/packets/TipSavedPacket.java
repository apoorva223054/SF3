package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
@XmlRootElement(name = "TipSavedPacket")
public class TipSavedPacket  extends PostPacket  {
	String orderHeaderId;
	List<OrderPaymentDetail> orderPaymentDetails;
	
	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}
	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}
	public List<OrderPaymentDetail> getOrderPaymentDetails() {
		return orderPaymentDetails;
	}
	public void setOrderPaymentDetails(List<OrderPaymentDetail> orderPaymentDetails) {
		this.orderPaymentDetails = orderPaymentDetails;
	}
	@Override
	public String toString() {
		return "TipSavedPacket [orderHeaderId=" + orderHeaderId
				+ ", orderPaymentDetails=" + orderPaymentDetails + "]";
	}
	
	
	

}
