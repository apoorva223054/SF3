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
 * The persistent class for the reservations_status database table.
 * 
 */
@Entity
@Table(name = "reservations_status")
@XmlRootElement(name = "reservations_status")
public class ReservationsStatus implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private Integer displaySequence;

	@Column(name = "hex_code_values", length = 60)
	private String hexCodeValues;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 32)
	private String name;

	@Column(name = "show_to_customer", nullable = false)
	private int showToCustomer;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(nullable = false, length = 1)
	private String status;

	@Column(name = "is_server_driven")
	private int isServerDriven;

	private String description;

	@Column(name = "is_send_sms")
	private int isSendSms;
	
	@Column(name = "template_id")
	private int templateId;
 

	

	public int getIsSendSms() {
		return isSendSms;
	}

	public void setIsSendSms(int isSendSms) {
		this.isSendSms = isSendSms;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	
	public ReservationsStatus(Object object[], int index)
	{

		// 24
		if (object[index] != null)
		{
			id = (String) object[index];
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
			description = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			showToCustomer = ((Byte) object[index]).intValue();
		}
		index++;

		if (object[index] != null)
		{
			locationsId = (String) object[index];
		}
		index++;
		if (object[index] != null)
		{
			hexCodeValues = (String) object[index];
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

	public ReservationsStatus()
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

	public String getHexCodeValues()
	{
		return this.hexCodeValues;
	}

	public void setHexCodeValues(String hexCodeValues)
	{
		this.hexCodeValues = hexCodeValues;
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

	public int getShowToCustomer()
	{
		return this.showToCustomer;
	}

	public void setShowToCustomer(int showToCustomer)
	{
		this.showToCustomer = showToCustomer;
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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getIsServerDriven()
	{
		return isServerDriven;
	}

	public void setIsServerDriven(int isServerDriven)
	{
		this.isServerDriven = isServerDriven;
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

	
}