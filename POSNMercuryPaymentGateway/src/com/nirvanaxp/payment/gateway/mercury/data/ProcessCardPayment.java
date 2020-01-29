/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mercury.data;

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

	public abstract void processSwipeCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, String serverConstant, String url)
			throws JsonGenerationException, JsonMappingException, IOException, Exception;

	public abstract void processManualCard(HttpServletRequest httpRequest, EntityManager em, CreditCard creditCard, Invoice invoice, MerchentAccount merchentAccount, String serverConstant, String url)
			throws JsonGenerationException, JsonMappingException, IOException, Exception;

	public abstract void processBatchClose(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, String serverConstant, String serverUrl);

	public abstract void processCreditCardVoidSale(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, Invoice invoice, String serverConstant, String serverUrl)
			throws JsonGenerationException, JsonMappingException, IOException, Exception;

	public abstract void processCreditCardPreAuthCapture(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, String serverConstant, Invoice invoice, String serverUrl)
			throws JsonGenerationException, JsonMappingException, IOException, Exception;

	public abstract void processBatchSummery(HttpServletRequest httpRequest, EntityManager em, MerchentAccount merchentAccount, String serverConstant, String serverUrl)
			throws JsonGenerationException, JsonMappingException, IOException, Exception;

}
