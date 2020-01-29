/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.services.jaxrs.packets.BraintreePaymentPacket;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;

/**
 * @author XPERT
 *
 */
public class BraintreePaymentService {

//	private static final NirvanaLogger logger = new NirvanaLogger(
//			BraintreePaymentService.class.getName());
	

	
//	public BraintreePaymentService() throws FileNotFoundException, IOException {
//	}

	
	

	/**
	 * @param httpRequest
	 * @param em
	 * @param braintreePaymentPacket
	 * @param isVoid
	 * @return
	 * @throws Exception 
	 */
	OrderPaymentDetail authOrVoidOrderPaymentForBrainTree(HttpServletRequest httpRequest,
			EntityManager em, BraintreePaymentPacket braintreePaymentPacket,boolean isVoid)
			throws Exception {
		BraintreePayment braintreePayment = new BraintreePayment();
		if(isVoid){
			return braintreePayment.voidOrderPaymentForBrainTree(httpRequest, em, braintreePaymentPacket.getOrderPaymentDetail());
		}else{
			
		}
		return null;
	}

	
	
}
