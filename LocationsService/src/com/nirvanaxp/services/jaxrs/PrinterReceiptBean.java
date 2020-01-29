/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.locations.receipt.PrinterReceipt;

public class PrinterReceiptBean
{

	public PrinterReceipt addPrinterReceipt(EntityManager em, PrinterReceipt printerReceipt,HttpServletRequest httpRequest) throws Exception
	{

		printerReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printerReceipt.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			if(printerReceipt.getId()==null)
			printerReceipt.setId(new StoreForwardUtility().generateUUID());

			em.persist(printerReceipt);
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
		return printerReceipt;

	}

	public PrinterReceipt updatePrinterReceipt(EntityManager em, PrinterReceipt printerReceipt) throws Exception
	{

		printerReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(printerReceipt);
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
		return printerReceipt;

	}

	public PrinterReceipt deletePrinterReceipt(EntityManager em, PrinterReceipt printerReceipt) throws Exception
	{

		printerReceipt = (PrinterReceipt) new CommonMethods().getObjectById("PrinterReceipt", em,PrinterReceipt.class, printerReceipt.getId());
		printerReceipt.setStatus("D");
		printerReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(printerReceipt);
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
		return printerReceipt;

	}

}
