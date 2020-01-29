/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.mdb;

import javax.annotation.PreDestroy;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.PreRemove;

import com.nirvanaxp.server.util.NirvanaLogger;

/**
 * Message-Driven Bean implementation class for: QueueListenerMDB
 */
@MessageDriven(mappedName = "PosnirvanaQueueVersion6")
public class JmsListener6 implements MessageListener
{

	private final static NirvanaLogger logger = new NirvanaLogger(JmsListener6.class.getName());

	/**
	 * Default constructor.
	 */
	public JmsListener6()
	{
	}

	@PreDestroy
	public void preDestroy()
	{
		try
		{
			logger.info("predestroy shutting down thread");
			WebSocketPacketSender sender = WebSocketPacketSender.getInstance();
			sender.shutDownSocket();
		}
		catch (Exception e)
		{
			logger.severe(e, "Error when destroying queue listener");
		}
	}

	@PreRemove
	public void preRemove()
	{
		try
		{
			logger.info("preRemove shutting down thread");
			WebSocketPacketSender sender = WebSocketPacketSender.getInstance();
			sender.shutDownSocket();
		}
		catch (Exception e)
		{
			logger.severe(e, "Error when removing queue listener");
		}
	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message)
	{
		try
		{

			logger.fine("Queue: I received a message version6 queue ");

			if (message instanceof TextMessage)
			{

				logger.info("Message is : ", ((TextMessage) message).getText());

				String stringToPost = ((TextMessage) message).getText();

				WebSocketPacketSender webSocketPacketSender = WebSocketPacketSender.getInstance();
				webSocketPacketSender.sendToSocketForBroadcast(stringToPost);
			}
		}
		catch (JMSException e)
		{
			logger.severe(e, "Error in Message Queue onMessage");
		}
	}

}
