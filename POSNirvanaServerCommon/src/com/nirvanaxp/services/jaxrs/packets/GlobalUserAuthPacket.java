/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GlobalUserAuthPacket")
public class GlobalUserAuthPacket extends PostPacket
{

	private String username;
	private String email;
	private String phone;
	private String verificationUrl;
	private String userId;
	private String password;
	private String authCode;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getVerificationUrl()
	{
		return verificationUrl;
	}

	public void setVerificationUrl(String verificationUrl)
	{
		this.verificationUrl = verificationUrl;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	@Override
	public String toString()
	{
		return "GlobalUserAuthPacket [username=" + username + ", email=" + email + ", phone=" + phone + ", verificationUrl=" + verificationUrl + ", userId=" + userId + ", password=" + password
				+ ", authCode=" + authCode + "]";
	}

}
