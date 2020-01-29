/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
@XmlRootElement(name = "BraintreePaymentPacket")
public class BraintreePaymentPacket
{

	private OrderPaymentDetail orderPaymentDetail;

	private String userId;

	public OrderPaymentDetail getOrderPaymentDetail() {
		return orderPaymentDetail;
	}

	public void setOrderPaymentDetail(OrderPaymentDetail orderPaymentDetail) {
		this.orderPaymentDetail = orderPaymentDetail;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	} 
	
	
}
