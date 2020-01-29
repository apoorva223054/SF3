/**
 * Copyright (c) 2014, All rights reserved, http://NirvanaXP.com
 * 
 * This source is the intellectual property of NirvanaXP Inc. This source shall
 * not be copied, distributed or referenced without express written consent from
 * NirvanaXP Inc.
 * 
 * File Name = MessageConstants.java
 */

/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.synchistory;

public class MessageConstants
{

	public final static String GLOBAL_NAME_EXCEPTION = "Global database name is missing in config file";
	public final static String DRIVER_NAME_EXCEPTION = "Driver name is missing in config file";
	public final static String UNABLE_OBTAIN_DATABASE_CONNECTION_EXCEPTION = "Unable to obtain database connection for global database.";
	public final static String INVALID_SESSION_ID_MSG = "Invalid session id. Please login to	 get the session id";
	public final static String UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE = "Unable to obtain entity manger factory for local databse.";
	public final static String UNABLE_TO_GET_CONNECTION_USING_JDBC_DRIVER = "Unable to get connection using jdbc driver";
	public final static String DATABASE_NAME_EXCEPTION = "Database schema name cannot be blank.";
	public final static String IMAGE_UPLOAD_PATH_EXCEPTION = "Image upload path is missing in config file";
	public final static String IMAGE_UPLOAD_MAX_SIZE_EXCEPTION = "Image size is more than maximum allowed";
	public final static String ORDERID_0_EXCEPTION = "OrderId cannot be 0";
	public final static String RESERVATION_EXCEPTION_HOLD_SESSION_EXPIRED = "Your reservation slot session is expired.Please create a new slot session";
	public final static String ORDER_HEADER_CONTAINS_ITEMS = "Order header contains items. Unable to change order source.";

	public final static String NIRVANAXP_NAME_EXCEPTION = "NirvanaXP database name is missing in config file";

	public final static String REFRENCE_NUMBER_EXCEPTION = "Invalid Reference Number";
	public final static String SCHEMA_NAME_EXCEPTION_CODE = "ACC002";
	public final static String SCHEMA_NAME_EXCEPTION = "Invalid schema name";

	public final static String ACCOUNT_EXCEPTION_CODE = "ACC001";
	public final static String ACCOUNT_EXCEPTION = "Invalid Account";

	/************ Order Status Constant ******************/
	public final static String OA_RECEIVED = "Order Ahead Received";
	public final static String OA_PLACED = "Order Ahead Placed";
	public final static String OA_CHECKPRESENTED = "Order Ahead Check Presented";
	public final static String OA_PAID = "Order Ahead Paid";
	
	public final static String OA_ORDERAHEADCASHONDELIVERY = "Order Ahead Cash On Delivery";
	public final static String OA_CASHONDELIVERYPRINT = "OrderAheadCashOnDeliveryPrint";
	public final static String OA_CASHONDELIVERYNOPRINT = "OrderAheadCashOnDeliveryNoPrint";
	public final static String OA_ORDERAHEADPAIDPRINT = "OrderAheadPaidPrint";
	public final static String OA_ORDERAHEADPAIDNOPRINT = "OrderAheadPaidNoPrint";


	/****************************************************/

	/*********** Order related Codes and messages ************/
	public static final String ERROR_MESSAGE_ORDER_LOCATION_ALREADY_HAS_ORDER_DISPLAY_MESSAGE = "Specified Location for Order already has some order";
	public static final String ERROR_CODE_ORDER_LOCATION_ALREADY_HAS_ORDER_EXCEPTION = "ORD1000";
	
	public static final String ERROR_MESSAGE_ORDER_LOCK = "Order is Locked";
	public static final String ERROR_CODE_ORDER_LOCK = "ORD1030";
	
	public static final String ERROR_MESSAGE_ORDER_NOT_LOCK = "Order is not Locked";
	public static final String ERROR_CODE_ORDER_NOT_LOCK = "ORD1031";
	
	

	public static final String ERROR_CODE_ORDER_LOCATION_CANNOT_UPDATE_ORDER_STATUS_EXCEPTION = "ORD1001";
	public static final String ERROR_MESSAGE_ORDER_LOCATION_CANNOT_UPDATE_ORDER_STATUS_READY_DISPLAY_MESSAGE = "Cannot update location as order status is Ready to Order";

