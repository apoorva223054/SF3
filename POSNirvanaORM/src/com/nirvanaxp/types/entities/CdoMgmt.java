/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities;

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
 * The persistent class for the cdo_mgmt database table.
 * 
 */
@Entity
@Table(name = "cdo_mgmt")
@XmlRootElement(name = "cdo_mgmt")
public class CdoMgmt implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "cdo_description", nullable = false, length = 256)
	private String cdoDescription;

	@Column(name = "cdo_name", nullable = false, length = 64)
	private String cdoName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "version_number", nullable = false)
	private BigInteger versionNumber;
	
	@Column(name = "is_location_specific", nullable = false)
	private int isLocationSpecific;

	public CdoMgmt()
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

	public String getCdoDescription()
	{
		return this.cdoDescription;
	}

	public void setCdoDescription(String cdoDescription)
	{
		this.cdoDescription = cdoDescription;
	}

	public String getCdoName()
	{
		return this.cdoName;
	}

	public void setCdoName(String cdoName)
	{
		this.cdoName = cdoName;
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

	public BigInteger getVersionNumber()
	{
		return this.versionNumber;
	}

	public void setVersionNumber(BigInteger versionNumber)
	{
		this.versionNumber = versionNumber;
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

	public int getIsLocationSpecific()
	{
		return isLocationSpecific;
	}

	public void setIsLocationSpecific(int isLocationSpecific)
	{
		this.isLocationSpecific = isLocationSpecific;
	}

	@Override
	public String toString()
	{
		return "CdoMgmt [id=" + id + ", cdoDescription=" + cdoDescription + ", cdoName=" + cdoName + ", created=" + created + ", createdBy=" + createdBy + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", versionNumber=" + versionNumber + ", isLocationSpecific=" + isLocationSpecific + "]";
	}

	 

}