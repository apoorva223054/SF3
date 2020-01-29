/**
sync * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
import javax.ws.rs.core.Response;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.nirvana.services.tipping.NiravanaXpTippingBean;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
/*import com.nirvanaxp.common.utils.EncryptionDecryption;*/
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.global.types.entities.DriverOrder;
import com.nirvanaxp.global.types.entities.DriverOrder_;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.HTTPClient;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterUtility;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.email.ReceiptPDFFormat;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.exceptions.ewards.Charge;
import com.nirvanaxp.services.exceptions.ewards.Customer;
import com.nirvanaxp.services.exceptions.ewards.CustomerPacket;
import com.nirvanaxp.services.exceptions.ewards.EwardsBillCancellationPacket;
import com.nirvanaxp.services.exceptions.ewards.Item;
import com.nirvanaxp.services.exceptions.ewards.Redemption;
import com.nirvanaxp.services.exceptions.ewards.Tax;
import com.nirvanaxp.services.exceptions.ewards.Transaction;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.BatchDetailPacket;
import com.nirvanaxp.services.jaxrs.packets.BatchDetailUpdatePacket;
import com.nirvanaxp.services.jaxrs.packets.DepartmentPacket;
import com.nirvanaxp.services.jaxrs.packets.DriverOrderPacket;
import com.nirvanaxp.services.jaxrs.packets.DriverOrderPostPacket;
import com.nirvanaxp.services.jaxrs.packets.DriverOrderSummaryPacket;
import com.nirvanaxp.services.jaxrs.packets.EmployeeMasterPacket;
import com.nirvanaxp.services.jaxrs.packets.GetOrderPaymentDetailForBatchSettlePacket;
import com.nirvanaxp.services.jaxrs.packets.JobRolesPacket;
import com.nirvanaxp.services.jaxrs.packets.KDSToOrderDetailItemStatusPacket;
import com.nirvanaxp.services.jaxrs.packets.NotPrintedOrderDetailItemsToPrinterPacket;
import com.nirvanaxp.services.jaxrs.packets.OperationalShiftSchedulePacket;
import com.nirvanaxp.services.jaxrs.packets.OrderHeaderSourcePacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacketForOrderTransfer;
import com.nirvanaxp.services.jaxrs.packets.OrderPaymentDetailsWithOrderHeaderLocationsIdPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderToKDSPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderTransferPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.PrintQueuePacket;
import com.nirvanaxp.services.jaxrs.packets.StorageTypePacket;
import com.nirvanaxp.services.jaxrs.packets.TipClassPacket;
import com.nirvanaxp.services.jaxrs.packets.TipPoolBasisPacket;
import com.nirvanaxp.services.jaxrs.packets.TipPoolPacket;
import com.nirvanaxp.services.jaxrs.packets.TipPoolRulesPacket;
import com.nirvanaxp.services.jaxrs.packets.TipSavedPacket;
import com.nirvanaxp.services.util.email.EmailHelper;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.storeForward.PaymentBatchManager;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.OrderLocking;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.Category_;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem_;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter_;
import com.nirvanaxp.types.entities.catalog.items.StorageType;
import com.nirvanaxp.types.entities.concurrent.ProcessLock;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.Discount_;
import com.nirvanaxp.types.entities.email.PrintQueue;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationBatchTiming;
import com.nirvanaxp.types.entities.locations.LocationBatchTiming_;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.LocationSetting_;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.OperationalShiftSchedule;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderHeader_;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail_;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentType;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.tip.Department;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.tip.JobRoles;
import com.nirvanaxp.types.entities.tip.TipClass;
import com.nirvanaxp.types.entities.tip.TipPool;
import com.nirvanaxp.types.entities.tip.TipPoolBasis;
import com.nirvanaxp.types.entities.tip.TipPoolRules;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.User_;
import com.nirvanaxp.types.entity.snssms.SmsConfig;
import com.nirvanaxp.user.utility.GlobalUserUtil;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.user.utility.UserManagementServiceBean;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * se The Class OrderService.
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class OrderService extends AbstractNirvanaService {

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The Constant logger. */
	private final static NirvanaLogger logger = new NirvanaLogger(OrderService.class.getName());

	/** The row count. */
	int rowCount = 88;

	/** The row shipping count. */
	int rowShippingCount = 95;

	/** The row billing count. */
	int rowBillingCount = 108;

	/** The row shipping id count. */
	int rowShippingIdCount = 7;

	/** The row billing id count. */
	int rowBillingIdCount = 8;

	/** The Constant SQL_FOR_ORDER_PAYMENT_DETAIL. */
	private static final String SQL_FOR_ORDER_PAYMENT_DETAIL = new StringBuilder()
			.append("opd.id as  opd_id,opd.order_header_id as opd_order_header_id,opd.payment_method_id as opd_payment_method_id,")
			.append("opd.payment_transaction_type_id as opd_payment_transaction_type_id,opd.seat_id as opd_seat_id,opd.pn_ref as opd_pn_ref, opd.host_ref as opd_host_ref,")
			.append("opd.date as opd_date,opd.time as opd_time,opd.register as opd_register,opd.amount_paid as opd_amount_paid,opd.balance_due as opd_balance_due,")
			.append("opd.total_amount as opd_total_amount,opd.card_number opd_card_number,opd.expiry_month as opd_expiry_month,opd.expiry_year as opd_expiry_year,")
			.append("opd.security_code as opd_security_code,opd.auth_amount as opd_auth_amount,opd.settled_amount as opd_settled_amount,opd.tip_amount as opd_tip_amount,")
			.append("opd.auth_code as opd_auth_code,opd.pos_entry as opd_pos_entry,opd.batch_number as opd_batch_number,opd.avs_response as opd_avs_response,opd.cv_result as opd_cv_result,")
			.append("opd.cv_message as opd_cv_message,opd.result as opd_result,opd.message as opd_message,opd.comments as opd_comments,opd.is_refunded as opd_is_refunded,")
			.append("opd.created as opd_created,opd.created_by as opd_created_by,opd.updated as opd_updated,opd.updated_by as opd_updated_by,opd.signature_url as opd_signature_url,")
			.append("opd.transaction_status_id as opd_transaction_status_id,opd.cash_tip_amt as opd_cash_tip_amt,opd.creditcard_tip_amt as opd_creditcard_tip_amt,")
			.append("opd.change_due as opd_change_due,opd.card_type as opd_card_type,opd.acq_ref_data as opd_acq_ref_data,")
			.append("opd.order_source_group_to_paymentgatewaytype_id as opd_order_source_group_to_paymentgatewaytype_id,opd.order_source_to_paymentgatewaytype_id as opd_order_source_to_paymentgatewaytype_id")
			.append(",pm.id as pm_id,pm.payment_method_type_id as pm_payment_method_type_id,pm.name as pm_name,pm.display_name as pm_display_name, pm.description as pm_description,")
			.append("pm.locations_id as pm_locations_id,pm.status as pm_status,pm.is_active as pm_is_active,pm.display_sequence as pm_display_sequence, pm.created as pm_created,")
			.append("pm.created_by as pm_created_by,pm.updated as pm_updated,pm.updated_by as pm_updated_by,")
			.append("ptt.id as ptt_id,ptt.name as ptt_name,ptt.display_name as ptt_display_name,ptt.display_sequence as ptt_display_sequence,ptt.status as ptt_status,ptt.")
			.append("locations_id as ptt_locations_id,ptt.created as ptt_created,ptt.created_by as ptt_created_by,ptt.updated as ptt_updated,ptt.updated_by as ptt_updated_by,")
			.append("ts.id as ts_id,ts.name as ts_name,ts.display_name as ts_display_name,ts.display_sequence as ts_display_sequence,ts.paymentgateway_type_id as ts_paymentgateway_type_id , ")
			.append("ts.created as ts_created,ts.created_by as ts_created_by,ts.updated as ts_updated,ts.updated_by ts_updated_by,ts.status as ts_status,")
			.append("pgt.id as pgt_id,pgt.name as pgt_name, pgt.display_name as pgt_display_name,pgt.created as pgt_created, pgt.created_by as pgt_created_by, ")
			.append("pgt.updated as pgt_updated, pgt.updated_by as pgt_updated_by, pgt.status as pgt_status, pgt.location_id as pgt_location_id, ")
			.append("pgt.display_sequence as pgt_display_sequence,opd.payementgateway_id as opd_payementgateway_id,opd.host_ref_str as opd_host_ref_str,opd.invoice_number as opd_invoice_number,opd.cheque_tip as opd_cheque_tip, ")
			.append("oh.id as oh_id,oh.ip_address as oh_ip_address,oh.reservations_id as oh_reservations_id,oh.users_id as oh_users_id,oh.order_status_id as oh_order_status_id,")
			.append("oh.order_source_id as oh_order_source_id, oh.locations_id as oh_locations_id,oh.address_shipping_id as oh_address_shipping_id,oh.address_billing_id as oh_address_billing_id,")
			.append("oh.point_of_service_count as oh_point_of_service_count,oh.price_extended as oh_price_extended,oh.price_gratuity as oh_price_gratuity,")
			.append("oh.price_tax_1 as oh_price_tax_1,oh.price_tax_2 as oh_price_tax_2,oh.price_tax_3 as oh_price_tax_3,oh.price_tax_4 as oh_price_tax_4,oh.discounts_name as oh_discounts_name,oh.")
			.append("discounts_type_id as oh_discounts_type_id,oh.discounts_type_name as oh_discounts_type_name,oh.discounts_value as oh_discounts_value,")
			.append("oh.discounts_id as oh_discounts_id,oh.payment_ways_id as oh_payment_ways_id,oh.split_count as oh_split_count,oh.price_discount as oh_price_discount,")
			.append("oh.service_tax as oh_service_tax,oh.sub_total as oh_sub_total,oh.gratuity as oh_gratuity,oh.total as oh_total,oh.amount_paid as oh_amount_paid,")
			.append("oh.balance_due as oh_balance_due,oh.created as oh_created,oh.created_by as oh_created_by,oh.updated as oh_updated,")
			.append("oh.updated_by as oh_updated_by,oh.date as oh_date,oh.verification_code as oh_verification_code,oh.qrcode as oh_qrcode,oh.first_name as oh_first_name,")
			.append("oh.last_name as oh_last_name,oh.server_id as oh_server_id,oh.cashier_id as oh_cashier_id, oh.void_reason_id as oh_void_reason_id,oh.open_time as oh_open_time,")
			.append("oh.close_time as oh_close_time,oh.tax_name_1 as oh_tax_name_1,oh.tax_name_2 as oh_tax_name_2,oh.tax_name_3 as oh_tax_name_3,oh.tax_name_4 as oh_tax_name_4,")
			.append("oh.tax_display_name_1 as oh_tax_display_name_1, oh.tax_display_name_2 as oh_tax_display_name_2, oh.tax_display_name_3 as oh_tax_display_name_3, ")
			.append("oh.tax_display_name_4 as oh_tax_display_name_4,oh.tax_rate_1 as oh_tax_rate_1, oh.tax_rate_2 as oh_tax_rate_2,oh.tax_rate_3 as oh_tax_rate_3,")
			.append("oh.tax_rate_4 as oh_tax_rate_4,oh.total_tax as oh_total_tax,oh.is_gratuity_applied as oh_is_gratuity_applied, ")
			.append("oh.round_off_total as oh_round_off_total, oh.session_id as oh_session_id ").toString();

	/**
	 * Gets the order by id.
	 *
	 * @param id
	 *            the id
	 * @param sessionId
	 *            the session id
	 * @return the order by id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	@GET
	@Path("/getOrderById/{id}")
	public String getOrderById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderById(em, id);

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the orders by location.
	 *
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the orders by location
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	@GET
	@Path("/getOrdersByLocation/{locationId}")
	public String getOrdersByLocation(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
			Root<OrderHeader> r = criteria.from(OrderHeader.class);
			TypedQuery<OrderHeader> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(OrderHeader_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the orders payment by order header id.
	 *
	 * @param orderHeaderId
	 *            the order header id
	 * @param sessionId
	 *            the session id
	 * @return the orders payment by order header id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	@GET
	@Path("/getOrdersPaymentByOrderHeaderId/{orderHeaderId}")
	public String getOrdersPaymentByOrderHeaderId(@PathParam("orderHeaderId") int orderHeaderId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderPaymentDetail> criteria = builder.createQuery(OrderPaymentDetail.class);
			Root<OrderPaymentDetail> orderPaymentDetail = criteria.from(OrderPaymentDetail.class);
			TypedQuery<OrderPaymentDetail> query = em.createQuery(criteria.select(orderPaymentDetail)
					.where(builder.equal(orderPaymentDetail.get(OrderPaymentDetail_.orderHeaderId), orderHeaderId)));
			List<OrderPaymentDetail> orderPaymentDetails = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(orderPaymentDetails);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all orders with user by date location walkin phone.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param walkinId
	 *            the walkin id
	 * @param phoneId
	 *            the phone id
	 * @param sessionId
	 *            the session id
	 * @return the all orders with user by date location walkin phone
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrdersWithUserByDateLocationWalkinPhone/{date}/{locationId}/{walkinId}/{phoneId}")
	public String getAllOrdersWithUserByDateLocationWalkinPhone(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @PathParam("walkinId") int walkinId,
			@PathParam("phoneId") int phoneId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getAllOrdersWithUserByDateLocationWalkinPhone(?,?,?,?,?,?,?)")
					.setParameter(1, locationId).setParameter(2, date).setParameter(3, date + "?")
					.setParameter(4, walkinId).setParameter(5, phoneId).setParameter(6, startDate)
					.setParameter(7, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest).convertToJsonString(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the open orders with user info for take out.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param walkinId
	 *            the walkin id
	 * @param phoneId
	 *            the phone id
	 * @param sessionId
	 *            the session id
	 * @return the open orders with user info for take out
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOpenOrdersWithUserByDateLocationWalkinPhone/{date}/{locationId}/{walkinId}/{phoneId}")
	public String getOpenOrdersWithUserInfoForTakeOut(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @PathParam("walkinId") int walkinId,
			@PathParam("phoneId") int phoneId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getOpenOrdersWithUserByDateLocationWalkinPhone(?,?,?,?,?,?,?) ")
					.setParameter(1, locationId).setParameter(2, date).setParameter(3, date + "%")
					.setParameter(4, walkinId).setParameter(5, phoneId).setParameter(6, startDate)
					.setParameter(7, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);

			return new JSONUtility(httpRequest).convertToJsonString(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the open orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the open orders with user by date and location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOpenOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getOpenOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getOpenOrdersWithUserByDateAndLocation(?,?,?,?,?) ")
					.setParameter(1, locationId).setParameter(2, date).setParameter(3, date + "%")
					.setParameter(4, startDate).setParameter(5, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all open orders with user for location.
	 *
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all open orders with user for location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOpenOrdersWithUserForLocation/{locationId}")
	public String getAllOpenOrdersWithUserForLocation(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllOpenOrdersWithUserForLocation(?) ")
					.setParameter(1, locationId).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all open orders with user for location.
	 *
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all open orders with user for location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOpenProductionOrdersWithUserForLocation/{locationId}")
	public String getAllOpenProductionOrdersWithUserForLocation(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllOpenProductionOrdersWithUserForLocation(?) ")
					.setParameter(1, locationId).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the closed orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the closed orders with user by date and location
	 * @throws Exception
	 *             the exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	@GET
	@Path("/getClosedOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getClosedOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception, NirvanaXPException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderManagementServiceBean bean = new OrderManagementServiceBean();

			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getClosedOrdersWithUserByDateAndLocation(?,?,?) ")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);

			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the closed orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the closed orders with user by date and location
	 * @throws Exception
	 *             the exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	@GET
	@Path("/getClosedProductionOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getClosedProductionOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception, NirvanaXPException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderManagementServiceBean bean = new OrderManagementServiceBean();

			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getClosedProductionOrdersWithUserByDateAndLocation(?,?,?) ")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);

			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Insert order.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/add")
	public String insertOrder(OrderPacket orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;

		String json = null;
		try {
			// create json packet for store forward

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader header = new OrderServiceForPost().insertOrder(httpRequest, sessionId, em, orderPacket, false);
			try {
				new PrinterUtility().insertIntoPrintQueueForPartySizeUpdate(httpRequest, em, header,
						orderPacket.getLocationId(), false);
			} catch (Exception e) {
				logger.severe(e);
			}

			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(header);
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Creates the new batch.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/createNewBatch")
	public String createNewBatch(BatchDetailPacket packet) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "BatchDetailPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
			BatchDetail bd = batchManager.createNewBatchDetails(httpRequest, em,
					packet.getBatchDetail().getLocationId(), packet);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(bd);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/createBatchForSynch")
	public String createBatchForSynch(BatchDetailPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
			tx = em.getTransaction();
			tx.begin();
			BatchDetail bd = batchManager.createNewBatch(em, packet.getBatchDetail());
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(bd);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Insert nirvana XP order.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addNirvanaXPOrder")
	public String insertNirvanaXPOrder(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;

		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			OrderHeader header = new OrderServiceForPost().insertNirvanaXPOrder(httpRequest, sessionId, em, orderPacket,
					true);
			orderPacket.setOrderHeader(header);
			tx.commit();
			// call synchPacket for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order batch.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderBatch")
	public String updateOrderBatch(OrderPacket orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;

		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward

			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			OrderHeader header = new OrderServiceForPost().insertNirvanaXPOrder(httpRequest, sessionId, em, orderPacket,
					true);
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(header);
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Insert order for take out.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderForTakeOut")
	public String insertOrderForTakeOut(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward

			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().insertOrder(httpRequest, sessionId, em, orderPacket,
					true);
			orderPacket.setOrderHeader(orderHeader);
			tx.commit();

			// call synchPacket for store forward

			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/update")
	public String updateOrder(OrderPacket orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			OrderHeader orderHeader = new OrderServiceForPost().updateOrder(httpRequest, em, orderPacket,
					httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			orderPacket.setOrderHeader(orderHeader);
			tx.commit();
			String result = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(orderHeader);
			// call synchPacket for store forward
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return result;
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order status.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderStatus")
	public String updateOrderStatus(OrderPacket orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderStatus(httpRequest, em, orderPacket);
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, orderHeader.getOrderStatusId());
			if (orderStatus.getName().equals("Void Order")) {
				return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
			}
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order schedule date.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderScheduleDate")
	public String updateOrderScheduleDate(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().updateOrderScheduleDate(httpRequest, em, orderPacket);
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Merge order.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/mergeOrder")
	public String mergeOrder(OrderPacket orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().mergeOrder(httpRequest, em, orderPacket);
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Switch order location.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/switchOrderLocation")
	public String switchOrderLocation(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader;

			orderHeader = new OrderServiceForPost().switchOrderLocation(httpRequest, em, orderPacket);
			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, orderHeader.getId());
			try {
				new PrinterUtility().insertIntoPrintQueueForPartySizeUpdate(httpRequest, em, header,
						orderPacket.getLocationId(), false);
			} catch (Exception e) {
				// todo shlok need
				// handel proper exception
				logger.severe(e);
			}
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Change point of service count.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/changePointOfServiceCount")
	public String changePointOfServiceCount(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().changePointOfServiceCount(httpRequest, em, orderPacket);
			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, orderHeader.getId());
			try {
				new PrinterUtility().insertIntoPrintQueueForPartySizeUpdate(httpRequest, em, header,
						orderPacket.getLocationId(), true);
			} catch (Exception e) {
				// todo shlok need
				// handel proper exception

				logger.severe(e);
			}
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order payment.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderPayment")
	public String updateOrderPayment(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			boolean isDuplicatePacketCheck = false;
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPayment(httpRequest, em, orderPacket,
					isDuplicatePacketCheck, sessionId, false);
			try {
				new PrinterUtility().insertIntoPrintQueueForCancelOrderAndQuickPay(httpRequest, em, orderHeader,
						orderPacket.getLocationId());
			} catch (Exception e) {
				// todo shlok need
				// handel proper exception

				logger.severe(e);
			}
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the order for QSR with item details.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @param schemaName
	 *            the schema name
	 * @return the string
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderForQSRWithItemDetails")
	public String addOrderForQSRWithItemDetails(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@CookieParam(value = "SchemaName") String schemaName) throws NumberFormatException, Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, orderPacket,
					schemaName);
			tx = em.getTransaction();
			tx.begin();
			boolean duplicatePacketCheck = false;
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPayment(httpRequest, em, orderPacket,
					duplicatePacketCheck, sessionId, false);
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the order with user info by id.
	 *
	 * @param id
	 *            the id
	 * @param sessionId
	 *            the session id
	 * @return the order with user info by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderWithUserInfoById/{id}")
	public String getOrderWithUserInfoById(@PathParam("id") int id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getOrderWithUserInfoById(?) ").setParameter(1, id)
					.getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			OrderWithUsers users = null;
			// only one order
			if (orderWithUsersList != null && orderWithUsersList.size() == 1) {
				users = orderWithUsersList.get(0);
			}

			return new JSONUtility(httpRequest).convertToJsonString(users);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all open takeout orders with user by date S ource group ID.
	 *
	 * @param locationId
	 *            the location id
	 * @param sourceGroupID
	 *            the source group ID
	 * @param sessionId
	 *            the session id
	 * @return the all open takeout orders with user by date S ource group ID
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOpenTakeoutOrdersWithUserByDateSourceGroupID/{locationId}/{sourceGroupID}")
	public String getAllOpenTakeoutOrdersWithUserByDateSOurceGroupID(@PathParam("locationId") String locationId,
			@PathParam("sourceGroupID") String sourceGroupID,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getAllOpenTakeoutOrdersWithUserByDateSourceGroupID(?,?)")
					.setParameter(1, locationId).setParameter(2, sourceGroupID).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the orders payment by created date.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the orders payment by created date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrdersPaymentByDate/{date}/{locationId}")
	public String getOrdersPaymentByCreatedDate(@PathParam("date") Date date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {

		EntityManager em = null;
		List<OrderPaymentDetailsWithOrderHeaderLocationsIdPacket> headerLocationsIdPackets = new ArrayList<OrderPaymentDetailsWithOrderHeaderLocationsIdPacket>();
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "SELECT  " + SQL_FOR_ORDER_PAYMENT_DETAIL
					+ " , oh.locations_id FROM order_payment_details opd left join payment_method pm on opd.payment_method_id= pm.id "
					+ "left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ "left join order_header oh on oh.id=opd.order_header_id "
					+ "left join transaction_status ts on ts.id=opd.transaction_status_id left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ "CROSS JOIN locations location1_ WHERE oh.locations_id=location1_.id "
					+ "and (location1_.locations_id  in (select location0_.id as id1 from locations location0_ where location0_.locations_id=?)) and opd.date like ?";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
					.setParameter(2, date + "%").getResultList();
			for (Object[] objRow : resultList) {

				// if this has primary key not 0
				OrderPaymentDetailsWithOrderHeaderLocationsIdPacket orderPacket = new OrderPaymentDetailsWithOrderHeaderLocationsIdPacket();
				OrderPaymentDetail paymentDetail = new OrderPaymentDetail();
				paymentDetail.setOrderPaymentDetailsResultSet(objRow);
				orderPacket.setOrderPaymentDetail(paymentDetail);
				orderPacket.setLocationID((String) objRow[90]);

				headerLocationsIdPackets.add(orderPacket);

			}
			return new JSONUtility(httpRequest).convertToJsonString(headerLocationsIdPackets);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order payment details by user id location date.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderPaymentDetailsByUserIdLocationDate/{userId}/{locationId}/{date}")
	public String getAllOrderPaymentDetailsByUserIdLocationDate(@PathParam("userId") String userId,
			@PathParam("locationId") String locationId, @PathParam("date") Date date,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(orderManagementServiceBean
					.getAllOrderPaymentDetailsByUserIdLocationDate(httpRequest, em, userId, locationId, date));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all payment gateway type.
	 *
	 * @param sessionId
	 *            the session id
	 * @return the all payment gateway type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/allPaymentGatewayType")
	public String getAllPaymentGatewayType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();

			CriteriaQuery<PaymentGatewayType> criteria = builder.createQuery(PaymentGatewayType.class);
			Root<PaymentGatewayType> r = criteria.from(PaymentGatewayType.class);
			TypedQuery<PaymentGatewayType> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order payment details by user id location date and updated.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param updated
	 *            the updated
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location date and
	 *         updated
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderPaymentDetailsByUserIdLocationDateAndUpdated/{userId}/{locationId}/{date}/{updated}")
	public String getAllOrderPaymentDetailsByUserIdLocationDateAndUpdated(@PathParam("userId") String userId,
			@PathParam("locationId") String locationId, @PathParam("date") String date,
			@PathParam("updated") String updated,
			@CookieParam(INirvanaService.NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderManagementServiceBean managementService = new OrderManagementServiceBean();
			List<OrderHeader> orderHeaders = managementService.getAllOrderPaymentDetailsByUserIdLocationDateAndUpdated(
					httpRequest, em, userId, locationId, date, updated);
			return new JSONUtility(httpRequest).convertToJsonString(orderHeaders);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all orders with user by location and updated.
	 *
	 * @param locationId
	 *            the location id
	 * @param updatedDate
	 *            the updated date
	 * @param sessionId
	 *            the session id
	 * @return the all orders with user by location and updated
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrdersWithUserByLocationAndUpdated/{locationId}/{updatedDate}/")
	public String getAllOrdersWithUserByLocationAndUpdated(@PathParam("locationId") String locationId,
			@PathParam("updatedDate") String updatedDate, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderManagementServiceBean managementService = new OrderManagementServiceBean();
			return managementService.getAllOrdersWithUserByLocationAndUpdated(httpRequest, em, updatedDate, locationId);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all payment type.
	 *
	 * @param sessionId
	 *            the session id
	 * @return the all payment type
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPaymentType")
	public String getAllPaymentType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			;
			CriteriaQuery<PaymentType> criteria = builder.createQuery(PaymentType.class);
			Root<PaymentType> r = criteria.from(PaymentType.class);
			TypedQuery<PaymentType> query = em.createQuery(criteria.select(r));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the void orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the void orders with user by date and location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getVoidOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getVoidOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllVoidOrdersWithUserByDateAndLocation(?,?,? )")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);

			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all void orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all void orders with user by date and location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllVoidOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getAllVoidOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllVoidOrdersWithUserByDateAndLocation(?,?,?)")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all closed orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all closed orders with user by date and location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllClosedOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getAllClosedOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getClosedOrdersWithUserByDateAndLocation( ?,?,?)")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the inventory closed orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the inventory closed orders with user by date and location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getInventoryClosedOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getInventoryClosedOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getInventoryClosedOrdersWithUserByDateAndLocation( ?,?,?)")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order payment for batch settle.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderPaymentForBatchSettle")
	public String updateOrderPaymentForBatchSettle(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPaymentForBatchSettle(httpRequest, em,
					orderPacket);
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order source.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return String of update source packet @
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	@POST
	@Path("/updateOrderSource")
	public String updateOrderSource(OrderHeaderSourcePacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			orderPacket = new OrderServiceForPost().updateOrderSource(httpRequest, em, orderPacket, false);
			tx.commit();

			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderHeaderSourcePacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket.getOrderHeaderSource());
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order status to paid.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderStatusToPaid")
	public String updateOrderStatusToPaid(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			int needToSendSNS = orderPacket.getLocalServerURL();
			String oldStatusId = "";
			if (orderPacket.getOrderHeader().getId() != null) {
				OrderHeader o = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderHeader().getId());
				oldStatusId = o.getOrderStatusId();
			}
			List<User> users = null;
			// sending mail to all user according to their role
			// in below code we are searching for user according to role and
			// their function and after that we are sending eod mail to all
			// the users
			users = new EmailHelper().sendEmailByFunctionByLocationIdWithQuery(em, orderPacket.getLocationId(),
					"Order Confirmation Email");

			String ccArray = "";

			if (users != null && users.size() > 0) {
				for (User user : users) {
					if (user.getEmail() != null && !user.getEmail().isEmpty()) {
						if (ccArray.length() != 0) {
							ccArray = ccArray + "," + user.getEmail();
						} else {
							ccArray = ccArray + user.getEmail();
						}
					}

				}
			}

			OrderHeader orderHeader = new OrderServiceForPost().updateOrderStatusToPaid(httpRequest, em, orderPacket,
					ccArray);
			tx.commit();

			users = new EmailHelper().sendEmailByFunctionByLocationIdWithQuery(em, orderPacket.getLocationId(),
					"Order Confirmation SMS");
			if (users != null && users.size() > 0) {
				// sendding mail to all the user

				for (User user : users) {
					synchronized (user) {
						if (user.getPhone() != null && !user.getPhone().isEmpty()) {
							sendSNSByNumber(em, user.getId(), orderHeader.getOrderStatusId(),
									orderPacket.getLocationId(), orderHeader.getBalanceDue(),
									orderHeader.getOrderNumber(), orderHeader.getAmountPaid(), orderHeader, sessionId,
									orderPacket.getMerchantId(), orderPacket.getClientId(), true, needToSendSNS,
									oldStatusId);
						}
					}

				}

			}
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.INirvanaService#isAlive()
	 */
	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger() {
		return logger;
	}

	/**
	 * Gets the all order payment details by user id location batch wise.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location batch wise
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderPaymentDetailsByUserIdLocationBatchWise/{userId}/{locationId}")
	public String getAllOrderPaymentDetailsByUserIdLocationBatchWise(@PathParam("userId") String userId,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(
					orderManagementServiceBean.getAllOrderPaymentDetailsByUserIdLocationBatchWise(httpRequest, em,
							userId, locationId, sessionId));
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order payment details by user id location batch wise for
	 * pick date.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param pickDate
	 *            the pick date
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location batch wise for
	 *         pick date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPickDate/{userId}/{locationId}/{pickDate}")
	public String getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPickDate(@PathParam("userId") String userId,
			@PathParam("locationId") String locationId, @PathParam("pickDate") String pickDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(
					orderManagementServiceBean.getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPickDate(
							httpRequest, em, userId, locationId, pickDate));
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order payment details by user id location batch wise for
	 * payment gateway type specific.
	 *
	 * @param getOrderPaymentDetailForBatchSettlePacket
	 *            the get order payment detail for batch settle packet
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location batch wise for
	 *         payment gateway type specific
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPaymentGatewayTypeSpecific")
	public String getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPaymentGatewayTypeSpecific(
			GetOrderPaymentDetailForBatchSettlePacket getOrderPaymentDetailForBatchSettlePacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
			// call synchPacket for store forward
			return new JSONUtility(httpRequest).convertToJsonString(orderManagementServiceBean
					.getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPaymentGatewayTypeId(httpRequest, em,
							getOrderPaymentDetailForBatchSettlePacket.getUsersId(),
							getOrderPaymentDetailForBatchSettlePacket.getLocationsId(),
							getOrderPaymentDetailForBatchSettlePacket.getPaymentGatewayIds(), sessionId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order payment for tip save.
	 *
	 * @param tipSavedPacket
	 *            the tip saved packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderPaymentForTipSave")
	public String updateOrderPaymentForTipSave(TipSavedPacket tipSavedPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(tipSavedPacket, "TipSavedPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, tipSavedPacket);

			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPaymentForTipSaved(httpRequest, em,
					tipSavedPacket);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, tipSavedPacket.getLocationId(),
					Integer.parseInt(tipSavedPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Send email for order.
	 *
	 * @param orderId
	 *            the order id
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param emailAddress
	 *            the email address
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/sendEmailForOrder/{orderId}/{userId}/{locationId}/{emailAddress}")
	public String sendEmailForOrder(@PathParam(value = "orderId") String orderId,
			@PathParam(value = "userId") String userId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "emailAddress") String emailAddress,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager localEM = null;
		EntityManager globalEM = null;
		EntityTransaction tx = null;
		try {
			localEM = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", localEM,
					OrderHeader.class, orderId);
			// 0 because mail sending from dine in
			User user = null;
			UserManagementServiceBean userManagementServiceBean = new UserManagementServiceBean(httpRequest, localEM);
			try {
				user = userManagementServiceBean.getUserByEmail(emailAddress);
			} catch (NoResultException e) {
				logger.severe(httpRequest, "User not present in database so need to create user : " + emailAddress);
			}
			try {
				tx = localEM.getTransaction();
				tx.begin();

				String data = receiptPDFFormat.createReceiptPDFString(localEM, httpRequest, orderId, 1, false, false)
						.toString();

				EmailTemplateKeys.sendOrderConfirmationEmailToCustomer(httpRequest, localEM, locationId,
						header.getUsersId(), header.getUpdatedBy(), data, EmailTemplateKeys.ORDER_CONFIRMATION,
						header.getOrderNumber(), emailAddress);
				// change by vaibhav,suggested by Kris, email-date- Dec 03, 2015
				// 12:57 am
				if (user == null && header.getUsersId() == null && header.getUsersId() == null) {

					user = createNewUserBeforeSendingEmail(localEM, globalEM, emailAddress, header.getCreatedBy(),
							header.getUpdatedBy(), locationId, null);
					header.setUsersId(user.getId());
					localEM.merge(header);
				} else {

					if (header.getReservationsId() != null) {
						Reservation reservation = new CommonMethods().getReservationById(httpRequest, localEM,
								header.getReservationsId());
						// for takeout and delivery we do not have reservations
						if (reservation != null) {
							if (user == null) {
								user = createNewUserBeforeSendingEmail(localEM, globalEM, emailAddress,
										header.getCreatedBy(), header.getUpdatedBy(), locationId, null);
							}
							if (reservation != null && reservation.getEmail() == null) {
								reservation.setEmail(emailAddress);
								localEM.merge(reservation);
							}

						}
					}

				}
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	/**
	 * Creates the new user before sending email.
	 *
	 * @param localEM
	 *            the local EM
	 * @param globalEM
	 *            the global EM
	 * @param emailAddress
	 *            the email address
	 * @param createdBy
	 *            the created by
	 * @param updatedBy
	 *            the updated by
	 * @param locationId
	 *            the location id
	 * @return the user
	 * @throws Exception
	 */
	User createNewUserBeforeSendingEmail(EntityManager localEM, EntityManager globalEM, String emailAddress,
			String createdBy, String updatedBy, String locationId, PostPacket packet) throws Exception {
		User user = null;

		GlobalUserUtil globalUserUtil = new GlobalUserUtil();
		com.nirvanaxp.global.types.entities.User user1 = globalUserUtil.getUserByEmail(globalEM, emailAddress);
		if (user1 != null) {
			user = new User();
			user.setAuthPin(user1.getAuthPin());
			user.setComments("");
			user.setPhone(null);
			user.setDateofbirth(user1.getDateofbirth());
			user.setEmail(emailAddress);
			user.setFirstName(user1.getFirstName());
			user.setLastName(user1.getLastName());
			user.setPassword(user1.getPassword());
			user.setStatus("A");
			user.setUserColor("");
			user.setUsername(user1.getUsername());
			user.setVisitCount(0);
			user.setGlobalUsersId(user1.getId());
			user.setCreatedBy(createdBy);
			user.setUpdatedBy(updatedBy);
		} else {
			user = new User();
			user.setAuthPin("");
			user.setComments("");
			user.setDateofbirth("");
			user.setEmail(emailAddress);
			user.setFirstName("");
			user.setLastName("");
			user.setPassword("");
			user.setStatus("A");
			user.setUserColor("");
			user.setUsername(emailAddress);
			user.setVisitCount(0);
			user.setCreatedBy(createdBy);
			user.setUpdatedBy(updatedBy);
		}

		GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
		user = globalUsermanagement.addUserToLocalDatabaseProgramatically(httpRequest, globalEM, localEM, user,
				locationId, packet);

		return user;
	}

	/**
	 * Send email for order payment details.
	 *
	 * @param orderPaymentId
	 *            the order payment id
	 * @param locationId
	 *            the location id
	 * @param emailAddress
	 *            the email address
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/sendEmailForOrderPaymentDetails/{orderPaymentId}/{locationId}/{emailAddress}")
	public String sendEmailForOrderPaymentDetails(@PathParam(value = "orderPaymentId") String orderPaymentId,
			@PathParam(value = "locationId") String locationId, @PathParam(value = "emailAddress") String emailAddress,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// starting transaction to make single transaction in all services
			// :- By Ap 2015-12-29
			tx = em.getTransaction();
			tx.begin();
			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			OrderPaymentDetail orderPaymentDetail = (OrderPaymentDetail) new CommonMethods()
					.getObjectById("OrderPaymentDetail", em, OrderPaymentDetail.class, orderPaymentId);
			OrderManagementServiceBean managementServiceBean = new OrderManagementServiceBean();
			OrderHeader orderHeader = managementServiceBean.getOrderById(em, orderPaymentDetail.getOrderHeaderId());

			// 0 because mail sending from dine in
			String data = receiptPDFFormat
					.createPaymentReceiptPDFString(em, httpRequest, orderPaymentDetail, orderHeader, 0).toString();
			// as we are passing email address so dont need
			EmailTemplateKeys.sendOrderPaymentConfirmationEmailToCustomer(httpRequest, em, locationId,
					orderHeader.getUsersId(), orderHeader.getUpdatedBy(), data,
					EmailTemplateKeys.ORDER_PAYMENT_CONFIRMATION, null, emailAddress);
			// commiting transaction
			tx.commit();

			return new JSONUtility(httpRequest).convertToJsonString(true);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order for seatwise.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderForSeatwise")
	public String updateOrderForSeatwise(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderForSeatwise(httpRequest, em, orderPacket,
					httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			String result = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(orderHeader);
			tx.commit();
			// call synchPacket for store forward
			orderPacket.setOrderHeader(orderHeader);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));
			return result;
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order for duplicate check.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderForDuplicateCheck")
	public String updateOrderForDuplicateCheck(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			int needToSendSNS = orderPacket.getLocalServerURL();
			String oldStatusId = "";
			if (orderPacket.getOrderHeader().getId() != null) {
				OrderHeader o = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderHeader().getId());
				oldStatusId = o.getOrderStatusId();
			}
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderForDuplicateCheck(httpRequest, em,
					orderPacket, httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			orderPacket.setOrderHeader(orderHeader);
			try {
				sendSNSByNumber(em, orderHeader.getUsersId(), orderHeader.getOrderStatusId(),
						orderPacket.getLocationId(), orderHeader.getBalanceDue(), orderHeader.getOrderNumber(),
						orderHeader.getAmountPaid(), orderHeader, sessionId, orderPacket.getMerchantId(),
						orderPacket.getClientId(), false, needToSendSNS, oldStatusId);
			} catch (Exception e) {
				logger.severe(e);
			}

			tx.commit();
			// call synchPacket for store forward
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order payment for duplicate check.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@POST
	@Path("/updateOrderPaymentForDuplicateCheck")
	public String updateOrderPaymentForDuplicateCheck(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			boolean isDuplicatePacketCheck = true;
			int needToSendSNS = orderPacket.getLocalServerURL();
			String oldStatusId = "";
			if (orderPacket.getOrderHeader().getId() != null) {
				OrderHeader o = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderHeader().getId());
				oldStatusId = o.getOrderStatusId();
			}
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPayment(httpRequest, em, orderPacket,
					isDuplicatePacketCheck, sessionId, false);

			sendSNSByNumber(em, orderHeader.getUsersId(), orderHeader.getOrderStatusId(), orderPacket.getLocationId(),
					orderHeader.getBalanceDue(), orderHeader.getOrderNumber(), orderHeader.getAmountPaid(), orderHeader,
					sessionId, orderPacket.getMerchantId(), orderPacket.getClientId(), false, needToSendSNS,
					oldStatusId);

			tx.commit();
			// create json packet for store forward
			orderPacket.setOrderHeader(orderHeader);
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);

			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all batch by date and location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param sessionId
	 *            the session id
	 * @return the all batch by date and location id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws ParseException
	 *             the parse exception
	 */
	@GET
	@Path("/getAllBatchByDateAndLocationId/{locationId}/{date}")
	public String getAllBatchByDateAndLocationId(@PathParam("locationId") String locationId,
			@PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException, ParseException {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(bean.batchDetailList(em, locationId, date));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order payment for under processing transaction.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@POST
	@Path("/updateOrderPaymentForUnderProcessingTransaction")
	public String updateOrderPaymentForUnderProcessingTransaction(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			OrderHeader orderHeader = new OrderServiceForPost()
					.updateOrderPaymentForUnderProcessingTransaction(httpRequest, em, orderPacket);
			tx.commit();
			// call synchPacket for store forward
			// create json packet for store forward
			orderPacket.setOrderHeader(orderHeader);
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the order to card.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderToCard")
	public String addOrderToCard(OrderPacket orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().addOrderToCard(httpRequest, auth_token, em, orderPacket,
					true);
			orderPacket.setOrderHeader(orderHeader);
			tx.commit();
			// call synchPacket for store forward
			// create json packet for store forward
			orderPacket.setOrderHeader(orderHeader);
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Send email testing.
	 *
	 * @param orderId
	 *            the order id
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/sendEmailTesting/{orderId}")
	public String sendEmailTesting(@PathParam(value = "orderId") String orderId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em, OrderHeader.class,
					orderId);
			// 0 because mail sending from dine in

			tx = em.getTransaction();
			tx.begin();

			String data = receiptPDFFormat.createReceiptPDFString(em, httpRequest, orderId, 1, true, false).toString();
			EmailTemplateKeys.sendOrderReceivedEmailToCustomer(httpRequest, em, "1", header.getUsersId(),
					header.getUpdatedBy(), data, EmailTemplateKeys.ORDER_RECEIVED, header.getOrderNumber(), null, null);
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all order by email or phone.
	 *
	 * @param email
	 *            the email
	 * @param phone
	 *            the phone
	 * @param businessId
	 *            the business id
	 * @param sessionId
	 *            the session id
	 * @return the all order by email or phone
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderByEmailOrPhone/{email}/{phone}/{businessId}")
	public String getAllOrderByEmailOrPhone(@PathParam("email") String email, @PathParam("phone") String phone,
			@PathParam("businessId") int businessId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			OrderManagementBeanForCustomerService beanForCustomerService = new OrderManagementBeanForCustomerService(
					httpRequest);
			return new JSONUtility(httpRequest).convertToJsonString(
					beanForCustomerService.getAllOrderByEmailOrPhone(em, email, phone, businessId, false));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all order by email or phone and account id.
	 *
	 * @param email
	 *            the email
	 * @param phone
	 *            the phone
	 * @param accountId
	 *            the account id
	 * @return the all order by email or phone and account id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderByEmailOrPhoneAndAccountId/{email}/{phone}/{accountId}")
	public String getAllOrderByEmailOrPhoneAndAccountId(@PathParam("email") String email,
			@PathParam("phone") String phone, @PathParam("accountId") int accountId) throws Exception {
		EntityManager local = null;
		EntityManager globalEM = null;

		try {
			if (accountId > 0) {
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
				Account account = globalEM.find(Account.class, accountId);
				if (account != null) {
					local = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(account.getSchemaName());
					OrderManagementBeanForCustomerService beanForCustomerService = new OrderManagementBeanForCustomerService(
							httpRequest);
					return new JSONUtility(httpRequest).convertToJsonString(
							beanForCustomerService.getAllOrderByEmailOrPhoneByAccountId(local, email, phone, false));
				} else {
					throw new NirvanaXPException(
							new NirvanaServiceErrorResponse(MessageConstants.ACCOUNT_EXCEPTION_CODE,
									MessageConstants.ACCOUNT_EXCEPTION, MessageConstants.ACCOUNT_EXCEPTION));
				}

			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ACCOUNT_EXCEPTION_CODE,
						MessageConstants.ACCOUNT_EXCEPTION, MessageConstants.ACCOUNT_EXCEPTION));

			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(local);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	/**
	 * Gets the cancel orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the cancel orders with user by date and location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCancelOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getCancelOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllCancelOrdersWithUserByDateAndLocation(?,?,? )")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);

			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all cancel orders with user by date and location.
	 *
	 * @param date
	 *            the date
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all cancel orders with user by date and location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCancelOrdersWithUserByDateAndLocation/{date}/{locationId}")
	public String getAllCancelOrdersWithUserByDateAndLocation(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllCancelOrdersWithUserByDateAndLocation(?,?,?)")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order source for cancel order.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	@POST
	@Path("/updateOrderSourceForCancelOrder")
	public String updateOrderSourceForCancelOrder(OrderHeaderSourcePacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			orderPacket = new OrderServiceForPost().updateOrderSource(httpRequest, em, orderPacket, true);
			tx.commit();
			// call synchPacket for store forward

			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderHeaderSourcePacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket.getOrderHeaderSource());
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update order status for duplicate check.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderStatusForDuplicateCheck")
	public String updateOrderStatusForDuplicateCheck(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			int needToSendSNS = orderPacket.getLocalServerURL();
			String oldStatusId = "";
			if (orderPacket.getOrderHeader().getId() != null) {
				OrderHeader o = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderHeader().getId());
				oldStatusId = o.getOrderStatusId();
			}

			OrderHeader orderHeader = new OrderServiceForPost().updateOrderStatusForDuplicateCheck(httpRequest, em,
					orderPacket);
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, orderHeader.getOrderStatusId());
			if (orderStatus.getName().equals("Cancel Order")) {
				try {
					new PrinterUtility().insertIntoPrintQueueForCancelOrderAndQuickPay(httpRequest, em, orderHeader,
							orderPacket.getLocationId());
				} catch (Exception e) {
					// todo shlok need
					// handel proper exception

					logger.severe(e);
				}
			}
			if (orderStatus.getName().equals("Void Order")) {
				return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
			}

			sendSNSByNumber(em, orderHeader.getUsersId(), orderHeader.getOrderStatusId(), orderPacket.getLocationId(),
					orderHeader.getBalanceDue(), orderHeader.getOrderNumber(), orderHeader.getAmountPaid(), orderHeader,
					sessionId, orderPacket.getMerchantId(), orderPacket.getClientId(), false, needToSendSNS,
					oldStatusId);

			tx.commit();
			// call synchPacket for store forward
			// create json packet for store forward
			orderPacket.setOrderHeader(orderHeader);
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Merge order with items.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/mergeOrderWithItems")
	public String mergeOrderWithItems(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			OrderHeader orderHeader = new OrderServiceForPost().mergeOrderWithItems(httpRequest, em, orderPacket);
			tx.commit();
			// call synchPacket for store forward
			// create json packet for store forward
			orderPacket.setOrderHeader(orderHeader);
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order info for delivery takeout.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderInfoForDeliveryTakeout")
	public String updateOrderInfoForDeliveryTakeout(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			OrderPacket newOrderPacket = new OrderServiceForPost().updateOrderInfoForDeliveryTakeout(httpRequest, em,
					orderPacket);

			tx.commit();
			// call synchPacket for store forward
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(newOrderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(newOrderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update order transfer.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderTransfer")
	public String updateOrderTransfer(OrderPacketForOrderTransfer orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacketForOrderTransfer", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();

			OrderPacketForOrderTransfer newOrderPacket = new OrderServiceForPost().updateOrderTransfer(httpRequest, em,
					orderPacket, tx);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));
			OrderHeader toOrder = new OrderManagementServiceBean().getOrderById(em,
					orderPacket.getToOrderHeader().getId());

			new PrinterUtility().insertIntoPrintQueueForOrderTransfer(httpRequest, em, toOrder,
					orderPacket.getLocationId(), newOrderPacket.getFromOrderHeader().getId());

			tx.commit();

			orderPacket.setFromOrderHeader(newOrderPacket.getFromOrderHeader());
			orderPacket.setToOrderHeader(newOrderPacket.getToOrderHeader());
			// call synchPacket for store forward
			// create json packet for store forward

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the two order by id.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the two order by id
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/getTwoOrderById")
	public String getTwoOrderById(OrderPacketForOrderTransfer orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacketForOrderTransfer", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			OrderPacketForOrderTransfer newOrderPacket = new OrderServiceForPost().getTwoOrderById(httpRequest, em,
					orderPacket);

			orderPacket.setFromOrderHeader(newOrderPacket.getFromOrderHeader());
			orderPacket.setToOrderHeader(newOrderPacket.getToOrderHeader());
			// call synchPacket for store forward

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Update item transfer.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateItemTransfer")
	public String updateItemTransfer(OrderPacketForOrderTransfer orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			new OrderServiceForPost().updateItemTransfer(httpRequest, em, orderPacket);
			OrderHeader fromOrderHeader = new OrderManagementServiceBean().getOrderById(em,
					orderPacket.getFromOrderHeader().getId());
			OrderHeader toOrderHeader = new OrderManagementServiceBean().getOrderById(em,
					orderPacket.getToOrderHeader().getId());
			try {
				new PrinterUtility().insertIntoPrintQueueForItemTransfer(httpRequest, em, toOrderHeader,
						orderPacket.getLocationId(), fromOrderHeader);

			} catch (Exception e) {
				// todo shlok need
				// handel proper exception

				logger.severe(e);
			}

			tx.commit();

			orderPacket.setFromOrderHeader(
					new OrderManagementServiceBean().getOrderById(em, orderPacket.getFromOrderHeader().getId()));
			orderPacket.setToOrderHeader(
					new OrderManagementServiceBean().getOrderById(em, orderPacket.getToOrderHeader().getId()));
			// call synchPacket for store forward
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacketForOrderTransfer", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all order payment details by payment type ids.
	 *
	 * @param getOrderPaymentDetailForBatchSettlePacket
	 *            the get order payment detail for batch settle packet
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by payment type ids
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/getAllOrderPaymentDetailsByPaymentTypeIds")
	public String getAllOrderPaymentDetailsByPaymentTypeIds(
			GetOrderPaymentDetailForBatchSettlePacket getOrderPaymentDetailForBatchSettlePacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
			// call synchPacket for store forward

			return new JSONUtility(httpRequest).convertToJsonString(
					orderManagementServiceBean.getAllOrderPaymentDetailsByPaymentTypeIds(httpRequest, em,
							getOrderPaymentDetailForBatchSettlePacket.getUsersId(),
							getOrderPaymentDetailForBatchSettlePacket.getLocationsId(),
							getOrderPaymentDetailForBatchSettlePacket.getPaymentTypeId(), sessionId,
							getOrderPaymentDetailForBatchSettlePacket.getStartDate(),
							getOrderPaymentDetailForBatchSettlePacket.getEndDate()));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete order by order management role.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteOrderByOrderManagementRole")
	public String deleteOrderByOrderManagementRole(OrderPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			orderPacket = new OrderServiceForPost().deleteOrderByOrderManagementRole(httpRequest, em, orderPacket);
			tx.commit();
			// call synchPacket for store forward
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the not printed order detail items to printer.
	 *
	 * @param packet
	 *            the packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addNotPrintedOrderDetailItemsToPrinter")
	public String addNotPrintedOrderDetailItemsToPrinter(NotPrintedOrderDetailItemsToPrinterPacket packet,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "NotPrintedOrderDetailItemsToPrinterPacket",
					httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, packet);

			tx = em.getTransaction();
			tx.begin();
			List<NotPrintedOrderDetailItemsToPrinter> o = new OrderManagementServiceBean()
					.addNotPrintedOrderDetailItemsToPrinter(packet.getNotPrintedOrderDetailItemsToPrinter(),
							httpRequest, em, packet.getLocationId());
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update not printed order detail items to printer.
	 *
	 * @param packet
	 *            the packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateNotPrintedOrderDetailItemsToPrinter")
	public String updateNotPrintedOrderDetailItemsToPrinter(NotPrintedOrderDetailItemsToPrinterPacket packet,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "NotPrintedOrderDetailItemsToPrinterPacket",
					httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, packet);

			tx = em.getTransaction();
			tx.begin();
			List<NotPrintedOrderDetailItemsToPrinter> o = new OrderManagementServiceBean()
					.updateNotPrintedOrderDetailItemsToPrinter(packet.getNotPrintedOrderDetailItemsToPrinter(),
							httpRequest, em);
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Delete not printed order detail items to printer.
	 *
	 * @param packet
	 *            the packet
	 * @param sessionId
	 *            the session id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/deleteNotPrintedOrderDetailItemsToPrinter")
	public String deleteNotPrintedOrderDetailItemsToPrinter(NotPrintedOrderDetailItemsToPrinterPacket packet,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "NotPrintedOrderDetailItemsToPrinterPacket",
					httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, packet);
			tx = em.getTransaction();
			tx.begin();
			List<NotPrintedOrderDetailItemsToPrinter> o = new OrderManagementServiceBean()
					.deleteNotPrintedOrderDetailItemsToPrinter(packet.getNotPrintedOrderDetailItemsToPrinter(),
							httpRequest, em);
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the not printed order detail items to printer by id.
	 *
	 * @param id
	 *            the id
	 * @param sessionId
	 *            the session id
	 * @return the not printed order detail items to printer by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getNotPrintedOrderDetailItemsToPrinterById/{id}")
	public String getNotPrintedOrderDetailItemsToPrinterById(@PathParam("id") int id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<NotPrintedOrderDetailItemsToPrinter> criteria = builder
					.createQuery(NotPrintedOrderDetailItemsToPrinter.class);
			Root<NotPrintedOrderDetailItemsToPrinter> ic = criteria.from(NotPrintedOrderDetailItemsToPrinter.class);
			TypedQuery<NotPrintedOrderDetailItemsToPrinter> query = em.createQuery(
					criteria.select(ic).where(builder.equal(ic.get(NotPrintedOrderDetailItemsToPrinter_.id), id),
							builder.notEqual(ic.get(NotPrintedOrderDetailItemsToPrinter_.status), "D"),
							builder.notEqual(ic.get(NotPrintedOrderDetailItemsToPrinter_.status), "I")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the prints the queue by status.
	 *
	 * @param status
	 *            the status
	 * @param printerId
	 *            the printer id
	 * @param locationId
	 *            the location id
	 * @param isOrderAhead
	 *            the is order ahead
	 * @return the prints the queue by status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPrintQueueByStatus/{status}/{printerId}/{locationId}/{isOrderAhead}")
	public String getPrintQueueByStatus(@PathParam("status") String status, @PathParam("printerId") int printerId,
			@PathParam("locationId") String locationId, @PathParam("locationId") int isOrderAhead) throws Exception {
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, auth_token);
			String batchId = PaymentBatchManager.getInstance().getCurrentBatchIdBySession(httpRequest, em, locationId,
					false, null, "21");
			List<PrintQueue> resultSet = null;
			if (status.equals("B")) {
				String queryString = "select l from PrintQueue l where l.status =? and l.printerId=? and l.locationId=? and l.isOrderAhead=? and l.orderId in (select o.id from OrderHeader o where o.nirvanaXpBatchNumber=?) order by l.updated desc ";

				TypedQuery<PrintQueue> query = em.createQuery(queryString, PrintQueue.class).setParameter(1, status)
						.setParameter(2, printerId).setParameter(3, locationId).setParameter(4, isOrderAhead)
						.setParameter(5, batchId);
				resultSet = query.getResultList();

			} else {

				String queryString = "select l from PrintQueue l where l.status in (? ,'C') and l.printerId=? and l.locationId=? and l.orderId in (select o.id from OrderHeader o where o.nirvanaXpBatchNumber=?) ";
				TypedQuery<PrintQueue> query = em.createQuery(queryString, PrintQueue.class).setParameter(1, status)
						.setParameter(2, printerId).setParameter(3, locationId).setParameter(4, batchId);
				resultSet = query.getResultList();
			}
			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update print queue.
	 *
	 * @param printQueuePacket
	 *            the print queue packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updatePrintQueue")
	public String updatePrintQueue(PrintQueuePacket printQueuePacket) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			PrintQueuePacket printQueuePacket2 = new OrderServiceForPost().updatePrintQueue(em, httpRequest,
					printQueuePacket);
			tx.commit();
			// call synchPacket for store forward
			json = new StoreForwardUtility().returnJsonPacket(printQueuePacket2, "PrintQueuePacket", httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, printQueuePacket.getLocationId(),
					Integer.parseInt(printQueuePacket.getMerchantId()));
			String result = new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(printQueuePacket2);
			return result;
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the KDS to order detail item status.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addKDSToOrderDetailItemStatus")
	public String addKDSToOrderDetailItemStatus(KDSToOrderDetailItemStatusPacket packet) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "KDSToOrderDetailItemStatusPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// TODO Ankur - make all synchronized calls synchronize at a
			// location level, not whole server
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));
			String result = new JSONUtility(httpRequest).convertToJsonString(
					KDSSingletonUtil.getInstance().addKDSToOrderDetailItemStatus(httpRequest, em, packet));
			return result;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update KDS to order detail item status.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateKDSToOrderDetailItemStatus")
	public String updateKDSToOrderDetailItemStatus(KDSToOrderDetailItemStatusPacket packet) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "KDSToOrderDetailItemStatusPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// TODO Ankur - make all synchronized calls synchronize at a
			// location level, not whole server
			KDSSingletonUtil.getInstance().updateKDSToOrderDetailItemStatus(httpRequest, em, packet);
			String result = new JSONUtility(httpRequest).convertToJsonString(packet);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return result;
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order header batch wise.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order header batch wise
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderHeaderBatchWise/{locationId}/{startDate}/{endDate}")
	public String getAllOrderHeaderBatchWise(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			if (checkDateRange(em, locationId, startDate, endDate)) {
				// changed this as this method is used to prcess payments also
				OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
				TimezoneTime timezoneTime = new TimezoneTime();

				String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
				String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);
				List<BatchDetail> batchDetails = null;
				try {
					batchDetails = orderManagementServiceBean.getBatchForStartEndDate(httpRequest, em, locationId,
							pickStartDate, pickEndDate);
				} catch (Exception e1) {
					logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
				}
				return new JSONUtility(httpRequest).convertToJsonString(orderManagementServiceBean
						.getAllOrderHeaderBatchWise(httpRequest, em, locationId, batchDetails));

			}

		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the all order header by created.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order header by created
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderHeaderByCreated/{locationId}/{startDate}/{endDate}")
	public String getAllOrderHeaderByCreated(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also
			if (checkDateRange(em, locationId, startDate, endDate)) {
				OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();

				TimezoneTime timezoneTime = new TimezoneTime();
				String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
				String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);
				return new JSONUtility(httpRequest).convertToJsonString(orderManagementServiceBean
						.getAllOrderHeaderByCreated(httpRequest, em, locationId, pickStartDate, pickEndDate));
			}
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the all order header by last updated.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order header by last updated
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderHeaderByLastUpdated/{locationId}/{startDate}/{endDate}")
	public String getAllOrderHeaderByLastUpdated(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also
			if (checkDateRange(em, locationId, startDate, endDate)) {
				OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();

				// why call this method again? checkDateRange(httpRequest, em,
				// locationId, startDate, endDate);

				return new JSONUtility(httpRequest).convertToJsonString(orderManagementServiceBean
						.getAllOrderHeaderByLastUpdated(httpRequest, em, locationId, startDate, endDate));
			}

		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the active batch.
	 *
	 * @param locationId
	 *            the location id
	 * @return the active batch
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getActiveBatch/{locationId}")
	public String getActiveBatch(@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(
					orderManagementServiceBean.getActiveBatch(httpRequest, em, locationId, null, false, null, "21"));
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the batch for start end date.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the batch for start end date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getBatchForStartEndDate/{locationId}/{startDate}/{endDate}")
	public String getBatchForStartEndDate(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();

			TimezoneTime timezoneTime = new TimezoneTime();

			String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
			String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);
			List<BatchDetail> batchDetails = null;
			try {
				batchDetails = orderManagementServiceBean.getBatchForStartEndDate(httpRequest, em, locationId,
						pickStartDate, pickEndDate);
			} catch (Exception e1) {
				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}

			return new JSONUtility(httpRequest).convertToJsonString(batchDetails);
		}

		finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Check date range.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return true, if successful
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 */
	private boolean checkDateRange(EntityManager em, String locationId, String startDate, String endDate)
			throws NirvanaXPException, ParseException {
		TimezoneTime timezoneTime = new TimezoneTime();

		long pickStartDate = timezoneTime.getDayCount(locationId, em, startDate, endDate);

		if (pickStartDate > 7 || pickStartDate < 0) {
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDER_DATE_OUT_OF_RANGE_EXCEPTION,
							MessageConstants.ERROR_MESSAGE_ORDER_DATE_OUT_OF_RANGE_DISPLAY_MESSAGE, null));
		}
		return true;
	}

	/**
	 * Gets the all order header by open time.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order header by open time
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderHeaderByOpenTime/{locationId}/{startDate}/{endDate}")
	public String getAllOrderHeaderByOpenTime(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			checkDateRange(em, locationId, startDate, endDate);
			// changed this as this method is used to prcess payments also
			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();

			return new JSONUtility(httpRequest).convertToJsonString(orderManagementServiceBean
					.getAllOrderHeaderByOpenTime(httpRequest, em, locationId, startDate, endDate));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order header by close time.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order header by close time
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderHeaderByCloseTime/{locationId}/{startDate}/{endDate}")
	public String getAllOrderHeaderByCloseTime(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			if (checkDateRange(em, locationId, startDate, endDate)) {
				// changed this as this method is used to prcess payments also
				OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
				return new JSONUtility(httpRequest).convertToJsonString(orderManagementServiceBean
						.getAllOrderHeaderByCloseTime(httpRequest, em, locationId, startDate, endDate));
			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the all order header by start batch time.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order header by start batch time
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderHeaderByStartBatchTime/{locationId}/{startDate}/{endDate}")
	public String getAllOrderHeaderByStartBatchTime(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			if (checkDateRange(em, locationId, startDate, endDate)) {
				// changed this as this method is used to prcess payments also
				OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();

				TimezoneTime timezoneTime = new TimezoneTime();

				String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
				String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);
				List<BatchDetail> batchDetails = null;
				try {
					batchDetails = orderManagementServiceBean.getBatchForStartTime(httpRequest, em, locationId,
							pickStartDate, pickEndDate);
				} catch (Exception e1) {
					logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
				}

				return new JSONUtility(httpRequest)
						.convertToJsonString(orderManagementServiceBean.getAllOrderHeaderByStartBatchTime(httpRequest,
								em, locationId, startDate, endDate, batchDetails));
			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the all order header by end batch time.
	 *
	 * @param locationId
	 *            the location id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order header by end batch time
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOrderHeaderByEndBatchTime/{locationId}/{startDate}/{endDate}")
	public String getAllOrderHeaderByEndBatchTime(@PathParam("locationId") String locationId,
			@PathParam("startDate") String startDate, @PathParam("endDate") String endDate)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			if (checkDateRange(em, locationId, startDate, endDate)) {
				// changed this as this method is used to prcess payments also
				OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();

				TimezoneTime timezoneTime = new TimezoneTime();

				String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate, locationId, em);
				String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate, locationId, em);
				List<BatchDetail> batchDetails = null;
				try {
					batchDetails = orderManagementServiceBean.getBatchForEndTime(httpRequest, em, locationId,
							pickStartDate, pickEndDate);
				} catch (Exception e1) {
					logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
				}

				return new JSONUtility(httpRequest)
						.convertToJsonString(orderManagementServiceBean.getAllOrderHeaderByEndBatchTime(httpRequest, em,
								locationId, startDate, endDate, batchDetails));
			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/**
	 * Gets the all department by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all department by location id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllDepartmentByLocationId/{locationId}")
	public String getAllDepartmentByLocationId(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<Department> departments = niravanaXpTippingBean.getAllDepartmentByLocationId(httpRequest, em,
					locationId);
			return new JSONUtility(httpRequest).convertToJsonString(departments);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the department by id.
	 *
	 * @param id
	 *            the id
	 * @return the department by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getDepartmentById/{id}")
	public String getDepartmentById(@PathParam("id") String id) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			Department departments = niravanaXpTippingBean.getDepartmentById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(departments);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat department.
	 *
	 * @param departmentPacket
	 *            the department packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatDepartment")
	public String addUpdatDepartment(DepartmentPacket departmentPacket) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			Department department = niravanaXpTippingBean.addUpdatDepartment(httpRequest, em,
					departmentPacket.getDepartments());
			departmentPacket.setDepartments(department);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(departmentPacket, "DepartmentPacket", httpRequest);
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, departmentPacket.getLocationId(),
					Integer.parseInt(departmentPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(department);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/addMultipleLocationDepartment")
	public String addMultipleLocationDepartment(DepartmentPacket lookupPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			List<Department> result = new NiravanaXpTippingBean().addMultipleLocationDepartment(em,
					lookupPacket.getDepartments(), lookupPacket, httpRequest);
			tx.commit();

			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				lookupPacket.setLocationId(locationId);
				// sendPacketForBroadcast(POSNServiceOperations.OrderManagementService_add.name(),
				// lookupPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateMultipleLocationDepartment")
	public String updateMultipleLocationDepartment(DepartmentPacket lookupPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			List<Department> result = new NiravanaXpTippingBean().updateMultipleLocationDepartment(em,
					lookupPacket.getDepartments(), lookupPacket, httpRequest);
			tx.commit();

			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				lookupPacket.setLocationId(locationId);
				// sendPacketForBroadcast(POSNServiceOperations.OrderManagementService_add.name(),
				// lookupPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteMultipleLocationDepartment")
	public String deleteMultipleLocationDepartment(DepartmentPacket lookupPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, lookupPacket);
			tx = em.getTransaction();
			tx.begin();
			Department result = new NiravanaXpTippingBean().deleteMultipleLocationDepartment(em,
					lookupPacket.getDepartments(), lookupPacket, httpRequest);
			tx.commit();

			String[] locationsId = lookupPacket.getLocationsListId().split(",");
			for (String locationId : locationsId) {
				lookupPacket.setLocationId(locationId);
				// sendPacketForBroadcast(POSNServiceOperations.OrderManagementService_add.name(),
				// lookupPacket);
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all job roles by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all job roles by location id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllJobRolesByLocationId/{locationId}")
	public String getAllJobRolesByLocationId(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<JobRoles> o = niravanaXpTippingBean.getAllJobRolesByLocationId(httpRequest, em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the job roles by id.
	 *
	 * @param id
	 *            the id
	 * @return the job roles by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getJobRolesById/{id}")
	public String getJobRolesById(@PathParam("id") String id) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			JobRoles o = niravanaXpTippingBean.getJobRolesById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the tip class by id.
	 *
	 * @param id
	 *            the id
	 * @return the tip class by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getTipClassById/{id}")
	public String getTipClassById(@PathParam("id") int id) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipClass o = niravanaXpTippingBean.getTipClassById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the tip pool by id.
	 *
	 * @param id
	 *            the id
	 * @return the tip pool by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getTipPoolById/{id}")
	public String getTipPoolById(@PathParam("id") int id) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipPool o = niravanaXpTippingBean.getTipPoolById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the tip pool basis by id.
	 *
	 * @param id
	 *            the id
	 * @return the tip pool basis by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getTipPoolBasisById/{id}")
	public String getTipPoolBasisById(@PathParam("id") int id) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipPoolBasis o = niravanaXpTippingBean.getTipPoolBasisById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat job roles.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatJobRoles")
	public String addUpdatJobRoles(JobRolesPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "JobRolesPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<JobRoles> department = niravanaXpTippingBean.addUpdatJobRoles(httpRequest, em, packet);

			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));
			sendPacketForBroadcast(POSNServiceOperations.LookupService_addJobRoles.name(), packet,
					POSNServices.EmployeeManagementService.name());
			return new JSONUtility(httpRequest).convertToJsonString(department);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all tip class.
	 *
	 * @return the all tip class
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllTipClass/")
	public String getAllTipClass() throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<TipClass> o = niravanaXpTippingBean.getAllTipClass(httpRequest, em);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat tip class.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatTipClass")
	public String addUpdatTipClass(TipClassPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "TipClassPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipClass department = niravanaXpTippingBean.addUpdatTipClass(httpRequest, em, packet);
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(department);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all tip pool by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all tip pool by location id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllTipPoolByLocationId/{locationId}")
	public String getAllTipPoolByLocationId(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<TipPool> o = niravanaXpTippingBean.getAllTipPoolByLocationId(httpRequest, em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat tip pool.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatTipPool")
	public String addUpdatTipPool(TipPoolPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "TipPoolPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipPool o = niravanaXpTippingBean.addUpdatTipPool(httpRequest, em, packet);
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all tip pool basis.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all tip pool basis
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllTipPoolBasis/{locationId}")
	public String getAllTipPoolBasis(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<TipPoolBasis> o = niravanaXpTippingBean.getAllTipPoolBasis(httpRequest, em);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat tip pool basis.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatTipPoolBasis")
	public String addUpdatTipPoolBasis(TipPoolBasisPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "TipPoolBasisPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipPoolBasis o = niravanaXpTippingBean.addUpdatTipPoolBasis(httpRequest, em, packet);
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all tip pool rules.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all tip pool rules
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllTipPoolRules/{locationId}")
	public String getAllTipPoolRules(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<TipPoolRules> o = niravanaXpTippingBean.getAllTipPoolRules(httpRequest, em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat tip pool rules.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatTipPoolRules")
	public String addUpdatTipPoolRules(TipPoolRulesPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "TipPoolRulesPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipPoolRules o = niravanaXpTippingBean.addUpdatTipPoolRules(httpRequest, em, packet);

			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the tip pool rules by id.
	 *
	 * @param id
	 *            the id
	 * @return the tip pool rules by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getTipPoolRulesById/{id}")
	public String getTipPoolRulesById(@PathParam("id") int id) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			TipPoolRules o = niravanaXpTippingBean.getTipPoolRulesById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the employee master by id.
	 *
	 * @param id
	 *            the id
	 * @return the employee master by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getEmployeeMasterById/{id}")
	public String getEmployeeMasterById(@PathParam("id") int id)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			EmployeeMaster o = niravanaXpTippingBean.getEmployeeMasterById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat employee master.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatEmployeeMaster")
	public String addUpdatEmployeeMaster(EmployeeMasterPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "EmployeeMasterPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			EmployeeMaster o = niravanaXpTippingBean.addUpdatEmployeeMaster(httpRequest, em, packet);

			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all employee master.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all employee master
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllEmployeeMaster/{locationId}")
	public String getAllEmployeeMaster(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<EmployeeMaster> o = niravanaXpTippingBean.getAllEmployeeMaster(httpRequest, em, locationId);

			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all employee master by locations id and is tipped employee.
	 *
	 * @param locationId
	 *            the location id
	 * @param isTippedEmployee
	 *            the is tipped employee
	 * @return the all employee master by locations id and is tipped employee
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllEmployeeMasterByLocationsIdAndIsTippedEmployee/{locationId}/{isTippedEmployee}")
	public String getAllEmployeeMasterByLocationsIdAndIsTippedEmployee(@PathParam("locationId") String locationId,
			@PathParam("isTippedEmployee") int isTippedEmployee)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			List<EmployeeMaster> o = niravanaXpTippingBean.getAllEmployeeMasterByLocationsIdAndIsTippedEmployee(
					httpRequest, em, locationId, isTippedEmployee);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all operational shift schedule.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all operational shift schedule
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOperationalShiftSchedule/{locationId}")
	public String getAllOperationalShiftSchedule(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();

			List<OperationalShiftSchedule> o = niravanaXpTippingBean.getAllOperationalShiftSchedule(httpRequest, em,
					locationId);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all operational shift schedule by id.
	 *
	 * @param id
	 *            the id
	 * @return the all operational shift schedule by id
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/getAllOperationalShiftScheduleById/{id}")
	public String getAllOperationalShiftScheduleById(@PathParam("id") String id)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();

			OperationalShiftSchedule o = niravanaXpTippingBean.getAllOperationalShiftScheduleById(httpRequest, em, id);
			return new JSONUtility(httpRequest).convertToJsonString(o);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the updat operational shift schedule.
	 *
	 * @param packet
	 *            the packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addUpdatOperationalShiftSchedule")
	public String addUpdatOperationalShiftSchedule(OperationalShiftSchedulePacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "OperationalShiftSchedulePacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();

			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
			OperationalShiftSchedule o = niravanaXpTippingBean.addUpdatOperationalShiftSchedule(httpRequest, em,
					packet);

			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			new OrderServiceForPost().sendPacketForBroadcast(httpRequest,
					POSNServiceOperations.LookupService_updOperationalShiftSchedule.name(), packet);

			return new JSONUtility(httpRequest).convertToJsonString(o);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Calculate employee operational hours.
	 *
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 * @param batchId
	 *            the batch id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/calculateEmployeeOperationalHours/{locationId}/{userId}/{batchId}")
	public String calculateEmployeeOperationalHours(@PathParam("locationId") String locationId,
			@PathParam("userId") String userId, @PathParam("batchId") int batchId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			BatchDetail batchDetail = em.find(BatchDetail.class, batchId);
			List<Object[]> list = bean.calculateEmployeeOperationalHours(httpRequest, em, locationId, userId,
					batchDetail);
			return new JSONUtility(httpRequest).convertToJsonString(list);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/updateOrderStatusForAlexa")
	public String updateOrderStatusForAlexa() throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			String sessionId = "1020fb1e5b19165b266fd4b5b67e179";
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, null);
			OrderStatus orderStatus = new OrderServiceForPost().getOrderStatusByNameAndLocation(em, "Check Presented",
					"1", "1");
			header.setOrderStatusId(orderStatus.getId());
			OrderPacket orderPacket = new OrderPacket();
			orderPacket.setOrderHeader(header);
			orderPacket.setClientId("40F7A1AC-C767-45F0-86B1-B016B254E6FB");
			orderPacket.setEchoString(
					"40F7A1AC-C767-45F0-86B1-B016B254E6FB_updateOrderStatusForDuplicateCheck_3-update order status");
			orderPacket.setLocationId("1");
			tx = em.getTransaction();
			tx.begin();
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderStatus(httpRequest, em, orderPacket);
			if (orderStatus.getName().equals("Void Order")) {
				return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
			}
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/test/{locationId}")
	public boolean test(@PathParam("driverId") int driverId, @PathParam("orderId") int orderId,
			@PathParam("createdBy") int createdBy, @PathParam("locationId") String locationId) throws Exception {
		try {
			FutureUpdateQueue queue = new FutureUpdateQueue(httpRequest, locationId,
					httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), "2018-03-09");
			Thread t = new Thread(queue);
			t.start();
		} catch (Exception e) {
			logger.severe(e);
		}
		return true;
	}

	@GET
	@Path("/getAllDriverOrder")
	public String getAllDriverOrder() throws Exception {
		EntityManager emGlobal = null;
		try {
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = emGlobal.getCriteriaBuilder();
			CriteriaQuery<DriverOrder> criteria = builder.createQuery(DriverOrder.class);
			Root<DriverOrder> r = criteria.from(DriverOrder.class);
			TypedQuery<DriverOrder> query = emGlobal.createQuery(criteria.select(r).where(
					builder.and(builder.notEqual(r.get(DriverOrder_.statusName), "Ready to Order")),
					builder.and(builder.notEqual(r.get(DriverOrder_.statusName), "Delivered"))));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
		}
	}

	@POST
	@Path("/updateDriverOrder")
	public String updateDriverOrder(DriverOrderPacket packet) throws Exception {
		EntityManager emGlobal = null;
		EntityManager emLocal = null;
		EntityTransaction txGlobal = null;
		EntityTransaction txLocal = null;
		OrderHeader header = new OrderHeader();
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "DriverOrderPacket", httpRequest);
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			emLocal = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			txGlobal = emGlobal.getTransaction();
			txGlobal.begin();
			txLocal = emLocal.getTransaction();
			txLocal.begin();

			DriverOrder driverOrder = packet.getDriverOrder();

			header = new OrderManagementServiceBean().getOrderById(emLocal, driverOrder.getOrderId());

			/*
			 * String queryString =
			 * "select s from OrderSource s where s.id = ?";
			 * TypedQuery<OrderSource> query = emLocal.createQuery(queryString,
			 * OrderSource.class).setParameter (1, header.getOrderSourceId());
			 * OrderSource orderSource = query.getSingleResult();
			 * 
			 * String queryStringStatus =
			 * "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'"
			 * ; TypedQuery<OrderStatus> queryStatus =
			 * emLocal.createQuery(queryStringStatus,
			 * OrderStatus.class).setParameter(1, "Ready to Order")
			 * .setParameter(2, header.getLocationsId()).setParameter(3,
			 * orderSource.getOrderSourceGroupId()); OrderStatus orderStatus =
			 * queryStatus.getSingleResult();
			 */

			if (header != null) {
				header.setOrderDetailItems(null);

				header.setUpdatedBy(driverOrder.getUpdatedBy());
				header.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				header.setOrderStatusId(packet.getDriverOrder().getStatusId());
				header = emLocal.merge(header);

				OrderPacket orderPacket = new OrderPacket();
				orderPacket.setOrderHeader(header);
				orderPacket.setMerchantId(driverOrder.getAccountId() + "");
				orderPacket.setClientId(packet.getClientId());
				orderPacket.setLocationId(driverOrder.getLocationsId() + "");
				orderPacket.setEchoString(packet.getEchoString());
				orderPacket.setSchemaName(packet.getSchemaName());
				orderPacket.setSessionId(packet.getSessionId());
				orderPacket.setIdOfSessionUsedByPacket(packet.getIdOfSessionUsedByPacket());
				new OrderServiceForPost().sendPacketForBroadcast(httpRequest, orderPacket,
						POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), true);

			}

			driverOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			driverOrder.setStatusId(packet.getDriverOrder().getStatusId());
			driverOrder.setStatusName(packet.getDriverOrder().getStatusName());
			driverOrder = emGlobal.merge(driverOrder);

			txLocal.commit();
			txGlobal.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(packet.getDriverOrder());
		} catch (RuntimeException e) {
			if (txGlobal != null && txGlobal.isActive()) {
				txGlobal.rollback();
			}

			if (txLocal != null && txLocal.isActive()) {
				txLocal.rollback();
			}
			throw e;
		}

		finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
			LocalSchemaEntityManager.getInstance().closeEntityManager(emLocal);
		}
	}

	@GET
	@Path("/getDriverOrderByDateAndStatusName/{date}/{statusName}")
	public String getDriverOrderByDateAndStatusName(@PathParam("date") String date,
			@PathParam("statusName") String statusName) throws Exception {
		EntityManager emGlobal = null;
		try {
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			/*
			 * CriteriaBuilder builder = emGlobal.getCriteriaBuilder();
			 * CriteriaQuery<DriverOrder> criteria =
			 * builder.createQuery(DriverOrder.class); Root<DriverOrder> r =
			 * criteria.from(DriverOrder.class); TypedQuery<DriverOrder> query =
			 * emGlobal.createQuery(criteria.select(r)
			 * .where(builder.or(builder.equal(r.get(DriverOrder_.statusName),
			 * "Ready to Order")),
			 * builder.or(builder.equal(r.get(DriverOrder_.statusName),
			 * "Delivered"))));
			 */

			String queryString = "select s from DriverOrder s where s.statusName = 'Ready to Order' or s.statusName = 'Delivered')";
			TypedQuery<DriverOrder> query = emGlobal.createQuery(queryString, DriverOrder.class);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
		}
	}

	private String createAddress(Address address) {

		String add = "";
		if (address != null) {
			if (address.getAddress1() != null && address.getAddress1().length() > 0) {
				add += address.getAddress1();
			}
			if (address.getAddress2() != null && address.getAddress2().length() > 0) {
				add += "," + address.getAddress2();
			}
			if (address.getCity() != null && address.getCity().length() > 0) {
				add += "," + address.getCity();
			}
			if (address.getState() != null && address.getState().length() > 0) {
				add += "," + address.getState();
			}
			if (address.getZip() != null && address.getZip().length() > 0) {
				add += "," + address.getZip();
			}
		}
		return add;
	}

	@GET
	@Path("/getOrderLockingStatus/{locationId}/{userId}/{orderId}")
	public String getOrderLockingStatus(@PathParam("locationId") String locationId, @PathParam("userId") String userId,
			@PathParam("orderId") String orderId) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;

		em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

		try {
			OrderManagementServiceBean bean = new OrderManagementServiceBean();

			return new JSONUtility(httpRequest)
					.convertToJsonString(bean.getOrderLockingStatus(em, locationId, orderId, userId));

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/registerOrderLocking/{locationId}/{userId}/{orderId}")
	public String registerOrderLocking(@PathParam("locationId") String locationId, @PathParam("userId") String userId,
			@PathParam("orderId") String orderId) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		boolean result = false;
		em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

		try {
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, locationId, 0);
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			// location setting is disabled then dont need to check
			if (locationSetting != null && locationSetting.getIsOrderLocking() == 1) {

				em.getTransaction().begin();
				OrderLocking locking = bean.registerOrderLockingStatus(httpRequest, em, locationId, orderId, userId);
				em.getTransaction().commit();
				if (locking != null && locking.getId().compareTo(BigInteger.ZERO) > 0) {
					result = true;
				}
			} else {
				result = true;
			}

			return new JSONUtility(httpRequest).convertToJsonString(result);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/deRegisterOrderLocking/{locationId}/{userId}/{orderId}")
	public String deRegisterOrderLocking(@PathParam("locationId") String locationId, @PathParam("userId") String userId,
			@PathParam("orderId") String orderId) throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		boolean result = false;
		em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

		try {
			new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, locationId, 0);
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			// if location setting is disabled then dont need to check
			if (locationSetting != null && locationSetting.getIsOrderLocking() == 1) {
				em.getTransaction().begin();
				OrderLocking locking = bean.deRegisterOrderLocking(httpRequest, em, locationId, orderId, userId);
				em.getTransaction().commit();
				if (locking != null && locking.getId().compareTo(BigInteger.ZERO) > 0) {
					result = true;
				}
			} else {
				result = true;
			}
			em.getTransaction().begin();
			OrderLocking locking = bean.deRegisterOrderLocking(httpRequest, em, locationId, orderId, userId);
			em.getTransaction().commit();
			if (locking != null && locking.getId().compareTo(BigInteger.ZERO) > 0) {
				result = true;
			}
			return new JSONUtility(httpRequest).convertToJsonString(result);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/getPrintQueueCountByStatusAndPriterIds")
	public int getPrintQueueCountByStatusAndPriterIds(OrderToKDSPacket packet) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "OrderToKDSPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			BatchDetail batchDetail = PaymentBatchManager.getInstance().getCurrentBatchBySession(httpRequest, em,
					packet.getLocationId(), false, null, "21");
			List<Object[]> resultSet = null;
			TimezoneTime timezoneTime = new TimezoneTime();
			String dateString[] = timezoneTime.getCurrentTimeofLocation(packet.getLocationId(), em);
			String utcTime = "";
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			if (batchDetail.getCloseTime() == 0) {
				utcTime = date.format(new Date()) + " 23:59:59";

			} else {
				utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
			}

			String gmtStartTime = dateFormatGmt.format(new Date(batchDetail.getStartTime()));
			if (packet.getStatus().equals("U")) {
				if (packet.getIsOrderAhead() != 1) {
					String queryString = "select l.id,l.order_id,l.print_string,l.status,l.account_id,l.location_id,l.order_detail_item_id,l.printer_id,l.local_time,l.is_order_ahead,l.schedule_date_time from print_queue l where l.status in (? ,'C') and (l.is_order_ahead=? or DATE_ADD( l.schedule_date_time, INTERVAL l.local_time HOUR_MINUTE)  like ? ) and l.printer_id in ("
							+ packet.getPrinterId()
							+ ")and l.location_id=? and l.order_id in (select o.id from order_header o where o.nirvanaxp_batch_number=? or o.schedule_date_time between ? and ?) order by l.updated asc";

					resultSet = em.createNativeQuery(queryString).setParameter(1, packet.getStatus())
							.setParameter(2, packet.getIsOrderAhead()).setParameter(3, dateString[0] + "%")
							.setParameter(4, packet.getLocationId()).setParameter(5, batchDetail.getId())
							.setParameter(6, gmtStartTime).setParameter(7, utcTime).getResultList();

				} else {
					String queryString = "select l.id,l.order_id,l.print_string,l.status,l.account_id,l.location_id,l.order_detail_item_id,l.printer_id,l.local_time,l.is_order_ahead,l.schedule_date_time from print_queue l where l.status in (? ,'C') and (l.is_order_ahead=? and DATE_ADD( l.schedule_date_time, INTERVAL l.local_time HOUR_MINUTE) > ? ) and l.printer_id in ("
							+ packet.getPrinterId() + ")and l.location_id=? order by l.schedule_date_time asc";

					resultSet = em.createNativeQuery(queryString).setParameter(1, packet.getStatus())
							.setParameter(2, packet.getIsOrderAhead()).setParameter(3, dateString[0] + "%")
							.setParameter(4, packet.getLocationId()).getResultList();
				}
			} else {

				String queryString = "select l.id,l.order_id,l.print_string,l.status,l.account_id,l.location_id,l.order_detail_item_id,l.printer_id,l.local_time,l.is_order_ahead,l.schedule_date_time from print_queue l where l.status =? and l.printer_id in ("
						+ packet.getPrinterId()
						+ ") and l.location_id=? and l.order_id in (select o.id from order_header o where o.nirvanaxp_batch_number=?) order by l.updated desc ";
				resultSet = em.createNativeQuery(queryString).setParameter(1, packet.getStatus())
						.setParameter(2, packet.getLocationId()).setParameter(3, batchDetail.getId()).getResultList();
			}
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(), 0);

			return resultSet.size();
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/getPrintQueueByStatusAndPriterIds")
	public String getPrintQueueByStatusAndPriterIds(OrderToKDSPacket packet) throws Exception {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "OrderToKDSPacket", httpRequest);
			List<PrintQueue> printQueueList = new ArrayList<PrintQueue>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			BatchDetail batchDetail = PaymentBatchManager.getInstance().getCurrentBatchBySession(httpRequest, em,
					packet.getLocationId(), false, null, "21");
			if (batchDetail != null) {
				List<Object[]> resultSet = null;
				TimezoneTime timezoneTime = new TimezoneTime();
				String dateString[] = timezoneTime.getCurrentTimeofLocation(packet.getLocationId(), em);
				String utcTime = "";
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
				if (batchDetail.getCloseTime() == 0) {
					utcTime = date.format(new Date()) + " 23:59:59";

				} else {
					utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
				}

				String gmtStartTime = dateFormatGmt.format(new Date(batchDetail.getStartTime()));

				if (packet.getStatus().equals("U")) {
					if (packet.getIsOrderAhead() != 1) {
						String queryString = "select l.id,l.order_id,l.print_string,l.status,l.account_id,l.location_id,l.order_detail_item_id,l.printer_id,l.local_time,l.is_order_ahead,l.schedule_date_time from print_queue l where l.status in (? ,'C') and (l.is_order_ahead=? or DATE_ADD( l.schedule_date_time, INTERVAL l.local_time HOUR_MINUTE)  like ? ) and l.printer_id in ("
								+ packet.getPrinterId()
								+ ")and l.location_id=? and l.order_id in (select o.id from order_header o where o.nirvanaxp_batch_number=? or o.schedule_date_time between ? and ?) order by l.updated asc";

						resultSet = em.createNativeQuery(queryString).setParameter(1, packet.getStatus())
								.setParameter(2, packet.getIsOrderAhead()).setParameter(3, dateString[0] + "%")
								.setParameter(4, packet.getLocationId()).setParameter(5, batchDetail.getId())
								.setParameter(6, gmtStartTime).setParameter(7, utcTime).getResultList();

					} else {
						String queryString = "select l.id,l.order_id,l.print_string,l.status,l.account_id,l.location_id,l.order_detail_item_id,l.printer_id,l.local_time,l.is_order_ahead,l.schedule_date_time from print_queue l where l.status in (? ,'C') and (l.is_order_ahead=? and DATE_ADD( l.schedule_date_time, INTERVAL l.local_time HOUR_MINUTE) > ? ) and l.printer_id in ("
								+ packet.getPrinterId() + ")and l.location_id=? order by l.schedule_date_time asc";

						resultSet = em.createNativeQuery(queryString).setParameter(1, packet.getStatus())
								.setParameter(2, packet.getIsOrderAhead()).setParameter(3, dateString[0] + "%")
								.setParameter(4, packet.getLocationId()).getResultList();

					}
				} else {

					String queryString = "select l.id,l.order_id,l.print_string,l.status,l.account_id,l.location_id,l.order_detail_item_id,l.printer_id,l.local_time,l.is_order_ahead,l.schedule_date_time from print_queue l where l.status =? and l.printer_id in ("
							+ packet.getPrinterId()
							+ ") and l.location_id=? and l.order_id in (select o.id from order_header o where o.nirvanaxp_batch_number=?) order by l.updated desc ";
					resultSet = em.createNativeQuery(queryString).setParameter(1, packet.getStatus())
							.setParameter(2, packet.getLocationId()).setParameter(3, batchDetail.getId())
							.getResultList();
				}
				if (resultSet.size() > 0) {
					for (Object[] objRow : resultSet) {
						// if this has primary key not 0
						PrintQueue printQueue = new PrintQueue();
						printQueue.setId((BigInteger) objRow[0]);
						printQueue.setOrderId((String) objRow[1]);
						printQueue.setPrintString((String) objRow[2]);
						printQueue.setStatus((String) "" + objRow[3]);
						printQueue.setAccountId((Integer) objRow[4]);
						printQueue.setLocationId((String) objRow[5]);
						printQueue.setOrderDetailItemId((String) objRow[6]);
						printQueue.setPrinterId((String) objRow[7]);
						printQueue.setLocalTime((String) objRow[8]);
						printQueue.setIsOrderAhead((Integer) objRow[9]);
						printQueue.setScheduleDateTime((String) objRow[10]);

						printQueueList.add(printQueue);
					}
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(printQueueList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/sendEmailForEODSummaryByDate/{locationId}/{userId}/{pickDate}/{email}")
	public String sendEmailForEODSummaryByDate(@PathParam("locationId") String locationId,
			@PathParam("userId") String userId, @PathParam("pickDate") String pickDate,
			@PathParam("email") String email)
			throws FileNotFoundException, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			String sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			OrderManagementServiceBean orderManagementServiceBean = new OrderManagementServiceBean();
			List<OrderHeader> headers = new ArrayList<OrderHeader>();
			try {

				headers = orderManagementServiceBean.getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPickDate(
						httpRequest, em, userId, locationId, pickDate);

			} catch (Exception e) {
				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}

			// Send Email EOD summary
			try {
				tx = em.getTransaction();
				tx.begin();

				TimezoneTime timezoneTime = new TimezoneTime();
				String pickStartDate = timezoneTime.getDateAccordingToGMT(pickDate + " 00:00:00", locationId, em);
				String pickEndDate = timezoneTime.getDateAccordingToGMT(pickDate + " 23:59:59", locationId, em);

				List<String> batchId = null;
				try {
					batchId = orderManagementServiceBean.getActiveBatchForPickDate(httpRequest, em, locationId,
							pickStartDate, pickEndDate);
				} catch (Exception e1) {
					logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
				}

				BatchDetail startBatchDetail = null;
				if (batchId != null) {
					if (batchId.size() > 0) {
						String startBatchId = batchId.get(0);
						startBatchDetail = em.find(BatchDetail.class, startBatchId);

					}
				}

				new OrderManagementServiceBean().sendEODSettledmentMail(httpRequest, em, userId, locationId, sessionId,
						startBatchDetail, headers, email);
				tx.commit();
			} catch (Exception e) {
				logger.severe(e);
			}

			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/sendEmailForEODSummary/{updatedBy}/{location_id}/{business_id}/{batchDetailId}/{email}")
	public boolean sendEmailForEODSummary(@PathParam("updatedBy") String updatedBy,
			@PathParam("location_id") String locationId, @PathParam("business_id") int businessId,
			@PathParam("batchDetailId") int batchDetailId, @PathParam("email") String email)
			throws FileNotFoundException, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			String sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			BatchDetail batchDetail = null;
			try {
				batchDetail = em.find(BatchDetail.class, batchDetailId);
			} catch (Exception e1) {

				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}

			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			List<OrderHeader> headers = new ArrayList<OrderHeader>();
			try {
				headers = bean.getAllOrderPaymentDetailsByUserIdLocationBatchWise(httpRequest, em, updatedBy,
						locationId, sessionId, batchDetail);

			} catch (Exception e) {
				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}
			// boolean isDone = false;

			if (batchDetail != null) {

				// Send Email EOD summary
				try {
					tx = em.getTransaction();
					tx.begin();
					bean.sendEODSettledmentMail(httpRequest, em, updatedBy, locationId, sessionId, batchDetail, headers,
							email);
					tx.commit();
				} catch (Exception e) {
					logger.severe(e);
				}

				return true;
			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_BATCH_ID_NOT_PRESENT_WITH_DATE__EXCEPTION,
						MessageConstants.ERROR_MESSAGE_BATCH_ID_NOT_PRESENT_WITH_DATE_DISPLAY_MESSAGE, null));

			}

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/*
	 * private List<OrderHeader> populateOrderHeaderForPayment(EntityManager em,
	 * List<Object[]> resultList, List<OrderHeader> orderHeaders, OrderHeader
	 * header, List<OrderPaymentDetail> orderPaymentDetails, int index) { for
	 * (Object[] objRow : resultList) { ++index;
	 * 
	 * // if this has primary key not 0
	 * 
	 * if ((Integer) objRow[118] != header.getId()) { if (header.getId() != 0) {
	 * if (orderPaymentDetails != null) { if (orderPaymentDetails.size() > 0) {
	 * header.setOrderPaymentDetails(orderPaymentDetails); } }
	 * orderHeaders.add(header); header = new OrderHeader(); }
	 * 
	 * // ------------------ Add address object for address // shipping id and
	 * address billing id --- Address addressShipping = new Address(); Address
	 * addressBilling = new Address(); try {
	 * addressBilling.setAddressByResultSet(objRow, 186);
	 * addressShipping.setAddressByResultSet(objRow, 199);
	 * 
	 * } catch (Exception e) { logger.severe(e); }
	 * 
	 * objRow[125] = addressShipping; objRow[126] = addressBilling;
	 * 
	 * header.setOrderHeaderByResultSetAllValue(objRow, 117);
	 * orderPaymentDetails = new ArrayList<OrderPaymentDetail>(); }
	 * 
	 * OrderPaymentDetail paymentDetail = new OrderPaymentDetail();
	 * paymentDetail.setOrderPaymentDetailsResultSet(objRow); if (paymentDetail
	 * != null) { if (paymentDetail.getId() != 0)
	 * paymentDetail.setOrderPaymentDetailsToSalesTax(
	 * getOrderPaymentDetailsToSalesTaxForOrderPaymentDetailsId(em,
	 * paymentDetail.getId())); orderPaymentDetails.add(paymentDetail); }
	 * 
	 * if (index == resultList.size()) { if (orderPaymentDetails != null) { if
	 * (orderPaymentDetails.size() > 0) {
	 * header.setOrderPaymentDetails(orderPaymentDetails); } } //
	 * header.setOrderPaymentDetails(orderPaymentDetails);
	 * orderHeaders.add(header); }
	 * 
	 * } return orderHeaders; }
	 */

	/*
	 * List<OrderPaymentDetailsToSalesTax>
	 * getOrderPaymentDetailsToSalesTaxForOrderPaymentDetailsId(EntityManager
	 * em, int orderPaymentDetailsId) { String queryString =
	 * "select odi from OrderPaymentDetailsToSalesTax odi where odi.orderPaymentDetailsId= ? order by id asc "
	 * ; TypedQuery<OrderPaymentDetailsToSalesTax> query = em
	 * .createQuery(queryString,
	 * OrderPaymentDetailsToSalesTax.class).setParameter(1,
	 * orderPaymentDetailsId); try { return query.getResultList(); } catch
	 * (NoResultException e) { logger.severe(e); } return null;
	 * 
	 * }
	 */

	@GET
	@Path("/getDriverOrderSummaryByDate/{date}/{driverId}")
	public String getDriverOrderSummaryByDate(@PathParam("date") String date, @PathParam("driverId") int driverId)
			throws Exception {
		EntityManager emGlobal = null;
		EntityManager emLocal = null;
		try {
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager();

			String toDate = date + " 23:59:59";
			String fromDate = date + " 00:00:00";

			String queryString = "select s from DriverOrder s where (s.statusName = 'Ready to Order' "
					+ "or s.statusName = 'Delivered' or s.statusName = 'Picked up From Restaurant') "
					+ "and (s.time between ? and ?) and (s.driverId = ?)";
			TypedQuery<DriverOrder> query = emGlobal.createQuery(queryString, DriverOrder.class)
					.setParameter(1, Utilities.convertStringToDate(fromDate))
					.setParameter(2, Utilities.convertStringToDate(toDate)).setParameter(3, driverId);

			DriverOrderSummaryPacket packet = new DriverOrderSummaryPacket();
			List<DriverOrder> driverOrders = query.getResultList();

			int count = 0;
			int deliveredCount = 0;
			int pendingCount = 0;
			BigDecimal balanceDue = new BigDecimal(0);
			for (DriverOrder driverOrder : driverOrders) {
				count++;

				if (driverOrder.getStatusName().equals("Ready to Order")
						|| driverOrder.getStatusName().equals("Delivered")) {
					deliveredCount++;
				} else if (driverOrder.getStatusName().equals("Picked up From Restaurant")) {
					pendingCount++;
				}

				Account account = emGlobal.find(Account.class, driverOrder.getAccountId());
				emLocal = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(account.getSchemaName());

				OrderHeader header = emLocal.find(OrderHeader.class, driverOrder.getOrderId());

				balanceDue = balanceDue.add(header.getBalanceDue());

			}

			packet.setTotalDriverOrders(count);
			packet.setTotalDeliveredOrders(deliveredCount);
			packet.setTotalPendingOrders(pendingCount);
			packet.setBalanceDue(balanceDue);

			return new JSONUtility(httpRequest).convertToJsonString(packet);
		} finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
			LocalSchemaEntityManager.getInstance().closeEntityManager(emLocal);
		}
	}

	@GET
	@Path("/getDriverOrderByDateAndStatusName/{date}/{statusName}/{driverId}")
	public String getDriverOrderByDateAndStatusName(@PathParam("date") String date,
			@PathParam("statusName") String statusName, @PathParam("driverId") int driverId) throws Exception {
		EntityManager emGlobal = null;
		try {
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			String queryString = "select s from DriverOrder s where  s.driverId=? and  s.statusName = 'Ready to Order' or s.statusName = 'Delivered')";
			TypedQuery<DriverOrder> query = emGlobal.createQuery(queryString, DriverOrder.class).setParameter(1,
					driverId);

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
		}

	}

	@GET
	@Path("/getAllDriverOrder/{driverId}")
	public String getAllDriverOrder(@PathParam("driverId") int driverId) throws Exception {
		EntityManager emGlobal = null;
		try {
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = emGlobal.getCriteriaBuilder();
			CriteriaQuery<DriverOrder> criteria = builder.createQuery(DriverOrder.class);
			Root<DriverOrder> r = criteria.from(DriverOrder.class);
			TypedQuery<DriverOrder> query = emGlobal.createQuery(criteria.select(r).where(
					builder.and(builder.notEqual(r.get(DriverOrder_.statusName), "Ready to Order")),
					builder.and(builder.notEqual(r.get(DriverOrder_.statusName), "Delivered"),
							builder.and(builder.equal(r.get(DriverOrder_.driverId), driverId)))));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
		}
	}

	@GET
	@Path("/tipPoolCalculation/{locationId}/{userId}/{startDateTime}/{endDateTime}")
	public String tipPoolCalculation(@PathParam("locationId") String locationId, @PathParam("userId") String userId,
			@PathParam("startDateTime") String startDateTime, @PathParam("endDateTime") String endDateTime)
			throws Exception, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, locationId, 0);

			TimezoneTime timezoneTime = new TimezoneTime();

			String pickStartDate = timezoneTime.getDateAccordingToGMT(startDateTime, locationId, em);
			String pickEndDate = timezoneTime.getDateAccordingToGMT(endDateTime, locationId, em);
			List<BatchDetail> batchDetailList = new OrderManagementServiceBean().getBatchForStartEndDate(httpRequest,
					em, locationId, pickStartDate, pickEndDate);
			BatchDetail calculatedBatch = new BatchDetail();
			List<BatchDetail> list = new ArrayList<BatchDetail>();
			for (BatchDetail batchDetail : batchDetailList) {

				if (batchDetail.getStatus().equals("C") && batchDetail.getIsTipCalculated().equals("N")) {
					list.add(batchDetail);
				}
			}
			processBatchDetailForTipCalculation(locationId, list);
			// divide the list into chunks, such that there are not too many
			// threads
			// created to process all batch details. limit to number of CPU
			// cores
			// available plus 1 additional
			/*
			 * Stream<List<BatchDetail>> chunks = chunks(list,
			 * Runtime.getRuntime().availableProcessors() + 1);
			 * chunks.forEach((List<BatchDetail> subList) -> { try {
			 * processBatchDetailForTipCalculation(locationId, subList); } catch
			 * (Exception e) { logger.severe(httpRequest, e,
			 * "Exception during tip calculation: ", e.getMessage()); try {
			 * throw e; } catch (Exception e1) { // TODO Auto-generated catch
			 * block logger.severe(httpRequest, e1,
			 * "Exception during tip calculation: ", e1.getMessage()); throw e1;
			 * 
			 * } } });
			 */

			List<BatchDetail> updatedBatchDetailList = new ArrayList<BatchDetail>(batchDetailList.size());
			for (BatchDetail batchDetail : batchDetailList) {
				if (batchDetail.getStatus().equals("C")) {
					calculatedBatch = em.find(BatchDetail.class, batchDetail.getId());
					updatedBatchDetailList.add(calculatedBatch);
				}
			}

			return new JSONUtility().convertToJsonString(updatedBatchDetailList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private void processBatchDetailForTipCalculation(String locationId, List<BatchDetail> inBatchList)
			throws InterruptedException, NirvanaXPException, IOException, InvalidSessionException {
		CountDownLatch latch = new CountDownLatch(inBatchList.size());
		EntityManager em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
		for (BatchDetail batchDetail : inBatchList) {
			if (batchDetail.getStatus().equals("C") && batchDetail.getIsTipCalculated().equals("N")) {
				int count = new OrderManagementServiceBean().getClockedInEmployees(em, batchDetail.getStartTime(),
						batchDetail.getCloseTime());

				if (count != 0) {
					throw new NirvanaXPException(
							new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_EMPLOYEES_STILL_CLOCKED_IN,
									MessageConstants.ERROR_MESSAGE_EMPLOYEES_STILL_CLOCKED_IN,
									MessageConstants.ERROR_MESSAGE_EMPLOYEES_STILL_CLOCKED_IN));
				}
				batchDetail.setTipSettle(false);
				TipPoolCalculation calculation = new TipPoolCalculation(httpRequest, locationId,
						httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), 0, batchDetail, latch);
				Thread t = new Thread(calculation);
				t.start();

			}

		}
		latch.await();

	}

	private <T> Stream<List<T>> chunks(List<T> source, int length) {
		if (length <= 0)
			throw new IllegalArgumentException("length = " + length);
		int size = source.size();
		if (size <= 0)
			return Stream.empty();
		int fullChunks = (size - 1) / length;
		return IntStream.range(0, fullChunks + 1)
				.mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
	}

	@POST
	@Path("/updateTipPoolStatus")
	public String updateTipPoolStatus(BatchDetailUpdatePacket batchDetailUpdatePacket)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(batchDetailUpdatePacket, "BatchDetailUpdatePacket",
					httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			// get batch details for the location
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			// String pickStartDate =
			// timezoneTime.getDateAccordingToGMT(batchDetailUpdatePacket.getStartDateTime(),
			// batchDetailUpdatePacket.getLocationId(), em);
			// String pickEndDate =
			// timezoneTime.getDateAccordingToGMT(batchDetailUpdatePacket.getEndDateTime(),
			// batchDetailUpdatePacket.getLocationId(), em);
			String pickStartDate = batchDetailUpdatePacket.getStartDateTime();
			String pickEndDate = batchDetailUpdatePacket.getEndDateTime();
			List<BatchDetail> batchDetailList = bean.getBatchForStartEndDate(httpRequest, em,
					batchDetailUpdatePacket.getLocationId(), pickStartDate, pickEndDate);

			// check for employees still clocked in
			// send email if someone found to be still clocked in and throw
			// exception to stop processing
			checkEmployeesStillClockedIn(em, batchDetailUpdatePacket.getLocationId(), pickStartDate, pickEndDate,
					batchDetailUpdatePacket.getStartDateTime(), batchDetailList);

			// delete tip pool calculations, and add the clean batches to list
			// that will be processed
			List<BatchDetail> list = new ArrayList<BatchDetail>();
			for (BatchDetail batchDetail : batchDetailList) {
				if (batchDetail.getStatus().equals("C") && !batchDetail.getIsTipCalculated().equals("F")) {
					EntityTransaction tx = em.getTransaction();
					try {
						tx.begin();
						bean.deleteTipPoolCalculation(batchDetail, httpRequest, em);
						tx.commit();

						// successfully deleted, now add to list to be processed
						list.add(batchDetail);
					} catch (Exception e) {
						if (tx != null && tx.isActive()) {
							tx.rollback();
						}
						logger.severe(httpRequest, e,
								"unable to delete tip pool calculation for batch: " + batchDetail.getId(),
								e.getMessage());
					}
				}

			}

			// now work on the list of batch details that had successful
			// deletion of tip records
			// divide the list into chunks, such that there are not too many
			// threads
			// created to process all batch details. limit to number of CPU
			// cores
			// available plus 1 additional
			Stream<List<BatchDetail>> chunks = chunks(list, Runtime.getRuntime().availableProcessors() + 1);
			chunks.forEach((List<BatchDetail> subList) -> {
				try {
					CountDownLatch latch = new CountDownLatch(subList.size());
					for (BatchDetail batchDetail : subList) {

						if (batchDetail.getStatus().equals("C") && !batchDetail.getIsTipCalculated().equals("F")) {

							batchDetail.setTipSettle(true);
							TipPoolCalculation calculation = new TipPoolCalculation(httpRequest,
									batchDetailUpdatePacket.getLocationId(),
									httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), 0,
									batchDetail, latch);
							Thread t = new Thread(calculation);
							t.start();
						}
					}
					latch.await();
				} catch (Exception e) {
					logger.severe(httpRequest, e, "Exception during tip calculation: ", e.getMessage());
					throw new RuntimeException(e);
				}
			});

			// now read back these processed batches from database
			BatchDetail settledBatch = new BatchDetail();
			List<BatchDetail> settledBatchDetailsList = new ArrayList<BatchDetail>();
			for (BatchDetail batchDetail : list) {

				settledBatch = em.find(BatchDetail.class, batchDetail.getId());
				settledBatchDetailsList.add(settledBatch);

			}
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					batchDetailUpdatePacket.getLocationId(), 0);

			return new JSONUtility(httpRequest).convertToJsonString(settledBatchDetailsList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private void checkEmployeesStillClockedIn(EntityManager em, String locationId, String pickStartDate,
			String pickEndDate, String locationStartDate, List<BatchDetail> batchDetailList) throws Exception {
		for (BatchDetail batchDetail : batchDetailList) {
			if (batchDetail.getStatus().equals("C") && !batchDetail.getIsTipCalculated().equals("F")) {
				int count = new OrderManagementServiceBean().getClockedInEmployees(em, batchDetail.getStartTime(),
						batchDetail.getCloseTime());

				if (count != 0) {
					new OrderManagementServiceBean().sendEODMailForTipSettlementFromCronJob(httpRequest, em, locationId,
							pickStartDate, pickEndDate, locationStartDate);

					throw new NirvanaXPException(
							new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_EMPLOYEES_STILL_CLOCKED_IN,
									MessageConstants.ERROR_MESSAGE_EMPLOYEES_STILL_CLOCKED_IN,
									MessageConstants.ERROR_MESSAGE_EMPLOYEES_STILL_CLOCKED_IN));

				}
			}
		}
	}

	@GET
	@Path("/deleteTipPoolCalculation/{batchId}")
	public BatchDetail deleteTipPoolCalculation(@PathParam("batchId") int batchId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			BatchDetail batchDetail = em.find(BatchDetail.class, batchId);
			new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, batchDetail.getLocationId(), 0);

			if (batchDetail != null) {
				tx = em.getTransaction();
				tx.begin();
				bean.deleteTipPoolCalculation(batchDetail, httpRequest, em);
				batchDetail.setIsTipCalculated("N");
				tx.commit();
			}

			return batchDetail;
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param operation
	 *            the operation
	 * @param postPacket
	 *            the post packet
	 */
	private void sendPacketForBroadcast(String operation, PostPacket postPacket, String posnService) {

		try {

			MessageSender messageSender = new MessageSender();
			operation = ServiceOperationsUtility.getOperationName(operation);
			messageSender.sendMessage(httpRequest, postPacket.getClientId(), posnService, operation, null,
					postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
					postPacket.getSessionId());
		} catch (Exception e) {
			logger.severe(httpRequest, e);
		}

	}

	@GET
	@Path("/sendEmailForTipSettlement/{batchId}")
	public boolean sendEmailForTipSettlement(@PathParam("batchId") int batchId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			tx = em.getTransaction();
			// why is a transaction started here when all that is being done is
			// select from database?
			tx.begin();
			BatchDetail bd = em.find(BatchDetail.class, batchId);
			bean.sendEODMailForTipSettlementFromEOD(httpRequest, em, bd.getLocationId(), bd, null);
			tx.commit();
			return true;

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.severe(e);
		} catch (Exception e) {
			logger.severe(e);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return false;
	}

	public void sendSNSByNumber(EntityManager em, String userId, String statusId, String locationId, BigDecimal balDue,
			String orderNumber, BigDecimal amountPaid, OrderHeader order, String sessionId, String merchantId,
			String clientId, boolean isOrderConfirm, int needToSendSNS, String OldStatusId) throws IOException {

		logger.severe("userId=needToSendSNS===================================================================" + userId
				+ "==" + needToSendSNS);
		if (userId != null && needToSendSNS != 1 && !statusId.equals(OldStatusId)) {
			User user = (User) new CommonMethods().getObjectById("User", em, User.class, userId);

			// em.getTransaction().begin();
			PublishResult result = null;
			try {

				String message = getMessageBodyToSend(em, user, statusId, locationId, balDue, orderNumber, amountPaid,
						order, sessionId, merchantId, clientId, isOrderConfirm);

				if (message != null) {

					AmazonSNS snsClient = getSNS();// AmazonSNSClientBuilder.defaultClient();//
					// AmazonSNS snsClient =
					// AmazonSNSClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
					// snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
					Map<String, MessageAttributeValue> smsAttributes = new java.util.HashMap<String, MessageAttributeValue>();

					smsAttributes.put("AWS.SNS.SMS.SMSType",
							new MessageAttributeValue().withStringValue("Transactional") // Sets
																							// the
																							// type
																							// to
																							// promotional.
									.withDataType("String"));

					smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue().withStringValue("NXP") // The
																													// sender
																													// ID
																													// shown
																													// on
																													// the
																													// device.
							.withDataType("String"));

					result = snsClient.publish(new PublishRequest().withMessage(message)
							.withPhoneNumber(user.getPhone()).withMessageAttributes(smsAttributes));
					// the
					// message
					// ID.

					/*
					 * PublishResult result = null;
					 * 
					 * logger.
					 * severe("---------------------------------------- 1 ");
					 * AmazonSNS snsClient =
					 * AmazonSNSClientBuilder.defaultClient();// //AmazonSNS
					 * snsClient =
					 * AmazonSNSClientBuilder.standard().withRegion(Regions.
					 * US_WEST_2).build();
					 * snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
					 * logger.
					 * severe("---------------------------------------- 2 ");
					 * 
					 * logger.
					 * severe("---------------------------------------- 3 ");
					 * Map<String, MessageAttributeValue> smsAttributes = new
					 * java.util.HashMap<String, MessageAttributeValue>();
					 * 
					 * logger.
					 * severe("---------------------------------------- 4 ");
					 * smsAttributes.put("AWS.SNS.SMS.SMSType", new
					 * MessageAttributeValue() .withStringValue("Transactional")
					 * //Sets the type to promotional. .withDataType("String"));
					 * 
					 * logger.
					 * severe("---------------------------------------- 5 ");
					 * smsAttributes.put("AWS.SNS.SMS.SenderID", new
					 * MessageAttributeValue() .withStringValue("NXP") //The
					 * sender ID shown on the device. .withDataType("String"));
					 * 
					 * logger.
					 * severe("---------------------------------------- 6 ");
					 * result = snsClient.publish(new PublishRequest()
					 * .withMessage(message) .withPhoneNumber(user.getPhone())
					 * .withMessageAttributes(smsAttributes)); logger.
					 * severe("---------------------------------------- 7 ");
					 * System.out.println(result); // Prints the message ID.
					 */
				}

			} catch (RuntimeException e) {

				logger.severe("" + e);
			}

			// em.getTransaction().commit();
		}

	}

	private static AmazonSNS getSNS() throws IOException {
		String awsAccessKey = System.getProperty("AWS_ACCESS_KEY_ID"); // "YOUR_AWS_ACCESS_KEY";
		String awsSecretKey = System.getProperty("AWS_SECRET_KEY"); // "YOUR_AWS_SECRET_KEY";

		if (awsAccessKey == null)
			awsAccessKey = "AKIAJ4FQSALKK4XTOZOQ";
		if (awsSecretKey == null)
			awsSecretKey = "qLOsioPoWtJWp+EldQMAN2FUXkQiiMv5P0PqpEET";

		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		AmazonSNS sns = new AmazonSNSClient(credentials);

		sns.setEndpoint("https://sns.us-west-2.amazonaws.com");
		return sns;
	}

	@SuppressWarnings("unused")
	private String getMessageBodyToSend(EntityManager em, User user, String statusId, String locationId,
			BigDecimal balDue, String orderNumber, BigDecimal amountPaid, OrderHeader order, String sessionId,
			String merchantId, String clientId, boolean isOrderConfirm) {

		String retrnMsg = null;
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				statusId);
		if (orderStatus.getIsSendSms() == 1 || isOrderConfirm) {

			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;

			String queryStringT;
			if (!isOrderConfirm) {
				queryStringT = "select ci from SMSTemplate ci where  ci.status not in ('I','D') " + " and ci.id =  "
						+ orderStatus.getTemplateId();

			} else {
				queryStringT = "select ci from SMSTemplate ci where  ci.status not in ('I','D') "
						+ " and ci.templateName like 'Order Confirmation SMS' and ci.locationId = '" + locationId + "'";
			}

			TypedQuery<SMSTemplate> queryT = em.createQuery(queryStringT, SMSTemplate.class);
			SMSTemplate snsSmsTemplate = queryT.getSingleResult();

			String queryStringConfig = "select ci from SmsConfig ci where  ci.status !='D'and "
					+ "ci.gatewayName  = 'AWS SMS' ";
			TypedQuery<SmsConfig> query = em.createQuery(queryStringConfig, SmsConfig.class);
			SmsConfig smsConfig = query.getSingleResult();

			if (snsSmsTemplate == null && !isOrderConfirm) {
				logger.severe("Sns Sms Template Not Configure");
				return null;

			} else if (smsConfig == null) {
				logger.severe("Sns Sms Config Not Configure");
				return null;

			} else {
				retrnMsg = snsSmsTemplate.getTemplateText();
				retrnMsg = retrnMsg.replace("<BalDue>", balDue + "");
				retrnMsg = retrnMsg.replace("<BusinessName>", location.getName());
				retrnMsg = retrnMsg.replace("<OrderNumber>", orderNumber + "");
				retrnMsg = retrnMsg.replace("<AmountPaid>", amountPaid + "");

				try {
					OrderSource orderSource = getOrderSourceById(order.getOrderSourceId(), em);
					OrderSourceGroup orderSourceGroup = getOrderSourceGroupById(order.getOrderSourceId(), em);

					retrnMsg = retrnMsg.replace("<OrderSourceGroup>", orderSourceGroup.getDisplayName());
					retrnMsg = retrnMsg.replace("<OrderSource>", orderSource.getDisplayName());

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.severe(e1);
				}

				String gName = "";
				if (user.getFirstName() != null && user.getLastName() != null) {
					gName = gName + user.getFirstName() + " " + user.getLastName();
				} else if (user.getFirstName() != null && user.getLastName() == null) {
					gName = gName + user.getFirstName();
				} else if (user.getFirstName() == null && user.getLastName() != null) {
					gName = gName + user.getLastName();
				}

				retrnMsg = retrnMsg.replace("<GuestName>", gName);
				retrnMsg = retrnMsg.replace("<GuestPhone>", user.getPhone());

				String datetime = new TimezoneTime().getDateTimeFromGMTToLocation(em, order.getScheduleDateTime(),
						locationId);
				SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");

				try {
					java.util.Date date = toformatter.parse(datetime);
					datetime = fromFormatter.format(date);
				} catch (ParseException e) {
					logger.severe("Unable to parse date", datetime, "while generating pdf receipt");
				}

				retrnMsg = retrnMsg.replace("<SchedDateTime>", datetime);

				try {

					if (retrnMsg.contains("<ClickHere>")) {

						// EncryptionDecryption encryptionDecryption=new
						// EncryptionDecryption();
						String path = "/OrderManagementServiceV6/updateOrderStatusForPickedUp/" + order.getId() + "/"
								+ sessionId + "/" + location.getId() + "/" + merchantId + "/" + clientId + "/";
						// String
						// encryptedURL=encryptionDecryption.encryption(path);
						String encryptedPath = "https://" + ConfigFileReader.getQRCodeServerName() + path;

						String link = new CommonMethods().shortURLWithTinyURL(encryptedPath);

						retrnMsg = retrnMsg.replace("<ClickHere>", " " + link + " ");
					}

				} catch (Exception e) {
					// TODO: handle exception
					logger.severe(e);
				}

				new InsertIntoHistory().insertSMSIntoHistory(em, user, snsSmsTemplate, retrnMsg, user.getPhone(),
						smsConfig, null, locationId);

			}
		}

		logger.severe("SMS Text " + retrnMsg);
		return retrnMsg;

	}

	private OrderSource getOrderSourceById(String id, EntityManager em) throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
		Root<OrderSource> orderSource = criteria.from(OrderSource.class);
		TypedQuery<OrderSource> query = em
				.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.id), id)));
		return query.getSingleResult();
	}

	private OrderSourceGroup getOrderSourceGroupById(String id, EntityManager em) throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
		Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
		TypedQuery<OrderSourceGroup> query = em.createQuery(
				criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.id), id)));
		return query.getSingleResult();
	}

	@POST
	@Path("/checkTransfer")
	public String checkTransfer(OrderTransferPacket orderPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderTransferPacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, orderPacket);
			tx = em.getTransaction();
			tx.begin();
			String locationId = orderPacket.getLocationId();
			OrderHeader orderHeader = new OrderServiceForPost().checkTransfer(httpRequest, em, orderPacket);
			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderId());

			if (tx.isActive()) {
				tx.commit();
			}
			orderPacket.setLocationId(locationId);
			orderPacket.setOrderHeader(header);
			new OrderServiceForPost().sendPacketForBroadcast(httpRequest, orderPacket,
					POSNServiceOperations.OrderManagementService_update.name(), false);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
					Integer.parseInt(orderPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(header);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getUsersToDiscountByUserIdAndDiscountCodeAndLocationId/{userId}/{dicountCode}/{locationId}/{date}/{discountId}")
	public String getUsersToDiscountByUserIdAndDiscountCodeAndLocationId(@PathParam("userId") String userId,
			@PathParam("dicountCode") String dicountCode, @PathParam("locationId") String locationId,
			@PathParam("date") String date, @PathParam("discountId") String discountId)
			throws IOException, InvalidSessionException {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			return new OrderServiceForPost().getUsersToDiscountByUserIdAndDisCode(httpRequest, em, userId, dicountCode,
					locationId, date, discountId);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getUsersToDiscountByUserIdAndDiscountCodeAndLocationIdWithoutValidation/{userId}/{dicountCode}/{locationId}/{discountId}")
	public String getUsersToDiscountByUserIdAndDiscountCodeAndLocationIdWithoutValidation(
			@PathParam("userId") String userId, @PathParam("dicountCode") String dicountCode,
			@PathParam("locationId") String locationId,

			@PathParam("discountId") String discountId) throws IOException, InvalidSessionException {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			return new OrderServiceForPost().getUsersToDiscountByUserIdAndDiscountCodeAndLocationIdWithoutValidation(
					httpRequest, em, userId, dicountCode, locationId, discountId);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/updateUsersToDiscountNumberOfTimeDiscountUsed/{userId}/{dicountCode}/{locationId}/{discountId}")
	public boolean updateUsersToDiscountNumberOfTimeDiscountUsed(@PathParam("userId") String userId,
			@PathParam("dicountCode") String dicountCode, @PathParam("locationId") String locationId,
			@PathParam("discountId") String discountId) throws IOException, InvalidSessionException {

		EntityManager em = null;
		try {
			new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, locationId, 0);

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			return new OrderServiceForPost().updateUsersToDiscountNumberOfTimeDiscountUsed(httpRequest, em, userId,
					dicountCode, locationId, discountId);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateDeliveryOrderForDriver/")
	public String updateDeliveryOrderForDriver(DriverOrderPostPacket packet) throws Exception {

		EntityManager emLocal = null;
		EntityManager emGlobal = null;
		EntityTransaction txGlobal = null;
		EntityTransaction txLocal = null;
		OrderHeader header = new OrderHeader();
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(packet, "DriverOrderPostPacket", httpRequest);
			emLocal = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			// finding locations
			Location l = emLocal.find(Location.class, packet.getLocationId());
			// finding order Header
			header = new OrderManagementServiceBean().getOrderById(emLocal, packet.getOrderId());
			// finding users

			User user = emLocal.find(User.class, header.getUsersId());
			// create driver order
			if (l != null && header != null && user != null) {
				DriverOrder driverOrder = new DriverOrder();

				try {

					CriteriaBuilder builder = emGlobal.getCriteriaBuilder();
					CriteriaQuery<DriverOrder> criteria = builder.createQuery(DriverOrder.class);
					Root<DriverOrder> orderPaymentDetail = criteria.from(DriverOrder.class);
					TypedQuery<DriverOrder> query = emGlobal.createQuery(criteria.select(orderPaymentDetail)
							.where(builder.equal(orderPaymentDetail.get(DriverOrder_.orderId), packet.getOrderId())));
					DriverOrder driverOrderDB = query.getSingleResult();
					if (driverOrderDB != null) {
						driverOrder.setId(driverOrderDB.getId());
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.severe(e);
				}

				Location location = emLocal.find(Location.class, packet.getLocationId());

				driverOrder.setBusinessAddress(createAddress(l.getAddress()));
				driverOrder.setCustomerAddress(createAddress(header.getAddressShipping()));
				driverOrder.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				driverOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				driverOrder.setCreatedBy(packet.getUpdatedBy());
				driverOrder.setUpdatedBy(packet.getUpdatedBy());
				driverOrder.setFirstName(user.getFirstName());
				driverOrder.setLastName(user.getLastName());
				driverOrder.setOrderId(packet.getOrderId());
				driverOrder.setOrderNumber(header.getOrderNumber());
				driverOrder.setBusinessName(l.getName());
				driverOrder.setEmail(user.getEmail());
				driverOrder.setPhone(user.getPhone());
				driverOrder.setStatusId(header.getOrderStatusId());
				driverOrder.setTime(header.getCreated());
				driverOrder.setScheduleDateTime(header.getScheduleDateTime());
				OrderStatus orderStatus = emLocal.find(OrderStatus.class, header.getOrderStatusId());
				if (orderStatus != null)
					driverOrder.setStatusName(orderStatus.getDisplayName());

				driverOrder.setNxpAccessToken(httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));

				driverOrder.setBusinessId(location.getBusinessId());
				driverOrder.setLocationsId(location.getId());

				Business business = emGlobal.find(Business.class, location.getBusinessId());
				driverOrder.setAccountId(business.getAccountId());

				driverOrder.setDriverId(packet.getDriverId());

				txGlobal = emGlobal.getTransaction();
				txGlobal.begin();

				if (driverOrder.getId() != 0) {
					driverOrder = emGlobal.merge(driverOrder);
				} else {
					emGlobal.persist(driverOrder);
				}

				txGlobal.commit();

				txLocal = emLocal.getTransaction();
				txLocal.begin();
				header.setDriverId(packet.getDriverId());
				header.setUpdatedBy(packet.getUpdatedBy());
				header.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				header.setOrderStatusId(packet.getOrderStatusId());
				header = emLocal.merge(header);
				txLocal.commit();

				header.setOrderDetailItems(null);
				OrderPacket orderPacket = new OrderPacket();
				orderPacket.setOrderHeader(header);
				orderPacket.setMerchantId(packet.getMerchantId());
				orderPacket.setClientId(packet.getClientId());
				orderPacket.setLocationId(packet.getLocationId());
				orderPacket.setEchoString(packet.getEchoString());
				orderPacket.setSchemaName(packet.getSchemaName());
				orderPacket.setSessionId(packet.getSessionId());
				orderPacket.setIdOfSessionUsedByPacket(packet.getIdOfSessionUsedByPacket());

				if (driverOrder.getEmail() != null && !driverOrder.getEmail().isEmpty()) {
					try {
						// insert into order history
						ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
						// 0 because mail sending from dine in
						String data = "";

						data = receiptPDFFormat
								.createReceiptPDFString(emLocal, httpRequest, header.getId(), 1, true, false)
								.toString();
						// Send email functionality :- printing order
						// number instead of orderID :- By AP 2015-12-29
						EmailTemplateKeys.sendOrderReceivedEmailToCustomer(httpRequest, emLocal,
								orderPacket.getLocationId(), header.getUsersId(), header.getUpdatedBy(), data,
								EmailTemplateKeys.ORDER_RECEIVED, header.getOrderNumber(), driverOrder.getEmail(),
								null);

					} catch (Exception e) {
						logger.severe(httpRequest, e, "Could not send email due to configuration mismatch");
					}
				}
				new OrderServiceForPost().sendPacketForBroadcast(httpRequest, orderPacket,
						POSNServiceOperations.OrderManagementService_update.name(), true);
				// call synchPacket for store forward
				new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(),
						Integer.parseInt(orderPacket.getMerchantId()));

				return new JSONUtility(httpRequest).convertToJsonString(header);
			}

		} catch (RuntimeException e) {
			if (txGlobal != null && txGlobal.isActive()) {

				txGlobal.rollback();
			}
			if (txLocal != null && txLocal.isActive()) {
				txLocal.rollback();
			}

			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(emLocal);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
		}
		return new JSONUtility(httpRequest).convertToJsonString(header);
	}

	@GET
	@Path("/sendEmailForQuotation/{orderId}/{userId}/{locationId}/{emailAddress}")
	public String sendEmailForQuotation(@PathParam(value = "orderId") String orderId,
			@PathParam(value = "userId") String userId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "emailAddress") String emailAddress,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager localEM = null;
		EntityManager globalEM = null;
		EntityTransaction tx = null;
		try {
			localEM = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", localEM,
					OrderHeader.class, orderId);
			// 0 because mail sending from dine in
			User user = null;
			UserManagementServiceBean userManagementServiceBean = new UserManagementServiceBean(httpRequest, localEM);
			try {
				user = userManagementServiceBean.getUserByEmail(emailAddress);
			} catch (NoResultException e) {
				logger.severe(httpRequest, "User not present in database so need to create user : " + emailAddress);
			}
			try {
				tx = localEM.getTransaction();
				tx.begin();

				String data = receiptPDFFormat.createReceiptPDFString(localEM, httpRequest, orderId, 1, false, true)
						.toString();

				String emailSubject = "";
				OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", localEM,
						OrderSource.class, header.getOrderSourceId());
				OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById(
						"OrderSourceGroup", localEM, OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
				emailSubject = orderSourceGroup.getDisplayName() + " Quote";
				EmailTemplateKeys.sendQuotationEmailToCustomer(httpRequest, localEM, locationId, header.getUsersId(),
						header.getUpdatedBy(), data, EmailTemplateKeys.QUOTE_RECEIVED_EMAIL_STRING,
						header.getOrderNumber(), emailAddress, emailSubject);
				// change by vaibhav,suggested by Kris, email-date- Dec 03, 2015
				// 12:57 am
				if (user == null && header.getUsersId() == null && header.getUsersId() == null) {

					user = createNewUserBeforeSendingEmail(localEM, globalEM, emailAddress, header.getCreatedBy(),
							header.getUpdatedBy(), locationId, null);
					header.setUsersId(user.getId());
					localEM.merge(header);
				} else {

					if (header.getReservationsId() != null) {
						Reservation reservation = new CommonMethods().getReservationById(httpRequest, localEM,
								header.getReservationsId());
						// for takeout and delivery we do not have reservations
						if (reservation != null) {
							if (user == null) {
								user = createNewUserBeforeSendingEmail(localEM, globalEM, emailAddress,
										header.getCreatedBy(), header.getUpdatedBy(), locationId, null);
							}
							if (reservation != null && reservation.getEmail() == null) {
								reservation.setEmail(emailAddress);
								localEM.merge(reservation);
							}

						}
					}

				}
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	@POST
	@Path("/addUpdateStorageType")
	public String addUpdateStorageType(StorageTypePacket storageTypePacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, storageTypePacket);
			tx = em.getTransaction();
			tx.begin();
			StorageType storageType = new OrderServiceForPost().addUpdateStorageType(em,
					storageTypePacket.getStorageType());
			storageTypePacket.setStorageType(storageType);
			tx.commit();

			List<Location> list = getAllLocationList(httpRequest, em);
			Location baseLocation = new CommonMethods().getBaseLocation(em);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(storageTypePacket, "StorageTypePacket", httpRequest);

			for (Location location : list) {
				storageTypePacket.setLocationId(location.getId() + "");
				sendPacketForBroadcast(POSNServiceOperations.OrderManagementService_addUpdateStorageType.name(),
						storageTypePacket, POSNServices.ItemsService.name());

			}
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, baseLocation.getId(),
					Integer.parseInt(storageTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(storageTypePacket);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteStorageType")
	public String deleteStorageType(StorageTypePacket storageTypePacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try {
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(storageTypePacket, "StorageTypePacket", httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest,
					sessionId, storageTypePacket);
			tx = em.getTransaction();
			tx.begin();
			StorageType storageType = new OrderServiceForPost().deleteStorageType(em,
					storageTypePacket.getStorageType());
			storageTypePacket.setStorageType(storageType);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.OrderManagementService_deleteStorageType.name(),
					storageTypePacket, POSNServices.ItemsService.name());
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, storageTypePacket.getLocationId(),
					Integer.parseInt(storageTypePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(storageTypePacket);
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllStorageType/")
	public String getAllStorageType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<StorageType> storageTypeList = new OrderServiceForPost().getAllStorageType(em);
			return new JSONUtility(httpRequest).convertToJsonString(storageTypeList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getStorageTypeById/{id}")
	public String getAllStorageType(@PathParam("id") String id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception, InvalidSessionException, IOException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			StorageType storageType = (StorageType) new CommonMethods().getObjectById("StorageType", em,
					StorageType.class, id);
			;
			return new JSONUtility(httpRequest).convertToJsonString(storageType);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/*
	 * @POST
	 * 
	 * @Path("/addUpdateOrderDetailItemComment") public String
	 * addUpdateOrderDetailItemComment(OrderDetailItemPacket
	 * orderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
	 * throws Exception { EntityManager em = null; EntityTransaction tx = null;
	 * try { em = LocalSchemaEntityManager.getInstance().
	 * getEntityManagerWithSessionIdInPostPacket(httpRequest, sessionId,
	 * orderPacket); tx = em.getTransaction(); tx.begin(); OrderHeader
	 * orderHeader = new
	 * OrderServiceForPost().addUpdateOrderDetailItemComment(httpRequest, em,
	 * orderPacket); tx.commit(); return new
	 * JSONUtility(httpRequest).convertToJsonString(orderHeader); } catch
	 * (RuntimeException e) { if (tx != null && tx.isActive()) { tx.rollback();
	 * } throw e; } finally {
	 * LocalSchemaEntityManager.getInstance().closeEntityManager(em); } }
	 */
	List<Location> getAllLocationList(HttpServletRequest httpRequest, EntityManager em) {

		// get users to business list

		TypedQuery<Location> query = em.createQuery(
				"SELECT l FROM Location l where l.locationsTypeId = 1 and l.isGlobalLocation = 0 ", Location.class);

		List<Location> locations = query.getResultList();
		return locations;

	}

	@GET
	@Path("/sendEmailReceiptForAdditionalQuestion/{orderId}/{userId}/{locationId}/{emailAddress}")
	public String snedEmailReceiptForAdditionalQuestion(@PathParam(value = "orderId") String orderId,
			@PathParam(value = "userId") String userId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "emailAddress") String emailAddress,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager localEM = null;
		EntityManager globalEM = null;
		EntityTransaction tx = null;
		try {
			localEM = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", localEM,
					OrderHeader.class, orderId);
			// 0 because mail sending from dine in
			User user = null;
			UserManagementServiceBean userManagementServiceBean = new UserManagementServiceBean(httpRequest, localEM);
			try {
				user = userManagementServiceBean.getUserByEmail(emailAddress);
			} catch (NoResultException e) {
				logger.severe(httpRequest, "User not present in database so need to create user : " + emailAddress);
			}
			try {
				tx = localEM.getTransaction();
				tx.begin();

				String data = receiptPDFFormat
						.createEmailReceiptForAdditionalQuestion(localEM, httpRequest, orderId, 1, false, true)
						.toString();

				String emailSubject = "";
				OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", localEM,
						OrderSource.class, header.getOrderSourceId());
				OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById(
						"OrderSourceGroup", localEM, OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
				emailSubject = orderSourceGroup.getDisplayName() + " Order Additional Question";
				EmailTemplateKeys.sendOrderAdditionQuestionToCustomer(httpRequest, localEM, locationId,
						header.getUsersId(), header.getUpdatedBy(), data, EmailTemplateKeys.QUOTE_RECEIVED_EMAIL_STRING,
						header.getOrderNumber(), emailAddress, emailSubject);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	@GET
	@Path("/sendEmailReceiptForAdditionalQuestion/{orderId}/{userId}/{locationId}/{emailAddress}")
	public String sendEmailReceiptForAdditionalQuestion(@PathParam(value = "orderId") String orderId,
			@PathParam(value = "userId") String userId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "emailAddress") String emailAddress,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager localEM = null;
		EntityManager globalEM = null;
		EntityTransaction tx = null;
		try {
			localEM = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", localEM,
					OrderHeader.class, orderId);
			// 0 because mail sending from dine in
			User user = null;
			UserManagementServiceBean userManagementServiceBean = new UserManagementServiceBean(httpRequest, localEM);
			try {
				user = userManagementServiceBean.getUserByEmail(emailAddress);
			} catch (NoResultException e) {
				logger.severe(httpRequest, "User not present in database so need to create user : " + emailAddress);
			}
			try {
				tx = localEM.getTransaction();
				tx.begin();

				String data = receiptPDFFormat
						.createEmailReceiptForAdditionalQuestion(localEM, httpRequest, orderId, 1, false, true)
						.toString();

				String emailSubject = "";
				OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", localEM,
						OrderSource.class, header.getOrderSourceId());
				OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById(
						"OrderSourceGroup", localEM, OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
				emailSubject = orderSourceGroup.getDisplayName() + " Order Additional Question";
				EmailTemplateKeys.sendOrderAdditionQuestionToCustomer(httpRequest, localEM, locationId,
						header.getUsersId(), header.getUpdatedBy(), data, EmailTemplateKeys.QUOTE_RECEIVED_EMAIL_STRING,
						header.getOrderNumber(), emailAddress, emailSubject);

				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/updateOrderStatusForPickedUp/{id}/{refNumber}/{locationId}/{merchantId}/{clientId}")
	public Response updateOrderStatusForPickedUp(@PathParam("id") String id, @PathParam("refNumber") String refNumber,
			@PathParam("locationId") String locationId, @PathParam("merchantId") String merchantId,
			@PathParam("clientId") String clientId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, refNumber);
			new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, locationId, 0);

			OrderPacket orderPacket = new OrderPacket();
			OrderHeader orderHeader = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,
					OrderHeader.class, id);

			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
					OrderSource.class, orderHeader.getOrderSourceId());

			OrderServiceForPost orderServiceForPost = new OrderServiceForPost();
			if (orderHeader
					.getOrderStatusId() != orderServiceForPost.getOrderStatusByNameAndLocation(em, "Ready to Order",
							locationId, orderSource.getOrderSourceGroupId()).getId()
					&& orderHeader.getOrderStatusId() != orderServiceForPost.getOrderStatusByNameAndLocation(em,
							"Void Order", locationId, orderSource.getOrderSourceGroupId()).getId()
					&& orderHeader.getOrderStatusId() != orderServiceForPost.getOrderStatusByNameAndLocation(em,
							"Cancel Order", locationId, orderSource.getOrderSourceGroupId()).getId()) {
				tx = em.getTransaction();
				tx.begin();

				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
				Root<OrderStatus> ic = criteria.from(OrderStatus.class);
				TypedQuery<OrderStatus> query = em.createQuery(
						criteria.select(ic).where(builder.equal(ic.get(OrderStatus_.name), "Presented At Curb Side"),
								builder.equal(ic.get(OrderStatus_.orderSourceGroupId),
										orderSource.getOrderSourceGroupId()),
								builder.notEqual(ic.get(OrderStatus_.status), "D"),
								builder.notEqual(ic.get(OrderStatus_.status), "I")));
				OrderStatus orderStatus = query.getSingleResult();

				orderHeader.setOrderStatusId(orderStatus.getId());

				orderPacket.setOrderHeader(orderHeader);

				orderPacket.setLocationId(locationId + "");
				orderPacket.setMerchantId(merchantId + "");
				// orderPacket.setClientId(clientId+"");
				orderPacket.setEchoString("updateOrderStatusForDuplicateCheck-update order status");

				orderHeader = orderServiceForPost.updateOrderStatus(httpRequest, em, orderPacket);

				Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
						locationId);
				;
				tx.commit();

				StringBuilder receipt = new StringBuilder().append("<html>").append("<head>")

						.append("</head>").append("<body>").append("<center>")
						.append("<div style= 'font-size:50px'> <br><br><br><br><br><br><br>" + "Your Order number "
								+ orderHeader.getOrderNumber() + " <br>" + "is updated to " + location.getName() + "."
								+ "<br>Please wait at curb side, <br>"
								+ "some one will be there with your order shortly." + "</div> </center></body></html>");

				/*
				 * return "Your Order number " + orderHeader.getOrderNumber() +
				 * " is updated to "+location.getName()+"."+
				 * " Please wait at curb side, some one will be there with your order shortly."
				 * ;
				 */

				return Response.ok(receipt.toString()).build();

			} else {
				return Response.ok("Sorry!!! Unable To update Order/>").build();
			}

		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/synchPacketsWithServer/{locationId}")
	public String synchPacketsWithServer(@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		ProcessLock lock = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			new StoreForwardUtility().synchUtility(em, httpRequest, locationId);
			new StoreForwardUtility().getRejectedPublisherRetry(em, locationId);
		} catch (Exception e) {
			logger.severe(e);
		} finally {

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
		return "" + true;

	}

	@GET
	@Path("/pullSynchPacketsWithServer/{accountId}/{locationId}")
	public boolean pullSynchPacketsWithServer(@PathParam("accountId") int accountId,
			@PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			new StoreForwardUtility().pullSynchUtility(em, httpRequest, accountId, locationId,
					"/OrderManagementServiceV6/publishers/" + accountId + "/" + locationId);

		} catch (Exception e) {
			logger.severe(e);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return true;

	}

	@GET
	@Path("/publishers/{accountId}/{locationId}")
	public String publishers(@PathParam("accountId") int accountId, @PathParam("locationId") String locationId)
			throws Exception, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			return new JSONUtility(httpRequest)
					.convertToJsonString(new StoreForwardUtility().getNonSynchPublisher(em, locationId));

		} catch (Exception e) {
			logger.severe(e);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;

	}

	@POST
	@Path("/createBatch/")
	public String createBatch(BatchDetailPacket packet)
			throws Exception, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			em.getTransaction().begin();
			BatchDetail bd = PaymentBatchManager.getInstance().createNewBatches(httpRequest, em, packet.getLocationId(),
					packet, "21");
			em.getTransaction().commit();
			return new JSONUtility(httpRequest).convertToJsonString(bd);

		} catch (Exception e) {
			logger.severe(e);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;

	}

	@POST
	@Path("/manageBatch/")
	public String manageBatch(BatchDetailPacket packet)
			throws Exception, InvalidSessionException, IOException, NirvanaXPException {
		EntityManager em = null;
		EntityTransaction tx= null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx= em.getTransaction();
			tx.begin();
			LocationSetting locationSetting = getAllLocationSettingByLocationId(em, packet.getLocationId());
			logger.severe("" + locationSetting.getLocationBatchTimingList().toString());
			if (locationSetting != null && locationSetting.getLocationBatchTimingList() != null
					&& !locationSetting.getLocationBatchTimingList().isEmpty()) {

				TimezoneTime time = new TimezoneTime();
				String currentTime = time.getCurrentTimeHHMM(em, packet.getLocationId()) + ":00";
				String date[] = time.getCurrentTimeofLocation(packet.getLocationId(), em);
				String curDate = date[0];

				for (LocationBatchTiming locationBatchTiming : locationSetting.getLocationBatchTimingList()) {
					// check start time
					logger.severe(
							currentTime + "<-current_time and batch time -> " + locationBatchTiming.getStartTime());
					if (currentTime.equals(locationBatchTiming.getStartTime())) {
						logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@batch creation starts");
						BatchDetail bd = PaymentBatchManager.getInstance().createNewBatches(httpRequest, em,
								packet.getLocationId(), packet, "21");
					}
					// check end time
					if (currentTime.equals(locationBatchTiming.getEndtime())) {
						logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@batch close");
						new OrderManagementServiceBean().closeBusiness(httpRequest, em,
								packet.getBatchDetail().getUpdatedBy(), packet.getLocationId(),
								httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), packet,
								curDate);
					}
				}
			}
			tx.commit();

			try {

				if (locationSetting != null && locationSetting.getLocationBatchTimingList() != null
						&& !locationSetting.getLocationBatchTimingList().isEmpty()) {

					TimezoneTime time = new TimezoneTime();
					String currentTime = time.getCurrentTimeHHMM(em, packet.getLocationId()) + ":00";
					String date[] = time.getCurrentTimeofLocation(packet.getLocationId(), em);
					String curDate = date[0];

					for (LocationBatchTiming locationBatchTiming : locationSetting.getLocationBatchTimingList()) {
						// check start time

						// check batch falls
						// check batch exist
						String todayDateTime = curDate + " " + locationBatchTiming.getEndtime();
						String gmtTodayTime = time.getDateAccordingToGMT(todayDateTime, packet.getLocationId(), em);
						logger.severe(currentTime + "<-current_time and batch time -> " + gmtTodayTime);
						boolean batchExist = isBatchExistBetweenTime(em, gmtTodayTime, packet.getLocationId());
						boolean batchCloseTimeExist = isBatchSettingExistBetweenTime(em, "00:00:00", currentTime,
								locationBatchTiming.getEndtime());
						logger.severe(batchExist + "<-isBatchExistBetweenTime and isBatchSettingExistBetweenTime -> "
								+ batchCloseTimeExist);
						if (batchCloseTimeExist && !batchExist) {
							try {
								tx.begin();
								new OrderManagementServiceBean().closeBusiness(httpRequest, em,
										packet.getBatchDetail().getUpdatedBy(), packet.getLocationId(),
										httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), packet,
										curDate);
								tx.commit();
							} catch (Exception e) {
								logger.severe(e);
							}

//							logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@batch creation starts");
//							Thread.sleep(1000);
//							tx.begin();
//							PaymentBatchManager.getInstance().createNewBatches(httpRequest, em, packet.getLocationId(),
//									packet, "21");
//							tx.commit();
						}

					}
				}

			} catch (Exception e) {
				logger.severe(e);
			}
		}catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}  finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;

	}

	// @POST
	// @Path("/checkBatchClose/")
	// public String checkBatchClose(BatchDetailPacket packet)
	// throws Exception, InvalidSessionException, IOException,
	// NirvanaXPException {
	// EntityManager em = null;
	// try {
	// em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
	// null);
	//
	// TimezoneTime time = new TimezoneTime();
	//
	// String date[] = time.getCurrentTimeofLocation(packet.getLocationId(),
	// em);
	// String curDate = date[0];
	// BatchDetail bd =
	// PaymentBatchManager.getInstance().getCurrentBatchBySession(httpRequest,
	// em,
	// packet.getLocationId(), false, packet, "21");
	//
	// if (bd != null && bd.getCloseTime() == 0) {
	//
	// logger.severe(((new TimezoneTime().getGMTTimeInMilis() -
	// bd.getStartTime()) / 3600)
	// + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	// if (((new TimezoneTime().getGMTTimeInMilis() - bd.getStartTime()) / 3600)
	// > 24000) {
	// em.getTransaction().begin();
	// new OrderManagementServiceBean().closeBusiness(httpRequest, em,
	// packet.getBatchDetail().getUpdatedBy(), packet.getLocationId(),
	// httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME),
	// packet, curDate);
	// em.getTransaction().commit();
	// Thread.sleep(60100);
	// // after 1 mins create new batch
	// logger.severe(
	// "wakkkkkkkkkkkkkeeeeeeeeeeeeeeeee up after one
	// mintureeeeeee@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	// try {
	// em.getTransaction().begin();
	// PaymentBatchManager.getInstance().createNewBatches(httpRequest, em,
	// packet.getLocationId(),
	// packet, "21");
	// em.getTransaction().commit();
	// } catch (Exception e) {
	// logger.severe(e);
	// }
	// logger.severe(
	// "batch should get created after one
	// mintureeeeeee@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	// }
	//
	// }
	//
	// } catch (RuntimeException e) {
	// if (em.getTransaction() != null && em.getTransaction().isActive()) {
	// em.getTransaction().rollback();
	// }
	// throw e;
	// } finally {
	// LocalSchemaEntityManager.getInstance().closeEntityManager(em);
	// }
	// return null;
	//
	// }
