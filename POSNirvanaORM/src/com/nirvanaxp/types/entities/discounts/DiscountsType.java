/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.discounts;

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
 * The persistent class for the discounts_type database table.
 * 
 */
@Entity
@Table(name = "discounts_type")
@XmlRootElement(name = "discounts_type")
public class DiscountsType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "discounts_type", nullable = false, length = 64)
	private String discountsType;

	@Column(name = "display_name", nullable = false, length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private int displaySequence;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 1)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;
	
	@Column(name = "global_discount_type_id", nullable = false)
	private String globalDiscountTypeId;
	
	

	public DiscountsType()
	{
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



	public String getDiscountsType()
	{
		return this.discountsType;
	}

	public void setDiscountsType(String discountsType)
	{
		this.discountsType = discountsType;
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

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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

	public String getGlobalDiscountTypeId()
	{
		 if(globalDiscountTypeId != null && (globalDiscountTypeId.length()==0 || globalDiscountTypeId.equals("0"))){return null;}else{	return globalDiscountTypeId;}
	}

	public void setGlobalDiscountTypeId(String globalDiscountTypeId)
	{
		this.globalDiscountTypeId = globalDiscountTypeId;
	}
	
	public DiscountsType getDiscountTypeObject(DiscountsType discountsTypeTemp){
		DiscountsType discountsType = new DiscountsType();
		discountsType.setCreated(discountsTypeTemp.getCreated());
		discountsType.setCreatedBy(discountsTypeTemp.getCreatedBy());
		discountsType.setLocationsId(discountsTypeTemp.getLocationsId());
		discountsType.setStatus(discountsTypeTemp.getStatus());
		discountsType.setUpdated(discountsTypeTemp.getUpdated());
		discountsType.setUpdatedBy(discountsTypeTemp.getUpdatedBy());
		discountsType.setDiscountsType(discountsTypeTemp.getDiscountsType());
		discountsType.setDisplayName(discountsTypeTemp.getDisplayName());
		discountsType.setDisplaySequence(discountsTypeTemp.getDisplaySequence());
		return discountsType;
	}
	

	@Override
	public String toString()
	{
		return "DiscountsType [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", discountsType=" + discountsType + ", displayName=" + displayName + ", displaySequence="
				+ displaySequence + ", locationsId=" + locationsId + ", status=" + status + ", updated=" + updated + ", updatedBy=" + updatedBy + ", globalDiscountTypeId=" + globalDiscountTypeId
				+ "]";
	}

	

}