/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.tip;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;


/**
 * The persistent class for the order_source database table.
 * 
 */
@Entity
@Table(name = "department")
@XmlRootElement(name = "department")
public class Department extends POSNirvanaBaseClassWithoutGeneratedIds implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	String name;
	
	@Column(name = "global_id")
	String globalId;
	
	@Column(name = "display_name")
	String displayName;
	
	@Column(name = "location_id")
	String locationId;
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public Department()
	{
	}

	@Override
	public String toString() {
		return "Department [name=" + name + ", globalId=" + globalId + ", displayName=" + displayName + ", locationId="
				+ locationId + "]";
	}
	public Department getDepartments(Department d){
		Department newDepartments = new Department();
		newDepartments.setCreated(d.getCreated());
		newDepartments.setCreatedBy(d.getCreatedBy());
		newDepartments.setDisplayName(d.getDisplayName());
		newDepartments.setLocationId(d.getLocationId());
		newDepartments.setName(d.getName());
		newDepartments.setStatus(d.getStatus());
		newDepartments.setUpdated(d.getUpdated());
		newDepartments.setUpdatedBy(d.getUpdatedBy());
		newDepartments.setGlobalId(d.getGlobalId());
		return newDepartments;
	}

	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}
}