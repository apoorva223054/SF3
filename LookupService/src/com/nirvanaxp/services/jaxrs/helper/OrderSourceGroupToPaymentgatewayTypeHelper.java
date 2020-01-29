/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.helper;

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
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType_;
import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderSourceGroupToPaymentgatewayTypeHelper.
 */
public class OrderSourceGroupToPaymentgatewayTypeHelper
{

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(OrderSourceGroupToPaymentgatewayTypeHelper.class.getName());

	/** The should eliminate D status. */
	public boolean shouldEliminateDStatus = false;

	/**
	 * Update order source group to paymentgateway type.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param orderSourceGroupToPaymentgatewayType
	 *            the order source group to paymentgateway type
	 * @return the order source group to paymentgateway type
	 * @throws Exception
	 *             the exception
	 */
	public OrderSourceGroupToPaymentgatewayType updateOrderSourceGroupToPaymentgatewayType(HttpServletRequest httpRequest, EntityManager em,
			OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType) throws Exception
	{
		boolean isExist = false;
		List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToPaymentgatewayTypes = getOrderSourceGroupToPaymentgatewayType(orderSourceGroupToPaymentgatewayType.getOrderSourceGroupId(), em);
		List<OrderSourceGroupToPaymentgatewayType> newOrderSourceGroupToPaymentgatewayTypes = new ArrayList<OrderSourceGroupToPaymentgatewayType>();
		if (orderSourceGroupToPaymentgatewayTypes != null && orderSourceGroupToPaymentgatewayTypes.size() > 0)
		{
			for (OrderSourceGroupToPaymentgatewayType sourceGroupToPaymentgatewayType : orderSourceGroupToPaymentgatewayTypes)
			{
				if (sourceGroupToPaymentgatewayType.getOrderSourceGroupId().equals(orderSourceGroupToPaymentgatewayType.getOrderSourceGroupId()) && // checking
																																				// order
																																				// exist
																																				// in
																																				// database
						sourceGroupToPaymentgatewayType.getPaymentgatewayTypeId() == orderSourceGroupToPaymentgatewayType.getPaymentgatewayTypeId() && !isExist)
				{
					isExist = true;
					orderSourceGroupToPaymentgatewayType.setId(sourceGroupToPaymentgatewayType.getId());
					orderSourceGroupToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					newOrderSourceGroupToPaymentgatewayTypes.add(orderSourceGroupToPaymentgatewayType);
				}
				else
				{
					sourceGroupToPaymentgatewayType.setStatus("D");
					sourceGroupToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					sourceGroupToPaymentgatewayType.setUpdatedBy(orderSourceGroupToPaymentgatewayType.getUpdatedBy());

					newOrderSourceGroupToPaymentgatewayTypes.add(sourceGroupToPaymentgatewayType);

				}
			}
			if (!isExist)
			{

				newOrderSourceGroupToPaymentgatewayTypes.add(orderSourceGroupToPaymentgatewayType);
			}
			for (OrderSourceGroupToPaymentgatewayType type : newOrderSourceGroupToPaymentgatewayTypes)
			{
				if (type.getId() > 0)
				{
					type.setCreated(em.find(OrderSourceGroupToPaymentgatewayType.class, type.getId()).getCreated());

				}
				type = em.merge(type);
				if (!type.getStatus().equals("D"))
				{

					// todo shlok need
					// remove if
				}

			}
		}
		else
		{
			List<PaymentGatewayToPinpad> gatewayToPinpads = new ArrayList<PaymentGatewayToPinpad>();

			if (orderSourceGroupToPaymentgatewayType.getPinpadList() != null && orderSourceGroupToPaymentgatewayType.getPinpadList().size() > 0)
			{
				gatewayToPinpads = orderSourceGroupToPaymentgatewayType.getPinpadList();
			}
			orderSourceGroupToPaymentgatewayType.setStatus("D");
			orderSourceGroupToPaymentgatewayType.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			orderSourceGroupToPaymentgatewayType = em.merge(orderSourceGroupToPaymentgatewayType);

		}

		return orderSourceGroupToPaymentgatewayType;

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
	 * Gets the order source group to paymentgateway type by order source group
	 * id.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @return the order source group to paymentgateway type by order source
	 *         group id
	 * @throws Exception
	 *             the exception
	 */
	//
	public List<OrderSourceGroupToPaymentgatewayType> getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId(HttpServletRequest httpRequest, EntityManager em, String id) throws Exception
	{

		List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToPaymentgatewayType = new ArrayList<OrderSourceGroupToPaymentgatewayType>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getOrderSourceGroupToPaymentgatewayTypeByOrderSourceGroupId( '"+ id +"')").getResultList();
		for (Object[] objRow : resultList)
		{
			// if this has primary key not 0

			OrderSourceGroupToPaymentgatewayType paymentgatewayType = new OrderSourceGroupToPaymentgatewayType();
			paymentgatewayType.setId((Integer) objRow[0]);
			paymentgatewayType.setMerchantId((String) objRow[1]);
			paymentgatewayType.setPassword((String) objRow[2]);
			paymentgatewayType.setParameter1((String) objRow[3]);
			paymentgatewayType.setParameter2((String) objRow[4]);
			paymentgatewayType.setParameter3((String) objRow[5]);
			paymentgatewayType.setParameter4((String) objRow[6]);
			paymentgatewayType.setParameter5((String) objRow[7]);
			paymentgatewayType.setStatus(objRow[8].toString());
			paymentgatewayType.setOrderSourceGroupId((String) objRow[9]);
			paymentgatewayType.setOrderSourceGroupName((String) objRow[10]);
			paymentgatewayType.setPaymentgatewayTypeId((Integer) objRow[11]);
			if (objRow[12] != null)
			{
				paymentgatewayType.setCreated((Timestamp) objRow[12]);
			}
			paymentgatewayType.setCreatedBy((String) objRow[13]);
			if (objRow[14] != null)
			{
				paymentgatewayType.setUpdated((Timestamp) objRow[14]);
			}
			paymentgatewayType.setUpdatedBy((String) objRow[15]);

			paymentgatewayType.setPinpadList(getPaymentGatewayToPinpads(em, paymentgatewayType.getPaymentgatewayTypeId(), id));
			orderSourceGroupToPaymentgatewayType.add(paymentgatewayType);

		}
		return orderSourceGroupToPaymentgatewayType;
	}

	/**
	 * Gets the payment gateway to pinpads.
	 *
	 * @param em
	 *            the em
	 * @param id
	 *            the id
	 * @param orderSourceGroupId
	 *            the order source group id
	 * @return the payment gateway to pinpads
	 */
	private List<PaymentGatewayToPinpad> getPaymentGatewayToPinpads(EntityManager em, int id, String orderSourceGroupId)
	{
		List<PaymentGatewayToPinpad> resultSet = new ArrayList<PaymentGatewayToPinpad>();
		try
		{
			String queryString = "select l from PaymentGatewayToPinpad l where l.paymentGatewayId=?  and l.status NOT IN ('D','I')";

			TypedQuery<PaymentGatewayToPinpad> query = em.createQuery(queryString, PaymentGatewayToPinpad.class).setParameter(1, id);
			resultSet = query.getResultList();
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return resultSet;
	}

	/**
	 * Gets the all order source group to paymentgateway type.
	 *
	 * @param httpRequest
	 *            the http request
	 * @param em
	 *            the em
	 * @return the all order source group to paymentgateway type
	 * @throws Exception
	 *             the exception
	 */
	public String getAllOrderSourceGroupToPaymentgatewayType(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{

		List<OrderSourceGroupToPaymentgatewayType> orderSourceGroupToPaymentgatewayType = new ArrayList<OrderSourceGroupToPaymentgatewayType>();
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery("call getAllOrderSourceGroupToPaymentgatewayType()").getResultList();
		for (Object[] objRow : resultList)
		{
			OrderSourceGroupToPaymentgatewayType paymentgatewayType = new OrderSourceGroupToPaymentgatewayType();
			paymentgatewayType.setId((Integer) objRow[0]);
			paymentgatewayType.setMerchantId((String) objRow[1]);
			paymentgatewayType.setPassword((String) objRow[2]);
			paymentgatewayType.setParameter1((String) objRow[3]);
			paymentgatewayType.setParameter2((String) objRow[4]);
			paymentgatewayType.setParameter3((String) objRow[5]);
			paymentgatewayType.setParameter4((String) objRow[6]);
			paymentgatewayType.setStatus(objRow[7].toString());
			paymentgatewayType.setOrderSourceGroupId((String) objRow[8]);
			paymentgatewayType.setOrderSourceGroupName((String) objRow[9]);
			paymentgatewayType.setPaymentgatewayTypeId((Integer) objRow[10]);
			if (objRow[11] != null)
			{
				paymentgatewayType.setCreated((Timestamp) objRow[11]);
			}
			paymentgatewayType.setCreatedBy((String) objRow[12]);
			if (objRow[13] != null)
			{
				paymentgatewayType.setUpdated((Timestamp) objRow[13]);
			}
			paymentgatewayType.setUpdatedBy((String) objRow[14]);
			orderSourceGroupToPaymentgatewayType.add(paymentgatewayType);

		}
		return new JSONUtility(httpRequest).convertToJsonString(orderSourceGroupToPaymentgatewayType);

	}

	/**
	 * Gets the order source group to paymentgateway type.
	 *
	 * @param id
	 *            the id
	 * @param em
	 *            the em
	 * @return the order source group to paymentgateway type
	 */
	public List<OrderSourceGroupToPaymentgatewayType> getOrderSourceGroupToPaymentgatewayType(String id, EntityManager em)
	{
		if (id != null && em != null)
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<OrderSourceGroupToPaymentgatewayType> criteria = builder.createQuery(OrderSourceGroupToPaymentgatewayType.class);
			Root<OrderSourceGroupToPaymentgatewayType> r = criteria.from(OrderSourceGroupToPaymentgatewayType.class);
			if (shouldEliminateDStatus)
			{
				TypedQuery<OrderSourceGroupToPaymentgatewayType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceGroupToPaymentgatewayType_.orderSourceGroupId), id),
						builder.notEqual(r.get(OrderSourceGroupToPaymentgatewayType_.status), "D")));
				return query.getResultList();
			}
			else
			{
				TypedQuery<OrderSourceGroupToPaymentgatewayType> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderSourceGroupToPaymentgatewayType_.orderSourceGroupId), id)));
				return query.getResultList();
			}
		}
		return null;

	}

}
