/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the order_detail_attribute database table.
 * 
 */

@Entity
@Table(name = "batch_detail")
@XmlRootElement(name = "batch_detail")
public class BatchDetail implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;

	@Column(name = "closeTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date closeTime;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "is_precaptured_error")
	private int isPrecapturedError;

	@Column(name = "is_batch_settled_error")
	private int isBatchSettledError;

	@Column(name = "status")
	private String status;

	@Column(name = "local_time")
	private String localTime;

	@Column(name = "is_tip_calculated")
	private String isTipCalculated;
	
	@Column(name = "day_of_year")
	private String dayOfYear;
	 
	private transient boolean isTipSettle;
	public BatchDetail()
	{
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public long getStartTime()
	{
		if (this.startTime != null)
		{
			return this.startTime.getTime();
		}
		return 0;
	}

	public void setStartTime(long startTime)
	{
		if (startTime != 0)
		{
			this.startTime = new Date(startTime);
		}
	}

	public long getCloseTime()
	{
		if (this.closeTime != null)
		{
			return this.closeTime.getTime();
		}
		return 0;
	}

	public void setCloseTime(long closeTime)
	{
		if (closeTime != 0)
		{
			this.closeTime = new Date(closeTime);
		}
	}


	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getIsPrecapturedError()
	{
		return isPrecapturedError;
	}

	public void setIsPrecapturedError(int isPrecapturedError)
	{
		this.isPrecapturedError = isPrecapturedError;
	}

	public int getIsBatchSettledError()
	{
		return isBatchSettledError;
	}

	public void setIsBatchSettledError(int isBatchSettledError)
	{
		this.isBatchSettledError = isBatchSettledError;
	}

	public String getIsTipCalculated() {
		return isTipCalculated;
	}

	public void setIsTipCalculated(String isTipCalculated) {
		this.isTipCalculated = isTipCalculated;
	}


	public boolean isTipSettle()
	{
		return isTipSettle;
	}

	public void setTipSettle(boolean isTipSettle)
	{
		this.isTipSettle = isTipSettle;
	}

	public String getDayOfYear() {
		return dayOfYear;
	}

	public void setDayOfYear(String dayOfYear) {
		this.dayOfYear = dayOfYear;
	}

	@Override
	public String toString()
	{
		return "BatchDetail [id=" + id + ", startTime=" + startTime + ", closeTime=" + closeTime + ", updatedBy=" + updatedBy + ", locationId=" + locationId + ", isPrecapturedError="
				+ isPrecapturedError + ", isBatchSettledError=" + isBatchSettledError + ", status=" + status + ", localTime=" + localTime + "]";
	}

	

}