//	@POST
//	@Path("/checkBatchClose/")
//	public String checkBatchClose(BatchDetailPacket packet)
//			throws Exception, InvalidSessionException, IOException, NirvanaXPException {
//		EntityManager em = null;
//		try {
//
//			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
//
//			LocationSetting locationSetting = getAllLocationSettingByLocationId(em, packet.getLocationId());
//			if (locationSetting != null && locationSetting.getLocationBatchTimingList() != null
//					&& !locationSetting.getLocationBatchTimingList().isEmpty()) {
//
//				TimezoneTime time = new TimezoneTime();
//				String currentTime = time.getCurrentTimeHHMM(em, packet.getLocationId()) + ":00";
//				String date[] = time.getCurrentTimeofLocation(packet.getLocationId(), em);
//				String curDate = date[0];
//				String currentDateTime = date[2];
//
//				for (LocationBatchTiming locationBatchTiming : locationSetting.getLocationBatchTimingList()) {
//					// check start time
//
//					// check batch falls
//					// check batch exist
//					String todayDateTime = curDate + " " + locationBatchTiming.getEndtime();
//					String gmtTodayTime = time.getDateAccordingToGMT(todayDateTime, packet.getLocationId(), em);
//					logger.severe(currentTime + "<-current_time and batch time -> " + gmtTodayTime);
//					boolean batchExist = isBatchExistBetweenTime(em, gmtTodayTime, packet.getLocationId());
//					boolean batchCloseTimeExist = isBatchSettingExistBetweenTime(em, "00:00:00", currentTime,
//							locationBatchTiming.getEndtime());
//					logger.severe(batchExist + "<-isBatchExistBetweenTime and isBatchSettingExistBetweenTime -> "
//							+ batchCloseTimeExist);
//					if (batchCloseTimeExist && !batchExist) {
//						try {
//							em.getTransaction().begin();
//							new OrderManagementServiceBean().closeBusiness(httpRequest, em,
//									packet.getBatchDetail().getUpdatedBy(), packet.getLocationId(),
//									httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), packet,
//									curDate);
//							em.getTransaction().commit();
//						} catch (Exception e) {
//							logger.severe(e);
//						}
//
//						logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@batch creation starts");
//						Thread.sleep(1000);
//						em.getTransaction().begin();
//						PaymentBatchManager.getInstance().createNewBatches(httpRequest, em, packet.getLocationId(),
//								packet, "21");
//						em.getTransaction().commit();
//					}
//
//				}
//			}
//
//		} catch (Exception e) {
//			logger.severe(e);
//		} finally {
//			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
//		}
//		return null;
//
//	}

	public boolean isBatchExistBetweenTime(EntityManager em, String todayDateTime, String locationId) throws Exception {
		List<BatchDetail> resultSet = null;
		try {
			Date tTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((todayDateTime));
			Date cTime = new Date(System.currentTimeMillis());
			logger.severe(cTime + "<- cTime    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@     tTime   ->" + tTime);
			if (cTime.compareTo(tTime) > 0) {
				String queryString = "select l from BatchDetail l where l.startTime between '" + todayDateTime
						+ "' and now() and l.locationId=? and l.status ='A' and l.closeTime = null ";
				TypedQuery<BatchDetail> query = em.createQuery(queryString, BatchDetail.class).setParameter(1,
						locationId);
				resultSet = query.getResultList();
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.severe(e);
		}
		if (resultSet != null && resultSet.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isBatchSettingExistBetweenTime(EntityManager em, String todayTime, String currentTime,
			String batchCloseTime) throws Exception {
		Date tTime = new SimpleDateFormat("HH:mm:ss").parse((todayTime));
		Date cTime = new SimpleDateFormat("HH:mm:ss").parse((currentTime));
		Date bCloseTime = new SimpleDateFormat("HH:mm:ss").parse((batchCloseTime));
		return isDateInBetweenIncludingEndPoints(tTime, cTime, bCloseTime);
	}

	public boolean isDateInBetweenIncludingEndPoints(Date min, Date max, Date date) {
		return !(date.before(min) || date.after(max));
	}

	public LocationSetting getAllLocationSettingByLocationId(EntityManager em, String locationId) throws Exception {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationSetting> criteria = builder.createQuery(LocationSetting.class);
			Root<LocationSetting> r = criteria.from(LocationSetting.class);
			TypedQuery<LocationSetting> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(LocationSetting_.locationId), locationId),
							builder.notEqual(r.get(LocationSetting_.status), "D")));

			LocationSetting l = query.getSingleResult();

			l.setLocationBatchTimingList(getLocationBatchTiming(em, l.getId()));
			;

			return l;
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	private List<LocationBatchTiming> getLocationBatchTiming(EntityManager em, String locationSettingId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationBatchTiming> criteria = builder.createQuery(LocationBatchTiming.class);
			Root<LocationBatchTiming> r = criteria.from(LocationBatchTiming.class);
			TypedQuery<LocationBatchTiming> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(LocationBatchTiming_.locationSettingId), locationSettingId),
					builder.notEqual(r.get(LocationSetting_.status), "D")));
			return query.getResultList();
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	@GET
	@Path("/ewardsBillSettlementById/{id}/{accountId}/{locationId}/")
	public String ewardsBillSettlementById(@PathParam("id") String id, @PathParam("accountId") int accountId,
			@PathParam("locationId") String locationId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		EntityManager globalEM = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderById(em, id);
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			POSNPartners posnPartners = new OrderManagementServiceBean()
					.getPOSNPartnersByBusinessIdForEward(httpRequest, globalEM, location.getBusinessId());

			CustomerPacket customerPacket = new CustomerPacket();
			if (orderHeader != null && orderHeader.getUsersId() != null) {
				User user = getUserById(orderHeader.getUsersId(), em);
				Transaction transaction = new Transaction();
				Customer customer = new Customer();
				List<Tax> taxes = new ArrayList<Tax>();
				List<Charge> charges = new ArrayList<Charge>();
				Tax tax1 = new Tax();
				Tax tax2 = new Tax();
				Tax tax3 = new Tax();
				Tax tax4 = new Tax();
				Charge charge = new Charge();
				Redemption redemption = new Redemption();
				if (user != null) {

					if (user.getEmail() != null) {
						customer.setEmail(user.getEmail());
					} else {
						customer.setEmail("");
					}
					if (user.getPhone() != null) {
						String[] mobile = user.getPhone().split("-");

						customer.setMobile(mobile[1]);
					} else {
						customer.setMobile("");
					}
					customer.setName(user.getFirstName() + " " + user.getLastName());
					customer.setAddress("");
					customer.setCity("");
					customer.setState("");
					customer.setDob("");
				}
				transaction.setId(orderHeader.getOrderNumber());
				transaction.setNumber(orderHeader.getOrderNumber());
				transaction.setGross_amount("" + orderHeader.getSubTotal());
				transaction.setDiscount(
						"" + (orderHeader.getPriceDiscount().add(orderHeader.getPriceDiscountItemLevel())));
				transaction.setAmount("" + orderHeader.getSubTotal());
				/*
				 * BigDecimal netAmount=orderHeader.getSubTotal();
				 * 
				 * if(orderHeader.getDiscountsValue().compareTo(BigDecimal.ZERO)
				 * >0){
				 * netAmount=orderHeader.getSubTotal().subtract(orderHeader.
				 * getDiscountsValue()); }
				 */
				// transaction.setNet_amount(""+netAmount);
				transaction.setOrder_time(orderHeader.getScheduleDateTime());
				transaction.setType("DINE-IN");
				transaction.setPayment_type("");
				transaction.setOnline_bill_source("");
				tax1.setName(orderHeader.getTaxDisplayName1());
				tax1.setAmount("" + orderHeader.getPriceTax1());
				taxes.add(tax1);
				tax2.setName(orderHeader.getTaxDisplayName2());
				tax2.setAmount("" + orderHeader.getPriceTax2());
				taxes.add(tax2);
				tax3.setName(orderHeader.getTaxDisplayName3());
				tax3.setAmount("" + orderHeader.getPriceTax3());
				taxes.add(tax3);
				tax4.setName(orderHeader.getTaxDisplayName4());
				tax4.setAmount("" + orderHeader.getPriceTax4());
				taxes.add(tax4);
				charge.setAmount("" + orderHeader.getPriceGratuity());
				charge.setName("Gratuity");
				charges.add(charge);
				if (orderHeader.getOrderDetailItems() != null) {
					List<Item> items = new ArrayList<Item>();
					for (OrderDetailItem orderDetailItem : orderHeader.getOrderDetailItems()) {
						Item item = new Item();
						String categoryName = getCategoryItem(orderDetailItem.getItemsId(), em);
						item.setId(orderDetailItem.getItemsId());
						item.setName(orderDetailItem.getItemsShortName());
						item.setQuantity("" + orderDetailItem.getItemsQty());
						item.setRate("" + orderDetailItem.getPriceSelling());
						item.setSubtotal("" + orderDetailItem.getSubTotal());
						item.setCategory(categoryName);
						items.add(item);
						transaction.setItems(items);
					}
				}
				Discount discount = getDiscount(orderHeader.getDiscountsId(), em);
				if (discount != null
						&& (discount.getName().equals("MyEWards") || discount.getName().equals("MyEWards AmountOff"))) {
					redemption.setRedeemed_amount("" + orderHeader.getPriceDiscount());
					transaction.setNet_amount("" + orderHeader.getSubTotal());
				} else {
					redemption.setRedeemed_amount("0");
					BigDecimal netAmount = orderHeader.getSubTotal();

					if (orderHeader.getPriceDiscount().compareTo(BigDecimal.ZERO) > 0) {
						netAmount = netAmount.subtract(orderHeader.getPriceDiscount());
					}
					if (orderHeader.getPriceDiscountItemLevel().compareTo(BigDecimal.ZERO) > 0) {
						netAmount = netAmount.subtract(orderHeader.getPriceDiscountItemLevel());
					}
					transaction.setNet_amount("" + netAmount);
				}
				redemption.setReward_id("");
				transaction.setCharges(charges);
				transaction.setTaxes(taxes);
				transaction.setRedemption(redemption);
				customerPacket.setCustomer(customer);
				customerPacket.setTransaction(transaction);
				customerPacket.setMerchant_id(posnPartners.getMerchantId());
				customerPacket.setCustomer_key(posnPartners.getTokenId());
			}
			HTTPClient client = new HTTPClient();
			String liveResponse = null;

			boolean connectionAvailable = false;
			// checkNetworkConnection
			// check connection code
			try {
				liveResponse = client.sendGet("https://mint.live.nirvanaxp.com" + "/OrderManagementServiceV6/isAlive",
						null);
			} catch (Exception e1) {
				logger.severe(e1);
			}
			if (liveResponse != null) {
				connectionAvailable = true;
			}
			if (connectionAvailable) {
				//// [[StaticVariables sharedSingleton]
				//// setMYEWARDS_SERVERURL:@"https://www.myewards.com/api/v1/merchant"];
				// [[StaticVariables sharedSingleton]
				//// setMYEWARDS_MERCHANTID:@"55836"];
				// [[StaticVariables sharedSingleton]
				//// setMYEWARDS_CUSTOMERKEY:@"55836cafepeter187cabaret"];
				String refundURL = posnPartners.getUrl() + "/posAddPoint";
				String response = null;
				try {
					response = client.sendPostJSONObject(
							new JSONUtility(httpRequest).convertToJsonString(customerPacket), refundURL);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.severe(e);
				}
				return response;
			} else {
				// need to take value from client :- orderPacket.getMerchantId()
				// right now its hardcoded
				logger.severe("we arrrreeeeee hereeee :=new StoreForwardUtility().callSynchPacketsWithServer");
				new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, locationId, accountId);

				return null;
			}

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	public User getUserById(String id, EntityManager em) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteria = builder.createQuery(User.class);
			Root<User> r = criteria.from(User.class);
			TypedQuery<User> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(User_.id), id)));
			User result = (User) query.getSingleResult();
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getCategoryItem(String itemId, EntityManager em) {
		try {
			if (itemId != null && em != null) {

				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<CategoryItem> criteria = builder.createQuery(CategoryItem.class);
				Root<CategoryItem> r = criteria.from(CategoryItem.class);
				TypedQuery<CategoryItem> query = em
						.createQuery(criteria.select(r).where(builder.equal(r.get(CategoryItem_.itemsId), itemId),
								builder.notEqual(r.get(CategoryItem_.status), "D")));
				CategoryItem categoryItem = query.getSingleResult();

				CriteriaQuery<Category> category = builder.createQuery(Category.class);
				Root<Category> rcategory = category.from(Category.class);
				TypedQuery<Category> query1 = em.createQuery(category.select(rcategory).where(
						builder.equal(rcategory.get(Category_.id), categoryItem.getCategoryId()),
						builder.notEqual(rcategory.get(Category_.status), "D")));
				Category category2 = query1.getSingleResult();

				return category2.getName();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@GET
	@Path("/ewardsBillCancellationById/{id}/{mobile}/{accountId}/{locationId}/")
	public String ewardsBillCancellationById(@PathParam("id") String id, @PathParam("mobile") String mob,
			@PathParam("locationId") String locationId, @PathParam("accountId") int accountId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		EntityManager globalEM = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			POSNPartners posnPartners = new OrderManagementServiceBean()
					.getPOSNPartnersByBusinessIdForEward(httpRequest, globalEM, location.getBusinessId());
			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderById(em, id);
			EwardsBillCancellationPacket billCancellationPacket = new EwardsBillCancellationPacket();
			billCancellationPacket.setBill_number(orderHeader.getOrderNumber());
			String[] mobile = mob.split("-");

			billCancellationPacket.setMobile(mobile[1]);
			billCancellationPacket.setMerchant_id(posnPartners.getMerchantId());
			billCancellationPacket.setAccount_email(posnPartners.getEmailId());

			// billCancellationPacket.setAccount_email("cpd@pimplesaudagar");
			HTTPClient client = new HTTPClient();
			String liveResponse = null;

			boolean connectionAvailable = false;
			try {
				liveResponse = client.sendGet("https://mint.live.nirvanaxp.com" + "/OrderManagementServiceV6/isAlive",
						null);
			} catch (Exception e1) {
				logger.severe(e1);
			}
			if (liveResponse != null) {
				connectionAvailable = true;
			}
			if (connectionAvailable) {

				String refundURL = posnPartners.getUrl() + "/billing/cancellation";
				String response = null;
				try {
					response = client.sendPostJSONObjectToEwards(
							new JSONUtility(httpRequest).convertToJsonString(billCancellationPacket), refundURL);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.severe(e);
				}
				return response;
			} else {

				// need to take value from client :- orderPacket.getMerchantId()
				// right now its hardcoded
				new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, locationId, accountId);

				return null;

			}
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
	}

	public Discount getDiscount(String id, EntityManager em) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			TypedQuery<Discount> query = em
					.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.id), id)));
			return query.getSingleResult();

		} catch (Exception e) {
			logger.severe(e);
		}
		return null;

	}

	@GET
	@Path("/getAllOpenOrdersWithUserForLocationForLazyLoading/{locationId}/{startIndex}/{endIndex}")
	public String getAllOpenOrdersWithUserForLocationForLazyLoading(@PathParam("locationId") String locationId,
			@PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getAllOpenOrdersWithUserForLocationForLazyLoading(?,?,?) ")
					.setParameter(1, locationId).setParameter(2, startIndex).setParameter(3, endIndex).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllClosedOrdersWithUserByDateAndLocationForLazyLoading/{date}/{locationId}/{startIndex}/{endIndex}")
	public String getAllClosedOrdersWithUserByDateAndLocationForLazyLoading(@PathParam("date") String date,
			@PathParam("locationId") String locationId, @PathParam("startIndex") int startIndex,
			@PathParam("endIndex") int endIndex) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			TimezoneTime timezoneTime = new TimezoneTime();
			String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
			String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call getClosedOrdersWithUserByDateAndLocationForLazyLoading( ?,?,?,?,?)")
					.setParameter(1, locationId).setParameter(2, startDate).setParameter(3, endDate)
					.setParameter(4, startIndex).setParameter(5, endIndex).getResultList();
			OrderManagementServiceBean bean = new OrderManagementServiceBean();
			orderWithUsersList = bean.getListOrderWithUser(em, resultList, rowShippingCount, rowBillingCount, rowCount,
					rowShippingIdCount, rowBillingIdCount);
			return new JSONUtility(httpRequest)
					.convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

}
