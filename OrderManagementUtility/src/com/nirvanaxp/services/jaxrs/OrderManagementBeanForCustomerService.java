/*
 * 
 */
package com.nirvanaxp.services.jaxrs;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.types.entities.orders.OrderDetailItem;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.user.User;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderManagementBeanForCustomerService.
 */
public class OrderManagementBeanForCustomerService
{

	/** The http request. */
	private HttpServletRequest httpRequest;

	/**
	 * Instantiates a new order management bean for customer service.
	 *
	 * @param httpRequest
	 *            the http request
	 */
	public OrderManagementBeanForCustomerService(HttpServletRequest httpRequest)
	{
		super();
		this.httpRequest = httpRequest;
	}

	/**
	 * Gets the all order by email or phone.
	 *
	 * @param em
	 *            the em
	 * @param email
	 *            the email
	 * @param phone
	 *            the phone
	 * @param businessId
	 *            the business id
	 * @param getPaid
	 *            the get paid
	 * @return the all order by email or phone
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws Exception
	 *             the exception
	 */
	public List<OrderHeader> getAllOrderByEmailOrPhone(EntityManager em, String email, String phone, int businessId, boolean getPaid) throws NirvanaXPException, Exception
	{
		User resultSet = null;
		List<OrderHeader> orders = null;
		List<OrderHeader> headers = new ArrayList<OrderHeader>();
		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		// create where condition
		String whereCondition = "";
		if (email != null && !email.equalsIgnoreCase("NULL"))
		{
			whereCondition = "u.email ='" + email + "'";
		}
		if (phone != null && !phone.equalsIgnoreCase("NULL"))
		{
			if (whereCondition.length() > 0)
			{
				whereCondition += " or u.phone ='" + phone + "'";
			}
			else
			{
				whereCondition = "u.phone ='" + phone + "'";
			}

		}
		try
		{
			String queryString = "select u from User u where " + whereCondition;
			TypedQuery<User> query = em.createQuery(queryString, User.class);
			List<User> users = query.getResultList();
			if (users != null && users.size() > 0)
			{
				resultSet = users.get(0);
			}
			else
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE,
						MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE, MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE));
			}
		}
		catch (NoResultException e)
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE,
					MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE, MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE));
		}

		String locationsId = "";
		try
		{
			String queryString = "select id from locations where business_id = ? ";
			Query query = em.createNativeQuery(queryString).setParameter(1, businessId);
			@SuppressWarnings("unchecked")
			List<Object> objects = query.getResultList();

			// todo shlok need
			// make common method
			if (objects != null && objects.size() > 0)
			{
				for (int i = 0; i < objects.size(); i++)
				{
					if (i == (objects.size() - 1))
					{
						locationsId += objects.get(i);
					}
					else
					{
						locationsId += objects.get(i) + ",";
					}
				}
			}
		}
		catch (NoResultException e)
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER,
					MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER));
		}
		if (getPaid)
		{

			String statusId = "";
			try
			{
				String queryString = "select os.id from order_status os where os.name in ('Ready to Order', 'Bus Ready','Cancel Order') and locations_id in (" + locationsId + " )  ";
				Query query = em.createNativeQuery(queryString);
				@SuppressWarnings("unchecked")
				List<Object> objects = query.getResultList();

				// todo shlok need
				// make common method
				if (objects != null && objects.size() > 0)
				{
					for (int i = 0; i < objects.size(); i++)
					{
						if (i == (objects.size() - 1))
						{
							statusId += objects.get(i);
						}
						else
						{
							statusId += objects.get(i) + ",";
						}
					}
				}
			}
			catch (NoResultException e)
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER,
						MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER));
			}

			if (resultSet != null)
			{
				String queryString = "select u from OrderHeader u  " + " where u.locationsId in (" + locationsId + " ) and u.orderStatusId not in (" + statusId
						+ ") and  u.usersId = ?  order by u.id desc";
				TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class).setParameter(1, resultSet.getId());
				orders = query.getResultList();
				// setting order detail item Obhnject
				for (OrderHeader header : orders)
				{
					header.setOrderDetailItems(getOrderDetailsItemForOrderId(em, header.getId(), locationsId, bean));
					headers.add(header);
				}
			}

		}
		else
		{
			String statusId = "";
			try
			{
				String queryString = "select os.id from order_status os where os.name in ('Ready to Order', 'Bus Ready', 'Cancel Order') and locations_id in (" + locationsId + " )  ";
				Query query = em.createNativeQuery(queryString);
				@SuppressWarnings("unchecked")
				List<Object> objects = query.getResultList();

				if (objects != null && objects.size() > 0)
				{
					for (int i = 0; i < objects.size(); i++)
					{
						if (i == (objects.size() - 1))
						{
							statusId += objects.get(i);
						}
						else
						{
							statusId += objects.get(i) + ",";
						}
					}
				}
			}
			catch (NoResultException e)
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER,
						MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER));
			}

			if (resultSet != null)
			{
				String queryString = "select u from OrderHeader u  " + " where u.locationsId in (" + locationsId + " ) and u.orderStatusId not in (" + statusId
						+ ") and  u.usersId = ?  order by u.id desc";
				TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class).setParameter(1, resultSet.getId());
				orders = query.getResultList();
				// setting order detail item Obhnject
				for (OrderHeader header : orders)
				{
					header = new OrderManagementServiceBean().getOrderById(em, header.getId());
					headers.add(header);
				}
			}
		}
		return headers;

	}

	/**
	 * Gets the all order by email or phone by account id.
	 *
	 * @param em
	 *            the em
	 * @param email
	 *            the email
	 * @param phone
	 *            the phone
	 * @param getPaid
	 *            the get paid
	 * @return the all order by email or phone by account id
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 * @throws Exception
	 *             the exception
	 */
	public List<OrderHeader> getAllOrderByEmailOrPhoneByAccountId(EntityManager em, String email, String phone, boolean getPaid) throws NirvanaXPException, Exception
	{
		User resultSet = null;
		List<OrderHeader> orders = null;
		List<OrderHeader> headers = new ArrayList<OrderHeader>();
		OrderManagementServiceBean bean = new OrderManagementServiceBean();
		// create where condition
		String whereCondition = "";
		if (email != null && !email.equalsIgnoreCase("NULL"))
		{
			whereCondition = "u.email ='" + email + "'";
		}
		if (phone != null && !phone.equalsIgnoreCase("NULL"))
		{
			if (whereCondition.length() > 0)
			{
				whereCondition += " or u.phone ='" + phone + "'";
			}
			else
			{
				whereCondition = "u.phone ='" + phone + "'";
			}

		}
		try
		{
			String queryString = "select u from User u where " + whereCondition;
			TypedQuery<User> query = em.createQuery(queryString, User.class);
			List<User> users = query.getResultList();
			if (users != null && users.size() > 0)
			{
				resultSet = users.get(0);
			}
			else
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE,
						MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE, MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE));
			}
		}
		catch (NoResultException e)
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_NOT_PRESENT_IN_DATABASE,
					MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE, MessageConstants.ERROR_MESSAGE_USER_NOT_PRESENT_IN_DATABASE_DISPLAY_MESSAGE));
		}

		if (getPaid)
		{

			String statusId = "";
			try
			{
				String queryString = "select os.id from order_status os where os.name in ('Ready to Order', 'Bus Ready','Cancel Order','Void Order','Reopen') ";
				Query query = em.createNativeQuery(queryString);
				@SuppressWarnings("unchecked")
				List<Object> objects = query.getResultList();
				// todo shlok need
				// make common method
				if (objects != null && objects.size() > 0)
				{
					for (int i = 0; i < objects.size(); i++)
					{
						if (i == (objects.size() - 1))
						{
							statusId += objects.get(i);
						}
						else
						{
							statusId += objects.get(i) + ",";
						}
					}
				}
			}
			catch (NoResultException e)
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER,
						MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER));
			}

			if (resultSet != null)
			{
				String queryString = "select u from OrderHeader u  " + " where  u.orderStatusId not in (" + statusId + ") and  u.usersId = ?  order by u.id desc";
				TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class).setParameter(1, resultSet.getId());
				orders = query.getResultList();
				// setting order detail item Obhnject
				for (OrderHeader header : orders)
				{
					header.setOrderDetailItems(getOrderDetailsItemForOrderId(em, header.getId(), bean));
					headers.add(header);
				}
			}

		}
		else
		{
			String statusId = "";
			try
			{
				String queryString = "select os.id from order_status os where os.name in (" + "'Ready to Order', 'Bus Ready', 'Cancel Order','Order Paid') ";
				Query query = em.createNativeQuery(queryString);
				@SuppressWarnings("unchecked")
				List<Object> objects = query.getResultList();
				// todo shlok need
				//make common method
				if (objects != null && objects.size() > 0)
				{
					for (int i = 0; i < objects.size(); i++)
					{
						if (i == (objects.size() - 1))
						{
							statusId += objects.get(i);
						}
						else
						{
							statusId += objects.get(i) + ",";
						}
					}
				}
			}
			catch (NoResultException e)
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER,
						MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER));
			}

			if (resultSet != null)
			{
				String queryString = "select u from OrderHeader u  " + " where u.orderStatusId not in (" + statusId + ") and  u.usersId = ?  order by u.id desc";
				TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class).setParameter(1, resultSet.getId());
				orders = query.getResultList();
				// setting order detail item Obhnject
				for (OrderHeader header : orders)
				{
					header = new OrderManagementServiceBean().getOrderById(em, header.getId());
					headers.add(header);
				}
			}
		}
		return headers;

	}

	/**
	 * Gets the order details item for order id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @param locationsId
	 *            the locations id
	 * @param bean
	 *            the bean
	 * @return the order details item for order id
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	List<OrderDetailItem> getOrderDetailsItemForOrderId(EntityManager em, String orderHeaderId, String locationsId, OrderManagementServiceBean bean) throws NirvanaXPException
	{

		String statusId = "";
		try
		{
			String queryString = "select os.id from order_detail_status os where os.name in ('Item Removed','Recall','Attribute Removed') and locations_id in (" + locationsId + " )  ";
			Query query = em.createNativeQuery(queryString);
			@SuppressWarnings("unchecked")
			List<Object> objects = query.getResultList();

			// todo shlok need
			// make common method
			if (objects != null && objects.size() > 0)
			{
				for (int i = 0; i < objects.size(); i++)
				{
					if (i == (objects.size() - 1))
					{
						statusId += objects.get(i);
					}
					else
					{
						statusId += objects.get(i) + ",";
					}
				}
			}
		}
		catch (NoResultException e)
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER,
					MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER));
		}

		String queryString = "select odi from OrderDetailItem odi where odi.orderHeaderId= ? and odi.orderDetailStatusId not in (" + statusId + ") ";
		TypedQuery<OrderDetailItem> query = em.createQuery(queryString, OrderDetailItem.class).setParameter(1, orderHeaderId);
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();

		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList)
		{
			orderDetailItemObj.setOrderDetailAttributes(bean.getOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId(), statusId));
		}
		return orderDetailItemsList;

	}

	/**
	 * Gets the order details item for order id.
	 *
	 * @param em
	 *            the em
	 * @param orderHeaderId
	 *            the order header id
	 * @param bean
	 *            the bean
	 * @return the order details item for order id
	 * @throws NirvanaXPException
	 *             the nirvana XP exception
	 */
	List<OrderDetailItem> getOrderDetailsItemForOrderId(EntityManager em, String orderHeaderId, OrderManagementServiceBean bean) throws NirvanaXPException
	{

		String statusId = "";
		try
		{
			String queryString = "select os.id from order_detail_status os where os.name in ('Item Removed','Recall','Attribute Removed') ";
			Query query = em.createNativeQuery(queryString);
			@SuppressWarnings("unchecked")
			List<Object> objects = query.getResultList();
			// todo shlok need
			// make common method
			if (objects != null && objects.size() > 0)
			{
				for (int i = 0; i < objects.size(); i++)
				{
					if (i == (objects.size() - 1))
					{
						statusId += objects.get(i);
					}
					else
					{
						statusId += objects.get(i) + ",";
					}
				}
			}
		}
		catch (NoResultException e)
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_ACTIVE_BUSINESS_FOR_USER, MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER,
					MessageConstants.ERROR_MESSAGE_NO_ACTIVE_BUSINESS_FOR_USER));
		}

		String queryString = "select odi from OrderDetailItem odi where odi.orderHeaderId= ? and odi.orderDetailStatusId not in (" + statusId + ") ";
		TypedQuery<OrderDetailItem> query = em.createQuery(queryString, OrderDetailItem.class).setParameter(1, orderHeaderId);
		List<OrderDetailItem> orderDetailItemsList = query.getResultList();

		for (OrderDetailItem orderDetailItemObj : orderDetailItemsList)
		{
			orderDetailItemObj.setOrderDetailAttributes(bean.getOrderDetailAttributeForOrderDetailItemId(em, orderDetailItemObj.getId(), statusId));
		}
		return orderDetailItemsList;

	}
}
