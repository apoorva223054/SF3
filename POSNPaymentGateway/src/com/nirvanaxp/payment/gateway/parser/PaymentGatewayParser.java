/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.parser;

import com.braintreegateway.CreditCard;
import com.braintreegateway.Transaction;
import com.nirvanaxp.payment.gateway.data.PaymentGatewayConstant;
import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.DataCapResponse;
import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.RStream;
import com.nirvanaxp.payment.gateway.mercury.data.MercuryResponse;
import com.nirvanaxp.payment.gateway.mosambee.transact.amx.data.response.PaymentGatewayMosambeeResponse;
import com.nirvanaxp.payment.gateway.pathlink.transact.amx.data.response.PaymentGatewayPathLinkResponse;

public class PaymentGatewayParser
{

	public PaymentGatewayResponse Parse(PaymentGatewayPathLinkResponse response)
	{

		try
		{
			PaymentGatewayConstant paymentGatewayConstant = new PaymentGatewayConstant();
			PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
			paymentGatewayResponse.setTheResponseXmlStr(response.getTheResponseXmlStr());
			paymentGatewayResponse.setResult(response.getResult());

			if (response.getMessage().equals("APPROVAL") || response.getResponseMsg().equals("Approved"))
			{

				paymentGatewayResponse.setMessage(paymentGatewayConstant.successfulResponse);
				paymentGatewayResponse.setResponseMsg(paymentGatewayConstant.successfulResponse);
			}
			else
			{
				paymentGatewayResponse.setMessage(response.getMessage());
				paymentGatewayResponse.setResponseMsg(response.getResponseMsg());
			}
			paymentGatewayResponse.setMessage1(response.getMessage1());
			paymentGatewayResponse.setMessage2(response.getMessage2());
			paymentGatewayResponse.setAuthCode(response.getAuthCode());
			paymentGatewayResponse.setPnrRef(response.getPnrRef());

			// host code is null that is why changing it to blank
			paymentGatewayResponse.setHostCode("");
			paymentGatewayResponse.setHostURL(response.getHostURL());
			paymentGatewayResponse.setReceiptURL(response.getReceiptURL());
			paymentGatewayResponse.setGetAvsResult(response.getGetAvsResult());
			paymentGatewayResponse.setGetAvsResultTxt(response.getGetAvsResultTxt());
			paymentGatewayResponse.setGetStreetMatchTxt(response.getGetZipMatchTxt());
			paymentGatewayResponse.setGetZipMatchTxt(response.getGetZipMatchTxt());
			paymentGatewayResponse.setGetCVResult(response.getGetCVResult());
			paymentGatewayResponse.setGetCVResultTxt(response.getGetCVResultTxt());
			paymentGatewayResponse.setGetGetOrigResult(response.getGetGetOrigResult());
			paymentGatewayResponse.setGetCommercialCard(response.getGetCommercialCard());
			paymentGatewayResponse.setWorkingKey(response.getWorkingKey());
			paymentGatewayResponse.setKeyPointer(response.getKeyPointer());
			paymentGatewayResponse.setInvNum(response.getInvNum());
			paymentGatewayResponse.setExtData(response.getExtData());
			paymentGatewayResponse.setCardName(response.getCardName());

			return paymentGatewayResponse;
		}
		catch (Exception e)
		{

		}

		return null;
	}

	 

