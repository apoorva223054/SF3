/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>
 * Title: SmartPaymentsClient_JavaHTTPPost
 * </p>
 *
 * <p>
 * Description: A simple example to show how to use HTTP POST to send
 * transactions to the SmartPayments Web Service
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 *
 * <p>
 * Company: TPI Software
 * </p>
 *
 * @author Andy Chau
 * @version 1.0
 */
public class Util
{
	public Util()
	{
	}

	/**
	 * Post to a HTTP URL with the request string.
	 *
	 * @param url
	 *            is the url to post to
	 * @requestString is the request string to post to the url
	 */
	public static String sendHttpPost(String url, String sData, int timeout, boolean verbose) throws IOException
	{
		if (verbose)
		{
			System.out.println("URL: " + url);
			System.out.println("Send to host:\r\n" + sData);
		}

		OutputStream ostream = null;
		InputStream istream = null;
		ByteArrayOutputStream baos = null;
		try
		{
			byte[] data = sData.getBytes();
			// Send data
			java.net.URL theUrl = new URL(url);
			URLConnection conn = theUrl.openConnection();
			// conn.setReadTimeout(timeout);
			// conn.setConnectTimeout(timeout);
			conn.setDoOutput(true);
			ostream = conn.getOutputStream();
			ostream.write(data);
			ostream.flush();

			// Get the response
			java.lang.StringBuffer sb = new java.lang.StringBuffer();
			byte[] b = new byte[1024];
			istream = conn.getInputStream();
			int byteRead = istream.read(b);
			while (byteRead > 0)
			{
				// byte[] buf = new byte[byteRead];
				baos = new ByteArrayOutputStream();
				baos.write(b, 0, byteRead);
				byte[] result = baos.toByteArray();
				sb.append(new String(result));
				byteRead = istream.read(b);
			}
			String sResult = sb.toString();

			return sResult;
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (baos != null)
					baos.close();
			}
			catch (Exception e)
			{
			}
			try
			{
				if (istream != null)
					istream.close();
			}
			catch (Exception e)
			{
			}
			try
			{
				if (ostream != null)
					ostream.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	public static String retriveFromXMLTag(String xml, String tag)
	{
		String startTag = "<" + tag + ">";
		String endTag = "</" + tag + ">";
		int startIndx = xml.indexOf(startTag);
		int endIndx = xml.indexOf(endTag);

		if (startIndx == -1 || endIndx == -1)
			return "";

		int subStrBeginIndx = startIndx + startTag.length();
		int subStrEndIndx = subStrBeginIndx + (endIndx - startIndx - startTag.length());
		String str = xml.substring(subStrBeginIndx, subStrEndIndx);
		return str;
	}

	public static String getCardNameFromExtData(String data)
	{

		try
		{
			// eg. CARDTYPE=VISA i.e. 9
			String CardName = null;
			String partData = null;
			if (data.contains(","))
			{
				String parts[] = data.split("\\,");
				partData = (parts[1]);
			}
			if (partData.substring(9) != null)
				CardName = (partData.substring(9)).toLowerCase();

			return CardName;
		}
		catch (Exception e)
		{
		}
		return null;

	}
	
}
