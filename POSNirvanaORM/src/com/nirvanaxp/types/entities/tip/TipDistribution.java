/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.tip;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the order_source database table.
 * 
 */
@Entity
@Table(name = "tip_distribution")
@XmlRootElement(name = "tip_distribution")
public class TipDistribution extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	 
	@Column(name = "user_id")
	String userId;

	@Column(name = "shift_id")
	String shiftId;

	@Column(name = "cash_total")
	BigDecimal cashTotal;

	@Column(name = "card_total")
	BigDecimal cardTotal;

	@Column(name = "credit_total")
	BigDecimal creditTotal;

	@Column(name = "direct_cash_tip")
	BigDecimal directCashTip;
	
	@Column(name = "direct_card_tip")
	BigDecimal directCardTip;
	
	@Column(name = "direct_credit_term_tip")
	BigDecimal directCreditTermTip;


	@Column(name = "indirect_cash_tip")
	BigDecimal indirectCashTip;
	
	@Column(name = "indirect_card_tip")
	BigDecimal indirectCardTip;
	
	@Column(name = "indirect_credit_term_tip")
	BigDecimal indirectCreditTermTip;
	
	@Column(name = "batch_salary")
	BigDecimal batchSalary; //(calculate salary according to clock in clock out hours)
	
	@Column(name = "nirvanaxp_batch_id")
	String nirvanaxpBatchId;
	
	@Column(name = "local_time")
	private String localTime;

	@Column(name = "hourly_rate")
	BigDecimal hourlyRate; //(calculate salary according to clock in clock out hours)
	
	@Column(name = "no_of_hours")
	BigDecimal noOfHours; //(calculate salary according to clock in clock out hours)
	
	
	@Column(name = "job_role_id")
	String jobRoleId;
	
	@Column(name = "section_id")
	String sectionId;
	
	@Column(name = "order_source_group_id")
	String orderSourceGroupId;
	
	public String getSectionId()
	{
		 if(sectionId != null && (sectionId.length()==0 || sectionId.equals("0"))){return null;}else{	return sectionId;}
	}


	public void setSectionId(String sectionId)
	{
		this.sectionId = sectionId;
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	
	public TipDistribution()
	{
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public BigDecimal getBatchSalary()
	{
		return batchSalary;
	}



	public void setBatchSalary(BigDecimal batchSalary)
	{
		this.batchSalary = batchSalary;
	}

	public String getShiftId() {
		 if(shiftId != null && (shiftId.length()==0 || shiftId.equals("0"))){return null;}else{	return shiftId;}
	}


	public void setShiftId(String shiftId) {
		this.shiftId = shiftId;
	}


	public BigDecimal getCashTotal() {
		return cashTotal;
	}


	public void setCashTotal(BigDecimal cashTotal) {
		this.cashTotal = cashTotal;
	}


	public BigDecimal getCardTotal() {
		return cardTotal;
	}


	public void setCardTotal(BigDecimal cardTotal) {
		this.cardTotal = cardTotal;
	}


	public BigDecimal getCreditTotal() {
		return creditTotal;
	}


	public void setCreditTotal(BigDecimal creditTotal) {
		this.creditTotal = creditTotal;
	}


	public BigDecimal getDirectCashTip() {
		return directCashTip;
	}


	public void setDirectCashTip(BigDecimal directCashTip) {
		this.directCashTip = directCashTip;
	}


	public BigDecimal getDirectCardTip() {
		return directCardTip;
	}


	public void setDirectCardTip(BigDecimal directCardTip) {
		this.directCardTip = directCardTip;
	}


	public BigDecimal getDirectCreditTermTip() {
		return directCreditTermTip;
	}


	public void setDirectCreditTermTip(BigDecimal directCreditTermTip) {
		this.directCreditTermTip = directCreditTermTip;
	}


	public BigDecimal getIndirectCashTip() {
		return indirectCashTip;
	}


	public void setIndirectCashTip(BigDecimal indirectCashTip) {
		this.indirectCashTip = indirectCashTip;
	}


	public BigDecimal getIndirectCardTip() {
		return indirectCardTip;
	}


	public void setIndirectCardTip(BigDecimal indirectCardTip) {
		this.indirectCardTip = indirectCardTip;
	}


	public BigDecimal getIndirectCreditTermTip() {
		return indirectCreditTermTip;
	}


	public void setIndirectCreditTermTip(BigDecimal indirectCreditTermTip) {
		this.indirectCreditTermTip = indirectCreditTermTip;
	}


	public String getNirvanaxpBatchId() {
		return nirvanaxpBatchId;
	}


	public void setNirvanaxpBatchId(String nirvanaxpBatchId) {
		this.nirvanaxpBatchId = nirvanaxpBatchId;
	}

	

		public BigDecimal getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(BigDecimal hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public String getJobRoleId() {
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	public void setJobRoleId(String jobRoleId) {
		this.jobRoleId = jobRoleId;
	}

	
		public BigDecimal getNoOfHours() {
		return noOfHours;
	}

	public void setNoOfHours(BigDecimal noOfHours) {
		this.noOfHours = noOfHours;
	}

	public String getOrderSourceGroupId()
	{
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}

	public void setOrderSourceGroupId(String orderSourceGroupId)
	{
		this.orderSourceGroupId = orderSourceGroupId;
	}


	@Override
	public String toString()
	{
		return "TipDistribution [userId=" + userId + ", shiftId=" + shiftId + ", cashTotal=" + cashTotal + ", cardTotal=" + cardTotal + ", creditTotal=" + creditTotal + ", directCashTip="
				+ directCashTip + ", directCardTip=" + directCardTip + ", directCreditTermTip=" + directCreditTermTip + ", indirectCashTip=" + indirectCashTip + ", indirectCardTip=" + indirectCardTip
				+ ", indirectCreditTermTip=" + indirectCreditTermTip + ", batchSalary=" + batchSalary + ", nirvanaxpBatchId=" + nirvanaxpBatchId + ", localTime=" + localTime + ", hourlyRate="
				+ hourlyRate + ", noOfHours=" + noOfHours + ", jobRoleId=" + jobRoleId + ", sectionId=" + sectionId + ", orderSourceGroupId=" + orderSourceGroupId + "]";
	}


	

}