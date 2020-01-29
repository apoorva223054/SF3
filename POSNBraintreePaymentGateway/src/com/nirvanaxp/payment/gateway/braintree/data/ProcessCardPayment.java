/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.braintree.data;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.braintreegateway.Transaction;
import com.nirvanaxp.payment.gateway.data.Invoice;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;

public abstract class ProcessCardPayment
{

	public abstract void processCatureAllCreditCardPayment(
			HttpServletRequest httpRequest, EntityManager em,
			TransactionType transType, Invoice invoice,OrderHeader orderHeader,OrderPaymentDetail orderPaymentDetail)
			throws JsonGenerationException, JsonMappingException, IOException ;

	public abstract void responseObtainedFromWebService(HttpServletRequest httpRequest,
			EntityManager em, Transaction transaction, OrderHeader orderHeader)
			throws JsonGenerationException, JsonMappingException, IOException;




	
}
