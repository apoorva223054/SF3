/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.math.BigInteger;
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
	private static final long serialVersionUID = 7146943228178598146L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	protected int id;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;

	@Column(name = "updated_by", nullable = false)
	protected String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	@Column(name = "created_by")
	protected String createdBy;

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


	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
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

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	@Override
	public String toString() {
		return "POSNirvanaBaseClass [id=" + id + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", created=" + created
				+ ", createdBy=" + createdBy + ", status=" + status + "]";
	}

}
