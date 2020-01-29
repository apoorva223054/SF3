/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class ReservationsSeatedReport
{

	private int reservationCount;

	private String date;

	private String timeDuration;

	public int getReservationCount()
	{
		return reservationCount;
	}

	public void setReservationCount(int reservationCount)
	{
		this.reservationCount = reservationCount;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getTimeDuration()
	{
		return timeDuration;
	}

	public void setTimeDuration(String timeDuration)
	{
		this.timeDuration = timeDuration;
	}

}
