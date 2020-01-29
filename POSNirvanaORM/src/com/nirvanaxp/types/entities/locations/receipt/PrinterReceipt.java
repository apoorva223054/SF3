/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations.receipt;

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

@Entity
@Table(name = "printer_receipt")
@XmlRootElement(name = "printer_receipt")
public class PrinterReceipt implements Serializable
{

	@Override
	public String toString() {
		return "PrinterReceipt [id=" + id + ", status=" + status + ", created="
				+ created + ", createdBy=" + createdBy + ", name=" + name
				+ ", displayName=" + displayName + ", value=" + value
				+ ", alignment=" + alignment + ", position=" + position
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", displaySequence=" + displaySequence + ", locationId="
				+ locationId + "]";
	}

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

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	@Column(name = "value")
	private String value;

	@Column(name = "alignment", nullable = false)
	private String alignment;

	@Column(name = "position", nullable = false)
	private String position;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "display_sequence")
	private int displaySequence;

	@Column(name = "location_id")
	private String locationId;

	

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

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getAlignment()
	{
		return alignment;
	}

	public void setAlignment(String alignment)
	{
		this.alignment = alignment;
	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
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

	public int getDisplaySequence()
	{
		return displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
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

}
