/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

public abstract class ProcessCardPayment
{
	public abstract void processCatureAllCreditCardPayment(HttpServletRequest httpRequest, EntityManager em,	TransactionType transType, String serverUrl) throws IOException, Exception ;
}
