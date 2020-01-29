/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.tip;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the order_source database table.
 * 
 */
@Entity
@Table(name = "employee_master_to_job_roles")
@XmlRootElement(name = "employee_master_to_job_roles")
public class EmployeeMasterToJobRoles extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	String userId;

	@Column(name = "job_role_id")
	String jobRoleId;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;
	
	@Column(name = "is_default_role")
	int isDefaultRole;
	
	public int getIsDefaultRole() {
		return isDefaultRole;
	}

	public void setIsDefaultRole(int isDefaultRole) {
		this.isDefaultRole = isDefaultRole;
	}
	
	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}
	
	public EmployeeMasterToJobRoles()
	{
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

 
	public String getJobRoleId() {
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	public void setJobRoleId(String jobRoleId) {
		this.jobRoleId = jobRoleId;
	}

	@Override
	public String toString()
	{
		return "EmployeeMasterToJobRoles [userId=" + userId + ", jobRoleId=" + jobRoleId + ", locationsId=" + locationsId + "]";
	}

	
}