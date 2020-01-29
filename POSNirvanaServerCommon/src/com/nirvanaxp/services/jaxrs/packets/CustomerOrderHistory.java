/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "CustomerOrderHistory")
public class CustomerOrderHistory
{

	private List<CustomerHistoryPacket> customerHistoryPackets;

	public List<CustomerHistoryPacket> getCustomerHistoryPackets()
	{
		return customerHistoryPackets;
	}

	public void setCustomerHistoryPackets(List<CustomerHistoryPacket> customerHistoryPackets)
	{
		this.customerHistoryPackets = customerHistoryPackets;
	}

	@Override
	public String toString() {
		return "CustomerOrderHistory [customerHistoryPackets="
				+ customerHistoryPackets + "]";
	}

	
}
