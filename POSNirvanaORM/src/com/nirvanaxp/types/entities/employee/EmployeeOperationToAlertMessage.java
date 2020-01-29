package com.nirvanaxp.types.entities.employee;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;


/**
 * The persistent class for the employee_operation_to_cash_register database table.
 * 
 */
@Entity
@Table(name="employee_operation_to_alert_message")
@NamedQuery(name="EmployeeOperationToAlertMessage.findAll", query="SELECT e FROM EmployeeOperationToAlertMessage e")
public class EmployeeOperationToAlertMessage extends POSNirvanaBaseClass implements Serializable  {
	private static final long serialVersionUID = 1L;

	
	@Column(name="alert_message")
	private String alertMessage;
	
	@Column(name="alert_name")
	private String alertName;

	@Column(name="employee_operation_id")
	private String employeeOperationId;
	
	@Column(name="display_sequence")
	private int displaySequence;

	public String getEmployeeOperationId() {
		 if(employeeOperationId != null && (employeeOperationId.length()==0 || employeeOperationId.equals("0"))){return null;}else{	return employeeOperationId;}
	}

	public void setEmployeeOperationId(String employeeOperationId) {
		this.employeeOperationId = employeeOperationId;
	}

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public String getAlertName() {
		return alertName;
	}

	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}

	public int getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}

	@Override
	public String toString() {
		return "EmployeeOperationToAlertMessage [alertMessage=" + alertMessage + ", alertName=" + alertName
				+ ", employeeOperationId=" + employeeOperationId + ", displaySequence=" + displaySequence + "]";
	}

	  
}