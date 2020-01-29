/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.reservation.ReservationsSlot;

// TODO: Auto-generated Javadoc
/**
 * 
 */
@XmlRootElement(name = "HoldReservationSlotPacket")
public class HoldReservationSlotPacket extends PostPacket
{

	/**  */
	private ReservationsSlot reservationSlot;

	/**
	 * 
	 *
	 * @return 
	 */
	public ReservationsSlot getReservationSlot()
	{
		return reservationSlot;
	}

	/**
	 * 
	 *
	 * @param reservationSlot 
	 */
	public void setReservationSlot(ReservationsSlot reservationSlot)
	{
		this.reservationSlot = reservationSlot;
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.packets.PostPacket#toString()
	 */
	@Override
	public String toString()
	{
		return "HoldReservationSlotPacket [reservationSlot=" + reservationSlot + "]";
	}

}