	public PaymentGatewayResponse Parse(MercuryResponse response, int currentMethod)
	{

		if (response != null)
		{
			PaymentGatewayConstant paymentGatewayConstant = new PaymentGatewayConstant();
			PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
			if (response.getCmdStatus() != null && !(response.getCmdStatus().equals("")) && response.getDSIXReturnCode().equals("000000"))
			{
				if (response.getCmdStatus().equals("Approved") || response.getCmdStatus().equals("Success"))
				{
					paymentGatewayResponse.setResponseMsg(paymentGatewayConstant.successfulResponse);
					paymentGatewayResponse.setMessage(paymentGatewayConstant.successfulResponse);

				}
				else
				{
					paymentGatewayResponse.setResponseMsg(paymentGatewayConstant.unsuccessfulResponse);
					paymentGatewayResponse.setMessage(paymentGatewayConstant.unsuccessfulResponse);

				}

				if (response.getRecordNo() != null)
					paymentGatewayResponse.setHostCode(response.getRecordNo());
				if (response.getRefNo() != null)
				{
					paymentGatewayResponse.setPnrRef(response.getRefNo());
				}
				else
				{
					paymentGatewayResponse.setPnrRef(response.getInvoiceNo());
				}
				if (response.getAuthCode() != null)
					paymentGatewayResponse.setAuthCode(response.getAuthCode());
				if (response.getAcctNo() != null)
					paymentGatewayResponse.setCardNumber(response.getAcctNo());
				if (response.getCardType() != null)
					paymentGatewayResponse.setCardName(response.getCardType());
				if (response.getAcqRefData() != null)
					paymentGatewayResponse.setAcqRefData(response.getAcqRefData());

				if (response.getBatchNo() != null)
					paymentGatewayResponse.setBatchId(response.getBatchNo());
				if (response.getBatchItemCount() != null)
					paymentGatewayResponse.setBatchItemCount(response.getBatchItemCount());
				if (response.getNetBatchTotal() != null)
					paymentGatewayResponse.setNetBatchTotal(response.getNetBatchTotal());
				if (response.getCreditPurchaseCount() != null)
					paymentGatewayResponse.setCreditPurchaseCount(response.getCreditPurchaseCount());
				if (response.getCreditPurchaseAmount() != null)
					paymentGatewayResponse.setCreditPurchaseAmount(response.getCreditPurchaseAmount());
				if (response.getCreditReturnCount() != null)
					paymentGatewayResponse.setCreditReturnCount(response.getCreditReturnCount());
				if (response.getCreditReturnAmount() != null)
					paymentGatewayResponse.setCreditReturnAmount(response.getCreditReturnAmount());
				if (response.getDebitPurchaseCount() != null)
					paymentGatewayResponse.setDebitPurchaseCount(response.getDebitPurchaseCount());
				if (response.getDebitPurchaseAmount() != null)
					paymentGatewayResponse.setDebitPurchaseAmount(response.getDebitPurchaseAmount());
				if (response.getDebitReturnCount() != null)
					paymentGatewayResponse.setDebitReturnCount(response.getDebitReturnCount());
				if (response.getDebitReturnAmount() != null)
					paymentGatewayResponse.setDebitReturnAmount(response.getDebitReturnAmount());

				if (response.getAuthorize() != null)
					paymentGatewayResponse.setInvoicePaidAmount(response.getAuthorize());

				if (response.getPurchase() != null)
					paymentGatewayResponse.setInvoicePaidAmount(response.getPurchase());

				if (response.getGratuity() != null)
					paymentGatewayResponse.setInvoicePaidTip(response.getGratuity());

				if (response.getAuthorize() != null)
					paymentGatewayResponse.setInvoiceAmtWithTip(response.getAuthorize());
			}

			if (response.getInvoiceNo() != null)
			{
				paymentGatewayResponse.setInvNum(response.getInvoiceNo());
			}
			return paymentGatewayResponse;
		}
		return null;
	}
	
