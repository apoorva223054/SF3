/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OperationalShiftSchedule;


@XmlRootElement(name = "OperationalShiftSchedulePacket")
public class OperationalShiftSchedulePacket extends PostPacket
{
	private OperationalShiftSchedule operationalShiftSchedule;

	public OperationalShiftSchedule getOperationalShiftSchedule()
	{
		return operationalShiftSchedule;
	}

	public void setOperationalShiftSchedule(OperationalShiftSchedule operationalShiftSchedule)
	{
		this.operationalShiftSchedule = operationalShiftSchedule;
	}

	@Override
	public String toString()
	{
		return "OperationalShiftSchedulePacket [operationalShiftSchedule=" + operationalShiftSchedule + "]";
	}

	

	

}
