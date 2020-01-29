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
 * The persistent class for the payment_method database table.
 * 
 */
@Entity
@Table(name = "payment_method")
@XmlRootElement(name = "payment_method")
public class PaymentMethod implements Serializable
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

	@Column(name = "display_sequence", nullable = false)
	private Integer displaySequence;

	@Column(name = "is_active")
	private int isActive;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 20)
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	// uni-directional many-to-one association to PaymentMethodType
	@Column(name = "payment_method_type_id", nullable = false)
	private String paymentMethodTypeId;
	
	private transient String paymentMethodTypeName;

	@Column(nullable = false, length = 1)
	private String status;

	public PaymentMethod()
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
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
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

	public String getPaymentMethodTypeId()
	{
		return paymentMethodTypeId;
	}

	public void setPaymentMethodTypeId(String paymentMethodTypeId)
	{
		this.paymentMethodTypeId = paymentMethodTypeId;
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

	public String getPaymentMethodTypeName() {
		return paymentMethodTypeName;
	}

	public void setPaymentMethodTypeName(String paymentMethodTypeName) {
		this.paymentMethodTypeName = paymentMethodTypeName;
	}

	@Override
	public String toString() {
		return "PaymentMethod [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", description="
				+ description + ", displayName=" + displayName + ", displaySequence=" + displaySequence + ", isActive="
				+ isActive + ", locationsId=" + locationsId + ", name=" + name + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", paymentMethodTypeId=" + paymentMethodTypeId + ", paymentMethodTypeName="
				+ paymentMethodTypeName + ", status=" + status + "]";
	}
 
}