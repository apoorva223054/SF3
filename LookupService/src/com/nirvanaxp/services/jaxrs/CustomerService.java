/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
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

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.countries.City;
import com.nirvanaxp.global.types.entities.countries.City_;
import com.nirvanaxp.global.types.entities.countries.Countries;
import com.nirvanaxp.global.types.entities.countries.Countries_;
import com.nirvanaxp.global.types.entities.countries.State;
import com.nirvanaxp.global.types.entities.countries.State_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.helper.OrderSourceGroupToPaymentgatewayTypeHelper;
import com.nirvanaxp.services.jaxrs.helper.OrderSourceToPaymentgatewayTypeHelper;
import com.nirvanaxp.types.entities.CdoMgmt;
import com.nirvanaxp.types.entities.CdoMgmt_;
import com.nirvanaxp.types.entities.TransactionalCurrency;
import com.nirvanaxp.types.entities.business.BusinessHour;
import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.category.CategoryToDiscount;
import com.nirvanaxp.types.entities.catalog.category.CategoryToPrinter;
import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.course.Course_;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeTypeToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType_;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsCharToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToDiscount;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsToItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsToPrinter;
import com.nirvanaxp.types.entities.catalog.items.ItemsType;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.discounts.Discount_;
import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.discounts.DiscountsType_;
import com.nirvanaxp.types.entities.feedback.FeedbackField;
import com.nirvanaxp.types.entities.feedback.FeedbackField_;
import com.nirvanaxp.types.entities.feedback.FeedbackQuestion;
import com.nirvanaxp.types.entities.feedback.FeedbackQuestion_;
import com.nirvanaxp.types.entities.feedback.FeedbackType;
import com.nirvanaxp.types.entities.feedback.FeedbackType_;
import com.nirvanaxp.types.entities.feedback.Smiley;
import com.nirvanaxp.types.entities.feedback.Smiley_;
import com.nirvanaxp.types.entities.locations.LocationDetailsType;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.LocationToLocationDetails;
import com.nirvanaxp.types.entities.locations.LocationsDetail;
import com.nirvanaxp.types.entities.locations.LocationsToImages;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus;
import com.nirvanaxp.types.entities.orders.OrderDetailStatus_;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceGroup_;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToShiftSchedule;
import com.nirvanaxp.types.entities.orders.OrderSourceToShiftSchedule_;
import com.nirvanaxp.types.entities.orders.OrderSource_;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.orders.ShiftSlots;
import com.nirvanaxp.types.entities.orders.ShiftSlots_;
import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentMethodType;
import com.nirvanaxp.types.entities.payment.PaymentMethodType_;
import com.nirvanaxp.types.entities.payment.PaymentMethod_;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType_;
import com.nirvanaxp.types.entities.payment.PaymentWay;
import com.nirvanaxp.types.entities.payment.PaymentWay_;
import com.nirvanaxp.types.entities.payment.TransactionStatus;
import com.nirvanaxp.types.entities.reasons.ReasonType;
import com.nirvanaxp.types.entities.reasons.ReasonType_;
import com.nirvanaxp.types.entities.reasons.Reasons;
import com.nirvanaxp.types.entities.reasons.Reasons_;
import com.nirvanaxp.types.entities.reservation.ContactPreference;
import com.nirvanaxp.types.entities.reservation.ContactPreference_;
import com.nirvanaxp.types.entities.reservation.RequestType;
import com.nirvanaxp.types.entities.reservation.RequestType_;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule_;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus_;
import com.nirvanaxp.types.entities.reservation.ReservationsType;
import com.nirvanaxp.types.entities.reservation.ReservationsType_;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.roles.Role_;
import com.nirvanaxp.types.entities.salestax.OrderSourceToSalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax_;
import com.nirvanaxp.types.entities.time.Timezone;
import com.nirvanaxp.types.entities.time.Timezone_;

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

	/** The logger. */
	private NirvanaLogger logger = new NirvanaLogger(CustomerService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	/**
	 * Gets the all reservations status by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all reservations status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsStatusByLocationId/{locationId}")
	public String getAllReservationsStatusByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<ReservationsStatus> reservationsStatus = new ArrayList<ReservationsStatus>();
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
			Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
			TypedQuery<ReservationsStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsStatus_.locationsId), locationId),
					builder.notEqual(r.get(ReservationsStatus_.status), "D")));
			reservationsStatus = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(reservationsStatus);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all reservations type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsTypeByLocationId/{locationId}")
	public String getAllReservationsTypeByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsType> criteria = builder.createQuery(ReservationsType.class);
			Root<ReservationsType> r = criteria.from(ReservationsType.class);
			TypedQuery<ReservationsType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsType_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all request type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all request type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRequestTypeByLocationId/{locationId}")
	public String getAllRequestTypeByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<RequestType> criteria = builder.createQuery(RequestType.class);
			Root<RequestType> r = criteria.from(RequestType.class);
			TypedQuery<RequestType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(RequestType_.locationsId), locationId), builder.notEqual(r.get(RequestType_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all contact preference by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all contact preference by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllContactPreferenceByLocationId/{locationId}")
	public String getAllContactPreferenceByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ContactPreference> criteria = builder.createQuery(ContactPreference.class);
			Root<ContactPreference> r = criteria.from(ContactPreference.class);
			TypedQuery<ContactPreference> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ContactPreference_.locationsId), locationId),
					builder.notEqual(r.get(ContactPreference_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order status by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all order status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderStatusByLocationId/{locationId}")
	public String getAllOrderStatusByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all roles by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all roles by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRolesByLocationId/{locationId}")
	public String getAllRolesByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.locationsId), locationId), builder.notEqual(r.get(Role_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order detail status by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all order detail status by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderDetailStatusByLocationId/{locationId}")
	public String getAllOrderDetailStatusByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderDetailStatus> criteria = builder.createQuery(OrderDetailStatus.class);
			Root<OrderDetailStatus> r = criteria.from(OrderDetailStatus.class);
			TypedQuery<OrderDetailStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderDetailStatus_.locationsId), locationId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discounts by location id and discount type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param discoutsTypeId
	 *            the discouts type id
	 * @return the discounts by location id and discount type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsByLocationIdAndDiscountTypeId/{locationId}/{discoutsTypeId}")
	public String getDiscountsByLocationIdAndDiscountTypeId(@PathParam("locationId") String locationId, @PathParam("discoutsTypeId") int discoutsTypeId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Discount> criteria = builder.createQuery(Discount.class);
			Root<Discount> discount = criteria.from(Discount.class);
			TypedQuery<Discount> query = em.createQuery(criteria.select(discount).where(builder.equal(discount.get(Discount_.locationsId), locationId),
					builder.equal(discount.get(Discount_.discountsTypeId), discoutsTypeId), builder.notEqual(discount.get(Discount_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the discounts type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the discounts type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getDiscountsTypeByLocationId/{locationId}")
	public String getDiscountsTypeByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<DiscountsType> criteria = builder.createQuery(DiscountsType.class);
			Root<DiscountsType> discount_type = criteria.from(DiscountsType.class);
			TypedQuery<DiscountsType> query = em.createQuery(criteria.select(discount_type).where(builder.equal(discount_type.get(DiscountsType_.locationsId), locationId),
					builder.notEqual(discount_type.get(DiscountsType_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order sourceion id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all order sourceion id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderSourceGroupByLocationId/{locationId}")
	public String getAllOrderSourceionId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroup> criteria = builder.createQuery(OrderSourceGroup.class);
			Root<OrderSourceGroup> orderSourceGroup = criteria.from(OrderSourceGroup.class);
			TypedQuery<OrderSourceGroup> query = em.createQuery(criteria.select(orderSourceGroup).where(builder.equal(orderSourceGroup.get(OrderSourceGroup_.locationsId), locationId),
					builder.notEqual(orderSourceGroup.get(OrderSourceGroup_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order source by location id and order source group id.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the all order source by location id and order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderSourceByLocationIdAndOrderSourceGroupId/{locationId}/{orderSourceGroupId}")
	public String getAllOrderSourceByLocationIdAndOrderSourceGroupId(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") String orderSourceGroupId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSource> criteria = builder.createQuery(OrderSource.class);
			Root<OrderSource> orderSource = criteria.from(OrderSource.class);
			TypedQuery<OrderSource> query = em.createQuery(criteria.select(orderSource).where(builder.equal(orderSource.get(OrderSource_.locationsId), locationId),
					builder.equal(orderSource.get(OrderSource_.orderSourceGroupId), orderSourceGroupId), builder.notEqual(orderSource.get(OrderSource_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all order status by location id and order source group id.
	 *
	 * @param locationId
	 *            the location id
	 * @param orderSourceId
	 *            the order source id
	 * @return the all order status by location id and order source group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOrderStatusByLocationIdAndOrderSourceGroupId/{locationId}/{orderSourceGroupId}")
	public String getAllOrderStatusByLocationIdAndOrderSourceGroupId(@PathParam("locationId") String locationId, @PathParam("orderSourceGroupId") int orderSourceId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
			Root<OrderStatus> r = criteria.from(OrderStatus.class);
			TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.locationsId), locationId),
					builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceId), builder.notEqual(r.get(OrderStatus_.status), "D")));

			List<OrderStatus> orderStatus = query.getResultList();
			Collections.sort(orderStatus, new Comparator<OrderStatus>()
			{
				@Override
				public int compare(OrderStatus p1, OrderStatus p2)
				{
					return p1.getDisplaySequence() - p2.getDisplaySequence();
				}

			});
			return new JSONUtility(httpRequest).convertToJsonString(orderStatus);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the payment method type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodTypeByLocationId/{locationId}")
	public String getPaymentMethodTypeByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethodType> criteria = builder.createQuery(PaymentMethodType.class);
			Root<PaymentMethodType> paymentMethodType = criteria.from(PaymentMethodType.class);
			TypedQuery<PaymentMethodType> query = em.createQuery(criteria.select(paymentMethodType).where(builder.equal(paymentMethodType.get(PaymentMethodType_.locationsId), locationId),
					builder.notEqual(paymentMethodType.get(PaymentMethodType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment transaction type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the payment transaction type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentTransactionTypeByLocationId/{locationId}")
	public String getPaymentTransactionTypeByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			// removed location id condition because payment gateway type is not
			// location specific :- By Apoorva 2016-01-08
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentTransactionType> criteria = builder.createQuery(PaymentTransactionType.class);
			Root<PaymentTransactionType> paymentTransactionType = criteria.from(PaymentTransactionType.class);
			TypedQuery<PaymentTransactionType> query = em.createQuery(criteria.select(paymentTransactionType).where(builder.notEqual(paymentTransactionType.get(PaymentTransactionType_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment method by location id and payment type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param paymentMethodTypeId
	 *            the payment method type id
	 * @return the payment method by location id and payment type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentMethodByLocationIdAndPaymentTypeId/{locationId}/{paymentMethodTypeId}")
	public String getPaymentMethodByLocationIdAndPaymentTypeId(@PathParam("locationId") String locationId, @PathParam("paymentMethodTypeId") int paymentMethodTypeId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentMethod> criteria = builder.createQuery(PaymentMethod.class);
			Root<PaymentMethod> paymentMethod = criteria.from(PaymentMethod.class);
			TypedQuery<PaymentMethod> query = em.createQuery(criteria.select(paymentMethod).where(builder.equal(paymentMethod.get(PaymentMethod_.locationsId), locationId),
					builder.equal(paymentMethod.get(PaymentMethod_.paymentMethodTypeId), paymentMethodTypeId), builder.notEqual(paymentMethod.get(PaymentMethod_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all courses by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all courses by location id
	 * @throws Exception
	 *             the exception
	 */

	@GET
	@Path("/getCourseByLocationId/{locationId}")
	public String getAllCoursesByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Course> criteria = builder.createQuery(Course.class);
			Root<Course> r = criteria.from(Course.class);
			TypedQuery<Course> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Course_.locationsId), locationId)).orderBy(builder.asc(r.get(Course_.displaySequence))));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all items attribute type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all items attribute type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllItemsAttributeTypeByLocationId/{locationId}")
	public String getAllItemsAttributeTypeByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();

			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> r = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ItemsAttributeType_.locationsId), locationId))
					.orderBy(builder.asc(r.get(ItemsAttributeType_.sortSequence))));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the items attribute type by id.
	 *
	 * @param id
	 *            the id
	 * @return the items attribute type by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getItemsAttributeTypeById/{id}")
	public String getItemsAttributeTypeById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			;
			CriteriaQuery<ItemsAttributeType> criteria = builder.createQuery(ItemsAttributeType.class);
			Root<ItemsAttributeType> ic = criteria.from(ItemsAttributeType.class);
			TypedQuery<ItemsAttributeType> query = em.createQuery(criteria.select(ic).where(builder.equal(ic.get(ItemsAttributeType_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all items attribute by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @param itemsAttributeTypeId
	 *            the items attribute type id
	 * @return the all items attribute by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllItemsAttributeByLocationIdAndItemsAttributeTypeId/{locationId}/{itemsAttributeTypeId}")
	public String getAllItemsAttributeByLocationId(@PathParam("locationId") String locationId, @PathParam("itemsAttributeTypeId") int itemsAttributeTypeId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			List<ItemsAttribute> ans = new ArrayList<ItemsAttribute>();
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			String sql = "select items_attribute0_.id as items_attribute0_id, items_attribute0_.created as items_attribute0_created,"
					+ " items_attribute0_.created_by as items_attribute0_created_by,"
					+ " items_attribute0_.is_active as items_attribute0_is_active,"
					+ " items_attribute0_.msr_price as items_attribute0_msr_price,"
					+ " items_attribute0_.multi_select as items_attribute0_multi_select, "
					+ " items_attribute0_.selling_price as items_attribute0_selling_price, "
					+ "items_attribute0_.sort_sequence as items_attribute0_sort_sequence,"
					+ " items_attribute0_.description as items_attribute0_description,"
					+ " items_attribute0_.display_name as items_attribute0_display_name,"
					+ " items_attribute0_.hex_code_values as items_attribute0_hex_code_values, items_attribute0_.image_name as items_attribute0_image_name, "
					+ " items_attribute0_.locations_id as items_attribute0_locations_id, items_attribute0_.name as items_attribute0_name, items_attribute0_.short_name as items_attribute0_short_name,  "
					+ " items_attribute0_.updated as items_attribute0_updated, items_attribute0_.updated_by as items_attribute0_updated_by, items_attribute0_.status as items_attribute0_status, "
					+ " items_attribute0_.image_name as items_attribute0_image_naame"
					+ " from items_attribute items_attribute0_ join items_attribute_type_to_items_attribute items_attribute_type_to_items_attribute0_ "
					+ " where items_attribute0_.id=items_attribute_type_to_items_attribute0_.items_attribute_id and items_attribute0_.locations_id=? " + " and items_attribute0_."
					+ "is_online_attribute=1 AND items_attribute_type_to_items_attribute0_.items_attribute_type_id= order by items_attribute0_.sort_sequence";
			// todo shlok need to handle exception in below line

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).setParameter(2, itemsAttributeTypeId).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				ItemsAttribute itemsAttribute = new ItemsAttribute();
				itemsAttribute.setId((String) objRow[0]);
				itemsAttribute.setCreated((Date) objRow[1]);
				itemsAttribute.setCreatedBy((String) objRow[2]);// (rs.getInt(3));
				if (objRow[3] != null)
					itemsAttribute.setIsActive(((Byte) objRow[3]).intValue());// (rs.getInt(4));
				if ((BigDecimal) objRow[4] != null)
				{
					itemsAttribute.setMsrPrice((BigDecimal) objRow[4]);// (rs.getBigDecimal(5));
				}
				if (objRow[5] != null)
					itemsAttribute.setMultiSelect(((Byte) objRow[5]).intValue());// (rs.getInt(6));

				if ((BigDecimal) objRow[6] != null)
				{
					itemsAttribute.setSellingPrice((BigDecimal) objRow[6]);// (rs.getBigDecimal(7));
				}
				itemsAttribute.setSortSequence((Integer) objRow[7]);// (rs.getInt(8));
				if ((String) objRow[8] != null)
				{
					itemsAttribute.setDescription((String) objRow[8]);// (rs.getString(9));
				}
				if ((String) objRow[9] != null)
				{
					itemsAttribute.setDisplayName((String) objRow[9]);// (rs.getString(10));
				}
				if ((String) objRow[10] != null)
				{
					itemsAttribute.setHexCodeValues((String) objRow[10]);// (rs.getString(11));
				}
				if ((String) objRow[11] != null)
				{
					itemsAttribute.setImageName((String) objRow[11]);// (rs.getString(12));
				}
				itemsAttribute.setLocationsId((String) objRow[12]);// (rs.getInt(13));
				if ((String) objRow[13] != null)
				{
					itemsAttribute.setName((String) objRow[13]);// (rs.getString(14));
				}
				if ((String) objRow[14] != null)
				{
					itemsAttribute.setShortName((String) objRow[14]);// (rs.getString(15));
				}
				itemsAttribute.setUpdated((Date) objRow[15]);// (rs.getLong(16));
				itemsAttribute.setUpdatedBy((String) objRow[16]);// (rs.getInt(17));
				if (objRow[17] != null)
				{
					itemsAttribute.setStatus(objRow[17].toString());// (rs.getString(18));
				}

				if ((String) objRow[18] != null)
				{
					itemsAttribute.setImageName((String) objRow[18]);// (rs.getString(19));
				}

				ans.add(itemsAttribute);

			}

			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the all countries.
	 *
	 * @return the all countries
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCountries")
	public String getAllCountries() throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Countries> criteria = builder.createQuery(Countries.class);
			Root<Countries> account = criteria.from(Countries.class);
			TypedQuery<Countries> query = em.createQuery(criteria.select(account));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all feedback type by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all feedback type by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFeedbackTypeByLocationId/{locationId}")
	public String getAllFeedbackTypeByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackType> criteria = builder.createQuery(FeedbackType.class);
			Root<FeedbackType> r = criteria.from(FeedbackType.class);
			TypedQuery<FeedbackType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FeedbackType_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the timezone by id.
	 *
	 * @param id
	 *            the id
	 * @return the timezone by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getTimezoneById/{id}")
	public String getTimezoneById(@PathParam("id") int id) throws Exception
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Timezone> criteria = builder.createQuery(Timezone.class);
			Root<Timezone> r = criteria.from(Timezone.class);
			TypedQuery<Timezone> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Timezone_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all feedback question by location id and feedback type id.
	 *
	 * @param locationId
	 *            the location id
	 * @param feedbackTypeId
	 *            the feedback type id
	 * @return the all feedback question by location id and feedback type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFeedbackQuestionByLocationIdAndFeedbackTypeId/{locationId}/{feedbackTypeId}")
	public String getAllFeedbackQuestionByLocationIdAndFeedbackTypeId(@PathParam("locationId") String locationId, @PathParam("feedbackTypeId") String feedbackTypeId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackQuestion> criteria = builder.createQuery(FeedbackQuestion.class);
			Root<FeedbackQuestion> r = criteria.from(FeedbackQuestion.class);
			TypedQuery<FeedbackQuestion> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FeedbackQuestion_.locationsId), locationId),
					builder.equal(r.get(FeedbackQuestion_.feedbackTypeId), feedbackTypeId), builder.notEqual(r.get(FeedbackQuestion_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all reservations schedule by location id and date.
	 *
	 * @param locationId
	 *            the location id
	 * @param date
	 *            the date
	 * @return the all reservations schedule by location id and date
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllReservationsScheduleByLocationIdAndDate/{locationId}/{date}")
	public String getAllReservationsScheduleByLocationIdAndDate(@PathParam("locationId") String locationId, @PathParam("date") String date) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSchedule> criteria = builder.createQuery(ReservationsSchedule.class);
			Root<ReservationsSchedule> root = criteria.from(ReservationsSchedule.class);
			Predicate predicate1 = builder.equal(root.get(ReservationsSchedule_.locationId), locationId);
			Predicate predicate2 = builder.greaterThanOrEqualTo(root.get(ReservationsSchedule_.toDate), date);
			TypedQuery<ReservationsSchedule> query = em.createQuery(criteria.select(root).where(predicate1, predicate2));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all smiley by location id and feedback type id.
	 *
	 * @param feedbackTypeId
	 *            the feedback type id
	 * @return the all smiley by location id and feedback type id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllSmileyByFeedbackTypeId/{feedbackTypeId}")
	public String getAllSmileyByLocationIdAndFeedbackTypeId(@PathParam("feedbackTypeId") String feedbackTypeId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Smiley> criteria = builder.createQuery(Smiley.class);
			Root<Smiley> r = criteria.from(Smiley.class);
			TypedQuery<Smiley> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Smiley_.feedbackTypeId), feedbackTypeId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all sales tax by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all sales tax by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllSalesTaxByLocationId/{locationId}")
	public String getAllSalesTaxByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<SalesTax> criteria = builder.createQuery(SalesTax.class);
			Root<SalesTax> r = criteria.from(SalesTax.class);
			TypedQuery<SalesTax> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(SalesTax_.locationsId), locationId), builder.notEqual(r.get(SalesTax_.status), "D")));

			List<SalesTax> salesTax = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(salesTax);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source group to paymentgateway type by order source group
	 * id.
	 *
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the order source group to paymentgateway type by order source
	 *         group id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId/{orderSourceGroupId}")
	public String getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId(@PathParam("orderSourceGroupId") String orderSourceGroupId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderSourceGroupToPaymentgatewayTypeHelper orderSourceGroupToPaymentgatewayTypeHelper = new OrderSourceGroupToPaymentgatewayTypeHelper();
			List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToPaymentgatewayType = orderSourceGroupToPaymentgatewayTypeHelper.getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId(
					httpRequest, em, orderSourceGroupId);
			if (orderSourceGroupToPaymentgatewayType.size() == 1)
			{
				return new JSONUtility(httpRequest).convertToJsonString(orderSourceGroupToPaymentgatewayType.get(0));
			}
			else if (orderSourceGroupToPaymentgatewayType.size() == 0)
			{
				throw new NonUniqueResultException(MessageConstants.ERROR_MESSAGE_ADD_PAYMENT_GATEWAY_FOR_ORDERSOURCEGROUP + orderSourceGroupId);
			}
			else
			{
				throw new NonUniqueResultException(MessageConstants.ERROR_MESSAGE_MULTIPLE_PAYMENT_GATEWAY_FOR_ORDERSOURCEGROUP + orderSourceGroupId);
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source to paymentgateway type by order source id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @return the order source to paymentgateway type by order source id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceToPaymentgatewayTypeByOrderSourceId/{orderSourceId}")
	public String getOrderSourceToPaymentgatewayTypeByOrderSourceId(@PathParam("orderSourceId") String orderSourceId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			OrderSourceToPaymentgatewayTypeHelper orderSourceToPaymentgatewayTypeHelper = new OrderSourceToPaymentgatewayTypeHelper();
			List<OrderSourceToPaymentgatewayType> orderSourceToPaymentgatewayType = orderSourceToPaymentgatewayTypeHelper.getOrderSourceToPaymentgatewayTypeByOrderSourceId(httpRequest, em,
					orderSourceId);
			if (orderSourceToPaymentgatewayType.size() == 1)
			{
				return new JSONUtility(httpRequest).convertToJsonString(orderSourceToPaymentgatewayType.get(0));
			}
			else if (orderSourceToPaymentgatewayType.size() == 0)
			{
				throw new NonUniqueResultException(MessageConstants.ERROR_MESSAGE_ADD_PAYMENT_GATEWAY_FOR_ORDERSOURCEGROUP + orderSourceId);
			}
			else
			{
				throw new NonUniqueResultException(MessageConstants.ERROR_MESSAGE_MULTIPLE_PAYMENT_GATEWAY_FOR_ORDERSOURCEGROUP + orderSourceId);
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all feedback field by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all feedback field by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllFeedbackFieldByLocationId/{locationId}")
	public String getAllFeedbackFieldByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<FeedbackField> criteria = builder.createQuery(FeedbackField.class);
			Root<FeedbackField> r = criteria.from(FeedbackField.class);
			TypedQuery<FeedbackField> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(FeedbackField_.locationsId), locationId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the printer receipt by location id and name.
	 *
	 * @param locationId
	 *            the location id
	 * @return the printer receipt by location id and name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getReservationsTypesByLocationId/{locationId}")
	public String getPrinterReceiptByLocationIdAndName(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsType> criteria = builder.createQuery(ReservationsType.class);
			Root<ReservationsType> r = criteria.from(ReservationsType.class);
			TypedQuery<ReservationsType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsType_.locationsId), locationId),
					builder.notEqual(r.get(ReservationsType_.status), "D")));
			List<ReservationsType> resultSet = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(resultSet);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the business hour by location id and reference number.
	 *
	 * @param locationId
	 *            the location id
	 * @return the business hour by location id and reference number
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getBusinessHourByLocationIdAndReferenceNumber/{LocationId}")
	public String getBusinessHourByLocationIdAndReferenceNumber(@PathParam("LocationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new LookupServiceBean().getBusinessHour(locationId, httpRequest, em));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the payment way by location id and reference number.
	 *
	 * @param locationId
	 *            the location id
	 * @return the payment way by location id and reference number
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentWayByLocationIdAndReferenceNumber/{LocationId}")
	public String getPaymentWayByLocationIdAndReferenceNumber(@PathParam("LocationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			String queryString = "select p from PaymentWay p where p.locationsId='" + locationId+"'";
			TypedQuery<PaymentWay> query = em.createQuery(queryString, PaymentWay.class);
			List<PaymentWay> resultSet = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all states by country id.
	 *
	 * @param countryId
	 *            the country id
	 * @return the all states by country id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllStatesByCountryId/{countryId}")
	public String getAllStatesByCountryId(@PathParam("countryId") int countryId) throws Exception
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<State> criteria = builder.createQuery(State.class);
			Root<State> account = criteria.from(State.class);
			TypedQuery<State> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(State_.countryId), countryId), builder.notEqual(account.get(State_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all city by country id.
	 *
	 * @param countryId
	 *            the country id
	 * @return the all city by country id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCityByCountryId/{countryId}")
	public String getAllCityByCountryId(@PathParam("countryId") int countryId) throws Exception
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<City> criteria = builder.createQuery(City.class);
			Root<City> account = criteria.from(City.class);
			TypedQuery<City> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(City_.countryId), countryId), builder.notEqual(account.get(City_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all city by state id.
	 *
	 * @param stateId
	 *            the state id
	 * @return the all city by state id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCityByStateId/{stateId}")
	public String getAllCityByStateId(@PathParam("stateId") int stateId) throws Exception
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<City> criteria = builder.createQuery(City.class);
			Root<City> account = criteria.from(City.class);
			TypedQuery<City> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(City_.stateId), stateId), builder.notEqual(account.get(City_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all nirvana XP role by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all nirvana XP role by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllNirvanaXPRoleByLocationId/{locationId}")
	public String getAllNirvanaXPRoleByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumberForNirvanaXP(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.locationsId), locationId), builder.notEqual(r.get(Role_.status), "D")));

			List<Role> role = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(role);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all role by location id.
	 *
	 * @param locationId
	 *            the location id
	 * @return the all role by location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllRoleByLocationId/{locationId}")
	public String getAllRoleByLocationId(@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.locationsId), locationId), builder.notEqual(r.get(Role_.status), "D")));

			List<Role> role = query.getResultList();

			return new JSONUtility(httpRequest).convertToJsonString(role);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order source by id.
	 *
	 * @param id
	 *            the id
	 * @return the order source by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderSourceById/{id}")
	public String getOrderSourceById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			OrderSource orderSource = new LookupServiceBean().getOrderSourceById(id, httpRequest, em);
			return new JSONUtility(httpRequest).convertToJsonString(orderSource);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Sync cdo by cdo name and updated date.
	 *
	 * @param cdoName
	 *            the cdo name
	 * @param updatedDate
	 *            the updated date
	 * @param locationId
	 *            the location id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/syncCdoByCdoNameAndUpdatedDate/{cdoName}/{updatedDate}/{locationId}")
	public String syncCdoByCdoNameAndUpdatedDate(@PathParam("cdoName") String cdoName, @PathParam("updatedDate") String updatedDate, @PathParam("locationId") String locationId) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		// By Apoorva
		// for getting non deleted records we set isLogin =1, if we want deleted
		// records then we need to pass 0
		int isLogin = 1;

		EntityManager em = null;
		if (!"countries".equals(cdoName))
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
		}

		boolean isGlobal = false;
		String logicCheckString = "";
		try
		{
			Timestamp updated = Timestamp.valueOf(updatedDate);

			if (cdoName.equals("category"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "and c.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select c from Category c where c.updated>=?  " + logicCheckString + " and c.locationsId=?";
				TypedQuery<Category> query = em.createQuery(queryString, Category.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<Category> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}else if (cdoName.equals("transactional_currency"))
			{
				String queryString = "select r from TransactionalCurrency r where r.updated>=?";
				TypedQuery<TransactionalCurrency> query = em.createQuery(queryString, TransactionalCurrency.class).setParameter(1, updated);
				List<TransactionalCurrency> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}

			else if (cdoName.equals("contact_preferences"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and c.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select c from ContactPreference c where c.updated>=? " + logicCheckString + " and c.locationsId=?";
				TypedQuery<ContactPreference> query = em.createQuery(queryString, ContactPreference.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<ContactPreference> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("course"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "and c.status != 'D'";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select c from Course c where c.updated>=? " + logicCheckString + " and c.locationsId=?";
				// todo shlok need to handle exception in below line
				TypedQuery<Course> query = em.createQuery(queryString, Course.class).setParameter(1, updated).setParameter(2, locationId);
				List<Course> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items"))
			{

				if (isLogin == 1)
				{
					logicCheckString = " and i.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select i from Item i where i.updated>=?  " + logicCheckString + " and i.locationsId=?";
				TypedQuery<Item> query = em.createQuery(queryString, Item.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<Item> resultSet = query.getResultList();
				return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(resultSet);

			}
			else if (cdoName.equals("items_attribute"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and i.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}

				String queryString = "select i from ItemsAttribute i where i.updated>=? " + logicCheckString + " and i.locationsId=?";
				TypedQuery<ItemsAttribute> query = em.createQuery(queryString, ItemsAttribute.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<ItemsAttribute> resultSet = query.getResultList();
				return new JSONUtility(httpRequest).convertToJsonString(resultSet);

			}
			else if (cdoName.equals("items_attribute_type"))
			{

				if (isLogin == 1)
				{
					logicCheckString = " and i.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select i from ItemsAttributeType i where i.updated>=?  " + logicCheckString + " and i.locationsId=?";
				TypedQuery<ItemsAttributeType> query = em.createQuery(queryString, ItemsAttributeType.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<ItemsAttributeType> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_char"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "and i.status != 'D'";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select i from ItemsChar i where i.updated>=?  " + logicCheckString + " and i.locationsId=?";
				TypedQuery<ItemsChar> query = em.createQuery(queryString, ItemsChar.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<ItemsChar> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("order_detail_status"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and o.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select o from OrderDetailStatus o where o.updated>=?  " + logicCheckString + " and o.locationsId=?";
				TypedQuery<OrderDetailStatus> query = em.createQuery(queryString, OrderDetailStatus.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<OrderDetailStatus> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("order_source"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and o.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select o from OrderSource o where o.updated>=?" + logicCheckString + " and o.locationsId=?";
				TypedQuery<OrderSource> query = em.createQuery(queryString, OrderSource.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<OrderSource> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("order_source_group"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and o.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select o from OrderSourceGroup o where o.updated>=? " + logicCheckString + " and o.locationsId=?";
				TypedQuery<OrderSourceGroup> query = em.createQuery(queryString, OrderSourceGroup.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<OrderSourceGroup> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("order_status"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and o.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select o from OrderStatus o where o.updated>=? " + logicCheckString + " and o.locationsId=?";
				TypedQuery<OrderStatus> query = em.createQuery(queryString, OrderStatus.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<OrderStatus> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("transaction_status"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and t.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select t from TransactionStatus t where t.updated>=?  " + logicCheckString;
				TypedQuery<TransactionStatus> query = em.createQuery(queryString, TransactionStatus.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<TransactionStatus> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("request_type"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and r.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from RequestType r where r.updated>=?  " + logicCheckString + " and r.locationsId=?";
				TypedQuery<RequestType> query = em.createQuery(queryString, RequestType.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<RequestType> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("reservations_status"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "and r.status != 'D'";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from ReservationsStatus r where r.updated>=?  " + logicCheckString + " and r.locationsId=?";
				TypedQuery<ReservationsStatus> query = em.createQuery(queryString, ReservationsStatus.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<ReservationsStatus> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("reservations_types"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and r.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from ReservationsType r where r.updated>=?  " + logicCheckString + " and r.locationsId=?";
				TypedQuery<ReservationsType> query = em.createQuery(queryString, ReservationsType.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<ReservationsType> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("timezone"))
			{
				String queryString = "select r from Timezone r where r.updated>=? ";

				TypedQuery<Timezone> query = em.createQuery(queryString, Timezone.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<Timezone> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_attribute_type_to_items_attribute"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and r.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from ItemsAttributeTypeToItemsAttribute r where r.updated>=? " + logicCheckString;
				TypedQuery<ItemsAttributeTypeToItemsAttribute> query = em.createQuery(queryString, ItemsAttributeTypeToItemsAttribute.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsAttributeTypeToItemsAttribute> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("category_items"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and r.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from CategoryItem r where r.updated>=?  " + logicCheckString;
				TypedQuery<CategoryItem> query = em.createQuery(queryString, CategoryItem.class).setParameter(1, updated).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<CategoryItem> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_char_to_items_attribute"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and r.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from ItemsCharToItemsAttribute r where r.updated>=? " + logicCheckString;
				TypedQuery<ItemsCharToItemsAttribute> query = em.createQuery(queryString, ItemsCharToItemsAttribute.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsCharToItemsAttribute> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_to_items_attribute_type"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "and r.status != 'D'";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from ItemsToItemsAttributeType r where r.updated>=?  " + logicCheckString;
				TypedQuery<ItemsToItemsAttributeType> query = em.createQuery(queryString, ItemsToItemsAttributeType.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsToItemsAttributeType> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_to_items_attribute"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "and r.status != 'D'";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from ItemsToItemsAttribute r where r.updated>=?  " + logicCheckString;
				TypedQuery<ItemsToItemsAttribute> query = em.createQuery(queryString, ItemsToItemsAttribute.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsToItemsAttribute> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_to_items_char"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and r.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select r from ItemsToItemsChar r where r.updated>=?  " + logicCheckString;
				TypedQuery<ItemsToItemsChar> query = em.createQuery(queryString, ItemsToItemsChar.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsToItemsChar> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}

			else if (cdoName.equals("category_to_printer"))
			{
				if (isLogin == 1)
				{
					logicCheckString = " and p.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select p from CategoryToPrinter p where p.updated>=? " + logicCheckString;
				TypedQuery<CategoryToPrinter> query = em.createQuery(queryString, CategoryToPrinter.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<CategoryToPrinter> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_to_printers"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "and p.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select p from ItemsToPrinter p where p.updated>=?  " + logicCheckString;
				TypedQuery<ItemsToPrinter> query = em.createQuery(queryString, ItemsToPrinter.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsToPrinter> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_to_discounts"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and itd.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select itd from ItemsToDiscount itd where itd.updated>=?  " + logicCheckString;
				TypedQuery<ItemsToDiscount> query = em.createQuery(queryString, ItemsToDiscount.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsToDiscount> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("category_to_discounts"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and ctd.status != 'D'   ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select ctd from CategoryToDiscount ctd where ctd.updated>=?  " + logicCheckString;
				TypedQuery<CategoryToDiscount> query = em.createQuery(queryString, CategoryToDiscount.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<CategoryToDiscount> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("items_type"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and ctd.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select ctd from ItemsType ctd where ctd.updated>=?  " + logicCheckString;
				TypedQuery<ItemsType> query = em.createQuery(queryString, ItemsType.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<ItemsType> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("countries"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select c from Countries c";
				TypedQuery<Countries> query = em.createQuery(queryString, Countries.class);
				// todo shlok need to handle exception in below line
				List<Countries> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("sales_tax"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and s.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select s from SalesTax s where s.updated>=?  " + logicCheckString + " and s.locationsId=?";
				TypedQuery<SalesTax> query = em.createQuery(queryString, SalesTax.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<SalesTax> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("order_source_to_sales_tax"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and s.status != 'D' ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select s from OrderSourceToSalesTax s where s.updated>=?  " + logicCheckString + " and s.locationsId=?";
				TypedQuery<OrderSourceToSalesTax> query = em.createQuery(queryString, OrderSourceToSalesTax.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<OrderSourceToSalesTax> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("business_hours"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and b.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select b from BusinessHour b where b.updated>=? " + logicCheckString + " and b.locationsId=?";
				TypedQuery<BusinessHour> query = em.createQuery(queryString, BusinessHour.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<BusinessHour> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("locations_to_images"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and b.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select b from LocationsToImages b where b.updated>=? " + logicCheckString + " and b.locationsId=?";
				TypedQuery<LocationsToImages> query = em.createQuery(queryString, LocationsToImages.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<LocationsToImages> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("location_to_location_details"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and l.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select l from LocationToLocationDetails l where l.updated>=? " + logicCheckString + " and l.locationsId=?";
				TypedQuery<LocationToLocationDetails> query = em.createQuery(queryString, LocationToLocationDetails.class).setParameter(1, updated).setParameter(2, locationId);
				// todo shlok need to handle exception in below line
				List<LocationToLocationDetails> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("locations_details"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and l.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select l from LocationsDetail l where l.updated>=? " + logicCheckString;
				TypedQuery<LocationsDetail> query = em.createQuery(queryString, LocationsDetail.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<LocationsDetail> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("location_details_type"))
			{
				if (isLogin == 1)
				{
					logicCheckString = "  and l.status != 'D'  ";
				}
				else
				{
					logicCheckString = "";
				}
				String queryString = "select l from LocationDetailsType l where l.updated>=? " + logicCheckString;
				TypedQuery<LocationDetailsType> query = em.createQuery(queryString, LocationDetailsType.class).setParameter(1, updated);
				// todo shlok need to handle exception in below line
				List<LocationDetailsType> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}
			else if (cdoName.equals("location_setting"))
			{
				logicCheckString = "";
				String queryString = "select ls from LocationSetting ls where ls.updated>=?  " + logicCheckString;
				TypedQuery<LocationSetting> query = em.createQuery(queryString, LocationSetting.class).setParameter(1, updated);
				List<LocationSetting> resultSet = query.getResultList();
				return convertToJson(resultSet);
			}

			else
				return null;
		}
		finally
		{
			if (em != null)
			{
				if (!isGlobal)
				{
					LocalSchemaEntityManager.getInstance().closeEntityManager(em);
				}
			}
		}

	}

	/**
	 * Convert to json.
	 *
	 * @param cdoMgmtObj
	 *            the cdo mgmt obj
	 * @return the string
	 * @throws JsonGenerationException
	 *             the json generation exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String convertToJson(Object cdoMgmtObj) throws JsonGenerationException, JsonMappingException, IOException
	{

		ObjectMapper objectMapper = new ObjectMapper();

		Writer strWriter = new StringWriter();
		objectMapper.writeValue(strWriter, cdoMgmtObj);
		String jsonString = strWriter.toString();
		return jsonString;

	}

	/**
	 * Gets the paymen gateway type by id.
	 *
	 * @param id
	 *            the id
	 * @return the paymen gateway type by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymenGatewayTypeById/{id}")
	public String getPaymenGatewayTypeById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			return new JSONUtility(httpRequest).convertToJsonString(em.find(PaymentGatewayType.class, id));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the transaction status by name.
	 *
	 * @param name
	 *            the name
	 * @return the transaction status by name
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getTransactionStatusByName/{name}")
	public String getTransactionStatusByName(@PathParam("name") String name) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			String queryString = "select ctd from TransactionStatus ctd where ctd.name = ? ";
			TypedQuery<TransactionStatus> query = em.createQuery(queryString, TransactionStatus.class).setParameter(1, name);
			// todo shlok need to handle exception in below line
			List<TransactionStatus> resultSet = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order detail status by id.
	 *
	 * @param id
	 *            the id
	 * @return the order detail status by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderDetailStatusById/{id}")
	public String getOrderDetailStatusById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			return new JSONUtility(httpRequest).convertToJsonString(em.find(OrderDetailStatus.class, id));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the order status by id.
	 *
	 * @param id
	 *            the id
	 * @return the order status by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getOrderStatusById/{id}")
	public String getOrderStatusById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			return new JSONUtility(httpRequest).convertToJsonString((OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, id));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the shift slots by order source id.
	 *
	 * @param orderSourceId
	 *            the order source id
	 * @param date
	 *            the date
	 * @param fromTime
	 *            the from time
	 * @param sessionId
	 *            the session id
	 * @return the shift slots by order source id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getShiftSlotsByOrderSourceId/{orderSourceId}/{date}/{fromTime}")
	public String getShiftSlotsByOrderSourceId(@PathParam("orderSourceId") int orderSourceId, @PathParam("date") String date, @PathParam("fromTime") String fromTime,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			List<ShiftSlots> shiftSlotsList = new ArrayList<ShiftSlots>();

			String queryString = "call p_get_shift_slots(?,?, ?)";
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, orderSourceId).setParameter(2, date).setParameter(3, fromTime).getResultList();
			for (Object[] objRow : resultList)
			{

				if ((int) objRow[0] != 0)
				{
					int i = 0;

					ShiftSlots shiftSlot = new ShiftSlots();
					shiftSlot.setId((int) objRow[i++]);
					shiftSlot.setShiftScheduleId((String) objRow[i++]);
					shiftSlot.setDate((String) objRow[i++]);
					shiftSlot.setSlotTime((String) objRow[i++]);
					shiftSlot.setSlotInterval((int) objRow[i++]);
					shiftSlot.setCurrentOrderInSlot((int) objRow[i++]);
					shiftSlot.setStatus((char) objRow[i++]+"");
					shiftSlot.setCurrentlyHoldedClient((int) objRow[i++]);
					shiftSlot.setLocationId((String) objRow[i++]);
					shiftSlot.setIsBlocked((int) objRow[i++]);
					shiftSlot.setCreated((Timestamp) objRow[i++]);
					shiftSlot.setCreatedBy((String) objRow[i++]);
					shiftSlot.setUpdated((Timestamp) objRow[i++]);
					shiftSlot.setUpdatedBy((String) objRow[i++]);
					shiftSlotsList.add(shiftSlot);

				}

			}

			return new JSONUtility(httpRequest).convertToJsonString(shiftSlotsList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the shift slots by shift schedule id.
	 *
	 * @param em
	 *            the em
	 * @param shiftId
	 *            the shift id
	 * @param date
	 *            the date
	 * @param fromTime
	 *            the from time
	 * @return the shift slots by shift schedule id
	 * @throws Exception
	 *             the exception
	 */
	public List<ShiftSlots> getShiftSlotsByShiftScheduleId(EntityManager em, String shiftId, String date, String fromTime) throws Exception
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ShiftSlots> criteria = builder.createQuery(ShiftSlots.class);
			Root<ShiftSlots> r = criteria.from(ShiftSlots.class);
			TypedQuery<ShiftSlots> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(ShiftSlots_.shiftScheduleId), shiftId), builder.equal(r.get(ShiftSlots_.date), date),
					builder.notEqual(r.get(ShiftSlots_.isBlocked), 1), builder.notEqual(r.get(ShiftSlots_.status), "H"), builder.greaterThanOrEqualTo(r.get(ShiftSlots_.slotTime), fromTime)));
			return query.getResultList();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return null;
	}

	/**
	 * Gets the order source to shift schedule by shift schedule id.
	 *
	 * @param em
	 *            the em
	 * @param shiftScheduleId
	 *            the shift schedule id
	 * @param orderSourceId
	 *            the order source id
	 * @param date
	 *            the date
	 * @return the order source to shift schedule by shift schedule id
	 * @throws Exception
	 *             the exception
	 */
	public List<OrderSourceToShiftSchedule> getOrderSourceToShiftScheduleByShiftScheduleId(EntityManager em, int shiftScheduleId, int orderSourceId, String date) throws Exception
	{
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceToShiftSchedule> criteria = builder.createQuery(OrderSourceToShiftSchedule.class);
			Root<OrderSourceToShiftSchedule> r = criteria.from(OrderSourceToShiftSchedule.class);
			TypedQuery<OrderSourceToShiftSchedule> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToShiftSchedule_.shiftScheduleId), shiftScheduleId),
					builder.equal(r.get(OrderSourceToShiftSchedule_.status), "B")));
			return query.getResultList();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return null;
	}

	@POST
	@Path("/holdShiftSlotForSlotId")
	public String holdShiftSession(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, HoldShiftSlotPacket holdShiftSlotPacket) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			tx = em.getTransaction();
			tx.begin();
			String schema = LocalSchemaEntityManager.getInstance().getSchemaNameUsingReffrenceNo(httpRequest, auth_token);

			if (schema != null)
			{
				holdShiftSlotPacket.setSchemaName(schema);
			}
			String myToken = httpRequest.getHeader("auth-token");
			int clientID = holdShiftSlotForClient(httpRequest, em, holdShiftSlotPacket, myToken, schema);
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(clientID);
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
	 * Un hold shift session for client.
	 *
	 * @param shiftHoldingClientId
	 *            the shift holding client id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/unHoldShiftSlotForClientId/{shiftHoldingClientId}")
	public String unHoldShiftSessionForClient(@PathParam("shiftHoldingClientId") String shiftHoldingClientId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			String schema = LocalSchemaEntityManager.getInstance().getSchemaNameUsingReffrenceNo(httpRequest, auth_token);

			return "" + (new LookupServiceBean().unHoldShiftSlotForClient(httpRequest, em, auth_token, shiftHoldingClientId, schema));

		}
		catch (RuntimeException e)
		{
			if (em != null && em.getTransaction() != null && em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Hold shift slot for client.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param holdShiftSlotPacket
	 *            the hold shift slot packet
	 * @param sessionId
	 *            the session id
	 * @param schema
	 *            the schema
	 * @return the int
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	public int holdShiftSlotForClient(HttpServletRequest httpRequest, EntityManager em, HoldShiftSlotPacket holdShiftSlotPacket, String sessionId, String schema) throws NirvanaXPException
	{

		HoldShiftSlotResponse holdShiftSlotResponse = new LookupServiceBean().holdShiftSlotForClient(httpRequest, em, holdShiftSlotPacket.getShiftSlots().getId(), sessionId, holdShiftSlotPacket
				.getShiftSlots().getUpdatedBy(), schema);
		ShiftSlots shiftSlots = holdShiftSlotResponse.getShiftSlots();
		if (shiftSlots != null)
		{
			ShiftSlots shiftSlotForpush = new ShiftSlots();
			shiftSlotForpush.setId(shiftSlots.getId());
			shiftSlotForpush.setStatus(shiftSlots.getStatus());
			holdShiftSlotPacket.setShiftSlots(shiftSlotForpush);
			// false will broadcast the entire packet
			// sendPacketForBroadcast(POSNServiceOperations.LookupService_slotUpdate.name(),
			// holdShiftSlotPacket);
			return holdShiftSlotResponse.getShiftHoldingClientId();
		}

		return 0;
	}

	/**
	 * Gets the payment way by name and location id.
	 *
	 * @param name
	 *            the name
	 * @param locationId
	 *            the location id
	 * @return the payment way by name and location id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getPaymentWayByNameAndLocationId/{name}/{locationId}")
	public String getPaymentWayByNameAndLocationId(@PathParam("name") String name, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<PaymentWay> criteria = builder.createQuery(PaymentWay.class);
			Root<PaymentWay> r = criteria.from(PaymentWay.class);
			TypedQuery<PaymentWay> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(PaymentWay_.locationsId), locationId), builder.notEqual(r.get(PaymentWay_.status), "D"),
					builder.equal(r.get(PaymentWay_.name), name)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all online city.
	 *
	 * @return the all online city
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllOnlineCity")
	public String getAllOnlineCity() throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<City> criteria = builder.createQuery(City.class);
			Root<City> r = criteria.from(City.class);
			TypedQuery<City> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(City_.isOnlineCity), 1), builder.notEqual(r.get(City_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the state by id.
	 *
	 * @param id
	 *            the id
	 * @return the state by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getStateById/{id}")
	public String getStateById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<State> criteria = builder.createQuery(State.class);
			Root<State> r = criteria.from(State.class);
			TypedQuery<State> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(State_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the country by id.
	 *
	 * @param id
	 *            the id
	 * @return the country by id
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getCountryById/{id}")
	public String getCountryById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Countries> criteria = builder.createQuery(Countries.class);
			Root<Countries> r = criteria.from(Countries.class);
			TypedQuery<Countries> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Countries_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the all cdo mgmt location specific.
	 *
	 * @param isLocationSpecific
	 *            the is location specific
	 * @return the all cdo mgmt location specific
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/getAllCdoMgmtLocationSpecific/{isLocationSpecific}")
	public String getAllCdoMgmtLocationSpecific(@PathParam("isLocationSpecific") int isLocationSpecific) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CdoMgmt> criteria = builder.createQuery(CdoMgmt.class);
			Root<CdoMgmt> r = criteria.from(CdoMgmt.class);
			TypedQuery<CdoMgmt> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(CdoMgmt_.isLocationSpecific), isLocationSpecific)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Sync cdo by cdo name and updated date location specific.
	 *
	 * @param cdoName
	 *            the cdo name
	 * @param updatedDate
	 *            the updated date
	 * @param isLogin
	 *            the is login
	 * @param isLocationSpecific
	 *            the is location specific
	 * @param locationId
	 *            the location id
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@GET
	@Path("/syncCdoByCdoNameAndUpdatedDateLocationSpecific/{cdoName}/{updatedDate}/{isLogin}/{isLocationSpecific}/{locationId}")
	public String syncCdoByCdoNameAndUpdatedDateLocationSpecific(@PathParam("cdoName") String cdoName, @PathParam("updatedDate") String updatedDate, @PathParam("isLogin") int isLogin,
			@PathParam("isLocationSpecific") int isLocationSpecific, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			// todo shlok need to handle exception in below line
			// handle cdo name null
			if (!"countries".equals(cdoName))
			{
				em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			}
			else
			{
				em = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			}

			LookupServiceBean bean = new LookupServiceBean();
			return bean.syncCdoByCdoNameAndUpdatedDateRecords(cdoName, updatedDate, null, em, httpRequest, isLogin, isLocationSpecific, locationId);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getOrderStatusByAccountAndLocationId/{accountId}/{locationId}")
	public String getOrderStatusByAccountAndLocationId(@PathParam("accountId") int accountId, 
			@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager emLocal = null;
		EntityManager emGlobal = null;
		//String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			
			emGlobal = GlobalSchemaEntityManager.getInstance().getEntityManager();
			
			Account account = emGlobal.find(Account.class, accountId);
			
			emLocal = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(account.getSchemaName());
			
			String queryStringSG = "select o from OrderSourceGroup o where o.locationsId = '" + locationId + "' and o.name = 'Delivery' " ;
			TypedQuery<OrderSourceGroup> querySG = emLocal.createQuery(queryStringSG, OrderSourceGroup.class);
			OrderSourceGroup resultSetSG = querySG.getSingleResult();
			
			String queryString = "select o from OrderStatus o where o.locationsId = '" + locationId + "' and o.orderSourceGroupId = " + resultSetSG.getId() ;
			TypedQuery<OrderStatus> query = emLocal.createQuery(queryString, OrderStatus.class);
			List<OrderStatus> resultSet = query.getResultList();
			return convertToJson(resultSet);
			
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(emLocal);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(emGlobal);
		}
	}
	
	@GET
	@Path("/getCancelReasonByLocationIdAndName/{locationId}/{name}")
	public String getReasonByLocationIdAndName(@PathParam("locationId") String locationId, @PathParam("name") String name, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReasonType> criteria = builder.createQuery(ReasonType.class);
			Root<ReasonType> rReasonType = criteria.from(ReasonType.class);
			TypedQuery<ReasonType> query = em.createQuery(criteria.select(rReasonType)
					.where(builder.equal(rReasonType.get(ReasonType_.name), "Cancel Reasons"),
					builder.notEqual(rReasonType.get(ReasonType_.status), "D")));
			ReasonType resultSet = null;
			try
			{
				resultSet = query.getSingleResult();
			}
			catch (NoResultException nre)
			{
				// todo shlok need
				// handle proper Exception
				logger.info("No result found for name -", name);
			}
			
			CriteriaBuilder newBuilder = em.getCriteriaBuilder();
			CriteriaQuery<Reasons> newCriteria = newBuilder.createQuery(Reasons.class);
			Root<Reasons> r = newCriteria.from(Reasons.class);
			TypedQuery<Reasons> newQuery= em.createQuery(newCriteria.select(r).
					where(newBuilder.equal(r.get(Reasons_.locationsId), locationId),
							newBuilder.equal(r.get(Reasons_.name), name),
							newBuilder.equal(r.get(Reasons_.reasonTypeId), resultSet.getId()),
					newBuilder.notEqual(r.get(Reasons_.status), "D")));
			Reasons reasons = null;
			try
			{
				reasons = newQuery.getSingleResult();
			}
			catch (NoResultException nre)
			{
				// todo shlok need
				// handle proper Exception
				logger.info("No result found for name -", name);
			}
			return convertToJson(reasons);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	

}
