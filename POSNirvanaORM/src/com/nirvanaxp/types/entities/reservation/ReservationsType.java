/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.reservation;

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
 * The persistent class for the reservations_types database table.
 * 
 */
@Entity
@Table(name = "reservations_types")
@XmlRootElement(name = "reservations_types")
public class ReservationsType implements Serializable
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

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private int displaySequence;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 56)
	private String name;

	@Column(nullable = false, length = 1)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	public ReservationsType(Object object[], int index)
	{
		// 37
		if (object[index] != null)
		{
			id = (Integer) object[index];
		}
		index++;
		if (object[index] != null)
		{
			name = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			displayName = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			displaySequence = (Integer) object[index];
		}
		index++;

		if (object[index] != null)
		{
			locationsId = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			status = "" + object[index];
		}
		index++;

		if (object[index] != null)
		{
			created = (Date) object[index];
		}
		index++;

		if (object[index] != null)
		{
			createdBy = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			updated = (Date) object[index];
		}
		index++;
		if (object[index] != null)
		{
			updatedBy = (String) object[index];
		}
	}

	public ReservationsType()
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

	public ReservationsType(int id)
	{
		super();
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

	public boolean equals(ReservationsType reservationsType)
	{
		if (reservationsType instanceof ReservationsType && ((ReservationsType) reservationsType).getId() == this.id)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString() {
		return "ReservationsType [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", displayName=" + displayName
				+ ", displaySequence=" + displaySequence + ", locationsId="
				+ locationsId + ", name=" + name + ", status=" + status
				+ ", updated=" + updated + ", updatedBy=" + updatedBy + "]";
	}

}