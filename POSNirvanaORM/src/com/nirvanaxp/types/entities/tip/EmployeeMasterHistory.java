/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.tip;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.partners.TimezoneTime;
import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the order_source database table.
 * 
 */
@Entity
@Table(name = "employee_master_history")
@XmlRootElement(name = "employee_master_history")
public class EmployeeMasterHistory extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	String userId;

	@Column(name = "job_role_id")
	String jobRoleId;
	
	@Column(name = "employee_master_id")
	String employeeMasterId;

	@Column(name = "department_id")
	String departmentId;

	@Column(name = "is_tipped_employee")
	int isTippedEmployee;

	@Column(name = "tip_class_id")
	int tipClassId;

	@Column(name = "hourly_rate")
	BigDecimal hourlyRate;
	
	@Column(name = "locations_id", nullable = false)
	private String locationsId;

/*	@Column(name = "is_default_role")
	int isDefaultRole;
	*/
	public BigDecimal getHourlyRate()
	{
		return hourlyRate;
	}

	public void setHourlyRate(BigDecimal hourlyRate)
	{
		this.hourlyRate = hourlyRate;
	}

	private transient String empName;

	public EmployeeMasterHistory()
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

	public String getJobRoleId()
	{
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	public void setJobRoleId(String jobRoleId)
	{
		this.jobRoleId = jobRoleId;
	}

	public String getDepartmentId()
	{
		return departmentId;
	}

	public void setDepartmentId(String departmentId)
	{
		this.departmentId = departmentId;
	}

	public int getIsTippedEmployee()
	{
		return isTippedEmployee;
	}

	public void setIsTippedEmployee(int isTippedEmployee)
	{
		this.isTippedEmployee = isTippedEmployee;
	}

	public int getTipClassId()
	{
		return tipClassId;
	}

	public void setTipClassId(int tipClassId)
	{
		this.tipClassId = tipClassId;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}
	
	
	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	 
	public String getEmployeeMasterId() {
		 if(employeeMasterId != null && (employeeMasterId.length()==0 || employeeMasterId.equals("0"))){return null;}else{	return employeeMasterId;}
	}

	public void setEmployeeMasterId(String employeeMasterId) {
		this.employeeMasterId = employeeMasterId;
	}
	
	
/*
	public int getIsDefaultRole() {
		return isDefaultRole;
	}

	public void setIsDefaultRole(int isDefaultRole) {
		this.isDefaultRole = isDefaultRole;
	}*/

	public EmployeeMasterHistory employeeMasterHistoryObject(EmployeeMaster employeeMaster){
		EmployeeMasterHistory history= new EmployeeMasterHistory();
		history.setCreated(employeeMaster.getCreated());
		history.setCreatedBy(employeeMaster.getCreatedBy());
		history.setDepartmentId(employeeMaster.getDepartmentId());
		history.setEmpName(employeeMaster.getEmpName());
		history.setHourlyRate(employeeMaster.getHourlyRate());
		history.setIsTippedEmployee(employeeMaster.getIsTippedEmployee());
		history.setJobRoleId(employeeMaster.getJobRoleId());
		history.setLocationsId(employeeMaster.getLocationsId());
		history.setStatus(employeeMaster.getStatus());
		history.setTipClassId(employeeMaster.getTipClassId());
		history.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		history.setUpdatedBy(employeeMaster.getUpdatedBy());
		history.setUserId(employeeMaster.getUserId());
		history.setDepartmentId(employeeMaster.getDepartmentId());
		history.setEmployeeMasterId(employeeMaster.getId());
		/*history.setIsDefaultRole(employeeMaster.getIsDefaultRole());*/
		return history;
	}

	@Override
	public String toString() {
		return "EmployeeMasterHistory [userId=" + userId + ", jobRoleId="
				+ jobRoleId + ", employeeMasterId=" + employeeMasterId
				+ ", departmentId=" + departmentId + ", isTippedEmployee="
				+ isTippedEmployee + ", tipClassId=" + tipClassId
				+ ", hourlyRate=" + hourlyRate + ", locationsId=" + locationsId+"]";
	}

	
	 

}