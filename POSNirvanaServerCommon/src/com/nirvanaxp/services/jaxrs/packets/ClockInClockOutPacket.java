/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.employee.ClockInClockOut;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;

@XmlRootElement(name = "ClockInClockOutPacket")
public class ClockInClockOutPacket extends PostPacket
{
	private ClockInClockOut clockInClockOut;
	private int isUpdate;
	private int isAddUpdateFromMetro;

	public ClockInClockOut getClockInClockOut()
	{
		return clockInClockOut;
	}

	public int getIsAddUpdateFromMetro()
	{
		return isAddUpdateFromMetro;
	}

	public void setIsAddUpdateFromMetro(int isAddUpdateFromMetro)
	{
		this.isAddUpdateFromMetro = isAddUpdateFromMetro;
	}

	public void setClockInClockOut(ClockInClockOut clockInClockOut)
	{
		this.clockInClockOut = clockInClockOut;
	}

	public int getIsUpdate()
	{
		return isUpdate;
	}

	public void setIsUpdate(int isUpdate)
	{
		this.isUpdate = isUpdate;
	}

	@Override
	public String toString()
	{
		return "ClockInClockOutPacket [clockInClockOut=" + clockInClockOut + ", isUpdate=" + isUpdate + "]";
	}

	

}
