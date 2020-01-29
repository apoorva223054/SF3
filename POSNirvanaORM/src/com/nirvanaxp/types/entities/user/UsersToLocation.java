/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;



/**
 * The persistent class for the users_to_locations database table.
 * 
 */
@Entity
@Table(name = "users_to_locations")
@XmlRootElement(name = "users_to_locations")
public class UsersToLocation extends POSNirvanaBaseClass implements Serializable
{


	@Column(name = "locations_id", nullable = false)
	private String locationsId;

 
	@Column(name = "users_id", nullable = false)
	private String usersId;
	private transient int  locationsTypeId;
	public UsersToLocation()
	{
	}

	public UsersToLocation(String status, String createdBy, String locationsId, String updatedBy, String usersId)
	{
		super();
		this.status = status;
		this.createdBy = createdBy;
		this.locationsId = locationsId;
		this.updatedBy = updatedBy;
		this.usersId = usersId;
	}

	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	 

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}
 

	public int getLocationsTypeId() {
		return locationsTypeId;
	}

	public void setLocationsTypeId(int locationsTypeId) {
		this.locationsTypeId = locationsTypeId;
	}

	@Override
	public String toString() {
		return "UsersToLocation [id=" + id + ", created=" + created
				+ ", status=" + status + ", createdBy=" + createdBy
				+ ", locationsId=" + locationsId + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", usersId=" + usersId + "]";
	}

}