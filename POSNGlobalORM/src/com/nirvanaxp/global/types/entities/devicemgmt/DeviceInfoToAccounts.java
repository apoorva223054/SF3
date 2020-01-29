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
@Table(name = "deviceinfo_to_accounts")
@XmlRootElement(name = "DeviceInfoToAccounts")
public class DeviceInfoToAccounts
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	protected int id;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;

	@Column(name = "updated_by", nullable = false)
	protected String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	@Column(name = "created_by")
	protected String createdBy;

	@Column(nullable = false, length = 1)
	protected String status;

	@Column(name = "account_id", nullable = false)
	protected int accountId;

	@Column(name = "deviceinfo_id", nullable = false)
	protected int deviceInfoId;

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Date getCreated()
	{
		return created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(Character status)
	{
		this.status = "" + status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public int getDeviceInfoId()
	{
		return deviceInfoId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public void setDeviceInfoId(int deviceInfoId)
	{
		this.deviceInfoId = deviceInfoId;
	}

	@Override
	public String toString() {
		return "DeviceInfoToAccounts [id=" + id + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", created=" + created
				+ ", createdBy=" + createdBy + ", status=" + status
				+ ", accountId=" + accountId + ", deviceInfoId=" + deviceInfoId
				+ "]";
	}

}
