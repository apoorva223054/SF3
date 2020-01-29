/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.DeliveryOption;
@XmlRootElement(name = "DeliveryOptionPacket")
public class DeliveryOptionPacket extends PostPacket
{

	private DeliveryOption deliveryOption;

	
	public DeliveryOption getDeliveryOption() {
		return deliveryOption;
	}

	public void setDeliveryOption(DeliveryOption deliveryOption) {
		this.deliveryOption = deliveryOption;
	}

	@Override
	public String toString()
	{
		return "DeliveryOptionPacket [deliveryOption=" + deliveryOption + "]";
	}
}
