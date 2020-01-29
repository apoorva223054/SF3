package com.nirvanaxp.services.util.sms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.mail.SendMail;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.util.email.EmailHelper;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.user.User;

// TODO: Auto-generated Javadoc
/**
 * The Class SMSTemplateKeys.
 */
public class SMSTemplateKeys {

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(SendMail.class.getName());

	/** The Constant RESERVATION_CREATED. */
	public final static String RESERVATION_CREATED = "RESERVATION_CREATED";

	/** The Constant RESERVATION_UPDATED. */
	public final static String RESERVATION_UPDATED = "RESERVATION_UPDATED";

	/** The Constant RESERVATION_CANCELLED. */
	public final static String RESERVATION_CANCELLED = "RESERVATION_CANCELLED";

	/** The Constant ORDER_CONFIRMATION. */
	public final static String ORDER_CONFIRMATION = "ORDER_CONFIRMATION";

	/** The Constant ORDER_RECEIVED. */
	public final static String ORDER_RECEIVED = "ORDER_RECEIVED";

	/** The Constant ORDER_PAYMENT_CONFIRMATION. */
	public final static String ORDER_PAYMENT_CONFIRMATION = "ORDER_PAYMENT_CONFIRMATION";

	/** The Constant ORDER_STATUS_UPDATED. */
	public final static String ORDER_STATUS_UPDATED = "ORDER_STATUS_UPDATED";

	/** The Constant CUSTOMER_FULL_NAME. */
	// Reservation required constant
	public final static String CUSTOMER_FULL_NAME = "CUSTOMER_FULL_NAME";

	/** The Constant CUSTOMER_FIRST_NAME. */
	public final static String CUSTOMER_FIRST_NAME = "CUSTOMER_FIRST_NAME";

	/** The Constant RESERVATION_DATE. */
	public final static String RESERVATION_DATE = "RESERVATION_DATE";

	/** The Constant RESERVATION_TIME. */
	public final static String RESERVATION_TIME = "RESERVATION_TIME";

	/** The Constant ORDER_SCHEDULE_TIME. */
	public final static String ORDER_SCHEDULE_TIME = "ORDER_SCHEDULE_TIME";

	/** The Constant GUEST_COUNT. */
	public final static String GUEST_COUNT = "GUEST_COUNT";

	/** The Constant CANCEL_RESERVATION_URL. */
	public final static String CANCEL_RESERVATION_URL = "CANCEL_RESERVATION_URL";

	/** The Constant BUSINESS_NAME. */
	public final static String BUSINESS_NAME = "BUSINESS_NAME";

	/** The Constant BUSINESS_ADDRESS. */
	public final static String BUSINESS_ADDRESS = "BUSINESS_ADDRESS";

	/** The Constant BUSINESS_WEBSITE_URL. */
	public final static String BUSINESS_WEBSITE_URL = "BUSINESS_WEBSITE_URL";

	/** The Constant ORDER_NUMBER. */
	public final static String ORDER_NUMBER = "ORDER_NUMBER";

	// Order confirmation
//	private final static String ORDER_CONFIRMATION_EMAIL_STRING= "ORDER_CONFIRMATION_EMAIL_STRING";
//	private final static String ORDER_RECEIVED_EMAIL_STRING= "ORDER_RECEIVED_EMAIL_STRING";
//	private final static String ORDER_TRANSACTION_EMAIL_STRING= "ORDER_TRANSACTION_EMAIL_STRING";
	/** The Constant AMOUNT_PAID. */
	// private final static String ORDER_ID_STRING= "ORDER_ID";
	public final static String AMOUNT_PAID = "AMOUNT_PAID";

	/**
	 * Send reservation SMS to customer.
	 *
	 * @param httpRequest   the http request
	 * @param em            the em
	 * @param reservation   the reservation
	 * @param l             the l
	 * @param webSiteUrl    the web site url
	 * @param operationName the operation name
	 */
	public static void sendReservationSMSToCustomer(HttpServletRequest httpRequest, EntityManager em,
			Reservation reservation, Location l, String webSiteUrl, String operationName) {

		try {
			SMSHelper emailHelper = new SMSHelper();
			String locationName = "";
			if (reservation.getPhoneNumber() != null && reservation.getPhoneNumber().length() > 0) {
				if (l != null) {
					locationName = l.getName();
				}
				int partySize = reservation.getPartySize();
				String dateString = emailHelper.getDateForEmail(reservation);
				String time = emailHelper.getTimeForEmail(reservation);

				Map<String, String> inputParams = new HashMap<String, String>();
				inputParams.put(SMSTemplateKeys.BUSINESS_NAME, locationName);
				inputParams.put(SMSTemplateKeys.CUSTOMER_FIRST_NAME, reservation.getFirstName());
				inputParams.put(SMSTemplateKeys.CUSTOMER_FULL_NAME,
						reservation.getFirstName() + " " + reservation.getLastName());
				inputParams.put(SMSTemplateKeys.RESERVATION_DATE, dateString);
				inputParams.put(SMSTemplateKeys.RESERVATION_TIME, time);
				inputParams.put(SMSTemplateKeys.GUEST_COUNT, partySize + "");

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
				inputParams.put(SMSTemplateKeys.BUSINESS_ADDRESS, businessString);
				if (l.getWebsite() != null) {
					if (l.getWebsite().startsWith("http://") || l.getWebsite().startsWith("https://")) {
						inputParams.put(SMSTemplateKeys.BUSINESS_WEBSITE_URL, l.getWebsite());
					} else {
						inputParams.put(SMSTemplateKeys.BUSINESS_WEBSITE_URL, "http://" + l.getWebsite());
					}
				} else {
					inputParams.put(SMSTemplateKeys.BUSINESS_WEBSITE_URL, "#");
				}
				SendSMS.sendSMSToCustomer(em, httpRequest, operationName, l.getId(), inputParams,
						reservation.getUsersId(), reservation.getUpdatedBy(), reservation.getPhoneNumber(),
						reservation.getId());
			}
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not send email to customer for reservation confirmation");
		}

	}

