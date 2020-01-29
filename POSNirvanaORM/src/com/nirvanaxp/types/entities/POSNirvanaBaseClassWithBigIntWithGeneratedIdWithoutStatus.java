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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class POSNirvanaBaseClassWithBigIntWithGeneratedIdWithoutStatus implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -272304085097200736L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	protected BigInteger id;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;

	@Column(name = "updated_by", nullable = false)
	protected String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	@Column(name = "created_by")
	protected String createdBy;

	public BigInteger getId()
	{
		return id;
	}

	public void setId(BigInteger id)
	{
		this.id = id;
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

	@Override
	public String toString() {
		return "POSNirvanaBaseClassWithBigIntWithGeneratedIdWithoutStatus [id="
				+ id + ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", created=" + created + ", createdBy=" + createdBy + "]";
	}



	 
	
}