	public static final String ERROR_CODE_ORDER_CANNOT_VOID_AMOUNT_GREATER_THAN_ZERO = "ORD1002";
	public static final String ERROR_MESSAGE_ORDER_CANNOT_VOID_AMOUNT_GREATER_THAN_ZERO_DISPLAY_MESSAGE = "Cannot void an order as its amount is greater than 0";

	public static final String ERROR_CODE_ORDER_VOID_REASON_ID_CANNOT_BE_ZERO = "ORD1003";
	public static final String ERROR_MESSAGE_ORDER_VOID_REASON_ID_CANNOT_BE_ZERO_DISPLAY_MESSAGE = "Order void reason id cannot be 0 for Void Order  status";
	public static final String ERROR_MESSAGE_ORDER_CANCEL_REASON_ID_CANNOT_BE_ZERO_DISPLAY_MESSAGE = "Order cancel reason id cannot be 0 for cancel Order  status";

	public static final String ERROR_MESSAGE_ORDER_LOCATION_ALREADY_MERGE_WITH_OTHER_LOCATION_DISPLAY_MESSAGE = "Specified Location is already merged with some other location";
	public static final String ERROR_CODE_ORDER_LOCATION_ALREADY_MERGE_WITH_OTHER_LOCATION_EXCEPTION = "ORD1004";

	public static final String ERROR_MESSAGE_VOID_REASON_NOT_PRESENT_DISPLAY_MESSAGE = "Void reasons not present in database";
	public static final String ERROR_CODE_VOID_REASON_NOT_PRESENT_EXCEPTION = "ORD1005";

	public static final String ERROR_MESSAGE_VOID_ORDER_STATUS_NOT_PRESENT_DISPLAY_MESSAGE = "Void Order  status not present in database";
	public static final String ERROR_CODE_VOID_ORDER_STATUS_NOT_PRESENT_EXCEPTION = "ORD1006";

	public static final String ERROR_MESSAGE_ORDER_SOURCE_GROUP_NOT_PRESENT_DISPLAY_MESSAGE = "Order source group not present in database";
	public static final String ERROR_CODE_ORDER_SOURCE_GROUP_NOT_PRESENT_EXCEPTION = "ORD1007";

	public static final String ERROR_MESSAGE_GLOBAL_USER_NOT_ADD_DISPLAY_MESSAGE = "Not able to add user in global database";
	public static final String ERROR_CODE_GLOBAL_USER_NOT_ADD_EXCEPTION = "ORD1008";

	public static final String ERROR_MESSAGE_ORDER_HEADER_CONTAINS_ITEMS_DISPLAY_MESSAGE = "Order header contains items. Unable to change order source";
	public static final String ERROR_CODE_ORDER_HEADER_CONTAINS_ITEMS_EXCEPTION = "ORD1011";

	public static final String ERROR_MESSAGE_ORDERID_0_EXCEPTION_DISPLAY_MESSAGE = "Order Id cannot be 0";
	public static final String ERROR_CODE_ORDERID_0_EXCEPTION = "ORD1012";

	public static final String ERROR_MESSAGE_INVENTORY_OUT_OF_STOCK_DISPLAY_MESSAGE = "Inventory out of stck";
	public static final String ERROR_CODE_INVENTORY_OUT_OF_STOCK_EXCEPTION = "ORD1013";

	public static final String ERROR_CODE_ORDER_PLACED_STATUS_NOT_PRESENT_IN_DATABASE = "ORD1014";
	public static final String ERROR_CODE_ORDER_PLACED_STATUS_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE = "Cannot update order as order status not exist in database";

	public static final String ERROR_CODE_NO_ORDER_EXIST_TO_PROCESS_CLOSE_BUSINESS = "ORD1015";
	public static final String ERROR_MESSAGE_NO_ORDER_EXIST_TO_PROCESS_CLOSE_BUSINESS = "No order exist to process close business";

	public static final String ERROR_CODE_NO_ACTIVE_BUSINESS_PRESENT = "ORD1016";
	public static final String ERROR_MESSAGE_NO_ACTIVE_BUSINESS_PRESENT = "No active business present";

	public static final String ERROR_CODE_NO_ACTIVE_BATCH_ALREADY_PRESENT = "ORD1027";
	public static final String ERROR_MESSAGE_NO_ACTIVE_BATCH_ALREADY_PRESENT = "Active batch already present";

