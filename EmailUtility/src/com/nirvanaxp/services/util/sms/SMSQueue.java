package com.nirvanaxp.services.util.sms;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.sms.SMSHistory;
import com.nirvanaxp.types.entities.sms.SMSSetting;

/**
 * The Email sender that sends email in a parallel thread.
 * @author Apoorva
 *
 */
public class SMSQueue implements Runnable
{

	private SMSSetting smsSetting;
	private SMSHistory smsHistory;
	private String requestData;

	private static final NirvanaLogger logger = new NirvanaLogger(SMSQueue.class.getName());

	public SMSQueue(String requestData, SMSSetting smsSetting, SMSHistory smsHistory)
	{
		super();
		this.smsSetting = smsSetting;
		this.smsHistory = smsHistory;
		this.requestData = requestData;
	}

	@Override
	public void run()
	{
		SendSMS sms = new SendSMS();
		try {
			sms.sendSMS(smsSetting, smsHistory);
		} catch (Exception e) {
			
			 logger.severe(e);
		}
	}

}
