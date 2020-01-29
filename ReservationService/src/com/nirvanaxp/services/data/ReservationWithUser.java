/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.data;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.user.User;
@XmlRootElement(name = "ReservationWithUser")
public class ReservationWithUser extends PostPacket
{

	private Reservation reservation;
	private Reservation updatedReservation;

	private User user;
	
	public ReservationWithUser(Reservation r,Reservation updatedReservation, User u)
	{
		this.reservation = r;
		this.user = u;
		this.updatedReservation = updatedReservation;
		
	}

	public Reservation getReservation()
	{
		return reservation;
	}

	public User getUser()
	{
		return user;
	}

	public Reservation getUpdatedReservation()
	{
		return updatedReservation;
	}

	public void setUpdatedReservation(Reservation updatedReservation)
	{
		this.updatedReservation = updatedReservation;
	}

	public void setReservation(Reservation reservation)
	{
		this.reservation = reservation;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
 
}
