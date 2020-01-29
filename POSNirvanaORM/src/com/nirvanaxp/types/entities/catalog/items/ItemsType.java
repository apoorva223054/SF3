/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the items_type database table.
 * 
 */
@Entity
@Table(name = "items_type")
@NamedQuery(name = "ItemsType.findAll", query = "SELECT i FROM ItemsType i")
public class ItemsType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private Timestamp created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "display_sequence")
	private int displaySequence;

	private String name;

	private String status;

	private Timestamp updated;

	@Column(name = "updated_by")
	private String updatedBy;

	public ItemsType()
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

	public int getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	@Override
	public String toString() {
		return "ItemsType [id=" + id + ", created=" + created + ", createdBy="
				+ createdBy + ", displayName=" + displayName
				+ ", displaySequence=" + displaySequence + ", name=" + name
				+ ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + "]";
	}

}