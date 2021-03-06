/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.reservation;

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
 * The persistent class for the request_type database table.
 * 
 */
@Entity
@Table(name = "request_type")
@XmlRootElement(name = "request_type")
public class RequestType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private Integer displaySequence;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "request_name", nullable = false, length = 32)
	private String requestName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(nullable = false, length = 1)
	private String status;

	private String description;

	public RequestType(Object object[], int index)
	{

		// 47
		if (object[index] != null)
		{
			id = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			requestName = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			displayName = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			displaySequence = (Integer) object[index];
		}
		index++;
		if (object[index] != null)
		{
			description = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			locationsId = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			status = "" + object[index];
		}
		index++;
		if (object[index] != null)
		{
			created = ((Date) object[index]);
		}
		index++;
		if (object[index] != null)
		{
			createdBy = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			updated = ((Date) object[index]);
		}
		index++;
		if (object[index] != null)
		{
			updatedBy = (String) object[index];
		}

	}

	public RequestType()
	{
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Integer getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getRequestName()
	{
		return this.requestName;
	}

	public void setRequestName(String requestName)
	{
		this.requestName = requestName;
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
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
		return "RequestType [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", displayName=" + displayName
				+ ", displaySequence=" + displaySequence + ", locationsId="
				+ locationsId + ", requestName=" + requestName + ", updated="
				+ updated + ", updatedBy=" + updatedBy + ", status=" + status
				+ ", description=" + description + "]";
	}

}