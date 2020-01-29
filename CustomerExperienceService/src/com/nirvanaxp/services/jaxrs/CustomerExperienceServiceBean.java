/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.CustomerExperiencePacket;
import com.nirvanaxp.services.jaxrs.packets.CustomerFeedbackHistoryPacket;
import com.nirvanaxp.services.jaxrs.packets.CustomerHistoryPacket;
import com.nirvanaxp.services.jaxrs.packets.CustomerOrderHistory;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.feedback.CustomerExperienceToUserDetail;
import com.nirvanaxp.types.entities.feedback.CustomerExperienceToUserFeedback;
import com.nirvanaxp.types.entities.feedback.FeedbackField;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToFeebackDetail;
import com.nirvanaxp.types.entities.user.experience.CustomerExperience;
import com.nirvanaxp.types.entities.user.experience.CustomerFeedBackExperience;
import com.nirvanaxp.user.utility.GlobalUsermanagement;

/**
 * Session Bean implementation class CustomerExperienceServiceBean
 */
class CustomerExperienceServiceBean
{
	private final static NirvanaLogger logger = new NirvanaLogger(CustomerExperienceServiceBean.class.getName());

	CustomerExperience add(EntityManager em, CustomerExperience exp, long timeInMilis)
	{
		// CustomerExperience oldExp =null;
		// if(exp.getId()!=0){
		// oldExp = em.find(CustomerExperience.class, exp.getId());
		// }
		// if(oldExp != null){
		// exp.setUpdated(oldExp.getUpdated().getTime());
		// exp.setCreated(oldExp.getCreated());
		// }else{
		// exp.setUpdated(timeInMilis);
		// exp.setCreated(timeInMilis);
		// }
		exp.setId(new StoreForwardUtility().generateUUID());
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			
			em.persist(exp);
			tx.commit();
		}
		catch (RuntimeException e)
		{
			// on error, if transaction active, rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
			// TODO uzma remove try catch and handle it on add function
		}

