/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.sms;

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
@Table(name = "sms_template")
@NamedQuery(name = "SMSTemplate.findAll", query = "SELECT e FROM SMSTemplate e")
public class SMSTemplate extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "template_name")
	private String templateName;

	@Column(name = "template_value")
	private String templateValue;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "template_text")
	private String templateText;

	@Column(name = "template_display_name")
	private String templateDisplayName;

	public SMSTemplate()
	{
	}

	public String getTemplateText()
	{
		return templateText;
	}

	public void setTemplateText(String templateText)
	{
		this.templateText = templateText;
	}

	public String getTemplateDisplayName()
	{
		return templateDisplayName;
	}

	public void setTemplateDisplayName(String templateDisplayName)
	{
		this.templateDisplayName = templateDisplayName;
	}

	public String getTemplateName()
	{
		return templateName;
	}

	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	public String getTemplateValue()
	{
		return templateValue;
	}

	public void setTemplateValue(String templateValue)
	{
		this.templateValue = templateValue;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	@Override
	public String toString()
	{
		return "SMSTemplate [templateName=" + templateName + ", templateValue=" + templateValue + ", locationId=" + locationId + ", templateText=" + templateText + ", templateDisplayName="
				+ templateDisplayName + "]";
	}

}