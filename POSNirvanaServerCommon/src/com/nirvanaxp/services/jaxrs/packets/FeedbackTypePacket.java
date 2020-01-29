/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.feedback.FeedbackType;
@XmlRootElement(name = "FeedbackTypePacket")
public class FeedbackTypePacket extends PostPacket
{

	private FeedbackType feedbackType;

	public FeedbackType getFeedbackType()
	{
		return feedbackType;
	}

	public void setFeedbackType(FeedbackType feedbackType)
	{
		this.feedbackType = feedbackType;
	}

	@Override
	public String toString()
	{
		return "FeedbackTypePacket [feedbackType=" + feedbackType + "]";
	}

}
