package com.nirvanaxp.services.util.sms;

import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.SNSService;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.HttpRequestUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.sms.SMSHistory;
import com.nirvanaxp.types.entities.sms.SMSSetting;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.user.User;


public class SendSMS
{
	private static final NirvanaLogger logger = new NirvanaLogger(SendSMS.class.getName());
	private static final String SMS_SUBSCRIBER_BULK_INDIA="SMS_SUBSCRIBER_BULK_INDIA";
	/**
	 * This method is used to send email to the user found in database
	 * for the given customerId input.
	 * @param em
	 * @param httpRequest
	 * @param operationName
	 * @param locationId
	 * @param inputParams
	 * @param customerId
	 * @param userId
	 */
	public static void sendSMSToCustomer(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams, String customerId, String userId,String phone,String referenceId)
	{
		try
		{
			// find the user in the database for the given customerId
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, customerId);
			
			// if a user is found and that user has an email address	
			if(phone!= null){
				user = new User();
				user.setPhone(phone);
			}
			if (user != null && user.getPhone() != null)
			{
				SMSHelper smsHelper = new SMSHelper();
				SMSHistory smsHistory = new SMSHistory();
				SMSTemplate template = smsHelper.getSMSTemplateByLocationId(em, locationId, operationName);
				if (smsHelper.sendSMSBySMSTemplateIdLocationId(em, locationId, template.getId()))
				{
					String smsText = createSMSPartFromTemplate(inputParams, template.getTemplateText());
				
					smsHistory.setPhone(user.getPhone());
					smsHistory.setSmsText(smsText);
					smsHistory.setCreatedBy(userId);
					smsHistory.setLocationId(locationId);
					smsHistory.setTemplateId(0);
					smsHistory.setStatus("S");
					smsHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					smsHistory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					smsHistory.setReferenceId(referenceId);
					em.merge(smsHistory);
					
					
					logger.fine(httpRequest, "====================zero================email sending started :- ");
					SMSSetting setting = smsHelper.getSMSSettingByLocationId(em, locationId);
					SMSQueue emailQueue = new SMSQueue(logger.extractBuffer(httpRequest), setting, smsHistory);
					Thread thread = new Thread(emailQueue);
					thread.start();

					// ConfigFileReader.initSMTPCredentials(smtpCredentials,
					// action);
				}
				else
				{
					logger.severe(httpRequest, "email sending not configured for this location id :- " + locationId);
				}

			}
			else
			{
				logger.severe(httpRequest, "email-", user.getPhone(), " of the customer not present on database");
			}

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send sms to customer for reservation confirmation");
		}

	}

	/**
	 * This is used to send HTML format email to the recipient found in the
	 * EmailHistory input.
	 * @param smtpConfig
	 * @param email
	 * @return
	 * @throws Exception 
	 */
	public boolean sendSMS(SMSSetting setting, SMSHistory smsHistory) throws Exception
	{
		if(setting.getSmsSubscriber().equals(SMS_SUBSCRIBER_BULK_INDIA)){
			return sendSMSUsingBulkIndiaServiceProvider(setting.getUsername(), setting.getPassword(), smsHistory.getPhone(), smsHistory.getSmsText(), setting.getSenderName());
		}
		return false;
	}
	
	public static boolean sendSMSUsingBulkIndiaServiceProvider(String userName, String password, String phoneNumber, String message, String messageSender) throws Exception
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
				logger.severe(result);
				if (result.contains("Send Successful"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}

		}
		catch (Exception e)
		{
			
			throw e;

		}
		return false;
	}

	/**
	 * This is used to replace all the keys in the template
	 * with values that are sent in the inputParams map.
	 * The template can be email body or email subject.
	 * @param inputParams
	 * @param template
	 * @return
	 */
	private static String createSMSPartFromTemplate(Map<String, String> inputParams, String template)
	{
		String body = template;

		for (Map.Entry<String, String> inputParam : inputParams.entrySet())
		{
			String key = inputParam.getKey();
			String value = inputParam.getValue();

			body = body.replaceAll(key, value);
		}

		return body;

	}
	
	public static void sendItemCancelledSMSToCustomer(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams, String userId,String phone,String referenceId)
	{
		try
		{
			// find the user in the database for the given customerId
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, userId);
			
			// if a user is found and that user has an email address	
			if(phone!= null){
				user = new User();
				user.setPhone(phone);
			}
			if (user != null && user.getPhone() != null)
			{
				SMSHelper smsHelper = new SMSHelper();
				SMSHistory smsHistory = new SMSHistory();
				SMSTemplate template = smsHelper.getSMSTemplateByLocationId(em, locationId, operationName);
				if (true)
				{
					String smsText = createSMSPartFromTemplate(inputParams, template.getTemplateText());
				
					smsHistory.setPhone(user.getPhone());
					smsHistory.setSmsText(smsText);
					smsHistory.setCreatedBy(userId);
					smsHistory.setLocationId(locationId);
					smsHistory.setTemplateId(0);
					smsHistory.setStatus("S");
					smsHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					smsHistory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					smsHistory.setReferenceId(referenceId);
					em.merge(smsHistory);
					
					
					logger.fine(httpRequest, "====================zero================sms sending started :- ");
				 
					SNSService service = new SNSService();
					service.sendSNSByNumber(em, user, locationId, smsText, operationName, user.getPhone());

					// ConfigFileReader.initSMTPCredentials(smtpCredentials,
					// action);
				}
				 

			}
			else
			{
				logger.severe(httpRequest, "sms -", user.getPhone(), " of the customer not present on database");
			}

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send sms to customer for reservation confirmation");
		}

	}

}
