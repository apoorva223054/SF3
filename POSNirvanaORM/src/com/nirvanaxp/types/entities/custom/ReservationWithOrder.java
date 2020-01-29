/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.custom;

import com.nirvanaxp.types.entities.reservation.Reservation;

public class ReservationWithOrder
{

	private Reservation reservation;

	private String orderid;

	private String locationId;

	private String locationName;

	public Reservation getReservation()
	{
		return reservation;
	}

	public void setReservation(Reservation reservation)
	{
		this.reservation = reservation;
	}

	public String getOrderid()
	{
		 if(orderid != null && (orderid.length()==0 || orderid.equals("0"))){return null;}else{	return orderid;}
	}

	public void setOrderid(String orderid)
	{
		this.orderid = orderid;
	}

	public String getLocationid()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationid(String locationId)
	{
		this.locationId = locationId;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}

	@Override
	public String toString() {
		return "ReservationWithOrder [reservation=" + reservation
				+ ", orderid=" + orderid + ", locationid=" + locationId
				+ ", locationName=" + locationName + "]";
	}

}
