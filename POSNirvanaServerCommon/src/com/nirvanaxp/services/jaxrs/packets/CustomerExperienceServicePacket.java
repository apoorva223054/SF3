/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.experience.CustomerFeedBackExperience;

@XmlRootElement(name = "CustomerExperienceServicePacket")
public class CustomerExperienceServicePacket extends PostPacket
{
	
	private CustomerFeedBackExperience customerFeedBackExperience;
	
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CustomerFeedBackExperience getCustomerFeedBackExperience() {
		return customerFeedBackExperience;
	}

	public void setCustomerFeedBackExperience(
			CustomerFeedBackExperience customerFeedBackExperience) {
		this.customerFeedBackExperience = customerFeedBackExperience;
	}

	@Override
	public String toString() {
		return "CustomerExperienceServicePacket [customerFeedBackExperience="
				+ customerFeedBackExperience + ", user=" + user + "]";
	}

	

	
}
