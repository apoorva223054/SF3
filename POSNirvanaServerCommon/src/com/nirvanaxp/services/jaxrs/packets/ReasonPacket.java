/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reasons.Reasons;
@XmlRootElement(name = "ReasonPacket")
public class ReasonPacket extends PostPacket
{

	private Reasons reasons;

	public Reasons getReasons()
	{
		return reasons;
	}

	public void setReasons(Reasons reasons)
	{
		this.reasons = reasons;
	}

	@Override
	public String toString()
	{
		return "ReasonPacket [reasons=" + reasons + "]";
	}

}