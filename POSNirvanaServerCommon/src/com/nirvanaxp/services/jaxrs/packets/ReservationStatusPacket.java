/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
@XmlRootElement(name = "ReservationStatusPacket")
public class ReservationStatusPacket extends PostPacket
{

	private ReservationsStatus reservationsStatus;

	public ReservationsStatus getReservationsStatus()
	{
		return reservationsStatus;
	}

	public void setReservationsStatus(ReservationsStatus reservationsStatus)
	{
		this.reservationsStatus = reservationsStatus;
	}

	@Override
	public String toString()
	{
		return "ReservationStatusPacket [reservationsStatus=" + reservationsStatus + "]";
	}
}
