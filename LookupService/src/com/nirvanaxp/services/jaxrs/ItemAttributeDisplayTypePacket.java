package com.nirvanaxp.services.jaxrs;

// TODO: Auto-generated Javadoc
/**
 * The Class ItemAttributeDisplayTypePacket.
 */
public class ItemAttributeDisplayTypePacket
{

	/** The id. */
	private String id;

	/** The name. */
	private String name;

	/** The display name. */
	private String displayName;

	/** The description. */
	private String description;

	/** The modifier type. */
	private int modifierType;

	/** The modifier type name. */
	private String modifierTypeName;

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
	 * Gets the modifier type.
	 *
	 * @return the modifier type
	 */
	public int getModifierType()
	{
		return modifierType;
	}

	/**
	 * Sets the modifier type.
	 *
	 * @param modifierType
	 *            the new modifier type
	 */
	public void setModifierType(int modifierType)
	{
		this.modifierType = modifierType;
	}

	/**
	 * Gets the modifier type name.
	 *
	 * @return the modifier type name
	 */
	public String getModifierTypeName()
	{
		return modifierTypeName;
	}

	/**
	 * Sets the modifier type name.
	 *
	 * @param modifierTypeName
	 *            the new modifier type name
	 */
	public void setModifierTypeName(String modifierTypeName)
	{
		this.modifierTypeName = modifierTypeName;
	}

	@Override
	public String toString()
	{
		return "ItemAttributeDisplayTypePacket [id=" + id + ", name=" + name + ", displayName=" + displayName + ", description=" + description + ", modifierType=" + modifierType
				+ ", modifierTypeName=" + modifierTypeName + "]";
	}

}
