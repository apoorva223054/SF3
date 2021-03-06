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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the business_type database table.
 * 
 */
@Entity
@Table(name="business_type")
@XmlRootElement(name = "business_type")
public class BusinessType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name="created_by")
	private String createdBy;

	@Column(name="hex_code_values")
	private String hexCodeValues;

	@Column(name="image_name")
	private String imageName;

	private String name;

	@Column(name="short_name")
	private String shortName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name="updated_by")
	private String updatedBy;

	public BusinessType()
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
			this.created =  new Date(created);
		}
		
	}


	public String getHexCodeValues()
	{
		return this.hexCodeValues;
	}

	public void setHexCodeValues(String hexCodeValues)
	{
		this.hexCodeValues = hexCodeValues;
	}

	public String getImageName()
	{
		return this.imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getShortName()
	{
		return this.shortName;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
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
		return "BusinessType [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", hexCodeValues="
				+ hexCodeValues + ", imageName=" + imageName + ", name=" + name
				+ ", shortName=" + shortName + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + "]";
	}
	

}