	public static final String ERROR_MESSAGE_UNSETTLED_TRANSACTION_PRESENT_DISPLAY_MESSAGE = "Some unsettled transaction is present in current batch. Please settle that first ";
	public static final String ERROR_CODE_UNSETTLED_TRANSACTION_PRESENT = "ORD1017";

	public static final String ERROR_MESSAGE_ORDER_OUT_OF_SYNCH_DISPLAY_MESSAGE = "Order is out of synch with server";
	public static final String ERROR_CODE_ORDER_ORDER_OUT_OF_SYNCH_EXCEPTION = "ORD1018";

	public static final String ERROR_MESSAGE_BATCH_ID_NOT_PRESENT_WITH_DATE_DISPLAY_MESSAGE = "We have not found any batch for the date";
	public static final String ERROR_CODE_BATCH_ID_NOT_PRESENT_WITH_DATE__EXCEPTION = "ORD1019";

	public static final String ERROR_CODE_ORDER_CANNOT_TRANSFER_CODE = "ORD1020";
	public static final String ERROR_MESSAGE_ORDER_CANNOT_TRANSFER_DISPLAY_MESSAGE = "Order cannot transferred as order is already paid";
	public static final String ERROR_MESSAGE_ORDER_CANNOT_TRANSFER__MESSAGE = "Order cannot transferred as order is already paid";
	
	public static final String ERROR_CODE_ORDER_CANNOT_TRANSFER_DIS_APPLY_CODE = "ORD1020";
	public static final String ERROR_MESSAGE_ORDER_CANNOT_TRANSFER_DIS_APPLY_DISPLAY_MESSAGE = "Order cannot transferred as order have discount.";
	public static final String ERROR_MESSAGE_ORDER_CANNOT_TRANSFER_DIS_APPLY_MESSAGE = "Order cannot transferred as order have discount.";


	public static final String ERROR_CODE_ITEM_CANNOT_TRANSFER_CODE = "ORD1020";
	public static final String ERROR_MESSAGE_ITEM_CANNOT_TRANSFER_DISPLAY_MESSAGE = "Item cannot transferred as order is already paid";
	public static final String ERROR_MESSAGE_ITEM_CANNOT_TRANSFER__MESSAGE = "Item cannot transferred as order is already paid";

	public static final String ERROR_CODE_SHIFT_SCHEDULE_TAKEN_EXCEPTION = "ORD1021";
	public static final String ERROR_MESSAGE_SHIFT_SCHEDULE_TAKEN_DISPLAY_MESSAGE = "Shift schedule is either null or not active for the given slot.";

	public static final String ERROR_MESSAGE_SHIFT_SCHEDULE_STATUS_INVALID_DISPLAY_MESSAGE = "Shift schedule status is invalid.";
	public static final String ERROR_CODE_SHIFT_SCHEDULE_STATUS_INVALID_EXCEPTION = "ORD1022";
	
	public static final String ERROR_CODE_SHIFT_SLOT_NOT_EXIST = "ORD1023";
	public static final String ERROR_MESSAGE_SHIFT_SLOT_NOT_EXIST = "Shift slot not exist in database.";
	
	public final static String ERROR_MESSAGE_SHIFT_NO_ACTIVE_CLIENT_ID_AVAILABLE_DISPLAY_MESSAGE = "Active client id holding the slot cannot be 0, you must hold a slot first to make a shift";
	public static final String ERROR_CODE_SHIFT_NO_ACTIVE_CLIENT_ID_AVAILABLE_EXCEPTION = "ORD1024";
	
	public static final String ERROR_MESSAGE_ORDER_MANAGEMENT_ROLE= "Order Management Role Not Assigned to User";
	public static final String ERROR_CODE_ORDER_MANAGEMENT_ROLE_EXCEPTION = "ORD1025";
	
	public static final String ERROR_CODE_ORDER_DATE_OUT_OF_RANGE_EXCEPTION = "ORD1026";
	public static final String ERROR_MESSAGE_ORDER_DATE_OUT_OF_RANGE_DISPLAY_MESSAGE = "Error date out of range";
	
	public static final String ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH = "ORD1027";
	public static final String ERROR_CODE_PRE_AUTH_TRANSACTION_PRESENT_IN_CURRENT_BATCH_MESSAGE = "Some Pre-Auth transactions is present in current batch. Please settle that first. ";

