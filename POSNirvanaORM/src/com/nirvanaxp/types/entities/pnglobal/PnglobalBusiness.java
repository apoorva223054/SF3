/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.pnglobal;

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
 * The persistent class for the pnglobal_business database table.
 * 
 */
@Entity
@Table(name = "pnglobal_business")
@XmlRootElement(name = "pnglobal_business")
public class PnglobalBusiness implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "billing_address_id", nullable = false)
	private int billingAddressId;

	@Column(name = "business_name", nullable = false, length = 32)
	private String businessName;

	@Column(name = "business_type_id", nullable = false)
	private int businessTypeId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(length = 64)
	private String email;

	@Column(length = 256)
	private String logo;

	@Column(name = "schema_name", nullable = false, length = 128)
	private String schemaName;

	@Column(name = "shipping_address_id", nullable = false)
	private int shippingAddressId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(length = 256)
	private String website;

	public PnglobalBusiness()
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

	public int getBillingAddressId()
	{
		return this.billingAddressId;
	}

	public void setBillingAddressId(int billingAddressId)
	{
		this.billingAddressId = billingAddressId;
	}

	public String getBusinessName()
	{
		return this.businessName;
	}

	public void setBusinessName(String businessName)
	{
		this.businessName = businessName;
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

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getLogo()
	{
		return this.logo;
	}

	public void setLogo(String logo)
	{
		this.logo = logo;
	}

	public String getSchemaName()
	{
		return this.schemaName;
	}

	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}

	public int getShippingAddressId()
	{
		return this.shippingAddressId;
	}

	public void setShippingAddressId(int shippingAddressId)
	{
		this.shippingAddressId = shippingAddressId;
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



	public String getWebsite()
	{
		return this.website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
	}

	@Override
	public String toString() {
		return "PnglobalBusiness [id=" + id + ", billingAddressId="
				+ billingAddressId + ", businessName=" + businessName
				+ ", businessTypeId=" + businessTypeId + ", created=" + created
				+ ", createdBy=" + createdBy + ", email=" + email + ", logo="
				+ logo + ", schemaName=" + schemaName + ", shippingAddressId="
				+ shippingAddressId + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", website=" + website + "]";
	}

}