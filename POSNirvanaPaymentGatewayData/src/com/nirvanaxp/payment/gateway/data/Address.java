/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

/**
 * @author Pos Nirvana
 *
 */
public class Address
{

	/**
	 * Customer address street
	 */
	private String street;

	/**
	 * Customer address city
	 */
	private String city;

	/**
	 * Customer address state
	 */
	private String addressState;

	/**
	 * Customer address country
	 */
	private String addressCountry;

	/**
	 * Customer address zip code
	 */
	private String zip;

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getAddressState()
	{
		return addressState;
	}

	public void setAddressState(String addressState)
	{
		this.addressState = addressState;
	}

	public String getAddressCountry()
	{
		return addressCountry;
	}

	public void setAddressCountry(String addressCountry)
	{
		this.addressCountry = addressCountry;
	}

	public String getZip()
	{
		return zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

}
