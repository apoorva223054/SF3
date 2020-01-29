/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UserDetails;
import com.nirvanaxp.types.entities.user.UsersToRole;
import com.nirvanaxp.types.entities.user.UsersToUserDetails;

@XmlRootElement(name = "UserPostPacket")
public class UserPostPacket extends PostPacket
{

	private User user;

	private String updatedBy;

	List<String> locationsList;

	Set<Address> addressList;

	Set<UsersToRole> globalRoles;
	
	private transient List<UserDetails> userDetails;
	

	private transient List<UsersToUserDetails> usersToUserDetails;

	private EmployeeMaster employeeMaster;

	public EmployeeMaster getEmployeeMaster()
	{
		return employeeMaster;
	}

	public void setEmployeeMaster(EmployeeMaster employeeMaster)
	{
		this.employeeMaster = employeeMaster;
	}
	
	public List<UserDetails> getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(List<UserDetails> userDetails) {
		this.userDetails = userDetails;
	}
	
	public List<UsersToUserDetails> getUsersToUserDetails() {
		return usersToUserDetails;
	}

	public void setUsersToUserDetails(List<UsersToUserDetails> usersToUserDetails) {
		this.usersToUserDetails = usersToUserDetails;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public Set<Address> getAddressList()
	{
		return addressList;
	}

	public void setAddressList(Set<Address> addressList)
	{
		this.addressList = addressList;
	}

	public Set<UsersToRole> getGlobalRoles()
	{
		return globalRoles;
	}

	public void setGlobalRoles(Set<UsersToRole> globalRoles)
	{
		this.globalRoles = globalRoles;
	}

	

	public String getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public List<String> getLocationsList()
	{
		return locationsList;
	}

	public void setLocationsList(List<String> locationsList)
	{
		this.locationsList = locationsList;
	}

	@Override
	public String toString() {
		return "UserPostPacket [user=" + user + ", updatedBy=" + updatedBy
				+ ", locationsList=" + locationsList + ", addressList="
				+ addressList + ", globalRoles=" + globalRoles
				+ ", userDetails=" + userDetails + ", usersToUserDetails="
				+ usersToUserDetails + "]";
	}

	

	

}
