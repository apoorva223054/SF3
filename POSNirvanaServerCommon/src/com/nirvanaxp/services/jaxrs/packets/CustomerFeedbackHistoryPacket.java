/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomerFeedbackHistoryPacket")
public class CustomerFeedbackHistoryPacket
{
	private int feedbackTypeId;
	private int totalFeedbackCount;
	private double rating;

	public int getFeedbackTypeId()
	{
		return feedbackTypeId;
	}

	public void setFeedbackTypeId(int feedbackTypeId)
	{
		this.feedbackTypeId = feedbackTypeId;
	}

	public int getTotalFeedbackCount()
	{
		return totalFeedbackCount;
	}

	public void setTotalFeedbackCount(int totalFeedbackCount)
	{
		this.totalFeedbackCount = totalFeedbackCount;
	}

	public double getRating()
	{
		return rating;
	}

	public void setRating(double rating)
	{
		this.rating = rating;
	}

	@Override
	public String toString()
	{
		return "CustomerFeedbackHistoryPacket [feedbackTypeId=" + feedbackTypeId + ", totalFeedbackCount=" + totalFeedbackCount + ", rating=" + rating + "]";
	}

}
