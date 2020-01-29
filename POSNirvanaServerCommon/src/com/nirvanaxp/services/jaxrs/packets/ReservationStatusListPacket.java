/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
@XmlRootElement(name = "ReservationStatusListPacket")
public class ReservationStatusListPacket extends PostPacket
{

	List<ReservationsStatus> reservationStatus;

	/**
	 * @return the reservationStatus
	 */
	public List<ReservationsStatus> getReservationStatus()
	{
		return reservationStatus;
	}

	/**
	 * @param reservationStatus
	 *            the reservationStatus to set
	 */
	public void setReservationStatus(List<ReservationsStatus> reservationStatus)
	{
		this.reservationStatus = reservationStatus;
	}

	@Override
	public String toString()
	{
		final int maxLen = 10;
		return "ReservationStatusListPacket [reservationStatus=" + (reservationStatus != null ? reservationStatus.subList(0, Math.min(reservationStatus.size(), maxLen)) : null) + "]";
	}

}
