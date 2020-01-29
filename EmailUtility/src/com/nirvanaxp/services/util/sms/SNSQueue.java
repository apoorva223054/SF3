package com.nirvanaxp.services.util.sms;

import javax.persistence.EntityManager;

import com.nirvanaxp.common.utils.SNSService;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.user.User;

/**
 * The Email sender that sends email in a parallel thread.
 * 
 * @author Apoorva
 *
 */
public class SNSQueue implements Runnable {

	EntityManager em;
	User user;
	String locationId;
	String smsMessage;
	String operationName;
	String phoneNumber;

	private static final NirvanaLogger logger = new NirvanaLogger(SNSQueue.class.getName());

	public SNSQueue() {
		
	}

	public SNSQueue(EntityManager em, User user, String locationId, String smsMessage, String operationName,
			String phoneNumber) {
		super();
		this.em = em;
		this.user = user;
		this.locationId = locationId;
		this.smsMessage = smsMessage;
		this.operationName = operationName;
		this.phoneNumber = phoneNumber;
	}

	@Override
	public void run() {
		SNSService sms = new SNSService();
		try {
			sms.sendSNSByNumber(em, user, locationId, smsMessage, operationName, phoneNumber);
		} catch (Exception e) {

			logger.severe(e);
		}
	}

}
