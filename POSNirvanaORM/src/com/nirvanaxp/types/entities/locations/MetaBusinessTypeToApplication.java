/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the meta_business_type_to_applications database
 * table.
 * 
 */
@Entity
@Table(name = "meta_business_type_to_applications")
@NamedQuery(name = "MetaBusinessTypeToApplication.findAll", query = "SELECT m FROM MetaBusinessTypeToApplication m")
public class MetaBusinessTypeToApplication implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name = "application_name")
	private String applicationName;

	@Column(name = "applications_id")
	private int applicationsId;

	@Column(name = "business_type_id")
	private int businessTypeId;

	private Timestamp created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_name")
	private String displayName;

	private String status;

	private Timestamp updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "image_url")
	private String imageUrl;

	public MetaBusinessTypeToApplication()
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

	public String getApplicationName()
	{
		return this.applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public int getApplicationsId()
	{
		return this.applicationsId;
	}

	public void setApplicationsId(int applicationsId)
	{
		this.applicationsId = applicationsId;
	}

	public int getBusinessTypeId()
	{
		return this.businessTypeId;
	}

	public void setBusinessTypeId(int businessTypeId)
	{
		this.businessTypeId = businessTypeId;
	}

	public Timestamp getCreated()
	{
		return this.created;
	}

	public void setCreated(Timestamp created)
	{
		this.created = created;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Timestamp getUpdated()
	{
		return this.updated;
	}

	public void setUpdated(Timestamp updated)
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

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "MetaBusinessTypeToApplication [id=" + id + ", applicationName="
				+ applicationName + ", applicationsId=" + applicationsId
				+ ", businessTypeId=" + businessTypeId + ", created=" + created
				+ ", createdBy=" + createdBy + ", displayName=" + displayName
				+ ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", imageUrl=" + imageUrl + "]";
	}

}