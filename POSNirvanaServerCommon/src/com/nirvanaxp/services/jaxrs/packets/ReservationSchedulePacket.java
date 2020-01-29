/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
@XmlRootElement(name = "ReservationSchedulePacket")
public class ReservationSchedulePacket extends PostPacket
{

	private ReservationsSchedule reservationsSchedule;

	private List<String> blockDates;

	public ReservationsSchedule getReservationsSchedule()
	{
		return reservationsSchedule;
	}

	public void setReservationsSchedule(ReservationsSchedule reservationsSchedule)
	{
		this.reservationsSchedule = reservationsSchedule;
	}

	public List<String> getBlockDates()
	{
		return blockDates;
	}

	public void setBlockDates(List<String> blockDates)
	{
		this.blockDates = blockDates;
	}

	@Override
	public String toString()
	{
		final int maxLen = 10;
		return "ReservationSchedulePacket [reservationsSchedule=" + reservationsSchedule + ", blockDates=" + (blockDates != null ? blockDates.subList(0, Math.min(blockDates.size(), maxLen)) : null)
				+ "]";
	}

}
