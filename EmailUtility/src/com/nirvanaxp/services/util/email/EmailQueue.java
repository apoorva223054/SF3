package com.nirvanaxp.services.util.email;

import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.email.EmailHistory;
import com.nirvanaxp.types.entities.email.SmtpConfig;

// TODO: Auto-generated Javadoc
/**
 * The Email sender that sends email in a parallel thread.
 * @author Apoorva
 *
 */
public class EmailQueue implements Runnable
{

	/** The smtp config. */
	// the configuration for SMTP
	private SmtpConfig smtpConfig;
	
	/** The email history. */
	// the email address etc. from database
	private EmailHistory emailHistory;
	
	/** The request data. */
	// the info from http request for logger
	private String requestData;

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(EmailQueue.class.getName());

	/**
	 * Instantiates a new email queue.
	 *
	 * @param requestData the request data
	 * @param smtpConfig the smtp config
	 * @param emailHistory the email history
	 */
	public EmailQueue(String requestData, SmtpConfig smtpConfig, EmailHistory emailHistory)
	{
		 
		synchronized (this) {
			this.smtpConfig = smtpConfig;
			this.emailHistory = emailHistory;
			this.requestData = requestData;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		logger.fine(requestData.toString(), "====================first================email sending started :- ");
		SendEmail sendEmail = new SendEmail();
		sendEmail.sendHtmlMail(smtpConfig, emailHistory);
	}

}
