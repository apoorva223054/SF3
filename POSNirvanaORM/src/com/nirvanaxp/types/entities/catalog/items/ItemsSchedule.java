/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the reservation_schedule database table.
 * 
 */
@Entity
@Table(name = "items_schedule")
@XmlRootElement(name = "items_schedule")
public class ItemsSchedule implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "from_date", nullable = false, length = 30)
	private String fromDate;

	@Column(name = "from_time", nullable = false, length = 30)
	private String fromTime;

	@Column(name = "location_id", nullable = false)
	private String locationId;
	
	@Column(name = "priority", nullable = false)
	private int priority;
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Column(name = "schedule_name", nullable = false, length = 250)
	private String scheduleName;

	@Column(nullable = false, length = 1)
	private String status;

	@Column(name = "to_date", nullable = false, length = 30)
	private String toDate;

	@Column(name = "to_time", nullable = false, length = 30)
	private String toTime;


	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "discount_id", nullable = false)
	private String discountId;

	// uni-directional many-to-many association to Discount
	@JoinColumn(name = "items_schedule_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<ItemsScheduleDay> itemsScheduleDays;
	
	/*@JoinColumn(name = "schedule_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<ItemsToSchedule> itemsToSchedule;
		

	public Set<ItemsToSchedule> getItemsToSchedule() {
		return itemsToSchedule;
	}

	public void setItemsToSchedule(Set<ItemsToSchedule> itemsToSchedule) {
		this.itemsToSchedule = itemsToSchedule;
	}
*/
	public Set<ItemsScheduleDay> getItemsScheduleDays() {
		return itemsScheduleDays;
	}

	public void setItemsScheduleDays(Set<ItemsScheduleDay> itemsScheduleDays) {
		this.itemsScheduleDays = itemsScheduleDays;
	}

	public String getDiscountId() {
		 if(discountId != null && (discountId.length()==0 || discountId.equals("0"))){return null;}else{	return discountId;}
	}

	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}

	 

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

	public String getFromDate()
	{
		return this.fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getFromTime()
	{
		return this.fromTime;
	}

	public void setFromTime(String fromTime)
	{
		this.fromTime = fromTime;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getToDate()
	{
		return this.toDate;
	}

	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	public String getToTime()
	{
		return this.toTime;
	}

	public void setToTime(String toTime)
	{
		this.toTime = toTime;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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

	@Override
	public String toString() {
		return "ItemsSchedule [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", fromDate=" + fromDate
				+ ", fromTime=" + fromTime + ", locationId=" + locationId
				+ ", priority=" + priority + ", scheduleName=" + scheduleName
				+ ", status=" + status + ", toDate=" + toDate + ", toTime="
				+ toTime + ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", discountId=" + discountId + ", itemsScheduleDays="
				+ itemsScheduleDays + "]";
	}

	

	
	
	

	
}