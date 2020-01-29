/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.user.User;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
@XmlRootElement(name = "ReservationPacket")
public class ReservationPacket extends PostPacket
{

	/**  */
	private Reservation reservation;

	/**  */
	private User user;

	/**  */
	int idOfReservationHoldingClientObj;

	/** this user sends when they search from auto-complete. */
	private String globalUserId;

	/**  */
	private int reservationSlotId;

	/**  */
	private String webSiteUrl;

	/**
	 * 
	 *
	 * @return 
	 */
	public String getWebSiteUrl()
	{
		return webSiteUrl;
	}

	/**
	 * 
	 *
	 * @param webSiteUrl 
	 */
	public void setWebSiteUrl(String webSiteUrl)
	{
		this.webSiteUrl = webSiteUrl;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public Reservation getReservation()
	{
		return reservation;
	}

	/**
	 * 
	 *
	 * @param reservation 
	 */
	public void setReservation(Reservation reservation)
	{
		this.reservation = reservation;
	}

	

	public String getGlobalUserId()
	{
		return globalUserId;
	}

	public void setGlobalUserId(String globalUserId)
	{
		this.globalUserId = globalUserId;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public User getUser()
	{
		return user;
	}

	/**
	 * 
	 *
	 * @param user 
	 */
	public void setUser(User user)
	{
		this.user = user;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public int getReservationSlotId()
	{
		return reservationSlotId;
	}

	/**
	 * 
	 *
	 * @param reservationSlotId 
	 */
	public void setReservationSlotId(int reservationSlotId)
	{
		this.reservationSlotId = reservationSlotId;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public int getIdOfReservationHoldingClientObj()
	{
		return idOfReservationHoldingClientObj;
	}

	/**
	 * 
	 *
	 * @param idOfReservationHoldingClientObj 
	 */
	public void setIdOfReservationHoldingClientObj(int idOfReservationHoldingClientObj)
	{
		this.idOfReservationHoldingClientObj = idOfReservationHoldingClientObj;
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.packets.PostPacket#toString()
	 */
	@Override
	public String toString()
	{
		return "ReservationPacket [reservation=" + reservation + ", user=" + user + ", idOfReservationHoldingClientObj=" + idOfReservationHoldingClientObj + ", globalUserId=" + globalUserId
				+ ", reservationSlotId=" + reservationSlotId + ", webSiteUrl=" + webSiteUrl + "]";
	}

}
