/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.CustomerExperiencePacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.User_;
import com.nirvanaxp.types.entities.user.UsersToFeebackDetail;
import com.nirvanaxp.types.entities.user.experience.CustomerExperience;
import com.nirvanaxp.user.utility.GlobalUsermanagement;

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
		String createdBy =null;
		if (customerExperience != null)
		{

			createdBy = customerExperience.getCreatedBy();

		}

		if (createdBy == null)
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
		String updatedBy = null;;
		if (customerExperience != null)
		{

			updatedBy = customerExperience.getUpdatedBy();

		}

		if (updatedBy == null )
		{
			throw new IllegalArgumentException("No info on updated by for customer experience");
		}

		return updatedBy;
	}

	/**
	 * Adds the.
	 *
	 * @param customerExperiencePacket the customer experience packet
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
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

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

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			CustomerExperienceServiceBean bean = new CustomerExperienceServiceBean();
			Set<UsersToFeebackDetail> usersToFeebackDetails = null;
			User user = customerExperiencePacket.getUser();
			if (user != null && user.getUsersToFeebackDetail() != null && user.getUsersToFeebackDetail().size() > 0)
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
			if (phoneNotAvailable && emailNotAvailable && (customerExperiencePacket.getReservationId()!=null && customerExperiencePacket.getReservationId()!=null))
			{
				// we fetch user id using reservation id
				Reservation reservation = new CommonMethods().getReservationById(httpRequest, em, customerExperiencePacket.getReservationId());


				addCustomerExperienceIntoDatabase(em, reservation.getUsersId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);

				addCustomerFeedbackIntoDatabase(em, reservation.getUsersId(), usersToFeebackDetails, bean, timeInMilis,customerExperiencePacket.getCustomerExperience().get(0).getLocationsId());

				return "1";

			}

			// we fetch user id using user id - this condition is for
			// takeout
			if (phoneNotAvailable && emailNotAvailable && (user.getId() != null))
			{

				addCustomerExperienceIntoDatabase(em, user.getId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);
				addCustomerFeedbackIntoDatabase(em, user.getId(), usersToFeebackDetails, bean, timeInMilis,customerExperiencePacket.getCustomerExperience().get(0).getLocationsId());
				return "1";
			}

			if (phoneNotAvailable && emailNotAvailable && (user.getId() == null))
			{
				// find the guest user in that database and add user id
				// accordingly
				// if feedback sent from walkin and no user info is there then this condition occur
				User guestUser = new User();
				if (guestUser != null)
				{
					addCustomerExperienceIntoDatabase(em, guestUser.getId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);

					addCustomerFeedbackIntoDatabase(em, guestUser.getId(), usersToFeebackDetails, bean, timeInMilis,customerExperiencePacket.getCustomerExperience().get(0).getLocationsId());
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
						globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

						localUser = globalUsermanagement.addUserToLocalDatabaseProgramatically(httpRequest, globalEM, em, localUser, locationId,customerExperiencePacket);
						if(header != null && localUser!=null && header.getUsersId() == null){
							header.setUsersId(localUser.getId());
							// Added by Apoorva July 23, 2015 :-28271
							if(header.getFirstName()==null && header.getLastName()==null ){
								header.setFirstName(localUser.getFirstName());
								header.setLastName(localUser.getLastName());;
							}
							GlobalSchemaEntityManager.merge(em, header);
						}
					}
					finally
					{
						GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
					}

					addCustomerExperienceIntoDatabase(em, localUser.getId(), customerExperiencePacket.getCustomerExperience(), bean, timeInMilis);

					addCustomerFeedbackIntoDatabase(em, localUser.getId(), usersToFeebackDetails, bean, timeInMilis,customerExperiencePacket.getCustomerExperience().get(0).getLocationsId());

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
	private void addCustomerFeedbackIntoDatabase(EntityManager em, String userId, Set<UsersToFeebackDetail> feedbackList, CustomerExperienceServiceBean bean, long timeInMilis, String locationId) throws Exception
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
				if(userId != null){
					customerExperience.setUsersId(userId);
				}
				
				bean.add(em, customerExperience, timeInMilis);
			}
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
	private OrderHeader getOrderHeaderByReservationId(EntityManager em,String reservationId) throws Exception{
		OrderHeader header = null;
		try {
			String queryString = "select o from OrderHeader o where o.reservationsId=?";
				TypedQuery<OrderHeader> query = em.createQuery(queryString, OrderHeader.class).setParameter(1, reservationId);
				header = query.getSingleResult();
		} catch (Exception e) {
			
			logger.severe(httpRequest, "Order Header not present in database");
		}
	return header;
	}
}
