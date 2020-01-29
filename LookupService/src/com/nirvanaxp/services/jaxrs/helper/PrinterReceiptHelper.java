/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.helper;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.locations.receipt.PrinterReceipt;

// TODO: Auto-generated Javadoc
/**
 * The Class PrinterReceiptHelper.
 */
public class PrinterReceiptHelper
{

	/**
	 * Adds the printer receipt.
	 *
	 * @param em
	 *            the em
	 * @param printerReceipt
	 *            the printer receipt
	 * @return the printer receipt
	 */
	public PrinterReceipt addPrinterReceipt(EntityManager em, PrinterReceipt printerReceipt,HttpServletRequest httpRequest)
	{

		printerReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		printerReceipt.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		try {
			if(printerReceipt.getId()==null)
			printerReceipt.setId(new StoreForwardUtility().generateUUID());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		em.persist(printerReceipt);
		return printerReceipt;

	}

	/**
	 * Update printer receipt.
	 *
	 * @param em
	 *            the em
	 * @param printerReceipt
	 *            the printer receipt
	 * @return the printer receipt
	 */
	public PrinterReceipt updatePrinterReceipt(EntityManager em, PrinterReceipt printerReceipt)
	{

		printerReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(printerReceipt);
		return printerReceipt;

	}

	/**
	 * Delete printer receipt.
	 *
	 * @param em
	 *            the em
	 * @param printerReceipt
	 *            the printer receipt
	 * @return the printer receipt
	 */
	public PrinterReceipt deletePrinterReceipt(EntityManager em, PrinterReceipt printerReceipt)
	{
		printerReceipt = (PrinterReceipt) new CommonMethods().getObjectById("PrinterReceipt", em,PrinterReceipt.class, printerReceipt.getId());
		printerReceipt.setStatus("D");
		printerReceipt.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		em.merge(printerReceipt);
		return printerReceipt;

	}

}
