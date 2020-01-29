/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.tip.TipPool;

@XmlRootElement(name = "TipPoolPacket")
public class TipPoolPacket extends PostPacket
{

	private TipPool tipPool;

	public TipPool getTipPool() {
		return tipPool;
	}

	public void setTipPool(TipPool tipPool) {
		this.tipPool = tipPool;
	}
	
	@Override
	public String toString()
	{
		return "TipPoolPacket [tipPool=" + tipPool + "]";
	}


}
