/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import com.nirvanaxp.websocket.JWebSocketPacket;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.UserSession_;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.websocket.protocol.JSONWrapper;

@ServerEndpoint(value = "/POSNWebSocketServlet")
public class POSNWebSocketServerEndpoint
{

	private static final NirvanaLogger logger = new NirvanaLogger(POSNWebSocketServerEndpoint.class.getName());

	private POSNWebSocketInternal inboundSocket = null;

	private static final Set<POSNWebSocketServerEndpoint> SOCKET_SET = new CopyOnWriteArraySet<POSNWebSocketServerEndpoint>();

	private Session session;

	public POSNWebSocketServerEndpoint()
	{

	}

	@OnOpen
	public void start(Session session)
	{
		this.session = session;
		// add handler to echo binary messages
		session.addMessageHandler(new EchoMessageHandlerBinary(session.getBasicRemote()));
		try
		{
			this.inboundSocket = new POSNWebSocketInternal(session);

			String clientId = inboundSocket.getClientId();
			logger.info("Open Client " + clientId);
			SOCKET_SET.add(this);
			logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@======================================Current WebSocket Session Count: "+ SOCKET_SET.size());
		}
		catch (InvalidSessionException ise)
		{
			closeSocketOnError(ise);
		}
		catch (Exception e)
		{
			logger.severe(e, "Cannot add websocket for incoming query string:", session.getQueryString());
		}
	}
	
	private static class EchoMessageHandlerBinary implements MessageHandler.Whole<ByteBuffer>
	{

		private final RemoteEndpoint.Basic remoteEndpointBasic;

		private EchoMessageHandlerBinary(RemoteEndpoint.Basic remoteEndpointBasic)
		{
			this.remoteEndpointBasic = remoteEndpointBasic;
		}

		@Override
		public void onMessage(ByteBuffer message)
		{
			try
			{
				if (remoteEndpointBasic != null)
				{
					remoteEndpointBasic.sendPong(message);
				}
			}
			catch (IOException e)
			{
				logger.severe(e, "Error processing Binary message");
			}
		}
	}

	@OnClose
	public void end(CloseReason closeReason)
	{
		SOCKET_SET.remove(this);
		
		String clientId = "NO Client ID";
		if(inboundSocket!=null)
		{
			clientId = inboundSocket.getClientId();
		}

		String reasonPhrase = "NO Reason";
		if(closeReason!=null)
		{
			reasonPhrase = closeReason.getReasonPhrase();
		}
		
		logger.fine("removing socket connection for:", clientId , "; Reason: ", reasonPhrase);
	}

	@OnError
	public void onError(Throwable t) throws Throwable
	{
		if(session.isOpen())
		closeSocketOnError(t);
	}

	
	@OnMessage
	public void incoming(String message)
	{
		// Never trust the client
		// TODO check how incoming strings should be checked for
		// sensitive sql characters
		logger.info("Incoming Message from: ", inboundSocket.getClientId(),"Incoming Message: ", message);
		
		
		EntityManager em = null;
		try
		{
			// first check if session is still good
			if (!"jwebsocket".equalsIgnoreCase(inboundSocket.getClientId()))
			{
				em = GlobalSchemaEntityManager.getInstance().getEntityManager();
				UserSession us = getUserSession(em, inboundSocket.getSessionId());
				if (us == null)
				{
					closeSocketOnError(new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION, 
							MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE,
							MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE)));
					return;
				}
			}

			String msg = message.toString();
			logger.fine("Message: ", msg);

			// for ios ping
			if ("Ping!!!".equals(msg))
			{
				sendPong();
				return;
			}

