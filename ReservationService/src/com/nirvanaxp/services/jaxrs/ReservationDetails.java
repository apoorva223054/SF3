/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
@XmlRootElement(name = "ReservationPacket")
public class ReservationDetails
{

	/**  */
	private int reservationTypeId;

	/**  */
	private int waitlistTypeId;

	/**  */
	private String confirmedStatusId;

	/**  */
	private String cancelledStatusId;

	/**
	 * 
	 *
	 * @return 
	 */
	public int getReservationTypeId()
	{
		return reservationTypeId;
	}

	/**
	 * 
	 *
	 * @param reservationTypeId 
	 */
	public void setReservationTypeId(int reservationTypeId)
	{
		this.reservationTypeId = reservationTypeId;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public int getWaitlistTypeId()
	{
		return waitlistTypeId;
	}

	/**
	 * 
	 *
	 * @param waitlistTypeId 
	 */
	public void setWaitlistTypeId(int waitlistTypeId)
	{
		this.waitlistTypeId = waitlistTypeId;
	}

 

 
	public String getConfirmedStatusId() {
		return confirmedStatusId;
	}

	public void setConfirmedStatusId(String confirmedStatusId) {
		this.confirmedStatusId = confirmedStatusId;
	}

	public String getCancelledStatusId() {
		return cancelledStatusId;
	}

	public void setCancelledStatusId(String cancelledStatusId) {
		this.cancelledStatusId = cancelledStatusId;
	}

 
}
