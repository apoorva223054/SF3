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
import com.nirvanaxp.payment.gateway.listner.PaymentMosambeeGatewayResponseListner;
import com.nirvanaxp.payment.gateway.mosambee.data.server.PayLinkServerConstants;
import com.nirvanaxp.payment.gateway.mosambee.transact.amx.data.response.PaymentGatewayMosambeeResponse;
import com.nirvanaxp.payment.gateway.mosambee.transact.amx.service.creditcard.ProcessCreditCardService;
import com.nirvanaxp.service.ServiceInterface;
public class MosambeePaymentGatewayManager implements ServiceInterface
{

	private SupportedPaymentAction supportedPaymentAction;
	private SupportedCardType supportedCardType;

	private CreditCard creditCard;
	private Invoice invoice;
	private MerchentAccount merchentAccount;
	private String transactionType;
	private PaymentMosambeeGatewayResponseListner paymentMosambeeGatewayResponseListner;
	private String serverUrl;

	public MosambeePaymentGatewayManager(SupportedPaymentAction supportedPaymentAction, SupportedCardType supportedCardType, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount,
			String transactionType, PaymentMosambeeGatewayResponseListner paymentMosambeeGatewayResponseListner, String url)
	{

		this.setCreditCard(creditCard);
		this.setSupportedPaymentAction(supportedPaymentAction);
		this.setSupportedCardType(supportedCardType);
		this.setInvoice(invoice);
		this.setMerchentAccount(merchentAccount);
		this.transactionType = (transactionType);
		this.paymentMosambeeGatewayResponseListner = paymentMosambeeGatewayResponseListner;
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
		if (supportedPaymentAction.equals(SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED))
		{
			if (transactionType.equals(TransactionType.TRANSACTION_TYPE_FORCE.getName()))
			{
				processCreditCardService.processCatureAllCreditCardPayment(httpRequest, em, TransactionType.TRANSACTION_TYPE_CAPTURE_ALL, serverUrl);
			}
		}
	}
	public void ProcessPayment(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		if (supportedCardType.equals(SupportedCardType.CARD_TYPE_CREDIT))
		{
			ProcessCreditCardRequest(httpRequest, em);
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
		if (paymentMosambeeGatewayResponseListner != null)
			paymentMosambeeGatewayResponseListner.processMosambeePaymentGatewayResponse(httpRequest, em, PayLinkServerConstants.PROCESS_CREDIT_CARD_ID, currentMethod,
					((PaymentGatewayMosambeeResponse) parsedResponse));

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
