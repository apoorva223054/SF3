/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.pathlink.transact.amx.service.creditcard;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.pathlink.data.TransactionType;

public class ProcessCreditCardRequestHelper
{

	public static List<NameValuePair> createCreditCardResponse(String userName, String password, TransactionType transType, String cardNum, String expDate, String magData, String nameOnCard,
			String amount, String invNum, String pNRef, String zip, String street, String cVNum, String extData)
	{

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (userName != null)
		{
			nameValuePairs.add(new BasicNameValuePair("UserName", userName));
		}
		if (password != null)
		{
			nameValuePairs.add(new BasicNameValuePair("Password", password));
		}
		if (transType != null && transType.getName() != null)
		{
			nameValuePairs.add(new BasicNameValuePair("TransType", transType.getName()));
		}
		if (cardNum != null)
		{
			nameValuePairs.add(new BasicNameValuePair("CardNum", cardNum));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("CardNum", ""));
		}
		if (expDate != null)
		{
			nameValuePairs.add(new BasicNameValuePair("ExpDate", expDate));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("ExpDate", ""));
		}
		if ((nameOnCard) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("NameOnCard", nameOnCard));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("NameOnCard", ""));
		}
		if ((amount) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("Amount", amount));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("Amount", null));
		}
		if ((magData) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("MagData", magData));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("MagData", ""));
		}
		if ((invNum) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("InvNum", invNum));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("InvNum", ""));
		}
		if ((pNRef) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("PNRef", pNRef));

		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("PNRef", ""));
		}
		if ((zip) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("Zip", zip));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("Zip", ""));
		}
		if ((street) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("Street", street));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("Street", ""));
		}
		if ((cVNum) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("CVNum", cVNum));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("CVNum", ""));
		}
		if ((extData) != null)
		{
			nameValuePairs.add(new BasicNameValuePair("ExtData", extData));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("ExtData", ""));
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
