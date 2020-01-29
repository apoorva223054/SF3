/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.sms;

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
@Table(name = "sms_history")
@NamedQuery(name = "SMSHistory.findAll", query = "SELECT e FROM SMSHistory e")
public class SMSHistory extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "sms_text")
	private String smsText;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "phone")
	private String phone;
	
	@Column(name = "reference_id")
	private String referenceId;
	

	@Column(name = "template_id")
	private int templateId;
	
	@Column(name="responce_code")
	private String responceCode;
	
	@Column(name="sender_id")
	private String senderId;
	
	@Column(name = "user_id")
	private String userId;
	


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getResponceCode()
	{
		return responceCode;
	}


	public void setResponceCode(String responceCode)
	{
		this.responceCode = responceCode;
	}

	public String getSenderId()
	{
		return senderId;
	}

	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
	}
	

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getReferenceId() {
		if(referenceId != null && (referenceId.length()==0 || referenceId.equals("0"))){return null;}else{	return referenceId;}
	}


	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}


	 


	public int getTemplateId() {
		return templateId;
	}


	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}


	@Override
	public String toString() {
		return "SMSHistory [smsText=" + smsText + ", locationId=" + locationId
				+ ", phone=" + phone + ", referenceId=" + referenceId
				+ ", templateId=" + templateId + ", responceCode="
				+ responceCode + ", senderId=" + senderId + ", userId="
				+ userId + "]";
	}

	
	
}