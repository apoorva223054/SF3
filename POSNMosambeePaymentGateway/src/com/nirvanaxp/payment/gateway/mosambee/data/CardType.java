/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mosambee.data;

public class CardType
{

	public static CardType CARD_TYPE_MASTERCARD = new CardType("MasterCard");
	public static CardType CARD_TYPE_VISA = new CardType("Visa");
	public static CardType CARD_TYPE_DEBITCARD = new CardType("DebitCard");
	public static CardType CARD_TYPE_DISCOVER = new CardType("Discover");
	public static CardType CARD_TYPE_DINERS = new CardType("Diner");
	public static CardType CARD_TYPE_AMEX = new CardType("AMEX");

	public String name;

	/**
	 * @param name
	 */
	public CardType(String name)
	{
		super();
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

}
