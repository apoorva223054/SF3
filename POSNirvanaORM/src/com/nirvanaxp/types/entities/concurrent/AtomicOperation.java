/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.concurrent;

public class AtomicOperation
{

	public enum Type {
		PRE_CAPTURE, BATCH_SETTLE,BATCH_SYNC
	}
	

	private int id;

	private String name;

	@Override
	public String toString() {
		return "AtomicOperation [id=" + id + ", name=" + name + "]";
	}

}
