/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.listener;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.payment.gateway.data.MerchentAccount;
import com.nirvanaxp.payment.gateway.data.SupportedPaymentGateway;
import com.nirvanaxp.payment.gateway.parser.PaymentGatewayResponse;

public interface PaymentGatewayResponseListener
{

	public void proessPaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, PaymentGatewayResponse response, SupportedPaymentGateway supportedPaymentGateway,
			MerchentAccount merchentAccount) throws Exception;

}
