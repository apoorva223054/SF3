/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.Utilities;
import com.nirvanaxp.common.utils.manageslots.ManageSlotsUtils;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.PrinterUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.data.ReservationWithUser;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.util.email.EmailTemplateKeys;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.custom.ReservationsSearchCriteria;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderHeader_;
import com.nirvanaxp.types.entities.orders.OrderSource;
import com.nirvanaxp.types.entities.orders.OrderStatus;
import com.nirvanaxp.types.entities.orders.OrderStatus_;
import com.nirvanaxp.types.entities.reservation.ContactPreference;
import com.nirvanaxp.types.entities.reservation.RequestType;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.ReservationSlotActiveClientInfo;
import com.nirvanaxp.types.entities.reservation.Reservation_;
import com.nirvanaxp.types.entities.reservation.ReservationsHistory;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
import com.nirvanaxp.types.entities.reservation.ReservationsSlot;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus;
import com.nirvanaxp.types.entities.reservation.ReservationsStatus_;
import com.nirvanaxp.types.entities.reservation.ReservationsType;
import com.nirvanaxp.types.entities.reservation.ReservationsType_;
import com.nirvanaxp.types.entities.time.Timezone;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.user.utility.GlobalUsermanagement;

// TODO: Auto-generated Javadoc
/**
 * Session Bean implementation class ReservationServiceBean.
 */
public class ReservationServiceBean
{

	/**  */
	// private User userForPush;
	private static final NirvanaLogger logger = new NirvanaLogger(ReservationServiceBean.class.getName());

	/**  */
	private static final int sessionClearTimeInMinutes = 10;

	/**  */
	private int reservationTypeId = 0;

	/**  */
	private String locationId = null;

	/**  */
	private String reservationStatusId = null;

	/**  */
	private String contactPreferenceId1 = null;

	/**  */
	private String contactPreferenceId2 = null;

	/**  */
	private String contactPreferenceId3 = null;

	/**  */
	private String requestTypeId = null;

