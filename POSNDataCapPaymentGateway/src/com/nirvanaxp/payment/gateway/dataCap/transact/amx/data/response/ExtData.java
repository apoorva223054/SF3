/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.transact.amx.data.response;

import com.nirvanaxp.payment.gateway.dataCap.data.CardType;

public class ExtData
{

	/**
	 * Returns the current batch number, returned by the payment processor, for
	 * transactions, settlement, and batch inquiries.Not all payment processors
	 * support returning this data element
	 */
	private String batchNumber;

	/**
	 * Returns the credit card type (VISA, MASTERCARD, etc), payment method
	 * (Debit, EBT, or EGC ) for cardbased payments.This value is not returned
	 * for Check/ACH payments
	 */
	private CardType cardType;

	/**
	 * Returns the same invoice number for the transaction that was originally
	 * sent in the request to the Payment Serve
	 */
	private String invNumber;

	public String getBatchNumber()
	{
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber)
	{
		this.batchNumber = batchNumber;
	}

	public CardType getCardType()
	{
		return cardType;
	}

	public void setCardType(CardType cardType)
	{
		this.cardType = cardType;
	}

	public String getInvNumber()
	{
		return invNumber;
	}

	public void setInvNumber(String invNumber)
	{
		this.invNumber = invNumber;
	}

}
