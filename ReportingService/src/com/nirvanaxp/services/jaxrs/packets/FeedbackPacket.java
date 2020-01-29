/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class FeedbackPacket
{
	private int totalFeedback;
	private int happyFace;
	private int straightFace;
	private int sadFace;
	private int star1;
	private int star2;
	private int star3;
	private int star4;
	private int star5;

	public int getTotalFeedback()
	{
		return totalFeedback;
	}

	public void setTotalFeedback(int totalFeedback)
	{
		this.totalFeedback = totalFeedback;
	}

	public int getStar1()
	{
		return star1;
	}

	public void setStar1(int star1)
	{
		this.star1 = star1;
	}

	public int getStar2()
	{
		return star2;
	}

	public void setStar2(int star2)
	{
		this.star2 = star2;
	}

	public int getStar3()
	{
		return star3;
	}

	public void setStar3(int star3)
	{
		this.star3 = star3;
	}

	public int getStar4()
	{
		return star4;
	}

	public void setStar4(int star4)
	{
		this.star4 = star4;
	}

	public int getStar5()
	{
		return star5;
	}

	public void setStar5(int star5)
	{
		this.star5 = star5;
	}

	public int getHappyFace()
	{
		return happyFace;
	}

	public void setHappyFace(int happyFace)
	{
		this.happyFace = happyFace;
	}

	public int getStraightFace()
	{
		return straightFace;
	}

	public void setStraightFace(int straightFace)
	{
		this.straightFace = straightFace;
	}

	public int getSadFace()
	{
		return sadFace;
	}

	public void setSadFace(int sadFace)
	{
		this.sadFace = sadFace;
	}

	@Override
	public String toString()
	{
		return "FeedbackPacket [totalFeedback=" + totalFeedback + ", happyFace=" + happyFace + ", straightFace=" + straightFace + ", sadFace=" + sadFace + ", star1=" + star1 + ", star2=" + star2
				+ ", star3=" + star3 + ", star4=" + star4 + ", star5=" + star5 + "]";
	}

}
