/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.email;

import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the email_history database table.
 * 
 */
@Entity
@Table(name = "email_history")
@NamedQuery(name = "EmailHistory.findAll", query = "SELECT e FROM EmailHistory e")
public class EmailHistory extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "email_body")
	private String emailBody;

	@Column(name = "email_subject")
	private String emailSubject;

	@Column(name = "email_template_id")
	private int emailTemplateId;

	@Column(name = "from_email")
	private String fromEmail;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "to_email")
	private String toEmail;
	
	@Column(name = "cc_email")
	private String ccEmail;
	
	@Column(name = "reference_id")
	private String referenceId;
	
	transient private File file;
	

	
	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}


	public String getReferenceId() {
		if(referenceId != null && (referenceId.length()==0 || referenceId.equals("0"))){return null;}else{	return referenceId;}
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public EmailHistory()
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

	public int getEmailTemplateId()
	{
		return this.emailTemplateId;
	}

	public void setEmailTemplateId(int emailTemplateId)
	{
		this.emailTemplateId = emailTemplateId;
	}

	public String getFromEmail()
	{
		return this.fromEmail;
	}

	public void setFromEmail(String fromEmail)
	{
		this.fromEmail = fromEmail;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public void setToEmail(String toEmail)
	{
		this.toEmail = toEmail;
	}

	public String getToEmail()
	{
		return toEmail;
	}

	public String getEmailSubject()
	{
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject)
	{
		this.emailSubject = emailSubject;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	@Override
	public String toString() {
		return "EmailHistory [emailBody=" + emailBody + ", emailSubject="
				+ emailSubject + ", emailTemplateId=" + emailTemplateId
				+ ", fromEmail=" + fromEmail + ", locationId=" + locationId
				+ ", toEmail=" + toEmail + ", ccEmail=" + ccEmail
				+ ", referenceId=" + referenceId + ", localTime=" + localTime
				+ "]";
	}

	

}