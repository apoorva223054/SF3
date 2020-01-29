/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

/**
 * @author Pos Nirvana Required for Concord EFS Purchase Card Level 3 and fuel
 *         card purchases. One item in invoice (item details nested within).
 *         There may be multiple
 */
public class Items
{

	/**
	 * SKU number of item
	 */
	private String skuNumber;

	/**
	 * Required for Concord EFS Purchase Card Level 3 and fuel purchases. UPC
	 * number of item for Purchase Card Level 3 or the NACS (National
	 * Association of Convenience Stores) product code for fuel purchases. The
	 * NACS is an industry standard list that Concord is utilizing. For a list
	 * of NACS product codes, please contact EFSnet customer support at
	 * support@concordefsnet.com or 1-877-852-2637.
	 */
	private String upcNumber;

	/**
	 * Possible tax rate applied to item
	 */
	private Double taxRate;

	/**
	 * Possible tax amount for item
	 */
	private Double taxAmount;

	/**
	 * Total amount of the transaction on the item
	 */
	private Double totalAmount;

	/**
	 * Required for Concord EFS Purchase Card Level 3 and fuel purchases.
	 * Quantity purchased of item
	 */
	private int quantity;

	/**
	 * Item description
	 */
	private String discription;

	/**
	 * Unit of measurement for item
	 */
	private String unitOfMeasurement;

	/**
	 * Required for Concord EFS Purchase Card Level 3 and fuel purchases. Unit
	 * price of item
	 */
	private String unitPrice;

	/**
	 * Possible discount amount on item
	 */
	private Double discountAmount;

	/**
	 * Required for Concord EFS Purchase Card Level 3 and fuel purchases. Item
	 * category for Purchase Card Level 3 or the specific value Fuel to
	 * designate a fuel purchase item
	 */
	private String category;

	public String getSkuNumber()
	{
		return skuNumber;
	}

	public void setSkuNumber(String skuNumber)
	{
		this.skuNumber = skuNumber;
	}

	public String getUpcNumber()
	{
		return upcNumber;
	}

	public void setUpcNumber(String upcNumber)
	{
		this.upcNumber = upcNumber;
	}

	public Double getTaxRate()
	{
		return taxRate;
	}

	public void setTaxRate(Double taxRate)
	{
		this.taxRate = taxRate;
	}

	public Double getTaxAmount()
	{
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount)
	{
		this.taxAmount = taxAmount;
	}

	public Double getTotalAmount()
	{
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	public String getDiscription()
	{
		return discription;
	}

	public void setDiscription(String discription)
	{
		this.discription = discription;
	}

	public String getUnitOfMeasurement()
	{
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement)
	{
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public String getUnitPrice()
	{
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice)
	{
		this.unitPrice = unitPrice;
	}

	public Double getDiscountAmount()
	{
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount)
	{
		this.discountAmount = discountAmount;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

}
