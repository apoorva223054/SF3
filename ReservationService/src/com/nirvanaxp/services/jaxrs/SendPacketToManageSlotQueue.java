/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.nirvanaxp.server.util.NirvanaLogger;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
public class SendPacketToManageSlotQueue
{

	/**  */
	private static final NirvanaLogger logger = new NirvanaLogger(SendPacketToManageSlotQueue.class.getName());

	/**  */
	private Connection connection;

	/**  */
	private Session session;

	/**  */
	private MessageProducer producer;

	/**
	 * 
	 *
	 * @throws Exception
	 */
	public void init() throws Exception
	{

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

		connection = connectionFactory.createConnection();
		connection.start();

		// Create a Session
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("ManageSlotMDB6");

		producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

	}

	/**
	 * 
	 *
	 * @param reservationScheduleId
	 * @param schemaName
	 * @param operationName
	 * @throws Exception
	 */
	public void sendMessage(int reservationScheduleId, String schemaName, String operationName) throws Exception
	{

		String message = "" + reservationScheduleId + "," + schemaName + "," + operationName;
		sendMessage(message);
	}

	/**
	 * 
	 *
	 * @param message
	 * @throws Exception
	 */
	public void sendMessage(String message) throws Exception
	{
		try
		{
			init();

			// Create a message
			TextMessage mdbMessage = session.createTextMessage(message);

			// Tell the producer to send the message
			producer.send(mdbMessage);

			// close the producer
			producer.close();

		}
		finally
		{
			if (session != null)
			{
				try
				{
					session.close();
				}
				catch (JMSException e)
				{
					logger.severe(e);
				}
			}
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (JMSException e)
				{
					logger.severe(e);
				}
			}
		}

	}
}
