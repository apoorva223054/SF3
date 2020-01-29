/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.feedback.FeedbackField;
@XmlRootElement(name = "FeedbackFieldPacket")
public class FeedbackFieldPacket extends PostPacket
{

	private FeedbackField feedbackField;

	public FeedbackField getFeedbackField()
	{
		return feedbackField;
	}

	public void setFeedbackField(FeedbackField feedbackField)
	{
		this.feedbackField = feedbackField;
	}

	@Override
	public String toString()
	{
		return "FeedbackFieldPacket [feedbackField=" + feedbackField + "]";
	}

}
