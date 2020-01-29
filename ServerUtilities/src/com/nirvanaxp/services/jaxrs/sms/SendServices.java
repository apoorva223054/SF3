/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.sms;

import java.util.List;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;

@WebListener
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class SendServices extends AbstractNirvanaService
{
	  
	@Context
	protected HttpServletRequest httpRequest;

	private static final NirvanaLogger logger = new NirvanaLogger(SendServices.class.getName());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/sendSMS")
	public String sendSMS(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, Message message) throws Exception
	{
		// http://BULK.SMS-INDIA.IN/send.php?usr=25278&pwd=123456&ph=9920105002&text=Hello
		// India

		// http://BULK.SMS-INDIA.IN/send.php?usr=25278&pwd=123456&ph=9920105002&sendr=POSNIR&text=Hello
		// India
		if (sessionId != null)
		{
			if (message != null)
			{
				// if no exception occurs, this is a valid session id
				String messageSender = "";
				// send specific message sender name
				UserSession us = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);			
				String schema = us.getSchema_name();
				if (schema.equals("April_Rain_Aundh_62_FSR"))
				{
					messageSender = ConfigFileReader.getMesssageSender1FromFile();
				}
				else
				{
					messageSender = ConfigFileReader.getMesssageSender2FromFile();
				}
				String messageSend = message.getMessage();
				if (messageSend != null)
				{
					messageSend = messageSend.replace(" ", "%20");
				}
				if (message.getServiceProvier() != null && message.getServiceProvier().equalsIgnoreCase("BULK.SMS-INDIA.IN"))
				{
					// changed for new number format

					String phoneNumber = "";
					String isSMSSent = "";
					if (message.getPhoneNumber() != null)
					{
						phoneNumber = message.getPhoneNumber();
						phoneNumber = phoneNumber.substring(phoneNumber.indexOf("-") + 1);

						if (message.getPhoneNumber().startsWith("91-"))
						{
							isSMSSent = SendSMSUtil.sendSMSUsingBulkIndiaServiceProvider("25278", "123456", phoneNumber, messageSend, messageSender);
						}
						else
						{
							isSMSSent = "We do support messaging in this number yet.";
						}
					}

					return isSMSSent;
				}
				else
				{
					return "Service provider cannot be null. Also we only support BULK.SMS-INDIA.IN today";
				}

			}
			else
			{
				return "message cannot be null or empty";
			}
		}
		else
		{
			return "sessionId name cannot be null or empty";
		}

	}
	
	
	

	/**
	 * @param attachments
	 * @param httpRequest
	 * @param locationId
	 * @param gatewayName
	 * @param date
	 * @param filename
	 * @param orderPaymentDetailId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/fileupload/{locationId}/{gatewayName}/{date}/{filename}/{orderPaymentDetailId}")
	public String uploadFile(List<Attachment> attachments, @Context HttpServletRequest httpRequest, @PathParam("locationId") String locationId, @PathParam("gatewayName") String gatewayName,
			@PathParam("date") String date, @PathParam("filename") String filename, @PathParam("orderPaymentDetailId") int orderPaymentDetailId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{

		UploadFile uploadFile = new UploadFile();
		String response = uploadFile.fileUpload(httpRequest, locationId, gatewayName, date,filename, orderPaymentDetailId, sessionId,attachments);
		return response;
	}
	
	
	
	
	

	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}
}
