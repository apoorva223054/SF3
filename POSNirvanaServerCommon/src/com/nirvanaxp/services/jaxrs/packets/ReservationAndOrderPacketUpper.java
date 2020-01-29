/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The persistent class for the reservations_status database table.
 * 
 */
@XmlRootElement(name = "ReservationAndOrderPacketUpper")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationAndOrderPacketUpper extends PostPacket
{
	@JsonIgnoreProperties(ignoreUnknown = true)
	private ReservationAndOrderPacket reservationAndOrderPacket;

	public ReservationAndOrderPacket getReservationAndOrderPacket() {
		return reservationAndOrderPacket;
	}

	public void setReservationAndOrderPacket(ReservationAndOrderPacket reservationAndOrderPacket) {
		this.reservationAndOrderPacket = reservationAndOrderPacket;
	}

	@Override
	public String toString() {
		return "ReservationAndOrderPacketUpper [reservationAndOrderPacket=" + reservationAndOrderPacket + "]";
	}
	 
	
}