/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.ProcessCardPayment;
import com.nirvanaxp.payment.gateway.data.TransactionType;
import com.nirvanaxp.payment.gateway.dataCap.data.server.DataCapServerConstants;
import com.nirvanaxp.proxy.Proxy;
import com.nirvanaxp.proxy.ProxyInterface;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.service.ServiceInterface;

public class ProcessCreditCardService extends ProcessCardPayment implements ProxyInterface
{
	private static final NirvanaLogger logger = new NirvanaLogger(ProcessCreditCardService.class.getName());
	/** The service interface. */
	private ServiceInterface serviceInterface;

	private String password;
	private String username;
	private String MerchantId;
	private String TerminalID;
	private String OperatorID;
	private String UserTrace;
	private String TranType;
	private String TranCode;
	private String SecureDevice;
	private String SequenceNo;
	private String TranDeviceID;
	private String PinPadIpAddress;
	private String PinPadIpPort;

	private DataCapResponse dataCapResponse;
	/** The current method. */
	private static int currentMethod = -1;

	public static final int processCreditCardManully = 1;

	public static final int processCreditCardThroughSwipe = 2;

	public ProcessCreditCardService(ServiceInterface serviceInterface, MerchentAccount merchentAccount)
	{
		super();
		this.serviceInterface = serviceInterface;
		username = merchentAccount.getLicenseId();
		password = merchentAccount.getPassword();
		MerchantId = merchentAccount.getUserName();
		TerminalID = merchentAccount.getTerminalId();
		TranCode = "BatchSummary";
		TranType = "Administrative";
		SecureDevice = "CloudEMV2";
		TranDeviceID = merchentAccount.getDeviceId();
		OperatorID = "TEST";
		UserTrace = "Dev1";
		SequenceNo = "0010010010";
		PinPadIpAddress = merchentAccount.getPinPadIpAddress();
		PinPadIpPort = merchentAccount.getPinPadIpPort();
	}
// need to check 
	public ProcessCreditCardService(String username, String password, String merchantId, String terminalId, String deviceId)
	{
		super();
		this.username = username;
		this.password = password;
		MerchantId = merchantId;
		TerminalID = terminalId;
		TranCode = "BatchSummary";
		TranType = "Administrative";
		SecureDevice = "CloudEMV2";
		TranDeviceID = deviceId;
		OperatorID = "TEST";
		UserTrace = "Dev1";
		SequenceNo = "0010010010";
		PinPadIpAddress = "192.168.1.26";
		PinPadIpPort = "12000";
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
	 * 
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
		// for batch summary call
		String requestParams = ProcessCreditCardRequestHelper
				.createBatchSummaryRequest(MerchantId, TerminalID, OperatorID, UserTrace,
						TranType, TranCode, SecureDevice, SequenceNo, TranDeviceID,
				PinPadIpAddress, PinPadIpPort);

		DataCapResponse capResponse = sendRequestToServer(httpRequest, em, requestParams, serverUrl);
		logger.severe(requestParams);
		logger.severe(capResponse.toString());
		logger.severe("Batch summary request Gateway status:- " + capResponse.getrStream().getCmdStatus() + " Gatway response :- " + capResponse.getrStream().getTextResponse());
		// if(capResponse!=null &&
		// capResponse.getRStream().getBatchItemCount()!= null ){
		if (capResponse != null)
		{
			if (capResponse != null && capResponse.getrStream() != null && capResponse.getrStream().getCmdStatus().equals("Error"))
			{
				logger.severe(httpRequest, "Gateway status:- " + capResponse.getrStream().getCmdStatus() + " Gatway response :- " + capResponse.getrStream().getTextResponse());

			}
			else if (capResponse != null && capResponse.getrStream() != null && capResponse.getrStream().getCmdStatus().equals("Declined"))
			{
				logger.severe(httpRequest, "Gateway status:- " + capResponse.getrStream().getCmdStatus() + " Gatway response :- " + capResponse.getrStream().getTextResponse());
			}
			else if (capResponse != null && capResponse.getrStream() != null && capResponse.getrStream().getCmdStatus().equals("Success")) 
			{
				String requestParamsForBatchClose = ProcessCreditCardRequestHelper.createBatchCloseRequest(MerchantId, TerminalID, OperatorID, UserTrace, TranType, TranCode, SecureDevice, SequenceNo,
						TranDeviceID, PinPadIpAddress, PinPadIpPort, capResponse.getrStream().getNetBatchTotal(), capResponse.getrStream().getBatchItemCount(), capResponse.getrStream().getBatchNo());
				DataCapResponse capResponseForBatchClose = sendRequestToServer(httpRequest, em, requestParamsForBatchClose, serverUrl);
				logger.severe("AFTER BATCH CLOSE SERVER rEUEST :-"+requestParamsForBatchClose);
				this.dataCapResponse = capResponseForBatchClose;
				logger.severe("AFTER BATCH CLOSE SERVER RESPONSE :-"+dataCapResponse.toString());
				responseObtainedFromWebService(httpRequest, em, null);
			}

		}
		// for batch close

	}

	
	public void processPreCatureAllCreditCardPayment(HttpServletRequest httpRequest, EntityManager em, TransactionType transType, String serverUrl,Invoice invoice) throws Exception
	{

		 currentMethod = processCreditCardManully;
		 String tipAmount=null;
		 String totalAmount=null;
		if(invoice.getTipAmountWithFirstData()!= null ){
			 tipAmount=invoice.getTipAmountWithFirstData().toPlainString();
			 logger.severe("tipAmoutn:-ToPlainString"+tipAmount);
		}else{
			 tipAmount="0.00";
		}
		if(invoice.getTotalAmountWithoutTip()!= null ){
			 totalAmount=invoice.getTotalAmountWithoutTip().toPlainString();
			 logger.severe("totalAmount:-ToPlainString"+totalAmount);
		}else{
			 totalAmount="0.00";
		}
		
		
				 
		String requestParams = ProcessCreditCardRequestHelper.createPreCaptureRequest( tipAmount,  totalAmount,  invoice.getAcqRefData(),  invoice.getAuthCode(),  MerchantId,  
				  invoice.getProcessData(), invoice.getRecordNumber(), invoice.getPnrRef(), SecureDevice, SequenceNo,TranDeviceID,PinPadIpAddress, PinPadIpPort);
		
		logger.severe( "Gateway requestParams status:- " + requestParams);
		DataCapResponse capResponse = sendRequestToServer(httpRequest, em, requestParams, serverUrl);
		
		
		logger.severe( "Gateway status:- " + capResponse.getrStream().getCmdStatus() + " Gatway response :- " + capResponse.getrStream().getTextResponse());
		// if(capResponse!=null &&
		// capResponse.getRStream().getBatchItemCount()!= null ){
		if (capResponse != null)
		{
			if (capResponse != null && capResponse.getrStream() != null && capResponse.getrStream().getCmdStatus().equals("Error"))
			{
				logger.severe("Gateway status:- " + capResponse.getrStream().getCmdStatus() + " Gatway response :- " + capResponse.getrStream().getTextResponse());

			}
			else if (capResponse != null && capResponse.getrStream() != null && capResponse.getrStream().getCmdStatus().equals("Declined"))
			{
				logger.severe( "Gateway status:- " + capResponse.getrStream().getCmdStatus() + " Gatway response :- " + capResponse.getrStream().getTextResponse());
			}
			else if (capResponse != null && capResponse.getrStream() != null && capResponse.getrStream().getTextResponse().equals("APPROVED")) 
			{
				logger.severe( "Gateway status:- " + capResponse.getrStream().getCmdStatus() + " Gatway response :- " + capResponse.getrStream().getTextResponse());
				this.dataCapResponse = capResponse;
				responseObtainedFromWebService(httpRequest, em, null);
			}

		}
		// for batch close

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
	public DataCapResponse sendRequestToServer(HttpServletRequest httpRequest, EntityManager em, String argumentsToPost, String serverUrl) throws Exception
	{

		// start thread to get data from the web-service
		try {
			if (serverUrl != null && !(serverUrl.equals("")))
			{
				Proxy proxy = new Proxy(this, Proxy.REQUEST_TYPE_POST, Proxy.RESPONSE_TYPE_STRING, argumentsToPost, serverUrl);
				String auth64 = Base64.encodeBase64String((username + ":" + password).getBytes());

				proxy.setAuthrization("Basic " + auth64);
				String response = proxy.sendPostJSONObject(argumentsToPost, serverUrl);
				logger.severe(response);
				return new ObjectMapper().readValue(response, DataCapResponse.class);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.severe(e);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.proxy.ProxyInterface#responseObtainedFromWebService(java
	 * .lang.String)
	 */
	@Override
	public void responseObtainedFromWebService(HttpServletRequest httpRequest, EntityManager em, String str) throws Exception
	{

		if (dataCapResponse != null)
		{
			if (serviceInterface != null)
			{

				serviceInterface.responseFromService(httpRequest, em, DataCapServerConstants.PROCESS_CREDIT_CARD_ID, currentMethod, dataCapResponse);

			}

		}

	}


}
