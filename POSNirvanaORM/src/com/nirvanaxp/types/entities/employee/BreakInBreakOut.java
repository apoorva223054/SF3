/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.employee;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.user.User;

/**
 * The persistent class for the employees_to_employees_operations database
 * table.
 * 
 */
@Entity
@Table(name = "break_in_break_out")
@NamedQuery(name = "BreakInBreakOut.findAll", query = "SELECT e FROM BreakInBreakOut e")
public class BreakInBreakOut extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = 1L;

	
	@Column(name = "users_id")
	private String usersId;

	@Column(name = "clock_in_clock_out_id")
	private int clockInClockOutId;
	
	@Column(name = "break_in_operation_id")
	private String breakInOperationId;
	
	@Column(name = "break_in")
	protected Date breakIn;
	
	@Column(name = "break_out_operation_id")
	private String breakOutOperationId;
	
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
	
	transient private String breakInStr;
	transient private String breakOutStr;
	
	transient private BigDecimal breakInBreakOutMinutes;
	transient private User user ;
	
	
	
	
	
	
	
	
	
	public BigDecimal getBreakInBreakOutMinutes()
	{
		return breakInBreakOutMinutes;
	}


	public void setBreakInBreakOutMinutes(BigDecimal breakInBreakOutMinutes)
	{
		this.breakInBreakOutMinutes = breakInBreakOutMinutes;
	}


	public User getUser()
	{
		return user;
	}


	public void setUser(User user)
	{
		this.user = user;
	}
	
	
	public String getBreakInStr()
	{
		return breakInStr;
	}


	public void setBreakInStr(String breakInStr)
	{
		this.breakInStr = breakInStr;
	}


	public String getBreakOutStr()
	{
		return breakOutStr;
	}


	public void setBreakOutStr(String breakOutStr)
	{
		this.breakOutStr = breakOutStr;
	}


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

 

	public int getClockInClockOutId() {
		return clockInClockOutId;
	}


	public void setClockInClockOutId(int clockInClockOutId) {
		this.clockInClockOutId = clockInClockOutId;
	}


	public String getBreakInOperationId() {
		 if(breakInOperationId != null && (breakInOperationId.length()==0 || breakInOperationId.equals("0"))){return null;}else{	return breakInOperationId;}
	}


	public void setBreakInOperationId(String breakInOperationId) {
		this.breakInOperationId = breakInOperationId;
	}


	public Date getBreakIn() {
		return breakIn;
	}


	public void setBreakIn(Date breakIn) {
		this.breakIn = breakIn;
	}


	public String getBreakOutOperationId() {
		 if(breakOutOperationId != null && (breakOutOperationId.length()==0 || breakOutOperationId.equals("0"))){return null;}else{	return breakOutOperationId;}
	}


	public void setBreakOutOperationId(String breakOutOperationId) {
		this.breakOutOperationId = breakOutOperationId;
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


	@Override
	public String toString()
	{
		return "BreakInBreakOut [usersId=" + usersId + ", clockInClockOutId=" + clockInClockOutId + ", breakInOperationId=" + breakInOperationId + ", breakIn=" + breakIn + ", breakOutOperationId="
				+ breakOutOperationId + ", breakOut=" + breakOut + ", localTime=" + localTime + ", sessionId=" + sessionId + ", locationId=" + locationId + ", jobRoleId=" + jobRoleId
				+ ", sourceName=" + sourceName + "]";
	}


	

	

}