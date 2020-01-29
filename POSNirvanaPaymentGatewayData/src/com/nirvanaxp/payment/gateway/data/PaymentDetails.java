/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

public class PaymentDetails
{

	/**
	 * Optional except for these TransTypes: Auth, Sale, Return, Force (both
	 * PostAuth and ForceAuth). The total transaction amount in DDDD.CC format
	 */
	private Double amount;

	/**
	 * Optional. Invoice tracking number. This parameter will remove invalid
	 * characters. See list of Removed Characters for more details
	 */
	private String invoiceNumber;

	/**
	 * Optional except for these TransTypes: Void, Force (PostAuth), Capture.
	 * Reference number assigned by the payment server
	 */
	private String pNRef;

	public Double getAmount()
	{
		return amount;
	}

	public void setAmount(Double amount)
	{
		this.amount = amount;
	}

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public String getpNRef()
	{
		return pNRef;
	}

	public void setpNRef(String pNRef)
	{
		this.pNRef = pNRef;
	}

}
