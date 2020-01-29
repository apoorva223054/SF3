/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.reasons;

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
 * Entity implementation class for Entity: Reasons
 * 
 */
@Entity
@Table(name = "reasons")
@XmlRootElement(name = "reasons")
public class Reasons implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(name = "status")
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(name = "display_name", nullable = false, length = 200)
	private String displayName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "reason_type_id", nullable = false)
	private String reasonTypeId;

	@Column(name = "locations_id")
	private String locationsId;
	
	@Column(name = "display_sequence")
	private int displaySequence;

	public Reasons()
	{
		super();
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

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
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

	public String getReasonTypeId()
	{
		 if(reasonTypeId != null && (reasonTypeId.length()==0 || reasonTypeId.equals("0"))){return null;}else{	return reasonTypeId;}
	}

	public void setReasonTypeId(String reasonTypeId)
	{
		this.reasonTypeId = reasonTypeId;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public int getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}

	@Column(name = "inventory_consumed")
	 private String inventoryConsumed;

	public String getInventoryConsumed() {
		return inventoryConsumed;
	}

	public void setInventoryConsumed(String inventoryConsumed) {
		this.inventoryConsumed = inventoryConsumed;
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

	@Override
	public String toString() {
		return "Reasons [id=" + id + ", status=" + status + ", created="
				+ created + ", createdBy=" + createdBy + ", name=" + name
				+ ", displayName=" + displayName + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", reasonTypeId=" + reasonTypeId
				+ ", locationsId=" + locationsId + ", displaySequence="
				+ displaySequence + ", inventoryConsumed=" + inventoryConsumed
				+ "]";
	}
	
	

}
