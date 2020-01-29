/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
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
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
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

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
/*import com.nirvanaxp.common.utils.EncryptionDecryption;*/
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.email.ReceiptPDFFormat;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.TipSavedPacket;
import com.nirvanaxp.services.jaxrs.packets.UsersToDiscountPacket;
import com.nirvanaxp.services.util.email.EmailHelper;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute;
import com.nirvanaxp.types.entities.orders.OrderDetailAttribute_;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderDetailItem_;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail_;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToDiscount;
import com.nirvanaxp.types.entity.snssms.SmsConfig;
import com.nirvanaxp.user.utility.UserManagementServiceBean;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomerService.
 */
@WebListener
@Path("/CustomerService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CustomerService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The Constant logger. */
	private final static NirvanaLogger logger = new NirvanaLogger(CustomerService.class.getName());

	/** The Constant rowCount. */
	private final static int rowCount = 83;

	/** The Constant rowShippingCount. */
	private final static int rowShippingCount = 90;

	/** The Constant rowBillingCount. */
	private final static int rowBillingCount = 103;

	/** The Constant rowShippingIdCount. */
	private final static int rowShippingIdCount = 7;

	/** The Constant rowBillingIdCount. */
	private final static int rowBillingIdCount = 8;

	/**
	 * Gets the order by id.
	 *
	 * @param id
	 *            the id
	 * @return the order by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderById/{id}")
	public String getOrderById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderById(em, id);
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order by id with user.
	 *
	 * @param id
	 *            the id
	 * @return the order by id with user
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderByIdWithUser/{id}")
	public String getOrderByIdWithUser(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderPacket orderPacket = new OrderManagementServiceBean().getOrderByIdWithUser(em, id);
			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order by reservation id.
	 *
	 * @param reservationId
	 *            the reservation id
	 * @param locationId
	 *            the location id
	 * @return the order by reservation id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderByReservationId/{reservationId}/{locationId}")
	public String getOrderByReservationId(@PathParam("reservationId") int reservationId, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderByReservationId(em, reservationId, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order status history by order id.
	 *
	 * @param orderId
	 *            the order id
	 * @return the order status history by order id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderStatusHistoryByOrderId/{orderId}/")
	public String getOrderStatusHistoryByOrderId(@PathParam("orderId") String orderId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			List<OrderWithStatusHistory> orderWithStatusHistories = new OrderManagementServiceBean().getOrderStatusHistory(em, orderId);

			return new JSONUtility(httpRequest).convertToJsonString(orderWithStatusHistories);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all online order status.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all online order status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOnlineOrderStatus/{locationId}/")
	public String getAllOnlineOrderStatus(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		List<OrderStatus> orderStatusList = new ArrayList<OrderStatus>();
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			try
			{
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
				Root<OrderSource> orderSource = criteria.from(OrderSource.class);
				TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.locationsId), locationId),
						builder.equal(orderSource.get(OrderSource_.name), "Web")));
				OrderSource orderSource1 = query.getSingleResult();

				CriteriaBuilder builder1 = em.getCriteriaBuilder();
				CriteriaQuery<OrderStatus> criteria1 = builder1.createQuery(OrderStatus.class);
				Root<OrderStatus> orderStatus = criteria1.from(OrderStatus.class);
				TypedQuery<OrderStatus> queryStatus = em.createQuery(criteria1.select(orderStatus).where(builder1.equal(orderStatus.get(OrderStatus_.locationsId), locationId),
						builder1.equal(orderStatus.get(OrderStatus_.orderSourceGroupId), orderSource1.getOrderSourceGroupId())));
				orderStatusList = queryStatus.getResultList();

			}
			catch (NoResultException e)
			{
				// TODO Auto-generated catch block
				logger.severe("No Result Found for OrderSource for locationId  " + locationId);
			}

			return new JSONUtility(httpRequest).convertToJsonString(orderStatusList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the orders by user id and location id.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @return the orders by user id and location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrdersByUserIdAndLocationId/{userId}/{locationId}")
	public String getOrdersByUserIdAndLocationId(@PathParam("userId") String userId, @PathParam("locationId") String locationId)  
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getOrderByLocationIdAndUserId(?,?) ").setParameter(1, locationId).setParameter(2, userId).getResultList();

			for (Object[] objRow : resultList)
			{

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
				User user = new User();
				if (objRow[rowCount] != null)
					user = user.getUserByResultSetForOrder(objRow, user, rowCount);
				if (user.getId()!=null )
				{
					orderWithUsers.setUser(user);
				}
				// orderHeader.setOrderDetailItems(getOrderDetailsItemForOrderId(em,
				// orderHeader.getId()));
				orderHeader.setOrderDetailItems(new OrderManagementServiceBean().getCustomerOrderDetailsItemForOrderId(em, orderHeader.getId(), locationId));
				orderHeader.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, orderHeader.getId()));

				orderWithUsers.setOrderHeader(orderHeader);
				orderWithUsersList.add(orderWithUsers);

			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		}catch (Exception e) {
			e.printStackTrace();
			logger.severe(e);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return auth_token;
	}

	/**
	 * Gets the orders by user id and location id and paid status.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @return the orders by user id and location id and paid status
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrdersByUserIdAndLocationIdAndPaidStatus/{userId}/{locationId}")
	public String getOrdersByUserIdAndLocationIdAndPaidStatus(@PathParam("userId") String userId, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getOrdersByUserIdAndLocationIdAndPaidStatus(?,?) ").setParameter(1, locationId).setParameter(2, userId).getResultList();

			for (Object[] objRow : resultList)
			{

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
				User user = new User();
				if (objRow[rowCount] != null)
					user = user.getUserByResultSetForOrder(objRow, user, rowCount);
				if (user.getId()!=null )
				{
					orderWithUsers.setUser(user);
				}
				// orderHeader.setOrderDetailItems(getOrderDetailsItemForOrderId(em,
				// orderHeader.getId()));
				orderHeader.setOrderDetailItems(new OrderManagementServiceBean().getCustomerOrderDetailsItemForOrderId(em, orderHeader.getId(), locationId));
				orderHeader.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, orderHeader.getId()));

				orderWithUsers.setOrderHeader(orderHeader);
				orderWithUsersList.add(orderWithUsers);

			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
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
	private List<OrderPaymentDetail> getOrderPaymentDetailForOrderId(EntityManager em, String orderHeaderId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderPaymentDetail> criteria = builder.createQuery(OrderPaymentDetail.class);
		Root<OrderPaymentDetail> orderPaymentDetail = criteria.from(OrderPaymentDetail.class);
		TypedQuery<OrderPaymentDetail> query = em.createQuery(criteria.select(orderPaymentDetail).where(builder.equal(orderPaymentDetail.get(OrderPaymentDetail_.orderHeaderId), orderHeaderId)));
		List<OrderPaymentDetail> orderDetailItemsList = query.getResultList();

		return orderDetailItemsList;

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
	private List<OrderDetailItem> getOrderDetailsItemForOrderId(EntityManager em, String orderHeaderId)
	{
		String statusName = "'Item Removed','Recall','Cancel Item','Void Item'";
		String queryString = "select id from order_detail_status os where os.name in (" + statusName + ")";

		Query queryStatus = em.createNativeQuery(queryString);

		List<Integer> orderStatus = (List<Integer>) queryStatus.getResultList();
		String orderStatusIds = orderStatus.toString();
		orderStatusIds = orderStatusIds.replace("[", "").replace("]", "");

		String queryS = "select os from OrderDetailItem os where os.orderHeaderId = " + orderHeaderId + " and os.orderDetailStatusId NOT IN (" + orderStatusIds + ")";

		TypedQuery<OrderDetailItem> query = em.createQuery(queryS, OrderDetailItem.class);
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();

		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList)
		{
			orderDetailItemObj.setOrderDetailAttributes(getOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId()));
		}
		return orderDetailItemsList;

	}

	/**
	 * Gets the order detail attribute for order detail item id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @return the order detail attribute for order detail item id
	 */
	private List<OrderDetailAttribute> getOrderDetailAttributeForOrderDetailItemId(EntityManager em, String orderHeaderId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderDetailAttribute> criteria = builder.createQuery(OrderDetailAttribute.class);
		Root<OrderDetailAttribute> orderDetailAttribute = criteria.from(OrderDetailAttribute.class);
		TypedQuery<OrderDetailAttribute> query = em.createQuery(criteria.select(orderDetailAttribute).where(
				builder.equal(orderDetailAttribute.get(OrderDetailAttribute_.orderDetailItemId), orderHeaderId)));
		return query.getResultList();

	}

	/**
	 * Gets the all open orders with user for location.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all open orders with user for location
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOpenOrdersWithUserForLocation/{locationId}")
	public String getAllOpenOrdersWithUserForLocation(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllOpenOrdersWithUserForLocation(?) ").setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{

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
				User user = new User();
				if (objRow[rowCount] != null)
					user = user.getUserByResultSetForOrder(objRow, user, rowCount);
				if (user.getId()!=null )
				{
					orderWithUsers.setUser(user);
				}

				orderWithUsers.setOrderHeader(orderHeader);
				orderWithUsersList.add(orderWithUsers);

			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the order to card.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderToCard")
	public String addOrderToCard(OrderPacket orderPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader("auth-token");
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket",httpRequest);
			int needToSendSNS=orderPacket.getLocalServerURL();
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			PostPacket packet = new CommonMethods().getPOSNPartner(httpRequest, orderPacket, auth_token);
			orderPacket.setMerchantId(packet.getMerchantId());
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().addOrderToCard(httpRequest, auth_token, em, orderPacket, true);
			orderPacket.setOrderHeader(orderHeader);
			if(needToSendSNS!=1){
			sendSNSByNumber(em,orderHeader.getUsersId(),orderHeader.getOrderStatusId(),orderPacket.getLocationId(),
					orderHeader.getBalanceDue(), orderHeader.getOrderNumber(), orderHeader.getAmountPaid(), orderHeader, auth_token
					,orderPacket.getMerchantId(), orderPacket.getClientId(),false,null,needToSendSNS); 
			}
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(), Integer.parseInt(orderPacket.getMerchantId()));
					
			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
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

	/**
	 * Update order payment.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/updateOrderPayment")
	public String updateOrderPayment(OrderPacket orderPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket",httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			PostPacket packet = new CommonMethods().getPOSNPartner(httpRequest, orderPacket, auth_token);
			orderPacket.setMerchantId(packet.getMerchantId());
			tx = em.getTransaction();
			tx.begin();

			boolean isDuplicatePacketCheck = false;
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPayment(httpRequest, em, orderPacket, isDuplicatePacketCheck, auth_token, true);

			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(), Integer.parseInt(orderPacket.getMerchantId()));
					
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
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

	/**
	 * Insert order for take out.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addOrderForTakeOut")
	public String insertOrderForTakeOut(OrderPacket orderPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		String json = null;
		try
		{
			 
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket",httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			PostPacket packet = new CommonMethods().getPOSNPartner(httpRequest, orderPacket, auth_token);
			orderPacket.setMerchantId(packet.getMerchantId());
			tx = em.getTransaction();
			tx.begin();

			OrderHeader orderHeader = new OrderServiceForPost().insertOrder(httpRequest, auth_token, em, orderPacket, true);
			orderPacket.setOrderHeader(orderHeader);
			tx.commit();

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(), Integer.parseInt(orderPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(orderPacket);
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

	/**
	 * Update order status to paid.
	 *
	 * @param orderPacket
	 *            the order packet
	 * @return the string
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InvalidSessionException
	 *             the invalid session exception
	 * @throws Exception
	 *             the exception
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	@POST
	@Path("/updateOrderStatusToPaid")
	public String updateOrderStatusToPaid(OrderPacket orderPacket) throws FileNotFoundException, InvalidSessionException, Exception, NirvanaXPException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket",httpRequest);
			int needToSendSNS=orderPacket.getLocalServerURL();
			
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			PostPacket packet = new CommonMethods().getPOSNPartner(httpRequest, orderPacket, auth_token);
			orderPacket.setMerchantId(packet.getMerchantId());
			tx = em.getTransaction();
			tx.begin();
			
			List<User> users = null;
			// sending mail to all user according to their role
			// in below code we are searching for user according to role and
			// their function and after that we are sending eod mail to all
			// the users
			users = new EmailHelper().sendEmailByFunctionByLocationIdWithQuery(em,orderPacket.getLocationId(),"Order Confirmation Email");
		
			String ccArray = "";
			
			if(users != null && users.size() > 0)
			{
				for(User user : users)
				{
					if(user.getEmail() != null && !user.getEmail().isEmpty())
					{
						if(ccArray.length() != 0 )
						{
							ccArray = ccArray + ","+ user.getEmail();	
						}else
						{
							ccArray = ccArray + user.getEmail();
						}	
					}
					
					
				}
			}
			
			
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderStatusToPaid(httpRequest, em, orderPacket,ccArray);
			
			users = new EmailHelper().sendEmailByFunctionByLocationIdWithQuery(em,orderPacket.getLocationId(),"Order Confirmation SMS");
			if (users != null && users.size() > 0)
			{
				// sendding mail to all the user
			
				for (User user : users)
				{
					synchronized (user)
					{
						if (user.getPhone() != null && !user.getPhone().isEmpty())
						{
						
							sendSNSByNumber(em,orderHeader.getUsersId(),orderHeader.getOrderStatusId(),orderPacket.getLocationId(),
									orderHeader.getBalanceDue(), orderHeader.getOrderNumber(),
									orderHeader.getAmountPaid(), orderHeader, auth_token,
									orderPacket.getMerchantId(), orderPacket.getClientId(),true,user,needToSendSNS); 
						}
					}

				}

			}
			
			sendSNSByNumber(em,orderHeader.getUsersId(),orderHeader.getOrderStatusId(),orderPacket.getLocationId(),
					orderHeader.getBalanceDue(), orderHeader.getOrderNumber(),orderHeader.getAmountPaid(),
					orderHeader, auth_token,orderPacket.getMerchantId(), orderPacket.getClientId(),false,null,needToSendSNS); 
			
			tx.commit();

			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(), Integer.parseInt(orderPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
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

	/**
	 * Gets the all paid order by email or phone.
	 *
	 * @param userId
	 *            the user id
	 * @param locationId
	 *            the location id
	 * @return the all paid order by email or phone
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllPaidOrderByEmailOrPhone/{userId}/{locationId}")
	public String getAllPaidOrderByEmailOrPhone(@PathParam("userId") String userId, @PathParam("locationId") String locationId) throws Exception
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			List<OrderWithUsers> orderWithUsersList = new ArrayList<OrderWithUsers>();
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery("call getAllPaidOrderByEmailOrPhone(?,?) ").setParameter(1, locationId).setParameter(2, userId).getResultList();

			for (Object[] objRow : resultList)
			{

				// if this has primary key not 0
				OrderWithUsers orderWithUsers = new OrderWithUsers();
				OrderHeader orderHeader = new OrderHeader();

				// ------------------ Add address object for address shipping id
				// and address billing id ---
				Address addressShipping = new Address();
				Address addressBilling = new Address();
				//addressShipping.setAddressByResultSet(objRow, 84);
				addressShipping.setAddressByResultSet(objRow, rowShippingCount);
				addressBilling.setAddressByResultSet(objRow, rowBillingCount);

				objRow[rowShippingIdCount] = addressShipping;
				objRow[rowBillingIdCount] = addressBilling;

				orderHeader.setOrderHeaderByResultSet(objRow, rowCount + 7);
				User user = new User();
				if (objRow[rowCount] != null)
					user = user.getUserByResultSetForOrder(objRow, user, rowCount);
				if (user.getId()!=null  )
				{
					orderWithUsers.setUser(user);
				}
				orderHeader.setOrderDetailItems(getOrderDetailsItemForOrderId(em, orderHeader.getId()));
				orderHeader.setOrderPaymentDetails(getOrderPaymentDetailForOrderId(em, orderHeader.getId()));

				orderWithUsers.setOrderHeader(orderHeader);
				orderWithUsersList.add(orderWithUsers);

			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(orderWithUsersList);
		}catch (Exception e) {
			e.printStackTrace();
			logger.severe(e);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.INirvanaService#isAlive()
	 */
	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
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
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/sendEmailForOrder/{orderId}/{userId}/{locationId}/{emailAddress}")
	public String sendEmailForOrder(@PathParam(value = "orderId") String orderId, @PathParam(value = "userId") String userId, @PathParam(value = "locationId") String locationId,
			@PathParam(value = "emailAddress") String emailAddress) throws Exception
	{
		EntityManager localEM = null;
		EntityManager globalEM = null;
		EntityTransaction tx = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

			// insert into order history
			ReceiptPDFFormat receiptPDFFormat = new ReceiptPDFFormat();
			OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", localEM,OrderHeader.class, orderId);
			// 0 because mail sending from dine in

			tx = localEM.getTransaction();
			tx.begin();

			String data = receiptPDFFormat.createReceiptPDFString(localEM, httpRequest, orderId, 1, false,false).toString();

			EmailTemplateKeys.sendOrderConfirmationEmailToCustomer(httpRequest, localEM, locationId, header.getUsersId(), header.getUpdatedBy(), data, EmailTemplateKeys.ORDER_CONFIRMATION,
					header.getOrderNumber(), emailAddress);
			// change by vaibhav,suggested by Kris, email-date- Dec 03, 2015
			// 12:57 am
			if (header.getUsersId()!=null )
			{
				User user = new OrderService().createNewUserBeforeSendingEmail(localEM, globalEM, emailAddress, header.getCreatedBy(), header.getUpdatedBy(), locationId,null);
				header.setUsersId(user.getId());
				localEM.merge(header);
			}
			else
			{
				User user = null;
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, localEM, header.getReservationsId());
				// for takeout and delivery we do not have reservations
				if (reservation != null)
				{
					UserManagementServiceBean userManagementServiceBean = new UserManagementServiceBean(httpRequest, localEM);
					try
					{
						user = userManagementServiceBean.getUserByEmail(emailAddress);
					}
					catch (Exception e)
					{

						logger.severe(httpRequest, "User not present in database so need to create user : " + emailAddress);
					}

					try
					{
						if (user == null)
						{
							user = new OrderService().createNewUserBeforeSendingEmail(localEM, globalEM, emailAddress, header.getCreatedBy(), header.getUpdatedBy(), locationId,null);
						}
						reservation.setEmail(emailAddress);
						localEM.merge(reservation);
					}
					catch (Exception e)
					{

						// todo shlok need
						// handel proper exception

						logger.severe(e);
					}
				}

			}
			tx.commit();
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
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	/**
	 * Gets the order by id and update date.
	 *
	 * @param id
	 *            the id
	 * @param updatedDate
	 *            the updated date
	 * @return the order by id and update date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderByIdAndUpdateDate/{id}/{updatedDate}")
	public String getOrderByIdAndUpdateDate(@PathParam("id") String id, @PathParam("updatedDate") String updatedDate) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderByIdAndUpdateDate(em, id, updatedDate);
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order by id and location id.
	 *
	 * @param id
	 *            the id
	 * @param locationId
	 *            the location id
	 * @return the order by id and location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderByIdAndLocationId/{id}/{locationId}")
	public String getOrderByIdAndLocationId(@PathParam("id") String id, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderById(em, id);
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getUsersToDiscountByUserIdAndDiscountCodeAndLocationId/{userId}/{dicountCode}/{locationId}/{date}")
	public String getUsersToDiscountByUserIdAndDiscountCodeAndLocationId(@PathParam("userId") String userId,
			@PathParam("dicountCode") String dicountCode,
			@PathParam("locationId") String locationId,
			@PathParam("date") String date) throws IOException, InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			return new OrderServiceForPost().getUsersToDiscountByUserIdAndDisCode(httpRequest,em, userId, dicountCode,locationId,date,null);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	public void sendSNSByNumber(EntityManager em, String userId, String statusId,
			String locationId, BigDecimal balDue,String orderNumber,
			BigDecimal amountPaid, OrderHeader order, String refNumber,
			String merchantId, String clientId, boolean isOrderConfirm,User users,int needToSendSNS) {

		if (userId!=null && needToSendSNS!=1) {
			User user = (User) new CommonMethods().getObjectById("User", em,User.class, userId);

			// em.getTransaction().begin();
			PublishResult result = null;
			try {

				String message = getMessageBodyToSend(em, user, statusId,
						locationId, balDue, orderNumber, amountPaid, order, refNumber,
						merchantId, clientId,isOrderConfirm, users);

				if (message != null) {
					AmazonSNS snsClient = AmazonSNSClientBuilder
							.defaultClient();
					String phoneNumber;
					if(isOrderConfirm && users!= null)
					{
						phoneNumber = users.getPhone();
						
					}else
					{
						phoneNumber = user.getPhone();	
					}
					
					// snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
					Map<String, MessageAttributeValue> smsAttributes = new java.util.HashMap<String, MessageAttributeValue>();

					smsAttributes.put(
							"AWS.SNS.SMS.SMSType",
							new MessageAttributeValue().withStringValue(
									"Transactional") // Sets the type to
														// promotional.
									.withDataType("String"));
					smsAttributes.put(
							"AWS.SNS.SMS.SenderID",
							new MessageAttributeValue().withStringValue(
									"mySenderID") // The sender ID shown on the
													// device.
									.withDataType("String"));

					result = snsClient.publish(new PublishRequest()
							.withMessage(message).withPhoneNumber(phoneNumber)
							.withMessageAttributes(smsAttributes));
					System.out.println(result); // Prints the message ID.
				}

			} catch (RuntimeException e) {

				logger.severe("" + e);
			}

			// em.getTransaction().commit();
		}

	}

	@SuppressWarnings("unused")
	private String getMessageBodyToSend(EntityManager em, User user,
			String statusId, String locationId, BigDecimal balDue,String orderNumber,
			BigDecimal amountPaid, OrderHeader order, String refNumber,
			String merchantId, String clientId, boolean isOrderConfirm,User users) {

		String retrnMsg = null;
		OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, statusId);
		if (orderStatus.getIsSendSms() == 1 || isOrderConfirm) {

			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;

			SMSTemplate snsSmsTemplate = null;
			String queryStringT;
			if(!isOrderConfirm)
			{
				queryStringT = "select ci from SMSTemplate ci where  ci.status not in ('I','D') "
						+ " and ci.id =  " + orderStatus.getTemplateId();
				
	
			}else
			{
				queryStringT = "select ci from SMSTemplate ci where  ci.status not in ('I','D') "
						+ " and ci.templateName like 'Order Confirmation SMS' and ci.locationId = " +locationId;
			}
			
			TypedQuery<SMSTemplate> queryT = em.createQuery(queryStringT,
					SMSTemplate.class);
			snsSmsTemplate = queryT.getSingleResult();
			
			String queryStringConfig = "select ci from SmsConfig ci where  ci.status !='D'and "
						+ "ci.gatewayName  = 'AWS SMS' ";	
			
			TypedQuery<SmsConfig> query = em.createQuery(queryStringConfig,
					SmsConfig.class);
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
				retrnMsg = retrnMsg.replace("<BusinessName>",
						location.getName());
				retrnMsg = retrnMsg.replace("<OrderNumber>", orderNumber + "");
				retrnMsg = retrnMsg.replace("<AmountPaid>", amountPaid + "");

				
				
				try {
					OrderSource orderSource = getOrderSourceById(order.getOrderSourceId(), em);
					OrderSourceGroup orderSourceGroup = getOrderSourceGroupById(order.getOrderSourceId(), em);
					
					retrnMsg = retrnMsg.replace("<OrderSourceGroup>", orderSourceGroup.getDisplayName());
					retrnMsg = retrnMsg.replace("<OrderSource>",  orderSource.getDisplayName());
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					logger.severe(e1);
				}
				
				
				
				String gName = "";
				if(user.getFirstName() != null && user.getLastName() != null)
				{
					gName = gName + user.getFirstName() + " " + user.getLastName();
				}else if(user.getFirstName() != null && user.getLastName() == null)
				{
					gName = gName + user.getFirstName();
				}
				else if(user.getFirstName() == null && user.getLastName() != null)
				{
					gName = gName + user.getLastName();
				}
				
				retrnMsg = retrnMsg.replace("<GuestName>", gName);
				retrnMsg = retrnMsg.replace("<GuestPhone>", user.getPhone());
				
				String datetime = new TimezoneTime().getDateTimeFromGMTToLocation(em, order.getScheduleDateTime(), locationId);
				SimpleDateFormat toformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat fromFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa");

				try
				{
					java.util.Date date = toformatter.parse(datetime);
					datetime = fromFormatter.format(date);
				}
				catch (ParseException e)
				{
					logger.severe("Unable to parse date", datetime, "while generating pdf receipt");
				}
				
				retrnMsg = retrnMsg.replace("<SchedDateTime>", datetime);
				
				try {

					if (retrnMsg.contains("<ClickHere>")) {
						String path = "https://"
								+ ConfigFileReader.getQRCodeServerName()
								+ "/OrderManagementServiceV6/CustomerService/updateOrderStatusForPickedUp/"
								+ order.getId() + "/" + refNumber + "/"
								+ location.getId() + "/" + merchantId + "/"
								+ clientId + "/";
						 
						String link = new CommonMethods().shortURLWithTinyURL(path);
						retrnMsg = retrnMsg.replace("<ClickHere>", " " +link + " ");
					}

				} catch (Exception e) {
					// TODO: handle exception
					logger.severe(e);
				}

				if(isOrderConfirm && users != null)
				{
					new InsertIntoHistory().insertSMSIntoHistory(em, users,
							snsSmsTemplate, retrnMsg, users.getPhone(), smsConfig,
							null, locationId);
				}else
				{
					new InsertIntoHistory().insertSMSIntoHistory(em, user,
							snsSmsTemplate, retrnMsg, user.getPhone(), smsConfig,
							null, locationId);
				}
				

			}
		}

		return retrnMsg;

	}
	
	private OrderSource getOrderSourceById(String id, EntityManager em) throws Exception
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
		Root<OrderSource> orderSource = criteria.from(OrderSource.class);
		TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.id), id)));
		return query.getSingleResult();
	}
	
	private OrderSourceGroup getOrderSourceGroupById(String id,EntityManager em) throws Exception
	{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.id), id)));
			return query.getSingleResult();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/updateOrderStatusForPickedUp/{id}/{refNumber}/{locationId}/{merchantId}/{clientId}")
	public Response updateOrderStatusForPickedUp(@PathParam("id") String id,
			@PathParam("refNumber") String refNumber,
			@PathParam("locationId") String locationId,
			@PathParam("merchantId") String merchantId,
			@PathParam("clientId") String clientId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
		 	

			em = LocalSchemaEntityManager.getInstance()
					.getEntityManagerUsingReferenceNumber(httpRequest,
							refNumber);
			OrderPacket orderPacket = new OrderPacket();
			OrderHeader orderHeader = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, id);

			OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class,
					orderHeader.getOrderSourceId());

			OrderServiceForPost orderServiceForPost = new OrderServiceForPost();
			if (orderHeader.getOrderStatusId() != orderServiceForPost
					.getOrderStatusByNameAndLocation(em, "Ready to Order",
							locationId, orderSource.getOrderSourceGroupId())
					.getId()
					&& orderHeader.getOrderStatusId() != orderServiceForPost
							.getOrderStatusByNameAndLocation(em, "Void Order",
									locationId,
									orderSource.getOrderSourceGroupId())
							.getId()
					&& orderHeader.getOrderStatusId() != orderServiceForPost
							.getOrderStatusByNameAndLocation(em,
									"Cancel Order", locationId,
									orderSource.getOrderSourceGroupId())
							.getId()) {
				tx = em.getTransaction();
				tx.begin();

				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<OrderStatus> criteria = builder
						.createQuery(OrderStatus.class);
				Root<OrderStatus> ic = criteria.from(OrderStatus.class);
				TypedQuery<OrderStatus> query = em.createQuery(criteria.select(
						ic).where(
						builder.equal(ic.get(OrderStatus_.name),
								"Presented At Curb Side"),
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
				orderPacket
						.setEchoString("updateOrderStatusForDuplicateCheck-update order status");

				orderHeader = orderServiceForPost.updateOrderStatus(
						httpRequest, em, orderPacket);

				Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
				tx.commit();

				/*return "Your Order number " + orderHeader.getOrderNumber() + " is updated to "+location.getName()+"."+
				" Please wait at curb side, some one will be there with your order shortly.";*/
				StringBuilder receipt = new StringBuilder().append("<html>")
						.append("<head>")

						.append("</head>").append("<body>")
								.append("<center>")
								.append("<div style= 'font-size:50px'> <br><br><br><br><br><br><br>"+
										"Your Order number " + orderHeader.getOrderNumber() + " <br>"
												+ "is updated to "+location.getName()+"."+
										"<br>Please wait at curb side, <br>"
										+ "some one will be there with your order shortly."
										+ "</div> </center></body></html>");
				
				
						
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
	
	@POST
	@Path("/updateUserToDiscount/")
	public String updateUserToDiscount(UsersToDiscountPacket usersToDiscountPacket) throws NumberFormatException, Exception
	{

		EntityManager em = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(usersToDiscountPacket, "UsersToDiscountPacket",httpRequest);
			String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			em.getTransaction().begin();
			OrderServiceForPost orderServiceForPost = new OrderServiceForPost();
			UsersToDiscount discount = orderServiceForPost.updateUserToDiscount(usersToDiscountPacket.getUsersToDiscount(), em, usersToDiscountPacket.getLocationId(),httpRequest);
		
			em.getTransaction().commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, usersToDiscountPacket.getLocationId(), Integer.parseInt(usersToDiscountPacket.getMerchantId()));
						
			return new JSONUtility(httpRequest).convertToJsonString(discount);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/checkInventoryForOrder/{orderId}/{locationId}")
	public String checkInventoryForOrder(@PathParam("orderId") String orderId, @PathParam("locationId") String locationId) throws IOException, InvalidSessionException, NirvanaXPException
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderHeader order = new OrderManagementServiceBean().getOrderById(em, orderId);
			String needToReject=new OrderManagementServiceBean().checkInventoryForOrder(httpRequest, order, em, ""+locationId);
			if(needToReject!=null){
				
				throw (new NirvanaXPException(new NirvanaServiceErrorResponse("ORD10021",needToReject,
						needToReject)));
			 }else {
				return new JSONUtility(httpRequest).convertToJsonString(true);
			}
			
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@POST
	@Path("/updateOrderPaymentForTipSave")
	public String updateOrderPaymentForTipSave(TipSavedPacket tipSavedPacket) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(tipSavedPacket, "TipSavedPacket",httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPaymentForTipSaved(httpRequest, em, tipSavedPacket);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, tipSavedPacket.getLocationId(), Integer.parseInt(tipSavedPacket.getMerchantId()));
						
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
		}

		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@POST
	@Path("/updateOrderPaymentForDuplicateCheck")
	public String updateOrderPaymentForDuplicateCheck(OrderPacket orderPacket) throws Exception, InvalidSessionException, IOException
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		EntityTransaction tx = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(orderPacket, "OrderPacket",httpRequest);
			int needToSendSNS=orderPacket.getLocalServerURL();
			
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			tx = em.getTransaction();
			tx.begin();
			boolean isDuplicatePacketCheck = false;
			OrderHeader orderHeader = new OrderServiceForPost().updateOrderPayment(httpRequest, em, orderPacket, isDuplicatePacketCheck, null, false);

			sendSNSByNumber(em,orderHeader.getUsersId(),orderHeader.getOrderStatusId(),orderPacket.getLocationId(),orderHeader.getBalanceDue(), orderHeader.getOrderNumber(),
					orderHeader.getAmountPaid(), orderHeader, null,
					orderPacket.getMerchantId(), orderPacket.getClientId(),false,null,needToSendSNS);
			
			tx.commit();
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, orderPacket.getLocationId(), Integer.parseInt(orderPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(orderHeader);
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
}
