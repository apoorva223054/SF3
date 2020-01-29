/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.zxing.WriterException;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.DriverOrder;
import com.nirvanaxp.global.types.entities.DriverOrder_;
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
import com.nirvanaxp.services.jaxrs.packets.InventoryPostPacket;
import com.nirvanaxp.services.jaxrs.packets.KDSToOrderDetailItemStatusPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderHeaderSource;
import com.nirvanaxp.services.jaxrs.packets.OrderHeaderSourcePacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.jaxrs.packets.OrderPacketForOrderTransfer;
import com.nirvanaxp.services.jaxrs.packets.OrderTransferPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.PrintQueuePacket;
import com.nirvanaxp.services.jaxrs.packets.TipSavedPacket;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter_;
import com.nirvanaxp.types.entities.catalog.items.StorageType;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.email.PrintQueue;
import com.nirvanaxp.types.entities.email.PrintQueue_;
import com.nirvanaxp.types.entities.inventory.GoodsReceiveNotes;
import com.nirvanaxp.types.entities.inventory.RequestOrder;
import com.nirvanaxp.types.entities.inventory.RequestOrderDetailItems;
import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus;
import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus_;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute_;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus_;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToDiscount;
import com.nirvanaxp.types.entities.user.UsersToDiscount_;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderServiceForPost.
 */
