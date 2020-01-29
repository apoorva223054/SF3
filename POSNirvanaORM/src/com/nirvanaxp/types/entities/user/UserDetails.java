/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the UserDetails database table.
 * 
 */
@Entity
@Table(name = "user_details")
@XmlRootElement(name = "user_details")
public class UserDetails extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(length = 10, name = "date_of_birth")
	private String dateOfBirth;
	
	@Column(length = 10 , name = "date_of_anniversary")
	private String dateOfAnniversary;

	@Column(length = 10 , name = "reference_id")
	private String referenceId;

	public UserDetails(String createdBy, String dateofbirth, String updatedBy, String dateOfAnniversary, String referenceId)
	{
		super();
		this.createdBy = createdBy;
		this.dateOfBirth = dateofbirth;
		this.updatedBy = updatedBy;
		this.dateOfAnniversary = dateOfAnniversary;
		this.referenceId = referenceId;
	}

	public UserDetails()
	{

	}

	public String getDateofbirth()
	{
		return this.dateOfBirth;
	}

	public void setDateofbirth(String dateofbirth)
	{
		this.dateOfBirth = dateofbirth;
	}

	public String getDateOfAnniversary() {
		return dateOfAnniversary;
	}

	public void setDateOfAnniversary(String dateOfAnniversary) {
		this.dateOfAnniversary = dateOfAnniversary;
	}
	
	public String getReferenceId() {
		if(referenceId != null && (referenceId.length()==0 || referenceId.equals("0"))){return null;}else{	return referenceId;}
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public String toString() {
		return "UserDetails [dateofbirth=" + dateOfBirth
				+ ", dateOfAnniversary=" + dateOfAnniversary + ", referenceId="
				+ referenceId + "]";
	}
}