/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.discounts.DiscountsType;
@XmlRootElement(name = "DiscountsTypePacket")
public class DiscountsTypePacket extends PostPacket
{
	private DiscountsType discountsType;

	public DiscountsType getDiscountsType()
	{
		return discountsType;
	}

	public void setDiscountsType(DiscountsType discountsType)
	{
		this.discountsType = discountsType;
	}

	@Override
	public String toString()
	{
		return "DiscountsTypePacket [discountsType=" + discountsType + "]";
	}

}