public class OrderServiceForPost
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(OrderServiceForPost.class.getName());

	/**
	 * Instantiates a new order service for post.
	 */
	public OrderServiceForPost()
	{

	}

	/**
	 * Addorder.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param isTakeOutOrder
	 *            the is take out order
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
	private OrderHeaderWithUser addorder(HttpServletRequest httpRequest, String sessionId, EntityManager em, OrderPacket orderPacket, boolean isTakeOutOrder)
			throws NirvanaXPException, IOException, WriterException, InvalidSessionException, Exception
	{
		String locationIdAsString = orderPacket.getLocationId();
		String locationId = null;
		int merchantId = 0;

		if (locationIdAsString != null && locationIdAsString.length() > 0)
		{
			locationId = locationIdAsString.trim();
		}

		if (orderPacket.getMerchantId() != null && orderPacket.getMerchantId().trim().length() > 0)
		{
			merchantId = Integer.parseInt(orderPacket.getMerchantId().trim());

		}

		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		return bean.add(httpRequest, sessionId, em, orderPacket.getOrderHeader(), isTakeOutOrder, orderPacket.getGlobalUserId(), locationId, merchantId, orderPacket.getIdOfSessionUsedByPacket(),
				orderPacket.getSchemaName(), orderPacket);

	}

	/**
	 * Adds the nirvana X porder.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param isTakeOutOrder
	 *            the is take out order
	 * @return the order header with inventory post packet
	 * @throws Exception
	 *             the exception
	 */
	private OrderHeaderWithInventoryPostPacket addNirvanaXPorder(HttpServletRequest httpRequest, String sessionId, EntityManager em, OrderPacket orderPacket, boolean isTakeOutOrder) throws Exception
	{
		String locationIdAsString = orderPacket.getLocationId();
		String locationId = null;
		int merchantId = 0;

		if (locationIdAsString != null && locationIdAsString.length() > 0)
		{
			locationId = locationIdAsString.trim();
		}

		if (orderPacket.getMerchantId() != null && orderPacket.getMerchantId().trim().length() > 0)
		{
			merchantId = Integer.parseInt(orderPacket.getMerchantId().trim());

		}

		OrderManagementServiceBean bean = new OrderManagementServiceBean();

		return bean.addNirvanaXP(httpRequest, sessionId, em, orderPacket.getOrderHeader(), isTakeOutOrder, orderPacket.getGlobalUserId(), locationId, merchantId,
				orderPacket.getIdOfSessionUsedByPacket(), orderPacket.getSchemaName());

	}

	/**
	 * Insert order.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param isTakeOutOrder
	 *            the is take out order
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws WriterException
	 *             the writer exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader insertOrder(HttpServletRequest httpRequest, String sessionId, EntityManager em, OrderPacket orderPacket, boolean isTakeOutOrder)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException, WriterException, InvalidSessionException, Exception
	{

		OrderHeaderWithUser ohWithUser = addorder(httpRequest, sessionId, em, orderPacket, isTakeOutOrder);
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithUser.getOrderHeader());
		orderPacket.setOrderHeader(orderHeaderForPush);

		User localUser = ohWithUser.getUser();
		if (localUser != null)
		{
			User userForPost = new User();
			userForPost.setId(localUser.getId());
			userForPost.setFirstName(localUser.getFirstName());
			userForPost.setLastName(localUser.getLastName());
			userForPost.setEmail(localUser.getEmail());
			userForPost.setPhone(localUser.getPhone());
			userForPost.setAuthPin(localUser.getAuthPin());
			userForPost.setCountryId(localUser.getCountryId());
			// global user id needed by client during update push
			userForPost.setGlobalUsersId(localUser.getGlobalUsersId());
			orderPacket.setUser(userForPost);
		}

		try
		{
			addUpdateRequestOrder(ohWithUser.getOrderHeader(), httpRequest, em, null);

		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		try
		{
			new PrinterUtility().insertIntoPrintQueueForPartySizeUpdate(httpRequest, em, ohWithUser.getOrderHeader(), orderPacket.getLocationId(), false);

		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		if (ohWithUser.isTabOrderPush())
		{
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_seatTabOrderHeader.name(), true);
		}
		else
		{
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_add.name(), true);
		}

		return ohWithUser.getOrderHeader();

	}

	/**
	 * Adds the order to card.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param referenceNumber
	 *            the reference number
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param isTakeOutOrder
	 *            the is take out order
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 * @throws WriterException
	 *             the writer exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 */
	public OrderHeader addOrderToCard(HttpServletRequest httpRequest, String referenceNumber, EntityManager em, OrderPacket orderPacket, boolean isTakeOutOrder)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException, ParseException, WriterException, InvalidSessionException
	{
		OrderHeader prevOrderHeader = orderPacket.getOrderHeader();
		OrderHeader orderHeader = addToCart(httpRequest, referenceNumber, em, orderPacket, isTakeOutOrder);
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);

		if (orderHeader != null && orderHeader.getUsersId() != null )
		{
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, orderHeader.getUsersId());
			User newUser = new User();
			newUser.setUsername(user.getUsername());
			newUser.setEmail(user.getEmail());
			newUser.setPhone(user.getPhone());
			newUser.setFirstName(user.getFirstName());
			newUser.setLastName(user.getLastName());
			newUser.setId(user.getId());
			newUser.setGlobalUsersId(user.getGlobalUsersId());
			orderPacket.setUser(newUser);
		}
		/*
		 * try { OrderHeader header = new
		 * OrderManagementServiceBean().getOrderById(em,
		 * orderPacket.getOrderHeader().getId()); new
		 * PrinterUtility().insertIntoPrintQueueForCustomer(httpRequest, em,
		 * header, orderPacket.getLocationId()); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		if (prevOrderHeader.getId()!= null && prevOrderHeader.getId()== null)
		{
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_add.name(), true);
		}
		else
		{
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), true);
		}

		return orderHeader;
	}

	/**
	 * Adds the to cart.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param referenceNumber
	 *            the reference number
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param isTakeOutOrder
	 *            the is take out order
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
	private OrderHeader addToCart(HttpServletRequest httpRequest, String referenceNumber, EntityManager em, OrderPacket orderPacket, boolean isTakeOutOrder)
			throws NirvanaXPException, IOException, ParseException, WriterException, InvalidSessionException

	{
		String locationIdAsString = orderPacket.getLocationId();

		if (locationIdAsString != null && locationIdAsString.length() > 0)
		{
			locationIdAsString.trim();
		}

		if (orderPacket.getMerchantId() != null && orderPacket.getMerchantId().trim().length() > 0)
		{
			Integer.parseInt(orderPacket.getMerchantId().trim());

		}

		OrderHeader orderHeader = new OrderManagementServiceBean().addOrderToCard(httpRequest, em, orderPacket, referenceNumber);
		return orderHeader;

	}

	/**
	 * Insert nirvana XP order.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param isTakeOutOrder
	 *            the is take out order
	 * @return the order header
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader insertNirvanaXPOrder(HttpServletRequest httpRequest, String sessionId, EntityManager em, OrderPacket orderPacket, boolean isTakeOutOrder) throws Exception
	{

		OrderHeaderWithInventoryPostPacket ohWithUser = addNirvanaXPorder(httpRequest, sessionId, em, orderPacket, isTakeOutOrder);
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithUser.getOrderHeader());
		orderPacket.setOrderHeader(orderHeaderForPush);
		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_add.name(), true);

		return ohWithUser.getOrderHeader();

	}

	/**
	 * Update order.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader updateOrder(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket,String sessionId) throws Exception
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		boolean isDuplicatePacketCheck = false;
		OrderHeaderWithInventoryPostPacket ohWithInventoryPostPacket = new OrderManagementServiceBean().update(httpRequest, em, orderPacket.getOrderHeader(),
				orderPacket.getLocationId().trim(), isDuplicatePacketCheck, orderPacket.getIdOfOrderHoldingClientObj(),Integer.parseInt(orderPacket.getMerchantId()),sessionId);

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithInventoryPostPacket.getOrderHeader());

		InventoryPostPacket inventoryPostPacket = ohWithInventoryPostPacket.getInventoryPostPacket();
		if (inventoryPostPacket != null && inventoryPostPacket.getInventoryList() != null && inventoryPostPacket.getInventoryList().size() > 0)
		{

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			String inventoryPostPacketAsStr = objectMapper.writeValueAsString(inventoryPostPacket);

			orderHeaderForPush.setInventoryPostPacket(inventoryPostPacketAsStr);
			ohWithInventoryPostPacket.getOrderHeader().setInventoryPostPacket(inventoryPostPacketAsStr);
		}

		orderPacket.setOrderHeader(orderHeaderForPush);

		if (orderPacket.getPacketVersion() == 1)
		{
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), true);
		}
		else
		{
			// for 0 send
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), false);
		}

		return ohWithInventoryPostPacket.getOrderHeader();

	}

	/**
	 * Switch order location.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OrderHeader switchOrderLocation(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NumberFormatException, NirvanaXPException, JsonGenerationException, JsonMappingException, IOException
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());

		OrderHeader orderHeader = new OrderManagementServiceBean().switchOrderLocation(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim());

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);
		try
		{
			new PrinterUtility().insertIntoPrintQueueForPartySizeUpdate(httpRequest, em, orderHeader, orderPacket.getLocationId(), false);
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}
		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderLocation.name(), false);

		return orderHeader;

	}

	/**
	 * Change point of service count.
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
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OrderHeader changePointOfServiceCount(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException
	{

		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());

		OrderHeader orderHeader = new OrderManagementServiceBean().changePointOfServiceCount(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim());

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);

		try
		{
			new PrinterUtility().insertIntoPrintQueueForPartySizeUpdate(httpRequest, em, orderHeader, orderPacket.getLocationId(), true);
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderPointOfServiceCount.name(), false);

		return orderHeader;

	}

	/**
	 * Update order status.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws Exception
	 */
	public OrderHeader updateOrderStatus(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket) throws Exception
	{
		boolean isDuplicatePacketCheck = false;
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		OrderHeader orderHeader = new OrderManagementServiceBean().updateOrderStatus(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim(),
				orderPacket.getPacketVersion(), isDuplicatePacketCheck,Integer.parseInt(orderPacket.getMerchantId()));

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), false);

		return orderHeader;

	}

	/**
	 * Update order schedule date.
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
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OrderHeader updateOrderScheduleDate(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		OrderHeader orderHeader = new OrderManagementServiceBean().updateOrderScheduleDate(em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim(),
				orderPacket.getPacketVersion());

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderScheduleDate.name(), false);

		return orderHeader;
	}

	/**
	 * Merge order.
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
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OrderHeader mergeOrder(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket) throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		OrderHeader orderHeader = new OrderManagementServiceBean().mergeOrder(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getUnmergedLocationsId());
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderHeaderForPush.setMergedLocationsId(orderHeader.getMergedLocationsId());
		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_mergeOrder.name(), false);

		return orderHeader;

	}

	/**
	 * Update order payment.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param isDuplicatePacketCheck
	 *            the is duplicate packet check
	 * @param sessionId
	 *            the session id
	 * @param isReferenceNumber
	 *            the is reference number
	 * @return the order header
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader updateOrderPayment(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket, boolean isDuplicatePacketCheck, String sessionId, boolean isReferenceNumber)
			throws Exception
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());

		OrderHeaderWithInventoryPostPacket ohWithIPP = new OrderManagementServiceBean().updateOrderPayment(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId(),
				isDuplicatePacketCheck, sessionId, isReferenceNumber, orderPacket);

		em.getTransaction().commit();
		em.getTransaction().begin();

		if (ohWithIPP.getOrderHeader() != null && ohWithIPP.getOrderHeader().getOrderDetailItems() != null && ohWithIPP.getOrderHeader().getOrderDetailItems().size() > 0)
		{
			try
			{
				// OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class,
				// ohWithIPP.getOrderHeader().getOrderSourceId());
				// OrderSourceGroup orderSourceGroup =
				// (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class,
				// orderSource.getOrderSourceGroupId());
				new OrderManagementServiceBean().insertIntoKDSToOrderDetailItemStatus(httpRequest, em, ohWithIPP.getOrderHeader());

				/*
				 * if (orderSourceGroup.getName().equals("In Store") &&
				 * ohWithIPP.getOrderHeader().getReservationsId() == 0 &&
				 * ohWithIPP.getOrderHeader().getIsTabOrder() == 0 &&
				 * ohWithIPP.getOrderHeader().getLocationsId() ==
				 * orderPacket.getLocationId()) { new
				 * OrderManagementServiceBean().
				 * insertIntoKDSToOrderDetailItemStatus(httpRequest, em,
				 * ohWithIPP.getOrderHeader()); }
				 */
				OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, ohWithIPP.getOrderHeader().getOrderStatusId());
				if (orderStatus.getName().equals("Cancel Order"))
				{
					try
					{
						new PrinterUtility().insertIntoPrintQueueForCancelOrderAndQuickPay(httpRequest, em, ohWithIPP.getOrderHeader(), orderPacket.getLocationId() + "");
					}
					catch (Exception e)
					{

						// todo shlok need
						// handle proper Exception
						logger.severe(e);
					}
				}
				else
				{
					new PrinterUtility().insertIntoPrintQueue(httpRequest, em, ohWithIPP.getOrderHeader(), orderPacket.getLocationId());

				}
			}
			catch (Exception e)
			{

				// todo shlok need
				// handle proper Exception
				logger.severe(e);
			}
		}

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithIPP.getOrderHeader());

		InventoryPostPacket inventoryPostPacket = ohWithIPP.getInventoryPostPacket();
		if (inventoryPostPacket != null && inventoryPostPacket.getInventoryList() != null && inventoryPostPacket.getInventoryList().size() > 0)
		{

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			String inventoryPostPacketAsStr = objectMapper.writeValueAsString(inventoryPostPacket);

			orderHeaderForPush.setInventoryPostPacket(inventoryPostPacketAsStr);
			ohWithIPP.getOrderHeader().setInventoryPostPacket(inventoryPostPacketAsStr);
		}

		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderPayment.name(), false);

		return ohWithIPP.getOrderHeader();

	}

	/**
	 * Update order payment for braintree.
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
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OrderHeader updateOrderPaymentForBraintree(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		// by pass need to remove after uzma fixes this
		orderPacket.setLocationId(orderPacket.getOrderHeader().getLocationsId() + "");
		OrderHeader orderHeader = new OrderManagementServiceBean().updateOrderPaymentForBraintree(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId());

		return orderHeader;

	}

	/**
	 * Update order payment for batch settle.
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
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OrderHeader updateOrderPaymentForBatchSettle(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException
	{

		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());

		OrderHeader orderHeader = new OrderManagementServiceBean().updateOrderPaymentForBatchSettle(httpRequest, em, orderPacket.getOrderHeader());

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);

		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderPayment.name(), false);

		return orderHeader;

	}

	/**
	 * Adds the order for QSR with item details.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param sessionId
	 *            the session id
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws WriterException
	 *             the writer exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader addOrderForQSRWithItemDetails(HttpServletRequest httpRequest, String sessionId, EntityManager em, OrderPacket orderPacket)
			throws NirvanaXPException, NumberFormatException, ParseException, IOException, WriterException, InvalidSessionException, Exception
	{

		// for QSR order will get created with item details
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		OrderHeader orderHeader = null;
		OrderHeaderWithUser ohWithUser = addorder(httpRequest, sessionId, em, orderPacket, true);
		if (ohWithUser != null)
		{

			User localUser = ohWithUser.getUser();
			if (localUser != null)
			{
				User userForPost = new User();
				userForPost.setId(localUser.getId());
				userForPost.setFirstName(localUser.getFirstName());
				userForPost.setLastName(localUser.getLastName());
				userForPost.setEmail(localUser.getEmail());
				userForPost.setPhone(localUser.getPhone());
				// global user id needed by client during update push
				userForPost.setGlobalUsersId(localUser.getGlobalUsersId());
				orderPacket.setUser(userForPost);
			}

			// now update this order
			// TODO Ankur - why?
			boolean isDuplicatePacketCheck = false;
			OrderHeaderWithInventoryPostPacket ohWithIPP = new OrderManagementServiceBean().update(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim(),
					isDuplicatePacketCheck, orderPacket.getIdOfOrderHoldingClientObj(),Integer.parseInt(orderPacket.getMerchantId()),sessionId);

			InventoryPostPacket inventoryPostPacket = ohWithIPP.getInventoryPostPacket();
			if (inventoryPostPacket != null && inventoryPostPacket.getItemList() != null && inventoryPostPacket.getItemList().size() != 0)
			{

				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
				objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
				objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

				String inventoryPostPacketAsStr = null;

				try
				{
					inventoryPostPacketAsStr = objectMapper.writeValueAsString(inventoryPostPacket);

					OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithIPP.getOrderHeader());
					orderPacket.setOrderHeader(orderHeaderForPush);
					orderHeaderForPush.setInventoryPostPacket(inventoryPostPacketAsStr);
					sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_add.name(), true);
				}
				catch (IOException e)
				{
					// could not send push
					// todo shlok need
					// handle proper Exception
					logger.severe(httpRequest, e);
				}

				if (inventoryPostPacketAsStr != null)
				{
					ohWithIPP.getOrderHeader().setInventoryPostPacket(inventoryPostPacketAsStr);
				}
			}
			orderHeader = ohWithIPP.getOrderHeader();
		}

		return orderHeader;

	}

	/**
	 * Gets the order header with minimun required details.
	 *
	 * @param orderHeader
	 *            the order header
	 * @return the order header with minimun required details
	 */
	public OrderHeader getOrderHeaderWithMinimunRequiredDetails(OrderHeader orderHeader)
	{
		OrderHeader orderHeaderForPush = new OrderHeader();
		orderHeaderForPush.setId(orderHeader.getId());
		orderHeaderForPush.setLocationsId(orderHeader.getLocationsId());
		orderHeaderForPush.setReservationsId(orderHeader.getReservationsId());
		orderHeaderForPush.setOrderStatusId(orderHeader.getOrderStatusId());
		orderHeaderForPush.setPointOfServiceCount(orderHeader.getPointOfServiceCount());
		orderHeaderForPush.setCreated(orderHeader.getCreated());
		orderHeaderForPush.setUpdated(orderHeader.getUpdated());
		orderHeaderForPush.setBalanceDue(orderHeader.getBalanceDue());
		orderHeaderForPush.setOrderSourceId(orderHeader.getOrderSourceId());
		orderHeaderForPush.setAmountPaid(orderHeader.getAmountPaid());
		orderHeaderForPush.setFirstName(orderHeader.getFirstName());
		orderHeaderForPush.setLastName(orderHeader.getLastName());
		orderHeaderForPush.setVoidReasonId(orderHeader.getVoidReasonId());
		orderHeaderForPush.setIsOrderReopened(orderHeader.getIsOrderReopened());
		orderHeaderForPush.setPriceDiscount(orderHeader.getPriceDiscount());
		orderHeaderForPush.setSubTotal(orderHeader.getSubTotal());
		orderHeaderForPush.setTaxExemptId(orderHeader.getTaxExemptId());
		orderHeaderForPush.setScheduleDateTime(orderHeader.getScheduleDateTime());
		orderHeaderForPush.setOrderNumber(orderHeader.getOrderNumber());
		orderHeaderForPush.setMergedLocationsId(orderHeader.getMergedLocationsId());
		String correctDateFormat = null;
		correctDateFormat = ConfigFileReader.correctDateFormat(orderHeader.getDate());

		if (correctDateFormat != null && correctDateFormat.trim().length() > 0)
		{
			orderHeaderForPush.setDate(correctDateFormat);
		}
		else
		{
			orderHeaderForPush.setDate(orderHeader.getDate());
		}
		orderHeaderForPush.setCreatedBy(orderHeader.getCreatedBy());
		orderHeaderForPush.setPartySizeUpdated(orderHeader.isPartySizeUpdated());
		if (orderHeader.getCloseTime() != 0)
		{
			orderHeaderForPush.setCloseTime(orderHeader.getCloseTime());
		}
		orderHeaderForPush.setIsTabOrder(orderHeader.getIsTabOrder());
		orderHeaderForPush.setServername(orderHeader.getServername());
		orderHeaderForPush.setAddressShipping(orderHeader.getAddressShipping());
		orderHeaderForPush.setNirvanaXpBatchNumber(orderHeader.getNirvanaXpBatchNumber());
		orderHeaderForPush.setIsSeatWiseOrder(orderHeader.getIsSeatWiseOrder());
		orderHeaderForPush.setMergeOrderId(orderHeader.getMergeOrderId());
		orderHeaderForPush.setPriceDiscount(orderHeader.getPriceDiscount());
		orderHeaderForPush.setSubTotal(orderHeader.getSubTotal());
		// add by apoorv after ketan requirement
		orderHeaderForPush.setServerId(orderHeader.getServerId());
		orderHeaderForPush.setCashierId(orderHeader.getCashierId());
		orderHeaderForPush.setShiftSlotId(orderHeader.getShiftSlotId());
		orderHeaderForPush.setPriceDiscountItemLevel(orderHeader.getPriceDiscountItemLevel());
		orderHeaderForPush.setPoRefrenceNumber(orderHeader.getPoRefrenceNumber());
		orderHeaderForPush.setRequestedLocationId(orderHeader.getRequestedLocationId());
		orderHeaderForPush.setOrderTypeId(orderHeader.getOrderTypeId());
		orderHeaderForPush.setUsersId(orderHeader.getUsersId());
		return orderHeaderForPush;
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param orderPacket
	 *            the order packet
	 * @param operationName
	 *            the operation name
	 * @param shouldSendUSerInfoToo
	 *            the should send U ser info too
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public void sendPacketForBroadcast(HttpServletRequest httpRequest, PostPacket orderPacket, String operationName, boolean shouldSendUSerInfoToo) throws NirvanaXPException
	{
		try
		{
			// so that session id value does not get broadcasted
			orderPacket.setIdOfSessionUsedByPacket(0);
			orderPacket.setSessionId(null);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			String internalJson = null;

			internalJson = objectMapper.writeValueAsString(orderPacket);

			operationName = ServiceOperationsUtility.getOperationName(operationName);
			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, orderPacket.getClientId(), POSNServices.OrderManagementService.name(), operationName, internalJson, orderPacket.getMerchantId(),
					orderPacket.getLocationId(), orderPacket.getEchoString(), orderPacket.getSchemaName());
		}
		catch (IOException e)
		{
			// could not send push
			logger.severe(httpRequest, e, "Unable to broadcast order packet");
		}
	}

	public void sendPacketForBroadcast(HttpServletRequest httpRequest, String operation, PostPacket postPacket)
	{

		operation = ServiceOperationsUtility.getOperationName(operation);
		MessageSender messageSender = new MessageSender();

		messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.LookupService.name(), operation, null, postPacket.getMerchantId(), postPacket.getLocationId(),
				postPacket.getEchoString(), postPacket.getSchemaName());

	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param orderPacket
	 *            the order packet
	 * @param operationName
	 *            the operation name
	 * @param shouldSendUSerInfoToo
	 *            the should send U ser info too
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public void sendPacketForBroadcast(HttpServletRequest httpRequest, OrderPacket orderPacket, String operationName, boolean shouldSendUSerInfoToo) throws NirvanaXPException
	{
		try
		{
			// so that session id value does not get broadcasted
			orderPacket.setIdOfSessionUsedByPacket(0);
			orderPacket.setSessionId(null);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			String internalJson = null;
			if (!shouldSendUSerInfoToo)
			{
				internalJson = objectMapper.writeValueAsString(orderPacket.getOrderHeader());
			}
			else
			{
				internalJson = objectMapper.writeValueAsString(orderPacket);
			}

			operationName = ServiceOperationsUtility.getOperationName(operationName);
			MessageSender messageSender = new MessageSender();
			messageSender.sendMessage(httpRequest, orderPacket.getClientId(), POSNServices.OrderManagementService.name(), operationName, internalJson, orderPacket.getMerchantId(),
					orderPacket.getLocationId(), orderPacket.getEchoString(), orderPacket.getSchemaName());
		}
		catch (IOException e)
		{
			// could not send push
			logger.severe(httpRequest, e, "Unable to broadcast order packet");
		}
	}

	/**
	 * Update order source.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderSourcePacket
	 *            the order source packet
	 * @param cancelOrder
	 *            the cancel order
	 * @return the order header source packet
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public OrderHeaderSourcePacket updateOrderSource(HttpServletRequest httpRequest, EntityManager em, OrderHeaderSourcePacket orderSourcePacket, boolean cancelOrder) throws NirvanaXPException
	{

		String[] reservationStatusId = null;
		OrderHeaderSource orderPacket = orderSourcePacket.getOrderHeaderSource();
		if (orderSourcePacket.getLocationId() != null)
		{
			String locationId = orderSourcePacket.getLocationId();
			reservationStatusId = new OrderManagementServiceBean().updateOrderAndSource(httpRequest, em, orderPacket.getOrderId(), orderPacket.getOrderSourceId(), orderPacket.getReservationId(),
					orderPacket.getUpdatedBy(), locationId, orderPacket.getUserId(), cancelOrder);

		}
		if (reservationStatusId[0]!=null )
		{// setting previous reservation
			// status id
			orderPacket.setPreviousReservationStatusId(reservationStatusId[0]);
		}
		if (reservationStatusId[1]!=null )
		{
			orderPacket.setNewReservationStatusId(reservationStatusId[1]);
		}
		if (reservationStatusId[2]!=null )
		{
			orderPacket.setPartySize(Integer.parseInt(reservationStatusId[2]));
		}
		if (reservationStatusId[3]!=null )
		{
			orderPacket.setOrderStatusId(reservationStatusId[3]);
		}
		if (reservationStatusId[4]!=null )
		{
			orderPacket.setOrderId(reservationStatusId[4]);
		}
		if (reservationStatusId[5]!=null )
		{
			orderPacket.setCancelOrderId(reservationStatusId[5]);
		}
		OrderHeader header = null;

		if (orderPacket.getOrderId()!=null )
		{
			header = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderId());
		}

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(header);

		orderPacket.setOrderHeader(orderHeaderForPush);
		orderPacket.setOrderHeader(header);
		orderSourcePacket.setOrderHeaderSource(orderPacket);
		sendPacketForBroadcast(httpRequest, orderSourcePacket, POSNServiceOperations.OrderManagementService_updateOrderSource.name());

		return orderSourcePacket;

	}

	/**
	 * Update order status to paid.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader updateOrderStatusToPaid(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket,
			String ccArray) throws NumberFormatException, Exception
	{

		OrderHeader orderHeader = orderPacket.getOrderHeader();
		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		OrderHeaderWithInventoryPostPacket ohWithIPP = new OrderManagementServiceBean().updateOrderStatusToPaid(httpRequest, em, orderHeader.getId(), orderSource.getOrderSourceGroupId(),
				orderHeader.getUpdatedBy(), orderHeader.getLocationsId(), Integer.parseInt(orderPacket.getMerchantId()), orderPacket.getCashOnDelivery(),ccArray);
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithIPP.getOrderHeader());

		InventoryPostPacket inventoryPostPacket = ohWithIPP.getInventoryPostPacket();
		if (inventoryPostPacket != null && inventoryPostPacket.getInventoryList() != null && inventoryPostPacket.getInventoryList().size() > 0)
		{

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			String inventoryPostPacketAsStr = objectMapper.writeValueAsString(inventoryPostPacket);

			orderHeaderForPush.setInventoryPostPacket(inventoryPostPacketAsStr);
			ohWithIPP.getOrderHeader().setInventoryPostPacket(inventoryPostPacketAsStr);
		}
		if (orderPacket.getCashOnDelivery() == 1)
		{
			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderHeader().getId());
			new PrinterUtility().insertIntoPrintQueueForCustomer(httpRequest, em, header, orderPacket.getLocationId());
		}

		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderPayment.name(), false);

		return ohWithIPP.getOrderHeader();

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
	 * @param businessId
	 *            the business id
	 * @param sessionId
	 *            the session id
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	public boolean closeBusiness(HttpServletRequest httpRequest, EntityManager em, String updatedBy, String locationId, int businessId, String sessionId, PostPacket packet, String date) throws Exception
	{
		return new OrderManagementServiceBean().closeBusiness(httpRequest, em, updatedBy, locationId, sessionId, packet, date);
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param orderPacket
	 *            the order packet
	 * @param operationName
	 *            the operation name
	 */
	private void sendPacketForBroadcast(HttpServletRequest httpRequest, OrderHeaderSourcePacket orderPacket, String operationName)
	{
		try
		{
			// so that session id value does not get broadcasted
			orderPacket.setIdOfSessionUsedByPacket(0);
			orderPacket.setSessionId(null);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			String internalJson = objectMapper.writeValueAsString(orderPacket.getOrderHeaderSource());

			operationName = ServiceOperationsUtility.getOperationName(operationName);
			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, orderPacket.getClientId(), POSNServices.OrderManagementService.name(), operationName, internalJson, orderPacket.getMerchantId(),
					orderPacket.getLocationId(), orderPacket.getEchoString(), orderPacket.getSchemaName());
		}
		catch (IOException e)
		{
			// could not send push
			// todo shlok need
			// handle proper Exception
			logger.severe(httpRequest, e);
		}
	}

	/**
	 * Update order payment for tip saved.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param tipSavedPacket
	 *            the tip saved packet
	 * @return the order header
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader updateOrderPaymentForTipSaved(HttpServletRequest httpRequest, EntityManager em, TipSavedPacket tipSavedPacket) throws Exception
	{

		OrderPacket orderPacket = new OrderPacket();

		OrderHeaderWithInventoryPostPacket ohWithIPP = new OrderManagementServiceBean().updateOrderPaymentForTipSave(httpRequest, em, tipSavedPacket);
		// setting post packet
		orderPacket.setClientId(tipSavedPacket.getClientId());
		orderPacket.setEchoString(tipSavedPacket.getEchoString());
		orderPacket.setLocationId(tipSavedPacket.getLocationId());
		orderPacket.setMerchantId(tipSavedPacket.getMerchantId());
		orderPacket.setSchemaName(tipSavedPacket.getSchemaName());
		orderPacket.setSessionId(tipSavedPacket.getSessionId());
		orderPacket.setIdOfSessionUsedByPacket(tipSavedPacket.getIdOfSessionUsedByPacket());
		orderPacket.setOrderHeader(ohWithIPP.getOrderHeader());
		orderPacket.getOrderHeader().setSessionKey(tipSavedPacket.getIdOfSessionUsedByPacket());
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithIPP.getOrderHeader());

		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderPayment.name(), false);

		return ohWithIPP.getOrderHeader();

	}

	/**
	 * Update order for seatwise.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader updateOrderForSeatwise(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket,String sessionId) throws Exception
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		OrderHeaderWithInventoryPostPacket ohWithIPP = new OrderManagementServiceBean().updateOrderForSeatwise(httpRequest, em, orderPacket.getOrderHeader(),
				orderPacket.getLocationId().trim(),Integer.parseInt(orderPacket.getMerchantId()),sessionId);

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithIPP.getOrderHeader());

		InventoryPostPacket inventoryPostPacket = ohWithIPP.getInventoryPostPacket();
		if (inventoryPostPacket != null && inventoryPostPacket.getInventoryList() != null && inventoryPostPacket.getInventoryList().size() > 0)
		{

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			String inventoryPostPacketAsStr = objectMapper.writeValueAsString(inventoryPostPacket);

			orderHeaderForPush.setInventoryPostPacket(inventoryPostPacketAsStr);
			ohWithIPP.getOrderHeader().setInventoryPostPacket(inventoryPostPacketAsStr);
		}

		orderPacket.setOrderHeader(orderHeaderForPush);

		if (orderPacket.getPacketVersion() == 1)
		{
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), true);
		}
		else
		{
			// for 0 send
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), false);
		}

		return ohWithIPP.getOrderHeader();

	}

	/**
	 * Update order for duplicate check.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws Exception
	 *             the exception
	 */
	public OrderHeader updateOrderForDuplicateCheck(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket,String sessionId) throws Exception
	{

		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		boolean isDuplicatePacketCheck = true;
		OrderHeaderWithInventoryPostPacket ohWithIPP = new OrderManagementServiceBean().update(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim(),
				isDuplicatePacketCheck, orderPacket.getIdOfOrderHoldingClientObj(),Integer.parseInt(orderPacket.getMerchantId()),sessionId);
		em.getTransaction().commit();
		em.getTransaction().begin();

		if (ohWithIPP.getOrderHeader() != null && ohWithIPP.getOrderHeader().getOrderDetailItems() != null && ohWithIPP.getOrderHeader().getOrderDetailItems().size() > 0)
		{
			logger.severe("ohWithIPP.getOrderHeader().getOrderDetailItems() " + ohWithIPP.getOrderHeader().getOrderDetailItems());

			addUpdateRequestOrder(ohWithIPP.getOrderHeader(), httpRequest, em, ohWithIPP.getOrderHeader().getOrderDetailItems());
			try
			{
				new PrinterUtility().insertIntoPrintQueue(httpRequest, em, ohWithIPP.getOrderHeader(), orderPacket.getLocationId());
			}
			catch (Exception e)
			{

				// todo shlok need
				// handle proper Exception
				logger.severe(e);
			}
		}
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(ohWithIPP.getOrderHeader());

		InventoryPostPacket inventoryPostPacket = ohWithIPP.getInventoryPostPacket();
		if (inventoryPostPacket != null && inventoryPostPacket.getInventoryList() != null && inventoryPostPacket.getInventoryList().size() > 0)
		{

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);

			String inventoryPostPacketAsStr = objectMapper.writeValueAsString(inventoryPostPacket);

			orderHeaderForPush.setInventoryPostPacket(inventoryPostPacketAsStr);
			ohWithIPP.getOrderHeader().setInventoryPostPacket(inventoryPostPacketAsStr);
		}

		orderPacket.setOrderHeader(orderHeaderForPush);

		if (orderPacket.getPacketVersion() == 1)
		{
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), true);
		}
		else
		{
			// for 0 send
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), false);
		}

		return ohWithIPP.getOrderHeader();

	}

	/**
	 * Update order payment for under processing transaction.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public OrderHeader updateOrderPaymentForUnderProcessingTransaction(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NumberFormatException, IOException, InvalidSessionException, NirvanaXPException, ParseException
	{
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());

		OrderHeader orderHeader = new OrderManagementServiceBean().updateOrderPaymentForUnderProcessingTransaction(em, orderPacket.getOrderHeader());

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);

		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderPayment.name(), false);

		return orderHeader;

	}

	/**
	 * Update order status for duplicate check.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public OrderHeader updateOrderStatusForDuplicateCheck(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket) throws NumberFormatException, Exception
	{
		boolean isDuplicatePacketCheck = true;
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		OrderHeader orderHeader = new OrderManagementServiceBean().updateOrderStatus(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim(),
				orderPacket.getPacketVersion(), isDuplicatePacketCheck,Integer.parseInt(orderPacket.getMerchantId()));
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, orderHeader.getOrderStatusId());
		if (orderStatus.getName().equals("Cancel Order"))
		{
			try
			{
				new PrinterUtility().insertIntoPrintQueueForCancelOrderAndQuickPay(httpRequest, em, orderHeader, orderPacket.getLocationId());
			}
			catch (Exception e)
			{
				// todo shlok need
				// handle proper Exception
				logger.severe(e);
			}
		}
		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), false);

		return orderHeader;

	}

	/**
	 * Merge order with items.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order header
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public OrderHeader mergeOrderWithItems(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket) throws NumberFormatException, Exception
	{
		boolean isDuplicatePacketCheck = true;
		orderPacket.getOrderHeader().setSessionKey(orderPacket.getIdOfSessionUsedByPacket());
		OrderHeader orderHeader = new OrderManagementServiceBean().updateOrderStatus(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId().trim(),
				orderPacket.getPacketVersion(), isDuplicatePacketCheck,Integer.parseInt(orderPacket.getMerchantId()));

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), false);

		return orderHeader;

	}

	/**
	 * Update order info for delivery takeout.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order packet
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws Exception
	 *             the exception
	 */
	public OrderPacket updateOrderInfoForDeliveryTakeout(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NumberFormatException, NirvanaXPException, ParseException, JsonGenerationException, JsonMappingException, IOException, Exception
	{

		User user = orderPacket.getUser();
		OrderHeader orderHeader = orderPacket.getOrderHeader();
		OrderHeader o = new OrderManagementServiceBean().getOrderById(em, orderHeader.getId());
		// check for global user id
		GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
		User localUser = null;

		// add this user to global and local database and insert into
		// reservation user id
		// create new user
		localUser = new User();
		localUser.setFirstName(user.getFirstName());
		localUser.setLastName(user.getLastName());
		localUser.setPhone(user.getPhone());
		localUser.setEmail(user.getEmail());
		localUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		localUser.setUpdatedBy(user.getCreatedBy());
		localUser.setCreatedBy(user.getCreatedBy());
		localUser.setStatus("A");
		localUser.setCountryId(user.getCountryId());
		localUser.setId(new StoreForwardUtility().generateUUID());
		if (localUser.getPhone() != null && localUser.getPhone().length() > 0)
		{
			localUser.setUsername(localUser.getPhone());
		}
		else
		{
			localUser.setUsername(localUser.getEmail());
		}

		EntityManager globalEM = null;
		try
		{

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			String username = "";
			if (localUser.getUsername() != null)
			{
				username = localUser.getUsername();
			}
			if (username == null || localUser.getUsername().trim().length() == 0)
			{
				if (localUser.getPhone() != null && localUser.getPhone().length() > 0)
				{
					username = localUser.getPhone();
				}
				else
				{
					username = localUser.getEmail();
				}
			}
			if (username != null && username.trim().length() != 0)
			{
				localUser = globalUsermanagement.addUserToLocalDatabaseProgramatically(httpRequest, globalEM, em, localUser, orderHeader.getLocationsId(),orderPacket);
			}

		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

		if (localUser != null)
		{
			o.setUsersId(localUser.getId());

			o.setPointOfServiceCount(orderHeader.getPointOfServiceCount());
			o.setSessionKey(orderHeader.getSessionKey());
			o.setFirstName(orderHeader.getFirstName());
			o.setLastName(orderHeader.getLastName());
			o.setAddressShipping(orderHeader.getAddressShipping());
			o.setAddressBilling(orderHeader.getAddressBilling());

			// added to insert current time of the location in the
			// order
			// header

			// 37640 changes as time is noit changing in device done by apoorv
			TimezoneTime timezoneTime = new TimezoneTime();
			String locationID = orderHeader.getLocationsId();

			String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(locationID, em);
			if (currentDateTime != null && currentDateTime.length > 0)
			{
				o.setDate(currentDateTime[0]);
				if (orderHeader.getScheduleDateTime() == null)
				{
					o.setScheduleDateTime(currentDateTime[2]);
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationID, em));
				}
				else
				{
					o.setScheduleDateTime(orderHeader.getScheduleDateTime());
					o.setScheduleDateTime(timezoneTime.getDateAccordingToGMT(o.getScheduleDateTime(), locationID, em));
				}

			}
			// added by Apoorva by 29-03-2018 #44107 ,
			if (orderHeader.getOrderStatusId() != null)
			{
				o.setOrderStatusId(orderHeader.getOrderStatusId());
			}
			o.setOrderSourceId(orderHeader.getOrderSourceId());
			o.setDeliveryTax(orderHeader.getDeliveryTax());
			o.setDeliveryCharges(orderHeader.getDeliveryCharges());
			o.setDeliveryOptionId(orderHeader.getDeliveryOptionId());
			o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			o.setUpdatedBy(orderHeader.getUpdatedBy());
			o.setRequestedLocationId(orderHeader.getRequestedLocationId());

			orderPacket.setUser(localUser);
			if (orderHeader.getEventName() != null)
			{
				o.setEventName(orderHeader.getEventName());
			}
			if (orderHeader.getStartDate() != null)
			{
				o.setStartDate(orderHeader.getStartDate());
			}
			if (orderHeader.getEndDate() != null)
			{
				o.setEndDate(orderHeader.getEndDate());
			}
			if (orderHeader.getPointOfServiceCount() != 0)
			{
				o.setPointOfServiceCount(orderHeader.getPointOfServiceCount());
			}
			OrderHeader header = em.merge(o);
			o.setAddressShipping(header.getAddressShipping());
			o.setAddressBilling(header.getAddressBilling());

			EntityManager emGlobal = null;
			try
			{
				if (header.getDriverId() != null)
				{

					emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

					EntityTransaction txGlobal = emGlobal.getTransaction();
					txGlobal.begin();

					CriteriaBuilder builder = emGlobal.getCriteriaBuilder();
					CriteriaQuery<DriverOrder> criteria = builder.createQuery(DriverOrder.class);
					Root<DriverOrder> r = criteria.from(DriverOrder.class);
					TypedQuery<DriverOrder> query = emGlobal.createQuery(criteria.select(r).where(builder.and(builder.equal(r.get(DriverOrder_.orderId), o.getId())),
							builder.and(builder.equal(r.get(DriverOrder_.locationsId), o.getLocationsId()))));

					DriverOrder driverOrder = query.getSingleResult();

					driverOrder.setCustomerAddress(createAddress(o.getAddressShipping()));

					emGlobal.merge(driverOrder);

					txGlobal.commit();

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
			}

			orderPacket.setOrderHeader(o);

			try
			{
				new PrinterUtility().insertIntoPrintQueueForOrderUpdateFromOutside(httpRequest, em, orderPacket.getOrderHeader(), orderPacket.getLocationId());
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			orderPacket.setOrderHeader(header);
			sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), true);

		}
		return orderPacket;
	}

	/**
	 * Update order transfer.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @param tx
	 *            the tx
	 * @return the order packet for order transfer
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws Exception
	 *             the exception
	 */
	public OrderPacketForOrderTransfer updateOrderTransfer(HttpServletRequest httpRequest, EntityManager em, OrderPacketForOrderTransfer orderPacket, EntityTransaction tx)
			throws NumberFormatException, NirvanaXPException, ParseException, JsonGenerationException, JsonMappingException, IOException, Exception
	{
		OrderPacketForOrderTransfer o = new OrderManagementServiceBean().updateOrderTransfer(httpRequest, em, orderPacket, tx);
		// check for global user id

		orderPacket.getFromOrderHeader().setOrderDetailItems(null);
		orderPacket.getToOrderHeader().setOrderDetailItems(null);

		orderPacket.getFromOrderHeader().setOrderPaymentDetails(null);
		orderPacket.getToOrderHeader().setOrderPaymentDetails(null);

		OrderHeader toOrder = new OrderManagementServiceBean().getOrderById(em, orderPacket.getToOrderHeader().getId());

		try
		{
			new PrinterUtility().insertIntoPrintQueueForOrderTransfer(httpRequest, em, toOrder, orderPacket.getLocationId(), o.getFromOrderHeader().getId());
		}
		catch (Exception e)
		{

			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}

		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderTransfer.name(), true);
		return o;
	}

	/**
	 * Gets the two order by id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the two order by id
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws Exception
	 *             the exception
	 */
	public OrderPacketForOrderTransfer getTwoOrderById(HttpServletRequest httpRequest, EntityManager em, OrderPacketForOrderTransfer orderPacket)
			throws NumberFormatException, NirvanaXPException, ParseException, JsonGenerationException, JsonMappingException, IOException, Exception
	{

		orderPacket.setFromOrderHeader(new OrderManagementServiceBean().getOrderByIdAndParentLocationId(em, orderPacket.getFromOrderHeader().getId(), orderPacket.getRootLocationId()));
		orderPacket.setToOrderHeader(new OrderManagementServiceBean().getOrderByIdAndParentLocationId(em, orderPacket.getToOrderHeader().getId(), orderPacket.getRootLocationId()));

		return orderPacket;
	}

	/**
	 * Update item transfer.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order packet for order transfer
	 * @throws NumberFormatException
	 *             the number format exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws Exception
	 *             the exception
	 */
	public OrderPacketForOrderTransfer updateItemTransfer(HttpServletRequest httpRequest, EntityManager em, OrderPacketForOrderTransfer orderPacket)
			throws NumberFormatException, NirvanaXPException, ParseException, JsonGenerationException, JsonMappingException, IOException, Exception
	{
		OrderPacketForOrderTransfer o = new OrderManagementServiceBean().updateItemTransfer(httpRequest, em, orderPacket,Integer.parseInt(orderPacket.getMerchantId()));
		// check for global user id
		orderPacket.getFromOrderHeader().setOrderDetailItems(null);
		orderPacket.getToOrderHeader().setOrderDetailItems(null);

		orderPacket.getFromOrderHeader().setOrderPaymentDetails(null);
		orderPacket.getToOrderHeader().setOrderPaymentDetails(null);
		OrderHeader fromOrderHeader = new OrderManagementServiceBean().getOrderById(em, orderPacket.getFromOrderHeader().getId());
		OrderHeader toOrderHeader = new OrderManagementServiceBean().getOrderById(em, orderPacket.getToOrderHeader().getId());
		try
		{
			new PrinterUtility().insertIntoPrintQueueForItemTransfer(httpRequest, em, toOrderHeader, orderPacket.getLocationId(), fromOrderHeader);

		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}
		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateItemTransfer.name(), true);

		return o;
	}

	/**
	 * Delete order by order management role.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderPacket
	 *            the order packet
	 * @return the order packet
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public OrderPacket deleteOrderByOrderManagementRole(HttpServletRequest httpRequest, EntityManager em, OrderPacket orderPacket)
			throws NirvanaXPException, JsonGenerationException, JsonMappingException, IOException
	{
		for (String orderId : orderPacket.getOrderHeaderIds())
		{
			OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, orderId);
			if (header != null)
			{
				boolean result = new OrderManagementServiceBean().deleteOrderHeader(httpRequest, em, orderId);

				if (result)
				{
					OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(header);
					orderPacket.setOrderHeader(orderHeaderForPush);

					sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), false);

				}
			}

		}

		return orderPacket;

	}

	public PrintQueuePacket updatePrintQueue(EntityManager em, HttpServletRequest httpRequest, PrintQueuePacket printQueuePacket) throws NirvanaXPException
	{

		PrintQueuePacket packet = new PrintQueuePacket();
		List<PrintQueue> printQueues = new ArrayList<PrintQueue>();

		for (PrintQueue printQueue : printQueuePacket.getPrintQueueList())
		{
			PrintQueue  pq= em.find(PrintQueue.class, printQueue.getId());
			if(pq!=null){
			pq.setStatus(printQueue.getStatus());
			pq.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			try
			{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date scheduleDateTime = formatter.parse(pq.getScheduleDateTime());
				if (scheduleDateTime.after(pq.getUpdated()))
				{
					pq.setUpdated(formatter.parse(pq.getScheduleDateTime()));
				}
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pq=em.merge(pq);
			printQueues.add(pq);
	
			if (pq.getStatus().equals("B"))
			{

				OrderHeader header = new OrderManagementServiceBean().getOrderById(em, pq.getOrderId());
				OrderStatus currentOrderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, header.getOrderStatusId());
				OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, header.getOrderSourceId());
				OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
	
				if (  !currentOrderStatus.getName().equals("Bus Ready") && !currentOrderStatus.getName().equals("Ready to Order")
						&& !currentOrderStatus.getName().equals("Ready To Serve") && !currentOrderStatus.getName().equals("Reopen") )
				{
	
					OrderStatus orderStatus = null;

					if (header.getOrderTypeId() == 2)
					{
						orderStatus = getOrderStatusByNameAndLocation(em, "Close Production", pq.getLocationId(), orderSource.getOrderSourceGroupId());
					}
					else
					{
						if (orderSourceGroup.getName().equals("Pick Up"))
						{
							orderStatus = getOrderStatusByNameAndLocation(em, "Ready for pick up", pq.getLocationId(), orderSource.getOrderSourceGroupId());
						}
						else if (orderSourceGroup.getName().equals("Delivery"))
						{
							orderStatus = getOrderStatusByNameAndLocation(em, "Ready for delivery", pq.getLocationId(), orderSource.getOrderSourceGroupId());

						}
						else
						{
							if(!currentOrderStatus.getName().equals("Order Paid")&& !currentOrderStatus.getName().equals("Cash On Delivery")){
							orderStatus = getOrderStatusByNameAndLocation(em, "Ready To Serve", pq.getLocationId(), orderSource.getOrderSourceGroupId());
							}
						}

					}

					if(orderStatus!=null){
					header.setOrderStatusId(orderStatus.getId());
					header.setSessionKey(printQueuePacket.getIdOfSessionUsedByPacket());
					header = em.merge(header);
					OrderPacket orderPacket = new OrderPacket();
					OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(header);
					orderPacket.setOrderHeader(orderHeaderForPush);
					orderPacket.setClientId(printQueuePacket.getClientId());
					orderPacket.setEchoString(printQueuePacket.getEchoString());
					orderPacket.setMerchantId(printQueuePacket.getMerchantId());
					orderPacket.setLocationId(printQueuePacket.getLocationId());
					orderPacket.setSessionId(printQueuePacket.getSessionId());
					orderPacket.setIdOfSessionUsedByPacket(printQueuePacket.getIdOfSessionUsedByPacket());
					sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), false);
				}

				// Close in house production PO After Bump
				// closeProductionOrder(header,em,httpRequest,printQueuePacket,true);
			}
			}
			else if (pq.getStatus().equals("U"))
			{

				OrderHeader header = new OrderManagementServiceBean().getOrderById(em, pq.getOrderId());
				OrderStatus currentOrderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, header.getOrderStatusId());
				OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, header.getOrderSourceId());
				OrderSourceGroup orderSourceGroup = (OrderSourceGroup) new CommonMethods().getObjectById("OrderSourceGroup", em,OrderSourceGroup.class, orderSource.getOrderSourceGroupId());
				if (!currentOrderStatus.getName().equals("Order Paid") && !currentOrderStatus.getName().equals("Bus Ready") && !currentOrderStatus.getName().equals("Ready to Order")
						&& !currentOrderStatus.getName().equals("Cooking") && !currentOrderStatus.getName().equals("Reopen") && !currentOrderStatus.getName().equals("Cash On Delivery"))
				{
					OrderStatus orderStatus;
					if (header.getOrderTypeId() == 2)
					{
						orderStatus = getOrderStatusByNameAndLocation(em, "Order Placed", pq.getLocationId(), orderSource.getOrderSourceGroupId());
					}
					else
					{
						orderStatus = getOrderStatusByNameAndLocation(em, "Cooking", pq.getLocationId(), orderSource.getOrderSourceGroupId());
					}
		
					header.setOrderStatusId(orderStatus.getId());
					header.setSessionKey(printQueuePacket.getIdOfSessionUsedByPacket());
					header = em.merge(header);
					OrderPacket orderPacket = new OrderPacket();
					OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(header);
					orderPacket.setOrderHeader(orderHeaderForPush);
					orderPacket.setClientId(printQueuePacket.getClientId());
					orderPacket.setEchoString(printQueuePacket.getEchoString());
					orderPacket.setMerchantId(printQueuePacket.getMerchantId());
					orderPacket.setLocationId(printQueuePacket.getLocationId());
					orderPacket.setSessionId(printQueuePacket.getSessionId());
					orderPacket.setIdOfSessionUsedByPacket(printQueuePacket.getIdOfSessionUsedByPacket());
					sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_updateOrderStatus.name(), false);
				}

				// Close in house production PO After Bump
				// closeProductionOrder(header,em,httpRequest,printQueuePacket,false);
			}
		}
	}
		packet.setMerchantId(printQueuePacket.getMerchantId());
		packet.setLocationId(printQueuePacket.getLocationId());
		packet.setPrintQueueList(printQueues);
		return packet;

	}
	/*
	 * private void closePoOrder(OrderHeader header,EntityManager em) {
	 * 
	 * if(header.getPoRefrenceNumber() != 0) {
	 * 
	 * RequestOrder requestOrder = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,RequestOrder.class,
	 * header.getPoRefrenceNumber());
	 * 
	 * String queryStringStatus =
	 * "select s from OrderStatus s where s.name =? and s.locationsId=? and s.status !='D'"
	 * ; TypedQuery<OrderStatus> queryStatus = em.createQuery(queryStringStatus,
	 * OrderStatus.class).setParameter(1, "PO Received") .setParameter(2,
	 * requestOrder.getLocationId()); OrderStatus orderStatus =
	 * queryStatus.getSingleResult();
	 * 
	 * if(orderStatus.getId() != requestOrder.getStatusId() &&
	 * requestOrder.getSupplierId() == requestOrder.getLocationId()) {
	 * requestOrder.setStatusId(orderStatus.getId());
	 * 
	 * requestOrder = em.merge(requestOrder);
	 * 
	 * String queryStringDetailsStatus =
	 * "select s from OrderDetailStatus s where s.name =? and s.locationsId=? and s.status !='D'"
	 * ; TypedQuery<OrderDetailStatus> queryDetailsStatus =
	 * em.createQuery(queryStringDetailsStatus, OrderDetailStatus.class)
	 * .setParameter(1, "PO Item Received").setParameter(2,
	 * requestOrder.getLocationId()); OrderDetailStatus orderDetailStatus =
	 * queryDetailsStatus.getSingleResult();
	 * 
	 * 
	 * String queryStringItems =
	 * "select s from RequestOrderDetailItems s where s.requestId =? and s.status !='D'"
	 * ; TypedQuery<RequestOrderDetailItems> queryDetailsItems =
	 * em.createQuery(queryStringItems, RequestOrderDetailItems.class)
	 * .setParameter(1, requestOrder.getId()); List<RequestOrderDetailItems>
	 * detailItems = queryDetailsItems.getResultList();
	 * 
	 * Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class,
	 * requestOrder.getLocationId());
	 * 
	 * 
	 * int grnCount = requestOrder.getGrnCount(); for(RequestOrderDetailItems
	 * detailItems2 : detailItems) {
	 * detailItems2.setStatusId(orderDetailStatus.getId());
	 * detailItems2.setBalance(new BigDecimal(0));
	 * detailItems2.setReceivedQuantity(detailItems2.getQuantity());
	 * detailItems2 = em.merge(detailItems2);
	 * 
	 * String name = location.getName().replace("(", "").replace(")", "");
	 * String[] locationNameString = name.split(" "); String locationName = "";
	 * for (String locationNameObj : locationNameString) { locationName =
	 * locationName + locationNameObj.substring(0, 1); } String grnNumber =
	 * locationName + "-" + requestOrder.getId() + "-" + (++grnCount);
	 * 
	 * insertGoodsReceiveNotes(em, detailItems2, grnNumber, false,
	 * requestOrder.getGrnDate(), true,true,false);
	 * 
	 * } }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * } }
	 */

	/*
	 * private void closeProductionOrder(OrderHeader header,EntityManager em,
	 * HttpServletRequest httpRequest, PrintQueuePacket printQueuePacket,boolean
	 * isBump) {
	 * 
	 * if(header.getOrderTypeId() == 2) {
	 * 
	 * String queryString =
	 * "select s from OrderSourceGroup s where s.name = ? and s.locationsId = ?"
	 * ; TypedQuery<OrderSourceGroup> query = em.createQuery(queryString,
	 * OrderSourceGroup.class).setParameter(1, "In Store").setParameter(2,
	 * header.getLocationsId()); OrderSourceGroup orderSourceGroup =
	 * query.getSingleResult();
	 * 
	 * String queryStringStatus =
	 * "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'"
	 * ; TypedQuery<OrderStatus> queryStatus; if(isBump) { queryStatus =
	 * em.createQuery(queryStringStatus, OrderStatus.class).setParameter(1,
	 * "Close Production") .setParameter(2,
	 * header.getLocationsId()).setParameter(3, orderSourceGroup.getId()); }else
	 * { queryStatus = em.createQuery(queryStringStatus,
	 * OrderStatus.class).setParameter(1, "Order Placed") .setParameter(2,
	 * header.getLocationsId()).setParameter(3, orderSourceGroup.getId()); }
	 * 
	 * 
	 * OrderStatus orderStatus = queryStatus.getSingleResult();
	 * 
	 * 
	 * header.setOrderStatusId(orderStatus.getId()); OrderPacket orderPacket =
	 * new OrderPacket(); orderPacket.setOrderHeader(header);
	 * orderPacket.setClientId(printQueuePacket.getClientId());
	 * orderPacket.setLocationId(header.getLocationsId()+"");
	 * orderPacket.setMerchantId(printQueuePacket.getMerchantId());
	 * 
	 * try { updateOrderStatus(httpRequest, em, orderPacket); } catch
	 * (NirvanaXPException | IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * 
	 * } }
	 */

	List<GoodsReceiveNotes> insertGoodsReceiveNotes(EntityManager em, RequestOrderDetailItems items, String grnNumber, boolean isAllotment, String grnDate, boolean isAdminReceive,
			boolean isDirectRecieve, boolean isNeedToCloseGRN)
	{
		List<GoodsReceiveNotes> goodsReceiveNotes = new ArrayList<GoodsReceiveNotes>();

		if (items.getReceivedQuantity() == null)
		{
			items.setReceivedQuantity(new BigDecimal(0));
		}
		BigDecimal balance = new BigDecimal(0);
		if (isAdminReceive)
		{
			balance = items.getQuantity().subtract(items.getReceivedQuantity());

		}
		else
		{
			balance = items.getAllotmentQty().subtract(items.getReceivedQuantity());

		}

		GoodsReceiveNotes notes = new GoodsReceiveNotes();
		notes.setBalance(balance);
		notes.setAllotmentQty(items.getAllotmentQty());
		notes.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, items.getStatusId());
		if (detailStatus != null)
		{
			notes.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(detailStatus.getLocationsId(), em));
		}

		notes.setCreatedBy(items.getCreatedBy());
		notes.setGrnNumber(grnNumber);

		notes.setPrice(items.getPrice());
		notes.setRate(items.getUnitPurchasedPrice());
		notes.setReceivedQuantity(items.getReceivedQuantity());
		notes.setRequestOrderDetailsItemId(items.getId());
		notes.setStatus("A");
		notes.setTax(items.getTax());
		notes.setTotal(items.getTotal());
		notes.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		notes.setUpdatedBy(items.getUpdatedBy());
		notes.setDate(grnDate);
		notes.setUnitPrice(items.getUnitPrice());
		notes.setUnitPurchasedPrice(items.getUnitPurchasedPrice());
		notes.setUnitTaxRate(items.getUnitTaxRate());
		if (isAllotment)
		{
			notes.setIsAllotment(1);
		}
		if (isNeedToCloseGRN)
		{
			if (notes.getBalance() != null && notes.getBalance().compareTo((new BigDecimal(0))) == 0)
			{
				notes.setIsGRNClose(1);
			}
		}

		if (isDirectRecieve)
		{
			notes.setIsGRNClose(1);
		}
		notes.setUomName(items.getUomName());
		notes.setRequestOrderDetailItemName(items.getItemName());
		notes.setStatusId(items.getStatusId());

		notes = em.merge(notes);
		goodsReceiveNotes.add(notes);

		return goodsReceiveNotes;

	}

	/**
	 * Gets the order status by name and location.
	 *
	 * @param em
	 *            the em
	 * @param statusName
	 *            the status name
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the order status by name and location
	 */
	public OrderStatus getOrderStatusByNameAndLocation(EntityManager em, String statusName, String locationId, String orderSourceGroupId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), statusName), builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceGroupId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Update oder detail item status.
	 *
	 * @param em
	 *            the em
	 * @param httpRequest
	 *            the http request
	 * @param packet
	 *            the packet
	 * @param kdsToOrderDetailItemStatusList
	 *            the kds to order detail item status list
	 * @param isAdd
	 *            the is add
	 * @return the KDS to order detail item status packet
	 */
	public KDSToOrderDetailItemStatusPacket updateOderDetailItemStatus(EntityManager em, HttpServletRequest httpRequest, KDSToOrderDetailItemStatusPacket packet,
			List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatusList, boolean isAdd)
	{
		try
		{

			List<String> orderIds = new ArrayList<String>();
			for (KDSToOrderDetailItemStatus kdsToOrderDetailItemStatus : kdsToOrderDetailItemStatusList)
			{
				String orderDetailStatusName = getOrderDetailStatusName(em, kdsToOrderDetailItemStatus.getStatusId());
				if (orderDetailStatusName.equals("Item Displayed"))
				{
					List<PrintQueue> printQueueList = getPrintQueueByOrderDetailId(em, kdsToOrderDetailItemStatus.getOrderDetailItemId());
					List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatus1 = getkdsToODIStatusByOrderDetailId(em, kdsToOrderDetailItemStatus.getOrderDetailItemId(),
							kdsToOrderDetailItemStatus.getStatusId());
		
					if (printQueueList.size() == kdsToOrderDetailItemStatus1.size())
					{
						OrderDetailItem orderDetailItem = (OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class, kdsToOrderDetailItemStatus.getOrderDetailItemId());
						String locationsId = null;
						if (packet.getLocationId() != null && packet.getLocationId().length() > 0)
						{
							locationsId = packet.getLocationId();
						}
						OrderDetailStatus detailStatus = getOrderDetailStatusByNameAndLocation(em, "KOT Printed", locationsId);
						OrderDetailStatus oldDetailStatus = em.find(OrderDetailStatus.class, orderDetailItem.getOrderDetailStatusId());
						List<NotPrintedOrderDetailItemsToPrinter> notPrintedItems = getNotPrintedItems(em, orderDetailItem.getId());

						if (!oldDetailStatus.getName().equals("KOT Printed") && (notPrintedItems == null || notPrintedItems.size() == 0))
						{
							orderDetailItem.setOrderDetailStatusId(detailStatus.getId());
							orderDetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							orderDetailItem = em.merge(orderDetailItem);
							List<OrderDetailAttribute> OrderDetailAttributeList = getOrderDetailAttributes(em, orderDetailItem.getId());
							if (OrderDetailAttributeList != null)
							{
								for (OrderDetailAttribute attribute : OrderDetailAttributeList)
								{
									OrderDetailStatus attrOrderDetailStatus = em.find(OrderDetailStatus.class, attribute.getOrderDetailStatusId());

									if (!attrOrderDetailStatus.getName().equals("Attribute Removed"))
									{
										attribute.setOrderDetailStatusId(detailStatus.getId());
									}
									attribute.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
									attribute = em.merge(attribute);
								}
							}
							if (!orderIds.contains(orderDetailItem.getOrderHeaderId()))
							{
								orderIds.add(orderDetailItem.getOrderHeaderId());
							}

						}
					}
				}
			}
			packet.setIsOrderBumped(PrinterUtility.isOrderBumped);

			if (isAdd)
			{
				sendPacketForBroadcast(httpRequest, packet, POSNServiceOperations.OrderManagementService_addBumpUnbumpOrder.name(), true);
			}
			else
			{
				sendPacketForBroadcast(httpRequest, packet, POSNServiceOperations.OrderManagementService_updateBumpUnbumpOrder.name(), true);
			}

			return packet;
		}
		catch (NumberFormatException e)
		{
			// todo shlok need
			// return proper Exception
			logger.severe(e);
		}
		catch (NirvanaXPException e)
		{
			// todo shlok need
			// return proper Exception
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Gets the prints the queue by order detail id.
	 *
	 * @param em
	 *            the em
	 * @param orderDetailItemId
	 *            the order detail item id
	 * @return the prints the queue by order detail id
	 */
	public List<PrintQueue> getPrintQueueByOrderDetailId(EntityManager em, String orderDetailItemId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PrintQueue> criteria = builder.createQuery(PrintQueue.class);
			Root<PrintQueue> ic = criteria.from(PrintQueue.class);
			TypedQuery<PrintQueue> query = em
					.createQuery(criteria.select(ic).where(builder.equal(ic.get(PrintQueue_.status), "U"), builder.like(ic.get(PrintQueue_.orderDetailItemId), "%" + orderDetailItemId + "%")));
			return query.getResultList();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// return proper Exception
			return null;
		}
	}

	/**
	 * Gets the kds to ODI status by order detail id.
	 *
	 * @param em
	 *            the em
	 * @param orderDetailItemId
	 *            the order detail item id
	 * @param statusId
	 *            the status id
	 * @return the kds to ODI status by order detail id
	 */
	public List<KDSToOrderDetailItemStatus> getkdsToODIStatusByOrderDetailId(EntityManager em, String orderDetailItemId, int statusId)
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<KDSToOrderDetailItemStatus> criteria = builder.createQuery(KDSToOrderDetailItemStatus.class);
			Root<KDSToOrderDetailItemStatus> ic = criteria.from(KDSToOrderDetailItemStatus.class);
			TypedQuery<KDSToOrderDetailItemStatus> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(KDSToOrderDetailItemStatus_.orderDetailItemId), orderDetailItemId),
					builder.equal(ic.get(KDSToOrderDetailItemStatus_.statusId), statusId), builder.notEqual(ic.get(KDSToOrderDetailItemStatus_.status), "D")));
			return query.getResultList();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// return proper Exception
			return null;
		}

	}

	/**
	 * Gets the order detail status name.
	 *
	 * @param em
	 *            the em
	 * @param statusId
	 *            the status id
	 * @return the order detail status name
	 */
	private String getOrderDetailStatusName(EntityManager em, int statusId)
	{
		// todo need to handle null pointer while fetch the name
		OrderDetailStatus detailStatus = em.find(OrderDetailStatus.class, statusId);
		return detailStatus.getName();
	}

	/**
	 * Gets the order detail status by name and location.
	 *
	 * @param em
	 *            the em
	 * @param statusName
	 *            the status name
	 * @param locationId
	 *            the location id
	 * @return the order detail status by name and location
	 */
	public OrderDetailStatus getOrderDetailStatusByNameAndLocation(EntityManager em, String statusName, String locationId)
	{
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.name), statusName), builder.equal(r.get(OrderDetailStatus_.locationsId), locationId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			// todo shlok need
			// return proper Exception
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Gets the order detail attributes.
	 *
	 * @param em
	 *            the em
	 * @param orderDetailItemId
	 *            the order detail item id
	 * @return the order detail attributes
	 */
	public List<OrderDetailAttribute> getOrderDetailAttributes(EntityManager em, String orderDetailItemId)
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailAttribute> criteria = builder.createQuery(OrderDetailAttribute.class);
			Root<OrderDetailAttribute> ic = criteria.from(OrderDetailAttribute.class);
			TypedQuery<OrderDetailAttribute> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(OrderDetailAttribute_.orderDetailItemId), orderDetailItemId)));
			return query.getResultList();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// return proper Exception
			return null;
		}

	}

	/**
	 * Gets the not printed items.
	 *
	 * @param em
	 *            the em
	 * @param orderDetailItemId
	 *            the order detail item id
	 * @return the not printed items
	 */
	public List<NotPrintedOrderDetailItemsToPrinter> getNotPrintedItems(EntityManager em, String orderDetailItemId)
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<NotPrintedOrderDetailItemsToPrinter> criteria = builder.createQuery(NotPrintedOrderDetailItemsToPrinter.class);
			Root<NotPrintedOrderDetailItemsToPrinter> ic = criteria.from(NotPrintedOrderDetailItemsToPrinter.class);
			TypedQuery<NotPrintedOrderDetailItemsToPrinter> query = em
					.createQuery(criteria.select(ic).where(builder.equal(ic.get(NotPrintedOrderDetailItemsToPrinter_.orderDetailItemsId), orderDetailItemId)));
			return query.getResultList();
		}
		catch (NoResultException e)
		{
			// todo shlok need
			// return proper Exception
			return null;
		}

	}

	public void addUpdateRequestOrder(OrderHeader orderHeader, HttpServletRequest httpRequest, EntityManager em, List<OrderDetailItem> detailItems) throws Exception
	{

		OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, orderHeader.getOrderSourceId());
		if (orderHeader.getRequestedLocationId() != null && (orderSource.getName().equals(
				"Internal") /* || orderSource.getName().equals("External") */))
		{
			// List<RequestOrderDetailItems> orderDetailItemsList = new
			// ArrayList<RequestOrderDetailItems>();
			EntityTransaction tx = null;
			try
			{
				tx = em.getTransaction();

				String queryString = "select s from OrderSourceGroup s where s.locationsId=? and name like '%Inventory%'";
				TypedQuery<OrderSourceGroup> query = em.createQuery(queryString, OrderSourceGroup.class).setParameter(1, orderHeader.getRequestedLocationId());
				OrderSourceGroup orderSourceGroup = query.getSingleResult();

				if (orderHeader.getPoRefrenceNumber()!=null )
				{
					RequestOrder requestOrder = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,RequestOrder.class, orderHeader.getPoRefrenceNumber());

					String queryStringStatus = "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'";
					TypedQuery<OrderStatus> queryStatus = em.createQuery(queryStringStatus, OrderStatus.class).setParameter(1, "Request Cancelled")
							.setParameter(2, orderHeader.getRequestedLocationId()).setParameter(3, orderSourceGroup.getId());
					OrderStatus orderStatus = queryStatus.getSingleResult();

					if (requestOrder.getStatusId() == orderStatus.getId())
					{
						return;
					}

				}

				String queryStringStatus = "select s from OrderStatus s where s.name =? and s.locationsId=? and s.orderSourceGroupId=? and s.status !='D'";
				TypedQuery<OrderStatus> queryStatus = em.createQuery(queryStringStatus, OrderStatus.class).setParameter(1, "PO Created").setParameter(2, orderHeader.getRequestedLocationId())
						.setParameter(3, orderSourceGroup.getId());
				OrderStatus orderStatus = queryStatus.getSingleResult();

				/*
				 * String queryStringDetailsStatus =
				 * "select s from OrderDetailStatus s where s.name =? and s.locationsId=? and s.status !='D'"
				 * ; TypedQuery<OrderDetailStatus> queryDetailsStatus =
				 * em.createQuery(queryStringDetailsStatus,
				 * OrderDetailStatus.class).setParameter(1,
				 * "PO Item Requested").setParameter(2,
				 * orderHeader.getRequestedLocationId());
				 */
				OrderDetailStatus orderDetailStatus = getOrderDetailStatusByNameAndLocationId(em, "PO Item Requested", orderHeader.getRequestedLocationId());

				RequestOrder requestOrder = new RequestOrder();
				requestOrder.setId(orderHeader.getPoRefrenceNumber());
				requestOrder.setLocationId(orderHeader.getRequestedLocationId());
				requestOrder.setSupplierId(orderHeader.getLocationsId());
				requestOrder.setStatus("A");
				requestOrder.setStatusId(orderStatus.getId());
				requestOrder.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				requestOrder.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				requestOrder.setUpdatedBy(orderHeader.getCreatedBy());
				requestOrder.setCreatedBy(orderHeader.getUpdatedBy());
				requestOrder.setIsPOOrder(1);
				requestOrder.setOrderSourceGroupId(orderHeader.getOrderSourceId());
				requestOrder.setName("Internal Request");
				TimezoneTime timezoneTime = new TimezoneTime();
				String currentDateTime[] = timezoneTime.getCurrentTimeofLocation(orderHeader.getRequestedLocationId(), em);
				requestOrder.setDate(currentDateTime[0]);

				if (requestOrder.getId() != null )
				{
					requestOrder = em.merge(requestOrder);
				}
				else
				{
					em.persist(requestOrder);
				}

				requestOrder.setPurchaseOrderId(requestOrder.getId());

				requestOrder = em.merge(requestOrder);

				orderHeader.setPoRefrenceNumber(requestOrder.getId());

				orderHeader = em.merge(orderHeader);

				if (detailItems != null && detailItems.size() > 0)
				{
					for (OrderDetailItem detailItem : detailItems)
					{
						Item itemAdapter = null;
						try
						{
							String queryStringItem = "select s from Item s where s.id =?";
							TypedQuery<Item> queryItem = em.createQuery(queryStringItem, Item.class).setParameter(1, detailItem.getItemsId());
							Item itemsQ = queryItem.getSingleResult();

							queryStringItem = "select s from Item s where s.globalItemId =? and s.locationsId = ?";
							queryItem = em.createQuery(queryStringItem, Item.class).setParameter(1, itemsQ.getGlobalItemId()).setParameter(2, orderHeader.getRequestedLocationId());
							itemAdapter = queryItem.getSingleResult();
						}
						catch (Exception e)
						{
							// TODO: handle exception
							logger.severe("No Item Entity Found");
						}
						if (itemAdapter != null)
						{
							RequestOrderDetailItems rdetailItem = new RequestOrderDetailItems();

							OrderDetailStatus orderDetailStatusRecall = getOrderDetailStatusById(em, detailItem.getOrderDetailStatusId());

							if (orderDetailStatusRecall.getName().equals("Item Removed") || orderDetailStatusRecall.getName().equals("Recall"))
							{
								rdetailItem.setStatusId(getOrderDetailStatusByNameAndLocationId(em, "Item Removed", orderHeader.getRequestedLocationId()).getId());
								if (detailItem.getPoItemRefrenceNumber() != null)
								{
									rdetailItem.setId(detailItem.getPoItemRefrenceNumber());
								}
								else
								{
									rdetailItem.setId(null);

								}

							}
							else
							{
								rdetailItem.setStatusId(orderDetailStatus.getId());
								if (detailItem.getPoItemRefrenceNumber() != null)
								{
									rdetailItem.setId(detailItem.getPoItemRefrenceNumber());
								}
								else
								{
									rdetailItem.setId(null);

								}
							}

							rdetailItem.setItemName(itemAdapter.getShortName());
							rdetailItem.setQuantity(detailItem.getItemsQty());
							rdetailItem.setCreatedBy(orderHeader.getCreatedBy());
							rdetailItem.setUpdatedBy(orderHeader.getCreatedBy());
							rdetailItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							rdetailItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							rdetailItem.setStatus("A");
							rdetailItem.setRequestTo(orderHeader.getLocationsId());
							rdetailItem.setRequestId(requestOrder.getId());
							rdetailItem.setBalance(detailItem.getItemsQty());
							rdetailItem.setAllotmentQty(new BigDecimal(0));
							rdetailItem.setReceivedQuantity(new BigDecimal(0));
							rdetailItem.setItemsId(itemAdapter.getId());
							rdetailItem.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(requestOrder.getSupplierId(), em));
							rdetailItem.setPurchaseOrderId(requestOrder.getId());
							rdetailItem.setUnitPrice(itemAdapter.getPurchasingRate());
							rdetailItem.setPrice(itemAdapter.getDistributionPrice());
							rdetailItem.setTax(new BigDecimal(0));
							rdetailItem.setUnitTaxRate(new BigDecimal(0));
							rdetailItem.setTotal(new BigDecimal(0));
							if (itemAdapter.getStockUom() != null)
							{
								try
								{
									String queryStringItem = "select s from UnitOfMeasurement s where s.id =?";
									TypedQuery<UnitOfMeasurement> queryItem = em.createQuery(queryStringItem, UnitOfMeasurement.class).setParameter(1, itemAdapter.getStockUom());
									UnitOfMeasurement itemsQ = queryItem.getSingleResult();
									rdetailItem.setUomName(itemsQ.getDisplayName());
								}
								catch (Exception e)
								{
									// TODO: handle exception
									logger.severe("No result found Unit Of Measurement for itemAdapter.getStockUom() " + itemAdapter.getStockUom());
								}
							}

							rdetailItem = em.merge(rdetailItem);

							// OrderDetailItem detailItem2 =
							// (OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class,
							// detailItem.getId());
							detailItem.setPoItemRefrenceNumber(rdetailItem.getId());
							detailItem = em.merge(detailItem);

						}

					}

				}

			}
			catch (Exception e)
			{
				logger.severe(e);
			}

		}

	}

	private OrderDetailStatus getOrderDetailStatusByNameAndLocationId(EntityManager em, String name, String locationId)
	{
		String queryStringDetailsStatus = "select s from OrderDetailStatus s where s.name =? and s.locationsId=? and s.status !='D'";
		TypedQuery<OrderDetailStatus> queryDetailsStatus = em.createQuery(queryStringDetailsStatus, OrderDetailStatus.class).setParameter(1, name).setParameter(2, locationId);
		return queryDetailsStatus.getSingleResult();
	}

	private OrderDetailStatus getOrderDetailStatusById(EntityManager em, int id)
	{
		String queryStringDetailsStatus = "select s from OrderDetailStatus s where s.id=?";
		TypedQuery<OrderDetailStatus> queryDetailsStatus = em.createQuery(queryStringDetailsStatus, OrderDetailStatus.class).setParameter(1, id);
		return queryDetailsStatus.getSingleResult();
	}

	public String sendEmailForAddUpdateRequestOrder(String requestOrderId, String locationId, HttpServletRequest httpRequest) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{

			// em =
			// LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
			// sessionId);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			RequestOrder requestOrderheader = (RequestOrder) new CommonMethods().getObjectById("RequestOrder", em,RequestOrder.class, requestOrderId);

			Location supplier = (Location) new CommonMethods().getObjectById("Location", em,Location.class, requestOrderheader.getSupplierId());
			// 0 because mail sending from dine in

			try
			{
				tx = em.getTransaction();
				tx.begin();

				Location foundLocation = null;
				if (locationId != null)
				{
					String queryString = "select l from Location l where " + "l.id ='" + locationId + "' and l.locationsId = '0' and l.locationsTypeId = '1'";
					TypedQuery<Location> query = em.createQuery(queryString, Location.class);
					foundLocation = query.getSingleResult();
				}

				String fileName = foundLocation.getName() + " Purchase Order.pdf";

				String pdfData = receiptPDFFormat.createRequestOrderInvoicePDFString(em, httpRequest, requestOrderId, locationId, 2, "").toString();

				String emailBody = receiptPDFFormat.createRequestOrderInvoiceBodyString().toString();

				String emailFooter = receiptPDFFormat.createRequestOrderInvoiceFooterString(em, locationId).toString();

				pdfData = pdfData.replace("</body>", emailFooter + "</body>");

				EmailTemplateKeys.sendRequestOrderConfirmationEmailToUser(httpRequest, em, locationId, requestOrderheader.getUpdatedBy(), pdfData, EmailTemplateKeys.REQUEST_ORDER_CONFIRMATION,
						requestOrderheader.getId(), fileName, emailBody, emailFooter, supplier, 2, "");

				tx.commit();
			}
			catch (RuntimeException e)
			{
				if (tx != null && tx.isActive())
				{
					tx.rollback();
				}
				throw e;
			}
			return new JSONUtility(httpRequest).convertToJsonString(true);

		}
		catch (RuntimeException e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private String createAddress(Address address)
	{

		String add = "";
		if (address != null)
		{
			if (address.getAddress1() != null && address.getAddress1().length() > 0)
			{
				add += address.getAddress1();
			}
			if (address.getAddress2() != null && address.getAddress2().length() > 0)
			{
				add += "," + address.getAddress2();
			}
			if (address.getCity() != null && address.getCity().length() > 0)
			{
				add += "," + address.getCity();
			}
			if (address.getState() != null && address.getState().length() > 0)
			{
				add += "," + address.getState();
			}
			if (address.getZip() != null && address.getZip().length() > 0)
			{
				add += "," + address.getZip();
			}
		}
		return add;
	}

	public String getUsersToDiscountByUserIdAndDiscountCodeAndLocationIdWithoutValidation(HttpServletRequest httpRequest, EntityManager em, String userId, String dicountCode, String locationId,
			String discountId) throws IOException, InvalidSessionException
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UsersToDiscount> criteria = builder.createQuery(UsersToDiscount.class);
		Root<UsersToDiscount> r = criteria.from(UsersToDiscount.class);

		TypedQuery<UsersToDiscount> query = null;
		query = em.createQuery(
				criteria.select(r).where(builder.equal(r.get(UsersToDiscount_.usersId), userId), builder.equal(builder.lower(r.get(UsersToDiscount_.discountCode)), dicountCode.toLowerCase()),
						builder.equal(r.get(UsersToDiscount_.locationId), locationId), builder.equal(r.get(UsersToDiscount_.discountId), discountId)));
		return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
	}

	public boolean updateUsersToDiscountNumberOfTimeDiscountUsed(HttpServletRequest httpRequest, EntityManager em, String userId, String dicountCode, String locationId, String discountId)
			throws IOException, InvalidSessionException
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UsersToDiscount> criteria = builder.createQuery(UsersToDiscount.class);
		Root<UsersToDiscount> r = criteria.from(UsersToDiscount.class);

		TypedQuery<UsersToDiscount> query = null;
		query = em.createQuery(
				criteria.select(r).where(builder.equal(r.get(UsersToDiscount_.usersId), userId), builder.equal(builder.lower(r.get(UsersToDiscount_.discountCode)), dicountCode.toLowerCase()),
						builder.equal(r.get(UsersToDiscount_.locationId), locationId), builder.equal(r.get(UsersToDiscount_.discountId), discountId)));
		UsersToDiscount usersToDiscount = query.getSingleResult();

		if (usersToDiscount.getNumberOfTimeDiscountUsed() > 0)
		{
			usersToDiscount.setNumberOfTimeDiscountUsed(usersToDiscount.getNumberOfTimeDiscountUsed() - 1);
		}

		em.getTransaction().begin();
		usersToDiscount = em.merge(usersToDiscount);
		em.getTransaction().commit();

		return true;
	}

	public String getUsersToDiscountByUserIdAndDisCode(HttpServletRequest httpRequest, EntityManager em, String userId, String dicountCode, String locationId, String date, String discountId)
			throws IOException, InvalidSessionException
	{

		em.getTransaction().begin();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UsersToDiscount> criteria = builder.createQuery(UsersToDiscount.class);
		Root<UsersToDiscount> r = criteria.from(UsersToDiscount.class);

		TypedQuery<UsersToDiscount> query = null;
		if (discountId !=null)
		{
			query = em.createQuery(
					criteria.select(r).where(builder.equal(r.get(UsersToDiscount_.usersId), userId), builder.equal(builder.lower(r.get(UsersToDiscount_.discountCode)), dicountCode.toLowerCase()),
							builder.equal(r.get(UsersToDiscount_.locationId), locationId), builder.equal(r.get(UsersToDiscount_.discountId), discountId)));

		}
		else
		{

			/*
			 * String queryStringConfig =
			 * "select ci.id from discounts ci where  ci.status not in ('D', 'I') and "
			 * + "ci.coupan_code  = ? and " +
			 * " ci.is_auto_generated = 0 and ci.locations_id = ?";
			 */

			// Remove is_auto_generated = 0 condition for task #46369
			String queryStringConfig = "select ci.id from discounts ci where  ci.status not in ('D', 'I') and " + " ci.is_coupan = 1 and " + "ci.coupan_code  = ? and " + " ci.locations_id = ?";

			Object discountlist = null;
			try
			{

				discountlist = em.createNativeQuery(queryStringConfig).setParameter(1, dicountCode).setParameter(2, locationId).getSingleResult();

			}
			catch (Exception e)
			{
				// TODO: handle exception
				logger.severe(e);
			}

			if (discountlist != null)
			{
				Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, (String) discountlist);

				if (discount.getIsCoupan() == 0)
				{

					return (new NirvanaXPException(
							new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_USER, MessageConstants.ERROR_MESSAGE_DISCOUNT_CODE_NOT_VALID_FOR_USER, null))
									.toString());

				}

				query = em.createQuery(
						criteria.select(r).where(builder.equal(r.get(UsersToDiscount_.usersId), userId), builder.equal(builder.lower(r.get(UsersToDiscount_.discountCode)), dicountCode.toLowerCase()),
								builder.equal(r.get(UsersToDiscount_.locationId), locationId), builder.equal(r.get(UsersToDiscount_.discountId), (int) discountlist)));
			}
			else
			{

				query = em.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToDiscount_.usersId), userId),
						builder.equal(builder.lower(r.get(UsersToDiscount_.discountCode)), dicountCode.toLowerCase()), builder.equal(r.get(UsersToDiscount_.locationId), locationId)));
				/*
				 * return (new NirvanaXPException(new
				 * NirvanaServiceErrorResponse(MessageConstants.
				 * ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_USER,
				 * MessageConstants.
				 * ERROR_MESSAGE_DISCOUNT_CODE_NOT_VALID_FOR_USER,
				 * null)).toString());
				 */
			}

		}

		/*
		 * personCriteriaQuery.where(criteriaBuilder.like(
		 * criteriaBuilder.upper(personRoot.get(Person_.description)),
		 * "%"+filter.getDescription().toUpperCase()+"%"));
		 */
		UsersToDiscount usersToDiscount = null;
		try
		{

			usersToDiscount = query.getSingleResult();

		}
		catch (Exception e)
		{
			// TODO: handle exception
			logger.severe(e);
		}

		if (usersToDiscount == null)
		{

			/*
			 * String queryStringConfig =
			 * "select ci.id from discounts ci where  ci.status not in ('D', 'I') and "
			 * + "ci.coupan_code  = ? and " + " ci.is_auto_generated = 0 and " +
			 * " ci.effective_end_date >= ? ";
			 */

			// Remove is_auto_generated = 0 condition for task #46369
			String queryStringConfig = "select ci.id from discounts ci where  ci.status not in ('D', 'I') and " + "ci.coupan_code  = ? and " + " " + " ci.is_coupan = 1 and "
					+ " ci.effective_end_date >= ? and locations_id='" + locationId+"'";

			if (discountId !=null)
			{
				queryStringConfig = queryStringConfig + " and ci.id  =  " + discountId;
			}

			Object discountlist = null;
			try
			{

				discountlist = em.createNativeQuery(queryStringConfig).setParameter(1, dicountCode).setParameter(2, date).getSingleResult();

			}
			catch (Exception e)
			{
				// TODO: handle exception
				logger.severe(e);
			}

			if (discountlist == null)
			{

				return (new NirvanaXPException(
						new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_USER, MessageConstants.ERROR_MESSAGE_DISCOUNT_CODE_NOT_VALID_FOR_USER, null))
								.toString());
			}
			else
			{
				Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, (String) discountlist);

				if (discount.getIsAllCustomer() == 1)
				{

					if (discount.getIsCoupan() == 0)
					{

						return (new NirvanaXPException(
								new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_USER, MessageConstants.ERROR_MESSAGE_DISCOUNT_CODE_NOT_VALID_FOR_USER, null))
										.toString());

					}

					usersToDiscount = new UsersToDiscount();
					usersToDiscount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToDiscount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToDiscount.setCreatedBy(userId);
					usersToDiscount.setUpdatedBy(userId);
					usersToDiscount.setDiscountId(discount.getId());
					usersToDiscount.setNumberOfTimeDiscountUsed(0);
					usersToDiscount.setDiscountCode(dicountCode);
					usersToDiscount.setUsersId(userId);
					usersToDiscount.setLocationId(locationId);
					try {
						if(usersToDiscount.getId()==null)
						usersToDiscount.setId(new StoreForwardUtility().generateDynamicIntId(em, locationId, httpRequest, "users_to_discount"));
					} catch (Exception e) {
						logger.severe(e);
					}	
					usersToDiscount = em.merge(usersToDiscount);

					DiscountsType discountType = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,DiscountsType.class, discount.getDiscountsTypeId());
					usersToDiscount.setDiscountType(discountType);

					usersToDiscount.setDiscount(discount);
				}
				else
				{

				
					return (new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_ALL_CUSTOMER,
							MessageConstants.ERROR_MESSAGE_CODE_DISCOUNT_CODE_NOT_VALID_FOR_ALL_CUSTOMER, null)).toString());

				}

			}

		}
		else
		{

			Discount discount = (Discount) new CommonMethods().getObjectById("Discount", em,Discount.class, usersToDiscount.getDiscountId());

			if (discount.getIsCoupan() == 0)
			{

				return (new NirvanaXPException(
						new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DISCOUNT_CODE_NOT_VALID_FOR_USER, MessageConstants.ERROR_MESSAGE_DISCOUNT_CODE_NOT_VALID_FOR_USER, null))
								.toString());

			}

			DiscountsType discountType = (DiscountsType) new CommonMethods().getObjectById("DiscountsType", em,DiscountsType.class, discount.getDiscountsTypeId());
			usersToDiscount.setDiscountType(discountType);
			usersToDiscount.setDiscount(discount);

		
			if (discount.getNumberOfTimeDiscountUsed() != -1)
			{
				if (!(usersToDiscount.getNumberOfTimeDiscountUsed() < discount.getNumberOfTimeDiscountUsed()))
				{
					return (new NirvanaXPException(
							new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DISCOUNT_CODE_ALREADY_USED_BY_USER, MessageConstants.ERROR_MESSAGE_DISCOUNT_CODE_ALREADY_USED_BY_USER, null))
									.toString());
				}
			}

		}

		em.getTransaction().commit();
		return new JSONUtility(httpRequest).convertToJsonString(usersToDiscount);

	}

	public OrderHeader checkTransfer(HttpServletRequest httpRequest, EntityManager em, OrderTransferPacket orderPacket)
			throws NumberFormatException, NirvanaXPException, JsonGenerationException, JsonMappingException, IOException

	{
		OrderHeader order = new OrderManagementServiceBean().getOrderById(em, orderPacket.getOrderId());
		order.setSessionKey(orderPacket.getIdOfSessionUsedByPacket());

		OrderHeader orderHeader = new OrderManagementServiceBean().checkTransfer(httpRequest, em, order, orderPacket.getLocationId().trim(), orderPacket);
		if (orderHeader != null)
		{
			orderHeader = new OrderManagementServiceBean().getOrderById(em, orderHeader.getId());
		}
		else
		{
			orderHeader = order;
		}

		OrderHeader orderHeaderForPush = getOrderHeaderWithMinimunRequiredDetails(orderHeader);
		orderPacket.setOrderHeader(orderHeaderForPush);
		orderPacket.setLocationId("" + orderHeader.getLocationsId());
		sendPacketForBroadcast(httpRequest, orderPacket, POSNServiceOperations.OrderManagementService_update.name(), false);

		return orderHeader;

	}

	public StorageType addUpdateStorageType(EntityManager em, StorageType storageType)
	{
		try
		{
			if (storageType.getId() == null)
			{
				storageType.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				storageType.setId(new StoreForwardUtility().generateUUID());
			}
			else
			{
				StorageType storageType2 = (StorageType) new CommonMethods().getObjectById("StorageType", em,StorageType.class, storageType.getId());
				if(storageType2!=null){
				storageType.setCreated(storageType2.getCreated());
				}

			}
			storageType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			storageType = em.merge(storageType);
			return storageType;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	public StorageType deleteStorageType(EntityManager em, StorageType storageType)
	{
		try
		{
			storageType = (StorageType) new CommonMethods().getObjectById("StorageType", em,StorageType.class, storageType.getId());
			storageType.setStatus("D");
			storageType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			storageType = em.merge(storageType);
			return storageType;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;
	}

	public List<StorageType> getAllStorageType(EntityManager em)
	{
		try
		{
			String queryString = "select st from StorageType st where st.status != 'D'";
			TypedQuery<StorageType> query = em.createQuery(queryString, StorageType.class);
			List<StorageType> resultSet = query.getResultList();
			return resultSet;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * public OrderHeader addUpdateOrderDetailItemComment(HttpServletRequest
	 * httpRequest,EntityManager em, OrderDetailItemPacket orderPacket) throws
	 * NirvanaXPException, JsonGenerationException,JsonMappingException,
	 * IOException {
	 * 
	 * OrderDetailItem detailItem =(OrderDetailItem) new CommonMethods().getObjectById("OrderDetailItem", em,OrderDetailItem.class,
	 * orderPacket.getOrderDetailItemId());
	 * detailItem.setComment(orderPacket.getComment());
	 * detailItem=em.merge(detailItem); OrderHeader orderHeader = new
	 * OrderManagementServiceBean().getOrderById(em,
	 * detailItem.getOrderHeaderId()); OrderHeader orderHeaderForPush =
	 * getOrderHeaderWithMinimunRequiredDetails(orderHeader);
	 * orderPacket.setOrderHeader(orderHeaderForPush);
	 * sendPacketForBroadcast(httpRequest,orderPacket,
	 * POSNServiceOperations.OrderManagementService_update.name(), false);
	 * return orderHeader; }
	 */

	public UsersToDiscount updateUserToDiscount(UsersToDiscount discount, EntityManager em,String locationId,HttpServletRequest request) throws Exception
	{
		if (discount.getId() == null)
		{
			discount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			discount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			discount.setId(new StoreForwardUtility().generateDynamicIntId(em, locationId, request, "users_to_discount"));	
			discount = em.merge(discount);
		}
		else
		{

			UsersToDiscount toDiscount = em.find(UsersToDiscount.class, discount.getId());
			discount.setCreated(toDiscount.getCreated());
			discount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			discount = em.merge(discount);
		}

		return discount;
	}

}
