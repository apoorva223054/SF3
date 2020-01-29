package com.nirvanaxp.services.util.email;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.mail.SendMail;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.reservation.Reservation;


// TODO: Auto-generated Javadoc
/**
 * The Class EmailTemplateKeys.
 */
public class EmailTemplateKeys {
	
	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(SendMail.class.getName());
	
	/** The Constant RESERVATION_CREATED. */
	public final static String RESERVATION_CREATED = "RESERVATION_CREATED";
	
	/** The Constant RESERVATION_UPDATED. */
	public final static String RESERVATION_UPDATED = "RESERVATION_UPDATED";
	
	/** The Constant RESERVATION_CANCELLED. */
	public final static String RESERVATION_CANCELLED = "RESERVATION_CANCELLED";
	
	/** The Constant ORDER_CONFIRMATION. */
	public final static String ORDER_CONFIRMATION= "ORDER_CONFIRMATION";
	
	/** The Constant ORDER_RECEIVED. */
	public final static String ORDER_RECEIVED= "ORDER_RECEIVED";
	
	/** The Constant ORDER_PAYMENT_CONFIRMATION. */
	public final static String ORDER_PAYMENT_CONFIRMATION="ORDER_PAYMENT_CONFIRMATION";
	
	/** The Constant REQUEST_ORDER_CONFIRMATION. */
	public final static String REQUEST_ORDER_CONFIRMATION= "REQUEST_ORDER_CONFIRMATION";
			
	/** The Constant CUSTOMER_FULL_NAME. */
	// Reservation required constant
	public final static String CUSTOMER_FULL_NAME= "CUSTOMER_FULL_NAME";
	
	/** The Constant CUSTOMER_FIRST_NAME. */
	public final static String CUSTOMER_FIRST_NAME= "CUSTOMER_FIRST_NAME";
	
	/** The Constant RESERVATION_DATE. */
	public final static String RESERVATION_DATE= "RESERVATION_DATE";
	
	/** The Constant RESERVATION_TIME. */
	public final static String RESERVATION_TIME= "RESERVATION_TIME";
	
	/** The Constant GUEST_COUNT. */
	public final static String GUEST_COUNT= "GUEST_COUNT";
	
	/** The Constant CANCEL_RESERVATION_URL. */
	public final static String CANCEL_RESERVATION_URL= "CANCEL_RESERVATION_URL";
	
	/** The Constant BUSINESS_NAME. */
	public final static String BUSINESS_NAME= "BUSINESS_NAME";
	
	/** The Constant BUSINESS_ADDRESS. */
	public final static String BUSINESS_ADDRESS= "BUSINESS_ADDRESS";
	
	/** The Constant BUSINESS_WEBSITE_URL. */
	public final static String BUSINESS_WEBSITE_URL= "BUSINESS_WEBSITE_URL";
	
	/** The Constant ORDER_CONFIRMATION_EMAIL_STRING. */
	//Order confirmation 
	private final static String ORDER_CONFIRMATION_EMAIL_STRING= "ORDER_CONFIRMATION_EMAIL_STRING";
	
	/** The Constant ORDER_RECEIVED_EMAIL_STRING. */
	private final static String ORDER_RECEIVED_EMAIL_STRING= "ORDER_RECEIVED_EMAIL_STRING";
	
	/** The Constant ORDER_TRANSACTION_EMAIL_STRING. */
	private final static String ORDER_TRANSACTION_EMAIL_STRING= "ORDER_TRANSACTION_EMAIL_STRING";
	
	/** The Constant ORDER_ID_STRING. */
	private final static String ORDER_ID_STRING= "ORDER_ID";
	
	/** The Constant REQUEST_ORDER_CONFIRMATION_STRING. */
	public final static String REQUEST_ORDER_CONFIRMATION_STRING= "REQUEST_ORDER_CONFIRMATION_STRING";
	
	/** The Constant REQUEST_ORDER_BODY_STRING. */
	public final static String REQUEST_ORDER_BODY_STRING= "REQUEST_ORDER_BODY_STRING";
	
	/** The Constant REQUEST_ORDER_FOOTER_STRING. */
	public final static String REQUEST_ORDER_FOOTER_STRING= "REQUEST_ORDER_FOOTER_STRING";
	
	/** The Constant EOD_SETTLEDMENT_CONFIRMATION. */
	public final static String EOD_SETTLEDMENT_CONFIRMATION= "EOD_SETTLEDMENT_CONFIRMATION";
	
