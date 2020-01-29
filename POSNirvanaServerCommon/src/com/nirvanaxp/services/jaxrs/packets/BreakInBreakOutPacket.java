/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.employee.BreakInBreakOut;
import com.nirvanaxp.types.entities.employee.ClockInClockOut;
import com.nirvanaxp.types.entities.employee.EmployeeOperation;

@XmlRootElement(name = "BreakInBreakOutPacket")
public class BreakInBreakOutPacket extends PostPacket
{
	private BreakInBreakOut breakInBreakOut;
	private int isUpdate;

	public BreakInBreakOut getBreakInBreakOut()
	{
		return breakInBreakOut;
	}

	public void setBreakInBreakOut(BreakInBreakOut breakInBreakOut)
	{
		this.breakInBreakOut = breakInBreakOut;
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
		return "BreakInBreakOutPacket [breakInBreakOut=" + breakInBreakOut + ", isUpdate=" + isUpdate + "]";
	}


	

}
