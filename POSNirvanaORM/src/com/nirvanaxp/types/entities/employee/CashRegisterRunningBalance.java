package com.nirvanaxp.types.entities.employee;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

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
@Table(name="cash_register_running_balance")
@NamedQuery(name="CashRegisterRunningBalance.findAll", query="SELECT e FROM CashRegisterRunningBalance e")
public class CashRegisterRunningBalance extends POSNirvanaBaseClass implements Serializable  {
	private static final long serialVersionUID = 1L;

	@Column(name="opd_id")
	private String opdId;
	
	@Column(name="employee_operation_to_cash_register_id")
	private String employeeOperationToCashRegisterId;
	
	@Column(name="transaction_amount")
	private BigDecimal transactionAmount;
	
	@Column(name="running_balance")
	private BigDecimal runningBalance;
	
	@Column(name="register_id")
	private String registerId;
	
	@Column(name = "nirvanaxp_batch_number")
	private String nirvanaXpBatchNumber;
	
	@Column(name="is_amount_credit_forwarded")
	private int isAmountCarryForwarded;
	
	@Column(name="carry_forward_balance")
	private BigDecimal carryForwardedBalance;

	@Column(name="transaction_status")
	private String transactionStatus;
	
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

	public String getOpdId() {
		 if(opdId != null && (opdId.length()==0 || opdId.equals("0"))){return null;}else{	return opdId;}
	}

	public void setOpdId(String opdId) {
		this.opdId = opdId;
	}

	 

	public String getEmployeeOperationToCashRegisterId() {
		return employeeOperationToCashRegisterId;
	}

	public void setEmployeeOperationToCashRegisterId(String employeeOperationToCashRegisterId) {
		this.employeeOperationToCashRegisterId = employeeOperationToCashRegisterId;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BigDecimal getRunningBalance() {
		return runningBalance;
	}

	public void setRunningBalance(BigDecimal runningBalance) {
		this.runningBalance = runningBalance;
	}

	 
	public String getRegisterId() {
		 if(registerId != null && (registerId.length()==0 || registerId.equals("0"))){return null;}else{	return registerId;}
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public String getNirvanaXpBatchNumber() {
		return nirvanaXpBatchNumber;
	}

	public void setNirvanaXpBatchNumber(String nirvanaXpBatchNumber) {
		this.nirvanaXpBatchNumber = nirvanaXpBatchNumber;
	}

	public int getIsAmountCarryForwarded() {
		return isAmountCarryForwarded;
	}

	public void setIsAmountCarryForwarded(int isAmountCarryForwarded) {
		this.isAmountCarryForwarded = isAmountCarryForwarded;
	}

	public BigDecimal getCarryForwardedBalance() {
		return carryForwardedBalance;
	}

	public void setCarryForwardedBalance(BigDecimal carryForwardedBalance) {
		this.carryForwardedBalance = carryForwardedBalance;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "CashRegisterRunningBalance [  opdId=" + opdId
				+ ", employeeOperationToCashRegisterId=" + employeeOperationToCashRegisterId + ", transactionAmount="
				+ transactionAmount + ", runningBalance=" + runningBalance + ", registerId=" + registerId
				+ ", nirvanaXpBatchNumber=" + nirvanaXpBatchNumber + ", isAmountCarryForwarded="
				+ isAmountCarryForwarded + ", carryForwardedBalance=" + carryForwardedBalance + ", transactionStatus="
				+ transactionStatus + "]";
	}

 
	

}