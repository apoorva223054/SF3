/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.user.UsersToDiscount;

@XmlRootElement(name = "UsersToDiscountPacket")
public class UsersToDiscountPacket extends PostPacket
{

	private UsersToDiscount usersToDiscount;
	private List<UsersToDiscount> usersToDiscountList;
	private String usersId;
	public UsersToDiscount getUsersToDiscount() {
		return usersToDiscount;
	}

	public void setUsersToDiscount(UsersToDiscount usersToDiscount) {
		this.usersToDiscount = usersToDiscount;
	}

	public List<UsersToDiscount> getUsersToDiscountList() {
		return usersToDiscountList;
	}

	public void setUsersToDiscountList(List<UsersToDiscount> usersToDiscountList) {
		this.usersToDiscountList = usersToDiscountList;
	}

	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	@Override
	public String toString() {
		return "UsersToDiscountPacket [usersToDiscount=" + usersToDiscount
				+ ", usersToDiscountList=" + usersToDiscountList + ", usersId="
				+ usersId + "]";
	}


	

	

}
