/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.helper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType_;
import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderSourceToPaymentgatewayTypeHelper.
 */
public class OrderSourceToPaymentgatewayTypeHelper
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(OrderSourceToPaymentgatewayTypeHelper.class.getName());

	/** The should eliminate D status. */
	public boolean shouldEliminateDStatus = false;

	/**
	 * Adds the order source to paymentgateway type.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderSourceToPaymentgatewayType
	 *            the order source to paymentgateway type
	 * @return the order source to paymentgateway type
	 * @throws Exception
	 *             the exception
	 */
	public OrderSourceToPaymentgatewayType addOrderSourceToPaymentgatewayType(HttpServletRequest httpRequest, EntityManager em, OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType)
			throws Exception
	{

		orderSourceToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		orderSourceToPaymentgatewayType.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.persist(orderSourceToPaymentgatewayType);
		return orderSourceToPaymentgatewayType;

	}

	/**
	 * Update order source to paymentgateway type.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderSourceToPaymentgatewayType
	 *            the order source to paymentgateway type
	 * @return the order source to paymentgateway type
	 * @throws Exception
	 *             the exception
	 */
	public OrderSourceToPaymentgatewayType updateOrderSourceToPaymentgatewayType(HttpServletRequest httpRequest, EntityManager em, OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType)
			throws Exception
	{

		boolean isExist = false;
		logger.severe("orderSourceToPaymentgatewayType.getOrderSourceId()================================================================"+orderSourceToPaymentgatewayType.getOrderSourceId());
		List<OrderSourceToPaymentgatewayType> orderSourceToPaymentgatewayTypes = getOrderSourceToPaymentgatewayType(orderSourceToPaymentgatewayType.getOrderSourceId(), em);
		List<OrderSourceToPaymentgatewayType> newOrderSourceToPaymentgatewayTypes = new ArrayList<OrderSourceToPaymentgatewayType>();
		logger.severe("orderSourceToPaymentgatewayTypes================================================================"+orderSourceToPaymentgatewayTypes);
		
		if (orderSourceToPaymentgatewayTypes != null && orderSourceToPaymentgatewayTypes.size() > 0)
		{
			for (OrderSourceToPaymentgatewayType sourceToPaymentgatewayType : orderSourceToPaymentgatewayTypes)
			{
				if (sourceToPaymentgatewayType.getOrderSourceId().equals(orderSourceToPaymentgatewayType.getOrderSourceId()) && // checking
																															// order
																															// exist
																															// in
																															// database
						sourceToPaymentgatewayType.getPaymentgatewayTypeId() == orderSourceToPaymentgatewayType.getPaymentgatewayTypeId() && !isExist)
				{
					isExist = true;
					orderSourceToPaymentgatewayType.setId(sourceToPaymentgatewayType.getId());
					orderSourceToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					newOrderSourceToPaymentgatewayTypes.add(orderSourceToPaymentgatewayType);
				}
				else
				{
					sourceToPaymentgatewayType.setStatus("D");
					sourceToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					sourceToPaymentgatewayType.setUpdatedBy(orderSourceToPaymentgatewayType.getUpdatedBy());
					newOrderSourceToPaymentgatewayTypes.add(sourceToPaymentgatewayType);

				}
			}
			if (!isExist)
			{
				newOrderSourceToPaymentgatewayTypes.add(orderSourceToPaymentgatewayType);
			}
			for (OrderSourceToPaymentgatewayType type : newOrderSourceToPaymentgatewayTypes)
			{
				if (type.getId() > 0)
				{

					type.setCreated(em.find(OrderSourceToPaymentgatewayType.class, type.getId()).getCreated());

				}
				type = em.merge(type);
				if (!type.getStatus().equals("D"))
				{

				}

			}
		}
		else
		{
			List<PaymentGatewayToPinpad> gatewayToPinpads = new ArrayList<PaymentGatewayToPinpad>();

			if (orderSourceToPaymentgatewayType.getPinpadList() != null && orderSourceToPaymentgatewayType.getPinpadList().size() > 0)
			{
				gatewayToPinpads = orderSourceToPaymentgatewayType.getPinpadList();
			}

			orderSourceToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			orderSourceToPaymentgatewayType = em.merge(orderSourceToPaymentgatewayType);

		}

		return orderSourceToPaymentgatewayType;

	}

	/**
	 * Update payment gateway to pinpad.
	 *
	 * @param paymentGatewayToPinpad
	 *            the payment gateway to pinpad
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the payment gateway to pinpad
	 * @throws Exception
	 *             the exception
	 */
	PaymentGatewayToPinpad updatePaymentGatewayToPinpad(PaymentGatewayToPinpad paymentGatewayToPinpad, HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		paymentGatewayToPinpad.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		paymentGatewayToPinpad = em.merge(paymentGatewayToPinpad);
		return paymentGatewayToPinpad;

	}

	/**
	 * Delete order source to paymentgateway type.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderSourceToPaymentgatewayType
	 *            the order source to paymentgateway type
	 * @return the order source to paymentgateway type
	 */
	//
	public OrderSourceToPaymentgatewayType deleteOrderSourceToPaymentgatewayType(HttpServletRequest httpRequest, EntityManager em, OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType)
	{

		orderSourceToPaymentgatewayType = em.find(OrderSourceToPaymentgatewayType.class, orderSourceToPaymentgatewayType.getId());

		orderSourceToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		orderSourceToPaymentgatewayType.setStatus("D");
		em.merge(orderSourceToPaymentgatewayType);
		return orderSourceToPaymentgatewayType;

	}

	/**
	 * Gets the order source to paymentgateway type by order source id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the order source to paymentgateway type by order source id
	 * @throws SQLException
	 *             the SQL exception
	 */
	public List<OrderSourceToPaymentgatewayType> getOrderSourceToPaymentgatewayTypeByOrderSourceId(HttpServletRequest httpRequest, EntityManager em, String id) throws SQLException
	{
		List<OrderSourceToPaymentgatewayType> orderSourceToPaymentgatewayType = new ArrayList<OrderSourceToPaymentgatewayType>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getOrderSourceToPaymentgatewayTypeByOrderSourceId( ? )").setParameter(1, id).getResultList();

		for (Object[] objRow : resultList)
		{
			OrderSourceToPaymentgatewayType paymentgatewayType = new OrderSourceToPaymentgatewayType();
			paymentgatewayType.setId((Integer) objRow[0]);
			paymentgatewayType.setMerchantId((String) objRow[1]);
			paymentgatewayType.setPassword((String) objRow[2]);
			paymentgatewayType.setParameter1((String) objRow[3]);
			paymentgatewayType.setParameter2((String) objRow[4]);
			paymentgatewayType.setParameter3((String) objRow[5]);
			paymentgatewayType.setParameter4((String) objRow[6]);
			paymentgatewayType.setParameter5((String) objRow[7]);
			paymentgatewayType.setStatus(objRow[8].toString());
			paymentgatewayType.setOrderSourceId((String) objRow[9]);
			paymentgatewayType.setOrderSourceName((String) objRow[10]);
			paymentgatewayType.setPaymentgatewayTypeId((Integer) objRow[11]);
			if (objRow[12] != null)
			{
				paymentgatewayType.setCreated(((Timestamp) objRow[12]));
			}
			paymentgatewayType.setCreatedBy((String) objRow[13]);
			if (objRow[14] != null)
			{
				paymentgatewayType.setUpdated(((Timestamp) objRow[14]));
			}
			paymentgatewayType.setUpdatedBy((String) objRow[15]);
			paymentgatewayType.setPinpadList(getPaymentGatewayToPinpads(em, paymentgatewayType.getPaymentgatewayTypeId(), id));
			orderSourceToPaymentgatewayType.add(paymentgatewayType);

		}
		return orderSourceToPaymentgatewayType;

	}

	/**
	 * Gets the order source to paymentgateway type.
	 *
	 * @param id
	 *            the id
	 * @param em
	 *            the em
	 * @return the order source to paymentgateway type
	 */
	public List<OrderSourceToPaymentgatewayType> getOrderSourceToPaymentgatewayType(String id, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderSourceToPaymentgatewayType> criteria = builder.createQuery(OrderSourceToPaymentgatewayType.class);
		Root<OrderSourceToPaymentgatewayType> r = criteria.from(OrderSourceToPaymentgatewayType.class);
		if (shouldEliminateDStatus)
		{
			TypedQuery<OrderSourceToPaymentgatewayType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToPaymentgatewayType_.orderSourceId), id),
					builder.notEqual(r.get(OrderSourceToPaymentgatewayType_.status), "D")));
			return query.getResultList();
		}
		else
		{
			TypedQuery<OrderSourceToPaymentgatewayType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceToPaymentgatewayType_.orderSourceId), id)));
			return query.getResultList();
		}

	}

	/**
	 * Gets the payment gateway to pinpads.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param orderSourceId
	 *            the order source id
	 * @return the payment gateway to pinpads
	 */
	private List<PaymentGatewayToPinpad> getPaymentGatewayToPinpads(EntityManager em, int id, String orderSourceId)
	{
		List<PaymentGatewayToPinpad> resultSet = new ArrayList<PaymentGatewayToPinpad>();
		try
		{
			String queryString = "select l from PaymentGatewayToPinpad l where l.paymentGatewayId=? and l.status NOT IN ('D', 'I')";

			TypedQuery<PaymentGatewayToPinpad> query = em.createQuery(queryString, PaymentGatewayToPinpad.class).setParameter(1, id);
			resultSet = query.getResultList();
		}
		catch (Exception e)
		{
			// todo shlok need
			// handle proper Exception
			logger.severe(e);
		}
		return resultSet;
	}
}
