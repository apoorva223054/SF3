/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.devicemgmt;

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
@Table(name = "deviceinfo_to_encryptionkey")
@XmlRootElement(name = "deviceinfo_to_encryptionkey")
public class DeviceInfoToEncyptionKey
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "device_info_id")
	private int deviceInfoId;

	@Column(name = "encryption_key_id")
	private int encryptionKeyId;

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created)
	{
		if (created != 0)
		{
			this.created = new Date(created);
		}

	}

	public long getUpdated()
	{
		if (this.updated != null)
		{
			return this.updated.getTime();
		}
		return 0;
	}
	
	public void setUpdated(long updated)
	{
		if (updated != 0)
		{
			this.updated = new Date(updated);
		}
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getDeviceInfoId()
	{
		return deviceInfoId;
	}

	public void setDeviceInfoId(int deviceInfoId)
	{
		this.deviceInfoId = deviceInfoId;
	}

	public int getEncryptionKeyId()
	{
		return encryptionKeyId;
	}

	public void setEncryptionKeyId(int encryptionKeyId)
	{
		this.encryptionKeyId = encryptionKeyId;
	}

	@Override
	public String toString() {
		return "DeviceInfoToEncyptionKey [id=" + id + ", created=" + created
				+ ", updated=" + updated + ", deviceInfoId=" + deviceInfoId
				+ ", encryptionKeyId=" + encryptionKeyId + "]";
	}

}
