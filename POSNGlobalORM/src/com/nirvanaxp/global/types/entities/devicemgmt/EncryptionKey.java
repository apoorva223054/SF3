/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.devicemgmt;

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
@Table(name = "encryption_key")
@XmlRootElement(name = "encryption_key")
public class EncryptionKey
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "encryption_key")
	private String encryptionKey;

	@Column(name = "account_id")
	private int accountId;

	public EncryptionKey(String createdBy, String updatedBy, String encryptionKey, int accountId)
	{
		super();
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.encryptionKey = encryptionKey;
		this.accountId = accountId;
	}

	public EncryptionKey()
	{
		super();
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

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
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

	public String getEncryptionKey()
	{
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey)
	{
		this.encryptionKey = encryptionKey;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		return "EncryptionKey [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", encryptionKey="
				+ encryptionKey + ", accountId=" + accountId + "]";
	}

}
