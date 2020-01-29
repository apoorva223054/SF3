/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.ReservationPacket;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.Reservation_;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
/**
 * @author nirvanaxp
 *
 */
@WebListener
@Path("/CustomerService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CustomerService extends AbstractNirvanaService
{

	/**  */
	@Context
	HttpServletRequest httpRequest;

	/**  */
	private static final NirvanaLogger logger = new NirvanaLogger(CustomerService.class.getName());

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
	 * @param date 
	 * @param locationId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationSlotForDateAndLocationId/{date}/{locationId}")
	public String getReservationSlotForDate( @PathParam("date") String date, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

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
	 * @param id 
	 * @param date 
	 * @param time 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationByIdAndDateAndTime/{id}/{date}/{time}")
	public String getReservationByIdAndDateAndTime(@PathParam("id") int id, @PathParam("date") String date, @PathParam("time") String time) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByIdAndDateAndTime(httpRequest, em, id, date, time, null));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param holdReservationSlotPacket 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/holdReservationSlotForSlotId")
	public String holdReservationSessionForClient( HoldReservationSlotPacket holdReservationSlotPacket) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			tx = em.getTransaction();
			
			tx.begin();
			 String schema =
			 LocalSchemaEntityManager.getInstance().getSchemaNameUsingReffrenceNo(httpRequest,
					 auth_token);
			 if (schema != null) {
			 holdReservationSlotPacket.setSchemaName(schema);
			 }

			int clientID = new ReservationServiceForPost().holdReservationSlotForClient(httpRequest, em, holdReservationSlotPacket, auth_token);
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(clientID);
		}catch (RuntimeException e)
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
	 * 
	 *
	 * @param reservationHoldingClientId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/unHoldReservationSlotForClientId/{reservationHoldingClientId}")
	public String unHoldReservationSessionForClient( @PathParam("reservationHoldingClientId") String reservationHoldingClientId)
			throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			String schema = LocalSchemaEntityManager.getInstance().getSchemaNameUsingReffrenceNo(httpRequest, auth_token);

			return "" + (new ReservationServiceBean().unHoldReservationSlotForClient(httpRequest, em, null, reservationHoldingClientId, schema));

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
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationById/{id}")
	public String getReservationById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

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
	 * @param reservationPacket 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/add")
	public String insertReservation(ReservationPacket reservationPacket) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			PostPacket packet = new CommonMethods().getPOSNPartner(httpRequest, reservationPacket, auth_token);
			reservationPacket.setMerchantId(packet.getMerchantId());
			tx = em.getTransaction();
			tx.begin();
			reservationPacket.setReservation( new ReservationServiceForPost().addReservation(httpRequest, em, reservationPacket, auth_token,true));
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(reservationPacket);
		}catch (RuntimeException e)
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
	 * 
	 *
	 * @param reservationPacket 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/update")
	public String updateReservation(ReservationPacket reservationPacket) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			PostPacket packet = new CommonMethods().getPOSNPartner(httpRequest, reservationPacket, auth_token);
			reservationPacket.setMerchantId(packet.getMerchantId());
			tx = em.getTransaction();
			tx.begin();
			reservationPacket.setReservation( new ReservationServiceForPost().updateReservation(httpRequest, em, reservationPacket, false,auth_token));
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(reservationPacket);
		}catch (RuntimeException e)
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
	 * 
	 *
	 * @param reservationPacket 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/updateReservationStatus")
	public String updateReservationStatus(ReservationPacket reservationPacket) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			PostPacket packet = new CommonMethods().getPOSNPartner(httpRequest, reservationPacket, auth_token);
			reservationPacket.setMerchantId(packet.getMerchantId());
			// Single transaction implementation By Ap :- 2015-12-29
			tx = em.getTransaction();
			tx.begin();
			Reservation reservation = new ReservationServiceForPost().updateReservation(httpRequest, em, reservationPacket, true,auth_token);
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(reservation);
		}catch (RuntimeException e)
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
	 * 
	 *
	 * @param id 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationByUserId/{userid}")
	public String getReservationByUserId(@PathParam("userid") int id) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

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
	 * @param id 
	 * @param typeid 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationByUserIdAndType/{userid}/{typeid}")
	public String getReservationByUserIdAndType(@PathParam("userid") int id,@PathParam("typeid") int typeid) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new ReservationServiceBean().getReservationByUserIdAndTypeNew( httpRequest, em, id,typeid));
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
	 * @param userId 
	 * @param rid 
	 * @param wid 
	 * @param walkinTypeId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationAndWaitlistWithOrderByLocationAndUserId/{locationid}/{userId}/{resTypeId}/{waitTypeId}/{walkinTypeId}")
	public String getReservationAndWaitlistWithOrderByLocationAndUserId(@PathParam("locationid") String locationId, @PathParam("userId") String userId, @PathParam("resTypeId") int rid,
			@PathParam("waitTypeId") int wid, @PathParam("walkinTypeId") int walkinTypeId) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(new ReservationServiceBean().getReservationAndWaitlistWithOrderByLocationAndUserId(httpRequest, em,
					locationId, userId, rid, wid, walkinTypeId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	
	/**
	 * 
	 *
	 * @param userId 
	 * @param updatedDate 
	 * @param locationId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getAllReservationsByUserIdAndUpdatedAfter/{userId}/{updatedDate}/{locationId}")
	public String getAllReservationsByUserIdAndUpdatedAfter(@PathParam("userId") String userId, 
			@PathParam("updatedDate") String updatedDate, @PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(new ReservationServiceBean().getAllReservationsByUserIdAndUpdatedAfter(httpRequest, em,
					userId, updatedDate, locationId));
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
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getReservationsForCustomerById/{id}")
	public String getReservationsForCustomerById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Reservation> criteria = builder.createQuery(Reservation.class);
			Root<Reservation> root = criteria.from(Reservation.class);
			TypedQuery<Reservation> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(Reservation_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		
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

}
