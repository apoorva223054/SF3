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
 * The persistent class for the printers_interface database table.
 * 
 */
@Entity
@Table(name = "printers_interface")
@XmlRootElement(name = "printers_interface")
public class PrintersInterface implements Serializable
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


	@Column(name = "global_printers_interface_id")
	private String globalPrintersInterfaceId;
	
	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	public PrintersInterface()
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

 
	
	public String getGlobalPrintersInterfaceId() {
		 if(globalPrintersInterfaceId != null && (globalPrintersInterfaceId.length()==0 || globalPrintersInterfaceId.equals("0"))){return null;}else{	return globalPrintersInterfaceId;}
	}
	public void setGlobalPrintersInterfaceId(String globalPrintersInterfaceId) {
		this.globalPrintersInterfaceId = globalPrintersInterfaceId;
	}
	public PrintersInterface getPrinterInterfaceObject(PrintersInterface p){
		PrintersInterface printer = new PrintersInterface();
		printer.setCreated(p.getCreated());
		printer.setCreatedBy(p.getCreatedBy());
		printer.setLocationsId(p.getLocationsId());
		printer.setStatus(p.getStatus());
		printer.setUpdated(p.getUpdated());
		printer.setUpdatedBy(p.getUpdatedBy());
		printer.setName(p.getName());
		return printer;
	}

	@Override
	public String toString()
	{
		return "PrintersInterface [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", locationsId=" + locationsId + ", name=" + name + ", status=" + status + ", updated=" + updated
				+ ", globalPrintersInterfaceId=" + globalPrintersInterfaceId + ", updatedBy=" + updatedBy + "]";
	}

	 
}