/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.custom;

import java.io.Serializable;

public class OrderLocationUpdateInfo implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4953493372015815425L;
	private int id;
	private int oldLocationId;
	private int newLocationId;

	public OrderLocationUpdateInfo(int id, int oldLocationId, int newLocationId)
	{
		setId(id);
		setOldLocationId(oldLocationId);
		setNewLocationId(newLocationId);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getOldLocationId()
	{
		return oldLocationId;
	}

	public void setOldLocationId(int oldLocationId)
	{
		this.oldLocationId = oldLocationId;
	}

	public int getNewLocationId()
	{
		return newLocationId;
	}

	public void setNewLocationId(int newLocationId)
	{
		this.newLocationId = newLocationId;
	}

	@Override
	public String toString() {
		return "OrderLocationUpdateInfo [id=" + id + ", oldLocationId="
				+ oldLocationId + ", newLocationId=" + newLocationId + "]";
	}

}
