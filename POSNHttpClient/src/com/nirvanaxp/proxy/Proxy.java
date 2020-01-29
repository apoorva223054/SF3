/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.INirvanaService;

// TODO: Auto-generated Javadoc
/**
 * The Class Proxy.
 *
 * @author Pos Nirvana 2 This class is an intermediate class between the service
 *         layer and the network layer. This class takes in the appropriate
 *         params like if a GET request or POST request and params value and
 *         based on that, calls appropriate method and returns the response back
 *         to the service layer.Also note that Proxy is a thread, hence if we
 *         make a call of it as thread it will execute in background
 */
public class Proxy
{

	// required by proxy class
	/** The Constant REQUEST_TYPE_GET. */
	public static final int REQUEST_TYPE_GET = 0;

	/** The Constant REQUEST_TYPE_POST. */
	public static final int REQUEST_TYPE_POST = 1;

	/** The Constant RESPONSE_TYPE_STRING. */
	public static final int RESPONSE_TYPE_STRING = 0;

	/** The Constant REQUEST_TYPE_INPUTSTREAM. */
	public static final int REQUEST_TYPE_INPUTSTREAM = 2;

	/** The proxy interface. */
	private ProxyInterface proxyInterface;

	/** The request type. */
	private int requestType;

	/** The arguments. */
	private String arguments;

	/** The server url. */
	private String serverUrl;

	/** The name value pairs. */
	private List<NameValuePair> nameValuePairs;

	/** The reponse type. */
	private int reponseType = -1;

	private final static NirvanaLogger logger = new NirvanaLogger(Proxy.class.getName());

	private String contentType = null;

	private String accept = null;

	private String authrization;

	/**
	 * Instantiates a new proxy.
	 *
	 * @param proxyInterface
	 *            the proxy interface
	 * @param requestType
	 *            the request type
	 * @param reponseType
	 *            the reponse type
	 * @param arguments
	 *            the arguments
	 * @param serverUrl
	 *            the server url
	 */
	public Proxy(ProxyInterface proxyInterface, int requestType, int reponseType, String arguments, String serverUrl)
	{
		this.proxyInterface = proxyInterface;
		this.requestType = requestType;
		this.arguments = arguments;
		this.serverUrl = serverUrl;
		this.reponseType = reponseType;
	}
	public Proxy()
	{
	 
	}

	/**
	 * Instantiates a new proxy.
	 *
	 * @param proxyInterface
	 *            the proxy interface
	 * @param requestType
	 *            the request type
	 * @param reponseType
	 *            the reponse type
	 * @param nameValuePairs
	 *            the name value pairs
	 * @param serverUrl
	 *            the server url
	 */
	public Proxy(ProxyInterface proxyInterface, int requestType, int reponseType, List<NameValuePair> nameValuePairs, String serverUrl)
	{
		this.proxyInterface = proxyInterface;
		this.requestType = requestType;
		this.nameValuePairs = nameValuePairs;
		this.serverUrl = serverUrl;
		this.reponseType = reponseType;
	}

	/**
	 * Instantiates a new proxy.
	 *
	 * @param proxyInterface
	 *            the proxy interface
	 * @param requestType
	 *            the request type
	 * @param reponseType
	 *            the reponse type
	 * @param serverUrl
	 *            the server url
	 */
	public Proxy(ProxyInterface proxyInterface, int requestType, int reponseType, String serverUrl)
	{
		this.proxyInterface = proxyInterface;
		this.requestType = requestType;
		this.serverUrl = serverUrl;
		this.reponseType = reponseType;
	}

	/**
	 * Sends the request to the server.
	 * @throws Exception 
	 */
	public void sendRequestToServer(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		try
		{

			String httpResponse = null;

			NetworkConnection networkConnection = new NetworkConnection();
			if (requestType == REQUEST_TYPE_GET)
			{
				httpResponse = networkConnection.executeGetRequest(serverUrl);

			}
			else if (requestType == REQUEST_TYPE_POST)
			{

				// check if request is for json string or not
				if (arguments != null)
				{
					if (contentType == null || contentType.length() == 0)
					{
						httpResponse = networkConnection.executePostRequest(serverUrl, arguments);
					}
					else if (authrization == null || authrization.length() == 0)
					{
						httpResponse = networkConnection.executePostRequestForContentType(serverUrl, arguments, contentType, accept);
					}
					else
					{
						httpResponse = networkConnection.executePostRequestWithAuthorization(serverUrl, arguments, contentType, accept, authrization);
					}

				}
				else
				{
					// namevalue pairs are provided
					httpResponse = networkConnection.executePostRequest(serverUrl, nameValuePairs);
				}

			}
			else if (requestType == REQUEST_TYPE_INPUTSTREAM)
			{
				httpResponse = networkConnection.executePostRequestForImageUpload(serverUrl, arguments);
			}

			// some exception has occured, send no data
			if (proxyInterface != null)
			{
				proxyInterface.responseObtainedFromWebService(httpRequest, em, httpResponse);
			}
		}
		catch (Exception e)
		{
			if (proxyInterface != null)
			{
				proxyInterface.responseObtainedFromWebService(httpRequest, em, "Error: " + e.getMessage());
			}
			throw e;

		}
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public String getAccept()
	{
		return accept;
	}

	public void setAccept(String accept)
	{
		this.accept = accept;
	}

	/**
	 * @return the authrization
	 */
	public String getAuthrization()
	{
		return authrization;
	}

	/**
	 * @param authrization
	 *            the authrization to set
	 */
	public void setAuthrization(String authrization)
	{
		this.authrization = authrization;
	}


	public String sendPostJSONObject(String postObject,String url) throws Exception {
			HttpClient client = new DefaultHttpClient();
			 HttpPost postRequest = new HttpPost();
	         // construct a URI object
	         postRequest.setURI(new URI(url));
	         postRequest.setHeader("Accept", "application/json");
	         postRequest.setHeader("Authorization", authrization);
	         postRequest.setHeader("Content-type", "application/json");
	         postRequest.setEntity(new StringEntity(postObject));
	
			HttpResponse response = client.execute(postRequest);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + postRequest.getEntity());
			System.out.println("Response Code : " + 
	                                    response.getStatusLine().getStatusCode());
	
			BufferedReader rd = new BufferedReader(
	                        new InputStreamReader(response.getEntity().getContent()));
	
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		}
	
	public String sendPostJSONObject(String postObject, String url, String accessToken) throws Exception
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost();
		// construct a URI object
		postRequest.setURI(new URI(url));
		postRequest.setHeader("Accept", "application/json");
		postRequest.setHeader("Content-type", "application/json");
		postRequest.setHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME, "accessToken");
		postRequest.setEntity(new StringEntity(postObject));

		HttpResponse response = client.execute(postRequest);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postRequest.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null)
		{
			result.append(line);
		}
		return result.toString();
	}


}
