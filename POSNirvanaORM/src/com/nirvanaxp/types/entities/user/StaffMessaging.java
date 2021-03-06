/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

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
 * The persistent class for the users_to_address database table.
 * 
 */
@Entity
@Table(name = "staff_messaging")
@XmlRootElement(name = "staff_messaging")
public class StaffMessaging implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "message")
	private String message;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "schedule_date_time")
	private Date scheduleDateTime;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "locations_id")
	private String locationsId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "role_list")
	private String roleList;

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
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


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public Date getScheduleDateTime() {
		return scheduleDateTime;
	}

	public void setScheduleDateTime(Date scheduleDateTime) {
		this.scheduleDateTime = scheduleDateTime;
	}

	
	public String getRoleList() {
		return roleList;
	}

	public void setRoleList(String roleList) {
		this.roleList = roleList;
	}

	
	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	@Override
	public String toString() {
		return "StaffMessaging [id=" + id + ", message=" + message + ", created=" + created + ", scheduleDateTime="
				+ scheduleDateTime + ", createdBy=" + createdBy + ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", locationsId=" + locationsId + ", status=" + status + ", roleList=" + roleList + "]";
	}

	 

}