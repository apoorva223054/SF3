/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.mercury.gateway.manager;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.SupportedCardType;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentAction;
import com.nirvanaxp.payment.gateway.data.TransactionType;
import com.nirvanaxp.payment.gateway.mercury.data.MercuryResponse;
import com.nirvanaxp.payment.gateway.mercury.data.PaymentGatewayServerConstant;
import com.nirvanaxp.payment.gateway.mercury.processcreditcard.ProcessMercuryCreditCardService;
import com.nirvanaxp.payment.mercury.gateway.listner.MercuryPaymentGatewayResponseListener;
import com.nirvanaxp.service.ServiceInterface;

public class MercuryPaymentGatewayManager implements ServiceInterface
{

	private SupportedPaymentAction supportedPaymentAction;
	private SupportedCardType supportedCardType;

	private CreditCard creditCard;
	private Invoice invoice;
	private MerchentAccount merchentAccount;
	private MercuryPaymentGatewayResponseListener mercuryPaymentGatewayResponseListener;
	private String serverUrl;
	private String transaction;
	private boolean shouldPerformVoid = false;

	public MercuryPaymentGatewayManager(SupportedPaymentAction supportedPaymentAction, SupportedCardType supportedCardType, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount,
			String transactionType, MercuryPaymentGatewayResponseListener mercuryPaymentGatewayResponseListener, String url)
	{

		this.setCreditCard(creditCard);
		this.setSupportedPaymentAction(supportedPaymentAction);
		this.setSupportedCardType(supportedCardType);
		this.setInvoice(invoice);
		this.setMerchentAccount(merchentAccount);
		this.transaction = (transactionType);

		this.mercuryPaymentGatewayResponseListener = mercuryPaymentGatewayResponseListener;
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
		ProcessMercuryCreditCardService processCreditCardService = new ProcessMercuryCreditCardService(this);

		if (supportedPaymentAction.equals(SupportedPaymentAction.ACTION_PAYMENT_SWIPE_NONENCRYPTED))
		{

		}
		else if (supportedPaymentAction.equals(SupportedPaymentAction.ACTION_PAYMENT_SWIPE_ENCYPTED))
		{

			if (transaction.equals(TransactionType.TRANSACTION_TYPE_AUTH.getName()))
			{
				processCreditCardService.processSwipeCard(httpRequest, em, creditCard, invoice, merchentAccount, PaymentGatewayServerConstant.CREDIT_PREAUTH, serverUrl);

			}
			else if (transaction.equals(TransactionType.TRANSACTION_TYPE_VIOD.getName()))
			{

				if (!invoice.isSettle())
				{
					shouldPerformVoid = true;
					processCreditCardService.processCreditCardPreAuthCapture(httpRequest, em, merchentAccount, PaymentGatewayServerConstant.CREDIT_PREAUTHCAPTURE, invoice, serverUrl);
				}
				else
				{
					processCreditCardService.processCreditCardVoidSale(httpRequest, em, merchentAccount, invoice, PaymentGatewayServerConstant.CREDIT_VOID, serverUrl);
				}
			}
			else if (transaction.equals(TransactionType.TRANSACTION_TYPE_CAPTURE_ALL.getName()))
			{
				processCreditCardService.processBatchSummery(httpRequest, em, merchentAccount, PaymentGatewayServerConstant.CREDIT_BATCHSUMMERY, serverUrl);

			}
			else if (transaction.equals(TransactionType.TRANSACTION_TYPE_FORCE.getName()))
			{
				processCreditCardService.processCreditCardPreAuthCapture(httpRequest, em, merchentAccount, PaymentGatewayServerConstant.CREDIT_PREAUTHCAPTURE, invoice, serverUrl);
			}
			else if (transaction.equals(""))
			{
				processCreditCardService.processBatchSummery(httpRequest, em, merchentAccount, PaymentGatewayServerConstant.CREDIT_PREAUTHCAPTURE, serverUrl);

			}

		}

	}

	public void ProcessDebitCardRequest()
	{/**/
	}

	public void ProcessPayment(HttpServletRequest httpRequest, EntityManager em) throws Exception
	{
		if (supportedCardType.equals(SupportedCardType.CARD_TYPE_CREDIT))
		{
			ProcessCreditCardRequest(httpRequest, em);
		}
		else if (supportedCardType.equals(SupportedCardType.CARD_TYPE_DEBITCARD))
		{
			ProcessDebitCardRequest();
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
		if (shouldPerformVoid)
		{
			performVoidOperation(httpRequest, em, (MercuryResponse) parsedResponse);
			shouldPerformVoid = false;
		}
		else if (mercuryPaymentGatewayResponseListener != null)
		{
			mercuryPaymentGatewayResponseListener.proessMercuryLandPaymentGatewayResponse(httpRequest, em, 1, currentMethod, (MercuryResponse) parsedResponse);

		}
	}

	private void performVoidOperation(HttpServletRequest httpRequest, EntityManager em, MercuryResponse parsedResponse) throws Exception
	{

		invoice.setAcqRefData(parsedResponse.getAcqRefData());
		invoice.setRecordNumber(parsedResponse.getRecordNo());
		invoice.setPnrRef(parsedResponse.getRefNo());
		ProcessMercuryCreditCardService processCreditCardService = new ProcessMercuryCreditCardService(this);
		processCreditCardService.processCreditCardVoidSale(httpRequest, em, merchentAccount, invoice, PaymentGatewayServerConstant.CREDIT_VOID, serverUrl);

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