	/**
	 * Send order SMS to customer.
	 *
	 * @param httpRequest   the http request
	 * @param em            the em
	 * @param orderHeader   the order header
	 * @param l             the l
	 * @param webSiteUrl    the web site url
	 * @param operationName the operation name
	 */
	public static void sendOrderSMSToCustomer(HttpServletRequest httpRequest, EntityManager em, OrderHeader orderHeader,
			Location l, String webSiteUrl, String operationName) {

		try {
			User user = (User) new CommonMethods().getObjectById("User", em, User.class, orderHeader.getUsersId());
			// SMSHelper emailHelper = new SMSHelper();
			String locationName = "";
			if (user.getPhone() != null && user.getPhone().length() > 0) {
				if (l != null) {
					locationName = l.getName();
				}
				int partySize = orderHeader.getPointOfServiceCount();

				Map<String, String> inputParams = new HashMap<String, String>();
				inputParams.put(SMSTemplateKeys.BUSINESS_NAME, locationName);
				inputParams.put(SMSTemplateKeys.CUSTOMER_FIRST_NAME, orderHeader.getFirstName());
				inputParams.put(SMSTemplateKeys.CUSTOMER_FULL_NAME,
						orderHeader.getFirstName() + " " + orderHeader.getLastName());
				inputParams.put(SMSTemplateKeys.ORDER_SCHEDULE_TIME, orderHeader.getScheduleDateTime());
				inputParams.put(SMSTemplateKeys.GUEST_COUNT, partySize + "");
				if (orderHeader.getAmountPaid() != null)
					inputParams.put(SMSTemplateKeys.AMOUNT_PAID, orderHeader.getAmountPaid().toString());

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
				inputParams.put(SMSTemplateKeys.BUSINESS_ADDRESS, businessString);
				if (l.getWebsite() != null) {
					if (l.getWebsite().startsWith("http://") || l.getWebsite().startsWith("https://")) {
						inputParams.put(SMSTemplateKeys.BUSINESS_WEBSITE_URL, l.getWebsite());
					} else {
						inputParams.put(SMSTemplateKeys.BUSINESS_WEBSITE_URL, "http://" + l.getWebsite());
					}
				} else {
					inputParams.put(SMSTemplateKeys.BUSINESS_WEBSITE_URL, "#");
				}
				SendSMS.sendSMSToCustomer(em, httpRequest, operationName, l.getId(), inputParams,
						orderHeader.getUsersId(), orderHeader.getUpdatedBy(), user.getPhone(), orderHeader.getId());
			}
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not send email to customer for reservation confirmation");
		}

	}

	public static void sendItemCancelledSMSToCustomerCalling(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader orderHeader, OrderDetailItem orderDetailItem, String locationId,String sessionId) {

		try {
			 
			Location l = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);

			User cancelledBy = (User) new CommonMethods().getObjectById("User", em, User.class,
					orderDetailItem.getUpdatedBy());
			// SMSHelper emailHelper = new SMSHelper();

			Map<String, String> inputParams = new HashMap<String, String>();
			inputParams.put(SMSTemplateKeys.ORDER_NUMBER, orderHeader.getOrderNumber());
			inputParams.put("ItemName", orderDetailItem.getItemsShortName());
			inputParams.put("ItemPrice", orderDetailItem.getTotal() + "");
			inputParams.put("CancelledBy", cancelledBy.getFirstName() + " " + cancelledBy.getLastName());
			EmailHelper emailHelper = new EmailHelper();
			List<User> userList = emailHelper.sendEmailByFunctionByLocationIdWithQuery(em,locationId,"Cancel Item");
			logger.severe("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@userList="+userList.toString());
			for(User user:userList) {
				logger.severe("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@user="+user.toString());
			SendSMS.sendItemCancelledSMSToCustomer(em, httpRequest, "Cancel Item", l.getId(), inputParams,
					user.getId(), user.getPhone(), orderHeader.getId());
			}
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not send sms for sendItemCancelledSMSToCustomer");
		}

	}
}
