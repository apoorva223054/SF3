/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.experience.CustomerExperience;

@XmlRootElement(name = "CustomerExperiencePacket")
public class CustomerExperiencePacket extends PostPacket
{
	List<CustomerExperience> customerExperience;

	User user;

	String reservationId;


	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	public List<CustomerExperience> getCustomerExperience()
	{
		return customerExperience;
	}

	public void setCustomerExperience(List<CustomerExperience> customerExperience)
	{
		this.customerExperience = customerExperience;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	public String toString()
	{
		return "CustomerExperiencePacket [customerExperience=" + customerExperience + ", user=" + user + ", reservationId=" + reservationId + "]";
	}

}
