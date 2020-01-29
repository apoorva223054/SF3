/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.util.JSONWrappedObject;

@XmlRootElement(name = "LookupPacket")
public class LookupPacket extends PostPacket
{

	JSONWrappedObject lookupObj;

	public JSONWrappedObject getLookupObj()
	{
		return lookupObj;
	}

	public void setLookupObj(JSONWrappedObject lookupObj)
	{
		this.lookupObj = lookupObj;
	}

	@Override
	public String toString()
	{
		return "LookupPacket [lookupObj=" + lookupObj + "]";
	}

}
