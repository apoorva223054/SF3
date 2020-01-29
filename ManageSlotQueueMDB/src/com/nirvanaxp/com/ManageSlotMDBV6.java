/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.com;

import javax.annotation.PreDestroy;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PreRemove;

import com.nirvanaxp.common.utils.manageslots.ManageSlotsUtils;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.types.entities.orders.ShiftSchedule;
import com.nirvanaxp.types.entities.orders.ShiftSlots;
import com.nirvanaxp.types.entities.reservation.ReservationsSchedule;
import com.nirvanaxp.types.entities.reservation.ReservationsSlot;

// TODO: Auto-generated Javadoc
/**
 * The Class ManageSlotMDBV6.
 */
@MessageDriven(mappedName = "ManageSlotMDB6")
public class ManageSlotMDBV6 implements MessageListener
{

	/** The Constant logger. */
	private final static NirvanaLogger logger = new NirvanaLogger(ManageSlotMDBV6.class.getName());

	/**
	 * Pre destroy.
	 */
	@PreDestroy
	public void preDestroy()
	{
		try
		{
			System.out.println("predestro shuttong down thread");
		}
		catch (Exception e)
		{

			// todo shlok need to handle exception in below line
			logger.severe(e);
		}
	}

	/**
	 * Pre remove.
	 */
	@PreRemove
	public void preRemove()
	{
		try
		{
			System.out.println("predestro shuttong down thread");
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below line
			logger.severe(e);
		}
	}

	/**
	 * Default constructor.
	 */
	public ManageSlotMDBV6()
	{
	}

	/**
	 * On message.
	 *
	 * @param message
	 *            the message
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message)
	{

		logger.info("Queue: I received a message version3 queue ");

		try
		{
			if (message instanceof TextMessage)
			{

				logger.info("Message is : version 3" + ((TextMessage) message).getText());
				String text = ((TextMessage) message).getText();
				String arr[] = text.split(",");
				if (arr[2].equals("reservationSlot"))
				{
					manageReservationSlotUnHold(Integer.parseInt(arr[0]), arr[1]);
				}
				else
				{
					manageShiftSlotUnHold(Integer.parseInt(arr[0]), arr[1]);
				}
			}
		}
		catch (Exception e)
		{
			// todo shlok need to handle exception in below line
			logger.severe(e);
		}

	}

	/**
	 * Manage reservation slot un hold.
	 *
	 * @param reservationsSlotId
	 *            the reservations slot id
	 * @param schemaName
	 *            the schema name
	 * @throws Exception
	 *             the exception
	 */
	private void manageReservationSlotUnHold(int reservationsSlotId, String schemaName) throws Exception
	{
		EntityManager entitymanager = null;
		try
		{

			if (reservationsSlotId != 0)
			{
				entitymanager = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
				EntityTransaction tx = entitymanager.getTransaction();
				tx.begin();
				ReservationsSlot reservationsSlot = entitymanager.find(ReservationsSlot.class, reservationsSlotId);
				int currentClientCountHoldingTheSlot = reservationsSlot.getCurrentlyHoldedClient();
				currentClientCountHoldingTheSlot = currentClientCountHoldingTheSlot - 1;
				if (currentClientCountHoldingTheSlot < 0)
				{
					currentClientCountHoldingTheSlot = 0;
				}
				reservationsSlot.setCurrentlyHoldedClient(currentClientCountHoldingTheSlot);
				ReservationsSchedule reservationsSchedule = entitymanager.find(ReservationsSchedule.class, reservationsSlot.getReservationScheduleId());
				if (reservationsSchedule != null)
				{

					ManageSlotsUtils.maintainReservationSlotStatus(reservationsSchedule, reservationsSlot);
					// transaction cleanup By Ap :- 2015-12-29
					entitymanager.merge(reservationsSlot);
				}
				tx.commit();
			}
		}
		finally
		{
			if (entitymanager != null)
			{
				LocalSchemaEntityManager.getInstance().closeEntityManager(entitymanager);
			}

		}
	}

	/**
	 * Manage shift slot un hold.
	 *
	 * @param shiftSlotId
	 *            the shift slot id
	 * @param schemaName
	 *            the schema name
	 * @throws Exception
	 *             the exception
	 */
	private void manageShiftSlotUnHold(int shiftSlotId, String schemaName) throws Exception
	{
		EntityManager entitymanager = null;
		EntityTransaction tx = null;
		try
		{

			if (shiftSlotId != 0)
			{
				entitymanager = LocalSchemaEntityManager.getInstance().getEntityManagerUsingName(schemaName);
				tx = entitymanager.getTransaction();
				tx.begin();
				ShiftSlots shiftSlots = entitymanager.find(ShiftSlots.class, shiftSlotId);
				int currentClientCountHoldingTheSlot = shiftSlots.getCurrentlyHoldedClient();
				currentClientCountHoldingTheSlot = currentClientCountHoldingTheSlot - 1;
				if (currentClientCountHoldingTheSlot < 0)
				{
					currentClientCountHoldingTheSlot = 0;
				}
				shiftSlots.setCurrentlyHoldedClient(currentClientCountHoldingTheSlot);
				ShiftSchedule shiftSchedule = entitymanager.find(ShiftSchedule.class, shiftSlots.getShiftScheduleId());
				if (shiftSchedule != null)
				{

					ManageSlotsUtils.maintainShiftSlotStatus(shiftSchedule, shiftSlots);
					// transaction cleanup By Ap :- 2015-12-29
					entitymanager.merge(shiftSlots);
				}
				tx.commit();
			}
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
		finally
		{
			if (entitymanager != null)
			{
				LocalSchemaEntityManager.getInstance().closeEntityManager(entitymanager);
			}

		}

	}
}
