/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.business;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nirvanaxp.types.entities.Day;

/**
 * The persistent class for the business_hours database table.
 * 
 */
@Entity
@Table(name = "business_hours")
@NamedQuery(name = "BusinessHour.findAll", query = "SELECT b FROM BusinessHour b")
public class BusinessHour implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "isclosed")
	private int isClosed;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "time_from")
	private String timeFrom;

	@Column(name = "time_to")
	private String timeTo;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "status")
	private String status;

	@ManyToOne
	@JoinColumn(name = "days_id", nullable = false)
	private Day day;

		 

	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getTimeFrom()
	{
		return this.timeFrom;
	}

	public void setTimeFrom(String timeFrom)
	{
		this.timeFrom = timeFrom;
	}

	public String getTimeTo()
	{
		return this.timeTo;
	}

	public void setTimeTo(String timeTo)
	{
		this.timeTo = timeTo;
	}

	public int getIsClosed()
	{
		return isClosed;
	}

	public void setIsClosed(int isClosed)
	{
		this.isClosed = isClosed;
	}

	public Day getDay()
	{
		return day;
	}

	public void setDay(Day day)
	{
		this.day = day;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
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

	@Override
	public String toString() {
		return "BusinessHour [id=" + id + ", created=" + created + ", updated="
				+ updated + ", createdBy=" + createdBy + ", isClosed="
				+ isClosed + ", locationsId=" + locationsId + ", timeFrom="
				+ timeFrom + ", timeTo=" + timeTo + ", updatedBy=" + updatedBy
				+ ", status=" + status + ", day=" + day + "]";
	}

}