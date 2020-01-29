/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.feedback.FeedbackQuestion;
@XmlRootElement(name = "FeedbackQuestionPacket")
public class FeedbackQuestionPacket extends PostPacket
{

	private FeedbackQuestion feedbackQuestion;

	public FeedbackQuestion getFeedbackQuestion()
	{
		return feedbackQuestion;
	}

	public void setFeedbackQuestion(FeedbackQuestion feedbackQuestion)
	{
		this.feedbackQuestion = feedbackQuestion;
	}

	@Override
	public String toString()
	{
		return "FeedbackQuestionPacket [feedbackQuestion=" + feedbackQuestion + "]";
	}

}
