/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FloorSummary")
public class FloorSummary implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3751444101664150430L;
	private Integer confirmCount;
	private Integer partiallyCount;
	private Integer reservationCount;
	private Integer waitlistCount;
	private Integer coverCount;
	private Integer walkinCount;
	private Integer seatCount;
	private BigDecimal spaceCount;

	public FloorSummary()
	{

	}

	public Integer getConfirmCount()
	{
		return confirmCount;
	}

	public void setConfirmCount(Integer confirmCount)
	{
		this.confirmCount = confirmCount;
	}

	public Integer getPartiallyCount()
	{
		return partiallyCount;
	}

	public void setPartiallyCount(Integer partiallyCount)
	{
		this.partiallyCount = partiallyCount;
	}

	public Integer getReservationCount()
	{
		return reservationCount;
	}

	public void setReservationCount(Integer reservationCount)
	{
		this.reservationCount = reservationCount;
	}

	public Integer getWaitlistCount()
	{
		return waitlistCount;
	}

	public void setWaitlistCount(Integer waitlistCount)
	{
		this.waitlistCount = waitlistCount;
	}

	public Integer getWalkinCount()
	{
		return walkinCount;
	}

	public void setWalkinCount(Integer walkinCount)
	{
		this.walkinCount = walkinCount;
	}

	public BigDecimal getSpaceCount()
	{
		return spaceCount;
	}

	public void setSpaceCount(BigDecimal spaceCount)
	{
		this.spaceCount = spaceCount;
	}

	public Integer getCoverCount()
	{
		return coverCount;
	}

	public void setCoverCount(Integer coverCount)
	{
		this.coverCount = coverCount;
	}

	public Integer getSeatCount()
	{
		return seatCount;
	}

	public void setSeatCount(Integer seatCount)
	{
		this.seatCount = seatCount;
	}

}
