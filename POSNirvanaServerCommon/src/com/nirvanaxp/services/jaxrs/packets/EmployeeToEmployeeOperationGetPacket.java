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
public class EmployeeToEmployeeOperationGetPacket
{
	private String  operationDisplayName;
	private String created;
	private String date;
	private String firstName;
	private String lastName;
	private String id;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOperationDisplayName() {
		return operationDisplayName;
	}
	public void setOperationDisplayName(String operationDisplayName) {
		this.operationDisplayName = operationDisplayName;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<EmployeeToEmployeeOperationGetPacket> setEmployeeToEmployeeOperationGetPacket(List<Object[]> arrays){
		List<EmployeeToEmployeeOperationGetPacket> getPackets = new ArrayList<EmployeeToEmployeeOperationGetPacket>();
		EmployeeToEmployeeOperationGetPacket employeeOperationGetPacket =null;
		// TODO- check array is null or not
		for(Object[] array : arrays){
		
		employeeOperationGetPacket = new EmployeeToEmployeeOperationGetPacket();
		if(array[0]!= null)
			employeeOperationGetPacket.setOperationDisplayName((String)array[0]);
		if(array[1]!= null)
			employeeOperationGetPacket.setCreated((String)array[1]);
		if(array[2]!= null)
			employeeOperationGetPacket.setDate((String)array[2]);
		if(array[3]!= null)
			employeeOperationGetPacket.setFirstName((String)array[3]);
		if(array[4]!= null)
			employeeOperationGetPacket.setLastName((String)array[4]);
		if(array[5]!= null)
			employeeOperationGetPacket.setId((String)array[5]);
		
		getPackets.add(employeeOperationGetPacket);
		}
		return getPackets;
	}
}
