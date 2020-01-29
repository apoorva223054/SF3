/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.employee;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the employees_to_employees_operations database
 * table.
 * 
 */
@Entity
@Table(name = "break_in_break_out_history")
@NamedQuery(name = "BreakInBreakOutHistory.findAll", query = "SELECT e FROM BreakInBreakOutHistory e")
public class BreakInBreakOutHistory extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = 1L;

	
	@Column(name = "users_id")
	private String usersId;

	@Column(name = "break_in_break_out_id")
	private int breakInBreakOutId;
	
	@Column(name = "clock_in_clock_out_id")
	private int clockInClockOutId;
	
	@Column(name = "break_in_operation_id")
	private String breakInOperationId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "break_in")
	protected Date breakIn;
	
	@Column(name = "break_out_operation_id")
	private String breakOutOperationId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "break_out")
	protected Date breakOut;
	
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "session_id")
	private String sessionId;
	
	@Column(name = "location_id")
	private String locationId;
	
	
	@Column(name = "job_role_id")
	private String jobRoleId;

	@Column(name = "source_name")
	private String sourceName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "break_in_break_out_created")
	private Date breakInBreakOutCreated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "break_in_break_out_updated")
	private Date breakInBreakOutUpdated;
	
	@Column(name = "clock_in_clock_out_history_id")
	private int clockInClockOutHistoryId;
	
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


	public int getBreakInBreakOutId()
	{
		return breakInBreakOutId;
	}


	public void setBreakInBreakOutId(int breakInBreakOutId)
	{
		this.breakInBreakOutId = breakInBreakOutId;
	}


	public int getClockInClockOutId()
	{
		return clockInClockOutId;
	}


	public void setClockInClockOutId(int clockInClockOutId)
	{
		this.clockInClockOutId = clockInClockOutId;
	}


	public Date getBreakIn()
	{
		return breakIn;
	}


	public void setBreakIn(Date breakIn)
	{
		this.breakIn = breakIn;
	}


	public Date getBreakOut()
	{
		return breakOut;
	}


	public void setBreakOut(Date breakOut)
	{
		this.breakOut = breakOut;
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


	public Date getBreakInBreakOutCreated()
	{
		return breakInBreakOutCreated;
	}


	public void setBreakInBreakOutCreated(Date breakInBreakOutCreated)
	{
		this.breakInBreakOutCreated = breakInBreakOutCreated;
	}


	public Date getBreakInBreakOutUpdated()
	{
		return breakInBreakOutUpdated;
	}


	public void setBreakInBreakOutUpdated(Date breakInBreakOutUpdated)
	{
		this.breakInBreakOutUpdated = breakInBreakOutUpdated;
	}


	public int getClockInClockOutHistoryId()
	{
		return clockInClockOutHistoryId;
	}


	public void setClockInClockOutHistoryId(int clockInClockOutHistoryId)
	{
		this.clockInClockOutHistoryId = clockInClockOutHistoryId;
	}


	
	
	public String getBreakInOperationId() {
		 if(breakInOperationId != null && (breakInOperationId.length()==0 || breakInOperationId.equals("0"))){return null;}else{	return breakInOperationId;}
	}


	public void setBreakInOperationId(String breakInOperationId) {
		this.breakInOperationId = breakInOperationId;
	}


	public String getBreakOutOperationId() {
		 if(breakOutOperationId != null && (breakOutOperationId.length()==0 || breakOutOperationId.equals("0"))){return null;}else{	return breakOutOperationId;}
	}


	public void setBreakOutOperationId(String breakOutOperationId) {
		this.breakOutOperationId = breakOutOperationId;
	}


	@Override
	public String toString()
	{
		return "BreakInBreakOutHistory [usersId=" + usersId + ", breakInBreakOutId=" + breakInBreakOutId + ", clockInClockOutId=" + clockInClockOutId + ", breakInOperationId=" + breakInOperationId
				+ ", breakIn=" + breakIn + ", breakOutOperationId=" + breakOutOperationId + ", breakOut=" + breakOut + ", localTime=" + localTime + ", sessionId=" + sessionId + ", locationId="
				+ locationId + ", jobRoleId=" + jobRoleId + ", sourceName=" + sourceName + ", breakInBreakOutCreated=" + breakInBreakOutCreated + ", breakInBreakOutUpdated=" + breakInBreakOutUpdated
				+ ", clockInClockOutHistoryId=" + clockInClockOutHistoryId + "]";
	}


	

	

	
	

}