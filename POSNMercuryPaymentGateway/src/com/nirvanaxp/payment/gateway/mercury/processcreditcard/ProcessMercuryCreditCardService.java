/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mercury.processcreditcard;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.mercury.data.MercuryResponse;
import com.nirvanaxp.payment.gateway.mercury.data.PaymentGatewayServerConstant;
import com.nirvanaxp.payment.gateway.mercury.data.ProcessCardPayment;
import com.nirvanaxp.payment.mercury.gateway.manager.MercuryPaymentGatewayManager;
import com.nirvanaxp.proxy.Proxy;
import com.nirvanaxp.proxy.ProxyInterface;
import com.nirvanaxp.server.util.CalculationRoundUp;
import com.nirvanaxp.service.ServiceInterface;

public class ProcessMercuryCreditCardService extends ProcessCardPayment implements ProxyInterface
{

	private ServiceInterface serviceInterface;
	private static int currentMethod = 0;

	private static final int creditAuth = 1;
	private static final int creditVoid = 2;
	private static final int creditPreCapture = 3;
	private static final int creditBatchSummery = 4;
	private static final int creditBatchClose = 5;

	private MerchentAccount merchant = null;
	private String serverUrl;

	public ProcessMercuryCreditCardService(MercuryPaymentGatewayManager serviceInterface)
	{
		this.serviceInterface = serviceInterface;
	}

