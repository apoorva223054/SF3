/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.websocket;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.websocket.Session;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.websocket.filter.SecurityFilter;

public class POSNWebSocketInternal
{

	private static final NirvanaLogger logger = new NirvanaLogger(POSNWebSocketInternal.class.getName());

	private String clientId;
	private String merchantId;
	private String clientType;
	private String sessionId;
	private List<String> accessibleLocationsIdList = new ArrayList<String>();

	POSNWebSocketInternal(Session session) throws FileNotFoundException,  IOException, InvalidSessionException
	{
		// url that will be received will be like this
		// "ClientId=12345&ClientType=Android&SessionId=abc&locationsId=1,2,3,5,6";
		
		String queryString = session.getQueryString();
		if(queryString==null)
		{
			URI uri = session.getRequestURI();
			String url = uri.toString();
			int index =  url.indexOf(";");
			if(url!=null && index>0)
			{
				queryString = url.substring(++index);
			}
		}
		logger.info("Query String from incoming socket: ", queryString);

		if (queryString == null || !queryString.contains("&"))
		{
			throw new InvalidSessionException();
		}

		String paramsArr[] = queryString.split("&");
		for (int index = 0; index < paramsArr.length; index++)
		{
			paramsArr[index].trim();
			String key = paramsArr[index].substring(0, paramsArr[index].indexOf("=")).toLowerCase();
			String data = paramsArr[index].substring(paramsArr[index].indexOf("=") + 1);
			logger.info("Processing key and data", key, data);
			switch (key)
			{
				case "clientid" :
				{
					clientId = data;
					break;
				}
				case "clienttype" :
				{
					clientType = data;
					break;
				}
				case "locationsid" :
				{
					initializeInterestedLocations(data);
					break;
				}
				default :
				{
					logger.info("Skipping query string param for websocket session init: ",paramsArr[index]);
				}
			}

		}

		initializeMerchantId(session);

		logger.fine("Extracted data from incoming socket query string: ", clientId, ":", clientType, ":", sessionId, ":", accessibleLocationsIdList.toString(), ":", merchantId);

	}

	private void initializeMerchantId(Session session) throws FileNotFoundException,  IOException, InvalidSessionException
	{
		if (!"jwebsocket".equalsIgnoreCase(clientId))
		{
			// get session
			String sessionData = session.getRequestParameterMap().get(SecurityFilter.NXP_WEBSOCKET_SESSION_DATA).get(0);
			logger.info("Got session data", sessionData);
			String[] sessionDataArray = sessionData.split(";");
			for(String str : sessionDataArray)
			{
				if(str.startsWith("MerchantId="))
				{					
					merchantId = str.split("=")[1];
					logger.info("Found merchant id:", merchantId );
					break;
				}
			}			
		}
	}

	public String getClientId()
	{
		return clientId;
	}

	public String getMerchantId()
	{
		return merchantId;
	}

	public String getClientType()
	{
		return clientType;
	}

	public List<String> getAccessibleLocationsId()
	{
		return accessibleLocationsIdList;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	private void initializeInterestedLocations(String accessibleLocations)
	{
		try
		{
			if (accessibleLocations != null)
			{
				// check if user is interested in multiple locations
				if (accessibleLocations.contains(","))
				{
					String accessibleLocationsArr[] = accessibleLocations.split(",");

					accessibleLocationsIdList.addAll(Arrays.asList(accessibleLocationsArr));

				}
				else
				{
					// user has only 1 location to listen to
					String locationId = accessibleLocations.trim();
					accessibleLocationsIdList = new ArrayList<String>();
					accessibleLocationsIdList.add(locationId);
				}
			}
		}
		catch (Exception e)
		{
			logger.severe(e, "exception while initializing incoming socket accessible locations: ", e.getMessage());
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((clientType == null) ? 0 : clientType.hashCode());
		result = prime * result + ((merchantId == null) ? 0 : merchantId.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POSNWebSocketInternal other = (POSNWebSocketInternal) obj;
		if (clientId == null)
		{
			if (other.clientId != null)
				return false;
		}
		else if (!clientId.equals(other.clientId))
			return false;
		if (clientType == null)
		{
			if (other.clientType != null)
				return false;
		}
		else if (!clientType.equals(other.clientType))
			return false;
		if (merchantId == null)
		{
			if (other.merchantId != null)
				return false;
		}
		else if (!merchantId.equals(other.merchantId))
			return false;
		if (sessionId == null)
		{
			if (other.sessionId != null)
				return false;
		}
		else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}

}
