/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.debitcard;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.nirvanaxp.payment.gateway.data.TransactionType;

public class ProcessDebitCardRequestHelper
{

	public static List<NameValuePair> createDebitCardResponse(String userName, String password, TransactionType transType, String cardNum, String expDate, String magData, String nameOnCard,
			String amount, String invNum, String pNRef, String pin, String registerNum, String sureChargeAmt, String cashBackAmt, String extData)
	{

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("UserName", userName));
		nameValuePairs.add(new BasicNameValuePair("Password", password));
		nameValuePairs.add(new BasicNameValuePair("TransType", transType.getName()));
		nameValuePairs.add(new BasicNameValuePair("CardNum", cardNum));
		nameValuePairs.add(new BasicNameValuePair("ExpDate", expDate));

		nameValuePairs.add(new BasicNameValuePair("NameOnCard", nameOnCard));
		nameValuePairs.add(new BasicNameValuePair("Amount", amount));
		nameValuePairs.add(new BasicNameValuePair("MagData", magData));
		nameValuePairs.add(new BasicNameValuePair("InvNum", invNum));
		nameValuePairs.add(new BasicNameValuePair("PNRef", pNRef));

		nameValuePairs.add(new BasicNameValuePair("Pin", pin));
		nameValuePairs.add(new BasicNameValuePair("RegisterNum", registerNum));
		nameValuePairs.add(new BasicNameValuePair("SureChargeAmt", sureChargeAmt));
		nameValuePairs.add(new BasicNameValuePair("CashBackAmt", cashBackAmt));
		nameValuePairs.add(new BasicNameValuePair("ExtData", extData));

		List<NameValuePair> externalData = new ArrayList<NameValuePair>(2);
		externalData.add(new BasicNameValuePair("Force", "T"));
		nameValuePairs.add(new BasicNameValuePair("ExtData", externalData.toString()));

		return nameValuePairs;

	}

}
