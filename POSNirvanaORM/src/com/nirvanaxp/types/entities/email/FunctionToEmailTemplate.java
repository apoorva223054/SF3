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
 * The persistent class for the function_to_email_template database table.
 * 
 */
@Entity
@Table(name = "function_to_email_template")
@NamedQuery(name = "FunctionToEmailTemplate.findAll", query = "SELECT b FROM FunctionToEmailTemplate b")
public class FunctionToEmailTemplate extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "function_id")
	private int functionId;
	
	
	public int getFunctionId()
	{
		return functionId;
	}

	public void setFunctionId(int functionId)
	{
		this.functionId = functionId;
	}

	@Column(name = "email_template_id")
	private int emailTemplateId;

	@Column(name = "location_id")
	private String locationId;

	public FunctionToEmailTemplate()
	{
	}

	public int getEmailTemplateId()
	{
		return this.emailTemplateId;
	}

	public void setEmailTemplateId(int emailTemplateId)
	{
		this.emailTemplateId = emailTemplateId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	@Override
	public String toString()
	{
		return "FunctionToEmailTemplate [functionId=" + functionId + ", emailTemplateId=" + emailTemplateId + ", locationId=" + locationId + "]";
	}

	
	
}