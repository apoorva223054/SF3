/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.websocket.protocol;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class JSONWrapper
{

	private String internalJSON;
	private String posnService;
	private String operationName;
	private String clientId;
	private String merchantId;
	private String locationId;
	private String echoString;
	private String schemaName;
	private boolean isGlobalLevelInfo;

	public JSONWrapper()
	{

	}

	public String getInternalJSON()
	{
		return internalJSON;
	}

	public void setInternalJSON(String internalJSON)
	{
		this.internalJSON = internalJSON;
	}

	public String getPosnService()
	{
		return posnService;
	}

	public void setPosnService(String posnService)
	{
		this.posnService = posnService;
	}

	public String getOperationName()
	{
		return operationName;
	}

	public void setOperationName(String operationName)
	{
		this.operationName = operationName;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getMerchantId()
	{
		return merchantId;
	}

	public void setMerchantId(String merchantId)
	{
		this.merchantId = merchantId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	@JsonIgnore
	public boolean isSupportedService()
	{
		if (posnService != null)
		{
			return POSNServices.valueOf(posnService) != null;
		}
		return false;
	}

	@JsonIgnore
	public boolean isSupportedOperation()
	{
		if (posnService != null && operationName != null)
		{
			return POSNServiceOperations.valueOf(posnService + "_" + operationName) != null;
		}
		return false;
	}

	public String getSchemaName()
	{
		return schemaName;
	}

	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}

	@JsonIgnore
	public void setInternalJSONWithEscapeCharacters(String json)
	{
		char[] b = json.toCharArray();
		List<Character> l = new ArrayList<Character>();
		for (char c : b)
		{
			if ('"' == c)
			{
				l.add(new Character('\\'));
			}

			l.add(new Character(c));
		}

		char[] c = new char[l.size()];
		for (int i = 0; i < l.size(); ++i)
		{
			c[i] = l.get(i);
		}

		setInternalJSON(new String(c));
	}

	public String getEchoString()
	{
		return echoString;
	}

	public void setEchoString(String echoString)
	{
		this.echoString = echoString;
	}

	public boolean isGlobalLevelInfo()
	{
		return isGlobalLevelInfo;
	}

	public void setGlobalLevelInfo(boolean isGlobalLevelInfo)
	{
		this.isGlobalLevelInfo = isGlobalLevelInfo;
	}

}
