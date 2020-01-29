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
 * The persistent class for the template_to_business_type database table.
 * 
 */
@Entity
@Table(name = "template_to_business_type")
@XmlRootElement(name = "template_to_business_type")
public class TemplateToBusinessType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "business_type_id")
	private int businessTypeId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "template_id")
	private int templateId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	public TemplateToBusinessType()
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

	public int getBusinessTypeId()
	{
		return this.businessTypeId;
	}

	public void setBusinessTypeId(int businessTypeId)
	{
		this.businessTypeId = businessTypeId;
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

	public int getTemplateId()
	{
		return this.templateId;
	}

	public void setTemplateId(int templateId)
	{
		this.templateId = templateId;
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

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "TemplateToBusinessType [id=" + id + ", businessTypeId="
				+ businessTypeId + ", created=" + created + ", createdBy="
				+ createdBy + ", templateId=" + templateId + ", updated="
				+ updated + ", updatedBy=" + updatedBy + "]";
	}

}