	@Override
	public void processSwipeCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, String serverConstant, String url)
			throws Exception
	{
		currentMethod = creditAuth;
		double tipAmt = 0;
		CalculationRoundUp calculationRoundUp = new CalculationRoundUp();
		if (invoice.getTipAmount() != null && !(invoice.getTipAmount().equals("")))
		{
			tipAmt = Double.parseDouble(invoice.getTipAmount());
		}
		// merchentAccount.setUserName("118725340908147");
		// merchentAccount.setPassword("xyz");
		// url = "https://w1.mercurycert.net/PaymentsAPI";
		// creditCard.setEncryptedBlock("2F8248964608156B2B1745287B44CA90A349905F905514ABE3979D7957F13804705684B1C9D5641C");
		// creditCard.setEncryptedBlock("3784b7ec19f57b2461b6548e9232c332b7a7db0952f4c1006eeadef61961e7e23928b1f795a1359c");
		// creditCard.setEncryptedKey("9500030000040C200026");
		// creditCard.setEncryptedKey("62994910450000600018");

		creditCard.setEncryptedFormat("MagneSafe");

		creditCard.setEncryptedBlock(creditCard.getTrack2EncytedHexData());

		creditCard.setEncryptedKey("" + creditCard.getKsn());
		double invoiceAmount = invoice.getTotalAmount() - tipAmt;
		String purchaseAmt = null;
		BigDecimal digitAfterDecimalPoint = new BigDecimal("" + calculationRoundUp.roundOffTo2Digit(invoiceAmount));
		if (digitAfterDecimalPoint.scale() == 1)
		{
			purchaseAmt = ("" + invoiceAmount + "0");
		}
		else
		{
			purchaseAmt = "" + invoiceAmount;
		}

		Map<String, String> map = ProcessCreditCardRequestHelper.createObjectForCreditPreAuth(invoice.getInvoiceNumber(), "POSNirvana 1.0", "" + purchaseAmt, "OneTime", "RecordNumberRequested",
				creditCard.getEncryptedFormat(), "Swiped", creditCard.getEncryptedBlock(), creditCard.getEncryptedKey(), "Test");

		String targetURL = url + serverConstant;
		runTransactionJSON(httpRequest, em, map, targetURL, merchentAccount.getUserName(), merchentAccount.getPassword());

	}

	@Override
	public void processBatchSummery(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, String serverConstant, String url) throws Exception
	{
		merchant = merchentAccount;
		serverUrl = url;
		Map<String, String> map = ProcessCreditCardRequestHelper.createObjectForBatchSummary("POSNirvana 1.0");
		String targetURL = url + serverConstant;
		runTransactionJSON(httpRequest, em, map, targetURL, merchentAccount.getUserName(), merchentAccount.getPassword());

	}

	@Override
	public void processBatchClose(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, String serverConstant, String serverUrl)
	{
		currentMethod = creditBatchSummery;

	}

	private void sendObjectForBatchClose(HttpServletRequest httpRequest, EntityManager em, MercuryResponse mercuryResponse) throws Exception
	{
		currentMethod = creditBatchClose;
		Map<String, String> map = ProcessCreditCardRequestHelper.createObjectForBatchClose("POSNirvana 1.0", mercuryResponse.getBatchNo(), mercuryResponse.getBatchItemCount(),
				mercuryResponse.getNetBatchTotal(), mercuryResponse.getCreditPurchaseCount(), mercuryResponse.getCreditPurchaseAmount(), mercuryResponse.getCreditReturnCount(),
				mercuryResponse.getCreditReturnAmount(), mercuryResponse.getDebitPurchaseCount(), mercuryResponse.getDebitPurchaseAmount(), mercuryResponse.getDebitReturnCount(),
				mercuryResponse.getDebitReturnAmount());
		String targetURL = serverUrl + PaymentGatewayServerConstant.CREDIT_BATCHCLOSE;
		runTransactionJSON(httpRequest, em, map, targetURL, merchant.getUserName(), merchant.getPassword());

	}

	@Override
	public void processCreditCardVoidSale(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, Invoice invoice, String serverConstant, String serverUrl)
			throws Exception
	{
		currentMethod = creditVoid;
		// double tipAmt = 0;
		// merchentAccount.setUserName("112438931977591");
		// merchentAccount.setPassword("xyz");
		// serverUrl = "https://w1.mercurycert.net/PaymentsAPI";
		CalculationRoundUp calculationRoundUp = new CalculationRoundUp();
		// if (invoice.getTipAmount() != null &&
		// !(invoice.getTipAmount().equals(""))) {
		// tipAmt = Double.parseDouble(invoice.getTipAmount());
		// }
		double invoiceAmount = invoice.getTotalAmount();
		String purchaseAmt = null;
		BigDecimal digitAfterDecimalPoint = new BigDecimal("" + calculationRoundUp.roundOffTo2Digit(invoiceAmount));
		if (digitAfterDecimalPoint.scale() == 1)
		{
			purchaseAmt = ("" + invoiceAmount + "0");
		}
		else
		{
			purchaseAmt = "" + invoiceAmount;
		}
		Map<String, String> map = ProcessCreditCardRequestHelper.createObjectForCreditVoidSale(invoice.getInvoiceNumber(), invoice.getPnrRef(), "POSNirvana 1.0", "" + purchaseAmt,
				invoice.getTipAmount(), "OneTime", invoice.getRecordNumber(), invoice.getAuthCode());

		String targetURL = serverUrl + serverConstant;
		runTransactionJSON(httpRequest, em, map, targetURL, merchentAccount.getUserName(), merchentAccount.getPassword());

	}

	@Override
	public void processCreditCardPreAuthCapture(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, String serverConstant, Invoice invoice, String serverUrl)
			throws Exception
	{

		currentMethod = creditPreCapture;
		// merchentAccount.setUserName("112438931977591");
		// merchentAccount.setPassword("xyz");
		// serverUrl = "https://w1.mercurycert.net/PaymentsAPI";
		double tipAmt = 0;
		if (invoice.getTipAmount() != null && !(invoice.getTipAmount().equals("")))
		{
			tipAmt = Double.parseDouble(invoice.getTipAmount());
		}
		double invoiceAmount = invoice.getTotalAmount() - tipAmt;
		CalculationRoundUp calculationRoundUp = new CalculationRoundUp();
		String purchaseAmt = null;
		BigDecimal digitAfterDecimalPoint = new BigDecimal("" + calculationRoundUp.roundOffTo2Digit(invoiceAmount));
		if (digitAfterDecimalPoint.scale() == 1)
		{
			purchaseAmt = ("" + calculationRoundUp.roundOffTo2Digit(invoiceAmount) + "0");
		}
		else
		{
			purchaseAmt = "" + calculationRoundUp.roundOffTo2Digit(invoiceAmount);
		}

		String gratuity = null;
		if (invoice.getTipAmount() != null)
		{
			BigDecimal digitAfterDecimalPointTip = new BigDecimal(invoice.getTipAmount());
			if (digitAfterDecimalPointTip.scale() == 1)
			{
				gratuity = ("" + invoice.getTipAmount() + "0");
			}
			else
			{
				gratuity = ("" + invoice.getTipAmount());
			}

		}
		Map<String, String> map = ProcessCreditCardRequestHelper.createObjectForCreditPreAuthCapture(invoice.getInvoiceNumber(), invoice.getPnrRef(), "POSNirvana 1.0", "" + purchaseAmt, gratuity,
				"OneTime", invoice.getRecordNumber(), invoice.getAcqRefData(), invoice.getAuthCode());
		String targetURL = serverUrl + serverConstant;
		runTransactionJSON(httpRequest, em, map, targetURL, merchentAccount.getUserName(), merchentAccount.getPassword());

	}

	private void runTransactionJSON(HttpServletRequest httpRequest, EntityManager em, Map<String, String> params, String targetURL, String merchantID, String password) throws Exception
	{

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(params);
		// String targetURL = apiURL + endPoint;
		String auth64 = Base64.encodeBase64String((merchantID + ":" + password).getBytes());

		sendRequestToServer(httpRequest, em, jsonString, targetURL, auth64);

	}

	private void sendRequestToServer(HttpServletRequest httpRequest, EntityManager em, String argumentsToPost, String serverUrl, String auth64) throws Exception
	{

		// check Internet before starting thread

		Proxy proxy = new Proxy(this, Proxy.REQUEST_TYPE_POST, Proxy.RESPONSE_TYPE_STRING, argumentsToPost, serverUrl);
		proxy.setContentType("application/json");
		proxy.setAccept("application/json");
		proxy.setAuthrization(auth64);
		proxy.sendRequestToServer(httpRequest, em);

	}

	@Override
	public void responseObtainedFromWebService(HttpServletRequest httpRequest, EntityManager em, String response) throws Exception
	{

		if (response != null)
		{
			MercuryResponse mercuryResponse = new ObjectMapper().readValue(response, MercuryResponse.class);


			if (serviceInterface != null)
			{
				if (currentMethod != creditBatchSummery)
				{
					serviceInterface.responseFromService(httpRequest, em, currentMethod, currentMethod, mercuryResponse);
				}
				else
				{
					sendObjectForBatchClose(httpRequest, em, mercuryResponse);

				}
			}

		}
	}

	@Override
	public void processManualCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, String serverConstant, String url)
			throws Exception
	{

		currentMethod = creditAuth;
		double tipAmt = 0;
		if (invoice.getTipAmount() != null && !(invoice.getTipAmount().equals("")))
		{
			tipAmt = Double.parseDouble(invoice.getTipAmount());
		}
		CalculationRoundUp calculationRoundUp = new CalculationRoundUp();
		creditCard.setEncryptedFormat("MagneSafe");

		creditCard.setEncryptedBlock(creditCard.getTrack2EncytedHexData());

		creditCard.setEncryptedKey("" + creditCard.getKsn());
		double invoiceAmount = invoice.getTotalAmount() - tipAmt;
		String purchaseAmt = null;
		BigDecimal digitAfterDecimalPoint = new BigDecimal("" + calculationRoundUp.roundOffTo2Digit(invoiceAmount));
		if (digitAfterDecimalPoint.scale() == 1)
		{
			purchaseAmt = ("" + invoiceAmount + "0");
		}
		else
		{
			purchaseAmt = "" + invoiceAmount;
		}

		Map<String, String> map = ProcessCreditCardRequestHelper.createObjectForCreditManualPreAuth(invoice.getInvoiceNumber(), "POSNirvana 1.0", "" + purchaseAmt, "OneTime", "Keyed",
				creditCard.getStreet(), creditCard.getZip(), creditCard.getCVNum(), "Test");

		String targetURL = url + serverConstant;
		runTransactionJSON(httpRequest, em, map, targetURL, merchentAccount.getUserName(), merchentAccount.getPassword());

	}

}
