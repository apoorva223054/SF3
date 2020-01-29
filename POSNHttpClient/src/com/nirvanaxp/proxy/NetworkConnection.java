/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.nirvanaxp.proxy.httpscons.EasySSLSocketFactory;
import com.nirvanaxp.proxy.httpscons.PosNirvanaX509TrustManager;
import com.nirvanaxp.server.util.NirvanaLogger;

// TODO: Auto-generated Javadoc
/**
 * The Class NetworkConnection.
 * 
 * @author Pos Nirvana 2 This class is used to connect to the server and get the
 *         response from the server.
 */
public class NetworkConnection
{

	public static int CONNECTION_TIMEOUT_MILLISEC = 100000;
	public static int SOCKET_TIMEOUT_MILLISEC = 150000;
	private HttpResponse response;
	private final static NirvanaLogger logger = new NirvanaLogger(NetworkConnection.class.getName());

	/**
	 * Gets the http client.
	 * 
	 * @return the httpclient object
	 */
	private DefaultHttpClient getHttpClient()
	{

		// set time out parameter for client
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT_MILLISEC);
		HttpConnectionParams.setLinger(httpParams, -1);
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);
		// HttpConnectionParams.setSoReuseaddr(httpParams, false);
		return new DefaultHttpClient(httpParams);
	}

	/**
	 * Gets the https client.
	 * 
	 * @return the https client
	 */
	private DefaultHttpClient getHttpsClient()
	{

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
		DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);
		return httpClient;

	}

	/**
	 * @param http
	 *            client
	 * @return the https client
	 */
	private DefaultHttpClient getHttpsClient(HttpClient client)
	{

		try
		{
			X509TrustManager tm = new PosNirvanaX509TrustManager();
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]
			{ tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager clientConnectionManager = client.getConnectionManager();
			SchemeRegistry schemeRegistry = clientConnectionManager.getSchemeRegistry();
			schemeRegistry.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(clientConnectionManager, client.getParams());
		}
		catch (Exception ex)
		{
			return null;
		}

	}

	/**
	 * this method checks if this is http request or https request and based on
	 * that returns the appropriate http client.
	 * 
	 * @param serverUrl
	 *            - url to connect to
	 * @return the appropriate http client
	 */
	private DefaultHttpClient getAppropriateHttpClient(String serverUrl)
	{

		// check if its http or https clients
		DefaultHttpClient httpClient = null;
		if (serverUrl.startsWith("https"))
		{
			// get the https client
			httpClient = getHttpsClient(new DefaultHttpClient());

		}
		else
		{
			// get the normal http client
			httpClient = getHttpClient();
		}

		return httpClient;
	}

	/**
	 * Execute get request.
	 * 
	 * @param serviceURL
	 *            - url to connect to
	 * @return - the response returned by the server
	 * @throws POSNNetworkProxyException
	 */
	public String executeGetRequest(String serviceURL) throws POSNNetworkProxyException
	{
		try
		{
			HttpGet getRequest = new HttpGet(serviceURL);

			// construct a URI object
			// getRequest.setURI(new URI(serviceURL));

			return executeNetworkRequest(serviceURL, getRequest);

		}
		catch (Exception e)
		{
			throw new POSNNetworkProxyException("Error while executing POST request: ", e);
		}
	}

	/**
	 * Execute post request.
	 * 
	 * @param serviceURL
	 *            - url to connect to
	 * @param nameValuePairs
	 *            - the params that server accepts in nvps format
	 * @return response returned by the server
	 */
	public String executePostRequest(String serviceURL, List<NameValuePair> nameValuePairs) throws POSNNetworkProxyException
	{
		try
		{
			HttpPost postRequest = new HttpPost();

			// construct a URI object
			postRequest.setURI(new URI(serviceURL));
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));

			return executeNetworkRequest(serviceURL, postRequest);
		}
		catch (Exception e)
		{
			throw new POSNNetworkProxyException("Error while executing POST request: ", e);
		}
	}

	/**
	 * Execute post request for ContentType.
	 * 
	 * @param serviceURL
	 *            - url to connect to
	 * @param jsonString
	 *            - params in form of json string
	 * @param accept
	 * @param contentType
	 * @return response returned by the server
	 */
	public String executePostRequestForContentType(String serviceURL, String jsonString, String contentType, String accept) throws POSNNetworkProxyException
	{
		try
		{
			HttpPost postRequest = new HttpPost();
			// construct a URI object
			postRequest.setURI(new URI(serviceURL));
			postRequest.setHeader("Accept", accept);
			postRequest.setHeader("Content-type", contentType);
			postRequest.setEntity(new StringEntity(jsonString));

			return executeNetworkRequest(serviceURL, postRequest);
		}
		catch (Exception e)
		{
			throw new POSNNetworkProxyException("Error while executing POST request: ", e);
		}
	}

	/**
	 * Execute post request.
	 * 
	 * @param serviceURL
	 *            - url to connect to
	 * @param jsonString
	 *            - params in form of json string
	 * @return response returned by the server
	 */
	public String executePostRequest(String serviceURL, String jsonString) throws POSNNetworkProxyException
	{
		try
		{
			HttpPost postRequest = new HttpPost();
			// construct a URI object
			postRequest.setURI(new URI(serviceURL));
			postRequest.setHeader("Accept", "application/json");
			postRequest.setHeader("Content-type", "application/json");
			postRequest.setEntity(new StringEntity(jsonString));

			return executeNetworkRequest(serviceURL, postRequest);
		}
		catch (Exception e)
		{
			throw new POSNNetworkProxyException("Error while executing POST request: ", e);
		}
	}

	public String executePostRequestWithAuthorization(String serviceURL, String jsonString, String contentType, String accept, String auth) throws POSNNetworkProxyException
	{
		try
		{
			HttpPost postRequest = new HttpPost();
			// construct a URI object
			postRequest.setURI(new URI(serviceURL));
			postRequest.setHeader("Accept", accept);
			postRequest.setHeader("Content-type", contentType);
			postRequest.setHeader("Authorization", auth);
			postRequest.setEntity(new StringEntity(jsonString));

			return executeNetworkRequest(serviceURL, postRequest);
		}
		catch (Exception e)
		{
			throw new POSNNetworkProxyException("Error while executing POST request: ", e);
		}
	}

	public String executePostRequestForImageUpload(String serviceURL, String jsonString) throws POSNNetworkProxyException
	{
		try
		{

			// HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(serviceURL);

			FileBody fileContent = new FileBody(new File(jsonString));

			StringBody comment = new StringBody("Filename: " + "Screenshot.png");
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("file", fileContent);
			httppost.setEntity(reqEntity);

			return executeNetworkRequest(serviceURL, httppost);
		}
		catch (Exception e)
		{
			throw new POSNNetworkProxyException("Error while executing POST request: ", e);
		}
	}

	private String executeNetworkRequest(String serviceURL, HttpUriRequest request) throws ClientProtocolException, IOException
	{
		// get the client
		DefaultHttpClient httpClient = getAppropriateHttpClient(serviceURL);

		ResponseHandler<String> handler = new ResponseHandler<String>()
		{

			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
			{

				if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201)
				{

					throw new IOException("Failed Network Call: HTTP error code : " + response.getStatusLine().getStatusCode());
				}

				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					InputStream in = entity.getContent();

					StringBuilder out = new StringBuilder();
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					for (String line = br.readLine(); line != null; line = br.readLine())
					{
						out.append(line);
					}
					br.close();
					String str = sanitize(out);
					return str;
				}
				else
				{
					throw new IOException("Failed Network Call: Null Response");
				}
			}

			private String sanitize(StringBuilder out)
			{
				StringBuilder o2 = new StringBuilder(out.length());

				int curlyIndex = out.indexOf("{");
				int squareIndex = out.indexOf("[");

				int i = Math.min(curlyIndex, squareIndex); // lesser of two
				i = i < 0 ? Math.max(curlyIndex, squareIndex) : i; // if <0,
																	// then
																	// greater
																	// of
																	// two
				i = i < 0 ? 0 : i; // if still <0, then 0
				int j = 0;
				for (; i < out.length(); ++i)
				{
					char c = out.charAt(i);
					if (c > -1 && c < 128)
					{
						o2.insert(j, c);
						++j;
					}
					else
					{

					}
				}
				return o2.toString();
			}
		};

		String str = "Error: ";
		try
		{
			try
			{
				str = httpClient.execute(request, handler);
			}
			catch (Exception e)
			{
				str += e.getMessage();
				logger.severe(e);
			}

		}
		catch (Exception e)
		{
			str += e.getMessage();

		}
		finally
		{
			httpClient.getConnectionManager().shutdown();
		}

		return str;

	}

	private String getStringFromResponse(HttpResponse response)
	{
		HttpEntity entity = response.getEntity();
		if (entity != null)
		{
			try
			{
				InputStream in;
				in = entity.getContent();

				StringBuilder out = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				for (String line = br.readLine(); line != null; line = br.readLine())
				{
					out.append(line);
				}
				br.close();
				return out.toString();
			}
			catch (IOException e)
			{
				
				logger.severe(e, "Error during updating Item: ");
			}
			catch (IllegalStateException e)
			{
				logger.severe(e, "Error during updating Item: ");
			}

		}
		return null;
	}

	public String executeGetRequest(String serviceURL, int timeOut) throws POSNNetworkProxyException
	{
		try
		{
			HttpGet getRequest = new HttpGet(serviceURL);

			// construct a URI object
			// getRequest.setURI(new URI(serviceURL));

			return executeNetworkRequest(serviceURL, getRequest, timeOut);

		}
		catch (Exception e)
		{
			throw new POSNNetworkProxyException("Error while executing POST request: ", e);
		}
	}

	private String executeNetworkRequest(String serviceURL, HttpUriRequest request, int timeOut) throws ClientProtocolException, IOException
	{
		// get the client
		DefaultHttpClient httpClient = getHttpClientForPrinters(timeOut);

		ResponseHandler<String> handler = new ResponseHandler<String>()
		{

			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
			{

				if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201)
				{

					throw new IOException("Failed Network Call: HTTP error code : " + response.getStatusLine().getStatusCode());
				}

				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					InputStream in = entity.getContent();

					StringBuilder out = new StringBuilder();
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					for (String line = br.readLine(); line != null; line = br.readLine())
					{
						out.append(line);
					}
					br.close();
					String str = sanitize(out);
					return str;
				}
				else
				{
					throw new IOException("Failed Network Call: Null Response");
				}
			}

			private String sanitize(StringBuilder out)
			{
				StringBuilder o2 = new StringBuilder(out.length());

				int curlyIndex = out.indexOf("{");
				int squareIndex = out.indexOf("[");

				int i = Math.min(curlyIndex, squareIndex); // lesser of two
				i = i < 0 ? Math.max(curlyIndex, squareIndex) : i; // if <0,
																	// then
																	// greater
																	// of
																	// two
				i = i < 0 ? 0 : i; // if still <0, then 0
				int j = 0;
				for (; i < out.length(); ++i)
				{
					char c = out.charAt(i);
					if (c > -1 && c < 128)
					{
						o2.insert(j, c);
						++j;
					}
					else
					{

					}
				}
				return o2.toString();
			}
		};

		String str = "Error: ";
		try
		{
			try
			{
				str = httpClient.execute(request, handler);
			}
			catch (Exception e)
			{
				// TODO: handle exception
			}

		}
		catch (Exception e)
		{
			str += e.getMessage();

		}
		finally
		{
			httpClient.getConnectionManager().shutdown();
		}

		return str;

	}

	private DefaultHttpClient getHttpClientForPrinters(int timeOut)
	{

		// set time out parameter for client
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, timeOut);
		HttpConnectionParams.setSoTimeout(httpParams, timeOut);
		HttpConnectionParams.setLinger(httpParams, -1);
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);
		// HttpConnectionParams.setSoReuseaddr(httpParams, false);
		return new DefaultHttpClient(httpParams);
	}

}
