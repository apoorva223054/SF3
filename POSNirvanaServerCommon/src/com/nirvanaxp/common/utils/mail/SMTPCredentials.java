/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.mail;

public class SMTPCredentials
{

	private String toEmail;
	private String fromEmail;
	private String host;
	private String subject;
	private String text;
	private String port;
	private String userName;
	private String password;
	private String verificationUrl;

	public String getToEmail()
	{
		return toEmail;
	}

	public String getFromEmail()
	{
		return fromEmail;
	}

	public String getHost()
	{
		return host;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getText()
	{
		return text;
	}

	public String getPort()
	{
		return port;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setToEmail(String toEmail)
	{
		this.toEmail = toEmail;
	}

	public void setFromEmail(String fromEmail)
	{
		this.fromEmail = fromEmail;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getVerificationUrl()
	{
		return verificationUrl;
	}

	public void setVerificationUrl(String verificationUrl)
	{
		this.verificationUrl = verificationUrl;
	}

}
