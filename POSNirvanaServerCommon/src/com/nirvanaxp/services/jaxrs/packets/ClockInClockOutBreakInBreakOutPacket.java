/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.employee.EmployeesToEmployeesOperation;
import com.nirvanaxp.types.entities.user.User;

@XmlRootElement(name = "ClockInClockOutBreakInBreakOutPacket")
public class ClockInClockOutBreakInBreakOutPacket
{
	private String locationsId;
	private String fromDate;
	private String toDate;
	private String employeeIds;
	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}
	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}
	public String getFromDate()
	{
		return fromDate;
	}
	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}
	public String getToDate()
	{
		return toDate;
	}
	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}
	public String getEmployeeIds()
	{
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds)
	{
		this.employeeIds = employeeIds;
	}
	

	

}
