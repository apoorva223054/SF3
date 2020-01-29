/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.custom;

import java.io.Serializable;
import java.util.Date;


public class ReservationsSearchCriteria implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1608451291797143542L;

	private String usersId;

	private String locationId;

	private int reservationsTypeId;

	private String reservationDate;

	private String reservationFromDate;

	private int first;

	private int maxResults;

	private String orderByAttributeName;

	private boolean descending;
	private Date reservationFromCurrentTime;

	public ReservationsSearchCriteria()
	{

	}

	public String getUsersId()
	{
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String userId)
	{
		this.usersId = userId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public int getReservationsTypeId()
	{
		return reservationsTypeId;
	}

	public void setReservationsTypeId(int reservationsTypeId)
	{
		this.reservationsTypeId = reservationsTypeId;
	}

	public String getReservationDate()
	{
		return reservationDate;
	}

	public void setReservationDate(String reservationDate)
	{
		this.reservationDate = reservationDate;
	}

	public String getReservationFromDate()
	{
		return reservationFromDate;
	}

	public void setReservationFromDate(String reservationFromDate)
	{
		this.reservationFromDate = reservationFromDate;
	}

	public int getFirst()
	{
		return first;
	}

	public void setFirst(int first)
	{
		this.first = first;
	}

	public int getMaxResults()
	{
		return maxResults;
	}

	public void setMaxResults(int maxResults)
	{
		this.maxResults = maxResults;
	}

	public String getOrderByAttributeName()
	{
		return orderByAttributeName;
	}

	public void setOrderByAttributeName(String orderByAttributeName)
	{
		this.orderByAttributeName = orderByAttributeName;
	}

	public boolean isDescending()
	{
		return descending;
	}

	public void setDescending(boolean descending)
	{
		this.descending = descending;
	}

	public Date getReservationFromCurrentTime()
	{
		return reservationFromCurrentTime;
	}

	public void setReservationFromCurrentTime(Date reservationFromCurrentTime)
	{
		this.reservationFromCurrentTime = reservationFromCurrentTime;
	}

	@Override
	public String toString() {
		return "ReservationsSearchCriteria [usersId=" + usersId
				+ ", locationId=" + locationId + ", reservationsTypeId="
				+ reservationsTypeId + ", reservationDate=" + reservationDate
				+ ", reservationFromDate=" + reservationFromDate + ", first="
				+ first + ", maxResults=" + maxResults
				+ ", orderByAttributeName=" + orderByAttributeName
				+ ", descending=" + descending
				+ ", reservationFromCurrentTime=" + reservationFromCurrentTime
				+ "]";
	}

}
