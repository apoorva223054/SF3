/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo;

@Entity
@Table(name = "user_session_history")
@XmlRootElement(name = "user_session_history")
public class UserSessionHistory implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "merchant_id")
	private int merchant_id;

	// id of user session
	@Column(name = "user_session_id")
	private int userSessionId;

	@Column(name = "user_id")
	private String user_id;

	@Column(name = "schema_name")
	private String schema_name;

	// this is unique key for authorizing a user to access our application
	@Column(name = "session_id")
	private String session_id;

	@Column(name = "users_roles_id")
	private Integer usersRolesId;

	@ManyToOne
	@JoinColumn(name = "device_info_id")
	private DeviceInfo deviceInfo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "login_time")
	private Date loginTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "logout_time")
	private Date logoutTime;

	@Column(name = "ipaddress", length = 256)
	private String ipAddress;

	@Column(name = "version_info")
	private String versionInfo;

	public UserSessionHistory()
	{
	}

	public UserSessionHistory(int merchant_id, int userSessionId, String user_id, String schema_name, String session_id, Integer usersRolesId, DeviceInfo deviceInfo, long loginTime, String ipAddress,
			String versionInfo)
	{
		super();
		this.merchant_id = merchant_id;
		this.userSessionId = userSessionId;
		this.user_id = user_id;
		this.schema_name = schema_name;
		this.session_id = session_id;
		this.usersRolesId = usersRolesId;
		this.deviceInfo = deviceInfo;
		setLoginTime(loginTime);
		setLogoutTime(loginTime);
		this.ipAddress = ipAddress;
		this.versionInfo = versionInfo;
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getMerchant_id()
	{
		return this.merchant_id;
	}

	public void setMerchant_id(int merchant_id)
	{
		this.merchant_id = merchant_id;
	}

	public String getSchema_name()
	{
		return this.schema_name;
	}

	public void setSchema_name(String schema_name)
	{
		this.schema_name = schema_name;
	}

	public String getSession_id()
	{
		return this.session_id;
	}

	public void setSession_id(String session_id)
	{
		this.session_id = session_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Integer getUsersRolesId()
	{
		return usersRolesId;
	}

	public void setUsersRolesId(int usersRolesId)
	{
		this.usersRolesId = usersRolesId;
	}

	public void setUsersRolesId(Integer usersRolesId)
	{
		this.usersRolesId = usersRolesId;
	}

	public DeviceInfo getDeviceInfo()
	{
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo)
	{
		this.deviceInfo = deviceInfo;
	}

	public int getUserSessionId()
	{
		return userSessionId;
	}

	public void setUserSessionId(int userSessionId)
	{
		this.userSessionId = userSessionId;
	}

	public long getLoginTime()
	{
		if (this.loginTime != null)
		{
			return this.loginTime.getTime();
		}
		return 0;
	}

	public void setLoginTime(long loginTime)
	{
		if (loginTime != 0)
		{
			this.loginTime = new Date(loginTime);
		}
	}

	public long getLogoutTime()
	{
		if (this.logoutTime != null)
		{
			return this.logoutTime.getTime();
		}
		return 0;
	}

	public void setLogoutTime(long logoutTime)
	{
		if (logoutTime != 0)
		{
			this.logoutTime = new Date(logoutTime);
		}
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the versionInfo
	 */
	public String getVersionInfo()
	{
		return versionInfo;
	}

	/**
	 * @param versionInfo
	 *            the versionInfo to set
	 */
	public void setVersionInfo(String versionInfo)
	{
		this.versionInfo = versionInfo;
	}

	@Override
	public String toString() {
		return "UserSessionHistory [id=" + id + ", merchant_id=" + merchant_id
				+ ", userSessionId=" + userSessionId + ", user_id=" + user_id
				+ ", schema_name=" + schema_name + ", session_id=" + session_id
				+ ", usersRolesId=" + usersRolesId + ", deviceInfo="
				+ deviceInfo + ", loginTime=" + loginTime + ", logoutTime="
				+ logoutTime + ", ipAddress=" + ipAddress + ", versionInfo="
				+ versionInfo + "]";
	}

}