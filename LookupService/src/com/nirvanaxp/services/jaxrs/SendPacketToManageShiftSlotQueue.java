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
 * The Class SendPacketToManageShiftSlotQueue.
 */
public class SendPacketToManageShiftSlotQueue
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(SendPacketToManageShiftSlotQueue.class.getName());

	/**
	 * Send message.
	 *
	 * @param shiftScheduleId
	 *            the shift schedule id
	 * @param schemaName
	 *            the schema name
	 * @param operationName
	 *            the operation name
	 * @throws Exception
	 *             the exception
	 */
	public void sendMessage(int shiftScheduleId, String schemaName, String operationName) throws Exception
	{

		String message = "" + shiftScheduleId + "," + schemaName + "," + operationName;
		sendMessage(message);
	}

	/**
	 * Send message.
	 *
	 * @param message
	 *            the message
	 * @throws Exception
	 *             the exception
	 */
	public void sendMessage(String message) throws Exception
	{
		Connection connection = null;

		Session session = null;

		MessageProducer producer = null;
		try
		{
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			connection = connectionFactory.createConnection();
			connection.start();

			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("ManageSlotMDB6");

			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

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
