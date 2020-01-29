/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 * The persistent class for the payment database table.
 * 
 */
@Entity
@NamedQuery(name = "Payment.findAll", query = "SELECT p FROM Payment p")
public class Payment implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name = "amount_due")
	private BigDecimal amountDue;

	@Column(name = "account_id")
	private int accountId;

	@Column(name = " created_name")
	private Timestamp createdName;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_payment_date")
	private Timestamp lastPaymentDate;

	@Column(name = "due_date")
	private Timestamp dueDate;

	@Column(name = "payment_amount")
	private BigDecimal paymentAmount;

	@Column(name = "reminder_time")
	private String reminderTime;

	@Column(name = "updated_name")
	private Timestamp updatedName;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "maxdue_Date")
	private Timestamp maxdueDate;

	@Column(name = "payment_reminder_message")
	private String paymentReminderMessage;

	@Column(name = "application_termination_message")
	private String applicationTerminationMessage;

	public Payment()
	{
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public BigDecimal getAmountDue()
	{
		return this.amountDue;
	}

	public void setAmountDue(BigDecimal amountDue)
	{
		this.amountDue = amountDue;
	}


	public Timestamp getLastPaymentDate()
	{
		return this.lastPaymentDate;
	}

	public void setLastPaymentDate(Timestamp lastPaymentDate)
	{
		this.lastPaymentDate = lastPaymentDate;
	}

	public BigDecimal getPaymentAmount()
	{
		return this.paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount)
	{
		this.paymentAmount = paymentAmount;
	}

	public String getReminderTime()
	{
		return this.reminderTime;
	}

	public void setReminderTime(String reminderTime)
	{
		this.reminderTime = reminderTime;
	}


	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(Timestamp dueDate)
	{
		this.dueDate = dueDate;
	}

	public Timestamp getCreatedName()
	{
		return createdName;
	}

	public void setCreatedName(Timestamp createdName)
	{
		this.createdName = createdName;
	}

	public String getPaymentReminderMessage()
	{
		return paymentReminderMessage;
	}

	public void setPaymentReminderMessage(String paymentReminderMessage)
	{
		this.paymentReminderMessage = paymentReminderMessage;
	}

	public Timestamp getMaxdueDate()
	{
		return maxdueDate;
	}

	public void setMaxdueDate(Timestamp maxdueDate)
	{
		this.maxdueDate = maxdueDate;
	}

	public String getApplicationTerminationMessage()
	{
		return applicationTerminationMessage;
	}

	public void setApplicationTerminationMessage(String applicationTerminationMessage)
	{
		this.applicationTerminationMessage = applicationTerminationMessage;
	}

	public Timestamp getUpdatedName()
	{
		return updatedName;
	}

	public void setUpdatedName(Timestamp updatedName)
	{
		this.updatedName = updatedName;
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", amountDue=" + amountDue
				+ ", accountId=" + accountId + ", createdName=" + createdName
				+ ", createdBy=" + createdBy + ", lastPaymentDate="
				+ lastPaymentDate + ", dueDate=" + dueDate + ", paymentAmount="
				+ paymentAmount + ", reminderTime=" + reminderTime
				+ ", updatedName=" + updatedName + ", updatedBy=" + updatedBy
				+ ", maxdueDate=" + maxdueDate + ", paymentReminderMessage="
				+ paymentReminderMessage + ", applicationTerminationMessage="
				+ applicationTerminationMessage + "]";
	}

}