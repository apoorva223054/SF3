/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProcessPaymentPacket")
public class ProcessPaymentPacket
{

	private String date;
	private String locationId;
	private String userId;
	private String currentDate;
	private String currentTime;
	private String paymentGatewayTypeIdString;

	/**
	 * @return the date
	 */
	public String getDate()
	{
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date)
	{
		this.date = date;
	}

	/**
	 * @return the locationId
	 */
	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	/**
	 * @param locationId
	 *            the locationId to set
	 */
	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return the currentDate
	 */
	public String getCurrentDate()
	{
		return currentDate;
	}

	/**
	 * @param currentDate
	 *            the currentDate to set
	 */
	public void setCurrentDate(String currentDate)
	{
		this.currentDate = currentDate;
	}

	/**
	 * @return the currentTime
	 */
	public String getCurrentTime()
	{
		return currentTime;
	}

	/**
	 * @param currentTime
	 *            the currentTime to set
	 */
	public void setCurrentTime(String currentTime)
	{
		this.currentTime = currentTime;
	}

	/**
	 * @return the paymentGatewayTypeIdString
	 */
	public String getPaymentGatewayTypeIdString()
	{
		return paymentGatewayTypeIdString;
	}

	/**
	 * @param paymentGatewayTypeIdString
	 *            the paymentGatewayTypeIdString to set
	 */
	public void setPaymentGatewayTypeIdString(String paymentGatewayTypeIdString)
	{
		this.paymentGatewayTypeIdString = paymentGatewayTypeIdString;
	}

	@Override
	public String toString()
	{
		return "ProcessPaymentPacket [date=" + date + ", locationId=" + locationId + ", userId=" + userId + ", currentDate=" + currentDate + ", currentTime=" + currentTime
				+ ", paymentGatewayTypeIdString=" + paymentGatewayTypeIdString + "]";
	}

}
