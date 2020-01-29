/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;



public class DisplaySequenceData
{

	private String id;
	private int displaySequence;
	private String updatedBy;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public int getDisplaySequence()
	{
		return displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "DisplaySequenceData [id=" + id + ", displaySequence="
				+ displaySequence + ", updatedBy=" + updatedBy + "]";
	}

	
}
