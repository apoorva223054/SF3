/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the items_schedule_days database table.
 * 
 */
@Entity
@Table(name = "items_schedule_days")
@XmlRootElement(name = "items_schedule_days")
public class ItemsScheduleDay implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "days_id", nullable = false)
	private int daysId;

	@Column(name = "status")
	private String status;

	@Column(name = "items_schedule_id", nullable = false)
	private String itemsScheduleId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "location_id", nullable = false)
	private String locationId;
	
	public ItemsScheduleDay()
	{
	}

	public int getId() {
			return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public int getDaysId()
	{
		return this.daysId;
	}

	public void setDaysId(int daysId)
	{
		this.daysId = daysId;
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

	public String getItemsScheduleId() {
		return itemsScheduleId;
	}

	public void setItemsScheduleId(String itemsScheduleId) {
		this.itemsScheduleId = itemsScheduleId;
	}
	
	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "ItemsScheduleDay [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", daysId=" + daysId
				+ ", status=" + status + ", itemsScheduleId=" + itemsScheduleId
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", locationId=" + locationId + "]";
	}

	

}