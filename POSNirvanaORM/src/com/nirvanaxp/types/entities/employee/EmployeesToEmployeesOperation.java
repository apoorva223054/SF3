/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.employee;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.tip.EmployeeMasterToJobRoles;
import com.nirvanaxp.types.entities.user.User;

/**
 * The persistent class for the employees_to_employees_operations database
 * table.
 * 
 */
@Entity
@Table(name = "employees_to_employees_operations")
@NamedQuery(name = "EmployeesToEmployeesOperation.findAll", query = "SELECT e FROM EmployeesToEmployeesOperation e")
public class EmployeesToEmployeesOperation extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = 1L;

	@Column(name = "employee_operation_id")
	private String employeeOperationId;

	@Column(name = "users_id")
	private String usersId;
	
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "job_role_id")
	private String jobRoleId;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	private transient String updatedStr;
	private transient User user;

	
	transient List<EmployeeMasterToJobRoles> employeeMasterToJobRoles;
	
	

	public List<EmployeeMasterToJobRoles> getEmployeeMasterToJobRoles()
	{
		return employeeMasterToJobRoles;
	}

	public void setEmployeeMasterToJobRoles(List<EmployeeMasterToJobRoles> employeeMasterToJobRoles)
	{
		this.employeeMasterToJobRoles = employeeMasterToJobRoles;
	}
	
	public EmployeesToEmployeesOperation()
	{
	}

	public String getEmployeeOperationId()
	{
		 if(employeeOperationId != null && (employeeOperationId.length()==0 || employeeOperationId.equals("0"))){return null;}else{	return employeeOperationId;}
	}

	public void setEmployeeOperationId(String employeeOperationId)
	{
		this.employeeOperationId = employeeOperationId;
	}


	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public String getUpdatedStr() {
		return updatedStr;
	}

	public void setUpdatedStr(String updatedStr) {
		this.updatedStr = updatedStr;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getJobRoleId() {
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	public void setJobRoleId(String jobRoleId) {
		this.jobRoleId = jobRoleId;
	}

	@Override
	public String toString() {
		return "EmployeesToEmployeesOperation [employeeOperationId="
				+ employeeOperationId + ", usersId=" + usersId + ", localTime="
				+ localTime + ", jobRoleId=" + jobRoleId + "]";
	}

	
	

}