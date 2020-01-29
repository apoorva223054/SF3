/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mosambee.transact.amx.service.creditcard;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mosambee.des3ede.DES3ede;
import com.nirvanaxp.payment.gateway.data.Invoice;

public class ProcessCreditCardRequestHelper
{

	public static List<NameValuePair> createCreditCardResponse(String merchantId, String password, String key)
	{

		String encryptedPassword = 	DES3ede.ED(key,"password="+password);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (merchantId != null)
		{
			nameValuePairs.add(new BasicNameValuePair("merchantId", merchantId));
		}
		if (password != null)
		{
			nameValuePairs.add(new BasicNameValuePair("password", encryptedPassword));
		}
		
		return nameValuePairs;

	}

	public static String createExtData(Invoice invoice)
	{
		try
		{
			String extData = "";
			if (invoice != null)
			{
				if (invoice.getTipAmount() != null)
				{
					String tipAmout = "<TipAmt>" + invoice.getTipAmount() + "</TipAmt>";
					extData = extData + tipAmout;
				}
				if (invoice.getTaxAmount() != null && invoice.getTaxAmount() > 0)
				{
					String taxAmount = "<TaxAmt>" + invoice.getTaxAmount() + "</TaxAmt>";
					extData = extData + taxAmount;
				}
				/*
				 * if (invoice.getInvoiceNumber() != null) { String
				 * invoiceNumber = "<PONum>" + invoice.getInvoiceNumber() +
				 * "</PONum>"; extData = extData + invoiceNumber; }
				 */
			}

			return extData;

		}
		catch (Exception e)
		{
		}
		return null;
	}

}