	/** The Constant EOD_SETTLEDMENT_STRING. */
	public final static String EOD_SETTLEDMENT_STRING= "EOD_SETTLEDMENT_STRING";
	
	/** The Constant EOD_SETTLEDMENT_BODY_STRING. */
	public final static String EOD_SETTLEDMENT_BODY_STRING= "EOD_SETTLEDMENT_BODY_STRING";
	
	/** The Constant EOD_SETTLEDMENT_BODY_STRING. */
	public final static String EOD_TIP_SETTLEDMENT_BODY_STRING= "EOD_TIP_SETTLEMENT_BODY_STRING";
	
	/** The Constant EMAIL_STRING. */
	public final static String EMAIL_STRING= "EMAIL_STRING";
	
	public final static String QUOTE_RECEIVED_EMAIL_STRING= "QUOTE_RECEIVED_EMAIL_STRING";
	
	public final static String EMAIL_SUBJECT= "EMAIL_SUBJECT";
	
	
	/**
	 * Send reservation email to customer.
	 *
	 * @param httpRequest the http request
	 * @param EntityManager the em
	 * @param reservation the reservation
	 * @param location the l
	 * @param webSiteUrl the web site url
	 * @param operationName the operation name
	 * @param isReservation the is reservation
	 */
	public static void sendReservationEmailToCustomer(HttpServletRequest httpRequest,EntityManager em,
			Reservation reservation, Location l, String webSiteUrl,String operationName, boolean isReservation) {

		try {
			EmailHelper emailHelper = new EmailHelper();
			String locationName = "";
			if (reservation.getEmail() != null && reservation.getEmail().length() > 0) {
				if (l != null) {
					locationName = l.getName();
				}
				int partySize = reservation.getPartySize();
				String dateString = emailHelper.getDateForEmail(reservation);
				String time = emailHelper.getTimeForEmail(reservation);
				
				Map<String, String> inputParams = new HashMap<String, String>();
				inputParams.put(EmailTemplateKeys.BUSINESS_NAME, locationName);
				inputParams.put(EmailTemplateKeys.CUSTOMER_FIRST_NAME, reservation.getFirstName());
				inputParams.put(EmailTemplateKeys.CUSTOMER_FULL_NAME,reservation.getFirstName() + " " + reservation.getLastName());
				inputParams.put(EmailTemplateKeys.RESERVATION_DATE, dateString);
				inputParams.put(EmailTemplateKeys.RESERVATION_TIME, time);
				inputParams.put(EmailTemplateKeys.GUEST_COUNT, partySize+"");
				
				String businessString = "";
				if (l.getAddress() != null) {
					if (l.getAddress().getAddress1() != null) {
						businessString = businessString + l.getAddress().getAddress1() + " ";
					}

					if (l.getAddress().getAddress2() != null) {
						businessString = businessString + l.getAddress().getAddress2() + " ";
					}

					if (l.getAddress().getCity() != null) {
						businessString = businessString + l.getAddress().getCity() + " ";
					}

					if (l.getAddress().getZip() != null) {
						businessString = businessString + l.getAddress().getZip() + " ";
					}

					if (l.getAddress().getPhone() != null) {
						businessString = "<br>" + businessString + l.getAddress().getPhone();
					}
				}
				inputParams.put(EmailTemplateKeys.BUSINESS_ADDRESS, businessString);
				if (l.getWebsite() != null) {
					if (l.getWebsite().startsWith("http://") || l.getWebsite().startsWith("https://")) {
						inputParams.put(EmailTemplateKeys.BUSINESS_WEBSITE_URL, l.getWebsite());
					}
					else {
						inputParams.put(EmailTemplateKeys.BUSINESS_WEBSITE_URL,  "http://" + l.getWebsite());
					}
				}
				else {
					inputParams.put(EmailTemplateKeys.BUSINESS_WEBSITE_URL,   "#");
				}

				if (webSiteUrl != null) {
					if(isReservation)
					{
						if (webSiteUrl.startsWith("http://") || webSiteUrl.startsWith("https://")) {
							//inputParams.put(EmailTemplateKeys.CANCEL_RESERVATION_URL,   webSiteUrl + reservation.getId());
							inputParams.put(EmailTemplateKeys.CANCEL_RESERVATION_URL, "<br /><br /><a href=\""+webSiteUrl + reservation.getId()+"\">Change or cancel this reservation &gt;</a><br />");
						}
						else {
							inputParams.put(EmailTemplateKeys.CANCEL_RESERVATION_URL, "<br /><br /><a href=\"http://" +webSiteUrl + reservation.getId()+"\">Change or cancel this reservation &gt;</a><br />");
						}	
					}else
					{
						if (webSiteUrl.startsWith("http://") || webSiteUrl.startsWith("https://")) {
							//inputParams.put(EmailTemplateKeys.CANCEL_RESERVATION_URL,   webSiteUrl + reservation.getId());
							inputParams.put(EmailTemplateKeys.CANCEL_RESERVATION_URL, "<br /><br /><a href=\""+webSiteUrl + reservation.getId()+"\">Change or cancel this waitlist &gt;</a><br />");
						}
						else {
							inputParams.put(EmailTemplateKeys.CANCEL_RESERVATION_URL, "<br /><br /><a href=\"http://" +webSiteUrl + reservation.getId()+"\">Change or cancel this waitlist &gt;</a><br />");
						}
					}
					
				}else{
					inputParams.put(EmailTemplateKeys.CANCEL_RESERVATION_URL, "");
				}
				// TODO :- how to handle exception
				SendEmail.sendEmailToCustomerOfReservation(em, httpRequest, operationName, l.getId(), inputParams, reservation.getUsersId(), reservation.getUpdatedBy(),reservation.getEmail(),reservation.getId(), isReservation);

			}
		}
		catch (Exception e) {
			// TODO :- DEfine constant for error msg
			logger.severe(httpRequest, e, "Could not send email to customer for reservation confirmation");
		}
		
	}

	
	/**
	 * Send reservation cancelled email to customer.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param reservation the reservation
	 * @param l the l
	 * @param webSiteUrl the web site url
	 * @param operationName the operation name
	 */
	public static void  sendReservationCancelledEmailToCustomer(HttpServletRequest httpRequest,EntityManager em,
			Reservation reservation, Location l, String webSiteUrl,String operationName) {

		try {
			EmailHelper emailHelper = new EmailHelper();
			String locationName = "";
			if (reservation.getEmail() != null && reservation.getEmail().length() > 0) {
				if (l != null) {
					locationName = l.getName();
				}
				int partySize = reservation.getPartySize();
				String dateString = emailHelper.getDateForEmail(reservation);
				String time = emailHelper.getTimeForEmail(reservation);
				
				Map<String, String> inputParams = new HashMap<String, String>();
				inputParams.put(EmailTemplateKeys.BUSINESS_NAME, locationName);
				inputParams.put(EmailTemplateKeys.CUSTOMER_FIRST_NAME, reservation.getFirstName());
				inputParams.put(EmailTemplateKeys.CUSTOMER_FULL_NAME,reservation.getFirstName() + " " + reservation.getLastName());
				inputParams.put(EmailTemplateKeys.RESERVATION_DATE, dateString);
				inputParams.put(EmailTemplateKeys.RESERVATION_TIME, time);
				inputParams.put(EmailTemplateKeys.GUEST_COUNT, partySize+"");
				
				String businessString = "";
				if (l.getAddress() != null) {
					if (l.getAddress().getAddress1() != null) {
						businessString = businessString + l.getAddress().getAddress1() + " ";
					}

					if (l.getAddress().getAddress2() != null) {
						businessString = businessString + l.getAddress().getAddress2() + " ";
					}

					if (l.getAddress().getCity() != null) {
						businessString = businessString + l.getAddress().getCity() + " ";
					}

					if (l.getAddress().getZip() != null) {
						businessString = businessString + l.getAddress().getZip() + " ";
					}

					if (l.getAddress().getPhone() != null) {
						businessString = "<br>" + businessString + l.getAddress().getPhone();
					}
				}
				inputParams.put(EmailTemplateKeys.BUSINESS_ADDRESS, businessString);
				if (l.getWebsite() != null) {
					if (l.getWebsite().startsWith("http://") || l.getWebsite().startsWith("https://")) {
						inputParams.put(EmailTemplateKeys.BUSINESS_WEBSITE_URL, l.getWebsite());
					}
					else {
						inputParams.put(EmailTemplateKeys.BUSINESS_WEBSITE_URL,  "http://" + l.getWebsite());
					}
				}
				else {
					inputParams.put(EmailTemplateKeys.BUSINESS_WEBSITE_URL,   "#");
				}

				
				SendEmail.sendEmailToCustomer(em, httpRequest, operationName, l.getId(), inputParams, reservation.getUsersId(), reservation.getUpdatedBy(),null,reservation.getId(),null);

			}
		}
		catch (Exception e) {
			// TODO :- DEfine constant for error msg
			logger.severe(httpRequest, e, "Could not send email to customer for reservation confirmation");
		}
		
	}

