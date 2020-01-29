/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.pathlink.transact.amx.service.creditcard;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;

import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.payment.gateway.data.Globals;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.pathlink.data.ProcessCardPayment;
import com.nirvanaxp.payment.gateway.pathlink.data.TransactionType;
import com.nirvanaxp.payment.gateway.pathlink.data.server.PayLinkServerConstants;
import com.nirvanaxp.payment.gateway.pathlink.transact.amx.data.response.PaymentGatewayPathLinkResponse;
import com.nirvanaxp.payment.gateway.pathlink.transact.amx.parser.ParseCreditCardResponse;
import com.nirvanaxp.proxy.Proxy;
import com.nirvanaxp.proxy.ProxyInterface;
import com.nirvanaxp.service.ServiceInterface;

public class ProcessCreditCardService extends ProcessCardPayment implements ProxyInterface
{

	/** The service interface. */
	private ServiceInterface serviceInterface;

	public String userName;

	public String password;

	/** The current method. */
	private static int currentMethod = -1;

	public static final int processCreditCardManully = 1;

	public static final int processCreditCardThroughSwipe = 2;

	public ProcessCreditCardService(ServiceInterface serviceInterface, MerchentAccount merchentAccount)
	{
		super();
		this.serviceInterface = serviceInterface;

		if (Globals.IS_TESTING == true)
		{
			userName = MerchentAccount.TEST_ACCOUNT_STAGING.getUserName();
			password = MerchentAccount.TEST_ACCOUNT_STAGING.getPassword();
		}
		else
		{
			userName = merchentAccount.getUserName();
			password = merchentAccount.getPassword();
		}
	}

