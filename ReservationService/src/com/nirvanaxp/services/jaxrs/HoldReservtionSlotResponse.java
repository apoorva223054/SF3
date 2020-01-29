/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import com.nirvanaxp.types.entities.reservation.ReservationsSlot;

// TODO: Auto-generated Javadoc
/**
 * 
 */
public class HoldReservtionSlotResponse
{

	/**  */
	private ReservationsSlot reservationsSlot;

	/**  */
	private int reservationHoldingClientId;

	/**
	 * 
	 *
	 * @return 
	 */
	public ReservationsSlot getReservationsSlot()
	{
		return reservationsSlot;
	}

	/**
	 * 
	 *
	 * @param reservationsSlot 
	 */
	public void setReservationsSlot(ReservationsSlot reservationsSlot)
	{
		this.reservationsSlot = reservationsSlot;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public int getReservationHoldingClientId()
	{
		return reservationHoldingClientId;
	}

	/**
	 * 
	 *
	 * @param reservationHoldingClientId 
	 */
	public void setReservationHoldingClientId(int reservationHoldingClientId)
	{
		this.reservationHoldingClientId = reservationHoldingClientId;
	}

}
