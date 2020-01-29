/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.tip.TipPoolBasis;

@XmlRootElement(name = "TipPoolBasisPacket")
public class TipPoolBasisPacket extends PostPacket
{

	private TipPoolBasis tipPoolBasis;

	public TipPoolBasis getTipPoolBasis() {
		return tipPoolBasis;
	}

	public void setTipPoolBasis(TipPoolBasis tipPoolBasis) {
		this.tipPoolBasis = tipPoolBasis;
	}

	@Override
	public String toString()
	{
		return "TipPoolBasisPacket [tipPoolBasis=" + tipPoolBasis + "]";
	}

	
}
