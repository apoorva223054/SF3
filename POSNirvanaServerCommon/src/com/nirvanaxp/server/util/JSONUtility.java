/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.util;

import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public final class JSONUtility
{

	private static final String DEFAULT_ERROR = "{\"code\":\"JSON1000\",\"displayMessage\":\"Could not generate JSON String\",\"technicalErrorMessage\":null}";

	private HttpServletRequest httpRequest;

	private static final NirvanaLogger logger = new NirvanaLogger(JSONUtility.class.getName());
	
	private ObjectMapper mapper = new ObjectMapper();

 	public JSONUtility(HttpServletRequest httpRequest)
	{
		this.httpRequest = httpRequest;
		
		this.mapper = new ObjectMapper();
	}

public JSONUtility()
{
this.mapper = new ObjectMapper();
 
	 
}
 
 
	private String convertToJSON(Object obj)
	{
		try
		{
			Writer strWriter = new StringWriter();
			mapper.writeValue(strWriter, obj);
			String jsonString = strWriter.toString();
			if (jsonString == null || jsonString.isEmpty())
			{
				if (obj instanceof java.util.Collection<?>)
				{
					return "[]";
				}
				else
				{
					return "{}";
				}
			}
			return jsonString;
		}
		catch (Exception t)
		{
			logger.severe(httpRequest, t, "Error while converting to JSON: ");
		}
		return DEFAULT_ERROR;
	}

	public String convertToJsonString(Object obj)
	{

		return convertToJSON(obj);

	}

	public String convertToJsonStringViaEliminatingNullValues(Object obj)
	{

		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		return convertToJSON(obj);

	}

	public String convertToJsonStringViaEliminatingNullValuesAndNotDefault(Object obj)
	{

		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		mapper.setSerializationInclusion(Inclusion.NON_DEFAULT);
		return convertToJSON(obj);

	}

	public String convertToJsonIncludeNotNullAndNotEmpty(Object obj)
	{

		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
		return convertToJSON(obj);

	}
	
	/*public String getSortedJson() {
	    try {
	     String secret = "secret";
	     String message = "Message";

	     Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	     SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
	     sha256_HMAC.init(secret_key);

	     String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
	     System.out.println(hash);
	     
	     String jsonData = "{\"e\":\"6\",\"f\":\"1\",\"b\":\"3\",\"c\":\"1\",\"a\":\"4\"}";
	     com.fasterxml.jackson.databind.ObjectMapperObjectMapper om = new ObjectMapper();
	     om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
	     Map<String, Object> map = om.readValue(jsonData, HashMap.class);
	     String json = om.writeValueAsString(map);
	     System.out.println(json); // result : {"a":"4","b":"3","c":"1","e":"6","f":"1"}
	     return hash;
	    }
	    catch (Exception e){
	     System.out.println("Error");
	    }
		return null;
	   } */
}