	public static final String ERROR_CODE_AMOUNT_PAID_CANNOT_BE_ZERO = "ORD1028";
	public static final String ERROR_CODE_AMOUNT_PAID_CANNOT_BE_ZERO_DISPLAY_MESSAGE = "Cannot apply tips as amount paid is 0 ";
	

	/********** Reservation related Codes and messages *************/
	public static final String ERROR_CODE_RESERVATION_SCHEDULE_TAKEN_EXCEPTION = "RES1000";
	public static final String ERROR_MESSAGE_RESERVATION_SCHEDULE_TAKEN_DISPLAY_MESSAGE = "Reservation schedule is either null or not active for the given slot.";

	public static final String ERROR_MESSAGE_RESERVATION_SCHEDULE_STATUS_INVALID_DISPLAY_MESSAGE = "Reservation schedule status is invalid.";
	public static final String ERROR_CODE_RESERVATION_SCHEDULE_STATUS_INVALID_EXCEPTION = "RES1001";

	public final static String ERROR_MESSAGE_RESERVATION_NO_ACTIVE_CLIENT_ID_AVAILABLE_DISPLAY_MESSAGE = "Active client id holding the slot cannot be 0, you must hold a slot first to make a reservation";
	public static final String ERROR_CODE_RESERVATION_NO_ACTIVE_CLIENT_ID_AVAILABLE_EXCEPTION = "RES1002";

	public final static String ERROR_MESSAGE_RESERVATION_TYPE_UNKNOWN = "Invalid Reservation Type";
	public static final String ERROR_CODE_RESERVATION_TYPE_UNKNOWN = "RES1003";

	public static final String ERROR_CODE_RESERVATION_SLOT_NOT_EXIST = "RES1004";
	public static final String ERROR_MESSAGE_RESERVATION_SLOT_NOT_EXIST = "Reservation slot not exist in database.";

	public final static String ERROR_MESSAGE_RESERVATION_NO_ACTIVE_CLIENT_FOUND_FOR_GIVEN_ID = "No Reservation Slot Active client info found in database for given slot holder client id.";
	public static final String ERROR_CODE_RESERVATION_NO_ACTIVE_CLIENT_FOUND_FOR_GIVEN_ID = "RES1005";

	public static final String ERROR_MESSAGE_NEXT_RESERVATION_SLOT_NOT_EXIST = "Next availble slots are not available so not holding slot";

	/********** Payments related Codes and messages ***************/
	public static final String ERROR_CODE_PAYMENT_UNIMPLEMENTED_GATEWAY_EXCEPTION = "PMT1000";
	public static final String ERROR_MESSAGE_PAYMENT_UNIMPLEMENTED_GATEWAY_EXCEPTION_DISPLAY_MESSAGE = "The specified payment gateway is not supported.";

	public static final String ERROR_CODE_PAYMENT_NO_ORDERS_FOUND_EXCEPTION = "PMT1001";
	public static final String ERROR_MESSAGE_PAYMENT_NO_ORDERS_FOUND_DISPLAY_MESSAGE = "No Orders Found to process payments.";

	public static final String ERROR_MESSAGE_PAYMENT_SERVICE_INPUT_INVALID = "PMT1002";
	public static final String ERROR_CODE_PAYMENT_SERVICE_INPUT_INVALID = "The input provided for payment is invalid.";

	public static final String ERROR_CODE_BRAINTREE_TOKEN_NOT_GENERATED = "PMT1003";
	public static final String ERROR_MESSAGE_BRAINTREE_TOKEN_NOT_GENERATED = "The braintree service did not generate a token";
	
	

	/********** JSONUtility related Codes and messages ***************/
	public static final String ERROR_CODE_JSON_STRING_GENERATION_UNEXPECTED_EXCEPTION = "JSON1000";
	public static final String ERROR_MESSAGE_JSON_STRING_GENERATION_UNEXPECTED_EXCEPTION_DISPLAY_MESSAGE = "Could not generate JSON String";

	/********** Location Service related codes and messages *************/

	public static final String ERROR_CODE_LOCATION_DELETE = "LXN1000";
	public static final String ERROR_MESSAGE_LOCATION_DELETE_DISPLAY_MESSAGE = "Could not delete location";

	public static final String ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER = "LXN1000";
	public static final String ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER = "No active business found for User";

