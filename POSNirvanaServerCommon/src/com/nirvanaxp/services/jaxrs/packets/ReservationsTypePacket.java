/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reservation.ReservationsType;
@XmlRootElement(name = "ReservationsTypePacket")
public class ReservationsTypePacket extends PostPacket
{

	private ReservationsType reservationsType;

	public ReservationsType getReservationsType()
	{
		return reservationsType;
	}

	public void setReservationsType(ReservationsType reservationsType)
	{
		this.reservationsType = reservationsType;
	}

	@Override
	public String toString()
	{
		return "ReservationsTypePacket [reservationsType=" + reservationsType + "]";
	}

}
