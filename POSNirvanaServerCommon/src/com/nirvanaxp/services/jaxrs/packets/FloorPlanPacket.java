/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.Floorplan;
@XmlRootElement(name = "FloorPlanPacket")
public class FloorPlanPacket extends PostPacket
{

	private Floorplan floorplan;

	public Floorplan getFloorplan()
	{
		return floorplan;
	}

	public void setFloorplan(Floorplan floorplan)
	{
		this.floorplan = floorplan;
	}

	@Override
	public String toString()
	{
		return "FloorPlanPacket [floorplan=" + floorplan + "]";
	}

}
