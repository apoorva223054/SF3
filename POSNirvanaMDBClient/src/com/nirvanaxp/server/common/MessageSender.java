/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.common;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.websocket.protocol.JSONWrapper;

public class MessageSender
{

	private static NirvanaLogger logger = new NirvanaLogger(MessageSender.class.getName());

	public void init()
	{

	}

	public void sendMessage(HttpServletRequest httpRequest, String clientId, String serviceName, String operationName, String internalJson, String merchantId, String locationId, String echoString,
			String schemaName)
	{

		try
		{
			JSONWrapper jsonWrapper = constructJsonWrapper(serviceName, operationName, internalJson, clientId, merchantId, locationId, schemaName, echoString);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			String jsonToSendToServer = objectMapper.writeValueAsString(jsonWrapper);
			sendMessage(httpRequest, jsonToSendToServer);
		}
		catch (JsonGenerationException e)
		{
			logger.severe(httpRequest, e);
		}
		catch (JsonMappingException e)
		{
			logger.severe(httpRequest, e);
		}
		catch (IOException e)
		{
			logger.severe(httpRequest, e);
		}
	}

	private JSONWrapper constructJsonWrapper(String serviceName, String operationName, String objectToBeBroadcasted, String clientId, String merchantId, String locationId, String schemaName,
			String echoString)
	{

		JSONWrapper jsonWrapper = new JSONWrapper();
		jsonWrapper.setOperationName(operationName.toString());
		jsonWrapper.setPosnService(serviceName);
		jsonWrapper.setInternalJSON(objectToBeBroadcasted);
		jsonWrapper.setClientId(clientId);
		jsonWrapper.setMerchantId(merchantId);
		jsonWrapper.setLocationId(locationId);
		jsonWrapper.setEchoString(echoString);
		jsonWrapper.setSchemaName(schemaName);
		return jsonWrapper;
	}

	private void sendMessage(HttpServletRequest httpRequest, String message)
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
			Destination destination = session.createQueue("PosnirvanaQueueVersion6");

			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Create a message
			TextMessage mdbMessage = session.createTextMessage(message);

			// Tell the producer to send the message
			producer.send(mdbMessage);

			// close the producer
			producer.close();

		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e);
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
					logger.severe(httpRequest, e);
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
					logger.severe(httpRequest, e);
				}
			}
		}

	}

}
