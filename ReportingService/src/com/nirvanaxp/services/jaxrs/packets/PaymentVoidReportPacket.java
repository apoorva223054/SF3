/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class PaymentVoidReportPacket
{
	private String orderHeaderId;
	private String transactionType;
	private String paymentMethod;
	private String methodType;
	private int pnRef;
	private String date;
	private String time;
	private BigDecimal totalAmount;
	private BigDecimal authAmount;
	private BigDecimal tipAmount;
	private int authCode;
	private String updatedBy;
	private String cardNumber;

	public BigDecimal getAuthAmount()
	{
		return authAmount;
	}

	public void setAuthAmount(BigDecimal authAmount)
	{
		this.authAmount = authAmount;
	}

	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	public String getOrderHeaderId()
	{
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId)
	{
		this.orderHeaderId = orderHeaderId;
	}

	public String getTransactionType()
	{
		return transactionType;
	}

	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}

	public String getMethodType()
	{
		return methodType;
	}

	public void setMethodType(String methodType)
	{
		this.methodType = methodType;
	}

	public int getPnRef()
	{
		return pnRef;
	}

	public void setPnRef(int pnRef)
	{
		this.pnRef = pnRef;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public BigDecimal getTotalAmount()
	{
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTipAmount()
	{
		return tipAmount;
	}

	public void setTipAmount(BigDecimal tipAmount)
	{
		this.tipAmount = tipAmount;
	}

	public int getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(int authCode)
	{
		this.authCode = authCode;
	}

	public String getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString()
	{
		return "PaymentVoidReportPacket [orderHeaderId=" + orderHeaderId + ", transactionType=" + transactionType + ", paymentMethod=" + paymentMethod + ", methodType=" + methodType + ", pnRef="
				+ pnRef + ", date=" + date + ", time=" + time + ", totalAmount=" + totalAmount + ", authAmount=" + authAmount + ", tipAmount=" + tipAmount + ", authCode=" + authCode + ", updatedBy="
				+ updatedBy + ", cardNumber=" + cardNumber + "]";
	}

}
