package com.nirvanaxp.types.entities.employee;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigIntWithGeneratedId;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;


/**
 * The persistent class for the employee_operation_to_cash_register database table.
 * 
 */
@Entity
@Table(name="employee_operation_to_cash_register")
@NamedQuery(name="EmployeeOperationToCashRegister.findAll", query="SELECT e FROM EmployeeOperationToCashRegister e")
public class EmployeeOperationToCashRegister extends POSNirvanaBaseClassWithoutGeneratedIds implements Serializable  {
	private static final long serialVersionUID = 1L;

	
	private double amount;

	private String comments;

	@Column(name="employee_operation_id")
	private String employeeOperationId;

	@Column(name="locations_id")
	private String locationsId;

	@Column(name="reasons_id")
	private String reasonsId;

	@Column(name="register_id")
	private String registerId;
	
	@Column(name = "nirvanaxp_batch_number")
	private String nirvanaXpBatchNumber;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name = "display_id")
	private String displayId;
	
	public EmployeeOperationToCashRegister() {
	}


	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	

	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getEmployeeOperationId() {
		 if(employeeOperationId != null && (employeeOperationId.length()==0 || employeeOperationId.equals("0"))){return null;}else{	return employeeOperationId;}
	}

	public void setEmployeeOperationId(String employeeOperationId) {
		this.employeeOperationId = employeeOperationId;
	}

	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	public String getReasonsId() {
		 if(reasonsId != null && (reasonsId.length()==0 || reasonsId.equals("0"))){return null;}else{	 if(reasonsId != null && (reasonsId.length()==0 || reasonsId.equals("0"))){return null;}else{	return reasonsId;}}
	}

	public void setReasonsId(String reasonsId) {
		this.reasonsId = reasonsId;
	}

	public String getRegisterId() {
		 if(registerId != null && (registerId.length()==0 || registerId.equals("0"))){return null;}else{	return registerId;}
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	 

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getNirvanaXpBatchNumber() {
		return nirvanaXpBatchNumber;
	}
	public void setNirvanaXpBatchNumber(String nirvanaXpBatchNumber) {
		this.nirvanaXpBatchNumber = nirvanaXpBatchNumber;
	}

	
	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	@Override
	public String toString() {
		return "EmployeeOperationToCashRegister [amount=" + amount + ", comments=" + comments + ", employeeOperationId="
				+ employeeOperationId + ", locationsId=" + locationsId + ", reasonsId=" + reasonsId + ", registerId="
				+ registerId + ", nirvanaXpBatchNumber=" + nirvanaXpBatchNumber + ", userId=" + userId + ", localTime="
				+ localTime + "]";
	}
 
	

}