	/********** User Service related codes and messages *************/

	public static final String ERROR_CODE_LOCATION_CANNOT_BE_BLANK_EXCEPTION = "US1000";
	public static final String ERROR_MESSAGE_LOCATION_CANNOT_BE_BLANK_DISPLAY_MESSAGE = "location id cannot be blank";

	public static final String MSG_USER_ADDED_GLOBAL_LOCAL_DB = "Added user to global and local database";
	public static final String MSG_USER_UPDATED_GLOBAL_LOCAL_DB = "Local and global user updated successfully";
	public static final String MSG_USER_ALREADY_EXISTS_IN_GLOBAL_DB = "User already exists in global database";
	public static final String MSG_USER_ALREADY_EXISTS_WITH_SAME_AUTH_PIN = "User with same Auth pin exists in database already";

	public static final String ERROR_CODE_PHONE_ALREADY_EXIST_EXCEPTION = "US1001";
	public static final String ERROR_MESSAGE_PHONE_ALREADY_EXIST_DISPLAY_MESSAGE = "Phone exists in global database already";

	public static final String ERROR_CODE_EMAIL_ALREADY_EXIST_EXCEPTION = "US1002";
	public static final String ERROR_MESSAGE_EMAIL_ALREADY_EXIST_DISPLAY_MESSAGE = "Email exists in global database already";

	public static final String ERROR_CODE_USERNAME_ALREADY_EXIST_EXCEPTION = "US1003";
	public static final String ERROR_MESSAGE_USERNAME_ALREADY_EXIST_DISPLAY_MESSAGE = "Username exists in global database already";

	public static final String ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION = "US1004";
	public static final String ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE = "User not present in database";

	public static final String ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE = "US1005";
	public static final String ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE = "User not present in database";
	
	public static final String ERROR_CODE_DUPLICATE_ITEMS_SCHEDULE_ON_SAME_DATE = "US1006";
	public static final String ERROR_MESSAGE_DUPLICATE_ITEMS_SCHEDULE_ON_SAME_DATE = "Could not schedule same item on same date.";


	/**********
	 * Employee Operation Management related codes and messages
	 *************/

	public static final String ERROR_CODE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN = "EOM1000";
	public static final String ERROR_MESSAGE_CANNOT_CLOCK_OUT_BEFORE_CLOCK_IN = "User cannot clock out before clock in.";

	public static final String ERROR_CODE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT = "EOM1001";
	public static final String ERROR_MESSAGE_USER_CANNOT_CLOCK_IN_BEFORE_CLOCK_OUT = "User cannot clock in before clock out.";

	public static final String ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN = "EOM1002";
	public static final String ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_BREAK_IN = "User cannot start break before break ends.";

	public static final String ERROR_CODE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN = "EOM1002";
	public static final String ERROR_MESSAGE_USER_CANNOT_BREAK_OUT_BEFORE_CLOCK_IN = "User cannot start break before clock in.";

	public static final String ERROR_CODE_USER_CANNOT_PAID_OUT_BEFORE_PAID_IN = "EOM1006";
	public static final String ERROR_MESSAGE_USER_CANNOT_PAID_OUT_BEFORE_PAID_IN = "User cannot paid out before paid in.";
	
	public static final String ERROR_CODE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN = "EOM1004";
	public static final String ERROR_MESSAGE_USER_CANNOT_CLOCK_OUT_WITHOUT_BREAK_IN = "User cannot clock-out without break-in";

	public static final String ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT = "EOM1005";
	public static final String ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_BREAK_OUT = "User cannot end break before break start.";
	
	public static final String ERROR_CODE_USER_CANNOT_END_BREAK_WITHOUT_CLOCK_IN= "EOM1006";
	public static final String ERROR_MESSAGE_USER_CANNOT_END_BREAK_WITHOUT_CLOCK_IN = "User cannot break in before Clock in.";
	
	public static final String ERROR_CODE_TIP_CALCULATED_DONE = "EOM1005";
	public static final String ERROR_MESSAGE_TIP_CALCULATED_DONE = "Time Add/update Not Allowed Tips Calculation Already Settled";

	/********** Common Exception codes and messages ***************/
	public static final String ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE = "Session expired or invalid. Please Login again.";
	public static final String ERROR_CODE_INVALID_SESSION_EXCEPTION = "NXP1000";

