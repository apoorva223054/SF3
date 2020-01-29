/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nirvanaxp.types.entities.reservation.Reservation;

/**
 * The persistent class for the reservations_status database table.
 * 
 */
@XmlRootElement(name = "ReservationAndOrderPacket")
@JsonInclude(Include.NON_EMPTY)
public class ReservationAndOrderPacket extends PostPacket
{
	private ReservationPacket reservationPacket;
	private OrderPacket orderPacket;
	
	private Reservation reservationUpdated;

	/**
	 * @return the reservationPacket
	 */
	public ReservationPacket getReservationPacket()
	{
		return reservationPacket;
	}

	/**
	 * @param reservationPacket
	 *            the reservationPacket to set
	 */
	public void setReservationPacket(ReservationPacket reservationPacket)
	{
		this.reservationPacket = reservationPacket;
	}

	/**
	 * @return the orderPacket
	 */
	public OrderPacket getOrderPacket()
	{
		return orderPacket;
	}

	/**
	 * @param orderPacket
	 *            the orderPacket to set
	 */
	public void setOrderPacket(OrderPacket orderPacket)
	{
		this.orderPacket = orderPacket;
	}

	public Reservation getReservationUpdated()
	{
		return reservationUpdated;
	}

	public void setReservationUpdated(Reservation reservationUpdated)
	{
		this.reservationUpdated = reservationUpdated;
	}

 
	

}