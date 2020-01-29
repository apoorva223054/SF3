/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

public class SupportedPaymentGateway
{

	// Variable use for PATHLINK_PAYMENT_GATEWAY
	public static SupportedPaymentGateway PATHLINK_PAYMENT_GATEWAY = new SupportedPaymentGateway("Bridge Pay");
	public static SupportedPaymentGateway HEARTLAND_PAYMENT_GATEWAY = new SupportedPaymentGateway("Heartland");
	public static SupportedPaymentGateway MERCURY_PAYMENT_GATEWAY = new SupportedPaymentGateway("Mercury");
	public static SupportedPaymentGateway BRAINTREE_PAYMENT_GATEWAY = new SupportedPaymentGateway("Braintree");
	public static SupportedPaymentGateway MOSAMBEE_PAYMENT_GATEWAY = new SupportedPaymentGateway("Mosambee");
	public static SupportedPaymentGateway DATACAP_PAYMENT_GATEWAY = new SupportedPaymentGateway("Data Cap");
	public static SupportedPaymentGateway DATACAP_PAYMENT_GATEWAY_FIRST_DATA = new SupportedPaymentGateway("DataCap with FirstData");

	// public static SupportedPaymentGateway CURRENT_GATEWAY = new
	// SupportedPaymentGateway("","",0);

	private String supportedPaymentGateway;
	private String supportedPaymentGatewayUrl;
	private int id;

	public SupportedPaymentGateway(String supportedPaymentGateway)
	{
		this.supportedPaymentGateway = supportedPaymentGateway;
	}

	public String getSupportedPaymentGateway()
	{
		return supportedPaymentGateway;
	}

	public SupportedPaymentGateway(String supportedPaymentGateway, String supportedPaymentGatewayUrl, int id)
	{
		this.supportedPaymentGateway = supportedPaymentGateway;
		this.supportedPaymentGatewayUrl = supportedPaymentGatewayUrl;
		this.setId(id);
	}

	public void setSupportedPaymentGateway(String supportedPaymentGateway)
	{
		this.supportedPaymentGateway = supportedPaymentGateway;
	}

	public boolean equals(Object object)
	{

		if (object != null && object instanceof SupportedPaymentGateway)
		{

			SupportedPaymentGateway supportedPaymentGatewayObj = (SupportedPaymentGateway) object;
			if (supportedPaymentGateway.equals(supportedPaymentGatewayObj.getSupportedPaymentGateway()))
			{
				return true;
			}
			return false;
		}

		return false;
	}

	public String getSupportedPaymentGatewayUrl()
	{
		return supportedPaymentGatewayUrl;
	}

	public void setSupportedPaymentGatewayUrl(String supportedPaymentGatewayUrl)
	{
		this.supportedPaymentGatewayUrl = supportedPaymentGatewayUrl;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

}
