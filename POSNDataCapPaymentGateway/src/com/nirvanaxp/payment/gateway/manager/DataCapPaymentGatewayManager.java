/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.manager;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.SupportedCardType;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentAction;
import com.nirvanaxp.payment.gateway.data.TransactionType;
import com.nirvanaxp.payment.gateway.dataCap.data.server.DataCapServerConstants;
import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.DataCapResponse;
import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.ProcessCreditCardService;
import com.nirvanaxp.payment.gateway.listner.PaymentDataCapGatewayResponseListner;
import com.nirvanaxp.service.ServiceInterface;

public class DataCapPaymentGatewayManager implements ServiceInterface
{

	private SupportedPaymentAction supportedPaymentAction;
	private SupportedCardType supportedCardType;

	private CreditCard creditCard;
	private Invoice invoice;
	private MerchentAccount merchentAccount;
	private String transactionType;
	private PaymentDataCapGatewayResponseListner paymentDataCapGatewayResponseListner;
	private String serverUrl;

	public DataCapPaymentGatewayManager(SupportedPaymentAction supportedPaymentAction, SupportedCardType supportedCardType, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount,
			String transactionType, PaymentDataCapGatewayResponseListner paymentDataCapGatewayResponseListner, String url)
	{
		this.paymentDataCapGatewayResponseListner=paymentDataCapGatewayResponseListner;
		this.setCreditCard(creditCard);
		this.setSupportedPaymentAction(supportedPaymentAction);
		this.setSupportedCardType(supportedCardType);
		this.setInvoice(invoice);
		this.setMerchentAccount(merchentAccount);
		this.transactionType = (transactionType);
		 
		this.serverUrl = url;
	}

	public SupportedPaymentAction getSupportedPaymentAction()
	{
		return supportedPaymentAction;
	}

	public void setSupportedPaymentAction(SupportedPaymentAction supportedPaymentAction)
	{
		this.supportedPaymentAction = supportedPaymentAction;
	}

	public void ProcessCreditCardRequest(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		ProcessCreditCardService processCreditCardService = new ProcessCreditCardService(this, merchentAccount);
	 if (transactionType.equals(TransactionType.TRANSACTION_TYPE_CAPTURE_ALL.getName()))
		{
			processCreditCardService.processCatureAllCreditCardPayment(httpRequest, em, TransactionType.TRANSACTION_TYPE_CAPTURE_ALL, serverUrl);

		}
	 else if ( transactionType.equals(TransactionType.TRANSACTION_TYPE_FORCE.getName())){
		 processCreditCardService.processPreCatureAllCreditCardPayment(httpRequest, em, TransactionType.TRANSACTION_TYPE_FORCE, serverUrl,invoice);
	 	}
	 
	}
	public void ProcessCreditCardRequestPrecaptureAndSettle(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		ProcessCreditCardService processCreditCardService = new ProcessCreditCardService(this, merchentAccount);
	 if (transactionType.equals(TransactionType.TRANSACTION_TYPE_CAPTURE_ALL.getName()))
		{
			processCreditCardService.processCatureAllCreditCardPayment(httpRequest, em, TransactionType.TRANSACTION_TYPE_CAPTURE_ALL, serverUrl);

		}
	 else if ( transactionType.equals(TransactionType.TRANSACTION_TYPE_FORCE.getName())){
		 processCreditCardService.processPreCatureAllCreditCardPayment(httpRequest, em, TransactionType.TRANSACTION_TYPE_FORCE, serverUrl,invoice);
	 	}
	}

	 
 

	public void ProcessPayment(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		if (supportedCardType.equals(SupportedCardType.CARD_TYPE_CREDIT))
		{
			ProcessCreditCardRequest(httpRequest, em);
		}
		 

	}
	public void ProcessPaymentForPrecaptureAndSettle(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		if (supportedCardType.equals(SupportedCardType.CARD_TYPE_CREDIT))
		{
			ProcessCreditCardRequestPrecaptureAndSettle(httpRequest, em);
		}
		 

	}

	public SupportedCardType getSupportedCardType()
	{
		return supportedCardType;
	}

	public void setSupportedCardType(SupportedCardType supportedCardType)
	{
		this.supportedCardType = supportedCardType;
	}

	public CreditCard getCreditCard()
	{
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard)
	{
		this.creditCard = creditCard;
	}

	@Override
	public void responseFromService(HttpServletRequest httpRequest, EntityManager em, int arg0, int currentMethod, Object parsedResponse) throws Exception
	{
		if (paymentDataCapGatewayResponseListner != null)
			paymentDataCapGatewayResponseListner.proessDataCapPaymentGatewayResponse(httpRequest, em, DataCapServerConstants.PROCESS_CREDIT_CARD_ID, currentMethod,(DataCapResponse) parsedResponse);

	}

	public Invoice getInvoice()
	{
		return invoice;
	}

	public void setInvoice(Invoice invoice)
	{
		this.invoice = invoice;
	}

	public MerchentAccount getMerchentAccount()
	{
		return merchentAccount;
	}

	public void setMerchentAccount(MerchentAccount merchentAccount)
	{
		this.merchentAccount = merchentAccount;
	}

}
