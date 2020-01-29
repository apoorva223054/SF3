/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;



import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "EmployeeToEmployeeOperationGetPacket")
public class ClockInClockOutGetPacket
{
	private String clockOutDateTime;
	private String clockInDateTime;
	private String clockInDate;
	private String timeDiff;
	private int id;
	private String name;

	

	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
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



	public List<ClockInClockOutGetPacket> setEmployeeToEmployeeOperationGetPacket(List<Object[]> arrays){
		List<ClockInClockOutGetPacket> getPackets = new ArrayList<ClockInClockOutGetPacket>();
		ClockInClockOutGetPacket clockInClockOutGetPacket =null;
		// TODO - check array to be null or not 
		for(Object[] array : arrays){
		
			clockInClockOutGetPacket = new ClockInClockOutGetPacket();
		if(array[0]!= null)
			clockInClockOutGetPacket.setName((String)array[0]);
		if(array[1]!= null)
			clockInClockOutGetPacket.setClockInDate((String)array[1]);
		if(array[2]!= null)
			clockInClockOutGetPacket.setClockInDateTime((String)array[2]);
		if(array[3]!= null)
			clockInClockOutGetPacket.setClockOutDateTime((String)array[3]);
		if(array[4]!= null)
			clockInClockOutGetPacket.setTimeDiff((String)array[4]);
		if(array[5]!= null)
			clockInClockOutGetPacket.setId((int)array[5]);
		
		getPackets.add(clockInClockOutGetPacket);
		}
		return getPackets;
	}
}
