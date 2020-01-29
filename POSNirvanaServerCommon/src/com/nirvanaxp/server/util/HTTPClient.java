package com.nirvanaxp.server.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.nirvanaxp.services.jaxrs.INirvanaService;

public class HTTPClient
{

	private final String USER_AGENT = "Mozilla/5.0";

	// HTTP GET request
	public String sendGet(String url) throws Exception
	{

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// add request header
		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + url);
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

	// HTTP POST request
	public String sendPost(List<NameValuePair> urlParameters, String url) throws Exception
	{

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		// List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		// urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
		// urlParameters.add(new BasicNameValuePair("cn", ""));
		// urlParameters.add(new BasicNameValuePair("locale", ""));
		// urlParameters.add(new BasicNameValuePair("caller", ""));
		// urlParameters.add(new BasicNameValuePair("num", "12345"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters, HTTP.UTF_8));

		// post.setEntity(new
		// StringEntity("merchantId=9920105002&password=y7XQjTcTvH8="));

		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
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

	public String sendPostJSONObject(String postObject, String url) throws Exception
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost();
		// construct a URI object
		postRequest.setURI(new URI(url));
		postRequest.setHeader("Accept", "application/json");
		postRequest.setHeader("Content-type", "application/json");
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

	public String sendPostJSONObject(String postObject, String url, String accessToken) throws Exception
	{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost();
		// construct a URI object
		postRequest.setURI(new URI(url));
		postRequest.setHeader("Accept", "application/json");
		postRequest.setHeader("Content-type", "application/json");
		postRequest.setHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME, accessToken);
		postRequest.setEntity(new StringEntity(postObject));

		HttpResponse response = client.execute(postRequest);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postRequest.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		if(response!=null && response.getStatusLine().getStatusCode()==200){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null)
			{
				result.append(line);
			}
			return result.toString();
		}
		return null;
	}
	public String sendPostJSONObjectToEwards(String postObject, String url) throws Exception
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost();
		// construct a URI object
		postRequest.setURI(new URI(url));
		postRequest.setHeader("Accept", "application/json");
		postRequest.setHeader("Content-type", "application/json");
		postRequest.setHeader("merchantid","$2y$10$utAZUJrV.TpXSh1Go.dhtuFCqP8hlGAxftltCRwvTxWY/sg8SzMVm");
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
	public String sendGet(String url,String accessToken) throws Exception
	{

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		// add request header
		request.setHeader("Content-type", "application/json");
		request.setHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME, accessToken);

		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + url);
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
	public String sendPostJSONObjectForHEBKDS(String postObject, String url) throws Exception
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost();
		// construct a URI object
		postRequest.setURI(new URI(url));
		postRequest.setHeader("gw-app-identifier", "nirvana");
		postRequest.setHeader("gw-sales-identifier", "cafeorder");
		postRequest.setHeader("apiKey", "apiKey will be shared separately");
		postRequest.setHeader("sngStoreNbr", "Store number");
		postRequest.setHeader("sng-session ", "HMAC");
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
