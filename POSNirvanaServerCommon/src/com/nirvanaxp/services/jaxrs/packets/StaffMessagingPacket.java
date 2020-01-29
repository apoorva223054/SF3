/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.catalog.items.Nutritions;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.user.StaffMessaging;
@XmlRootElement(name = "StaffMessagingPacket")
public class StaffMessagingPacket extends PostPacket
{

	private StaffMessaging staffMessaging;
	public StaffMessaging getStaffMessaging() {
		return staffMessaging;
	}
	public void setStaffMessaging(StaffMessaging staffMessaging) {
		this.staffMessaging = staffMessaging;
	}
	@Override
	public String toString() {
		return "StaffMessagingPacket [staffMessaging=" + staffMessaging + "]";
	}
	
	 
	 
}
