/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.data.result;

public class PaymentPathlinkGatewayResponse
{

	private String result;

	private String responseMsg;

	private String message;

	private String authCode;

	private String pnRef;

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getResponseMsg()
	{
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg)
	{
		this.responseMsg = responseMsg;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public String getPnRef()
	{
		return pnRef;
	}

	public void setPnRef(String pnRef)
	{
		this.pnRef = pnRef;
	}

}
