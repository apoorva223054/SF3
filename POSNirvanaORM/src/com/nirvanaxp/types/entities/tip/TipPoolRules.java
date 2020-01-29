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
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the order_source database table.
 * 
 */
@Entity
@Table(name = "tip_pool_rules")
@XmlRootElement(name = "tip_pool_rules")
public class TipPoolRules extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "tip_class_id")
	int tipClassId;
	
	@Column(name = "tip_pool_id")
	int tipPoolId;
	
	@Column(name = "name")
	String name;

	@Column(name = "display_name")
	String displayName;
	
	@Column(name = "order_source_group_id")
	String orderSourceGroupId;
	
	@Column(name = "item_group_id")
	String itemGroupId;
	
	@Column(name = "section_id")
	String sectionId;
	
	@Column(name = "job_role_id")
	String jobRoleId;
	
	@Column(name = "tip_pool_basis_id")
	int tipPoolBasisId;
	
	@Column(name = "tip_rate")
	BigDecimal tipRate;
	
	@Column(name = "from_job_role_id")
	String fromJobRoleId;
	
	@Column(name = "location_id")
	String locationId;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "effective_start_date")
	private Date effectiveStartDate;
	
	
	@Temporal(TemporalType.DATE)
	@Column(name = "effective_end_date")
	private Date effectiveEndDate;
	
	/*@Column(name = "start_time")
	private String startTime;

	
	@Column(name = "end_time")
	private String endTime;*/
	
	public Date getEffectiveStartDate()
	{
		return this.effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate)
	{
		this.effectiveStartDate = effectiveStartDate;
	}
	
	
	public Date getEffectiveEndDate()
	{
		return this.effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate)
	{
		this.effectiveEndDate = effectiveEndDate;
	}
	
	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public TipPoolRules()
	{
	}

	public int getTipClassId()
	{
		return tipClassId;
	}

	public void setTipClassId(int tipClassId)
	{
		this.tipClassId = tipClassId;
	}

	public int getTipPoolId()
	{
		return tipPoolId;
	}

	public void setTipPoolId(int tipPoolId)
	{
		this.tipPoolId = tipPoolId;
	}

	public String getOrderSourceGroupId()
	{
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}

	public void setOrderSourceGroupId(String orderSourceGroupId)
	{
		this.orderSourceGroupId = orderSourceGroupId;
	}

	public String getItemGroupId()
	{
		 if(itemGroupId != null && (itemGroupId.length()==0 || itemGroupId.equals("0"))){return null;}else{	return itemGroupId;}
	}

	public void setItemGroupId(String itemGroupId)
	{
		this.itemGroupId = itemGroupId;
	}

	public String getSectionId()
	{
		 if(sectionId != null && (sectionId.length()==0 || sectionId.equals("0"))){return null;}else{	return sectionId;}
	}

	public void setSectionId(String sectionId)
	{
		this.sectionId = sectionId;
	}

	public String getJobRoleId()
	{
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}

	public void setJobRoleId(String jobRoleId)
	{
		this.jobRoleId = jobRoleId;
	}

	public int getTipPoolBasisId()
	{
		return tipPoolBasisId;
	}

	public void setTipPoolBasisId(int tipPoolBasisId)
	{
		this.tipPoolBasisId = tipPoolBasisId;
	}

	public BigDecimal getTipRate()
	{
		return tipRate;
	}

	public void setTipRate(BigDecimal tipRate)
	{
		this.tipRate = tipRate;
	}

	public String getFromJobRoleId()
	{
		return fromJobRoleId;
	}

	public void setFromJobRoleId(String fromJobRoleId)
	{
		this.fromJobRoleId = fromJobRoleId;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return "TipPoolRules [tipClassId=" + tipClassId + ", tipPoolId=" + tipPoolId + ", name=" + name + ", displayName=" + displayName + ", orderSourceGroupId=" + orderSourceGroupId
				+ ", itemGroupId=" + itemGroupId + ", sectionId=" + sectionId + ", jobRoleId=" + jobRoleId + ", tipPoolBasisId=" + tipPoolBasisId + ", tipRate=" + tipRate + ", fromJobRoleId="
				+ fromJobRoleId + ", locationId=" + locationId + ", effectiveStartDate=" + effectiveStartDate + ", effectiveEndDate=" + effectiveEndDate + "]";
	}

/*	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}*/

	

/*	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}*/
	
	
}