/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.payment;

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
 * The persistent class for the payment_method_type database table.
 * 
 */
@Entity
@Table(name = "payment_method_type")
@XmlRootElement(name = "payment_method_type")
public class PaymentMethodType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(length = 100)
	private String description;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private Integer displaySequence;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 20)
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "payment_type_id")
	private int paymentTypeId;

	@Column(name = "printers_id")
	private String printersId;

	@Column(nullable = false, length = 1)
	private String status;

	public PaymentMethodType()
	{
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

 

	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Integer getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public int getPaymentTypeId()
	{
		return paymentTypeId;
	}

	public void setPaymentTypeId(int paymentTypeId)
	{
		this.paymentTypeId = paymentTypeId;
	}

	public String getPrintersId()
	{
		 if(printersId != null && (printersId.length()==0 || printersId.equals("0"))){return null;}else{	return printersId;}
	}

	public void setPrintersId(String printersId)
	{
		this.printersId = printersId;
	}

	@Override
	public String toString() {
		return "PaymentMethodType [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", description=" + description
				+ ", displayName=" + displayName + ", displaySequence="
				+ displaySequence + ", locationsId=" + locationsId + ", name="
				+ name + ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", paymentTypeId=" + paymentTypeId + ", printersId="
				+ printersId + ", status=" + status + "]";
	}

}