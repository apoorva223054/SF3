/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

// TODO: Auto-generated Javadoc
/**
 * The Class PrintJob.
 */
class PrintJob
{

	/** The info. */
	private String info = null;

	/** The order id. */
	private String orderId;

	/** The attempt counter. */
	private int attemptCounter = 0;

	/**
	 * Instantiates a new prints the job.
	 *
	 * @param info the info
	 * @param attemptCounter the attempt counter
	 * @param orderId the order id
	 */
	public PrintJob(String info, int attemptCounter, String orderId)
	{
		this.info = info;
		this.attemptCounter = attemptCounter;
		this.orderId = orderId;
	}

	/**
	 * Instantiates a new prints the job.
	 */
	public PrintJob()
	{

	}

	/**
	 * Gets the info.
	 *
	 * @return the info
	 */
	public String getInfo()
	{
		return info;
	}

	/**
	 * Sets the info.
	 *
	 * @param info the new info
	 */
	public void setInfo(String info)
	{
		this.info = info;
	}

	/**
	 * Gets the attempt counter.
	 *
	 * @return the attempt counter
	 */
	public int getAttemptCounter()
	{
		return attemptCounter;
	}

	/**
	 * Sets the attempt counter.
	 *
	 * @param attemptCounter the new attempt counter
	 */
	public void setAttemptCounter(int attemptCounter)
	{
		this.attemptCounter = attemptCounter;
	}

	/**
	 * Gets the order id.
	 *
	 * @return the order id
	 */
	public String getOrderId()
	{
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	/**
	 * Sets the order id.
	 *
	 * @param orderId the new order id
	 */
	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

}
