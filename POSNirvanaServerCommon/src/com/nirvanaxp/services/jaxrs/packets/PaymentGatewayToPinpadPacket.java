/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;

@XmlRootElement(name = "PaymentGatewayToPinpadPacket")
public class PaymentGatewayToPinpadPacket extends PostPacket
{
	private List<PaymentGatewayToPinpad> pinpadList;
	private PaymentGatewayToPinpad paymentGatewayToPinpad;

	public PaymentGatewayToPinpad getPaymentGatewayToPinpad() {
		return paymentGatewayToPinpad;
	}

	public void setPaymentGatewayToPinpad(
			PaymentGatewayToPinpad paymentGatewayToPinpad) {
		this.paymentGatewayToPinpad = paymentGatewayToPinpad;
	}

	public List<PaymentGatewayToPinpad> getPinpadList()
	{
		return pinpadList;
	}

	public void setPinpadList(List<PaymentGatewayToPinpad> pinpadList)
	{
		this.pinpadList = pinpadList;
	}

	@Override
	public String toString()
	{
		return "PaymentGatewayToPinpadPacket [pinpadList=" + pinpadList + ", paymentGatewayToPinpad=" + paymentGatewayToPinpad + "]";
	}

	 
	
	  
   
	
  
}
