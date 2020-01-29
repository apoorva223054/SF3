/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.email;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the email_template database table.
 * 
 */
@Entity
@Table(name = "email_template")
@NamedQuery(name = "EmailTemplate.findAll", query = "SELECT e FROM EmailTemplate e")
public class EmailTemplate extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "email_body")
	private String emailBody;

	@Column(name = "email_subject")
	private String emailSubject;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "operation_name")
	private String operationName;
	
	@Column(name = "display_name")
	private String displayName;

	public EmailTemplate()
	{
	}

	public String getEmailBody()
	{
		return this.emailBody;
	}

	public void setEmailBody(String emailBody)
	{
		this.emailBody = emailBody;
	}

	public String getEmailSubject()
	{
		return this.emailSubject;
	}

	public void setEmailSubject(String emailSubject)
	{
		this.emailSubject = emailSubject;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public String getOperationName()
	{
		return this.operationName;
	}

	public void setOperationName(String operationName)
	{
		this.operationName = operationName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "EmailTemplate [emailBody=" + emailBody + ", emailSubject=" + emailSubject + ", locationId=" + locationId
				+ ", operationName=" + operationName + ", displayName=" + displayName + "]";
	}

}