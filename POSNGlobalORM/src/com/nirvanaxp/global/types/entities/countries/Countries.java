/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.countries;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "countries")
@XmlRootElement(name = "countries")
public class Countries
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	protected int id;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "name")
	private String name;

	@Column(name = "short_name")
	private String shortName;

	@Column(name = "phone_code")
	private int phoneCode;
	
	

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getPhoneCode()
	{
		return phoneCode;
	}

	public void setPhoneCode(int phoneCode)
	{
		this.phoneCode = phoneCode;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public String getName()
	{
		return name;
	}

	public String getShortName()
	{
		return shortName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	@Override
	public String toString() {
		return "Countries [id=" + id + ", displayName=" + displayName
				+ ", name=" + name + ", shortName=" + shortName
				+ ", phoneCode=" + phoneCode + "]";
	}

}
