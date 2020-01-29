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
 * The persistent class for the printers_type database table.
 * 
 */
@Entity
@Table(name = "printers_type")
@XmlRootElement(name = "printers_type")
public class PrintersType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 256)
	private String name;

	@Column(nullable = false, length = 1)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;
	
	@Column(name = "global_printers_type_id")
	private String globalPrintersTypeId;

	public PrintersType()
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

	 

	public String getGlobalPrintersTypeId() {
		 if(globalPrintersTypeId != null && (globalPrintersTypeId.length()==0 || globalPrintersTypeId.equals("0"))){return null;}else{	return globalPrintersTypeId;}
	}



	public void setGlobalPrintersTypeId(String globalPrintersTypeId) {
		this.globalPrintersTypeId = globalPrintersTypeId;
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

	public PrintersType getPrinterTypeObject(PrintersType printerTypeTemp){
		PrintersType printerType = new PrintersType();
		printerType.setCreated(printerTypeTemp.getCreated());
		printerType.setCreatedBy(printerTypeTemp.getCreatedBy());
		printerType.setLocationsId(printerTypeTemp.getLocationsId());
		printerType.setStatus(printerTypeTemp.getStatus());
		printerType.setUpdated(printerTypeTemp.getUpdated());
		printerType.setUpdatedBy(printerTypeTemp.getUpdatedBy());
		printerType.setName(printerTypeTemp.getName());
		return printerType;
	}
	
	@Override
	public String toString()
	{
		return "PrintersType [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", locationsId=" + locationsId + ", name=" + name + ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", globalPrintersTypeId=" + globalPrintersTypeId + "]";
	}

	
	

}