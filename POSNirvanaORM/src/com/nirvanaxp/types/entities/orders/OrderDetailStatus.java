/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

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
 * The persistent class for the order_detail_status database table.
 * 
 */
@Entity
@Table(name = "order_detail_status")
@XmlRootElement(name = "order_detail_status")
public class OrderDetailStatus implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(length = 256)
	private String description;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private Integer displaySequence;

	@Column(name = "is_server_driven")
	private int isServerDriven;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(nullable = false, length = 32)
	private String name;

	@Column(name = "order_source_group_id")
	private String orderSourceGroupId;

	@Column(name = "status_colour", length = 8)
	private String statusColour;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(nullable = false, length = 1)
	private String status;

	public OrderDetailStatus()
	{
	}

	public OrderDetailStatus(String name)
	{
		super();
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

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
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

	public int getIsServerDriven()
	{
		return this.isServerDriven;
	}

	public void setIsServerDriven(int isServerDriven)
	{
		this.isServerDriven = isServerDriven;
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

	public String getOrderSourceGroupId()
	{
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}

	public void setOrderSourceGroupId(String orderSourceId)
	{
		this.orderSourceGroupId = orderSourceId;
	}

	public String getStatusColour()
	{
		return this.statusColour;
	}

	public void setStatusColour(String statusColour)
	{
		this.statusColour = statusColour;
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
	public boolean equals(Object obj)
	{
		// we do this as we need to check if printer relation exists or not
		if (obj instanceof OrderDetailStatus)
		{

			if (this.name.equals(((OrderDetailStatus) obj).getName()))
			{
				return true;
			}

		}
		return false;
	}

	@Override
	public String toString() {
		return "OrderDetailStatus [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", description=" + description
				+ ", displayName=" + displayName + ", displaySequence="
				+ displaySequence + ", isServerDriven=" + isServerDriven
				+ ", locationsId=" + locationsId + ", name=" + name
				+ ", orderSourceGroupId=" + orderSourceGroupId
				+ ", statusColour=" + statusColour + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", status=" + status + "]";
	}

	
}