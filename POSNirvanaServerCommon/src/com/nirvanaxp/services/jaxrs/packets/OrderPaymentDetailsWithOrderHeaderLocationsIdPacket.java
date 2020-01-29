/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
@XmlRootElement(name = "OrderPaymentDetailsWithOrderHeaderLocationsIdPacket")
public class OrderPaymentDetailsWithOrderHeaderLocationsIdPacket
{

	private String locationID;
	private OrderPaymentDetail orderPaymentDetail;

	public String getLocationID()
	{
		if(locationID != null && (locationID.length()==0 || locationID.equals("0"))){return null;}else{	return locationID;}
	}

	public void setLocationID(String locationID)
	{
		this.locationID = locationID;
	}

	public OrderPaymentDetail getOrderPaymentDetail()
	{
		return orderPaymentDetail;
	}

	public void setOrderPaymentDetail(OrderPaymentDetail orderPaymentDetail)
	{
		this.orderPaymentDetail = orderPaymentDetail;
	}

	@Override
	public String toString()
	{
		return "OrderPaymentDetailsWithOrderHeaderLocationsIdPacket [locationID=" + locationID + ", orderPaymentDetail=" + orderPaymentDetail + "]";
	}

}