			// for android ping
			if (msg != null && msg.length() > 0)
			{
				// put the packet in queue to be processed, implements
				// producer-consumer pattern
				startProcessingResponse(msg);
			}

		}
		catch (Exception e)
		{
			logger.severe(e, "exception while recieiving incoming text message on socket: ", e.getMessage());

		}finally{
			 GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private UserSession getUserSession(EntityManager em, String sessionId) throws InvalidSessionException 
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UserSession> criteria = builder.createQuery(UserSession.class);
		Root<UserSession> root = criteria.from(UserSession.class);
		TypedQuery<UserSession> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(UserSession_.session_id), sessionId)));
		try
		{
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e, "Error while getting User Session for Session Id: ", sessionId, ", message= " + e.getMessage());
			throw new InvalidSessionException();
		}

	}
	

	private void closeSocketOnError(Throwable t)
	{
		try
		{
			SOCKET_SET.remove(session);
			
			// close the connection for invalid session id
			session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, t.getMessage()));
		}
		catch (Exception e)
		{
			logger.severe("Could not transmit invalid session message:", t.getMessage(),"\nFailed to close session:", e.getMessage(),"\nCannot add websocket for incoming query string:", session.getQueryString());
		}
	}

	private void startProcessingResponse(String response) throws IOException
	{
		if (response != null && response.length() > 0)
		{
			// checks of the posnirvana packet that needs to be broadcasted or
			// not

			String packettoBroadcast = processPacket(response);
			logger.fine("Socket Packet to Broadcast is:", packettoBroadcast);

			// handle old android client ping
			if (packettoBroadcast != null && "Ping!!!".equals(packettoBroadcast))
			{
				sendPong();
				return;
			}

			// only jwebsocket sends data to broadcast, clients only send ping
			// so we can safely assume that the input response is the same text
			// that needs to be broadcasted. The input text also contains
			// JSONWrapper
			// data, which gives us meta info on the text, like locationId
			// and merchantId
			if (packettoBroadcast == null)
			{
				JSONWrapper jsonWrapper = processDataOfJsonWrapper(response);
				logger.fine("jsonWrapper is ", jsonWrapper.toString());

				if (jsonWrapper != null)
				{
					broadcast(response, jsonWrapper);
					return;
				}
			}

			logger.severe("DID NOT BROADCAST:", packettoBroadcast);

		}

	}

	private JSONWrapper processDataOfJsonWrapper(String dataToProcess)
	{
		ObjectMapper g = new ObjectMapper();
		// parse the input json
		try
		{
			JSONWrapper jsonWrapper = g.readValue(dataToProcess, JSONWrapper.class);
			return jsonWrapper;
		}
		catch (JsonParseException e)
		{
			logger.severe(e);
		}
		catch (JsonMappingException e)
		{
			logger.severe(e);
		}
		catch (IOException e)
		{
			logger.severe(e);
		}
		return null;
	}

	private String processPacket(String packet)
	{
		try
		{
			ObjectMapper g = new ObjectMapper();
			// parse the input json
			JWebSocketPacket jWebSocetPacket = g.readValue(packet, JWebSocketPacket.class);
			return jWebSocetPacket.getData();
			// replace all null values to empty string to avoid characters
		}
		catch (JsonParseException e)
		{
			logger.severe(e);
		}
		catch (JsonMappingException e)
		{
			logger.severe(e);
		}
		catch (IOException e)
		{
			logger.severe(e);
		}
		return null;
	}

	private void sendPong() throws IOException
	{
		logger.fine("Sending Pong");
		final RemoteEndpoint.Basic endPoint = session.getBasicRemote();
		 new Thread() {
			@Override
			public void run()
			{				
				try
				{
					sleep(500);
					if(endPoint!=null)
					{
						synchronized(endPoint)
						{
							endPoint.sendText("Pong!!!");
						}
					}
					else
					{
						throw new IOException("Socket was closed before sending pong");
					}
				}
				catch (IOException e)
				{
					logger.fine("Exception while sending pong:",e.getMessage());
				}
				catch (InterruptedException e)
				{
					logger.fine("Exception while sending pong:", e.getMessage());
				}
			}
		}.start();
	}

	private void broadcast(final String packettoBroadcast, final JSONWrapper jsonWrapper)
	{
		try
		{

			for (final POSNWebSocketServerEndpoint sock : SOCKET_SET)
			{
				final POSNWebSocketInternal websocketInternal = sock.inboundSocket;
				if (websocketInternal != null)
				{
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							// check if this socket belongs to the
							// same merchant for
							// whom business packet is sent
							logger.fine("client id is ", websocketInternal.getClientId());
							try
							{
								if (shouldBroadcastToClient(websocketInternal, jsonWrapper.getMerchantId(), jsonWrapper.getLocationId(), jsonWrapper.getClientId()))
								{
									// Broadcast to this client
									logger.fine(websocketInternal.getClientId(), " is interested in current data broadcast");
									try
									{
										synchronized (sock)
										{
											sock.session.getBasicRemote().sendText(packettoBroadcast);
										}
									}
									catch (IOException e)
									{

										SOCKET_SET.remove(sock);

										logger.severe(e, "exception while writing to client: " + websocketInternal.getClientId() + ", Message: ", e.getMessage());
									}

								}
								else
								{
									logger.fine(websocketInternal.getClientId(), " is NOT interested in current data broadcast");
								}
							}
							catch (Exception e)
							{
								logger.warn(e, "exception while processing packet to send to", websocketInternal.getClientId());
							}
						}

					}).start();
				}
			}

		}
		catch (Exception e)
		{
			logger.severe(e, "exception while broadcasting message sent from", inboundSocket.getClientId());
		}
	}

	private boolean shouldBroadcastToClient(POSNWebSocketInternal webSocketInternal, String merchantId, String locationId, String clientId)
	{

		try
		{
			// check if the same client that has posted onto the server, then
			// don't
			// broadcast to the client
			
			if (!webSocketInternal.getClientId().equalsIgnoreCase(clientId) && !webSocketInternal.getClientId().equals("jwebsocket"))
			{

				// some other client

				// check if this client is listening for the updates for this
				// merchant or not
				if (webSocketInternal.getMerchantId() != null && webSocketInternal.getMerchantId().trim().length() > 0 && merchantId != null && merchantId.trim().length() > 0)
				{
					if (webSocketInternal.getMerchantId().trim().equalsIgnoreCase(merchantId))
					{

						logger.finest("socket and merchant id match");
						// check if this client is interested in this location
						// or
						// not

						// if location id -1, then FINE must be broadcasted on
						// basis of merchant only
						if (locationId != null && locationId.trim().length() > 0)
						{
							if (locationId.equals("-1"))
							{
								return true;
							}
						}

						if (webSocketInternal.getAccessibleLocationsId() != null && webSocketInternal.getAccessibleLocationsId().size() > 0)
						{
							if (webSocketInternal.getAccessibleLocationsId().contains(locationId))
							{
								// Broadcast to this client
								return true;
							}
						}

					}
				}

			}
		}
		catch (Exception e)
		{
			logger.severe(e, "exception while checking if message should be sent to client: " + webSocketInternal.getClientId() + ", Message: ", e.getMessage());
		}

		return false;
	}

}
