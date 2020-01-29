/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mosambee.data;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.payment.gateway.data.MerchentAccount;

public abstract class ProcessCardPayment
{

	public abstract void processSwipeCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, TransactionType transactionType,
			String url) throws JsonGenerationException, JsonMappingException, IOException;

	public abstract void processManualCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, TransactionType transactionType,
			String url) throws JsonGenerationException, JsonMappingException, IOException;

	public abstract void processCatureAllCreditCardPayment(HttpServletRequest httpRequest, EntityManager em, TransactionType transactionType, String serverUrl) throws JsonGenerationException,
			JsonMappingException, IOException;

	public abstract void processCreditCardForVoid(HttpServletRequest httpRequest, EntityManager em, Invoice invoice, TransactionType transactionType, String serverUrl) throws JsonGenerationException,
			JsonMappingException, IOException;

	public abstract void processCreditCardForForce(HttpServletRequest httpRequest, EntityManager em, TransactionType transactionType, Invoice invoice, String serverUrl)
			throws JsonGenerationException, JsonMappingException, IOException;

	public abstract void processCreditCardForCapture(HttpServletRequest httpRequest, EntityManager em, TransactionType transactionType, Invoice invoice, String serverUrl)
			throws JsonGenerationException, JsonMappingException, IOException;

}
