/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/

package com.nirvanaxp.payment.gateway.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.payment.BraintreePayment;
import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.SupportedCardType;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentAction;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentGateway;
import com.nirvanaxp.payment.gateway.exception.POSNirvanaGatewayException;
import com.nirvanaxp.payment.gateway.listener.PaymentGatewayResponseListener;
import com.nirvanaxp.payment.gateway.parser.PaymentGatewayResponse;
import com.nirvanaxp.payment.gateway.receiver.PaymentGatewayReceiver;
import com.nirvanaxp.payment.mercury.gateway.manager.MercuryPaymentGatewayManager;
import com.nirvanaxp.types.entities.orders.OrderHeader;

public class PaymentGatewayManager {

	private SupportedPaymentGateway supportedPaymentGateway;
	private SupportedPaymentAction supportedPaymentAction;
	private SupportedCardType supportedCardType;

	private CreditCard creditCard;
	private Invoice invoice;
	private MerchentAccount merchentAccount;
	private String paymentTransactionType;
	private PaymentGatewayReceiver paymentGatewayReceiver;
	private PaymentGatewayResponseListener paymentGatewayResponseListner;
	private CreditCard requestObjSentToServer;
	private BraintreePayment braintreePayment;

	public PaymentGatewayManager(SupportedPaymentGateway supportedPaymentGateway, SupportedPaymentAction supportedPaymentAction,
			SupportedCardType supportedCardType, String paymenttransactionType, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount) {

		this.setSupportedPaymentGateway(supportedPaymentGateway);
		this.setSupportedPaymentAction(supportedPaymentAction);
		this.setSupportedCardType(supportedCardType);
		this.setInvoice(invoice);
		this.setCreditCard(creditCard);
		this.setMerchentAccount(merchentAccount);
		this.paymentTransactionType = (paymenttransactionType);
	}

	public PaymentGatewayManager(SupportedPaymentGateway supportedPaymentGateway, SupportedPaymentAction supportedPaymentAction,
			SupportedCardType supportedCardType, String transactionType, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount,
			PaymentGatewayResponseListener paymentGatewayResponseListner,BraintreePayment braintreePayment) {

		this.setSupportedPaymentGateway(supportedPaymentGateway);
		this.setSupportedPaymentAction(supportedPaymentAction);
		this.setSupportedCardType(supportedCardType);
		this.setInvoice(invoice);
		this.setCreditCard(creditCard);
		this.setMerchentAccount(merchentAccount);
		this.paymentTransactionType = transactionType;

		this.paymentGatewayResponseListner = paymentGatewayResponseListner;
		this.braintreePayment = braintreePayment;
	}
	
	public SupportedPaymentGateway getSupportedPaymentGateway() {
		return supportedPaymentGateway;
	}

	public void setSupportedPaymentGateway(SupportedPaymentGateway supportedPaymentGateway) {
		this.supportedPaymentGateway = supportedPaymentGateway;
	}

	public SupportedPaymentAction getSupportedPaymentAction() {
		return supportedPaymentAction;
	}

	public void setSupportedPaymentAction(SupportedPaymentAction supportedPaymentAction) {
		this.supportedPaymentAction = supportedPaymentAction;
	}

	public SupportedCardType getSupportedCardType() {
		return supportedCardType;
	}

