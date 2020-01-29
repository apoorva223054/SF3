/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.tip;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

/**
 * The persistent class for the order_source database table.
 * 
 */
@Entity
@Table(name = "job_roles")
@XmlRootElement(name = "job_roles")
public class JobRoles extends POSNirvanaBaseClassWithoutGeneratedIds implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	String name;

	@Column(name = "display_name")
	String displayName;

	@Column(name = "location_id")
	String locationId;

	@Column(name = "is_tipped_role")
	int isTippedRole;
	
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

	public JobRoles()
	{
	}

	public int getIsTippedRole() {
		return isTippedRole;
	}

	public void setIsTippedRole(int isTippedRole) {
		this.isTippedRole = isTippedRole;
	}

	@Override
	public String toString() {
		return "JobRoles [name=" + name + ", displayName=" + displayName
				+ ", locationId=" + locationId + ", isTippedRole="
				+ isTippedRole + "]";
	}

	
}