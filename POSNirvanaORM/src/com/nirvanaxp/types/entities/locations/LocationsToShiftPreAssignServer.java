/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations;

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
 * The persistent class for the locations_to_functions database table.
 * 
 */
@Entity
@Table(name = "locations_to_shift_pre_assign_server")
@XmlRootElement(name = "locations_to_shift_pre_assign_server")
public class LocationsToShiftPreAssignServer implements Serializable
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

	@Column(name = "shift_id", nullable = false)
	private String shiftId;

	@Column(name = "server_name")
	private String serverName;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "is_auto_unassigned")
	private boolean isAutoUnassigned;
	
	public LocationsToShiftPreAssignServer()
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

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
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

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getShiftId()
	{
		 if(shiftId != null && (shiftId.length()==0 || shiftId.equals("0"))){return null;}else{	return shiftId;}
	}

	public void setShiftId(String shiftId)
	{
		this.shiftId = shiftId;
	}



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}

	public boolean isAutoUnassigned()
	{
		return isAutoUnassigned;
	}

	public void setAutoUnassigned(boolean isAutoUnassigned)
	{
		this.isAutoUnassigned = isAutoUnassigned;
	}

	@Override
	public String toString()
	{
		return "LocationsToShiftPreAssignServer [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", shiftId=" + shiftId + ", serverName=" + serverName + ", userId=" + userId
				+ ", locationsId=" + locationsId + ", updated=" + updated + ", updatedBy=" + updatedBy + ", status=" + status + ", localTime=" + localTime + ", isAutoUnassigned=" + isAutoUnassigned
				+ "]";
	}

	
	

}