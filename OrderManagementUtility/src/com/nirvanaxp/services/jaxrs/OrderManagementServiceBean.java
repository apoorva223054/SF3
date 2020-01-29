/**
o * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;

import com.google.zxing.WriterException;
import com.nirvana.services.tipping.NiravanaXpTippingBean;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.manageslots.ManageSlotsUtils;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.NameConstant;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.DriverOrder;
import com.nirvanaxp.global.types.entities.DriverOrder_;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.global.types.entities.partners.POSNPartners_;
import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.DataCapResponse;
import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.ProcessCreditCardRequestHelper;
import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.ProcessCreditCardService;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterUtility;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.data.OrderHeaderWithInventoryPostPacket;
import com.nirvanaxp.services.data.OrderHeaderWithUser;
import com.nirvanaxp.services.email.ReceiptPDFFormat;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.BatchDetailPacket;
import com.nirvanaxp.services.jaxrs.packets.InventoryPostPacket;
import com.nirvanaxp.services.jaxrs.packets.ItemPacket;
import com.nirvanaxp.services.jaxrs.packets.KDSToOrderDetailItemStatusPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacketForOrderTransfer;
import com.nirvanaxp.services.jaxrs.packets.OrderPaymentWithBatchDetailPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderTransferPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderWithBatchDetailPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.TipSavedPacket;
import com.nirvanaxp.services.util.ItemsServiceBean;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.services.util.sms.SMSTemplateKeys;
import com.nirvanaxp.services.util.sms.SendSMS;
import com.nirvanaxp.storeForward.PaymentBatchManager;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.OrderLocking;
import com.nirvanaxp.types.entities.OrderLocking_;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.Item_;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute_;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter_;
import com.nirvanaxp.types.entities.device.DeviceToPinPad;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.DiscountWays;
import com.nirvanaxp.types.entities.employee.CashRegisterRunningBalance;
import com.nirvanaxp.types.entities.inventory.Inventory;
import com.nirvanaxp.types.entities.inventory.RequestOrder;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.LocationsToShiftPreAssignServer;
import com.nirvanaxp.types.entities.locations.LocationsToShiftPreAssignServerHistory;
import com.nirvanaxp.types.entities.orders.BatchDetail;
import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus;
import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus_;
import com.nirvanaxp.types.entities.orders.OperationalShiftSchedule;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailItem_;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus_;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderHeaderToSalesTax;
import com.nirvanaxp.types.entities.orders.OrderHeaderToSalesTax_;
import com.nirvanaxp.types.entities.orders.OrderHeaderToSeatDetail;
import com.nirvanaxp.types.entities.orders.OrderHeaderToSeatDetail_;
import com.nirvanaxp.types.entities.orders.OrderHeader_;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetailHistory;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail_;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetailsToSalesTax;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetailsToSalesTax_;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatusHistory;
import com.nirvanaxp.types.entities.orders.OrderStatusHistory_;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.orders.Publisher;
import com.nirvanaxp.types.entities.orders.ShiftSchedule;
import com.nirvanaxp.types.entities.orders.ShiftSlotActiveClientInfo;
import com.nirvanaxp.types.entities.orders.ShiftSlots;
import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.payment.TransactionStatus_;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.printers.Printer_;
import com.nirvanaxp.types.entities.printers.PrintersModel;
import com.nirvanaxp.types.entities.reasons.ReasonType;
import com.nirvanaxp.types.entities.reasons.Reasons;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus_;
import com.nirvanaxp.types.entities.reservation.ReservationsType;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.tip.TipClass;
import com.nirvanaxp.types.entities.tip.TipDistribution;
import com.nirvanaxp.types.entities.tip.TipPool;
import com.nirvanaxp.types.entities.tip.TipPoolRules;
import com.nirvanaxp.types.entities.tip.TipPoolingByOrder;
import com.nirvanaxp.types.entities.tip.TipPoolingByPool;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.User_;
import com.nirvanaxp.types.entities.user.UsersToDiscount;
import com.nirvanaxp.types.entities.user.UsersToDiscount_;
import com.nirvanaxp.types.entities.user.UsersToPayment;
import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderManagementServiceBean.
 */
public class OrderManagementServiceBean {

	/** The row count. */
	private final int rowCount = 65;

	/** The Constant VOID_ORDER. */
	public static final String VOID_ORDER = "Void Order ";

	/** The Constant ORDER_CANCEL. */
	public static final String ORDER_CANCEL = "Cancel Order ";

	/** The Constant DATACAP. */
	public static final String DATACAP = "Data Cap";

	/** The Terminal ID. */
	private String TerminalID = null;

	/** The Tran code. */
	private String TranCode = null;

	/** The row shipping count. */
	int rowShippingCount = 92;

	/** The row billing count. */
	int rowBillingCount = 103;

	/** The row shipping id count. */
	int rowShippingIdCount = 8;

	/** The row billing id count. */
	int rowBillingIdCount = 9;

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(OrderManagementServiceBean.class.getName());

	/** The objects with column str. */
	private String objectsWithColumnStr = new StringBuilder()
			.append(" distinct  opd.id as  opd_id,opd.order_header_id as opd_order_header_id,opd.payment_method_id as opd_payment_method_id,opd.payment_transaction_type_id as opd_payment_transaction_type_id,opd.seat_id as opd_seat_id,opd.pn_ref as opd_pn_ref, opd.host_ref as opd_host_ref,")
			.append("opd.date as opd_date,opd.time as opd_time,opd.register as opd_register,opd.amount_paid as opd_amount_paid,opd.balance_due as opd_balance_due,opd.total_amount as opd_total_amount,opd.card_number opd_card_number,opd.expiry_month as opd_expiry_month,opd.expiry_year as opd_expiry_year,opd.security_code as opd_security_code,")
			.append("opd.auth_amount as opd_auth_amount,opd.settled_amount as opd_settled_amount,opd.tip_amount as opd_tip_amount,opd.auth_code as opd_auth_code,opd.pos_entry as opd_pos_entry,opd.batch_number as opd_batch_number,opd.avs_response as opd_avs_response,opd.cv_result as opd_cv_result,")
			.append("opd.cv_message as opd_cv_message,opd.result as opd_result,opd.message as opd_message,opd.comments as opd_comments,opd.is_refunded as opd_is_refunded,opd.created as opd_created,opd.created_by as opd_created_by,")
			.append("opd.updated as opd_updated,opd.updated_by as opd_updated_by,opd.signature_url as opd_signature_url,opd.transaction_status_id as opd_transaction_status_id,opd.cash_tip_amt as opd_cash_tip_amt,opd.creditcard_tip_amt as opd_creditcard_tip_amt,opd.change_due as opd_change_due,")
			.append("opd.card_type as opd_card_type,opd.acq_ref_data as opd_acq_ref_data,opd.order_source_group_to_paymentgatewaytype_id as opd_order_source_group_to_paymentgatewaytype_id,opd.order_source_to_paymentgatewaytype_id as opd_order_source_to_paymentgatewaytype_id,opd.discounts_name as opd_discounts_name,opd.discount_id as opd_discount_id,opd.discounts_value as opd_discounts_value,opd.calculated_discount_value as opd_calculated_discount_value,opd.price_discount as opd_price_discount,")
			.append("pm.id as pm_id,pm.payment_method_type_id as pm_payment_method_type_id,pm.name as pm_name,pm.display_name as pm_display_name, pm.description as pm_description,pm.locations_id as pm_locations_id,pm.status as pm_status,pm.is_active as pm_is_active,pm.display_sequence as pm_display_sequence, pm.created as pm_created,")
			.append("pm.created_by as pm_created_by,pm.updated as pm_updated,pm.updated_by as pm_updated_by,")
			.append("ptt.id as ptt_id,ptt.name as ptt_name,ptt.display_name as ptt_display_name,ptt.display_sequence as ptt_display_sequence,ptt.status as ptt_status,ptt.locations_id as ptt_locations_id,ptt.created as ptt_created,ptt.created_by as ptt_created_by,ptt.updated as ptt_updated,ptt.updated_by as ptt_updated_by,")
			.append("ts.id as ts_id,ts.name as ts_name,ts.display_name as ts_display_name,ts.display_sequence as ts_display_sequence,ts.paymentgateway_type_id as ts_paymentgateway_type_id , ts.created as ts_created,ts.created_by as ts_created_by,ts.updated as ts_updated,ts.updated_by ts_updated_by,ts.status as ts_status,")
			.append("pgt.id as pgt_id,pgt.name as pgt_name, pgt.display_name as pgt_display_name,pgt.created as pgt_created, pgt.created_by as pgt_created_by, pgt.updated as pgt_updated, pgt.updated_by as pgt_updated_by, pgt.status as pgt_status, pgt.location_id as pgt_location_id, ")
			.append("pgt.display_sequence as pgt_display_sequence,opd.payementgateway_id as opd_payementgateway_id,opd.host_ref_str as opd_host_ref_str,opd.invoice_number as opd_invoice_number,opd.nirvanaxp_batch_number as opd_nirvanaxp_batch_number , ")
			.append(" opd.price_tax_1 as opd_price_tax_1, opd.price_tax_2 as opd_price_tax_2, opd.price_tax_3 opd_price_tax_3, opd.price_tax_4 opd_price_tax_4, opd.tax_name_1 opd_tax_name_1, opd.tax_name_2 opd_tax_name_2, opd.tax_name_3 opd_tax_name_3,opd.tax_name_4 opd_tax_name_4,opd.tax_display_name_1 opd_tax_display_name_1, "
					+ " opd.tax_display_name_2 opd_tax_display_name_2, opd.tax_display_name_3 opd_tax_display_name_3, opd.tax_display_name_4 opd_tax_display_name_4,opd.tax_rate_1 opd_tax_rate_1 , opd.tax_rate_2 opd_tax_rate_2, opd.tax_rate_3 opd_tax_rate_3, opd.tax_rate_4 opd_tax_rate_4, opd.price_gratuity as opd_price_gratuity,opd.gratuity opd_gratuity,"
					+ " opd.process_data opd_process_data, opd.sequence_no opd_sequence_no, opd.credit_term_tip opd_credit_term_tip, opd.device_to_pinpad_id opd_device_to_pinpad_id, "
					+ "opd.discount_code, opd.customer_first_name as opd_customer_first_name, opd.customer_last_name opd_customer_last_name,"
					+ " opd.account_number as opd_account_number, opd.cheque_number as opd_cheque_number,opd.bank_name as opd_bank_name,opd.opd_id as opd_opd_id,opd.cheque_tip as opd_cheque_tip, ")

			.append("oh.id as oh_id,oh.ip_address as oh_ip_address,oh.reservations_id as oh_reservations_id,oh.users_id as oh_users_id,oh.order_status_id as oh_order_status_id,oh.order_source_id as oh_order_source_id,")
			.append("oh.locations_id as oh_locations_id,oh.address_shipping_id as oh_address_shipping_id,oh.address_billing_id as oh_address_billing_id,oh.point_of_service_count as oh_point_of_service_count,oh.price_extended as oh_price_extended,oh.price_gratuity as oh_price_gratuity,")
			.append("oh.price_tax_1 as oh_price_tax_1,oh.price_tax_2 as oh_price_tax_2,oh.price_tax_3 as oh_price_tax_3,oh.price_tax_4 as oh_price_tax_4,oh.discounts_name as oh_discounts_name,oh.discounts_type_id as oh_discounts_type_id,oh.discounts_type_name as oh_discounts_type_name,oh.discounts_value as oh_discounts_value,")
			.append("oh.discounts_id as oh_discounts_id,oh.payment_ways_id as oh_payment_ways_id,oh.split_count as oh_split_count,oh.price_discount as oh_price_discount,oh.service_tax as oh_service_tax,oh.sub_total as oh_sub_total,")
			.append("oh.gratuity as oh_gratuity,oh.total as oh_total,oh.amount_paid as oh_amount_paid,oh.balance_due as oh_balance_due,oh.created as oh_created,oh.created_by as oh_created_by,oh.updated as oh_updated,")
			.append("oh.updated_by as oh_updated_by,oh.date as oh_date,oh.verification_code as oh_verification_code,oh.qrcode as oh_qrcode,oh.first_name as oh_first_name,oh.last_name as oh_last_name,oh.server_id as oh_server_id,oh.cashier_id as oh_cashier_id, oh.void_reason_id as oh_void_reason_id,oh.open_time as oh_open_time,")
			.append("oh.close_time as oh_close_time,oh.tax_name_1 as oh_tax_name_1,oh.tax_name_2 as oh_tax_name_2,oh.tax_name_3 as oh_tax_name_3,oh.tax_name_4 as oh_tax_name_4,oh.tax_display_name_1 as oh_tax_display_name_1,")
			.append("oh.tax_display_name_2 as oh_tax_display_name_2, oh.tax_display_name_3 as oh_tax_display_name_3, oh.tax_display_name_4 as oh_tax_display_name_4,oh.tax_rate_1 as oh_tax_rate_1, oh.tax_rate_2 as oh_tax_rate_2,oh.tax_rate_3 as oh_tax_rate_3,")
			.append("oh.tax_rate_4 as oh_tax_rate_4,oh.total_tax as oh_total_tax,oh.is_gratuity_applied as oh_is_gratuity_applied, oh.round_off_total as oh_round_off_total, oh.session_id as oh_session_id , ")
			.append("oh.tax_exempt_id as oh_tax_exempt_id,oh.schedule_date_time as oh_schedule_date_time ,oh.server_name as oh_server_name,oh.cashier_name as oh_cashier_name,l.name as l_name,oh.nirvanaxp_batch_number as oh_nirvanaxp_batch_number,oh.order_number as oh_order_number,oh.price_discount_item_level as oh_price_discount_item_level,oh.merge_order_id as oh_merge_order_id, "
					+ "oh.delivery_charges as oh_delivery_charges,oh.delivery_option_id as oh_delivery_option_id,oh.delivery_tax as oh_delivery_tax, osg.id as oh_order_source_group_id, l.locations_id as section_id, oh.preassigned_server_id as oh_preassigned_server_id  ,oh.service_charges as service_charges ,oh.local_time as local_time , ")
			.append("asi.id as asi_id,asi.address1 as asi_address1, asi.address2 as asi_address2, asi.city as asi_city,")
			.append("asi.state as asi_state, asi.country_id as asi_country_id,")
			.append("asi.created as asi_created, asi.created_by as asi_created_by, asi.updated as asi_updated, ")
			.append("asi.updated_by as asi_updated_by,asi.is_default_address as asi_is_default_address, ")
			.append("asi.state_id as asi_state_id, asi.city_id as asi_city_id, ")
			.append("ab.id as ab_id,ab.address1 as ab_address1, ab.address2 as ab_address2, ab.city as ab_city,")
			.append("ab.state as ab_state, ab.country_id as ab_country_id, ")
			.append("ab.created as ab_created, ab.created_by as ab_created_by, ab.updated as ab_updated, ")
			.append("ab.updated_by as ab_updated_by,ab.is_default_address as ab_is_default_address, ")
			.append("ab.state_id as ab_state_id, ab.city_id as ab_city_id").toString();

	/**
	 * Adds the.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param isTakeOutOrder
	 *            the is take out order
	 * @param globalUserId
	 *            the global user id
	 * @param parentlocationId
	 *            the parent location id
	 * @param merchantId
	 *            the merchant id
	 * @param sessionKey
	 *            the session key
	 * @param schemaName
	 *            the schema name
	 * @param packet
	 *            the packet
	 * @return the order header with user
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws WriterException
	 *             the writer exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws Exception
	 *             the exception
	 */
	OrderHeaderWithUser add(HttpServletRequest httpRequest, String sessionId, EntityManager em, OrderHeader order,
			boolean isTakeOutOrder, String globalUserId, String parentlocationId, int merchantId, int sessionKey,
			String schemaName, OrderPacket packet)
			throws NirvanaXPException, IOException, WriterException, InvalidSessionException, Exception {

		boolean shouldAllowAddingOrder = true;

		OrderHeader header = null;
		User newUser = null;
		OrderHeader order_his = null;
		boolean tabOrderPush = false;

		PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
		String batchId = batchManager.getCurrentBatchIdBySession(httpRequest, em, parentlocationId, true, packet,
				order.getUpdatedBy());
		ShiftSlotActiveClientInfo shiftSlotActiveClientInfo = null;

		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em, OrderSource.class,
				order.getOrderSourceId());
		OrderSourceGroup sourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,
				OrderSourceGroup.class, orderSource.getOrderSourceGroupId());

		if ((sourceGroup.getName().equals("Pick Up") || sourceGroup.getName().equals("Delivery"))
				&& packet.getIdOfOrderHoldingClientObj() != 0) {
			shiftSlotActiveClientInfo = checkActiveClientForShiftSlot(httpRequest, em, order,
					packet.getIdOfOrderHoldingClientObj());
		}

		if (order.getReservationsId() != null && order.getReservationsId() != null) {
			// get order by reservation Id
			try {
				TypedQuery<OrderHeader> query = em.createQuery(
						"select o from OrderHeader o, OrderStatus os where o.orderStatusId=os.id and o.reservationsId=? and os.name not in ('Ready to Order','Void Order ','Cancel Order')",
						OrderHeader.class).setParameter(1, order.getReservationsId());
				header = query.getSingleResult();
			} catch (NoResultException e) {
				logger.fine(httpRequest, "Could not find order with reservation Id: " + order.getReservationsId());
			}

			if (header != null) {
				header.setPreassignedServerId(order.getPreassignedServerId());

			}

		}

		if (isTakeOutOrder && header != null) {

			return new OrderHeaderWithUser(header, newUser, tabOrderPush);
		} else {

			// checking conditon

			if ((header != null && order.getIsTabOrder() == 0 && checkOrderExist(em, header.getId()) && !isTakeOutOrder)
					|| ((header != null && order.getIsTabOrder() == 1 && checkOrderExist(em, header.getId())
							&& isTakeOutOrder))) {
				if (isTakeOutOrder == false) {
					shouldAllowAddingOrder = shouldAllowAddingOrder(em, order.getLocationsId());
					// check if location in which we are trying to add an
					// order
					// is
					// already merged or not
					if (shouldAllowAddingOrder) {
						Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
								order.getLocationsId());
						if (location != null && location.getIsCurrentlyMerged() == 1) {
							throw new NirvanaXPException(new NirvanaServiceErrorResponse(
									MessageConstants.ERROR_CODE_ORDER_LOCATION_ALREADY_MERGE_WITH_OTHER_LOCATION_EXCEPTION,
									MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_ALREADY_MERGE_WITH_OTHER_LOCATION_DISPLAY_MESSAGE,
									null));
						}
					}

				}
				if (shouldAllowAddingOrder || isTakeOutOrder) {
					// Checking isTab order
					if (!isTakeOutOrder) {
						header.setIsTabOrder(0);// inserting 0
					}
					header.setLocationsId(order.getLocationsId());
					header.setSessionKey(sessionKey);

					// condition :- when pre order sitting current reservation

					OrderSourceGroup orderSourceGroup = null;
					OrderStatus orderStatus = null;
					orderSourceGroup = getOrderSourceGroupByNameAndlocationId(httpRequest, em, "In Store",
							parentlocationId);
					if (orderSourceGroup != null) {
						// getting status
						orderStatus = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em,
								"Order Placed", parentlocationId, orderSourceGroup.getId());
						header.setOrderStatusId(orderStatus.getId());

					} else {
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_ORDER_PLACED_STATUS_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE,
								MessageConstants.ERROR_CODE_ORDER_PLACED_STATUS_NOT_PRESENT_IN_DATABASE, null));
					}
					em.merge(header);

					order = header;
					tabOrderPush = true;

					order_his = updateQRCodeAndHistory(httpRequest, em, order, merchantId, parentlocationId, schemaName)
							.getOrderHeader();
					if ((sourceGroup.getName().equals("Pick Up") || sourceGroup.getName().equals("Delivery"))
							&& packet.getIdOfOrderHoldingClientObj() != 0) {
						updateShiftSlotCurrentActiveShiftCount(httpRequest, em, shiftSlotActiveClientInfo, order, true,
								true);

					}
				} else {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_ORDER_LOCATION_ALREADY_HAS_ORDER_EXCEPTION,
							MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_ALREADY_HAS_ORDER_DISPLAY_MESSAGE, null));
				}

			} else {

				// if not take-out then check if addition of take out is
				// possible or
				// not

				if (!isTakeOutOrder) {
					shouldAllowAddingOrder = shouldAllowAddingOrder(em, order.getLocationsId());
					// check if location in which we are trying to add an
					// order
					// is
					// already merged or not
					if (shouldAllowAddingOrder) {
						Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
								order.getLocationsId());
						if (location != null && location.getIsCurrentlyMerged() == 1) {
							throw new NirvanaXPException(new NirvanaServiceErrorResponse(
									MessageConstants.ERROR_CODE_ORDER_LOCATION_ALREADY_MERGE_WITH_OTHER_LOCATION_EXCEPTION,
									MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_ALREADY_MERGE_WITH_OTHER_LOCATION_DISPLAY_MESSAGE,
									null));
						}
					}

				} else {
					// its a take out order, we must check if user exists in
					// local or not and get him migrated
					GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
					if ((order.getUsersId() == null || order.getUsersId() == null) && globalUserId != null) {
						EntityManager globalEM = null;
						try {
							globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

							User localUser = globalUsermanagement.addGlobalUserToLocalDatabaseIfNotExixts(globalEM, em,
									globalUserId, parentlocationId, null, httpRequest);
							if (localUser != null) {
								order.setUsersId(localUser.getId());
								newUser = localUser;
							}
						} catch (Exception e) {
							throw new NirvanaXPException(new NirvanaServiceErrorResponse(
									MessageConstants.ERROR_CODE_GLOBAL_USER_NOT_ADD_EXCEPTION,
									MessageConstants.ERROR_MESSAGE_GLOBAL_USER_NOT_ADD_DISPLAY_MESSAGE, null));
						} finally {
							GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
						}
					}
				}

				if (shouldAllowAddingOrder) {
					// added to insert current time of the location in the
					// order
					// header
					TimezoneTime timezoneTime = new TimezoneTime();
					String locationId = order.getLocationsId();

					order.setSessionKey(sessionKey);

					if (order.getAddressBilling() != null && (order.getAddressBilling().getId() == null
							|| order.getAddressBilling().getId().equals(BigInteger.ZERO))) {
						order.getAddressBilling().setId(new StoreForwardUtility().generateDynamicBigIntId(em,
								locationId, httpRequest, "address"));
					}

					if (order.getAddressShipping() != null && (order.getAddressShipping().getId() == null
							|| order.getAddressShipping().getId().equals(BigInteger.ZERO))) {
						order.getAddressShipping().setId(new StoreForwardUtility().generateDynamicBigIntId(em,
								locationId, httpRequest, "address"));
					}
					Location l = null;
					EntityManager globalEM = null;
					try {
						globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
						l = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
								parentlocationId);
						OrderManagementServiceBean bean = new OrderManagementServiceBean();

						String refNo = bean.getBusinessWithRefrenceNumber(globalEM, l.getBusinessId());
						if (refNo != null) {
							order.setReferenceNumber(refNo);
						} else {
							logger.severe(httpRequest,
									MessageConstants.ERROR_MESSAGE_NO_RESULT_FOUND_FOR_REFERENCE_NUMBER);
						}
					} catch (Exception exception) {
						logger.severe(httpRequest, exception);
					} finally {
						GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
					}

					order.setUpdated(new Date(updatedTime()));
					order.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(parentlocationId, em));
					order.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					if (order.getNirvanaXpBatchNumber() == null) {
						order.setNirvanaXpBatchNumber(batchId);
					}

					// apoorva

					if (order.getDeliveryCharges() == null) {
						order.setDeliveryCharges(new BigDecimal(0.00));
					}

					if (order.getDeliveryTax() == null) {
						order.setDeliveryTax(new BigDecimal(0.00));
					}

					if (order.getId() == null) {
						String orderNumber = new StoreForwardUtility().generateOrderNumber(em, parentlocationId,
								httpRequest, Integer.parseInt(packet.getMerchantId()), packet, order.getUpdatedBy());
						order.setId(orderNumber);
						order.setOrderNumber(new StoreForwardUtility().generateOrderNumberNew(parentlocationId, em,
								order.getNirvanaXpBatchNumber(), "order_header"));
						order.setMergeOrderId(order.getId());
					}
					// adding order dequence

					// Location baseLocation = new
					// CommonMethods().getBaseLocation(em);
					// if (baseLocation.getIsOrderNumberSequencing() == 0)
					// {
					// //order.setOrderNumber(orderNumber);
					// order.setOrderNumber(nirvanaIndex.getOrderNumber());
					// }
					// else
					// {
					// order.setOrderNumber(order.getId());
					// }

					// priya told us to make -1 for
					order.setIsSeatWiseOrder(-1);
					order = em.merge(order);
					if (em.getTransaction() != null && em.getTransaction().isActive()) {
						em.getTransaction().commit();
						em.getTransaction().begin();
					}
					// / we always send gmt time to clieny and client convert it
					// to gmt
					// String currentDateTime[] =
					// timezoneTime.getCurrentTimeofLocation(locationId, em);
					// logger.severe(currentDateTime[2]+"they are
					// setttttttttttttttting
					// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+order.getScheduleDateTime());
					////
					// if (currentDateTime != null && currentDateTime.length >
					// 0) {
					// order.setDate(currentDateTime[0]);
					// if (order.getScheduleDateTime() == null) {
					//
					// SimpleDateFormat formatter = new
					// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// Date date = new Date();
					// System.out.println(formatter.format(date));
					// order.setScheduleDateTime(formatter.format(date));
					// logger.severe(currentDateTime[2]+"we are
					// setttttttttttttttting
					// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+order.getScheduleDateTime());
					// order.setScheduleDateTime(
					// timezoneTime.getDateAccordingToGMT(order.getScheduleDateTime(),
					// locationId, em));
					// } else {
					// order.setScheduleDateTime(
					// timezoneTime.getDateAccordingToGMT(order.getScheduleDateTime(),
					// locationId, em));
					// }
					//
					// }
					String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(locationId, em);
					if (currentDateTime != null && currentDateTime.length > 0) {
						order.setDate(currentDateTime[0]);
						if (order.getScheduleDateTime() == null) {
							order.setScheduleDateTime(currentDateTime[2]);
							order.setScheduleDateTime(
									timezoneTime.getDateAccordingToGMT(order.getScheduleDateTime(), locationId, em));
						} else {
							order.setScheduleDateTime(
									timezoneTime.getDateAccordingToGMT(order.getScheduleDateTime(), locationId, em));
						}

					}
					if (order.getOpenTime() == 0) {
						order.setOpenTime(new TimezoneTime().getGMTTimeInMilis());

					}

					order = em.merge(order);

					if ((sourceGroup.getName().equals("Pick Up") || sourceGroup.getName().equals("Delivery"))
							&& packet.getIdOfOrderHoldingClientObj() != 0) {
						updateShiftSlotCurrentActiveShiftCount(httpRequest, em, shiftSlotActiveClientInfo, order, true,
								true);

					}
					order_his = updateQRCodeAndHistory(httpRequest, em, order, merchantId, parentlocationId, schemaName)
							.getOrderHeader();

				} else {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_ORDER_LOCATION_ALREADY_HAS_ORDER_EXCEPTION,
							MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_ALREADY_HAS_ORDER_DISPLAY_MESSAGE, null));
				}
			}

			return new OrderHeaderWithUser(order_his, newUser, tabOrderPush);
		}

	}

	/**
	 * Adds the nirvana XP.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param isTakeOutOrder
	 *            the is take out order
	 * @param globalUserId
	 *            the global user id
	 * @param parentlocationId
	 *            the parent location id
	 * @param merchantId
	 *            the merchant id
	 * @param sessionKey
	 *            the session key
	 * @param schemaName
	 *            the schema name
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeaderWithInventoryPostPacket addNirvanaXP(HttpServletRequest httpRequest, String sessionId,
			EntityManager em, OrderHeader order, boolean isTakeOutOrder, String globalUserId, String parentlocationId,
			int merchantId, int sessionKey, String schemaName) throws Exception {

		OrderHeader order_his = new OrderHeader();
		InventoryPostPacket ipp = null;

		String rootlocationId = order.getLocationsId();

		TimezoneTime timezoneTime = new TimezoneTime();
		String locationId = order.getLocationsId();

		// todo shlok need
		// modularise code here to make object
		OrderHeaderCalculation calculation = new OrderHeaderCalculation();

		OrderHeader orderHeader = calculation.getOrderHeaderCalculation(em, order, null);
		orderHeader.setSessionKey(1);
		OrderHeader o = new OrderHeader();
		o.setOrderStatusId(orderHeader.getOrderStatusId());
		o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		o.setUpdated(new Date(updatedTime()));
		o.setTotal(orderHeader.getTotal());
		o.setBalanceDue(orderHeader.getBalanceDue());
		o.setServiceTax(orderHeader.getServiceTax());
		o.setPriceTax4(orderHeader.getPriceTax4());
		o.setPriceTax1(orderHeader.getPriceTax1());
		o.setPriceTax2(orderHeader.getPriceTax2());
		o.setPriceTax3(orderHeader.getPriceTax3());
		o.setPriceExtended(orderHeader.getPriceExtended());
		o.setPriceGratuity(orderHeader.getPriceGratuity());
		o.setPriceDiscount(orderHeader.getPriceDiscount());
		o.setGratuity(orderHeader.getGratuity());
		o.setAmountPaid(orderHeader.getAmountPaid());
		o.setSubTotal(orderHeader.getSubTotal());
		o.setLocationsId(orderHeader.getLocationsId());
		o.setAddressShipping(orderHeader.getAddressShipping());
		o.setAddressBilling(orderHeader.getAddressBilling());
		o.setDiscountsId(orderHeader.getDiscountsId());
		o.setDiscountsName(orderHeader.getDiscountsName());
		o.setDiscountsTypeId(orderHeader.getDiscountsTypeId());
		o.setDiscountsTypeName(orderHeader.getDiscountsTypeName());
		o.setDiscountsValue(orderHeader.getDiscountsValue());
		o.setPointOfServiceCount(orderHeader.getPointOfServiceCount());
		o.setSessionKey(orderHeader.getSessionKey());
		o.setFirstName(orderHeader.getFirstName());
		o.setLastName(orderHeader.getLastName());
		o.setUpdatedBy(orderHeader.getUpdatedBy());
		if (orderHeader.getSplitCount() != null) {
			o.setSplitCount(orderHeader.getSplitCount());
		}

		if (orderHeader.getPaymentWaysId() != null) {
			o.setPaymentWaysId(orderHeader.getPaymentWaysId());
		}

		if (orderHeader.getOrderSourceId() != null) {
			o.setOrderSourceId(orderHeader.getOrderSourceId());
		}
		o.setTaxDisplayName1(orderHeader.getTaxDisplayName1());
		o.setTaxDisplayName2(orderHeader.getTaxDisplayName2());
		o.setTaxDisplayName3(orderHeader.getTaxDisplayName3());
		o.setTaxDisplayName4(orderHeader.getTaxDisplayName4());
		o.setTaxName1(orderHeader.getTaxName1());
		o.setTaxName2(orderHeader.getTaxName2());
		o.setTaxName3(orderHeader.getTaxName3());
		o.setTaxName4(orderHeader.getTaxName4());
		o.setTaxRate1(orderHeader.getTaxRate1());
		o.setTaxRate2(orderHeader.getTaxRate2());
		o.setTaxRate3(orderHeader.getTaxRate3());
		o.setTaxRate4(orderHeader.getTaxRate4());
		o.setTotalTax(orderHeader.getTotalTax());
		o.setRoundOffTotal(orderHeader.getRoundOffTotal());
		o.setIsGratuityApplied(orderHeader.getIsGratuityApplied());
		o.setDiscountDisplayName(orderHeader.getDiscountDisplayName());
		o.setUsersId(orderHeader.getUsersId());
		o.setCashierId(orderHeader.getCashierId());
		o.setIsTabOrder(orderHeader.getIsTabOrder());
		o.setTaxExemptId(orderHeader.getTaxExemptId());
		o.setCreatedBy(orderHeader.getCreatedBy());
		o.setUpdatedBy(orderHeader.getUpdatedBy());
		o.setIpAddress(orderHeader.getIpAddress());
		o.setScheduleDateTime(orderHeader.getScheduleDateTime());
		o.setPriceDiscountItemLevel(orderHeader.getPriceDiscountItemLevel());
		String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(locationId, em);
		if (currentDateTime != null && currentDateTime.length > 0) {
			o.setDate(currentDateTime[0]);
			if (o.getScheduleDateTime() == null) {
				o.setScheduleDateTime(currentDateTime[2]);
				o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationId, em));
			} else {
				o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationId, em));
			}

		}
		o.setIsSeatWiseOrder(-1);
		if (o.getId() == null) {
			String orderNumber = new StoreForwardUtility().generateOrderNumber(em, parentlocationId, httpRequest,
					merchantId, null, o.getUpdatedBy());
			order.setId(orderNumber);
			order.setOrderNumber(new StoreForwardUtility().generateOrderNumberNew(parentlocationId, em,
					orderHeader.getNirvanaXpBatchNumber(), "order_header"));
			order.setMergeOrderId(order.getId());
		}

		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				o.getOrderStatusId());
		// manage inventory
		ipp = manageInventoryForOrder(httpRequest, order, em, rootlocationId, o, false, false, orderStatus, merchantId,
				null);

		// ------------Change by uzma- for order object by id ------
		// find order
		order_his = getOrderById(em, o.getId());
		// insert into order status history
		insertIntoOrderStatusHistory(em, order_his);

		// we need to save the order detail item and order detail item
		// attribute that is sent by the client always
		order_his.setOrderDetailItems(orderHeader.getOrderDetailItems());
		order_his = updateQRCodeAndHistory(httpRequest, em, o, merchantId, rootlocationId, schemaName).getOrderHeader();

		// insert into order history
		new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order_his, em);

		return new OrderHeaderWithInventoryPostPacket(o, ipp);

	}

	/**
	 * Update.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param rootlocationId
	 *            the root location id
	 * @param isDupilatePacketCheck
	 *            the is dupilate packet check
	 * @param idOfClientHoldingTheSlot
	 *            the id of client holding the slot
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeaderWithInventoryPostPacket update(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order, String rootlocationId, boolean isDupilatePacketCheck, int idOfClientHoldingTheSlot,
			int accountId, String sessionId) throws Exception {

		// todo shlok need
		// modularise method
		logger.severe(
				"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@order.getScheduleDateTime()@@@@@@@@@@@@@@@@@"
						+ order.getScheduleDateTime());
		boolean isPartySizeUpdated = false;
		OrderHeader o = getOrderById(em, order.getId());
		if (order.getPreassignedServerId() != null) {
			o.setPreassignedServerId(order.getPreassignedServerId());
		}

		if (order.getComment() != null) {
			o.setComment(order.getComment());
		}

		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				o.getOrderStatusId());

		if (order.getPoRefrenceNumber() != null && order.getPoRefrenceNumber() != null) {

			o.setPoRefrenceNumber(order.getPoRefrenceNumber());

			if (orderStatus != null && (orderStatus.getName().equals("Cancel Order"))) {
				String queryString = "select l from RequestOrder l where l.id =" + o.getPoRefrenceNumber();
				TypedQuery<RequestOrder> query = em.createQuery(queryString, RequestOrder.class);
				RequestOrder rOrder = query.getSingleResult();

				String queryS = "select s from OrderSourceGroup s where s.name =? and s.locationsId=? and s.status !='D'";
				TypedQuery<OrderSourceGroup> querySG = em.createQuery(queryS, OrderSourceGroup.class)
						.setParameter(1, "Inventory").setParameter(2, rootlocationId);
				OrderSourceGroup orderSourceGroup = querySG.getSingleResult();

				String queryStringStatus = "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'";
				TypedQuery<OrderStatus> queryOS = em.createQuery(queryStringStatus, OrderStatus.class)
						.setParameter(1, "PO Cancelled").setParameter(2, rootlocationId)
						.setParameter(3, orderSourceGroup.getId());
				OrderStatus orderSPO = queryOS.getSingleResult();
				rOrder.setStatusId(orderSPO.getId());
				rOrder = em.merge(rOrder);
			}
		}

		InventoryPostPacket ipp = null;
		if (isDupilatePacketCheck && checkDuplicateOrderHeader(httpRequest, em, order)) {
			logger.severe(httpRequest,
					MessageConstants.ERROR_MESSAGE_ORDER_OUT_OF_SYNCH_DISPLAY_MESSAGE + "-- " + order);

			// todo shlok need
			// throw proper exception for same packet or duplicate

			return new OrderHeaderWithInventoryPostPacket(o, null);
		}

		String oldSourceId = o.getOrderSourceId();
		String newSourceId = order.getOrderSourceId();
		ShiftSlotActiveClientInfo shiftSlotActiveClientInfo = null;
		boolean needToUpdateSlots = false;
		if (!oldSourceId.equals(newSourceId) && idOfClientHoldingTheSlot != 0) {
			OrderSource orderSourceFromDb = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
					OrderSource.class, o.getOrderSourceId());
			OrderSourceGroup sourceGroupFromDb = (OrderSourceGroup) new CommonMethods().getObjectById(
					"OrderSourceGroup", em, OrderSourceGroup.class, orderSourceFromDb.getOrderSourceGroupId());
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
					OrderSource.class, order.getOrderSourceId());
			OrderSourceGroup sourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,
					OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
			if ((sourceGroupFromDb.getName().equals("Pick Up") || sourceGroupFromDb.getName().equals("Delivery"))
					&& (sourceGroup.getName().equals("Pick Up") || sourceGroup.getName().equals("Delivery")
							|| sourceGroup.getName().equals("In Store"))) {
				shiftSlotActiveClientInfo = checkActiveClientForShiftSlot(httpRequest, em, order,
						idOfClientHoldingTheSlot);
				needToUpdateSlots = true;
			}
		}
		// check if order party size is updated, then we must update
		// associated reservation party size too
		if (o.getPointOfServiceCount() != order.getPointOfServiceCount()) {
			isPartySizeUpdated = true;
		}

		if (orderStatus != null && (!orderStatus.getName().equals("Ready to Order"))) {

			String statusId = o.getOrderStatusId();
			// set the all the varaible
			o.setOrderStatusId(order.getOrderStatusId());
			o.setUpdated(new Date(updatedTime()));
			o.setTotal(order.getTotal());
			o.setBalanceDue(order.getBalanceDue());
			o.setServiceTax(order.getServiceTax());
			o.setPriceTax4(order.getPriceTax4());
			o.setPriceTax1(order.getPriceTax1());
			o.setPriceTax2(order.getPriceTax2());
			o.setPriceTax3(order.getPriceTax3());
			o.setPriceExtended(order.getPriceExtended());
			o.setPriceGratuity(order.getPriceGratuity());
			o.setPriceDiscount(order.getPriceDiscount());
			o.setGratuity(order.getGratuity());
			o.setAmountPaid(order.getAmountPaid());
			o.setSubTotal(order.getSubTotal());
			o.setLocationsId(order.getLocationsId());
			o.setAddressShipping(order.getAddressShipping());
			o.setAddressBilling(order.getAddressBilling());
			o.setDiscountsId(order.getDiscountsId());
			o.setDiscountsName(order.getDiscountsName());
			o.setDiscountsTypeId(order.getDiscountsTypeId());
			o.setDiscountsTypeName(order.getDiscountsTypeName());
			o.setDiscountsValue(order.getDiscountsValue());
			o.setPointOfServiceCount(order.getPointOfServiceCount());
			o.setSessionKey(order.getSessionKey());
			o.setFirstName(order.getFirstName());
			o.setLastName(order.getLastName());
			if (order.getDeliveryCharges() != null) {
				o.setDeliveryCharges(order.getDeliveryCharges());
			}
			/*
			 * else { o.setDeliveryCharges(new BigDecimal(0.00)); }
			 */

			if (order.getDeliveryOptionId() != null) {
				o.setDeliveryOptionId(order.getDeliveryOptionId());
			}
			/*
			 * else { o.setDeliveryOptionId(0); }
			 */

			if (order.getDeliveryTax() != null) {
				o.setDeliveryTax(order.getDeliveryTax());
			}
			/*
			 * else { o.setDeliveryTax(new BigDecimal(0.00)); }
			 */

			if (order.getServiceCharges() != null) {
				o.setServiceCharges(order.getServiceCharges());
			}
			/*
			 * else { o.setServiceCharges(new BigDecimal(0.00)); }
			 */

			o.setUpdatedBy(order.getUpdatedBy());
			if (order.getSplitCount() != null) {
				o.setSplitCount(order.getSplitCount());
			}

			if (order.getPaymentWaysId() != null) {
				o.setPaymentWaysId(order.getPaymentWaysId());
			}

			if (order.getOrderSourceId() != null) {
				o.setOrderSourceId(order.getOrderSourceId());
			}
			o.setTaxDisplayName1(order.getTaxDisplayName1());
			o.setTaxDisplayName2(order.getTaxDisplayName2());
			o.setTaxDisplayName3(order.getTaxDisplayName3());
			o.setTaxDisplayName4(order.getTaxDisplayName4());
			o.setTaxName1(order.getTaxName1());
			o.setTaxName2(order.getTaxName2());
			o.setTaxName3(order.getTaxName3());
			o.setTaxName4(order.getTaxName4());
			o.setTaxRate1(order.getTaxRate1());
			o.setTaxRate2(order.getTaxRate2());
			o.setTaxRate3(order.getTaxRate3());
			o.setTaxRate4(order.getTaxRate4());
			o.setTotalTax(order.getTotalTax());
			o.setRoundOffTotal(order.getRoundOffTotal());
			o.setIsGratuityApplied(order.getIsGratuityApplied());
			o.setDiscountDisplayName(order.getDiscountDisplayName());

			// add user info with tax (for gst)
			if (order.getUsersId() != null) {
				o.setUsersId(order.getUsersId());
				User user = (User) new CommonMethods().getObjectById("User", em, User.class, order.getUsersId());
				if (user != null) {
					if (order.getTaxDisplayName() != null) {
						user.setTaxDisplayName(order.getTaxDisplayName());
					}

					if (order.getTaxNo() != null) {
						user.setTaxNo(order.getTaxNo());
					}

					if (order.getCompanyName() != null) {
						user.setCompanyName(order.getCompanyName());
					}

					em.merge(user);
				}

			}

			o.setCashierId(order.getCashierId());
			o.setIsTabOrder(order.getIsTabOrder());
			o.setTaxExemptId(order.getTaxExemptId());
			o.setIsSeatWiseOrder(order.getIsSeatWiseOrder());
			o.setCalculatedDiscountValue(order.getCalculatedDiscountValue());
			o.setPriceDiscountItemLevel(order.getPriceDiscountItemLevel());
			// adding cashier name 28605
			o.setCashierName(order.getCashierName());
			o.setOrderHeaderToSeatDetails(order.getOrderHeaderToSeatDetails());
			// for converting quote order to normal order.
			// change by ap :- #46310: deliveryOptionID not becoming zero in
			// updateOrderForDuplicateCheck service
			o.setDeliveryOptionId(order.getDeliveryOptionId());

			if (order.getTaxDisplayName() != null) {
				o.setTaxDisplayName(order.getTaxDisplayName());
			}

			if (order.getTaxNo() != null) {
				o.setTaxNo(order.getTaxNo());
			}

			if (order.getCompanyName() != null) {
				o.setCompanyName(order.getCompanyName());
			}

			// added to insert current time of the location in the
			// order
			// header
			TimezoneTime timezoneTime = new TimezoneTime();
			String locationId = order.getLocationsId();
			String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(locationId, em);
			logger.severe(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@.getScheduleDateTime()@@@@@@@@@@@@@@@@@"
							+ order.getScheduleDateTime());
			if (currentDateTime != null && currentDateTime.length > 0) {
				o.setDate(currentDateTime[0]);
				if (order.getScheduleDateTime() == null) {
					o.setScheduleDateTime(currentDateTime[2]);
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationId, em));
				} else {
					logger.severe(
							"1@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@1111111111111111order.getScheduleDateTime()@@@@@@@@@@@@@@@@@"
									+ order.getScheduleDateTime());
					o.setScheduleDateTime(order.getScheduleDateTime());
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationId, em));
				}

			}
			logger.severe(
					"22222222222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22222222222222order.getScheduleDateTime()@@@@@@@@@@@@@@@@@"
							+ o.getScheduleDateTime());
			o.setUpdated(new Date(updatedTime()));
			// 28436 closing the order when 100 % discount applied By Apoorva
			if (o.getPriceDiscount().compareTo(o.getSubTotal()) == 0
					&& o.getSubTotal().compareTo(new BigDecimal("0")) == 1) {
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			}
			// find order
			// get order before update
			// to make time effective 0.01 sec difference
			if (order.getOrderTypeId() > 0) {
				o.setOrderTypeId(order.getOrderTypeId());
			}
			if (o.getAddressBilling() != null && o.getAddressBilling().getId() == null) {
				o.getAddressBilling().setId(
						new StoreForwardUtility().generateDynamicBigIntId(em, rootlocationId, httpRequest, "address"));
			}
			if (o.getAddressShipping() != null && o.getAddressShipping().getId() == null) {
				o.getAddressShipping().setId(
						new StoreForwardUtility().generateDynamicBigIntId(em, rootlocationId, httpRequest, "address"));
			}

			em.merge(o);
			ipp = manageInventoryForOrder(httpRequest, order, em, rootlocationId, o, false, false, orderStatus,
					accountId, sessionId);
			o = manageOrderRelationship(em, o);
			em.getTransaction().commit();
			em.getTransaction().begin();
			if (needToUpdateSlots) {
				updateShiftSlotCurrentActiveShiftCount(httpRequest, em, shiftSlotActiveClientInfo, o, true, true);
			}
			// setting order header_to_tax
			// manage inventory

			// ------------Change by uzma- for order object by id ------
			// find order
			OrderHeader order_his = getOrderById(em, order.getId());

			if (statusId != order_his.getOrderStatusId()) {
				// insert into order status history
				insertIntoOrderStatusHistory(em, order_his);

			}

			// we need to save the order detail item and order detail item
			// attribute that is sent by the client always

			List<OrderDetailItem> orderDetailItemsListOfHistory = order_his.getOrderDetailItems();
			// need latest object for device to kds
			order_his.setOrderDetailItems(order.getOrderDetailItems());

			// insert into order history
			OrderHeader header = o;
			header.setOrderDetailItems(order.getOrderDetailItems());
			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, header, em);

			if (order.getUsersId() != null) {
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, em,
						order.getReservationsId());
				if (reservation != null) {
					reservation.setSessionKey(order.getSessionKey());
					updateReservationUser(reservation, order, em);

					// also insert this into reservation history
					new InsertIntoHistory().insertReservationIntoHistory(httpRequest, reservation, em);
				}
			}
			// if party size is updated, then update the guest count of
			// reservations
			if (isPartySizeUpdated) {
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, em,
						o.getReservationsId());

				if (reservation.getPartySize() != o.getPointOfServiceCount()) {

					reservation.setSessionKey(o.getSessionKey());
					updateReservationPartySize(reservation, o.getPointOfServiceCount(), em);

					// also insert this into reservation history
					new InsertIntoHistory().insertReservationIntoHistory(httpRequest, reservation, em);
					// tell client party size is updated
					order_his.setPartySizeUpdated(true);
				}

			}

			if (orderDetailItemsListOfHistory != null && order_his.getOrderDetailItems() != null) {
				for (OrderDetailItem detailItem : orderDetailItemsListOfHistory) {

					for (OrderDetailItem newOrderDetailItem : order_his.getOrderDetailItems()) {
						detailItem.setSellableUom(newOrderDetailItem.getSellableUom());
						if (detailItem.getItemsId() == newOrderDetailItem.getItemsId()) {
							if (newOrderDetailItem.getDeviceToKDSIds() != null) {
								detailItem.setDeviceToKDSIds(newOrderDetailItem.getDeviceToKDSIds());
							}
							break;
						}
					}
				}
			}
			order_his.setOrderDetailItems(orderDetailItemsListOfHistory);
			order_his.setScheduleDateTime(o.getScheduleDateTime());
			insertIntoKDSToOrderDetailItemStatus(httpRequest, em, order_his);
			return new OrderHeaderWithInventoryPostPacket(order_his, ipp);
		} else {
			return new OrderHeaderWithInventoryPostPacket(o, ipp);
		}

	}

	/**
	 * Update item transfer.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param rootlocationId
	 *            the root location id
	 * @param isDupilatePacketCheck
	 *            the is dupilate packet check
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeaderWithInventoryPostPacket updateItemTransfer(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order, String rootlocationId, boolean isDupilatePacketCheck, int accountId) throws Exception {

		// todo shlok need
		// modularise code here
		boolean isPartySizeUpdated = false;
		OrderHeader o = getOrderById(em, order.getId());
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				o.getOrderStatusId());
		InventoryPostPacket ipp = null;
		if (isDupilatePacketCheck && checkDuplicateOrderHeader(httpRequest, em, order)) {

			// todo shlok need
			// return proper mgs to show duplicate order packet
			logger.severe(httpRequest,
					MessageConstants.ERROR_MESSAGE_ORDER_OUT_OF_SYNCH_DISPLAY_MESSAGE + "-- " + order);
			return new OrderHeaderWithInventoryPostPacket(o, ipp);
		}

		// check if order party size is updated, then we must update
		// associated reservation party size too
		if (o.getPointOfServiceCount() != order.getPointOfServiceCount()) {
			isPartySizeUpdated = true;
		}

		if (orderStatus != null && (!orderStatus.getName().equals("Ready to Order"))) {

			String statusId = o.getOrderStatusId();
			// set the all the varaible
			o.setOrderStatusId(order.getOrderStatusId());
			o.setUpdated(new Date(updatedTime()));
			o.setTotal(order.getTotal());
			o.setBalanceDue(order.getBalanceDue());
			o.setServiceTax(order.getServiceTax());
			o.setPriceTax4(order.getPriceTax4());
			o.setPriceTax1(order.getPriceTax1());
			o.setPriceTax2(order.getPriceTax2());
			o.setPriceTax3(order.getPriceTax3());
			o.setPriceExtended(order.getPriceExtended());
			o.setPriceGratuity(order.getPriceGratuity());
			o.setPriceDiscount(order.getPriceDiscount());
			o.setGratuity(order.getGratuity());
			o.setAmountPaid(order.getAmountPaid());
			o.setSubTotal(order.getSubTotal());
			o.setLocationsId(order.getLocationsId());
			o.setAddressShipping(order.getAddressShipping());
			o.setAddressBilling(order.getAddressBilling());
			o.setDiscountsId(order.getDiscountsId());
			o.setDiscountsName(order.getDiscountsName());
			o.setDiscountsTypeId(order.getDiscountsTypeId());
			o.setDiscountsTypeName(order.getDiscountsTypeName());
			o.setDiscountsValue(order.getDiscountsValue());
			o.setPointOfServiceCount(order.getPointOfServiceCount());
			o.setSessionKey(order.getSessionKey());
			o.setFirstName(order.getFirstName());
			o.setLastName(order.getLastName());
			o.setUpdatedBy(order.getUpdatedBy());
			if (order.getSplitCount() != null) {
				o.setSplitCount(order.getSplitCount());
			}

			if (order.getPaymentWaysId() != null) {
				o.setPaymentWaysId(order.getPaymentWaysId());
			}

			if (order.getOrderSourceId() != null) {
				o.setOrderSourceId(order.getOrderSourceId());
			}
			o.setTaxDisplayName1(order.getTaxDisplayName1());
			o.setTaxDisplayName2(order.getTaxDisplayName2());
			o.setTaxDisplayName3(order.getTaxDisplayName3());
			o.setTaxDisplayName4(order.getTaxDisplayName4());
			o.setTaxName1(order.getTaxName1());
			o.setTaxName2(order.getTaxName2());
			o.setTaxName3(order.getTaxName3());
			o.setTaxName4(order.getTaxName4());
			o.setTaxRate1(order.getTaxRate1());
			o.setTaxRate2(order.getTaxRate2());
			o.setTaxRate3(order.getTaxRate3());
			o.setTaxRate4(order.getTaxRate4());
			o.setTotalTax(order.getTotalTax());
			o.setRoundOffTotal(order.getRoundOffTotal());
			o.setIsGratuityApplied(order.getIsGratuityApplied());
			o.setDiscountDisplayName(order.getDiscountDisplayName());
			o.setUsersId(order.getUsersId());
			o.setCashierId(order.getCashierId());
			o.setIsTabOrder(order.getIsTabOrder());
			o.setTaxExemptId(order.getTaxExemptId());
			o.setIsSeatWiseOrder(order.getIsSeatWiseOrder());
			o.setCalculatedDiscountValue(order.getCalculatedDiscountValue());
			o.setPriceDiscountItemLevel(order.getPriceDiscountItemLevel());
			// adding cashier name 28605
			o.setCashierName(order.getCashierName());

			// added to insert current time of the location in the
			// order
			// header
			TimezoneTime timezoneTime = new TimezoneTime();
			String locationId = order.getLocationsId();

			String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(locationId, em);
			if (currentDateTime != null && currentDateTime.length > 0) {
				o.setDate(currentDateTime[0]);
				if (order.getScheduleDateTime() == null) {
					o.setScheduleDateTime(currentDateTime[2]);
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationId, em));
				} else {
					o.setScheduleDateTime(order.getScheduleDateTime());
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationId, em));
				}

			}
			// added by Apoorva
			o.setUpdated(new Date(updatedTime()));
			// 28436 closing the order when 100 % discount applied By Apoorva
			if (o.getPriceDiscount().compareTo(o.getSubTotal()) == 0
					&& o.getSubTotal().compareTo(new BigDecimal("0")) == 1) {
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			}
			// find order
			// get order before update
			o.setMergeOrderId(order.getMergeOrderId());
			OrderHeader newOH = em.merge(o);
			o.setUpdated(newOH.getUpdated());

			// setting order header_to_tax
			// manage inventory
			ipp = manageInventoryForOrder(httpRequest, order, em, rootlocationId, o, false, false, orderStatus,
					accountId, null);
			// ------------Change by uzma- for order object by id ------
			// find order

			// OrderDetailItems insert in db
			if (order.getOrderDetailItems() != null && order.getOrderDetailItems().size() > 0) {
				for (OrderDetailItem detailItem : order.getOrderDetailItems()) {
					em.merge(detailItem);
				}

			}

			// Order payment detials insert in db
			if (order.getOrderPaymentDetails() != null && order.getOrderPaymentDetails().size() > 0) {
				for (OrderPaymentDetail orderPaymentDetail : order.getOrderPaymentDetails()) {
					orderPaymentDetail.setOrderHeaderId(o.getId());
					em.merge(orderPaymentDetail);
				}

			}

			EntityTransaction tx = em.getTransaction();
			tx.commit();
			tx.begin();

			OrderHeader order_his = getOrderById(em, order.getId());

			if (statusId != order_his.getOrderStatusId()) {
				// insert into order status history
				insertIntoOrderStatusHistory(em, order_his);

			}

			// we need to save the order detail item and order detail item
			// attribute that is sent by the client always
			List<OrderDetailItem> orderDetailItemsListOfHistory = order_his.getOrderDetailItems();
			order_his.setOrderDetailItems(order.getOrderDetailItems());

			// insert into order history
			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order_his, em);

			if (order.getUsersId() != null) {
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, em,
						order.getReservationsId());
				if (reservation != null) {
					reservation.setSessionKey(order.getSessionKey());
					updateReservationUser(reservation, order, em);

					// also insert this into reservation history
					new InsertIntoHistory().insertReservationIntoHistory(httpRequest, reservation, em);
				}
			}
			// if party size is updated, then update the guest count of
			// reservations
			if (isPartySizeUpdated) {
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, em,
						o.getReservationsId());

				if (reservation.getPartySize() != o.getPointOfServiceCount()) {

					reservation.setSessionKey(o.getSessionKey());
					updateReservationPartySize(reservation, o.getPointOfServiceCount(), em);

					// also insert this into reservation history
					new InsertIntoHistory().insertReservationIntoHistory(httpRequest, reservation, em);
					// tell client party size is updated
					order_his.setPartySizeUpdated(true);
				}

			}

			order_his.setOrderDetailItems(orderDetailItemsListOfHistory);
			order_his.setScheduleDateTime(o.getScheduleDateTime());
			order_his.setOrderPaymentDetails(getOrderPaymentDetailsByOrderHeaderId(em, order_his.getId()));

			return new OrderHeaderWithInventoryPostPacket(order_his, ipp);
		} else {
			return new OrderHeaderWithInventoryPostPacket(o, ipp);
		}

	}

	/**
	 * Sets the status to attribute.
	 *
	 * @param orderDetailItem
	 *            the order detail item
	 * @param orderDetailStatusOutOfStock
	 *            the order detail status out of stock
	 */
	private void setStatusToAttribute(OrderDetailItem orderDetailItem, OrderDetailStatus orderDetailStatusOutOfStock) {

		if (orderDetailItem != null) {
			if (orderDetailItem.getOrderDetailAttributes() != null) {
				for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
					orderDetailAttribute.setOrderDetailStatusId(orderDetailStatusOutOfStock.getId());
				}
			}
		}

	}

	/**
	 * Sets the value for merged locations.
	 *
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param isLocationsMerged
	 *            the is locations merged
	 */
	private void setValueForMergedLocations(EntityManager em, OrderHeader order, boolean isLocationsMerged) {
		// check if its merging and locations, then make that location inactive
		// for creating orders
		if (order.getMergedLocationsId() != null && order.getMergedLocationsId().trim().length() > 0) {
			// client is trying to merge location
			String mergedlocationsArray[] = order.getMergedLocationsId().split(",");
			if (mergedlocationsArray != null && mergedlocationsArray.length > 0) {
				for (String locationsIdInStr : mergedlocationsArray) {
					if (locationsIdInStr != null && locationsIdInStr.trim().length() > 0) {
						String locationId = locationsIdInStr.trim();
						Location locationForMerge = (Location) new CommonMethods().getObjectById("Location", em,
								Location.class, locationId);
						;
						if (isLocationsMerged) {
							if (locationForMerge.getIsCurrentlyMerged() == 0) {
								// set location status to 1 indication its
								// already
								// merged with order
								locationForMerge.setIsCurrentlyMerged(1);
								em.merge(locationForMerge);

							}
						} else {
							// free the merged locations now

							if (locationForMerge.getIsCurrentlyMerged() == 1) {

								locationForMerge.setIsCurrentlyMerged(0);
								em.merge(locationForMerge);

							}

						}
					}
				}
			}
		}

	}

	/**
	 * Insert into order status history.
	 *
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @return the order status history
	 */
	private OrderStatusHistory insertIntoOrderStatusHistory(EntityManager em, OrderHeader order) {

		OrderStatusHistory orderStatusHistory = new OrderStatusHistory();

		orderStatusHistory.setOrderHeaderId(order.getId());
		orderStatusHistory.setOrderStatusId(order.getOrderStatusId());
		orderStatusHistory.setCreated(order.getCreated());
		orderStatusHistory.setLocalTime(order.getLocalTime());
		orderStatusHistory.setCreatedBy(order.getCreatedBy());
		orderStatusHistory.setUpdatedBy(order.getUpdatedBy());
		orderStatusHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.persist(orderStatusHistory);
		return orderStatusHistory;

	}

	/**
	 * Insert into order payment detail history.
	 *
	 * @param em
	 *            the em
	 * @param orderPaymentDetail
	 *            the order payment detail
	 * @return the order payment detail history
	 */
	public OrderPaymentDetailHistory insertIntoOrderPaymentDetailHistory(EntityManager em,
			OrderPaymentDetail orderPaymentDetail) {
		// creating history object
		OrderPaymentDetailHistory orderPaymentDetailHistory = new OrderPaymentDetailHistory()
				.setOrderPaymentDetailHistory(orderPaymentDetail);
		// inserting records into history
		em.persist(orderPaymentDetailHistory);
		return orderPaymentDetailHistory;

	}

	/**
	 * Update order status.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param parentlocationId
	 *            the parent location id
	 * @param packetVersion
	 *            the packet version
	 * @param isDupilatePacketCheck
	 *            the is dupilate packet check
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public OrderHeader updateOrderStatus(HttpServletRequest httpRequest, EntityManager em, OrderHeader order,
			String parentlocationId, int packetVersion, boolean isDupilatePacketCheck, int merchantId)
			throws NirvanaXPException, Exception {

		// todo shlok need
		// modularise code here
		OrderStatus orderStatus1 = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				order.getOrderStatusId());

		OrderHeader o = getOrderById(em, order.getId());
		if (isDupilatePacketCheck) {
			if (checkDuplicateOrderHeader(httpRequest, em, order)) {

				logger.severe(httpRequest,
						MessageConstants.ERROR_MESSAGE_ORDER_OUT_OF_SYNCH_DISPLAY_MESSAGE + "-- " + order);
				return o;
			}
		}

		// if order is reopened, and it does not exits in order header table
		// now
		OrderStatus pre_orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
				OrderStatus.class, o.getOrderStatusId());

		logger.severe(
				".gorderStatus===================================================================================="
						+ pre_orderStatus.getName());
		logger.severe(
				".orderStatus1===================================================================================="
						+ orderStatus1.getName());

		if (isAdvanceOrder(o, em, parentlocationId)) {
			logger.severe(
					"here11111111111===================================================================================="
							+ o);
			manageInventoryForOrder(httpRequest, o, em, parentlocationId, o, false, false, orderStatus1, merchantId,
					null);
		}
		if ((pre_orderStatus != null && orderStatus1.getName().equals("Void Order"))) {
			deleteOrder(em, order.getId());
			o.setOrderStatusId(orderStatus1.getId());
			return o;
		}

		if ((pre_orderStatus != null && !pre_orderStatus.getName().equals("Ready to Order"))
				|| orderStatus1.getName().equals("Reopen")) {

			if (orderStatus1.getName().equals("Reopen")) {
				o.setIsOrderReopened(1);
			}
			if (orderStatus1.getName().equals("Order Suspend")) {

				o = unMergeOrder(em, order);
			}

			// SET REASON ID
			o = setReasonForOrder(o, order, orderStatus1, pre_orderStatus, packetVersion);

			if ((!(pre_orderStatus.equals("Ready to Order")) && orderStatus1.getName().equals("Ready to Order"))) {
				o.setIsOrderReopened(0);
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
				if (!pre_orderStatus.getName().equals("Reopen")) {
					updateUserVisitCount(em, o.getUsersId());
				}

			} else if ((!(pre_orderStatus.equals("Bus Ready")) && orderStatus1.getName().equals("Bus Ready"))) {
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			}

			o.setUpdated(new Date(updatedTime()));
			o.setOrderStatusId(order.getOrderStatusId());
			o.setSessionKey(order.getSessionKey());
			o.setUpdatedBy(order.getUpdatedBy());
			o.setVoidReasonId(order.getVoidReasonId());
			o.setVoidReasonName(order.getVoidReasonName());

			if (orderStatus1.getName().equals("Close Production")) {
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			}

			OrderHeader newOH = em.merge(o);
			o.setUpdated(newOH.getUpdated());

			// insert into order history
			insertIntoOrderStatusHistory(em, o);
			// insert into order history
			OrderHeader order_his = getOrderById(em, order.getId());
			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order_his, em);
			// get all possible status
			// if status is highest display sequence, delete from
			// orderheader
			// table

			// #45918: When order status changed to delivered from business app,
			// its still remain in current order list in driver app
			// Changed By Ap , 2018-08-23 updating order status of delivery
			// order
			updateDriverOrderStatus(em, order.getId(), orderStatus1, httpRequest);
			// Changed by Apoorva
			// check if order status is ready to order then its
			// corresponding
			// reservation must also change its status to check out
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, order.getOrderStatusId());
			if (orderStatus != null
					&& (orderStatus.getName().equals("Ready to Order") || (orderStatus.getName().equals("Void Order "))
							|| (orderStatus.getName().equals("Cancel Order")))) {
				if (o.getIsTabOrder() == 1) {
					Reservation oldReservation = new CommonMethods().getReservationById(httpRequest, em,
							o.getReservationsId());

					ReservationsType oldReservationsType = oldReservation.getReservationsType();

					if (oldReservationsType.getName().equals("Walk in")) {
						oldReservation = updateReservationStatus(httpRequest, em, "Void Walkin", oldReservation,
								parentlocationId, o.getUpdatedBy());
					} else if (oldReservationsType.getName().equals("Waitlist")) {
						oldReservation = updateReservationStatus(httpRequest, em, "Waiting", oldReservation,
								parentlocationId, o.getUpdatedBy());
					} else {
						oldReservation = updateReservationStatus(httpRequest, em, "Confirmed", oldReservation,
								parentlocationId, o.getUpdatedBy());
					}

					// insert into reservation history
					new InsertIntoHistory().insertReservationIntoHistory(httpRequest, oldReservation, em);
				} else {

					if (pre_orderStatus.getName().contains("Order Ahead")) {
						updateReservationStatus(httpRequest, em, o, parentlocationId, "Cancelled");
					} else if (orderStatus.getName().equals("Cancel Order")) {
						updateReservationStatus(httpRequest, em, o, parentlocationId, "Cancelled");
					} else {

						updateReservationStatus(httpRequest, em, o, parentlocationId, "Checked Out");

						List<OrderHeader> orderFromMergeOrderIdList = getOrderByMergedOrderId(em, o.getId());
						if (orderFromMergeOrderIdList != null) {
							for (OrderHeader header : orderFromMergeOrderIdList) {
								if (header.getId() != o.getId()) {

									updateReservationStatus(httpRequest, em, header, parentlocationId, "Checked Out");
								}

							}
						}
					}

				}
				// also change all merged locations id to 0, as these
				// locations are no longer merged as the order is freed
				// now
				setValueForMergedLocations(em, o, false);

			}

			return o;
		} else {
			return o;

		}

	}

	/**
	 * Update order schedule date.
	 *
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param parentlocationId
	 *            the parent location id
	 * @param packetVersion
	 *            the packet version
	 * @return the order header
	 */
	public OrderHeader updateOrderScheduleDate(EntityManager em, OrderHeader order, String parentlocationId,
			int packetVersion) {

		OrderHeader o = getOrderById(em, order.getId());
		o.setUpdated(new Date(updatedTime()));
		o.setSessionKey(order.getSessionKey());
		o.setUpdatedBy(order.getUpdatedBy());
		o.setScheduleDateTime(order.getScheduleDateTime());
		OrderHeader newOH = em.merge(o);
		o.setUpdated(newOH.getUpdated());
		// add inventory back for item which has inventory accrual 0 if user
		// change date of order for eg if user create a takeout order and
		// add item

		return o;

	}

	/**
	 * Merge order.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param unMergedLocationsId
	 *            the un merged locations id
	 * @return the order header
	 */
	public OrderHeader mergeOrder(HttpServletRequest httpRequest, EntityManager em, OrderHeader order,
			String unMergedLocationsId) {

		OrderHeader o = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em, OrderHeader.class,
				order.getId());

		o.setMergedLocationsId(order.getMergedLocationsId());
		o.setUpdated(new Date(updatedTime()));
		o.setSessionKey(order.getSessionKey());
		o.setUpdatedBy(order.getUpdatedBy());
		o.setSplitCount(order.getSplitCount());
		OrderHeader newOH = em.merge(o);
		o.setUpdated(newOH.getUpdated());

		if (unMergedLocationsId != null && unMergedLocationsId.length() > 0) {

			// unmerge list of locations
			String unmergedLocationsArr[] = unMergedLocationsId.split(",");
			for (String locationToUnmerge : unmergedLocationsArr) {
				if (locationToUnmerge != null && locationToUnmerge.length() > 0) {
					String locationId = locationToUnmerge.trim();
					Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
							locationId);
					;
					if (location != null && location.getIsCurrentlyMerged() == 1) {
						location.setIsCurrentlyMerged(0);
						em.merge(location);
					}
				}
			}
		}

		// check if its merging and locations, then make that location
		// inactive for creating orders
		if (o.getMergedLocationsId() != null && o.getMergedLocationsId().length() > 0) {
			setValueForMergedLocations(em, o, true);
			// insert into order history

			// we need to save the order detail item and order detail item
			// attribute that is sent by the client always
			List<OrderDetailItem> orderDetailItemsBeforeIntoHistory = o.getOrderDetailItems();
			o.setOrderDetailItems(null);
			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, o, em);
			// reset what it was before as what clints need it
			o.setOrderDetailItems(orderDetailItemsBeforeIntoHistory);
		}

		return o;

	}

	/**
	 * Switch order location.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param parentlocationId
	 *            the parent location id
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public OrderHeader switchOrderLocation(HttpServletRequest httpRequest, EntityManager em, OrderHeader order,
			String parentlocationId) throws NirvanaXPException {

		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				order.getOrderStatusId());

		OrderHeader orderFromDatabase = getOrderById(em, order.getId());

		boolean shouldAllowAddingOrder = shouldAllowAddingOrder(em, order.getLocationsId());

		if (shouldAllowAddingOrder) {
			// order is closed hence it cannot be updated now
			if (orderStatus != null && !orderStatus.getName().equals("Ready to Order")) {

				orderFromDatabase.setUpdated(new Date(updatedTime()));
				orderFromDatabase.setUpdatedBy(order.getUpdatedBy());
				orderFromDatabase.setLocationsId(order.getLocationsId());
				orderFromDatabase.setOrderStatusId(order.getOrderStatusId());

				orderFromDatabase.setPreassignedServerId(order.getPreassignedServerId());
				orderFromDatabase.setSessionKey(order.getSessionKey());
				em.merge(orderFromDatabase);

				// insert this entry into database too
				orderFromDatabase.setOrderDetailItems(null);
				new InsertIntoHistory().insertOrderIntoHistory(httpRequest, orderFromDatabase, em);

				return orderFromDatabase;

			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_ORDER_LOCATION_CANNOT_UPDATE_ORDER_STATUS_EXCEPTION,
						MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_CANNOT_UPDATE_ORDER_STATUS_READY_DISPLAY_MESSAGE,
						null));

			}
		} else {
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(
					MessageConstants.ERROR_CODE_ORDER_LOCATION_ALREADY_HAS_ORDER_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_ALREADY_HAS_ORDER_DISPLAY_MESSAGE, null));
		}
	}

	/**
	 * Change point of service count.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param parentlocationId
	 *            the parent location id
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public OrderHeader changePointOfServiceCount(HttpServletRequest httpRequest, EntityManager em, OrderHeader order,
			String parentlocationId) throws NirvanaXPException {

		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				order.getOrderStatusId());

		OrderHeader orderFromDatabase = getOrderById(em, order.getId());

		// order is closed hence it cannot be updated now
		if (orderStatus != null && !"Ready to Order".equals(orderStatus.getName())) {

			orderFromDatabase.setUpdated(new Date(updatedTime()));

			orderFromDatabase.setUpdatedBy(order.getUpdatedBy());

			orderFromDatabase.setPointOfServiceCount(order.getPointOfServiceCount());
			orderFromDatabase.setSessionKey(order.getSessionKey());

			em.merge(orderFromDatabase);

			// insert this entry into database too
			// we do not want the item details to get inserted at this point
			// if client does not sent us
			orderFromDatabase.setOrderDetailItems(null);
			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, orderFromDatabase, em);

			Reservation reservation = new CommonMethods().getReservationById(httpRequest, em,
					orderFromDatabase.getReservationsId());

			// change if reservation is not present for take out and delivery
			if (reservation != null) {
				reservation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				reservation.setSessionKey(order.getSessionKey());

				updateReservationPartySize(reservation, orderFromDatabase.getPointOfServiceCount(), em);

				// also insert this into reservation history
				new InsertIntoHistory().insertReservationIntoHistory(httpRequest, reservation, em);
			}
			// tell client party size is updated
			orderFromDatabase.setPartySizeUpdated(true);

			return orderFromDatabase;

		} else {
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(
					MessageConstants.ERROR_CODE_ORDER_LOCATION_ALREADY_HAS_ORDER_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_ALREADY_HAS_ORDER_DISPLAY_MESSAGE, null));

		}
	}

	/**
	 * Update reservation status.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderHeader
	 *            the order header
	 * @param parentlocationId
	 *            the parent location id
	 * @param statusName
	 *            the status name
	 */
	private void updateReservationStatus(HttpServletRequest httpRequest, EntityManager em, OrderHeader orderHeader,
			String parentlocationId, String statusName) {
		if (orderHeader != null && orderHeader.getReservationsId() != null && orderHeader.getReservationsId() != null) {

			Reservation reservation = new CommonMethods().getReservationById(httpRequest, em,
					orderHeader.getReservationsId());
			if (reservation != null) {
				// find reservation status id for checkout stuff

				ReservationsStatus reservationsStatus = getReservationStatus(em, parentlocationId, statusName);

				reservation.setReservationsStatus(reservationsStatus);
				reservation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				reservation.setUpdatedBy(orderHeader.getUpdatedBy());
				reservation.setSessionKey(orderHeader.getSessionKey());
				reservation.setReservationsStatusId(reservationsStatus.getId());
				em.merge(reservation);

				// insert into reservation history also this checkout
				Reservation res_histroy = new CommonMethods().getReservationById(httpRequest, em, reservation.getId());

				if (res_histroy != null) {
					// insert into reservation history
					new InsertIntoHistory().insertReservationIntoHistory(httpRequest, res_histroy, em);
				}

			}
		}
	}

	/**
	 * Gets the reservation status.
	 *
	 * @param em
	 *            the em
	 * @param parentlocationId
	 *            the parent location id
	 * @param statusName
	 *            the status name
	 * @return the reservation status
	 */
	private ReservationsStatus getReservationStatus(EntityManager em, String parentlocationId, String statusName) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
		Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
		TypedQuery<ReservationsStatus> query = em.createQuery(
				criteria.select(r).where(new Predicate[] { builder.equal(r.get(ReservationsStatus_.name), statusName),
						builder.equal(r.get(ReservationsStatus_.locationsId), parentlocationId) }));

		return query.getSingleResult();

	}

	/**
	 * Update order payment.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param locationId
	 *            the location id
	 * @param isDupilatePacketCheck
	 *            the is dupilate packet check
	 * @param sessionId
	 *            the session id
	 * @param isReferenceNumber
	 *            the is reference number
	 * @param packet
	 *            the packet
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeaderWithInventoryPostPacket updateOrderPayment(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order, String locationId, boolean isDupilatePacketCheck, String sessionId,
			boolean isReferenceNumber, OrderPacket packet) throws Exception {

		// todo shlok need
		// modularise code here
		// removed by Apoorva
		// boolean checkDuplicate = isDuplicatePacket(em, order);
		OrderHeader o = getOrderById(em, order.getId());
		InventoryPostPacket ipp = null;

		String batchId = PaymentBatchManager.getInstance().getCurrentBatchIdBySession(httpRequest, em, locationId, true,
				packet, order.getUpdatedBy());
		if (isDupilatePacketCheck) {
			if (checkDuplicateOrderHeader(httpRequest, em, order)) {
				logger.severe(httpRequest,
						MessageConstants.ERROR_MESSAGE_ORDER_OUT_OF_SYNCH_DISPLAY_MESSAGE + "-- " + order);
				return new OrderHeaderWithInventoryPostPacket(o, ipp);
			}
		}
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em, OrderSource.class,
				o.getOrderSourceId());
		OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,
				OrderSourceGroup.class, orderSource.getOrderSourceGroupId());

		OrderStatus orderStatus1 = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				order.getOrderStatusId());
		OrderStatus pre_orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
				OrderStatus.class, o.getOrderStatusId());
		if ((pre_orderStatus != null && orderStatus1.getName().equals("Void Order"))) {
			deleteOrder(em, order.getId());
			o.setOrderStatusId(orderStatus1.getId());
			return new OrderHeaderWithInventoryPostPacket(o, ipp);
		}

		try {
			if (/*
				 * (orderStatus1.getName().equals("Discount Applied") ||
				 * orderStatus1.getName().equals("Discount Replaced") ||
				 * orderStatus1.getName().equals("Order Paid")) &&
				 */o.getUsersId() != null) {
				if (order.getUsersToDiscounts() != null) {

					if (!orderStatus1.getName().equals("Discount Removed")
							&& !orderStatus1.getName().equals("Item Discount Removed")) {
						Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
								order.getUsersToDiscounts().getDiscountId());
						if (discount.getNumberOfTimeDiscountUsed() != -1) {
							if (!(order.getUsersToDiscounts().getNumberOfTimeDiscountUsed() <= discount
									.getNumberOfTimeDiscountUsed())) {

								throw (new NirvanaXPException(new NirvanaServiceErrorResponse(
										MessageConstants.ERROR_CODE_DISCOUNT_CODE_ALREADY_USED_BY_USER,
										MessageConstants.ERROR_MESSAGE_DISCOUNT_CODE_ALREADY_USED_BY_USER, null)));
							}
						}
					}

					order.getUsersToDiscounts().setUpdated(new Date(updatedTime()));
					em.merge(order.getUsersToDiscounts());
				}
			} /*
				 * else if(orderStatus1.getName().equals("Discount Removed") &&
				 * o.getUsersId() > 0) {
				 * logger.severe("----------------------------- dis 3 " );
				 * 
				 * PaymentTransactionType paymentTransactionType =
				 * getPaymentTransactionTypeBylocationIdAndName (em,locationId,
				 * "discount"); OrderPaymentDetail orderPaymentDetail; int
				 * ifOPDExist = -1; if (order.getOrderPaymentDetails() != null
				 * && order.getOrderPaymentDetails().size() > 0) {
				 * List<OrderPaymentDetail> orderPaymentDetailList = new
				 * ArrayList<>(o.getOrderPaymentDetails()); int size =
				 * order.getOrderPaymentDetails().size(); for (int i = 0; i <
				 * size; i++) { if
				 * (orderPaymentDetailList.get(i).getPaymentTransactionType(
				 * ).getId() == paymentTransactionType.getId() &&
				 * orderPaymentDetailList.get(i).getSeatId().equals(seatNo)) {
				 * ifOPDExist = i; break; } } }
				 * logger.severe("----------------------------- dis 33 " );
				 * 
				 * if (ifOPDExist != -1 && o.getOrderPaymentDetails() != null) {
				 * orderPaymentDetail = new
				 * ArrayList<>(o.getOrderPaymentDetails()).get(ifOPDExist); }
				 * else { orderPaymentDetail = new OrderPaymentDetail();
				 * orderPaymentDetail.setDiscountCode(""); }
				 * 
				 * logger.severe("----------------------------- dis 4 " +
				 * o.getUsersId() +
				 * " orderPaymentDetail.getDiscountCode().toLowerCase() " +
				 * orderPaymentDetail.getDiscountCode().toLowerCase()
				 * +" locationId " + locationId + " o.getDiscountsId() " +
				 * o.getDiscountsId()); CriteriaBuilder builder =
				 * em.getCriteriaBuilder(); CriteriaQuery<UsersToDiscount>
				 * criteria = builder.createQuery(UsersToDiscount.class);
				 * Root<UsersToDiscount> r =
				 * criteria.from(UsersToDiscount.class);
				 * TypedQuery<UsersToDiscount> query =
				 * em.createQuery(criteria.select(r).where
				 * (builder.equal(r.get(UsersToDiscount_.usersId),
				 * o.getUsersId()),
				 * builder.equal(builder.lower(r.get(UsersToDiscount_.
				 * discountCode)),
				 * orderPaymentDetail.getDiscountCode().toLowerCase()),
				 * builder.equal(r.get(UsersToDiscount_.locationId),
				 * locationId),
				 * builder.equal(r.get(UsersToDiscount_.discountId),
				 * o.getDiscountsId())));
				 * 
				 * logger.severe("----------------------------- dis 5 " );
				 * UsersToDiscount usersToDiscount = query.getSingleResult();
				 * usersToDiscount.setNumberOfTimeDiscountUsed((
				 * usersToDiscount.getNumberOfTimeDiscountUsed()-1));
				 * usersToDiscount = em.merge(usersToDiscount);
				 * 
				 * }
				 */
		} catch (Exception e) {
			// TODO: handle exception
			logger.severe(e);
		}

		if (o.getCloseTime() == 0) {
			if ((!(pre_orderStatus.equals("Ready to Order")) && orderStatus1.getName().equals("Ready to Order"))) {
				if (orderSourceGroup.getName().equals("In Store"))
					o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
				updateReservationStatus(httpRequest, em, o, locationId, "Checked Out");

				setValueForMergedLocations(em, o, false);
			} else if ((!(pre_orderStatus.equals("Order Paid")) && orderStatus1.getName().equals("Order Paid"))) {
				if (orderSourceGroup.getName().equals("In Store"))
					o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			}
			if (orderStatus1.getName().equals("Cancel Order") || orderStatus1.getName().equals("Void Order")) {

				updateReservationStatus(httpRequest, em, o, locationId, "Cancelled");
				o = unMergeOrder(em, order);

			}

		}

		if (order.getPoRefrenceNumber() != null && order.getPoRefrenceNumber() != null
				&& orderSourceGroup.getName().equals("Inventory")) {

			o.setPoRefrenceNumber(order.getPoRefrenceNumber());
			if (orderStatus1 != null && (orderStatus1.getName().equals("Cancel Order"))) {
				String queryString = "select l from RequestOrder l where l.id =" + o.getPoRefrenceNumber();
				TypedQuery<RequestOrder> query = em.createQuery(queryString, RequestOrder.class);
				RequestOrder rOrder = query.getSingleResult();
				String queryStringStatus = "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'";
				TypedQuery<OrderStatus> queryOS = em.createQuery(queryStringStatus, OrderStatus.class)
						.setParameter(1, "PO Cancelled").setParameter(2, locationId)
						.setParameter(3, orderSourceGroup.getId());
				OrderStatus orderSPO = queryOS.getSingleResult();
				rOrder.setStatusId(orderSPO.getId());
				rOrder = em.merge(rOrder);
			}
		}
		// #45918: When order status changed to delivered from business app,
		// its still remain in current order list in driver app
		// Changed By Ap , 2018-08-23 updating order status of delivery
		// order
		updateDriverOrderStatus(em, order.getId(), orderStatus1, httpRequest);
		if ((orderStatus1.getName().equals("Order Paid")) || orderStatus1.getName().equals(MessageConstants.OA_PAID)) {
			if (orderSourceGroup.getName().equals("In Store")) {
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			}
			ipp = handleInventoryForAdvanceOrder(httpRequest, o, em, locationId);
		}
		// round off total not updating in order header
		// issue reported by Priya dat July 20, 2015
		// added by Apoorva
		o.setRoundOffTotal(order.getRoundOffTotal());

		o.setUpdatedBy(order.getUpdatedBy());
		o.setOrderStatusId(order.getOrderStatusId());
		o.setTotal(order.getTotal());
		o.setBalanceDue(order.getBalanceDue());
		o.setServiceTax(order.getServiceTax());
		o.setPriceTax1(order.getPriceTax1());
		o.setPriceTax2(order.getPriceTax2());
		o.setPriceTax3(order.getPriceTax3());
		o.setPriceTax4(order.getPriceTax4());
		o.setPriceExtended(order.getPriceExtended());
		o.setPriceGratuity(order.getPriceGratuity());
		o.setPriceDiscount(order.getPriceDiscount());
		o.setGratuity(order.getGratuity());
		o.setAmountPaid(order.getAmountPaid());
		o.setSubTotal(order.getSubTotal());
		o.setCashierName(order.getCashierName());
		o.setCalculatedDiscountValue(order.getCalculatedDiscountValue());

		// adding for quick pay 26689
		if (order.getTaxDisplayName1() != null) {
			o.setTaxDisplayName1(order.getTaxDisplayName1());
		}
		if (order.getTaxDisplayName2() != null) {
			o.setTaxDisplayName2(order.getTaxDisplayName2());
		}
		if (order.getTaxDisplayName3() != null) {
			o.setTaxDisplayName3(order.getTaxDisplayName3());
		}
		if (order.getTaxDisplayName4() != null) {
			o.setTaxDisplayName4(order.getTaxDisplayName4());
		}
		if (order.getTaxRate1() != null) {
			o.setTaxRate1(order.getTaxRate1());
		}
		if (order.getTaxRate2() != null) {
			o.setTaxRate2(order.getTaxRate2());
		}
		if (order.getTaxRate3() != null) {
			o.setTaxRate3(order.getTaxRate3());
		}
		if (order.getTaxRate4() != null) {
			o.setTaxRate4(order.getTaxRate4());
		}

		if (order.getTaxName1() != null) {
			o.setTaxName1(order.getTaxName1());
		}
		if (order.getTaxName2() != null) {
			o.setTaxName2(order.getTaxName2());
		}
		if (order.getTaxName3() != null) {
			o.setTaxName3(order.getTaxName3());
		}
		if (order.getTaxName4() != null) {
			o.setTaxName4(order.getTaxName4());
		}
		if (order.getRoundOffTotal() != null && o.getRoundOffTotal().compareTo(new BigDecimal("0.00")) == 0) {
			o.setRoundOffTotal(order.getRoundOffTotal());
		}

		if (order.getPaymentWaysId() != null) {
			o.setPaymentWaysId(order.getPaymentWaysId());
		}
		if (order.getSplitCount() != null) {
			o.setSplitCount(order.getSplitCount());
		}
		if (order.getComment() != null) {
			o.setComment(order.getComment());
		}
		o.setSessionKey(order.getSessionKey());
		o.setCashierId(order.getCashierId());

		// added by Apoorva to set updated time of order
		Date dates = new Date(updatedTime());
		o.setUpdated(dates);
		// added by Apoorva 30462 2015-09-11
		o.setDiscountsId(order.getDiscountsId());
		o.setDiscountDisplayName(order.getDiscountDisplayName());
		o.setDiscountsTypeId(order.getDiscountsTypeId());
		o.setDiscountsTypeName(order.getDiscountsTypeName());
		o.setDiscountsName(order.getDiscountsName());
		o.setDiscountsValue(order.getDiscountsValue());
		o.setPriceDiscountItemLevel(order.getPriceDiscountItemLevel());
		o.setIsSeatWiseOrder(order.getIsSeatWiseOrder());
		if (order.getDeliveryCharges() != null) {
			o.setDeliveryCharges(order.getDeliveryCharges());
		} else {
			o.setDeliveryCharges(new BigDecimal(0.00));
		}

		if (order.getDeliveryOptionId() != null) {
			o.setDeliveryOptionId(order.getDeliveryOptionId());
		} else {
			o.setDeliveryOptionId(null);
		}

		if (order.getServiceCharges() != null) {
			o.setServiceCharges(order.getServiceCharges());
		} else {
			o.setServiceCharges(new BigDecimal(0.00));
		}

		if (order.getDeliveryTax() != null) {
			o.setDeliveryTax(order.getDeliveryTax());
		} else {
			o.setDeliveryTax(new BigDecimal(0.00));
		}
		// total tax should not be null -34424
		if (order.getTotalTax() != null)
			o.setTotalTax(order.getTotalTax());

		// change by ap :- #46310: deliveryOptionID not becoming zero in
		// updateOrderForDuplicateCheck service
		o.setDeliveryOptionId(order.getDeliveryOptionId());

		// SET REASON ID
		o = setReasonForOrder(o, order, orderStatus1, pre_orderStatus, packet.getPacketVersion());
		OrderHeader newOH = em.merge(o);
		o.setUpdated(dates);

		o = manageOrderRelationship(em, o);
		if (o.getIsTabOrder() == 1 && checkOrderExistForUpdateReservationStatus(em, o.getId())) {
			Reservation oldReservation = new CommonMethods().getReservationById(httpRequest, em, o.getReservationsId());
			ReservationsType oldReservationsType = oldReservation.getReservationsType();

			if (oldReservationsType.getName().equals("Walk in")) {
				oldReservation = updateReservationStatus(httpRequest, em, "Void Walkin", oldReservation, locationId,
						o.getUpdatedBy());
			} else if (oldReservationsType.getName().equals("Waitlist")) {
				oldReservation = updateReservationStatus(httpRequest, em, "Waiting", oldReservation, locationId,
						o.getUpdatedBy());
			} else {
				oldReservation = updateReservationStatus(httpRequest, em, "Confirmed", oldReservation, locationId,
						o.getUpdatedBy());
			}

			// insert into reservation history
			new InsertIntoHistory().insertReservationIntoHistory(httpRequest, oldReservation, em);
		}

		if (order.getOrderPaymentDetails() != null) {
			for (OrderPaymentDetail orderPaymentDetail : order.getOrderPaymentDetails()) {
				orderPaymentDetail.setOrderHeaderId(o.getId());
				if (orderPaymentDetail.getNirvanaXpBatchNumber() == null) {
					if (pre_orderStatus.getName().equals("Reopen")) {
						orderPaymentDetail.setNirvanaXpBatchNumber(o.getNirvanaXpBatchNumber());
					} else {
						orderPaymentDetail.setNirvanaXpBatchNumber(batchId);
					}

				}

				UsersToPaymentHistory usersToPaymentHistory = orderPaymentDetail.getUsersToPaymentHistory();
				// add or update payment detail
				orderPaymentDetail.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				if (orderPaymentDetail.getCreated() == null) {
					orderPaymentDetail.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}

				orderPaymentDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				boolean newEntry = false;
				if (orderPaymentDetail.getId() == null) {
					newEntry = true;
					orderPaymentDetail.setId(new StoreForwardUtility().generateDynamicBigIntId(em, locationId,
							httpRequest, "order_payment_details"));
				}
				orderPaymentDetail = em.merge(orderPaymentDetail);

				if (usersToPaymentHistory != null) {
					BigDecimal balance = new BigDecimal(0);
					logger.severe(
							"usersToPaymentHistory.getPaymentMethodTypeId()==========================================================================="
									+ usersToPaymentHistory.getPaymentMethodTypeId());
					usersToPaymentHistory.getPaymentMethodTypeId();

					if (usersToPaymentHistory.getUsersToPaymentId() != null) {

						UsersToPayment usersToPayment = em.find(UsersToPayment.class,
								usersToPaymentHistory.getUsersToPaymentId());
						balance = usersToPayment.getAmount().subtract(usersToPaymentHistory.getAmountPaid());
						usersToPayment
								.setAmount(usersToPayment.getAmount().subtract(usersToPaymentHistory.getAmountPaid()));
						usersToPayment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						usersToPayment.setPaymentMethodTypeId(usersToPaymentHistory.getPaymentMethodTypeId());
						usersToPayment.setPaymentTypeId(usersToPaymentHistory.getPaymentTypeId());
						em.merge(usersToPayment);

					} else {

						BigDecimal amount_zero = new BigDecimal(0);
						balance = amount_zero.subtract(usersToPaymentHistory.getAmountPaid());

						UsersToPayment usersToPayment = new UsersToPayment();
						usersToPayment.setAmount(amount_zero.subtract(usersToPaymentHistory.getAmountPaid()));
						usersToPayment.setPaymentTypeId(usersToPaymentHistory.getPaymentTypeId());
						usersToPayment.setPaymentMethodTypeId(usersToPaymentHistory.getPaymentMethodTypeId());

						usersToPayment.setUsersId(usersToPaymentHistory.getUserId());
						usersToPayment.setUpdatedBy(usersToPaymentHistory.getUpdatedBy());
						usersToPayment.setCreatedBy(usersToPaymentHistory.getCreatedBy());
						usersToPayment.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						usersToPayment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						usersToPayment.setStatus(usersToPaymentHistory.getStatus());

						usersToPayment.setId(new StoreForwardUtility().generateDynamicIntId(em, locationId, httpRequest,
								"users_to_payment"));
						usersToPayment = em.merge(usersToPayment);
						em.getTransaction().commit();
						em.getTransaction().begin();
						usersToPaymentHistory.setUsersToPaymentId(usersToPayment.getId());

					}

					usersToPaymentHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToPaymentHistory.setOrderPaymentDetailsId(orderPaymentDetail.getId());
					usersToPaymentHistory.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					usersToPaymentHistory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToPaymentHistory.setBalanceDue(balance);
					em.persist(usersToPaymentHistory);
					em.getTransaction().commit();
					em.getTransaction().begin();
				}

				if (newEntry) {
					try {
						CashRegisterRunningBalanceThread balanceThread = new CashRegisterRunningBalanceThread(
								orderPaymentDetail, httpRequest, logger, locationId, sessionId);
						Thread t = new Thread(balanceThread);
						t.start();
					} catch (Exception e) {
						logger.severe(e);
					}
					// setCashRegisterRunningBalance(em, orderPaymentDetail,
					// locationId,localTime);
				}

				insertIntoOrderPaymentDetailHistory(em, orderPaymentDetail);
			}

		}
		// insert into order history
		insertIntoOrderStatusHistory(em, o);

		// as we want the order details to go to history if they have gone a
		// change

		o.setOrderDetailItems(order.getOrderDetailItems());

		if (order.getOrderDetailItems() != null && order.getOrderDetailItems().size() > 0) {
			// handling inventory for customer app pay later by apoorv 11-05
			boolean isDeductibleForOnlineAndPostPayment = false;
			if ((orderStatus1.getName().equals("Order Paid")) || orderStatus1.getName().equals(MessageConstants.OA_PAID)
					|| orderStatus1.getName().equals(MessageConstants.OA_PLACED)) {
				isDeductibleForOnlineAndPostPayment = true;
			}
			ipp = manageInventoryForOrder(httpRequest, order, em, locationId, order, true,
					isDeductibleForOnlineAndPostPayment, orderStatus1, Integer.parseInt(packet.getMerchantId()), null);
			// ipp = manageInventoryForOrder(httpRequest, order, em,
			// locationId, order, true, false);
		}

		// insert into order history

		new InsertIntoHistory().insertOrderIntoHistory(httpRequest, o, em);

		logger.severe(httpRequest, "adding order payment detail for order: " + o.getId());

		EntityTransaction tx = em.getTransaction();
		tx.commit();
		tx.begin();
		OrderHeader new_order = getOrderById(em, o.getId());
		// manage inventory
		return new OrderHeaderWithInventoryPostPacket(new_order, ipp);

	}

	public void setCashRegisterRunningBalance(EntityManager em, OrderPaymentDetail orderPaymentDetail,
			String locationId) {
		try {
			int transectionTypeId = orderPaymentDetail.getPaymentTransactionType().getId();
			/*
			 * if (transectionTypeId == getPaymentTransactionType(em,
			 * locationId, "Sale").getId() || (getPaymentTransactionType(em,
			 * locationId, "Credit") != null && transectionTypeId ==
			 * getPaymentTransactionType(em, locationId, "Credit").getId()) ||
			 * transectionTypeId != getPaymentTransactionType(em, locationId,
			 * "Void").getId() || transectionTypeId !=
			 * getPaymentTransactionType(em, locationId, "Refund").getId() ||
			 * transectionTypeId == getPaymentTransactionType(em, locationId,
			 * "Auth").getId() || transectionTypeId ==
			 * getPaymentTransactionType(em, locationId,
			 * "Manual CC Auth").getId()) {
			 */

			BigDecimal amount = new BigDecimal(0);
			if (transectionTypeId == getPaymentTransactionType(em, locationId, "Sale").getId()
					|| transectionTypeId == getPaymentTransactionType(em, locationId, "Refund").getId()) {
				amount = orderPaymentDetail.getAmountPaid();
			}

			if (orderPaymentDetail.getCashTipAmt() != null) {
				amount = amount.add(orderPaymentDetail.getCashTipAmt());
			}

			if (amount.doubleValue() > 0) {
				CashRegisterRunningBalance balance = new CashRegisterRunningBalance();
				balance.setIsAmountCarryForwarded(0);
				balance.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				balance.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				balance.setCreatedBy(orderPaymentDetail.getCreatedBy());
				balance.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				balance.setUpdatedBy(orderPaymentDetail.getUpdatedBy());

				balance.setNirvanaXpBatchNumber(orderPaymentDetail.getNirvanaXpBatchNumber());

				balance.setRegisterId(orderPaymentDetail.getRegister());
				balance.setStatus("A");

				balance.setTransactionAmount(amount);
				CashRegisterRunningBalance resultSet = null;
				List<CashRegisterRunningBalance> result = null;

				try {
					String queryString = "select l from CashRegisterRunningBalance l where l.registerId =? order by id asc ";
					TypedQuery<CashRegisterRunningBalance> query = em
							.createQuery(queryString, CashRegisterRunningBalance.class)
							.setParameter(1, balance.getRegisterId());
					result = query.getResultList();
				} catch (Exception e) {
					// todo shlok need
					// handle proper exception
					logger.severe(e);
				}

				// todo shlok need
				// ?
				for (CashRegisterRunningBalance c : result) {
					resultSet = c;
					continue;
				}

				if (resultSet != null) {
					balance.setRunningBalance(resultSet.getRunningBalance());
				} else {
					balance.setRunningBalance(new BigDecimal(0));
				}
				balance.setOpdId(orderPaymentDetail.getId());
				if (transectionTypeId == getPaymentTransactionType(em, locationId, "Void").getId()
						|| transectionTypeId == getPaymentTransactionType(em, locationId, "Refund").getId()
						|| transectionTypeId == getPaymentTransactionType(em, locationId, "Credit Refund").getId()
						|| transectionTypeId == getPaymentTransactionType(em, locationId, "void order").getId()
						|| transectionTypeId == getPaymentTransactionType(em, locationId, "Cancel Order").getId()
						|| transectionTypeId == getPaymentTransactionType(em, locationId, "Return").getId()) {
					// remove
					balance.setRunningBalance(balance.getRunningBalance().subtract(balance.getTransactionAmount()));
					balance.setTransactionStatus("DR");
					em.persist(balance);
				} else {
					// add
					balance.setTransactionStatus("CR");
					balance.setRunningBalance(balance.getRunningBalance().add(balance.getTransactionAmount()));
					em.persist(balance);

				}

			}

			// }
		} catch (Exception e) {
			logger.severe(e);
		}
	}

	/**
	 * Gets the payment transaction type.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the payment transaction type
	 */
	private PaymentTransactionType getPaymentTransactionType(EntityManager em, String locationId, String name) {

		try {
			String queryString = "select l from PaymentTransactionType l where   l.name='" + name + "'";
			TypedQuery<PaymentTransactionType> query = em.createQuery(queryString, PaymentTransactionType.class);
			return query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new PaymentTransactionType();
	}

	/**
	 * Update order payment for batch settle.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @return the order header
	 */
	public OrderHeader updateOrderPaymentForBatchSettle(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order) {
		OrderHeader o = getOrderById(em, order.getId());
		logger.info("updating for order: " + order.getId(), ", " + order.getOrderPaymentDetails().size(),
				"order details");
		if (order.getOrderPaymentDetails() != null) {

			for (OrderPaymentDetail orderPaymentDetail : order.getOrderPaymentDetails())

			{
				orderPaymentDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				orderPaymentDetail.setOrderHeaderId(o.getId());
				logger.info("Order payment detail:" + orderPaymentDetail.getId(), "status is:",
						orderPaymentDetail.getTransactionStatus().getName());
				orderPaymentDetail = em.merge(orderPaymentDetail);
				// inserting into history
				insertIntoOrderPaymentDetailHistory(em, orderPaymentDetail);

			}
		}

		// insert into order history
		insertIntoOrderStatusHistory(em, o);

		logger.info("adding order payment detail for order: " + o.getId());
		OrderHeader new_order = getOrderById(em, o.getId());
		return new_order;

	}

	/**
	 * Gets the order by id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the order by id
	 */
	public OrderHeader getOrderById(EntityManager em, String id) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> r = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderHeader_.id), id)));
		OrderHeader orderHeader = query.getSingleResult();
		OrderHeader result = new OrderHeader(id, orderHeader.getAddressBilling(), orderHeader.getAddressShipping(),
				orderHeader.getAmountPaid(), orderHeader.getBalanceDue(), orderHeader.getCreated(),
				orderHeader.getCreatedBy(), orderHeader.getDiscountsId(), orderHeader.getDiscountsName(),
				orderHeader.getDiscountsTypeId(), orderHeader.getDiscountsTypeName(), orderHeader.getDiscountsValue(),
				orderHeader.getGratuity(), orderHeader.getIpAddress(), orderHeader.getOrderSourceId(),
				orderHeader.getOrderStatusId(), orderHeader.getPointOfServiceCount(), orderHeader.getPriceDiscount(),
				orderHeader.getPriceExtended(), orderHeader.getPriceGratuity(), orderHeader.getPriceTax1(),
				orderHeader.getPriceTax2(), orderHeader.getPriceTax3(), orderHeader.getPriceTax4(),
				orderHeader.getReservationsId(), orderHeader.getServiceTax(), orderHeader.getSubTotal(),
				orderHeader.getTotal(), orderHeader.getSplitCount(), orderHeader.getUpdated(),
				orderHeader.getUpdatedBy(), orderHeader.getUsersId(), orderHeader.getLocationsId(),
				orderHeader.getPaymentWaysId(), orderHeader.getDate(), orderHeader.getVerificationCode(),
				orderHeader.getQrcode(), orderHeader.getSessionKey(), orderHeader.getFirstName(),
				orderHeader.getLastName(), orderHeader.getServerId(), orderHeader.getCashierId(),
				orderHeader.getVoidReasonId(), orderHeader.getMergedLocationsId(), orderHeader.getOpenTime(),
				orderHeader.getCloseTime(), orderHeader.getDiscountDisplayName(), orderHeader.getTaxDisplayName1(),
				orderHeader.getTaxDisplayName2(), orderHeader.getTaxDisplayName3(), orderHeader.getTaxDisplayName4(),
				orderHeader.getTaxRate1(), orderHeader.getTaxRate2(), orderHeader.getTaxRate3(),
				orderHeader.getTaxRate4(), orderHeader.getTotalTax(), orderHeader.getTaxName1(),
				orderHeader.getTaxName2(), orderHeader.getTaxName3(), orderHeader.getTaxName4(),
				orderHeader.getIsGratuityApplied(), orderHeader.getRoundOffTotal(), orderHeader.getIsTabOrder(),
				orderHeader.getServername(), orderHeader.getCashierName(), orderHeader.getVoidReasonName(),
				orderHeader.getIsOrderReopened(), orderHeader.getTaxExemptId(), orderHeader.getScheduleDateTime(),
				orderHeader.getReferenceNumber(), orderHeader.getNirvanaXpBatchNumber(), orderHeader.getOrderNumber(),
				orderHeader.getIsSeatWiseOrder(), orderHeader.getCalculatedDiscountValue(),
				orderHeader.getShiftSlotId(), orderHeader.getPriceDiscountItemLevel(), orderHeader.getMergeOrderId(),
				orderHeader.getCompanyName(), orderHeader.getTaxNo(), orderHeader.getTaxDisplayName(),
				orderHeader.getDeliveryCharges(), orderHeader.getDeliveryTax(), orderHeader.getDeliveryOptionId(),
				orderHeader.getServiceCharges(), orderHeader.getPreassignedServerId(), orderHeader.getLocalTime(),
				orderHeader.getPoRefrenceNumber(), orderHeader.getRequestedLocationId(), orderHeader.getOrderTypeId(),
				orderHeader.getDriverId(), orderHeader.getComment(), orderHeader.getStartDate(),
				orderHeader.getEndDate(), orderHeader.getEventName());

		result.setOrderDetailItems(getOrderDetailsItemForOrderId(em, id));
		result.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, id));
		result.setOrderHeaderToSeatDetails(getOrderHeaderToSeatDetailForOrderId(em, id));
		return result;
	}

	/**
	 * Gets the order by id with user.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the order by id with user
	 */
	public OrderPacket getOrderByIdWithUser(EntityManager em, @PathParam("id") String id) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> r = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderHeader_.id), id)));
		OrderHeader orderHeader = query.getSingleResult();
		OrderHeader result = new OrderHeader(id, orderHeader.getAddressBilling(), orderHeader.getAddressShipping(),
				orderHeader.getAmountPaid(), orderHeader.getBalanceDue(), orderHeader.getCreated(),
				orderHeader.getCreatedBy(), orderHeader.getDiscountsId(), orderHeader.getDiscountsName(),
				orderHeader.getDiscountsTypeId(), orderHeader.getDiscountsTypeName(), orderHeader.getDiscountsValue(),
				orderHeader.getGratuity(), orderHeader.getIpAddress(), orderHeader.getOrderSourceId(),
				orderHeader.getOrderStatusId(), orderHeader.getPointOfServiceCount(), orderHeader.getPriceDiscount(),
				orderHeader.getPriceExtended(), orderHeader.getPriceGratuity(), orderHeader.getPriceTax1(),
				orderHeader.getPriceTax2(), orderHeader.getPriceTax3(), orderHeader.getPriceTax4(),
				orderHeader.getReservationsId(), orderHeader.getServiceTax(), orderHeader.getSubTotal(),
				orderHeader.getTotal(), orderHeader.getSplitCount(), orderHeader.getUpdated(),
				orderHeader.getUpdatedBy(), orderHeader.getUsersId(), orderHeader.getLocationsId(),
				orderHeader.getPaymentWaysId(), orderHeader.getDate(), orderHeader.getVerificationCode(),
				orderHeader.getQrcode(), orderHeader.getSessionKey(), orderHeader.getFirstName(),
				orderHeader.getLastName(), orderHeader.getServerId(), orderHeader.getCashierId(),
				orderHeader.getVoidReasonId(), orderHeader.getMergedLocationsId(), orderHeader.getOpenTime(),
				orderHeader.getCloseTime(), orderHeader.getDiscountDisplayName(), orderHeader.getTaxDisplayName1(),
				orderHeader.getTaxDisplayName2(), orderHeader.getTaxDisplayName3(), orderHeader.getTaxDisplayName4(),
				orderHeader.getTaxRate1(), orderHeader.getTaxRate2(), orderHeader.getTaxRate3(),
				orderHeader.getTaxRate4(), orderHeader.getTotalTax(), orderHeader.getTaxName1(),
				orderHeader.getTaxName2(), orderHeader.getTaxName3(), orderHeader.getTaxName4(),
				orderHeader.getIsGratuityApplied(), orderHeader.getRoundOffTotal(), orderHeader.getIsTabOrder(),
				orderHeader.getServername(), orderHeader.getCashierName(), orderHeader.getVoidReasonName(),
				orderHeader.getIsOrderReopened(), orderHeader.getTaxExemptId(), orderHeader.getScheduleDateTime(),
				orderHeader.getReferenceNumber(), orderHeader.getNirvanaXpBatchNumber(), orderHeader.getOrderNumber(),
				orderHeader.getIsSeatWiseOrder(), orderHeader.getCalculatedDiscountValue(),
				orderHeader.getShiftSlotId(), orderHeader.getPriceDiscountItemLevel(), orderHeader.getMergeOrderId(),
				orderHeader.getCompanyName(), orderHeader.getTaxNo(), orderHeader.getTaxDisplayName(),
				orderHeader.getDeliveryCharges(), orderHeader.getDeliveryTax(), orderHeader.getDeliveryOptionId(),
				orderHeader.getServiceCharges(), orderHeader.getPreassignedServerId(), orderHeader.getLocalTime(),
				orderHeader.getPoRefrenceNumber(), orderHeader.getRequestedLocationId(), orderHeader.getOrderTypeId(),
				orderHeader.getDriverId(), orderHeader.getComment(), orderHeader.getStartDate(),
				orderHeader.getEndDate(), orderHeader.getEventName());

		result.setOrderDetailItems(getOrderDetailsItemForOrderIdWithoutRemoveAndRecallItem(em, id));

		result.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, id));
		result.setOrderHeaderToSeatDetails(getOrderHeaderToSeatDetailForOrderId(em, id));
		OrderPacket orderPacket = new OrderPacket();
		orderPacket.setOrderHeader(result);

		if (result.getUsersId() != null) {
			User tempUser = null;
			try {

				CriteriaQuery<User> criteriaUser = builder.createQuery(User.class);
				Root<User> rUser = criteriaUser.from(User.class);
				TypedQuery<User> queryUser = em.createQuery(
						criteriaUser.select(rUser).where(builder.equal(rUser.get(User_.id), result.getUsersId())));
				tempUser = queryUser.getSingleResult();
			} catch (Exception e) {
				// todo shlok need
				// hadle proper exception
				logger.severe(e);
			}

			if (tempUser != null) {
				orderPacket.setUser(tempUser);
			}

		}

		return orderPacket;
	}

	/**
	 * Gets the order details item for order id without remove and recall item.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the order details item for order id without remove and recall
	 *         item
	 */
	List<OrderDetailItem> getOrderDetailsItemForOrderIdWithoutRemoveAndRecallItem(EntityManager em,
			String orderHeaderId) {

		String statusName = "'Item Removed','Recall','Cancel Item','Void Item'";
		String queryString = "select id from order_detail_status os where os.name in (" + statusName + ")";

		Query queryStatus = em.createNativeQuery(queryString);

		List<Integer> orderStatus = (List<Integer>) queryStatus.getResultList();
		String orderStatusIds = orderStatus.toString();
		orderStatusIds = orderStatusIds.replace("[", "").replace("]", "");

		String queryS = "select os from OrderDetailItem os where os.orderHeaderId = " + orderHeaderId
				+ " and os.orderDetailStatusId NOT IN (" + orderStatusIds + ")";

		TypedQuery<OrderDetailItem> query = em.createQuery(queryS, OrderDetailItem.class);
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();

		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList) {
			orderDetailItemObj.setOrderDetailAttributes(
					getOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId()));
		}
		return orderDetailItemsList;

	}

	/**
	 * Gets the order details item with item group id for order id without
	 * remove and recall item.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the order details item with item group id for order id without
	 *         remove and recall item
	 */
	List<OrderDetailItem> getOrderDetailsItemWithItemGroupIdForOrderIdWithoutRemoveAndRecallItem(EntityManager em,
			String orderHeaderId) {

		String statusName = "'Item Removed','Recall','Cancel Item','Void Item'";
		String queryString = "select id from order_detail_status os where os.name in (" + statusName + ")";

		Query queryStatus = em.createNativeQuery(queryString);

		List<Integer> orderStatus = (List<Integer>) queryStatus.getResultList();
		String orderStatusIds = orderStatus.toString();

		orderStatusIds = orderStatusIds.replace("[", "").replace("]", "");

		String queryS = "select os from OrderDetailItem os where os.orderHeaderId = ?"
				+ " and os.orderDetailStatusId NOT IN (" + orderStatusIds + ")";

		TypedQuery<OrderDetailItem> query = em.createQuery(queryS, OrderDetailItem.class).setParameter(1,
				orderHeaderId);
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();

		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList) {
			orderDetailItemObj.setOrderDetailAttributes(
					getOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId()));
			String queryStringForItemGroup = "select item_group_id from items i where i.id  = ?";

			Query queryItemGroup = em.createNativeQuery(queryStringForItemGroup).setParameter(1,
					orderDetailItemObj.getItemsId());

			String itemGroupId = null;
			try {
				itemGroupId = (String) queryItemGroup.getSingleResult();
				orderDetailItemObj.setItemGroupId(itemGroupId);
			} catch (Exception e) {
				// todo shlok need
				// hadle proper exception
				// TODO Auto-generated catch block
				logger.severe(e);
			}

		}
		return orderDetailItemsList;

	}

	/**
	 * Gets the order by id and parent location id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param locationId
	 *            the location id
	 * @return the order by id and parent location id
	 */
	public OrderHeader getOrderByIdAndParentLocationId(EntityManager em, String id, String locationId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> r = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderHeader_.id), id)));
		OrderHeader orderHeader = query.getSingleResult();
		OrderHeader result = new OrderHeader(id, orderHeader.getAddressBilling(), orderHeader.getAddressShipping(),
				orderHeader.getAmountPaid(), orderHeader.getBalanceDue(), orderHeader.getCreated(),
				orderHeader.getCreatedBy(), orderHeader.getDiscountsId(), orderHeader.getDiscountsName(),
				orderHeader.getDiscountsTypeId(), orderHeader.getDiscountsTypeName(), orderHeader.getDiscountsValue(),
				orderHeader.getGratuity(), orderHeader.getIpAddress(), orderHeader.getOrderSourceId(),
				orderHeader.getOrderStatusId(), orderHeader.getPointOfServiceCount(), orderHeader.getPriceDiscount(),
				orderHeader.getPriceExtended(), orderHeader.getPriceGratuity(), orderHeader.getPriceTax1(),
				orderHeader.getPriceTax2(), orderHeader.getPriceTax3(), orderHeader.getPriceTax4(),
				orderHeader.getReservationsId(), orderHeader.getServiceTax(), orderHeader.getSubTotal(),
				orderHeader.getTotal(), orderHeader.getSplitCount(), orderHeader.getUpdated(),
				orderHeader.getUpdatedBy(), orderHeader.getUsersId(), orderHeader.getLocationsId(),
				orderHeader.getPaymentWaysId(), orderHeader.getDate(), orderHeader.getVerificationCode(),
				orderHeader.getQrcode(), orderHeader.getSessionKey(), orderHeader.getFirstName(),
				orderHeader.getLastName(), orderHeader.getServerId(), orderHeader.getCashierId(),
				orderHeader.getVoidReasonId(), orderHeader.getMergedLocationsId(), orderHeader.getOpenTime(),
				orderHeader.getCloseTime(), orderHeader.getDiscountDisplayName(), orderHeader.getTaxDisplayName1(),
				orderHeader.getTaxDisplayName2(), orderHeader.getTaxDisplayName3(), orderHeader.getTaxDisplayName4(),
				orderHeader.getTaxRate1(), orderHeader.getTaxRate2(), orderHeader.getTaxRate3(),
				orderHeader.getTaxRate4(), orderHeader.getTotalTax(), orderHeader.getTaxName1(),
				orderHeader.getTaxName2(), orderHeader.getTaxName3(), orderHeader.getTaxName4(),
				orderHeader.getIsGratuityApplied(), orderHeader.getRoundOffTotal(), orderHeader.getIsTabOrder(),
				orderHeader.getServername(), orderHeader.getCashierName(), orderHeader.getVoidReasonName(),
				orderHeader.getIsOrderReopened(), orderHeader.getTaxExemptId(), orderHeader.getScheduleDateTime(),
				orderHeader.getReferenceNumber(), orderHeader.getNirvanaXpBatchNumber(), orderHeader.getOrderNumber(),
				orderHeader.getIsSeatWiseOrder(), orderHeader.getCalculatedDiscountValue(),
				orderHeader.getShiftSlotId(), orderHeader.getPriceDiscountItemLevel(), orderHeader.getMergeOrderId(),
				orderHeader.getCompanyName(), orderHeader.getTaxNo(), orderHeader.getTaxDisplayName(),
				orderHeader.getDeliveryCharges(), orderHeader.getDeliveryTax(), orderHeader.getDeliveryOptionId(),
				orderHeader.getServiceCharges(), orderHeader.getPreassignedServerId(), orderHeader.getLocalTime(),
				orderHeader.getPoRefrenceNumber(), orderHeader.getRequestedLocationId(), orderHeader.getOrderTypeId(),
				orderHeader.getDriverId(), orderHeader.getComment(), orderHeader.getStartDate(),
				orderHeader.getEndDate(), orderHeader.getEventName());

		result.setOrderDetailItems(getOrderDetailsItemForOrderIdAndlocationId(em, id, locationId));

		result.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, id));
		result.setOrderHeaderToSeatDetails(getOrderHeaderToSeatDetailForOrderId(em, id));
		return result;
	}

	/**
	 * Gets the order by merged order id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the order by merged order id
	 */
	public List<OrderHeader> getOrderByMergedOrderId(EntityManager em, String id) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> r = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderHeader_.mergeOrderId), id)));
		return query.getResultList();
	}

	/**
	 * Gets the order payment detail for order id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the order payment detail for order id
	 */
	private List<OrderPaymentDetail> getOrderPaymentDetailForOrderId(EntityManager em, String orderHeaderId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderPaymentDetail> criteria = builder.createQuery(OrderPaymentDetail.class);
		Root<OrderPaymentDetail> orderPaymentDetail = criteria.from(OrderPaymentDetail.class);
		TypedQuery<OrderPaymentDetail> query = em.createQuery(criteria.select(orderPaymentDetail)
				.where(builder.equal(orderPaymentDetail.get(OrderPaymentDetail_.orderHeaderId), orderHeaderId)));
		List<OrderPaymentDetail> orderPaymentDetails = query.getResultList();

		for (OrderPaymentDetail paymentDetail : orderPaymentDetails) {
			paymentDetail.setOrderPaymentDetailsToSalesTax(
					getOrderPaymentDetailsToSalesTaxForOrderPaymentDetailsId(em, paymentDetail.getId()));
		}

		return orderPaymentDetails;

	}

	/**
	 * Gets the order details item for order id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the order details item for order id
	 */
	List<OrderDetailItem> getOrderDetailsItemForOrderId(EntityManager em, String orderHeaderId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<OrderDetailItem> criteria = builder.createQuery(OrderDetailItem.class);
		Root<OrderDetailItem> orderDetailItem = criteria.from(OrderDetailItem.class);
		TypedQuery<OrderDetailItem> query = em.createQuery(criteria.select(orderDetailItem)
				.where(builder.equal(orderDetailItem.get(OrderDetailItem_.orderHeaderId), orderHeaderId)));
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();
		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList) {
			orderDetailItemObj.setOrderDetailAttributes(
					getOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId()));

			// NotPrintedOrderDetailItemsToPrinter
			List<NotPrintedOrderDetailItemsToPrinter> obj = getNotPrintedOrderDetailItemsToPrinter(em,
					orderDetailItemObj.getId());
			if (obj != null) {
				orderDetailItemObj.setNotPrintedOrderDetailItemsToPrinter(obj);
			}

		}
		return orderDetailItemsList;

	}

	/**
	 * Gets the not printed order detail items to printer.
	 *
	 * @param em
	 *            the em
	 * @param oDIId
	 *            the o DI id
	 * @return the not printed order detail items to printer
	 */
	private List<NotPrintedOrderDetailItemsToPrinter> getNotPrintedOrderDetailItemsToPrinter(EntityManager em,
			String oDIId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<NotPrintedOrderDetailItemsToPrinter> criteria = builder
				.createQuery(NotPrintedOrderDetailItemsToPrinter.class);
		Root<NotPrintedOrderDetailItemsToPrinter> orderDetailItem = criteria
				.from(NotPrintedOrderDetailItemsToPrinter.class);
		TypedQuery<NotPrintedOrderDetailItemsToPrinter> query = em.createQuery(criteria.select(orderDetailItem).where(
				builder.equal(orderDetailItem.get(NotPrintedOrderDetailItemsToPrinter_.orderDetailItemsId), oDIId)));
		return query.getResultList();
	}

	/**
	 * Gets the order details item for order id and location id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @param locationId
	 *            the location id
	 * @return the order details item for order id and location id
	 */
	List<OrderDetailItem> getOrderDetailsItemForOrderIdAndlocationId(EntityManager em, String orderHeaderId,
			String locationId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();

		OrderDetailStatus orderDetailStatusRemoved = getOrderDetailStatusByName(em, locationId, "Item Removed");
		OrderDetailStatus orderDetailStatusCancled = getOrderDetailStatusByName(em, locationId, "Recall");

		CriteriaQuery<OrderDetailItem> criteria = builder.createQuery(OrderDetailItem.class);
		Root<OrderDetailItem> orderDetailItem = criteria.from(OrderDetailItem.class);
		TypedQuery<OrderDetailItem> query = em.createQuery(criteria.select(orderDetailItem).where(
				builder.equal(orderDetailItem.get(OrderDetailItem_.orderHeaderId), orderHeaderId),
				builder.notEqual(orderDetailItem.get(OrderDetailItem_.orderDetailStatusId),
						orderDetailStatusRemoved.getId()),
				builder.notEqual(orderDetailItem.get(OrderDetailItem_.orderDetailStatusId),
						orderDetailStatusCancled.getId())));
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();
		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList) {
			orderDetailItemObj.setOrderDetailAttributes(
					getOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId()));
		}
		return orderDetailItemsList;

	}

	/**
	 * Gets the order header to seat detail for order id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the order header to seat detail for order id
	 */
	List<OrderHeaderToSeatDetail> getOrderHeaderToSeatDetailForOrderId(EntityManager em, String orderHeaderId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeaderToSeatDetail> criteria = builder.createQuery(OrderHeaderToSeatDetail.class);
		Root<OrderHeaderToSeatDetail> criteria1 = criteria.from(OrderHeaderToSeatDetail.class);
		TypedQuery<OrderHeaderToSeatDetail> query = em.createQuery(criteria.select(criteria1)
				.where(builder.equal(criteria1.get(OrderHeaderToSeatDetail_.orderHeaderId), orderHeaderId)));

		try {
			return query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
		}
		return null;

	}

	/**
	 * Gets the order detail attribute for order detail item id.
	 *
	 * @param em
	 *            the em
	 * @param orderDetailItemId
	 *            the order detail item id
	 * @return the order detail attribute for order detail item id
	 */
	List<OrderDetailAttribute> getOrderDetailAttributeForOrderDetailItemId(EntityManager em, String orderDetailItemId) {
		String queryString = "select odi from OrderDetailAttribute odi where odi.orderDetailItemId= ? order by id asc ";
		TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class).setParameter(1,
				orderDetailItemId);
		return query.getResultList();

	}

	/**
	 * Gets the order payment details to sales tax for order payment details id.
	 *
	 * @param em
	 *            the em
	 * @param orderPaymentDetailsId
	 *            the order payment details id
	 * @return the order payment details to sales tax for order payment details
	 *         id
	 */
	List<OrderPaymentDetailsToSalesTax> getOrderPaymentDetailsToSalesTaxForOrderPaymentDetailsId(EntityManager em,
			String orderPaymentDetailsId) {
		String queryString = "select odi from OrderPaymentDetailsToSalesTax odi where odi.orderPaymentDetailsId= ? order by id asc ";
		TypedQuery<OrderPaymentDetailsToSalesTax> query = em
				.createQuery(queryString, OrderPaymentDetailsToSalesTax.class).setParameter(1, orderPaymentDetailsId);
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			logger.severe(e);
		}
		return null;

	}

	/**
	 * Gets the order detail attribute for order detail item id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @param statusId
	 *            the status id
	 * @return the order detail attribute for order detail item id
	 */
	List<OrderDetailAttribute> getOrderDetailAttributeForOrderDetailItemId(EntityManager em, String orderHeaderId,
			String statusId) {
		String queryString = "select odi from OrderDetailAttribute odi where odi.orderDetailItemId= ? and odi.orderDetailStatusId not in ("
				+ statusId + ") ";
		TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class).setParameter(1,
				orderHeaderId);
		return query.getResultList();
	}

	/**
	 * Should allow adding order.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return true, if successful
	 */
	private boolean shouldAllowAddingOrder(EntityManager em, @PathParam("locationId") String locationId) {

		String queryString = "SELECT o.* FROM order_header o LEFT JOIN order_status os on o.order_status_id = os.id Where o.locations_Id = ? "
				+ " and o.is_order_reopened =0 AND os.name NOT IN ('Ready to Order','Reopen','Void Order ','Order Suspend','Cancel Order')";

		Query q = em.createNativeQuery(queryString).setParameter(1, locationId);
		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();
		if (l == null || l.size() == 0 || l.isEmpty()) {
			return true;
		}
		return false;

	}

	/**
	 * Check order exist.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @return true, if successful
	 */
	private boolean checkOrderExist(EntityManager em, String orderId) {

		String queryString = "SELECT o.* FROM order_header o LEFT JOIN order_status os on o.order_status_id = os.id Where o.id = ? "
				+ " and o.is_order_reopened =0 AND os.name NOT IN ('Ready to Order','Reopen','Void Order ', 'Check Presented','Cancel Order')";

		Query q = em.createNativeQuery(queryString).setParameter(1, orderId);
		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();
		if (l != null && l.size() > 0) {
			return true;
		}
		return false;

	}

	/**
	 * Check order exist for update reservation status.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @return true, if successful
	 */
	private boolean checkOrderExistForUpdateReservationStatus(EntityManager em, String orderId) {

		String queryString = "SELECT o.* FROM order_header o left join locations l on l.id=o.locations_id LEFT JOIN order_status os on o.order_status_id = os.id Where o.id = ? "
				+ " and o.is_order_reopened =0 AND os.name  IN ('Order Paid','Ready to Order') and (l.locations_id='0' or l.locations_id is null) ";

		Query q = em.createNativeQuery(queryString).setParameter(1, orderId);
		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();
		if (l != null && l.size() > 0) {
			return true;
		}
		return false;

	}

	/**
	 * Gets the all order payment details by user id location date and updated.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param updateDate
	 *            the update date
	 * @return the all order payment details by user id location date and
	 *         updated
	 */
	public List<OrderHeader> getAllOrderPaymentDetailsByUserIdLocationDateAndUpdated(HttpServletRequest httpRequest,
			EntityManager em, String userId, String locationId, String date, String updateDate) {

		int isManager = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = new ArrayList<OrderPaymentDetail>();
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();
		OrderHeader header = new OrderHeader();

		String query = "SELECT count(*) FROM `users_to_roles` utr left join roles r on r.id=utr.roles_id  "
				+ " where utr.users_id = ? and r.role_name in ('ALL','POS Supervisor')";
		TimezoneTime time = new TimezoneTime();
		updateDate = time.getDateAccordingToGMTForConnection(em, updateDate, locationId);

		Object result = em.createNativeQuery(query).setParameter(1, userId).getSingleResult();

		if (result != null) {
			// if this has primary key not 0
			if (((BigInteger) result).intValue() > 0) {
				isManager = 1;
			}
		} else {
			logger.severe(httpRequest, "Query result is null or not an integer: " + result);
		}

		// TODO - Ankur - what should happen if there is an exception in the
		// block above?
		if (isManager == 0) { // not manager

			// order source production excluded from list
			String queryString = "SELECT   " + objectsWithColumnStr
					+ "  FROM order_header oh left join order_payment_details opd on oh.id=opd.order_header_id "
					+ " join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id "
					+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
					+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
					+ " left join payment_method pm on opd.payment_method_id= pm.id "
					+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
					+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ " left JOIN order_status os on oh.order_status_id = os.id "
					+ "left JOIN order_source_group osg on os.order_source_group_id = osg.id "
					+ " left JOIN order_source osou on oh.order_source_id = osou.id "
					+ " WHERE  ( l.locations_id  in (select location0_.id as id1 from locations location0_     "
					+ " where location0_.locations_id=?) or oh.locations_id=?) "
					+ " and (opd.order_header_id in(select opd1.order_header_id as opd1_order_header_id "
					+ " from order_payment_details opd1 join payment_transaction_type ptt1 on ptt1.id=opd1.payment_transaction_type_id  where "
					+ " ptt1.name = 'Auth' and opd1.date = ? ) " + " or oh.date = ? "
					+ " or (date_format(str_to_date(CONVERT_TZ( oh.schedule_date_time, '+00:00', "
					+ " SUBSTRING( t.display_name, LOCATE( 'GMT', t.display_name ) +3 ) ) , '%Y-%m-%d'), '%Y-%m-%d') =?) )  "
					+ "  and os.name not in ('Void Order ','Cancel Order') and  "
					+ " and osou.name != 'Production'  and oh.sub_total != '0.00'" + "and  oh.created_by=? "
					+ "and oh.updated >= ? order by oh.id,opd.id asc ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
					.setParameter(2, locationId).setParameter(3, date).setParameter(4, date).setParameter(5, date)
					.setParameter(6, userId).setParameter(7, updateDate).getResultList();
			int index = 0;
			orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
					index);

		} else { // manager

			// TODO - Ankur, if the difference is only in what query is used,
			// then result processing could be a common method for the block
			// above and this one

			// order source production excluded from list
			String queryString = "SELECT  " + objectsWithColumnStr
					+ "  FROM order_header oh left join order_payment_details opd on oh.id=opd.order_header_id "
					+ " join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id "
					+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
					+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
					+ " left join payment_method pm on opd.payment_method_id= pm.id "
					+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
					+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ " left JOIN order_status os on oh.order_status_id = os.id  "
					+ " left JOIN order_source osou on oh.order_source_id = osou.id "
					+ "left JOIN order_source_group osg on os.order_source_group_id = osg.id "
					+ " WHERE  ( l.locations_id  in (select location0_.id as id1 from locations location0_     "
					+ " where location0_.locations_id=?) or oh.locations_id=?) "
					+ " and (opd.order_header_id in(select opd1.order_header_id as opd1_order_header_id "
					+ " from order_payment_details opd1 join payment_transaction_type ptt1 on ptt1.id=opd1.payment_transaction_type_id  where "
					+ " ptt1.name = 'Auth' and opd1.date = ? ) " + " or oh.date = ? "
					+ " or (date_format(str_to_date(CONVERT_TZ( oh.schedule_date_time, '+00:00', "
					+ " SUBSTRING( t.display_name, LOCATE( 'GMT', t.display_name ) +3 ) ) , '%Y-%m-%d'), '%Y-%m-%d') =?) )  "
					+ "  and os.name not in ('Void Order ','Cancel Order ') "
					+ "and osou.name != 'Production'  and  oh.sub_total != '0.00' and oh.updated >= ? order by oh.id,opd.id asc ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
					.setParameter(2, locationId).setParameter(3, date).setParameter(4, date).setParameter(5, date)
					.setParameter(6, updateDate).getResultList();

			int index = 0;
			orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
					index);
		}
		return orderHeaders;

	}

	/**
	 * Gets the all orders with user by location and updated.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param updated
	 *            the updated
	 * @param locationId
	 *            the location id
	 * @return the all orders with user by location and updated
	 * @throws Exception
	 *             the exception
	 */
	public String getAllOrdersWithUserByLocationAndUpdated(HttpServletRequest httpRequest, EntityManager em,
			String updated, String locationId) throws Exception {

		List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
		TimezoneTime time = new TimezoneTime();
		updated = time.getDateAccordingToGMTForConnection(em, updated, locationId);
		// for take out order
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getAllOpenOrdersWithUserForLocation(?)")
				.setParameter(1, locationId).getResultList();
		for (Object[] objRow : resultList) {

			// if this has primary key not 0
			OrderWithUsers orderWithUsers = new OrderWithUsers();
			OrderHeader orderHeader = new OrderHeader();

			// ------------------ Add address object for address shipping id and
			// address billing id ---
			Address addressShipping = new Address();
			Address addressBilling = new Address();
			addressShipping.setAddressByResultSet(objRow, 151);
			addressBilling.setAddressByResultSet(objRow, 164);

			objRow[151] = addressShipping;
			objRow[164] = addressBilling;
			int rowOrderCount = 67;

			orderHeader.setOrderHeaderByResultSet(objRow, rowOrderCount + 7);
			orderWithUsers.setOrderHeader(orderHeader);
			User user = new User();
			if (objRow[rowCount] != null)
				user = user.getUserByResultSetForOrder(objRow, user, rowCount);
			if (user.getId() != null) {
				orderWithUsers.setUser(user);
			}

			orderWithUsers.setOrderHeader(orderHeader);
			orderWithUsersList.add(orderWithUsers);

		}

		return new JSONUtility(httpRequest).convertToJsonString(orderWithUsersList);

	}

	/**
	 * Gets the payment transaction type by location id and name.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the payment transaction type by location id and name
	 */
	public PaymentTransactionType getPaymentTransactionTypeBylocationIdAndName(EntityManager em,
			@PathParam("locationId") String locationId, @PathParam("name") String name) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
		Root<PaymentTransactionType> paymentTransactionType = criteria.from(PaymentTransactionType.class);
		TypedQuery<PaymentTransactionType> query = em.createQuery(criteria.select(paymentTransactionType).where(
				builder.equal(paymentTransactionType.get(PaymentTransactionType_.locationsId), locationId),
				builder.notEqual(paymentTransactionType.get(PaymentTransactionType_.status), "D"),
				builder.equal(paymentTransactionType.get(PaymentTransactionType_.name), name)));
		return query.getSingleResult();
	}

	/**
	 * Gets the all order payment details by user id location date.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the all order payment details by user id location date
	 */
	public List<OrderHeader> getAllOrderPaymentDetailsByUserIdLocationDate(HttpServletRequest httpRequest,
			EntityManager em, String userId, String locationId, Date date) {

		int isManager = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = null;
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();

		OrderHeader header = new OrderHeader();

		String query = "SELECT count(*) FROM `users_to_roles` utr left join roles "
				+ "r on r.id=utr.roles_id where utr.users_id = ? and r.role_name in ('ALL','POS Supervisor')";

		Object result = em.createNativeQuery(query).setParameter(1, userId).getSingleResult();

		if (result != null) {
			// if this has primary key not 0
			if (((BigInteger) result).intValue() > 0) {
				isManager = 1;
			}
		} else {
			logger.severe(httpRequest, "Could not get count of users to roles");
		}

		// Change by Apoorva for matching date format issue after ankur fix july
		// 21,2015 sprint 7 release
		SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateChange = dmyFormat.format(date);
		if (isManager == 0) {

			// order source production excluded from list
			// not manager
			String queryString = "SELECT  " + objectsWithColumnStr
					+ "  FROM order_header oh left join order_payment_details opd on oh.id=opd.order_header_id "
					+ " join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id "
					+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
					+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
					+ " left join payment_method pm on opd.payment_method_id= pm.id "
					+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
					+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ " left JOIN order_status os on oh.order_status_id = os.id  "
					+ " left JOIN order_source osou on oh.order_source_id = osou.id "
					+ " left JOIN order_source_group osg on os.order_source_group_id = osg.id "
					+ " WHERE  ( l.locations_id  in (select location0_.id as id1 from locations location0_     "
					+ " where location0_.locations_id=?) or oh.locations_id=?) "
					+ " and (opd.order_header_id in(select opd1.order_header_id as opd1_order_header_id "
					+ " from order_payment_details opd1 join payment_transaction_type ptt1 on ptt1.id=opd1.payment_transaction_type_id  where "
					+ " ptt1.name = 'Auth' and opd1.date = '2015-03-30' ) " + " or oh.date = ? "
					+ " or (date_format(str_to_date(CONVERT_TZ( oh.schedule_date_time, '+00:00', "
					+ " SUBSTRING( t.display_name, LOCATE( 'GMT', t.display_name ) +3 ) ) , '%Y-%m-%d'), '%Y-%m-%d') =?) )  "
					+ " and os.name not in ('Void Order ','Cancel Order ') "
					+ "and osou.name != 'Production' and  oh.sub_total != '0.00' "
					+ "and oh.created_by=? order by oh.id,opd.id asc ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
					.setParameter(2, locationId).setParameter(3, dateChange).setParameter(4, dateChange)
					.setParameter(5, userId).getResultList();

			int index = 0;
			orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
					index);

		} else {

			// order source production excluded from list
			// manager
			String queryString = "SELECT  " + objectsWithColumnStr
					+ "  FROM order_header oh left join order_payment_details opd on oh.id=opd.order_header_id "
					+ " join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id "
					+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
					+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
					+ " left join payment_method pm on opd.payment_method_id= pm.id "
					+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
					+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ " left JOIN order_status os on oh.order_status_id = os.id "
					+ " left JOIN order_source osou on oh.order_source_id = osou.id "
					+ " left JOIN order_source_group osg on os.order_source_group_id = osg.id "
					+ " WHERE  ( l.locations_id  in (select location0_.id as id1 from locations location0_     "
					+ " where location0_.locations_id=?) or oh.locations_id=?) "
					+ " and (opd.order_header_id in(select opd1.order_header_id as opd1_order_header_id "
					+ " from order_payment_details opd1 join payment_transaction_type ptt1 on ptt1.id=opd1.payment_transaction_type_id  where "
					+ " ptt1.name = 'Auth' and opd1.date = '2015-03-30' ) " + " or oh.date = ? "
					+ " or (date_format(str_to_date(CONVERT_TZ( oh.schedule_date_time, '+00:00', "
					+ " SUBSTRING( t.display_name, LOCATE( 'GMT', t.display_name ) +3 ) ) , '%Y-%m-%d'), '%Y-%m-%d') =?) )  "
					+ " and os.name not in ('Void Order','Cancel Order')  "
					+ " and osou.name != 'Production' and oh.sub_total != '0.00' order by oh.id,opd.id asc ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
					.setParameter(2, locationId).setParameter(3, dateChange).setParameter(4, dateChange)
					.getResultList();
			int index = 0;
			orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
					index);

		}

		return orderHeaders;
	}

	/**
	 * Gets the reservation by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param locationId
	 *            the location id
	 * @return the reservation by name and location id
	 */
	private ReservationsStatus getReservationByNameAndlocationId(EntityManager em, String name, String locationId) {
		ReservationsStatus reservationsStatus = new ReservationsStatus();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
		Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
		TypedQuery<ReservationsStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsStatus_.locationsId), locationId),
						builder.equal(r.get(ReservationsStatus_.name), name),
						builder.notEqual(r.get(ReservationsStatus_.status), "D")));
		reservationsStatus = query.getSingleResult();
		return reservationsStatus;
	}

	/**
	 * Gets the order header by reservation id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param orderStatusId
	 *            the order status id
	 * @return the order header by reservation id
	 */
	private OrderHeader getOrderHeaderByReservationId(HttpServletRequest httpRequest, EntityManager em, String id,
			String orderStatusId) {
		OrderHeader resultSet = null;
		try {
			String queryString = "select oh from OrderHeader oh where oh.reservationsId = ? and oh.orderStatusId !=? ";
			TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class).setParameter(1, id)
					.setParameter(2, orderStatusId);
			resultSet = query.getSingleResult();
		} catch (Exception e) {
			// todo shlok need
			// hadle proper exception
			logger.severe(httpRequest, e);
		}

		return resultSet;
	}

	/**
	 * Gets the order source group by name and location id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param locationId
	 *            the location id
	 * @return the order source group by name and location id
	 */
	private OrderSourceGroup getOrderSourceGroupByNameAndlocationId(HttpServletRequest httpRequest, EntityManager em,
			String name, String locationId) {
		OrderSourceGroup resultSet = null;
		try {
			String queryString = "select os from OrderSourceGroup os  where os.name ='" + name
					+ "' and  os.locationsId= '" + locationId + "'";
			TypedQuery<OrderSourceGroup> query = em.createQuery(queryString, OrderSourceGroup.class);
			resultSet = query.getSingleResult();
		} catch (Exception e) {
			// todo shlok need
			// hadle proper exception

			logger.severe(httpRequest, e);
		}

		return resultSet;
	}

	/**
	 * Gets the reasons by name and location id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param locationId
	 *            the location id
	 * @param reasonTypeId
	 *            the reason type id
	 * @return the reasons by name and location id
	 */
	private Reasons getReasonsByNameAndlocationId(HttpServletRequest httpRequest, EntityManager em, String name,
			String locationId, String reasonTypeId) {
		Reasons resultSet = null;
		try {
			String queryString = "select os from Reasons os  where os.name =? and os.locationsId= ? and os.reasonTypeId=? ";
			TypedQuery<Reasons> query = em.createQuery(queryString, Reasons.class).setParameter(1, name)
					.setParameter(2, locationId).setParameter(3, reasonTypeId);
			resultSet = query.getSingleResult();
		} catch (Exception e) {
			// todo shlok need
			// hadle proper exception
			logger.severe(httpRequest, e);
		}

		return resultSet;
	}

	/**
	 * Gets the reasons type by name and location id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @return the reasons type by name and location id
	 */
	private ReasonType getReasonsTypeByNameAndlocationId(HttpServletRequest httpRequest, EntityManager em,
			String name) {
		ReasonType resultSet = null;
		try {
			String queryString = "select os from ReasonType os  where os.name ='" + name + "'";
			TypedQuery<ReasonType> query = em.createQuery(queryString, ReasonType.class);
			resultSet = query.getSingleResult();
		} catch (Exception e) {
			// todo shlok need
			// hadle proper exception
			logger.severe(httpRequest, e);
		}

		return resultSet;
	}

	/**
	 * Gets the order detail item list by order id.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @return the order detail item list by order id
	 */
	private List<OrderDetailItem> getOrderDetailItemListByOrderId(EntityManager em, String orderId) {
		List<OrderDetailItem> resultSet = null;
		String queryString = "select odi from OrderDetailItem odi  where odi.orderHeaderId = ?";
		TypedQuery<OrderDetailItem> query = em.createQuery(queryString, OrderDetailItem.class);
		query.setParameter(1, orderId);
		resultSet = query.getResultList();

		return resultSet;
	}

	/**
	 * Update order and source.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @param orderSourceId
	 *            the order source id
	 * @param reservationId
	 *            the reservation id
	 * @param updatedBy
	 *            the updated by
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 * @param cancelOrder
	 *            the cancel order
	 * @return the int[]
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public String[] updateOrderAndSource(HttpServletRequest httpRequest, EntityManager em, String orderId,
			String orderSourceId, String reservationId, String updatedBy, String locationId, String userId,
			boolean cancelOrder) throws NirvanaXPException {
		// todo shlok need
		// modularise method
		OrderHeader header = null;
		String[] reservationStatusId = new String[6]; // for adding old and new
		// reservation statusID
		if (orderId != null) {
			header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em, OrderHeader.class, orderId);

			// updating old reservation object
			Reservation oldReservation = new CommonMethods().getReservationById(httpRequest, em,
					header.getReservationsId());

			Reservation newReservation = new CommonMethods().getReservationById(httpRequest, em, reservationId);
			OrderHeader newOrderHeader = null;
			// getting order source
			OrderSourceGroup orderSourceGroup = null;
			orderSourceGroup = getOrderSourceGroupByNameAndlocationId(httpRequest, em, "In Store", locationId);
			if (orderSourceGroup != null) {
				// getting status
				OrderStatus orderStatus = null;
				ReasonType reasonType = null;
				if (cancelOrder) {
					orderStatus = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em,
							"Cancel Order", locationId, orderSourceGroup.getId());
					reasonType = getReasonsTypeByNameAndlocationId(httpRequest, em, "Cancel Reasons");
				} else {
					orderStatus = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em,
							"Void Order", locationId, orderSourceGroup.getId());
					reasonType = getReasonsTypeByNameAndlocationId(httpRequest, em, "Void Reasons");
				}
				if (newReservation != null) {
					newOrderHeader = getOrderHeaderByReservationId(httpRequest, em, newReservation.getId(),
							orderStatus.getId());

				}

				// updating current order
				// for tab order case newOrderHeader has some value
				if (newOrderHeader != null) {
					List<OrderDetailItem> orderDetailItems = getOrderDetailItemListByOrderId(em, header.getId());
					if (orderDetailItems == null || orderDetailItems.size() == 0) {
						newOrderHeader.setReservationsId(reservationId);
						newOrderHeader.setOrderSourceId(orderSourceId);
						newOrderHeader.setUpdated(new Date(updatedTime()));
						newOrderHeader.setUpdatedBy(updatedBy);
						newOrderHeader.setUsersId(userId);
						newOrderHeader.setLocationsId(header.getLocationsId());
						newOrderHeader.setIsTabOrder(0);

						newOrderHeader = em.merge(newOrderHeader);
						reservationStatusId[3] = newOrderHeader.getOrderStatusId();
						reservationStatusId[4] = newOrderHeader.getId();

						// insert into order history
						new InsertIntoHistory().insertOrderIntoHistory(httpRequest, newOrderHeader, em);

						if (orderStatus != null) {
							// getting reason name

							Reasons reasons = getReasonsByNameAndlocationId(httpRequest, em, "Seating Tab Order",
									locationId, reasonType.getId());
							if (reasons != null) {

								header.setUpdated(new Date(updatedTime()));
								header.setUpdatedBy(updatedBy);
								header.setOrderStatusId(orderStatus.getId());
								header.setVoidReasonId(reasons.getId());
								header.setVoidReasonName(reasons.getName());

								reservationStatusId[5] = header.getId();

							} else {

								throw new NirvanaXPException(new NirvanaServiceErrorResponse(
										MessageConstants.ERROR_CODE_VOID_REASON_NOT_PRESENT_EXCEPTION,
										MessageConstants.ERROR_MESSAGE_VOID_REASON_NOT_PRESENT_DISPLAY_MESSAGE, null));
							}
						} else {

							throw new NirvanaXPException(new NirvanaServiceErrorResponse(
									MessageConstants.ERROR_CODE_VOID_ORDER_STATUS_NOT_PRESENT_EXCEPTION,
									MessageConstants.ERROR_MESSAGE_VOID_ORDER_STATUS_NOT_PRESENT_DISPLAY_MESSAGE,
									null));
						}
					} else {

						throw new NirvanaXPException(new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_ORDER_HEADER_CONTAINS_ITEMS_EXCEPTION,
								MessageConstants.ERROR_MESSAGE_ORDER_HEADER_CONTAINS_ITEMS_DISPLAY_MESSAGE, null));
					}
				} else {
					header.setReservationsId(reservationId);
					header.setOrderSourceId(orderSourceId);
					header.setUpdated(new Date(updatedTime()));
					header.setUpdatedBy(updatedBy);
					header.setUsersId(userId);

					reservationStatusId[3] = header.getOrderStatusId();
					// insert into order history
					new InsertIntoHistory().insertOrderIntoHistory(httpRequest, header, em);
				}
			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_ORDER_SOURCE_GROUP_NOT_PRESENT_EXCEPTION,
						MessageConstants.ERROR_MESSAGE_ORDER_SOURCE_GROUP_NOT_PRESENT_DISPLAY_MESSAGE, null));
			}

			if (oldReservation.getReservationsType() != null) {
				ReservationsType oldReservationsType = oldReservation.getReservationsType();

				// updating status depending upon reservation type
				if (oldReservationsType.getName().equals("Walk in")) {
					oldReservation = updateReservationStatus(httpRequest, em, "Void Walkin", oldReservation, locationId,
							updatedBy);

					if (oldReservation.getReservationSlotId() != null && oldReservation.getReservationSlotId() != 0) {
						ManageSlotsUtils.updateReservationSlotCurrentActiveReservationCount(httpRequest, em,
								oldReservation.getReservationSlotId(), oldReservation, 0, false);
					}

				} else if (oldReservationsType.getName().equals("Waitlist")) {
					oldReservation = updateReservationStatus(httpRequest, em, "Waiting", oldReservation, locationId,
							updatedBy);
				} else {
					oldReservation = updateReservationStatus(httpRequest, em, "Confirmed", oldReservation, locationId,
							updatedBy);
				}
				if (oldReservation.getReservationsStatus() != null) {
					reservationStatusId[0] = oldReservation.getReservationsStatus().getId(); // adding
																								// old
																								// index
				}
			}

			if (newReservation.getReservationsType() != null) {
				// updating status depending upon reservation type
				newReservation = updateReservationStatus(httpRequest, em, "Check In", newReservation, locationId,
						updatedBy);
				if (newReservation.getReservationsStatus() != null) {
					reservationStatusId[1] = newReservation.getReservationsStatus().getId(); // adding
																								// index
				}
				header.setPointOfServiceCount(newReservation.getPartySize());
				reservationStatusId[2] = "" + newReservation.getPartySize();

				header = em.merge(header);
				// insert into order history
				new InsertIntoHistory().insertOrderIntoHistory(httpRequest, header, em);
			}
		} else {
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDERID_0_EXCEPTION,
							MessageConstants.ERROR_MESSAGE_ORDERID_0_EXCEPTION_DISPLAY_MESSAGE, null));
		}

		return reservationStatusId;
	}

	/**
	 * Update order status to paid.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @param updatedBy
	 *            the updated by
	 * @param locationId
	 *            the location id
	 * @param merchantId
	 *            the merchant id
	 * @param cashOnDelivery
	 *            the cash on delivery
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeaderWithInventoryPostPacket updateOrderStatusToPaid(HttpServletRequest httpRequest, EntityManager em,
			String orderId, String orderSourceGroupId, String updatedBy, String locationId, int merchantId,
			int cashOnDelivery, String ccArray) throws Exception {
		OrderHeader header = null;
		InventoryPostPacket ipp = null;
		if (orderId != null) {
			header = getOrderById(em, orderId);
			logger.severe(header.getCalculatedDiscountValue()
					+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			logger.severe(
					header.getDiscountsValue() + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup",
					em, OrderSourceGroup.class, orderSourceGroupId);
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, header.getOrderStatusId());
			String orderStatusName = orderStatus.getName();
			OrderStatus orderStatusToUpdate = null;

			if (orderSourceGroup.getName().equals("In Store")) {
				if (orderStatusName.equals("Order Ahead Received") || orderStatusName.equals("Order Ahead Placed")
						|| orderStatusName.equals("Order Ahead Check Presented")) {
					orderStatusToUpdate = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em,
							"Order Ahead Paid", locationId, orderSourceGroupId);
				} else {
					orderStatusToUpdate = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em,
							"Order Paid", locationId, orderSourceGroupId);
					header.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
				}

			} else {
				if (orderStatusName.equals("Order Ahead Received") || orderStatusName.equals("Order Ahead Placed")
						|| orderStatusName.equals("Order Ahead Check Presented")) {

					if (cashOnDelivery == 1) {
						orderStatusToUpdate = new CommonMethods()
								.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em,
										"Order Ahead Cash On Delivery", locationId, orderSourceGroupId);
					} else {
						orderStatusToUpdate = new CommonMethods()
								.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "Order Ahead Paid",
										locationId, orderSourceGroupId);
					}
				} else {
					if (cashOnDelivery == 1) {
						orderStatusToUpdate = new CommonMethods()
								.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "Cash On Delivery",
										locationId, orderSourceGroupId);
					} else {
						orderStatusToUpdate = new CommonMethods()
								.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "Order Paid", locationId,
										orderSourceGroupId);
					}

				}
			}

			// updating current order
			header.setOrderStatusId(orderStatusToUpdate.getId());
			header.setUpdatedBy(updatedBy);

			em.merge(header);
			// managing inventory for online order
			ipp = manageInventoryForOrder(httpRequest, header, em, locationId, header, true, true, orderStatus,
					merchantId, null);

			insertIntoOrderStatusHistory(em, header);

			// insert into order history
			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, header, em);

			try {
				// insert into order history
				ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
				// 0 because mail sending from dine in
				String data = "";
				if (cashOnDelivery == 1) {
					data = receiptPDFFormat.createReceiptPDFStringForCOD(em, httpRequest, header.getId(), 1, true)
							.toString();
				} else {
					data = receiptPDFFormat.createReceiptPDFString(em, httpRequest, header.getId(), 1, true, false)
							.toString();
				}
				// Send email functionality :- printing order
				// number instead of orderID :- By AP 2015-12-29

				EmailTemplateKeys.sendOrderReceivedEmailToCustomer(httpRequest, em, locationId, header.getUsersId(),
						header.getUpdatedBy(), data, EmailTemplateKeys.ORDER_RECEIVED, header.getOrderNumber(), null,
						ccArray);

			} catch (Exception e) {
				logger.severe(httpRequest, e, "Could not send email due to configuration mismatch");
			}

			// removed the logic of mail sending on order paid by apoorv
		} else {
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDERID_0_EXCEPTION,
							MessageConstants.ERROR_MESSAGE_ORDERID_0_EXCEPTION_DISPLAY_MESSAGE, null));
		}

		return new OrderHeaderWithInventoryPostPacket(header, ipp);
	}

	/**
	 * Send EOD settledment mail.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @param headers
	 *            the headers
	 * @param email
	 *            the email
	 * @throws Exception
	 *             the exception
	 */

	public void sendEODMailForTipSettlementFromEOD(HttpServletRequest httpRequest, EntityManager em, String locationId,
			BatchDetail activeBatchDetail, String email) throws Exception {

		// clean up this method like the method below this one

		String emailBody = "";

		List<String> list = null;
		if (activeBatchDetail != null) {
			// get user flow
			// making email body and Send Email to user
			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
			String fromDate = dateFormat.format(new Date(activeBatchDetail.getStartTime()));
			String toDate = dateFormat.format(new Date(activeBatchDetail.getCloseTime()));

			TimezoneTime time = new TimezoneTime();
			String batchStartDate = time.getDateTimeFromGMTToLocation(em,
					dateFormat.format(activeBatchDetail.getStartTime()), locationId);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse(batchStartDate));
			cal.add(Calendar.DATE, 1);
			String convertedDate = "8 am, " + "(" + dateFormat1.format(cal.getTime()) + ")";

			try {
				String queryString = " SELECT concat(u.first_name,' ' , u.last_name ,' (' , u.username,') ' ) "
						+ " FROM  clock_in_clock_out cl join users u on u.id=cl.users_id "
						+ " where cl.clock_in between '" + fromDate + "' " + " and '" + toDate
						+ "' and clock_out_operation_id=0 ";
				list = (List<String>) (em.createNativeQuery(queryString).getResultList());
				logger.severe(queryString);
				if (list != null && list.size() > 0) {
					emailBody = (receiptPDFFormat.sendEODMailForTipSettlementFromEOD(em, httpRequest, list,
							activeBatchDetail.getId(), locationId).toString());

					try {
						if (emailBody != null) {
							EmailTemplateKeys.sendEODTipSettlementMailToUser(httpRequest, em, locationId,
									EmailTemplateKeys.EOD_TIP_SETTLEDMENT_BODY_STRING, emailBody, email, convertedDate);
						}

					} catch (Exception e) {
						// todo shlok need
						// modularise method
						logger.severe(e);
					}
				}
			} catch (Exception e) {

				logger.severe(e);
			}
		} else {
			logger.severe("No Order Result Found for EOD Tip Email");
		}

	}

	public void sendEODMailForTipSettlementFromCronJob(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String gmtStartDate, String gmtEndDate, String locationStartDate) throws ParseException {
		String queryString = " SELECT concat(u.first_name,' ' , u.last_name ,' (' , u.username,') ' ) "
				+ " FROM  clock_in_clock_out cl join users u on u.id=cl.users_id "
				+ " where cl.clock_in between ? and ? and clock_out_operation_id=0 ";

		@SuppressWarnings("unchecked")
		List<String> list = em.createNativeQuery(queryString).setParameter(1, gmtStartDate).setParameter(2, gmtEndDate)
				.getResultList();

		if (list != null && list.size() > 0) {
			StringBuilder emailContent = ReceiptPDFFormat.generateEmailContentForEmployeesStillClockedIn(list,
					locationStartDate, locationId);

			// prepare date values to be sent in email
			DateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse(locationStartDate));

			String convertedDate = "8 am, " + "(" + newDateFormat.format(cal.getTime()) + ")";

			// the null argument passed to method below will cause email to be
			// sent out to everyone with specific roles at the location
			EmailTemplateKeys.sendEODTipSettlementMailToUser(httpRequest, em, locationId,
					EmailTemplateKeys.EOD_TIP_SETTLEDMENT_BODY_STRING, emailContent.toString(), null, convertedDate);

		}
	}

	/**
	 * Tip pooling calculation.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @param headers
	 *            the headers
	 * @throws Exception
	 *             the exception
	 */
	public void tipPoolingCalculation(HttpServletRequest httpRequest, EntityManager em, String userId,
			String locationId, BatchDetail activeBatchDetail, List<OrderHeader> headers) throws Exception {
		if (headers != null && headers.size() > 0) {
			List<TipPoolRules> poolRulesList = null;

			try {
				String queryString = "select tr from TipPoolRules tr where  tr.locationId ='" + locationId
						+ "' and tr.status not in ('D')";
				TypedQuery<TipPoolRules> query = em.createQuery(queryString, TipPoolRules.class);

				poolRulesList = query.getResultList();
			} catch (Exception e) {
				logger.severe("No result Fopund for Tip Pool Rules for location Id " + locationId);
			}

			try {

				// fill employee operational hour with SP
				calculateEmployeeOperationalHours(httpRequest, em, locationId, userId, activeBatchDetail);

				if (poolRulesList != null && poolRulesList.size() > 0) {
					calculateTipPoolOnTipPoolRules(httpRequest, em, userId, locationId, activeBatchDetail,
							poolRulesList, headers);
				} else {
					calculateTipPoolOnPreassignServer(httpRequest, em, userId, locationId, activeBatchDetail, headers);
				}
			} catch (Exception e) {

				logger.severe(e);
			}

		} else {
			logger.severe("No Order Result Found for Tip Pooling Calculation");
		}

	}

	private void calculateTipPoolOnTipPoolRules(HttpServletRequest httpRequest, EntityManager em, String userId,
			String locationId, BatchDetail activeBatchDetail, List<TipPoolRules> poolRulesList,
			List<OrderHeader> orderHeaderList) {
		// todo shlok need
		// modularise method

		try {
			NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();

			String name = "Direct";
			TipClass directTipClass = getTipClassByName(httpRequest, em, name);
			name = "Indirect";
			TipClass indirectTipClass = getTipClassByName(httpRequest, em, name);
			HashMap<String, TipPoolingByPool> tipPoolingByPoolHashMap = new HashMap<String, TipPoolingByPool>();
			List<OperationalShiftSchedule> operationalShiftList = niravanaXpTippingBean
					.getAllOperationalShiftSchedule(httpRequest, em, locationId);
			for (OrderHeader order : orderHeaderList) {
				if (order.getSectionId() == null) {
					order.setSectionId(order.getLocationsId());
				}
				String jobroleId = getJobRoleIdFromOpenTime(em, order.getPreassignedServerId(), order.getOpenTime());
				List<TipPoolRules> directTipPoolRules = niravanaXpTippingBean.getTipRulesByValidation(
						order.getOrderSourceGroupId(), order.getSectionId(), directTipClass.getId(), poolRulesList,
						jobroleId);

				List<TipPoolRules> indirectTipPoolRules = niravanaXpTippingBean.getTipRulesByValidation(
						order.getOrderSourceGroupId(), order.getSectionId(), indirectTipClass.getId(), poolRulesList,
						jobroleId);
				ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
				CalculatedPaymentSummary calculatedPaymentSummary = new CalculatedPaymentSummary();
				calculatedPaymentSummary = receiptPDFFormat.getPaymentSummary(em, locationId,
						order.getOrderPaymentDetails(), activeBatchDetail, false);
				BigDecimal calculatedCashTip = calculatedPaymentSummary.getCashTipAmount();
				BigDecimal calculatedCardTip = calculatedPaymentSummary.getCardTipAmount();
				BigDecimal calculatedCreditTermTip = calculatedPaymentSummary.getCreditTermTipAmount();
				BigDecimal calculatedCashAmt = calculatedPaymentSummary.getTotalCashAmmount();
				BigDecimal calculatedCardAmt = calculatedPaymentSummary.getTotalCardAmmount();
				BigDecimal calculatedCreditTermAmt = calculatedPaymentSummary.getTotalCreditTermAmmount();
				// in which shift order is taken
				String shiftId = niravanaXpTippingBean.getOrderSiftByOpenTime(em, order.getOpenTime(),
						operationalShiftList, locationId);

				BigDecimal totalAmt = calculatedCashAmt.add(calculatedCardAmt).add(calculatedCreditTermAmt);

				if (totalAmt.doubleValue() > 0) {
					BigDecimal totalCashPer = (calculatedCashAmt.multiply(new BigDecimal(100))).divide(totalAmt, 2,
							RoundingMode.HALF_UP);
					BigDecimal totalCardPer = (calculatedCardAmt.multiply(new BigDecimal(100))).divide(totalAmt, 2,
							RoundingMode.HALF_UP);
					BigDecimal totalCreditPer = (calculatedCreditTermAmt.multiply(new BigDecimal(100))).divide(totalAmt,
							2, RoundingMode.HALF_UP);

					TipPoolingByOrder tpByOrder = new TipPoolingByOrder();

					tpByOrder.setOrderId(order.getId());

					String preAssignServerId = null;
					if (order.getPreassignedServerId() != null) {
						preAssignServerId = order.getPreassignedServerId();
					} else {
						preAssignServerId = order.getServerId();
					}
					tpByOrder.setUserId(preAssignServerId);
					if (userId != null) {
						tpByOrder.setCreatedBy(userId);
						tpByOrder.setUpdatedBy(userId);
					}
					tpByOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					tpByOrder.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					tpByOrder.setStatus("A");

					BigDecimal calculatedIndirectCardTip = new BigDecimal(0);
					BigDecimal calculatedIndirectCashTip = new BigDecimal(0);
					BigDecimal calculatedIndirectCreditTermTip = new BigDecimal(0);

					BigDecimal calculatedPendingCardTip = new BigDecimal(0);
					BigDecimal calculatedPendingCashTip = new BigDecimal(0);
					BigDecimal calculatedPendingCreditTermTip = new BigDecimal(0);

					BigDecimal totalCard = new BigDecimal(0);
					BigDecimal totalCash = new BigDecimal(0);
					BigDecimal totalAmtByItemGroupId = new BigDecimal(0);
					BigDecimal totalCredit = new BigDecimal(0);
					BigDecimal newCashTip = calculatedCashTip;
					BigDecimal newCardTip = calculatedCardTip;
					BigDecimal newCreditTip = calculatedCreditTermTip;
					// calculation for indirect tip rules
					if (indirectTipPoolRules.size() > 0) {
						for (TipPoolRules tipRule : indirectTipPoolRules) {
							TipPoolingByPool tipPoolByPool = new TipPoolingByPool();

							if (tipRule.getTipPoolBasisId() == 1) {
								totalAmtByItemGroupId = niravanaXpTippingBean.calculatedCashAmtByItemGroup(
										order.getOrderDetailItems(), tipRule.getItemGroupId());
								totalCash = (totalAmtByItemGroupId.multiply(totalCashPer)).divide(new BigDecimal(100));
								totalCard = (totalAmtByItemGroupId.multiply(totalCardPer)).divide(new BigDecimal(100));
								totalCredit = (totalAmtByItemGroupId.multiply(totalCreditPer))
										.divide(new BigDecimal(100));
							} else {
								totalCash = calculatedCashTip;
								totalCard = calculatedCardTip;
								totalCredit = calculatedCreditTermTip;
							}

							BigDecimal indirectCardTip = new BigDecimal(0);
							BigDecimal indirectCashTip = new BigDecimal(0);
							BigDecimal indirectCreditTermTip = new BigDecimal(0);
							BigDecimal newIndirectCardTip = new BigDecimal(0);
							BigDecimal newIndirectCashTip = new BigDecimal(0);
							BigDecimal newIndirectCreditTermTip = new BigDecimal(0);
							BigDecimal pendingCardTip = new BigDecimal(0);
							BigDecimal pendingCashTip = new BigDecimal(0);
							BigDecimal pendingCreditTermTip = new BigDecimal(0);

							if (totalCard.compareTo(new BigDecimal(0)) > 0) {
								indirectCardTip = (tipRule.getTipRate().multiply(totalCard))
										.divide(new BigDecimal(100.00));
							}
							if (totalCash.compareTo(new BigDecimal(0)) > 0) {
								indirectCashTip = (tipRule.getTipRate().multiply(totalCash))
										.divide(new BigDecimal(100.00));
							}
							if (totalCredit.compareTo(new BigDecimal(0)) > 0) {
								indirectCreditTermTip = (tipRule.getTipRate().multiply(totalCredit))
										.divide(new BigDecimal(100.00));
							}

							newIndirectCashTip = indirectCashTip;
							newIndirectCardTip = indirectCardTip;
							newIndirectCreditTermTip = indirectCreditTermTip;

							if (indirectCashTip.compareTo(newCashTip) > 0) {
								pendingCashTip = indirectCashTip.subtract(newCashTip);
								newIndirectCashTip = newCashTip;
							}
							if (indirectCardTip.compareTo(newCardTip) > 0) {
								pendingCardTip = indirectCardTip.subtract(newCardTip);
								newIndirectCardTip = newCardTip;
							}
							if (indirectCreditTermTip.compareTo(newCreditTip) > 0) {
								pendingCreditTermTip = indirectCreditTermTip.subtract(newCreditTip);
								newIndirectCreditTermTip = newCreditTip;
							}

							if ((indirectCashTip.subtract(newCashTip)).compareTo(new BigDecimal(0)) > 0) {
								newCashTip = new BigDecimal(0);
							}
							if ((indirectCardTip.subtract(newCardTip)).compareTo(new BigDecimal(0)) > 0) {
								newCardTip = new BigDecimal(0);
							}
							if ((indirectCreditTermTip.subtract(newCreditTip)).compareTo(new BigDecimal(0)) > 0) {
								newCreditTip = new BigDecimal(0);
							}
							calculatedIndirectCardTip = calculatedIndirectCardTip.add(newIndirectCardTip);
							calculatedIndirectCashTip = calculatedIndirectCashTip.add(newIndirectCashTip);
							calculatedIndirectCreditTermTip = calculatedIndirectCreditTermTip
									.add(newIndirectCreditTermTip);

							calculatedPendingCardTip = calculatedPendingCardTip.add(pendingCardTip);
							calculatedPendingCashTip = calculatedPendingCashTip.add(pendingCashTip);
							calculatedPendingCreditTermTip = calculatedPendingCreditTermTip.add(pendingCreditTermTip);

							tipPoolByPool.setTipPoolId(tipRule.getTipPoolId());
							tipPoolByPool.setDirectCashTip(new BigDecimal(0));
							tipPoolByPool.setDirectCardTip(new BigDecimal(0));
							tipPoolByPool.setDirectCreditTermTip(new BigDecimal(0));
							tipPoolByPool.setCashTotal(new BigDecimal(0));
							tipPoolByPool.setCardTotal(new BigDecimal(0));
							tipPoolByPool.setCreditTotal(new BigDecimal(0));
							tipPoolByPool.setShiftId(shiftId);

							if (tipPoolingByPoolHashMap.get(tipRule.getTipPoolId() + "_" + shiftId + "_"
									+ tipRule.getSectionId() + "_" + order.getOrderSourceGroupId()) != null) {
								TipPoolingByPool prevTipPoolingByPool = tipPoolingByPoolHashMap
										.get(tipRule.getTipPoolId() + "_" + shiftId + "_" + tipRule.getSectionId() + "_"
												+ order.getOrderSourceGroupId());
								tipPoolByPool.setIndirectCashTip(
										newIndirectCashTip.add(prevTipPoolingByPool.getIndirectCashTip()));
								tipPoolByPool.setIndirectCardTip(
										newIndirectCardTip.add(prevTipPoolingByPool.getIndirectCardTip()));
								tipPoolByPool.setIndirectCreditTermTip(
										newIndirectCreditTermTip.add(prevTipPoolingByPool.getIndirectCreditTermTip()));
								tipPoolByPool.setPendingCashTip(
										pendingCashTip.add(prevTipPoolingByPool.getPendingCashTip()));
								tipPoolByPool.setPendingCardTip(
										pendingCardTip.add(prevTipPoolingByPool.getPendingCardTip()));
								tipPoolByPool.setPendingCreditTip(
										pendingCreditTermTip.add(prevTipPoolingByPool.getPendingCreditTip()));

							} else {
								tipPoolByPool.setIndirectCashTip(newIndirectCashTip);
								tipPoolByPool.setIndirectCardTip(newIndirectCardTip);
								tipPoolByPool.setIndirectCreditTermTip(newIndirectCreditTermTip);
								tipPoolByPool.setPendingCashTip(pendingCashTip);
								tipPoolByPool.setPendingCardTip(pendingCardTip);
								tipPoolByPool.setPendingCreditTip(pendingCreditTermTip);
							}
							tipPoolByPool.setSectionId(tipRule.getSectionId());
							tipPoolByPool.setOrderSourceGroupId(order.getOrderSourceGroupId());
							tipPoolingByPoolHashMap.put(tipRule.getTipPoolId() + "_" + shiftId + "_"
									+ tipRule.getSectionId() + "_" + order.getOrderSourceGroupId(), tipPoolByPool);
						}
					}
					BigDecimal directCardTip = new BigDecimal(0);
					BigDecimal directCashTip = new BigDecimal(0);
					BigDecimal directCreditTermTip = new BigDecimal(0);
					// calculation for direct tip rules
					if (directTipPoolRules.size() > 0) {
						for (TipPoolRules tipRule : directTipPoolRules) {
							TipPoolingByPool tipPoolByPool = new TipPoolingByPool();

							if (tipRule.getTipPoolBasisId() == 1) {
								totalAmtByItemGroupId = niravanaXpTippingBean.calculatedCashAmtByItemGroup(
										order.getOrderDetailItems(), tipRule.getItemGroupId());
								totalCash = (totalAmtByItemGroupId.multiply(totalCashPer)).divide(new BigDecimal(100));
								totalCard = (totalAmtByItemGroupId.multiply(totalCardPer)).divide(new BigDecimal(100));
								totalCredit = (totalAmtByItemGroupId.multiply(totalCreditPer))
										.divide(new BigDecimal(100));
							} else {
								totalCash = calculatedCashTip;
								totalCard = calculatedCardTip;
								totalCredit = calculatedCreditTermTip;
							}

							BigDecimal pendingCardTip = new BigDecimal(0);
							BigDecimal pendingCashTip = new BigDecimal(0);
							BigDecimal pendingCreditTermTip = new BigDecimal(0);

							if (totalCard.compareTo(new BigDecimal(0)) > 0) {
								directCardTip = (tipRule.getTipRate().multiply(totalCard))
										.divide(new BigDecimal(100.00)).subtract(calculatedIndirectCardTip);

							}
							if (totalCash.compareTo(new BigDecimal(0)) > 0) {
								directCashTip = (tipRule.getTipRate().multiply(totalCash))
										.divide(new BigDecimal(100.00)).subtract(calculatedIndirectCashTip);
							}

							if (totalCredit.compareTo(new BigDecimal(0)) > 0) {
								directCreditTermTip = (tipRule.getTipRate().multiply(totalCredit))
										.divide(new BigDecimal(100.00)).subtract(calculatedIndirectCreditTermTip);

							}
							if (directCashTip.compareTo(calculatedCashTip) > 0) {
								pendingCashTip = directCashTip.subtract(calculatedCashTip);
							}

							if (directCardTip.compareTo(calculatedCardTip) > 0) {
								pendingCardTip = directCardTip.subtract(calculatedCardTip);
							}
							if (directCreditTermTip.compareTo(calculatedCreditTermTip) > 0) {
								pendingCreditTermTip = directCreditTermTip.subtract(calculatedCreditTermTip);
							}

							tipPoolByPool.setTipPoolId(tipRule.getTipPoolId());
							tipPoolByPool.setIndirectCashTip(new BigDecimal(0));
							tipPoolByPool.setIndirectCardTip(new BigDecimal(0));
							tipPoolByPool.setIndirectCreditTermTip(new BigDecimal(0));

							tipPoolByPool.setShiftId(shiftId);

							if (tipPoolingByPoolHashMap.get(tipRule.getTipPoolId() + "_" + shiftId + "_"
									+ tipRule.getSectionId() + "_" + order.getOrderSourceGroupId()) != null) {

								TipPoolingByPool prevTipPoolingByPool = tipPoolingByPoolHashMap
										.get(tipRule.getTipPoolId() + "_" + shiftId + "_" + tipRule.getSectionId() + "_"
												+ order.getOrderSourceGroupId());

								tipPoolByPool.setPendingCashTip(
										pendingCashTip.add(prevTipPoolingByPool.getPendingCashTip()));
								tipPoolByPool.setPendingCardTip(
										pendingCardTip.add(prevTipPoolingByPool.getPendingCardTip()));
								tipPoolByPool.setPendingCreditTip(
										pendingCreditTermTip.add(prevTipPoolingByPool.getPendingCreditTip()));

								tipPoolByPool
										.setDirectCashTip(directCashTip.add(prevTipPoolingByPool.getDirectCashTip()));
								tipPoolByPool
										.setDirectCardTip(directCardTip.add(prevTipPoolingByPool.getDirectCardTip()));

								tipPoolByPool.setDirectCreditTermTip(
										directCreditTermTip.add(prevTipPoolingByPool.getDirectCreditTermTip()));
								tipPoolByPool.setIndirectCashTip(prevTipPoolingByPool.getIndirectCashTip());
								tipPoolByPool.setIndirectCardTip(prevTipPoolingByPool.getIndirectCardTip());
								tipPoolByPool.setIndirectCreditTermTip(prevTipPoolingByPool.getIndirectCreditTermTip());
								tipPoolByPool.setCashTotal(calculatedCashAmt.add(prevTipPoolingByPool.getCashTotal()));
								tipPoolByPool.setCardTotal(calculatedCardAmt.add(prevTipPoolingByPool.getCardTotal()));
								tipPoolByPool.setCreditTotal(
										calculatedCreditTermAmt.add(prevTipPoolingByPool.getCreditTotal()));
								tipPoolByPool.setSectionId(tipRule.getSectionId());
								tipPoolByPool.setOrderSourceGroupId(order.getOrderSourceGroupId());
							} else {
								tipPoolByPool.setDirectCashTip(directCashTip);
								tipPoolByPool.setDirectCardTip(directCardTip);
								tipPoolByPool.setDirectCreditTermTip(directCreditTermTip);
								tipPoolByPool.setPendingCashTip(pendingCashTip);
								tipPoolByPool.setPendingCardTip(pendingCardTip);
								tipPoolByPool.setPendingCreditTip(pendingCreditTermTip);
								tipPoolByPool.setCashTotal(calculatedCashAmt);
								tipPoolByPool.setCardTotal(calculatedCardAmt);
								tipPoolByPool.setCreditTotal(calculatedCreditTermAmt);
								tipPoolByPool.setSectionId(tipRule.getSectionId());
								tipPoolByPool.setOrderSourceGroupId(order.getOrderSourceGroupId());
							}

							tipPoolingByPoolHashMap.put(tipRule.getTipPoolId() + "_" + shiftId + "_"
									+ tipRule.getSectionId() + "_" + order.getOrderSourceGroupId(), tipPoolByPool);

						}

					}

					if (userId != null) {
						tpByOrder.setCreatedBy(userId);
						tpByOrder.setUpdatedBy(userId);
					}
					tpByOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					tpByOrder.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					tpByOrder.setStatus("A");
					tpByOrder.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					if (directCardTip.compareTo(new BigDecimal(0)) > 0)
						tpByOrder.setDirectCardTip(directCardTip);

					if (directCashTip.compareTo(new BigDecimal(0)) > 0)
						tpByOrder.setDirectCashTip(directCashTip);

					if (directCreditTermTip.compareTo(new BigDecimal(0)) > 0)
						tpByOrder.setDirectCreditTermTip(directCreditTermTip);

					tpByOrder.setIndirectCardTipSubmited(calculatedIndirectCardTip);
					tpByOrder.setIndirectCashTipSubmited(calculatedIndirectCashTip);
					tpByOrder.setIndirectCreditTermTipSubmited(calculatedIndirectCreditTermTip);
					tpByOrder.setCardTotal(calculatedCardAmt);
					tpByOrder.setCashTotal(calculatedCashAmt);
					tpByOrder.setCreditTotal(calculatedCreditTermAmt);
					tpByOrder.setNirvanaxpBatchId(locationId + "" + activeBatchDetail.getId());
					tpByOrder.setPendingCashTip(calculatedPendingCashTip);
					tpByOrder.setPendingCardTip(calculatedPendingCardTip);
					tpByOrder.setPendingCreditTip(calculatedPendingCreditTermTip);
					// fill tipPoolByOrder table with each orders with or
					// without tips
					em.persist(tpByOrder);

				}
			}
			List<TipPoolingByPool> tipPoolingByPoolList = new ArrayList<TipPoolingByPool>();

			for (Map.Entry<String, TipPoolingByPool> entry : tipPoolingByPoolHashMap.entrySet()) {
				TipPoolingByPool tipPoolingByPool = entry.getValue();

				if (userId != null) {
					tipPoolingByPool.setCreatedBy(userId);
					tipPoolingByPool.setUpdatedBy(userId);
				}
				tipPoolingByPool.setNirvanaxpBatchId(locationId + "-" + activeBatchDetail.getId());
				tipPoolingByPool.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				tipPoolingByPool.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				tipPoolingByPool.setStatus("A");
				tipPoolingByPool.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				tipPoolingByPool.setShiftId(tipPoolingByPool.getShiftId());
				// fill tipPoolingByPool table ,number of entries in this table
				// is depend on number of shifts in the batch
				// one entrie per batch
				// contain total of all type of sale amounts and tip amounts for
				// a shift
				em.persist(tipPoolingByPool);
				tipPoolingByPoolList.add(tipPoolingByPool);
			}
			List<EmployeeOperationalHoursWithTotalHours> employeeOperationalHoursList = niravanaXpTippingBean
					.getAllEmployeeOperationalHoursByBatchId(httpRequest, em, activeBatchDetail.getId());
			for (EmployeeOperationalHoursWithTotalHours employeeOperationalHoursWithTotalHours : employeeOperationalHoursList) {

				BigDecimal employeeHrSec = employeeOperationalHoursWithTotalHours.getEmployeeHrSec();

				for (TipPoolingByPool tipPoolingByPool : tipPoolingByPoolList) {
					EmployeeOperationalHoursWithTotalHours employeeMaster = niravanaXpTippingBean
							.getEmployeeMasterByUserIdandShiftId(httpRequest, em,
									employeeOperationalHoursWithTotalHours);
					if (employeeMaster != null) {
						int tipPoolId = employeeMaster.getTipPoolId();

						TipPool tipPool = em.find(TipPool.class, tipPoolId);
						BigDecimal totalHrSec = niravanaXpTippingBean.getTotalEmployeeOperationalHoursByBatchId(
								httpRequest, em, activeBatchDetail.getId(),
								employeeOperationalHoursWithTotalHours.getShiftId(), tipPool.getJobRoleId());
						if (totalHrSec != null && tipPoolingByPool.getTipPoolId() == tipPoolId && tipPoolingByPool
								.getShiftId().equals(employeeOperationalHoursWithTotalHours.getShiftId())) {

							TipDistribution tipDistribution = new TipDistribution();

							OperationalShiftSchedule shiftSchedule = em.find(OperationalShiftSchedule.class,
									tipPoolingByPool.getShiftId());

							// calculation of ammount on shift and section
							BigDecimal calculatedCashAmtForUser = calculateCashAmountTotal(httpRequest, em,
									employeeOperationalHoursWithTotalHours.getEmployeeId(), locationId,
									activeBatchDetail, shiftSchedule, tipPoolingByPool.getSectionId(),
									employeeMaster.getJobRoleId(), tipPoolingByPool.getOrderSourceGroupId());

							BigDecimal calculatedCardAmtForUser = calculateCardAmountTotal(httpRequest, em,
									employeeOperationalHoursWithTotalHours.getEmployeeId(), locationId,
									activeBatchDetail, shiftSchedule, tipPoolingByPool.getSectionId(),
									employeeMaster.getJobRoleId(), tipPoolingByPool.getOrderSourceGroupId());
							BigDecimal calculatedCreditTermAmtForUser = calculateCreditTermAmountTotal(httpRequest, em,
									employeeOperationalHoursWithTotalHours.getEmployeeId(), locationId,
									activeBatchDetail, shiftSchedule, tipPoolingByPool.getSectionId(),
									employeeMaster.getJobRoleId(), tipPoolingByPool.getOrderSourceGroupId());
							if (calculatedCashAmtForUser == null) {
								calculatedCashAmtForUser = new BigDecimal(0);
							}
							if (calculatedCardAmtForUser == null) {
								calculatedCardAmtForUser = new BigDecimal(0);
							}
							if (calculatedCreditTermAmtForUser == null) {
								calculatedCreditTermAmtForUser = new BigDecimal(0);
							}

							tipDistribution.setBatchSalary(employeeMaster.getHourlyRate().multiply(employeeHrSec));
							tipDistribution.setHourlyRate(employeeMaster.getHourlyRate());

							tipDistribution.setJobRoleId(employeeOperationalHoursWithTotalHours.getJobRoleId());

							if (userId != null) {
								tipDistribution.setCreatedBy(userId);
								tipDistribution.setUpdatedBy(userId);
							}
							tipDistribution.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							tipDistribution.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							tipDistribution.setStatus("A");
							tipDistribution.setNoOfHours(employeeHrSec);
							tipDistribution.setNirvanaxpBatchId(locationId + "-" + activeBatchDetail.getId());
							tipDistribution.setShiftId(employeeOperationalHoursWithTotalHours.getShiftId());
							tipDistribution.setUserId(employeeOperationalHoursWithTotalHours.getEmployeeId());
							tipDistribution
									.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							tipDistribution.setCashTotal(calculatedCashAmtForUser);
							tipDistribution.setCardTotal(calculatedCardAmtForUser);
							tipDistribution.setCreditTotal(calculatedCreditTermAmtForUser);
							BigDecimal directCardTip = tipPoolingByPool.getDirectCardTip();
							BigDecimal directCashTip = tipPoolingByPool.getDirectCashTip();
							BigDecimal directCreditTermTip = tipPoolingByPool.getDirectCreditTermTip();

							BigDecimal calculatedDirectCardTip = (new BigDecimal(
									(directCardTip.doubleValue() / totalHrSec.doubleValue())
											* (employeeHrSec.doubleValue()))).setScale(2, RoundingMode.HALF_EVEN);
							BigDecimal calculatedDirectCashTip = (new BigDecimal(
									(directCashTip.doubleValue() / totalHrSec.doubleValue())
											* (employeeHrSec.doubleValue()))).setScale(2, RoundingMode.HALF_EVEN);
							BigDecimal calculatedDirectCreditTermTip = (new BigDecimal(
									(directCreditTermTip.doubleValue() / totalHrSec.doubleValue())
											* (employeeHrSec.doubleValue()))).setScale(2, RoundingMode.HALF_EVEN);

							tipDistribution.setDirectCardTip(calculatedDirectCardTip);
							tipDistribution.setDirectCashTip(calculatedDirectCashTip);
							tipDistribution.setDirectCreditTermTip(calculatedDirectCreditTermTip);
							tipDistribution.setIndirectCardTip(new BigDecimal(
									(tipPoolingByPool.getIndirectCardTip().doubleValue() / totalHrSec.doubleValue())
											* (employeeHrSec.doubleValue())));
							tipDistribution.setIndirectCashTip(new BigDecimal(
									(tipPoolingByPool.getIndirectCashTip().doubleValue() / totalHrSec.doubleValue())
											* (employeeHrSec.doubleValue())));
							tipDistribution.setIndirectCreditTermTip(
									new BigDecimal((tipPoolingByPool.getIndirectCreditTermTip().doubleValue()
											/ totalHrSec.doubleValue()) * (employeeHrSec.doubleValue())));
							tipDistribution.setSectionId(tipPoolingByPool.getSectionId());
							tipDistribution.setOrderSourceGroupId(tipPoolingByPool.getOrderSourceGroupId());
							niravanaXpTippingBean.addUpdatTipPoolingForUser(httpRequest, em, tipDistribution,
									activeBatchDetail);

						}
					}
				}

			}

		} catch (Exception e) {
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}

	}

	/**
	 * Calculate employee operational hours.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param userId
	 *            the user id
	 * @param batchDetail
	 *            the batch detail
	 * @return the list
	 */
	public List<Object[]> calculateEmployeeOperationalHours(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String userId, BatchDetail batchDetail) {
		TimezoneTime timezoneTime = new TimezoneTime();
		List<Object[]> resultList = new ArrayList<Object[]>();

		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String gmtStartDate = dateFormatGmt.format(new Date(batchDetail.getStartTime()));
		gmtStartDate = timezoneTime.getDateAccordingToLocationFromGMTForConnection(em, gmtStartDate, locationId);
		String gmtStartDateTimeArray[] = gmtStartDate.split(" ");

		String gmtEndDate = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
		gmtEndDate = timezoneTime.getDateAccordingToLocationFromGMTForConnection(em, gmtEndDate, locationId);
		String gmtEndDateTimeArray[] = gmtEndDate.split(" ");

		Location foundLocation = null;
		if (locationId != null) {
			String queryString = "select l from Location l where l.id =? and l.locationsId = '0' and l.locationsTypeId = '1' ";
			TypedQuery<Location> query = em.createQuery(queryString, Location.class).setParameter(1, locationId);
			foundLocation = query.getSingleResult();
		}

		List<OperationalShiftSchedule> operationalShiftScheduleList = null;
		try {
			String queryString = "";
			// convert this to paremterized form and why is fromtime and totime
			// the same value?
			queryString = "select tr from OperationalShiftSchedule tr where  ((fromTime <='" + gmtEndDateTimeArray[1]
					+ "' " + "and toTime>='" + gmtEndDateTimeArray[1] + "') or ( fromTime <='"
					+ gmtStartDateTimeArray[1] + "' or toTime>='" + gmtStartDateTimeArray[1] + "')) and tr.fromDate<='"
					+ gmtStartDateTimeArray[0] + "' and tr.toDate>='" + gmtEndDateTimeArray[0] + "' and tr.locationId ="
					+ locationId + " and tr.status not in ('D', 'I')";

			TypedQuery<OperationalShiftSchedule> query = em.createQuery(queryString, OperationalShiftSchedule.class);

			operationalShiftScheduleList = query.getResultList();
		} catch (Exception e) {
			logger.severe(httpRequest, e,
					"No result Fopund for employee Operational Hours List for location Id " + locationId);
		}

		// why is this list selected from database? it's not used anywhere.
		List<User> users = getAllAdminUsersBylocationIdList(em, locationId);

		String userList = "";

		List<EmployeeMaster> employeeMasterList = null;
		try {
			String queryString = "select tr from EmployeeMaster tr where   tr.locationsId=?  and tr.status not in ('D') and tr.isTippedEmployee = 1";
			TypedQuery<EmployeeMaster> query = em.createQuery(queryString, EmployeeMaster.class).setParameter(1,
					locationId);
			employeeMasterList = query.getResultList();
		} catch (Exception e) {

			logger.severe(httpRequest, e, "No result Fopund for Employee Master for User Id ");
		}

		// what purpose does this if block serve, where is userlist variable
		// used after it is initialized in the block below?
		if (employeeMasterList != null) {
			for (int i = 0; i < employeeMasterList.size(); i++) {
				if (i == employeeMasterList.size() - 1) {
					userList += employeeMasterList.get(i).getUserId();
				} else {
					userList += employeeMasterList.get(i).getUserId() + ",";
				}
			}

		}

		try {
			resultList.addAll(em.createNativeQuery("call get_emloyee_hours_by_batch(?,?,?,?,?,?)")
					.setParameter(1, foundLocation.getBusinessId())
					.setParameter(2, dateFormatGmt.format(new Date(batchDetail.getStartTime())))
					.setParameter(3, dateFormatGmt.format(batchDetail.getCloseTime())).setParameter(4, 0)
					.setParameter(5, batchDetail.getId()).setParameter(6, userId).getResultList());

		} catch (Exception e) {
			logger.severe(httpRequest, e);
		}
		/*
		 * if (operationalShiftScheduleList != null &&
		 * operationalShiftScheduleList.size() > 0 && users != null &&
		 * users.size() > 0) { for (OperationalShiftSchedule
		 * operationalShiftSchedule : operationalShiftScheduleList) {
		 * 
		 * try { logger.
		 * severe("================================$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$======================================call get_emloyee_hours_by_shift( "
		 * + foundLocation.getBusinessId() + ", " + dateFormatGmt.format(new
		 * Date(batchDetail.getStartTime())) + "," +
		 * dateFormatGmt.format(batchDetail.getCloseTime()) + ", " + userList +
		 * ", " + operationalShiftSchedule.getId() + ", " + batchDetail.getId()
		 * + ", " + userId + " )");
		 * 
		 * resultList.addAll(em.
		 * createNativeQuery("call get_emloyee_hours_by_shift(?,?,?,?,?,?,?)").
		 * setParameter(1, foundLocation.getBusinessId()) .setParameter(2,
		 * dateFormatGmt.format(new
		 * Date(batchDetail.getStartTime()))).setParameter(3,
		 * dateFormatGmt.format(batchDetail.getCloseTime())).setParameter(4,
		 * userList) .setParameter(5,
		 * operationalShiftSchedule.getId()).setParameter(6,
		 * batchDetail.getId()).setParameter(7, userId).getResultList());
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace(); logger.severe(e +
		 * "No result Found for get_emloyee_hours_by_shift "); }
		 * 
		 * } }
		 */
		return resultList;

	}

	/**
	 * Gets the all admin users by location id list.
	 *
	 * @param em
	 *            the em
	 * @param locationIds
	 *            the location ids
	 * @return the all admin users by location id list
	 */
	public List<User> getAllAdminUsersBylocationIdList(EntityManager em, String locationIds) {

		List<User> usersList = new ArrayList<User>();

		String queryString = "select u.id,u.first_name,u.last_name,u.email,u.phone,u.username,u.status "
				+ "from users u left join users_to_roles utr on u.id=utr.users_id left join roles r on r.id=utr.roles_id"
				+ " left join users_to_locations utl on u.id=utl.users_id where utl.locations_id= ?  and  r.role_name != 'POS Customer' and u.status != 'D' group by u.id";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationIds).getResultList();
		for (Object[] objRow : resultList) {
			// if this has primary key not 0
			User user = new User();
			user.setId((String) objRow[0]);
			user.setFirstName((String) objRow[1]);
			user.setLastName((String) objRow[2]);
			user.setEmail((String) objRow[3]);
			user.setPhone((String) objRow[4]);
			user.setUsername((String) objRow[5]);
			user.setStatus((String) objRow[6].toString());

			usersList.add(user);
		}

		return usersList;

	}

	/**
	 * Calculate tip pool on preassign server.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @param activeBatchDetail
	 *            the active batch detail
	 * @param orderHeaderList
	 *            the order header list
	 */
	private void calculateTipPoolOnPreassignServer(HttpServletRequest httpRequest, EntityManager em, String userId,
			String locationId, BatchDetail activeBatchDetail, List<OrderHeader> orderHeaderList) {
		// TODO Auto-generated method stub
		try {
			try {
				NiravanaXpTippingBean niravanaXpTippingBean = new NiravanaXpTippingBean();
				List<OperationalShiftSchedule> operationalShiftList = niravanaXpTippingBean
						.getAllOperationalShiftSchedule(httpRequest, em, locationId);
				for (OrderHeader order : orderHeaderList) {
					ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
					CalculatedPaymentSummary calculatedPaymentSummary = new CalculatedPaymentSummary();
					calculatedPaymentSummary = receiptPDFFormat.getPaymentSummary(em, locationId,
							order.getOrderPaymentDetails(), activeBatchDetail, false);

					BigDecimal calculatedCashTip = calculatedPaymentSummary.getCashTipAmount();
					BigDecimal calculatedCardTip = calculatedPaymentSummary.getCardTipAmount();
					BigDecimal calculatedCreditTermTip = calculatedPaymentSummary.getCreditTermTipAmount();
					BigDecimal calculatedCashAmt = calculatedPaymentSummary.getTotalCashAmmount();
					BigDecimal calculatedCardAmt = calculatedPaymentSummary.getTotalCardAmmount();
					BigDecimal calculatedCreditTermAmt = calculatedPaymentSummary.getTotalCreditTermAmmount();

					BigDecimal totalTip = calculatedCashTip.add(calculatedCardTip).add(calculatedCreditTermTip);

					// BigDecimal calculatedCashTip =
					// receiptPDFFormat.calculateTotalCashTip(em, locationId,
					// order.getOrderPaymentDetails(), activeBatchDetail);
					// BigDecimal calculatedCardTip =
					// receiptPDFFormat.calculateTotalCardTip(em, locationId,
					// order.getOrderPaymentDetails(), activeBatchDetail);
					// BigDecimal calculatedCreditTermTip =
					// receiptPDFFormat.calculateTotalCreditTermTip(em,
					// locationId, order.getOrderPaymentDetails(),
					// activeBatchDetail);
					// BigDecimal calculatedCashAmt =
					// receiptPDFFormat.calculateCashAmountTotal(em, locationId,
					// order.getOrderPaymentDetails(), activeBatchDetail,
					// false);
					// BigDecimal calculatedCardAmt =
					// receiptPDFFormat.calculateCardAmountTotal(em, locationId,
					// order.getOrderPaymentDetails(), activeBatchDetail,
					// false);
					// BigDecimal calculatedCreditTermAmt =
					// receiptPDFFormat.calculateCreditTerm(em, locationId,
					// order.getOrderPaymentDetails(), activeBatchDetail,
					// false);
					// BigDecimal totalTip =
					// calculatedCashTip.add(calculatedCardTip).add(calculatedCreditTermTip);
					if (totalTip.doubleValue() > 0) {

						TipPoolingByOrder tpByOrder = new TipPoolingByOrder();

						tpByOrder.setOrderId(order.getId());

						if (order.getPreassignedServerId() != null)
							tpByOrder.setUserId(order.getPreassignedServerId());
						else
							tpByOrder.setUserId(order.getServerId());

						tpByOrder.setCreatedBy(userId);
						tpByOrder.setUpdatedBy(userId);
						tpByOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						tpByOrder.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						tpByOrder.setStatus("A");
						tpByOrder.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));

						tpByOrder.setDirectCardTip(calculatedCardTip);
						tpByOrder.setDirectCashTip(calculatedCashTip);
						tpByOrder.setDirectCreditTermTip(calculatedCreditTermTip);
						tpByOrder.setIndirectCardTipSubmited(new BigDecimal(0));
						tpByOrder.setIndirectCashTipSubmited(new BigDecimal(0));
						tpByOrder.setIndirectCreditTermTipSubmited(new BigDecimal(0));
						tpByOrder.setCardTotal(calculatedCardAmt);
						tpByOrder.setCashTotal(calculatedCashAmt);
						tpByOrder.setCreditTotal(calculatedCreditTermAmt);
						tpByOrder.setNirvanaxpBatchId(activeBatchDetail.getId());

						em.persist(tpByOrder);

						TipDistribution o = new TipDistribution();

						String shiftId = niravanaXpTippingBean.getOrderSiftByOpenTime(em, order.getOpenTime(),
								operationalShiftList, locationId);

						if (order.getPreassignedServerId() != null)
							o.setUserId(order.getPreassignedServerId());
						else
							o.setUserId(order.getServerId());

						o.setCreatedBy(userId);
						o.setUpdatedBy(userId);
						o.setShiftId(shiftId);
						o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						o.setStatus("A");
						o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
						o.setBatchSalary(new BigDecimal(0));
						o.setDirectCardTip(calculatedCardTip);
						o.setDirectCashTip(calculatedCashTip);
						o.setDirectCreditTermTip(calculatedCreditTermTip);
						o.setIndirectCardTip(new BigDecimal(0));
						o.setIndirectCashTip(new BigDecimal(0));
						o.setIndirectCreditTermTip(new BigDecimal(0));
						o.setNirvanaxpBatchId(activeBatchDetail.getId());
						o.setCardTotal(calculatedCardAmt);
						o.setCashTotal(calculatedCashAmt);
						o.setCreditTotal(calculatedCreditTermAmt);

						niravanaXpTippingBean.addUpdatTipPoolingForUser(httpRequest, em, o, activeBatchDetail);
					}

				}

			} catch (Exception e) {
				// todo shlok need
				// handle proper exception
				logger.severe(e);

			}
		} catch (Exception e) {
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}
	}

	/**
	 * Gets the tip class by name.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @return the tip class by name
	 */
	private TipClass getTipClassByName(HttpServletRequest httpRequest, EntityManager em, String name) {
		TipClass resultSet = null;
		try {
			String queryString = "select tc from TipClass tc  where tc.name ='" + name + "'";
			TypedQuery<TipClass> query = em.createQuery(queryString, TipClass.class);
			resultSet = query.getSingleResult();
		} catch (Exception e) {
			// todo shlok need
			// handle proper exception
			logger.severe(httpRequest, e);
		}

		return resultSet;
	}

	/**
	 * Update reservation status.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param reservationStatusName
	 *            the reservation status name
	 * @param reservation
	 *            the reservation
	 * @param locationId
	 *            the location id
	 * @param updatedBy
	 *            the updated by
	 * @return update reservation status
	 */
	private Reservation updateReservationStatus(HttpServletRequest httpRequest, EntityManager em,
			String reservationStatusName, Reservation reservation, String locationId, String updatedBy) {

		ReservationsStatus reservationsStatus = getReservationByNameAndlocationId(em, reservationStatusName,
				locationId);
		reservation.setReservationsStatusId(reservationsStatus.getId());
		reservation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		reservation.setUpdatedBy(updatedBy);
		reservation.setReservationsStatus(reservationsStatus);

		em.merge(reservation);
		// find reservation
		Reservation res_histroy = new CommonMethods().getReservationById(httpRequest, em, reservation.getId());

		// insert into reservation history
		new InsertIntoHistory().insertReservationIntoHistory(httpRequest, res_histroy, em);
		return reservation;
	}

	/**
	 * Gets the orderstatus for inventory.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the orderstatus for inventory
	 */
	private List<OrderDetailStatus> getOrderstatusForInventory(EntityManager em, String locationId) {
		String query = "select ods from OrderDetailStatus ods where ods.status !='D' and ods.name in ('Out Of Stock','Ordered Qty More Than Avail Qty','No Inventory Available') and ods.locationsId = ? ";
		TypedQuery<OrderDetailStatus> query2 = em.createQuery(query, OrderDetailStatus.class).setParameter(1,
				locationId);
		List<OrderDetailStatus> resultSet = query2.getResultList();
		return resultSet;
	}

	/**
	 * Seat tab order.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @param updatedBy
	 *            the updated by
	 * @param locationId
	 *            the location id
	 * @return the order header
	 */
	public OrderHeader seatTabOrder(HttpServletRequest httpRequest, EntityManager em, String orderId, String updatedBy,
			String locationId) {

		OrderHeader prevHeader = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em, OrderHeader.class,
				orderId);
		prevHeader.setUpdated(new Date(updatedTime()));
		prevHeader.setUpdatedBy(updatedBy);
		prevHeader.setLocationsId(locationId);
		prevHeader.setIsTabOrder(0);

		em.merge(prevHeader);

		OrderHeader order_his = getOrderById(em, prevHeader.getId());

		// insert into order history
		new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order_his, em);
		return order_his;

	}

	/**
	 * Update QR code and history.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param merchantId
	 *            the merchant id
	 * @param parentlocationId
	 *            the parent location id
	 * @param schemaName
	 *            the schema name
	 * @return the order header with user
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws WriterException
	 *             the writer exception
	 */

	public OrderHeaderWithUser updateQRCodeAndHistory(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order, int merchantId, String parentlocationId, String schemaName)
			throws IOException, WriterException {

		// add QR code and verification code
		String basePath = ConfigFileReader.getQRCodeUploadPathFromFile();
		String basePath2 = ConfigFileReader.getQRCodeUploadPathFromFile2();
		String adminFeedbackURL = ConfigFileReader.getAdminFeedbackURL();
		boolean isQrcodeGenerated = false;

		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				order.getLocationsId());
		if (location != null) {
			EntityManager globalEM = null;
			try {
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

				POSNPartners partners = getPOSNPartnersByBusinessId(httpRequest, globalEM, location.getBusinessId());
				if (partners != null) {
					String fileName = "qrcode_" + order.getId();
					if (partners != null) {
						try {
							new CommonMethods().createFileWithAllPermission(basePath + basePath2 + merchantId);

							String pathSpecificToMerchantWithSlash = "" + merchantId + "/" + parentlocationId + "/";
							String folderPath = basePath + basePath2 + pathSpecificToMerchantWithSlash;
							File childDirectory = new CommonMethods().createFileWithAllPermission(folderPath);
							String path = folderPath + fileName;

							String codeText = adminFeedbackURL + "refno=" + partners.getReferenceNumber() + "&order_id="
									+ order.getId();

							isQrcodeGenerated = new CommonMethods().generateQrcode(codeText, path + ".png",
									childDirectory.getAbsolutePath());
							if (isQrcodeGenerated) {
								// save order qr code and images
								order.setQrcode(basePath2 + pathSpecificToMerchantWithSlash + fileName);
								order.setVerificationCode("" + order.getId());

								em.merge(order);
							}
						} catch (Exception e) {
							logger.severe(e);
						}
					}
				}
			} catch (Exception e1) {
				logger.severe(httpRequest, "Could not find globaldatabaseConnection for printing qrcode ");
				throw e1;
			} finally {
				GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			}
		}
		// Change by uzma for enter the value in order history
		// find order
		OrderHeader order_his = getOrderById(em, order.getId());

		// insert into order status history
		insertIntoOrderStatusHistory(em, order_his);

		// insert into order history
		new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order_his, em);

		// -------------------END--------------------------------

		// fetch user info, pass user information to the clients
		User user = null;
		if (order.getUsersId() != null)// && user ==
										// null)
		{
			user = (User) new CommonMethods().getObjectById("User", em, User.class, order.getUsersId());
		}

		if (isQrcodeGenerated && order != null && order_his != null) {

			order_his.setQrcode(order.getQrcode());
			order_his.setVerificationCode(order.getVerificationCode());
		}

		return new OrderHeaderWithUser(order_his, user, false);

	}

	/**
	 * Checks if is advance order.
	 *
	 * @param order
	 *            the order
	 * @param em
	 *            the em
	 * @param rootlocationId
	 *            the root location id
	 * @return true, if is advance order
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 */
	private boolean isAdvanceOrder(OrderHeader order, EntityManager em, String rootlocationId)
			throws NirvanaXPException, ParseException {

		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				order.getOrderStatusId());
		String orderStatusName = orderStatus.getName();

		if (orderStatusName.equals(MessageConstants.OA_RECEIVED) || orderStatusName.equals(MessageConstants.OA_PLACED)
				|| orderStatusName.equals(MessageConstants.OA_CHECKPRESENTED)
				|| orderStatusName.equals(MessageConstants.OA_PAID)) {
			return true;
		}
		return false;

	}

	/**
	 * Handle inventory for advance order.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param order
	 *            the order
	 * @param em
	 *            the em
	 * @param rootlocationId
	 *            the root location id
	 * @return the inventory post packet
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	private InventoryPostPacket handleInventoryForAdvanceOrder(HttpServletRequest httpRequest, OrderHeader order,
			EntityManager em, String rootlocationId) throws NirvanaXPException {
		InventoryPostPacket inventoryPostPacket = new InventoryPostPacket();
		if (order.getOrderDetailItems() != null) {
			// boolean isAdvanceOrder = isAdvanceOrder(order, em,
			// rootlocationId);
			Location rootLocation = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					rootlocationId);
			int isRealTimeInventoryOn = 0;
			ItemInventoryManagementHelper inventoryManagementHelper = new ItemInventoryManagementHelper();

			if (rootLocation != null) {
				isRealTimeInventoryOn = rootLocation.getIsRealTimeInventoryRequired();
			}
			for (OrderDetailItem orderDetailItem : order.getOrderDetailItems()) {
				// isAdvanceOrder
				// &&
				if (orderDetailItem.getInventoryAccrual() == 0 && orderDetailItem.getIsInventoryHandled() == 0) {
					if (isRealTimeInventoryOn == 1) {
						inventoryManagementHelper.manageItemInventoryForAdvanceOrder(httpRequest, orderDetailItem, em,
								isRealTimeInventoryOn, order.getUpdatedBy(), inventoryPostPacket, rootlocationId, true);
						orderDetailItem.setIsInventoryHandled(1);
					}
				}

			}
		}
		return inventoryPostPacket;
	}

	/**
	 * Adds the order to card.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param referenceNumber
	 *            the reference number
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 * @throws WriterException
	 *             the writer exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	OrderHeader addOrderToCard(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket,
			String referenceNumber)
			throws NirvanaXPException, IOException, ParseException, WriterException, InvalidSessionException {

		try {
			// todo shlok need
			// modularise method code
			PaymentBatchManager batchManager = PaymentBatchManager.getInstance();

			OrderHeader order = orderPacket.getOrderHeader();
			String needToReject = checkInventoryForOrder(httpRequest, order, em, orderPacket.getLocationId());
			if (needToReject != null) {

				throw (new NirvanaXPException(new NirvanaServiceErrorResponse("ORD1022", needToReject, needToReject)));

			}
			OrderHeader oFromDB = null;
			if (order.getId() != null) {
				oFromDB = getOrderById(em, order.getId());
			}

			OrderHeader order_his = new OrderHeader();
			String batchId = batchManager.getCurrentBatchIdBySession(httpRequest, em, order.getLocationsId(), true,
					orderPacket, order.getUpdatedBy());
			List<OrderDetailItem> orderDetailItems = new ArrayList<OrderDetailItem>();
			ShiftSlotActiveClientInfo shiftSlotActiveClientInfo = null;
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
					OrderSource.class, order.getOrderSourceId());
			OrderSourceGroup sourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,
					OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
			if (orderPacket.getIsDigitalMenu() == 0 && order.getId() == null) {
				if (sourceGroup.getName().equals("Pick Up") || sourceGroup.getName().equals("Delivery")) {

					shiftSlotActiveClientInfo = checkActiveClientForShiftSlot(httpRequest, em, order,
							orderPacket.getIdOfOrderHoldingClientObj());
				}
			}

			for (OrderDetailItem orderItem : order.getOrderDetailItems()) {
				if (orderItem.getCreated() == null) {
					orderItem.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(order.getLocationsId(), em));
					orderItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				orderItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				orderDetailItems.add(orderItem);
			}
			order.setOrderDetailItems(orderDetailItems);

			String rootlocationId = order.getLocationsId();

			TimezoneTime timezoneTime = new TimezoneTime();
			String locationId = order.getLocationsId();

			String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(locationId, em);
			if (currentDateTime != null && currentDateTime.length > 0) {
				order.setDate(currentDateTime[0]);
				if (order.getScheduleDateTime() == null) {
					order.setScheduleDateTime(currentDateTime[2]);
					order.setScheduleDateTime(
							timezoneTime.getDateAccordingToGMT(order.getScheduleDateTime(), locationId, em));
				} else {
					order.setScheduleDateTime(
							timezoneTime.getDateAccordingToGMT(order.getScheduleDateTime(), locationId, em));
				}

			}

			OrderHeaderCalculation calculation = new OrderHeaderCalculation();

			OrderHeader orderHeader = calculation.getOrderHeaderCalculation(em, order, orderPacket);
			if (order.getId() != null && (orderPacket.getIsCustomerApp() == 1)) {

				if (order.getUsersToDiscounts() != null && orderHeader.getUsersId() != null) {
					Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
							order.getUsersToDiscounts().getDiscountId());
					UsersToDiscount usersToDiscount = getUsersToDiscount(httpRequest, orderHeader, em);

					if (usersToDiscount == null) {
						usersToDiscount = order.getUsersToDiscounts();
						usersToDiscount.setNumberOfTimeDiscountUsed(1);
					} else {
						usersToDiscount.setNumberOfTimeDiscountUsed(usersToDiscount.getNumberOfTimeDiscountUsed() + 1);
					}
					logger.severe(
							"usersToDiscount========================2222222222222================================="
									+ usersToDiscount.getNumberOfTimeDiscountUsed());

					if (discount.getNumberOfTimeDiscountUsed() != -1) {
						if ((usersToDiscount.getNumberOfTimeDiscountUsed() >= discount.getNumberOfTimeDiscountUsed())) {

							throw (new NirvanaXPException(new NirvanaServiceErrorResponse(
									MessageConstants.ERROR_CODE_DISCOUNT_CODE_ALREADY_USED_BY_USER,
									MessageConstants.ERROR_MESSAGE_DISCOUNT_CODE_ALREADY_USED_BY_USER, null)));
						}
					}

					usersToDiscount.setUpdated(new Date(updatedTime()));
					em.merge(usersToDiscount);

				} else if (orderHeader.getDiscountsId() == null && orderHeader.getUsersId() != null
						&& orderPacket.getIsDiscountRemove() == 1) {

					try {
						PaymentTransactionType paymentTransactionType = getPaymentTransactionTypeBylocationIdAndName(em,
								orderHeader.getLocationsId(), "discount");
						OrderPaymentDetail orderPaymentDetail;
						int ifOPDExist = -1;
						if (order.getOrderPaymentDetails() != null && order.getOrderPaymentDetails().size() > 0) {
							List<OrderPaymentDetail> orderPaymentDetailList = new ArrayList<>(
									oFromDB.getOrderPaymentDetails());
							int size = order.getOrderPaymentDetails().size();
							if (size > 0) {
								for (int i = 0; i < size; i++) {
									if (orderPaymentDetailList.size() > 0 && orderPaymentDetailList.get(i)
											.getPaymentTransactionType().getId() == paymentTransactionType.getId()) {
										ifOPDExist = i;
										break;
									}
								}
							}
						}
						if (ifOPDExist != -1 && orderHeader.getOrderPaymentDetails() != null) {
							orderPaymentDetail = new ArrayList<>(orderHeader.getOrderPaymentDetails()).get(ifOPDExist);
						} else {
							orderPaymentDetail = new OrderPaymentDetail();
							orderPaymentDetail.setDiscountCode("");
						}

						/*
						 * logger.severe("----------------------------- dis 4 "
						 * + orderHeader.getUsersId() +
						 * " orderPaymentDetail.getDiscountCode().toLowerCase() "
						 * + orderPaymentDetail.getDiscountCode().toLowerCase()
						 * +" locationId " + orderHeader.getLocationsId() +
						 * " oFromDB.getDiscountsId() " +
						 * oFromDB.getDiscountsId());
						 */
						CriteriaBuilder builder = em.getCriteriaBuilder();
						CriteriaQuery<UsersToDiscount> criteria = builder.createQuery(UsersToDiscount.class);
						Root<UsersToDiscount> r = criteria.from(UsersToDiscount.class);

						// Remove discountCode condition for task #46369
						TypedQuery<UsersToDiscount> query = em.createQuery(criteria.select(r).where(
								builder.equal(r.get(UsersToDiscount_.usersId), orderHeader.getUsersId()),
								/*
								 * builder.equal(builder.lower(r.get(
								 * UsersToDiscount_.discountCode)),
								 * orderPaymentDetail.getDiscountCode().
								 * toLowerCase( )),
								 */
								builder.equal(r.get(UsersToDiscount_.locationId), orderHeader.getLocationsId()),
								builder.equal(r.get(UsersToDiscount_.discountId), oFromDB.getDiscountsId())));

						UsersToDiscount usersToDiscount = query.getSingleResult();
						usersToDiscount
								.setNumberOfTimeDiscountUsed((usersToDiscount.getNumberOfTimeDiscountUsed() - 1));
						usersToDiscount = em.merge(usersToDiscount);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.severe(e);
					}

				}

			}

			// orderHeader.setSessionKey(httpRequest.getHeader("auth-token"));
			OrderHeader o = new OrderHeader();
			o.setOrderStatusId(orderHeader.getOrderStatusId());
			o.setId(orderHeader.getId());
			o.setUpdated(new Date(updatedTime()));
			o.setTotal(orderHeader.getTotal());
			o.setBalanceDue(orderHeader.getBalanceDue());
			o.setServiceTax(orderHeader.getServiceTax());
			o.setPriceTax4(orderHeader.getPriceTax4());
			o.setPriceTax1(orderHeader.getPriceTax1());
			o.setPriceTax2(orderHeader.getPriceTax2());
			o.setPriceTax3(orderHeader.getPriceTax3());
			o.setPriceExtended(orderHeader.getPriceExtended());
			o.setPriceGratuity(orderHeader.getPriceGratuity());
			o.setPriceDiscount(orderHeader.getPriceDiscount());
			o.setGratuity(orderHeader.getGratuity());
			o.setAmountPaid(orderHeader.getAmountPaid());
			o.setSubTotal(orderHeader.getSubTotal());
			o.setLocationsId(orderHeader.getLocationsId());
			o.setAddressShipping(orderHeader.getAddressShipping());
			o.setAddressBilling(orderHeader.getAddressBilling());
			o.setDiscountsId(orderHeader.getDiscountsId());
			o.setDiscountsName(orderHeader.getDiscountsName());
			o.setDiscountsTypeId(orderHeader.getDiscountsTypeId());
			o.setDiscountsTypeName(orderHeader.getDiscountsTypeName());
			o.setDiscountsValue(orderHeader.getDiscountsValue());
			o.setPointOfServiceCount(orderHeader.getPointOfServiceCount());
			o.setSessionKey(orderHeader.getSessionKey());
			o.setFirstName(orderHeader.getFirstName());
			o.setLastName(orderHeader.getLastName());
			o.setUpdatedBy(orderHeader.getUpdatedBy());
			o.setReferenceNumber(referenceNumber);
			o.setReservationsId(orderHeader.getReservationsId());
			if (orderHeader.getSplitCount() != null) {
				o.setSplitCount(orderHeader.getSplitCount());
			}

			if (orderHeader.getPaymentWaysId() != null) {
				o.setPaymentWaysId(orderHeader.getPaymentWaysId());
			}

			if (orderHeader.getOrderSourceId() != null) {
				o.setOrderSourceId(orderHeader.getOrderSourceId());
			}
			o.setTaxDisplayName1(orderHeader.getTaxDisplayName1());
			o.setTaxDisplayName2(orderHeader.getTaxDisplayName2());
			o.setTaxDisplayName3(orderHeader.getTaxDisplayName3());
			o.setTaxDisplayName4(orderHeader.getTaxDisplayName4());
			o.setTaxName1(orderHeader.getTaxName1());
			o.setTaxName2(orderHeader.getTaxName2());
			o.setTaxName3(orderHeader.getTaxName3());
			o.setTaxName4(orderHeader.getTaxName4());
			o.setTaxRate1(orderHeader.getTaxRate1());
			o.setTaxRate2(orderHeader.getTaxRate2());
			o.setTaxRate3(orderHeader.getTaxRate3());
			o.setTaxRate4(orderHeader.getTaxRate4());
			o.setTotalTax(orderHeader.getTotalTax());
			o.setRoundOffTotal(orderHeader.getRoundOffTotal());
			o.setIsGratuityApplied(orderHeader.getIsGratuityApplied());
			o.setDiscountDisplayName(orderHeader.getDiscountDisplayName());
			o.setUsersId(orderHeader.getUsersId());
			o.setCashierId(orderHeader.getCashierId());
			o.setIsTabOrder(orderHeader.getIsTabOrder());
			o.setTaxExemptId(orderHeader.getTaxExemptId());
			o.setCreatedBy(orderHeader.getCreatedBy());
			o.setUpdatedBy(orderHeader.getUpdatedBy());
			o.setIpAddress(orderHeader.getIpAddress());
			o.setReferenceNumber(referenceNumber);
			o.setScheduleDateTime(orderHeader.getScheduleDateTime());
			o.setOpenTime(new TimezoneTime().getGMTTimeInMilis());
			o.setDate(orderHeader.getDate());
			o.setNirvanaXpBatchNumber(batchId);
			o.setIsSeatWiseOrder(order.getIsSeatWiseOrder());
			// #47168: when we do check Present from order screen then the
			// discount
			// getting removed. iOS.
			o.setCalculatedDiscountValue(order.getCalculatedDiscountValue());

			// o.setCalculatedDiscountValue(orderHeader.getCalculatedDiscountValue());
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setOrderHeaderToSalesTax(order.getOrderHeaderToSalesTax());
			o.setShiftSlotId(order.getShiftSlotId());
			o.setPriceDiscountItemLevel(order.getPriceDiscountItemLevel());
			o.setDeliveryOptionId(order.getDeliveryOptionId());
			o.setDeliveryCharges(order.getDeliveryCharges());

			Location l = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					o.getLocationsId());

			if (order.getId() != null) {
				// adding order dequence
				order.setOrderNumber(orderHeader.getOrderNumber());
				o.setOrderNumber(orderHeader.getOrderNumber());
				o.setCreated(orderHeader.getCreated());
				o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(rootlocationId, em));
				o = em.merge(o);
			} else {
				o.setCreated(new Date(updatedTime()));
				o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(rootlocationId, em));
				if (o.getId() != null) {
					String orderNumber = new StoreForwardUtility().generateOrderNumber(em, rootlocationId, httpRequest,
							Integer.parseInt(orderPacket.getMerchantId()), orderPacket, o.getUpdatedBy());
					o.setId(orderNumber);
					o.setOrderNumber(new StoreForwardUtility().generateOrderNumberNew(rootlocationId, em,
							orderHeader.getNirvanaXpBatchNumber(), "order_header"));
					o.setMergeOrderId(o.getId());
				}

			}

			if (orderHeader.getOrderPaymentDetails() != null) {
				List<OrderPaymentDetail> details = new ArrayList<OrderPaymentDetail>();
				for (OrderPaymentDetail detail : orderHeader.getOrderPaymentDetails()) {
					detail.setOrderHeaderId(o.getId());
					detail = em.merge(detail);
					details.add(detail);
					insertIntoOrderPaymentDetailHistory(em, detail);

				}
				o.setOrderPaymentDetails(details);

			}

			if (orderPacket.getIsDigitalMenu() == 0 && order.getId() != null) {
				if (sourceGroup.getName().equals("Pick Up") || sourceGroup.getName().equals("Delivery")) {
					updateShiftSlotCurrentActiveShiftCount(httpRequest, em, shiftSlotActiveClientInfo, o, true, true);
				}
			}
			// ------------Change by uzma- for order object by id ------
			o = manageOrderRelationship(em, o);
			// find order
			order_his = getOrderById(em, o.getId());
			// insert into order status history
			// insertIntoOrderStatusHistory(em, order_his);
			if (orderHeader.getOrderDetailItems() != null) {
				for (OrderDetailItem orderDetailItem : orderHeader.getOrderDetailItems()) {

					orderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderDetailItem.setOrderHeaderId(o.getId());
					// add or update the order detail item
					if (orderDetailItem.getId() != null) {
						orderDetailItem
								.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(rootlocationId, em));
						orderDetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						orderDetailItem.setId(new StoreForwardUtility().generateDynamicBigIntId(em, rootlocationId,
								httpRequest, "order_detail_items"));
						orderDetailItem = em.merge(orderDetailItem);

					} else {
						em.merge(orderDetailItem);
					}
					// add orderdetails item attribute also if sent by
					// client
					if (orderDetailItem.getOrderDetailAttributes() != null
							&& orderDetailItem.getOrderDetailAttributes().size() > 0) {

						for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
							orderDetailAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

							orderDetailAttribute.setOrderDetailItemId(orderDetailItem.getId());
							if (orderDetailAttribute.getId() != null) {
								// its trying to insert first time
								orderDetailAttribute.setLocalTime(
										new TimezoneTime().getLocationSpecificTimeToAdd(rootlocationId, em));
								orderDetailAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
								orderDetailAttribute.setId(new StoreForwardUtility().generateDynamicBigIntId(em,
										rootlocationId, httpRequest, "order_detail_attribute"));
								orderDetailAttribute = em.merge(orderDetailAttribute);

							} else {
								// its trying to update the already
								// existing
								orderDetailAttribute.setLocalTime(
										new TimezoneTime().getLocationSpecificTimeToAdd(rootlocationId, em));
								em.merge(orderDetailAttribute);
							}
						}
					}

				}

				o.setOrderDetailItems(orderHeader.getOrderDetailItems());

			}

			// we need to save the order detail item and order detail item
			// attribute that is sent by the client always
			order_his.setOrderDetailItems(orderHeader.getOrderDetailItems());
			order_his.setOrderPaymentDetails(o.getOrderPaymentDetails());
			int merchantId = Integer.parseInt(orderPacket.getMerchantId().trim());

			order_his = updateQRCodeAndHistory(httpRequest, em, o, merchantId, rootlocationId,
					orderPacket.getSchemaName()).getOrderHeader();

			logger.severe(
					"o.getScheduleDateTime()========================================================================="
							+ o.getScheduleDateTime());

			return o;
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;

	}

	/**
	 * Update order to card.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public OrderHeader updateOrderToCard(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NirvanaXPException, ParseException {
		// todo shlok need
		// modularise code
		OrderHeader order = orderPacket.getOrderHeader();

		boolean isPartySizeUpdated = false;
		OrderHeader o = getOrderById(em, order.getId());
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				o.getOrderStatusId());

		// check if order party size is updated, then we must update
		// associated reservation party size too
		if (o.getPointOfServiceCount() != order.getPointOfServiceCount()) {
			isPartySizeUpdated = true;
		}

		if (orderStatus != null && (!orderStatus.getName().equals("Ready to Order"))) {

			// verifyOrderSubTotalAmount(order);
			// get all order haeder by id

			String statusId = o.getOrderStatusId();

			OrderHeaderCalculation calculation = new OrderHeaderCalculation();
			order = calculation.getOrderHeaderCalculation(em, order, null);

			// set the all the varaible
			o.setOrderStatusId(order.getOrderStatusId());
			o.setUpdated(new Date(updatedTime()));
			o.setTotal(order.getTotal());
			o.setBalanceDue(order.getBalanceDue());
			o.setServiceTax(order.getServiceTax());
			o.setPriceTax4(order.getPriceTax4());
			o.setPriceTax1(order.getPriceTax1());
			o.setPriceTax2(order.getPriceTax2());
			o.setPriceTax3(order.getPriceTax3());
			o.setPriceExtended(order.getPriceExtended());
			o.setPriceGratuity(order.getPriceGratuity());
			o.setPriceDiscount(order.getPriceDiscount());
			o.setGratuity(order.getGratuity());
			o.setAmountPaid(order.getAmountPaid());
			o.setSubTotal(order.getSubTotal());
			o.setLocationsId(order.getLocationsId());
			o.setAddressShipping(order.getAddressShipping());
			o.setAddressBilling(order.getAddressBilling());
			o.setDiscountsId(order.getDiscountsId());
			o.setDiscountsName(order.getDiscountsName());
			o.setDiscountsTypeId(order.getDiscountsTypeId());
			o.setDiscountsTypeName(order.getDiscountsTypeName());
			o.setDiscountsValue(order.getDiscountsValue());
			o.setPointOfServiceCount(order.getPointOfServiceCount());
			o.setSessionKey(order.getSessionKey());
			o.setFirstName(order.getFirstName());
			o.setLastName(order.getLastName());
			o.setUpdatedBy(order.getUpdatedBy());
			if (order.getSplitCount() != null) {
				o.setSplitCount(order.getSplitCount());
			}

			if (order.getPaymentWaysId() != null) {
				o.setPaymentWaysId(order.getPaymentWaysId());
			}

			if (order.getOrderSourceId() != null) {
				o.setOrderSourceId(order.getOrderSourceId());
			}
			o.setTaxDisplayName1(order.getTaxDisplayName1());
			o.setTaxDisplayName2(order.getTaxDisplayName2());
			o.setTaxDisplayName3(order.getTaxDisplayName3());
			o.setTaxDisplayName4(order.getTaxDisplayName4());
			o.setTaxName1(order.getTaxName1());
			o.setTaxName2(order.getTaxName2());
			o.setTaxName3(order.getTaxName3());
			o.setTaxName4(order.getTaxName4());
			o.setTaxRate1(order.getTaxRate1());
			o.setTaxRate2(order.getTaxRate2());
			o.setTaxRate3(order.getTaxRate3());
			o.setTaxRate4(order.getTaxRate4());
			o.setTotalTax(order.getTotalTax());
			o.setRoundOffTotal(order.getRoundOffTotal());
			o.setIsGratuityApplied(order.getIsGratuityApplied());
			o.setDiscountDisplayName(order.getDiscountDisplayName());
			o.setUsersId(order.getUsersId());
			o.setCashierId(order.getCashierId());
			o.setIsTabOrder(order.getIsTabOrder());
			o.setTaxExemptId(order.getTaxExemptId());
			o.setIsSeatWiseOrder(order.getIsSeatWiseOrder());
			o.setCalculatedDiscountValue(order.getCalculatedDiscountValue());
			o.setPriceDiscountItemLevel(order.getPriceDiscountItemLevel());
			// added to insert current time of the location in the
			// order
			// header
			TimezoneTime timezoneTime = new TimezoneTime();
			String locationId = order.getLocationsId();

			String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(locationId, em);
			if (currentDateTime != null && currentDateTime.length > 0) {
				o.setDate(currentDateTime[0]);
				if (order.getScheduleDateTime() == null) {
					o.setScheduleDateTime(currentDateTime[2]);
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationId, em));
				} else {
					o.setScheduleDateTime(
							timezoneTime.getDateAccordingToGMT(order.getScheduleDateTime(), locationId, em));
				}

			}
			// find order
			// get order before update
			o.setMergeOrderId(order.getMergeOrderId());
			OrderHeader newOH = em.merge(o);
			o.setUpdated(newOH.getUpdated());
			// tax add
			o = manageOrderRelationship(em, o);
			// ------------Change by uzma- for order object by id ------
			// find order
			OrderHeader order_his = getOrderById(em, order.getId());

			if (statusId != order_his.getOrderStatusId()) {
				// insert into order status history
				insertIntoOrderStatusHistory(em, order_his);

			}

			// we need to save the order detail item and order detail item
			// attribute that is sent by the client always
			List<OrderDetailItem> orderDetailItemsListOfHistory = order_his.getOrderDetailItems();
			order_his.setOrderDetailItems(order.getOrderDetailItems());

			// insert into order history
			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order_his, em);

			// if party size is updated, then update the guest count of
			// reservations
			if (isPartySizeUpdated) {
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, em,
						o.getReservationsId());
				if (reservation.getPartySize() != o.getPointOfServiceCount()) {

					reservation.setSessionKey(o.getSessionKey());
					updateReservationPartySize(reservation, o.getPointOfServiceCount(), em);

					// also insert this into reservation history
					new InsertIntoHistory().insertReservationIntoHistory(httpRequest, reservation, em);
					// tell client party size is updated
					order_his.setPartySizeUpdated(true);
				}
			}

			order_his.setOrderDetailItems(orderDetailItemsListOfHistory);

			return order_his;
		} else {
			return o;
		}

	}

	/**
	 * Update user visit count.
	 *
	 * @param em
	 *            the em
	 * @param userID
	 *            the user ID
	 * @return the user
	 */
	private User updateUserVisitCount(EntityManager em, String userId) {
		User user = (User) new CommonMethods().getObjectById("User", em, User.class, userId);
		if (user != null) {
			user.setVisitCount(user.getVisitCount() + 1);
			em.merge(user);
		}

		return user;
	}

	/**
	 * Close business.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param updatedBy
	 *            the updated by
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	public boolean closeBusiness(HttpServletRequest httpRequest, EntityManager em, String updatedBy, String locationId,
			String sessionId, PostPacket packet, String date) throws Exception {

		// todo shlok need
		// modularise code
		BatchDetail batchDetail = null;

		List<OrderHeader> headers = new ArrayList<OrderHeader>();

		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			try {
				headers = getAllOrderPaymentDetailsByUserIdLocationBatchWise(httpRequest, em, updatedBy, locationId,
						sessionId);

			} catch (Exception e) {
				e.printStackTrace();
				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}

			try {
				batchDetail = getActiveBatch(httpRequest, em, locationId, sessionId, false, packet, updatedBy);
			} catch (Exception e1) {
				logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		if (batchDetail != null) {

			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				// code of settle records for mosambee
				tx = em.getTransaction();
				tx.begin();
				updatePaymentPaymentMosambee(em, batchDetail.getId(), httpRequest);

				tx.commit();
				// commiting transaction because in next step we are checking
				// all
				// settled trasaction in db
				String query = " select opd.order_header_id,ptt.name as payment_transaction_type_name,ts.name from order_payment_details opd "
						+ " left join payment_method pm on opd.payment_method_id=pm.id  "
						+ " left join payment_method_type pmt on pmt.id=pm.payment_method_type_id "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id = opd.transaction_status_id "
						+ " where nirvanaxp_batch_number=?  and pmt.name in ('Credit Card') and ts.name not in ('Failed')  order by opd.order_header_id,opd.id ";

				@SuppressWarnings("unchecked")
				List<Object[]> result = em.createNativeQuery(query).setParameter(1, batchDetail.getId())
						.getResultList();

				String orderId = null;
				boolean orderSettled = false;
				List<String> orderHeader = new ArrayList<String>();
				boolean batchHavingIssue = false;
				// boolean isOrderTraversed = false;
				int i = 0;
				for (Object[] objRow : result) {
					i++;
					String currentOrderId = (String) objRow[0];
					if (orderId != currentOrderId) {

						if (orderId == null || orderSettled) {

							orderSettled = false;
							orderId = currentOrderId;
							if ("CaptureAll".equals((String) objRow[1]) || "Void".equals((String) objRow[1])
									|| "Cash Refunded".equals((String) objRow[2])
									|| "CC Refunded".equals((String) objRow[2])) {
								orderSettled = true;
							}
							if (result.size() == i) {
								if (!orderSettled) {
									batchHavingIssue = true;
									orderHeader.add(orderId);
								}
							}
						} else {
							batchHavingIssue = true;
							orderHeader.add(orderId);
							orderId = currentOrderId;
							if ("CaptureAll".equals((String) objRow[1]) || "Void".equals((String) objRow[1])
									|| "Cash Refunded".equals((String) objRow[2])
									|| "CC Refunded".equals((String) objRow[2])) {
								orderSettled = true;
							}
							continue;
						}

					} else {
						if ("CaptureAll".equals((String) objRow[1]) || "Void".equals((String) objRow[1])
								|| "Cash Refunded".equals((String) objRow[2])
								|| "CC Refunded".equals((String) objRow[2])) {
							orderSettled = true;
						}
						if (result.size() == i) {
							if (!orderSettled) {
								batchHavingIssue = true;
								orderHeader.add(currentOrderId);
							}
						}
					}

				}
				String orderListHavingIssues;
				if (orderHeader != null && orderHeader.size() > 0) {
					orderListHavingIssues = "";
					for (int j = 0; j < orderHeader.size(); j++) {
						if (j == (orderHeader.size() - 1)) {
							orderListHavingIssues += orderHeader.get(j);
						} else {
							orderListHavingIssues += orderHeader.get(j) + ",";
						}
					}
					if (batchHavingIssue) {
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(
								MessageConstants.ERROR_CODE_UNSETTLED_TRANSACTION_PRESENT,
								MessageConstants.ERROR_MESSAGE_UNSETTLED_TRANSACTION_PRESENT_DISPLAY_MESSAGE,
								orderListHavingIssues));
					}
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			} finally {
				LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			}

			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				batchDetail.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
				batchDetail.setStatus("C");
				batchDetail.setUpdatedBy(updatedBy);
				tx = em.getTransaction();
				tx.begin();
				em.merge(batchDetail);

				try {
					BatchDetailPacket newBDP = PaymentBatchManager.getInstance().createPacket(packet);
					newBDP.setBatchDetail(batchDetail);
					String json = new StoreForwardUtility().returnJsonPacket(newBDP, "BatchDetailPacket", httpRequest);
					new StoreForwardUtility().callSynchPacketsWithServerForGeneric(json, httpRequest,
							packet.getLocationId(), Integer.parseInt(packet.getMerchantId()),
							"/OrderManagementServiceV6/createBatchForSynch");
				} catch (Exception e) {
					logger.severe(e);
				}
				PaymentBatchManager.getInstance().closeCurrentActiveBatchAndInitNext(httpRequest, sessionId,
						locationId);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			} catch (Exception e) {
				logger.severe(e);
			} finally {
				LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			}
			// Send Email EOD summary
			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				tx.begin();
				new StoreForwardUtility().resetOrderNumber(em, locationId, "order_header", true);
				tx.commit();
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

			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				tx.begin();
				new StoreForwardUtility().resetOrderNumber(em, locationId, "employee_operation_to_cash_register", true);
				tx.commit();
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
			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				tx.begin();
				sendEODSettledmentMail(httpRequest, em, updatedBy, locationId, sessionId, batchDetail, headers, null);
				tx.commit();

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

			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
				OrderManagementServiceBean bean = new OrderManagementServiceBean();
				tx = em.getTransaction();
				tx.begin();
				bean.sendEODMailForTipSettlementFromEOD(httpRequest, em, locationId, batchDetail, null);
				tx.commit();

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

			try {
				FutureUpdateQueue queue = new FutureUpdateQueue(httpRequest, locationId,
						httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), date);
				Thread t = new Thread(queue);
				t.start();
			}

			catch (Exception e) {
				logger.severe(e);
			}
			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				tx.begin();
				String queryString = "UPDATE order_source_group SET avg_wait_time=0  WHERE avg_wait_time !=0 and locations_id=?";
				em.createNativeQuery(queryString, OrderSourceGroup.class).setParameter(1, locationId).executeUpdate();
				tx.commit();
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
			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				tx.begin();
				String queryString = "delete from publisher  where status ='S' ";
				em.createNativeQuery(queryString, Publisher.class).executeUpdate();
				tx.commit();
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
			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				tx.begin();
				String queryString = "delete from publisher_history";
				em.createNativeQuery(queryString, Publisher.class).executeUpdate();
				tx.commit();
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
			// tip settlement queue.
			int count = 0;
			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				count = new OrderManagementServiceBean().getClockedInEmployees(em, batchDetail.getStartTime(),
						batchDetail.getCloseTime());
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

			// removing because id is conflicting in case of reset
			// try
			// {
			// em =
			// LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
			// sessionId);
			// tx = em.getTransaction();
			// tx.begin();
			// Location location=(Location) new
			// CommonMethods().getObjectById("Location", em,Location.class,
			// locationId);;
			// PaymentBatchManager.getInstance().resetCountOfOrderNumber(location.getBusinessId(),
			// em);
			// tx.commit();
			// }
			// catch (RuntimeException e)
			// {
			// if (tx != null && tx.isActive())
			// {
			// tx.rollback();
			// }
			// logger.severe(e);
			// }
			// catch (Exception e)
			// {
			// logger.severe(e);
			// }
			// finally
			// {
			// LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			// }
			try {
				if (count == 0) {
					TipSettlementQueue queue = new TipSettlementQueue(httpRequest, locationId,
							httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME), updatedBy, headers,
							batchDetail);
					Thread t = new Thread(queue);
					t.start();
				}
			} catch (Exception e) {
				logger.severe(e);
			}

			/*
			 * try {
			 * 
			 * //////// NirvanaIndex nirvanaIndex = null; try { Location
			 * location = (Location) new
			 * CommonMethods().getObjectById("Location", em,Location.class,
			 * locationId);; String queryString =
			 * "select n from NirvanaIndex n where n.businessId =?  ";
			 * TypedQuery<NirvanaIndex> query = em.createQuery(queryString,
			 * NirvanaIndex.class).setParameter(1,location.getBusinessId() );
			 * nirvanaIndex = query.getSingleResult(); } catch
			 * (NoResultException e) { // todo shlok need to handle // proper
			 * exception logger.severe(e); } // creating new nirvanaIndex for
			 * first time if (nirvanaIndex != null) {
			 * nirvanaIndex.setOrderNumber(0); nirvanaIndex =
			 * em.merge(nirvanaIndex);
			 * 
			 * }
			 * 
			 * 
			 * } catch (Exception e) { // TODO: handle exception
			 * logger.severe(e); }
			 */

			LocationSetting locationSetting = null;
			try {
				em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
				tx = em.getTransaction();
				tx.begin();
				try {
					locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
				} catch (Exception e) {
					e.printStackTrace();
					logger.severe(httpRequest, "no location setting found for locationId: " + locationId);
				}
				Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
						locationId);
				;
				if (locationSetting.getIsUnassignServerOnBatchSettlement() == 1) {
					try {
						unassignedServerFromLocation(httpRequest, em, location.getBusinessId());
						sendPacketForBroadcast(
								POSNServiceOperations.LookupService_addUpdateLocationsToShiftPreAssignServer.name(),
								packet, httpRequest);
					} catch (Exception e) {
						logger.severe(e);
					}
				}
				tx.commit();

			} catch (RuntimeException e) {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			} catch (Exception e) {
				logger.severe(e);
			} finally {
				LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			}

			return true;
		} else {
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(
					MessageConstants.ERROR_CODE_BATCH_ID_NOT_PRESENT_WITH_DATE__EXCEPTION,
					MessageConstants.ERROR_MESSAGE_BATCH_ID_NOT_PRESENT_WITH_DATE_DISPLAY_MESSAGE, null));

		}
	}

	/**
	 * Gets the POSN partners by business id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param businessId
	 *            the business id
	 * @return the POSN partners by business id
	 */
	POSNPartners getPOSNPartnersByBusinessId(HttpServletRequest httpRequest, EntityManager em, int businessId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
		Root<POSNPartners> r = criteria.from(POSNPartners.class);
		TypedQuery<POSNPartners> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(POSNPartners_.businessId), businessId),
						builder.equal(r.get(POSNPartners_.partnerName), "BusinessApp")));
		POSNPartners result = null;
		try {
			result = query.getSingleResult();
		} catch (Exception e) {

			logger.severe(httpRequest, "Could not find POSNPartners with businessId : " + businessId);
		}
		return result;
	}

	public POSNPartners getPOSNPartnersByBusinessIdForEward(HttpServletRequest httpRequest, EntityManager em,
			int businessId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
		Root<POSNPartners> r = criteria.from(POSNPartners.class);
		TypedQuery<POSNPartners> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(POSNPartners_.businessId), businessId),
						builder.equal(r.get(POSNPartners_.partnerName), "Ewards"),
						builder.notEqual(r.get(POSNPartners_.status), "D")));
		POSNPartners result = null;
		try {
			result = (POSNPartners) query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);
			logger.severe(httpRequest, "Could not find POSNPartners with businessId : " + businessId);
		}
		return result;
	}

	/**
	 * Gets the business with refrence number.
	 *
	 * @param em
	 *            the em
	 * @param businessId
	 *            the business id
	 * @return the business with refrence number
	 */
	public String getBusinessWithRefrenceNumber(EntityManager em, int businessId) {
		String refNo = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
		Root<POSNPartners> r = criteria.from(POSNPartners.class);
		TypedQuery<POSNPartners> query = em.createQuery(criteria.select(r).where(new Predicate[] {
				builder.equal(r.get("businessId"), businessId), builder.equal(r.get("partnerName"), "BusinessApp") }));

		POSNPartners partnerRefNo = query.getSingleResult();
		if (partnerRefNo != null)
			refNo = partnerRefNo.getReferenceNumber();
		return refNo;
	}

	/**
	 * Gets the all order payment details by user id location batch wise.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location batch wise
	 */
	public List<OrderHeader> getAllOrderPaymentDetailsByUserIdLocationBatchWise(HttpServletRequest httpRequest,
			EntityManager em, String userId, String locationId, String sessionId) {

		int isManager = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = null;
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();
		// TimezoneTime time = new TimezoneTime();
		OrderHeader header = new OrderHeader();

		BatchDetail batchDetail = null;
		try {
			batchDetail = getActiveBatch(httpRequest, em, locationId, sessionId, false, null, userId);
		} catch (Exception e1) {

			logger.severe(httpRequest, e1, "no active batch detail found for locationId: " + locationId);

		}
		if (batchDetail != null) {
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;
			String utcTime = "";
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			if (batchDetail.getCloseTime() == 0) {
				utcTime = date.format(new Date()) + " 23:59:59";

			} else {
				utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
			}

			String gmtStartTime = dateFormatGmt.format(new Date(batchDetail.getStartTime()));

			String query = "SELECT count(*) FROM `users_to_roles` utr left join roles "
					+ "r on r.id=utr.roles_id where utr.users_id = ? and r.role_name in ('Account Admin','Business Admin','POS Supervisor')";

			Object result = em.createNativeQuery(query).setParameter(1, userId).getSingleResult();

			if (result != null) {
				// if this has primary key not 0
				if (((BigInteger) result).intValue() > 0) {
					isManager = 1;
				}
			} else {
				logger.severe(httpRequest, "Could not get count of users to roles");
			}

			if (isManager == 0) {

				// order source production excluded from list
				// not manager
				String queryString = "SELECT  " + objectsWithColumnStr
						+ "   FROM order_header oh join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id   "
						+ " left join order_payment_details opd on oh.id=opd.order_header_id "
						+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
						+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
						+ " left join payment_method pm on opd.payment_method_id= pm.id "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
						+ " left JOIN order_source osou on oh.order_source_id = osou.id "
						+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
						+ " JOIN order_status os on oh.order_status_id = os.id left JOIN order_source_group osg on os.order_source_group_id = osg.id "
						+ "  WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
						+ " and os.name not in ('Void Order ','Cancel Order ')"
						+ " and osou.name != 'Production'  and ((oh.schedule_date_time between ?  and ? )  "
						+ " or oh.id in (Select order_header_id from order_payment_details where nirvanaxp_batch_number = ?  ))  "
						+ "   and oh.created_by=? order by oh.id,opd.id asc ";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
						.setParameter(2, gmtStartTime).setParameter(3, utcTime).setParameter(4, batchDetail.getId())
						.setParameter(5, userId).getResultList();

				int index = 0;
				orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
						index);
			} else {
				// order source production excluded from list
				// manager
				String queryString = "SELECT  " + objectsWithColumnStr
						+ "  FROM order_header oh join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id   "
						+ " left join order_payment_details opd on oh.id=opd.order_header_id "
						+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
						+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
						+ " left join payment_method pm on opd.payment_method_id= pm.id "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id=opd.transaction_status_id left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
						+ " JOIN order_status os on oh.order_status_id = os.id "
						+ " left JOIN order_source osou on oh.order_source_id = osou.id "
						+ "left JOIN order_source_group osg on os.order_source_group_id = osg.id "
						+ "  WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
						+ " and osou.name != 'Production' and os.name not in ('Void Order ','Cancel Order ')   and ((oh.schedule_date_time between ?  and ? )  "
						+ " or oh.id in (Select order_header_id from order_payment_details where nirvanaxp_batch_number = ? ))  "
						+ "   order by oh.id,opd.id asc ";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
						.setParameter(2, gmtStartTime).setParameter(3, utcTime).setParameter(4, batchDetail.getId())
						.getResultList();

				int index = 0;
				orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
						index);
			}
		}
		return orderHeaders;
	}

	/**
	 * Gets the all order payment details by user id location batch wise for
	 * datacap with first data.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location batch wise for
	 *         datacap with first data
	 */
	public List<OrderHeader> getAllOrderPaymentDetailsByUserIdLocationBatchWiseForDatacapWithFirstData(
			HttpServletRequest httpRequest, EntityManager em, String userId, String locationId, String sessionId) {

		int isManager = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = null;
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();
		// TimezoneTime time = new TimezoneTime();
		OrderHeader header = new OrderHeader();

		BatchDetail batchDetail = null;
		try {
			batchDetail = getActiveBatch(httpRequest, em, locationId, sessionId, false, null, userId);
		} catch (Exception e1) {

			logger.severe(httpRequest, e1, "no active batch detail found for locationId: " + locationId);

		}
		if (batchDetail != null) {
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;
			String utcTime = "";
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			if (batchDetail.getCloseTime() == 0) {
				utcTime = date.format(new Date()) + " 23:59:59";

			} else {
				utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
			}

			String gmtStartTime = dateFormatGmt.format(new Date(batchDetail.getStartTime()));

			String query = "SELECT count(*) FROM `users_to_roles` utr left join roles "
					+ "r on r.id=utr.roles_id where utr.users_id = ? and r.role_name in ('Account Admin','Business Admin','POS Supervisor')";

			Object result = em.createNativeQuery(query).setParameter(1, userId).getSingleResult();

			if (result != null) {
				// if this has primary key not 0
				if (((BigInteger) result).intValue() > 0) {
					isManager = 1;
				}
			} else {
				logger.severe(httpRequest, "Could not get count of users to roles");
			}

			// order source production excluded from list
			// manager
			String queryString = "SELECT  " + objectsWithColumnStr
					+ "  FROM order_header oh join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id   "
					+ " left join order_payment_details opd on oh.id=opd.order_header_id "
					+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
					+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
					+ " left join payment_method pm on opd.payment_method_id= pm.id "
					+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ " left JOIN order_source osou on oh.order_source_id = osou.id "
					+ " left join transaction_status ts on ts.id=opd.transaction_status_id left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ " JOIN order_status os on oh.order_status_id = os.id left JOIN order_source_group osg on os.order_source_group_id = osg.id "
					+ "  WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? )  "
					+ " and  (opd.order_source_group_to_paymentgatewaytype_id "
					+ " in (select osgpt.id from order_source_group_to_paymentgateway_type osgpt  "
					+ " join paymentgateway_type pgt on osgpt.paymentgateway_type_id =  pgt.id  "
					+ " where pgt.name in ('DataCap with FirstData'))   or opd.order_source_to_paymentgatewaytype_id in "
					+ "  (select ospt.id from order_source_to_paymentgateway_type ospt "
					+ " join paymentgateway_type pgt on ospt.paymentgateway_type_id =  pgt.id  "
					+ " where pgt.name in ('DataCap with FirstData')))  and os.name not in ('Void Order ','Cancel Order') "
					+ "   and osou.name != 'Production' and ( 	 oh.id in (Select order_header_id  from order_payment_details where nirvanaxp_batch_number =? ))   "
					+ "   order by oh.id,opd.id asc  ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
					.setParameter(2, batchDetail.getId()).getResultList();

			int index = 0;
			orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
					index);

		}
		return orderHeaders;
	}

	/**
	 * Update order payment for tip save.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param tipSavedPacket
	 *            the tip saved packet
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeaderWithInventoryPostPacket updateOrderPaymentForTipSave(HttpServletRequest httpRequest,
			EntityManager em, TipSavedPacket tipSavedPacket) throws Exception {
		try {
			OrderHeader o = getOrderById(em, tipSavedPacket.getOrderHeaderId());
			TransactionStatus transactionStatus = getTransactionStatusByName(em, "Tip Saved");
			if (tipSavedPacket.getOrderPaymentDetails() != null) {
				for (OrderPaymentDetail orderPaymentDetail : o.getOrderPaymentDetails()) {
					for (OrderPaymentDetail detail : tipSavedPacket.getOrderPaymentDetails()) {
						if (detail.getId().compareTo(orderPaymentDetail.getId()) == 0) {
							// check datacap payment

							String[] values = getPaymentGatewayName(em,
									orderPaymentDetail.getOrderSourceGroupToPaymentGatewayTypeId(),
									orderPaymentDetail.getOrderSourceToPaymentGatewayTypeId());
							String gatewayName = values[0];
							String merchantID = values[1];
							String serverURL = values[2];
							String username = values[3];
							String password = values[4];
							String transDeviceId = values[5];
							String secureDeviceName = values[6];
							if (gatewayName != null && gatewayName.equals(DATACAP)) {
								DeviceToPinPad deviceToPinPad = (DeviceToPinPad) new CommonMethods().getObjectById(
										"DeviceToPinPad", em, DeviceToPinPad.class,
										orderPaymentDetail.getDeviceToPinPadId());
								PaymentGatewayToPinpad pinPad = (PaymentGatewayToPinpad) new CommonMethods()
										.getObjectById("PaymentGatewayToPinpad", em, PaymentGatewayToPinpad.class,
												deviceToPinPad.getPinPad());

								String requestParams = ProcessCreditCardRequestHelper.createTipSaveRequest(
										new BigDecimal(detail.getCreditcardTipAmt() + "").setScale(2,
												RoundingMode.HALF_UP) + "",
										orderPaymentDetail.getAmountPaid() + "", orderPaymentDetail.getAcqRefData(),
										orderPaymentDetail.getAuthCode(), merchantID, TranCode, "", "Allow",
										transDeviceId, pinPad.getIpAddress(), pinPad.getPort(),
										orderPaymentDetail.getProcessData(), orderPaymentDetail.getHostRefStr(),
										orderPaymentDetail.getPnRef(), secureDeviceName,
										orderPaymentDetail.getSequenceNo(), pinPad.getTerminalId(), "Dev1");
								logger.severe("Request Packet=" + requestParams);
								DataCapResponse capResponse = new ProcessCreditCardService(username, password,
										merchantID, TerminalID, deviceToPinPad.getDeviceId())
												.sendRequestToServer(httpRequest, em, requestParams, serverURL);
								// parse response if got positive response then
								// execute below code else skip current loop use
								// continue
								logger.severe("Response from gateway=" + capResponse);

								if (capResponse != null && capResponse.getrStream() != null
										&& capResponse.getrStream().getCmdStatus().equals("Error")) {
									throw new NirvanaXPException(
											new NirvanaServiceErrorResponse(capResponse.getrStream().getCmdStatus(),
													capResponse.getrStream().getTextResponse(),
													capResponse.getrStream().getTextResponse()));
								} else if (capResponse != null && capResponse.getrStream() != null
										&& capResponse.getrStream().getCmdStatus().equals("Declined")) {
									throw new NirvanaXPException(
											new NirvanaServiceErrorResponse(capResponse.getrStream().getCmdStatus(),
													capResponse.getrStream().getTextResponse(),
													capResponse.getrStream().getTextResponse()));
								} else if (capResponse != null && capResponse.getrStream() != null
										&& capResponse.getrStream().getCmdStatus().equals("Approved")) {
									// if i get positive response from gateway
									// for 2 transaction out of 5 then previous
									// code is not storing any device

									BigDecimal tipAmount = new BigDecimal(capResponse.getrStream().getGratuity());
									BigDecimal amountAuthorize = new BigDecimal(
											capResponse.getrStream().getAuthorize());
									if (tipAmount != null) {
										amountAuthorize = amountAuthorize.subtract(tipAmount);
									}
									em.getTransaction().begin();

									updateOrderPaymentDetails(transactionStatus, orderPaymentDetail, detail, em,
											amountAuthorize, tipAmount, detail.getCreditTermTip(),
											tipSavedPacket.getLocationId(), detail.getChequeTip());
									em.getTransaction().commit();
								}

							} else {
								// else condtion is for existing tip save for
								// bridge
								// pay and all
								BigDecimal tipAmount = detail.getCreditcardTipAmt();

								BigDecimal amountAuthorize = detail.getAmountPaid();
								em.getTransaction().begin();
								updateOrderPaymentDetails(transactionStatus, orderPaymentDetail, detail, em,
										amountAuthorize, tipAmount, detail.getCreditTermTip(),
										tipSavedPacket.getLocationId(), detail.getChequeTip());
								em.getTransaction().commit();
							}

						}

					}

				}
			}

			// insert into order history
			em.getTransaction().begin();
			insertIntoOrderStatusHistory(em, o);
			em.getTransaction().commit();
			logger.info("adding order payment detail for order: " + o.getId());
			OrderHeader new_order = getOrderById(em, o.getId());
			return new OrderHeaderWithInventoryPostPacket(new_order, null);
		} catch (RuntimeException e) {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		}

	}

	/**
	 * Update order payment details.
	 *
	 * @param transactionStatus
	 *            the transaction status
	 * @param oldOrderPaymentDetail
	 *            the old order payment detail
	 * @param newOrderPaymentDetail
	 *            the new order payment detail
	 * @param em
	 *            the em
	 * @param authorize
	 *            the authorize
	 * @param cardTip
	 *            the card tip
	 * @return the order payment detail
	 */
	private OrderPaymentDetail updateOrderPaymentDetails(TransactionStatus transactionStatus,
			OrderPaymentDetail oldOrderPaymentDetail, OrderPaymentDetail newOrderPaymentDetail, EntityManager em,
			BigDecimal authorize, BigDecimal cardTip, BigDecimal creditTermTip, String locationId,
			BigDecimal chequeTip) {

		try {
			int transectionTypeId = oldOrderPaymentDetail.getPaymentTransactionType().getId();

			/*
			 * if (transectionTypeId == getPaymentTransactionType(em,
			 * locationId, "Sale").getId() || (getPaymentTransactionType(em,
			 * locationId, "Credit") != null && transectionTypeId ==
			 * getPaymentTransactionType(em, locationId, "Credit").getId()) ||
			 * transectionTypeId != getPaymentTransactionType(em, locationId,
			 * "Void").getId() || transectionTypeId !=
			 * getPaymentTransactionType(em, locationId, "Refund").getId() ||
			 * transectionTypeId == getPaymentTransactionType(em, locationId,
			 * "Auth").getId() || transectionTypeId ==
			 * getPaymentTransactionType(em, locationId,
			 * "Manual CC Auth").getId() )
			 * 
			 * {
			 */

			CashRegisterRunningBalance balance = new CashRegisterRunningBalance();
			balance.setIsAmountCarryForwarded(0);
			balance.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			balance.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
			balance.setCreatedBy(oldOrderPaymentDetail.getCreatedBy());
			balance.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			balance.setUpdatedBy(newOrderPaymentDetail.getUpdatedBy());

			balance.setNirvanaXpBatchNumber(locationId + "-" + oldOrderPaymentDetail.getNirvanaXpBatchNumber());
			if (oldOrderPaymentDetail != null && oldOrderPaymentDetail.getRegister() != null) {
				balance.setRegisterId(oldOrderPaymentDetail.getRegister());
			}

			balance.setStatus("A");

			balance.setTransactionAmount(newOrderPaymentDetail.getCashTipAmt());
			CashRegisterRunningBalance resultSet = null;
			List<CashRegisterRunningBalance> result = null;

			try {
				String queryString = "select l from CashRegisterRunningBalance l where l.registerId =? order by id asc ";
				TypedQuery<CashRegisterRunningBalance> query = em
						.createQuery(queryString, CashRegisterRunningBalance.class)
						.setParameter(1, balance.getRegisterId());
				result = query.getResultList();
			} catch (Exception e) {
				// todo shlok need
				// handle proper exception
				logger.severe(e);
			}

			// todo shlok need
			// ?
			for (CashRegisterRunningBalance c : result) {
				resultSet = c;
				continue;
			}

			if (resultSet != null) {
				balance.setRunningBalance(resultSet.getRunningBalance());
			} else {
				balance.setRunningBalance(new BigDecimal(0));
			}
			balance.setRunningBalance(balance.getRunningBalance().subtract(oldOrderPaymentDetail.getCashTipAmt()));
			balance.setOpdId(newOrderPaymentDetail.getId());
			if (transectionTypeId == getPaymentTransactionType(em, locationId, "Sale").getId()
					|| (getPaymentTransactionType(em, locationId, "Credit") != null
							&& transectionTypeId == getPaymentTransactionType(em, locationId, "Credit").getId())
					|| transectionTypeId == getPaymentTransactionType(em, locationId, "Auth").getId()
					|| transectionTypeId == getPaymentTransactionType(em, locationId, "Manual CC Auth").getId()) {
				// add
				balance.setTransactionStatus("CR");
				balance.setRunningBalance(balance.getRunningBalance().add(balance.getTransactionAmount()));
				em.persist(balance);

			}
			/*
			 * else if (newOrderPaymentDetail.getIsRefunded() == 0 &&
			 * orderPaymentDetail
			 * .getPaymentTransactionType().getName().equals("Refund")) {
			 * //remove
			 * balance.setRunningBalance(balance.getRunningBalance().subtract
			 * (balance.getTransactionAmount()));
			 * balance.setTransactionStatus("DR"); em.persist(balance); }
			 */

			// }
		} catch (Exception e) {
			// TODO: handle exception
			logger.severe(e);
		}

		oldOrderPaymentDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		oldOrderPaymentDetail.setTransactionStatus(transactionStatus);

		if (newOrderPaymentDetail.getCashTipAmt() != null) {
			oldOrderPaymentDetail.setCashTipAmt(newOrderPaymentDetail.getCashTipAmt());
		}
		if (newOrderPaymentDetail.getCreditcardTipAmt() != null) {
			oldOrderPaymentDetail.setCreditcardTipAmt(cardTip);
		}
		if (newOrderPaymentDetail.getCreditTermTip() != null) {
			oldOrderPaymentDetail.setCreditTermTip(creditTermTip);
		}

		if (newOrderPaymentDetail.getChequeTip() != null) {
			oldOrderPaymentDetail.setChequeTip(chequeTip);
		}

		if (authorize != null) {
			oldOrderPaymentDetail.setAmountPaid(authorize);
		}

		logger.info("Order payment detail:" + oldOrderPaymentDetail.getId(), "status is:",
				oldOrderPaymentDetail.getTransactionStatus().getName());
		oldOrderPaymentDetail = em.merge(oldOrderPaymentDetail);
		// inserting into history
		insertIntoOrderPaymentDetailHistory(em, oldOrderPaymentDetail);
		return oldOrderPaymentDetail;
	}

	/**
	 * Gets the payment gateway name.
	 *
	 * @param em
	 *            the em
	 * @param orderSourceGroupToPaymentGatewayTypeId
	 *            the order source group to payment gateway type id
	 * @param orderSourceToPaymentGatewayTypeId
	 *            the order source to payment gateway type id
	 * @return the payment gateway name
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String[] getPaymentGatewayName(EntityManager em, int orderSourceGroupToPaymentGatewayTypeId,
			int orderSourceToPaymentGatewayTypeId) throws FileNotFoundException, IOException {
		String[] values = new String[7];
		PaymentGatewayType gatewayType = null;

		if (orderSourceToPaymentGatewayTypeId != 0) {
			OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType = em
					.find(OrderSourceToPaymentgatewayType.class, orderSourceToPaymentGatewayTypeId);
			gatewayType = em.find(PaymentGatewayType.class, orderSourceToPaymentgatewayType.getPaymentgatewayTypeId());
			if (gatewayType != null) {

				values[0] = gatewayType.getName();
				values[1] = orderSourceToPaymentgatewayType.getMerchantId();
				values[2] = gatewayType.getPaymentGatewayTransactionUrl();
				values[3] = orderSourceToPaymentgatewayType.getParameter1();
				values[4] = orderSourceToPaymentgatewayType.getPassword();
				values[5] = orderSourceToPaymentgatewayType.getParameter2(); // transdeviceid
				values[6] = orderSourceToPaymentgatewayType.getParameter3(); // securedevice
																				// name
			}

		} else if (orderSourceGroupToPaymentGatewayTypeId != 0) {
			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType = em
					.find(OrderSourceGroupToPaymentgatewayType.class, orderSourceGroupToPaymentGatewayTypeId);
			gatewayType = em.find(PaymentGatewayType.class,
					orderSourceGroupToPaymentgatewayType.getPaymentgatewayTypeId());

			values[0] = gatewayType.getName();
			values[1] = orderSourceGroupToPaymentgatewayType.getMerchantId();
			values[2] = gatewayType.getPaymentGatewayTransactionUrl();
			values[3] = orderSourceGroupToPaymentgatewayType.getParameter1();
			values[4] = orderSourceGroupToPaymentgatewayType.getPassword();
			values[5] = orderSourceGroupToPaymentgatewayType.getParameter2(); // transdeviceid
			values[6] = orderSourceGroupToPaymentgatewayType.getParameter3(); // securedevice
																				// name
		}
		// setting gateway name in array

		return values;
	}

	/**
	 * Gets the transaction status by name.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @return the transaction status by name
	 */
	private TransactionStatus getTransactionStatusByName(EntityManager em, String name) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TransactionStatus> criteria = builder.createQuery(TransactionStatus.class);
		Root<TransactionStatus> r = criteria.from(TransactionStatus.class);
		TypedQuery<TransactionStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(TransactionStatus_.name), name)));
		TransactionStatus result = query.getSingleResult();
		return result;
	}

	/**
	 * Gets the active batch.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param sessionId
	 *            the session id
	 * @param isCreateBatch
	 *            the is create batch
	 * @return the active batch
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	public BatchDetail getActiveBatch(HttpServletRequest httpRequest, EntityManager em, String locationId,
			String sessionId, boolean isCreateBatch, PostPacket packet, String updatedBy)
			throws IOException, InvalidSessionException {
		PaymentBatchManager batchManager = PaymentBatchManager.getInstance();
		BatchDetail batchDetai = batchManager.getCurrentBatchBySession(httpRequest, em, locationId, isCreateBatch,
				packet, updatedBy);
		return batchDetai;
	}

	/**
	 * Gets the active batch for pick date.
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
	 * @return the active batch for pick date
	 */
	@SuppressWarnings("unchecked")
	public List<String> getActiveBatchForPickDate(HttpServletRequest httpRequest, EntityManager em, String locationId,
			String startDate, String endDate) {

		// TODO should first check if dates are valid
		// TODO why are dates String and not Timestamp
		// TODO why only search for batch id and not full batch detail rows?
		String queryString = "select b.id from batch_detail b where b.location_id= ? and b.startTime between ? and ?  order by b.id asc ";
		List<String> resultSet = null;
		try {
			Query query = em.createNativeQuery(queryString).setParameter(1, locationId).setParameter(2, startDate)
					.setParameter(3, endDate);
			resultSet = query.getResultList();
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not find order with start date", startDate, "and end date", endDate);
		}

		if (resultSet == null || resultSet.size() == 0) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			try {
				Date start = dateFormat.parse(startDate);
				Date endd = dateFormat.parse(endDate);
				queryString = "select bd.id from batch_detail bd where bd.location_id= ?  and bd.startTime< ?  and (bd.closeTime > ? or bd.closeTime is null) order by bd.id asc";
				Query query = em.createNativeQuery(queryString).setParameter(1, locationId).setParameter(2, start)
						.setParameter(3, endd);
				resultSet = query.getResultList();
			} catch (Exception e) {
				logger.severe(httpRequest, e, "Could not find batch detail id for location " + locationId,
						"between start", startDate, "and end", endDate);
			}
		}
		return resultSet;
	}

	/**
	 * Gets the all order payment details by user id location batch wise for
	 * pick date.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the all order payment details by user id location batch wise for
	 *         pick date
	 */
	public List<OrderHeader> getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPickDate(
			HttpServletRequest httpRequest, EntityManager em, String userId, String locationId, String date) {

		// todo shlok need
		// modularise code
		int isManager = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = null;
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();

		TimezoneTime timezoneTime = new TimezoneTime();

		String pickStartDate = timezoneTime.getDateAccordingToGMT(date + " 00:00:00", locationId, em);
		String pickEndDate = timezoneTime.getDateAccordingToGMT(date + " 23:59:59", locationId, em);

		OrderHeader header = new OrderHeader();
		List<String> batchId = null;
		try {
			batchId = getActiveBatchForPickDate(httpRequest, em, locationId, pickStartDate, pickEndDate);
		} catch (Exception e1) {
			logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
		}

		BatchDetail startBatchDetail = null;
		BatchDetail endBatchDetail = null;
		if (batchId != null) {
			if (batchId.size() > 0) {
				String startBatchId = batchId.get(0);
				String endBatchId = batchId.get(batchId.size() - 1);

				startBatchDetail = em.find(BatchDetail.class, startBatchId);
				endBatchDetail = em.find(BatchDetail.class, endBatchId);

			}
		}
		// getting business id
		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;

		if (batchId != null && batchId.size() > 0) {

			String gmtEndTime = "";
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
			if (endBatchDetail.getCloseTime() == 0) {
				gmtEndTime = date2.format(new Date()) + " 23:59:59";

			} else {
				gmtEndTime = dateFormatGmt.format(new Date(endBatchDetail.getCloseTime()));
			}

			String gmtStartTime = dateFormatGmt.format(new Date(startBatchDetail.getStartTime()));

			String query = "SELECT count(*) FROM `users_to_roles` utr left join roles "
					+ "r on r.id=utr.roles_id where utr.users_id = ? and r.role_name in ('Account Admin','Business Admin','POS Supervisor')";

			Object result = em.createNativeQuery(query).setParameter(1, userId).getSingleResult();

			if (result != null) {
				// if this has primary key not 0
				if (((BigInteger) result).intValue() > 0) {
					isManager = 1;
				}
			} else {
				logger.severe(httpRequest, "Could not get count of users to roles");
			}

			if (isManager == 0) {

				// order source production excluded from list
				// not manager
				String queryString = "SELECT  " + objectsWithColumnStr
						+ "  FROM order_header oh join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id   "
						+ " left join order_payment_details opd on oh.id=opd.order_header_id"
						+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
						+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
						+ " left join payment_method pm on opd.payment_method_id= pm.id "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
						+ " left JOIN order_source osou on oh.order_source_id = osou.id "
						+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
						+ " JOIN order_status os on oh.order_status_id = os.id left JOIN order_source_group osg on os.order_source_group_id = osg.id    "
						+ "  WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
						+ " and os.name not in ('Void Order ','Cancel Order ') "
						+ " and osou.name != 'Production' and ((oh.schedule_date_time between ?  and ? )  "
						+ " or oh.id in (Select order_header_id from order_payment_details where created between ?  and ? ))  "
						+ "   and oh.created_by=? order by oh.id,opd.id asc ";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
						.setParameter(2, gmtStartTime).setParameter(3, gmtEndTime).setParameter(4, gmtStartTime)
						.setParameter(5, gmtEndTime).setParameter(6, userId).getResultList();

				int index = 0;
				orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
						index);
			} else {

				// order source production excluded from list
				// manager
				String queryString = "SELECT  " + objectsWithColumnStr
						+ " FROM order_header oh join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id   "
						+ " left join order_payment_details opd on oh.id=opd.order_header_id "
						+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
						+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
						+ " left join payment_method pm on opd.payment_method_id= pm.id "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
						+ " left JOIN order_source osou on oh.order_source_id = osou.id "
						+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
						+ " JOIN order_status os on oh.order_status_id = os.id left JOIN order_source_group osg on os.order_source_group_id = osg.id "
						+ "  WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
						+ " and os.name not in ('Void Order ','Cancel Order ') "
						+ " and osou.name != 'Production' and ((oh.schedule_date_time between ?  and ? )  "
						+ " or oh.id in (Select order_header_id from order_payment_details where created between ?  and ? ))  "
						+ "   order by oh.id,opd.id asc ";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
						.setParameter(2, gmtStartTime).setParameter(3, gmtEndTime).setParameter(4, gmtStartTime)
						.setParameter(5, gmtEndTime).getResultList();
				int index = 0;
				orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
						index);
			}
		}

		return orderHeaders;
	}

	/**
	 * Populate order header for payment.
	 *
	 * @param em
	 *            the em
	 * @param resultList
	 *            the result list
	 * @param orderHeaders
	 *            the order headers
	 * @param header
	 *            the header
	 * @param orderPaymentDetails
	 *            the order payment details
	 * @param index
	 *            the index
	 * @return the list
	 */
	private List<OrderHeader> populateOrderHeaderForPayment(EntityManager em, List<Object[]> resultList,
			List<OrderHeader> orderHeaders, OrderHeader header, List<OrderPaymentDetail> orderPaymentDetails,
			int index) {
		for (Object[] objRow : resultList) {
			++index;

			if ((String) objRow[125] != null && !((String) objRow[125]).equals(header.getId())) {
				if (header.getId() != null) {
					if (orderPaymentDetails != null) {
						if (orderPaymentDetails.size() > 0) {
							header.setOrderPaymentDetails(orderPaymentDetails);
						}
					}
					orderHeaders.add(header);
					header = new OrderHeader();
				}

				// ------------------ Add address object for address
				// shipping id and address billing id ---
				Address addressShipping = new Address();
				Address addressBilling = new Address();
				try {
					addressBilling.setAddressByResultSet(objRow, 202);
					addressShipping.setAddressByResultSet(objRow, 215);

				} catch (Exception e) {
					logger.severe(e);
				}

				objRow[132] = addressShipping;
				objRow[133] = addressBilling;

				header.setOrderHeaderByResultSetAllValue(objRow, 124);

				orderPaymentDetails = new ArrayList<OrderPaymentDetail>();
				header.setOrderDetailItems(
						getOrderDetailsItemWithItemGroupIdForOrderIdWithoutRemoveAndRecallItem(em, header.getId()));

			}

			OrderPaymentDetail paymentDetail = new OrderPaymentDetail();
			paymentDetail.setOrderPaymentDetailsResultSet(objRow);
			if (paymentDetail != null && paymentDetail.getId() != null) {
				paymentDetail.setOrderPaymentDetailsToSalesTax(
						getOrderPaymentDetailsToSalesTaxForOrderPaymentDetailsId(em, paymentDetail.getId()));

				orderPaymentDetails.add(paymentDetail);
			}
			if (index == resultList.size()) {
				if (orderPaymentDetails != null) {
					if (orderPaymentDetails.size() > 0) {
						header.setOrderPaymentDetails(orderPaymentDetails);
					}
				}
				orderHeaders.add(header);
			}

		}
		return orderHeaders;
	}

	/**
	 * Gets the all order payment details by user id location batch wise for
	 * payment gateway type id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param paymentGatewayTypeId
	 *            the payment gateway type id
	 * @param sessionId
	 *            the session id
	 * @return the all order payment details by user id location batch wise for
	 *         payment gateway type id
	 */
	public List<OrderHeader> getAllOrderPaymentDetailsByUserIdLocationBatchWiseForPaymentGatewayTypeId(
			HttpServletRequest httpRequest, EntityManager em, String userId, String locationId,
			int[] paymentGatewayTypeId, String sessionId) {

		int isManager = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = null;
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();
		String gatewayIds = "";

		// todo shlok need
		// make common method
		if (paymentGatewayTypeId != null && paymentGatewayTypeId.length > 0) {

			for (int j = 0; j < paymentGatewayTypeId.length; j++) {
				int payment = paymentGatewayTypeId[j];
				if (j == (paymentGatewayTypeId.length - 1)) {
					gatewayIds += payment;
				} else {
					gatewayIds += payment + ",";
				}
			}

		}
		OrderHeader header = new OrderHeader();
		BatchDetail batchDetail = null;
		try {
			batchDetail = getActiveBatch(httpRequest, em, locationId, sessionId, false, null, userId);
		} catch (Exception e1) {
			logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
		}

		if (batchDetail != null) {
			String query = "SELECT count(*) FROM `users_to_roles` utr left join roles "
					+ "r on r.id=utr.roles_id where utr.users_id = ? and r.role_name in ('Account Admin','Business Admin','POS Supervisor')";

			Object result = em.createNativeQuery(query).setParameter(1, userId).getSingleResult();

			if (result != null) {
				// if this has primary key not 0
				if (((BigInteger) result).intValue() > 0) {
					isManager = 1;
				}
			} else {
				logger.severe(httpRequest, "Could not get count of users to roles");
			}

			if (isManager == 0) {

				// order source production excluded from list
				// not manager
				String queryString = "SELECT  " + objectsWithColumnStr
						+ "  FROM order_header oh left join order_payment_details opd on oh.id=opd.order_header_id "
						+ " join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id "
						+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
						+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
						+ " left join payment_method pm on opd.payment_method_id= pm.id "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
						+ " left JOIN order_source osou on oh.order_source_id = osou.id "
						+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
						+ " left JOIN order_status os on oh.order_status_id = os.id  left JOIN order_source_group osg on os.order_source_group_id = osg.id "
						+ " WHERE  ( l.locations_id  in (select location0_.id as id1 from locations location0_     "
						+ " where location0_.locations_id=?) or oh.locations_id=?) " + "   "
						+ " and os.name not in ('Void Order ','Cancel Order ') "
						+ " and osou.name != 'Production' and  oh.sub_total != '0.00' "

						+ " "
						+ " and (opd.nirvanaxp_batch_number=? or opd.nirvanaxp_batch_number is null )   and oh.created_by=? and  (opd.order_source_group_to_paymentgatewaytype_id in (SELECT id FROM  order_source_group_to_paymentgateway_type  where paymentgateway_type_id in (?)) "
						+ " or opd.order_source_to_paymentgatewaytype_id in (SELECT id FROM  order_source_to_paymentgateway_type  where paymentgateway_type_id in (?)))  order by oh.id,opd.id asc ";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
						.setParameter(2, locationId).setParameter(3, batchDetail.getId()).setParameter(4, userId)
						.setParameter(5, gatewayIds).setParameter(6, gatewayIds).getResultList();

				int index = 0;
				orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
						index);
			} else {

				// order source production excluded from list
				// manager
				String queryString = "SELECT  " + objectsWithColumnStr
						+ "  FROM order_header oh left join order_payment_details opd on oh.id=opd.order_header_id "
						+ " join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id "
						+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
						+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
						+ " left join payment_method pm on opd.payment_method_id= pm.id "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
						+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
						+ " left JOIN order_source osou on oh.order_source_id = osou.id "
						+ " left JOIN order_status os on oh.order_status_id = os.id  left JOIN order_source_group osg on os.order_source_group_id = osg.id "
						+ " WHERE  ( l.locations_id  in (select location0_.id as id1 from locations location0_     "
						+ " where location0_.locations_id=?) or oh.locations_id=?) " + "  "
						+ " and os.name not in ('Void Order ','Cancel Order') "
						+ " and osou.name != 'Production' and oh.sub_total != '0.00'"
						+ " and (opd.nirvanaxp_batch_number=? or opd.nirvanaxp_batch_number is null )  "
						+ " and (opd.order_source_group_to_paymentgatewaytype_id in (SELECT id FROM  order_source_group_to_paymentgateway_type  where paymentgateway_type_id in (?)) "
						+ " or opd.order_source_to_paymentgatewaytype_id in (SELECT id FROM  order_source_to_paymentgateway_type  where paymentgateway_type_id in (?)))  order by oh.id,opd.id asc ";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
						.setParameter(2, locationId).setParameter(3, batchDetail.getId()).setParameter(4, gatewayIds)
						.setParameter(5, gatewayIds).getResultList();
				int index = 0;
				orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
						index);
			}
		}
		return orderHeaders;

	}

	/**
	 * Update order for seatwise.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param rootlocationId
	 *            the root location id
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeaderWithInventoryPostPacket updateOrderForSeatwise(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order, String rootlocationId, int merchantId, String sesionId) throws Exception {
		OrderHeader o = getOrderById(em, order.getId());
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em, OrderStatus.class,
				o.getOrderStatusId());
		InventoryPostPacket ipp = null;
		if (orderStatus != null && (!"Ready to Order".equalsIgnoreCase(orderStatus.getName()))) {

			String statusId = o.getOrderStatusId();
			// set the all the varaible
			o.setOrderStatusId(order.getOrderStatusId());
			o.setPriceGratuity(order.getPriceGratuity());
			o.setGratuity(order.getGratuity());
			if (order.getSplitCount() != null) {
				o.setSplitCount(order.getSplitCount());
			}

			if (order.getPaymentWaysId() != null) {
				o.setPaymentWaysId(order.getPaymentWaysId());
			}

			o.setIsGratuityApplied(order.getIsGratuityApplied());
			o.setOrderHeaderToSalesTax(order.getOrderHeaderToSalesTax());
			OrderHeader newOH = em.merge(o);
			o.setUpdated(newOH.getUpdated());

			if (order.getOrderDetailItems() != null) {
				ipp = manageInventoryForOrder(httpRequest, order, em, rootlocationId, o, false, false, orderStatus,
						merchantId, sesionId);
				// ------------Change by uzma- for order object by id ------
				// find order
				OrderHeader order_his = getOrderById(em, order.getId());

				if (statusId != order_his.getOrderStatusId()) {
					// insert into order status history
					insertIntoOrderStatusHistory(em, order_his);

				}

				// we need to save the order detail item and order detail item
				// attribute that is sent by the client always
				List<OrderDetailItem> orderDetailItemsListOfHistory = order_his.getOrderDetailItems();
				order_his.setOrderDetailItems(order.getOrderDetailItems());

				// insert into order history
				new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order_his, em);

				// if party size is updated, then update the guest count of
				// reservations

				order_his.setOrderDetailItems(orderDetailItemsListOfHistory);
				order_his.setScheduleDateTime(o.getScheduleDateTime());
				return new OrderHeaderWithInventoryPostPacket(order_his, ipp);
			}
		}
		return new OrderHeaderWithInventoryPostPacket(o, ipp);

	}

	/**
	 * Check duplicate order header.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param header
	 *            the header
	 * @return true, if successful
	 */
	private boolean checkDuplicateOrderHeader(HttpServletRequest httpRequest, EntityManager em, OrderHeader header) {
		boolean isDuplicate = true;
		List<OrderHeader> resultSet = null;
		String queryString = "select o from OrderHeader o where o.updated =? and o.id=? ";
		TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class)
				.setParameter(1, header.getUpdated()).setParameter(2, header.getId());
		try {
			resultSet = query.getResultList();
		} catch (NoResultException e) {
			logger.fine(httpRequest, "Could not find order with Id: " + header.getId());
		}
		if (resultSet != null && resultSet.size() > 0) {
			isDuplicate = false;
		}
		return isDuplicate;
	}

	/**
	 * Updated time.
	 *
	 * @return the long
	 */
	private long updatedTime() {
		long timee = new TimezoneTime().getGMTTimeInMilis();
		String timee2 = timee + "";
		timee2 = timee2.substring(0, timee2.length() - 3) + "000";
		return Long.parseLong(timee2);
	}

	/**
	 * Batch id string.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the string
	 */
	public String batchIdString(EntityManager em, String locationId, String date) {
		String batchIds = "";
		TimezoneTime timezoneTime = new TimezoneTime();
		String startDate = timezoneTime.getDateAccordingToGMT(date + " 00:00:00", locationId, em);
		String endDate = timezoneTime.getDateAccordingToGMT(date + " 23:59:59", locationId, em);

		String queryString = " SELECT id FROM batch_detail " + "  where (startTime >= ? and startTime <= ?) "
				+ " or (startTime <=? and closetime>=?)";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, startDate)
				.setParameter(2, endDate).setParameter(3, startDate).setParameter(4, endDate).getResultList();

		// todo shlok need
		// make common method
		for (int i = 0; i < resultList.size(); i++) {
			if (i == (resultList.size() - 1)) {
				batchIds += "'" + resultList.get(i) + "'";
			} else {
				batchIds += "'" + resultList.get(i) + "'" + ",";
			}
		}

		return batchIds;
	}

	/**
	 * Batch max min string.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @param isBatchWise
	 *            the is batch wise
	 * @return the list
	 * @throws ParseException
	 *             the parse exception
	 */
	public List<Date> batchMaxMinString(EntityManager em, String locationId, String date, boolean isBatchWise)
			throws ParseException {
		List<BatchDetail> batchDetails = new ArrayList<BatchDetail>();
		List<Date> batchIdDate = new ArrayList<Date>();

		batchDetails = batchDetailList(em, locationId, date);

		if (batchDetails != null && batchDetails.size() > 0) {
			BatchDetail activeBatch = null;
			for (BatchDetail batchDetail : batchDetails) {
				if (batchDetail.getStatus().equals("A")) {
					activeBatch = batchDetail;
					break;
				}
			}

			if (isBatchWise) {
				if (activeBatch != null) {
					Date start = new Date(activeBatch.getStartTime());
					batchIdDate.add(start);
					Date end = new Date(new TimezoneTime().getGMTTimeInMilis());
					batchIdDate.add(end);
				}

			} else {
				BatchDetail batchStart = batchDetails.get(0);
				BatchDetail batchEnd = batchDetails.get(batchDetails.size() - 1);
				Date start = new Date(batchStart.getStartTime());
				batchIdDate.add(start);
				Date end = new Date(new TimezoneTime().getGMTTimeInMilis());
				if (batchEnd.getCloseTime() != 0) {
					end = new Date(batchEnd.getCloseTime());
				}
				batchIdDate.add(end);
			}

		}

		return batchIdDate;
	}

	/**
	 * Batch max min string for reservation.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the list
	 * @throws ParseException
	 *             the parse exception
	 */
	public List<Date> batchMaxMinStringForReservation(EntityManager em, String locationId, String date)
			throws ParseException {
		List<BatchDetail> batchDetails = new ArrayList<BatchDetail>();
		List<Date> batchId = new ArrayList<Date>();

		batchDetails = batchDetailList(em, locationId, date);

		if (batchDetails != null && batchDetails.size() > 0) {
			BatchDetail batchStart = batchDetails.get(0);
			BatchDetail batchEnd = batchDetails.get(batchDetails.size() - 1);
			Date start = new Date(batchStart.getStartTime());
			batchId.add(start);
			Date end = null;
			if (batchEnd.getCloseTime() != 0) {
				end = new Date(batchEnd.getCloseTime());
			}
			if (end != null) {
				batchId.add(end);
			}
		}

		return batchId;
	}

	/**
	 * Batch detail list.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the list
	 * @throws ParseException
	 *             the parse exception
	 */
	public List<BatchDetail> batchDetailList(EntityManager em, String locationId, String date) throws ParseException {
		// String batchIds = "";
		TimezoneTime timezoneTime = new TimezoneTime();
		String startDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 00:00:00", locationId, em);
		String endDate = timezoneTime.getDateAccordingToGMT(date.substring(0, 10) + " 23:59:59", locationId, em);
		List<BatchDetail> resultSet = new ArrayList<BatchDetail>();
		String queryString = " SELECT b FROM BatchDetail b " + "  where ((b.startTime >= ? and b.startTime <= ?) "
				+ " or (b.startTime <=? and b.closeTime>=?)) and b.locationId =?  order by b.startTime desc ";

		TypedQuery<BatchDetail> query = em.createQuery(queryString, BatchDetail.class)
				.setParameter(1, Utilities.convertStringToDate(startDate))
				.setParameter(2, Utilities.convertStringToDate(endDate))
				.setParameter(3, Utilities.convertStringToDate(startDate))
				.setParameter(4, Utilities.convertStringToDate(endDate)).setParameter(5, locationId);
		resultSet = query.getResultList();
		if (resultSet != null && resultSet.size() == 0) {
			queryString = " SELECT b FROM BatchDetail b "
					+ "  where b.startTime <=? and b.status='A' and b.locationId =?  order by b.startTime desc ";

			query = em.createQuery(queryString, BatchDetail.class)
					.setParameter(1, Utilities.convertStringToDate(startDate)).setParameter(2, locationId);
			resultSet = query.getResultList();
		}
		return resultSet;
	}

	/**
	 * Gets the list order with user.
	 *
	 * @param em
	 *            the em
	 * @param resultList
	 *            the result list
	 * @param rowShippingCount
	 *            the row shipping count
	 * @param rowBillingCount
	 *            the row billing count
	 * @param rowCount
	 *            the row count
	 * @param rowShippingIdCount
	 *            the row shipping id count
	 * @param rowBillingIdCount
	 *            the row billing id count
	 * @return the list order with user
	 * @throws Exception
	 *             the exception
	 */
	public List<OrderWithUsers> getListOrderWithUser(EntityManager em, List<Object[]> resultList, int rowShippingCount,
			int rowBillingCount, int rowCount, int rowShippingIdCount, int rowBillingIdCount) throws Exception {
		List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
		for (Object[] objRow : resultList) {

			// if this has primary key not 0
			OrderWithUsers orderWithUsers = new OrderWithUsers();
			OrderHeader orderHeader = new OrderHeader();

			// ------------------ Add address object for address shipping id
			// and address billing id ---
			Address addressShipping = new Address();
			Address addressBilling = new Address();
			addressShipping.setAddressByResultSet(objRow, rowShippingCount);
			addressBilling.setAddressByResultSet(objRow, rowBillingCount);

			objRow[rowShippingIdCount] = addressShipping;
			objRow[rowBillingIdCount] = addressBilling;

			orderHeader.setOrderHeaderByResultSet(objRow, rowCount + 7);
			orderWithUsers.setOrderHeader(orderHeader);

			User user = new User();
			if (objRow[rowCount] != null)

				user = user.getUserByResultSetForOrder(objRow, user, rowCount);
			if (user.getId() != null) {
				orderWithUsers.setUser(user);
			}

			orderWithUsers.setOrderHeader(orderHeader);
			orderWithUsersList.add(orderWithUsers);

		}
		return orderWithUsersList;
	}

	/**
	 * Update reservation party size.
	 *
	 * @param reservation
	 *            the reservation
	 * @param partySize
	 *            the party size
	 * @param em
	 *            the em
	 */
	private void updateReservationPartySize(Reservation reservation, int partySize, EntityManager em) {
		if (reservation != null) {
			reservation.setPartySize(partySize);
			reservation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.merge(reservation);

		}

	}

	/**
	 * Update reservation user.
	 *
	 * @param reservation
	 *            the reservation
	 * @param orderHeader
	 *            the order header
	 * @param em
	 *            the em
	 */
	private void updateReservationUser(Reservation reservation, OrderHeader orderHeader, EntityManager em) {
		if (reservation != null) {
			reservation.setUsersId(orderHeader.getUsersId());
			reservation.setFirstName(orderHeader.getFirstName());
			reservation.setLastName(orderHeader.getLastName());
			reservation.setUpdatedBy(orderHeader.getUpdatedBy());
			User user = (User) new CommonMethods().getObjectById("User", em, User.class, orderHeader.getUsersId());
			if (user != null) {
				if (user.getPhone() != null) {
					reservation.setPhoneNumber(user.getPhone());
				}
				if (user.getEmail() != null) {
					reservation.setEmail(user.getEmail());
				}
			}
			reservation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.merge(reservation);

		}

	}

	/**
	 * Update payment payment mosambee.
	 *
	 * @param em
	 *            the em
	 * @param batchId
	 *            the batch id
	 * @return the list
	 */
	public List<OrderPaymentDetail> updatePaymentPaymentMosambee(EntityManager em, String batchId,
			HttpServletRequest request) {
		// getting all mosambee CC auth order payment id
		BatchDetail batchDetail = (BatchDetail) new CommonMethods().getObjectById("BatchDetail", em, BatchDetail.class,
				batchId);
		String query = " select opd.id from order_payment_details "
				+ " opd join payment_transaction_type ptt on opd.payment_transaction_type_id=ptt.id  		"
				+ "		  where (((order_source_group_to_paymentgatewaytype_id in "
				+ " (select osgpt.id from order_source_group_to_paymentgateway_type osgpt   				 "
				+ " join paymentgateway_type pgt on osgpt.paymentgateway_type_id =  pgt.id  "
				+ " where pgt.name in (?,?))  				 "
				+ " or order_source_to_paymentgatewaytype_id in (select ospt.id from order_source_to_paymentgateway_type ospt  				 "
				+ " join paymentgateway_type pgt on ospt.paymentgateway_type_id =  pgt.id  where pgt.name in (?,?))) "
				+ " and nirvanaxp_batch_number = ?   				 " + " and ptt.name in ('Auth')) or "
				+ " ((order_source_group_to_paymentgatewaytype_id in "
				+ " (select osgpt.id from order_source_group_to_paymentgateway_type osgpt   				 "
				+ " join paymentgateway_type pgt on osgpt.paymentgateway_type_id =  pgt.id   where pgt.name in (?)) 				 "
				+ " or order_source_to_paymentgatewaytype_id in (select ospt.id from order_source_to_paymentgateway_type ospt  				   "
				+ "join paymentgateway_type pgt on ospt.paymentgateway_type_id =  pgt.id  where pgt.name in (?))) "
				+ "and nirvanaxp_batch_number = ? 				" + "  and ptt.name in ('Force')))       ";

		@SuppressWarnings("unchecked")
		List<Object> result = (List<Object>) em.createNativeQuery(query).setParameter(1, NameConstant.JIO_GATEWAY)
				.setParameter(2, NameConstant.MOSAMBEE_GATEWAY).setParameter(3, NameConstant.JIO_GATEWAY)
				.setParameter(4, NameConstant.MOSAMBEE_GATEWAY).setParameter(5, batchId)
				.setParameter(6, NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA)
				.setParameter(7, NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA).setParameter(8, batchId).getResultList();
		String orderPaymentList = "";
		// creating comma sepearated string formt
		for (int i = 0; i < result.size(); i++) {
			if (i == (result.size() - 1)) {
				orderPaymentList += "'" + (Integer) result.get(i) + "'";
			} else {
				orderPaymentList += "'" + (Integer) result.get(i) + "',";
			}
		}
		if (orderPaymentList.length() > 0) {
			String queryString = "select opd from OrderPaymentDetail opd where opd.id in (" + orderPaymentList + ") ";
			TypedQuery<OrderPaymentDetail> query2 = em.createQuery(queryString, OrderPaymentDetail.class);
			List<OrderPaymentDetail> resultSet = query2.getResultList();

			for (OrderPaymentDetail orderPaymentDetail : resultSet) {
				boolean processed = updateDatabaseToSettleStatusForMosambee(em, orderPaymentDetail,
						batchDetail.getLocationId(), request);

			}
			try {

				String query1 = " select opd.id from order_payment_details "
						+ " opd join payment_transaction_type ptt on opd.payment_transaction_type_id=ptt.id  		"
						+ "		  where (  " + " ((order_source_group_to_paymentgatewaytype_id in "
						+ " (select osgpt.id from order_source_group_to_paymentgateway_type osgpt   				 "
						+ " join paymentgateway_type pgt on osgpt.paymentgateway_type_id =  pgt.id   where pgt.name in (?)) 				 "
						+ " or order_source_to_paymentgatewaytype_id in (select ospt.id from order_source_to_paymentgateway_type ospt  				   "
						+ "join paymentgateway_type pgt on ospt.paymentgateway_type_id =  pgt.id  where pgt.name in (?))) "
						+ "and nirvanaxp_batch_number = ? 				" + "  and ptt.name in ('Auth')))       ";

				@SuppressWarnings("unchecked")
				List<Object> result1 = (List<Object>) em.createNativeQuery(query1)
						.setParameter(1, NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA)
						.setParameter(2, NameConstant.DATACAP_GATEWAY_WITH_FIRSTDATA).setParameter(3, batchId)
						.getResultList();
				String orderPaymentList1 = "";
				// creating comma sepearated string formt
				for (int i = 0; i < result1.size(); i++) {
					if (i == (result1.size() - 1)) {
						orderPaymentList1 += "'" + (Integer) result1.get(i) + "'";
					} else {
						orderPaymentList1 += "'" + (Integer) result1.get(i) + "',";
					}
				}
				if (orderPaymentList1.length() > 0) {
					String queryString5 = "select opd from OrderPaymentDetail opd where opd.id in (" + orderPaymentList1
							+ ") ";
					TypedQuery<OrderPaymentDetail> query51 = em.createQuery(queryString5, OrderPaymentDetail.class);
					List<OrderPaymentDetail> resultSet1 = query51.getResultList();

					for (OrderPaymentDetail orderPaymentDetail : resultSet1) {
						try {
							// if transaction got precaptured only then we can
							// make cc auth to settle
							if (checkTransactionPrecapturedStatus(orderPaymentDetail.getAuthCode(), em,
									orderPaymentDetail.getOrderHeaderId())) {

								String queryts = "select ts from TransactionStatus ts where ts.name = 'CC Settled' ";
								TypedQuery<TransactionStatus> querytst = em.createQuery(queryts,
										TransactionStatus.class);
								TransactionStatus transactionStatus = querytst.getSingleResult();
								orderPaymentDetail.setTransactionStatus(transactionStatus);
								orderPaymentDetail = em.merge(orderPaymentDetail);

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							// todo shlok need
							// handle proper exception
							logger.severe(e);
						}
					}

				}
			} catch (Exception e) {
				// todo shlok need
				// handle proper exception
				logger.severe(e);
			}
			return resultSet;
		}

		return null;

	}

	/**
	 * Update database to settle status for mosambee.
	 *
	 * @param em
	 *            the em
	 * @param orderPaymentDetail
	 *            the order payment detail
	 * @return true, if successful
	 */
	private boolean updateDatabaseToSettleStatusForMosambee(EntityManager em, OrderPaymentDetail orderPaymentDetail,
			String locationId, HttpServletRequest request) {
		try {
			boolean result = false;
			// getting transaction status from DB of CC Settled
			String queryString = "select ts from TransactionStatus ts where ts.name = 'CC Settled' ";
			TypedQuery<TransactionStatus> query2 = em.createQuery(queryString, TransactionStatus.class);
			TransactionStatus transactionStatus = query2.getSingleResult();

			// getting paymentTransactiontype from db
			queryString = "select ts from PaymentTransactionType ts where ts.name = 'CaptureAll' ";
			TypedQuery<PaymentTransactionType> query3 = em.createQuery(queryString, PaymentTransactionType.class);
			PaymentTransactionType paymentTransactionType = query3.getSingleResult();

			if (transactionStatus != null && paymentTransactionType != null) {

				orderPaymentDetail.setTransactionStatus(transactionStatus);
				// updating status of exisiting orderpayment detail
				em.merge(orderPaymentDetail);

				// creating new entry in order payment for CC Settled

				if (!checkTransactionCCSettledStatus(orderPaymentDetail.getAuthCode(), em,
						orderPaymentDetail.getOrderHeaderId())) {
					OrderPaymentDetail detail = new OrderPaymentDetail();
					detail = detail.setOrderPaymentDetail(orderPaymentDetail);
					detail.setPaymentTransactionType(paymentTransactionType);
					detail.setId(new StoreForwardUtility().generateDynamicBigIntId(em, locationId, request,
							"order_payment_details"));
					detail = em.merge(detail);
				}
				result = true;
			}
			return result;
		} catch (Exception e) {
			logger.severe(e);

		}
		return false;
	}

	private boolean checkTransactionCCSettledStatus(String authCode, EntityManager em, String orderId) {
		// checking whether record is precaptured or not
		String queryString5 = "select opd.id from order_payment_details opd join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
				+ "  where opd.auth_code=? and ptt.name='CaptureAll' and opd.order_header_id=? ";
		int resultList = 0;
		try {
			resultList = (Integer) em.createNativeQuery(queryString5).setParameter(1, authCode).setParameter(2, orderId)
					.getSingleResult();
			if (resultList > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.severe(e);
		}

		return false;
	}

	/**
	 * Manage inventory for order.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param orderFromPacket
	 *            the order from packet
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param orderFromDatabase
	 *            the order from database
	 * @param fromPayment
	 *            the from payment
	 * @param isDeductibleForOnlineAndPostPayment
	 *            the is deductible for online and post payment
	 * @return the inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	private InventoryPostPacket manageInventoryForOrder(HttpServletRequest httpRequest, OrderHeader orderFromPacket,
			EntityManager em, String locationId, OrderHeader orderFromDatabase, boolean fromPayment,
			boolean isDeductibleForOnlineAndPostPayment, OrderStatus orderStatus, int accountId, String sessionId)
			throws Exception {
		// todo shlok need
		// modularize method
		logger.severe("!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@orderformDb" + orderFromDatabase.toString());
		logger.severe("!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@orderformPacket" + orderFromPacket.toString());
		ItemInventoryManagementHelper inventoryManagementHelper = new ItemInventoryManagementHelper();
		InventoryPostPacket inventoryPostPacket = new InventoryPostPacket();
		Location rootLocation = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
				locationId);
		;
		int isRealTimeInventoryOn = 0;

		if (rootLocation != null) {
			isRealTimeInventoryOn = rootLocation.getIsRealTimeInventoryRequired();
		}
		// 'Out Of Stock','Ordered Qty More Than Avail Qty','No
		// Inventory Available'
		OrderDetailStatus orderDetailStatusOutOfStock = new OrderDetailStatus("Out Of Stock");
		OrderDetailStatus orderDetailStatusOrderMoreThanAvail = new OrderDetailStatus(
				"Ordered Qty More Than Avail Qty");

		List<OrderDetailStatus> orderDetailStatusList = getOrderstatusForInventory(em, locationId);

		int index = orderDetailStatusList.indexOf(orderDetailStatusOutOfStock);
		orderDetailStatusOutOfStock = orderDetailStatusList.get(index);

		index = orderDetailStatusList.indexOf(orderDetailStatusOrderMoreThanAvail);
		orderDetailStatusOrderMoreThanAvail = orderDetailStatusList.get(index);

		if (orderFromPacket.getOrderDetailItems() != null) {

			boolean isAdvanceOrder = isAdvanceOrder(orderFromPacket, em, locationId);

			for (OrderDetailItem orderDetailItem : orderFromPacket.getOrderDetailItems()) {
				boolean isIdSet = false;

				if (orderDetailItem.getId() == null) {
					orderDetailItem.setId(new StoreForwardUtility().generateDynamicBigIntId(em, locationId, httpRequest,
							"order_detail_items"));
					isIdSet = true;
					orderDetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				}
				if (orderDetailItem.getCreated() == null) {
					orderDetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				if (orderDetailItem.getRecallReason() != null) {
					// send sms text
					try {
						logger.severe(
								"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@sms sending started");
						// SMSTemplateKeys.sendItemCancelledSMSToCustomerCalling(httpRequest,em,
						// orderFromDatabase, orderDetailItem,
						// locationId,sessionId);
					} catch (Exception e) {
						logger.severe(e);
					}
				}

				if (orderDetailItem.getId() != null) {
					try {
						OrderDetailItem odiPrevious = (OrderDetailItem) new CommonMethods()
								.getObjectById("OrderDetailItem", em, OrderDetailItem.class, orderDetailItem.getId());
						if (odiPrevious != null) {
							orderDetailItem.setPreviousOrderQuantity(odiPrevious.getItemsQty());
						}
					} catch (Exception e) {
						logger.severe(e);
					}
				}

				OrderDetailItem orderDetailItem2 = em.merge(orderDetailItem);

				if (isIdSet) {
					orderDetailItem.setId(null);
				}
				Reasons reasons = (Reasons) new CommonMethods().getObjectById("Reasons", em, Reasons.class,
						orderDetailItem.getRecallReason());

				if (orderFromPacket.getOrderTypeId() == 1) {
					if (reasons == null || !reasons.getInventoryConsumed().equals("1")) {
						try {
							// need to check for inventory acural =1 for normal
							// order?
							// need to check for inventory acural =1 for online
							// order?

							// normal order (Bisiness app)= irrespective of
							// inventory accural (save/send)
							// normal order (online Order)= irrespective of
							// inventory accural (payment)working
							// advance order (Bisiness app)= 1- inventory
							// accural =0 (save/send) working
							// advance order (online)= 1- inventory accural =0
							// (paid) working
							// 2- inventory accural =1 (close)

							if (fromPayment) {

								/*
								 * if ((isAdvanceOrder &&
								 * orderDetailItem.getInventoryAccrual() == 1 &
								 * fromPayment && isOrderClose) ) {
								 * 
								 * isDeductibleForOnlineAndPostPayment = true;
								 * inventoryManagementHelper.
								 * manageItemInventoryForOderDetailItem(
								 * httpRequest, orderDetailItem, em,
								 * isRealTimeInventoryOn,
								 * orderFromDatabase.getUpdatedBy(),
								 * inventoryPostPacket, locationId,
								 * isDeductibleForOnlineAndPostPayment);
								 * orderDetailItem.setIsInventoryHandled(1); }
								 * else
								 */ if ((!isAdvanceOrder)) {
									inventoryManagementHelper.manageItemInventoryForOderDetailItem(httpRequest,
											orderDetailItem, em, isRealTimeInventoryOn,
											orderFromDatabase.getUpdatedBy(), inventoryPostPacket, locationId,
											isDeductibleForOnlineAndPostPayment, false);
									orderDetailItem.setIsInventoryHandled(1);
								} else if (isDeductibleForOnlineAndPostPayment && !isAdvanceOrder) {

									inventoryManagementHelper.manageItemInventoryForOderDetailItem(httpRequest,
											orderDetailItem, em, isRealTimeInventoryOn,
											orderFromDatabase.getUpdatedBy(), inventoryPostPacket, locationId,
											isDeductibleForOnlineAndPostPayment, false);
									orderDetailItem.setIsInventoryHandled(1);
								}
							} else {

								if ((!isAdvanceOrder
										|| (orderDetailItem.getInventoryAccrual() == 0 && isAdvanceOrder))) {

									inventoryManagementHelper.manageItemInventoryForOderDetailItem(httpRequest,
											orderDetailItem, em, isRealTimeInventoryOn,
											orderFromDatabase.getUpdatedBy(), inventoryPostPacket, locationId,
											isDeductibleForOnlineAndPostPayment, true);
									orderDetailItem.setIsInventoryHandled(1);
								} else if (isAdvanceOrder && orderDetailItem.getInventoryAccrual() == 1
										&& orderStatus != null && orderStatus.getName().equals("Ready to Order")) {

									isDeductibleForOnlineAndPostPayment = true;
									inventoryManagementHelper.manageItemInventoryForOderDetailItem(httpRequest,
											orderDetailItem, em, isRealTimeInventoryOn,
											orderFromDatabase.getUpdatedBy(), inventoryPostPacket, locationId,
											isDeductibleForOnlineAndPostPayment, false);
									orderDetailItem.setIsInventoryHandled(1);
								}
							}

						} catch (NirvanaXPException e) {

							String exception = e.toString();
							if (exception != null && orderDetailStatusList != null) {
								if (exception.contains("ORD1013")) {

									orderDetailItem.setOrderDetailStatusId(orderDetailStatusOutOfStock.getId());
									setStatusToAttribute(orderDetailItem, orderDetailStatusOutOfStock);
								}

								else if (exception.contains("3:")) {
									orderDetailItem.setOrderDetailStatusId(orderDetailStatusOrderMoreThanAvail.getId());
									setStatusToAttribute(orderDetailItem, orderDetailStatusOrderMoreThanAvail);
								}
							}
						}
					}
				}

				if (orderDetailItem.getId() == null) {
					orderDetailItem.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					orderDetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				} else {
					OrderDetailItem newOrderDetailItem = (OrderDetailItem) new CommonMethods()
							.getObjectById("OrderDetailItem", em, OrderDetailItem.class, orderDetailItem.getId());
					if (newOrderDetailItem != null) {
						orderDetailItem.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
						orderDetailItem.setCreated(newOrderDetailItem.getCreated());
					}

				}
				orderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				orderDetailItem.setOrderHeaderId(orderFromDatabase.getId());

				if (orderDetailItem.getOrderHeaderToSeatDetailId() == null) {
					orderDetailItem.setOrderHeaderToSeatDetailId(BigInteger.ZERO);
				}

				// for order header to seat id entry in order detail item
				// table
				if (orderFromPacket.getIsSeatWiseOrder() == 1) {
					if (orderFromDatabase.getOrderHeaderToSeatDetails() != null) {
						for (OrderHeaderToSeatDetail orderHeaderToSeatDetail : orderFromDatabase
								.getOrderHeaderToSeatDetails()) {
							if (orderDetailItem.getSeatId().equals(orderHeaderToSeatDetail.getSeatId())) {
								orderDetailItem.setOrderHeaderToSeatDetailId(orderHeaderToSeatDetail.getId());
							}
						}
					}
				}

				// add or update the order detail item
				// // naman storeforward backlog
				// if(orderDetailItem.getId()==0){
				// orderDetailItem.setId(new
				// StoreForwardUtility().generateOrderDetailId(em, locationId,
				// httpRequest, accountId));
				// }
				// OrderDetailItem orderDetailItem2 = em.merge(orderDetailItem);

				if (orderDetailItem.getInventory() != null && orderDetailItem.getInventory().size() > 0) {
					for (Inventory inventory : orderDetailItem.getInventory()) {

						inventory.setOrderDetailItemId(orderDetailItem2.getId());
						em.merge(inventory);

						new InsertIntoHistory().insertInventoryIntoHistoryWithoutTransaction(null, inventory, em);
					}
				}

				// add orderdetails item attribute also if sent by
				// client
				if (orderDetailItem.getOrderDetailAttributes() != null
						&& orderDetailItem.getOrderDetailAttributes().size() > 0) {

					for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
						orderDetailAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						orderDetailAttribute.setOrderDetailItemId(orderDetailItem2.getId());

						if (orderDetailAttribute.getId() == null) {
							// its trying to insert first time
							orderDetailAttribute
									.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							orderDetailAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							orderDetailAttribute.setId(new StoreForwardUtility().generateDynamicBigIntId(em, locationId,
									httpRequest, "order_detail_attribute"));
							orderDetailAttribute = em.merge(orderDetailAttribute);
						} else {
							// its trying to update the already
							// existing

							orderDetailAttribute
									.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							em.merge(orderDetailAttribute);
						}
					}
				}

				// add update NotPrintedOrderDetailItemsToPrinter
				if (orderDetailItem.getNotPrintedOrderDetailItemsToPrinter() != null) {
					for (NotPrintedOrderDetailItemsToPrinter o : orderDetailItem
							.getNotPrintedOrderDetailItemsToPrinter()) {
						o.setOrderDetailItemsId(orderDetailItem2.getId());
						if (o.getId() == 0) {
							o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							// o.setId(new
							// StoreForwardUtility().generateDynamicBigIntId(em,
							// locationId, httpRequest,
							// "not_printed_order_detail_items_to_printer"));
							o = em.merge(o);

						} else {
							NotPrintedOrderDetailItemsToPrinter temp = em
									.find(NotPrintedOrderDetailItemsToPrinter.class, o.getId());
							if (temp != null) {
								o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
								o.setCreated(temp.getCreated());
							}
							o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							o = em.merge(o);
						}

					}
				}

			}

		}

		return inventoryPostPacket;
	}

	/**
	 * Update order payment for under processing transaction.
	 *
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @return the order header
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public OrderHeader updateOrderPaymentForUnderProcessingTransaction(EntityManager em, OrderHeader order)
			throws IOException, InvalidSessionException, NirvanaXPException, ParseException {
		// getting order from database
		OrderHeader o = getOrderById(em, order.getId());
		List<OrderPaymentDetail> details = o.getOrderPaymentDetails();
		if (details == null) {
			details = new ArrayList<OrderPaymentDetail>();
		}

		// merging underprocessing transactions
		for (OrderPaymentDetail detail : order.getOrderPaymentDetails()) {
			detail.setOrderHeaderId(o.getId());
			detail = em.merge(detail);
			insertIntoOrderPaymentDetailHistory(em, detail);
			details.add(detail);
		}
		o.setOrderPaymentDetails(details);
		return o;

	}

	/**
	 * Un merge order.
	 *
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @return the order header
	 */
	public OrderHeader unMergeOrder(EntityManager em, OrderHeader order) {

		OrderHeader o = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em, OrderHeader.class,
				order.getId());
		String unMergedLocationsId = o.getMergedLocationsId();
		o.setMergedLocationsId("");
		if (unMergedLocationsId != null && unMergedLocationsId.length() > 0) {

			// unmerge list of locations
			String unmergedLocationsArr[] = unMergedLocationsId.split(",");
			for (String locationToUnmerge : unmergedLocationsArr) {
				if (locationToUnmerge != null && locationToUnmerge.length() > 0) {
					String locationId = locationToUnmerge.trim();
					Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
							locationId);
					;
					if (location != null) {
						location.setIsCurrentlyMerged(0);
						em.merge(location);
					}
				}
			}
		}
		return o;

	}

	/**
	 * Sets the reason for order.
	 *
	 * @param o
	 *            the o
	 * @param oldOrder
	 *            the old order
	 * @param orderStatus1
	 *            the order status 1
	 * @param preOrderStatus
	 *            the pre order status
	 * @param packetVersion
	 *            the packet version
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	private OrderHeader setReasonForOrder(OrderHeader o, OrderHeader oldOrder, OrderStatus orderStatus1,
			OrderStatus preOrderStatus, int packetVersion) throws NirvanaXPException {
		if (orderStatus1.getName().equals("Void Order")) {
			if (oldOrder.getVoidReasonId() != null) {

				if (o.getAmountPaid() != null && (o.getAmountPaid().doubleValue() > 0
						|| (oldOrder.getAmountPaid() != null && oldOrder.getAmountPaid().doubleValue() > 0))) {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_ORDER_CANNOT_VOID_AMOUNT_GREATER_THAN_ZERO,
							MessageConstants.ERROR_MESSAGE_ORDER_CANNOT_VOID_AMOUNT_GREATER_THAN_ZERO_DISPLAY_MESSAGE,
							null));
				}

				o.setVoidReasonId(oldOrder.getVoidReasonId());
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_ORDER_VOID_REASON_ID_CANNOT_BE_ZERO,
						MessageConstants.ERROR_MESSAGE_ORDER_VOID_REASON_ID_CANNOT_BE_ZERO_DISPLAY_MESSAGE, null));

			}
		} else if (orderStatus1.getName().equals("Cancel Order")) {
			if (oldOrder.getVoidReasonId() != null) {

				if (o.getAmountPaid() != null
						&& (o.getAmountPaid().doubleValue() > 0
								|| (oldOrder.getAmountPaid() != null && oldOrder.getAmountPaid().doubleValue() > 0))
						&& !preOrderStatus.getName().equals("Reopen")) {

					throw new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_ORDER_VOID_REASON_ID_CANNOT_BE_ZERO,
							MessageConstants.ERROR_MESSAGE_ORDER_CANCEL_REASON_ID_CANNOT_BE_ZERO_DISPLAY_MESSAGE,
							null));
				}

				o.setVoidReasonId(oldOrder.getVoidReasonId());
				o.setCloseTime(new TimezoneTime().getGMTTimeInMilis());
			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_ORDER_VOID_REASON_ID_CANNOT_BE_ZERO,
						MessageConstants.ERROR_MESSAGE_ORDER_CANCEL_REASON_ID_CANNOT_BE_ZERO_DISPLAY_MESSAGE, null));
			}
		}
		return o;
	}

	/**
	 * Delete order.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return true, if successful
	 */
	private boolean deleteOrder(EntityManager em, String orderHeaderId) {
		BigInteger i = (BigInteger) em.createNativeQuery("call sp_DeleteOrderAndAllDetails(?)")
				.setParameter(1, orderHeaderId).getSingleResult();
		if (i != null && i.intValue() == 1) {
			return true;
		}
		return false;

	}

	/**
	 * Gets the order by reservation id.
	 *
	 * @param em
	 *            the em
	 * @param reservationId
	 *            the reservation id
	 * @param locationId
	 *            the location id
	 * @return the order by reservation id
	 */
	public OrderHeader getOrderByReservationId(EntityManager em, @PathParam("reservationId") int reservationId,
			String locationId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> r = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em.createQuery(
				criteria.select(r).where(builder.equal(r.get(OrderHeader_.reservationsId), reservationId)));
		OrderHeader orderHeader = query.getSingleResult();

		OrderHeader result = new OrderHeader(orderHeader.getId(), orderHeader.getAddressBilling(),
				orderHeader.getAddressShipping(), orderHeader.getAmountPaid(), orderHeader.getBalanceDue(),
				orderHeader.getCreated(), orderHeader.getCreatedBy(), orderHeader.getDiscountsId(),
				orderHeader.getDiscountsName(), orderHeader.getDiscountsTypeId(), orderHeader.getDiscountsTypeName(),
				orderHeader.getDiscountsValue(), orderHeader.getGratuity(), orderHeader.getIpAddress(),
				orderHeader.getOrderSourceId(), orderHeader.getOrderStatusId(), orderHeader.getPointOfServiceCount(),
				orderHeader.getPriceDiscount(), orderHeader.getPriceExtended(), orderHeader.getPriceGratuity(),
				orderHeader.getPriceTax1(), orderHeader.getPriceTax2(), orderHeader.getPriceTax3(),
				orderHeader.getPriceTax4(), orderHeader.getReservationsId(), orderHeader.getServiceTax(),
				orderHeader.getSubTotal(), orderHeader.getTotal(), orderHeader.getSplitCount(),
				orderHeader.getUpdated(), orderHeader.getUpdatedBy(), orderHeader.getUsersId(),
				orderHeader.getLocationsId(), orderHeader.getPaymentWaysId(), orderHeader.getDate(),
				orderHeader.getVerificationCode(), orderHeader.getQrcode(), orderHeader.getSessionKey(),
				orderHeader.getFirstName(), orderHeader.getLastName(), orderHeader.getServerId(),
				orderHeader.getCashierId(), orderHeader.getVoidReasonId(), orderHeader.getMergedLocationsId(),
				orderHeader.getOpenTime(), orderHeader.getCloseTime(), orderHeader.getDiscountDisplayName(),
				orderHeader.getTaxDisplayName1(), orderHeader.getTaxDisplayName2(), orderHeader.getTaxDisplayName3(),
				orderHeader.getTaxDisplayName4(), orderHeader.getTaxRate1(), orderHeader.getTaxRate2(),
				orderHeader.getTaxRate3(), orderHeader.getTaxRate4(), orderHeader.getTotalTax(),
				orderHeader.getTaxName1(), orderHeader.getTaxName2(), orderHeader.getTaxName3(),
				orderHeader.getTaxName4(), orderHeader.getIsGratuityApplied(), orderHeader.getRoundOffTotal(),
				orderHeader.getIsTabOrder(), orderHeader.getServername(), orderHeader.getCashierName(),
				orderHeader.getVoidReasonName(), orderHeader.getIsOrderReopened(), orderHeader.getTaxExemptId(),
				orderHeader.getScheduleDateTime(), orderHeader.getReferenceNumber(),
				orderHeader.getNirvanaXpBatchNumber(), orderHeader.getOrderNumber(), orderHeader.getIsSeatWiseOrder(),
				orderHeader.getCalculatedDiscountValue(), orderHeader.getShiftSlotId(),
				orderHeader.getPriceDiscountItemLevel(), orderHeader.getMergeOrderId(), orderHeader.getCompanyName(),
				orderHeader.getTaxNo(), orderHeader.getTaxDisplayName(), orderHeader.getDeliveryCharges(),
				orderHeader.getDeliveryTax(), orderHeader.getDeliveryOptionId(), orderHeader.getServiceCharges(),
				orderHeader.getPreassignedServerId(), orderHeader.getLocalTime(), orderHeader.getPoRefrenceNumber(),
				orderHeader.getRequestedLocationId(), orderHeader.getOrderTypeId(), orderHeader.getDriverId(),
				orderHeader.getComment(), orderHeader.getStartDate(), orderHeader.getEndDate(),
				orderHeader.getEventName());

		result.setOrderDetailItems(getCustomerOrderDetailsItemForOrderId(em, orderHeader.getId(), locationId));
		result.setOrderPaymentDetails(getCustomerOrderPaymentDetailForOrderId(em, orderHeader.getId()));
		return result;
	}

	/**
	 * Gets the order status history.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @return the order status history
	 */
	public List<OrderWithStatusHistory> getOrderStatusHistory(EntityManager em, String orderId) {
		List<OrderWithStatusHistory> orderWithStatusHistories = new ArrayList<OrderWithStatusHistory>();
		List<OrderStatusHistory> orderStatusHistoryList = new ArrayList<OrderStatusHistory>();
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatusHistory> criteria = builder.createQuery(OrderStatusHistory.class);
			Root<OrderStatusHistory> orderStatusHistory = criteria.from(OrderStatusHistory.class);
			TypedQuery<OrderStatusHistory> query = em.createQuery(criteria.select(orderStatusHistory)
					.where(builder.equal(orderStatusHistory.get(OrderStatusHistory_.orderHeaderId), orderId)));
			orderStatusHistoryList = query.getResultList();
		} catch (NoResultException e) {
			logger.severe("No Result Found for OrderStatusHistory for Order Id " + orderId);
		}

		if (orderStatusHistoryList != null) {
			String ids = "";

			if (orderStatusHistoryList.size() == 1) {
				ids = orderStatusHistoryList.get(0).getOrderStatusId() + "";
			} else {
				for (OrderStatusHistory history : orderStatusHistoryList) {
					ids = ids + history.getOrderStatusId() + ",";
				}

				ids = ids.substring(0, ids.length() - 1);
			}

			List<OrderStatus> orderStatusList = new ArrayList<OrderStatus>();
			try {
				String queryString = "select odi from OrderStatus odi where odi.id in ( " + ids
						+ ") and odi.status != 'D'";
				TypedQuery<OrderStatus> query1 = em.createQuery(queryString, OrderStatus.class);
				orderStatusList = query1.getResultList();
			} catch (NoResultException e) {
				logger.severe("No Result Found for OrderStatus for Order Id " + orderId);
			}

			if (orderStatusList != null) {
				OrderWithStatusHistory orderWithStatusHistory = null;
				for (OrderStatus orderStatus : orderStatusList) {
					orderWithStatusHistory = new OrderWithStatusHistory();
					orderWithStatusHistory.setOrderHeaderId(orderId);
					for (OrderStatusHistory orderStatusHistory : orderStatusHistoryList) {
						if (orderStatusHistory.getOrderStatusId() == orderStatus.getId()) {
							orderWithStatusHistory.setUpdated(orderStatusHistory.getUpdated());
						}
					}
					orderWithStatusHistory.setOrderStatusId(orderStatus.getId());
					orderWithStatusHistory.setOrderStatusName(orderStatus.getName());
					orderWithStatusHistories.add(orderWithStatusHistory);
				}
			}
		}

		return orderWithStatusHistories;
	}

	/**
	 * Gets the customer order payment detail for order id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the customer order payment detail for order id
	 */
	private List<OrderPaymentDetail> getCustomerOrderPaymentDetailForOrderId(EntityManager em, String orderHeaderId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderPaymentDetail> criteria = builder.createQuery(OrderPaymentDetail.class);
		Root<OrderPaymentDetail> orderPaymentDetail = criteria.from(OrderPaymentDetail.class);
		TypedQuery<OrderPaymentDetail> query = em.createQuery(criteria.select(orderPaymentDetail)
				.where(builder.equal(orderPaymentDetail.get(OrderPaymentDetail_.orderHeaderId), orderHeaderId)));
		List<OrderPaymentDetail> orderDetailItemsList = query.getResultList();

		return orderDetailItemsList;

	}

	/**
	 * Gets the customer order details item for order id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @param locationId
	 *            the location id
	 * @return the customer order details item for order id
	 */
	public List<OrderDetailItem> getCustomerOrderDetailsItemForOrderId(EntityManager em, String orderHeaderId,
			String locationId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderDetailItem> criteria = builder.createQuery(OrderDetailItem.class);
		Root<OrderDetailItem> orderDetailItem = criteria.from(OrderDetailItem.class);

		OrderDetailStatus orderDetailStatus = new OrderDetailStatus();

		orderDetailStatus = getOrderDetailStatusByName(em, locationId, "Item Removed");
		TypedQuery<OrderDetailItem> query = em.createQuery(criteria.select(orderDetailItem).where(
				builder.equal(orderDetailItem.get(OrderDetailItem_.orderHeaderId), orderHeaderId), builder.notEqual(
						orderDetailItem.get(OrderDetailItem_.orderDetailStatusId), orderDetailStatus.getId())));
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();
		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList) {
			orderDetailItemObj.setOrderDetailAttributes(
					getCustomerOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId(), locationId));
		}
		return orderDetailItemsList;

	}

	/**
	 * Gets the customer order detail attribute for order detail item id.
	 *
	 * @param em
	 *            the em
	 * @param orderDetailItemId
	 *            the order detail item id
	 * @param locationId
	 *            the location id
	 * @return the customer order detail attribute for order detail item id
	 */
	List<OrderDetailAttribute> getCustomerOrderDetailAttributeForOrderDetailItemId(EntityManager em,
			String orderDetailItemId, String locationId) {
		OrderDetailStatus orderDetailStatus = new OrderDetailStatus();

		orderDetailStatus = getOrderDetailStatusByName(em, locationId, "Attribute Removed");
		String queryString = "select odi from OrderDetailAttribute odi where odi.orderDetailItemId= ? and odi.orderDetailStatusId!=? order by id asc ";
		TypedQuery<OrderDetailAttribute> query = em.createQuery(queryString, OrderDetailAttribute.class)
				.setParameter(1, orderDetailItemId).setParameter(2, orderDetailStatus.getId());
		return query.getResultList();

	}

	/**
	 * Gets the order detail status by name.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param name
	 *            the name
	 * @return the order detail status by name
	 */
	OrderDetailStatus getOrderDetailStatusByName(EntityManager em, String locationId, String name) {
		String queryString = "select odi from OrderDetailStatus odi where odi.locationsId= ? and odi.name = ? and odi.status!='D' order by id asc ";
		TypedQuery<OrderDetailStatus> query = em.createQuery(queryString, OrderDetailStatus.class)
				.setParameter(1, locationId).setParameter(2, name);
		return query.getSingleResult();
	}

	/**
	 * Manage order relationship.
	 *
	 * @param em
	 *            the em
	 * @param o
	 *            the o
	 * @return the order header
	 */
	public OrderHeader manageOrderRelationship(EntityManager em, OrderHeader o) {
		// change by vaibhav for sale tax implementation

		List<OrderHeaderToSalesTax> orderHeaderToSalesTaxList = o.getOrderHeaderToSalesTax();
		if (orderHeaderToSalesTaxList != null && orderHeaderToSalesTaxList.size() != 0) {
			for (OrderHeaderToSalesTax orderHeaderToSalesTax : orderHeaderToSalesTaxList) {
				OrderHeaderToSalesTax orderHeaderToSalesTaxFromDB = getOrderHeaderToSalesTaxByOrderIdAndTaxId(em,
						o.getId(), orderHeaderToSalesTax.getTaxId());

				if (orderHeaderToSalesTaxFromDB != null) {
					orderHeaderToSalesTaxFromDB.setTaxDisplayName(orderHeaderToSalesTax.getTaxDisplayName());

					orderHeaderToSalesTaxFromDB.setTaxName(orderHeaderToSalesTax.getTaxName());
					orderHeaderToSalesTaxFromDB.setTaxRate(orderHeaderToSalesTax.getTaxRate());
					orderHeaderToSalesTaxFromDB.setTaxValue(orderHeaderToSalesTax.getTaxValue());
					orderHeaderToSalesTaxFromDB.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					em.merge(orderHeaderToSalesTaxFromDB);
				} else {
					orderHeaderToSalesTax.setOrderHeaderId(o.getId());
					orderHeaderToSalesTax.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					orderHeaderToSalesTax.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					em.merge(orderHeaderToSalesTax);
				}

			}
		}

		if (o.getOrderHeaderToSeatDetails() != null) {
			List<OrderHeaderToSeatDetail> orderHeaderToSeatDetails = new ArrayList<OrderHeaderToSeatDetail>();
			for (OrderHeaderToSeatDetail orderHeaderToSeatDetail : o.getOrderHeaderToSeatDetails()) {
				if (orderHeaderToSeatDetail.getCreated() == null) {
					orderHeaderToSeatDetail.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				if (orderHeaderToSeatDetail.getUpdated() == null) {
					orderHeaderToSeatDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				orderHeaderToSeatDetail.setOrderHeaderId(o.getId());
				OrderHeaderToSeatDetail orderHeaderToSeatDetail2 = em.merge(orderHeaderToSeatDetail);
				orderHeaderToSeatDetails.add(orderHeaderToSeatDetail2);
			}

			o.setOrderHeaderToSeatDetails(orderHeaderToSeatDetails);
		}

		return o;
	}

	/**
	 * Gets the order header to sales tax by order id and tax id.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @param taxId
	 *            the tax id
	 * @return the order header to sales tax by order id and tax id
	 */
	public OrderHeaderToSalesTax getOrderHeaderToSalesTaxByOrderIdAndTaxId(EntityManager em, String orderId,
			String taxId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderHeaderToSalesTax> criteria = builder.createQuery(OrderHeaderToSalesTax.class);
			Root<OrderHeaderToSalesTax> orderHeaderToSalesTax = criteria.from(OrderHeaderToSalesTax.class);
			TypedQuery<OrderHeaderToSalesTax> query = em.createQuery(criteria.select(orderHeaderToSalesTax).where(
					builder.equal(orderHeaderToSalesTax.get(OrderHeaderToSalesTax_.orderHeaderId), orderId),
					builder.equal(orderHeaderToSalesTax.get(OrderHeaderToSalesTax_.taxId), taxId)));
			return query.getSingleResult();
		} catch (Exception e) {
			logger.info(e, "No result found when searching for order header sales tax for order id" + orderId,
					"and tax " + taxId);
		}
		return null;
	}

	/**
	 * Gets the gratuity by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @return the gratuity by name and location id
	 */
	//
	public SalesTax getGratuityByNameAndlocationId(EntityManager em, String locationId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> salesTax = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(
					criteria.select(salesTax).where(builder.equal(salesTax.get(SalesTax_.locationsId), locationId),
							builder.notEqual(salesTax.get(SalesTax_.status), "D"),
							builder.notEqual(salesTax.get(SalesTax_.status), "I"),
							builder.equal(salesTax.get(SalesTax_.taxName), "Gratuity")));
			return query.getSingleResult();
		} catch (Exception e) {

			logger.info(e, "No result found when searching for gratuity name for location id " + locationId);
		}
		return null;
	}

	/**
	 * Gets the order payment details to sales tax by id and tax id.
	 *
	 * @param em
	 *            the em
	 * @param OrderPaymentDetailId
	 *            the order payment detail id
	 * @param taxId
	 *            the tax id
	 * @return the order payment details to sales tax by id and tax id
	 */
	public OrderPaymentDetailsToSalesTax getOrderPaymentDetailsToSalesTaxByIdAndTaxId(EntityManager em,
			int OrderPaymentDetailId, int taxId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderPaymentDetailsToSalesTax> criteria = builder
					.createQuery(OrderPaymentDetailsToSalesTax.class);
			Root<OrderPaymentDetailsToSalesTax> orderPaymentDetailsToSalesTaxs = criteria
					.from(OrderPaymentDetailsToSalesTax.class);
			TypedQuery<OrderPaymentDetailsToSalesTax> query = em
					.createQuery(criteria.select(orderPaymentDetailsToSalesTaxs).where(
							builder.equal(
									orderPaymentDetailsToSalesTaxs
											.get(OrderPaymentDetailsToSalesTax_.orderPaymentDetailsId),
									OrderPaymentDetailId),
							builder.equal(orderPaymentDetailsToSalesTaxs.get(OrderPaymentDetailsToSalesTax_.taxId),
									taxId)));
			return query.getSingleResult();
		} catch (NoResultException e) {
			logger.info(e, "No result found when searching for order payment details for order payment detail id "
					+ OrderPaymentDetailId, "and tax id " + taxId);
		}
		return null;
	}

	/**
	 * Update order transfer.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderTransfer
	 *            the order transfer
	 * @param tx
	 *            the tx
	 * @return the order packet for order transfer
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public OrderPacketForOrderTransfer updateOrderTransfer(HttpServletRequest httpRequest, EntityManager em,
			OrderPacketForOrderTransfer orderTransfer, EntityTransaction tx) throws NirvanaXPException {
		// todo shlok need
		// modularise method
		if (orderTransfer != null && orderTransfer.getFromOrderHeader() != null
				&& orderTransfer.getToOrderHeader() != null) {
			// this method will fetch old order update there status as merged ,
			// then change the orderid of order detail items of old to to new
			OrderHeader fromOrder = getOrderByIdAndParentLocationId(em, orderTransfer.getFromOrderHeader().getId(),
					orderTransfer.getRootLocationId());
			OrderHeader toOrder = getOrderByIdAndParentLocationId(em, orderTransfer.getToOrderHeader().getId(),
					orderTransfer.getRootLocationId());
			if (fromOrder.getAmountPaid().intValue() == 0 && toOrder.getAmountPaid().intValue() == 0) {

				if ((fromOrder.getPriceDiscount() == null || toOrder.getPriceDiscount() == null
						|| fromOrder.getPriceDiscountItemLevel() == null || toOrder.getPriceDiscountItemLevel() == null)

						||

						(fromOrder.getPriceDiscount() != null && toOrder.getPriceDiscount() != null
								&& fromOrder.getPriceDiscountItemLevel() != null
								&& toOrder.getPriceDiscountItemLevel() != null
								&& fromOrder.getPriceDiscount().intValue() == 0
								&& toOrder.getPriceDiscount().intValue() == 0
								&& fromOrder.getPriceDiscountItemLevel().intValue() == 0
								&& toOrder.getPriceDiscountItemLevel().intValue() == 0)) {

					// set merge order id of from order
					List<OrderHeader> orderFromMergeOrderIdList = getOrderByMergedOrderId(em, fromOrder.getId());
					if (orderFromMergeOrderIdList != null) {
						for (OrderHeader header : orderFromMergeOrderIdList) {
							header.setMergeOrderId(toOrder.getId());
							header = em.merge(header);
						}
						tx.commit();
						tx.begin();
					}
					fromOrder.setMergeOrderId(toOrder.getId());

					// set "Order Merged" reservation status of from order
					if (fromOrder.getReservationsId() != null) {
						Reservation fromOrderReservation = new CommonMethods().getReservationById(httpRequest, em,
								fromOrder.getReservationsId());

						ReservationsType fromOrderReservationsType = fromOrderReservation.getReservationsType();

						if (fromOrderReservationsType.getName().equals("Walk in")) {
							fromOrderReservation = updateReservationStatus(httpRequest, em, "Order Merged",
									fromOrderReservation, orderTransfer.getRootLocationId(),
									orderTransfer.getUpdatedBy());
						} else if (fromOrderReservationsType.getName().equals("Waitlist")) {
							fromOrderReservation = updateReservationStatus(httpRequest, em, "Order Merged",
									fromOrderReservation, orderTransfer.getRootLocationId(),
									orderTransfer.getUpdatedBy());
						} else {
							fromOrderReservation = updateReservationStatus(httpRequest, em, "Order Merged",
									fromOrderReservation, orderTransfer.getRootLocationId(),
									orderTransfer.getUpdatedBy());
						}

						// insert into reservation history
						new InsertIntoHistory().insertReservationIntoHistory(httpRequest, fromOrderReservation, em);

					}

					OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
							OrderSource.class, fromOrder.getOrderSourceId());
					OrderStatus orderStatus = new CommonMethods()
							.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "Cancel Order",
									orderSource.getLocationsId(), orderSource.getOrderSourceGroupId());
					ReasonType reasonType = getReasonsTypeByNameAndlocationId(httpRequest, em, "Cancel Reasons");
					Reasons cancelReason = getReasonsByNameAndlocationId(httpRequest, em, "Order Merged",
							orderTransfer.getRootLocationId(), reasonType.getId());
					// updating old order to table merged
					fromOrder.setOrderStatusId(orderStatus.getId());
					fromOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					fromOrder.setUpdatedBy(orderTransfer.getUpdatedBy());

					// changing amount of old order
					// adding amount to new order
					if (fromOrder.getPriceExtended() != null && toOrder.getPriceExtended() != null) {
						toOrder.setPriceExtended(fromOrder.getPriceExtended().add(toOrder.getPriceExtended()));
					}

					if (fromOrder.getSubTotal() != null && toOrder.getSubTotal() != null) {
						toOrder.setSubTotal(fromOrder.getSubTotal().add(toOrder.getSubTotal()));
					}

					// This process is done in 3 steps :-
					// 1. Check discount
					// 2. Check gratuity
					// 3. Check tax

					// first step to check discount is applied to target order
					if (toOrder.getDiscountsId() != null && toOrder.getSubTotal() != null
							&& toOrder.getDiscountsValue() != null && toOrder.getDiscountsValue().intValue() > 0) {
						if (toOrder.getDiscountsTypeName().equals("Amount Off")) {
							// do nothing because amount is already set in
							// price_discount of target order
						} else if (toOrder.getDiscountsTypeName().equals("Percentage Off")) {
							// now we have to calculate price_discount of new
							// subtotal
							BigDecimal priceDiscount = toOrder.getSubTotal().multiply(toOrder.getDiscountsValue())
									.divide(new BigDecimal(100));
							toOrder.setPriceDiscount(priceDiscount);
						}
					}

					// third step calculate tax
					// now in this we have two thing. whether
					// 1. whether tax get applied with gratuity
					// 2. Tax get applied without gratuity
					// for this we need to check tax_id in sales tax table
					// Start Tax Calculations

					if (toOrder.getTaxExemptId() == null
							|| (toOrder.getTaxExemptId() != null && toOrder.getTaxExemptId().isEmpty())) {
						if (fromOrder.getTaxName1() != null && !fromOrder.getTaxName1().isEmpty()) {
							toOrder.setTaxName1(fromOrder.getTaxName1());
							toOrder.setTaxDisplayName1(fromOrder.getTaxDisplayName1());
							toOrder.setTaxRate1(fromOrder.getTaxRate1());
						}

						if (fromOrder.getTaxName2() != null && !fromOrder.getTaxName2().isEmpty()) {
							toOrder.setTaxName2(fromOrder.getTaxName2());
							toOrder.setTaxDisplayName2(fromOrder.getTaxDisplayName2());
							toOrder.setTaxRate2(fromOrder.getTaxRate2());
						}

						if (fromOrder.getTaxName3() != null && !fromOrder.getTaxName3().isEmpty()) {
							toOrder.setTaxName3(fromOrder.getTaxName3());
							toOrder.setTaxDisplayName3(fromOrder.getTaxDisplayName3());
							toOrder.setTaxRate3(fromOrder.getTaxRate3());
						}

						if (fromOrder.getTaxName4() != null && !fromOrder.getTaxName4().isEmpty()) {
							toOrder.setTaxName4(fromOrder.getTaxName4());
							toOrder.setTaxDisplayName4(fromOrder.getTaxDisplayName4());
							toOrder.setTaxRate4(fromOrder.getTaxRate4());
						}
					}

					BigDecimal itemTotalTax1 = new BigDecimal(0.00);
					BigDecimal itemTotalTax2 = new BigDecimal(0.00);
					BigDecimal itemTotalTax3 = new BigDecimal(0.00);
					BigDecimal itemTotalTax4 = new BigDecimal(0.00);

					BigDecimal itemDiscount = new BigDecimal(0.00);
					BigDecimal orderDiscount = new BigDecimal(0.00);

					boolean isItemLevelDisApplied = false;

					int toOrderCount = toOrder.getSplitCount();
					// copy tax calculation from toorderheader
					for (OrderDetailItem detailItem : toOrder.getOrderDetailItems()) {

						// item level dis calculation
						if (detailItem.getPriceDiscount() != null) {
							itemDiscount = itemDiscount.add(detailItem.getPriceDiscount());
						}

						// order level dis calculation
						if (toOrder.getDiscountsId() != null && toOrder.getSubTotal() != null
								&& toOrder.getDiscountsValue().doubleValue() > 0) {
							if (detailItem.getDiscountId() == null) {
								if (toOrder.getDiscountDisplayName().equals("Amount Off")) {
									// do nothing because amount is already set
									// in
									// price_discount of target order
									// now we need to first identify, how much
									// percentage from amount off
									BigDecimal rate = toOrder.getPriceDiscount().multiply(new BigDecimal(100))
											.divide(toOrder.getSubTotal());
									orderDiscount = orderDiscount
											.add(detailItem.getSubTotal().multiply(rate).divide(new BigDecimal(100)));

								} else if (toOrder.getDiscountsTypeName().equals("Percentage Off")) {
									// now we have to calculate price_discount
									// of new
									// subtotal
									orderDiscount = orderDiscount.add(detailItem.getSubTotal()
											.multiply(toOrder.getDiscountsValue()).divide(new BigDecimal(100)));

								}
							}

						}
						if (detailItem.getDiscountWaysId() != 0) {
							DiscountWays discountWays = em.find(DiscountWays.class, detailItem.getDiscountWaysId());
							if (discountWays != null && (discountWays.getName().equals("Item Level")
									|| discountWays.getName().equals("Seat Level"))) {
								isItemLevelDisApplied = true;
							}
						}

						if (detailItem.getTaxName1() != null) {

							itemTotalTax1 = itemTotalTax1.add(detailItem.getPriceTax1());
						}

						if (detailItem.getTaxName2() != null) {

							itemTotalTax2 = itemTotalTax2.add(detailItem.getPriceTax2());
						}
						if (detailItem.getTaxName3() != null) {
							itemTotalTax3 = itemTotalTax3.add(detailItem.getPriceTax3());
						}
						if (detailItem.getTaxName4() != null) {
							itemTotalTax4 = itemTotalTax4.add(detailItem.getPriceTax4());
						}
					}

					for (OrderDetailItem detailItem : fromOrder.getOrderDetailItems()) {

						// seat assignment to items
						detailItem.setPriceGratuity(new BigDecimal(0));
						detailItem.setGratuity(new BigDecimal(0));

						if (toOrder.getIsSeatWiseOrder() == 1) {
							if (fromOrder.getIsSeatWiseOrder() == 1) {
								int itemSeatNumber = Integer.parseInt(detailItem.getSeatId().replace("S", ""));
								int count = toOrderCount + itemSeatNumber;
								detailItem.setSeatId("S" + count);

							} else {
								int count = toOrderCount + 1;
								detailItem.setSeatId("S" + count);
							}

						} else {
							detailItem.setSeatId("S0");
						}

						detailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						detailItem.setOrderHeaderId(toOrder.getId());
						detailItem.setUpdatedBy(orderTransfer.getUpdatedBy());
						detailItem.setPointOfServiceNum(toOrder.getPointOfServiceCount());
						BigDecimal priceDiscount = new BigDecimal(0);
						if (toOrder.getDiscountsId() != null && toOrder.getSubTotal() != null
								&& toOrder.getDiscountsValue().doubleValue() > 0) {
							if (toOrder.getDiscountsTypeName().equals("Amount Off")) {
								// do nothing because amount is already set in
								// price_discount of target order
								// now we need to first identify, how much
								// percentage from amount off
								BigDecimal rate = toOrder.getPriceDiscount().multiply(new BigDecimal(100))
										.divide(toOrder.getSubTotal());
								priceDiscount = detailItem.getSubTotal().multiply(rate).divide(new BigDecimal(100));

							} else if (toOrder.getDiscountsTypeName().equals("Percentage Off")) {
								// now we have to calculate price_discount of
								// new
								// subtotal
								priceDiscount = detailItem.getSubTotal().multiply(toOrder.getDiscountsValue())
										.divide(new BigDecimal(100));

							}

							if (isItemLevelDisApplied) {
								detailItem.setPriceDiscount(priceDiscount);
							}

							itemDiscount = itemDiscount.add(priceDiscount);

						}

						// order level dis calculation
						if (toOrder.getDiscountsId() != null && toOrder.getSubTotal() != null
								&& toOrder.getDiscountsValue().doubleValue() > 0) {
							if (toOrder.getDiscountDisplayName().equals("Amount Off")) {
								// do nothing because amount is already set in
								// price_discount of target order
								// now we need to first identify, how much
								// percentage from amount off
								BigDecimal rate = toOrder.getPriceDiscount().multiply(new BigDecimal(100))
										.divide(toOrder.getSubTotal());
								orderDiscount = orderDiscount
										.add(detailItem.getSubTotal().multiply(rate).divide(new BigDecimal(100)));

							} else if (toOrder.getDiscountsTypeName().equals("Percentage Off")) {
								// now we have to calculate price_discount of
								// new
								// subtotal
								orderDiscount = orderDiscount.add(detailItem.getSubTotal()
										.multiply(toOrder.getDiscountsValue()).divide(new BigDecimal(100)));

							}

						}
						// now we need to check gratuity applied or not
						if (toOrder.getIsGratuityApplied() == 1 && toOrder.getGratuity() != null
								&& toOrder.getGratuity().intValue() > 0) {
							// now we have to calculate price gratuity
							BigDecimal subTotal = detailItem.getSubTotal();
							BigDecimal gratuityValue = toOrder.getGratuity();
							// formula for calculating price gratuity
							BigDecimal priceGratuity = (subTotal.subtract(priceDiscount).multiply(gratuityValue)
									.divide(new BigDecimal(100)));
							detailItem.setPriceGratuity(priceGratuity);
							detailItem.setGratuity(toOrder.getGratuity());

						}

						// calculationg total tax from database
						// Start Tax Calculations

						BigDecimal itemTotalTax = new BigDecimal(0.00);

						if (toOrder.getTaxExemptId() == null
								|| (toOrder.getTaxExemptId() != null && toOrder.getTaxExemptId().isEmpty())) {
							SalesTax detailItemTax1 = null;
							SalesTax detailItemTax2 = null;
							SalesTax detailItemTax3 = null;
							SalesTax detailItemTax4 = null;

							if (detailItem.getTaxName1() != null) {

								detailItemTax1 = getSalesTaxByNameAndlocationId(em, detailItem.getTaxName1(),
										orderSource.getLocationsId());
							}

							if (detailItem.getTaxName2() != null) {

								detailItemTax2 = getSalesTaxByNameAndlocationId(em, detailItem.getTaxName2(),
										orderSource.getLocationsId());
							}
							if (detailItem.getTaxName3() != null) {

								detailItemTax3 = getSalesTaxByNameAndlocationId(em, detailItem.getTaxName3(),
										orderSource.getLocationsId());
							}
							if (detailItem.getTaxName4() != null) {

								detailItemTax4 = getSalesTaxByNameAndlocationId(em, detailItem.getTaxName4(),
										orderSource.getLocationsId());
							}

							if (detailItemTax1 != null) {
								SalesTax gratuityTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,
										SalesTax.class, detailItemTax1.getTaxId());

								BigDecimal subTotal = detailItem.getSubTotal();
								// checking the condtion for gratuity apprilesd
								// //
								// or not

								if (gratuityTax != null && gratuityTax.getTaxName().equals("Gratuity")) {
									BigDecimal priceGratuity = detailItem.getPriceGratuity();
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount)
											.add(priceGratuity));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax1.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax1(calculateTax);

									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax1 = itemTotalTax1.add(calculateTax);
								} else {
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax1.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax1(calculateTax);
									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax1 = itemTotalTax1.add(calculateTax);
								}

							}

							if (detailItemTax2 != null) {
								SalesTax gratuityTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,
										SalesTax.class, detailItemTax2.getTaxId());

								BigDecimal subTotal = detailItem.getSubTotal();
								// checking the condtion for gratuity apprilesd
								// //
								// or not

								if (gratuityTax != null && gratuityTax.getTaxName().equals("Gratuity")) {
									BigDecimal priceGratuity = detailItem.getPriceGratuity();
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount)
											.add(priceGratuity));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax2.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax2(calculateTax);

									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax2 = itemTotalTax2.add(calculateTax);
								} else {
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax2.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax2(calculateTax);
									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax2 = itemTotalTax2.add(calculateTax);
								}
							}
							if (detailItemTax3 != null) {
								SalesTax gratuityTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,
										SalesTax.class, detailItemTax3.getTaxId());

								BigDecimal subTotal = detailItem.getSubTotal();
								// checking the condtion for gratuity apprilesd
								// //
								// or not

								if (gratuityTax != null && gratuityTax.getTaxName().equals("Gratuity")) {
									BigDecimal priceGratuity = detailItem.getPriceGratuity();
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount)
											.add(priceGratuity));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax3.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax3(calculateTax);

									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax3 = itemTotalTax3.add(calculateTax);
								} else {
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax3.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax3(calculateTax);
									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax3 = itemTotalTax3.add(calculateTax);
								}
							}
							if (detailItemTax4 != null) {
								SalesTax gratuityTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,
										SalesTax.class, detailItemTax4.getTaxId());

								BigDecimal subTotal = detailItem.getSubTotal();
								// checking the condtion for gratuity apprilesd
								// //
								// or not

								if (gratuityTax != null && gratuityTax.getTaxName().equals("Gratuity")) {
									BigDecimal priceGratuity = detailItem.getPriceGratuity();
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount)
											.add(priceGratuity));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax4.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax4(calculateTax);

									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax4 = itemTotalTax4.add(calculateTax);
								} else {
									BigDecimal amountOnWhichTaxCalculate = (subTotal.subtract(priceDiscount));
									BigDecimal calculateTax = amountOnWhichTaxCalculate
											.multiply(detailItemTax4.getRate().divide(new BigDecimal(100)));
									detailItem.setPriceTax4(calculateTax);
									itemTotalTax = itemTotalTax.add(calculateTax);
									itemTotalTax4 = itemTotalTax4.add(calculateTax);
								}
							}
						}

						detailItem.setTotalTax(itemTotalTax);
						// End Tax Calculations

						BigDecimal subTotal = detailItem.getSubTotal();
						BigDecimal priceGratuity = detailItem.getPriceGratuity();
						BigDecimal totalTax1 = detailItem.getTotalTax();
						BigDecimal total = (subTotal.subtract(priceDiscount).add(priceGratuity).add(totalTax1));
						detailItem.setTotal(total);
						MathContext mc = new MathContext(3);
						if (detailItem.getTotal() != null)
							detailItem.setRoundOffTotal(detailItem.getTotal().round(mc));
						em.merge(detailItem);

					}

					toOrder.setPriceTax1(itemTotalTax1);
					toOrder.setPriceTax2(itemTotalTax2);
					toOrder.setPriceTax3(itemTotalTax3);
					toOrder.setPriceTax4(itemTotalTax4);
					toOrder.setTotalTax(itemTotalTax1.add(itemTotalTax2).add(itemTotalTax3).add(itemTotalTax4));
					toOrder.setPriceDiscount(orderDiscount);
					if (isItemLevelDisApplied) {
						toOrder.setPriceDiscountItemLevel(itemDiscount);
					} else {
						toOrder.setPriceDiscountItemLevel(new BigDecimal(0));
						itemDiscount = new BigDecimal(0);
					}

					// now we need to check gratuity applied or not
					if (toOrder.getIsGratuityApplied() == 1 && toOrder.getGratuity() != null
							&& toOrder.getGratuity().intValue() > 0) {
						// now we have to calculate price gratuity
						BigDecimal subTotal = toOrder.getSubTotal();
						BigDecimal priceDiscount = toOrder.getPriceDiscount();
						BigDecimal gratuityValue = toOrder.getGratuity();
						// formula for calculating price gratuity
						BigDecimal priceGratuity = (subTotal.subtract(priceDiscount.add(itemDiscount))
								.multiply(gratuityValue).divide(new BigDecimal(100)));
						toOrder.setPriceGratuity(priceGratuity);
					}

					if (toOrder.getTotal() != null) {
						// total tax
						// subtotal - price discount + price gratuity + total
						// tax
						BigDecimal subTotal = toOrder.getSubTotal().subtract(itemDiscount);
						BigDecimal priceDiscount = toOrder.getPriceDiscount();
						BigDecimal priceGratuity = toOrder.getPriceGratuity();
						BigDecimal totalTax = toOrder.getTotalTax();
						BigDecimal total = (subTotal.subtract(priceDiscount).add(priceGratuity).add(totalTax));
						toOrder.setTotal(total);
					}

					if (fromOrder.getMergedLocationsId() != null && toOrder.getMergedLocationsId() != null
							&& fromOrder.getMergedLocationsId().length() > 0
							&& toOrder.getMergedLocationsId().length() > 0) {
						toOrder.setMergedLocationsId(toOrder.getMergedLocationsId() + ","
								+ fromOrder.getMergedLocationsId() + "," + fromOrder.getLocationsId() + "");
					} else if (toOrder
							.getMergedLocationsId() != null /*
															 * && fromOrder .
															 * getMergedLocationsId
															 * () == null
															 */
							&& toOrder.getMergedLocationsId().length() > 0) {
						toOrder.setMergedLocationsId(
								toOrder.getMergedLocationsId() + "," + fromOrder.getLocationsId() + "");

					} else if (/* toOrder.getMergedLocationsId() == null && */fromOrder.getMergedLocationsId() != null
							&& fromOrder.getMergedLocationsId().length() > 0) {
						toOrder.setMergedLocationsId(
								fromOrder.getMergedLocationsId() + "," + fromOrder.getLocationsId() + "");
					} else {
						toOrder.setMergedLocationsId(fromOrder.getLocationsId() + "");
					}

					toOrder.setPointOfServiceCount(
							(toOrder.getPointOfServiceCount() + fromOrder.getPointOfServiceCount()));
					int splitCount = 0;

					if (toOrder.getIsSeatWiseOrder() == 1 && fromOrder.getIsSeatWiseOrder() == 1) {
						splitCount = toOrder.getSplitCount().intValue() + fromOrder.getSplitCount().intValue();

					} else if (fromOrder.getIsSeatWiseOrder() == 0 && toOrder.getIsSeatWiseOrder() == 1) {
						splitCount = toOrder.getSplitCount() + fromOrder.getPointOfServiceCount();

					} else {
						splitCount = 0;
					}

					toOrder.setSplitCount(splitCount);
					toOrder.setBalanceDue(toOrder.getTotal().subtract(toOrder.getAmountPaid()));
					MathContext mc = new MathContext(3);
					if (toOrder.getTotal() != null)
						toOrder.setRoundOffTotal(toOrder.getTotal().round(mc));
					em.merge(toOrder);

					// insert into order history
					insertIntoOrderStatusHistory(em, fromOrder);
					new InsertIntoHistory().insertOrderIntoHistory(httpRequest, fromOrder, em);

					fromOrder.setBalanceDue(new BigDecimal(0));
					fromOrder.setTotal(new BigDecimal(0));
					fromOrder.setAmountPaid(new BigDecimal(0));
					fromOrder.setPriceGratuity(new BigDecimal(0));
					fromOrder.setPriceDiscount(new BigDecimal(0));
					fromOrder.setCalculatedDiscountValue(new BigDecimal(0));
					fromOrder.setRoundOffTotal(new BigDecimal(0));
					fromOrder.setTotalTax(new BigDecimal(0));
					fromOrder.setSubTotal(new BigDecimal(0));
					fromOrder.setDiscountsId(null);
					fromOrder.setDiscountDisplayName(null);
					fromOrder.setDiscountsName(null);
					fromOrder.setDiscountsTypeId(null);
					fromOrder.setDiscountsTypeName(null);
					fromOrder.setDiscountsValue(new BigDecimal(0));
					fromOrder.setPriceExtended(new BigDecimal(0));
					fromOrder.setPriceTax1(new BigDecimal(0));
					fromOrder.setPriceTax2(new BigDecimal(0));
					fromOrder.setPriceTax3(new BigDecimal(0));
					fromOrder.setPriceTax4(new BigDecimal(0));
					fromOrder.setTaxDisplayName1(null);
					fromOrder.setTaxDisplayName2(null);
					fromOrder.setTaxDisplayName3(null);
					fromOrder.setTaxDisplayName4(null);
					fromOrder.setTaxName1(null);
					fromOrder.setTaxName2(null);
					fromOrder.setTaxName3(null);
					fromOrder.setTaxName4(null);
					fromOrder.setTaxRate1(new BigDecimal(0));
					fromOrder.setTaxRate2(new BigDecimal(0));
					fromOrder.setTaxRate3(new BigDecimal(0));
					fromOrder.setTaxRate4(new BigDecimal(0));

					fromOrder.setVoidReasonId(cancelReason.getId());
					fromOrder.setVoidReasonName(cancelReason.getDisplayName());
					em.merge(fromOrder);
					// setTaxValueInColumns(toOrder, em);
					// getting latest data from order
					toOrder = getOrderById(em, toOrder.getId());
					new InsertIntoHistory().insertOrderIntoHistory(httpRequest, toOrder, em);
					fromOrder = getOrderById(em, fromOrder.getId());
					orderTransfer.setFromOrderHeader(fromOrder);
					orderTransfer.setToOrderHeader(toOrder);

				} else {
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(
							MessageConstants.ERROR_CODE_ORDER_CANNOT_TRANSFER_DIS_APPLY_CODE,
							MessageConstants.ERROR_MESSAGE_ORDER_CANNOT_TRANSFER_DIS_APPLY_MESSAGE,
							MessageConstants.ERROR_MESSAGE_ORDER_CANNOT_TRANSFER_DIS_APPLY_DISPLAY_MESSAGE));
				}

			} else {
				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDER_CANNOT_TRANSFER_CODE,
								MessageConstants.ERROR_MESSAGE_ORDER_CANNOT_TRANSFER__MESSAGE,
								MessageConstants.ERROR_MESSAGE_ORDER_CANNOT_TRANSFER_DISPLAY_MESSAGE));
			}
		} else {
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDER_CANNOT_TRANSFER_CODE,
							MessageConstants.ERROR_MESSAGE_ORDER_CANNOT_TRANSFER__MESSAGE,
							MessageConstants.ERROR_MESSAGE_ORDER_CANNOT_TRANSFER_DISPLAY_MESSAGE));
		}
		return orderTransfer;
	}

	/**
	 * Update item transfer.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderTransfer
	 *            the order transfer
	 * @return the order packet for order transfer
	 * @throws Exception
	 *             the exception
	 */
	public OrderPacketForOrderTransfer updateItemTransfer(HttpServletRequest httpRequest, EntityManager em,
			OrderPacketForOrderTransfer orderTransfer, int accountId) throws Exception {
		if (orderTransfer != null && orderTransfer.getFromOrderHeader() != null
				&& orderTransfer.getToOrderHeader() != null) {
			orderTransfer.getFromOrderHeader().setSessionKey(orderTransfer.getIdOfSessionUsedByPacket());
			orderTransfer.getToOrderHeader().setSessionKey(orderTransfer.getIdOfSessionUsedByPacket());

			OrderHeaderWithInventoryPostPacket fromHeader = updateItemTransfer(httpRequest, em,
					orderTransfer.getFromOrderHeader(), orderTransfer.getRootLocationId(), false, accountId);
			OrderHeaderWithInventoryPostPacket toHeader = updateItemTransfer(httpRequest, em,
					orderTransfer.getToOrderHeader(), orderTransfer.getRootLocationId(), false, accountId);

			orderTransfer.setFromOrderHeader(fromHeader.getOrderHeader());
			orderTransfer.setToOrderHeader(toHeader.getOrderHeader());
		} else {
			throw new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ITEM_CANNOT_TRANSFER_CODE,
							MessageConstants.ERROR_MESSAGE_ITEM_CANNOT_TRANSFER__MESSAGE,
							MessageConstants.ERROR_MESSAGE_ITEM_CANNOT_TRANSFER_DISPLAY_MESSAGE));
		}
		return orderTransfer;
	}

	/**
	 * Gets the order detail item by order id and seat id.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @param seatId
	 *            the seat id
	 * @return the order detail item by order id and seat id
	 */
	public List<OrderDetailItem> getOrderDetailItemByOrderIdAndSeatId(EntityManager em, int orderId, String seatId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailItem> criteria = builder.createQuery(OrderDetailItem.class);
			Root<OrderDetailItem> orderDetailItemList = criteria.from(OrderDetailItem.class);
			TypedQuery<OrderDetailItem> query = em.createQuery(criteria.select(orderDetailItemList).where(
					builder.equal(orderDetailItemList.get(OrderDetailItem_.orderHeaderId), orderId),
					builder.equal(orderDetailItemList.get(OrderDetailItem_.seatId), seatId)));
			return query.getResultList();
		} catch (NoResultException e) {
			logger.info(e, "No result found when searching for order details for order id", "" + orderId, "and seat",
					seatId);
		}
		return null;
	}

	/**
	 * Gets the order detail item by order id.
	 *
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @return the order detail item by order id
	 */
	public List<OrderDetailItem> getOrderDetailItemByOrderId(EntityManager em, int orderId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailItem> criteria = builder.createQuery(OrderDetailItem.class);
			Root<OrderDetailItem> orderDetailItemList = criteria.from(OrderDetailItem.class);
			TypedQuery<OrderDetailItem> query = em.createQuery(criteria.select(orderDetailItemList)
					.where(builder.equal(orderDetailItemList.get(OrderDetailItem_.orderHeaderId), orderId)));
			return query.getResultList();
		} catch (NoResultException e) {
			logger.info(e, "No result found when searching for order details for order id", "" + orderId);
		}
		return null;
	}

	/**
	 * Gets the order payment details by order header id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the order payment details by order header id
	 */
	public List<OrderPaymentDetail> getOrderPaymentDetailsByOrderHeaderId(EntityManager em, String orderHeaderId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderPaymentDetail> criteria = builder.createQuery(OrderPaymentDetail.class);
		Root<OrderPaymentDetail> r = criteria.from(OrderPaymentDetail.class);
		TypedQuery<OrderPaymentDetail> query = em.createQuery(
				criteria.select(r).where(builder.equal(r.get(OrderPaymentDetail_.orderHeaderId), orderHeaderId),
						builder.equal(r.get(OrderPaymentDetail_.discountId), 0),
						builder.notEqual(r.get(OrderPaymentDetail_.isRefunded), 1)));

		List<OrderPaymentDetail> result = (List<OrderPaymentDetail>) query.getResultList();
		return result;

	}

	/**
	 * Gets the order by id and update date.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param updatedDate
	 *            the updated date
	 * @return the order by id and update date
	 */
	public OrderHeader getOrderByIdAndUpdateDate(EntityManager em, @PathParam("id") String id,
			@PathParam("updatedDate") String updatedDate) {

		Timestamp updated = Timestamp.valueOf(updatedDate);

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> r = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em.createQuery(criteria.select(r).where(
				builder.equal(r.get(OrderHeader_.id), id), builder.greaterThan(r.get(OrderHeader_.updated), updated)));
		OrderHeader orderHeader = query.getSingleResult();
		OrderHeader result = new OrderHeader(id, orderHeader.getAddressBilling(), orderHeader.getAddressShipping(),
				orderHeader.getAmountPaid(), orderHeader.getBalanceDue(), orderHeader.getCreated(),
				orderHeader.getCreatedBy(), orderHeader.getDiscountsId(), orderHeader.getDiscountsName(),
				orderHeader.getDiscountsTypeId(), orderHeader.getDiscountsTypeName(), orderHeader.getDiscountsValue(),
				orderHeader.getGratuity(), orderHeader.getIpAddress(), orderHeader.getOrderSourceId(),
				orderHeader.getOrderStatusId(), orderHeader.getPointOfServiceCount(), orderHeader.getPriceDiscount(),
				orderHeader.getPriceExtended(), orderHeader.getPriceGratuity(), orderHeader.getPriceTax1(),
				orderHeader.getPriceTax2(), orderHeader.getPriceTax3(), orderHeader.getPriceTax4(),
				orderHeader.getReservationsId(), orderHeader.getServiceTax(), orderHeader.getSubTotal(),
				orderHeader.getTotal(), orderHeader.getSplitCount(), orderHeader.getUpdated(),
				orderHeader.getUpdatedBy(), orderHeader.getUsersId(), orderHeader.getLocationsId(),
				orderHeader.getPaymentWaysId(), orderHeader.getDate(), orderHeader.getVerificationCode(),
				orderHeader.getQrcode(), orderHeader.getSessionKey(), orderHeader.getFirstName(),
				orderHeader.getLastName(), orderHeader.getServerId(), orderHeader.getCashierId(),
				orderHeader.getVoidReasonId(), orderHeader.getMergedLocationsId(), orderHeader.getOpenTime(),
				orderHeader.getCloseTime(), orderHeader.getDiscountDisplayName(), orderHeader.getTaxDisplayName1(),
				orderHeader.getTaxDisplayName2(), orderHeader.getTaxDisplayName3(), orderHeader.getTaxDisplayName4(),
				orderHeader.getTaxRate1(), orderHeader.getTaxRate2(), orderHeader.getTaxRate3(),
				orderHeader.getTaxRate4(), orderHeader.getTotalTax(), orderHeader.getTaxName1(),
				orderHeader.getTaxName2(), orderHeader.getTaxName3(), orderHeader.getTaxName4(),
				orderHeader.getIsGratuityApplied(), orderHeader.getRoundOffTotal(), orderHeader.getIsTabOrder(),
				orderHeader.getServername(), orderHeader.getCashierName(), orderHeader.getVoidReasonName(),
				orderHeader.getIsOrderReopened(), orderHeader.getTaxExemptId(), orderHeader.getScheduleDateTime(),
				orderHeader.getReferenceNumber(), orderHeader.getNirvanaXpBatchNumber(), orderHeader.getOrderNumber(),
				orderHeader.getIsSeatWiseOrder(), orderHeader.getCalculatedDiscountValue(),
				orderHeader.getShiftSlotId(), orderHeader.getPriceDiscountItemLevel(), orderHeader.getMergeOrderId(),
				orderHeader.getCompanyName(), orderHeader.getTaxNo(), orderHeader.getTaxDisplayName(),
				orderHeader.getDeliveryCharges(), orderHeader.getDeliveryTax(), orderHeader.getDeliveryOptionId(),
				orderHeader.getServiceCharges(), orderHeader.getPreassignedServerId(), orderHeader.getLocalTime(),
				orderHeader.getPoRefrenceNumber(), orderHeader.getRequestedLocationId(), orderHeader.getOrderTypeId(),
				orderHeader.getDriverId(), orderHeader.getComment(), orderHeader.getStartDate(),
				orderHeader.getEndDate(), orderHeader.getEventName());

		result.setOrderDetailItems(getOrderDetailsItemForOrderId(em, id));
		result.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, id));
		result.setOrderHeaderToSeatDetails(getOrderHeaderToSeatDetailForOrderId(em, id));
		return result;
	}

	/**
	 * Update order payment for braintree.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param order
	 *            the order
	 * @param locationId
	 *            the location id
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public OrderHeader updateOrderPaymentForBraintree(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader order, String locationId) throws NirvanaXPException {

		OrderHeader o = getOrderById(em, order.getId());
		o.setUpdated(new Date(updatedTime()));
		o.setUpdatedBy(order.getUpdatedBy());
		o.setOrderStatusId(order.getOrderStatusId());
		o.setTotal(order.getTotal());
		o.setBalanceDue(order.getBalanceDue());
		o.setServiceTax(order.getServiceTax());
		o.setPriceTax1(order.getPriceTax1());
		o.setPriceTax2(order.getPriceTax2());
		o.setPriceTax3(order.getPriceTax3());
		o.setPriceTax4(order.getPriceTax4());
		o.setPriceExtended(order.getPriceExtended());
		o.setPriceGratuity(order.getPriceGratuity());
		o.setPriceDiscount(order.getPriceDiscount());
		o.setGratuity(order.getGratuity());
		o.setAmountPaid(order.getAmountPaid());
		o.setSubTotal(order.getSubTotal());
		o.setCashierName(order.getCashierName());
		if (order.getCalculatedDiscountValue() != null) {
			o.setCalculatedDiscountValue(order.getCalculatedDiscountValue());
		}

		o.setPriceDiscountItemLevel(order.getPriceDiscountItemLevel());
		if (order.getPaymentWaysId() != null) {
			o.setPaymentWaysId(order.getPaymentWaysId());
		}
		if (order.getSplitCount() != null) {
			o.setSplitCount(order.getSplitCount());
		}
		o.setSessionKey(order.getSessionKey());
		o.setCashierId(order.getCashierId());

		OrderHeader newOH = em.merge(o);
		o.setUpdated(newOH.getUpdated());
		if (order.getOrderPaymentDetails() != null) {
			for (OrderPaymentDetail orderPaymentDetail : order.getOrderPaymentDetails()) {

				orderPaymentDetail.setOrderHeaderId(o.getId());
				// add or update payment detail
				if (orderPaymentDetail.getCreated() == null) {
					orderPaymentDetail.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				if (orderPaymentDetail.getUpdated() == null) {
					orderPaymentDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				}
				orderPaymentDetail.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				em.merge(orderPaymentDetail);
				insertIntoOrderPaymentDetailHistory(em, orderPaymentDetail);
			}

		}
		manageOrderPaymentDetailSaleTax(em, o, false);
		new InsertIntoHistory().insertOrderIntoHistory(httpRequest, o, em);

		logger.severe(httpRequest, "adding order payment detail for order: " + o.getId());

		OrderHeader new_order = getOrderById(em, o.getId());

		return new_order;

	}

	/**
	 * Manage order payment detail sale tax.
	 *
	 * @param em
	 *            the em
	 * @param o
	 *            the o
	 * @param isSeatWise
	 *            the is seat wise
	 * @return the list
	 */
	private List<OrderPaymentDetail> manageOrderPaymentDetailSaleTax(EntityManager em, OrderHeader o,
			Boolean isSeatWise) {

		if (o.getOrderPaymentDetails() != null) {
			// compare bg1 with bg2
			int res = o.getBalanceDue().compareTo(new BigDecimal(0));
			if (res != 1) {
				List<OrderPaymentDetail> orderPaymentDetailsList = new ArrayList<OrderPaymentDetail>();
				BigDecimal orderTotal = o.getTotal();
				BigDecimal divident = new BigDecimal(0);
				List<OrderPaymentDetail> orderPaymentDetailsArray = new ArrayList<OrderPaymentDetail>();
				List<OrderPaymentDetail> orderPaymentDetails = getOrderPaymentDetailsByOrderHeaderId(em, o.getId());
				if (orderPaymentDetails != null && orderPaymentDetails.size() != 0) {
					orderPaymentDetailsArray.addAll(orderPaymentDetails);
				}
				for (OrderPaymentDetail orderPaymentDetail : orderPaymentDetailsArray) {
					BigDecimal amountPaid = orderPaymentDetail.getAmountPaid();
					divident = amountPaid.divide(orderTotal, 2, BigDecimal.ROUND_HALF_DOWN)
							.multiply(new BigDecimal(100));

					if (o.getPriceTax1() != null && o.getPriceTax1() != new BigDecimal(0)) {

						orderPaymentDetail.setTaxName1(o.getTaxName1());
						orderPaymentDetail.setTaxDisplayName1(o.getTaxDisplayName1());
						orderPaymentDetail.setTaxRate1(o.getTaxRate1());
						orderPaymentDetail.setPriceTax1(o.getPriceTax1().multiply(divident).divide(new BigDecimal(100),
								2, BigDecimal.ROUND_HALF_DOWN));
					}
					if (o.getPriceTax2() != null && o.getPriceTax2() != new BigDecimal(0)) {

						orderPaymentDetail.setTaxName2(o.getTaxName2());
						orderPaymentDetail.setTaxDisplayName2(o.getTaxDisplayName2());
						orderPaymentDetail.setTaxRate2(o.getTaxRate2());
						orderPaymentDetail.setPriceTax2(o.getPriceTax2().multiply(divident).divide(new BigDecimal(100),
								2, BigDecimal.ROUND_HALF_DOWN));
					}
					if (o.getPriceTax3() != null && o.getPriceTax3() != new BigDecimal(0)) {

						orderPaymentDetail.setTaxName3(o.getTaxName3());
						orderPaymentDetail.setTaxDisplayName3(o.getTaxDisplayName3());
						orderPaymentDetail.setTaxRate3(o.getTaxRate3());
						orderPaymentDetail.setPriceTax3(o.getPriceTax3().multiply(divident).divide(new BigDecimal(100),
								2, BigDecimal.ROUND_HALF_DOWN));
					}
					if (o.getPriceTax4() != null && o.getPriceTax4() != new BigDecimal(0)) {

						orderPaymentDetail.setTaxName4(o.getTaxName4());
						orderPaymentDetail.setTaxDisplayName4(o.getTaxDisplayName4());
						orderPaymentDetail.setTaxRate4(o.getTaxRate4());
						orderPaymentDetail.setPriceTax4(o.getPriceTax4().multiply(divident).divide(new BigDecimal(100),
								2, BigDecimal.ROUND_HALF_DOWN));
					}

					orderPaymentDetailsList.add(orderPaymentDetail);
					em.merge(orderPaymentDetail);
				}
				return orderPaymentDetailsList;
			}
		}
		return null;
	}

	/**
	 * Check active client for shift slot.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderHeader
	 *            the order header
	 * @param idOfClientHoldingTheSlot
	 *            the id of client holding the slot
	 * @return the shift slot active client info
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	private ShiftSlotActiveClientInfo checkActiveClientForShiftSlot(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader orderHeader, int idOfClientHoldingTheSlot) throws NirvanaXPException {
		ShiftSlotActiveClientInfo slotActiveClientInfo = null;

		if (idOfClientHoldingTheSlot == 0) {
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(
					MessageConstants.ERROR_CODE_SHIFT_NO_ACTIVE_CLIENT_ID_AVAILABLE_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_SHIFT_NO_ACTIVE_CLIENT_ID_AVAILABLE_DISPLAY_MESSAGE, null));
		}

		slotActiveClientInfo = em.find(ShiftSlotActiveClientInfo.class, idOfClientHoldingTheSlot);

		slotActiveClientInfo.setUpdatedBy(orderHeader.getUpdatedBy());

		slotActiveClientInfo.setShiftMadeByClient(true);
		em.merge(slotActiveClientInfo);

		return slotActiveClientInfo;

	}

	/**
	 * Update shift slot current active shift count.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param shiftSlotActiveClientInfo
	 *            the shift slot active client info
	 * @param order
	 *            the order
	 * @param shouldIncrementOrderCount
	 *            the should increment order count
	 * @param fromTakeOutOrDelevery
	 *            the from take out or delevery
	 * @return the int
	 */
	private int updateShiftSlotCurrentActiveShiftCount(HttpServletRequest httpRequest, EntityManager em,
			ShiftSlotActiveClientInfo shiftSlotActiveClientInfo, OrderHeader order, boolean shouldIncrementOrderCount,
			boolean fromTakeOutOrDelevery) {
		int shiftSlotId = shiftSlotActiveClientInfo.getShiftSlotId();
		ShiftSlots shiftSlot = em.find(ShiftSlots.class, shiftSlotId);
		if (shiftSlot != null) {
			shiftSlot.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			shiftSlot.setUpdatedBy(order.getUpdatedBy());
			// a shift might have been created in this slot, so
			// increment the count
			if (shouldIncrementOrderCount) {
				shiftSlot.setCurrentOrderInSlot(shiftSlot.getCurrentOrderInSlot() + 1);
				shiftSlot.setCurrentlyHoldedClient(shiftSlot.getCurrentlyHoldedClient() - 1);
				order.setShiftSlotId(shiftSlotId);
				em.remove(em.merge(shiftSlotActiveClientInfo));

			} else {
				// a order might have been cancelled, so we must
				// change the count
				shiftSlot.setCurrentOrderInSlot(shiftSlot.getCurrentOrderInSlot() - 1);
			}

			// check if the slot status must be on hold or made activated or
			// not after the change
			if (shiftSlot.getStatus().equals("D") == false && shiftSlot.getStatus().equals("I") == false) {
				// get shift schedule, to get the the max allowed
				// shift count and active client holding the slot
				ShiftSchedule shiftSchedule = (ShiftSchedule) new CommonMethods().getObjectById("ShiftSchedule", em,
						ShiftSchedule.class, shiftSlot.getShiftScheduleId());
				if (shiftSchedule != null) {

					maintainShiftSlotStatus(shiftSchedule, shiftSlot);
				}
			}
			// transaction cleanup
			em.merge(shiftSlot);

		}

		return 0;
	}

	/**
	 * Maintain shift slot status.
	 *
	 * @param shiftSchedule
	 *            the shift schedule
	 * @param shiftSlots
	 *            the shift slots
	 */
	private void maintainShiftSlotStatus(ShiftSchedule shiftSchedule, ShiftSlots shiftSlots) {

		int maxOrderAllowedinslot = shiftSchedule.getMaxOrderAllowed();
		int currentOrderInSlot = shiftSlots.getCurrentOrderInSlot();
		int currentClientCountHoldingTheSlot = shiftSlots.getCurrentlyHoldedClient();
		if (maxOrderAllowedinslot <= (currentClientCountHoldingTheSlot + currentOrderInSlot)) {
			// do not change the status if its in
			// deleted or inactive state
			if (shiftSlots.getStatus().equals("D") == false && shiftSlots.getStatus().equals("I") == false) {
				shiftSlots.setStatus("H");
			}
		} else {
			// make this slot active
			// do not change the status if its in
			// deleted or inactive state
			if (shiftSlots.getStatus().equals("D") == false && shiftSlots.getStatus().equals("I") == false) {
				shiftSlots.setStatus("A");
			}

		}

	}

	/**
	 * Gets the order by id and location id.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param locationId
	 *            the location id
	 * @return the order by id and location id
	 */
	public OrderHeader getOrderByIdAndlocationId(EntityManager em, String id, String locationId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> r = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderHeader_.id), id)));
		OrderHeader orderHeader = query.getSingleResult();
		OrderHeader result = new OrderHeader(id, orderHeader.getAddressBilling(), orderHeader.getAddressShipping(),
				orderHeader.getAmountPaid(), orderHeader.getBalanceDue(), orderHeader.getCreated(),
				orderHeader.getCreatedBy(), orderHeader.getDiscountsId(), orderHeader.getDiscountsName(),
				orderHeader.getDiscountsTypeId(), orderHeader.getDiscountsTypeName(), orderHeader.getDiscountsValue(),
				orderHeader.getGratuity(), orderHeader.getIpAddress(), orderHeader.getOrderSourceId(),
				orderHeader.getOrderStatusId(), orderHeader.getPointOfServiceCount(), orderHeader.getPriceDiscount(),
				orderHeader.getPriceExtended(), orderHeader.getPriceGratuity(), orderHeader.getPriceTax1(),
				orderHeader.getPriceTax2(), orderHeader.getPriceTax3(), orderHeader.getPriceTax4(),
				orderHeader.getReservationsId(), orderHeader.getServiceTax(), orderHeader.getSubTotal(),
				orderHeader.getTotal(), orderHeader.getSplitCount(), orderHeader.getUpdated(),
				orderHeader.getUpdatedBy(), orderHeader.getUsersId(), orderHeader.getLocationsId(),
				orderHeader.getPaymentWaysId(), orderHeader.getDate(), orderHeader.getVerificationCode(),
				orderHeader.getQrcode(), orderHeader.getSessionKey(), orderHeader.getFirstName(),
				orderHeader.getLastName(), orderHeader.getServerId(), orderHeader.getCashierId(),
				orderHeader.getVoidReasonId(), orderHeader.getMergedLocationsId(), orderHeader.getOpenTime(),
				orderHeader.getCloseTime(), orderHeader.getDiscountDisplayName(), orderHeader.getTaxDisplayName1(),
				orderHeader.getTaxDisplayName2(), orderHeader.getTaxDisplayName3(), orderHeader.getTaxDisplayName4(),
				orderHeader.getTaxRate1(), orderHeader.getTaxRate2(), orderHeader.getTaxRate3(),
				orderHeader.getTaxRate4(), orderHeader.getTotalTax(), orderHeader.getTaxName1(),
				orderHeader.getTaxName2(), orderHeader.getTaxName3(), orderHeader.getTaxName4(),
				orderHeader.getIsGratuityApplied(), orderHeader.getRoundOffTotal(), orderHeader.getIsTabOrder(),
				orderHeader.getServername(), orderHeader.getCashierName(), orderHeader.getVoidReasonName(),
				orderHeader.getIsOrderReopened(), orderHeader.getTaxExemptId(), orderHeader.getScheduleDateTime(),
				orderHeader.getReferenceNumber(), orderHeader.getNirvanaXpBatchNumber(), orderHeader.getOrderNumber(),
				orderHeader.getIsSeatWiseOrder(), orderHeader.getCalculatedDiscountValue(),
				orderHeader.getShiftSlotId(), orderHeader.getPriceDiscountItemLevel(), orderHeader.getMergeOrderId(),
				orderHeader.getCompanyName(), orderHeader.getTaxNo(), orderHeader.getTaxDisplayName(),
				orderHeader.getDeliveryCharges(), orderHeader.getDeliveryTax(), orderHeader.getDeliveryOptionId(),
				orderHeader.getServiceCharges(), orderHeader.getPreassignedServerId(), orderHeader.getLocalTime(),
				orderHeader.getPoRefrenceNumber(), orderHeader.getRequestedLocationId(), orderHeader.getOrderTypeId(),
				orderHeader.getDriverId(), orderHeader.getComment(), orderHeader.getStartDate(),
				orderHeader.getEndDate(), orderHeader.getEventName());

		result.setOrderDetailItems(getCustomerOrderDetailsItemForOrderId(em, id, locationId));
		result.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, id));
		result.setOrderHeaderToSeatDetails(getOrderHeaderToSeatDetailForOrderId(em, id));
		return result;
	}

	/**
	 * Gets the sales tax by name and location id.
	 *
	 * @param em
	 *            the em
	 * @param name
	 *            the name
	 * @param locationId
	 *            the location id
	 * @return the sales tax by name and location id
	 */
	private SalesTax getSalesTaxByNameAndlocationId(EntityManager em, String name, String locationId) {
		try {
			String queryString = "select s from SalesTax s where s.taxName =? and s.locationsId=?  ";
			TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, name)
					.setParameter(2, locationId);
			return query.getSingleResult();
		} catch (NoResultException e) {

			// todo shlok need
			// handle proper exception
			logger.severe(e);

		}
		return null;
	}

	/**
	 * Gets the all order payment details by payment type ids.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @param paymentTypeId
	 *            the payment type id
	 * @param sessionId
	 *            the session id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the all order payment details by payment type ids
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public List<OrderHeader> getAllOrderPaymentDetailsByPaymentTypeIds(HttpServletRequest httpRequest, EntityManager em,
			String userId, String locationId, int[] paymentTypeId, String sessionId, String startDate, String endDate)
			throws NirvanaXPException {

		int isOrderManagement = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = null;
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();

		String paymentTypeIds = "";
		String skipPaymentTypeIds = "";

		if (paymentTypeId != null && paymentTypeId.length > 0) {

			for (int j = 0; j < paymentTypeId.length; j++) {
				int payment = paymentTypeId[j];
				if (j == (paymentTypeId.length - 1)) {
					paymentTypeIds += payment;
				} else {
					paymentTypeIds += payment + ",";
				}
			}

		}
		OrderHeader header = new OrderHeader();
		List<String> batchDetail = getStartAndEndBatchDetail(startDate, endDate, locationId, em, httpRequest);

		if (batchDetail != null && batchDetail.size() == 2) {
			String startBatchTime = batchDetail.get(0);
			String endBatchTime = batchDetail.get(1);
			String query = "SELECT count(*) FROM `users_to_roles` utr left join roles "
					+ "r on r.id=utr.roles_id where utr.users_id = ? and r.role_name in ('Order Management')";

			Object result = em.createNativeQuery(query).setParameter(1, userId).getSingleResult();

			if (result != null) {
				// if this has primary key not 0
				if (((BigInteger) result).intValue() > 0) {
					isOrderManagement = 1;
				}
			} else {
				logger.severe(httpRequest, "Could not get count of Order Management roles");
			}

			if (1 == 1) {

				String query2 = "SELECT id FROM `payment_type` where id not in (" + paymentTypeIds + ") ";
				List<Integer> resultList2 = em.createNativeQuery(query2).getResultList();

				if (resultList2 != null && resultList2.size() > 0) {

					for (int j = 0; j < resultList2.size(); j++) {
						int payment = resultList2.get(j);
						if (j == (resultList2.size() - 1)) {
							skipPaymentTypeIds += payment;
						} else {
							skipPaymentTypeIds += payment + ",";
						}
					}

				}

				if (result != null) {
					// if this has primary key not 0
					if (((BigInteger) result).intValue() > 0) {
						isOrderManagement = 1;
					}
				}

				// order source production excluded from list
				// manager
				String queryString = "SELECT  " + objectsWithColumnStr
						+ "  FROM order_header oh left join order_payment_details opd on oh.id=opd.order_header_id "
						+ " join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id "
						+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
						+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
						+ " left join payment_method pm on opd.payment_method_id= pm.id "
						+ " join payment_method_type pmt on pm.payment_method_type_id=pmt.id  "
						+ "	join payment_type pt  	on pmt.payment_type_id=pt.id  "
						+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
						+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
						+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
						+ " left JOIN order_source osou on oh.order_source_id = osou.id "
						+ " left JOIN order_status os on oh.order_status_id = os.id  left JOIN order_source_group osg on os.order_source_group_id = osg.id "
						+ " WHERE  ( l.locations_id  in (select location0_.id as id1 from locations location0_     "
						+ " where location0_.locations_id=?) or oh.locations_id=?) " + "  "
						+ " and os.name not in ('Void Order ','Cancel Order')"
						+ " and osou.name != 'Production' and oh.sub_total != '0.00'"
						+ " and (((oh.schedule_date_time between ?  and ? )  "
						+ " or ((oh.id in (Select order_header_id from order_payment_details   "
						+ " where created between ? and ?) and oh.id not in (Select opd_opd.order_header_id from order_payment_details opd_opd  "
						+ " join payment_method pm_pm on opd_opd.payment_method_id= pm_pm.id "
						+ " join payment_method_type pmt_pmt on pm_pm.payment_method_type_id=pmt_pmt.id   "
						+ " join payment_type pt_pt 	on pmt_pmt.payment_type_id=pt_pt.id   "
						+ " where  pt_pt.id not in (" + skipPaymentTypeIds
						+ ")  and opd_opd.created between ? and ?) )  )) )  " + " and pt.id in (" + paymentTypeIds
						+ ")  order by oh.id,opd.id asc ";

				@SuppressWarnings("unchecked")
				List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId)
						.setParameter(2, locationId).setParameter(3, startBatchTime).setParameter(4, endBatchTime)
						.setParameter(5, startBatchTime).setParameter(6, endBatchTime).setParameter(7, startBatchTime)
						.setParameter(8, endBatchTime).getResultList();
				int index = 0;
				orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
						index);
			} else {
				throw new NirvanaXPException(
						new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDER_MANAGEMENT_ROLE_EXCEPTION,
								MessageConstants.ERROR_MESSAGE_ORDER_MANAGEMENT_ROLE,
								MessageConstants.ERROR_MESSAGE_ORDER_MANAGEMENT_ROLE));
			}
		}
		return orderHeaders;

	}

	/**
	 * Gets the start and end batch detail.
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @param locationId
	 *            the location id
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @return the start and end batch detail
	 */
	private List<String> getStartAndEndBatchDetail(String startDate, String endDate, String locationId,
			EntityManager em, HttpServletRequest httpRequest) {
		BatchDetail startBatch = null;
		BatchDetail endBatch = null;
		List<String> details = null;
		TimezoneTime timezoneTime = new TimezoneTime();

		String pickStartDate = timezoneTime.getDateAccordingToGMT(startDate + " 00:00:00", locationId, em);
		String pickEndDate = timezoneTime.getDateAccordingToGMT(endDate + " 23:59:59", locationId, em);

		List<String> batchId = null;
		try {
			batchId = getActiveBatchForPickDate(httpRequest, em, locationId, pickStartDate, pickEndDate);
		} catch (Exception e1) {
			logger.severe(httpRequest, "no active batch detail found for locationId: " + locationId);
		}

		if (batchId != null && batchId.size() > 0) {

			String startBatchId = batchId.get(0);
			String endBatchId = batchId.get(batchId.size() - 1);

			startBatch = em.find(BatchDetail.class, startBatchId);
			endBatch = em.find(BatchDetail.class, endBatchId);

		}

		// todo shlok need
		// remove unwanted variable
		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;

		if (batchId != null && batchId.size() > 0) {

			String gmtEndTime = null;
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
			// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			if (endBatch.getCloseTime() == 0) {
				gmtEndTime = date2.format(new Date()) + " 23:59:59";

			} else {
				gmtEndTime = dateFormatGmt.format(new Date(endBatch.getCloseTime()));
			}

			String gmtStartTime = dateFormatGmt.format(new Date(startBatch.getStartTime()));
			if (gmtStartTime != null && gmtEndTime != null) {
				details = new ArrayList<String>();
				details.add(gmtStartTime);
				details.add(gmtEndTime);
				return details;
			}

		}
		return null;
	}

	/**
	 * Delete order header.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderId
	 *            the order id
	 * @return true, if successful
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public boolean deleteOrderHeader(HttpServletRequest httpRequest, EntityManager em, String orderId)
			throws NirvanaXPException {
		try {
			return deleteOrder(em, orderId);
		} catch (Exception e) {
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}
		return false;
	}

	/**
	 * Adds the not printed order detail items to printer.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	public List<NotPrintedOrderDetailItemsToPrinter> addNotPrintedOrderDetailItemsToPrinter(
			List<NotPrintedOrderDetailItemsToPrinter> rStatus, HttpServletRequest httpRequest, EntityManager em,
			String parentlocationId) throws Exception {
		for (NotPrintedOrderDetailItemsToPrinter o : rStatus) {
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			o = em.merge(o);
		}

		return rStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.services.jaxrs.ILookupService#update(com.nirvanaxp.types
	 * .entities.NotPrintedOrderDetailItemsToPrinter)
	 */
	/**
	 * Update not printed order detail items to printer.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	public List<NotPrintedOrderDetailItemsToPrinter> updateNotPrintedOrderDetailItemsToPrinter(
			List<NotPrintedOrderDetailItemsToPrinter> rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {

		for (NotPrintedOrderDetailItemsToPrinter o : rStatus) {
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			o = em.merge(o);
		}

		return rStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.services.jaxrs.ILookupService#delete(com.nirvanaxp.types
	 * .entities.NotPrintedOrderDetailItemsToPrinter)
	 */
	/**
	 * Delete not printed order detail items to printer.
	 *
	 * @param rStatus
	 *            the r status
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */

	public List<NotPrintedOrderDetailItemsToPrinter> deleteNotPrintedOrderDetailItemsToPrinter(
			List<NotPrintedOrderDetailItemsToPrinter> rStatus, HttpServletRequest httpRequest, EntityManager em)
			throws Exception {
		for (NotPrintedOrderDetailItemsToPrinter o : rStatus) {
			NotPrintedOrderDetailItemsToPrinter u = em.find(NotPrintedOrderDetailItemsToPrinter.class, o.getId());
			u.setStatus("D");
			u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			o = em.merge(u);
		}

		return rStatus;
	}

	/**
	 * Insert into KDS to order detail item status.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param header
	 *            the header
	 * @throws Exception
	 */
	public void insertIntoKDSToOrderDetailItemStatus(HttpServletRequest httpRequest, EntityManager em,
			OrderHeader header) throws Exception {
		List<OrderDetailItem> orderDetailItemList = header.getOrderDetailItems();
		if (orderDetailItemList != null && orderDetailItemList.size() != 0) {
			for (OrderDetailItem orderDetailItem : orderDetailItemList) {
				if (orderDetailItem.getDeviceToKDSIds() == null) {
					Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
							orderDetailItem.getItemsId());
					if (item != null) {
						List<ItemsToPrinter> itemsToPrinterList = getItemsToPrinterList(em, item.getId(),
								header.getOrderTypeId());
						String KDSIds = "";
						if (itemsToPrinterList != null && itemsToPrinterList.size() != 0) {
							for (ItemsToPrinter itemsToPrinter : itemsToPrinterList) {
								if (KDSIds.length() == 0) {
									KDSIds += "" + itemsToPrinter.getPrintersId();
								} else {
									KDSIds += "," + itemsToPrinter.getPrintersId();
								}

							}
						}
						orderDetailItem.setDeviceToKDSIds(KDSIds);
					}

				}

				if (orderDetailItem.getDeviceToKDSIds() != null
						&& orderDetailItem.getDeviceToKDSIds().trim().length() > 0) {
					String kdsIds[] = orderDetailItem.getDeviceToKDSIds().split(",");
					if (kdsIds != null && kdsIds.length > 0) {
						for (String idString : kdsIds) {
							if (idString != null && idString.trim().length() > 0) {
								String kdsId = idString.trim();

								Printer printer = getPrinterById(kdsId, em);
								if (printer != null) {
									PrintersModel printerModel = em.find(PrintersModel.class,
											printer.getPrintersModelId());
									if (printerModel != null && (printerModel.getModelNumber()
											.equalsIgnoreCase("NXP-KDS")
											|| printerModel.getModelNumber().equalsIgnoreCase("NXP-Prod-KDS"))) {
										KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus = new KDSToOrderDetailItemStatus();
										kdsToOrderDetailItemStatus.setOrderDetailItemId(orderDetailItem.getId());
										kdsToOrderDetailItemStatus.setPrinterId(kdsId);
										KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus2 = getkdsToODIStatusByOrderDetailIdAndPrinterId(
												em, kdsToOrderDetailItemStatus.getOrderDetailItemId(),
												kdsToOrderDetailItemStatus.getPrinterId());
										if (kdsToOrderDetailItemStatus2 == null) {
											kdsToOrderDetailItemStatus.setLocalTime(orderDetailItem.getLocalTime());
											kdsToOrderDetailItemStatus.setCreated(orderDetailItem.getCreated());
											kdsToOrderDetailItemStatus.setCreatedBy(orderDetailItem.getCreatedBy());
											kdsToOrderDetailItemStatus.setUpdated(orderDetailItem.getUpdated());
											kdsToOrderDetailItemStatus.setUpdatedBy(orderDetailItem.getUpdatedBy());
											kdsToOrderDetailItemStatus.setStatus("A");
											kdsToOrderDetailItemStatus
													.setStatusId(orderDetailItem.getOrderDetailStatusId());
											logger.severe(
													"kdsToOrderDetailItemStatus================================================================="
															+ kdsToOrderDetailItemStatus);

											kdsToOrderDetailItemStatus = em.merge(kdsToOrderDetailItemStatus);

										} else {
											// kdsToOrderDetailItemStatus2.setId(kdsToOrderDetailItemStatus2.getId());
											kdsToOrderDetailItemStatus2.setUpdated(orderDetailItem.getUpdated());
											kdsToOrderDetailItemStatus2.setUpdatedBy(orderDetailItem.getUpdatedBy());

											kdsToOrderDetailItemStatus2 = em.merge(kdsToOrderDetailItemStatus2);

										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the kds to ODI status by order detail id and printer id.
	 *
	 * @param em
	 *            the em
	 * @param orderDetailItemId
	 *            the order detail item id
	 * @param printerId
	 *            the printer id
	 * @return the kds to ODI status by order detail id and printer id
	 */
	public KDSToOrderDetailItemStatus getkdsToODIStatusByOrderDetailIdAndPrinterId(EntityManager em,
			String orderDetailItemId, String printerId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<KDSToOrderDetailItemStatus> criteria = builder.createQuery(KDSToOrderDetailItemStatus.class);
			Root<KDSToOrderDetailItemStatus> ic = criteria.from(KDSToOrderDetailItemStatus.class);
			TypedQuery<KDSToOrderDetailItemStatus> query = em.createQuery(criteria.select(ic).where(
					builder.equal(ic.get(KDSToOrderDetailItemStatus_.orderDetailItemId), orderDetailItemId),
					builder.equal(ic.get(KDSToOrderDetailItemStatus_.printerId), printerId)));
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Gets the items to printer list.
	 *
	 * @param em
	 *            the em
	 * @param itemId
	 *            the item id
	 * @return the items to printer list
	 */
	private List<ItemsToPrinter> getItemsToPrinterList(EntityManager em, String itemId, int orderTypeId) {
		List<ItemsToPrinter> itemsToPrinterList = new ArrayList<ItemsToPrinter>();

		String queryString = "";
		if (orderTypeId == 2) {
			queryString = "select ip.* from items_to_printers ip join printers p on p.id = ip.printers_id "
					+ "left join printers_model pm on p.printers_model_id = pm.id "
					+ "where ip.items_id = ? and pm.model_number = 'NXP-Prod-KDS' and ip.status!='D' ";
		} else {
			queryString = "select ip.* from items_to_printers ip join printers p on p.id = ip.printers_id "
					+ "left join printers_model pm on p.printers_model_id = pm.id "
					+ "where ip.items_id = ? and pm.model_number != 'NXP-Prod-KDS'  and ip.status!='D' ";
		}

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, itemId).getResultList();
		if (resultList != null) {
			for (Object[] objRow : resultList) {

				if ((int) objRow[0] != 0) {

					ItemsToPrinter itemToPrinter = new ItemsToPrinter();
					itemToPrinter.setId((Integer) objRow[0]);
					itemToPrinter.setItemsId((String) objRow[1]);
					itemToPrinter.setPrintersId((String) objRow[2]);
					itemsToPrinterList.add(itemToPrinter);
				}

			}
		}

		return itemsToPrinterList;
		// CriteriaBuilder builder = em.getCriteriaBuilder();
		// CriteriaQuery<ItemsToPrinter> criteria =
		// builder.createQuery(ItemsToPrinter.class);
		// Root<ItemsToPrinter> r = criteria.from(ItemsToPrinter.class);
		//
		// TypedQuery<ItemsToPrinter> query =
		// em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsToPrinter_.itemsId),
		// itemId), builder.notEqual(r.get(ItemsToPrinter_.status), "D")));
		//
		// return query.getResultList();

	}

	/**
	 * Gets the all order header by created.
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
	 * @return the all order header by created
	 */
	public List<OrderHeaderWithUser> getAllOrderHeaderByCreated(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate) {

		List<OrderHeaderWithUser> orderHeaders = new ArrayList<OrderHeaderWithUser>();
		// get First and last batch

		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;
		try {

			String queryString = "SELECT  oh.id    FROM order_header oh "
					+ "WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
					+ "and ( (oh.created between ?  and ? ))    order by oh.id asc ";

			@SuppressWarnings("unchecked")
			List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
					.setParameter(2, startDate).setParameter(3, endDate).getResultList();

			orderHeaders = populateOrderHeaderObject(orderHeaders, resultList, em);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}
		return orderHeaders;
	}

	/**
	 * Gets the all order header by last updated.
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
	 * @return the all order header by last updated
	 */
	public List<OrderHeaderWithUser> getAllOrderHeaderByLastUpdated(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate) {

		List<OrderHeaderWithUser> orderHeaders = new ArrayList<OrderHeaderWithUser>();
		// get First and last batch

		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;
		try {

			String queryString = "SELECT  oh.id    FROM order_header oh "
					+ "WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
					+ "and ( (oh.updated between ?  and ? ))    order by oh.id asc ";

			@SuppressWarnings("unchecked")
			List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
					.setParameter(2, startDate).setParameter(3, endDate).getResultList();

			orderHeaders = populateOrderHeaderObject(orderHeaders, resultList, em);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}
		return orderHeaders;
	}

	/**
	 * Gets the batch for start end date.
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
	 * @return the batch for start end date
	 * @throws NirvanaXPException
	 */
	public List<BatchDetail> getBatchForStartEndDate(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate) throws NirvanaXPException {

		String queryString = "select b from BatchDetail b where b.locationId= ? and b.startTime between ? and ?  order by b.id asc ";
		List<BatchDetail> resultSet = null;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = null;
		Date endd = null;
		try {
			start = dateFormat.parse(startDate);
			endd = dateFormat.parse(endDate);

			Query query = em.createQuery(queryString).setParameter(1, locationId).setParameter(2, start).setParameter(3,
					endd);
			resultSet = query.getResultList();

			if (resultSet == null || resultSet.size() == 0) {
				// reply
				// query will work in case of batch started few days prior of
				// current date
				// and not yet closed and we are passing current date for
				// current active batch
				queryString = "select bd from BatchDetail bd where bd.locationId= ?  and bd.startTime< ?  and (bd.closeTime > ? or bd.closeTime is null) order by bd.id asc";
				// this query will get batch before the start date-time, that
				// has not been closed or has been closed at a time later than
				// expected end time

				query = em.createQuery(queryString).setParameter(1, locationId).setParameter(2, start).setParameter(3,
						endd);
				resultSet = query.getResultList();
			}
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not find batch detail id for location " + locationId, "between start",
					startDate, "and end", endDate);
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(
					MessageConstants.ERROR_CODE_BATCH_ID_NOT_PRESENT_WITH_DATE__EXCEPTION,
					MessageConstants.ERROR_MESSAGE_BATCH_ID_NOT_PRESENT_WITH_DATE_DISPLAY_MESSAGE,
					MessageConstants.ERROR_MESSAGE_BATCH_ID_NOT_PRESENT_WITH_DATE_DISPLAY_MESSAGE));

		}

		return resultSet;
	}

	public List<BatchDetail> getBatchForClockInClockOut(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate) {

		String queryString = "";
		List<BatchDetail> batchDetails = new ArrayList<BatchDetail>();
		queryString = " select id from batch_detail where id in "
				+ " (select * from (select id from batch_detail where starttime < '" + startDate + "' "
				+ " order by id desc limit 0,1) as temp ) "
				+ " or id in (select * from (select id from batch_detail  where  " + " IFNULL(closeTime, now()) > '"
				+ endDate + "' " + " order by id desc limit 0,1) as temp )  ";

		@SuppressWarnings("unchecked")
		List<Object> resultList = em.createNativeQuery(queryString).getResultList();
		if (resultList != null) {
			for (Object objRow : resultList) {
				BatchDetail detail = (BatchDetail) new CommonMethods().getObjectById("BatchDetail", em,
						BatchDetail.class, (String) objRow);
				batchDetails.add(detail);
			}
		}

		return batchDetails;
	}

	/**
	 * Gets the all order header batch wise.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param locationId
	 *            the location id
	 * @param batchId
	 *            the batch id
	 * @return the all order header batch wise
	 */
	public List<OrderHeaderWithUser> getAllOrderHeaderBatchWise(HttpServletRequest httpRequest, EntityManager em,
			String locationId, List<BatchDetail> batchId) {

		List<OrderHeaderWithUser> orderHeaders = new ArrayList<OrderHeaderWithUser>();
		// get First and last batch
		BatchDetail firstBatch = null;
		BatchDetail lastBatch = null;
		if (batchId != null && batchId.size() > 0) {
			firstBatch = batchId.get(0);
			lastBatch = batchId.get(batchId.size() - 1);
		}

		String batchIDs = "";
		for (int i = 0; i < batchId.size(); i++) {
			if (i == (batchId.size() - 1)) {
				batchIDs += "" + (batchId.get(i).getId());
			} else {
				batchIDs += (batchId.get(i).getId()) + ",";
			}

		}

		if (firstBatch != null && lastBatch != null) {
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;
			String utcTime = "";
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			if (lastBatch.getCloseTime() == 0) {
				utcTime = date.format(new Date()) + " 23:59:59";
			} else {
				utcTime = dateFormatGmt.format(new Date(lastBatch.getCloseTime()));
			}

			try {
				String gmtStartTime = dateFormatGmt.format(new Date(firstBatch.getStartTime()));
				String queryString = "SELECT  oh.id    " + "FROM order_header oh "
						+ " WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
						+ "and ((oh.schedule_date_time between ?  and ? )  " + " or oh.nirvanaxp_batch_number in ("
						+ batchIDs + "))    " + "order by oh.id asc ";

				@SuppressWarnings("unchecked")
				List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
						.setParameter(2, gmtStartTime).setParameter(3, utcTime).getResultList();

				orderHeaders = populateOrderHeaderObject(orderHeaders, resultList, em);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// todo shlok need
				// handle proper exception
				logger.severe(e);
			}
		}

		return orderHeaders;
	}

	/**
	 * Gets the all order header by open time.
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
	 * @return the all order header by open time
	 */
	public List<OrderHeaderWithUser> getAllOrderHeaderByOpenTime(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate) {
		List<OrderHeaderWithUser> orderHeaders = new ArrayList<OrderHeaderWithUser>();
		// get First and last batch

		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;
		try {

			String queryString = "SELECT  oh.id    FROM order_header oh "
					+ "WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
					+ "and ( (oh.open_time between ?  and ? ))    order by oh.id asc ";

			@SuppressWarnings("unchecked")
			List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
					.setParameter(2, startDate).setParameter(3, endDate).getResultList();

			orderHeaders = populateOrderHeaderObject(orderHeaders, resultList, em);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}
		return orderHeaders;
	}

	/**
	 * Gets the all order header by close time.
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
	 * @return the all order header by close time
	 */
	public List<OrderHeaderWithUser> getAllOrderHeaderByCloseTime(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate) {
		List<OrderHeaderWithUser> orderHeaders = new ArrayList<OrderHeaderWithUser>();
		// get First and last batch

		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;
		try {

			String queryString = "SELECT  oh.id    FROM order_header oh "
					+ " WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
					+ " and ( (oh.close_time between ?  and ? ))    order by oh.id asc ";

			@SuppressWarnings("unchecked")
			List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
					.setParameter(2, startDate).setParameter(3, endDate).getResultList();

			orderHeaders = populateOrderHeaderObject(orderHeaders, resultList, em);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// todo shlok need
			// handle proper exception
			logger.severe(e);
		}
		return orderHeaders;
	}

	/**
	 * Gets the batch for end time.
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
	 * @return the batch for end time
	 */
	public List<BatchDetail> getBatchForEndTime(HttpServletRequest httpRequest, EntityManager em, String locationId,
			String startDate, String endDate) {

		String queryString = "select b from BatchDetail b where b.locationId= ? and b.closeTime between ? and ?  order by b.id asc ";
		List<BatchDetail> resultSet = null;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = null;
		Date endd = null;
		try {
			start = dateFormat.parse(startDate);
			endd = dateFormat.parse(endDate);
		} catch (ParseException e1) {
			logger.severe(e1);
		}

		try {

			// todo shlok need
			// null pass to param start and endd

			Query query = em.createQuery(queryString).setParameter(1, locationId).setParameter(2, start).setParameter(3,
					endd);
			resultSet = query.getResultList();
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not find order with start date", startDate, "and end date", endDate);
		}

		return resultSet;
	}

	/**
	 * Gets the all order header by end batch time.
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
	 * @param batchId
	 *            the batch id
	 * @return the all order header by end batch time
	 */
	public List<OrderHeaderWithUser> getAllOrderHeaderByEndBatchTime(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate, List<BatchDetail> batchId) {

		List<OrderHeaderWithUser> orderHeaders = new ArrayList<OrderHeaderWithUser>();
		String batchIDs = "";
		for (int i = 0; i < batchId.size(); i++) {
			if (i == (batchId.size() - 1)) {
				batchIDs += "" + (batchId.get(i).getId());
			} else {
				batchIDs += (batchId.get(i).getId()) + ",";
			}

		}
		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;

		if (batchIDs.length() > 0) {
			try {

				String queryString = "SELECT oh.id FROM order_header oh WHERE oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? )"
						+ "and oh.nirvanaxp_batch_number in (" + batchIDs + ")" + "order by oh.id asc ";

				@SuppressWarnings("unchecked")
				List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
						.getResultList();

				orderHeaders = populateOrderHeaderObject(orderHeaders, resultList, em);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// todo shlok need
				// handle proper exception
				logger.severe(e);
			}
		}

		return orderHeaders;

	}

	/**
	 * Gets the all order header by start batch time.
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
	 * @param batchId
	 *            the batch id
	 * @return the all order header by start batch time
	 */
	public List<OrderHeaderWithUser> getAllOrderHeaderByStartBatchTime(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate, List<BatchDetail> batchId) {

		List<OrderHeaderWithUser> orderHeaders = new ArrayList<OrderHeaderWithUser>();
		String batchIDs = "";
		for (int i = 0; i < batchId.size(); i++) {
			if (i == (batchId.size() - 1)) {
				batchIDs += "" + (batchId.get(i).getId());
			} else {
				batchIDs += (batchId.get(i).getId()) + ",";
			}

		}
		Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class, locationId);
		;
		if (batchIDs.length() > 0) {
			try {

				String queryString = "SELECT  oh.id    FROM order_header oh "
						+ "WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? )"
						+ "and oh.nirvanaxp_batch_number in (" + batchIDs + ")    order by oh.id asc ";

				@SuppressWarnings("unchecked")
				List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
						.getResultList();

				orderHeaders = populateOrderHeaderObject(orderHeaders, resultList, em);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// todo shlok need
				// handle proper exception
				logger.severe(e);
			}
		}
		return orderHeaders;
	}

	/**
	 * Gets the batch for start time.
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
	 * @return the batch for start time
	 */
	public List<BatchDetail> getBatchForStartTime(HttpServletRequest httpRequest, EntityManager em, String locationId,
			String startDate, String endDate) {

		String queryString = "select b from BatchDetail b where b.locationId= ? and b.startTime between ? and ?  order by b.id asc ";
		List<BatchDetail> resultSet = null;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = null;
		Date endd = null;
		try {
			start = dateFormat.parse(startDate);
			endd = dateFormat.parse(endDate);
		} catch (ParseException e1) {
			logger.severe(e1);
		}

		try {
			// todo shlok need
			// check null of start and endd

			Query query = em.createQuery(queryString).setParameter(1, locationId).setParameter(2, start).setParameter(3,
					endd);
			resultSet = query.getResultList();
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not find order with start date", startDate, "and end date", endDate);
		}

		return resultSet;
	}

	/**
	 * Populate order header object.
	 *
	 * @param orderHeaders
	 *            the order headers
	 * @param resultList
	 *            the result list
	 * @param em
	 *            the em
	 * @return the list
	 */
	private List<OrderHeaderWithUser> populateOrderHeaderObject(List<OrderHeaderWithUser> orderHeaders,
			List<Object> resultList, EntityManager em) {
		for (Object object : resultList) {
			OrderHeader header = getOrderById(em, (String) object);
			Discount discounts = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
					header.getDiscountsId());
			if (discounts != null) {
				header.setDiscountDisplayName(discounts.getDisplayName());
			}

			User user = (User) new CommonMethods().getObjectById("User", em, User.class, header.getUsersId());
			if (user != null) {
				user.setPassword(null);
				user.setAuthPin(null);
			}
			// setting orderSouceGroupName
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
					OrderSource.class, header.getOrderSourceId());
			if (orderSource != null) {
				OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById(
						"OrderSourceGroup", em, OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
				header.setOrderSourceGroupName(orderSourceGroup.getName());
			}
			// setting paymentMethodTypeName
			List<OrderPaymentDetail> list = new ArrayList<OrderPaymentDetail>();

			for (OrderPaymentDetail detail : header.getOrderPaymentDetails()) {
				if (detail.getPaymentMethod() != null) {
					PaymentMethodType paymentMethodType = (PaymentMethodType) new CommonMethods().getObjectById(
							"PaymentMethodType", em, PaymentMethodType.class,
							detail.getPaymentMethod().getPaymentMethodTypeId());
					detail.getPaymentMethod().setPaymentMethodTypeName(paymentMethodType.getName());
				}
				list.add(detail);
			}
			header.setOrderPaymentDetails(list);
			// setting orderDetailStatusName
			List<OrderDetailItem> listODI = new ArrayList<OrderDetailItem>();
			for (OrderDetailItem orderDetailItem : header.getOrderDetailItems()) {

				if (orderDetailItem.getDiscountId() != null) {
					Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em, Discount.class,
							orderDetailItem.getDiscountId());
					orderDetailItem.setShortDescriptionDiscount(discount.getShortDescription());
				}
				OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
						orderDetailItem.getOrderDetailStatusId());
				orderDetailItem.setOrderDetailStatusName(orderDetailStatus.getName());

				Reasons reasons = (Reasons) new CommonMethods().getObjectById("Reasons", em, Reasons.class,
						orderDetailItem.getDiscountReason());
				if (reasons != null) {
					orderDetailItem.setDiscountReasonName(reasons.getName());
					orderDetailItem.setDiscountReasonDisplayName(reasons.getDisplayName());
				}
				if (orderDetailItem.getDiscountReasonName() == null
						|| orderDetailItem.getDiscountReasonName().equalsIgnoreCase("<null>")
						|| orderDetailItem.getDiscountReasonName().equalsIgnoreCase("null")
						|| orderDetailItem.getDiscountReasonName().length() == 0) {
					orderDetailItem.setDiscountReasonName("0000");
				}
				if (orderDetailItem.getDiscountReasonName() != null
						&& orderDetailItem.getDiscountReasonName().equalsIgnoreCase("No Reasons")) {
					orderDetailItem.setDiscountReasonName("0000");
				}

				if (orderDetailItem.getDiscountReasonDisplayName() == null
						|| orderDetailItem.getDiscountReasonDisplayName().equalsIgnoreCase("<null>")
						|| orderDetailItem.getDiscountReasonDisplayName().equalsIgnoreCase("null")
						|| orderDetailItem.getDiscountReasonDisplayName().length() == 0) {
					orderDetailItem.setDiscountReasonDisplayName("0000");
				}
				if (orderDetailItem.getDiscountName() == null
						|| orderDetailItem.getDiscountName().equalsIgnoreCase("<null>")
						|| orderDetailItem.getDiscountName().equalsIgnoreCase("null")
						|| orderDetailItem.getDiscountName().length() == 0) {
					orderDetailItem.setDiscountName(null);
				}
				if (orderDetailItem.getDiscountName() == null
						|| orderDetailItem.getDiscountName().equalsIgnoreCase("<null>")
						|| orderDetailItem.getDiscountName().equalsIgnoreCase("null")
						|| orderDetailItem.getDiscountName().length() == 0) {
					orderDetailItem.setDiscountName(null);
				}
				BigDecimal itemQty = orderDetailItem.getItemsQty();
				List<OrderDetailAttribute> listOrderDetailAttribute = null;
				if (orderDetailItem.getOrderDetailAttributes() != null
						&& orderDetailItem.getOrderDetailAttributes().size() > 0) {
					listOrderDetailAttribute = new ArrayList<OrderDetailAttribute>();
					for (OrderDetailAttribute detailAttribute : orderDetailItem.getOrderDetailAttributes()) {
						detailAttribute.setAttributeQty(itemQty);
						listOrderDetailAttribute.add(detailAttribute);
					}
					orderDetailItem.setOrderDetailAttributes(listOrderDetailAttribute);
				}

				listODI.add(orderDetailItem);
			}
			header.setOrderDetailItems(listODI);
			if (header.getOrderPaymentDetails() != null && header.getOrderPaymentDetails().size() > 0) {
				orderHeaders.add(new OrderHeaderWithUser(header, user, false));
			}

		}
		return orderHeaders;
	}

	/**
	 * Update order batch.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderWithBatchDetailPacket
	 *            the order with batch detail packet
	 */
	public void updateOrderBatch(HttpServletRequest httpRequest, EntityManager em,
			OrderWithBatchDetailPacket orderWithBatchDetailPacket) {

		for (String orderId : orderWithBatchDetailPacket.getOrderIds()) {
			OrderHeader orderHeader = getOrderById(em, orderId);
			orderHeader.setNirvanaXpBatchNumber(orderWithBatchDetailPacket.getBatchId());
			orderHeader.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			orderHeader.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.merge(orderHeader);

		}
	}

	/**
	 * Update order payment batch.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPaymentWithBatchDetailPacket
	 *            the order payment with batch detail packet
	 */
	public void updateOrderPaymentBatch(HttpServletRequest httpRequest, EntityManager em,
			OrderPaymentWithBatchDetailPacket orderPaymentWithBatchDetailPacket) {

		for (String orderPaymentId : orderPaymentWithBatchDetailPacket.getOrderPaymentIds()) {
			OrderPaymentDetail orderPayment = (OrderPaymentDetail) new CommonMethods()
					.getObjectById("OrderPaymentDetail", em, OrderPaymentDetail.class, orderPaymentId);
			orderPayment.setNirvanaXpBatchNumber(orderPaymentWithBatchDetailPacket.getBatchId());
			if (orderPayment.getCreated() == null) {
				orderPayment.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}
			if (orderPayment.getUpdated() == null) {
				orderPayment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}

			em.merge(orderPayment);

		}
	}

	public InventoryPostPacket manageInventoryForKDSOrders(HttpServletRequest httpRequest,
			KDSToOrderDetailItemStatusPacket orderFromPacket, EntityManager em, String locationIdString)
			throws Exception {
		String locationId = locationIdString;
		ItemInventoryManagementHelper inventoryManagementHelper = new ItemInventoryManagementHelper();
		InventoryPostPacket inventoryPostPacket = new InventoryPostPacket();
		// Location rootLocation = (Location) new
		// CommonMethods().getObjectById("Location", em,Location.class,
		// locationId);;
		// 'Out Of Stock','Ordered Qty More Than Avail Qty','No
		// Inventory Available'
		OrderDetailStatus orderDetailStatusOutOfStock = new OrderDetailStatus("Out Of Stock");
		OrderDetailStatus orderDetailStatusOrderMoreThanAvail = new OrderDetailStatus(
				"Ordered Qty More Than Avail Qty");

		List<OrderDetailStatus> orderDetailStatusList = getOrderstatusForInventory(em, locationId);

		int index = orderDetailStatusList.indexOf(orderDetailStatusOutOfStock);
		orderDetailStatusOutOfStock = orderDetailStatusList.get(index);

		index = orderDetailStatusList.indexOf(orderDetailStatusOrderMoreThanAvail);
		orderDetailStatusOrderMoreThanAvail = orderDetailStatusList.get(index);

		if (orderFromPacket.getKdsToOrderDetailItemStatusList() != null) {

			for (KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus : orderFromPacket
					.getKdsToOrderDetailItemStatusList()) {
				String updatedBy = kdsToOrderDetailItemStatus.getUpdatedBy();
				OrderDetailItem orderDetailItem = (OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem",
						em, OrderDetailItem.class, kdsToOrderDetailItemStatus.getOrderDetailItemId());
				OrderDetailStatus orderDetailStatus = em.find(OrderDetailStatus.class,
						kdsToOrderDetailItemStatus.getStatusId());
				Reasons reasons = (Reasons) new CommonMethods().getObjectById("Reasons", em, Reasons.class,
						orderDetailItem.getRecallReason());
				if (reasons == null || !reasons.getInventoryConsumed().equals("1")) {
					try {
						if (orderDetailStatus.getName().equals("Item Ready")) {

							inventoryManagementHelper.manageItemInventoryForKDS(httpRequest, orderDetailItem, em,
									updatedBy, inventoryPostPacket, locationId, true);
						} else {
							inventoryManagementHelper.manageItemInventoryForKDS(httpRequest, orderDetailItem, em,
									updatedBy, inventoryPostPacket, locationId, false);
						}
						orderDetailItem.setIsInventoryHandled(1);
					} catch (NirvanaXPException e) {

						String exception = e.toString();
						if (exception != null && orderDetailStatusList != null) {
							if (exception.contains("ORD1013")) {

								orderDetailItem.setOrderDetailStatusId(orderDetailStatusOutOfStock.getId());
								setStatusToAttribute(orderDetailItem, orderDetailStatusOutOfStock);
							}

							else if (exception.contains("3:")) {
								orderDetailItem.setOrderDetailStatusId(orderDetailStatusOrderMoreThanAvail.getId());
								setStatusToAttribute(orderDetailItem, orderDetailStatusOrderMoreThanAvail);
							}
						}
					}
				}
				if (orderDetailItem.getId() != null) {
					orderDetailItem.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					orderDetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				} else {
					OrderDetailItem newOrderDetailItem = (OrderDetailItem) new CommonMethods()
							.getObjectById("OrderDetailItem", em, OrderDetailItem.class, orderDetailItem.getId());
					orderDetailItem.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
					orderDetailItem.setCreated(newOrderDetailItem.getCreated());
				}
				orderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (orderDetailItem.getOrderHeaderToSeatDetailId() == null) {
					orderDetailItem.setOrderHeaderToSeatDetailId(BigInteger.ZERO);
				}

				// add or update the order detail item

				OrderDetailItem orderDetailItem2 = em.merge(orderDetailItem);

				// add orderdetails item attribute also if sent by
				// client
				if (orderDetailItem.getOrderDetailAttributes() != null
						&& orderDetailItem.getOrderDetailAttributes().size() > 0) {

					for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
						orderDetailAttribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						orderDetailAttribute.setOrderDetailItemId(orderDetailItem2.getId());

						if (orderDetailAttribute.getId() != null) {
							// its trying to insert first time
							orderDetailAttribute
									.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							orderDetailAttribute.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							orderDetailAttribute.setId(new StoreForwardUtility().generateDynamicBigIntId(em, locationId,
									httpRequest, "order_detail_attribute"));

							orderDetailAttribute = em.merge(orderDetailAttribute);

						} else {
							// its trying to update the already
							// existing

							orderDetailAttribute
									.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							em.merge(orderDetailAttribute);
						}
					}
				}

				// add update NotPrintedOrderDetailItemsToPrinter
				if (orderDetailItem.getNotPrintedOrderDetailItemsToPrinter() != null) {
					for (NotPrintedOrderDetailItemsToPrinter o : orderDetailItem
							.getNotPrintedOrderDetailItemsToPrinter()) {
						o.setOrderDetailItemsId(orderDetailItem2.getId());
						if (o.getId() == 0) {
							o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
							o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							// o.setId(new
							// StoreForwardUtility().generateDynamicBigIntId(em,
							// locationId, httpRequest,
							// "not_printed_order_detail_items_to_printer"));

							o = em.merge(o);
						} else {
							NotPrintedOrderDetailItemsToPrinter temp = em
									.find(NotPrintedOrderDetailItemsToPrinter.class, o.getId());
							if (temp != null) {
								o.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
								o.setCreated(temp.getCreated());
							}
							o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							o = em.merge(o);
						}

					}
				}

			}

		}

		return inventoryPostPacket;
	}

	public OrderLocking getOrderLockingStatus(EntityManager em, String parentlocationId, String orderId,
			String userId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderLocking> criteria = builder.createQuery(OrderLocking.class);
		Root<OrderLocking> r = criteria.from(OrderLocking.class);
		TypedQuery<OrderLocking> query = em.createQuery(criteria.select(r)
				.where(new Predicate[] { builder.equal(r.get(OrderLocking_.orderId), orderId),
						builder.equal(r.get(OrderLocking_.userId), userId),
						builder.equal(r.get(OrderLocking_.status), "A"),
						builder.equal(r.get(OrderLocking_.orderId), orderId),
						builder.equal(r.get(OrderLocking_.locationId), parentlocationId) }));

		return query.getSingleResult();

	}

	public OrderLocking registerOrderLockingStatus(HttpServletRequest httpRequest, EntityManager em,
			String parentlocationId, String orderId, String userId) throws NirvanaXPException {

		OrderLocking locking = null;
		try {
			locking = getOrderLockingStatus(em, parentlocationId, orderId, userId);
		} catch (NoResultException e) {
			logger.severe(e);
		}
		if (locking == null) {
			locking = new OrderLocking();
			OrderHeader header = getOrderById(em, orderId);
			locking.setUserId(userId);
			locking.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			locking.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			locking.setCreatedBy(userId);
			locking.setSessionId(httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			locking.setUpdatedBy(userId);
			locking.setLocationId(parentlocationId);
			locking.setStatus("A");
			locking.setOrderNumber(header.getOrderNumber());
			locking.setOrderId(orderId);

			em.persist(locking);

		} else {
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDER_LOCK,
					MessageConstants.ERROR_MESSAGE_ORDER_LOCK, orderId + ""));
		}
		return locking;

	}

	public OrderLocking deRegisterOrderLocking(HttpServletRequest httpRequest, EntityManager em,
			String parentlocationId, String orderId, String userId) throws NirvanaXPException {

		OrderLocking locking = null;
		try {
			locking = getOrderLockingStatus(em, parentlocationId, orderId, userId);
		} catch (NoResultException e) {
			logger.severe(e);
		}
		if (locking != null) {

			locking.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			locking.setUpdatedBy(userId);
			locking.setLocationId(parentlocationId);
			locking.setStatus("D");

			locking = em.merge(locking);

		} else {
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDER_NOT_LOCK,
					MessageConstants.ERROR_MESSAGE_ORDER_NOT_LOCK, orderId + ""));
		}
		return locking;

	}

	private List<OrderSourceGroup> getOrderSourceGroupList(HttpServletRequest httpRequest, EntityManager em,
			String locationsId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
		Root<OrderSourceGroup> r = criteria.from(OrderSourceGroup.class);
		TypedQuery<OrderSourceGroup> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceGroup_.locationsId), locationsId),
						builder.notEqual(r.get(OrderSourceGroup_.avgWaitTime), 0)));
		List<OrderSourceGroup> result = null;
		try {
			result = query.getResultList();
		} catch (Exception e) {

			logger.severe(httpRequest, "Could not find OrderSourceGroup with businessId : " + locationsId);
		}
		return result;
	}

	public List<OrderHeader> getAllOrderPaymentDetailsByUserIdLocationBatchWise(HttpServletRequest httpRequest,
			EntityManager em, String userId, String locationId, String sessionId, BatchDetail batchDetail) {

		// int isManager = 0;// not manager
		List<OrderPaymentDetail> orderPaymentDetails = null;
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();
		// TimezoneTime time = new TimezoneTime();
		OrderHeader header = new OrderHeader();

		if (batchDetail != null) {
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;
			String utcTime = "";
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			if (batchDetail.getCloseTime() == 0) {
				utcTime = date.format(new Date()) + " 23:59:59";

			} else {
				utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
			}

			String gmtStartTime = dateFormatGmt.format(new Date(batchDetail.getStartTime()));

			String queryString = "SELECT  " + objectsWithColumnStr
					+ "  FROM order_header oh join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id   "
					+ " left join order_payment_details opd on oh.id=opd.order_header_id "
					+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
					+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
					+ " left join payment_method pm on opd.payment_method_id= pm.id "
					+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ " left join transaction_status ts on ts.id=opd.transaction_status_id left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ " JOIN order_status os on oh.order_status_id = os.id "
					+ " left JOIN order_source osou on oh.order_source_id = osou.id "
					+ "left JOIN order_source_group osg on os.order_source_group_id = osg.id "
					+ "  WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
					+ " and osou.name != 'Production' and os.name not in ('Void Order ','Cancel Order ')   and ((oh.schedule_date_time between ?  and ? ))  "
					+ " or oh.id in (Select order_header_id from order_payment_details where nirvanaxp_batch_number = ? )  "
					+ "   order by oh.id,opd.id asc ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
					.setParameter(2, gmtStartTime).setParameter(3, utcTime).setParameter(4, batchDetail.getId())
					.getResultList();

			int index = 0;
			orderHeaders = populateOrderHeaderForPayment(em, resultList, orderHeaders, header, orderPaymentDetails,
					index);

		}

		return orderHeaders;
	}

	private boolean checkTransactionPrecapturedStatus(String authCode, EntityManager em, String orderId) {
		// checking whether record is precaptured or not
		String queryString5 = "select opd.id from order_payment_details opd join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
				+ "  where opd.auth_code=? and ptt.name='Force' and opd.order_header_id=? ";
		int resultList = 0;
		try {
			resultList = (Integer) em.createNativeQuery(queryString5).setParameter(1, authCode).setParameter(2, orderId)
					.getSingleResult();
			if (resultList > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.severe(e);
		}

		return false;
	}

	public void tipPoolCalculation(BatchDetail batchDetail, String userId, HttpServletRequest httpRequest,
			String locationId, EntityManager em) throws Exception, InvalidSessionException, IOException {

		try {
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);

			List<OrderHeader> headers = getAllOrderPaymentDetailsByUserIdLocationBatchWise(httpRequest, em, userId,
					locationId, null, batchDetail);

			if (locationSetting.isTipPoolingNeeded()) {
				tipPoolingCalculation(httpRequest, em, userId, locationId, batchDetail, headers);

			}

		} catch (Exception e) {
			logger.severe(e);
			// reply:if it get exception for one batch it has to continue for
			// next batch
		}

	}

	public List<BatchDetail> getSettledBatchForStartEndDate(HttpServletRequest httpRequest, EntityManager em,
			String locationId, String startDate, String endDate) throws ParseException {

		String queryString = "select b from BatchDetail b where b.status='C' and b.locationId= ? and b.startTime between ? and ?  order by b.id asc ";
		List<BatchDetail> resultSet = null;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = dateFormat.parse(startDate);
		Date endd = dateFormat.parse(endDate);

		try {

			Query query = em.createQuery(queryString).setParameter(1, locationId).setParameter(2, start).setParameter(3,
					endd);
			resultSet = query.getResultList();
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not find order with start date", startDate, "and end date", endDate);
		}

		if (resultSet == null || resultSet.size() == 0) {

			try {

				queryString = "select bd from BatchDetail bd where b.status='C' and bd.locationId= ?  and bd.startTime< ?  and (bd.closeTime > ? or bd.closeTime is null) order by bd.id asc";
				Query query = em.createQuery(queryString).setParameter(1, locationId).setParameter(2, start)
						.setParameter(3, endd);
				resultSet = query.getResultList();
			} catch (Exception e) {
				logger.severe(httpRequest, e, "Could not find batch detail id for location " + locationId,
						"between start", startDate, "and end", endDate);
			}
		}
		return resultSet;
	}

	public void deleteTipPoolCalculation(BatchDetail batchDetail, HttpServletRequest httpRequest, EntityManager em)
			throws Exception, InvalidSessionException, IOException {
		logger.severe(
				"deleteTipPoolCalculation=======================================================================");

		String queryString1 = "delete from tip_distribution where nirvanaxp_batch_id=?";
		em.createNativeQuery(queryString1, BatchDetail.class).setParameter(1, batchDetail.getId()).executeUpdate();

		String queryString2 = "delete from tip_pooling_by_order where nirvanaxp_batch_id = ?";
		em.createNativeQuery(queryString2, BatchDetail.class).setParameter(1, batchDetail.getId()).executeUpdate();

		String queryString3 = "delete from tip_pooling_by_pool where nirvanaxp_batch_id =?";
		em.createNativeQuery(queryString3, BatchDetail.class).setParameter(1, batchDetail.getId()).executeUpdate();

		String queryString4 = "delete from employee_operational_hours where nirvanaxp_batch_id=?";
		em.createNativeQuery(queryString4, BatchDetail.class).setParameter(1, batchDetail.getId()).executeUpdate();

		String queryString5 = "UPDATE batch_detail SET is_tip_calculated='N' WHERE id=?";
		em.createNativeQuery(queryString5, BatchDetail.class).setParameter(1, batchDetail.getId()).executeUpdate();

	}

	public List<OrderPaymentDetail> getAllOrderPaymentDetailsByUserIdLocation(HttpServletRequest httpRequest,
			EntityManager em, String userId, String locationId, BatchDetail batchDetail,
			OperationalShiftSchedule shiftSchedule) {

		List<OrderPaymentDetail> orderPaymentDetails = new ArrayList<OrderPaymentDetail>();

		if (batchDetail != null) {
			Location location = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					locationId);
			;
			String utcTime = "";
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			if (batchDetail.getCloseTime() == 0) {
				utcTime = date.format(new Date()) + " 23:59:59";

			} else {
				utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
			}
			Date batchDate = new Date(batchDetail.getStartTime());
			String batchStart = date.format(batchDate);
			String shiftStartTime = batchStart + " " + shiftSchedule.getFromTime();
			String shiftEndTime = batchStart + " " + shiftSchedule.getToTime();
			String gmtStartTime = dateFormatGmt.format(new Date(batchDetail.getStartTime()));

			String queryString = "SELECT opd.id "

					+ "   FROM order_header oh join locations l on oh.locations_id = l.id left join timezone t on l.timezone_id = t.id   "
					+ " left join order_payment_details opd on oh.id=opd.order_header_id "
					+ " LEFT JOIN address asi on oh.address_shipping_id = asi.id "
					+ " LEFT JOIN address ab on oh.address_billing_id = ab.id "
					+ " left join payment_method pm on opd.payment_method_id= pm.id "
					+ " left join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id "
					+ " left join transaction_status ts on ts.id=opd.transaction_status_id "
					+ " left JOIN order_source osou on oh.order_source_id = osou.id "
					+ " left join paymentgateway_type pgt on pgt.id= ts.paymentgateway_type_id "
					+ " JOIN order_status os on oh.order_status_id = os.id "
					+ " left JOIN order_source_group osg on os.order_source_group_id = osg.id "
					+ "  WHERE  oh.locations_id IN (SELECT id  FROM locations WHERE business_id = ? ) "
					+ " and os.name not in ('Void Order ','Cancel Order ')" + " and osou.name != 'Production'  and (  "
					+ "  oh.id in (Select order_header_id from order_payment_details where nirvanaxp_batch_number = ?  ))  "
					+ " and oh.open_time between ? and ?  and oh.preassigned_server_id=? order by oh.id,opd.id asc ";

			@SuppressWarnings("unchecked")
			List<Object> resultList = em.createNativeQuery(queryString).setParameter(1, location.getBusinessId())
					.setParameter(2, batchDetail.getId()).setParameter(3, shiftStartTime).setParameter(4, shiftEndTime)
					.setParameter(5, userId).getResultList();

			for (Object objRow : resultList) {
				OrderPaymentDetail detail = (OrderPaymentDetail) new CommonMethods().getObjectById("OrderPaymentDetail",
						em, OrderPaymentDetail.class, (String) objRow);
				orderPaymentDetails.add(detail);
			}
		}
		return orderPaymentDetails;
	}

	public BigDecimal calculateCashAmountTotal(HttpServletRequest httpRequest, EntityManager em, String userId,
			String locationId, BatchDetail batchDetail, OperationalShiftSchedule shiftSchedule, String scectionId,
			String jobRoleId, String orderSourceGroupId) {

		try {

			if (batchDetail != null) {
				String utcTime = "";
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
				// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				if (batchDetail.getCloseTime() == 0) {
					utcTime = date.format(new Date()) + " 23:59:59";

				} else {
					utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
				}

				Date batchDate = new Date(batchDetail.getStartTime());
				String batchStart = date.format(batchDate);
				String pickStartDate = batchStart + " " + shiftSchedule.getFromTime();
				String pickEndDate = batchStart + " " + shiftSchedule.getToTime();
				TimezoneTime timezoneTime = new TimezoneTime();
				String shiftStartTime = timezoneTime.getDateAccordingToGMT(pickStartDate, locationId, em);
				String shiftEndTime = timezoneTime.getDateAccordingToGMT(pickEndDate, locationId, em);

				String queryString = " select (sum(opd.amount_paid)-(sum(opd.price_tax_1)+sum(opd.price_tax_2)+sum(opd.price_tax_3)+sum(opd.price_tax_4)+sum(opd.price_gratuity)) ) as calculatedWithTax        "
						+ " from order_payment_details opd  "
						+ " left join order_header oh on oh.id=opd.order_header_id    "
						+ "   join payment_method pm on pm.id=opd.payment_method_id   "
						+ "   join payment_method_type pmt on pmt.id=pm.payment_method_type_id   "
						+ "   join payment_type pt on pt.id =pmt.payment_type_id  "
						+ "   join payment_transaction_type ptt on ptt.id =opd.payment_transaction_type_id  "
						+ "   WHERE oh.preassigned_server_id IN (" + userId + ") AND opd.nirvanaxp_batch_number="
						+ batchDetail.getId() + "   and oh.open_time between '" + shiftStartTime + "'  and '"
						+ shiftEndTime + "' and pt.name='Cash' and opd.is_refunded=0 and  ptt.name not in ('Refund')  "
						+ " and oh.id in (select id from order_header "
						+ "where  getJobRoleId( open_time, preassigned_server_id) = " + jobRoleId
						+ " and order_source_id in (select id from order_source where order_source_group_id in ("
						+ orderSourceGroupId
						+ " ) ) and locations_id in ( select id from locations where locations_id in ( " + scectionId
						+ " )  or id= " + scectionId + "  ) ) ";

				Object result = em.createNativeQuery(queryString).getSingleResult();

				return (BigDecimal) result;
			}

		} catch (Exception e) {
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculateCardAmountTotal(HttpServletRequest httpRequest, EntityManager em, String userId,
			String locationId, BatchDetail batchDetail, OperationalShiftSchedule shiftSchedule, String scectionId,
			String jobRoleId, String orderSourceGroupId) {

		try {

			if (batchDetail != null) {
				String utcTime = "";
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
				// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				if (batchDetail.getCloseTime() == 0) {
					utcTime = date.format(new Date()) + " 23:59:59";

				} else {
					utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
				}
				Date batchDate = new Date(batchDetail.getStartTime());
				String batchStart = date.format(batchDate);
				String pickStartDate = batchStart + " " + shiftSchedule.getFromTime();
				String pickEndDate = batchStart + " " + shiftSchedule.getToTime();
				TimezoneTime timezoneTime = new TimezoneTime();
				String shiftStartTime = timezoneTime.getDateAccordingToGMT(pickStartDate, locationId, em);
				String shiftEndTime = timezoneTime.getDateAccordingToGMT(pickEndDate, locationId, em);

				String queryString = " select (sum(opd.amount_paid)-(sum(opd.price_tax_1)+sum(opd.price_tax_2)+sum(opd.price_tax_3)+sum(opd.price_tax_4)+sum(opd.price_gratuity)) ) as calculatedWithTax        "
						+ " from order_payment_details opd  "
						+ " left join order_header oh on oh.id=opd.order_header_id  "
						+ " join payment_transaction_type ptt on ptt.id=opd.payment_transaction_type_id  "
						+ "   join payment_method pm on pm.id=opd.payment_method_id   "
						+ "   join payment_method_type pmt on pmt.id=pm.payment_method_type_id   "
						+ "   join payment_type pt on pt.id =pmt.payment_type_id  "
						+ "   WHERE oh.preassigned_server_id IN (" + userId + ") AND opd.nirvanaxp_batch_number="
						+ batchDetail.getId() + " and  " + "  oh.open_time between '" + shiftStartTime + "'  and '"
						+ shiftEndTime
						+ "' and ptt.name='Auth' and  pt.name in ('credit card','Manual Credit Card') and opd.is_refunded=0  and ptt.name not in ('Refund')"
						+ " and oh.id in (select id from order_header where  getJobRoleId( open_time, preassigned_server_id) = "
						+ jobRoleId
						+ " and order_source_id in (select id from order_source where order_source_group_id in ("
						+ orderSourceGroupId
						+ " )) and locations_id in ( select id from locations where locations_id in ( " + scectionId
						+ " )  or id= " + scectionId + "   ) ) ";

				Object result = em.createNativeQuery(queryString).getSingleResult();

				return (BigDecimal) result;
			}

		} catch (Exception e) {
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculateCreditTermAmountTotal(HttpServletRequest httpRequest, EntityManager em, String userId,
			String locationId, BatchDetail batchDetail, OperationalShiftSchedule shiftSchedule, String sectionId,
			String jobRoleId, String orderSourceGroupId) {

		try {

			if (batchDetail != null) {
				String utcTime = "";
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
				// dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				if (batchDetail.getCloseTime() == 0) {
					utcTime = date.format(new Date()) + " 23:59:59";

				} else {
					utcTime = dateFormatGmt.format(new Date(batchDetail.getCloseTime()));
				}
				Date batchDate = new Date(batchDetail.getStartTime());
				String batchStart = date.format(batchDate);
				String pickStartDate = batchStart + " " + shiftSchedule.getFromTime();
				String pickEndDate = batchStart + " " + shiftSchedule.getToTime();
				TimezoneTime timezoneTime = new TimezoneTime();
				String shiftStartTime = timezoneTime.getDateAccordingToGMT(pickStartDate, locationId, em);
				String shiftEndTime = timezoneTime.getDateAccordingToGMT(pickEndDate, locationId, em);

				String queryString = " select (sum(opd.amount_paid)-(sum(opd.price_tax_1)+sum(opd.price_tax_2)+sum(opd.price_tax_3)+sum(opd.price_tax_4)+sum(opd.price_gratuity)) ) as calculatedWithTax        "
						+ " from order_payment_details opd  "
						+ " left join order_header oh on oh.id=opd.order_header_id    "
						+ "   join payment_method pm on pm.id=opd.payment_method_id   "
						+ "   join payment_method_type pmt on pmt.id=pm.payment_method_type_id   "
						+ "   join payment_type pt on pt.id =pmt.payment_type_id  "
						+ "   join payment_transaction_type ptt on ptt.id =opd.payment_transaction_type_id  "
						+ "   WHERE oh.preassigned_server_id IN (" + userId + ") AND opd.nirvanaxp_batch_number="
						+ batchDetail.getId() + " and  " + "  oh.open_time between '" + shiftStartTime + "'  and '"
						+ shiftEndTime
						+ "' and pt.name in ('Credit Term') and opd.is_refunded=0 and ptt.name not in ('Refund')"
						+ " and  oh.id in (select id from order_header where getJobRoleId( open_time, preassigned_server_id) = "
						+ jobRoleId
						+ " and order_source_id in (select id from order_source where order_source_group_id in ("
						+ orderSourceGroupId
						+ " )) and locations_id in ( select id from locations where locations_id in ( " + sectionId
						+ " )  or id= " + sectionId + "  ) ) ";

				Object result = em.createNativeQuery(queryString).getSingleResult();

				return (BigDecimal) result;
			}

		} catch (Exception e) {
			// todo shlok need
			// handel proper exception
			logger.severe(e);
		}
		return new BigDecimal(0);
	}

	public String getJobRoleIdFromOpenTime(EntityManager em, String userId, long openTime) {
		try {
			Date d = new Date(openTime);
			String dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
			String queryString = "select cico.job_role_id from  clock_in_clock_out cico  "
					+ " join order_header oh on oh.preassigned_server_id=cico.users_id  "
					+ " join employee_operations eo on eo.id=cico.clock_in_operation_id " + " where  (cico.clock_in ='"
					+ dateFormat + "' or cico.clock_in < '" + dateFormat + "') and cico.users_id= " + userId
					+ " and cico.status != 'D' and eo.operation_name ='Clock In' order by cico.id desc limit 0,1";

			Object query = em.createNativeQuery(queryString).getSingleResult();

			return (String) query;
		} catch (Exception e) {
			e.printStackTrace();
			// need to continue even with no job role assign to user
		}
		return null;
	}

	public int getClockedInEmployees(EntityManager em, long batchStartTime, long batchEndTime) {
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date batchStartDate = new Date(batchStartTime);
		Date batchEndDate = new Date(batchEndTime);
		String batchStart = date.format(batchStartDate);
		String batchEnd = date.format(batchEndDate);
		String queryString = "SELECT count(id) FROM clock_in_clock_out where " + " clock_in between '" + batchStart
				+ "' and '" + batchEnd + "' " + " and clock_out_operation_id=0 and status !='D'";
		Object query = em.createNativeQuery(queryString).getSingleResult();
		return ((BigInteger) query).intValue();

	}

	public void sendEODSettledmentMail(HttpServletRequest httpRequest, EntityManager em, String userId,
			String locationId, String sessionId, BatchDetail activeBatchDetail, List<OrderHeader> headers, String email)
			throws Exception {

		String emailBody = "";
		// EntityTransaction tx = null;

		if (headers != null && headers.size() > 0) {

			// get user flow

			// making email body and Send Email to user
			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();

			try {
				Location foundLocation = null;
				if (locationId != null) {
					String queryString = "select l from Location l where " + "l.id ='" + locationId
							+ "' and l.locationsId = '0' and l.locationsTypeId = '1' ";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				emailBody = receiptPDFFormat.sendEODSettledmentMailString(em, httpRequest, headers, foundLocation,
						userId, activeBatchDetail).toString();

				try {
					if (emailBody != null) {
						EmailTemplateKeys.sendEODSettledmentMailToUser(httpRequest, em, locationId,
								EmailTemplateKeys.EOD_SETTLEDMENT_CONFIRMATION, emailBody, email);
					}

				} catch (Exception e) {
					// todo shlok need
					// modularise method
					logger.severe(e);
				}

				// tx.commit();
			} catch (RuntimeException e) {
				// todo shlok need
				// modularise method
				logger.severe(e);
			}

		} else {
			logger.severe("No Order Result Found for EOD Send Email");
		}

	}

	public void unassignedServerFromLocation(HttpServletRequest httpRequest, EntityManager em, int businessId)
			throws Exception {
		// find all preassigned location for business
		List<LocationsToShiftPreAssignServer> assignServers = getAllLocationsToShiftPreAssignServer(httpRequest, em,
				businessId);
		// clear and insert into history for flush
		for (LocationsToShiftPreAssignServer preAssignServer : assignServers) {
			preAssignServer.setServerName("");
			preAssignServer.setUserId(null);
			preAssignServer.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			preAssignServer.setAutoUnassigned(true);
			preAssignServer = em.merge(preAssignServer);
			// inserting into history
			new LocationsToShiftPreAssignServerHistory().createLocationsToShiftPreAssignServerHistory(em,
					preAssignServer);
		}
	}

	public List<LocationsToShiftPreAssignServer> getAllLocationsToShiftPreAssignServer(HttpServletRequest httpRequest,
			EntityManager em, int businessId) throws Exception {

		try {
			String queryString = "select l from LocationsToShiftPreAssignServer l where (l.userId != '0' and l.userId is not null) and l.locationsId in (select id from Location where businessId = ?) ";
			TypedQuery<LocationsToShiftPreAssignServer> query = em
					.createQuery(queryString, LocationsToShiftPreAssignServer.class).setParameter(1, businessId);
			return query.getResultList();
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;

	}

	public void sendPacketForBroadcast(String operation, PostPacket postPacket, HttpServletRequest httpRequest) {
		try {
			MessageSender messageSender = new MessageSender();
			operation = ServiceOperationsUtility.getOperationName(operation);
			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.LookupService.name(),
					operation, null, postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
					postPacket.getSessionId());
		}

		catch (Exception e) {
			logger.severe(httpRequest, e, "Could not send broadcast of post packet in LookupService operation",
					operation);
		}
	}

	public OrderHeader checkTransfer(HttpServletRequest httpRequest, EntityManager em, OrderHeader order,
			String parentlocationId, OrderTransferPacket orderTransferPacket) throws NirvanaXPException {
		try {
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,
					OrderStatus.class, order.getOrderStatusId());
			OrderHeader orderFromDatabase = getOrderById(em, order.getId());
			OrderHeader orderHeader = getOrderById(em, order.getId());
			List<OrderDetailItem> orderDetailItemsList = orderHeader.getOrderDetailItems();
			handleOriginOrder(httpRequest, em, orderFromDatabase, parentlocationId, orderStatus);

			if (orderStatus != null && !orderStatus.getName().equals("Ready to Order")) {
				if (em.getTransaction().isActive()) {
					em.getTransaction().commit();
					em.getTransaction().begin();
				} else {
					em.getTransaction().begin();
				}

				OrderHeader newOrderHeader = updateOrderLocationWise(em, orderHeader, httpRequest, orderTransferPacket);

				em.getTransaction().commit();
				// em.getTransaction().begin();
				OrderHeader header = replaceOrderDetailItemLocationWise(em, orderDetailItemsList, httpRequest,
						newOrderHeader, orderTransferPacket);
				// em.getTransaction().commit();

				return header;
			} else {
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(
						MessageConstants.ERROR_CODE_ORDER_LOCATION_CANNOT_UPDATE_ORDER_STATUS_EXCEPTION,
						MessageConstants.ERROR_MESSAGE_ORDER_LOCATION_CANNOT_UPDATE_ORDER_STATUS_READY_DISPLAY_MESSAGE,
						null));

			}
		} catch (RuntimeException e) {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		}
	}

	public OrderHeader handleOriginOrder(HttpServletRequest httpRequest, EntityManager em, OrderHeader order,
			String parentlocationId, OrderStatus orderStatus) throws NirvanaXPException {
		try {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
				em.getTransaction().begin();
			} else {
				em.getTransaction().begin();
			}
			order.setOrderDetailItems(null);
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
					OrderSource.class, order.getOrderSourceId());
			OrderStatus transferOrderStatus = new CommonMethods()
					.getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "Transfer Order",
							orderSource.getLocationsId(), orderSource.getOrderSourceGroupId());
			order.setOrderStatusId(transferOrderStatus.getId());
			order.setBalanceDue(new BigDecimal(0));
			order.setTotal(new BigDecimal(0));
			order.setAmountPaid(new BigDecimal(0));
			order.setCalculatedDiscountValue(new BigDecimal(0));
			order.setRoundOffTotal(new BigDecimal(0));
			order.setTotalTax(new BigDecimal(0));
			order.setSubTotal(new BigDecimal(0));
			order.setPriceGratuity(new BigDecimal(0));
			order.setPriceDiscount(new BigDecimal(0));
			order.setDiscountsId(null);
			order.setDiscountDisplayName(null);
			order.setDiscountsName(null);
			order.setDiscountsTypeId(null);
			order.setDiscountsTypeName(null);
			order.setDiscountsValue(new BigDecimal(0));
			order.setPriceExtended(new BigDecimal(0));
			order.setPriceTax1(new BigDecimal(0));
			order.setPriceTax2(new BigDecimal(0));
			order.setPriceTax3(new BigDecimal(0));
			order.setPriceTax4(new BigDecimal(0));
			order.setTaxDisplayName1(null);
			order.setTaxDisplayName2(null);
			order.setTaxDisplayName3(null);
			order.setTaxDisplayName4(null);
			order.setTaxName1(null);
			order.setTaxName2(null);
			order.setTaxName3(null);
			order.setTaxName4(null);
			order.setTaxRate1(new BigDecimal(0));
			order.setTaxRate2(new BigDecimal(0));
			order.setTaxRate3(new BigDecimal(0));
			order.setTaxRate4(new BigDecimal(0));

			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order, em);
			em.merge(order);
			em.getTransaction().commit();
			OrderStatus readyToOrderStatus = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(
					em, "Ready To Order", orderSource.getLocationsId(), orderSource.getOrderSourceGroupId());
			order.setOrderStatusId(readyToOrderStatus.getId());
			em.getTransaction().begin();
			if (!orderStatus.getName().equals("Reopen")) {
				updateUserVisitCount(em, order.getUsersId());
			}

			new InsertIntoHistory().insertOrderIntoHistory(httpRequest, order, em);
			order = em.merge(order);
			em.getTransaction().commit();
			return order;
		} catch (RuntimeException e) {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		}

	}

	private OrderHeader updateOrderLocationWise(EntityManager em, OrderHeader order, HttpServletRequest httpRequest,
			OrderTransferPacket orderTransferPacket) {

		try {
			BigDecimal tempSubTotal = new BigDecimal(0);
			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,
					OrderSource.class, order.getOrderSourceId());
			OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup",
					em, OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
			OrderSourceGroup destinationOrderSourceGroup = getOrderSourceGroupByNameAndlocationId(em,
					orderSourceGroup.getName(), orderTransferPacket.getDestinationLocationId());
			OrderSource destinationOrderSource = getOrderSourceByNameAndlocationId(em, orderSource.getName(),
					orderTransferPacket.getDestinationLocationId(), destinationOrderSourceGroup.getId());
			OrderStatus destinationOrderStatus = getOrderStatusByNameAndlocationIdAndSourceId(em, "Order Created",
					orderTransferPacket.getDestinationLocationId(), destinationOrderSource.getOrderSourceGroupId());
			order.setOrderDetailItems(null);
			order.setTransferredOrderId(order.getId());
			order.setId(null);
			order.setLocationsId(orderTransferPacket.getDestinationLocationId());
			order.setOrderSourceId(destinationOrderSource.getId());
			order.setOrderStatusId(destinationOrderStatus.getId());
			order.setReservationsId(null);
			order.setSubTotal(tempSubTotal);
			BatchDetail batchDetail = null;
			try {
				batchDetail = getActiveBatch(httpRequest, em, orderTransferPacket.getDestinationLocationId(), null,
						false, null, order.getUpdatedBy());
			} catch (Exception e1) {
				logger.severe(httpRequest, e1, "no active batch detail found for locationId: "
						+ orderTransferPacket.getDestinationLocationId());
			}
			if (batchDetail != null) {
				order.setNirvanaXpBatchNumber(
						orderTransferPacket.getDestinationLocationId() + "-" + batchDetail.getId());
			}

			Location l = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
					orderTransferPacket.getDestinationLocationId());

			// adding order dequence

			PaymentBatchManager batchManager = PaymentBatchManager.getInstance();

			Location baseLocation = new CommonMethods().getBaseLocation(em);

			if (baseLocation.getIsOrderNumberSequencing() == 0) {
				String id = new StoreForwardUtility().generateUUID();
				order.setOrderNumber(
						new StoreForwardUtility().generateOrderNumberNew(orderTransferPacket.getDestinationLocationId(),
								em, order.getNirvanaXpBatchNumber(), "order_header"));
				order.setId(id);
			} else {
				order.setOrderNumber(
						new StoreForwardUtility().generateOrderNumberNew(orderTransferPacket.getDestinationLocationId(),
								em, order.getNirvanaXpBatchNumber(), "order_header"));
			}
			order.setSessionKey(orderTransferPacket.getIdOfSessionUsedByPacket());

			order = em.merge(order);

			return order;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	private OrderHeader replaceOrderDetailItemLocationWise(EntityManager em, List<OrderDetailItem> orderDetailItemsList,
			HttpServletRequest httpRequest, OrderHeader order, OrderTransferPacket orderTransferPacket) {

		try {
			List<OrderDetailItem> newOrderDetailItemsList = new ArrayList<OrderDetailItem>();
			BigDecimal tempSubTotal = new BigDecimal(0);
			List<ItemPacket> itemPacketList = new ArrayList<ItemPacket>();
			em.getTransaction().begin();
			for (OrderDetailItem orderDetailItem : orderDetailItemsList) {
				Item itemAdapter = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						orderDetailItem.getItemsId());
				Item itemFromDestinationLocation = getItemByGlobalItemIdAndLocation(em, itemAdapter.getGlobalItemId(),
						orderTransferPacket.getDestinationLocationId());
				if (itemFromDestinationLocation == null) {
					Location baseLocation = new CommonMethods().getBaseLocation(em);
					ItemPacket itemPacket = new ItemsServiceBean().createLocalItemFromGlobal(em,
							orderTransferPacket.getDestinationLocationId(), baseLocation, httpRequest,
							itemAdapter.getGlobalItemId());
					itemPacket.setMerchantId(orderTransferPacket.getMerchantId());
					itemPacket.setEchoString(orderTransferPacket.getEchoString());
					itemPacketList.add(itemPacket);
					// sendPacketForBroadcast(POSNServiceOperations.ItemsService_add.name(),
					// itemPacket,httpRequest );
					itemFromDestinationLocation = itemPacket.getItem();
				}
				if (itemFromDestinationLocation != null) {
					OrderDetailStatus destinationOrderDetailStatus = getOrderDetailStatusByName(em,
							orderTransferPacket.getDestinationLocationId(), "Item saved");
					orderDetailItem.setItemsId(itemFromDestinationLocation.getId());
					orderDetailItem.setItemGroupId(itemFromDestinationLocation.getItemGroupId());
					orderDetailItem.setItemsShortName(itemFromDestinationLocation.getShortName());
					orderDetailItem.setOrderDetailStatusId(destinationOrderDetailStatus.getId());
					orderDetailItem.setOrderDetailStatusName(destinationOrderDetailStatus.getName());
					orderDetailItem.setPriceSelling(itemFromDestinationLocation.getPriceSelling());
					orderDetailItem.setSentCourseId(itemFromDestinationLocation.getCourseId());
					orderDetailItem.setIsInventoryHandled(0);
					orderDetailItem.setDiscountReason(null);
					orderDetailItem.setDiscountValue(0);
					orderDetailItem.setDiscountId(null);
					orderDetailItem.setPriceDiscount(new BigDecimal(0));
					BigDecimal amount = itemFromDestinationLocation.getPriceSelling();
					amount = amount.multiply(orderDetailItem.getItemsQty());
					tempSubTotal = tempSubTotal.add(amount);
					orderDetailItem.setSubTotal(amount);

					orderDetailItem.setParentCategoryId(null);
					orderDetailItem.setIsInventoryHandled(0);

					List<Printer> printerArray = new PrinterUtility().getKDSListForIntraTransferByItemId(em,
							itemFromDestinationLocation.getId());
					String array = "";
					if (printerArray != null && printerArray.size() > 0) {
						for (Printer printer : printerArray) {
							if (array.length() != 0) {
								array = array + "," + printer.getId();
							} else {
								array = array + printer.getId();
							}

						}
					}
					orderDetailItem.setDeviceToKDSIds(array);
					orderDetailItem.setInventoryAccrual(0);
					List<OrderDetailAttribute> orderDetailAttributes = new ArrayList<OrderDetailAttribute>();
					for (OrderDetailAttribute orderDetailAttribute : orderDetailItem.getOrderDetailAttributes()) {
						ItemsAttribute attributeAdapter = (ItemsAttribute) new CommonMethods().getObjectById(
								"ItemsAttribute", em, ItemsAttribute.class, orderDetailAttribute.getItemsAttributeId());

						ItemsAttribute attributeFromDestinationLocation = getItemAttributeByGlobalItemAttributeIdAndLocation(
								em, attributeAdapter.getGlobalId(), orderTransferPacket.getDestinationLocationId());
						if (attributeFromDestinationLocation != null) {
							orderDetailAttribute.setItemsId(itemFromDestinationLocation.getId());
							orderDetailAttribute.setItemsAttributeId(attributeFromDestinationLocation.getId());
							orderDetailAttribute.setItemsAttributeName(attributeFromDestinationLocation.getName());
							orderDetailAttribute.setOrderDetailStatusId(destinationOrderDetailStatus.getId());
							orderDetailAttribute.setOrderDetailStatusName(destinationOrderDetailStatus.getName());
							orderDetailAttribute.setPriceSelling(attributeFromDestinationLocation.getSellingPrice());
							orderDetailAttributes.add(orderDetailAttribute);
						}
					}
					orderDetailItem.setOrderDetailAttributes(orderDetailAttributes);
					orderDetailItem.setOrderHeaderId(order.getId());
					newOrderDetailItemsList.add(orderDetailItem);

				}
			}

			order.setOrderDetailItems(newOrderDetailItemsList);
			order = new OrderHeaderCalculation().getOrderHeaderCalculation(em, order, null);

			order = em.merge(order);
			em.getTransaction().commit();
			// OrderHeader orderFromDatabase = getOrderById(em, order.getId());
			// orderFromDatabase = new
			// OrderHeaderCalculation().getOrderHeaderCalculation(em,
			// orderFromDatabase);

			// insert this entry into database too
			// order.setOrderDetailItems(null);
			// new InsertIntoHistory().insertOrderIntoHistory(httpRequest,
			// order, em);
			if (itemPacketList != null && itemPacketList.size() > 0) {
				for (ItemPacket itemPacket : itemPacketList) {
					sendPacketForBroadcast(POSNServiceOperations.ItemsService_add.name(), itemPacket, httpRequest);
				}
			}

			return order;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public Item getItemByGlobalItemIdAndLocation(EntityManager em, String globalItemId, String locationId)
			throws Exception {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
			Root<Item> r = criteria.from(Item.class);
			TypedQuery<Item> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(Item_.globalItemId), globalItemId), builder.notEqual(r.get(Item_.status), "D"),
					builder.equal(r.get(Item_.locationsId), locationId)));
			return query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public ItemsAttribute getItemAttributeByGlobalItemAttributeIdAndLocation(EntityManager em,
			String globalItemAttributeId, String locationId) throws Exception {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ItemsAttribute> criteria = builder.createQuery(ItemsAttribute.class);
			Root<ItemsAttribute> r = criteria.from(ItemsAttribute.class);
			TypedQuery<ItemsAttribute> query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(ItemsAttribute_.globalId), globalItemAttributeId),
							builder.notEqual(r.get(ItemsAttribute_.status), "D"),
							builder.equal(r.get(ItemsAttribute_.locationsId), locationId)));
			return query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public OrderSource getOrderSourceByNameAndlocationId(EntityManager em, String name, String locationId,
			String orderSourceGroupId) throws Exception {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> r = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(OrderSource_.name), name), builder.notEqual(r.get(OrderSource_.status), "D"),
					builder.equal(r.get(OrderSource_.locationsId), locationId),
					builder.equal(r.get(OrderSource_.orderSourceGroupId), orderSourceGroupId)));
			return query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public OrderStatus getOrderStatusByNameAndlocationIdAndSourceId(EntityManager em, String name, String locationId,
			String orderSourceGroupId) throws Exception {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(OrderStatus_.name), name), builder.notEqual(r.get(OrderStatus_.status), "D"),
					builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceGroupId)));
			return query.getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public OrderSourceGroup getOrderSourceGroupByNameAndlocationId(EntityManager em, String name, String locationId)
			throws Exception {

		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> r = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceGroup_.name), name),
							builder.notEqual(r.get(OrderSourceGroup_.status), "D"),
							builder.equal(r.get(OrderSourceGroup_.locationsId), locationId)));
			return query.getSingleResult();
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	public DriverOrder updateDriverOrderStatus(EntityManager em, String orderId, OrderStatus orderStatus,
			HttpServletRequest httpRequest) throws Exception {
		if (orderStatus != null && orderStatus.getName().equals("Ready to Order")) {
			OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup",
					em, OrderSourceGroup.class, orderStatus.getOrderSourceGroupId());
			if (orderSourceGroup != null && (orderSourceGroup.getName().equals("Pick Up")
					|| orderSourceGroup.getName().equals("Delivery"))) {
				EntityManager emGlobal = null;
				try {
					emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
					CriteriaBuilder builder = emGlobal.getCriteriaBuilder();
					CriteriaQuery<DriverOrder> criteria = builder.createQuery(DriverOrder.class);
					Root<DriverOrder> r = criteria.from(DriverOrder.class);
					TypedQuery<DriverOrder> query = emGlobal
							.createQuery(criteria.select(r).where(builder.equal(r.get(DriverOrder_.orderId), orderId)));

					DriverOrder order = query.getSingleResult();
					order.setStatusId(orderStatus.getId());
					order.setStatusName(orderStatus.getDisplayName());
					emGlobal.getTransaction().begin();
					order = emGlobal.merge(order);
					emGlobal.getTransaction().commit();
					return order;
				} catch (Exception e) {
					logger.severe(e);
				} finally {
					GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
				}
			}
		}

		return null;
	}

	public String checkInventoryForOrder(HttpServletRequest httpRequest, OrderHeader order, EntityManager em,
			String locationId) throws NirvanaXPException {
		String rootlocationId = locationId;
		InventoryPostPacket inventoryPostPacket = new InventoryPostPacket();
		if (order.getOrderDetailItems() != null) {
			// boolean isAdvanceOrder = isAdvanceOrder(order, em,
			// rootlocationId);

			ItemInventoryManagementHelper inventoryManagementHelper = new ItemInventoryManagementHelper();
			List errorMessage = new ArrayList<String>();
			for (OrderDetailItem orderDetailItem : order.getOrderDetailItems()) {
				Item item = (Item) new CommonMethods().getObjectById("Item", em, Item.class,
						orderDetailItem.getItemsId());
				int isRealTimeInventoryOn = item.getIsRealTimeUpdateNeeded();
				if (orderDetailItem.getInventoryAccrual() == 0 && orderDetailItem.getIsInventoryHandled() == 0) {
					if (isRealTimeInventoryOn == 1) {
						String needToReturn = inventoryManagementHelper.getItemInventoryForOrder(httpRequest,
								orderDetailItem, em, isRealTimeInventoryOn, order.getUpdatedBy(), inventoryPostPacket,
								rootlocationId);
						logger.severe(
								"needToReturn======================================================" + needToReturn);

						if (needToReturn != null && !needToReturn.equalsIgnoreCase("null")) {
							errorMessage.add(needToReturn);
						}

					}
				}

			}
			if (errorMessage != null && errorMessage.size() > 0) {
				String message = "";
				for (int i = 0; i < errorMessage.size(); i++) {

					String data = (String) errorMessage.get(i);
					if (i == (errorMessage.size() - 1)) {
						if (data != null && !data.equalsIgnoreCase("null")) {
							message += data;
						}
					} else {
						if (data != null && !data.equalsIgnoreCase("null")) {
							message += data + ",";
						}
					}
				}
				return message;
			}
		}
		return null;
	}

	private UsersToDiscount getUsersToDiscount(HttpServletRequest httpRequest, OrderHeader orderHeader,
			EntityManager em) throws NirvanaXPException {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToDiscount> criteria = builder.createQuery(UsersToDiscount.class);
			Root<UsersToDiscount> r = criteria.from(UsersToDiscount.class);

			// Remove discountCode condition for task #46369
			TypedQuery<UsersToDiscount> query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(UsersToDiscount_.usersId), orderHeader.getUsersId()),
							builder.equal(r.get(UsersToDiscount_.locationId), orderHeader.getLocationsId()),
							builder.equal(r.get(UsersToDiscount_.discountId), orderHeader.getDiscountsId())));

			UsersToDiscount usersToDiscount = query.getSingleResult();
			return usersToDiscount;
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	public Printer getPrinterById(String id, EntityManager em) throws Exception {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Printer> criteria = builder.createQuery(Printer.class);
			Root<Printer> r = criteria.from(Printer.class);
			TypedQuery<Printer> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Printer_.id), id)));
			Printer p = query.getSingleResult();
			String queryString = "select l from Location l where l.id in  (select p.locationsId from Printer p where p.globalPrinterId=? and p.status not in ('D')) ";
			TypedQuery<Location> query2 = em.createQuery(queryString, Location.class).setParameter(1, p.getId());

			List<Location> resultSet = query2.getResultList();
			p.setLocationList(resultSet);

			return p;
		} catch (Exception e) {

			logger.severe(e);
		}
		return null;
	}

}
