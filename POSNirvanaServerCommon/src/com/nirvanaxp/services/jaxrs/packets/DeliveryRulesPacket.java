/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.DeliveryRules;

@XmlRootElement(name = "DeliveryRulesPacket")
public class DeliveryRulesPacket extends PostPacket
{
	public DeliveryRules deliveryRules ;

	public DeliveryRules getDeliveryRules() {
		return deliveryRules;
	}

	public void setDeliveryRules(DeliveryRules deliveryRules) {
		this.deliveryRules = deliveryRules;
	}

	@Override
	public String toString() {
		return "DeliveryRulesPacket [deliveryRules=" + deliveryRules + "]";
	}

	 
	
	
}
