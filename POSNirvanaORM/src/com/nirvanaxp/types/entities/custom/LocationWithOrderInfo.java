/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.custom;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.user.User;

@XmlRootElement(name = "LocationWithOrderInfo")
public class LocationWithOrderInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4354899267943590571L;
	private String id;
	private String orderId;
	private String orderStatusId;
	private String reservationId;
	private long created;
	private int partySize;
	private BigDecimal balanceDue;
	private User user;
	private String mergedLocationsId;
	private String orderNumber;
	private Date updated;
	private String firstName;
	private String lastName;;

	public LocationWithOrderInfo()
	{
		super();
	}

	
	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public LocationWithOrderInfo(Object[] obj)
	{
		// the passed object[] must have data in this order - order_id,
		// order_status_id, created, locations_id,reservation_id

		setOrderId((String) obj[0]);
		setOrderStatusId((String) obj[1]);
		setId((String) obj[3]);
		setReservationId((String) obj[4]);
		setPartySize((Integer) obj[5]);
		setCreated((Timestamp) obj[2]);
		if (obj[6] != null)
		{
			setBalanceDue((BigDecimal) obj[6]);
		}

	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}



	public String getOrderStatusId()
	{
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	public void setOrderStatusId(String orderStatusId)
	{
		this.orderStatusId = orderStatusId;
	}

	public long getCreated()
	{
		return created;
	}

	public void setCreated(Timestamp created)
	{
		if (created != null)
		{
			this.created = created.getTime();
		}

	}

	public int getPartySize()
	{
		return partySize;
	}

	public void setPartySize(int partySize)
	{
		this.partySize = partySize;
	}

	public BigDecimal getBalanceDue()
	{
		return balanceDue;
	}

	public void setBalanceDue(BigDecimal balanceDue)
	{
		this.balanceDue = balanceDue;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getMergedLocationsId()
	{
		 if(mergedLocationsId != null && (mergedLocationsId.length()==0 || mergedLocationsId.equals("0"))){return null;}else{	return mergedLocationsId;}
	}

	public void setMergedLocationsId(String mergedLocationsId)
	{
		this.mergedLocationsId = mergedLocationsId;
	}


	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}


	public String getReservationId() {
		return reservationId;
	}


	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}


	public String getOrderNumber() {
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}


	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	

	
	

}
