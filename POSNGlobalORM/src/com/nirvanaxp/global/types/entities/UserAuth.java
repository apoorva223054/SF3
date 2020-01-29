/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the user_auth database table.
 * 
 */
@Entity
@Table(name = "user_auth")
@NamedQuery(name = "UserAuth.findAll", query = "SELECT u FROM UserAuth u")
public class UserAuth extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "auth_code")
	private String authCode;

	@Column(name = "user_id")
	private String userId;

	public UserAuth()
	{
	}

	public String getAuthCode()
	{
		return this.authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "UserAuth [authCode=" + authCode + ", userId=" + userId + "]";
	}

}