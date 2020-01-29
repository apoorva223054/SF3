/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.function;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

/**
 * The persistent class for the functions database table.
 * 
 */
@Entity
@Table(name = "functions")
@XmlRootElement(name = "functions")
public class Function extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "display_name", nullable = false, length = 64)
	private String displayName;

	@Column(name = "display_sequence", nullable = false)
	private int displaySequence;

	@Column(nullable = false, length = 32)
	private String name;

	public Function()
	{
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public int getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString() {
		return "Function [displayName=" + displayName + ", displaySequence="
				+ displaySequence + ", name=" + name + "]";
	}

}