/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.debitcard;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.proxy.ProxyInterface;
import com.nirvanaxp.service.ServiceInterface;

public class ProcessDebitCardService implements ProxyInterface
{

	/** The service interface. */
	// private ServiceInterface serviceInterface;

	/** The current method. */
	// private static int currentMethod = -1;

	public static final int processCreditCardManully = 1;

	public static final int processCreditCardThroughSwipe = 2;

	public ProcessDebitCardService(ServiceInterface serviceInterface)
	{
		super();
		// this.serviceInterface = serviceInterface;

	}

	/**
	 * Process ePayment Credit Card Transaction. Valid input values are:
	 * UserName & Password - Assigned by the payment gateway, <BR>
	 * TransType - Auth | Sale | Return | Void | Force | Capture | RepeatSale |
	 * CaptureAll, <BR>
	 * CardNum - Payment Card Number, ExpDate - Payment Card Expiration Date in
	 * MMYY format, <BR>
	 * MagData - Payment Card Track II Mag-Stripe Data, NameOnCard - Cardholders
	 * Name, Amount - Amount in DDDDD.CC format, <BR>
	 * InvNum - Invoice Tracking Number, PNRef - Reference Number Assigned by
	 * the payment gateway, <BR>
	 * Zip - CardHolder's billing ZipCode used with AVS, <BR>
	 * Street - CardHolder's billing Street Address used with AVS, <BR>
	 * CVNum - CardHolder's Card Verification Number used with CV Check, <BR>
	 * ExtData - Extended Data in XML, Valid Values are: CustomerID<BR>
	 * AuthCode - Original Authorization Code, <BR>
	 * CustCode - Customer Code used with Commercial Cards, <BR>
	 * TipAmt - Tip Amount <BR>
	 * TaxAmt - Tax Amount used with Commercial Cards, <BR>
	 * SequenceNum - Sequence Number used with Recurring Transactions, <BR>
	 * SequenceCount - Sequene Count used with Recurring Transactions, <BR>
	 * ServerID - Server ID, <BR>
	 * TrainingMode - Training Mode T or F, <BR>
	 * Force - Force Duplicates T or F, <BR>
	 * RegisterNum - Register Number, <BR>
	 * PONum - Purhcase Order Number, <BR>
	 * City - the City of the CardHolder's billing address<BR>
	 * @throws Exception 
	 */

//	private void sendRequestToServer(HttpServletRequest httpRequest, EntityManager em, List<NameValuePair> argumentsToPost, String serverUrl) throws Exception
//	{
//
//		// check Internet before starting thread
//
//		// start thread to get data from the web-service
//		Proxy proxy = new Proxy(this, Proxy.REQUEST_TYPE_POST, Proxy.RESPONSE_TYPE_STRING, argumentsToPost, serverUrl);
//		proxy.sendRequestToServer(httpRequest, em);
//
//	}

	@Override
	public void responseObtainedFromWebService(HttpServletRequest httpRequest, EntityManager em, String response)
	{

	}


 

}