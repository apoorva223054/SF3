/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;



import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BreakAndClockInOutGetPacket")
public class BreakAndClockInOutGetPacket
{
	private String clockOutDateTime;
	private String clockInDateTime;
	private String breakOutDateTime;
	private String breakInDateTime;
	private String clockInDate;
	private String timeDiff;
	private int id;
	private String name;
	private int clockInId;
	private int clockOutId;
	private int breakInId;
	private int breakOutId;
	private String userId;
	private String breakTime;
	private String jobRoleId ;
	private String shiftId ;
	
	


	public String getBreakTime()
	{
		return breakTime;
	}




	public void setBreakTime(String breakTime)
	{
		this.breakTime = breakTime;
	}




	public String getShiftId()
	{
		 if(shiftId != null && (shiftId.length()==0 || shiftId.equals("0"))){return null;}else{	return shiftId;}
	}




	public void setShiftId(String shiftId)
	{
		this.shiftId = shiftId;
	}




	public String getJobRoleId()
	{
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}




	public void setJobRoleId(String jobRoleId)
	{
		this.jobRoleId = jobRoleId;
	}




	public String getClockOutDateTime() {
		return clockOutDateTime;
	}




	public void setClockOutDateTime(String clockOutDateTime) {
		this.clockOutDateTime = clockOutDateTime;
	}




	public String getClockInDateTime() {
		return clockInDateTime;
	}




	public void setClockInDateTime(String clockInDateTime) {
		this.clockInDateTime = clockInDateTime;
	}




	public String getBreakOutDateTime() {
		return breakOutDateTime;
	}




	public void setBreakOutDateTime(String breakOutDateTime) {
		this.breakOutDateTime = breakOutDateTime;
	}




	public String getBreakInDateTime() {
		return breakInDateTime;
	}




	public void setBreakInDateTime(String breakInDateTime) {
		this.breakInDateTime = breakInDateTime;
	}




	public String getClockInDate() {
		return clockInDate;
	}




	public void setClockInDate(String clockInDate) {
		this.clockInDate = clockInDate;
	}




	public String getTimeDiff() {
		return timeDiff;
	}




	public void setTimeDiff(String timeDiff) {
		this.timeDiff = timeDiff;
	}




	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public int getClockInId() {
		return clockInId;
	}




	public void setClockInId(int clockInId) {
		this.clockInId = clockInId;
	}




	public int getClockOutId() {
		return clockOutId;
	}




	public void setClockOutId(int clockOutId) {
		this.clockOutId = clockOutId;
	}




	public int getBreakInId() {
		return breakInId;
	}




	public void setBreakInId(int breakInId) {
		this.breakInId = breakInId;
	}




	public int getBreakOutId() {
		return breakOutId;
	}




	public void setBreakOutId(int breakOutId) {
		this.breakOutId = breakOutId;
	}




	public String getUserId() {
		return userId;
	}




	public void setUserId(String userId) {
		this.userId = userId;
	}




	public List<BreakAndClockInOutGetPacket> setBreakAndClockInOutGetPacket(List<Object[]> arrays){
		List<BreakAndClockInOutGetPacket> getPackets = new ArrayList<BreakAndClockInOutGetPacket>();
		BreakAndClockInOutGetPacket packet =null;
		// TODO - check array to be null or not 

		for(Object[] array : arrays){
		int i =0;
		packet = new BreakAndClockInOutGetPacket();
		if(array[i]!= null)
			packet.setName((String)array[i]);
		i++;
		if(array[i]!= null)
			packet.setClockInDate((String)array[i]);
		i++;
		if(array[i]!= null)
			packet.setClockInDateTime((String)array[i]);
		i++;
		if(array[i]!= null)
			packet.setBreakOutDateTime((String)array[i]);
		i++;
		if(array[i]!= null)
			packet.setBreakInDateTime((String)array[i]);
		i++;
		if(array[i]!= null)
			packet.setClockOutDateTime((String)array[i]);
		i++;	
		if(array[i]!= null)
			packet.setUserId((String)array[i]);
		i++; 
		if(array[i]!= null)
			packet.setTimeDiff((String)array[i]);
		i++;
		if(array[i]!= null)
			packet.setClockInId((int)array[i]);
		i++;
		if(array[i]!= null)
			packet.setClockOutId((int)array[i]);
		i++;
		if(array[i]!= null)
			packet.setBreakInId((int)array[i]);
		i++;
		if(array[i]!= null)
			packet.setBreakOutId((int)array[i]);
		i++;
		
		if(array[i]!= null)
			packet.setBreakTime((String)array[i]);
		i++;
		
		if(array[i]!= null)
			packet.setJobRoleId((String)array[i]);
		i++;
	
		if(array[i]!= null)
			packet.setShiftId((String)array[i]);
		i++;
		
		
		if(packet!=null && packet.getName()!=null){
			getPackets.add(packet);
		}
		}
		return getPackets;
	}
}