	public void processTestPayment(HttpServletRequest httpRequest, EntityManager em, TransactionType transType) throws Exception
	{

		currentMethod = processCreditCardManully;

		// "4444333322221111", "1213", "", "John Doe", "1.00"

		String serverUrl = PayLinkServerConstants.SERVER_URL + PayLinkServerConstants.OPERATION_CREDIT_CARD;

		List<NameValuePair> requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse("POSN5309",

		"M9700fm2", transType, "", "", "", "", "", "", "", "", "", "", "");

		sendRequestToServer(httpRequest, em, requestParams, serverUrl);

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
	@Override
	public void processCatureAllCreditCardPayment(HttpServletRequest httpRequest, EntityManager em, TransactionType transType, String serverUrl) throws Exception
	{

		currentMethod = processCreditCardManully;

		// "4444333322221111", "1213", "", "John Doe", "1.00"

		/*
		 * String serverUrl = PayLinkServerConstants.SERVER_URL +
		 * PayLinkServerConstants.OPERATION_CREDIT_CARD;
		 */

		List<NameValuePair> requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(userName,

		password, transType, "", "", "", "", "", "", "", "", "", "", "");

		sendRequestToServer(httpRequest, em, requestParams, serverUrl);

	}

	/**
	 * Send request to server.
	 * 
	 * @param argumentsToPost
	 *            the arguments to post
	 * @param serverUrl
	 *            the server url
	 * @throws Exception 
	 */
	private void sendRequestToServer(HttpServletRequest httpRequest, EntityManager em, List<NameValuePair> argumentsToPost, String serverUrl) throws Exception
	{

		// start thread to get data from the web-service
		if (serverUrl != null && !(serverUrl.equals("")))
		{
			Proxy proxy = new Proxy(this, Proxy.REQUEST_TYPE_POST, Proxy.RESPONSE_TYPE_STRING, argumentsToPost, serverUrl);
			proxy.sendRequestToServer(httpRequest, em);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.proxy.ProxyInterface#responseObtainedFromWebService(java
	 * .lang.String)
	 */
	@Override
	public void responseObtainedFromWebService(HttpServletRequest httpRequest, EntityManager em, String response) throws Exception
	{

		if (response != null)
		{

			PaymentGatewayPathLinkResponse parsedResponse = new ParseCreditCardResponse().Parse((String) response);

			if (serviceInterface != null)
			{

				serviceInterface.responseFromService(httpRequest, em, PayLinkServerConstants.PROCESS_CREDIT_CARD_ID, currentMethod, parsedResponse);
				
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.payment.gateway.data.ProcessCardPayment#ProcessSwipeCard
	 * (com.nirvanaxp.payment.gateway.data.CreditCard,
	 * com.nirvanaxp.payment.gateway.data.Invoice,
	 * com.nirvanaxp.payment.gateway.data.MerchentAccount,
	 * com.nirvanaxp.payment.gateway.data.TransactionType)
	 */
	@Override
	public void processSwipeCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, TransactionType transactionType,
			String serverUrl) throws Exception
	{
		

		currentMethod = processCreditCardThroughSwipe;
		/*
		 * String serverUrl = PayLinkServerConstants.SERVER_URL +
		 * PayLinkServerConstants.OPERATION_CREDIT_CARD;
		 */
		String extData = null;
		if (invoice.getTipAmount() != null && invoice.getTipAmount() != "0")
		{
			String tipAmout = "<TipAmt>" + invoice.getTipAmount() + "</TipAmt>";
			extData = creditCard.getExtDataWithBothTrack() + tipAmout;
		}
		else
		{
			extData = creditCard.getExtDataWithBothTrack();
		}
		List<NameValuePair> requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(merchentAccount.getUserName(), merchentAccount.getPassword(),

		transactionType, creditCard.getCardNumber(), creditCard.getExpiryDate(), creditCard.getMagData(), creditCard.getNameOnCard(), "" + invoice.getTotalAmount(), invoice.getInvoiceNumber(), "",
				creditCard.getZip(), creditCard.getStreet(), creditCard.getCVNum(), extData);

		sendRequestToServer(httpRequest, em, requestParams, serverUrl);

	}

	@Override
	public void processCreditCardForVoid(HttpServletRequest httpRequest, EntityManager em, Invoice invoice, TransactionType transactionType, String serverUrl) throws Exception
	{

		// void
		// call when transation is voided
		currentMethod = processCreditCardManully;

		// "4444333322221111", "1213", "", "John Doe", "1.00"

		/*
		 * String serverUrl = PayLinkServerConstants.SERVER_URL +
		 * PayLinkServerConstants.OPERATION_CREDIT_CARD;
		 */

		List<NameValuePair> requestParams;
		if (MerchentAccount.isDemoBuild)
		{
			requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(userName,

			password, transactionType, "", "", "", "", "" + invoice.getTotalAmount(), invoice.getInvoiceNumber(), invoice.getPnrRef(), "", "", "", "");
		}
		else
		{
			String extData = "<Force>T</Force>";
			requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(userName,

			password, new TransactionType("Reversal"), "", "", "", "", "" + invoice.getTotalAmount(), invoice.getInvoiceNumber(), invoice.getPnrRef(), "", "", "", extData);
		}

		sendRequestToServer(httpRequest, em, requestParams, serverUrl);

	}

	@Override
	public void processCreditCardForForce(HttpServletRequest httpRequest, EntityManager em, TransactionType transactionType, Invoice invoice, String serverUrl) throws Exception
	{
		// force
		currentMethod = processCreditCardManully;

		/*
		 * String serverUrl = PayLinkServerConstants.SERVER_URL +
		 * PayLinkServerConstants.OPERATION_CREDIT_CARD;
		 */

		String extData = ProcessCreditCardRequestHelper.createExtData(invoice);

		List<NameValuePair> requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(userName,

		password, transactionType, "", "", "", "", "" + invoice.getTotalAmount(), invoice.getInvoiceNumber(), invoice.getPnrRef(), "", "", "", extData);

		sendRequestToServer(httpRequest, em, requestParams, serverUrl);

	}

	@Override
	public void processCreditCardForCapture(HttpServletRequest httpRequest, EntityManager em, TransactionType transactionType, Invoice invoice, String serverUrl) throws Exception
	{
		// capture
		currentMethod = processCreditCardManully;

		/*
		 * String serverUrl = PayLinkServerConstants.SERVER_URL +
		 * PayLinkServerConstants.OPERATION_CREDIT_CARD;
		 */
		List<NameValuePair> requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(userName,

		password, transactionType, "", "", "", "", "", invoice.getInvoiceNumber(), invoice.getPnrRef(), "", "", "", "");

		sendRequestToServer(httpRequest, em, requestParams, serverUrl);
	}

	@Override
	public void processManualCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, TransactionType transactionType,
			String serverUrl) throws Exception
	{

		currentMethod = processCreditCardManully;

		/*
		 * String serverUrl = PayLinkServerConstants.SERVER_URL +
		 * PayLinkServerConstants.OPERATION_CREDIT_CARD;
		 */

		String extData = ProcessCreditCardRequestHelper.createExtData(invoice);
		String cvv = "";

		if (creditCard.getCVNum() != null)
		{
			cvv = creditCard.getCVNum().trim();
		}

		List<NameValuePair> requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(userName,

		password, transactionType, creditCard.getCardNumber(), creditCard.getExpiryDate(), "", creditCard.getNameOnCard(), "" + invoice.getTotalAmount(), invoice.getInvoiceNumber(), "", "", "", cvv,
				extData);

		sendRequestToServer(httpRequest, em, requestParams, serverUrl);

	}

}
