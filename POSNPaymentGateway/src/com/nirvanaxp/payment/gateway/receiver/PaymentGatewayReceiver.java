/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.receiver;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard.DataCapResponse;
import com.nirvanaxp.payment.gateway.listner.PaymentDataCapGatewayResponseListner;
import com.nirvanaxp.payment.gateway.listner.PaymentMosambeeGatewayResponseListner;
import com.nirvanaxp.payment.gateway.listner.PaymentPathLinkGatewayResponseListner;
import com.nirvanaxp.payment.gateway.manager.PaymentGatewayManager;
import com.nirvanaxp.payment.gateway.mercury.data.MercuryResponse;
import com.nirvanaxp.payment.gateway.mosambee.transact.amx.data.response.PaymentGatewayMosambeeResponse;
import com.nirvanaxp.payment.gateway.parser.PaymentGatewayParser;
import com.nirvanaxp.payment.gateway.parser.PaymentGatewayResponse;
import com.nirvanaxp.payment.gateway.pathlink.data.server.PayLinkServerConstants;
import com.nirvanaxp.payment.gateway.pathlink.transact.amx.data.response.PaymentGatewayPathLinkResponse;
import com.nirvanaxp.payment.mercury.gateway.listner.MercuryPaymentGatewayResponseListener;

public class PaymentGatewayReceiver implements PaymentPathLinkGatewayResponseListner, MercuryPaymentGatewayResponseListener,  PaymentMosambeeGatewayResponseListner,PaymentDataCapGatewayResponseListner
{

	private PaymentGatewayManager paymentGatewayManager;

	public PaymentGatewayReceiver(PaymentGatewayManager paymentGatewayManager)
	{
		this.paymentGatewayManager = paymentGatewayManager;
	}

	@Override
	public void proessPathLinkPaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, PaymentGatewayPathLinkResponse response) throws Exception
	{
		PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayParser().Parse(response);
		paymentGatewayManager.getParseResonse(httpRequest, em, PayLinkServerConstants.PROCESS_CREDIT_CARD_ID, paymentGatewayResponse, currentMethod);

	}

	@Override
	public void proessMercuryLandPaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, MercuryResponse parsedResponse) throws Exception
	{

		PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayParser().Parse(parsedResponse, currentMethod);
		paymentGatewayManager.getParseResonse(httpRequest, em, 1, paymentGatewayResponse, currentMethod);

	}
	@Override
	public void processMosambeePaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, PaymentGatewayMosambeeResponse response) throws Exception
	{
		PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayParser().Parse(response);
		paymentGatewayManager.getParseResonse(httpRequest, em, PayLinkServerConstants.PROCESS_CREDIT_CARD_ID, paymentGatewayResponse, currentMethod);

	}
	
	@Override
	public void proessDataCapPaymentGatewayResponse(HttpServletRequest httpRequest, EntityManager em, int id, int currentMethod, DataCapResponse response) throws Exception
	{
		PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayParser().Parse(response);
		paymentGatewayManager.getParseResonse(httpRequest, em, PayLinkServerConstants.PROCESS_CREDIT_CARD_ID, paymentGatewayResponse, currentMethod);

	}

	 
	
	
}
