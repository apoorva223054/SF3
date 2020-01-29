/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;


/**
 * The persistent class for the users_to_user_details database table.
 * 
 */
@Entity
@Table(name = "users_to_user_details")
@XmlRootElement(name = "users_to_user_details")
public class UsersToUserDetails extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "user_details_id", nullable = false)
	private int userDetailsId;

	public int getUserDetailsId() {
		return userDetailsId;
	}

	public void setUserDetailsId(int userDetailsId) {
		this.userDetailsId = userDetailsId;
	}

	@Column(name = "users_id", nullable = false)
	private String usersId;

	public UsersToUserDetails()
	{
	}

	public UsersToUserDetails(String createdBy, String status, int userDetailsId, String updatedBy, String usersId)
	{
		super();
		this.createdBy = createdBy;
		this.status = status;
		this.userDetailsId = userDetailsId;
		this.updatedBy = updatedBy;
		this.usersId = usersId;
	}

	

	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	@Override
	public String toString() {
		return "UsersToRole [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", status=" + status
				+ ", rolesId=" + userDetailsId
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", usersId=" + usersId + "]";
	}

}