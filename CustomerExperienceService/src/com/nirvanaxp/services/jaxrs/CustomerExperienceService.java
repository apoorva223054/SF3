/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.CustomerExperiencePacket;
import com.nirvanaxp.services.jaxrs.packets.CustomerExperienceServicePacket;
import com.nirvanaxp.services.jaxrs.packets.CustomerOrderHistory;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToFeebackDetail;
import com.nirvanaxp.types.entities.user.experience.CustomerExperience;
import com.nirvanaxp.types.entities.user.experience.CustomerExperience_;
import com.nirvanaxp.user.utility.GlobalUsermanagement;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomerExperienceService.
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CustomerExperienceService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The Constant logger. */
	private final static NirvanaLogger logger = new NirvanaLogger(CustomerExperienceService.class.getName());

	//
	// private int createdBy = 0;
	// private int updatedBy = 0;
	// private String locationId = 0;

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
	 * Gets the experience list by user id.
	 *
	 * @param userId the user id
	 * @param sessionId the session id
	 * @return the experience list by user id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getUserExperiencesByUserId/{userid}")
	public String getExperienceListByUserId(@PathParam("userid") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;

		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<CustomerExperience> criteria = builder.createQuery(CustomerExperience.class);
			Root<CustomerExperience> r = criteria.from(CustomerExperience.class);
			TypedQuery<CustomerExperience> query = em.createQuery(criteria.select(r).where(new Predicate[]
			{ builder.equal(r.get(CustomerExperience_.usersId), userId) }));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the experience list by user id and order ID.
	 *
	 * @param userId the user id
	 * @param orderHeaderID the order header ID
	 * @param created the created
	 * @param sessionId the session id
	 * @return the experience list by user id and order ID
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getExperienceListByUserIdAndOrderID/{userid}/{orderHeaderID}/{created}")
	public String getExperienceListByUserIdAndOrderID(@PathParam("userid") String userId, @PathParam("orderHeaderID") String orderHeaderID, @PathParam("created") String created,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			List<CustomerExperience> list = new ArrayList<CustomerExperience>();

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CustomerExperience customerExperience = null;

			String sql = " select customer.id , customer.users_id ,customer.locations_id, customer.order_header_id ,"
					+ " customer.feedback_question_id ,customer.smiley_id ,"
					+ " customer.comments ,customer.created ,customer.created_by , "
					+ "customer.updated , customer.updated_by ,feedback.id as feedback_feedback_id ,"
					+ " feedback.feedback_question as feedback_feedback_question , "
					+ "feedback.feedback_type_id as feedback_feedback_type_id , "
					+ "feedback.status as feedback_status ,"
					+ " feedback.display_sequence as feedback_display_sequence ,"
					+ " feedback.locations_id as feedback_locations_id ,feedback.created as feedback_created ,"
					+ " feedback.created_by as feedback_created_by ,"
					+ "feedback.updated as feedback_updated,"
					+ " feedback.updated_by as feedback_updated_by ,smiley.id as  smiley_smiley_id,"
					+ " smiley.simley_name as  simley_name,"
					+ "smiley.image_name as  smiley_image_name, smiley.feedback_type_id as  smiley_feedback_type_id,"
					+ "smiley.star_value as  smiley_star_value,"
					+ " smiley.created as  smiley_created,smiley.created_by as  smiley_created_by,"
					+ " smiley.updated as  smiley_updated,smiley.updated_by as  smiley_updated_by,'' "
					+ " from customer_experience customer left join feedback_question feedback on customer.feedback_question_id =feedback.id "
					+ " left join smileys smiley on smiley.id = customer.smiley_id WHERE  customer.order_header_id=?" + " and customer.users_id=?" + " and customer.created=?";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, orderHeaderID).setParameter(2, userId).setParameter(3, created).getResultList();
			for (Object[] objRow : resultList)
			{
				customerExperience = new CustomerExperience(objRow);
				list.add(customerExperience);
			}

			return new JSONUtility(httpRequest).convertToJsonString(list);
		}
		finally
		{

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}
	}

	/**
	 * Gets the location id.
	 *
	 * @param customerExperience the customer experience
	 * @return the location id
	 */
	private String getLocationId(CustomerExperience customerExperience)
	{
		String locationId = null;
		if (customerExperience != null)
		{
			locationId = customerExperience.getLocationsId();
		}

		if (locationId == null /*|| locationId == -1*/)
		{
			throw new IllegalArgumentException("No info on location Id for customer experience");
		}

		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	/**
	 * Gets the created by.
	 *
	 * @param customerExperience the customer experience
	 * @return the created by
	 */
	private String getCreatedBy(CustomerExperience customerExperience)
	{
		String createdBy = null;
		if (customerExperience != null)
		{

			createdBy = customerExperience.getCreatedBy();

		}

		if (createdBy == null )
		{
			throw new IllegalArgumentException("No info on created by for customer experience");
		}

		return createdBy;
	}

	/**
	 * Gets the updated by.
	 *
	 * @param customerExperience the customer experience
	 * @return the updated by
	 */
	private String getUpdatedBy(CustomerExperience customerExperience)
	{
		String updatedBy =null;
		if (customerExperience != null)
		{

			updatedBy = customerExperience.getUpdatedBy();

		}

		if (updatedBy == null)
		{
			throw new IllegalArgumentException("No info on updated by for customer experience");
		}

		return updatedBy;
	}

	/**
	 * Adds the customer experience.
	 *
	 * @param customerExperiencePacket the customer experience packet
	 * @param sessionId the session id
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/add")
	public String add(CustomerExperiencePacket customerExperiencePacket) throws Exception
	{
		// sessionId = "3d7f1c36124cb911266c7379b277268e";
		EntityManager em = null;
		long timeInMilis = new TimezoneTime().getGMTTimeInMilis();
		try
		{

			if (customerExperiencePacket == null)
			{
				throw new IllegalArgumentException("No Customer Experience Data Sent to Service");
			}

			if (customerExperiencePacket.getCustomerExperience() == null)
			{
				throw new IllegalArgumentException("No Customer Experience Data Sent to Service");
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CustomerExperienceServiceBean bean = new CustomerExperienceServiceBean();
			Set<UsersToFeebackDetail> usersToFeebackDetails = null;
			User user = customerExperiencePacket.getUser();
			logger.severe("user=================================================================================="+user);
			if (user != null &&   user.getUsersToFeebackDetail() != null && user.getUsersToFeebackDetail().size() > 0)
			{
				usersToFeebackDetails = user.getUsersToFeebackDetail();
			}
			String phone = user.getPhone();
			String email = user.getEmail();

			boolean phoneNotAvailable = (phone == null || phone.trim().isEmpty());
			boolean emailNotAvailable = (email == null || email.trim().isEmpty());

			// check if phone and email are both blank then we need to fetch
			// user based on reservation id sent by user, this happens when user
			// does not fill the form
			if (phoneNotAvailable && emailNotAvailable && (customerExperiencePacket.getReservationId()!= null && customerExperiencePacket.getReservationId()!=null))
			{
				// we fetch user id using reservation id
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, em, customerExperiencePacket.getReservationId());

				addCustomerExperienceIntoDatabase(em, reservation.getUsersId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);

				addCustomerFeedbackIntoDatabase(em, reservation.getUsersId(), usersToFeebackDetails, bean, timeInMilis, customerExperiencePacket.getCustomerExperience().get(0).getLocationsId());

				return "1";

			}

			// we fetch user id using user id - this condition is for takeout
			if (phoneNotAvailable && emailNotAvailable && (user.getId()!= null))
			{

				addCustomerExperienceIntoDatabase(em, user.getId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);
				addCustomerFeedbackIntoDatabase(em, user.getId(), usersToFeebackDetails, bean, timeInMilis, customerExperiencePacket.getCustomerExperience().get(0).getLocationsId());
				return "1";
			}

			if (phoneNotAvailable && emailNotAvailable && (user.getId() == null) )
			{
				// find the guest user in that database and add user id
				// accordingly
				// if feedback sent from walkin and no user info is there then
				// this condition occur
				User guestUser = new User();
				if (guestUser != null)
				{
					addCustomerExperienceIntoDatabase(em, guestUser.getId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);

					// addCustomerFeedbackIntoDatabase(em, guestUser.getId(),
					// usersToFeebackDetails, bean, timeInMilis);
					return "1";
				}

			}

			else
			{

				if (customerExperiencePacket.getCustomerExperience() != null && customerExperiencePacket.getCustomerExperience().size() > 0)
				{
					String createdBy = customerExperiencePacket.getCustomerExperience().get(0).getCreatedBy();
					String updatedBy = customerExperiencePacket.getCustomerExperience().get(0).getUpdatedBy();
					String locationId = customerExperiencePacket.getCustomerExperience().get(0).getLocationsId();

					User localUser = createLocalUser(user, createdBy, updatedBy);
					// for walkin order :- inserting userid of feedback user
					OrderHeader header = getOrderHeaderByReservationId(em, customerExperiencePacket.getReservationId());

					GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
					EntityManager globalEM = null;
					try
					{
						globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

						localUser = globalUsermanagement.addUserToLocalDatabaseProgramatically(httpRequest, globalEM, em, localUser, locationId,customerExperiencePacket);
						if (header != null && localUser != null && (header.getUsersId() == null || header.getUsersId().length()==0))
						{
							header.setUsersId(localUser.getId());
							// Added by Apoorva July 23, 2015 :-28271
							if (header.getFirstName() == null && header.getLastName() == null)
							{
								header.setFirstName(localUser.getFirstName());
								header.setLastName(localUser.getLastName());
								;
							}
							GlobalSchemaEntityManager.merge(em, header);
						}
					}
					finally
					{
						GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
					}

					addCustomerExperienceIntoDatabase(em, localUser.getId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);

					addCustomerFeedbackIntoDatabase(em, localUser.getId(), usersToFeebackDetails, bean, timeInMilis, customerExperiencePacket.getCustomerExperience().get(0).getLocationsId());

				}

			}
			String json = new StoreForwardUtility().returnJsonPacket(customerExperiencePacket, "CustomerExperiencePacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					customerExperiencePacket.getLocationId(), Integer.parseInt(customerExperiencePacket.getMerchantId()));

			return "1";
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Creates the local user.
	 *
	 * @param user the user
	 * @param createdBy the created by
	 * @param updatedBy the updated by
	 * @return the user
	 * @throws Exception the exception
	 */
	private User createLocalUser(User user, String createdBy, String updatedBy) throws Exception
	{
		// create new user
		User newUser = new User();

		if (user.getFirstName() != null)
		{
			newUser.setFirstName(user.getFirstName());
		}
		else
		{
			newUser.setFirstName("");
		}

		if (user.getLastName() != null)
		{
			newUser.setLastName(user.getLastName());
		}
		else
		{
			newUser.setLastName("");
		}

		if (user.getDateofbirth() != null)
		{
			newUser.setDateofbirth(user.getDateofbirth());
		}
		else
		{
			newUser.setDateofbirth("");
		}

		if (user.getPhone() != null)
		{
			newUser.setPhone(user.getPhone());
		}
		else
		{
			newUser.setPhone("");
		}

		if (user.getEmail() != null)
		{
			newUser.setEmail(user.getEmail());
		}
		else
		{
			newUser.setEmail("");
		}
		newUser.setCountryId(user.getCountryId());
		newUser.setCreatedBy(createdBy);
		newUser.setUpdatedBy(updatedBy);

		// set encrypted password
		String sha512Password = null;

		sha512Password = DigestUtils.sha512Hex(generatePassword(8).getBytes(Charset.forName("UTF-8")));
		newUser.setPassword(sha512Password);

		if (user.getPhone() != null && (!user.getPhone().equals("")))
			newUser.setUsername(user.getPhone());
		else
			newUser.setUsername(user.getEmail());

		newUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		newUser.setStatus("A");

		return newUser;

	}

	/**
	 * Generate password.
	 *
	 * @param length the length
	 * @return the string
	 */
	private static String generatePassword(int length)
	{
		final String charset = "0123456789" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		Random rand = new Random(new TimezoneTime().getGMTTimeInMilis());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <= length; i++)
		{
			int pos = rand.nextInt(charset.length());
			sb.append(charset.charAt(pos));
		}
		return sb.toString();
	}

	/**
	 * Adds the customer feedback into database.
	 *
	 * @param em the em
	 * @param userId the user id
	 * @param feedbackList the feedback list
	 * @param bean the bean
	 * @param timeInMilis the time in milis
	 * @param locationId the location id
	 * @throws Exception the exception
	 */
	private void addCustomerFeedbackIntoDatabase(EntityManager em, String userId, Set<UsersToFeebackDetail> feedbackList, CustomerExperienceServiceBean bean, long timeInMilis, String locationId)
			throws Exception
	{

		if (feedbackList != null)
		{

			for (UsersToFeebackDetail usersToFeedback : feedbackList)
			{
				usersToFeedback.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
				usersToFeedback.setCreated(timeInMilis);
				usersToFeedback.setUpdated(timeInMilis);
				usersToFeedback.setUsersId(userId);
				bean.addUsersToFeedbackDetail(em, usersToFeedback);
			}
		}
	}

	/**
	 * Adds the customer experience into database.
	 *
	 * @param em the em
	 * @param userId the user id
	 * @param customerExperiencesList the customer experiences list
	 * @param bean the bean
	 * @param timeInMilis the time in milis
	 * @throws Exception the exception
	 */
	private void addCustomerExperienceIntoDatabase(EntityManager em, String userId, List<CustomerExperience> customerExperiencesList, CustomerExperienceServiceBean bean, long timeInMilis)
			throws Exception
	{

		if (customerExperiencesList != null)
		{
			for (CustomerExperience customerExperience : customerExperiencesList)
			{
				getLocationId(customerExperience);
				getCreatedBy(customerExperience);
				getUpdatedBy(customerExperience);
				customerExperience.setCreated(timeInMilis);

				customerExperience.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(customerExperience.getLocationsId(), em));
				customerExperience.setUpdated(timeInMilis);
				// if condition to check whether userid is setting to 0
				if (userId!= null)
				{
					customerExperience.setUsersId(userId);
				}
				// TODO uzma- handle add exception for user
				bean.add(em, customerExperience, timeInMilis);
			}
		}
	}

	/**
	 * Gets the all customer experience by user ID.
	 *
	 * @param userID the user ID
	 * @param sessionId the session id
	 * @return the all customer experience by user ID
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getAllCustomerExperienceByUserID/{userID}")
	public String getAllCustomerExperienceByUserID(@PathParam("userID") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		List<CustomerExperience> list = new ArrayList<CustomerExperience>();
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = " select * from (select customer.id , customer.users_id ,customer.locations_id ," + " customer.order_header_id ,customer.feedback_question_id ,customer.smiley_id ,"
					+ " customer.comments ,customer.created ,customer.created_by ,customer.updated ," + " customer.updated_by ,feedback.id as feedback_feedback_id ,"
					+ " feedback.feedback_question as feedback_feedback_question ," + " feedback.feedback_type_id as feedback_feedback_type_id ,feedback.status as feedback_status ,"
					+ " feedback.display_sequence as feedback_display_sequence ," + " feedback.locations_id as feedback_locations_id ,feedback.created as feedback_created ,"
					+ " feedback.created_by as feedback_created_by ,feedback.updated as feedback_updated ," + " feedback.updated_by as feedback_updated_by ,smiley.id as  smiley_smiley_id,"
					+ " smiley.simley_name as  simley_name,smiley.image_name as  smiley_image_name," + " smiley.feedback_type_id as  smiley_feedback_type_id,smiley.star_value as  smiley_star_value,"
					+ " smiley.created as  smiley_created,smiley.created_by as  smiley_created_by," + " smiley.updated as  smiley_updated,smiley.updated_by as  smiley_updated_by "
					+ " from customer_experience customer " + " left join feedback_question feedback on customer.feedback_question_id =feedback.id "
					+ " left join smileys smiley on smiley.id = customer.smiley_id WHERE  customer.users_id =?  ) temp";
			// String queryString =
			// "Select * from customer_experience where users_id= "+userID;
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, userId).getResultList();
			for (Object[] objRow : resultList)
			{
				try
				{
					CustomerExperience customerExperience = new CustomerExperience(objRow);
					list.add(customerExperience);
				}
				catch (Exception e)
				{
					logger.severe(e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(list);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	// private User getGuestUser(EntityManager em)
	// {
	// try
	// {
	// String guest = "guest";
	// CriteriaBuilder builder = em.getCriteriaBuilder();
	// CriteriaQuery<User> criteria = builder.createQuery(User.class);
	// Root<User> root = criteria.from(User.class);
	// TypedQuery<User> query =
	// em.createQuery(criteria.select(root).where(builder.equal(root.get(User_.username),
	// guest)));
	// User result = query.getSingleResult();
	// return result;
	// }
	// catch (NoResultException noResultException)
	// {
	// logger.info(httpRequest,
	// "No result found when searching for a guest user.");
	// return null;
	// }
	// }

	/**
	 * Gets the customer history by user id.
	 *
	 * @param userId the user id
	 * @param sessionId the session id
	 * @return the customer history by user id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getCustomerHistoryByUserId/{userId}")
	public String getCustomerHistoryByUserId(@PathParam("userId") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			CustomerExperienceServiceBean bean = new CustomerExperienceServiceBean();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CustomerOrderHistory customerHistory = bean.getCustomerHistoryByUserId(httpRequest, em, userId);
			return new JSONUtility(httpRequest).convertToJsonString(customerHistory);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the order header by reservation id.
	 *
	 * @param em the em
	 * @param reservationId the reservation id
	 * @return Order Header object
	 * @throws Exception the exception
	 */
	private OrderHeader getOrderHeaderByReservationId(EntityManager em, String reservationId) throws Exception
	{
		OrderHeader header = null;
		try
		{
			String queryString = "select o from OrderHeader o where o.reservationsId=?";
			TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class).setParameter(1, reservationId);
			header = query.getSingleResult();
		}
		catch (Exception e)
		{

			logger.severe(httpRequest, "Order Header not present in database");
		}
		return header;
	}

	/**
	 * Adds the customer experience.
	 *
	 * @param customerExperiencePacket the customer experience packet
	 * @param sessionId the session id
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/addCustomerExperience")
	public String addCustomerExperience(CustomerExperienceServicePacket customerExperiencePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			if (customerExperiencePacket == null)
			{
				throw new IllegalArgumentException("No Customer Experience Data Sent to Service");
			}

			if (customerExperiencePacket.getCustomerFeedBackExperience() == null)
			{
				throw new IllegalArgumentException("No Customer Experience Data Sent to Service");
			}

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, customerExperiencePacket);
			tx = em.getTransaction();
			tx.begin();
			CustomerExperienceServiceBean customerExperienceServiceBean = new CustomerExperienceServiceBean();
			customerExperienceServiceBean.addCustomerExperience(em, customerExperiencePacket.getCustomerFeedBackExperience(), httpRequest, sessionId,customerExperiencePacket);
			customerExperiencePacket.setCustomerFeedBackExperience(customerExperiencePacket.getCustomerFeedBackExperience());
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(customerExperiencePacket, "CustomerExperiencePacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					customerExperiencePacket.getLocationId(), Integer.parseInt(customerExperiencePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(customerExperiencePacket);

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
	 * Update customer experience.
	 *
	 * @param customerExperiencePacket the customer experience packet
	 * @param sessionId the session id
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/updateCustomerExperience")
	public String updateCustomerExperience(CustomerExperienceServicePacket customerExperiencePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		// TODO uzma handle try catch for user define error

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, customerExperiencePacket);
			tx = em.getTransaction();
			tx.begin();
			CustomerExperienceServiceBean customerExperienceServiceBean = new CustomerExperienceServiceBean();
			customerExperienceServiceBean.updateCustomerExperience(em, customerExperiencePacket.getCustomerFeedBackExperience());
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(customerExperiencePacket, "CustomerExperiencePacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, 
					customerExperiencePacket.getLocationId(), Integer.parseInt(customerExperiencePacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(customerExperiencePacket);

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
	 * Gets the customer experience packet by order id and location id.
	 *
	 * @param orderHeaderID the order header ID
	 * @param locationId the location id
	 * @param sessionId the session id
	 * @return the customer experience packet by order id and location id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getCustomerExperiencePacketByOrderIdAndLocationId/{orderHeaderID}/{locationId}")
	public String getCustomerExperiencePacketByOrderIdAndLocationId(@PathParam("orderHeaderID") String orderHeaderID, @PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CustomerExperienceServiceBean customerExperience = new CustomerExperienceServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(customerExperience.getCustomerExperiencePacketByOrderIdAndLocationId(em, orderHeaderID, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}


	/**
	 * Gets the customer history by user id.
	 *
	 * @param userId the user id
	 * @param sessionId the session id
	 * @return the customer history by user id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getCustomerHistoryByUserIdLocationId/{userId}/{locationId}/{orderId}")
	public String getCustomerHistoryByUserId(@PathParam("userId") String userId, @PathParam("locationId") String locationId,@PathParam("orderId") int orderId) throws Exception
	{
		EntityManager em = null;
		try
		{
			CustomerExperienceServiceBean bean = new CustomerExperienceServiceBean();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CustomerOrderHistory customerHistory = bean.getCustomerHistoryByUserIdAndLocationId(httpRequest, em, userId,locationId,orderId);
			return new JSONUtility(httpRequest).convertToJsonString(customerHistory);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

}
