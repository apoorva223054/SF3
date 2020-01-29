/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.user.UsersToPayment;

@XmlRootElement(name = "UsersToPaymentPacket")
public class UsersToPaymentPacket extends PostPacket
{

	private UsersToPayment usersToPayment;

	
	public UsersToPayment getUsersToPayment() {
		return usersToPayment;
	}


	public void setUsersToPayment(UsersToPayment usersToPayment) {
		this.usersToPayment = usersToPayment;
	}


	@Override
	public String toString()
	{
		return "UsersToPaymentPacket [usersToPayment=" + usersToPayment + "]";
	}

}
