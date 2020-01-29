/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.listner;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.payment.gateway.mosambee.transact.amx.data.response.PaymentGatewayMosambeeResponse;

public interface PaymentMosambeeGatewayResponseListner
{

	public void processMosambeePaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, PaymentGatewayMosambeeResponse response) throws Exception;

}
