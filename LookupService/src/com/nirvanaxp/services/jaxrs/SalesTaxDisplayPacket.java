package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;

// TODO: Auto-generated Javadoc
/**
 * The Class SalesTaxDisplayPacket.
 */
public class SalesTaxDisplayPacket
{

	/** The id. */
	private String id;

	/** The is item specific. */
	private int isItemSpecific;

	/** The name. */
	private String name;

	/** The display name. */
	private String displayName;

	/** The rate. */
	private BigDecimal rate;

	/** The order source group to sales taxes id. */
	private String orderSourceGroupToSalesTaxesId;

	/** The order source group to sales taxes name. */
	private String orderSourceGroupToSalesTaxesName;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName
	 *            the new display name
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * Gets the order source group to sales taxes id.
	 *
	 * @return the order source group to sales taxes id
	 */
	public String getOrderSourceGroupToSalesTaxesId()
	{
		return orderSourceGroupToSalesTaxesId;
	}

	/**
	 * Sets the order source group to sales taxes id.
	 *
	 * @param orderSourceGroupToSalesTaxesId
	 *            the new order source group to sales taxes id
	 */
	public void setOrderSourceGroupToSalesTaxesId(String orderSourceGroupToSalesTaxesId)
	{
		this.orderSourceGroupToSalesTaxesId = orderSourceGroupToSalesTaxesId;
	}

	/**
	 * Gets the order source group to sales taxes name.
	 *
	 * @return the order source group to sales taxes name
	 */
	public String getOrderSourceGroupToSalesTaxesName()
	{
		return orderSourceGroupToSalesTaxesName;
	}

	/**
	 * Sets the order source group to sales taxes name.
	 *
	 * @param orderSourceGroupToSalesTaxesName
	 *            the new order source group to sales taxes name
	 */
	public void setOrderSourceGroupToSalesTaxesName(String orderSourceGroupToSalesTaxesName)
	{
		this.orderSourceGroupToSalesTaxesName = orderSourceGroupToSalesTaxesName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SalesTaxDisplayPacket [ id=" + id + ", name=" + name + ", displayName=" + displayName + ", rate=" + rate + ", orderSourceGroupToSalesTaxesId=" + orderSourceGroupToSalesTaxesId
				+ ", orderSourceGroupToSalesTaxesName=" + orderSourceGroupToSalesTaxesName + "]";
	}

	/**
	 * Gets the rate.
	 *
	 * @return the rate
	 */
	public BigDecimal getRate()
	{
		return rate;
	}

	/**
	 * Sets the rate.
	 *
	 * @param rate
	 *            the new rate
	 */
	public void setRate(BigDecimal rate)
	{
		this.rate = rate;
	}

	/**
	 * Gets the checks if is item specific.
	 *
	 * @return the checks if is item specific
	 */
	public int getIsItemSpecific()
	{
		return isItemSpecific;
	}

	/**
	 * Sets the checks if is item specific.
	 *
	 * @param isItemSpecific
	 *            the new checks if is item specific
	 */
	public void setIsItemSpecific(int isItemSpecific)
	{
		this.isItemSpecific = isItemSpecific;
	}

}
