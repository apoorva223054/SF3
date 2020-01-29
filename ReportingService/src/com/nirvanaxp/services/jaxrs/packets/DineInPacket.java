/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class DineInPacket
{
	private String avgDineIn;
	private String highDineIn;
	private String avgTakeOut;
	private String highTakeOut;

	public String getAvgDineIn()
	{
		return avgDineIn;
	}

	public void setAvgDineIn(String avgDineIn)
	{
		this.avgDineIn = avgDineIn;
	}

	public String getHighDineIn()
	{
		return highDineIn;
	}

	public void setHighDineIn(String highDineIn)
	{
		this.highDineIn = highDineIn;
	}

	public String getAvgTakeOut()
	{
		return avgTakeOut;
	}

	public void setAvgTakeOut(String avgTakeOut)
	{
		this.avgTakeOut = avgTakeOut;
	}

	public String getHighTakeOut()
	{
		return highTakeOut;
	}

	public void setHighTakeOut(String highTakeOut)
	{
		this.highTakeOut = highTakeOut;
	}

	@Override
	public String toString()
	{
		return "DineInPacket [avgDineIn=" + avgDineIn + ", highDineIn=" + highDineIn + ", avgTakeOut=" + avgTakeOut + ", highTakeOut=" + highTakeOut + "]";
	}

}
