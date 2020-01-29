/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.payment.PaymentMethod;
@XmlRootElement(name = "PaymentMethodPacket")
public class PaymentMethodPacket extends PostPacket
{

	private PaymentMethod paymentMethod;

	public PaymentMethod getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	@Override
	public String toString()
	{
		return "PaymentMethodPacket [paymentMethod=" + paymentMethod + "]";
	}

}