	public PaymentGatewayResponse parse(Transaction response)
	{

		try
		{
			PaymentGatewayConstant paymentGatewayConstant = new PaymentGatewayConstant();
			PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
		
			paymentGatewayResponse.setInvoicePaidAmount(response.getAmount().toString());
			
			paymentGatewayResponse.setInvNum(response.getPurchaseOrderNumber());
			CreditCard card = response.getCreditCard();
			paymentGatewayResponse.setCardName(card.getCardholderName());
			paymentGatewayResponse.setCardNumber(card.getLast4());
			paymentGatewayResponse.setExpiryDate(card.getExpirationDate());

			if (response.getProcessorResponseText().equals("Approved"))
			{

				paymentGatewayResponse.setMessage(paymentGatewayConstant.successfulResponse);
				paymentGatewayResponse.setResponseMsg(paymentGatewayConstant.successfulResponse);
			}
			else
			{
				paymentGatewayResponse.setMessage(response.getProcessorResponseText());
				paymentGatewayResponse.setResponseMsg(response.getProcessorResponseText());
			}
			
			
			paymentGatewayResponse.setPnrRef(response.getProcessorAuthorizationCode());

			// host code is null that is why changing it to blank
		

			return paymentGatewayResponse;
		}
		catch (Exception e)
		{

		}

		return null;
	}
	public PaymentGatewayResponse Parse(PaymentGatewayMosambeeResponse response)
	{

		try
		{
			PaymentGatewayConstant paymentGatewayConstant = new PaymentGatewayConstant();
			PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
			paymentGatewayResponse.setTheResponseXmlStr(response.getTheResponseXmlStr());
			paymentGatewayResponse.setResult(response.getResult());

			if (response.getMessage().equals("APPROVAL") || response.getResponseMsg().equals("Approved"))
			{

				paymentGatewayResponse.setMessage(paymentGatewayConstant.successfulResponse);
				paymentGatewayResponse.setResponseMsg(paymentGatewayConstant.successfulResponse);
			}
			else
			{
				paymentGatewayResponse.setMessage(response.getMessage());
				paymentGatewayResponse.setResponseMsg(response.getResponseMsg());
			}
			paymentGatewayResponse.setMessage1(response.getMessage1());
			paymentGatewayResponse.setMessage2(response.getMessage2());
			paymentGatewayResponse.setAuthCode(response.getAuthCode());
			paymentGatewayResponse.setPnrRef(response.getPnrRef());

			// host code is null that is why changing it to blank
			paymentGatewayResponse.setHostCode("");
			paymentGatewayResponse.setHostURL(response.getHostURL());
			paymentGatewayResponse.setReceiptURL(response.getReceiptURL());
			paymentGatewayResponse.setGetAvsResult(response.getGetAvsResult());
			paymentGatewayResponse.setGetAvsResultTxt(response.getGetAvsResultTxt());
			paymentGatewayResponse.setGetStreetMatchTxt(response.getGetZipMatchTxt());
			paymentGatewayResponse.setGetZipMatchTxt(response.getGetZipMatchTxt());
			paymentGatewayResponse.setGetCVResult(response.getGetCVResult());
			paymentGatewayResponse.setGetCVResultTxt(response.getGetCVResultTxt());
			paymentGatewayResponse.setGetGetOrigResult(response.getGetGetOrigResult());
			paymentGatewayResponse.setGetCommercialCard(response.getGetCommercialCard());
			paymentGatewayResponse.setWorkingKey(response.getWorkingKey());
			paymentGatewayResponse.setKeyPointer(response.getKeyPointer());
			paymentGatewayResponse.setInvNum(response.getInvNum());
			paymentGatewayResponse.setExtData(response.getExtData());
			paymentGatewayResponse.setCardName(response.getCardName());

			return paymentGatewayResponse;
		}
		catch (Exception e)
		{

		}

		return null;
	}
	public PaymentGatewayResponse Parse(DataCapResponse response)
	{
		RStream  rStream = response.getrStream();
		try
		{
			PaymentGatewayConstant paymentGatewayConstant = new PaymentGatewayConstant();
			PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
			paymentGatewayResponse.setTheResponseXmlStr(rStream.getTextResponse());
			paymentGatewayResponse.setResult(rStream.getCmdStatus());

			if (rStream.getCmdStatus().equals("APPROVAL") || rStream.getCmdStatus().equals("Approved"))
			{

				paymentGatewayResponse.setMessage(paymentGatewayConstant.successfulResponse);
				paymentGatewayResponse.setResponseMsg(paymentGatewayConstant.successfulResponse);
				
			}
			else
			{
				paymentGatewayResponse.setMessage(rStream.getCmdStatus());
				paymentGatewayResponse.setResponseMsg(rStream.getCmdStatus());
			}
			 
		 

			// host code is null that is why changing it to blank
		 
		 
		 

			return paymentGatewayResponse;
		}
		catch (Exception e)
		{

		}

		return null;
	}
}
