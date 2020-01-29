/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;

@XmlRootElement(name = "UsersToPaymentHistoryPacket")
public class UsersToPaymentHistoryPacket extends PostPacket
{

	private UsersToPaymentHistory usersToPaymentHistory;

	
	public UsersToPaymentHistory getUsersToPaymentHistory() {
		return usersToPaymentHistory;
	}


	public void setUsersToPaymentHistory(UsersToPaymentHistory usersToPaymentHistory) {
		this.usersToPaymentHistory = usersToPaymentHistory;
	}


	@Override
	public String toString()
	{
		return "UsersToPaymentHistoryPacket [usersToPaymentHistory=" + usersToPaymentHistory + "]";
	}

}
