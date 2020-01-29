package com.nirvanaxp.services.util.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.util.htmltopdf.CSimpleConversion;
import com.nirvanaxp.types.entities.email.EmailHistory;
import com.nirvanaxp.types.entities.email.EmailTemplate;
import com.nirvanaxp.types.entities.email.SmtpConfig;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.user.User;
// TODO: Auto-generated Javadoc
/**
 * The Class SendEmail.
 */
public class SendEmail
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(SendEmail.class.getName());

	/**
	 * This method is used to send email to the user found in database for the
	 * given customerId input.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param operationName the operation name
	 * @param locationId the location id
	 * @param inputParams the input params
	 * @param customerId the customer id
	 * @param userId the user id
	 * @param emailAddress the email address
	 * @param referenceId the reference id
	 */
	public static void sendEmailToCustomer(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams, String customerId, String userId,
			String emailAddress, String referenceId, String ccEmail)
	{
		try
		{
			if (emailAddress == null)
			{
				// find the user in the database for the given customerId
				User user = (User) new CommonMethods().getObjectById("User", em,User.class, customerId);
				// if a user is found and that user has an email address
				if (user != null && emailAddress == null)
				{
					emailAddress = user.getEmail();
				}

			}

			if (emailAddress != null)
			{
				
				EmailHelper emailHelper = new EmailHelper();
				EmailHistory email = new EmailHistory();
				EmailTemplate template = emailHelper.getEmailTemplateByLocationId(em, locationId, operationName);
				if (emailHelper.sendEmailByEmailTemplateIdLocationId(em, locationId, template.getId()))
				{
					String emailBody = createEmailPartFromTemplate(inputParams, template.getEmailBody());
					String emailSubject = createEmailPartFromTemplate(inputParams, template.getEmailSubject());

					Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
					if(EmailTemplateKeys.QUOTE_RECEIVED_EMAIL_STRING.equals(template.getOperationName())){
						emailSubject =  location.getName()+" - "+inputParams.get(EmailTemplateKeys.EMAIL_SUBJECT);
					}else{
						emailSubject = emailSubject + " of " + location.getName();
					}
					 

					if(ccEmail != null)
					{
						email.setCcEmail(ccEmail);
					}
					
					email.setToEmail(emailAddress);
					email.setEmailBody(emailBody);
					email.setCreatedBy(userId);
					email.setEmailTemplateId(template.getId());
					email.setLocationId(locationId);
					email.setStatus("S");
					email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					email.setEmailSubject(emailSubject);
					email.setReferenceId(referenceId);
					email.setUpdatedBy(email.getCreatedBy());
					em.merge(email);

					SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
					EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
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
				logger.severe(httpRequest, "email-", emailAddress, " of the customer not present on database");
			}

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send email to customer for reservation confirmation");
		}

	}

	/**
	 * This method is used to send email to the user found in database for the
	 * given customerId input.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param operationName the operation name
	 * @param locationId the location id
	 * @param inputParams the input params
	 * @param customerId the customer id
	 * @param userId the user id
	 * @param emailAddress the email address
	 * @param referenceId the reference id
	 * @param isReservation the is reservation
	 */
	public static void sendEmailToCustomerOfReservation(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams, String customerId,
			String userId, String emailAddress, String referenceId, boolean isReservation)
	{
		try
		{
			// find the user in the database for the given customerId
			User user = (User) new CommonMethods().getObjectById("User", em,User.class , customerId);
			// if a user is found and that user has an email address
			if (user != null && emailAddress != null)
			{
				user.setEmail(emailAddress);
			}
			if (user == null && emailAddress != null)
			{
				user = new User();
				user.setEmail(emailAddress);
			}
			if (user != null && user.getEmail() != null)
			{
				EmailHelper emailHelper = new EmailHelper();
				EmailHistory email = new EmailHistory();
				// fetching email template from db
				EmailTemplate template = emailHelper.getEmailTemplateByLocationId(em, locationId, operationName);
				// todo check :- checking of email config
				if (emailHelper.sendEmailByEmailTemplateIdLocationId(em, locationId, template.getId()))
				{
					String emailBody = createEmailPartFromTemplate(inputParams, template.getEmailBody());
					String emailSubject = createEmailPartFromTemplate(inputParams, template.getEmailSubject());

					if (!isReservation)
					{
						emailBody = emailBody.replaceAll("reservation ", "waitlist ");
						emailBody = emailBody.replaceAll("Reservation ", "Waitlist ");
						emailSubject = emailSubject.replaceAll("reservation ", "waitlist ");
						emailSubject = emailSubject.replaceAll("Reservation ", "Waitlist ");
					}
					Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;

					emailSubject = emailSubject + " of " + location.getName();

					email.setToEmail(user.getEmail());
					email.setEmailBody(emailBody);
					email.setCreatedBy(userId);
					email.setEmailTemplateId(template.getId());
					email.setLocationId(locationId);
					email.setStatus("S");
					email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					email.setEmailSubject(emailSubject);
					email.setReferenceId(referenceId);
					email.setUpdatedBy(email.getCreatedBy());
					em.merge(email);
					// fetching smtp details
					SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
					// creating queue for mail sending and thread for
					// independent execution of email
					EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
					Thread thread = new Thread(emailQueue);
					thread.start();

				}
				else
				{
					logger.severe(httpRequest, "email sending not configured for this location id :- " + locationId);
				}

			}
			else
			{
				logger.severe(httpRequest, "email-", user.getEmail(), " of the customer not present on database");
			}

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send email to customer for reservation confirmation");
		}

	}

	/**
	 * This is used to send HTML format email to the recipient found in the
	 * EmailHistory input.
	 *
	 * @param smtpConfig the smtp config
	 * @param email the email
	 * @return true, if successful
	 */
	public boolean sendHtmlMail(SmtpConfig smtpConfig, EmailHistory email)
	{
		// Get the session object
		Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.host","localhost");
		properties.setProperty("mail.smtp.host", smtpConfig.getSmtpHost());
		properties.put("mail.smtp.port", smtpConfig.getSmtpPort());
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		final String username = smtpConfig.getSmtpUsername();
		final String pass = smtpConfig.getSmtpPassword();

		Authenticator auth = new Authenticator()
		{
			public PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(username, pass);
			}
		};

		Session session = Session.getInstance(properties, auth);

		// compose the message
		try
		{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(smtpConfig.getSenderEmail()));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getToEmail()));
			message.setSentDate(new Date(new TimezoneTime().getGMTTimeInMilis()));
			message.setSubject(email.getEmailSubject());

			
			if(email.getCcEmail() != null && !email.getCcEmail().isEmpty())
			{
				
				String[] ccArray = email.getCcEmail().split(",");
				InternetAddress[] ccAddress = new InternetAddress[ccArray.length];
	            
	            for( int i = 0; i < ccArray.length; i++ ) {
	                ccAddress[i] = new InternetAddress(ccArray[i]);
	            }
				            
				message.addRecipients(Message.RecipientType.CC, ccAddress);
					
					
			}
			
			
			message.setContent(email.getEmailBody(), "text/html");

			// attachment to mail if exist file
			if (email.getFile() != null)
			{
				MimeBodyPart messageBody = new MimeBodyPart();
				messageBody.setContent(email.getEmailBody(), "text/html");

				Multipart multipart = new MimeMultipart();
				MimeBodyPart messageAttachmentPart = new MimeBodyPart();
				String filename = email.getFile().getName();
				DataSource source = new FileDataSource(email.getFile());
				messageAttachmentPart.setDataHandler(new DataHandler(source));
				messageAttachmentPart.setFileName(filename);
				multipart.addBodyPart(messageBody);
				multipart.addBodyPart(messageAttachmentPart);
				message.setContent(multipart);
			}

			Transport transport = null;
			try
			{
				transport = session.getTransport();
				transport.connect(smtpConfig.getSmtpHost(), username, pass);
				
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			}
			finally
			{
				if (transport != null)
				{

					transport.close();
				}

			}

			return true;

		}
		catch (MessagingException mex)
		{
			logger.severe(mex, "Failed to send email");
		}catch (Exception e) {
			
			logger.severe(e);
		}

		return false;
	}

	/**
	 * This is used to replace all the keys in the template with values that are
	 * sent in the inputParams map. The template can be email body or email
	 * subject.
	 *
	 * @param inputParams the input params
	 * @param template the template
	 * @return the string
	 */
	private static String createEmailPartFromTemplate(Map<String, String> inputParams, String template)
	{
		String body = template;

		for (Map.Entry<String, String> inputParam : inputParams.entrySet())
		{
			String key = inputParam.getKey();
			String value = inputParam.getValue();
			value = value.replace("$", "\\$");
			body = body.replaceAll(key, value);
		}

		return body;

	}

	/**
	 * This method is used to send email to the user found in database for the
	 * given customerId input.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param operationName the operation name
	 * @param locationId the location id
	 * @param inputParams the input params
	 * @param referenceId the reference id
	 * @param fileName the file name
	 * @param supplier the supplier
	 */
	public static void sendEmailToRequestOrderConfirmation(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams, String referenceId,
			String fileName, Location supplier, int isPoFor, String grmNo)
	{
		try
		{
			EmailHelper emailHelper = new EmailHelper();
			EmailHistory email = new EmailHistory();

			EmailTemplate template = emailHelper.getEmailTemplateByLocationId(em, locationId, operationName);
			// creating pdf for email
			CSimpleConversion conversion = new CSimpleConversion();
			File pDFFile = conversion.convert(fileName, inputParams.get(EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION_STRING), inputParams.get(EmailTemplateKeys.REQUEST_ORDER_FOOTER_STRING));
			
//			File pDFFile = Html2Pdf.convertToPdf(fileName, inputParams.get(EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION_STRING));
			
			Location foundLocation = null;
			// todo -ap- modular coding
			if (locationId != null)
			{
				String queryString = "select l from Location l where " + "l.id ='" + locationId + "' and (l.locationsId = '0' or l.locationsId is null) and l.locationsTypeId = '1'";
				TypedQuery<Location> query = em.createQuery(queryString, Location.class);
				foundLocation = query.getSingleResult();
			}
			// todo - ap- modular coding requires
			if (supplier != null && supplier.getEmail() != null && !supplier.getEmail().isEmpty())
			{
				String emailBody = inputParams.get(EmailTemplateKeys.REQUEST_ORDER_BODY_STRING);
				String emailSubject = "";
				if(isPoFor == 3)
				{
					emailSubject = foundLocation.getName() + " " + template.getEmailSubject() + " PO No. " + referenceId + " (Cancelled)";	
				}else if(isPoFor == 1)
				{
					emailSubject = foundLocation.getName() + " Allotment Order - " + grmNo;	
				}else
				{
					emailSubject = foundLocation.getName() + " " + template.getEmailSubject() + " PO No. " + referenceId;
				}
				

				email.setToEmail(supplier.getEmail());
				email.setEmailBody(emailBody);
				email.setCreatedBy(""+supplier.getId());
				email.setEmailTemplateId(template.getId());
				email.setLocationId(locationId);
				email.setStatus("S");
				email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				email.setEmailSubject(emailSubject);
				email.setReferenceId(referenceId);
				if (pDFFile != null)
				{
					email.setFile(pDFFile);
				}
				email.setUpdatedBy(email.getCreatedBy());
				em.merge(email);
				// email sending code and queue creations
				SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
				EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
				Thread thread = new Thread(emailQueue);
				thread.start();

			}
			// todo :- modular coding requires
			List<User> users = emailHelper.sendEmailByEmailTemplateIdLocationIdRequestOrder(em, locationId, template.getId());
			if (users != null && users.size() > 0)
			{
				for (User user : users)
				{
					if (user.getEmail() != null && !user.getEmail().isEmpty())
					{
						String emailBody = inputParams.get(EmailTemplateKeys.REQUEST_ORDER_BODY_STRING);
						String emailSubject = foundLocation.getName() + " " + template.getEmailSubject();

						email.setToEmail(user.getEmail());
						email.setEmailBody(emailBody);
						email.setCreatedBy(user.getId());
						email.setEmailTemplateId(template.getId());
						email.setLocationId(locationId);
						email.setStatus("S");
						email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
						email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						email.setEmailSubject(emailSubject);
						email.setReferenceId(referenceId);
						if (pDFFile != null)
						{
							email.setFile(pDFFile);
						}
						email.setUpdatedBy(email.getCreatedBy());
						em.merge(email);

						SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
						EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
						Thread thread = new Thread(emailQueue);
						thread.start();
					}

				}

			}
			else
			{
				logger.severe(httpRequest, "email sending not configured for this location id :- " + locationId);
			}

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send email to customer for Request Order Confirmation");
		}

	}

	/**
	 * Send EOD settledment mail to user.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param operationName the operation name
	 * @param locationId the location id
	 * @param inputParams the input params
	 */
	public static void sendEODSettledmentMailToUser(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams)
	{
		try
		{
			EmailHelper emailHelper = new EmailHelper();

			EmailTemplate template = emailHelper.getEmailTemplateByLocationId(em, locationId, operationName);
			// todo :- modular coding required
			Location foundLocation = null;
			if (locationId != null)
			{
				String queryString = "select l from Location l where " + "l.id ='" + locationId + "' and l.locationsId = '0' and l.locationsTypeId = '1'";
				TypedQuery<Location> query = em.createQuery(queryString, Location.class);
				foundLocation = query.getSingleResult();
			}
			// sending mail directly to any user, i.e. why we created dummy user
			// with id 99999
			List<User> users = null;
			if (inputParams.get(EmailTemplateKeys.EMAIL_STRING) != null && (!inputParams.get(EmailTemplateKeys.EMAIL_STRING).equalsIgnoreCase("null")))
			{
				users = new ArrayList<User>();
				User user = new User();
				user.setEmail(inputParams.get(EmailTemplateKeys.EMAIL_STRING));
				user.setId(""+99999);
				users.add(user);
			}
			else
			{
				// sending mail to all user according to their role
				// in below code we are searching for user according to role and
				// their function and after that we are sending eod mail to all
				// the users
				users = emailHelper.sendEmailByFunctionByLocationIdWithQuery(em,locationId,"EOD Settlement Email");
			}
			if (users != null && users.size() > 0)
			{// sendding mail to all the user
				for (User user : users)
				{
					EmailHistory email = new EmailHistory();
					synchronized (user)
					{
						if (user.getEmail() != null && !user.getEmail().isEmpty())
						{
							String emailBody = inputParams.get(EmailTemplateKeys.EOD_SETTLEDMENT_BODY_STRING);
							String emailSubject = foundLocation.getName() + " " + template.getEmailSubject();

							email.setToEmail(user.getEmail());
							email.setEmailBody(emailBody);
							email.setCreatedBy(user.getId());
							email.setEmailTemplateId(template.getId());
							email.setLocationId(locationId);
							email.setStatus("S");
							email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							email.setEmailSubject(emailSubject);
							// email.setReferenceId(referenceId);
							email.setUpdatedBy(email.getCreatedBy());
							email = em.merge(email);

							SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
							EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
							Thread thread = new Thread(emailQueue);
							thread.start();
						}
					}

				}

			}
			else
			{
				logger.severe(httpRequest, "email sending not configured for this location id :- " + locationId);
			}

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send email to customer for Request Order Confirmation");
		}

	}

	/**
	 * This method is used to send email to the user found in database for the
	 * given customerId input.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param operationName the operation name
	 * @param locationId the location id
	 * @param inputParams the input params
	 * @param referenceId the reference id
	 * @param fileName the file name
	 * @param emailAddress the email address
	 */
	public static void sendEmailByEmailAddrForAddUpdateRequestOrder(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams,
			String referenceId, String fileName, String emailAddress)
	{
		try
		{
			EmailHelper emailHelper = new EmailHelper();
			EmailHistory email = new EmailHistory();

			EmailTemplate template = emailHelper.getEmailTemplateByLocationId(em, locationId, operationName);

			// pdf generation for email
			CSimpleConversion conversion = new CSimpleConversion();
			File pDFFile = conversion.convert(fileName, inputParams.get(EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION_STRING), inputParams.get(EmailTemplateKeys.REQUEST_ORDER_FOOTER_STRING));

//			File pDFFile = Html2Pdf.convertToPdf(fileName, inputParams.get(EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION_STRING));
			Location foundLocation = null;
			if (locationId != null)
			{
				String queryString = "select l from Location l where " + "l.id ='" + locationId + "' and l.locationsId = '0' and l.locationsTypeId = '1'";
				TypedQuery<Location> query = em.createQuery(queryString, Location.class);
				foundLocation = query.getSingleResult();
			}

			String emailBody = inputParams.get(EmailTemplateKeys.REQUEST_ORDER_BODY_STRING);
			String emailSubject = foundLocation.getName() + " " + template.getEmailSubject() + " PO No. " + referenceId;

			email.setToEmail(emailAddress);
			email.setEmailBody(emailBody);
			// email.setCreatedBy(user.getId());
			email.setEmailTemplateId(template.getId());
			email.setLocationId(locationId);
			email.setStatus("S");
			email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
			email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			email.setEmailSubject(emailSubject);
			email.setReferenceId(referenceId);
			email.setUpdatedBy(email.getCreatedBy());
			if (pDFFile != null)
			{
				email.setFile(pDFFile);
			}
			email.setUpdatedBy(email.getCreatedBy());
			em.merge(email);

			SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
			EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
			Thread thread = new Thread(emailQueue);
			thread.start();

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send email to customer for Request Order Confirmation");
		}

	}

	/**
	 * Gets the PDF for request order confirmation.
	 *
	 * @param em the em
	 * @param httpRequest the http request
	 * @param operationName the operation name
	 * @param locationId the location id
	 * @param inputParams the input params
	 * @param referenceId the reference id
	 * @param fileName the file name
	 * @return the PDF for request order confirmation
	 */
	public static String getPDFForRequestOrderConfirmation(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams, int referenceId,
			String fileName)
	{
		try
		{
			
			CSimpleConversion conversion = new CSimpleConversion();
			File pDFFile = conversion.convert(fileName, inputParams.get(EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION_STRING), inputParams.get(EmailTemplateKeys.REQUEST_ORDER_FOOTER_STRING));
			
			FileInputStream fis = null;
			String fileString = "";
			
			try
			{
				fis = new FileInputStream(pDFFile);
				int content;
				while ((content = fis.read()) != -1)
				{
					// convert to char and display it
					fileString += (char) content;
				}

			}
			catch (IOException e)
			{
				logger.severe(e);

			}
			finally
			{
				try
				{
					if (fis != null)
						fis.close();
				}
				catch (IOException ex)
				{
					logger.severe(ex);
				}
			}

			return fileString;

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send email to customer for Request Order Confirmation");
		}

		return null;

	}
	public static void sendEODTipSettledmentMailToUser(EntityManager em, HttpServletRequest httpRequest, String operationName, String locationId, Map<String, String> inputParams,String dateTime)
	{
		try
		{
			EmailHelper emailHelper = new EmailHelper();

			EmailTemplate template = emailHelper.getEmailTemplateByLocationId(em, locationId, operationName);
			// todo :- modular coding required
			Location foundLocation = null;
			if (locationId != null)
			{
				String queryString = "select l from Location l where " + "l.id ='" + locationId + "' and l.locationsId = '0' and l.locationsTypeId = '1'";
				TypedQuery<Location> query = em.createQuery(queryString, Location.class);
				foundLocation = query.getSingleResult();
			}
			// sending mail directly to any user, i.e. why we created dummy user
			// with id 99999
			List<User> users = null;
			if (inputParams.get(EmailTemplateKeys.EMAIL_STRING) != null && (!inputParams.get(EmailTemplateKeys.EMAIL_STRING).equalsIgnoreCase("null")))
			{
				users = new ArrayList<User>();
				User user = new User();
				user.setEmail(inputParams.get(EmailTemplateKeys.EMAIL_STRING));
				user.setId(""+99999);
				users.add(user);
			}
			else
			{
				// sending mail to all user according to their role
				// in below code we are searching for user according to role and
				// their function and after that we are sending eod mail to all
				// the users
			//	users = emailHelper.sendEmailByEmailTemplateIdLocationIdEODTipSettlement(em, locationId, template.getId());
				users = emailHelper.sendEmailByFunctionByLocationIdWithQuery(em, locationId, "Tip Settlement");
				
			}
			if (users != null && users.size() > 0)
			{// sendding mail to all the user
				for (User user : users)
				{
					EmailHistory email = new EmailHistory();
					synchronized (user)
					{
						if (user.getEmail() != null && !user.getEmail().isEmpty())
						{
							String emailBody = inputParams.get(EmailTemplateKeys.EOD_TIP_SETTLEDMENT_BODY_STRING);
							String emailSubject = foundLocation.getName() + " " + template.getEmailSubject() + " " + dateTime;

							email.setToEmail(user.getEmail());
							email.setEmailBody(emailBody);
							email.setCreatedBy(user.getId());
							email.setEmailTemplateId(template.getId());
							email.setLocationId(locationId);
							email.setStatus("S");
							email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							email.setEmailSubject(emailSubject);
							// email.setReferenceId(referenceId);
							email.setUpdatedBy(email.getCreatedBy());
							email = em.merge(email);

							SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
							EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
							Thread thread = new Thread(emailQueue);
							thread.start();
						}
					}

				}

			}
			else
			{
				logger.severe(httpRequest, "email sending not configured for this location id :- " + locationId);
			}

		}

		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Could not send email to customer for "+EmailTemplateKeys.EOD_TIP_SETTLEDMENT_BODY_STRING);
		}

	}
	public void sendHEBBatchCloseEmailToSupport(EntityManager em, HttpServletRequest httpRequest, String emailBody, String locationId,
			String emailAddress,String emailSubject)
	{
		try
		{
			 
			if (emailAddress != null)
			{
				EmailHelper emailHelper = new EmailHelper();
				EmailHistory email = new EmailHistory();
			 
				 	 
				

				email.setToEmail(emailAddress);
				email.setEmailBody(emailBody);
				email.setCreatedBy("21");
				email.setEmailTemplateId(0);
				email.setLocationId(locationId);
				email.setStatus("S");
				email.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				email.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				email.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				email.setEmailSubject(emailSubject);
				email.setReferenceId(null);
				email.setUpdatedBy(email.getCreatedBy());
				em.merge(email);

				SmtpConfig smtpConfig = emailHelper.getSMTPConfigByLocationId(em, locationId);
				EmailQueue emailQueue = new EmailQueue(logger.extractBuffer(httpRequest), smtpConfig, email);
				Thread thread = new Thread(emailQueue);
				thread.start();
	 
			}
			else
			{
				logger.severe("emailAddress is null");
			}

		}

		catch (Exception e)
		{
			logger.severe(e);
		}

	}
}
