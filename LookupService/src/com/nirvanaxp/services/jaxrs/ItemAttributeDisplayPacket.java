package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;

// TODO: Auto-generated Javadoc
/**
 * The Class ItemAttributeDisplayPacket.
 */
public class ItemAttributeDisplayPacket
{

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	/** The display name. */
	private String displayName;

	/** The short name. */
	private String shortName;

	/** The price selling. */
	private BigDecimal priceSelling;

	/** The image name. */
	private String imageName;

	private String itemAttributeTypedisplayName;
	
	private int availability;
	
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
	 * Gets the short name.
	 *
	 * @return the short name
	 */
	public String getShortName()
	{
		return shortName;
	}

	/**
	 * Sets the short name.
	 *
	 * @param shortName
	 *            the new short name
	 */
	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	/**
	 * Gets the price selling.
	 *
	 * @return the price selling
	 */
	public BigDecimal getPriceSelling()
	{
		return priceSelling;
	}

	/**
	 * Sets the price selling.
	 *
	 * @param priceSelling
	 *            the new price selling
	 */
	public void setPriceSelling(BigDecimal priceSelling)
	{
		this.priceSelling = priceSelling;
	}

	/**
	 * Gets the image name.
	 *
	 * @return the image name
	 */
	public String getImageName()
	{
		return imageName;
	}

	/**
	 * Sets the image name.
	 *
	 * @param imageName
	 *            the new image name
	 */
	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getItemAttributeTypedisplayName() {
		return itemAttributeTypedisplayName;
	}

	public void setItemAttributeTypedisplayName(String itemAttributeTypedisplayName) {
		this.itemAttributeTypedisplayName = itemAttributeTypedisplayName;
	}
	
	

	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}

	@Override
	public String toString() {
		return "ItemAttributeDisplayPacket [id=" + id + ", name=" + name
				+ ", displayName=" + displayName + ", shortName=" + shortName
				+ ", priceSelling=" + priceSelling + ", imageName=" + imageName
				+ ", itemAttributeTypedisplayName="
				+ itemAttributeTypedisplayName + ", availability="
				+ availability + "]";
	}
	

	
}
