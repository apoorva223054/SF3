/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.UsersToRole;
import com.nirvanaxp.global.types.entities.accounts.Account;

@XmlRootElement(name = "GlobalAccountPacket")
public class GlobalAccountPacket
{

	private Address address;

	private Account account;

	private List<User> usersList;

	private List<UsersToRole> usersToRoles;

	private List<Role> globalRoles;

	private int deviceTypeId;

	private String deviceId;

	private String deviceName;

	private String ipAddress;

	private String verificationCode;

	private String clientVersion;
	private int businessId;
	private String scope;

	public List<UsersToRole> getUsersToRoles()
	{
		return usersToRoles;
	}

	public void setUsersToRoles(List<UsersToRole> usersToRoles)
	{
		this.usersToRoles = usersToRoles;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public Account getAccount()
	{
		return account;
	}

	public void setAccount(Account account)
	{
		this.account = account;
	}

	public List<User> getUsersList()
	{
		return usersList;
	}

	public void setUsersList(List<User> usersList)
	{
		this.usersList = usersList;
	}

	public List<Role> getGlobalRoles()
	{
		return globalRoles;
	}

	public void setGlobalRoles(List<Role> globalRoles)
	{
		this.globalRoles = globalRoles;
	}

	public int getDeviceTypeId()
	{
		return deviceTypeId;
	}

	public void setDeviceTypeId(int deviceTypeId)
	{
		this.deviceTypeId = deviceTypeId;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getDeviceName()
	{
		return deviceName;
	}

	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public String getVerificationCode()
	{
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode)
	{
		this.verificationCode = verificationCode;
	}

	public String getClientVersion()
	{
		return clientVersion;
	}

	public void setClientVersion(String clientVersion)
	{
		this.clientVersion = clientVersion;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString()
	{
		return "GlobalAccountPacket [address=" + address + ", account=" + account + ", usersList=" + usersList + ", usersToRoles=" + usersToRoles + ", globalRoles=" + globalRoles + ", deviceTypeId="
				+ deviceTypeId + ", deviceId=" + deviceId + ", deviceName=" + deviceName + ", ipAddress=" + ipAddress + ", verificationCode=" + verificationCode + ", clientVersion=" + clientVersion
				+ ", businessId=" + businessId + ", scope=" + scope + "]";
	}

}