		return exp;

	}

	UsersToFeebackDetail addUsersToFeedbackDetail(EntityManager em, UsersToFeebackDetail usersToFeedback) throws Exception
	{
		// UsersToFeebackDetail oldDetail = null;
		// if(usersToFeedback.getId()!= 0){
		// oldDetail = em.find(UsersToFeebackDetail.class,
		// usersToFeedback.getId());
		// }
		// if(oldDetail!= null){
		//
		// }else{
		//
		// }
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(usersToFeedback);
			tx.commit();
		}
		catch (RuntimeException e)
		{
			// on error, if transaction active, rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		// TODO uzma remove try catch and handle it on add function

		return usersToFeedback;

	}

	/**
	 * @param httpRequest
	 * @param em
	 * @param userID
	 * @return
	 * @throws Exception
	 */
	public CustomerOrderHistory getCustomerHistoryByUserId(HttpServletRequest httpRequest, EntityManager em, String userId) throws Exception
	{

		String sql = "";

		List<CustomerHistoryPacket> list = new ArrayList<CustomerHistoryPacket>();
		CustomerHistoryPacket customerHistoryPacket = null;
		CustomerOrderHistory customerOrderHistory = new CustomerOrderHistory();
		CustomerFeedbackHistoryPacket customerFeedbackHistoryPacket = null;
		List<CustomerFeedbackHistoryPacket> packets = null;

		sql = new StringBuilder()
				.append(" SELECT id,users_id,point_of_service_count,date,(total/point_of_service_count),(SELECT CONCAT(cast(ftt.id as char),',',cast(count(distinct ce.created) as char),',' ,cast(avg(star_value) as char)) ")
				.append(" FROM order_header oh  JOIN `customer_experience` ce ON ce.order_header_id = oh.id  ").append(" LEFT JOIN `users` u ON ce.users_id = u.id ")
				.append(" JOIN `smileys` s ON ce.smiley_id = s.id join feedback_type ftt on ftt.id=s.feedback_type_id ").append(" LEFT JOIN `users_to_feeback_details` uf ON uf.users_id = u.id ")
				.append(" LEFT JOIN feedback_field ff ON uf.feedback_details_id = ff.id ").append(" JOIN `feedback_question` fq ON ce.feedback_question_id = fq.id ")
				.append(" WHERE oh.id=order_header.id and ftt.feedback_type_name ='Default' )  as  default1, ")
				.append(" (	SELECT CONCAT(cast(ftt.id as char),',',cast(count(distinct ce.created) as char),',' ,cast(avg(star_value)as char)) FROM order_header oh ")
				.append(" JOIN `customer_experience` ce ON ce.order_header_id = oh.id ").append(" LEFT JOIN `users` u ON ce.users_id = u.id JOIN `smileys` s ON ce.smiley_id = s.id ")
				.append(" join feedback_type ftt on ftt.id=s.feedback_type_id ").append(" LEFT JOIN `users_to_feeback_details` uf ON uf.users_id = u.id ")
				.append(" LEFT JOIN feedback_field ff ON uf.feedback_details_id = ff.id ").append(" JOIN `feedback_question` fq ON ce.feedback_question_id = fq.id ")
				.append(" WHERE oh.id=order_header.id and ftt.feedback_type_name ='Custom' and oh.order_status_id Not In ")
				.append(" (select id from order_status where `name` in ('Void Order','Cancel Order') ) )  as  custom1,  amount_paid ")
				.append(" FROM order_header where users_id=?  order by updated desc limit 0,3 ").toString();
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, userId).getResultList();
		for (Object[] rs : resultList)
		{

			// if this has primary key not 0
			// if this has primary key not 0
			int i = 0;

			packets = new ArrayList<CustomerFeedbackHistoryPacket>();
			customerHistoryPacket = new CustomerHistoryPacket();
			customerHistoryPacket.setOrderId((String) rs[i++]);
			customerHistoryPacket.setUserId((String) rs[i++]);
			customerHistoryPacket.setGuestCount((int) rs[i++]);
			customerHistoryPacket.setDateOfVisit((String) rs[i++]);
			customerHistoryPacket.setAverageTotalAmount((BigDecimal) rs[i++]);
			if (rs[i] != null)
			{
				customerFeedbackHistoryPacket = new CustomerFeedbackHistoryPacket();
				String[] str = ((String) rs[i]).split(",");
				if (str[0] != null)
				{
					int feedbackTypeId = Integer.parseInt(str[0]);
					customerFeedbackHistoryPacket.setFeedbackTypeId(feedbackTypeId);

				}
				if (str[1] != null)
				{
					int totalFeedbackCount = Integer.parseInt(str[1]);
					customerFeedbackHistoryPacket.setTotalFeedbackCount(totalFeedbackCount);

				}
				if (str[2] != null)
				{
					double averageRating = Double.parseDouble(str[2]);
					customerFeedbackHistoryPacket.setRating(averageRating);

				}
				packets.add(customerFeedbackHistoryPacket);
			}
			i++;
			if (rs[i] != null)
			{
				customerFeedbackHistoryPacket = new CustomerFeedbackHistoryPacket();
				String[] str = ((String) rs[i]).split(",");
				if (str[0] != null)
				{
					int feedbackTypeId = Integer.parseInt(str[0]);
					customerFeedbackHistoryPacket.setFeedbackTypeId(feedbackTypeId);

				}
				if (str[1] != null)
				{
					int totalFeedbackCount = Integer.parseInt(str[1]);
					customerFeedbackHistoryPacket.setTotalFeedbackCount(totalFeedbackCount);

				}
				if (str[2] != null)
				{
					double averageRating = Double.parseDouble(str[2]);
					customerFeedbackHistoryPacket.setRating(averageRating);

				}
				packets.add(customerFeedbackHistoryPacket);
			}
			// for custome + default
			if (rs[5] != null && rs[6] != null)
			{
				customerFeedbackHistoryPacket = new CustomerFeedbackHistoryPacket();
				customerFeedbackHistoryPacket.setFeedbackTypeId(0);
				int totalFeedbackCount = 0;
				double averageRating = 0;
				for (CustomerFeedbackHistoryPacket customerFeedbackPacket : packets)
				{
					averageRating += customerFeedbackPacket.getRating();
					totalFeedbackCount += customerFeedbackPacket.getTotalFeedbackCount();
				}
				averageRating = averageRating / 2;
				customerFeedbackHistoryPacket.setRating(averageRating);
				customerFeedbackHistoryPacket.setTotalFeedbackCount(totalFeedbackCount);
				packets.add(customerFeedbackHistoryPacket);
			}
			i++;
			customerHistoryPacket.setAmountPaid((BigDecimal) rs[i]);
			customerHistoryPacket.setCustomerFeedbackHistoryPackets(packets);
			list.add(customerHistoryPacket);

		}

		customerOrderHistory.setCustomerHistoryPackets(list);

		return customerOrderHistory;

	}

	public void updateCustomerExperience(EntityManager em, CustomerFeedBackExperience customerFeedBackExperience)
	{
		customerFeedBackExperience.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(customerFeedBackExperience);
	}

	void addCustomerExperience(EntityManager em, CustomerFeedBackExperience customerFeedBackExperience, HttpServletRequest httpRequest, String sessionId,PostPacket packet) throws IOException, InvalidSessionException,
			Exception
	{
		User user = new User();
		if (customerFeedBackExperience.getUsersId() == null)
		{

			List<CustomerExperienceToUserDetail> customerExperienceToUserDetailsList = customerFeedBackExperience.getCustomerExperienceToUserDetailsList();
			for (CustomerExperienceToUserDetail customerExperienceToUserDetail : customerExperienceToUserDetailsList)
			{

				FeedbackField feedbackField = em.find(FeedbackField.class, customerExperienceToUserDetail.getFeedbackDetailsId());
				
				//TODO uzma check if feedbackField is null
				if ("Phone".equals(feedbackField.getFieldName()))
				{
					user.setPhone(customerExperienceToUserDetail.getDetailsValue());
				}
				else if ("Email".equals(feedbackField.getFieldName()))
				{
					user.setEmail(customerExperienceToUserDetail.getDetailsValue());
				}
				else if ("Date of birth".equals(feedbackField.getFieldName()))
				{
					user.setDateofbirth(customerExperienceToUserDetail.getDetailsValue());
				}
				else if ("First Name".equals(feedbackField.getFieldName()))
				{
					user.setFirstName(customerExperienceToUserDetail.getDetailsValue());
				}
				else if ("Last Name".equals(feedbackField.getFieldName()))
				{
					user.setLastName(customerExperienceToUserDetail.getDetailsValue());
				}
			}
		}
		customerFeedBackExperience.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		customerFeedBackExperience.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.persist(customerFeedBackExperience);
		List<CustomerExperienceToUserFeedback> CustomerExperienceToUserFeedbackList = customerFeedBackExperience.getCustomerExperienceToUserFeedbackList();
		//TODO uzma- handle CustomerExperienceToUserFeedbackList array is not send by user/ client
		for (CustomerExperienceToUserFeedback customerExperienceToUserFeedback : CustomerExperienceToUserFeedbackList)
		{
			customerExperienceToUserFeedback.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			customerExperienceToUserFeedback.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.persist(customerExperienceToUserFeedback);
		}
		List<CustomerExperienceToUserDetail> customerExperienceToUserDetailsList = customerFeedBackExperience.getCustomerExperienceToUserDetailsList();
		//TODO uzma- handle customerExperienceToUserDetailsList array is not send by user/ client
		for (CustomerExperienceToUserDetail customerExperienceToUserDetail : customerExperienceToUserDetailsList)
		{
			customerExperienceToUserDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			customerExperienceToUserDetail.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			em.persist(customerExperienceToUserDetail);
		}
		GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
		EntityManager globalEM = null;
		OrderHeader header = (OrderHeader) new CommonMethods().getObjectById("OrderHeader", em,OrderHeader.class, customerFeedBackExperience.getOrderHeaderId());
		
		try
		{
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			user = globalUsermanagement.addUserToLocalDatabaseProgramatically(httpRequest, globalEM, em, user, customerFeedBackExperience.getLocationsId(),packet);
			if (header != null && user != null && header.getUsersId() == null)
			{
				header.setUsersId(user.getId());
				if (header.getFirstName() == null && header.getLastName() == null)
				{
					header.setFirstName(user.getFirstName());
					header.setLastName(user.getLastName());
				}
				em.merge(header);
			}
		}
		//TODO uzma handlle try catch if user data of global is not merge with order header

		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
	}

	public List<CustomerExperiencePacket> getCustomerExperiencePacketByOrderIdAndLocationId(EntityManager em, String orderId, String locationId) throws NirvanaXPException
	{
		List<CustomerExperiencePacket> customerExperiencePackets = null;
		if (orderId != null)
		{
			String queryString = "select distinct c.created from customer_experience c where c.locations_id =? and c.order_header_id=?";
			Query query = em.createNativeQuery(queryString).setParameter(1, locationId).setParameter(2, orderId);
			@SuppressWarnings("unchecked")
			List<Object> resultSet = query.getResultList();

			if (resultSet != null && resultSet.size() > 0)
			{
				customerExperiencePackets = new ArrayList<CustomerExperiencePacket>();
				queryString = "select c from CustomerExperience c where c.locationsId =? and c.orderHeaderId=?";
				TypedQuery<CustomerExperience> query1 = em.createQuery(queryString, CustomerExperience.class).setParameter(1, locationId).setParameter(2, orderId);
				List<CustomerExperience> resultSetCustomerExperience = query1.getResultList();

				queryString = "select c from UsersToFeebackDetail c where c.orderHeaderId=?";
				TypedQuery<UsersToFeebackDetail> query2 = em.createQuery(queryString, UsersToFeebackDetail.class).setParameter(1, orderId);
				List<UsersToFeebackDetail> resultSetUsersToFeebackDetail = query2.getResultList();
				// List<Date> differentDates = new ArrayList<Date>();
				for (Object object : resultSet)
				{
					CustomerExperiencePacket customerExperiencePacket = new CustomerExperiencePacket();

					Set<UsersToFeebackDetail> details = null;
					List<CustomerExperience> customerExperiences = null;
					long date = ((Date) object).getTime();
					if (resultSetCustomerExperience != null)
					{
						customerExperiences = new ArrayList<CustomerExperience>();
						for (CustomerExperience customerExperience : resultSetCustomerExperience)
						{
							if (date == customerExperience.getCreated())
							{
								customerExperiences.add(customerExperience);
							}
						}
					}
					String userId = null;
					if (resultSetUsersToFeebackDetail != null)
					{
						details = new LinkedHashSet<UsersToFeebackDetail>();
						for (UsersToFeebackDetail usersToFeebackDetail : resultSetUsersToFeebackDetail)
						{
							if (date == usersToFeebackDetail.getCreated())
							{
								userId = usersToFeebackDetail.getUsersId();
								details.add(usersToFeebackDetail);
							}
						}
					}
					customerExperiencePacket.setCustomerExperience(customerExperiences);
					User user = new User();
					user.setId(userId);
					user.setUsersToFeebackDetail(details);
					customerExperiencePacket.setUser(user);
					customerExperiencePackets.add(customerExperiencePacket);
				}
				return customerExperiencePackets;

			}
		}
		else
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ORDERID_0_EXCEPTION, MessageConstants.ERROR_MESSAGE_ORDERID_0_EXCEPTION_DISPLAY_MESSAGE,
					MessageConstants.ERROR_MESSAGE_ORDERID_0_EXCEPTION_DISPLAY_MESSAGE));
		}

		return customerExperiencePackets;
	}

	public CustomerOrderHistory getCustomerHistoryByUserIdAndLocationId(HttpServletRequest httpRequest, EntityManager em, String userId,String locationId,int orderId) throws Exception
	{

		String sql = "";

		List<CustomerHistoryPacket> list = new ArrayList<CustomerHistoryPacket>();
		CustomerHistoryPacket customerHistoryPacket = null;
		CustomerOrderHistory customerOrderHistory = new CustomerOrderHistory();
		List<CustomerFeedbackHistoryPacket> packets = null;
		Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);
		sql = " SELECT id,users_id,point_of_service_count,date,total FROM order_header "
				+ "  where users_id=? and locations_id in (select id from locations "
				+ " where business_id =?) and id !=? order by id desc limit 0,5 ";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, userId).setParameter(2, location.getBusinessId()).setParameter(3, orderId).getResultList();
		for (Object[] rs : resultList)
		{
			int i = 0;
			packets = new ArrayList<CustomerFeedbackHistoryPacket>();
			customerHistoryPacket = new CustomerHistoryPacket();
			customerHistoryPacket.setOrderId((String) rs[i++]);
			customerHistoryPacket.setUserId((String) rs[i++]);
			customerHistoryPacket.setGuestCount((int) rs[i++]);
			customerHistoryPacket.setDateOfVisit((String) rs[i++]);
			customerHistoryPacket.setAverageTotalAmount((BigDecimal) rs[i++]);
			customerHistoryPacket.setCustomerFeedbackHistoryPackets(packets);
			list.add(customerHistoryPacket);

		}

		customerOrderHistory.setCustomerHistoryPackets(list);

		return customerOrderHistory;

	}

	
}
