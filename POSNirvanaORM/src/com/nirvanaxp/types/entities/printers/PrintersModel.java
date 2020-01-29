/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.printers;

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
 * The persistent class for the printers_model database table.
 * 
 */
@Entity
@Table(name = "printers_model")
@XmlRootElement(name = "printers_model")
public class PrintersModel implements Serializable
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

	@Column(name = "display_name", length = 128)
	private String displayName;

	@Column(name = "display_sequence")
	private int displaySequence;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "model_number", nullable = false, length = 128)
	private String modelNumber;

	@Column(name = "printers_interface", nullable = false, length = 128)
	private String printersInterface;

	@Column(name = "printers_manufacturer", length = 128)
	private String printersManufacturer;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;
	
	@Column(name = "global_printers_model_id", nullable = false)
	private int globalPrintersModelId;

	@Column(nullable = false, length = 1)
	private String status;

	public PrintersModel()
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
			this.created = new Date(created);
		}

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

	public String getModelNumber()
	{
		return this.modelNumber;
	}

	public void setModelNumber(String modelNumber)
	{
		this.modelNumber = modelNumber;
	}

	public String getPrintersInterface()
	{
		return this.printersInterface;
	}

	public void setPrintersInterface(String printersInterface)
	{
		this.printersInterface = printersInterface;
	}

	public String getPrintersManufacturer()
	{
		return this.printersManufacturer;
	}

	public void setPrintersManufacturer(String printersManufacturer)
	{
		this.printersManufacturer = printersManufacturer;
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

	public int getGlobalPrintersModelId()
	{
		return globalPrintersModelId;
	}

	public void setGlobalPrintersModelId(int globalPrintersModelId)
	{
		this.globalPrintersModelId = globalPrintersModelId;
	}

	@Override
	public String toString()
	{
		return "PrintersModel [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", displayName=" + displayName + ", displaySequence=" + displaySequence + ", locationsId="
				+ locationsId + ", modelNumber=" + modelNumber + ", printersInterface=" + printersInterface + ", printersManufacturer=" + printersManufacturer + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", globalPrintersModelId=" + globalPrintersModelId + ", status=" + status + "]";
	}

	 
}