	/**
	 * Send order confirmation email to customer.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param userId the user id
	 * @param updatedBy the updated by
	 * @param data the data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param emailAddress the email address
	 * @throws Exception the exception
	 */
	public static void sendOrderConfirmationEmailToCustomer(HttpServletRequest httpRequest,EntityManager em,String locationId,String userId,String updatedBy,
			String data,String operationName,String orderId,String emailAddress) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(ORDER_CONFIRMATION_EMAIL_STRING, data);
			inputParams.put(ORDER_ID_STRING, orderId+"");
			SendEmail.sendEmailToCustomer(em, httpRequest, operationName, locationId, inputParams,userId,updatedBy,emailAddress,orderId,null);
	}
	
	/**
	 * Send request order confirmation email to user.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param updatedBy the updated by
	 * @param pDFData the DF data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param filename the filename
	 * @param emailBody the email body
	 * @param emailFooter the email footer
	 * @param supplier the supplier
	 * @throws Exception the exception
	 */
	public static void sendRequestOrderConfirmationEmailToUser(HttpServletRequest httpRequest,EntityManager em,
			String locationId,String updatedBy, String pDFData,String operationName,
			String orderId, String filename, String emailBody,  String emailFooter
			, Location supplier,int poFor, String grmNo) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(REQUEST_ORDER_CONFIRMATION_STRING, pDFData);
			//inputParams.put(ORDER_ID_STRING, orderId+"");
			inputParams.put(REQUEST_ORDER_BODY_STRING, emailBody);
			inputParams.put(REQUEST_ORDER_FOOTER_STRING, emailFooter);
			
			SendEmail.sendEmailToRequestOrderConfirmation(em, httpRequest, operationName, 
					locationId, inputParams,orderId, filename,supplier,poFor, grmNo);

	}
	
	/**
	 * Send EOD settledment mail to user.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param operationName the operation name
	 * @param emailBody the email body
	 * @param email the email
	 * @throws Exception the exception
	 */
	public static void sendEODSettledmentMailToUser(HttpServletRequest httpRequest,EntityManager em,
			String locationId, String operationName,
			String emailBody,String email) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(EOD_SETTLEDMENT_BODY_STRING, emailBody);
			inputParams.put(EMAIL_STRING, email);
			SendEmail.sendEODSettledmentMailToUser(em, httpRequest, operationName, 
					locationId, inputParams);

	}
	
	/**
	 * Send email by email addr for add update request order.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param updatedBy the updated by
	 * @param pDFData the DF data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param filename the filename
	 * @param emailBody the email body
	 * @param emailFooter the email footer
	 * @param emailAddress the email address
	 * @throws Exception the exception
	 */
	public static void sendEmailByEmailAddrForAddUpdateRequestOrder(HttpServletRequest httpRequest,EntityManager em,
			String locationId,String updatedBy, String pDFData,String operationName,
			String orderId, String filename, String emailBody,  String emailFooter
			,String emailAddress) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(REQUEST_ORDER_CONFIRMATION_STRING, pDFData);
			inputParams.put(REQUEST_ORDER_BODY_STRING, emailBody);
			inputParams.put(REQUEST_ORDER_FOOTER_STRING, emailFooter);
			
			SendEmail.sendEmailByEmailAddrForAddUpdateRequestOrder(em, httpRequest, operationName, 
					locationId, inputParams,orderId, filename,emailAddress);

		
		
	}
	
	
	
	/**
	 * Gets the PDF for request order confirmation.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param updatedBy the updated by
	 * @param pDFData the DF data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param filename the filename
	 * @param emailBody the email body
	 * @param emailFooter the email footer
	 * @return the PDF for request order confirmation
	 * @throws Exception the exception
	 */
	public static String getPDFForRequestOrderConfirmation(HttpServletRequest httpRequest,EntityManager em,
			String locationId,int updatedBy, String pDFData,String operationName,
			int orderId, String filename, String emailBody,  String emailFooter) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(REQUEST_ORDER_CONFIRMATION_STRING, pDFData);
			//inputParams.put(ORDER_ID_STRING, orderId+"");
			inputParams.put(REQUEST_ORDER_BODY_STRING, emailBody);
			inputParams.put(REQUEST_ORDER_FOOTER_STRING, emailFooter);
			
			return SendEmail.getPDFForRequestOrderConfirmation(em, httpRequest, operationName, 
					locationId, inputParams,orderId, filename);
	}
	
	
	
	/**
	 * Send order received email to customer.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param userId the user id
	 * @param updatedBy the updated by
	 * @param data the data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param emailAddress the email address
	 * @throws Exception the exception
	 */
	public static void sendOrderReceivedEmailToCustomer(HttpServletRequest httpRequest,EntityManager em,String locationId,String userId,String updatedBy,
			String data,String operationName,String orderId,String emailAddress,String ccEmail) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(ORDER_RECEIVED_EMAIL_STRING, data);
			inputParams.put(ORDER_ID_STRING, orderId+"");
			SendEmail.sendEmailToCustomer(em, httpRequest, operationName, locationId, inputParams,userId,updatedBy,emailAddress,orderId,ccEmail);
	}
	
	/**
	 * Send order payment confirmation email to customer.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param userId the user id
	 * @param updatedBy the updated by
	 * @param data the data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param emailAddress the email address
	 */
	public static void sendOrderPaymentConfirmationEmailToCustomer(HttpServletRequest httpRequest,EntityManager em,String locationId,String userId,String updatedBy,
			String data,String operationName,String  orderId,String emailAddress) {

		try {
			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(ORDER_TRANSACTION_EMAIL_STRING, data);
			inputParams.put(ORDER_ID_STRING, orderId+"");
			SendEmail.sendEmailToCustomer(em, httpRequest, operationName, locationId, inputParams,userId,updatedBy,emailAddress,orderId,null);

		}
		catch (Exception e) {
			logger.severe(httpRequest, e, "Could not send email to customer for order confirmation");
		}
		
	}
	
	/**
	 * Send feedback thanks email to customer.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param userId the user id
	 * @param updatedBy the updated by
	 * @param data the data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param emailAddress the email address
	 */
	public static void sendFeedbackThanksEmailToCustomer(HttpServletRequest httpRequest,EntityManager em,String locationId,String userId,String updatedBy,
			String data,String operationName,String orderId,String emailAddress) {

		try {
			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(ORDER_CONFIRMATION_EMAIL_STRING, data);
			inputParams.put(ORDER_ID_STRING, orderId+"");
			SendEmail.sendEmailToCustomer(em, httpRequest, operationName, locationId, inputParams,userId,updatedBy,emailAddress,orderId,null);

		}
		catch (Exception e) {
			// TODO :- DEfine constant for error msg
			logger.severe(httpRequest, e, "Could not send email to customer for order confirmation");
		}
		
	}
	
	
	public static void sendEODTipSettlementMailToUser(HttpServletRequest httpRequest, EntityManager em, String locationId,
			String operationName, String emailBody, String email, String dateTime) {

		Map<String, String> inputParams = new HashMap<String, String>();
		inputParams.put(EOD_TIP_SETTLEDMENT_BODY_STRING, emailBody);
		inputParams.put(EMAIL_STRING, email);
		SendEmail.sendEODTipSettledmentMailToUser(em, httpRequest, operationName, locationId, inputParams, dateTime);

	}

	/**
	 * Send order received email to customer.
	 *
	 * @param httpRequest the http request
	 * @param em the em
	 * @param locationId the location id
	 * @param userId the user id
	 * @param updatedBy the updated by
	 * @param data the data
	 * @param operationName the operation name
	 * @param orderId the order id
	 * @param emailAddress the email address
	 * @throws Exception the exception
	 */
	public static void sendQuotationEmailToCustomer(HttpServletRequest httpRequest,EntityManager em,String locationId,String userId,String updatedBy,
			String data,String operationName,String orderId,String emailAddress,String emailSubject) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(QUOTE_RECEIVED_EMAIL_STRING, data);
			inputParams.put(ORDER_ID_STRING, orderId+"");
			inputParams.put(EMAIL_SUBJECT, emailSubject);
			SendEmail.sendEmailToCustomer(em, httpRequest, operationName, locationId, inputParams,userId,updatedBy,emailAddress,orderId,null);
	}
	

	public static void sendOrderAdditionQuestionToCustomer(HttpServletRequest httpRequest,EntityManager em,String locationId,String userId,String updatedBy,
			String data,String operationName,String orderId,String emailAddress,String emailSubject) throws Exception{

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(QUOTE_RECEIVED_EMAIL_STRING, data);
			inputParams.put(ORDER_ID_STRING, orderId+"");
			inputParams.put(EMAIL_SUBJECT, emailSubject);
			SendEmail.sendEmailToCustomer(em, httpRequest, operationName, locationId, inputParams,userId,updatedBy,emailAddress,orderId,null);
	}
	
	

}
