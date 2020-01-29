/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the field_type database table.
 * 
 */
@Entity
@Table(name = "field_type")
@XmlRootElement(name = "field_type")
public class FieldType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "created")
	private Timestamp created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "field_type_name", nullable = false, length = 70)
	private String fieldTypeName;
	
	@Column(name = "field_display_name")
	private String fieldDisplayName;

	@Column(name = "updated")
	private Timestamp updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	public FieldType()
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
			this.created = new Timestamp(created);
		}
	}

	public String getFieldTypeName()
	{
		return this.fieldTypeName;
	}

	public void setFieldTypeName(String fieldTypeName)
	{
		this.fieldTypeName = fieldTypeName;
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
			this.updated = new Timestamp(updated);
		}
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

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

	public String getFieldDisplayName() {
		return fieldDisplayName;
	}

	public void setFieldDisplayName(String fieldDisplayName) {
		this.fieldDisplayName = fieldDisplayName;
	}

	@Override
	public String toString() {
		return "FieldType [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", fieldTypeName="
				+ fieldTypeName + ", fieldDisplayName=" + fieldDisplayName + ", updated=" + updated + ", updatedBy="
				+ updatedBy + "]";
	}

}