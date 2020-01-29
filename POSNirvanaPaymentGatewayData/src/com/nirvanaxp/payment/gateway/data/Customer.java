/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

public class Customer
{

	private long cusomerId;

	/**
	 * Customer name
	 */
	private String name;

	/**
	 * Customer email
	 */
	private String email;

	/**
	 * Customer phone number
	 */
	private String phone;

	/**
	 * Customer fax
	 */
	private String fax;

	/**
	 * Customer code
	 */
	private String customCode;

	/**
	 * Purchase order number from customer
	 */
	private String pONum;

	/**
	 * Customer tax exempt status
	 */
	private String taxExcempt;

	private Address address;

	public long getCusomerId()
	{
		return cusomerId;
	}

	public void setCusomerId(long cusomerId)
	{
		this.cusomerId = cusomerId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public String getFax()
	{
		return fax;
	}

	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public String getCustomCode()
	{
		return customCode;
	}

	public void setCustomCode(String customCode)
	{
		this.customCode = customCode;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public String getpONum()
	{
		return pONum;
	}

	public void setpONum(String pONum)
	{
		this.pONum = pONum;
	}

	public String getTaxExcempt()
	{
		return taxExcempt;
	}

	public void setTaxExcempt(String taxExcempt)
	{
		this.taxExcempt = taxExcempt;
	}

}
