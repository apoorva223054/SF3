/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.reservation.ContactPreference;
@XmlRootElement(name = "ContactPrefrencesPacket")
public class ContactPrefrencesPacket extends PostPacket
{

	private ContactPreference contactPreference;

	public ContactPreference getContactPreference()
	{
		return contactPreference;
	}

	public void setContactPreference(ContactPreference contactPreference)
	{
		this.contactPreference = contactPreference;
	}

	@Override
	public String toString()
	{
		return "ContactPrefrencesPacket [contactPreference=" + contactPreference + "]";
	}

}
