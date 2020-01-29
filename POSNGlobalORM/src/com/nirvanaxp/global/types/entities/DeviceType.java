/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

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

@Entity
@Table(name = "device_type")
@XmlRootElement(name = "device_type")
public class DeviceType
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "name")
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_name")
	private String displayName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "is_encryptionkey_required")
	private int isEncryptionkeyRequired;
	
	public DeviceType()
	{
		
	}

	public DeviceType(int id)
	{
		super();
		this.id = id;
	}

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created)
	{
		if (created != 0)
		{
			this.created = new Date(created);
		}

	}

	public long getUpdated()
	{
		if (this.updated != null)
		{
			return this.updated.getTime();
		}
		return 0;
	}
	
	public void setUpdated(long updated)
	{
		if (updated != 0)
		{
			this.updated = new Date(updated);
		}
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
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

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public boolean equals(Object otherObj)
	{
		if (otherObj != null && otherObj instanceof DeviceType)
		{
			DeviceType deviceTypeToCompare = (DeviceType) otherObj;
			if (id == deviceTypeToCompare.getId())
			{
				return true;

			}
		}
		return false;
	}

	public int getIsEncryptionkeyRequired()
	{
		return isEncryptionkeyRequired;
	}

	public void setIsEncryptionkeyRequired(int isEncryptionkeyRequired)
	{
		this.isEncryptionkeyRequired = isEncryptionkeyRequired;
	}

	@Override
	public String toString() {
		return "DeviceType [id=" + id + ", name=" + name + ", created="
				+ created + ", createdBy=" + createdBy + ", displayName="
				+ displayName + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", isEncryptionkeyRequired="
				+ isEncryptionkeyRequired + "]";
	}
	
}
