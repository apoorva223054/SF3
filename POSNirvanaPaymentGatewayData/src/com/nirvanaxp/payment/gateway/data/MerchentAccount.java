/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

public class MerchentAccount
{

	public static MerchentAccount TEST_ACCOUNT_STAGING = new MerchentAccount("777700006733", "$Test1234", "21159", "21162", "1525641", "002914", "1566", 0, 0);

	// public static MerchentAccount TEST_ACCOUNT_LIVE =new
	// MerchentAccount("POSN5309","M9700fm2","21159","21162","1525641","002914","1566");
	/**
	 * user name of merchant account
	 */
	private String userName;

	/** Please change value of isDemoBuild when creating demo build for testing */
	public static boolean isDemoBuild = false;

	/**
	 * password of merchant account
	 */
	private String password;

	private String licenseId;

	private String siteId;

	private String deviceId;

	private String developerID;

	private String versionNbr;
	
	private String terminalId;
	
	private String pinPadIpAddress;
	private String pinPadIpPort;

	private int orderSourceGroupToPaymentGatewayTypeId;
	private int orderSourceToPaymentGatewayTypeId;

	public MerchentAccount(String userName, String password, String licenseId, String siteId, String deviceId, String developerID, String versionNbr, int orderSourceGroupToPaymentGatewayTypeId,
			int orderSourceToPaymentGatewayTypeId)
	{
		super();
		this.userName = userName;
		this.password = password;
		// in case of mosambee // we are keeping key in licenseId variable
		this.licenseId = licenseId;
		this.siteId = siteId;
		this.deviceId = siteId;
		this.developerID = developerID;
		this.versionNbr = versionNbr;
		this.orderSourceGroupToPaymentGatewayTypeId = orderSourceGroupToPaymentGatewayTypeId;
		this.orderSourceToPaymentGatewayTypeId = orderSourceToPaymentGatewayTypeId;

	}

	/**
	 * @param userName
	 * @param password
	 */
	public MerchentAccount(String userName, String password)
	{
		super();
		this.userName = userName;
		this.password = password;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getLicenseId()
	{
		return licenseId;
	}

	public void setLicenseId(String licenseId)
	{
		this.licenseId = licenseId;
	}

	public String getSiteId()
	{
		return siteId;
	}

	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getDeveloperID()
	{
		return developerID;
	}

	public void setDeveloperID(String developerID)
	{
		this.developerID = developerID;
	}

	public String getVersionNbr()
	{
		return versionNbr;
	}

	public void setVersionNbr(String versionNbr)
	{
		this.versionNbr = versionNbr;
	}

	/**
	 * @return the orderSourceGroupToPaymentGatewayTypeId
	 */
	public int getOrderSourceGroupToPaymentGatewayTypeId()
	{
		return orderSourceGroupToPaymentGatewayTypeId;
	}

	/**
	 * @param orderSourceGroupToPaymentGatewayTypeId
	 *            the orderSourceGroupToPaymentGatewayTypeId to set
	 */
	public void setOrderSourceGroupToPaymentGatewayTypeId(int orderSourceGroupToPaymentGatewayTypeId)
	{
		this.orderSourceGroupToPaymentGatewayTypeId = orderSourceGroupToPaymentGatewayTypeId;
	}

	/**
	 * @return the orderSourceToPaymentGatewayTypeId
	 */
	public int getOrderSourceToPaymentGatewayTypeId()
	{
		return orderSourceToPaymentGatewayTypeId;
	}

	/**
	 * @param orderSourceToPaymentGatewayTypeId
	 *            the orderSourceToPaymentGatewayTypeId to set
	 */
	public void setOrderSourceToPaymentGatewayTypeId(int orderSourceToPaymentGatewayTypeId)
	{
		this.orderSourceToPaymentGatewayTypeId = orderSourceToPaymentGatewayTypeId;
	}

	public String getTerminalId()
	{
		return terminalId;
	}

	public void setTerminalId(String terminalId)
	{
		this.terminalId = terminalId;
	}

	public String getPinPadIpAddress()
	{
		return pinPadIpAddress;
	}

	public void setPinPadIpAddress(String pinPadIpAddress)
	{
		this.pinPadIpAddress = pinPadIpAddress;
	}

	public String getPinPadIpPort()
	{
		return pinPadIpPort;
	}

	public void setPinPadIpPort(String pinPadIpPort)
	{
		this.pinPadIpPort = pinPadIpPort;
	}
	

}
