/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

public class SupportedPaymentAction
{

	// Used for ACTION_PAYMENT_CREDITCARD
	public static SupportedPaymentAction ACTION_PAYMENT_MANUAL = new SupportedPaymentAction("manualPayment");

	// Used for ACTION_PAYMENT_DEBITCARD
	public static SupportedPaymentAction ACTION_PAYMENT_SWIPE_NONENCRYPTED = new SupportedPaymentAction("swipePaymentNonEncrypted");

	public static SupportedPaymentAction ACTION_PAYMENT_SWIPE_ENCYPTED = new SupportedPaymentAction("swipePaymentEncrypted");

	private String paymentAction;

	public SupportedPaymentAction(String paymentAction)
	{
		this.paymentAction = paymentAction;
	}

	public String getPaymentAction()
	{
		return paymentAction;
	}

	public void setPaymentAction(String paymentAction)
	{
		this.paymentAction = paymentAction;
	}

	public boolean equals(Object object)
	{

		if (object != null && object instanceof SupportedPaymentAction)
		{

			SupportedPaymentAction supportedPaymentAction = (SupportedPaymentAction) object;
			if (paymentAction.equals(supportedPaymentAction.getPaymentAction()))
			{
				return true;
			}
			return false;
		}

		return false;
	}
}
