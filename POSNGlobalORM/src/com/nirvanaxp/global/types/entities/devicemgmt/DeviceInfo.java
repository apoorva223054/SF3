/**
p * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.devicemgmt;

import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.DeviceType;

@Entity
@Table(name = "device_info")
@XmlRootElement(name = "device_info")
public class DeviceInfo
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "device_id", nullable = false)
	private String deviceId;

	@Column(name = "device_name", nullable = false)
	private String deviceName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "device_type_id")
	private DeviceType deviceType;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	// uni-directional many-to-many association to Discount
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "deviceinfo_to_encryptionkey", joinColumns =
	{ @JoinColumn(name = "device_info_id", nullable = false) }, inverseJoinColumns =
	{ @JoinColumn(name = "encryption_key_id", nullable = false) })
	private Set<EncryptionKey> encyptionKeyinfos;

	public DeviceInfo()
	{
	}

	public DeviceInfo(String deviceId, DeviceType deviceType)
	{
		super();
		this.deviceId = deviceId;
		this.deviceType = deviceType;
	}

	public DeviceInfo(String deviceId, DeviceType deviceType, String createdBy, String updatedBy, Set<EncryptionKey> encyptionKeyinfos, String deviceName)
	{
		super();
		this.deviceId = deviceId;
		this.deviceType = deviceType;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.encyptionKeyinfos = encyptionKeyinfos;
		this.deviceName = deviceName;
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

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public DeviceType getDeviceType()
	{
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType)
	{
		this.deviceType = deviceType;
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

	public Set<EncryptionKey> getEncyptionKeyinfos()
	{
		return encyptionKeyinfos;
	}

	public void setEncyptionKeyinfos(Set<EncryptionKey> encyptionKeyinfos)
	{
		this.encyptionKeyinfos = encyptionKeyinfos;
	}

	public String getDeviceName()
	{
		return deviceName;
	}

	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}

	public boolean equals(Object otherObj)
	{
		if (otherObj != null && otherObj instanceof DeviceInfo)
		{
			DeviceInfo deviceInfo2 = (DeviceInfo) otherObj;
			if (deviceId != null && deviceId.trim().length() > 0 && deviceInfo2.getDeviceId() != null && deviceInfo2.getDeviceId().trim().length() > 0)
			{
				if (deviceId.trim().equalsIgnoreCase(deviceInfo2.getDeviceId()))
				{
					// check if device type is also same or not
					if (deviceType != null && deviceInfo2.getDeviceType() != null)
					{
						if (deviceType.getId() == deviceInfo2.getDeviceType().getId())
						{
							return true;
						}

					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "DeviceInfo [id=" + id + ", deviceId=" + deviceId
				+ ", deviceName=" + deviceName + ", deviceType=" + deviceType
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", encyptionKeyinfos=" + encyptionKeyinfos + "]";
	}

}
