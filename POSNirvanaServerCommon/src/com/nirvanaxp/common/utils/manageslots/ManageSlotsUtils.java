/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.manageslots;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.orders.ShiftSchedule;
import com.nirvanaxp.types.entities.orders.ShiftSlots;
import com.nirvanaxp.types.entities.reservation.Reservation;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
import com.nirvanaxp.types.entities.reservation.ReservationsSlot;
import com.nirvanaxp.types.entities.reservation.ReservationsSlot_;

public class ManageSlotsUtils
{
	private static final NirvanaLogger logger = new NirvanaLogger(ManageSlotsUtils.class.getName());

	public static void maintainReservationSlotStatus(ReservationsSchedule reservationsSchedule, ReservationsSlot reservationsSlot)
	{

		int maxReservationAllowedinslot = reservationsSchedule.getReservationAllowed();
		int currentReservationInSlot = reservationsSlot.getCurrentReservationInSlot();
		int currentClientCountHoldingTheSlot = reservationsSlot.getCurrentlyHoldedClient();
		if (maxReservationAllowedinslot <= (currentClientCountHoldingTheSlot + currentReservationInSlot))
		{
			// do not change the status if its in
			// deleted or inactive state
			if (reservationsSlot.getStatus().equals("D") == false && reservationsSlot.getStatus().equals("I") == false)
			{
				reservationsSlot.setStatus("H");
			}
		}
		else
		{
			// make this slot active
			// do not change the status if its in
			// deleted or inactive state
			if (reservationsSlot.getStatus().equals("D") == false && reservationsSlot.getStatus().equals("I") == false)
			{
				reservationsSlot.setStatus("A");
			}

		}

	}

	public static int updateReservationSlotCurrentActiveReservationCount(HttpServletRequest httpRequest, EntityManager em, int reservationSlotId, Reservation r,
			int fromWalikInOrWaitList,
			boolean shouldIncrementReservationCount)
	{
		try
		{

			ReservationsSlot reservationsSlot = em.find(ReservationsSlot.class, reservationSlotId);
			if (reservationsSlot != null)
			{
				reservationsSlot.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				reservationsSlot.setUpdatedBy(r.getUpdatedBy());
				// a reservation might have been created in this slot, so
				// increment the count
				if (shouldIncrementReservationCount)
				{
					reservationsSlot.setCurrentReservationInSlot(reservationsSlot.getCurrentReservationInSlot() + 1);
					r.setReservationSlotId(reservationSlotId);

					// also only active hold slot client can make a reservation,
					// and since he has made a reservation, we must decrement
					// holded client by 1
					// to check it is coming from from walkin or waitlist then
					// in that case client not use to hold slot
					if (fromWalikInOrWaitList == 0)
					{
						reservationsSlot.setCurrentlyHoldedClient(reservationsSlot.getCurrentlyHoldedClient() - 1);
					}
				}
				else
				{
					// a reservation might have been cancelled, so we must
					// change the count
					reservationsSlot.setCurrentReservationInSlot(reservationsSlot.getCurrentReservationInSlot() - 1);
				}

				// check if the slot status must be on hold or made activated or
				// not after the change
				if (reservationsSlot.getStatus().equals("D") == false && reservationsSlot.getStatus().equals("I") == false)
				{
					// get reservation schedule, to get the the max allowed
					// reservation count and active client holding the slot
					ReservationsSchedule reservationsSchedule = (ReservationsSchedule) new CommonMethods().getObjectById("ReservationsSchedule", em,ReservationsSchedule.class, reservationsSlot.getReservationScheduleId());
					if (reservationsSchedule != null)
					{

						ManageSlotsUtils.maintainReservationSlotStatus(reservationsSchedule, reservationsSlot);
					}
				}
				// transaction cleanup
				em.merge(reservationsSlot);
				
			}

		}
		catch (Exception e)
		{

		}
		return reservationSlotId;
	}

