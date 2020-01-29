/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.tip.Department;

@XmlRootElement(name = "DepartmentPacket")
public class DepartmentPacket extends PostPacket
{
	private Department departments;
	private String locationsListId;
	private int isBaseLocationUpdate;
	
	
	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	public Department getDepartments() {
		return departments;
	}

	public void setDepartments(Department departments) {
		this.departments = departments;
	}

	@Override
	public String toString() {
		return "DepartmentPacket [departments=" + departments + ", locationsListId=" + locationsListId
				+ ", isBaseLocationUpdate=" + isBaseLocationUpdate + "]";
	}


}
