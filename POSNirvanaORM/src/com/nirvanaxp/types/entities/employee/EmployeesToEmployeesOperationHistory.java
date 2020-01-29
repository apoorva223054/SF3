/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.employee;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the employees_to_employees_operations database
 * table.
 * 
 */
@Entity
@Table(name = "employees_to_employees_operations_history")
@NamedQuery(name = "EmployeesToEmployeesOperationHistory.findAll", query = "SELECT e FROM EmployeesToEmployeesOperationHistory e")
public class EmployeesToEmployeesOperationHistory extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = 1L;

	@Column(name = "employee_operation_id")
	private String employeeOperationId;

	@Column(name = "users_id")
	private String usersId;
	
	@Column(name = "employees_to_employees_operations_id")
	private int employeesToEmployeesOperationId;

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
	
	public EmployeesToEmployeesOperationHistory()
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

	public int getEmployeesToEmployeesOperationId() {
		return employeesToEmployeesOperationId;
	}

	public void setEmployeesToEmployeesOperationId(
			int employeesToEmployeesOperationId) {
		this.employeesToEmployeesOperationId = employeesToEmployeesOperationId;
	}
	

	public String getJobRoleId() {
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	public void setJobRoleId(String jobRoleId) {
		this.jobRoleId = jobRoleId;
	}

	

  @Override
	public String toString() {
		return "EmployeesToEmployeesOperationHistory [employeeOperationId="
				+ employeeOperationId + ", usersId=" + usersId
				+ ", employeesToEmployeesOperationId="
				+ employeesToEmployeesOperationId + ", localTime=" + localTime
				+ ", jobRoleId=" + jobRoleId + "]";
	}

public static EmployeesToEmployeesOperationHistory setEmployeesToEmployeesOperationHistory(EmployeesToEmployeesOperation employeesToEmployeesOperation,EntityManager em){
	  EmployeesToEmployeesOperationHistory employeesOperationHistory = new EmployeesToEmployeesOperationHistory();
	  employeesOperationHistory.setCreated(employeesToEmployeesOperation.getCreated());
	  employeesOperationHistory.setCreatedBy(employeesToEmployeesOperation.getCreatedBy());
	  employeesOperationHistory.setEmployeeOperationId(employeesToEmployeesOperation.getEmployeeOperationId());
	  employeesOperationHistory.setStatus(employeesToEmployeesOperation.getStatus());
	  employeesOperationHistory.setUsersId(employeesToEmployeesOperation.getUsersId());
	  employeesOperationHistory.setUpdated(employeesToEmployeesOperation.getUpdated());
	  employeesOperationHistory.setUpdatedBy(employeesToEmployeesOperation.getUpdatedBy());
	  employeesOperationHistory.setEmployeesToEmployeesOperationId(employeesToEmployeesOperation.getId());
	  employeesOperationHistory.setLocalTime(employeesToEmployeesOperation.getLocalTime());
	  employeesOperationHistory.setJobRoleId(employeesToEmployeesOperation.getJobRoleId());
	  
	  return employeesOperationHistory;
  }
}