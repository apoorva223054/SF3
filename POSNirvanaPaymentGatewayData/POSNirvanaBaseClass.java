/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class POSNirvanaBaseClass implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -272304085097200736L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	protected int id;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;

	@Column(name = "updated_by", nullable = false)
	protected int updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	@Column(name = "created_by")
	protected int createdBy;

	@Column(nullable = false, length = 1)
	protected String status;

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getUpdatedBy()
	{
		return this.updatedBy;
	}

	public void setUpdatedBy(int updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public Date getCreated()
	{
		return created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public int getCreatedBy()
	{
		return this.createdBy;
	}

	public void setCreatedBy(int createdBy)
	{
		this.createdBy = createdBy;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	@Override
	public String toString() {
		return "POSNirvanaBaseClass [id=" + id + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", created=" + created
				+ ", createdBy=" + createdBy + ", status=" + status + "]";
	}

	
}
