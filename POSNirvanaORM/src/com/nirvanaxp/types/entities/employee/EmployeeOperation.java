/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.employee;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

/**
 * The persistent class for the employee_operations database table.
 * 
 */
@Entity
@Table(name = "employee_operations")
@NamedQuery(name = "EmployeeOperation.findAll", query = "SELECT e FROM EmployeeOperation e")
public class EmployeeOperation extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "operation_display_name")
	private String operationDisplayName;

	@Column(name = "operation_name")
	private String operationName;

	@Column(name = "hex_code")
	private String hexCode;

	@Column(name = "display_sequence")
	private int displaySequence;

	@Column(name = "image_url")
	private String imageUrl;
	
	private transient List<EmployeeOperationToAlertMessage> employeeOperationToAlertMessage;

	public EmployeeOperation()
	{
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getOperationDisplayName()
	{
		return this.operationDisplayName;
	}

	public void setOperationDisplayName(String operationDisplayName)
	{
		this.operationDisplayName = operationDisplayName;
	}

	public String getOperationName()
	{
		return this.operationName;
	}

	public void setOperationName(String operationName)
	{
		this.operationName = operationName;
	}

	/**
	 * @return the hexCode
	 */
	public String getHexCode()
	{
		return hexCode;
	}

	/**
	 * @param hexCode
	 *            the hexCode to set
	 */
	public void setHexCode(String hexCode)
	{
		this.hexCode = hexCode;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl()
	{
		return imageUrl;
	}

	/**
	 * @param imageUrl
	 *            the imageUrl to set
	 */
	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	/**
	 * @return the displaySequence
	 */
	public int getDisplaySequence()
	{
		return displaySequence;
	}

	/**
	 * @param displaySequence
	 *            the displaySequence to set
	 */
	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public boolean equals(EmployeeOperation employeeOperation)
	{
		if (employeeOperation instanceof EmployeeOperation && ((EmployeeOperation) employeeOperation).getId() == this.id)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public List<EmployeeOperationToAlertMessage> getEmployeeOperationToAlertMessage() {
		return employeeOperationToAlertMessage;
	}

	public void setEmployeeOperationToAlertMessage(List<EmployeeOperationToAlertMessage> employeeOperationToAlertMessage) {
		this.employeeOperationToAlertMessage = employeeOperationToAlertMessage;
	}

	@Override
	public String toString() {
		return "EmployeeOperation [locationsId=" + locationsId + ", operationDisplayName=" + operationDisplayName
				+ ", operationName=" + operationName + ", hexCode=" + hexCode + ", displaySequence=" + displaySequence
				+ ", imageUrl=" + imageUrl + "]";
	}
 

}