	public static final String ERROR_MESSAGE_UNEXPECTED_DISPLAY_MESSAGE = "Unexpected Service Failure";
	public static final String ERROR_CODE_UNEXPECTED_EXCEPTION = "NXP1001";

	public static final String ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE = "No Result Found";
	public static final String ERROR_CODE_NO_RESULT_EXCEPTION = "NXP1002";

	public static final String ERROR_MESSAGE_NON_UNIQUE_RESULT_DISPLAY_MESSAGE = "Unique Result Not Found";
	public static final String ERROR_CODE_NON_UNIQUE_RESULT_EXCEPTION = "NXP1003";

	public static final String ERROR_MESSAGE_FILE_NOT_FOUND = "File Not Found";
	public static final String ERROR_CODE_FILE_NOT_FOUND = "NXP1004";

	public static final String ERROR_MESSAGE_IO_EXCEPTION = "Unable to read file";
	public static final String ERROR_CODE_IO_EXCEPTION = "NXP1005";

	public static final String ERROR_MESSAGE_DATABASE_EXCEPTION = "Unable to use database";
	public static final String ERROR_CODE_DATABASE_EXCEPTION = "NXP1006";

	public static final String ERROR_MESSAGE_BAD_INPUT_EXCEPTION = "Invalid input sent to service";
	public static final String ERROR_CODE_BAD_INPUT_EXCEPTION = "NXP1007";

	public static final String ERROR_MESSAGE_LOGIN_EXCEPTION = "Login Failed";
	public static final String ERROR_CODE_LOGIN_EXCEPTION = "NXP1008";
	
	public static final String ERROR_CODE_ORDER_PAID = "NXP1009";
	public static final String ERROR_MESSAGE_ORDER_PAID = "Related order having amount paid";
	
	public static final String ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_USER = "EOM1010";
	public static final String ERROR_MESSAGE_DISCOUNT_CODE_NOT_VALID_FOR_USER = "Discount Code Not Valid For User.";

	public static final String ERROR_CODE_DISCOUNT_CODE_ALREADY_USED_BY_USER = "EOM1011";
	public static final String ERROR_MESSAGE_DISCOUNT_CODE_ALREADY_USED_BY_USER = "Discount Code Already Used By User.";
	
	public static final String ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_ALL_CUSTOMER = "EOM1012";
	public static final String ERROR_MESSAGE_CODE_DISCOUNT_CODE_NOT_VALID_FOR_ALL_CUSTOMER = "Discount Code Not Valid For All User.";

	
	public static final String ERROR_CODE_NO_PAYMENT_GATEWAY_ASSINGED = "EOM1012";
	public static final String ERROR_MESSAGE_NO_PAYMENT_GATEWAY_ASSINGED= "No Payment Gateway Assigned. Please Contact To business";
	
	/********** Global Account Service related codes and messages *************/

	public static final String ERROR_CODE_TEMP_ACCOUNT_NULL = "GAS1000";
	public static final String ERROR_MESSAGE_TEMP_ACCOUNT_NULL = "Temp account packet cannot be null";

	public static final String ERROR_CODE_FIRST_NAME_BLANK_NULL = "GAS1001";
	public static final String ERROR_MESSAGE_FIRST_NAME_BLANK_NULL = "First name cannot be null or blank";

	public static final String ERROR_CODE_LAST_NAME_BLANK_NULL = "GAS1002";
	public static final String ERROR_MESSAGE_LAST_NAME_BLANK_NULL = "Last name cannot be null or blank";

	public static final String ERROR_CODE_ACCOUNT_NAME_BLANK_NULL = "GAS1003";
	public static final String ERROR_MESSAGE_ACCOUNT_NAME_BLANK_NULL = "Account name cannot be null or blank";

	public static final String ERROR_CODE_PHONE_EMAIL_BLANK_NULL = "GAS1004";
	public static final String ERROR_MESSAGE_PHONE_EMAIL_BLANK_NULL = "Phone and email cannot be null or blank";

	public static final String ERROR_CODE_VERIFICATION_URL_BLANK_NULL = "GAS1005";
	public static final String ERROR_MESSAGE_VERIFICATION_URL_BLANK_NULL = "Verification url cannot be null or blank";

	public static final String ERROR_CODE_UNABLE_TO_GENERATE_VERIFICATION_CODE = "GAS1006";
	public static final String ERROR_MESSAGE_UNABLE_TO_GENERATE_VERIFICATION_CODE = "Unable to generate verification code";

