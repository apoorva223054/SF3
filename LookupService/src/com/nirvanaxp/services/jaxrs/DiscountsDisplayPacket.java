package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;

// TODO: Auto-generated Javadoc
/**
 * The Class DiscountsDisplayPacket.
 */
public class DiscountsDisplayPacket
{

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	/** The display name. */
	private String displayName;

	/** The value. */
	private BigDecimal value;

	/** The description. */
	private String description;

	/** The discount type name. */
	private String discountTypeName;

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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public BigDecimal getValue()
	{
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 */
	public void setValue(BigDecimal value)
	{
		this.value = value;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the discount type name.
	 *
	 * @return the discount type name
	 */
	public String getDiscountTypeName()
	{
		return discountTypeName;
	}

	/**
	 * Sets the discount type name.
	 *
	 * @param discountTypeName
	 *            the new discount type name
	 */
	public void setDiscountTypeName(String discountTypeName)
	{
		this.discountTypeName = discountTypeName;
	}
}