	public static ReservationsSlot getReservationSlotForDateAndTime(HttpServletRequest httpRequest, EntityManager em, String date, String time, String locationId)
	{
		try
		{
			if (date.contains(":") == false)
			{
				date = date + " 00:00:00";
			}
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<ReservationsSlot> criteria = builder.createQuery(ReservationsSlot.class);
			Root<ReservationsSlot> r = criteria.from(ReservationsSlot.class);

			TypedQuery<ReservationsSlot> query = em.createQuery(criteria.select(r).where(new Predicate[]
			{ builder.equal(r.get(ReservationsSlot_.date), date),

			builder.equal(r.get(ReservationsSlot_.slotStartTime), time), builder.equal(r.get(ReservationsSlot_.locationId), locationId) }));
			List<ReservationsSlot> reservationsSlotsList = query.getResultList();
			if (reservationsSlotsList != null && reservationsSlotsList.size() > 0)
			{
				return reservationsSlotsList.get(0);

			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e);
		}
		return null;
	}

	public static int getNextAvailableSlotByDateTimeLocationId(HttpServletRequest httpRequest, EntityManager em, String date, String time, String locationsId)
	{
		int reservationsSlotId = 0;
		String queryString = " select rsl.id from reservation_slots rsl " + " join reservations_schedule rch  on rch.id=rsl.reservation_schedule_id  " + " where rsl.date like '" + date
				+ "%' and rsl.slot_start_time>=?  " + " and rsl.is_blocked=0  and rsl.location_id=? " + " and rsl.status != 'D' " + " and rch.reservation_allowed>rsl.current_reservation_in_slot "
				+ " order by slot_start_time  limit 0,1  ";
		try
		{
			reservationsSlotId = (int) em.createNativeQuery(queryString).setParameter(1, time).setParameter(2, locationsId).getSingleResult();
		}
		catch (Exception e)
		{
			
			 logger.severe(e);
		}

		return reservationsSlotId;
	}

	public static int getNextAvailableSlotForUnholdByDateTimeLocationId(HttpServletRequest httpRequest, EntityManager em, String date, String time, String locationsId)
	{
		int reservationsSlotId = 0;
		String queryString = " select rsl.id from reservation_slots rsl " + " join reservations_schedule rch  on rch.id=rsl.reservation_schedule_id  " + " where rsl.date like '" + date
				+ "%' and rsl.slot_start_time>=?  " + " and rsl.is_blocked=0  and rsl.location_id=? " + " and rsl.status != 'D' " + " and rch.reservation_allowed>=rsl.current_reservation_in_slot "
				+ " order by slot_start_time  limit 0,1  ";
		try
		{
			reservationsSlotId = (int) em.createNativeQuery(queryString).setParameter(1, time).setParameter(2, locationsId).getSingleResult();
		}
		catch (Exception e)
		{
			
			 logger.severe(e);
		}

		return reservationsSlotId;
	}
	public static void maintainShiftSlotStatus(ShiftSchedule shiftSchedule, ShiftSlots shiftSlot)
	{

		int maxReservationAllowedinslot = shiftSchedule.getMaxOrderAllowed();
		int currentReservationInSlot = shiftSlot.getCurrentOrderInSlot();
		int currentClientCountHoldingTheSlot = shiftSlot.getCurrentlyHoldedClient();
		if (maxReservationAllowedinslot <= (currentClientCountHoldingTheSlot + currentReservationInSlot))
		{
			// do not change the status if its in
			// deleted or inactive state
			if (shiftSlot.getStatus().equals("D") == false && shiftSlot.getStatus().equals("I") == false)
			{
				shiftSlot.setStatus("H");
			}
		}
		else
		{
			// make this slot active
			// do not change the status if its in
			// deleted or inactive state
			if (shiftSlot.getStatus().equals("D") == false && shiftSlot.getStatus().equals("I") == false)
			{
				shiftSlot.setStatus("A");
			}

		}

	}
}
