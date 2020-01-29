/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RevenueSummary")
public class RevenueSummary implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4190197933062900098L;
	private List<Revenue> revenue;
	private List<CoverRevenue> coverRevenue;

	public RevenueSummary()
	{

	}

	public List<Revenue> getRevenue()
	{
		return revenue;
	}

	public void setRevenue(List<Revenue> revenue)
	{
		this.revenue = revenue;
	}

	public List<CoverRevenue> getCoverRevenue()
	{
		return coverRevenue;
	}

	public void setCoverRevenue(List<CoverRevenue> coverRevenue)
	{
		this.coverRevenue = coverRevenue;
	}

}