	public void setSupportedCardType(SupportedCardType supportedCardType) {
		this.supportedCardType = supportedCardType;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public MerchentAccount getMerchentAccount() {
		return merchentAccount;
	}

	public void setMerchentAccount(MerchentAccount merchentAccount) {
		this.merchentAccount = merchentAccount;
	}

	public void ProcessPayment(HttpServletRequest httpRequest, EntityManager em,List<OrderHeader> headers,int isPrecaptured) throws Exception {
		if (paymentGatewayReceiver == null) {
			paymentGatewayReceiver = new PaymentGatewayReceiver(this);
		}
		if (supportedPaymentGateway.getSupportedPaymentGateway()
				.equalsIgnoreCase(SupportedPaymentGateway.PATHLINK_PAYMENT_GATEWAY.getSupportedPaymentGateway())) {
			PathlinkPaymentGatewayManager pathlinkPaymentGatewayManager = new PathlinkPaymentGatewayManager(supportedPaymentAction, supportedCardType,
					creditCard, invoice, merchentAccount, paymentTransactionType, paymentGatewayReceiver,
					supportedPaymentGateway.getSupportedPaymentGatewayUrl());

			pathlinkPaymentGatewayManager.ProcessPayment(httpRequest, em);
			this.requestObjSentToServer = creditCard;
		}
		else if (supportedPaymentGateway.getSupportedPaymentGateway().equals(SupportedPaymentGateway.HEARTLAND_PAYMENT_GATEWAY.getSupportedPaymentGateway())) {
			/*
			 * HeartLandPaymentGatewayManager heartLandPaymentGatewayManager =
			 * new HeartLandPaymentGatewayManager( context,
			 * supportedPaymentAction, supportedCardType, creditCard, invoice,
			 * merchentAccount, paymentTransactionType, paymentGatewayReceiver,
			 * supportedPaymentGateway.getSupportedPaymentGatewayUrl()); try {
			 * heartLandPaymentGatewayManager.ProcessPayment(); } catch
			 * (POSNirvanaGatewayExceptions e) {
			 * 
			 * paymentGatewayResponseListner.proessPaymentGatewayResponse(0, 0,
			 * null); } this.requestObjSentToServer = creditCard;
			 */
		}
		else if (supportedPaymentGateway.getSupportedPaymentGateway().equals(SupportedPaymentGateway.MERCURY_PAYMENT_GATEWAY.getSupportedPaymentGateway())) {
			MercuryPaymentGatewayManager mercuryPaymentGatewayManager = new MercuryPaymentGatewayManager(supportedPaymentAction, supportedCardType, creditCard,
					invoice, merchentAccount, paymentTransactionType, paymentGatewayReceiver, supportedPaymentGateway.getSupportedPaymentGatewayUrl());
			try {
				mercuryPaymentGatewayManager.ProcessPayment(httpRequest, em);
			}
			catch (POSNirvanaGatewayException e) {

				paymentGatewayResponseListner.proessPaymentGatewayResponse(httpRequest, em, 0, 0, null, supportedPaymentGateway, merchentAccount);
			}
			this.requestObjSentToServer = creditCard;
		}
		else if (supportedPaymentGateway.getSupportedPaymentGateway().equals(SupportedPaymentGateway.BRAINTREE_PAYMENT_GATEWAY.getSupportedPaymentGateway())) {
			// 	only one step in payment for Braintree no precapture and batch settle
			BraintreePaymentGatewayManager pathlinkPaymentGatewayManager = new BraintreePaymentGatewayManager(supportedPaymentAction, supportedCardType,
					creditCard, invoice, merchentAccount, paymentTransactionType,braintreePayment);

			pathlinkPaymentGatewayManager.ProcessPayment(httpRequest, em,headers);
			this.requestObjSentToServer = creditCard;
		
		}else if (supportedPaymentGateway.getSupportedPaymentGateway()
			.equalsIgnoreCase(SupportedPaymentGateway.MOSAMBEE_PAYMENT_GATEWAY.getSupportedPaymentGateway())) {
//			MosambeePaymentGatewayManager mosambeePaymentGatewayManager = new MosambeePaymentGatewayManager(supportedPaymentAction, supportedCardType,
//				creditCard, invoice, merchentAccount, paymentTransactionType, paymentGatewayReceiver,
//				supportedPaymentGateway.getSupportedPaymentGatewayUrl());
//
//			mosambeePaymentGatewayManager.ProcessPayment(httpRequest, em);
		this.requestObjSentToServer = creditCard;
	}
		else if (supportedPaymentGateway.getSupportedPaymentGateway().equals(SupportedPaymentGateway.DATACAP_PAYMENT_GATEWAY.getSupportedPaymentGateway())) {
			DataCapPaymentGatewayManager dataCapPaymentGatewayManager = new DataCapPaymentGatewayManager(supportedPaymentAction, supportedCardType,
					creditCard, invoice, merchentAccount, paymentTransactionType,  paymentGatewayReceiver,
					supportedPaymentGateway.getSupportedPaymentGatewayUrl());

			dataCapPaymentGatewayManager.ProcessPayment(httpRequest, em);
			this.requestObjSentToServer = creditCard;
		}
		else if (supportedPaymentGateway.getSupportedPaymentGateway().equals(SupportedPaymentGateway.DATACAP_PAYMENT_GATEWAY_FIRST_DATA.getSupportedPaymentGateway())) {
			DataCapPaymentGatewayManager dataCapPaymentGatewayManager = new DataCapPaymentGatewayManager(supportedPaymentAction, supportedCardType,
					creditCard, invoice, merchentAccount, paymentTransactionType,  paymentGatewayReceiver,
					supportedPaymentGateway.getSupportedPaymentGatewayUrl());

			dataCapPaymentGatewayManager.ProcessPaymentForPrecaptureAndSettle(httpRequest, em);
			this.requestObjSentToServer = creditCard;
		}

		
	}

	public void getParseResonse(HttpServletRequest httpRequest, EntityManager em, int supportCardId, PaymentGatewayResponse paymentGatewayResponse,
			int currentMethod) throws Exception {
		
		if (requestObjSentToServer != null) {
			paymentGatewayResponse.setNameOnCard(requestObjSentToServer.getNameOnCard());
			paymentGatewayResponse.setExpiryDate(requestObjSentToServer.getExpiryDate());
			paymentGatewayResponse.setCardNumber(requestObjSentToServer.getCardNumber());
		}

		paymentGatewayResponseListner.proessPaymentGatewayResponse(httpRequest, em, supportCardId, currentMethod, paymentGatewayResponse, supportedPaymentGateway,
				merchentAccount);
	}
}
