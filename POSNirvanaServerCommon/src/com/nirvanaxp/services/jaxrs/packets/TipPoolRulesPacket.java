/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.tip.TipPoolRules;

@XmlRootElement(name = "TipPoolRulesPacket")
public class TipPoolRulesPacket extends PostPacket
{

	private TipPoolRules tipPoolRules;


	public TipPoolRules getTipPoolRules() {
		return tipPoolRules;
	}
	public void setTipPoolRules(TipPoolRules tipPoolRules) {
		this.tipPoolRules = tipPoolRules;
	}

	@Override
	public String toString()
	{
		return "TipPoolRulesPacket [tipPoolRules=" + tipPoolRules + "]";
	}



}
