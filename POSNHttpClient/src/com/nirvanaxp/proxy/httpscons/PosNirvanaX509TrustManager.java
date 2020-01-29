/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.proxy.httpscons;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class PosNirvanaX509TrustManager implements X509TrustManager
{

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{

	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{

	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return null;
	}

}
