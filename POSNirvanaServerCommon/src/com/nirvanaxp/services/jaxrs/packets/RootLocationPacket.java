/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.payment.Paymentgateway;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.user.User;
@XmlRootElement(name = "RootLocationPacket")
public class RootLocationPacket extends LocationPacket
{

	private List<Role> roleList;
	private LocationSetting locationSetting;

	private List<User> userList;

	private List<Paymentgateway> paymentgateway;

	private List<Location> locationsList;

	public List<Role> getRoleList()
	{
		return roleList;
	}

	public void setRoleList(List<Role> roleList)
	{
		this.roleList = roleList;
	}

	public List<User> getUserList()
	{
		return userList;
	}

	public void setUserList(List<User> userList)
	{
		this.userList = userList;
	}

	public List<Paymentgateway> getPaymentgateway()
	{
		return paymentgateway;
	}

	public void setPaymentgateway(List<Paymentgateway> paymentgateway)
	{
		this.paymentgateway = paymentgateway;
	}

	public List<Location> getLocationsList()
	{
		return locationsList;
	}

	public void setLocationsList(List<Location> locationsList)
	{
		this.locationsList = locationsList;
	}

	
	
	public LocationSetting getLocationSetting() {
		return locationSetting;
	}

	public void setLocationSetting(LocationSetting locationSetting) {
		this.locationSetting = locationSetting;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "RootLocationPacket [roleList=" + roleList + ", userList=" + userList + ", paymentgateway=" + paymentgateway 
				+ ", locationsList=" + locationsList + ", locationSetting=" + locationSetting + "]";
	}

}
