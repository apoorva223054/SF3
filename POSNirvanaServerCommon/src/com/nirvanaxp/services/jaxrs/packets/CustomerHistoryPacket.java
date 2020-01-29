/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "CustomerHistoryPacket")
public class CustomerHistoryPacket
{

	private String orderId;
	private String userId;
	private String dateOfVisit;
	private int guestCount;
	private BigDecimal amountPaid;
	private BigDecimal averageTotalAmount;
	private List<CustomerFeedbackHistoryPacket> customerFeedbackHistoryPackets;

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getDateOfVisit()
	{
		return dateOfVisit;
	}

	public void setDateOfVisit(String dateOfVisit)
	{
		this.dateOfVisit = dateOfVisit;
	}

	public int getGuestCount()
	{
		return guestCount;
	}

	public void setGuestCount(int guestCount)
	{
		this.guestCount = guestCount;
	}

	/**
	 * @return the amountPaid
	 */
	public BigDecimal getAmountPaid()
	{
		return amountPaid;
	}

	/**
	 * @param amountPaid
	 *            the amountPaid to set
	 */
	public void setAmountPaid(BigDecimal amountPaid)
	{
		this.amountPaid = amountPaid;
	}


	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<CustomerFeedbackHistoryPacket> getCustomerFeedbackHistoryPackets()
	{
		return customerFeedbackHistoryPackets;
	}

	public void setCustomerFeedbackHistoryPackets(List<CustomerFeedbackHistoryPacket> customerFeedbackHistoryPackets)
	{
		this.customerFeedbackHistoryPackets = customerFeedbackHistoryPackets;
	}

	/**
	 * @return the averageTotalAmount
	 */
	public BigDecimal getAverageTotalAmount()
	{
		return averageTotalAmount;
	}

	/**
	 * @param averageTotalAmount
	 *            the averageTotalAmount to set
	 */
	public void setAverageTotalAmount(BigDecimal averageTotalAmount)
	{
		this.averageTotalAmount = averageTotalAmount;
	}

	@Override
	public String toString()
	{
		return "CustomerHistoryPacket [orderId=" + orderId + ", userId=" + userId + ", dateOfVisit=" + dateOfVisit + ", guestCount=" + guestCount + ", amountPaid=" + amountPaid
				+ ", averageTotalAmount=" + averageTotalAmount + ", customerFeedbackHistoryPackets=" + customerFeedbackHistoryPackets + "]";
	}

}
