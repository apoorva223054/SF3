/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.accounts;

import java.io.Serializable;
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
@Table(name = "temp_accounts")
@XmlRootElement(name = "TempAccount")
public class TempAccount implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

	@Column(name = "verification_code")
	private String verificationCode;

	@Column(name = "phone_no")
	private String phoneNo;

	@Column(name = "status")
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	private transient int verificationMethod;

	private transient String verificationUrl;

	public int getId()
	{
		return id;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getName()
	{
		return name;
	}

	public String getEmail()
	{
		return email;
	}

	public String getVerificationCode()
	{
		return verificationCode;
	}

	public String getPhoneNo()
	{
		return phoneNo;
	}

	public String getStatus()
	{
		return status;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public void setVerificationCode(String verificationCode)
	{
		this.verificationCode = verificationCode;
	}

	public void setPhoneNo(String phoneNo)
	{
		this.phoneNo = phoneNo;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getVerificationMethod()
	{
		return verificationMethod;
	}

	public void setVerificationMethod(int verificationMethod)
	{
		this.verificationMethod = verificationMethod;
	}

	public String getVerificationUrl()
	{
		return verificationUrl;
	}

	public void setVerificationUrl(String verificationUrl)
	{
		this.verificationUrl = verificationUrl;
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

	@Override
	public String toString() {
		return "TempAccount [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", name=" + name + ", email="
				+ email + ", verificationCode=" + verificationCode
				+ ", phoneNo=" + phoneNo + ", status=" + status + ", created="
				+ created + ", updated=" + updated + ", verificationMethod="
				+ verificationMethod + ", verificationUrl=" + verificationUrl
				+ "]";
	}

}
