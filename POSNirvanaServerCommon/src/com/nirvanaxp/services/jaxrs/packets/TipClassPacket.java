/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.tip.TipClass;

@XmlRootElement(name = "TipClassPacket")
public class TipClassPacket extends PostPacket
{

	private TipClass tipClass;

	public TipClass getTipClass() {
		return tipClass;
	}

	public void setTipClass(TipClass tipClass) {
		this.tipClass = tipClass;
	}
	@Override
	public String toString()
	{
		return "TipClassPacket [tipClass=" + tipClass + "]";
	}


}
