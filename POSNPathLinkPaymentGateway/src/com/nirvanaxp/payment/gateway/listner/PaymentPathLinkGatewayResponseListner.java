/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.listner;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.payment.gateway.pathlink.transact.amx.data.response.PaymentGatewayPathLinkResponse;

public interface PaymentPathLinkGatewayResponseListner
{

	public void proessPathLinkPaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, PaymentGatewayPathLinkResponse response) throws Exception;

}
