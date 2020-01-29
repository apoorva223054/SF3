/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;

@XmlRootElement(name = "ProcessPaymentPacket")
public class ProcessPaymentPacket extends PostPacket
{

	private String date;
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

	

	public String getUserId()
	{
		return userId;
	}

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

	

	
}
