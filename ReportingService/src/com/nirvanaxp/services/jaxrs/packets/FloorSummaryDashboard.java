/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class FloorSummaryDashboard
{

	private int totalCover;
	private int totalReservation;
	private int waitListCount;
	private int seatedCount;
	private int walkinCount;
	private double spaceUtilization;
	private int approxWaitTime;
	private int availableTables;
	private int currentSeatedCount;

	public int getTotalCover()
	{
		return totalCover;
	}

	public void setTotalCover(int totalCover)
	{
		this.totalCover = totalCover;
	}

	public int getTotalReservation()
	{
		return totalReservation;
	}

	public void setTotalReservation(int totalReservation)
	{
		this.totalReservation = totalReservation;
	}

	public int getWaitListCount()
	{
		return waitListCount;
	}

	public void setWaitListCount(int waitListCount)
	{
		this.waitListCount = waitListCount;
	}

	public int getSeatedCount()
	{
		return seatedCount;
	}

	public void setSeatedCount(int seatedCount)
	{
		this.seatedCount = seatedCount;
	}

	public int getWalkinCount()
	{
		return walkinCount;
	}

	public void setWalkinCount(int walkinCount)
	{
		this.walkinCount = walkinCount;
	}

	public double getSpaceUtilization()
	{
		return spaceUtilization;
	}

	public void setSpaceUtilization(double spaceUtilization)
	{
		this.spaceUtilization = spaceUtilization;
	}

	public int getApproxWaitTime()
	{
		return approxWaitTime;
	}

	public void setApproxWaitTime(int approxWaitTime)
	{
		this.approxWaitTime = approxWaitTime;
	}

	public int getAvailableTables()
	{
		return availableTables;
	}

	public void setAvailableTables(int availableTables)
	{
		this.availableTables = availableTables;
	}

	public int getCurrentSeatedCount()
	{
		return currentSeatedCount;
	}

	public void setCurrentSeatedCount(int currentSeatedCount)
	{
		this.currentSeatedCount = currentSeatedCount;
	}

}