	public static final String ERROR_CODE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION = "GAS1007";
	public static final String ERROR_MESSAGE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION = "Email must be sent in packet for email verification";

	public static final String ERROR_CODE_UNABLE_TO_SEND_VERIFICATION_EMAIL = "GAS1008";
	public static final String ERROR_MESSAGE_UNABLE_TO_SEND_VERIFICATION_EMAIL = "Unable to send verification email. Please try again later";

	public static final String ERROR_CODE_UNABLE_TO_SEND_EMAIL = "GAS1008";
	public static final String ERROR_MESSAGE_UNABLE_TO_SEND_EMAIL = "Unable to send email. Please try again later";

	public static final String ERROR_CODE_UNABLE_TO_SEND_SINGUP_PROCESS_EMAIL = "GAS1011";
	public static final String ERROR_MESSAGE_UNABLE_TO_SEND_SINGUP_PROCESS_EMAIL = "Unable to send SIGNUP PROCESS email. Please try again later";

	public static final String ERROR_CODE_UNABLE_TO_SEND_SINGUP_SUCCESS_EMAIL = "GAS1012";
	public static final String ERROR_MESSAGE_UNABLE_TO_SEND_SINGUP_SUCCESS_EMAIL = "Unable to send SIGNUP SUCCESS email. Please try again later";

	public static final String ERROR_CODE_PHONE_MUST_SENT_IN_PACKET_FOR_VERIFICATION = "GAS1009";
	public static final String ERROR_MESSAGE_PHONE_MUST_SENT_IN_PACKET_FOR_VERIFICATION = "Phone must be sent in packet for email verification";

	public static final String ERROR_CODE_ILLEGAL_VERIFICATION_METHOD = "GAS1010";
	public static final String ERROR_MESSAGE_ILLEGAL_VERIFICATION_METHOD = "Illegal verification method";

	public static final String ERROR_MESSAGE_DEVICE_CANNOT_EMPTY = "DeviceId cannot be null or empty";
	public static final String ERROR_MESSAGE_DEVICE_NAME_CANNOT_EMPTY = "Device name cannot be null or empty";
	public static final String ERROR_MESSAGE_IPADDRESS_CANNOT_EMPTY = "Ipaddress cannot be null or empty";
	public static final String ERROR_MESSAGE_DEVICE_TYPE_0 = "DeviceTypeId cannot be 0";
	public static final String ERROR_MESSAGE_WRONG_DEVICE_TYPE = "Wrong device type id sent to server. Plese recheck the device Id";
	public static final String ERROR_MESSAGE_UNAUTHORIZED_ACCESS = "Unauthorized Access.";
	public static final String ERROR_MESSAGE_ILLEGAL_PACKET = "illegal packet sent to server. You must supply minimum one user";

	public static final String ERROR_CODE_COOKIE_NAME_MISSING = "GAS1011";
	public static final String ERROR_MESSAGE_COOKIE_NAME_MISSING = "Login cookie name missing on config file";

	public static final String ERROR_CODE_PROVIDE_REGISTER_EMAIL = "GAS1012";
	public static final String ERROR_MESSAGE_PROVIDE_REGISTER_EMAIL = "Please provide registered email id for this username";

	public static final String ERROR_CODE_PHONE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION = "GAS1013";
	public static final String ERROR_MESSAGE_PHONE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION = "Phone or Email must be sent for email verification";

	public static final String ERROR_CODE_LINK_EXPIRED = "GAS1014";
	public static final String ERROR_MESSAGE_LINK_EXPIRED = "SORRY, THIS LINK HAS EXPIRED.";
	
	

	public static final String ERROR_CODE_USER_EXISTS_IN_GLOBAL = "GAS1015";
	public static final String ERROR_MESSAGE_USER_EXISTS_IN_GLOBAL = "Username exists in global database already.";

	public static final String ERROR_CODE_VERIFICATION_CODE_EXPIRED = "GAS1016";
	public static final String ERROR_MESSAGE_VERIFICATION_CODE_LINK_EXPIRED = "Verification code is expired.";

	public static final String ERROR_CODE_VERIFICATION_CODE_NOT_FOUND = "GAS1017";
	public static final String ERROR_MESSAGE_VERIFICATION_CODE_NOT_FOUND = "Verification code is invalid.";