	/**  */
	private String selectClause =  new CommonMethods().SQL_SELECT_CLAUSE;

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param r
	 * @param globalUserId
	 * @param sessionId
	 * @param idOfClientHoldingTheSlot
	 * @param webSiteUrl
	 * @param packet
	 * @param isOnline
	 * @return
	 * @throws Exception
	 */
	public ReservationWithUser add(HttpServletRequest httpRequest, EntityManager em, Reservation r, String globalUserId, String sessionId, int idOfClientHoldingTheSlot, String webSiteUrl,
			PostPacket packet, boolean isOnline) throws Exception
	{
		// todo modular coding
		User newLocalUser = null;
		// find reservation type object
		// to get previous build supported
		locationId = packet.getLocationId();
		setRelationshipValues(r);
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ReservationsType> criteria = builder.createQuery(ReservationsType.class);
		Root<ReservationsType> rReservationsType = criteria.from(ReservationsType.class);
		TypedQuery<ReservationsType> query = em.createQuery(criteria.select(rReservationsType).where(builder.equal(rReservationsType.get(ReservationsType_.id), reservationTypeId)));

	 
		
		ReservationsType reservationsType = query.getSingleResult();

		ReservationSlotActiveClientInfo reservationSlotActiveClientInfo = null;
		int fromWalikInOrWaitList = 0;
		if(packet.getLocalServerURL()==0){
			if ( "Reservation".equals(reservationsType.getName()))
			{
			 	reservationSlotActiveClientInfo = checkActiveClientForReservationSlot(httpRequest, em, r, idOfClientHoldingTheSlot);
			}
			else if ("Waitlist".equals(reservationsType.getName()))
			{
				setDateAndTimeForWaitlist(httpRequest, em, r);

			}
			else if ("Walk in".equals(reservationsType.getName()))
			{
				// do nothing
			}
			else
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_RESERVATION_TYPE_UNKNOWN, MessageConstants.ERROR_MESSAGE_RESERVATION_TYPE_UNKNOWN,
						"Unknown  reservation type: " + reservationsType.getName()));
			}
		}
		

		// now put into database for client info table that we are
		// now about to use this session for adding a reservation and we
		// will now handle the slot change
		if (("Reservation".equals(reservationsType.getName()) || "Waitlist".equals(reservationsType.getName())) && (r.getUsersId()==null||r.getUsersId().length()==0))
		{
			newLocalUser = relateUserToReservation(httpRequest, sessionId, em, globalUserId, r,packet);
		}

		// update visit count
		User user = (User) new CommonMethods().getObjectById("User", em,User.class, r.getUsersId());
		if (user != null)
		{
			r.setVisitCount(user.getVisitCount());
		}

		if(locationId!=null){
		r.setLocalTime(new TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
		}
		r.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		r.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if (r.getCreatedBy()==null)
		{
			r.setCreatedBy(r.getUsersId());

		}
		if (r.getUpdatedBy()==null)
		{
			r.setUpdatedBy(r.getUsersId());
		}

		Location l = null;
		if (locationId!=null )
		{
			// to do what if location object is null it will throw null pointer exception
			l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;

		}
		if (r.getReservationSource().equals("Business App"))
		{
			// setting reference number in reservation object
			EntityManager globalEM = null;
			try
			{
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
				OrderManagementServiceBean bean = new OrderManagementServiceBean();

				String refNo = bean.getBusinessWithRefrenceNumber(globalEM, l.getBusinessId());
				if (refNo != null)
				{
					r.setReferenceNumber(refNo);
				}
				else
				{
					logger.severe(httpRequest, MessageConstants.ERROR_MESSAGE_NO_RESULT_FOUND_FOR_REFERENCE_NUMBER);
				}
			}
			finally
			{
				GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			}

		}
		// now insert reservation
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(r.getDate() + " " + r.getTime());

		r.setReservationDateTime(date);
		if (r.getLocation() != null)
		{
			r.setLocationId(r.getLocation().getId());
		}
		r.setReservationsTypeId(reservationTypeId);
		r.setReservationsStatusId(reservationStatusId);
		r.setRequestTypeId(requestTypeId);
		r.setContactPreferenceId1(contactPreferenceId1);
		r.setContactPreferenceId2(contactPreferenceId2);
		r.setContactPreferenceId3(contactPreferenceId3);
		r.setUsersId(r.getUsersId());
		// removed single transaction code
		if(r.getId() ==null){
			String id=new StoreForwardUtility().generateDynamicBigIntId(em, locationId, httpRequest, "reservations");
			r.setId(id);
		}
		
		r = em.merge(r);

		// Change by uzma for enter the value in reservation history
		// find reservation
		Reservation res_histroy =r;

		if (res_histroy != null)
		{
			// insert into reservation history
			new InsertIntoHistory().insertReservationIntoHistory(httpRequest, res_histroy, em);
		}

		// -------------------END--------------------------------

		if ("Reservation".equals(reservationsType.getName()))
		{

			if (r.getReservationSource().equals("Business App"))
			{
				try
				{
					webSiteUrl = ConfigFileReader.getRservationUrl() + r.getReferenceNumber() + "&reservation_id=";
				}
				catch (Exception e)
				{
					logger.severe(e);
				}
			}
			// sending reservation confirmation email to customer
			if (r != null)
			{
				EmailTemplateKeys.sendReservationEmailToCustomer(httpRequest, em, r, l, webSiteUrl, EmailTemplateKeys.RESERVATION_CREATED, true);
			}

			// SendMail.sendEmailToCustomer(httpRequest, r, l,
			// webSiteUrl,ConfigFileReader.CONFIG_FILE_NAME_FOR_RESERVATION_SMTP);

			// now that the reservation has been created
			// successfully,
			// increase the count in the appropriate reservation
			// slot
			if(packet.getLocalServerURL()==0){
				ManageSlotsUtils.updateReservationSlotCurrentActiveReservationCount(httpRequest, em, reservationSlotActiveClientInfo.getReservationSlotId(), r, fromWalikInOrWaitList, true);
				// remove the client holding the slot from database as
				// it
				// has made a reservation and can no longer use this slot
				// session again
				// remove it from the database
			 	em.remove(em.merge(reservationSlotActiveClientInfo));
			}
		

			

		}
		else if ("Waitlist".equals(reservationsType.getName()))
		{

			if (r.getReservationSource().equals("Business App"))
			{
				webSiteUrl = ConfigFileReader.getRservationUrl() + r.getReferenceNumber() + "&reservation_id=";
			}
			// sending mail to customer
			if (r != null &&  isOnline && packet.getLocalServerURL()==0)
			{
				EmailTemplateKeys.sendReservationEmailToCustomer(httpRequest, em, r, l, webSiteUrl, EmailTemplateKeys.RESERVATION_CREATED, false);
			}

		}
		// returning limited object to clients as they want limited fields
		Reservation r2 = new Reservation();
		r2.setId(r.getId());
		r2.setDate(r.getDate());
		ReservationsType reservationsType2 = em.find(ReservationsType.class, reservationTypeId);
		r2.setReservationsType(reservationsType2);
		r2.setTime(r.getTime());
		r2.setUpdatedBy(r.getUpdatedBy());
		r2.setCreatedBy(r.getCreatedBy());
		r2.setUsersId(r.getUsersId());
		r2.setVisitNumber(r.getVisitNumber());
		r2.setVisitNumber(r.getVisitCount());
		r2.setVisitCount(r.getVisitCount());
		r2.setReservationsTypeId(reservationTypeId);

		return new ReservationWithUser(r2, r,newLocalUser);

	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param r
	 * @param idOfClientHoldingTheSlot
	 * @return
	 * @throws NirvanaXPException
	 */
	private ReservationSlotActiveClientInfo checkActiveClientForReservationSlot(HttpServletRequest httpRequest, EntityManager em, Reservation r, int idOfClientHoldingTheSlot) throws NirvanaXPException
	{
		ReservationSlotActiveClientInfo reservationSlotActiveClientInfo = null;
		
		if (idOfClientHoldingTheSlot == 0)
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_RESERVATION_NO_ACTIVE_CLIENT_ID_AVAILABLE_EXCEPTION,
					MessageConstants.ERROR_MESSAGE_RESERVATION_NO_ACTIVE_CLIENT_ID_AVAILABLE_DISPLAY_MESSAGE, null));
		}
		reservationSlotActiveClientInfo = em.find(ReservationSlotActiveClientInfo.class, idOfClientHoldingTheSlot);
		reservationSlotActiveClientInfo.setUpdatedBy(r.getUpdatedBy());

		// todo method mentioned below always returning true, so we need to correct it
		shouldAllowAddingReservation(httpRequest, em, reservationSlotActiveClientInfo, r);

		return reservationSlotActiveClientInfo;

	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param reservationSlotActiveClientInfo
	 * @param r
	 * @return
	 * @throws NirvanaXPException
	 */
	private boolean shouldAllowAddingReservation(HttpServletRequest httpRequest, EntityManager em, ReservationSlotActiveClientInfo reservationSlotActiveClientInfo, Reservation r)
			throws NirvanaXPException
	{
		// check if this session exists in reservation_slots_active_client_info,
		// then client has requested for holding the block
		// and his session has yet not expired

		reservationSlotActiveClientInfo.setReservtionMadeByClient(true);
		// save that thread when tries to expire this session, it will
		// not update the slot as we will do it now
		em.merge(reservationSlotActiveClientInfo);

		Date reservationSlotTime = reservationSlotActiveClientInfo.getSlotHoldStartTime();
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(reservationSlotTime);
		gmtCal.add(Calendar.MINUTE, sessionClearTimeInMinutes);
		Date datefterincreasingSessionTimeout = gmtCal.getTime();
		if (!new Date(new TimezoneTime().getGMTTimeInMilis()).before(datefterincreasingSessionTimeout))
		{
			// remove it from the database
			em.remove(em.merge(reservationSlotActiveClientInfo));

		}
		// todo always returning true
		return true;

	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param r
	 * @throws ParseException
	 */
	private void setDateAndTimeForWaitlist(HttpServletRequest httpRequest, EntityManager em, Reservation r) throws ParseException
	{
		setRelationshipValues(r);
		Location l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
		Timezone t = em.find(Timezone.class, l.getTimezoneId());
		String timezone = t.getDisplayName();

		String[] parts = timezone.split(" ");
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(parts[1]));

		String month = "" + (cal.get(Calendar.MONTH) + 1);

		String day = "" + cal.get(Calendar.DAY_OF_MONTH);

		String currentDate = cal.get(Calendar.YEAR) + "-" + month + "-" + day;
		String currentTime = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
		Date newDate = simpleDate.parse(currentDate);

		SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		Date newTime = simpleTime.parse(currentTime);

		r.setDate(simpleDate.format(newDate));
		r.setTime(simpleTime.format(newTime));
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param sessionId
	 * @param em
	 * @param globalUserId
	 * @param r
	 * @return
	 * @throws Exception 
	 */
	private User relateUserToReservation(HttpServletRequest httpRequest, String sessionId, EntityManager em, String globalUserId, Reservation r,PostPacket packet) throws Exception
	{
		// todo modular coding needed
		// check for global user id
		setRelationshipValues(r);
		GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
		User localUser = null;
		if (globalUserId!=null )
		{
			// get user id for associated global user id and insert into
			// reservation user id
			EntityManager globalEM = null;
			try
			{
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

				localUser = globalUsermanagement.addGlobalUserToLocalDatabaseIfNotExixts(globalEM, em, globalUserId, locationId, null,httpRequest);
				if (localUser != null)
				{
					r.setUsersId(localUser.getId());
				}
			}
			finally
			{
				GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			}
		}
		else
		{
			// add this user to global and local database and insert into
			// reservation user id
			// create new user
			localUser = new User();
			localUser.setFirstName(r.getFirstName());
			localUser.setLastName(r.getLastName());
			localUser.setPhone(r.getPhoneNumber());
			localUser.setEmail(r.getEmail());
			localUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			localUser.setUpdatedBy(r.getCreatedBy());
			localUser.setCreatedBy(r.getCreatedBy());
			localUser.setStatus("A");
			localUser.setCountryId(r.getCountryId());
			localUser.setId(new StoreForwardUtility().generateUUID());	
			String sha512Password = null;
			sha512Password = DigestUtils.sha512Hex(generatePassword(8).getBytes(Charset.forName("UTF-8")));

			localUser.setPassword(sha512Password);
			if (localUser.getPhone() != null && localUser.getPhone().length() > 0)
			{
				localUser.setUsername(localUser.getPhone());
			}
			else
			{
				localUser.setUsername(localUser.getEmail());
			}
			if(localUser.getId()==null)
			localUser.setId(new StoreForwardUtility().generateUUID());
			EntityManager globalEM = null;
			try
			{
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
				//adding users to local db
				localUser = globalUsermanagement.addUserToLocalDatabaseProgramatically(httpRequest, globalEM, em, localUser, locationId,packet);

			}
			finally
			{
				GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			}
		}

		if (localUser != null)
		{

			r.setUsersId(localUser.getId());
			User userForPush = new User();
			userForPush.setFirstName(localUser.getFirstName());
			userForPush.setLastName(localUser.getLastName());
			userForPush.setEmail(localUser.getEmail());
			userForPush.setPhone(localUser.getPhone());
			userForPush.setId(localUser.getId());
			return userForPush;
		}

		return null;
	}

	/**
	 * 
	 *
	 * @param length
	 * @return
	 */
	private String generatePassword(int length)
	{
		final String charset = "~0!1@2-3_$4567(8)9" + "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

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
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param r
	 * @return
	 */
	public Reservation delete(HttpServletRequest httpRequest, EntityManager em, Reservation r)
	{
		setRelationshipValues(r);
		Reservation r2 = new Reservation();
		r2.setId(r.getId());
		r2.setDate(r.getDate());
		ReservationsType rt = em.find(ReservationsType.class, reservationTypeId);
		r2.setReservationsType(rt);
		em.remove(r);
		return r2;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param r
	 * @param isReservationStatusUpdate
	 * @param currentReservationSlotId
	 * @param idOfClientHoldingTheSlot
	 * @param webSiteUrl
	 * @param globalUserId
	 * @param sessionId
	 * @return
	 * @throws Exception 
	 */
	public ReservationWithUser update(HttpServletRequest httpRequest, EntityManager em, Reservation r, boolean isReservationStatusUpdate, int currentReservationSlotId, int idOfClientHoldingTheSlot,
			String webSiteUrl, String globalUserId, String sessionId,PostPacket packet) throws Exception
	{
		User newLocalUser = null;
		int slotAlreadyManaged = 0;
		int previosReservationSlotId = 0;
		ReservationSlotActiveClientInfo reservationSlotActiveClientInfo = null;
		boolean isReservationCancelled = false;
		setRelationshipValues(r);

		ReservationsType reservationsType = em.find(ReservationsType.class, reservationTypeId);

		// check if existing reservation is modified for new date and slot
		if (!isReservationStatusUpdate)
		{

			// client hold slot id will not be given by client in case they
			// are not updating the date and time
			Reservation reservation = new CommonMethods().getReservationById(httpRequest, em, r.getId());
			if (reservation == null)
			{
				// log severe
				logger.severe(httpRequest, "No Reservation found to update status for Id: " + r.getId());
				// throw back error
				throw new NoResultException("No Reservation found by Id : " + r.getId());
			}

			if (("Reservation".equals(reservationsType.getName()) || "Waitlist".equals(reservationsType.getName())) && (r.getUsersId() != reservation.getUsersId()
					|| (r.getPhoneNumber() != null && !r.getPhoneNumber().equals(reservation.getPhoneNumber())) || (r.getEmail() != null && !r.getEmail().equals(reservation.getEmail()))))
			{
				newLocalUser = relateUserToReservation(httpRequest, sessionId, em, globalUserId, r,packet);
			}
			r.setReferenceNumber(reservation.getReferenceNumber());
			r.setReservationSlotId(reservation.getReservationSlotId());
			if (idOfClientHoldingTheSlot > 0)
			{
				// they update date and time, they must hae session id, and
				// must go through similar flow like adding user

				reservationSlotActiveClientInfo = em.find(ReservationSlotActiveClientInfo.class, idOfClientHoldingTheSlot);
				if (shouldAllowAddingReservation(httpRequest, em, reservationSlotActiveClientInfo, r))
				{
					// allow updating reservation, hence initialize previous
					// slot id too, so that we can manage it
					/*
					 * Reservation reservation = em.find(Reservation.class,
					 * r.getId());
					 */
					if (reservation != null)
					{
						logger.severe(httpRequest, reservation.getDate() + " " + reservation.getTime());
					}
					previosReservationSlotId = reservation.getReservationSlotId();
					currentReservationSlotId = reservationSlotActiveClientInfo.getReservationSlotId();
					r.setReservationSlotId(currentReservationSlotId);

				}
			}
			// they update date and time, they must have session id, and
			// must go through similar flow like adding user

		}
		else
		{
			// check if client is cancelling the reservation or not
			Reservation previousReservation = new CommonMethods().getReservationById(httpRequest, em, r.getId());
			r.setReferenceNumber(previousReservation.getReferenceNumber());
			r.setReservationSlotId(previousReservation.getReservationSlotId());
			ReservationsStatus reservationsStatus = (ReservationsStatus) new CommonMethods().getObjectById("ReservationsStatus", em,ReservationsStatus.class, reservationStatusId);
			// get previous reservation to check if they are not
			// canceling again
			if ("Cancelled".equals(reservationsStatus.getName()) && previousReservation != null && !"Cancelled".equals(previousReservation.getReservationsStatus().getName()))
			{
				// maintain the slot, based on this flag
				isReservationCancelled = true;
				updateOrderForReservationUpdate(httpRequest, em, r);
			}

		}

		// managing slots for edit time case
		Reservation reservation = new CommonMethods().getReservationById(httpRequest, em, r.getId());
		if ((!reservation.getDate().equals(r.getDate())) || (!reservation.getTime().equals(r.getTime())))
		{
			slotAlreadyManaged = 0;
		}

		Reservation r2 = new Reservation();
		r2.setId(r.getId());
		r2.setDate(r.getDate());
		ReservationsType reservationsType2 = em.find(ReservationsType.class, reservationTypeId);
		r2.setReservationsType(reservationsType2);
		r2.setPreAssignedLocationId(r.getPreAssignedLocationId());
		r.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		setRelationshipValues(r);
		r.setLocationId(locationId);
		r.setReservationsTypeId(reservationTypeId);
		r.setReservationsStatusId(reservationStatusId);
		r.setRequestTypeId(requestTypeId);
		r.setContactPreferenceId1(contactPreferenceId1);
		r.setContactPreferenceId2(contactPreferenceId2);
		r.setContactPreferenceId3(contactPreferenceId3);

		// removed trasactional statement by Ap :- 2015-12-29\
		try
		{
			em.merge(r);
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		// to make data inserted on database before history insertion
		EntityTransaction tx = em.getTransaction();
		tx.commit();
		tx.begin();
		// Change by uzma for enter the value in reservation history
		// find reservation
		Reservation res_histroy = new CommonMethods().getReservationById(httpRequest, em, r.getId());

		// insert into reservation history
		new InsertIntoHistory().insertReservationIntoHistory(httpRequest, res_histroy, em);
		Location l = null;
		if (locationId != null)
		{
			l = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
		}
		ReservationsStatus reservationStatus = (ReservationsStatus) new CommonMethods().getObjectById("ReservationsStatus", em,ReservationsStatus.class, reservationStatusId);
		if (reservationStatus != null)
		{
			// so that we can have all information

			// check if reservation is cancelled, its associated
			// reservation slot must be cancelled too

			if ("Business App".equals(r.getReservationSource()))
			{
				webSiteUrl = ConfigFileReader.getRservationUrl() + r.getReferenceNumber() + "&reservation_id=" + r.getId();
			}

			// 26909 not send email on reservation update
			if ("Cancelled".equals(reservationStatus.getName()) || "Void Walkin".equals(reservationStatus.getName()))
			{
				EmailTemplateKeys.sendReservationCancelledEmailToCustomer(httpRequest, em, reservation, l, webSiteUrl, EmailTemplateKeys.RESERVATION_CANCELLED);
			}
		}

 
 
		String orderId = updatePreOrderForReservationUpdate(httpRequest, em, r);
		if (orderId!= null )
 
		{
			r.setOrderId(orderId);
			r.setPartySize(r.getPartySize());
			OrderHeader orderHeader = new OrderManagementServiceBean().getOrderById(em, orderId);
			OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, orderHeader.getOrderStatusId());
			
			if (orderStatus.getName().equals("Cancel Order"))
			{
				try
				{
					new PrinterUtility().insertIntoPrintQueueForCancelOrderAndQuickPay(httpRequest, em, orderHeader, r.getLocationId() + "");
				}
				catch (Exception e)
				{

					logger.severe(e);
				}
			}
			else
			{
				new PrinterUtility().insertIntoPrintQueueForOrderUpdateFromOutside(httpRequest, em, orderHeader, ""+r.getLocationId());

			}
		}

		// returning slot when doing Void walkin of reservation
		if (reservationStatus != null && reservationStatus.getName() != null && (reservationStatus.getName().equals("Void Walkin") || reservationStatus.getName().equals("No Show")))
		{
			isReservationCancelled = true;
		}
		if (slotAlreadyManaged == 0 && (isReservationStatusUpdate || isReservationCancelled))
		{
			manageReservationSlotForReservationUpdate(httpRequest, em, true, previosReservationSlotId, currentReservationSlotId, r);

		}
		else if (isReservationStatusUpdate == false && slotAlreadyManaged == 0)
		{
			manageReservationSlotForReservationUpdate(httpRequest, em, isReservationStatusUpdate, previosReservationSlotId, currentReservationSlotId, r);
		}

		// remove the client holding the slot from database as it has made a
		// reservation and can o longer use this slot session again
		// remove it from the database
		if (reservationSlotActiveClientInfo != null)
		{
			em.remove(em.merge(reservationSlotActiveClientInfo));
		}

		return new ReservationWithUser(r,r, newLocalUser);

	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param isReservationStatusUpdate
	 * @param previosReservationSlotId
	 * @param currentReservationSlotId
	 * @param r
	 */
	private void manageReservationSlotForReservationUpdate(HttpServletRequest httpRequest, EntityManager em, boolean isReservationStatusUpdate, int previosReservationSlotId,
			int currentReservationSlotId, Reservation r)
	{
		int fromWalikInOrWaitList = 0;
		setRelationshipValues(r);
		ReservationsStatus reservationStatus = (ReservationsStatus) new CommonMethods().getObjectById("ReservationsStatus", em,ReservationsStatus.class, reservationStatusId);

		if (isReservationStatusUpdate && reservationStatus != null)
		{
			if (reservationStatus != null)
			{
				// so that we can have all information
				// check if reservation is cancelled, its associated
				// reservation slot must be cancelled too
				if (reservationStatus.getName().equals("Cancelled") || reservationStatus.getName().equals("Void Walkin") || reservationStatus.getName().equals("No Show"))
				{
					// find reservation object as we need to get date n time
					// and client does not sent this information to server
					r = new CommonMethods().getReservationById(httpRequest, em, r.getId());
					// get slot for this reservation
					if (r.getReservationSlotId() != null && r.getReservationSlotId() != 0)
					{
						ManageSlotsUtils.updateReservationSlotCurrentActiveReservationCount(httpRequest, em, r.getReservationSlotId(), r, fromWalikInOrWaitList, false);
					}

				}
			}

		}
		else
		{
			// reservation is updated, so it could have booked new slot
			if (previosReservationSlotId != 0 && currentReservationSlotId != previosReservationSlotId)
			{
				// decrement reservation count old slot

				ManageSlotsUtils.updateReservationSlotCurrentActiveReservationCount(httpRequest, em, previosReservationSlotId, r, fromWalikInOrWaitList, false);
				// increment new reservation slot
				ManageSlotsUtils.updateReservationSlotCurrentActiveReservationCount(httpRequest, em, currentReservationSlotId, r, fromWalikInOrWaitList, true);
			}
		}
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param r
	 * @return
	 */
	private String updatePreOrderForReservationUpdate(HttpServletRequest httpRequest, EntityManager em, Reservation r)
	{
		// find order for this reservation and if guest count mismatch, then
		// update the guest count of order
		List<OrderHeader> orderHeadersList = getOrderHeaderForReservationId(em, r.getId());
		ReservationsStatus reservationStatus = (ReservationsStatus) new CommonMethods().getObjectById("ReservationsStatus", em,ReservationsStatus.class, r.getReservationsStatusId());
		if (orderHeadersList != null && orderHeadersList.size() > 0)
		{
			if (reservationStatus.getName().equals("Cancelled") || reservationStatus.getName().equals("Void Walkin") || reservationStatus.getName().equals("No Show"))
			{
				for (OrderHeader header : orderHeadersList)
				{
					OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, header.getOrderStatusId());
					if (orderStatus != null)
					{
						orderStatus = new CommonMethods().getOrderStatusByNameAndLocationIdAndOrderSourceGroupId(em, "Cancel Order", orderStatus.getLocationsId(), orderStatus.getOrderSourceGroupId());
						// to do by apoorv for cancel tab order
					}

				}
			}
			else
			{
				OrderHeader latestOrderHeader = orderHeadersList.get(0);
				if (orderHeadersList.size() > 2)
				{
					for (OrderHeader orderHeader : orderHeadersList)
					{
						if (orderHeader != null)
						{
							if (orderHeader.getId().compareTo(latestOrderHeader.getId())>0)
								latestOrderHeader = orderHeader;
						}
					}
				}
				if (latestOrderHeader != null)
				{

					latestOrderHeader.setSessionKey(r.getSessionKey());
					updatePartySizeOnPreOrder(latestOrderHeader, r, em);

					// so that histroy does not repeat details
					latestOrderHeader.setOrderDetailItems(null);

					new InsertIntoHistory().insertOrderIntoHistory(httpRequest, latestOrderHeader, em);
					return latestOrderHeader.getId();
				}
			}
		}
		return null;

	}

	/**
	 * 
	 *
	 * @param order
	 * @param reservation
	 * @param em
	 */
	private void updatePartySizeOnPreOrder(OrderHeader order, Reservation reservation, EntityManager em)
	{

		if (order != null)
		{
			order.setPointOfServiceCount(reservation.getPartySize());
			order.setFirstName(reservation.getFirstName());
			order.setLastName(reservation.getLastName());
			order.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			TimezoneTime timeZone = new TimezoneTime();
			if (order.getScheduleDateTime() != null)
			{
				// String scheduleDateTimeInGmt =
				// timeZone.getDateAccordingToGMTForConnection(em,
				// order.getScheduleDateTime(), order.getLocationsId());
				OrderStatus orderStatus = (OrderStatus) new CommonMethods().getObjectById("OrderStatus", em,OrderStatus.class, order.getOrderStatusId());
				String orderStatusName = orderStatus.getName();

				if (orderStatusName.equals(MessageConstants.OA_RECEIVED) || orderStatusName.equals(MessageConstants.OA_PLACED) || orderStatusName.equals(MessageConstants.OA_CHECKPRESENTED))
				{

					String scheduleDateTime = reservation.getDate() + " " + reservation.getTime();
					String scheduleDateTimeInGmt = timeZone.getDateAccordingToGMTForConnection(em, scheduleDateTime, order.getLocationsId());
					logger.info("scheduleDateTime -", scheduleDateTime, " in if  - ", scheduleDateTimeInGmt);
					order.setScheduleDateTime(scheduleDateTimeInGmt);
				}
			}
		
			// removed multiple transaction from code
			em.merge(order);

		}

	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param id
	 * @param date
	 * @param time
	 * @param sessionId
	 * @return
	 */
	public Reservation getReservationByIdAndDateAndTime(HttpServletRequest httpRequest, EntityManager em, int id, String date, String time, String sessionId)
	{
		Reservation reservation = null;
		// OrderManagementServiceBean bean = new
		// OrderManagementServiceBean(httpRequest);

		String selectClause =  new CommonMethods().SQL_SELECT_CLAUSE;
		String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id  LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id "
				+ " WHERE r.id = ?  and rs.name not in ('Void Walkin','Cancelled') AND  r.reservation_date_time  >=  ?";

		String dateTime = date + " " + time;
		Query query = em.createNativeQuery(queryString).setParameter(1, id).setParameter(2, dateTime);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		for (Object[] objRow : resultList)
		{
			try
			{
				// if this has primary key not 0
				reservation = new Reservation(objRow);
			}
			catch (Exception e)
			{
				logger.severe(httpRequest, e);
			}
			return reservation;
		}
		return reservation;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param id
	 * @return
	 */
	public List<Reservation> getReservationByUserId(HttpServletRequest httpRequest, EntityManager em, int id)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get("usersId"), id)));
		return query.getResultList();

	}

	/**
	 * 
	 *
	 * @param em
	 * @return
	 */
	public List<Reservation> getAllReservations(EntityManager em)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param date
	 * @return
	 */
	public List<Reservation> getReservationByDate(EntityManager em, java.util.Date date)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(builder.equal(r.get("date"), date)));
		return query.getResultList();

	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param tid
	 * @return
	 */
	public List<Reservation> getReservationByUserIdAndType(EntityManager em, int uid, int tid)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get("usersId"), uid), builder.equal(r.get("reservationsType"), tid) }));
		return query.getResultList();

	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param uid
	 * @param tid
	 * @param dateFrom
	 * @return
	 */
	public List<Reservation> getReservationByLocationAndUserIdAndTypeAndDateFrom(EntityManager em, String locationId, int uid, int tid, java.util.Date dateFrom)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.locationId), new Location(locationId)), builder.equal(r.get(Reservation_.reservationsTypeId), tid),
				builder.greaterThanOrEqualTo(r.get(Reservation_.date), dateFrom.toString()) }));
		return query.getResultList();

	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param tid
	 * @param dateFrom
	 * @return
	 */
	public List<Reservation> getReservationByUserIdAndTypeAndDateFrom(EntityManager em, int uid, int tid, java.util.Date dateFrom)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.reservationsTypeId), tid), builder.greaterThanOrEqualTo(r.get(Reservation_.date), dateFrom.toString()) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param tid
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getReservationByUserIdAndTypeAndDateTime(EntityManager em, int uid, int tid) throws ParseException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();

		String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.reservationsTypeId), tid),
				builder.greaterThanOrEqualTo(r.get(Reservation_.reservationDateTime), Utilities.convertStringToDate(dateTime)) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param dateFrom
	 * @return
	 */
	public List<Reservation> getReservationByUserIdAndDateFrom(EntityManager em, int uid, java.util.Date dateFrom)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.greaterThanOrEqualTo(r.get(Reservation_.date), dateFrom.toString()) }));
		return query.getResultList();

	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param uid
	 * @param tid
	 * @param date
	 * @return
	 */
	public List<Reservation> getReservationByLocationAndUserIdAndTypeAndDate(EntityManager em, String locationId, int uid, int tid, java.util.Date date)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.locationId), locationId), builder.equal(r.get(Reservation_.reservationsTypeId), tid),
				builder.equal(r.get(Reservation_.date), date) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param tid
	 * @param date
	 * @return
	 */
	public List<Reservation> getReservationByUserIdAndTypeAndDate(EntityManager em, int uid, int tid, java.util.Date date)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.reservationsTypeId), tid), builder.equal(r.get(Reservation_.date), date) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param tid
	 * @param date
	 * @param orderBy
	 * @return
	 */
	public List<Reservation> getAscendingReservationByUserIdAndTypeAndDate(EntityManager em, int uid, int tid, java.util.Date date, String orderBy)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.reservationsTypeId), new ReservationsType(tid)), builder.equal(r.get(Reservation_.date), date) })
				.orderBy(builder.asc(r.get(orderBy))));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param date
	 * @return
	 */
	public List<Reservation> getReservationByUserIdAndDate(EntityManager em, int uid, java.util.Date date)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.date), date) }));
		return query.getResultList();
	}

	/**
	 * @param em
	 * @param locationId
	 * @param date
	 * @param tid
	 * @return
	 */
	public List<Reservation> getReservationByLocationAndDateAndType(EntityManager em, String locationId, java.util.Date date, int tid)
	{
		// Change by Apoorva for matching date format issue after ankur fix july
		// 21,2015 sprint 7 release
		SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateChange = dmyFormat.format(date);
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get("date"), dateChange), builder.equal(r.get(Reservation_.locationId), locationId), builder.equal(r.get(Reservation_.reservationsTypeId), tid) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param date
	 * @param tid
	 * @return
	 */
	public List<Reservation> getReservationByDateAndType(EntityManager em, java.util.Date date, int tid)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get("date"), date), builder.equal(r.get(Reservation_.reservationsTypeId), tid) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param tid
	 * @param dateFrom
	 * @return
	 */
	public List<Reservation> getReservationsByLocationAndTypeAndDateFrom(EntityManager em, String locationId, int tid, java.util.Date dateFrom)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.locationId), locationId), builder.equal(r.get(Reservation_.reservationsTypeId), tid),
				builder.greaterThanOrEqualTo(r.get(Reservation_.date), dateFrom.toString()) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param tid
	 * @param dateFrom
	 * @return
	 */
	public List<Reservation> getReservationsByAndTypeAndDateFrom(EntityManager em, int tid, java.util.Date dateFrom)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.reservationsTypeId), tid), builder.greaterThanOrEqualTo(r.get(Reservation_.date), dateFrom.toString()) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param criteria
	 * @return
	 */
	public List<Reservation> getReservationsByCriteria(EntityManager em, ReservationsSearchCriteria criteria)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Reservation> cq = builder.createQuery(Reservation.class);
		Root<Reservation> r = cq.from(Reservation.class);
		Predicate[] p = null;
		// p = builder.equal(r.get(Reservation_.reservationsType),
		// criteria.getReservationsTypeId());
		if (criteria != null && (p = getPredicateArray(builder, r, criteria)) != null)
		{
			TypedQuery<Reservation> query = em.createQuery(cq.select(r).where(p).orderBy(getOrder(builder, r, criteria)));
			if (criteria.getFirst() >= 0 && criteria.getMaxResults() > 0)
			{
				query.setFirstResult(criteria.getFirst());
				query.setMaxResults(criteria.getMaxResults());
			}

			return query.getResultList();
		}
		else
		{
			throw new IllegalArgumentException("No valid criteria input sent to build query");
		}

	}

	/**
	 * 
	 *
	 * @param em
	 * @param uid
	 * @param tid
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getReservationByAndUserIdAndTypeAndDateTime(EntityManager em, int uid, int tid, String dateTime) throws ParseException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.usersId), uid), builder.equal(r.get(Reservation_.reservationsTypeId), tid),
				builder.greaterThanOrEqualTo(r.get(Reservation_.reservationDateTime), Utilities.convertStringToDate(dateTime)) }));

		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param tid
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getReservationByLocationIdAndTypeAndDateTime(EntityManager em, String locationId, int tid, String dateTime) throws ParseException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ builder.equal(r.get(Reservation_.locationId), locationId), builder.equal(r.get(Reservation_.reservationsTypeId), tid),
				builder.greaterThanOrEqualTo(r.get(Reservation_.reservationDateTime), Utilities.convertStringToDate(dateTime)) }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param dateTime
	 * @param rid
	 * @param wid
	 * @param walkinTypeId
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getReservationAndWaitlistByLocationIdAndDateTime(EntityManager em, String locationId, String dateTime, int rid, int wid, int walkinTypeId) throws ParseException
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);

		Predicate reservationsTypeReservation = builder.equal(r.get(Reservation_.reservationsTypeId), rid);
		Predicate reservationsTypeWaitlist = builder.equal(r.get(Reservation_.reservationsTypeId), wid);
		Predicate reservationsTypeWalklist = builder.equal(r.get(Reservation_.reservationsTypeId), walkinTypeId);

		// select reservations of waitlist and reservations type
		Predicate orReservationTypeCondition = builder.or(reservationsTypeReservation, reservationsTypeWaitlist, reservationsTypeWalklist);
		Predicate locationCondition = builder.equal(r.get(Reservation_.locationId), locationId);
		Predicate dateTimeContion = builder.greaterThanOrEqualTo(r.get(Reservation_.reservationDateTime), Utilities.convertStringToDate(dateTime));

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ locationCondition, orReservationTypeCondition, dateTimeContion }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param date
	 * @param rid
	 * @param wid
	 * @param walkId
	 * @return
	 */
	public List<Reservation> getReservationAndWaitlistByLocationAndDate(EntityManager em, String locationId, java.util.Date date, int rid, int wid, int walkId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);
		Predicate reservationsTypeReservation = builder.equal(r.get(Reservation_.reservationsTypeId), rid);
		Predicate reservationsTypeWaitlist = builder.equal(r.get(Reservation_.reservationsTypeId), wid);

		Predicate reservationsTypeWalklist = builder.equal(r.get(Reservation_.reservationsTypeId), walkId);
		// select reservations of waitlist and reservations type
		Predicate orReservationTypeCondition = builder.or(reservationsTypeReservation, reservationsTypeWaitlist, reservationsTypeWalklist);
		Predicate locationCondition = builder.equal(r.get(Reservation_.locationId), locationId);
		Predicate dateTimeContion = builder.equal(r.get(Reservation_.date), date.toString());

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ locationCondition, orReservationTypeCondition, dateTimeContion, }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param dateTime
	 * @param rid
	 * @param wid
	 * @param updatedTime
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getReservationAndWaitlistByLocationIdAndDateTime(EntityManager em, String locationId, String dateTime, int rid, int wid, String updatedTime) throws ParseException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);

		Predicate reservationsTypeReservation = builder.equal(r.get(Reservation_.reservationsTypeId), rid);
		Predicate reservationsTypeWaitlist = builder.equal(r.get(Reservation_.reservationsTypeId), wid);

		// select reservations of waitlist and reservations type
		Predicate orReservationTypeCondition = builder.or(reservationsTypeReservation, reservationsTypeWaitlist);
		Predicate locationCondition = builder.equal(r.get(Reservation_.locationId), locationId);
		Predicate dateTimeContion = builder.greaterThanOrEqualTo(r.get(Reservation_.reservationDateTime), Utilities.convertStringToDate(dateTime));

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date parsed = format.parse(updatedTime);

		Predicate updatedCondition = builder.greaterThanOrEqualTo(r.get(Reservation_.updated), parsed);

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ locationCondition, orReservationTypeCondition, dateTimeContion, updatedCondition }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param date
	 * @param rid
	 * @param wid
	 * @return
	 */
	public List<Reservation> getReservationAndWaitlistByLocationAndDate(EntityManager em, String locationId, java.util.Date date, int rid, int wid)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);

		Predicate reservationsTypeReservation = builder.equal(r.get(Reservation_.reservationsTypeId), rid);
		Predicate reservationsTypeWaitlist = builder.equal(r.get(Reservation_.reservationsTypeId), (wid));

		// select reservations of waitlist and reservations type
		Predicate orReservationTypeCondition = builder.or(reservationsTypeReservation, reservationsTypeWaitlist);
		Predicate locationCondition = builder.equal(r.get(Reservation_.locationId), (locationId));
		Predicate dateTimeContion = builder.equal(r.get(Reservation_.date), date.toString());

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ locationCondition, orReservationTypeCondition, dateTimeContion, }));
		return query.getResultList();

	}

	/**
	 * 
	 *
	 * @param builder
	 * @param r
	 * @param criteria
	 * @return
	 */
	private Predicate[] getPredicateArray(CriteriaBuilder builder, Root<Reservation> r, ReservationsSearchCriteria criteria)
	{
		Predicate[] p = null;

		List<Predicate> l = new ArrayList<Predicate>();

		if (criteria.getLocationId() != null)
		{
			l.add(builder.equal(r.get(Reservation_.locationId), criteria.getLocationId()));
		}

		if (criteria.getReservationsTypeId() > 0)
		{
			l.add(builder.equal(r.get(Reservation_.reservationsTypeId), criteria.getReservationsTypeId()));
		}

		if (criteria.getReservationDate() != null)
		{
			l.add(builder.equal(r.get(Reservation_.date), criteria.getReservationDate()));
		}
		else if (criteria.getReservationFromDate() != null)
		{
			l.add(builder.greaterThanOrEqualTo(r.get(Reservation_.date), criteria.getReservationFromDate()));
		}
		if (criteria.getReservationFromCurrentTime() != null)
		{
			l.add(builder.equal(r.get(Reservation_.time), criteria.getReservationFromCurrentTime()));
		}
		if (criteria.getUsersId()!=null)
		{
			l.add(builder.equal(r.get(Reservation_.usersId), criteria.getUsersId()));
		}

		if (!l.isEmpty())
		{
			p = l.toArray(new Predicate[l.size()]);
		}

		return p;
	}

	/**
	 * 
	 *
	 * @param builder
	 * @param r
	 * @param criteria
	 * @return
	 */
	private Order getOrder(CriteriaBuilder builder, Root<Reservation> r, ReservationsSearchCriteria criteria)
	{
		if (criteria != null && criteria.getOrderByAttributeName() != null)
		{
			SingularAttribute<Reservation, ? extends Object> saStr = getSingularAttribute(criteria.getOrderByAttributeName());
			if (saStr != null)
			{
				if (criteria.isDescending())
				{
					return builder.desc(r.get(saStr));
				}
				else
				{
					return builder.asc(r.get(saStr));
				}
			}

		}

		return builder.asc(r.get(Reservation_.id));
	}

	/**
	 * 
	 *
	 * @param orderByAttributeName
	 * @return
	 */
	private SingularAttribute<Reservation, ? extends Object> getSingularAttribute(String orderByAttributeName)
	{
		if (orderByAttributeName.equals(Reservation_.date.getName()))
		{
			return Reservation_.date;
		}
		else if (orderByAttributeName.equals(Reservation_.firstName.getName()))
		{
			return Reservation_.firstName;
		}
		else if (orderByAttributeName.equals(Reservation_.lastName.getName()))
		{
			return Reservation_.lastName;
		}
		else if (orderByAttributeName.equals(Reservation_.usersId.getName()))
		{
			return Reservation_.usersId;
		}
		return null;
	}

	/**
	 * 
	 *
	 * @param em
	 * @param httpRequest
	 * @param locationId
	 * @param dateTime
	 * @param rid
	 * @param wid
	 * @param walkinTypeId
	 * @param sessionId
	 * @return
	 */
	public List<Reservation> getReservationAndWaitlistWithOrderByLocationIdAndDateTime(EntityManager em, HttpServletRequest httpRequest, String locationId, String dateTime, int rid, int wid,
			int walkinTypeId, String sessionId)
	{
		List<Reservation> list = new ArrayList<Reservation>();

		String date = null;
		String time = null;
		if (dateTime != null && dateTime.contains(":"))
		{
			date = dateTime.substring(0, 10);
			time = dateTime.substring(10);
		}

		String selectClause =  new CommonMethods().SQL_SELECT_CLAUSE;
		String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id  LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id "
				+ " WHERE r.reservation_types_id in (?,?,?) and  r.date >= ? " + " and r.time >= ?  and rs.name not in ('Void Walkin') and r.locations_id = ? ";
		;

		Query query = em.createNativeQuery(queryString).setParameter(1, rid).setParameter(2, wid).setParameter(3, walkinTypeId).setParameter(4, date).setParameter(5, time).setParameter(6, locationId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		for (Object[] objRow : resultList)
		{
			try
			{
				// if this has primary key not 0
				Reservation reservation = new Reservation();
				reservation = new Reservation(objRow);
				list.add(reservation);
			}
			catch (Exception e)
			{

				logger.severe(httpRequest, e);
			}
		}
		return list;

	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @param date
	 * @param tid
	 * @param sessionId
	 * @return
	 */
	public List<Reservation> getReservationWithOrderByLocationAndDateAndType(HttpServletRequest httpRequest, EntityManager em, String locationId, java.util.Date date, int tid, String sessionId)
	{
		List<Reservation> list = new ArrayList<Reservation>();
		String selectClause =  new CommonMethods().SQL_SELECT_CLAUSE;
		String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id  LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id "
				+ " WHERE r.reservation_types_id in (?) and  r.date = ? " + "  and rs.name not in ('Void Walkin') and r.locations_id = ? ";
		;

		Query query = em.createNativeQuery(queryString).setParameter(1, tid).setParameter(2, date).setParameter(3, locationId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		for (Object[] objRow : resultList)
		{
			try
			{
				// if this has primary key not 0
				Reservation reservation = new Reservation();
				reservation = new Reservation(objRow);
				list.add(reservation);
			}
			catch (Exception e)
			{

				logger.severe(httpRequest, e);
			}
		}

		return list;
		/*
		 * ArrayList<Reservation> ans = new ArrayList<Reservation>();
		 * 
		 * Query q = em.createNativeQuery(queryString); List<Object[]> l =
		 * q.getResultList(); for (Object[] obj : l) { Reservation reservation =
		 * new Reservation(obj); ans.add(reservation); } return ans;
		 */
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @param date
	 * @param rid
	 * @param wid
	 * @param walkId
	 * @param sessionId
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getReservationAndWaitlistWithOrderByLocationAndDate(HttpServletRequest httpRequest, EntityManager em, String locationId, java.util.Date date, int rid, int wid, int walkId,
			String sessionId) throws ParseException
	{

		List<Reservation> list = new ArrayList<Reservation>();
		// OrderManagementServiceBean bean = new
		// OrderManagementServiceBean(httpRequest);
		// TimezoneTime timezoneTime = new TimezoneTime();

		String queryString = selectClause + "from reservations r  left join users u on u.id=r.users_id LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id " + " WHERE r.reservation_types_id in (?,?,?) "
				+ " and r.date=? and rs.name not in ('Void Walkin') and r.locations_id = ? ";

		// Change by Apoorva for matching date format issue after ankur fix july
		// 21,2015 sprint 7 release
		SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateChange = dmyFormat.format(date);

		Query query = em.createNativeQuery(queryString).setParameter(1, rid).setParameter(2, wid).setParameter(3, walkId).setParameter(4, dateChange).setParameter(5, locationId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		for (Object[] objRow : resultList)
		{
			try
			{
				// if this has primary key not 0
				Reservation reservation = new Reservation();
				reservation = new Reservation(objRow);
				// for setting visit count
				list.add(reservation);
			}
			catch (Exception e)
			{

				logger.severe(httpRequest, e);
			}
		}

		/*
		 * ArrayList<Reservation> ans = new ArrayList<Reservation>();
		 * 
		 * Query q = em.createNativeQuery(queryString); List<Object[]> l =
		 * q.getResultList(); for (Object[] obj : l) { Reservation reservation =
		 * new Reservation(obj); ans.add(reservation); } return ans;
		 */

		return list;
	}

	
	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @param dateTime
	 * @param wid
	 * @param updatedTime
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getWaitlistByLocationIdAndDateTime(HttpServletRequest httpRequest, EntityManager em, String locationId, String dateTime, int wid, String updatedTime) throws ParseException
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
		Root<Reservation> r = criteria.from(Reservation.class);

		Predicate reservationsTypeWaitlist = builder.equal(r.get(Reservation_.reservationsTypeId), (wid));

		// select reservations of waitlist and reservations type
		Predicate locationCondition = builder.equal(r.get(Reservation_.locationId), new Location(locationId));
		Predicate dateTimeContion = builder.greaterThanOrEqualTo(r.get(Reservation_.reservationDateTime), Utilities.convertStringToDate(dateTime));

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date parsed = null;
		try
		{
			parsed = format.parse(updatedTime);
		}
		catch (ParseException e)
		{

			logger.severe(httpRequest, e);
		}

		Predicate updatedCondition = builder.greaterThanOrEqualTo(r.get(Reservation_.updated), parsed);

		TypedQuery<Reservation> query = em.createQuery(criteria.select(r).where(new Predicate[]
		{ locationCondition, reservationsTypeWaitlist, dateTimeContion, updatedCondition }));
		return query.getResultList();
	}

	/**
	 * 
	 *
	 * @param em
	 * @param reservationId
	 * @return
	 */
	private List<OrderHeader> getOrderHeaderForReservationId(EntityManager em, String reservationId)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderHeader> criteria = builder.createQuery(OrderHeader.class);
		Root<OrderHeader> orderRoot = criteria.from(OrderHeader.class);
		TypedQuery<OrderHeader> query = em.createQuery(criteria.select(orderRoot).where(builder.equal(orderRoot.get(OrderHeader_.reservationsId), reservationId)));
		List<OrderHeader> orderHeadersList = query.getResultList();
		return orderHeadersList;
	}

	/**
	 * 
	 *
	 * @param em
	 * @param reservationId
	 * @return
	 */
	private List<OrderHeader> getOrderHeaderForReservationIdWithoutTabOrderAndOrderAhead(EntityManager em, String reservationId)
	{
		List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();
		String queryString = "select o.id from order_header o join order_status os on os.id=o.order_status_id   where  o.reservations_id=? " + " and o.is_Tab_Order=0 "
				+ " and os.name not in ('Order Ahead Received','Order Ahead Placed','Order Ahead Check Presented','Order Ahead Paid') ";
		Query query = em.createNativeQuery(queryString).setParameter(1, reservationId);
		@SuppressWarnings("unchecked")
		List<Object> resultList = query.getResultList();
		for (Object object : resultList)
		{
			String id = (String) object;
			OrderHeader header = new OrderManagementServiceBean().getOrderById(em, id);
			orderHeaders.add(header);
		}
		return orderHeaders;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param id
	 * @param sessionId
	 * @param schemaName
	 * @return
	 */
	public Reservation getReservationForCustomerById(HttpServletRequest httpRequest, EntityManager em, int id, String sessionId, String schemaName)
	{
		Reservation reservation = null;

		String queryString = selectClause + "from reservations r left join users u on u.id=r.users_id " + "LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id "
				+ " WHERE   rs.name not in ('Void Walkin') and r.id = ?";

		Query query = em.createNativeQuery(queryString).setParameter(1, id);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		for (Object[] objRow : resultList)
		{
			try
			{
				// if this has primary key not 0
				reservation = new Reservation(objRow);
			}
			catch (Exception e)
			{
				logger.severe(httpRequest, e);
			}
			return reservation;
		}

		return reservation;
	}

	/**
	 * 
	 *
	 * @param em
	 * @param date
	 * @param locationId
	 * @return
	 */
	public List<ReservationsSlot> getReservationSlotForDate(EntityManager em, String date, String locationId)
	{

		date = date + " 00:00:00";

		String queryString = "select r from ReservationsSlot r where r.date= '" + date + "' and r.status='A' and r.locationId= '" + locationId + "' order by slot_start_time  ";
	
		TypedQuery<ReservationsSlot> query = em.createQuery(queryString, ReservationsSlot.class);
		List<ReservationsSlot> resultSet = query.getResultList();

		return resultSet;
	}

	/**
	 * 
	 *
	 * @param em
	 * @param date
	 * @param time
	 * @param locationId
	 * @return
	 */
	public List<ReservationsSlot> getReservationSlotForDateAndTime(EntityManager em, String date, String time, String locationId)
	{

		date = date + " 00:00:00";

		String queryString = "select r from ReservationsSlot r where r.date= '" + date + "' and r.status='A' and r.slotStartTime>='" + time + "' and r.locationId= '" + locationId + "' order by id";
		TypedQuery<ReservationsSlot> query = em.createQuery(queryString, ReservationsSlot.class);
		List<ReservationsSlot> resultSet = query.getResultList();

		return resultSet;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param reservationSlotId
	 * @param sessionId
	 * @param updatedBy
	 * @param schemaName
	 * @return
	 * @throws Exception 
	 */
	public HoldReservtionSlotResponse holdReservationSlotForClient(HttpServletRequest httpRequest, EntityManager em, int reservationSlotId, String sessionId, String updatedBy, String schemaName,String locationId)
			throws Exception
	{
		HoldReservtionSlotResponse holdReservtionSlotResponse = null;
		// finding slotid in database
		ReservationsSlot reservationsSlot = null;
		try
		{
			reservationsSlot = em.find(ReservationsSlot.class, reservationSlotId);
		}
		catch (Exception e1)
		{
			logger.severe(httpRequest, "No Slot found for reservation slot id: " + reservationSlotId);
		}
		if (reservationsSlot != null)
		{
			// checking status of holded reservation slot
			if (reservationsSlot.getStatus().equals("A"))
			{

				// get reservation schedule for the slot, so that we would
				// know
				// max reservation allowed for the slot
				ReservationsSchedule reservationsSchedule;
				// find RSchedule for current slot
				reservationsSchedule = (ReservationsSchedule) new CommonMethods().getObjectById("ReservationsSchedule", em,ReservationsSchedule.class, reservationsSlot.getReservationScheduleId());
				// managing slot, making it holded
				if (reservationsSchedule != null && reservationsSchedule.getStatus().equals("A"))
				{
					int maxReservationAllowedinslot = reservationsSchedule.getReservationAllowed();
					int currentReservationMadeInSlot = reservationsSlot.getCurrentReservationInSlot();
					int currentReservationSlotHoldedByClient = reservationsSlot.getCurrentlyHoldedClient();

					// check if reservation slot is still available or
					// not
					if (maxReservationAllowedinslot > currentReservationMadeInSlot + currentReservationSlotHoldedByClient)
					{
						// reservation slot is allowed for hold by the
						// client

						// increment the slot hold client count in
						// reservation
						// slot table
						reservationsSlot.setCurrentlyHoldedClient(currentReservationSlotHoldedByClient + 1);

						currentReservationSlotHoldedByClient = currentReservationSlotHoldedByClient + 1;
						// check if now the slot is still available or
						// it must
						// be put on hold as now a new client has
						// requested for
						// hold
						if (maxReservationAllowedinslot <= currentReservationMadeInSlot + currentReservationSlotHoldedByClient)
						{
							if (reservationsSlot.getStatus().equals("D") == false && reservationsSlot.getStatus().equals("I") == false)
							{
								reservationsSlot.setStatus("H");
							}

						}
						reservationsSlot.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						reservationsSlot.setUpdatedBy(updatedBy);
						// save new slot entry in database
						em.merge(reservationsSlot);

						// now add this client so that he can do
						// Reservation
						ReservationSlotActiveClientInfo reservationSlotActiveClientInfo = new ReservationSlotActiveClientInfo(sessionId, reservationsSlot.getSlotTime(), reservationSlotId);
						reservationSlotActiveClientInfo.setSessionId(sessionId);
						reservationSlotActiveClientInfo.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						reservationSlotActiveClientInfo.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						reservationSlotActiveClientInfo.setSlotHoldStartTime(new Date(new TimezoneTime().getGMTTimeInMilis()));
						reservationSlotActiveClientInfo.setCreatedBy(updatedBy);
						reservationSlotActiveClientInfo.setUpdatedBy(updatedBy);
					/*	if (reservationSlotActiveClientInfo.getId() ==0)
						{
							reservationSlotActiveClientInfo.setId(new StoreForwardUtility().generateDynamicIntId(em, locationId, httpRequest, "reservation_slots_active_client_info"));
						}*/

						// clean up single trasaction :- By AP :- 2015-12-29
						reservationSlotActiveClientInfo =em.merge(reservationSlotActiveClientInfo);

						// start a thread that will remove this client
						// session from database after some time
						// this is configurable param and it will change
						// accordingly, need to read from some file
						int threadSleeptime = sessionClearTimeInMinutes * 60 * 1000;
						ManageReservationSlotHoldClient manageReservationSlotHoldClient = 
								new ManageReservationSlotHoldClient(threadSleeptime, 
								reservationSlotActiveClientInfo.getSessionId(),
								reservationSlotActiveClientInfo.getReservationSlotId(),
								reservationSlotActiveClientInfo.getId(), schemaName);
						
						Thread thread = new Thread(manageReservationSlotHoldClient);
						thread.start();

						holdReservtionSlotResponse = new HoldReservtionSlotResponse();
						holdReservtionSlotResponse.setReservationsSlot(reservationsSlot);
						holdReservtionSlotResponse.setReservationHoldingClientId(reservationSlotActiveClientInfo.getId());
					}
					else
					{
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_RESERVATION_SCHEDULE_TAKEN_EXCEPTION,
								MessageConstants.ERROR_MESSAGE_RESERVATION_SCHEDULE_TAKEN_DISPLAY_MESSAGE, null));
					}
				}
				else
				{
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_RESERVATION_SCHEDULE_TAKEN_EXCEPTION,
							MessageConstants.ERROR_MESSAGE_RESERVATION_SCHEDULE_TAKEN_DISPLAY_MESSAGE, null));
				}

			}
			else
			{

				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_RESERVATION_SCHEDULE_STATUS_INVALID_EXCEPTION,
						MessageConstants.ERROR_MESSAGE_RESERVATION_SCHEDULE_STATUS_INVALID_DISPLAY_MESSAGE, null));

			}

		}

		return holdReservtionSlotResponse;

	}

	/**
	 * 
	 */
	public class ManageReservationSlotHoldClient implements Runnable
	{

		/**  */
		// default sleep time
		int reservationslotHoldTime = sessionClearTimeInMinutes * 60 * 1000;

		/**  */
		String sessonId;

		/**  */
		int reservationSlotId;

		/**  */
		int clientReservationObjId = 0;

		/**  */
		String schemaName = null;

		/**  */
		private final NirvanaLogger logger = new NirvanaLogger(ManageReservationSlotHoldClient.class.getName());

		/**
		 * 
		 *
		 * @param reservationslotHoldTime
		 * @param sessonId
		 * @param reservationSlotId
		 * @param clientReservationObjId
		 * @param schemaName
		 */
		public ManageReservationSlotHoldClient(int reservationslotHoldTime, String sessonId, int reservationSlotId, int clientReservationObjId, String schemaName)
		{
			super();
			this.reservationslotHoldTime = reservationslotHoldTime;
			this.sessonId = sessonId;
			this.reservationSlotId = reservationSlotId;
			this.clientReservationObjId = clientReservationObjId;
			this.schemaName = schemaName;
			
			
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			EntityManager entityManager = null;
			try
			{
				Thread.sleep(reservationslotHoldTime);

				if (sessonId != null)
				{
					entityManager = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);

					if (entityManager != null)
					{
						ReservationSlotActiveClientInfo reservationSlotActiveClientInfo = entityManager.find(ReservationSlotActiveClientInfo.class, clientReservationObjId);
						if (reservationSlotActiveClientInfo != null)
						{
							// delete this client as his session is expired
							EntityTransaction tx = entityManager.getTransaction();
							try
							{
								// start transaction
								tx.begin();
								entityManager.remove(entityManager.merge(reservationSlotActiveClientInfo));
								tx.commit();
							}
							catch (RuntimeException e)
							{
								// on error, if transaction active,
								// rollback
								if (tx != null && tx.isActive())
								{
									tx.rollback();
								}
								throw e;
							}

							// check if the reservation is already added/updated
							// by
							// client, then the slot management is already
							// handled
							// by those methods
							// if reservation not made/updated, then client
							// session
							// has expired, we must now release the slot holded
							// by
							// him
							
							if (reservationSlotActiveClientInfo.isReservtionMadeByClient() == false)
							{

								ReservationsSlot reservationsSlot = entityManager.find(ReservationsSlot.class, reservationSlotActiveClientInfo.getReservationSlotId());
								if (reservationsSlot != null)
								{
									// send packet to message slot jms queue, so
									// that slot
									// could be re-maintained,this we need when
									// many
									// thread will expire at same time, they all
									// take
									// same value form database and decrement
									// slot
									// by lesser value than expected, hence slot
									// starts behaving abruptly
									if (reservationsSlot.getCurrentlyHoldedClient() == 1)
									{
										reservationsSlot.setCurrentlyHoldedClient(reservationsSlot.getCurrentlyHoldedClient() - 1);
										reservationsSlot.setStatus("A");

									}
									else
									{
										reservationsSlot.setCurrentlyHoldedClient(reservationsSlot.getCurrentlyHoldedClient() - 1);
									}
									tx.begin();
									entityManager.merge(reservationsSlot);
									tx.commit();
									new SendPacketToManageSlotQueue().sendMessage(reservationsSlot.getId(), schemaName, "reservationSlot");

								}

							}
						}
					}
				}

			}
			catch (InterruptedException e)
			{
				logger.severe(e, "Slot management thread interrupted: ", e.getMessage());
			}
			catch (Exception e)
			{
				logger.severe(e, "Exception in slot management thread", e.getMessage());
			}
			finally
			{
				LocalSchemaEntityManager.getInstance().closeEntityManager(entityManager);
			}

		}
	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @return
	 */
	public ReservationDetails getReservationDetailsForCustomer(EntityManager em, String locationId)
	{

		ReservationDetails reservationDetails = new ReservationDetails();
		ReservationsType reservationtype = null;
		try
		{
			reservationtype = getReservationTypeByNameAndLocationId(em, "Reservation", locationId);

			if (reservationtype != null)
			{
				reservationDetails.setReservationTypeId(reservationtype.getId());
			}
		}
		catch (NoResultException nre)
		{
			logger.info("No Result found for reservation type 'Reservation' for location: " + locationId);
		}

		try
		{
			reservationtype = getReservationTypeByNameAndLocationId(em, "Waitlist", locationId);
			if (reservationtype != null)
			{
				reservationDetails.setWaitlistTypeId(reservationtype.getId());
			}
		}
		catch (NoResultException nre)
		{
			logger.info("No Result found for reservation type 'WaitList' for location: " + locationId);
		}

		ReservationsStatus reservationsStatus = null;
		try
		{
			reservationsStatus = getReservationStatusByNameAndLocationId(em, "Confirmed", locationId);
			if (reservationsStatus != null)
			{
				reservationDetails.setConfirmedStatusId(reservationsStatus.getId());
			}
		}
		catch (NoResultException nre)
		{
			logger.info("No Result found for reservation status 'Confirmed' for location: " + locationId);
		}
		try
		{
			reservationsStatus = getReservationStatusByNameAndLocationId(em, "Cancelled", locationId);
			if (reservationsStatus != null)
			{
				reservationDetails.setCancelledStatusId(reservationsStatus.getId());
			}
		}
		catch (NoResultException nre)
		{
			logger.info("No Result found for reservation status 'Cancelled' for location: " + locationId);
		}
		return reservationDetails;

	}

	/**
	 * 
	 *
	 * @param em
	 * @param name
	 * @param locationId
	 * @return
	 */
	private ReservationsStatus getReservationStatusByNameAndLocationId(EntityManager em, String name, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ReservationsStatus> criteria = builder.createQuery(ReservationsStatus.class);
		Root<ReservationsStatus> r = criteria.from(ReservationsStatus.class);
		TypedQuery<ReservationsStatus> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsStatus_.name), name), builder.equal(r.get(ReservationsStatus_.locationsId), locationId)));
		ReservationsStatus reservationtypeReservation = null;
		reservationtypeReservation = query.getSingleResult();

		return reservationtypeReservation;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param sessionId
	 * @param reservationHoldingClientId
	 * @param schemaName
	 * @return
	 * @throws Exception
	 */
	public boolean unHoldReservationSlotForClient(HttpServletRequest httpRequest, EntityManager em, String sessionId, String reservationHoldingClientId, String schemaName) throws Exception
	{

		if (schemaName != null)
		{

			if (em != null)
			{
				ReservationSlotActiveClientInfo reservationSlotActiveClientInfo = em.find(ReservationSlotActiveClientInfo.class, Integer.parseInt(reservationHoldingClientId));
				if (reservationSlotActiveClientInfo != null)
				{

					// check if the reservation is already added/updated
					// by
					// client, then the slot management is already
					// handled
					// by those methods
					// if reservation not made/updated, then client
					// session
					// has expired, we must now release the slot holded
					// by
					// him
					if (reservationSlotActiveClientInfo.isReservtionMadeByClient() == false)
					{
						EntityTransaction tx = em.getTransaction();
						try
						{
							// start transaction
							tx.begin();
							em.remove(em.merge(reservationSlotActiveClientInfo));
							tx.commit();
						}
						catch (RuntimeException e)
						{
							// on error, if transaction active,
							// rollback
							if (tx != null && tx.isActive())
							{
								tx.rollback();
							}
							throw e;
						}

						ReservationsSlot reservationsSlot = em.find(ReservationsSlot.class, reservationSlotActiveClientInfo.getReservationSlotId());
						if (reservationsSlot != null)
						{

							// delete this client as his session is expired

							// send packet to message slot jms queue, so
							// that slot
							// could be re-maintained,this we need when
							// many
							// thread will expire at same time, they all
							// take
							// same value form database and decrement
							// slot
							// by lesser value than expected, hence slot
							// starts behaving abruptly
							new SendPacketToManageSlotQueue().sendMessage(reservationsSlot.getId(), schemaName, "reservationSlot");

						}
						return true;
					}
				}
			}

		}

		return false;
	}

	/**
	 * 
	 *
	 * @param em
	 * @param name
	 * @param locationId
	 * @return
	 */
	private ReservationsType getReservationTypeByNameAndLocationId(EntityManager em, String name, String locationId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ReservationsType> criteria = builder.createQuery(ReservationsType.class);
		Root<ReservationsType> r = criteria.from(ReservationsType.class);
		TypedQuery<ReservationsType> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(ReservationsType_.name), name), builder.equal(r.get(ReservationsType_.locationsId), locationId)));
		ReservationsType reservationtypeReservation = query.getSingleResult();

		return reservationtypeReservation;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.services.jaxrs.ILookupService#update(com.nirvanaxp.types
	 * .entities.ReservationsStatus)
	 */
	/**
	 * 
	 *
	 * @param reservationsSlotId
	 * @param holdFlag
	 * @param updatedBy
	 * @param httpRequest
	 * @param em
	 * @return
	 * @throws NirvanaXPException
	 */
	// @Override
	public ReservationsSlot updateReservationsSlot(int reservationsSlotId, int holdFlag, String updatedBy, HttpServletRequest httpRequest, EntityManager em) throws NirvanaXPException
	{
		ReservationsSlot reservationsSlot = em.find(ReservationsSlot.class, reservationsSlotId);
		if (reservationsSlot != null)
		{
			reservationsSlot.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			reservationsSlot.setIsBlocked(holdFlag);
			reservationsSlot.setUpdatedBy(updatedBy);
			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.merge(reservationsSlot);
				tx.commit();
			}
			catch (RuntimeException e)
			{
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive())
				{
					tx.rollback();
				}
				throw e;
			}
		}
		else
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_RESERVATION_SLOT_NOT_EXIST, MessageConstants.ERROR_MESSAGE_RESERVATION_SLOT_NOT_EXIST, null));
		}

		return reservationsSlot;
	}

	/**
	 * 
	 *
	 * @param em
	 * @param id
	 * @return
	 */
	public List<ReservationsHistory> getReservationHistoryByReservationId(EntityManager em, int id)
	{

		String queryString = "select r from ReservationsHistory r where r.reservationsId= " + id;
		TypedQuery<ReservationsHistory> query = em.createQuery(queryString, ReservationsHistory.class);
		List<ReservationsHistory> resultSet = query.getResultList();

		return resultSet;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param id
	 * @param tid
	 * @return
	 */
	public List<Reservation> getReservationByUserIdAndTypeNew(HttpServletRequest httpRequest, EntityManager em, int id, int tid)
	{

		List<Reservation> list = new ArrayList<Reservation>();
		String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id  LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id "
				+ " WHERE r.users_id = ?  and rs.name not in ('Void Walkin') AND r.reservation_types_id =  ? order by  concat(r.date, ' ', r.time) desc";

		Query query = em.createNativeQuery(queryString).setParameter(1, id).setParameter(2, tid);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		for (Object[] objRow : resultList)
		{
			try
			{
				// if this has primary key not 0
				Reservation reservation = new Reservation();
				reservation = new Reservation(objRow);
				list.add(reservation);
			}
			catch (Exception e)
			{

				logger.severe(httpRequest, e);
			}
		}
		return list;

	}

	/**
	 * 
	 *
	 * @param r
	 */
	private void setRelationshipValues(Reservation r)
	{
		if (r.getReservationsType() != null)
		{
			reservationTypeId = r.getReservationsType().getId();
		}
		else
		{
			reservationTypeId = r.getReservationsTypeId();
		}
		if (r.getLocation() != null)
		{
			locationId = r.getLocation().getId();
		}
		else
		{
			locationId = r.getLocationId();
		}
		if (r.getReservationsStatus() != null)
		{
			reservationStatusId = r.getReservationsStatus().getId();
		}
		else
		{
			reservationStatusId = r.getReservationsStatusId();
		}
		if (r.getContactPreference1() != null)
		{
			contactPreferenceId1 = r.getContactPreference1().getId();
		}
		else
		{
			contactPreferenceId1 = r.getContactPreferenceId1();
		}
		if (r.getContactPreference2() != null)
		{
			contactPreferenceId2 = r.getContactPreference2().getId();
		}
		else
		{
			contactPreferenceId2 = r.getContactPreferenceId2();
		}
		if (r.getContactPreference3() != null)
		{
			contactPreferenceId3 = r.getContactPreference3().getId();
		}
		else
		{
			contactPreferenceId3 = r.getContactPreferenceId3();
		}
		if (r.getRequestType() != null)
		{
			requestTypeId = r.getRequestType().getId();
		}
		else
		{
			requestTypeId = r.getRequestTypeId();
		}
	}

	/**
	 * 
	 *
	 * @param r
	 * @param em
	 * @return
	 */
	Reservation setReservationObjectVariables(Reservation r, EntityManager em)
	{
		if (r.getRequestTypeId() != null && r.getRequestTypeId() != null)
		{
			ReservationsType reservationsType = em.find(ReservationsType.class, r.getRequestTypeId());
			r.setReservationsType(reservationsType);
		}
		if (r.getRequestTypeId() != null && r.getRequestTypeId() != null)
		{
			RequestType requestType = (RequestType) new CommonMethods().getObjectById("RequestType", em,RequestType.class, r.getRequestTypeId());
			r.setRequestType(requestType);
		}
		if (r.getReservationsStatusId() != null )
		{
			ReservationsStatus reservationsStatus = (ReservationsStatus) new CommonMethods().getObjectById("ReservationsStatus", em,ReservationsStatus.class, r.getReservationsStatusId());
			r.setReservationsStatus(reservationsStatus);
		}
		if (r.getLocationId() != null )
		{
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, r.getLocationId());
			r.setLocation(location);
		}
		if (r.getContactPreferenceId1() != null )
		{
			ContactPreference contactPreference1 = (ContactPreference) new CommonMethods().getObjectById("ContactPreference", em,ContactPreference.class, r.getContactPreferenceId1());
			r.setContactPreference1(contactPreference1);
		}
		if (r.getContactPreferenceId2() != null )
		{
			ContactPreference contactPreference2 = (ContactPreference) new CommonMethods().getObjectById("ContactPreference", em,ContactPreference.class, r.getContactPreferenceId2());
			r.setContactPreference2(contactPreference2);
		}
		if (r.getContactPreferenceId3() != null )
		{
			ContactPreference contactPreference3 = (ContactPreference) new CommonMethods().getObjectById("ContactPreference", em,ContactPreference.class, r.getContactPreferenceId3());
			r.setContactPreference3(contactPreference3);
		}

		return r;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param locationId
	 * @param userId
	 * @param rid
	 * @param wid
	 * @param walkId
	 * @return
	 * @throws ParseException
	 */
	public List<Reservation> getReservationAndWaitlistWithOrderByLocationAndUserId(HttpServletRequest httpRequest, EntityManager em, String locationId, String userId, int rid, int wid, int walkId)
			throws ParseException
	{

		List<Reservation> list = new ArrayList<Reservation>();
		// OrderManagementServiceBean bean = new
		// OrderManagementServiceBean(httpRequest);

		String queryString = selectClause + "from reservations r  left join users u on u.id=r.users_id LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id " + " WHERE r.reservation_types_id in (?,?,?) "
				+ " and r.users_id=? and rs.name not in ('Void Walkin') and r.locations_id = ? ";

		Query query = em.createNativeQuery(queryString).setParameter(1, rid).setParameter(2, wid).setParameter(3, walkId).setParameter(4, userId).setParameter(5, locationId);
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = query.getResultList();

		for (Object[] objRow : resultList)
		{
			try
			{
				// if this has primary key not 0
				Reservation reservation = new Reservation();
				reservation = new Reservation(objRow);
				// for setting visit count
				list.add(reservation);
			}
			catch (Exception e)
			{

				logger.severe(httpRequest, e);
			}
		}

		return list;
	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param userId
	 * @param updatedDate
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	public List<Reservation> getAllReservationsByUserIdAndUpdatedAfter(HttpServletRequest httpRequest, EntityManager em, String userId, String updatedDate, String locationId) throws Exception
	{

		List<Reservation> list = new ArrayList<Reservation>();

		String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id " + " LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
				+ " LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
				+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
				+ " LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id where "
				+ " r.updated>? and r.locations_id = ? and r.users_id=?  and rs.name not in ('Void Walkin') ";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, updatedDate).setParameter(2, locationId).setParameter(3, userId).getResultList();
		for (Object[] objRow : resultList)
		{

			try
			{
				// if this has primary key not 0
				Reservation reservation = new Reservation();
				reservation = new Reservation(objRow);
				// for setting visit count
				list.add(reservation);
			}
			catch (Exception e)
			{

				logger.severe(httpRequest, e);
			}

		}
		return list;

	}

	/**
	 * 
	 *
	 * @param httpRequest
	 * @param em
	 * @param r
	 * @return
	 */
	private int updateOrderForReservationUpdate(HttpServletRequest httpRequest, EntityManager em, Reservation r)
	{
		List<OrderHeader> orderHeadersList = getOrderHeaderForReservationIdWithoutTabOrderAndOrderAhead(em, r.getId());
		if (orderHeadersList != null && orderHeadersList.size() > 0)
		{
			for (OrderHeader order : orderHeadersList)
			{
				OrderSource orderSource = (OrderSource) new CommonMethods().getObjectById("OrderSource", em,OrderSource.class, order.getOrderSourceId());

				order.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				OrderStatus orderStatus = getOrderStatusByNameAndLocationId(em, locationId, orderSource.getOrderSourceGroupId());
				if (orderStatus != null)
				{
					order.setOrderStatusId(orderStatus.getId());
					em.merge(order);
				}
			}
		}
		return 0;

	}

	/**
	 * 
	 *
	 * @param em
	 * @param locationId
	 * @param orderSourceGroupId
	 * @return
	 */
	private OrderStatus getOrderStatusByNameAndLocationId(EntityManager em, String locationId, String orderSourceGroupId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<OrderStatus> criteria = builder.createQuery(OrderStatus.class);
		Root<OrderStatus> r = criteria.from(OrderStatus.class);
		TypedQuery<OrderStatus> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(OrderStatus_.name), "Cancel Order"), builder.equal(r.get(OrderStatus_.locationsId), locationId),
				builder.equal(r.get(OrderStatus_.orderSourceGroupId), orderSourceGroupId)));
		OrderStatus orderStatus = null;
		orderStatus = query.getSingleResult();

		return orderStatus;
	}
}
