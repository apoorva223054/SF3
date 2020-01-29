/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.employee;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.tip.EmployeeMasterToJobRoles;
import com.nirvanaxp.types.entities.user.User;

/**
 * The persistent class for the employees_to_employees_operations database
 * table.
 * 
 */
@Entity
@Table(name = "clock_in_clock_out_history")
@NamedQuery(name = "ClockInClockOutHistory.findAll", query = "SELECT e FROM ClockInClockOutHistory e")
public class ClockInClockOutHistory extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = 1L;

	
	@Column(name = "users_id")
	private String usersId;

	@Column(name = "clock_in_operation_id")
	private String clockInOperationId;
	
	@Column(name = "clock_in")
	protected Date clockIn;
	
	@Column(name = "clock_out_operation_id")
	private String clockOutOperationId;
	
	@Column(name = "clock_out")
	protected Date clockOut;
	
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "session_id")
	private String sessionId;
	
	@Column(name = "location_id")
	private String locationId;
	
	
	@Column(name = "job_role_id")
	private String jobRoleId;
	
	@Column(name = "clock_in_clock_out_id")
	private int clockInClockOutId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "clock_in_clock_out_created")
	protected Date clockInClockOutCreated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "clock_in_clock_out_updated")
	protected Date clockInClockOutUpdated;

	@Column(name = "source_name")
	private String sourceName;
	
	public String getSourceName()
	{
		return sourceName;
	}


	public void setSourceName(String sourceName)
	{
		this.sourceName = sourceName;
	}

	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}


	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}


	public String getClockInOperationId()
	{
		 if(clockInOperationId != null && (clockInOperationId.length()==0 || clockInOperationId.equals("0"))){return null;}else{	return clockInOperationId;}
	}

	public void setClockInOperationId(String clockInOperationId)
	{
		this.clockInOperationId = clockInOperationId;
	}

	public Date getClockIn()
	{
		return clockIn;
	}

	public void setClockIn(Date clockIn)
	{
		this.clockIn = clockIn;
	}

	public String getClockOutOperationId()
	{
		 if(clockOutOperationId != null && (clockOutOperationId.length()==0 || clockOutOperationId.equals("0"))){return null;}else{	return clockOutOperationId;}
	}

	public void setClockOutOperationId(String clockOutOperationId)
	{
		this.clockOutOperationId = clockOutOperationId;
	}

	public Date getClockOut()
	{
		return clockOut;
	}

	public void setClockOut(Date clockOut)
	{
		this.clockOut = clockOut;
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public String getJobRoleId()
	{
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	public void setJobRoleId(String jobRoleId)
	{
		this.jobRoleId = jobRoleId;
	}

	public int getClockInClockOutId()
	{
		return clockInClockOutId;
	}

	public void setClockInClockOutId(int clockInClockOutId)
	{
		this.clockInClockOutId = clockInClockOutId;
	}

	public Date getClockInClockOutCreated()
	{
		return clockInClockOutCreated;
	}

	public void setClockInClockOutCreated(Date clockInClockOutCreated)
	{
		this.clockInClockOutCreated = clockInClockOutCreated;
	}

	public Date getClockInClockOutUpdated()
	{
		return clockInClockOutUpdated;
	}

	public void setClockInClockOutUpdated(Date clockInClockOutUpdated)
	{
		this.clockInClockOutUpdated = clockInClockOutUpdated;
	}


	@Override
	public String toString()
	{
		return "ClockInClockOutHistory [usersId=" + usersId + ", clockInOperationId=" + clockInOperationId + ", clockIn=" + clockIn + ", clockOutOperationId=" + clockOutOperationId + ", clockOut="
				+ clockOut + ", localTime=" + localTime + ", sessionId=" + sessionId + ", locationId=" + locationId + ", jobRoleId=" + jobRoleId + ", clockInClockOutId=" + clockInClockOutId
				+ ", clockInClockOutCreated=" + clockInClockOutCreated + ", clockInClockOutUpdated=" + clockInClockOutUpdated + ", sourceName=" + sourceName + "]";
	}

	
	  	
	

}