	public static final String ERROR_CODE_ACCOUNT_ADMIN_ROLE_NOT_FOUND = "GAS1018";
	public static final String ERROR_MESSAGE_ACCOUNT_ADMIN_ROLE_NOT_FOUND = "Account Admin Role Id was not found";

	public static final String ERROR_CODE_USERNAME_DOES_NOT_EXIST = "GAS1019";
	public static final String ERROR_MESSAGE_USERNAME_DOES_NOT_EXIST = "Username does not exist in our database";
	
	
	public static final String ERROR_CODE_OTP_EXPIRED = "GAS1020";
	public static final String ERROR_MESSAGE_OTP_EXPIRED = "SORRY, THIS OTP HAS EXPIRED.";
	

	/********** Braintree Service related codes and messages *************/
	public static final String ERROR_CODE_DUPLICATE_TRANSACTION = "BRT001";
	public static final String ERROR_MESSAGE_DUPLICATE_TRANSACTION = "Gateway Rejected: duplicate";

	public static final String ERROR_CODE_CANNOT_USE_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE = "BRT002";
	public static final String ERROR_MESSAGE_CANNOT_USE_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE = "Cannot use a payment_method_nonce more than once.";

	public static final String ERROR_CODE_UNKNOWN_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE = "BRT003";
	public static final String ERROR_MESSAGE_UNKNOWN_PAYMENT_METHOD_NOUNCE_MORE_THAN_ONE = "Unknown payment_method_nonce.";

	public static final String ERROR_CODE_FROM_GATEWAY = "BRT004";

	/**************************** Reporting codes and messages ***********/
	public static final String ERROR_CODE_NO_RESULT_FOUND = "RPT001";
	public static final String ERROR_MESSAGE_NO_RESULT_FOUND = "No Result return";

	/**************************** Printing codes and messages ***********/
	public static final String ERROR_CODE_NO_RESULT_FOUND_FOR_ORDER_SOURCE = "PT001";
	public static final String ERROR_MESSAGE_NO_RESULT_FOUND_FOR_ORDER_SOURCE = "No Order Source found for web";
	public static final String ERROR_CODE_NO_RESULT_FOUND_FOR_ORDER_SOURCE_GROUP = "PT002";
	public static final String ERROR_MESSAGE_NO_RESULT_FOUND_FOR_ORDER_SOURCE_GROUP = "No Order Source Group found for web";
	public static final String ERROR_CODE_NO_RESULT_FOUND_FOR_ORDER = "PT003";
	public static final String ERROR_MESSAGE_NO_RESULT_FOUND_FOR_ORDER = "Order Id not present in database";
	public static final String ERROR_CODE_NO_RESULT_FOUND_FOR_ORDER_STATUS = "PT004";
	public static final String ERROR_MESSAGE_NO_RESULT_FOUND_FOR_ORDER_STATUS = "No Order Status found for web";
	public static final String ERROR_MESSAGE_NO_RESULT_FOUND_FOR_REFERENCE_NUMBER = "No reference number found in the database";

	public static final String ERROR_MESSAGE_ADD_PAYMENT_GATEWAY_FOR_ORDERSOURCEGROUP = "Please add Payment Gateway Type for Order source group : ";
	public static final String ERROR_MESSAGE_MULTIPLE_PAYMENT_GATEWAY_FOR_ORDERSOURCEGROUP = "More than one Payment Gateway Type found for Order source group : ";
	
	public static final String ERROR_CODE_EMPLOYEES_STILL_CLOCKED_IN = "EM001";
	public static final String ERROR_MESSAGE_EMPLOYEES_STILL_CLOCKED_IN = "Please clocked out all employees before tip calculation";
	
	public final static String ERROR_MESSAGE_ITEM_NOT_AVAILABLE_DISPLAY_MESSAGE = "requested quantity  is not available at the moment, please check available quantity";
	public final static String ERROR_MESSAGE_ITEM_PARTLY_AVAILABLE_DISPLAY_MESSAGE = "left. Please update your requested quantity";
	
	public final static String ONLY_CLOCKOUT_OPERATION_ALLOWED = "Delete option not allowed! Please Clock Out the Employee";
	public final static String DELETE_OPTION_NOT_ALLOWED = "Delete option not allowed!";
	
	 
	
}
