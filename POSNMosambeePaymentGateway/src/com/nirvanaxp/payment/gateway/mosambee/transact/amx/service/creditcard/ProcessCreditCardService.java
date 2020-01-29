/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mosambee.transact.amx.service.creditcard;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.ProcessCardPayment;
import com.nirvanaxp.payment.gateway.data.TransactionType;
import com.nirvanaxp.payment.gateway.mosambee.data.server.PayLinkServerConstants;
import com.nirvanaxp.payment.gateway.mosambee.transact.amx.data.response.PaymentGatewayMosambeeResponse;
import com.nirvanaxp.payment.gateway.mosambee.transact.amx.parser.ParseCreditCardResponse;
import com.nirvanaxp.proxy.ProxyInterface;
import com.nirvanaxp.server.util.HTTPClient;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.service.ServiceInterface;

 

public class ProcessCreditCardService extends ProcessCardPayment implements ProxyInterface
{
	private static final NirvanaLogger logger = new NirvanaLogger(ProcessCreditCardService.class.getName());
	/** The service interface. */
	private ServiceInterface serviceInterface;

	public String merchantId;

	public String password;
	
	public String key;

	/** The current method. */
	private static int currentMethod = -1;

	public static final int processCreditCardManully = 1;
	public ProcessCreditCardService(ServiceInterface serviceInterface, MerchentAccount merchentAccount)
	{
		super();
		this.serviceInterface = serviceInterface;
		// merchant id is username
		merchantId = merchentAccount.getUserName();
		password = merchentAccount.getPassword();
		key = merchentAccount.getLicenseId();
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
	 * 
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	@Override
	public void processCatureAllCreditCardPayment(HttpServletRequest httpRequest, EntityManager em, TransactionType transType, String serverUrl) throws Exception
	{
		currentMethod = processCreditCardManully;
		key ="D30B0C665493191918EB6A4D4CB41CE9ED765596632C139A";
		List<NameValuePair> requestParams = ProcessCreditCardRequestHelper.createCreditCardResponse(merchantId,password,key);
		HTTPClient client = new HTTPClient();
		String response = null;
		try {
		 response =	client.sendPost(requestParams,serverUrl);
		} catch (Exception e) {
			
			 logger.severe(e);
		}
		responseObtainedFromWebService(httpRequest, em, response);
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.proxy.ProxyInterface#responseObtainedFromWebService(javax.servlet.http.HttpServletRequest, javax.persistence.EntityManager, java.lang.String)
	 */
	@Override
	public void responseObtainedFromWebService(HttpServletRequest httpRequest, EntityManager em, String response) throws Exception
	{

		if (response != null)
		{

			PaymentGatewayMosambeeResponse parsedResponse = new ParseCreditCardResponse().Parse((String) response);

			if (serviceInterface != null)
			{

				serviceInterface.responseFromService(httpRequest, em, PayLinkServerConstants.PROCESS_CREDIT_CARD_ID, currentMethod, parsedResponse);
				
			}

		}

	}
}
