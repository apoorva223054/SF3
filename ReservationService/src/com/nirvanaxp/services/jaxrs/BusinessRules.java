/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.reservation.ReservationsType;

/**
 * @author nirvanaxp
 *
 */
public class BusinessRules
{
	// todo method is static, we need to make it non static 
	// also method should return reservation object
	/**
	 * @param r
	 */
	public static void applyBusinessRules(Reservation r)
	{
		// set status
		ReservationsStatus status = new ReservationsStatus();
		status.setId("3");
		status.setName("Not Confirmed");
		// status.setAccountToLocationsId(1);
		// why locationid harcoded
		status.setLocationsId("1");
		status.setShowToCustomer((byte) 1);
		r.setReservationsStatus(status);

		// set type
		ReservationsType type = new ReservationsType();
		type.setId(1);
		type.setName("Reservation");
		r.setReservationsType(type);

	}

}
