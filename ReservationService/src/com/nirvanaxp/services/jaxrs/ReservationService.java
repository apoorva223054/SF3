/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.ReservationAndOrderPacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.custom.ReservationsSearchCriteria;
import com.nirvanaxp.types.entities.reservation.RequestType;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.ReservationsType;
import com.nirvanaxp.types.entities.reservation.ReservationsType_;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class ReservationService extends AbstractNirvanaService
{

	/**  */
	@Context
	HttpServletRequest httpRequest;
	
	/**  */
	String selectClause = new CommonMethods().SQL_SELECT_CLAUSE;
	
	/**  */
	private static final NirvanaLogger logger = new NirvanaLogger(ReservationService.class.getName());

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

	/**
	 * 
	 *
	 * @param sessionId 
	 * @param date 
	 * @param locationId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationSlotForDateAndLocationId/{date}/{locationId}")
	public String getReservationSlotForDate(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("date") String date, @PathParam("locationId") String locationId) throws IOException,
			 InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationSlotForDate(em, date, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param sessionId 
	 * @param schemaName 
	 * @param locationId 
	 * @return 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationDetailsForCustomerApplication/{locationId}")
	public String getReservationDetailsForCustomerApplication(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @CookieParam(value = "SchemaName") String schemaName,
			@PathParam("locationId") String locationId) throws FileNotFoundException,  IOException, InvalidSessionException
	{
		// todo this service should be in customer service 
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationDetailsForCustomer(em, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/all")
	public String getAllReservations(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getAllReservations(em));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param id 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationById/{id}")
	public String getReservationById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new CommonMethods().getReservationById(httpRequest, em, id));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param id 
	 * @param date 
	 * @param time 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByIdAndDateAndTime/{id}/{date}/{time}")
	public String getReservationByIdAndDateAndTime(@PathParam("id") int id, @PathParam("date") String date, @PathParam("time") String time, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException,  InvalidSessionException
	{
		EntityManager em = null;

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByIdAndDateAndTime(httpRequest, em, id, date, time, sessionId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param id 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByUserId/{userid}")
	public String getReservationByUserId(@PathParam("userid") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserId(httpRequest, em, id));

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param date 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByDate/{date}")
	public String getReservationByDate(@PathParam("date") Date date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByDate(em, date));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param uid 
	 * @param tid 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByUserIdAndType/{userid}/{type}")
	public String getReservationByUserIdAndType(@PathParam("userid") int uid, @PathParam("type") int tid, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, 
			InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserIdAndType(em, uid, tid));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param uid 
	 * @param tid 
	 * @param dateFrom 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByLocationAndUserIdAndTypeAndDateFrom/{locationid}/{userid}/{type}/{dateFrom}")
	public String getReservationByLocationAndUserIdAndTypeAndDateFrom(@PathParam("locationid") String locationId, @PathParam("userid") int uid, @PathParam("type") int tid,
			@PathParam("dateFrom") Date dateFrom, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByLocationAndUserIdAndTypeAndDateFrom(em, locationId, uid, tid, dateFrom));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// get reservation by date and type and date_from

	/**
	 * 
	 *
	 * @param uid 
	 * @param tid 
	 * @param dateFrom 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByUserIdAndTypeAndDateFrom/{userid}/{type}/{dateFrom}")
	public String getReservationByUserIdAndTypeAndDateFrom(@PathParam("userid") int uid, @PathParam("type") int tid, @PathParam("dateFrom") Date dateFrom,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserIdAndTypeAndDateFrom(em, uid, tid, dateFrom));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// get reservation by date and type and date_from

	/**
	 * 
	 *
	 * @param uid 
	 * @param tid 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 * @throws ParseException 
	 */
	@GET
	@Path("/getReservationByUserIdAndTypeAndDateTime/{userid}/{type}")
	public String getReservationByUserIdAndTypeAndDateTime(@PathParam("userid") int uid, @PathParam("type") int tid, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,
			 InvalidSessionException,ParseException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserIdAndTypeAndDateTime(em, uid, tid));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param uid 
	 * @param dateFrom 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByUserIdAndDateFrom/{userid}/{dateFrom}")
	public String getReservationByUserIdAndDateFrom(@PathParam("userid") int uid, @PathParam("dateFrom") Date dateFrom, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,
			 InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserIdAndDateFrom(em, uid, dateFrom));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param uid 
	 * @param tid 
	 * @param date 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByLocationAndUserIdAndTypeAndDate/{locationid}/{userid}/{type}/{date}")
	public String getReservationByLocationAndUserIdAndTypeAndDate(@PathParam("locationid") String locationId, @PathParam("userid") int uid, @PathParam("type") int tid, @PathParam("date") Date date,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByLocationAndUserIdAndTypeAndDate(em, locationId, uid, tid, date));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param uid 
	 * @param tid 
	 * @param date 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByUserIdAndTypeAndDate/{userid}/{type}/{date}")
	public String getReservationByUserIdAndTypeAndDate(@PathParam("userid") int uid, @PathParam("type") int tid, @PathParam("date") Date date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserIdAndTypeAndDateFrom(em, uid, tid, date));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param uid 
	 * @param tid 
	 * @param date 
	 * @param orderBy 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByUserIdAndTypeAndDate/{userid}/{type}/{date}/{orderBy}")
	public String getAscendingReservationByUserIdAndTypeAndDate(@PathParam("userid") int uid, @PathParam("type") int tid, @PathParam("date") Date date, @PathParam("orderBy") String orderBy,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getAscendingReservationByUserIdAndTypeAndDate(em, uid, tid, date, orderBy));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param uid 
	 * @param date 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByUserIdAndDate/{userid}/{date}")
	public String getReservationByUserIdAndDate(@PathParam("userid") int uid, @PathParam("date") Date date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, 
			InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserIdAndDate(em, uid, date));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param date 
	 * @param tid 
	 * @param sessionId 
	 * @return 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 * @throws SQLException 
	 * @throws ParseException 
	 */
	@GET
	@Path("/getReservationByLocationAndDateAndType/{locationid}/{date}/{type}")
	public String getReservationByLocationAndDateAndType(@PathParam("locationid") String locationId, @PathParam("date") Date date, @PathParam("type") int tid,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws FileNotFoundException, IOException,  InvalidSessionException, SQLException, ParseException
	{
		// todo need to modulize this code
		List<Reservation> list = new ArrayList<Reservation>();
		EntityManager em = null;
		em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
	
		try
		{
			
			// Change by Apoorva for matching date format issue after ankur fix july 21,2015 sprint 7 release
		    SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
		    String dateChange =dmyFormat.format(date);

			
			String queryString = selectClause + "from reservations r left join users u on u.id=r.users_id " + "  LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
					+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
					+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
					+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id where " + " r.locations_id = ? "
					+ " and r. reservation_types_id = ? and rs.name not in ('Void Walkin') and r.date=?";
			;

			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId).setParameter(2, tid).setParameter(3, dateChange).getResultList();
			for (Object[] objRow : resultList)
			{

				// if this has primary key not 0
				Reservation reservation = new Reservation(objRow);
				list.add(reservation);

			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(list);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	// get reservation by date and type

	/**
	 * 
	 *
	 * @param date 
	 * @param tid 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationByDateAndType/{date}/{type}")
	public String getReservationByDateAndType(@PathParam("date") Date date, @PathParam("type") int tid, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, 
			InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByDateAndType(em, date, tid));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param tid 
	 * @param dateFrom 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationsByLocationAndTypeAndDateFrom/{locationid}/{type}/{dateFrom}")
	public String getReservationsByLocationAndTypeAndDateFrom(@PathParam("locationid") String locationId, @PathParam("type") int tid, @PathParam("dateFrom") Date dateFrom,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationsByLocationAndTypeAndDateFrom(em, locationId, tid, dateFrom));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// get reservation by type and date_from

	/**
	 * 
	 *
	 * @param tid 
	 * @param dateFrom 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationsByTypeAndDateFrom/{type}/{dateFrom}")
	public String getReservationsByAndTypeAndDateFrom(@PathParam("type") int tid, @PathParam("dateFrom") Date dateFrom, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,
			 InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationsByAndTypeAndDateFrom(em, tid, dateFrom));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param criteria 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@POST
	@Path("/getReservationsByCriteria")
	public String getReservationsByCriteria(ReservationsSearchCriteria criteria, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationsByCriteria(em, criteria));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// future web service for customer app
	/**
	 * 
	 *
	 * @param uid 
	 * @param tid 
	 * @param dateTime 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 * @throws ParseException 
	 */
	// get reservation by userid and type and dateTime
	@GET
	@Path("/getReservationByUserIdAndTypeAndDateTime/{userid}/{type}/{dateTime}")
	public String getReservationByAndUserIdAndTypeAndDateTime(@PathParam("userid") int uid, @PathParam("type") int tid, @PathParam("dateTime") String dateTime,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException,ParseException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByAndUserIdAndTypeAndDateTime(em, uid, tid, dateTime));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// future web service for business app
	/**
	 * 
	 *
	 * @param locationId 
	 * @param tid 
	 * @param dateTime 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 * @throws ParseException 
	 */
	// get reservations by locationId and type and DateTime
	@GET
	@Path("/getReservationByLocationIdAndTypeAndDateTime/{locationId}/{type}/{dateTime}")
	public String getReservationByLocationIdAndTypeAndDateTime(@PathParam("locationId") String locationId, @PathParam("type") int tid, @PathParam("dateTime") String dateTime,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException,ParseException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByLocationIdAndTypeAndDateTime(em, locationId, tid, dateTime));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// future web service for business app
	/**
	 * 
	 *
	 * @param locationId 
	 * @param dateTime 
	 * @param rid 
	 * @param wid 
	 * @param walkinTypeId 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 * @throws ParseException 
	 */
	// get reservations by locationId and type and DateTime
	@GET
	@Path("/getReservationAndWaitlistByLocationIdAndDateTime/{locationId}/{dateTime}/{resType}/{waitType}/{walkinTypeId}")
	public String getReservationAndWaitlistByLocationIdAndDateTime(@PathParam("locationId") String locationId, @PathParam("dateTime") String dateTime, @PathParam("resType") int rid,
			@PathParam("waitType") int wid, @PathParam("walkinTypeId") int walkinTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, 
			InvalidSessionException,ParseException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			TimezoneTime time = new TimezoneTime();
			dateTime = time.getDateAccordingToGMT(dateTime, locationId, em);
			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationAndWaitlistByLocationIdAndDateTime(em, locationId, dateTime, rid, wid, walkinTypeId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param updatedTime 
	 * @param dateTime 
	 * @param rid 
	 * @param wid 
	 * @param sessionId 
	 * @return 
	 * @throws SQLException 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationAndWaitlistByLocationIdAndDateTime/{locationId}/{updatedTime}/{dateTime}/{resType}/{waitType}/")
	public String getReservationAndWaitlistByLocationIdAndDateTime(@PathParam("locationId") String locationId, @PathParam("updatedTime") String updatedTime, @PathParam("dateTime") String dateTime,
			@PathParam("resType") int rid, @PathParam("waitType") int wid, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws SQLException, FileNotFoundException, IOException,
			 InvalidSessionException
	{

		// todo need to modulize this code
		List<Reservation> list = new ArrayList<Reservation>();
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id LEFT JOIN reservations_status rs "
					+ " ON r.reservations_status_id=rs.id "
					+ " LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
					+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
					+ " LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id "
					+ " where r.updated>=? and r.locations_id = ? and  r.reservation_date_time  >= ? and ( r.reservation_types_id = ? or  r.reservation_types_id =?)  "
					+ " and rs.name not in ('Void Walkin') ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString)
					.setParameter(1, updatedTime)
					.setParameter(2, locationId)
					.setParameter(3, dateTime)
					.setParameter(4, rid)
					.setParameter(5, wid)
					.getResultList();

			for (Object[] objRow : resultList)
			{

				// if this has primary key not 0
				Reservation reservation = new Reservation(objRow);
				list.add(reservation);

			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(list);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param updatedTime 
	 * @param dateTime 
	 * @param wid 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getWaitlistByLocationIdAndDateTime/{locationId}/{updatedTime}/{dateTime}/{waitType}/")
	public String getReservationAndWaitlistByLocationIdAndDateTime(@PathParam("locationId") String locationId, @PathParam("updatedTime") String updatedTime, @PathParam("dateTime") String dateTime,
			@PathParam("waitType") int wid, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		// todo need to modulize this code
		List<Reservation> list = new ArrayList<Reservation>();
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TimezoneTime time = new TimezoneTime();
			updatedTime = time.getDateAccordingToGMTForConnection(em, updatedTime, locationId);

			String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
					+ " LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
					+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
					+ " LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id where "
					+ " r.updated>=? and r.locations_id=? and  r.reservation_date_time>=? and (r.reservation_types_id =?)  and rs.name not in ('Void Walkin')";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, updatedTime).setParameter(2, locationId).setParameter(3, dateTime).setParameter(4, wid).getResultList();

			for (Object[] objRow : resultList)
			{

				// if this has primary key not 0
				Reservation reservation = new Reservation(objRow);
				list.add(reservation);

			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(list);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// webservice for getting both reservation and waitlist by locationId & date

	/**
	 * 
	 *
	 * @param locationId 
	 * @param date 
	 * @param rid 
	 * @param wid 
	 * @param walkinTypeId 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationAndWaitlistByLocationAndDate/{locationid}/{date}/{resTypeId}/{waitTypeId}/{walkinTypeId}")
	public String getReservationAndWaitlistByLocationAndDate(@PathParam("locationid") String locationId, @PathParam("date") Date date, @PathParam("resTypeId") int rid, @PathParam("waitTypeId") int wid,
			@PathParam("walkinTypeId") int walkinTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationAndWaitlistByLocationAndDate(em, locationId, date, rid, wid, walkinTypeId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param date 
	 * @param rid 
	 * @param wid 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationAndWaitlistByLocationAndDate/{locationid}/{date}/{resTypeId}/{waitTypeId}")
	public String getReservationAndWaitlistByLocationAndDate(@PathParam("locationid") String locationId, @PathParam("date") Date date, @PathParam("resTypeId") int rid, @PathParam("waitTypeId") int wid,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationAndWaitlistByLocationAndDate(em, locationId, date, rid, wid));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param dateTime 
	 * @param rid 
	 * @param wid 
	 * @param walkinTypeId 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationAndWaitlistWithOrderByLocationIdAndDateTime/{locationId}/{dateTime}/{resType}/{waitType}/{walkinTypeId}")
	public String getReservationAndWaitlistWithOrderByLocationIdAndDateTime(@PathParam("locationId") String locationId, @PathParam("dateTime") String dateTime, @PathParam("resType") int rid,
			@PathParam("waitType") int wid, @PathParam("walkinTypeId") int walkinTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationAndWaitlistWithOrderByLocationIdAndDateTime(em, httpRequest, locationId, dateTime, rid,
					wid, walkinTypeId, sessionId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param date 
	 * @param tid 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationWithOrderByLocationAndDateAndType/{locationid}/{date}/{type}")
	public String getReservationWithOrderByLocationAndDateAndType(@PathParam("locationid") String locationId, @PathParam("date") Date date, @PathParam("type") int tid,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationWithOrderByLocationAndDateAndType(httpRequest, em, locationId, date, tid, sessionId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param date 
	 * @param rid 
	 * @param wid 
	 * @param walkinTypeId 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationAndWaitlistWithOrderByLocationAndDate/{locationid}/{date}/{resTypeId}/{waitTypeId}/{walkinTypeId}")
	public String getReservationAndWaitlistWithOrderByLocationAndDate(@PathParam("locationid") String locationId, @PathParam("date") Date date, @PathParam("resTypeId") int rid,
			@PathParam("waitTypeId") int wid, @PathParam("walkinTypeId") int walkinTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(new ReservationServiceBean().getReservationAndWaitlistWithOrderByLocationAndDate(httpRequest, em,
					locationId, date, rid, wid, walkinTypeId, sessionId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param reservationPacket 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/add")
	public String insertReservation(ReservationPacket reservationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		ReservationPacket newReservationPacket = null;
		String json = null;
		try
		{
			newReservationPacket = new ReservationPacket();
			newReservationPacket = 	reservationPacket;	
			
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest, sessionId, reservationPacket);
			// adding single transaction code 
			tx = em.getTransaction();
			tx.begin();
			Reservation reservation =  new ReservationServiceForPost().addReservation(httpRequest, em, reservationPacket, sessionId,false);
			
			tx.commit();
			// create json packet for store forward
			Reservation reservation2 = new CommonMethods().getReservationById(httpRequest, em, reservation.getId());
			newReservationPacket.setReservation(reservation2);
			// call synchPacket for store forward
			json = new StoreForwardUtility().returnJsonPacket(newReservationPacket, "ReservationPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, newReservationPacket.getLocationId(), Integer.parseInt(newReservationPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(reservationPacket);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * 
	 *
	 * @param reservationPacket 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/update")
	public String updateReservation(ReservationPacket reservationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest, sessionId, reservationPacket);
			tx = em.getTransaction();
			tx.begin();
			Reservation reservation = new ReservationServiceForPost().updateReservation(httpRequest, em, reservationPacket, false,sessionId);
			tx.commit();
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(reservationPacket, "ReservationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationPacket.getLocationId(), Integer.parseInt(reservationPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(reservation);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * 
	 *
	 * @param reservationPacket 
	 * @param sessionId 
	 * @return 
	 * @throws FileNotFoundException 
	 * @throws InvalidSessionException 
	 * @throws IOException 
	 */
	@POST
	@Path("/delete")
	public String deleteReservation(ReservationPacket reservationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws FileNotFoundException,  InvalidSessionException,
			IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json =null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest, sessionId, reservationPacket);
			tx = em.getTransaction();
			tx.begin();
			Reservation reservation =  new ReservationServiceForPost().deleteReservation(httpRequest, em, reservationPacket);
			tx.commit();
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(reservationPacket, "ReservationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationPacket.getLocationId(), Integer.parseInt(reservationPacket.getMerchantId()));
						
			return new JSONUtility(httpRequest).convertToJsonString(reservation);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * 
	 *
	 * @param reservationPacket 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/updateReservationStatus")
	public String updateReservationStatus(ReservationPacket reservationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json =null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest, sessionId, reservationPacket);
			tx = em.getTransaction();
			tx.begin();
			Reservation reservation = new ReservationServiceForPost().updateReservation(httpRequest, em, reservationPacket, true,sessionId);
			tx.commit();
			json = new StoreForwardUtility().returnJsonPacket(reservationPacket, "ReservationPacket",httpRequest);
			
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationPacket.getLocationId(), Integer.parseInt(reservationPacket.getMerchantId()));
						
			return new JSONUtility(httpRequest).convertToJsonString(reservation);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * 
	 *
	 * @param reservationPacket 
	 * @param sessionId 
	 * @param schemaName 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/addCustomerReservation")
	public String addCustomerReservation(ReservationPacket reservationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @CookieParam(value = "SchemaName") String schemaName)
			throws Exception
	{
		// todo it should be in customer service
		EntityManager em = null;
		EntityTransaction tx = null;
		String json =null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, reservationPacket, schemaName);
			tx = em.getTransaction();
			tx.begin();
			Reservation reservation = new ReservationServiceForPost().addReservation(httpRequest, em, reservationPacket, sessionId,false);
			tx.commit();
			json = new StoreForwardUtility().returnJsonPacket(reservationPacket, "ReservationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, reservationPacket.getLocationId(), Integer.parseInt(reservationPacket.getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(reservation);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * 
	 *
	 * @param reservationPacket 
	 * @param sessionId 
	 * @param schemaName 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/updateCustomerReservation")
	public String updateCustomerReservation(ReservationPacket reservationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @CookieParam(value = "SchemaName") String schemaName)
			throws Exception
	{
		// todo it should be in customer service
		EntityManager em = null;
		EntityTransaction tx = null;
		String json =null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, reservationPacket, schemaName);
			tx = em.getTransaction();
			tx.begin();
			Reservation reservation = new ReservationServiceForPost().updateReservation(httpRequest, em, reservationPacket, false,sessionId);
			tx.commit();
			json = new StoreForwardUtility().returnJsonPacket(reservationPacket, "ReservationPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,reservationPacket.getLocationId(), Integer.parseInt(reservationPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(reservation);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * 
	 *
	 * @param sessionId 
	 * @param holdReservationSlotPacket 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/holdReservationSlotForSlotId")
	public String holdReservationSessionForClient( HoldReservationSlotPacket holdReservationSlotPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		
		String json =null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null, holdReservationSlotPacket, false);
			tx = em.getTransaction();
			tx.begin();
			UserSession us = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, null);			
			String schema = us.getSchema_name();
			if (schema != null)
			{
				holdReservationSlotPacket.setSchemaName(schema);
			}

			int clientID = new ReservationServiceForPost().holdReservationSlotForClient(httpRequest, em, holdReservationSlotPacket, httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME));
			
			tx.commit();
			json = new StoreForwardUtility().returnJsonPacket(holdReservationSlotPacket, "HoldReservationSlotPacket",httpRequest);
			// call synchPacket for store forward
			if(holdReservationSlotPacket.getLocationId()==null){
				holdReservationSlotPacket.setLocationId("0");
			}
			if(holdReservationSlotPacket.getMerchantId()==null){
				holdReservationSlotPacket.setMerchantId("0");
			}
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, holdReservationSlotPacket.getLocationId(), Integer.parseInt(holdReservationSlotPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(clientID);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * 
	 *
	 * @param reservationDate 
	 * @param updatedDate 
	 * @param locationId 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getAllReservationsCreatedAndUpdatedAfter/{reservationDate}/{updatedDate}/{locationId}")
	public String getAllReservationsUpdatedAfter(@PathParam("reservationDate") String reservationDate, @PathParam("updatedDate") String updatedDate, @PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		List<Reservation> list = new ArrayList<Reservation>();
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			
//			OrderManagementServiceBean bean = new OrderManagementServiceBean(httpRequest);
//			TimezoneTime timezoneTime = new TimezoneTime();
//			SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
//		    String dateChange =dmyFormat.format(reservationDate);
			String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id " + " LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
					+ " LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
					+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
					+ " LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id where "
					+ " r.updated>? and r.locations_id = ? and r.date=?  and rs.name not in ('Void Walkin') ";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, updatedDate).setParameter(2, locationId).setParameter(3, reservationDate).getResultList();
			for (Object[] objRow : resultList)
			{

				// if this has primary key not 0
				Reservation reservation = new Reservation(objRow);
				list.add(reservation);

			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValuesAndNotDefault(list);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param reservationDate 
	 * @param updatedDate 
	 * @param locationId 
	 * @param waitlistId 
	 * @param sessionId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getAllWaitlistCreatedAndUpdatedAfter/{reservationDate}/{updatedDate}/{locationId}/{waitlistId}")
	public String getAllWaitlistCreatedAndUpdatedAfter(@PathParam("reservationDate") String reservationDate, @PathParam("updatedDate") String updatedDate, @PathParam("locationId") String locationId,
			@PathParam("waitlistId") int waitlistId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		List<Reservation> list = new ArrayList<Reservation>();
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			TimezoneTime time = new TimezoneTime();
			updatedDate = time.getDateAccordingToGMTForConnection(em, updatedDate, locationId);
		
			String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id " + " LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
					+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
					+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
					+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id where "
					+ "r.updated>? and r.locations_id = ? and r. reservation_types_id = ? and r.date=? and rs.name not in ('Void Walkin') ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, updatedDate).setParameter(2, locationId).setParameter(3, waitlistId).setParameter(4, reservationDate).getResultList();
			for (Object[] objRow : resultList)
			{

				// if this has primary key not 0
				Reservation reservation = new Reservation(objRow);
				list.add(reservation);

			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(list);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	/**
	 * 
	 *
	 * @param userID 
	 * @param sessionId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getAllCustomerReservation/{userID}")
	public String getAllCustomerReservation(@PathParam("userID") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException,  InvalidSessionException
	{

		EntityManager em = null;
		try
		{
			Reservation reservation = null;
			List<Reservation> list = new ArrayList<Reservation>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "Select * from reservations where users_id= " + userId;
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();
			for (Object[] objRow : resultList)
			{
				try
				{
					reservation = new Reservation(objRow);
					list.add(reservation);
				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(list);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param userID 
	 * @param date 
	 * @param type 
	 * @param sessionId 
	 * @param schemaName 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationOrWaitListByLocationIdUserIdDateAndType/{locationId}/{userID}/{date}/{type}/")
	public String getReservationOrWaitListByLocationIdUserIdDateAndType(@PathParam("locationId") String locationId, @PathParam("userID") String userId, @PathParam("date") String date,
			@PathParam("type") int type, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @CookieParam(value = "SchemaName") String schemaName) throws Exception
	{

		List<Reservation> list = new ArrayList<Reservation>();
		Reservation reservation = null;
		Statement cstmt = null;
//		ResultSet rs = null;
//		Connection connection = null;
		EntityManager em = null;
		try
		{
			
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);

			String queryString = selectClause + " from reservations r left join users u on u.id=r.users_id " + " LEFT JOIN reservations_status rs ON r.reservations_status_id=rs.id "
					+ "LEFT JOIN reservations_types rt ON r.reservation_types_id=rt.id LEFT JOIN request_type rqt ON r.request_type_id=rqt.id"
					+ " LEFT JOIN contact_preferences ctpref1 ON r.contact_preference_1=ctpref1.id LEFT JOIN contact_preferences ctpref2 ON r.contact_preference_2=ctpref2.id "
					+ "LEFT JOIN contact_preferences ctpref3 ON r.contact_preference_3=ctpref3.id LEFT JOIN locations loc ON r.locations_id=loc.id where " + " r.date  >='" + date
					+ "' and r.locations_id = '" + locationId + "' and  r.users_id = " + userId + " and (r. reservation_types_id =" + type + ")  and rs.name not in ('Void Walkin') ";

			List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();
			for (Object[] objRow : resultList)
			{
				reservation = new Reservation(objRow);
				list.add(reservation);
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(list);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);

		}

	}

	/**
	 * 
	 *
	 * @param id 
	 * @param sessionId 
	 * @param schemaName 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationForCustomerById/{id}")
	public String getReservationForCustomerById(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @CookieParam(value = "SchemaName") String schemaName) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationForCustomerById(httpRequest, em, id, sessionId, schemaName));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationsId 
	 * @param sessionId 
	 * @param schemaName 
	 * @return 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getAllRequestTypeForCustomerByLocationId/{locationsId}")
	public String getAllRequestTypeForCustomerByLocationId(@PathParam("locationsId") String locationsId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@CookieParam(value = "SchemaName") String schemaName) throws FileNotFoundException,  IOException, InvalidSessionException
	{

		List<RequestType> resultSet;
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);
			String queryString = "select r from RequestType r where r.status !='D' and r.locationsId=?";
			TypedQuery<RequestType> query = em.createQuery(queryString, RequestType.class).setParameter(1, locationsId);
			resultSet = query.getResultList();
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return new JSONUtility(httpRequest).convertToJsonString(resultSet);
	}

	/**
	 * 
	 *
	 * @param sessionId 
	 * @param reservationHoldingClientId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/unHoldReservationSlotForClientId/{reservationHoldingClientId}")
	public String unHoldReservationSessionForClient(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("reservationHoldingClientId") String reservationHoldingClientId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			UserSession us = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);			
			String schema = us.getSchema_name();
			return "" + (new ReservationServiceBean().unHoldReservationSlotForClient(httpRequest, em, sessionId, reservationHoldingClientId, schema));

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationId 
	 * @param sessionId 
	 * @param schemaName 
	 * @return 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getAllReservationsTypeForCustomerByLocationId/{locationId}")
	public String getAllReservationsTypeForCustomerByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@CookieParam(value = "SchemaName") String schemaName) throws FileNotFoundException,  IOException, InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForCustomer(httpRequest, sessionId, schemaName);
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
	 * 
	 *
	 * @param reservationSlotId 
	 * @param isBlock 
	 * @param updatedBy 
	 * @param sessionId 
	 * @return 
	 * @throws NirvanaXPException 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/updateReservationSlotToBlockUnblock/{reservation_slot_id}/{is_block}/{updatedBy}")
	public String updateReservationSlotToBlockUnblock(@PathParam("reservation_slot_id") int reservationSlotId, @PathParam("is_block") int isBlock, @PathParam("updatedBy") String updatedBy,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws NirvanaXPException,  IOException, InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			ReservationServiceBean bean = new ReservationServiceBean();
			return new JSONUtility(httpRequest).convertToJsonString(bean.updateReservationsSlot(reservationSlotId, isBlock, updatedBy, httpRequest, em));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
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
	 * 
	 *
	 * @param reservationAndOrderPacket 
	 * @param sessionId 
	 * @return 
	 * @throws NirvanaXPException 
	 * @throws Exception 
	 * @throws InvalidSessionException 
	 * @throws IOException 
	 */
	@POST
	@Path("/addWalkInOrderAndReservation")
	public String addWalkInOrderAndReservation(ReservationAndOrderPacket reservationAndOrderPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws NirvanaXPException, Exception,
			 InvalidSessionException, IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json =null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerWithSessionIdInPostPacket(httpRequest, sessionId, reservationAndOrderPacket);
			//logger.severe();
			ReservationAndOrderPacket packet = new ReservationServiceForPost().addWalkInOrderAndReservation(httpRequest, em, sessionId, reservationAndOrderPacket, true);
			 
			Reservation responseReservation = packet.getReservationPacket().getReservation();
			Reservation updatedReservation = packet.getReservationUpdated();
			packet.getReservationPacket().setReservation(updatedReservation);
			json = new StoreForwardUtility().returnJsonPacket(packet, "ReservationAndOrderPacket",httpRequest);
			
			packet.getReservationPacket().setReservation(responseReservation);
			
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,reservationAndOrderPacket.getOrderPacket().getLocationId(), Integer.parseInt(reservationAndOrderPacket.getOrderPacket().getMerchantId()));
			return new JSONUtility(httpRequest).convertToJsonString(packet);
		}finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param sessionId 
	 * @param date 
	 * @param time 
	 * @param locationId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationSlotForDateAndTimeLocationId/{date}/{time}/{locationId}")
	public String getReservationSlotForDateAndTimeLocationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("date") String date, @PathParam("time") String time,
			@PathParam("locationId") String locationId) throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationSlotForDateAndTime(em, date, time, locationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param sessionId 
	 * @param reservationId 
	 * @param locationId 
	 * @return 
	 * @throws IOException 
	 * @throws InvalidSessionException 
	 */
	@GET
	@Path("/getReservationHistoryByReservationId/{reservationId}")
	public String getReservationHistoryByReservationId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("reservationId") int reservationId, @PathParam("locationId") String locationId)
			throws IOException,  InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationHistoryByReservationId(em, reservationId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	
}
