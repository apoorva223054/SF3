/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateLocalServerInfo.
 */
@XmlRootElement(name = "UpdateLocalServerInfo")
public class UpdateLocalServerInfo
{

	/** The local server url. */
	private String localServerUrl;

	/** The account id. */
	private int accountId;

	/** The updated by. */
	private String updatedBy;

	/**
	 * Gets the local server url.
	 *
	 * @return the local server url
	 */
	public String getLocalServerUrl()
	{
		return localServerUrl;
	}

	/**
	 * Gets the account id.
	 *
	 * @return the account id
	 */
	public int getAccountId()
	{
		return accountId;
	}

	/**
	 * Sets the local server url.
	 *
	 * @param localServerUrl the new local server url
	 */
	public void setLocalServerUrl(String localServerUrl)
	{
		this.localServerUrl = localServerUrl;
	}

	/**
	 * Sets the account id.
	 *
	 * @param accountId the new account id
	 */
	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}


}
