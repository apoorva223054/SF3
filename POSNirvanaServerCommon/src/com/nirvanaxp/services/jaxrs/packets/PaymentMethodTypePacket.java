/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.payment.PaymentMethodType;
@XmlRootElement(name = "PaymentMethodTypePacket")
public class PaymentMethodTypePacket extends PostPacket
{
	private PaymentMethodType paymentMethodType;

	public PaymentMethodType getPaymentMethodType()
	{
		return paymentMethodType;
	}

	public void setPaymentMethodType(PaymentMethodType paymentMethodType)
	{
		this.paymentMethodType = paymentMethodType;
	}

	@Override
	public String toString()
	{
		return "PaymentMethodTypePacket [paymentMethodType=" + paymentMethodType + "]";
	}

}
