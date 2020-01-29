package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.user.UsersToRole;

@XmlRootElement(name = "UserPostPacketForGlobalUser")
public class UserPostPacketForGlobalUser extends PostPacket{

	private User user;

	private int updatedBy;

	List<Integer> locationsList;

	Set<Address> addressList;

	Set<UsersToRole> globalRoles;

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

	public int getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public List<Integer> getLocationsList()
	{
		return locationsList;
	}

	public void setLocationsList(List<Integer> locationsList)
	{
		this.locationsList = locationsList;
	}

	@Override
	public String toString() {
		return "UserPostPacketForGlobalUser [user=" + user + ", updatedBy="
				+ updatedBy + ", locationsList=" + locationsList
				+ ", addressList=" + addressList + ", globalRoles="
				+ globalRoles + "]";
	}

	

}
