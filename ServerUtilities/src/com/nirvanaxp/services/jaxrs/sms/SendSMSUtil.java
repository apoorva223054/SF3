/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.sms;

import com.nirvanaxp.server.util.HttpRequestUtility;

// TODO: Auto-generated Javadoc
/**
 * 
 */
public class SendSMSUtil
{

	/**
	 * 
	 *
	 * @param userName 
	 * @param password 
	 * @param phoneNumber 
	 * @param message 
	 * @param messageSender 
	 * @return 
	 * @throws Exception 
	 */
	public static String sendSMSUsingBulkIndiaServiceProvider(String userName, String password, String phoneNumber, String message, String messageSender) throws Exception
	{
		// http://BULK.SMS-INDIA.IN/send.php?usr=25278&pwd=123456&ph=9920105002&text=Hello
		// India
		String url = "http://BULK.SMS-INDIA.IN/send.php?usr=" + userName + "&pwd=" + password + "&ph=" + phoneNumber + "&sndr=" + messageSender + "&text=" + message;

		try
		{
			String[] response = HttpRequestUtility.sendHttpRequest(url, HttpRequestUtility.REQUEST_METHOD_GET, null);
			if (response != null)
			{
				String result = response[0];
				if (result.contains("Send Successful"))
				{
					return Boolean.toString(true);
				}
				else
				{
					return result;
				}
			}

		}
		catch (Exception e)
		{
			
			throw e;

		}
		return Boolean.toString(false);
	}

}
